package com.dtsgt.mpos;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.extTextDlg;

public class DomDetalle extends PBase {

    private TextView lblprev,lblprox,lblord,lblest,lblcli,lbldir,lbltext,lblcamb,lbltel;

    private clsD_domicilio_encObj D_domicilio_encObj;

    private clsClasses.clsD_domicilio_enc selitem;

    private String corel;
    private int estado,nuevoest,prevest;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_detalle);

            super.InitBase();

            lblprev =  findViewById(R.id.btndel);
            lblprox =  findViewById(R.id.btnadd);
            lblord  =  findViewById(R.id.textView363);
            lblest  =  findViewById(R.id.textView362);
            lblcli  =  findViewById(R.id.textView356);
            lbldir  =  findViewById(R.id.textView358);
            lbltext =  findViewById(R.id.textView360);
            lblcamb =  findViewById(R.id.textView364);
            lbltel  =  findViewById(R.id.textView365);

            estado=gl.dom_est_val;
            corel=gl.dom_det_cod;

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);

            loadItem();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doSalir(View view) {
        finish();
    }

    public void doPrev(View view) {
        msgask(0,"Cambiar estado a "+lblprev.getText().toString()+"?");
    }

    public void doProx(View view) {
        gl.dom_est_val=nuevoest;
        finish();
    }

    public void doAnul(View view) {
        msgask(1,"Anular orden?");
    }

    //endregion

    //region Main

    private void loadItem() {
        String ss="",sp="";
        try {

            switch (estado) {
                case 2:
                    ss="EN PROCESO";nuevoest=3;
                    sp="NUEVO";prevest=2;
                    break;
                case 3:
                    ss="COMPLETO";nuevoest=5;
                    sp="NUEVO";prevest=2;
                    break;
                case 5:
                    ss="EN TRANSITO";nuevoest=6;
                    sp="EN PROCESO";prevest=3;
                    break;
                case 6:
                    ss="ENTREGADO";nuevoest=7;
                    sp="COMPLETO";prevest=5;
                    break;
            }

            lblprox.setText(ss);lblprev.setText(sp);

            D_domicilio_encObj.fill("WHERE (corel='"+ corel +"')");
            selitem=D_domicilio_encObj.first();

            lblord.setText("#"+selitem.idorden % 1000);
            lblest.setText(app.estadoNombre(selitem.estado));
            lblcli.setText(""+selitem.cliente_nombre);
            lbldir.setText(""+selitem.direccion_text);
            lbltext.setText(""+selitem.texto);
            lblcamb.setText(""+selitem.cambio);
            lbltel.setText(""+selitem.telefono);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void anulaOrden() {
        gl.dom_est_val=4;

        try {
            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                gl.tipo=3;
                Intent intent = new Intent(this,Anulacion.class);
                startActivity(intent);

            };
            mtimer.postDelayed(mrunner,200);

            finish();
        } catch (Exception e) {
            msgexit(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    gl.dom_est_val=prevest; finish();break;
                case 1:
                    anulaOrden();break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void msgexit(String msg) {
        try {
            extTextDlg txtdlg = new extTextDlg();
            txtdlg.buildDialog(DomDetalle.this,"Orden","OK");

            txtdlg.setText(msg);

            txtdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            txtdlg.show();

        } catch (Exception e){ }
    }

    //endregion

    //region Aux


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            D_domicilio_encObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }
    //endregion


}