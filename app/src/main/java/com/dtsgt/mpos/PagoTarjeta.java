package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PagoTarjeta extends PBase {

    private EditText txtMonto, txtAut;
    private TextView lblTipo;

    final String[] selitems = new String[5];

    private double monto;
    private String tipo="Visa",aut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_tarjeta);

        super.InitBase();

        lblTipo = (TextView) findViewById(R.id.textView165);
        txtMonto = (EditText) findViewById(R.id.editText2);
        txtAut = (EditText) findViewById(R.id.editText1);

        monto=gl.total_pago;
        txtMonto.setText(""+monto);
        lblTipo.setText(tipo);txtAut.setText("");txtAut.requestFocus();

        llenaTipos();

        setHandlers();
    }

    //region Events

    public void doSave(View view) {
        save();
    }

    public void doExit(View view) {
        finish();
    }

    public void doList(View view) {
        listaTipos();
    }

    private void setHandlers(){

        try {

            txtMonto.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (!txtMonto.getText().toString().isEmpty()) txtAut.requestFocus();
                        return true;
                    }
                    return false;
                }
            });

            txtAut.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        if (!txtAut.getText().toString().isEmpty()) save();
                        return true;
                    }
                    return false;
                }
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Main

    private void save() {
        Cursor dt;
        double mto=0;

        try {
            mto=Double.parseDouble(txtMonto.getText().toString());
            if (mto<=0) {
                toast("Monto incorrecto");return;
            }
            if (mto>monto) {
                toast("Monto major que pago");txtMonto.setText(""+monto);txtAut.requestFocus();return;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        if (txtAut.getText().toString().isEmpty()) {
            toast("Falta # autorización");txtAut.requestFocus();return;
        }

        try {
            int codpago=0;

            sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 4";
            dt = Con.OpenDT(sql);
            if(dt!=null) {
                if(dt.getCount()>0){
                    dt.moveToFirst();
                    codpago=dt.getInt(0);
                }
                dt.close();
            }

            int item=1;

            sql="SELECT MAX(ITEM) FROM T_PAGO";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                item=dt.getInt(0)+1;
            }

            ins.init("T_PAGO");

            ins.add("ITEM",item);
            ins.add("CODPAGO",codpago);
            ins.add("TIPO","K");
            ins.add("VALOR",mto);
            ins.add("DESC1",txtAut.getText().toString());
            ins.add("DESC2",tipo);
            ins.add("DESC3","");

            db.execSQL(ins.sql());

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void llenaTipos() {
        selitems[0]="Visa";
        selitems[1]="American Express";
        selitems[2]="Mastercard";
        selitems[3]="Discover";
        selitems[4]="Citibank";
    }

    private void listaTipos() {
        final AlertDialog Dialog;
        int sidx=-1;

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Tipo tarjeta");

        menudlg.setSingleChoiceItems(selitems,sidx,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                tipo=selitems[item];
                lblTipo.setText(tipo);
                txtAut.requestFocus();
                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion

}