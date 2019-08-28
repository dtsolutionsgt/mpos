package com.dtsgt.mant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.ladapt.LA_Lista;
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

public class Lista extends PBase {

    private ListView listView;
    private TextView lblTit,lblReg;
    private EditText txtFilter;
    private ImageView imgadd;
    private Switch swact;

    private LA_Lista adapter;
    private clsListaObj ViewObj;

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

        ViewObj=new clsListaObj(this,Con,db);

        setMantID();
        setHandlers();
        //listItems();

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

                adapter.setSelectedIndex(position);
                gl.gcods=item.f1;
                abrirMant();
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

            ViewObj.fillSelect(sql);
            adapter=new LA_Lista(this,this,ViewObj.items);
            listView.setAdapter(adapter);

            lblReg.setText("Registros : "+ViewObj.count);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        for (int i = 0; i <ViewObj.count; i++) {
            ss=ViewObj.items.get(i).f1;
            if (ss.equalsIgnoreCase(gl.gcods)) selidx=i;
        }

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            //listView.smoothScrollToPosition(selidx);
         }

    }

    private void setTableSQL() {
        String ft=txtFilter.getText().toString();
        boolean flag=!ft.isEmpty();
        boolean act=!swact.isChecked();

        gl.banco = false;

        sql="";

        switch (gl.mantid) {
            case 0: // Almacen
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_ALMACEN WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 1: //Banco
                sql="SELECT 0,CODIGO,NOMBRE,CUENTA,'', '','','','' FROM P_BANCO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";

                gl.banco = true;
                break;
            case 2: // Clientes
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_CLIENTE WHERE ";
                if (act) sql+="(BLOQUEADO='N') ";else sql+="(BLOQUEADO='S') ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 3: // Empresa
                sql="SELECT 0,EMPRESA,NOMBRE,'','', '','','','' FROM P_EMPRESA ";
                break;
            case 4: // Familia
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_LINEA WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 5: //Forma de pago
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_MEDIAPAGO WHERE ";
                if (act) sql+="(ACTIVO='S') ";else sql+="(ACTIVO='N') ";
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
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_PROVEEDOR WHERE ";
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
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_RUTA WHERE ";
                if (act) sql+="(ACTIVO='S') ";else sql+="(ACTIVO='N?) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
                break;
            case 14: // Nivel Precio
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_NIVELPRECIO WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
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
                lblTit.setText("Usuarios");break;
            case 12:
                lblTit.setText("Tiendas");break;
            case 13:
                lblTit.setText("Caja");break;
            case 14:
                lblTit.setText("Nivel precio");break;
        }
    }

    private void abrirMant() {

        switch (gl.mantid) {
            case 0:
                startActivity(new Intent(this,MantAlmacen.class));break;
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
            case 10:
                lblTit.setText("Usuarios");break;
            case 11:
                startActivity(new Intent(this, MantVendedores.class));break;
            case 12:
                startActivity(new Intent(this, MantTienda.class));break;
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

        listItems();
    }

    //endregion


}
