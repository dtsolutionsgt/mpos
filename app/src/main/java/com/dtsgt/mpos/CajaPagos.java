package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_cajapagosObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsRepBuilder;

import java.util.ArrayList;


public class CajaPagos extends PBase {

    private long date;

    private clsClasses.clsP_cajapagos item=clsCls.new clsP_cajapagos();

    private Spinner cboProv, cboCPago;
    private EditText lblDate,lblDocAsoc,lblMonto,lblDesc;

    private ArrayList<String> spincode= new ArrayList<String>();
    private ArrayList<String> spinlist = new ArrayList<String>();
    private ArrayList<String> spincode1= new ArrayList<String>();
    private ArrayList<String> spinlist1 = new ArrayList<String>();

    private String dateS,dateAct,docAsoc,desc,montoS,provName,cPagoName;
    private double monto;
    private int proveedor,cPago,corel;

    //Impresion
    private clsRepBuilder rep;
    private CajaPagos.clsDocExist doc;
    private printer prn;
    private Runnable printclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja_pagos);

        cboProv = (Spinner) findViewById(R.id.cboProv);
        cboCPago = (Spinner) findViewById(R.id.cboCPago);
        lblDate = (EditText) findViewById(R.id.editText19);lblDate.setFocusable(false);
        lblDocAsoc = (EditText) findViewById(R.id.editText20);
        lblMonto = (EditText) findViewById(R.id.editText17);
        lblDesc = (EditText) findViewById(R.id.editText22);

        super.InitBase();
        addlog("CajaPagos",""+du.getActDateTime(),gl.vend);


        fillSpinner2();
        getdate();
        fillSpinner();
        setHandlers();

        rep=new clsRepBuilder(this,gl.prw,false,gl.peMon,gl.peDecImp, "");

        printclose= new Runnable() {
            public void run() {
                CajaPagos.super.finish();
            }
        };

        prn=new printer(this,printclose,gl.validimp);
        doc=new CajaPagos.clsDocExist(this,prn.prw,"");
    }

    private void setHandlers() {

        cboProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = spincode.get(position);
                    proveedor =  Integer.parseInt(scod);
                    provName = spinlist.get(position);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        cboCPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = spincode1.get(position);
                    cPago =  Integer.parseInt(scod);
                    cPagoName = spinlist1.get(position);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

    }

    //region Main

    private boolean Item() {

        try{
            item.empresa=gl.emp;
            item.sucursal=gl.tienda;
            item.ruta=gl.caja;
            item.corel=corel;
            item.item=0;
            item.anulado=0;
            item.fecha=date;
            item.tipo=cPago;
            item.proveedor=proveedor;
            item.monto=monto;
            item.nodocumento=docAsoc;
            item.referencia="";
            item.observacion=desc;
            item.vendedor=gl.vend;
            item.statcom="N";

            return true;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Item: "+e);return false;
        }

    }

    public void save(View view){

        clsP_cajapagosObj cpago = new clsP_cajapagosObj(this,Con,db);

        try{

            docAsoc = lblDocAsoc.getText().toString().trim();
            montoS = lblMonto.getText().toString().trim();
            desc = lblDesc.getText().toString().trim();

            if(!montoS.isEmpty()) monto = Double.parseDouble(montoS); else return;

            if(desc.isEmpty()) desc ="Sin Descripción";

            if(proveedor==0){
                msgbox("El proveedor no puede ir vacío");return;
            }

            if(cPago==0){
                msgbox("El concepto de pago no puede ir vacío");return;
            }

            cpago.fill(" ORDER BY COREL DESC");

            if(cpago.count==0) corel=1;

            if(cpago.count>0){
                corel = cpago.first().corel + 1;
            }

            if(Item()){

                cpago.add(item);

                Toast.makeText(this, "Pago realizado correctamente", Toast.LENGTH_LONG).show();

                super.finish();
            }else {
                Toast.makeText(this, "Error al guardar el pago", Toast.LENGTH_LONG).show();
            }

            doc.buildPrint("0", 0);
            GeneratePrint();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("save: "+e);
        }

    }

    //endregion

    //region Aux

    private void fillSpinner(){
        Cursor DT;
        String icode,iname;

        try {
            spincode1.clear();
            spinlist1.clear();

            spincode1.add("0");
            spinlist1.add("< Sin especificar >");

            sql="SELECT CODIGO,NOMBRE FROM P_CONCEPTOPAGO ORDER BY CODIGO";

            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                icode=DT.getString(0);
                iname=DT.getString(1);

                spincode1.add(icode);
                spinlist1.add(iname);

                DT.moveToNext();
            }

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        try {

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist1);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            cboCPago.setAdapter(dataAdapter);

            cboCPago.setSelection(0);

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            cboCPago.setSelection(0);
        }

    }

    private void fillSpinner2(){
        Cursor DT;
        String icode,iname;

        try {
            spincode.clear();
            spinlist.clear();

            spincode.add("0");
            spinlist.add("< Sin especificar >");

            sql="SELECT CODIGO,NOMBRE FROM P_PROVEEDOR ORDER BY CODIGO";

            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                icode=DT.getString(0);
                iname=DT.getString(1);

                spincode.add(icode);
                spinlist.add(iname);

                DT.moveToNext();
            }

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        try {

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            cboProv.setAdapter(dataAdapter);

            cboProv.setSelection(0);

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            cboProv.setSelection(0);
        }

    }

    private void getdate(){

        clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

        try{

            caja.fill();

            if(caja.count==0) throw new Exception();

            date = caja.first().fecha;

            dateS=du.univfechaReport(date);
            dateAct=du.getActDateStr();

            lblDate.setText(dateS);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("getDate: "+e);
        }
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }


    public void GeneratePrint(){
        try{

            app.doPrint();
            return;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("GeneratePrint: "+e);
        }
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

    //endregion

    //region DocPrint


    private class clsDocExist extends clsDocument {
        String fechaR="";
        int cantF,cantfF;
        double tot,totF;
        double porcentaje=0.0, comision;
        double totSinImp, sinImp,totSinImpF, impF;

        public clsDocExist(Context context, int printwidth, String archivo) {
            super(context, printwidth,gl.peMon,gl.peDecImp, archivo);

            pass = true;
            nombre="COMPROBANTE DE PAGO";
            numero="";
            serie="";
            ruta=gl.ruta;
            vendedor=gl.vendnom;
            cliente="";
            vendcod=gl.vend;
            fsfecha=du.getActDateStr();

        }

        protected boolean buildDetail() {
            int acc=1;
            String series="", fecha="";

            try {
                tot=0;
                totF=0;
                sinImp=0;
                totSinImpF=0;
                impF=0;
                fecharango=dateS;
                rep.add("Proveedor: "+provName);
                rep.add("Concepto Pago: "+cPagoName);
                rep.add("Fecha Inicio Caja: "+fecharango);
                rep.empty();

                rep.add("DESCRIPCION");
                rep.add("DOC ASOCIADO                   TOTAL");
                rep.line();
                rep.add(item.observacion);
                rep.addtot(item.nodocumento,item.monto);
                rep.line();
                rep.addtot("Total: ",item.monto);

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                msgbox(e.getMessage());
                return false;
            }

        }

        protected boolean buildFooter() {

            try {
                rep.empty();

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                return false;
            }

        }

    }


    //endregion


}
