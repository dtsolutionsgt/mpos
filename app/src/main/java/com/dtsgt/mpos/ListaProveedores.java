package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.ladapt.LA_Lista;
import com.dtsgt.ladapt.listAdapt_desc;
import com.dtsgt.mant.MantBanco;
import com.dtsgt.mant.MantCaja;
import com.dtsgt.mant.MantCli;
import com.dtsgt.mant.MantConceptoPago;
import com.dtsgt.mant.MantDescuento;
import com.dtsgt.mant.MantEmpresa;
import com.dtsgt.mant.MantFamilia;
import com.dtsgt.mant.MantImpuesto;
import com.dtsgt.mant.MantMediaPago;
import com.dtsgt.mant.MantMoneda;
import com.dtsgt.mant.MantNivelPrecio;
import com.dtsgt.mant.MantProducto;
import com.dtsgt.mant.MantProveedor;
import com.dtsgt.mant.MantTienda;
import com.dtsgt.mant.MantVendedores;

import java.util.ArrayList;

public class ListaProveedores extends PBase {

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
        setContentView(R.layout.activity_lista_proveedores);

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

        setHandlers();
        listItems();

        if (gl.grantaccess) {
            if (!app.grant(10,gl.rol)) imgadd.setVisibility(View.INVISIBLE);
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

    private void abrirMant() {

        browse=1;
        gl.savemantid=gl.mantid;

       startActivity(new Intent(this,MantProveedor.class));

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

                adapter.setSelectedIndex(position);
                gl.gcods=item.f1;

                gl.pickcode=item.f1;
                gl.pickname=item.f2;

                gl.cod_prov_recarga=Integer.valueOf(gl.pickcode);

                Intent intent = new Intent(ListaProveedores.this,Producto.class);
                startActivity(intent);

                finish();

                progress.cancel();
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
                adapter=new LA_Lista(this,this,ViewObj.items);
                listView.setAdapter(adapter);
            }

            lblReg.setText("Registros : "+ViewObj.count);

            progress.cancel();

        } catch (Exception e) {
            progress.cancel();
            mu.msgbox(e.getMessage());
        }

        for (int i = 0; i <ViewObj.count; i++) {
            ss=ViewObj.items.get(i).f1;
            if (ss.equalsIgnoreCase(gl.gcods)) selidx=i;

            if (gl.mantid==2) {
                if (fprints.contains(ss)) ViewObj.items.get(i).f8="X";
            }
        }

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
        }
    }

    private void setTableSQL() {
        String ft=txtFilter.getText().toString();
        boolean flag=!ft.isEmpty();
        boolean act=!swact.isChecked();

        // Proveedores
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_PROVEEDOR WHERE ";
                if (act) sql+="(ACTIVO=1) ";else sql+="(ACTIVO=0) ";
                if (flag) sql+="AND ((CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')) ";
                sql+="ORDER BY NOMBRE";
     }

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
