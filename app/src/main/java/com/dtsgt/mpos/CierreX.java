package com.dtsgt.mpos;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_cierreObj;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_cajahoraObj;
import com.dtsgt.classes.clsRepBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class CierreX extends PBase {

    private CierreX.clsDocExist doc;

    private TextView txtbtn;
    private TextView lblFact, txtEnviarCorreo;
    private CheckBox FactxDia, VentaxDia, VentaxProd, xFPago, xFam, VentaxVend, MBxProd, MBxFam, ClienteCon, ClienteDet, FactAnuladas;
    private int bFactxDia=1, bVentaxDia=2, bVentaxProd=3, bxFPago=4, bxFam=5, bVentaxVend=6, bMBxProd=7, bMBxFam=8,
            bClienteCon=9, bClienteDet=10,bFactAnuxDia=11, sw=0,counter=0;
    private boolean report, enc=true;
    private ImageView btnEnviarCorreo;

    private clsClasses.clsReport item;
    private clsClasses.clsBonifProd itemZ;

    private clsD_cierreObj D_cierreObj;

    private ArrayList<clsClasses.clsReport> itemR= new ArrayList<>();
    private ArrayList<clsClasses.clsBonifProd> itemRZ= new ArrayList<>();
    public  ArrayList<String> repl= new ArrayList<>();

    private Double Fondo;

    private String condition,stampstr;
    private boolean exito, reimpresion=false, esvacio;
    private ProgressDialog progressDialog;
    private String CorreoSucursal="", nombrecopia="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_cierre_x);
        } else {
            setContentView(R.layout.activity_cierre_x_ver);
        }

        super.InitBase();
        addlog("Cierres",""+du.getActDateTime(),String.valueOf(gl.vend));

        TextView lblTit = findViewById(R.id.lblTit);
        txtbtn = findViewById(R.id.txtBtn);
        txtEnviarCorreo = findViewById(R.id.txtEnviarCorreo);
        FactxDia = findViewById(R.id.checkBox11); FactxDia.setChecked(true);
        VentaxDia = findViewById(R.id.checkBox12); VentaxDia.setChecked(true);
        VentaxProd = findViewById(R.id.checkBox13); VentaxProd.setChecked(true);
        xFPago = findViewById(R.id.checkBox14); xFPago.setChecked(true);
        xFam = findViewById(R.id.checkBox15); xFam.setChecked(true);
        VentaxVend = findViewById(R.id.checkBox16); VentaxVend.setChecked(true);
        MBxProd = findViewById(R.id.checkBox17); MBxProd.setChecked(true);
        MBxFam = findViewById(R.id.checkBox18); MBxFam.setChecked(true);
        ClienteCon = findViewById(R.id.checkBox19); ClienteCon.setChecked(true);
        ClienteDet = findViewById(R.id.checkBox20); ClienteDet.setChecked(true);
        FactAnuladas = findViewById(R.id.checkBox25); FactAnuladas.setChecked(true);
        lblFact = findViewById(R.id.txtFact2);
        btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        FactxDia.setChecked(app.paramCierre(500));
        VentaxDia.setChecked(app.paramCierre(501));
        VentaxProd.setChecked(app.paramCierre(502));
        xFPago.setChecked(app.paramCierre(503));
        xFam.setChecked(app.paramCierre(504));
        FactAnuladas.setChecked(app.paramCierre(505));
        VentaxVend.setChecked(app.paramCierre(506));
        ClienteCon.setChecked(app.paramCierre(507));
        ClienteDet.setChecked(app.paramCierre(508));
        MBxProd.setChecked(app.paramCierre(509));
        MBxFam.setChecked(app.paramCierre(510));

        if(gl.reportid==9){
            lblTit.setText("Cierre X");
        }else if(gl.reportid==10){
            lblTit.setText("Cierre Z");
        }

        report = false;

        setHandlers();

        txtbtn.setText("GENERAR");

        D_cierreObj=new clsD_cierreObj(this,Con,db);

        clsRepBuilder rep = new clsRepBuilder(this, gl.prw, false, gl.peMon, gl.peDecImp, "");

        Runnable printclose = () -> CierreX.super.finish();

        printer prn = new printer(this, printclose, gl.validimp);
        doc=new CierreX.clsDocExist(this, prn.prw,"");

        lblFact.setMovementMethod(new ScrollingMovementMethod());
    }

    //region Events

    public void GeneratePrintCierre(View view){

        long cfi;

        try {

            try {
                clsP_cajahoraObj P_cajahoraObj=new clsP_cajahoraObj(this,Con,db);
                P_cajahoraObj.fill("WHERE COREL="+gl.corelZ);
                cfi=P_cajahoraObj.first().fechaini;
            } catch (Exception e) {
                cfi=0;
            }

            doc.horaini=du.sfecha(cfi)+" "+du.shora(cfi);
            doc.pais = gl.peFormatoFactura;

            if(gl.reportid==9){
                doc.nombre_reporte ="Reporte de cierre X";
            }else if(gl.reportid==10){
                doc.nombre_reporte ="Reporte de cierre Z";
            }

            stampstr="Generado : "+du.sfecha(du.getActDateTime())+" : "+du.shora(du.getActDateTime())+" ("+gl.corelZ+")";

            if (!report) {

                reimpresion = false;
                bFactxDia=0; bVentaxDia=0; bVentaxProd=0; bxFPago=0; bxFam=0; bVentaxVend=0;
                bMBxProd=0; bMBxFam=0;bClienteCon=0; bClienteDet=0;bFactAnuxDia=0;

                if (FactxDia.isChecked()) bFactxDia=1;
                if (VentaxDia.isChecked()) bVentaxDia=2;
                if (VentaxProd.isChecked()) bVentaxProd=3;
                if (xFPago.isChecked()) bxFPago=4;
                if (xFam.isChecked()) bxFam=5;
                if (VentaxVend.isChecked()) bVentaxVend=6;
                if (MBxProd.isChecked()) bMBxProd=7;
                if (MBxFam.isChecked()) bMBxFam=8;
                if (ClienteCon.isChecked()) bClienteCon=9;
                if (ClienteDet.isChecked()) bClienteDet=10;
                if (FactAnuladas.isChecked()) {
                    bFactAnuxDia=11;
                }

                if(fillItems()){

                    if (itemR.size() == 0) {
                        toastlong("No ha realizado ninguna venta desde el último cierre Z.");
                    }

                    doc.buildPrint("0", 0);

                    getTXT();

                    GenerarCopiaReporte();

                    if (!esvacio) respaldoReporte();
                    agregarReporte();

                    txtbtn.setText(R.string.res_imprimir);
                    txtEnviarCorreo.setVisibility(View.VISIBLE);
                    btnEnviarCorreo.setVisibility(View.VISIBLE);
                    report = true;
                }

            } else {
                reimpresion = true;
                printDoc();
            }

            if (report && !reimpresion) {

                SetCorreoCliente();

                if (!CorreoSucursal.equalsIgnoreCase(""))
                {
                    if(isValidEmail(CorreoSucursal)){
                        txtEnviarCorreo.setVisibility(View.VISIBLE);
                    }else{
                        txtEnviarCorreo.setVisibility(View.GONE);
                    }

                }else{
                    txtEnviarCorreo.setVisibility(View.GONE);
                }

                //msgAsk("Enviar correo a: "+CorreoSucursal);
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("GeneratePrint: "+e);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onBackPressed() {
        if(gl.reportid==5){
            msgAskExit("Salir");
        }else if(report){
            msgAskExit("Salir");
        }else {
            msgbox("No es posible salir sin generar el reporte antes");
        }
    }

    //endregion

    //region Main

    private void setHandlers(){

        FactxDia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(FactxDia.isChecked()){
                bFactxDia = 1;
            } else {
                bFactxDia = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        VentaxDia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(VentaxDia.isChecked())  {
                bVentaxDia = 2;
            } else {
                bVentaxDia = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        VentaxProd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(VentaxProd.isChecked()){
                bVentaxProd = 3;
            } else{
                bVentaxProd = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        xFPago.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(xFPago.isChecked()){
                bxFPago = 4;
            } else{
                bxFPago = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        xFam.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(xFam.isChecked()){
                bxFam = 5;
            } else{
                bxFam = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        VentaxVend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(VentaxVend.isChecked()){
                bVentaxVend = 6;
            } else{
                bVentaxVend = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        MBxProd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MBxProd.isChecked()){
                bMBxProd = 7;
            } else{
                bMBxProd = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        MBxFam.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(MBxFam.isChecked()){
                bMBxFam = 8;
            } else{
                bMBxFam = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        ClienteCon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(ClienteCon.isChecked()){
                bClienteCon = 9;
            } else{
                bClienteCon = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        ClienteDet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(ClienteDet.isChecked()){
                bClienteDet = 10;
            } else{
                bClienteDet = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });

        FactAnuladas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(FactAnuladas.isChecked()){
                bFactAnuxDia = 11;
            } else{
                bFactAnuxDia = 0;
            }
            report = false;
            txtbtn.setText("GENERAR");
            lblFact.setText("");
        });
    }

    private boolean fillItems(){

        Cursor dt;

        try{

            itemR.clear();

            if(gl.reportid==10) reporteZ();

            for (int i=0; i<11; i++){

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
                //if(i==10) sw=0;
                if(i==10) sw=bFactAnuxDia;

                switch (sw){

                    case 0:

                        sql="00";
                        break;

                    case 1:

                        if(gl.reportid==9){
                            condition =" WHERE ANULADO=0 AND KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE ANULADO=0 AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', SERIE, 0, '', '', '', COUNT(COREL), IMPMONTO, SUM(TOTAL), CAST(FECHA/10000 AS INTEGER) " +
                                "FROM D_FACTURA "+
                                condition+
                                "GROUP BY SERIE, IMPMONTO, CAST(FECHA/10000 AS INTEGER) " +
                                "ORDER BY CAST(FECHA/10000 AS INTEGER)";
                        break;

                    case 2:
                        if(gl.reportid==9){
                            condition =" WHERE ANULADO=0 AND KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE ANULADO=0 AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', SERIE, COUNT(COREL), '', '', '', 0, 0, " +
                                "SUM(TOTAL), CAST(FECHA/10000 AS INTEGER) " +
                                "FROM D_FACTURA "+
                                condition+
                                //"FECHA>="+ dateini +" AND FECHA<="+datefin+" " +
                                "GROUP BY CAST(FECHA/10000 AS INTEGER), SERIE";
                        break;
                    case 3:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, D.PRODUCTO, P.DESCCORTA, D.UMVENTA, " +
                                " SUM(D.CANT), 0,SUM(D.TOTAL), F.FECHA " +
                                " FROM P_LINEA L INNER JOIN P_PRODUCTO P ON L.CODIGO_LINEA = P.LINEA " +
                                " INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO_PRODUCTO  " +
                                " INNER JOIN D_FACTURA F ON F.COREL = D.COREL  " +
                                condition+
                                " GROUP BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA "+
                                " ORDER BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA ";
                        break;

                    case 4:
                        if(gl.reportid==9){
                            //condition =" WHERE ANULADO=0 AND KILOMETRAJE = 0 ";
                            condition =" AND D_FACTURA.KILOMETRAJE = 0 ";
                        } else if(gl.reportid==10){
                            //condition=" WHERE ANULADO=0 AND KILOMETRAJE = "+gl.corelZ+" ";
                            condition =" AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, '', P_MEDIAPAGO.NOMBRE, '', COUNT(DISTINCT D_FACTURA.COREL), 0,SUM(D_FACTURAP.VALOR), 0 " +
                                "FROM D_FACTURA INNER JOIN " +
                                "D_FACTURAP ON D_FACTURA.COREL = D_FACTURAP.COREL INNER JOIN " +
                                "P_MEDIAPAGO ON D_FACTURAP.CODPAGO = P_MEDIAPAGO.CODIGO " +
                                "WHERE D_FACTURA.ANULADO=0  "+condition+" " +
                                "GROUP BY P_MEDIAPAGO.NOMBRE";


                        break;

                    case 5:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', '', 0, '', L.NOMBRE, '', SUM(D.CANT), 0, SUM(D.TOTAL), 0 FROM P_LINEA L " +
                                "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO_LINEA " +
                                "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO_PRODUCTO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition +
                                " GROUP BY L.NOMBRE";
                        break;

                    case 6:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT V.CODIGO, '', 0, '', V.NOMBRE, '', COUNT(COREL), V.NIVELPRECIO, SUM(F.TOTAL), 0 FROM VENDEDORES V " +
                                "INNER JOIN D_FACTURA F ON F.VENDEDOR = V.CODIGO " +
                                condition+
                                " GROUP BY V.CODIGO, V.NOMBRE, V.NIVELPRECIO";
                        break;

                    case 7:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT D.PRODUCTO, '', 0, '',  P.DESCCORTA, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM D_FACTURAD D " +
                                "INNER JOIN P_PRODUCTO P ON D.PRODUCTO = P.CODIGO_PRODUCTO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition+
                                " GROUP BY D.PRODUCTO, P.DESCCORTA, P.COSTO, D.PRECIO";
                        break;

                    case 8:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT L.CODIGO, '', 0, '', L.NOMBRE, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM P_LINEA L " +
                                "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO_LINEA " +
                                "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO_PRODUCTO " +
                                "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                                condition+
                                "AND P.LINEA=L.CODIGO "+
                                " GROUP BY L.NOMBRE";
                        break;

                    case 9:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT C.CODIGO, '', 0, '', C.NOMBRE, '',  COUNT(DISTINCT F.COREL), 0, SUM(D.PRECIO*D.CANT), CAST(F.FECHA/10000 AS INTEGER)  " +
                                "FROM P_CLIENTE C " +
                                "INNER JOIN D_FACTURA F ON C.CODIGO = F.CLIENTE " +
                                "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                                condition+
                                " GROUP BY C.CODIGO, C.NOMBRE, CAST(F.FECHA/10000 AS INTEGER)";
                        break;

                    case 10:
                        if(gl.reportid==9){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition =" WHERE F.ANULADO=0 AND F.KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT F.COREL, C.CODIGO, 0, P.CODIGO, P.DESCCORTA, C.NOMBRE, SUM(D.CANT), D.PRECIO, D.PRECIO*D.CANT, F.FECHA, 0 " +
                                "FROM D_FACTURA F " +
                                "INNER JOIN P_CLIENTE C ON C.CODIGO = F.CLIENTE "+
                                "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                                "INNER JOIN P_PRODUCTO P ON P.CODIGO_PRODUCTO = D.PRODUCTO " +
                                condition+
                                " GROUP BY C.CODIGO, C.NOMBRE, F.COREL, F.FECHA, P.DESCCORTA, D.PRECIO";
                        break;
                    case 11:
                        if(gl.reportid==9){
                            condition =" WHERE ANULADO=1 AND KILOMETRAJE = 0 ";
                        }else if(gl.reportid==10){
                            condition=" WHERE ANULADO=1 AND KILOMETRAJE = "+gl.corelZ+" ";
                        }

                        sql="SELECT '', SERIE, 0, '', '', '', COUNT(COREL), IMPMONTO, SUM(TOTAL), CAST(FECHA/10000 AS INTEGER) " +
                                "FROM D_FACTURA "+
                                condition+
                                "GROUP BY SERIE, IMPMONTO, CAST(FECHA/10000 AS INTEGER) " +
                                "ORDER BY CAST(FECHA/10000 AS INTEGER)";
                        break;
                    default:
                        msgbox("Error, al identificar el tipo de reporte, cierre la ventana e intentelo de nuevo");return false;
                }

                if(!sql.equals("00")){

                    dt = Con.OpenDT(sql);

                    if(dt==null) {
                        msgbox("Ocurrió un error, vuelva a intentarlo");return false;
                    } else {

                        if (sw==4) {
                            esvacio=dt.getCount()==0;
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
                        } else {
                            if (sw==11 ) {
                                item = clsCls.new clsReport();

                                item.corel = "";
                                item.serie = "";
                                item.correl = 0;
                                item.codProd = "";
                                item.descrip = "";
                                item.um = "";
                                item.cant = 0;
                                item.imp = 0;
                                item.total = 0;
                                item.fecha = 0;
                                item.tipo = sw;

                                itemR.add(item);
                            }
                        }
                    }

                    dt.close();

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
        String ss;

        try{

            if (gl.corelZ!=0){

                sql="SELECT M.CODIGO, M.NOMBRE, C.FONDOCAJA, 0, 0, C.MONTOINI, C.MONTOFIN, C.MONTODIF, 0,C.COREL " +
                        "FROM P_CAJACIERRE C " +
                        "INNER JOIN P_MEDIAPAGO M ON C.CODPAGO = M.CODIGO " +
                        "WHERE C.COREL = "+ gl.corelZ +" " +
                        "GROUP BY M.CODIGO";

                dt = Con.OpenDT(sql);

                if(dt==null) {

                    msgbox("Ocurrió un error en reporte Z, vuelva a intentarlo");

                    writeCorelLog(201,dt.getCount(),sql);

                }

                itemRZ.clear();

                if(dt.getCount()!=0){

                    dt.moveToFirst();

                    Fondo = dt.getDouble(2);

                    ss="Fon:"+dt.getDouble(2)+",ini:"+dt.getDouble(5)+",fin:"+dt.getDouble(6)+",dif:"+dt.getDouble(7);
                    writeCorelLog(202,dt.getInt(9),ss);

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


                } else if(dt.getCount()==0) {

                    itemZ = clsCls.new clsBonifProd();
                    Fondo = gl.fondoCaja;

                    //#EJC20221213: Esto va a dar error, porque el datatable no tiene registros, porque se consulta?
                    ss="Fon:"+Fondo+",ini:"+dt.getDouble(5)+",fin:"+dt.getDouble(6)+",dif:"+dt.getDouble(7);
                    writeCorelLog(203,dt.getInt(9),ss);

                    itemZ.id="";
                    itemZ.nombre="Fondo de Caja";
                    itemZ.prstr="";
                    itemZ.flag=0;
                    itemZ.cant=0;
                    itemZ.cantmin=gl.fondoCaja;
                    itemZ.disp=gl.FinMonto;
                    itemZ.precio=gl.fondoCaja-gl.FinMonto;
                    itemZ.costo=0;
                    counter=1;
                    itemRZ.add(itemZ);
                }

            }else{
                msgAskOk("Aún no se ha realizado ningún cierre Z.");
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("reporteZ: "+e);
        }
    }

    public void getTXT(){

        StringBuilder text = new StringBuilder();

        try {

            File Storage = Environment.getExternalStorageDirectory();
            File file = new File(Storage,"print.txt");

            repl.clear();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);repl.add(line);
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

    private void GenerarCopiaReporte() {

        try {

            File Storage = Environment.getExternalStorageDirectory();
            File fileOrig= new File(Storage,"print.txt");

            //cierre_fecha_tienda_sucursal_caja
            nombrecopia = "Cierre_Caja_" + du.getFechaActualSinHora() + "_" +gl.tiendanom+"_"+gl.codigo_ruta+"_"+gl.cajanom+".txt";
            File fileDest = new File(Storage, nombrecopia);

            InputStream in = new FileInputStream(fileOrig);
            OutputStream out = new FileOutputStream(fileDest);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            msgbox(new Object() {} .getClass().getEnclosingMethod().getName() +" - "+ e.getMessage());
        }
    }

    public void printDoc() {
        try {
            if (checkPrintFile()) {
                gl.QRCodeStr="";
                app.doPrint();
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region clase clsDocExist

    private class clsDocExist extends clsDocument {

        String fechaR="";
        String test,horaini;
        int cantF,cantfF,SumaCant;
        int count1, count2, count3, count4, count5, count6, count7, count8, count9, count10, count11;
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
            codigo_ruta=""+gl.codigo_ruta;
            vendedor=gl.vendnom;
            nombre_cliente ="";
            vendcod=gl.vend;
            fsfecha=du.getActDateStr();

        }

        protected boolean buildDetail() {

            int acc1=1, acc2=1, acc3=1, acc4=1, acc5=1, acc6=1, acc7=1, acc9=1, acc10=1, acc11=1;
            String series="", fecha="";

            try {

                setDatosVersion();
                //#EJC202212131419:Este dato está mal guardado en la BD.
                //rep.add("Inicio : "+horaini);
                rep.add(fecharango);
                rep.empty();
                rep.add(stampstr);
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
                count11 = 0;

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
                    if(itemR.get(j).tipo==11) count11 +=1;
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
                count11 += count10;

                if(gl.reportid==10){
                    rep.add("Fondo caja : "+gl.peMon+Fondo);
                    rep.empty();
                    rep.addc("REPORTE DE CUADRE");
                    rep.line();
                    rep.add("CODIGO  M.PAGO");
                    rep.add("MONT.INI        MONT.FIN       DIF.");
                    rep.line();

                    tot=0;totF=0;totSinImp=0;

                    for (int j=0; j<itemRZ.size(); j++){
                        //rep.addtot(itemRZ.get(j).id,itemRZ.get(j).nombre);
                        rep.add(itemRZ.get(j).nombre);
                        rep.add4lrrTotZ(itemRZ.get(j).cantmin,itemRZ.get(j).disp,itemRZ.get(j).precio);
                        tot+= mu.round2(itemRZ.get(j).cantmin);
                        totF+=mu.round2(itemRZ.get(j).disp);
                        totSinImp+=mu.round2(itemRZ.get(j).precio);
                    }

                    for(int a=0; a<itemRZ.size(); a++){
                        if(itemRZ.get(a).id.equals("1")){
                            counter+=1;
                        }
                    }

                    rep.line();
                    rep.add4lrrTotZ(tot,totF,totSinImp);

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
                            cantF=0;

                            rep.empty();
                            rep.addc("REPORTE DOCUMENTOS POR DIA");
                            rep.line();
                            rep.add("Vesion MPos : "+gl.parVer);
                            rep.add("Impresion : "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
                            rep.line();
                            rep.add("Cant.Fact   Costo  Impuesto    Total");
                            rep.line();
                            rep.add("             "+du.sfecha(itemR.get(i).fecha*10000));
                            acc1 = 2;
                        }

                        if(!series.equals(itemR.get(i).serie)){
                            rep.add("--------(    Serie "+itemR.get(i).serie+"    )------------");
                        }

                        series=itemR.get(i).serie;

                        totSinImp = itemR.get(i).total - itemR.get(i).imp;
                        rep.add3Tot2(itemR.get(i).cant,totSinImp,itemR.get(i).imp, itemR.get(i).total);

                        tot += itemR.get(i).total;
                        sinImp += totSinImp;
                        impF += itemR.get(i).imp;
                        cantF += itemR.get(i).cant;

                        if (i+1==itemR.size()){

                            rep.line();
                            //rep.add3Tot(SumaCant, totSinImpF, impF, totF);
                            //rep.add3Tot2(cantF, sinImp, impF, tot);

                            totF += tot;
                            SumaCant += cantF;
                            totSinImpF += sinImp;

                        } else {

                            String fecha1=String.valueOf(itemR.get(i).fecha).substring(0,6);
                            String fecha2=String.valueOf(itemR.get(i + 1).fecha).substring(0,6);

                            if (!fecha1.equals(fecha2)) {
                                rep.line();
                                rep.add3Tot(cantF, sinImp, impF, tot);
                                totF += tot;
                                SumaCant += cantF;
                                totSinImpF += sinImp;

                                cantF = 0;
                                tot = 0;
                                sinImp = 0;

                                rep.empty();
                                rep.add("             " + du.sfecha(itemR.get(i+1).fecha*10000));
                            }
                        }

                    }else if(itemR.get(i).tipo==11){

                        test = "Reporte 10";
                        if(acc11==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;
                            cantF=0;

                            rep.empty();
                            rep.line();
                            rep.empty();

                            rep.addc("FACTURAS ANULADAS POR DÍA");
                            setDatosVersion();
                            rep.line();
                            rep.add("Cant.Fact    Costo    Impuesto    Total");
                            rep.line();
                            rep.add("             "+du.sfecha(itemR.get(i).fecha*10000));
                            acc11 = 2;
                        }

                        if(!series.equals(itemR.get(i).serie)){
                            rep.add("--------(    Serie "+itemR.get(i).serie+"    )------------");
                        }

                        series=itemR.get(i).serie;

                        totSinImp = itemR.get(i).total - itemR.get(i).imp;
                        rep.add3Tot(itemR.get(i).cant,totSinImp,itemR.get(i).imp, itemR.get(i).total);

                        tot += itemR.get(i).total;
                        sinImp += totSinImp;
                        impF += itemR.get(i).imp;
                        cantF += itemR.get(i).cant;

                        if(i+1==itemR.size()){

                            rep.line();
                            //rep.add3Tot(SumaCant, totSinImpF, impF, totF);
                            rep.add3Tot(cantF, sinImp, impF, tot);

                            totF += tot;
                            SumaCant += cantF;
                            totSinImpF += sinImp;

                        }else {

                            String fecha1=String.valueOf(itemR.get(i).fecha).substring(0,6);
                            String fecha2=String.valueOf(itemR.get(i + 1).fecha).substring(0,6);

                            if (!fecha1.equals(fecha2)) {
                                rep.line();
                                rep.add3Tot(cantF, sinImp, impF, tot);
                                totF += tot;
                                SumaCant += cantF;
                                totSinImpF += sinImp;

                                cantF = 0;
                                tot = 0;
                                sinImp = 0;

                                rep.empty();
                                rep.add("             " + du.sfecha(itemR.get(i+1).fecha*10000));
                            }
                        }
                    } else if(itemR.get(i).tipo==2){

                        test = "Reporte 2";
                        fecha = du.sfecha(itemR.get(i).fecha*10000);
                        if(acc2==1){

                            tot=0;
                            totF=0;
                            sinImp=0;
                            totSinImpF=0;
                            impF=0;
                            SumaCant=0;

                            rep.empty();
                            rep.addc("REPORTE DE VENTAS POR DIA");
                            setDatosVersion();
                            rep.add("Fecha      Serie   Cant.Fact   Total");
                            rep.line();
                            acc2 = 2;
                        }

                        if(i!=0){
                            //if(itemR.get(i).fecha!=itemR.get(i-1).fecha){

                            String fecha1=String.valueOf(itemR.get(i).fecha).substring(0,6);
                            String fecha2=String.valueOf(itemR.get(i - 1).fecha).substring(0,6);

                            if (!fecha1.equals(fecha2)) {
                                rep.line();
                                rep.add4lrrTot("","",Integer.toString(cantF),tot);
                                rep.empty();
                                tot = 0;
                                cantF =0;
                            }
                        }

                        if(i!=0){

                            String fecha1=String.valueOf(itemR.get(i).fecha).substring(0,6);
                            String fecha2=String.valueOf(itemR.get(i - 1).fecha).substring(0,6);

                            if (!fecha1.equals(fecha2)) {
                           // if(itemR.get(i).fecha==itemR.get(i-1).fecha){
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
                            rep.addc("REPORTE VENTA POR PRODUCTO");
                            setDatosVersion();
                            rep.add("Cod   Descripcion");
                            rep.add("Cant        UM       Total        %");
                            rep.line();
                            acc3 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        if (gl.peRepVenCod) {
                            rep.addtot2(itemR.get(i).codProd,itemR.get(i).descrip);
                        } else {
                           rep.add(itemR.get(i).descrip);
                        }

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
                            rep.addc("REPORTE POR FORMA DE PAGO");
                            setDatosVersion();
                            rep.add("Forma    Cantidad");
                            rep.add("Pago      Factura     Total     %");
                            rep.line();
                            acc4 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;
                        if (itemR.get(i).descrip.isEmpty()) itemR.get(i).descrip="Contado";
                        rep.add4rrrTotPorc(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count4){
                            rep.line();
                            rep.add4rrrTotPorc("",Integer.toString(SumaCant),totF,0.0);
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
                            rep.addc("REPORTE VENTA POR FAMILIA");
                            setDatosVersion();
                            rep.add("Seccion   Cant.Art Total        %");
                            rep.line();
                            acc5 = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        rep.add4lrrTotPorc2(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                        if(i+1==count5){
                            rep.line();
                            rep.add4lrrTotPorc2("",Integer.toString(SumaCant),totF,0.0);
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
                            rep.addc("VENTAS POR VENDEDOR");
                            setDatosVersion();
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
                            if(itemR.get(i).tipo==7) rep.add("MARGEN Y BENEFICIO POR PRODUCTO");
                            if(itemR.get(i).tipo==8) rep.add("MARGEN Y BENEFICIO POR FAMILIA");
                            rep.line();
                            setDatosVersion();
                            rep.line();
                            rep.add("Codigo     Nombre");
                            rep.add("Venta      Costo    Beneficio    %");
                            rep.line();
                            acc7 = 2;
                        }

                        tot = itemR.get(i).total - itemR.get(i).imp;
                        //porcentaje = (100/itemR.get(i).total) * tot;
                        porcentaje = (100/totF) * tot;
                        totSinImpF+=tot;

                        rep.addtot2(itemR.get(i).corel,itemR.get(i).descrip);
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
                            rep.addc("VENTAS POR CLIENTE CONSOLIDADO");
                            setDatosVersion();
                            rep.add("Codigo        Nombre");
                            rep.add("Fecha       Cant.Fact       Total");
                            rep.line();
                            acc9 = 2;
                        }

                        fechaR = du.sfecha(itemR.get(i).fecha*10000);

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

                    } else if(itemR.get(i).tipo==10){

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
                            rep.addc("VENTAS POR CLIENTE DETALLE");
                            setDatosVersion();
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

        private void setDatosVersion() {
            rep.empty();
            rep.line();
            rep.add("Empresa: " + gl.empnom);
            rep.add("Sucursal: " + gl.tiendanom);
            rep.add("Caja: " + gl.rutanom);
            rep.add("Impresión: "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
            rep.add("Vesión MPos: "+gl.parVer);
            rep.add("Generó: "+gl.vendnom);
            rep.line();
            rep.empty();
        }

        protected boolean buildFooter() {

            try {

                rep.empty();
                rep.empty();
                rep.add(stampstr);
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

    //region Dialogs

    private void msgAskOk(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Ok", (dialog1, which) -> finish());
        dialog.show();
    }

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog1, which) -> finish());
        dialog.setNegativeButton("No", (dialog12, which) -> {});
        dialog.show();
    }

    private void msgAskReset(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Continuar", (dialog1, which) -> restartApp());
        dialog.show();
    }

    //endregion

    //region Aux

    private void respaldoReporte() {
        String ss;

        try {

            db.beginTransaction();
            db.execSQL("DELETE FROM T_cierre");

            for (int i = 0; i <repl.size(); i++) {
                ss=repl.get(i);
                db.execSQL("INSERT INTO T_cierre VALUES ("+i+",0,'"+ss+"')");
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
            msgbox("No se pudo generar respaldo de impresion del cierre.\n"+e.getMessage());
        }
    }

    private void agregarReporte() {
        clsClasses.clsD_cierre item;
        String ss;
        int ii;
        long fl,ff=du.getActDateTime();

        try {

            db.beginTransaction();

            fl=du.addDays(ff,-7);
            db.execSQL("DELETE FROM D_cierre WHERE fecha<"+fl);

            ii=D_cierreObj.newID("SELECT MAX(ID) FROM D_cierre");

            for (int i = 0; i <repl.size(); i++) {
                ss=repl.get(i);

                item = clsCls.new clsD_cierre();

                item.id=ii+i;
                item.cierre=gl.corelZ;
                item.fecha=ff;
                item.text=ss;

                D_cierreObj.add(item);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
            msgbox("No se pudo agregar impresion del cierre.\n"+e.getMessage());
        }
    }

    private void writeCorelLog(int id,int corel,String text) {
        String ss;
        try {
            ss="INSERT INTO T_BARRA_BONIF VALUES ('"+du.getActDateTime()+"','"+id+"',"+corel+",0,0,'"+text+"')";
            db.execSQL(ss);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void EnviarCierre(View view) {

        try {

            String dir= Environment.getExternalStorageDirectory()+"";
            String nombrereporte = "Cierre_Caja_" + du.getFechaActualSinHora() + "_" +gl.tiendanom+"_"+gl.codigo_ruta+"_"+gl.cajanom+".txt";
            File f1 = new File(dir + "/"+nombrereporte);

            if (CorreoSucursal.isEmpty()) {
                SetCorreoCliente();
            }

            if (!f1.exists()) {
                GenerarCopiaReporte();
            }

            msgAsk("Enviar correo a: "+CorreoSucursal);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean checkPrintFile() {

        FileInputStream fIn = null;
        BufferedReader myReader = null;

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/print.txt");

            fIn = new FileInputStream(file1);
            myReader = new BufferedReader(new InputStreamReader(fIn));

            String line = myReader.readLine();
            if (!line.equalsIgnoreCase("")){
                myReader.close();
                fIn.close();
            }

            return true;

        } catch (Exception e) {
            try {
                if (myReader!=null){
                    myReader.close();
                }
                if (fIn!=null){
                    fIn.close();
                }
            } catch (Exception ee) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }
        }

        msgAskReset("Ocurrió error en impresión del cierre del dia.\n" +
                    "La aplicación se reiniciará.\n" +
                    "Para re-impresión de cierre, utilice opción: Menú Principal - Reportes / Último cierre");
        return false;
    }

    private void restartApp() {
        try{
            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent =Intent.makeRestartActivityTask(componentName);
            //Intent mainIntent = IntentCompat..makeRestartActivityTask(componentName);
            this.startActivity(mainIntent);
            System.exit(0);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    private void SetCorreoCliente() {

        Cursor DT;

        CorreoSucursal ="";

        try {

            sql = "SELECT CORREO FROM P_SUCURSAL";
            DT=Con.OpenDT(sql);

            if (DT!=null){

                if (DT.getCount() == 0) return;

                DT.moveToFirst();

                CorreoSucursal = DT.getString(0);
            }

        } catch (Exception e) {
            msgbox(new Object() {} .getClass().getEnclosingMethod().getName() + " - " + e.getMessage());
        }
    }

    public class AsyncEnviaCorreo extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CierreX.this,"Enviando reporte","Espere por favor...",false,false);
        }

        @Override
        protected String doInBackground(Void... vd) {

            exito = false;

            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", "smtp.office365.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("soportesw@dts.com.gt", "Dts2021#");
                }
            });

            try {

                String dir= Environment.getExternalStorageDirectory()+"";
                String nombrereporte = "Cierre_Caja_" + du.getFechaActualSinHora() + "_" +gl.tiendanom+"_"+gl.codigo_ruta+"_"+gl.cajanom+".txt";
                File f1 = new File(dir + "/"+nombrereporte);

                session.getProperties().put("mail.smtp.ssl.trust", "smtp.office365.com");
                session.getProperties().put("mail.smtp.starttls.enable", "true");

                MimeMessage mm = new MimeMessage(session);
                mm.setFrom(new InternetAddress("soportesw@dts.com.gt"));
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(CorreoSucursal));
                mm.setSubject("Tienda : "+gl.tiendanom+" caja : "+gl.codigo_ruta);

                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Adjunto reporte de cierre de tienda :" +gl.tiendanom + " caja :"+gl.codigo_ruta);

                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(f1);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(attachmentPart);

                mm.setContent(multipart);
                Transport.send(mm);
                exito = true;

                f1.delete();

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String vdata){

            super.onPostExecute(vdata);
            progressDialog.dismiss();

            if (exito) {
                Toast.makeText(CierreX.this, "Reporte enviado", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CierreX.this, "Error al enviar reporte", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void msgAsk(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog1, which) -> {
            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                AsyncEnviaCorreo enviar = new AsyncEnviaCorreo();
                enviar.execute();
            };
            mtimer.postDelayed(mrunner,2000);
        });
        dialog.setNegativeButton("No", (dialog12, which) -> {});
        dialog.show();
    }
    //endregion

    // Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            D_cierreObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

}
