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
import com.dtsgt.ladapt.LA_panama_ubic;

import java.util.ArrayList;

public class PanamaUbic extends PBase {

    private ListView listView;
    private EditText txtflt;
    private TextView lblreg;

    private LA_panama_ubic adapter;

    private ArrayList<clsClasses.clsLista> items= new ArrayList<clsClasses.clsLista>();

    private boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_panama_ubic);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtflt = findViewById(R.id.editTextText2);
            lblreg = findViewById(R.id.textView331);lblreg.setText("Encontrado: "+0);
            listView.requestFocus();

            sql="SELECT CODIGO_UBIC,NOMBRE FROM P_panama_ubic ORDER BY NOMBRE";
            listItems();

            gl.gstr="";

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
        sql="SELECT CODIGO_UBIC,NOMBRE FROM P_panama_ubic ORDER BY NOMBRE";
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

                    String[] pt = gl.cli_depto.split("-");
                    gl.gstr=pt[2];

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
                    //if (ss.length()<2) return;

                    sql="SELECT CODIGO_UBIC,NOMBRE FROM P_panama_ubic WHERE (NOMBRE LIKE '%"+ss+"%') ORDER BY NOMBRE";
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
        String u1,u2,u3;

        if (!idle) return;

        idle=false;
        lblreg.setText("Encontrado: "+0);

        try {
            items.clear();
            dt = Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    item=clsCls.new clsLista();

                    item.f1=dt.getString(0);
                    item.f2=dt.getString(1);

                    try {
                        String[] pt = item.f2.split("-");

                        item.f3=pt[0];
                        item.f4=pt[1];
                        item.f5=pt[2];

                        items.add(item);
                    } catch (Exception e) {
                        msgbox("Ubicacion incorrecta:\n"+item.f2);
                    }

                    dt.moveToNext();
                }
            }


            adapter=new LA_panama_ubic(this,this,items);
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