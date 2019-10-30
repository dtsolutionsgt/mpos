package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_CorrelativosObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantCorelPagos extends PBase {

    private EditText txt1,txt2,txt3,txt4;
    private clsP_CorrelativosObj holder;
    private String id;

    private clsClasses.clsP_correlativos item=clsCls.new clsP_correlativos();

    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_corel_pagos);

        super.InitBase();
        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.txt10);
        txt4 = (EditText) findViewById(R.id.txt12);txt4.setEnabled(false);

        holder =new clsP_CorrelativosObj(this,Con,db);

        id=gl.gcods;
        if (id.isEmpty()) newItem(); else loadItem();
    }

    //region Main


    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        item.empresa=gl.emp;
        item.ruta=gl.ruta;
        item.serie="";
        item.tipo="";
        item.inicial=0;
        item.fin=0;
        item.actual=0;
        item.enviado="N";

        showItem();
    }


    private void loadItem() {
        try {
            holder.fill("WHERE TIPO='"+id+"'");
            item=holder.first();

            showItem();

            txt1.requestFocus();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showItem() {
        if(newitem){
            txt1.setText("");
            txt2.setText("");
            txt3.setText("");
            txt4.setText("");
        } else {
            txt1.setText(item.serie);
            txt2.setText(""+item.inicial);
            txt3.setText(""+item.fin);
            txt4.setText(""+item.actual);
        }
    }

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    private boolean validaDatos() {
        String ss;

        try {

            ss=txt1.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir serie!");return false;
            }

            item.serie=ss;

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir inicial!");
                return false;
            } else {
                item.inicial=Integer.parseInt(ss);
            }

            ss=txt3.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir final!");
                return false;
            } else {
                item.fin=Integer.parseInt(ss);
            }

            item.actual = item.inicial;

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void updateItem() {
        try {
            holder.fill(" WHERE SERIE='"+item.serie+"'");

            if(holder.count!=0){
                //if(item.)
            }

            holder.update(item);
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=item.tipo;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region msg

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskUpdate(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    //endregion
}
