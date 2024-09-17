package com.dtsgt.mpos;


import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.ladapt.LA_municipio;

import java.util.ArrayList;

public class Municipio extends PBase {

    private ListView listView;
    private EditText txtflt;
    private TextView lblreg;

    private LA_municipio adapter;

    private ArrayList<clsClasses.clsLista> items= new ArrayList<clsClasses.clsLista>();

    private boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_municipio2);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtflt = findViewById(R.id.editTextText2);
            lblreg = findViewById(R.id.textView331);lblreg.setText("Encontrado: "+0);
            listView.requestFocus();

            sql="SELECT  P_MUNICIPIO.CODIGO, P_MUNICIPIO.CODIGO_DEPARTAMENTO, P_MUNICIPIO.NOMBRE, P_DEPARTAMENTO.NOMBRE " +
                    "FROM P_MUNICIPIO INNER JOIN  P_DEPARTAMENTO ON P_MUNICIPIO.CODIGO_DEPARTAMENTO = P_DEPARTAMENTO.CODIGO " +
                    "WHERE (P_MUNICIPIO.CODIGO_DEPARTAMENTO="+gl.cli_depto_suc+") ORDER BY P_MUNICIPIO.NOMBRE";
            listItems();

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
        sql="SELECT  P_MUNICIPIO.CODIGO, P_MUNICIPIO.CODIGO_DEPARTAMENTO, P_MUNICIPIO.NOMBRE, P_DEPARTAMENTO.NOMBRE " +
                "FROM P_MUNICIPIO INNER JOIN  P_DEPARTAMENTO ON P_MUNICIPIO.CODIGO_DEPARTAMENTO = P_DEPARTAMENTO.CODIGO " +
                "WHERE (1=0) ";
        listItems();
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsLista item = (clsClasses.clsLista) lvObj;

                    adapter.setSelectedIndex(position);
                    gl.cli_muni = item.f1;
                    gl.cli_depto = item.f2;

                    finish();
                } catch (Exception e) {
                }
            });

            txtflt.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String ss=s.toString().trim();

                    if (ss.isEmpty()) return;
                    if (ss.length()<2) return;

                    sql="SELECT  P_MUNICIPIO.CODIGO, P_MUNICIPIO.CODIGO_DEPARTAMENTO, P_MUNICIPIO.NOMBRE, P_DEPARTAMENTO.NOMBRE " +
                        "FROM P_MUNICIPIO INNER JOIN  P_DEPARTAMENTO ON P_MUNICIPIO.CODIGO_DEPARTAMENTO = P_DEPARTAMENTO.CODIGO " +
                        "WHERE (P_MUNICIPIO.NOMBRE  LIKE '%"+ss+"%') ORDER BY P_MUNICIPIO.NOMBRE";
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
        Cursor dt;
        clsClasses.clsLista item;

        if (!idle) return;

        idle=false;
        lblreg.setText("Encontrado: "+0);

        try {
            /*

            String flt=txtflt.getText().toString().toUpperCase();

            if (flt.isEmpty()) {
                P_productoObj.fill();
            } else {
                sql="WHERE (UPPER(CODIGO) LIKE '%"+flt+"%' ) OR (UPPER(DESCCORTA) LIKE '%"+flt+"%') ORDER BY DESCCORTA LIMIT 10";
                P_productoObj.fill(sql);
            }

             */

            items.clear();
            dt = Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    item=clsCls.new clsLista();

                    item.f1=dt.getString(0);
                    item.f2=dt.getString(1);
                    item.f3=dt.getString(2);
                    item.f4=dt.getString(3);

                    items.add(item);

                    dt.moveToNext();
                }
            }


            adapter=new LA_municipio(this,this,items);
            listView.setAdapter(adapter);

            lblreg.setText("Encontrado: "+items.size());
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        idle=true;
    }

    //endregion

    //region Dialogs


    //endregion

    //region Aux


    //endregion

    //region Activity Events


    //endregion




}