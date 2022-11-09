package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.classes.clsP_cortesiaObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.extListDlg;

public class PagoTarjeta extends PBase {

    private EditText txtMonto, txtAut;
    private TextView lblTipo;


    private double monto;
    private String tipo="Mastercard",aut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_tarjeta);

        super.InitBase();

        lblTipo = (TextView) findViewById(R.id.textView165);
        txtMonto = (EditText) findViewById(R.id.editText2);
        txtAut = (EditText) findViewById(R.id.editText1);

        monto=gl.total_pago;
        txtMonto.setText(""+round2(monto));
        lblTipo.setText(tipo);txtAut.setText("");txtAut.requestFocus();

        setHandlers();
    }

    public double round2(double val){

        int ival;

        val=(double) (100*val);
        double rslt=Math.round(val);
        rslt=Math.floor(rslt);

        ival=(int) rslt;
        rslt=(double) ival;

        return (double) (rslt/100);
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

            txtAut.setOnKeyListener((v, keyCode, event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (txtAut.getText().toString().isEmpty()){
                        txtAut.setText("No_Aut_20221022");
                    }
                    save();
                    return true;
                }
                return false;
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
//            toast("Falta # autorización");
//            txtAut.requestFocus();
//            return;
            //#EJC20221022:No requerir número de autorización obligatorio
            txtAut.setText("NO_AUT_20221022");
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

    private void listaTipos() {

        try {

            clsP_mediapagoObj P_mediapagoObj=new clsP_mediapagoObj(this,Con,db);
            P_mediapagoObj.fill("WHERE (ACTIVO=1)");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(PagoTarjeta.this,"Media pago");
            listdlg.setLines(6);

            //#AT20221109114: Listar formas de pago desde tabla local.
            //listdlg.add(0,"Visa");
            String vNombreFormaPago="";
            int vCodigoFormaPago=0;

            if (P_mediapagoObj.count>0) {

                for (int i = 0; i <P_mediapagoObj.count; i++) {

                    vCodigoFormaPago=P_mediapagoObj.items.get(i).codigo;
                    vNombreFormaPago=P_mediapagoObj.items.get(i).nombre;
                    listdlg.add(vCodigoFormaPago,vNombreFormaPago);
                }

            }

//            listdlg.add(0,"American Express");
//            listdlg.add(0,"Mastercard");
//            listdlg.add(0,"Visa link");
//            listdlg.add(0,"Discover");
//            listdlg.add(0,"Citibank");
//            listdlg.add(0,"Transferencia");
//            listdlg.add(0,"Cheque");
//            listdlg.add(0,"Credito 15 dias");
//            listdlg.add(0,"Credito 30 dias");
//            listdlg.add(0,"Credito 60 dias");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        tipo=listdlg.getText(position);
                        lblTipo.setText(tipo);
                        txtAut.requestFocus();

                        if (listdlg.getCodigoInt(position)==1) {
                            gl.modo_cortesia=true;
                            finish();
                        }
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion

}