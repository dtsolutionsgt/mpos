package com.dtsgt.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.extTextDlg;
import com.dtsgt.firebase.fbPedidoEnc;

public class DomDetComp extends PBase {

    private TextView lblprev,lblprox,lblord,lblest,lblcli,lbldir,lbltext,lblcamb,lbltel;

    private fbPedidoEnc fbpe;

    private clsD_domicilio_encObj D_domicilio_encObj;

    private clsClasses.clsD_domicilio_enc selitem;

    private String corel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_det_comp);

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

            corel=gl.dom_det_cod;

            fbpe = new fbPedidoEnc("Domicilio/"+gl.emp+"/"+gl.tienda+"/"+du.actDate()+"/");

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

    public void doReinic(View view) {
        msgask(0,"Activar orden");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {

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

    private void aplicarEstado() {
        try {
            selitem.estado=3;
            D_domicilio_encObj.update(selitem);

            fbpe.updateState(selitem.corel,selitem.estado);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    aplicarEstado();break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void msgexit(String msg) {
        try {
            extTextDlg txtdlg = new extTextDlg();
            txtdlg.buildDialog(DomDetComp.this,"Orden","OK");

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