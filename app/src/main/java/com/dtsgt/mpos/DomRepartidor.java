package com.dtsgt.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import android.widget.AdapterView.OnItemClickListener;
import com.dtsgt.classes.clsP_empresa_transObj;
import com.dtsgt.classes.clsP_repartidorObj;
import com.dtsgt.ladapt.LA_P_repartidor;


public class DomRepartidor extends PBase {

    ListView listView;
    TextView lbltit,lblreg;
    EditText txtflt;
    CheckBox cbdis;

    LA_P_repartidor adapter;

    clsP_repartidorObj P_repartidorObj;

    int selid=0;
    boolean mode_sel,activos=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_repartidor);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtflt =  findViewById(R.id.editTextText5);
            lbltit =  findViewById(R.id.textView367);
            lblreg =  findViewById(R.id.textView374);
            cbdis =  findViewById(R.id.checkBox29);

            mode_sel=gl.repartidor_select;
            if (mode_sel) {
                lbltit.setText("SELECCIÓN DE REPARTIDOR");cbdis.setVisibility(View.INVISIBLE);
            } else {
                lbltit.setText("MODIFICACIÓN DE REPARTIDOR");cbdis.setVisibility(View.VISIBLE);
            }

            P_repartidorObj=new clsP_repartidorObj(this,Con,db);

            cargaEmpresas();
            listItems();

            setHandlers();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doAdd(View view) {
        gl.gint=0;
        startActivity(new Intent(this, DomRepartDet.class));
    }

    public void doClear(View view) {
        txtflt.setText("");
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_repartidor item = (clsClasses.clsP_repartidor)lvObj;

                adapter.setSelectedIndex(position);
                selid=item.codigo;

                gl.repartidor_codigo=item.codigo;
                gl.gstr=item.nombre;
                gl.gstr2=item.placa;
                gl.ped_dom_empresa=item.codigo_empresa;

                if (mode_sel) {
                    finish();
                } else {
                    startActivity(new Intent(DomRepartidor.this, DomRepartDet.class));
                }
            };
        });

        cbdis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) activos=false; else activos=true;
                listItems();
            }
        });

        txtflt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listItems();
            }
        });

    }

    //endregion

    //region Main

    private void listItems() {
        int pp=0;

        try {
            String flt=txtflt.getText().toString().toUpperCase();

            sql="WHERE (1=1) ";
            if (activos) sql+="AND (Activo=1) ";
            if (!flt.isEmpty())  sql+="AND (UPPER(NOMBRE) LIKE '%"+flt+"%' ) OR (UPPER(PLACA) LIKE '%"+flt+"%') ";
            sql+="ORDER BY Nombre";

            P_repartidorObj.fill(sql);

            selidx=-1;
            for (clsClasses.clsP_repartidor itm:P_repartidorObj.items) {
                itm.pempresa=nombreEmpresa(itm.codigo_empresa);
                if (itm.codigo==selid) selidx=pp;
                pp++;
            }

            adapter=new LA_P_repartidor(this,this,P_repartidorObj.items);
            listView.setAdapter(adapter);

            lblreg.setText("Registros: "+P_repartidorObj.items.size());

            if (selidx>-1) {
                adapter.setSelectedIndex(selidx);
                listView.smoothScrollToPosition(selidx);
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    ;break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void cargaEmpresas() {
        clsClasses.clsP_empresa_trans item = clsCls.new clsP_empresa_trans();

        try {
            clsP_empresa_transObj P_empresa_transObj=new clsP_empresa_transObj(this,Con,db);
            P_empresa_transObj.fill("WHERE (CODIGO<>1) ORDER BY NOMBRE");

            gl.emptrans.clear();

            item.codigo=1;item.nombre="Transporte propio";gl.emptrans.add(item);

            for (int i = 0; i <P_empresa_transObj.items.size(); i++) {
                gl.emptrans.add(P_empresa_transObj.items.get(i));
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String nombreEmpresa(int eid) {
        for ( clsClasses.clsP_empresa_trans itm:gl.emptrans) {
            if (itm.codigo==eid) return itm.nombre;
        }
        return "";
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            P_repartidorObj.reconnect(Con,db);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

}