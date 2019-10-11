package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsRepBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CierreX extends PBase {

    private clsRepBuilder rep;
    private Runnable printclose;
    private printer prn;
    private CierreX.clsDocExist doc;

    private TextView txtbtn,lblFact,lblTit;
    private CheckBox FactxDia, VentaxDia, VentaxProd, xFPago, xFam, VentaxVend, MBxProd, MBxFam, ClienteCon, ClienteDet;
    private int bFactxDia=1, bVentaxDia=2, bVentaxProd=3, bxFPago=4, bxFam=5, bVentaxVend=6, bMBxProd=7, bMBxFam=8, bClienteCon=9, bClienteDet=10, sw=0;;
    private boolean report, enc=true;

    private clsClasses.clsReport item;
    private clsClasses.clsBonifProd itemZ;

    private ArrayList<clsClasses.clsReport> itemR= new ArrayList<clsClasses.clsReport>();
    private ArrayList<clsClasses.clsBonifProd> itemRZ= new ArrayList<clsClasses.clsBonifProd>();

    private Long dateini, datefin;

    //Reporte Z
    private int codPago;
    private String nombrePago;
    private Double valorPago, montoIni, montoFin, montoDif, Fondo;
    //Reporte Z

    private String condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_x);

        super.InitBase();
        addlog("Cierres",""+du.getActDateTime(),gl.vend);

        lblTit = (TextView) findViewById(R.id.lblTit);
        txtbtn = (TextView) findViewById(R.id.txtBtn);
        FactxDia = (CheckBox) findViewById(R.id.checkBox11); FactxDia.setChecked(true);
        VentaxDia = (CheckBox) findViewById(R.id.checkBox12); VentaxDia.setChecked(true);
        VentaxProd = (CheckBox) findViewById(R.id.checkBox13); VentaxProd.setChecked(true);
        xFPago = (CheckBox) findViewById(R.id.checkBox14); xFPago.setChecked(true);
        xFam = (CheckBox) findViewById(R.id.checkBox15); xFam.setChecked(true);
        VentaxVend = (CheckBox) findViewById(R.id.checkBox16); VentaxVend.setChecked(true);
        MBxProd = (CheckBox) findViewById(R.id.checkBox17); MBxProd.setChecked(true);
        MBxFam = (CheckBox) findViewById(R.id.checkBox18); MBxFam.setChecked(true);
        ClienteCon = (CheckBox) findViewById(R.id.checkBox19); ClienteCon.setChecked(true);
        ClienteDet = (CheckBox) findViewById(R.id.checkBox20); ClienteDet.setChecked(true);
        lblFact = (TextView) findViewById(R.id.txtFact2);

        datefin = du.getActDateTime();
        dateini = du.getActDate();

        if(gl.reportid==9){
            lblTit.setText("Cierre X");
        }else if(gl.reportid==10){
            lblTit.setText("Cierre Z");
        }

        report = false;

        setHandlers();

        txtbtn.setText("GENERAR");

        rep=new clsRepBuilder(this,gl.prw,false,gl.peMon,gl.peDecImp, "");

        printclose= new Runnable() {
            public void run() {
                CierreX.super.finish();
            }
        };

        prn=new printer(this,printclose,gl.validimp);
        doc=new CierreX.clsDocExist(this,prn.prw,"");

        lblFact.setMovementMethod(new ScrollingMovementMethod());
    }


    //region Events

    public void printDoc() {
        try{
            app.doPrint();
            //printEpson();
            //prn.printask();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void printEpson() {
        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.dts.epsonprint");
            intent.putExtra("mac","BT:00:01:90:85:0D:8C");
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
            intent.putExtra("askprint",1);
            this.startActivity(intent);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("printEpson" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if(report){
            msgAskExit("Está seguro de salir");
        }else {
            msgbox("No es posible salir sin generar el reporte antes");
        }

    }

    //endregion

    //region Main

    private void setHandlers(){

        FactxDia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(FactxDia.isChecked()){
                    bFactxDia = 1;
                } else {
                    bFactxDia = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        VentaxDia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(VentaxDia.isChecked())  {
                    bVentaxDia = 2;
                } else {
                    bVentaxDia = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        VentaxProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(VentaxProd.isChecked()){
                    bVentaxProd = 3;
                } else{
                    bVentaxProd = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        xFPago.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(xFPago.isChecked()){
                    bxFPago = 4;
                } else{
                    bxFPago = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        xFam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(xFam.isChecked()){
                    bxFam = 5;
                } else{
                    bxFam = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        VentaxVend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(VentaxVend.isChecked()){
                    bVentaxVend = 6;
                } else{
                    bVentaxVend = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        MBxProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MBxProd.isChecked()){
                    bMBxProd = 7;
                } else{
                    bMBxProd = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        MBxFam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MBxFam.isChecked()){
                    bMBxFam = 8;
                } else{
                    bMBxFam = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        ClienteCon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ClienteCon.isChecked()){
                    bClienteCon = 9;
                } else{
                    bClienteCon = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });

        ClienteDet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ClienteDet.isChecked()){
                    bClienteDet = 10;
                } else{
                    bClienteDet = 0;
                }
                report = false;
                txtbtn.setText("GENERAR");
                lblFact.setText("");
            }
        });
    }

    private boolean fillItems(){
        Cursor dt;

        try{
            itemR.clear();

            if(gl.reportid==10) reporteZ();

            for(int i=0; i<10; i++){
                if(i==0) sw=bFactxDia;
                if(i==1) sw=bVentaxDia;
                if(i==2) sw=bVentaxProd;
                if(i==3) sw=bxFPago;
                if(i==4) sw=bxFam;
                if(i==5) sw=bVentaxVend;
                if(i==6) sw=bMBxProd;
                if(i==7) sw=bMBxFam;
                if(i==8) sw=bClienteCon;
                if(i==9) sw=bClienteDet;

                switch (sw){
                    case 0:
                        sql="00";
                        break;

                    case 1:
                        if(gl.reportid==9){
                            condition =" WHERE ANULADO='N' AND KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE ANULADO='N' AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', SERIE, 0, '', '', '', COUNT(COREL), IMPMONTO, SUM(TOTAL), FECHA " +
                                "FROM D_FACTURA "+
                                condition+
                                "GROUP BY SERIE, IMPMONTO, FECHA " +
                                "ORDER BY FECHA";
                        break;
                    case 2:
                        if(gl.reportid==9){
                            condition =" WHERE ANULADO='N' AND KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE ANULADO='N' AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', SERIE, COUNT(COREL), '', '', '', 0, 0, " +
                                "SUM(TOTAL), FECHA " +
                                "FROM D_FACTURA "+
                                condition+
                                //"FECHA>="+ dateini +" AND FECHA<="+datefin+" " +
                                "GROUP BY FECHA, SERIE";
                        break;
                    case 3:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, D.PRODUCTO, P.DESCCORTA, D.UMVENTA, " +
                                " SUM(D.CANT), 0,SUM(D.TOTAL), F.FECHA " +
                                " FROM P_LINEA L INNER JOIN P_PRODUCTO P ON L.CODIGO = P.LINEA " +
                                " INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO  " +
                                " INNER JOIN D_FACTURA F ON F.COREL = D.COREL  " +
                                condition+
                                " GROUP BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA "+
                                " ORDER BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA ";
                        break;
                    case 4:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, '', M.NOMBRE, '', COUNT(F.COREL), 0,SUM(F.TOTAL), 0 FROM P_MEDIAPAGO M " +
                                "INNER JOIN D_FACTURAP P ON P.CODPAGO = M.CODIGO " +
                                "INNER JOIN D_FACTURA F ON F.COREL = P.COREL "+
                                condition+
                                "AND F.ANULADO='N' "+
                                " GROUP BY M.NOMBRE";
                        break;

                    case 5:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, '', L.NOMBRE, '', SUM(D.CANT), 0, SUM(D.TOTAL), 0 FROM P_LINEA L " +
                                "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO " +
                                "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition +
                                " GROUP BY L.NOMBRE";
                        break;

                    case 6:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT V.CODIGO, '', 0, '', V.NOMBRE, '', COUNT(COREL), V.NIVELPRECIO, SUM(F.TOTAL), 0 FROM VENDEDORES V " +
                                "INNER JOIN D_FACTURA F ON F.VENDEDOR = V.CODIGO " +
                                condition+
                                " GROUP BY V.CODIGO, V.NOMBRE, V.NIVELPRECIO";
                        break;

                    case 7:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT D.PRODUCTO, '', 0, '',  P.DESCCORTA, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM D_FACTURAD D " +
                                "INNER JOIN P_PRODUCTO P ON D.PRODUCTO = P.CODIGO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition+
                                " GROUP BY D.PRODUCTO, P.DESCCORTA, P.COSTO, D.PRECIO";
                        break;

                    case 8:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT L.CODIGO, '', 0, '', L.NOMBRE, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM P_LINEA L " +
                                "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO " +
                                "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition+
                                "AND P.LINEA=L.CODIGO "+
                                " GROUP BY L.NOMBRE";
                        break;

                    case 9:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT C.CODIGO, '', 0, '', C.NOMBRE, '',  COUNT(DISTINCT F.COREL), 0, SUM(D.PRECIO*D.CANT), F.FECHA " +
                                "FROM P_CLIENTE C " +
                                "INNER JOIN D_FACTURA F ON C.CODIGO = F.CLIENTE " +
                                "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                                condition+
                                " GROUP BY C.CODIGO, C.NOMBRE, F.FECHA";
                        break;

                    case 10:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO='N' AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO='N' AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT F.COREL, C.CODIGO, 0, P.CODIGO, P.DESCCORTA, C.NOMBRE, SUM(D.CANT), D.PRECIO, D.PRECIO*D.CANT, F.FECHA, 0 " +
                                "FROM D_FACTURA F " +
                                "INNER JOIN P_CLIENTE C ON C.CODIGO = F.CLIENTE "+
                                "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                                "INNER JOIN P_PRODUCTO P ON P.CODIGO = D.PRODUCTO " +
                                condition+
                                " GROUP BY C.CODIGO, C.NOMBRE, F.COREL, F.FECHA, P.DESCCORTA, D.PRECIO";
                        break;

                    default:
                        msgbox("Error, al identificar el tipo de reporte, cierre la ventana e intentelo de nuevo");return false;
                }

                if(!sql.equals("00")){

                    dt = Con.OpenDT(sql);

                    if(dt==null) {
                        msgbox("Ocurrío un error, vuelva a intentarlo");return false;
                    }

                    if(dt.getCount()!=0){
                        dt.moveToFirst();

                        while(!dt.isAfterLast()){

                            item = clsCls.new clsReport();

                            item.corel = dt.getString(0);
                            item.serie = dt.getString(1);
                            item.correl = dt.getInt(2);
                            item.codProd = dt.getString(3);
                            item.descrip = dt.getString(4);
                            item.um = dt.getString(5);
                            item.cant = dt.getInt(6);
                            item.imp = dt.getDouble(7);
                            item.total = dt.getDouble(8);
                            item.fecha = dt.getLong(9);
                            item.tipo = sw;

                            itemR.add(item);

                            dt.moveToNext();
                        }
                    }

                }

            }

            return true;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("fillItems: "+e);
            return false;
        }
    }

    private void reporteZ(){
        Cursor dt;

        try{
            sql="SELECT M.CODIGO, M.NOMBRE, C.FONDOCAJA, 0, 0, C.MONTOINI, C.MONTOFIN, C.MONTODIF, 0 " +
                    "FROM P_CAJACIERRE C " +
                    "INNER JOIN P_MEDIAPAGO M ON C.CODPAGO = M.CODIGO " +
                    "WHERE C.COREL = "+ gl.corelZ +
                    " GROUP BY M.CODIGO";

            dt = Con.OpenDT(sql);

            if(dt==null) {
                msgbox("Ocurrío un error en reporte Z, vuelva a intentarlo");
            }

            itemRZ.clear();

            if(dt.getCount()!=0){
                dt.moveToFirst();

                Fondo = dt.getDouble(2);

                while(!dt.isAfterLast()){

                    itemZ = clsCls.new clsBonifProd();

                    itemZ.id=dt.getString(0);
                    itemZ.nombre=dt.getString(1);
                    itemZ.prstr="";
                    itemZ.flag=dt.getInt(3);
                    itemZ.cant=dt.getDouble(4);
                    itemZ.cantmin=dt.getDouble(5);
                    itemZ.disp=dt.getDouble(6);
                    itemZ.precio=dt.getDouble(7);
                    itemZ.costo=dt.getDouble(8);

                    itemRZ.add(itemZ);

                    dt.moveToNext();
                }
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("reporteZ: "+e);
        }
    }

    public void GeneratePrintCierre(View view){
        try{
            if(!report) {
                if(fillItems()){
                    if (itemR.size() == 0) {
                        msgbox("No se ha realizado ninguna venta desde el ultimo cierre Z.");
                        return;
                    }
                    doc.buildPrint("0", 0);
                    getTXT();
                    txtbtn.setText("IMPRIMIR");
                    report = true;
                }else {
                    return;
                }

            }else {
                printDoc();
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("GeneratePrint: "+e);
        }
    }

    public void getTXT(){
        StringBuilder text = new StringBuilder();
        try {
            File Storage = Environment.getExternalStorageDirectory();
            File file = new File(Storage,"print.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close() ;
        }catch (IOException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("getTXT: "+e);
            e.printStackTrace();
        }

        try{
            lblFact.setText(text);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("getTXT setText: "+e);
        }

    }

    //endregion

    //region clase clsDocExist

    private class clsDocExist extends clsDocument {
        String fechaR="";
        String test;
        int cantF,cantfF,SumaCant;
        int count1, count2, count3, count4, count5, count6, count7, count8, count9, count10;
        double tot,totF;
        double porcentaje=0.0, comision;
        double totSinImp, sinImp,totSinImpF, impF;

        public clsDocExist(Context context, int printwidth, String archivo) {
            super(context, printwidth,gl.peMon,gl.peDecImp, archivo);

            pass = true;

            if(enc){
                nombre = "REPORTE DE CIERRE";enc=false;
            }

            numero="";
            serie="";
            ruta=gl.ruta;
            vendedor=gl.vendnom;
            cliente="";
            vendcod=gl.vend;
            fsfecha=du.getActDateStr();

        }

        protected boolean buildDetail() {
            int acc1=1, acc2=1, acc3=1, acc4=1, acc5=1, acc6=1, acc7=1, acc9=1, acc10=1,counter=0;
            String series="", fecha="";

            try {
                fecharango="Del "+du.univfechaReport(dateini)+" Hasta "+du.univfechaReport(datefin);
                rep.add(fecharango);
                rep.empty();

                count1 = 0;
                count2 = 0;
                count3 = 0;
                count4 = 0;
                count5 = 0;
                count6 = 0;
                count7 = 0;
                count8 = 0;
                count9 = 0;
                count10 = 0;

                for(int j = 0; j<itemR.size(); j++){
                    if(itemR.get(j).tipo==1) count1 +=1;
                    if(itemR.get(j).tipo==2) count2 +=1;
                    if(itemR.get(j).tipo==3) count3 +=1;
                    if(itemR.get(j).tipo==4) count4 +=1;
                    if(itemR.get(j).tipo==5) count5 +=1;
                    if(itemR.get(j).tipo==6) count6 +=1;
                    if(itemR.get(j).tipo==7) count7 +=1;
                    if(itemR.get(j).tipo==8) count8 +=1;
                    if(itemR.get(j).tipo==9) count9 +=1;
                    if(itemR.get(j).tipo==10) count10 +=1;
                }

                count2 += count1;
                count3 += count2;
                count4 += count3;
                count5 += count4;
                count6 += count5;
                count7 += count6;
                count8 += count7;
                count9 += count8;
                count10 += count9;

                if(gl.reportid==10){
                    rep.add("FONDO CAJA:        Q."+Fondo);
                    rep.empty();
                    rep.add("         REPORTE DE CUADRE");
                    rep.add("CODIGO  M.PAGO");
                    rep.add("MONT.INI        MONT.FIN       DIF.");
                    rep.line();

                    tot=0;totF=0;totSinImp=0;

                    for(int j=0; j<itemRZ.size(); j++){

                        rep.addtot(itemRZ.get(j).id,itemRZ.get(j).nombre);
                        rep.add4lrrTotZ(itemRZ.get(j).cantmin,itemRZ.get(j).disp,itemRZ.get(j).precio);

                        tot+=itemRZ.get(j).cantmin;
                        totF+=itemRZ.get(j).disp;
                        totSinImp+=itemRZ.get(j).precio;
                    }

                    for(int a=0; a<itemRZ.size(); a++){
                        if(itemRZ.get(a).id.equals("1")){
                            counter+=1;
                        }
                    }

                    if (counter==0){
                        tot+=Fondo;
                        totF+=Fondo;
                    }

                    rep.line();
                    rep.add4lrrTotZ(tot,totF,totSinImp);

                    rep.add("Totales sumados con el fondo de caja");

                    tot=0;totF=0;totSinImp=0;
                    rep.empty();
                }

                for (int i = 0; i <itemR.size(); i++) {

                    //Reporte 1
                    if(itemR.get(i).tipo==1){

                        test = "Reporte 1";
                        if(acc1==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            rep.empty();
                            rep.add("       REPORTE FACTURAS POR DIA");
                            rep.add("Cant.Fact   Costo  Impuesto    Total");
                            rep.line();
                            rep.add("             "+du.sfecha(itemR.get(i).fecha));
                            acc1 = 2;
                        }

                        if(series!=itemR.get(i).serie){
                            rep.add("--------(    Serie "+itemR.get(i).serie+"    )------------");
                        }

                        series=itemR.get(i).serie;

                        totSinImp = itemR.get(i).total - itemR.get(i).imp;
                        rep.add3Tot(itemR.get(i).cant,totSinImp,itemR.get(i).imp, itemR.get(i).total);

                        tot += itemR.get(i).total;
                        sinImp += totSinImp;
                        impF += itemR.get(i).imp;
                        cantF += itemR.get(i).cant;

                        if(i+1==count1){

                            totF += tot;
                            SumaCant += cantF;
                            totSinImpF += sinImp;
                            rep.line();
                            rep.add3Tot(SumaCant, totSinImpF, impF, totF);

                        }else {
                            if (itemR.get(i).fecha != itemR.get(i + 1).fecha) {
                                rep.line();
                                rep.add3Tot(cantF, sinImp, impF, tot);
                                totF += tot;
                                SumaCant += cantF;
                                totSinImpF += sinImp;

                                cantF = 0;
                                tot = 0;
                                sinImp = 0;

                                rep.empty();
                                rep.add("             " + du.sfecha(itemR.get(i+1).fecha));
                            }
                        }

                        //Reporte 2
                    } else if(itemR.get(i).tipo==2){

                        test = "Reporte 2";
                        fecha = du.sfecha(itemR.get(i).fecha);
                        if(acc2==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            rep.empty();
                            rep.add("      REPORTE DE VENTAS POR DIA");
                            rep.line();
                            rep.add("Fecha      Serie   Cant.Fact   Total");
                            rep.line();
                            acc2 = 2;
                        }

                        if(i!=0){
                            if(itemR.get(i).fecha!=itemR.get(i-1).fecha){
                                rep.line();
                                rep.add4lrrTot("","",Integer.toString(cantF),tot);
                                rep.empty();
                                tot = 0;
                                cantF =0;
                            }
                        }

                        if(i!=0){
                            if(itemR.get(i).fecha==itemR.get(i-1).fecha){
                                if(i-1<count2){
                                    rep.add4lrrTot(fecha,itemR.get(i).serie,Integer.toString(itemR.get(i).correl),itemR.get(i).total);
                                }else {
                                    rep.add4lrrTot("",itemR.get(i).serie,Integer.toString(itemR.get(i).correl),itemR.get(i).total);
                                }

                            }else {
                                rep.add4lrrTot(fecha,itemR.get(i).serie,Integer.toString(itemR.get(i).correl),itemR.get(i).total);
                            }
                        }else {
                            rep.add4lrrTot(fecha,itemR.get(i).serie,Integer.toString(itemR.get(i).correl),itemR.get(i).total);
                        }

                        tot += itemR.get(i).total;
                        totF += itemR.get(i).total;
                        cantF += itemR.get(i).correl;
                        cantfF += itemR.get(i).correl;

                        if(i+1==count2){
                            rep.line();
                            rep.add4lrrTot("Total: ","",Integer.toString(cantfF),totF);
                            rep.empty();
                            tot = 0;
                        }

                        //Reporte 3
                    }else if(itemR.get(i).tipo==3){

                        test = "Reporte 3";
                        if(acc3==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            for (int a = 0; a <count3-count2; a++) {
                                totF += itemR.get(a+count2).total;
                            }

                            rep.empty();
                            rep.add("      REPORTE VENTA POR PRODUCTO");
                            rep.add("Cod   Descripcion");
                            rep.add("Cant        UM       Total        %");
                            rep.line();
                            acc3 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        rep.addtot(itemR.get(i).codProd,itemR.get(i).descrip);
                        rep.add4lrrTotPorc(Integer.toString(itemR.get(i).cant), itemR.get(i).um,itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count3){
                            rep.line();
                            rep.add4lrrTotPorc(Integer.toString(SumaCant), "",totF,0.0);
                        }

                        //Reporte 4
                    }else if(itemR.get(i).tipo==4){

                        test = "Reporte 4";
                        if(acc4==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            for (int a = 0; a <count4-count3; a++) {
                                totF += itemR.get(a+count3).total;
                            }

                            rep.empty();
                            rep.add("     REPORTE POR FORMA DE PAGO");
                            rep.add("Forma    Cantidad");
                            rep.add("Pago      Factura     Total     %");
                            rep.line();
                            acc4 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;
                        rep.add4lrrTotPorc(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count4){
                            rep.line();
                            rep.add4lrrTotPorc("",Integer.toString(SumaCant),totF,0.0);
                            rep.empty();
                        }

                    }else if(itemR.get(i).tipo==5){

                        test = "Reporte 5";
                        if(acc5==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            for (int a = 0; a <count5-count4; a++) {
                                totF += itemR.get(a+count4).total;
                            }

                            rep.empty();
                            rep.add("      REPORTE VENTA POR FAMILIA");
                            rep.add("Seccion   Cant.Art    Total      %");
                            rep.line();
                            acc5 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        rep.add4lrrTotPorc(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count5){
                            rep.line();
                            rep.add4lrrTotPorc("",Integer.toString(SumaCant),totF,0.0);
                            rep.empty();
                        }

                    } else if(itemR.get(i).tipo==6){

                        test = "Reporte 6";
                        if(acc6==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            for (int a = 0; a <count6-count5; a++) {

                                totF += itemR.get(a+count5).total;
                            }

                            comision = (itemR.get(i).total * itemR.get(i).imp) / 100;

                            rep.empty();
                            rep.add("    REPORTE VENTAS POR VENDEDOR");
                            rep.add("Codigo     Nombre");
                            rep.add("Cant       %       Total    Comision");
                            rep.line();
                            acc6 = 2;
                        }

                        rep.addtot(itemR.get(i).corel, itemR.get(i).descrip);
                        rep.add4lrrTot(Integer.toString(itemR.get(i).cant), itemR.get(i).imp+"%", itemR.get(i).total, comision);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count6){
                            rep.line();
                            rep.add4lrrTot(Integer.toString(SumaCant), "", totF, comision);
                            rep.empty();
                        }

                    }else if(itemR.get(i).tipo==7 || itemR.get(i).tipo==8){

                        test = "Reporte 7-8";
                        if(acc7==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            if(itemR.get(i).tipo==7){
                                for (int a = 0; a <count7-count6; a++) {

                                    totF += itemR.get(a+count6).total;
                                    impF += itemR.get(a+count6).imp;
                                }
                            }else if(itemR.get(i).tipo==8){
                                for (int a = 0; a <count8-count7; a++) {

                                    totF += itemR.get(a+count7).total;
                                    impF += itemR.get(a+count7).imp;
                                }
                            }

                            rep.empty();
                            if(itemR.get(i).tipo==7) rep.add("MARGEN Y BENEFICION POR PRODUCTO");
                            if(itemR.get(i).tipo==8) rep.add("MARGEN Y BENEFICION POR FAMILIA");
                            rep.add("Codigo     Nombre");
                            rep.add("Venta      Costo    Beneficio    %");
                            rep.line();
                            acc7 = 2;
                        }

                        tot = itemR.get(i).total - itemR.get(i).imp;
                        porcentaje = (100/itemR.get(i).total) * tot;
                        totSinImpF+=tot;

                        rep.addtot(itemR.get(i).corel, "    "+itemR.get(i).descrip);
                        rep.add4(itemR.get(i).total, itemR.get(i).imp, tot, porcentaje);

                        if(itemR.get(i).tipo==7){
                            if(i+1==count7){
                                rep.line();
                                rep.add4(totF, impF, totSinImpF, 0.0);
                                rep.empty();
                                acc7=1;
                            }
                        }else if(itemR.get(i).tipo==8){
                            if(i+1==count8){
                                rep.line();
                                rep.add4(totF, impF, totSinImpF, 0.0);
                                rep.empty();
                                acc7=1;
                            }
                        }

                    }else if(itemR.get(i).tipo==9){

                        test = "Reporte 9";
                        if(acc9==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            cantF=0;
                            impF=0;
                            SumaCant=0;

                            rep.empty();
                            rep.add("REPORTE VENTAS POR CLIENTE CONSOLIDADO");
                            rep.add("Codigo        Nombre");
                            rep.add("Fecha       Cant.Fact       Total");
                            rep.line();
                            acc9 = 2;
                        }

                        fechaR = du.univfechaReport(itemR.get(i).fecha);

                        rep.addtwo(itemR.get(i).corel, itemR.get(i).descrip);
                        rep.add3lrrTot(fechaR, ""+itemR.get(i).cant, itemR.get(i).total);

                        SumaCant = SumaCant + itemR.get(i).cant;
                        totF += itemR.get(i).total;

                        if(i+1==count9){

                            rep.line();
                            rep.add3lrrTot("", ""+SumaCant, totF);
                            cantF = 0;
                            tot = 0;


                        }

                    }else if(itemR.get(i).tipo==10){

                        test = "Reporte 10";
                        if(acc10==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            cantF=0;
                            impF=0;
                            SumaCant=0;

                            rep.empty();
                            rep.add("REPORTE VENTAS POR CLIENTE DETALLE");

                            rep.add("Fecha        Corelativo");
                            rep.add("Producto   Cant    Precio    Total");
                            rep.line();
                            acc10 = 2;
                        }

                        if(!fechaR.equals(du.univfechaReport(itemR.get(i).fecha))){

                            if(i!=0){
                                rep.line();
                                rep.add4lrrT("", ""+cantF, 0.0,tot);
                                rep.empty();
                                cantF = 0;
                                tot = 0;
                            }

                            fechaR = du.univfechaReport(itemR.get(i).fecha);
                            rep.addtwo(fechaR, itemR.get(i).corel);
                            rep.add4lrrT(itemR.get(i).descrip, ""+itemR.get(i).cant, itemR.get(i).imp,itemR.get(i).total);

                        }else {

                            rep.add4lrrT(itemR.get(i).descrip, ""+itemR.get(i).cant, itemR.get(i).imp,itemR.get(i).total);

                        }

                        cantF += itemR.get(i).cant;
                        tot += itemR.get(i).total;
                        SumaCant += itemR.get(i).cant;
                        totF += itemR.get(i).total;

                        if(i+1==count10){
                            rep.line();
                            rep.add4lrrT("Total: ", ""+SumaCant, 0.0,totF);

                        }
                    }
                }

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                msgbox(test+e.getMessage());
                return false;
            }

        }

        protected boolean buildFooter() {

            try {

                rep.empty();
                rep.empty();

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                return false;
            }

        }

    }

    //endregion

    //region AUX

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

}
