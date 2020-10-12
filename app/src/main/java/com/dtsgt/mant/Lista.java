package com.dtsgt.mant;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.ladapt.LA_Lista;
import com.dtsgt.ladapt.listAdapt_desc;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class Lista extends PBase {

    private ListView listView;
    private TextView lblTit,lblReg;
    private EditText txtFilter;
    private ImageView imgadd;
    private Switch swact;

    private LA_Lista adapter;
    private listAdapt_desc adapt;
    private clsListaObj ViewObj;

    private ArrayList<String> fprints = new ArrayList<String>();

    private boolean listaedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        lblTit = (TextView) findViewById(R.id.lblTit);
        lblReg = (TextView) findViewById(R.id.textView85);
        txtFilter = (EditText) findViewById(R.id.txtFilter);
        imgadd = (ImageView) findViewById(R.id.imageView26);imgadd.setVisibility(View.VISIBLE);
        swact = (Switch) findViewById(R.id.switch1);swact.setVisibility(View.VISIBLE);

        listaedit=gl.listaedit;gl.listaedit=true;gl.pickcode="";

        ViewObj=new clsListaObj(this,Con,db);

        ProgressDialog("Cargando listado...");

        setMantID();
        setHandlers();
        //listItems();

        if (gl.peMCent) {
            //if (!app.grant(10,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
            //}
        } else {
            if (gl.mantid==2) {
                if (gl.rol==1) imgadd.setVisibility(View.INVISIBLE);
            }
        }

    }

    //region Events

    public void doAdd(View view) {
       gl.gcods="";
       abrirMant();
    }

    public void doClear(View view) {
        txtFilter.setText("");
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsLista item = (clsClasses.clsLista) lvObj;

                progress.setMessage("Cargando detalle...");
                progress.show();

                if(gl.mantid==15){
                    adapt.setSelectedIndex(position);
                }else {
                    adapter.setSelectedIndex(position);
                }

                if(gl.mantid!=20) {
                    gl.gcods=item.f1;
                } else {
                    if(item.f2.equals("Pagos")){
                        gl.gcods="P";
                    }
                }

                if (listaedit) {
                    abrirMant();
                } else {
                    gl.pickcode=item.f1;gl.pickname=item.f2;finish();
                }
            }

        });

        swact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               listItems();
            }
        });


        txtFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tl = txtFilter.getText().toString().length();
                if (tl == 0 || tl > 1) listItems();
            }
        });
    }

    //endregion

    //region Main

    private void listItems() {
        String ss;

        selidx=-1;

        try {
            lblReg.setText("Registros : 0");
            setTableSQL();

            ViewObj.fillSelect(sql,gl.mantid);

            if(gl.disc){
                adapt=new listAdapt_desc(this,this,ViewObj.items);
                listView.setAdapter(adapt);
            }else {
                adapter=new LA_Lista(this,this,ViewObj.items,gl.mantid);
                listView.setAdapter(adapter);
            }

            lblReg.setText("Registros : "+ViewObj.count);

            progress.cancel();

        } catch (Exception e) {
            progress.cancel();
            mu.msgbox(e.getMessage());
        }

        if (gl.mantid==2) listFPrints();

        for (int i = 0; i <ViewObj.count; i++) {
            ss=ViewObj.items.get(i).f1;
            if (ss.equalsIgnoreCase(gl.gcods)) selidx=i;

            if (gl.mantid==2) {
                if (fprints.contains(ss)) ViewObj.items.get(i).f8="X";
            }
        }

        if (selidx>-1) {
            if(gl.disc){
                adapt.setSelectedIndex(selidx);
            }else {
                adapter.setSelectedIndex(selidx);
            }
        }


    }

    private void setTableSQL() {
        String ft=txtFilter.getText().toString();
        boolean flag=!ft.isEmpty();
        boolean act=!swact.isChecked();

        gl.banco = false;
        gl.disc = false;

        sql="";

        switch (gl.mantid) {
            case 0: // Almacen
                   break;

            case 1: //Banco
                progress.setMessage("Cargando bancos");
                sql="SELECT 0,CODIGO,NOMBRE,CUENTA,'', '','','','' FROM P_BANCO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";

                gl.banco = true;
                break;

            case 2: // Clientes
                progress.setMessage("Cargando clientes");
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_CLIENTE WHERE ";
                if (act) sql+="(BLOQUEADO=0) ";else sql+="(BLOQUEADO=1) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 3: // Empresa
                progress.setMessage("Cargando empresa");
                sql="SELECT 0,EMPRESA,NOMBRE,'','', '','','','' FROM P_EMPRESA ";
                break;

            case 4: // Familia
                progress.setMessage("Cargando familias");
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_LINEA WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 5: //Forma de pago
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_MEDIAPAGO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO="+ft+") OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 6: // Impuesto
                sql="SELECT 0,CODIGO,VALOR,'','', '','','','' FROM P_IMPUESTO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (VALOR LIKE '%"+ft+"%')) ";
                sql+="ORDER BY VALOR";
                break;

            case 7: //Moneda
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_MONEDA WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 8: //Productos
                sql="SELECT 0,CODIGO,DESCLARGA,'','', '','','','' FROM P_PRODUCTO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (DESCLARGA LIKE '%"+ft+"%')) ";
                sql+="ORDER BY DESCLARGA";
                break;

            case 9: // Proveedores
                sql="SELECT 0,CODIGO_PROVEEDOR,NOMBRE,'','', '','','','' FROM P_PROVEEDOR WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 11: // Vendedores
                sql="SELECT DISTINCT 0,CODIGO,NOMBRE,'','', '','','','' FROM VENDEDORES WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 12: // Tienda
                sql="SELECT 0,CODIGO,DESCRIPCION,'','', '','','','' FROM P_SUCURSAL WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (DESCRIPCION LIKE '%"+ft+"%')) ";
                sql+="ORDER BY DESCRIPCION";
                break;

            case 13: // Caja
                sql="SELECT 0,P_RUTA.CODIGO,P_SUCURSAL.DESCRIPCION,P_RUTA.NOMBRE,'','', '','','','' ";
                sql+="FROM P_RUTA INNER JOIN P_SUCURSAL ON P_RUTA.SUCURSAL=P_SUCURSAL.CODIGO WHERE (1=1) ";
                //if (act) sql+="(P_RUTA.ACTIVO=1) ";else sql+="(P_RUTA.ACTIVO=0) ";
                if (flag) sql+="AND ((P_RUTA.CODIGO='"+ft+"') OR (P_RUTA.NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY P_SUCURSAL.DESCRIPCION,P_RUTA.NOMBRE";

                /*
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_RUTA WHERE ";
                if (act) sql+="(ACTIVO='S') ";else sql+="(ACTIVO='N') ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                */

                break;

            case 14: // Nivel Precio
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_NIVELPRECIO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 15: // Descuento
                sql="SELECT 0,d.PRODUCTO,p.DESCCORTA,d.VALOR,d.FECHAINI,d.FECHAFIN,'','','' FROM P_DESCUENTO d INNER JOIN P_PRODUCTO p on d.PRODUCTO = p.CODIGO WHERE ";
                if (act) sql+="(d.ACTIVO=1) ";else sql+="(d.ACTIVO=0) ";
                if (flag) sql+="AND ((d.PRODUCTO='"+ft+"') OR (p.DESCCORTA LIKE '%"+ft+"%')) ";
                sql+="ORDER BY d.PRODUCTO";
                gl.disc=true;
                break;

            case 17: //Combos
                sql="SELECT 0,CODIGO,DESCLARGA,'','', '','','','' FROM P_PRODUCTO WHERE (TIPO='M') AND ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (DESCLARGA LIKE '%"+ft+"%')) ";
                sql+="ORDER BY DESCLARGA";
                break;

            case 18: // Combo opcion
                sql="SELECT 0,ID,NOMBRE,'','', '','','','' FROM P_PRODOPC WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((ID='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 19: // Concepto Pago
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_CONCEPTOPAGO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;

            case 20: // Correlativos
                sql="SELECT 0,SERIE,TIPO,'','', '','','','',INICIAL,FINAL FROM P_CORREL_OTROS ";
                if (flag) sql+="WHERE ((SERIE='"+ft+"') ";
                sql+="ORDER BY SERIE";
                break;
            case 22: // Motivo ajuste
                sql="SELECT EMPRESA,CODIGO_MOTIVO_AJUSTE,NOMBRE,'','', '','','','' FROM P_MOTIVO_AJUSTE WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO_MOTIVO_AJUSTE='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 26: // Sala
                sql="SELECT 0,CODIGO_SALA,NOMBRE,'','', '','','','' FROM P_RES_SALA WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO_SALA="+ft+") OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 27: // Mesa
                sql="SELECT 0,CODIGO_MESA,NOMBRE,'','', '','','','' FROM P_RES_MESA  ";
                if (flag) sql+="WHERE ((CODIGO_MESA="+ft+") OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 28: // Grupo de mesas
                sql="SELECT 0,CODIGO_GRUPO,NOMBRE,'','', '','','','' FROM P_RES_GRUPO  ";
                if (flag) sql+="WHERE ((CODIGO_GRUPO="+ft+") OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
        }
    }

    //endregion

    //region Aux

    private void setMantID() {

        switch (gl.mantid) {
            case 0:
                lblTit.setText("Almacen");break;
            case 1:
                lblTit.setText("Bancos");break;
            case 2:
                lblTit.setText("Clientes");break;
            case 3:
                lblTit.setText("Empresa");
                swact.setVisibility(View.INVISIBLE);
                imgadd.setVisibility(View.INVISIBLE);break;
            case 4:
                lblTit.setText("Familia");break;
            case 5:
                lblTit.setText("Forma pago");
                imgadd.setVisibility(View.INVISIBLE);break;
            case 6:
                lblTit.setText("Impuestos");break;
            case 7:
                lblTit.setText("Moneda");break;
            case 8:
                lblTit.setText("Productos");break;
            case 9:
                lblTit.setText("Proveedores");break;
            case 11:
                swact.setText("Mostrar desactivados");
                lblTit.setText("Usuarios");break;
            case 12:
                lblTit.setText("Tiendas");break;
            case 13:
                lblTit.setText("Caja");break;
            case 14:
                lblTit.setText("Niveles de precio");break;
            case 15:
                lblTit.setText("Descuentos");break;
            case 17:
                lblTit.setText("Combo");break;
            case 18:
                lblTit.setText("Combo opciones");break;
            case 19:
                lblTit.setText("Concepto Pago");break;
            case 20:
                lblTit.setText("Correlativos");break;
            case 22:
                lblTit.setText("Motivos de ajuste");break;
            case 26:
                lblTit.setText("Sala");break;
            case 27:
                lblTit.setText("Mesa");break;
            case 28:
                lblTit.setText("Grupo de mesas");break;
        }
    }

    private void abrirMant() {

        browse=1;
        gl.savemantid=gl.mantid;

        switch (gl.mantid) {
            case 0:
                break;
            case 1:
                startActivity(new Intent(this,MantBanco.class));break;
            case 2:
                startActivity(new Intent(this,MantCli.class));break;
            case 3:
                startActivity(new Intent(this,MantEmpresa.class));break;
            case 4:
                startActivity(new Intent(this,MantFamilia.class));break;
            case 5:
                startActivity(new Intent(this,MantMediaPago.class));break;
            case 6:
                startActivity(new Intent(this,MantImpuesto.class));break;
            case 7:
                startActivity(new Intent(this,MantMoneda.class));break;
            case 8:
                startActivity(new Intent(this,MantProducto.class));break;
            case 9:
                startActivity(new Intent(this,MantProveedor.class));break;
            case 11:
                startActivity(new Intent(this, MantVendedores.class));break;
            case 12:
                startActivity(new Intent(this, MantTienda.class));break;
            case 13:
                startActivity(new Intent(this, MantCaja.class));break;
            case 14:
                startActivity(new Intent(this, MantNivelPrecio.class));break;
            case 15:
                startActivity(new Intent(this, MantDescuento.class));break;
            case 18:
                //startActivity(new Intent(this, MantOpcion.class));break;
            case 19:
                startActivity(new Intent(this, MantConceptoPago.class));break;
            case 22:
                startActivity(new Intent(this, MantMotivoAjuste.class));break;
            case 26:
                startActivity(new Intent(this, MantResSala.class));break;
            case 27:
                startActivity(new Intent(this, MantResMesa.class));break;
            case 28:
                startActivity(new Intent(this, MantResGrupo.class));break;
        }

        progress.cancel();

    }

    private void listFPrints() {
        Cursor dt;
        String ss;

        try {
            fprints.clear();

            sql="SELECT DISTINCT CODIGO FROM FPrint";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {
                    fprints.add(dt.getString(0));
                    dt.moveToNext();
                }
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ViewObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            gl.mantid=gl.savemantid;
        }

        listItems();
    }

    //endregion

}
