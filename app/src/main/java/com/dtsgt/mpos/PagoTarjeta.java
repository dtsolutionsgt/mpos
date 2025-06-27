package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_cortesiaObj;
import com.dtsgt.classes.clsP_hotel_brazaleteObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.extListDlg;

public class PagoTarjeta extends PBase {

    private EditText txtMonto, txtAut;
    private TextView lblTipo,lblAut,lblCred;

    private clsP_hotel_brazaleteObj P_hotel_brazaleteObj;

    private clsP_mediapagoObj P_mediapagoObj;
    private clsP_clienteObj P_clienteObj;

    private double monto,climite;
    private String tipo="",habitacion;
    private int cpago,pnivel,codcli;
    private Boolean modo_brazalete,valid_brazalete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pago_tarjeta);

            super.InitBase();

            lblTipo = (TextView) findViewById(R.id.textView165);
            txtMonto = (EditText) findViewById(R.id.editText2);
            txtAut = (EditText) findViewById(R.id.editText1);
            lblAut = (TextView) findViewById(R.id.textView154);
            lblCred = (TextView) findViewById(R.id.textView384);lblCred.setText("");

            monto=gl.total_pago;
            txtMonto.setText(""+round2(monto));
            lblTipo.setText(tipo);
            txtAut.setText("");txtAut.requestFocus();
            modo_brazalete=false;valid_brazalete=false;

            P_hotel_brazaleteObj=new clsP_hotel_brazaleteObj(this,Con,db);
            P_clienteObj=new clsP_clienteObj(this,Con,db);

            setHandlers();

            listaTipos();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
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

            txtAut.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {

                        String input = txtAut.getText().toString();

                        if (modo_brazalete) {
                            try {
                                if (input.length()<4) throw new Exception();
                                procesaBrazalete(input);
                            } catch (Exception e) {
                                mu.msgbox("Brazalete incorrecto");
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });

            /*
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
            */

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Main

    private void save() {
        Cursor dt;
        double mto=0;

        if (modo_brazalete) {
            saveBrazalete();
            return;
        }

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

            /*
            sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 4";
            dt = Con.OpenDT(sql);
            if(dt!=null) {
                if(dt.getCount()>0){
                    dt.moveToFirst();
                    codpago=dt.getInt(0);
                }
                dt.close();
            }
            */

            if (cpago==0) {
                msgAskExit("No se logro aplicar pago.\nPor favor repite el pago.");return;
            }

            codpago=cpago;
            P_mediapagoObj.fill("WHERE (codigo="+cpago+")");
            if (P_mediapagoObj.count>0) tipo=P_mediapagoObj.first().nombre;

            sql="SELECT MAX(ITEM) FROM T_PAGO";
            dt=Con.OpenDT(sql);
            int item=1;
            if (dt.getCount()>0) {
                dt.moveToFirst();
                item=dt.getInt(0)+1;
            }

            String tpago="K";
            if (pnivel==2 || pnivel==3) tpago="C";

            ins.init("T_PAGO");
            ins.add("ITEM",item);
            ins.add("CODPAGO",codpago);
            ins.add("TIPO",tpago);
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

    private void saveBrazalete() {
        Cursor dt;
        double mto=0;

        try {
            if (!valid_brazalete) {
                msgbox("Brazalete inválido");return;
            }

            mto=Double.parseDouble(txtMonto.getText().toString());
            if (mto<=0) {
                msgbox("Monto incorrecto");return;
            }

            if (mto>monto) {
                msgbox("Monto major que pago");txtMonto.setText(""+monto);txtAut.requestFocus();
                return;
            }

            if (mto>climite) {
                msgbox("Monto major que crédito disponible");txtMonto.setText(""+monto);txtAut.requestFocus();
                return;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return;
        }

        try {
            int codpago=0;

            if (cpago==0) {
                msgAskExit("No se logro aplicar pago.\nPor favor repite el pago.");return;
            }

            codpago=cpago;
            P_mediapagoObj.fill("WHERE (codigo="+cpago+")");
            if (P_mediapagoObj.count>0) tipo=P_mediapagoObj.first().nombre;

            sql="SELECT MAX(ITEM) FROM T_PAGO";
            dt=Con.OpenDT(sql);
            int item=1;
            if (dt.getCount()>0) {
                dt.moveToFirst();
                item=dt.getInt(0)+1;
            }

            ins.init("T_PAGO");
            ins.add("ITEM",item);
            ins.add("CODPAGO",codpago);
            ins.add("TIPO","C");
            ins.add("VALOR",mto);
            ins.add("DESC1",txtAut.getText().toString());
            ins.add("DESC2",tipo);
            ins.add("DESC3","");

            db.execSQL(ins.sql());

            gl.codigo_cliente=codcli;

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listaTipos() {

        try {

            gl.modo_cortesia=false;
            P_mediapagoObj=new clsP_mediapagoObj(this,Con,db);
            P_mediapagoObj.fill("WHERE (NIVEL>1) AND (ACTIVO=1) ORDER BY NOMBRE");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(PagoTarjeta.this,"Tipo pago");
            listdlg.setLines(6);

            if (P_mediapagoObj.count>0) {
                tipo="";
                for (int i = 0; i <P_mediapagoObj.count; i++) {
                    if (P_mediapagoObj.items.get(i).nivel!=5) {
                        listdlg.add(P_mediapagoObj.items.get(i).codigo, P_mediapagoObj.items.get(i).nombre);
                    }
                }
            } else {
                listdlg.add(0,"Tarjeta credito");tipo="Tarjeta credito";
            }

            lblTipo.setText(tipo);
            modo_brazalete=false;valid_brazalete=false;
            lblCred.setText("");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        cpago=listdlg.getCodigoInt(position);
                        tipo=listdlg.getText(position);
                        lblTipo.setText(tipo);
                        txtAut.requestFocus();

                        if (P_mediapagoObj.items.get(position).nivel==6) {
                            gl.modo_cortesia=true;
                            finish();
                        }

                        if (P_mediapagoObj.items.get(position).nivel==8) {
                            modo_brazalete=true;
                            lblCred.setText("Credito disponible:");
                        }

                        P_mediapagoObj.fill("WHERE (codigo="+cpago+")");
                        String lbnivel="Nota";
                        pnivel=P_mediapagoObj.first().nivel;
                        switch (pnivel) {
                            case 2:
                                lbnivel="#Cheque";break;
                            case 3:
                                lbnivel="#Cheque";break;
                            case 4:
                                lbnivel="#Autorización";break;
                            case 8:
                                lbnivel="Brazalete";break;
                        }
                        lblAut.setText(lbnivel);

                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                    finish();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void procesaBrazalete(String cb) {
        try {
            valid_brazalete=false;

            P_hotel_brazaleteObj.fill("WHERE (BARRA='"+cb+"')");
            if (P_hotel_brazaleteObj.count==0) {
                msgbox("Brazalete incorrecto");return;
            }

            habitacion=P_hotel_brazaleteObj.first().habitacion;
            codcli=P_hotel_brazaleteObj.first().codigo_cliente;
            valid_brazalete=true;

            climite=0;
            P_clienteObj.fill("WHERE (CODIGO_CLIENTE="+codcli+")");
            if (P_clienteObj.count>0) climite=P_clienteObj.first().limitecredito;

            try {
                sql="SELECT SUM(D_FACTURA.TOTAL) FROM  D_FACTURA " +
                        "INNER JOIN D_FACTURAP ON D_FACTURAP.COREL=D_FACTURA.COREL " +
                        "WHERE (D_FACTURA.ANULADO=0) AND " +
                        "(D_FACTURA.CLIENTE="+codcli+") AND (D_FACTURAP.CODPAGO="+cpago+")";

                Cursor dt=Con.OpenDT(sql);
                dt.moveToFirst();
                double totc=dt.getDouble(0);

                climite=climite-totc;
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                climite=0;
            }

            if (climite<0) climite=0;
            s="Habitación: "+habitacion+"\n" +
              "Crédito disponible: "+mu.frmcur(climite);

            lblCred.setText(s);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    public double round2(double val){

        int ival;

        val=(double) (100*val);
        double rslt=Math.round(val);
        rslt=Math.floor(rslt);

        ival=(int) rslt;
        rslt=(double) ival;

        return (double) (rslt/100);
    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  );

            dialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_hotel_brazaleteObj.reconnect(Con,db);
            P_clienteObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}