package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_domicilio_entregaObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.firebase.fbPedidoEnc;
import com.dtsgt.webservice.srvCommit;

public class DomPago extends PBase {

    EditText txtef, txtcard, txtauth;
    TextView lblord, lblpago,lbltot;

    fbPedidoEnc fbpe;

    clsD_domicilio_entregaObj D_domicilio_entregaObj;

    clsClasses.clsD_domicilio_entrega selitem;

    String corel,corel_fact,auth;
    double monto, pagoe, pagoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_pago);

            super.InitBase();

            txtef = findViewById(R.id.editTextNumberDecimal2);txtef.requestFocus();
            txtcard = findViewById(R.id.editTextNumberDecimal3);
            txtauth = findViewById(R.id.editTextText6);
            lblord = findViewById(R.id.lblTit);
            lbltot = findViewById(R.id.textView375);
            lblpago = findViewById(R.id.textView376);

            fbpe = new fbPedidoEnc("Domicilio/"+gl.emp+"/"+gl.tienda+"/"+du.actDate()+"/");

            D_domicilio_entregaObj=new clsD_domicilio_entregaObj(this,Con,db);

            setHandlers();

            loadItem();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }



    //region Events

    public void doApply(View view) {
        if (calculaPago()==monto) {
            if (pagoc >0 && auth.isEmpty()) {
                msgbox("Falta autorizacion.");return;
            }

            msgask(0,"¿Aplicar pago y completar la orden?");
        } else {
            msgbox("Pago incorrecto.");
        }
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        txtef.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculaPago();
            }
        });

        txtcard.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculaPago();
            }
        });

    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            lblord.setText(gl.ped_dom_orden);
            corel=gl.dom_det_cod;

            D_domicilio_entregaObj.fill("WHERE (COREL_ORDEN='"+corel+"') ORDER BY COREL DESC");
            selitem=D_domicilio_entregaObj.first();

            corel_fact=selitem.corel;
            monto=selitem.total;
            monto=mu.round2(monto);

            lbltot.setText("Monto a pagar: "+mu.frmcur(monto));
            lblpago.setText("Total pago: "+mu.frmcur(0));

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void saveItem() {
        Cursor dt;
        int cc=1,codpe,codpc;
        String nomc;

        try {
            sql="SELECT Codigo FROM P_MEDIAPAGO WHERE (NIVEL=1)";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                codpe=dt.getInt(0);
            } else {
                msgbox("No está configurado pago efectivo, no se puede aplicar ");return;
            }

            sql="SELECT Codigo,Nombre FROM P_MEDIAPAGO WHERE (NIVEL=4)";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                codpc=dt.getInt(0);
                nomc=dt.getString(1);
            } else {
                msgbox("No está configurado pago con tarjeta, no se puede aplicar ");return;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return;
        }


        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM D_facturap WHERE (corel='"+corel_fact+"')");

            clsD_facturapObj D_facturapObj=new clsD_facturapObj(this,Con,db);

            if (pagoe>0) {

                ins.init("D_FACTURAP");

                ins.add("COREL", corel_fact);
                ins.add("ITEM",cc);
                ins.add("ANULADO", false);
                ins.add("EMPRESA", gl.emp);
                ins.add("CODPAGO", codpe);
                ins.add("TIPO", "E");
                ins.add("VALOR", pagoe);
                ins.add("DESC1", "");
                ins.add("DESC2", "");
                ins.add("DESC3", "");
                ins.add("DEPOS", false);

                db.execSQL(ins.sql());
                cc++;
            }

            if (pagoc>0) {

                ins.init("D_FACTURAP");

                ins.add("COREL", corel_fact);
                ins.add("ITEM",cc);
                ins.add("ANULADO", false);
                ins.add("EMPRESA", gl.emp);
                ins.add("CODPAGO", codpc);
                ins.add("TIPO", "K");
                ins.add("VALOR", pagoc);
                ins.add("DESC1", ""+auth);
                ins.add("DESC2", ""+nomc);
                ins.add("DESC3", "");
                ins.add("DEPOS", false);

                db.execSQL(ins.sql());
            }

            selitem.estado=7;
            selitem.pago=monto;
            selitem.fechafin=du.getActDateTime();

            D_domicilio_entregaObj.update(selitem);

            db.setTransactionSuccessful();
            db.endTransaction();

            enviaEstadoCompleto();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void enviaEstadoCompleto() {
        String ss="",ssl="";
        String sf=du.univfechahora(du.getActDateTime());
        long ff=du.getActDateTime();
        int trprop=0;

        try {

            ssl="UPDATE D_DOMICILIO_ENC SET estado=7 WHERE (corel='"+selitem.corel_orden+"')";
            db.execSQL(ssl);

            ssl="UPDATE D_domicilio_entrega SET estado=7,FECHAFIN="+ff+" WHERE (corel='"+selitem.corel_orden+"')";
            db.execSQL(ssl);

            fbpe.updateState(selitem.corel_orden,7);

            if (selitem.idempresa==1) trprop=1;

            ss="UPDATE D_DOMICILIO_ENC SET estado=7,fecha_entrega='"+sf+"'," +
               "codigo_empresa_trans="+selitem.idempresa+",transporte_propio="+trprop+
               " WHERE (corel='"+selitem.corel_orden+"')";
            Intent intent = new Intent(DomPago.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",ss);
            startService(intent);

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
                    saveItem();break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private double calculaPago() {
        double val;

        try {
            lblpago.setText("Total pago: "+mu.frmcur(0));

            auth=txtauth.getText().toString();

            try {
                val=Double.parseDouble(txtef.getText().toString());
                pagoe =val;
            } catch (Exception e) {
                pagoe =0;
            }

            try {
                val=Double.parseDouble(txtcard.getText().toString());
                pagoc =val;
            } catch (Exception e) {
                pagoc =0;
            }

            val= pagoe + pagoc;
            val= mu.round2(val);

            lblpago.setText("Total pago: "+mu.frmcur(val));

            return val;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return 0;
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            D_domicilio_entregaObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

}