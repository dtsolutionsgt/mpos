package com.dtsgt.mpos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_giro_negocioObj;
import com.dtsgt.ladapt.LA_P_giro_negocio;

public class CliPosSVGiro extends PBase {

    private ListView listView;
    private EditText txtflt;

    private LA_P_giro_negocio adapter;
    private clsP_giro_negocioObj P_giro_negocioObj;

    private boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_pos_svgiro);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtflt = findViewById(R.id.editTextText2);

            gl.gint=0;

            P_giro_negocioObj=new clsP_giro_negocioObj(this,Con,db);

            setHandlers();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    public void doClear(View view) {
        txtflt.setText("");
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsP_giro_negocio item = (clsClasses.clsP_giro_negocio)lvObj;

                    adapter.setSelectedIndex(position);
                    gl.gint = item.codigo;

                    finish();
                } catch (Exception e) {
                }
            });

            txtflt.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listItems();
                }
            });

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void listItems() {

        if (!idle) return;
        idle=false;

        try {

            String flt=txtflt.getText().toString().toUpperCase();
            sql=" WHERE (1=1) ";

            if (!flt.isEmpty()) {
                String[] fsp = flt.split(" ");
                for (int i = 0; i < fsp.length; i++) {
                    sql+=" AND (UPPER(DESCRIPCION) LIKE '%" + fsp[i] + "%') ";
                }
            }
            sql+="ORDER BY DESCRIPCION LIMIT 300";

            P_giro_negocioObj.fill(sql);

            adapter=new LA_P_giro_negocio(this,this,P_giro_negocioObj.items);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        idle=true;
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_giro_negocioObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}