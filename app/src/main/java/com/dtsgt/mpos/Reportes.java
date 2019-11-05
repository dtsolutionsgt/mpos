package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.graphics.Color;
import android.os.Bundle;

import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.ladapt.ListAdaptProd;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.base.clsClasses.clsReport;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.ListAdaptExist;
import com.dtsgt.ladapt.ListAdaptProd;
import com.dtsgt.ladapt.ListReportVenta;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class Reportes extends PBase {

    private Button btnImp;
    private TextView lblDateini,lblDatefin, lblImp, lblFact,lblTit,lblProd,txtFill,txtTitle;
    private ImageView btnClear;
    private CheckBox cbDet;

    private String prodid,savecant;

    private ArrayList<clsClasses.clsCD> itemsFill;
    private ArrayList<clsClasses.clsVenta> items= new ArrayList<clsClasses.clsVenta>();
    private ArrayList<clsClasses.clsReport> itemR= new ArrayList<clsClasses.clsReport>();
    private ArrayList<String> spinlist = new ArrayList<String>();
    private ArrayList<String> spincode = new ArrayList<String>();
    private ListAdaptExist adapter;
    private ListAdaptProd adaptFill;
    private ListReportVenta adapt;
    private clsClasses.clsReport item;

    private ListView lvReport;

    private clsDocFactura fdoc;
    private double cantT,disp,dispm,dispT;
    private clsRepBuilder rep;
    private boolean dateTxt,report;
    private clsP_lineaObj linea;
    private clsP_mediapagoObj medP;
    private clsP_productoObj prod;
    private clsP_clienteObj cli;

    private int tipo, cantExistencia,validCli;
    private String CERO="0", nameProd, nameVend, id_item;
    private clsClasses.clsVenta itemV;

    private Reportes.clsDocExist doc;
    private printer prn;
    private Runnable printclose;

    private AppMethods app;
    private int SumaCant = 0;


    public final Calendar c = Calendar.getInstance();
    private static final String BARRA = "/";
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    public int cyear, cmonth, cday, validCB=0;
    private long datefin,dateini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        super.InitBase();
        addlog("Reportes",""+du.getActDateTime(),gl.vend);

        lvReport = (ListView) findViewById(R.id.listViewReport);lvReport.setVisibility(View.INVISIBLE);
        txtFill = (TextView) findViewById(R.id.txtFill);txtFill.setVisibility(View.INVISIBLE);
        lblProd = (TextView) findViewById(R.id.txtProdName);
        lblDateini = (TextView) findViewById(R.id.lblDateini);
        lblDatefin = (TextView) findViewById(R.id.lblDatefin);
        lblImp = (TextView) findViewById(R.id.txtBtn);
        lblFact = (TextView) findViewById(R.id.txtFact);
        lblTit = (TextView) findViewById(R.id.lblTit);
        txtTitle = (TextView) findViewById(R.id.txtTitle);txtTitle.setVisibility(View.INVISIBLE);
        btnClear = (ImageView) findViewById(R.id.imageView37);btnClear.setVisibility(View.INVISIBLE);
        cbDet = (CheckBox) findViewById(R.id.cbDet);

        lblProd.setVisibility(View.INVISIBLE);

        if(gl.reportid!=11 && gl.reportid!=12 && gl.reportid!=3 && gl.reportid!=6){
            cbDet.setVisibility(View.INVISIBLE);
        }

        if(gl.reportid==3 || gl.reportid==6){
            cbDet.setText("Reporte Consolidado");
        }

        linea =new clsP_lineaObj(this,Con,db);
        medP =new clsP_mediapagoObj(this,Con,db);
        prod =new clsP_productoObj(this,Con,db);
        cli =new clsP_clienteObj(this,Con,db);

        lblTit.setText(gl.titReport);
        getFechaAct();
        itemsFill= new ArrayList<clsClasses.clsCD>();

        report = false;

        app = new AppMethods(this, gl, Con, db);
        gl.validimp=app.validaImpresora();
        if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

        lblImp.setText("GENERAR");

        setHandlers();

        if(gl.reportid==3 || gl.reportid==4 || gl.reportid==7 || gl.reportid==8 || gl.reportid==11){
            txtTitle.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            txtFill.setVisibility(View.VISIBLE);
            lvReport.setVisibility(View.VISIBLE);
            listItems();
        }

        rep=new clsRepBuilder(this,gl.prw,false,gl.peMon,gl.peDecImp, "");

        printclose= new Runnable() {
            public void run() {
                Reportes.super.finish();
            }
        };

        prn=new printer(this,printclose,gl.validimp);
        doc=new Reportes.clsDocExist(this,prn.prw,"");

        lblFact.setMovementMethod(new ScrollingMovementMethod());
    }

    //region Events

    public void printDoc() {
        try{
            printEpson();
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

    //endregion

    //region Main

    private void listItems() {
        Cursor DT;
        clsClasses.clsCD vItem;
        String vF,cod,name,condi="";

        itemsFill.clear();id_item="*";

        vF=txtFill.getText().toString().replace("'","");

        String sql = "";

        try {


            if(!vF.isEmpty()){
                condi = " WHERE CODIGO LIKE '%"+vF+"%'";
            }

            switch(gl.reportid){
                case 3:
                    sql="SELECT CODIGO, NOMBRE FROM P_LINEA"+ condi;
                    break;
                case 4:
                    sql="SELECT CODIGO, NOMBRE FROM P_MEDIAPAGO"+ condi;
                    break;
                case 7:
                    sql="SELECT CODIGO, DESCCORTA FROM P_PRODUCTO"+ condi;
                    break;
                case 8:
                    sql="SELECT CODIGO, NOMBRE FROM P_LINEA"+ condi;
                    break;
                case 11:
                    sql="SELECT CODIGO, NOMBRE FROM P_CLIENTE"+ condi;
                    break;
            }

            DT=Con.OpenDT(sql);

            if (DT.getCount()==0) {

                if(!vF.isEmpty()){
                    if(gl.reportid==7){
                        condi = " WHERE DESCCORTA LIKE '%'"+vF+"%'";
                    }else {
                        condi = " WHERE NOMBRE LIKE '%"+vF+"%'";
                    }
                }

                switch(gl.reportid){
                    case 3:
                        sql="SELECT CODIGO, NOMBRE FROM P_LINEA"+ condi;
                        break;
                    case 4:
                        sql="SELECT CODIGO, NOMBRE FROM P_MEDIAPAGO"+ condi;
                        break;
                    case 7:
                        sql="SELECT CODIGO, DESCCORTA FROM P_PRODUCTO"+ condi;
                        break;
                    case 8:
                        sql="SELECT CODIGO, NOMBRE FROM P_LINEA"+ condi;
                        break;
                    case 11:
                        sql="SELECT CODIGO, NOMBRE FROM P_CLIENTE"+ condi;
                        break;
                }

                DT=Con.OpenDT(sql);

                if (DT.getCount()==0) return;
            }

            if(gl.reportid!=12){
                vItem = clsCls.new clsCD();
                vItem.Cod="Todos";
                vItem.Desc="";
                vItem.um="";
                vItem.Text="";
                itemsFill.add(vItem);
            }


            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                cod=DT.getString(0);
                name=DT.getString(1);

                vItem = clsCls.new clsCD();
                vItem.Cod=cod;
                vItem.Desc=name;

                vItem.um="";
                vItem.Text="";

                itemsFill.add(vItem);

                DT.moveToNext();
            }
        } catch (Exception e) 		{
            mu.msgbox( e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            Log.d("prods",e.getMessage());
        }

        adaptFill=new ListAdaptProd(this,itemsFill);
        lvReport.setAdapter(adaptFill);

    }

    public void GeneratePrint(View view){
        try{
            if(!report) {
                AskReport();
                return;
            }

            if(report) {
                app.doPrint();
                return;
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("GeneratePrint: "+e);
        }
    }

    private void AskReport() {

        try{

            if(dateini <= 0){
                msgbox("Fecha inicial erronea");return;
            }

            if(datefin <= 0){
                msgbox("Fecha final erronea");return;
            }

            if(dateini>datefin){
                msgbox("La fecha final no puede ser mayor a la inicial");return;
            }

            if(gl.reportid==3 || gl.reportid==4){
                if(lblProd.getText().toString().trim().isEmpty() && !id_item.equals("Todos")) {
                    if (gl.reportid == 3){
                        msgbox("Escoja un producto");
                        return;
                    }else if(gl.reportid==4) {
                        msgbox("Escoja una forma de pago");
                        return;
                    }
                }
            }

            if(!report) {
                if(fillItems()){
                    if (itemR.size() == 0) {
                        msgbox("No se ha realizado ninguna venta entre los parámetros impuestos.");
                        return;
                    }
                    doc.buildPrint("0", 0);
                    getTXT();
                    lblImp.setText("IMPRIMIR");
                    report = true;
                }else {
                    return;
                }

            }else {
                printDoc();
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("msgAskReport: "+e);
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

    private boolean fillItems(){
        Cursor dt;
        String condition;

        try{
            itemR.clear();

            switch (gl.reportid){
                case 1:
                    sql="SELECT '', SERIE, 0, '', '', '', COUNT(COREL), IMPMONTO, SUM(TOTAL), FECHA " +
                            "FROM D_FACTURA WHERE FECHA>='"+dateini+"' AND FECHA<='"+datefin+"' " +
                            "AND ANULADO='N' " +
                            "GROUP BY SERIE, IMPMONTO, FECHA " +
                            "ORDER BY FECHA";
                    break;
                case 2:
                    sql="SELECT '', SERIE, COUNT(COREL), '', '', '', 0, 0, " +
                            "SUM(TOTAL), FECHA " +
                            "FROM D_FACTURA WHERE FECHA=FECHA AND " +
                            "FECHA>="+ dateini +" AND FECHA<="+datefin+" " +
                            "AND ANULADO='N' " +
                            "GROUP BY FECHA, SERIE";
                    break;
                case 3:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                    }else{
                        condition = " AND L.CODIGO = '"+ id_item +"' ";
                    }

                    sql="SELECT '', '', 0, D.PRODUCTO, P.DESCCORTA, D.UMVENTA, " +
                            " SUM(D.CANT), 0,SUM(D.TOTAL), F.FECHA " +
                            " FROM P_LINEA L INNER JOIN P_PRODUCTO P ON L.CODIGO = P.LINEA " +
                            " INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO  " +
                            " INNER JOIN D_FACTURA F ON F.COREL = D.COREL  " +
                            " WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            " GROUP BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA "+
                            " ORDER BY D.PRODUCTO, P.DESCCORTA, D.UMVENTA ";
                    break;
                case 4:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                    }else{
                        condition = " AND M.CODIGO = '"+ id_item +"' ";
                    }

                    sql="SELECT '', '', 0, '', M.NOMBRE, '', COUNT(F.COREL), 0,SUM(F.TOTAL), 0 FROM P_MEDIAPAGO M " +
                            "INNER JOIN D_FACTURAP P ON P.CODPAGO = M.CODIGO " +
                            "INNER JOIN D_FACTURA F ON F.COREL = P.COREL "+//AND M.EMPRESA = F.EMPRESA " +
                            "WHERE F.ANULADO='N' AND F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            "GROUP BY M.NOMBRE";
                    break;

                case 5:

                    sql="SELECT '', '', 0, '', L.NOMBRE, '', SUM(D.CANT), 0, SUM(D.TOTAL), 0 FROM P_LINEA L " +
                            "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO " +
                            "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO " +
                            "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                            "WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+
                            " AND F.ANULADO='N' " +
                            "GROUP BY L.NOMBRE";
                    break;

                case 6:

                    sql="SELECT V.CODIGO, '', 0, '', V.NOMBRE, '', COUNT(COREL), V.NIVELPRECIO, SUM(F.TOTAL), 0 FROM VENDEDORES V " +
                            "INNER JOIN D_FACTURA F ON F.VENDEDOR = V.CODIGO " +
                            "WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+
                            " AND F.ANULADO='N' " +
                            "GROUP BY V.CODIGO, V.NOMBRE, V.NIVELPRECIO";
                    break;

                case 7:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                    }else{
                        condition = " AND P.CODIGO = '"+ id_item +"' ";
                    }

                    sql="SELECT D.PRODUCTO, '', 0, '',  P.DESCCORTA, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM D_FACTURAD D " +
                            "INNER JOIN P_PRODUCTO P ON D.PRODUCTO = P.CODIGO " +
                            "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                            "WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            "GROUP BY D.PRODUCTO, P.DESCCORTA, P.COSTO, D.PRECIO";
                    break;

                case 8:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                    }else{
                        condition = " AND L.CODIGO = '"+ id_item +"' ";
                    }

                    sql="SELECT L.CODIGO, '', 0, '', L.NOMBRE, '', 0, SUM(P.COSTO), SUM(D.PRECIO), 0 FROM P_LINEA L " +
                            "INNER JOIN P_PRODUCTO P ON P.LINEA = L.CODIGO " +
                            "INNER JOIN D_FACTURAD D ON D.PRODUCTO = P.CODIGO " +
                            "INNER JOIN D_FACTURA F ON D.COREL = F.COREL " +
                            "WHERE P.LINEA=L.CODIGO AND F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            "GROUP BY L.NOMBRE";
                    break;

                case 11:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                        validCli=1;
                    }else{
                        condition = " AND C.CODIGO = '"+ id_item +"' ";
                        validCli=2;
                    }

                    sql="SELECT C.CODIGO, '', 0, '', C.NOMBRE, '',  COUNT(DISTINCT F.COREL), 0, SUM(D.PRECIO*D.CANT), F.FECHA " +
                            "FROM P_CLIENTE C " +
                            "INNER JOIN D_FACTURA F ON C.CODIGO = F.CLIENTE " +
                            "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                            "WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            "GROUP BY C.CODIGO, C.NOMBRE, F.FECHA";
                    break;

                case 12:
                    if(id_item.equals("Todos")){
                        condition = " AND 1=1 ";
                    }else{
                        condition = " AND C.CODIGO = '"+ id_item +"' ";
                    }

                    sql="SELECT F.COREL, C.CODIGO, 0, P.CODIGO, P.DESCCORTA, C.NOMBRE, D.CANT, D.PRECIO, D.PRECIO*D.CANT, F.FECHA, 0 " +
                            "FROM D_FACTURA F " +
                            "INNER JOIN P_CLIENTE C ON C.CODIGO = F.CLIENTE "+
                            "INNER JOIN D_FACTURAD D ON F.COREL = D.COREL " +
                            "INNER JOIN P_PRODUCTO P ON P.CODIGO = D.PRODUCTO " +
                            "WHERE F.FECHA>="+ dateini +" AND F.FECHA<="+datefin+condition+
                            "AND F.ANULADO='N' " +
                            "GROUP BY C.CODIGO, C.NOMBRE, F.COREL, F.FECHA, P.DESCCORTA, D.CANT, D.PRECIO, F.TOTAL";
                    break;

                default:
                    msgbox("Error, al identificar el tipo de reporte, cierre la ventana e intentelo de nuevo");return false;
            }

            dt = Con.OpenDT(sql);
            if(dt==null) {
                msgbox("Ocurrío un error, vuelva a intentarlo");return false;
            }

            if(dt.getCount()==0){
                return true;
            }

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
                item.tipo = 0;

                itemR.add(item);

                dt.moveToNext();
            }

            return true;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("fillItems: "+e);
            return false;
        }
    }

    private void setHandlers(){

        try{
            lvReport.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {

                        Object lvObj = lvReport.getItemAtPosition(position);
                        clsClasses.clsCD item = (clsClasses.clsCD) lvObj;

                        adaptFill.setSelectedIndex(position);

                        report =  false;
                        id_item = item.Cod;

                        if(id_item.equals("NO")) { lblProd.setText("");}

                        if(!id_item.equals("Todos")){

                            if(gl.reportid==3 || gl.reportid==8){

                                linea.fill("WHERE CODIGO = '"+id_item+"'");
                                if(linea.count==0) return;
                                nameProd = linea.first().nombre.trim();
                                lblProd.setText(nameProd);

                            }else if(gl.reportid==4){

                                medP.fill("WHERE CODIGO = '"+id_item+"'");
                                if(medP.count==0) return;
                                nameVend = medP.first().nombre.trim();
                                lblProd.setText(nameVend);

                            }else if(gl.reportid==7){

                                prod.fill("WHERE CODIGO = '"+id_item+"'");
                                if(prod.count==0) return;
                                nameVend = prod.first().desccorta.trim();
                                lblProd.setText(nameVend);

                            }else if(gl.reportid==11){

                                cli.fill("WHERE CODIGO = '"+id_item+"'");
                                if(cli.count==0) return;
                                nameVend = cli.first().nombre.trim();
                                lblProd.setText(nameVend);

                            }

                        }else {
                            lblProd.setText("");
                        }

                        itemR.clear();
                        lblFact.setText("");
                        lblImp.setText("GENERAR");
                        report=false;

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox(e.getMessage());
                    }
                }
            });


            cbDet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    report = false;
                    lblImp.setText("GENERAR");
                    lblFact.setText("");

                    if(cbDet.isChecked()){
                        if(gl.reportid!=3 && gl.reportid!=6){
                            gl.reportid=12;
                        }else {
                            validCB=1;
                        }

                    }else{
                        if(gl.reportid!=3 && gl.reportid!=6){
                            gl.reportid=11;
                        }else {
                            validCB=0;
                        }

                    }

                }
            });

            txtFill.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listItems();
                }
            });

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void obtenerFecha(){
        try{
            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    final int mesActual = month + 1;
                    String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                    if(dateTxt) lblDatefin.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    if(!dateTxt) lblDateini.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);
                    if(dateTxt) datefin = du.cfechaDesc(cyear, cmonth, cday);
                    if(!dateTxt) dateini  = du.cfechaDesc(cyear, cmonth, cday);
                }
            },anio, mes, dia);

            itemR.clear();
            lblFact.setText("");
            lblImp.setText("GENERAR");
            report=false;

            recogerFecha.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void getFechaAct(){
        Long fecha;
        String date;

        try{
            fecha = du.getFechaActualReport();

            date = du.univfechaReport(fecha);

            lblDateini.setText(date);
            lblDatefin.setText(date);

            datefin = fecha;
            dateini = fecha;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region clase clsDocExist

    private class clsDocExist extends clsDocument {
        String fechaR="";
        int cantF,cantfF;
        double tot,totF;
        double porcentaje=0.0, comision;
        double totSinImp, sinImp,totSinImpF, impF;

        public clsDocExist(Context context, int printwidth, String archivo) {
            super(context, printwidth,gl.peMon,gl.peDecImp, archivo);

            pass = true;
            if(gl.reportid==1) nombre="REPORTE FACTURAS POR DIA";
            if(gl.reportid==2) nombre="REPORTE DE VENTAS POR DIA";
            if(gl.reportid==3) nombre="REPORTE VENTA POR PRODUCTO";
            if(gl.reportid==4) nombre="REPORTE POR FORMA DE PAGO";
            if(gl.reportid==5) nombre="REPORTE VENTA POR FAMILIA";
            if(gl.reportid==6) nombre="REPORTE VENTAS POR VENDEDOR";
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
                SumaCant=0;
                fecharango="Del "+du.univfechaReport(dateini)+" Hasta "+du.univfechaReport(datefin);
                rep.add(fecharango);
                rep.empty();

                for (int i = 0; i <itemR.size(); i++) {

                    //Reporte 1
                    if(gl.reportid==1){

                        if(acc==1){
                            rep.add("     REPORTE DOCUMENTOS POR DIA");
                            rep.add("Cant.Fact   Costo  Impuesto    Total");
                            rep.line();
                            rep.empty();
                            rep.add("             "+du.sfecha(itemR.get(i).fecha));
                            acc = 2;
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

                        if(i+1==itemR.size()){
                            rep.line();
                            rep.add3Tot(cantF, sinImp, impF, tot);
                            totF += tot;
                            SumaCant += cantF;
                            totSinImpF += sinImp;

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
                    } else if(gl.reportid==2){

                        fecha = du.sfecha(itemR.get(i).fecha);
                        if(acc==1){
                            rep.add("      REPORTE DE VENTAS POR DIA");
                            rep.add("Fecha      Serie   Cant.Fact   Total");
                            rep.line();
                            acc = 2;
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
                                rep.add4lrrTot("",itemR.get(i).serie,Integer.toString(itemR.get(i).correl),itemR.get(i).total);
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

                        if(i+1==itemR.size()){
                            rep.line();
                            rep.add4lrrTot("","",Integer.toString(cantF),tot);
                            rep.empty();
                            tot = 0;
                        }

                    //Reporte 3
                    }else if(gl.reportid==3){

                        if(acc==1){

                            for (int a = 0; a <itemR.size(); a++) {
                                totF += itemR.get(a).total;
                            }

                            rep.add("      REPORTE VENTA POR PRODUCTO");
                            if(validCB==1) rep.add("            CONSOLIDADO");
                            if(validCB==0) rep.add("Cod   Descripcion");
                            if(validCB==0) rep.add("Cant        UM       Total        %");
                            if(validCB==1) rep.add("Cod        Descripcion        Total");
                            rep.line();
                            acc = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        if(validCB==0){
                            rep.addtot(itemR.get(i).codProd,itemR.get(i).descrip);
                            rep.add4lrrTotPorc(Integer.toString(itemR.get(i).cant), itemR.get(i).um,itemR.get(i).total,porcentaje);
                        }else {
                            rep.addtot3(itemR.get(i).codProd,itemR.get(i).descrip,itemR.get(i).total);
                        }

                        SumaCant = SumaCant + itemR.get(i).cant;

                    //Reporte 4
                    }else if(gl.reportid==4){

                        if(acc==1){

                            for (int a = 0; a <itemR.size(); a++) {
                                totF += itemR.get(a).total;
                            }

                            rep.add("     REPORTE POR FORMA DE PAGO");
                            rep.add("Forma    Cantidad");
                            rep.add("Pago      Factura     Total     %");
                            rep.line();
                            acc = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;
                        rep.add4lrrTotPorc(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;
                    }else if(gl.reportid==5){

                        if(acc==1){

                            for (int a = 0; a <itemR.size(); a++) {
                                totF += itemR.get(a).total;
                            }

                            rep.add("      REPORTE VENTA POR FAMILIA");
                            rep.add("Seccion   Cant.Art    Total      %");
                            rep.line();
                            acc = 2;
                        }

                        porcentaje = (100 /totF) * itemR.get(i).total;

                        rep.add4lrrTotPorc(itemR.get(i).descrip, Integer.toString(itemR.get(i).cant),itemR.get(i).total,porcentaje);

                        SumaCant = SumaCant + itemR.get(i).cant;

                    } else if(gl.reportid==6){

                        if(acc==1){

                            for (int a = 0; a <itemR.size(); a++) {

                                totF += itemR.get(a).total;
                            }


                            rep.add("    REPORTE VENTAS POR VENDEDOR");
                            if(validCB==1) rep.add("              CONSOLIDADO");
                            if(validCB==0) rep.add("Codigo     Nombre");
                            if(validCB==0) rep.add("Cant       %       Total    Comision");
                            if(validCB==1) rep.add("Codigo     Nombre             Total");
                            rep.line();
                            acc = 2;
                        }

                        comision = (itemR.get(i).total * itemR.get(i).imp) / 100;

                        if(validCB==0){
                            rep.addtot(itemR.get(i).corel, itemR.get(i).descrip);

                            rep.add4lrrTotV(Integer.toString(itemR.get(i).cant), itemR.get(i).imp+"%", itemR.get(i).total, comision);
                        }else {
                            rep.addtot3(itemR.get(i).corel, itemR.get(i).descrip, itemR.get(i).total);
                        }

                        SumaCant = SumaCant + itemR.get(i).cant;
                        totSinImpF += comision;
                    }else if(gl.reportid==7 || gl.reportid==8){

                        if(acc==1){

                            for (int a = 0; a <itemR.size(); a++) {

                                totF += itemR.get(a).total;
                                impF += itemR.get(a).imp;
                            }

                            if(itemR.get(i).tipo==7) rep.add("MARGEN Y BENEFICION POR PRODUCTO");
                            if(itemR.get(i).tipo==8) rep.add("MARGEN Y BENEFICION POR FAMILIA");
                            rep.add("Codigo     Nombre");
                            rep.add("Venta      Costo    Beneficio    %");
                            rep.line();
                            acc = 2;
                        }

                        tot = itemR.get(i).total - itemR.get(i).imp;
                        porcentaje = (100/itemR.get(i).total) * tot;
                        totSinImpF+=tot;

                        rep.addtot(itemR.get(i).corel, "    "+itemR.get(i).descrip);
                        rep.add4(itemR.get(i).total, itemR.get(i).imp, tot, porcentaje);

                    }else if(gl.reportid==11){

                        if(acc==1){

                            rep.add("REPORTE VENTAS POR CLIENTE CONSOLIDADO");
                            rep.add("Codigo        Nombre");
                            rep.add("Fecha       Cant.Fact       Total");
                            rep.line();
                            acc = 2;
                        }

                        fechaR = du.univfechaReport(itemR.get(i).fecha);

                        if(validCli==2){
                            rep.addtwo(itemR.get(i).corel, itemR.get(i).descrip);
                            validCli=3;
                        }else if(validCli==1){
                            rep.addtwo(itemR.get(i).corel, itemR.get(i).descrip);
                        }

                        rep.add3lrrTot(fechaR, ""+itemR.get(i).cant, itemR.get(i).total);

                        SumaCant = SumaCant + itemR.get(i).cant;
                        totF += itemR.get(i).total;
                    }else if(gl.reportid==12){

                        if(acc==1){

                            rep.add("REPORTE VENTAS POR CLIENTE DETALLE");
                            rep.add("Codigo Cliente: "+itemR.get(0).serie);
                            rep.add("Nombre Cliente: "+itemR.get(0).um);

                            rep.add("Fecha        Corelativo");
                            rep.add("Producto   Cant    Precio    Total");
                            rep.line();
                            acc = 2;
                        }

                        //Nueva validacion
                        if(acc==2){
                            rep.empty();
                            rep.addtwo(itemR.get(i).serie, itemR.get(i).um);
                            rep.empty();
                            acc=3;
                        }else if(i+1!=itemR.size() && !itemR.get(i).serie.equals(itemR.get(i+1).serie)){
                            rep.empty();
                            rep.addtwo(itemR.get(i).serie, itemR.get(i).um);
                            rep.empty();
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

                        }else if(i+1==itemR.size()){

                            rep.line();
                            rep.add4lrrT("", ""+cantF, 0.0,tot);
                            rep.empty();
                            cantF = 0;
                            tot = 0;

                        }else {

                            rep.add4lrrT(itemR.get(i).descrip, ""+itemR.get(i).cant, itemR.get(i).imp,itemR.get(i).total);

                        }

                        cantF += itemR.get(i).cant;
                        tot += itemR.get(i).total;
                        SumaCant += itemR.get(i).cant;
                        totF += itemR.get(i).total;
                    }
                }
                rep.line();

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                msgbox(e.getMessage());
                return false;
            }

        }

        protected boolean buildFooter() {

            try {

                if(gl.reportid==1) rep.add3Tot(SumaCant, totSinImpF, impF, totF);
                if(gl.reportid==2) rep.add4lrrTot("Total: ","",Integer.toString(cantfF),totF);
                if(gl.reportid==3) {
                    if(validCB==0){
                        rep.add4lrrTotPorc(Integer.toString(SumaCant), "",totF,0.0);
                    }else {
                        rep.addtot3("","",totF);
                    }

                }
                if(gl.reportid==4 || gl.reportid==5) rep.add4lrrTotPorc("",Integer.toString(SumaCant),totF,0.0);
                if(gl.reportid==6) {
                    if(validCB==0){
                        rep.add4lrrTotV(""+SumaCant, "",totF, totSinImpF);
                    }else {
                        rep.addtot3("","",totF);
                    }
                }
                if(gl.reportid==7 || gl.reportid==8) rep.add4(totF, impF, totSinImpF, 0.0);
                if(gl.reportid==11) rep.add3lrrTot("", ""+SumaCant, totF);
                if(gl.reportid==12) rep.add4lrrT("Total: ", ""+SumaCant, 0.0,totF);
                rep.empty();

                return true;
            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                return false;
            }

        }

    }

    //endregion

    //region Activity Events

    @Override
    protected  void onResume(){
        try{
            super.onResume();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    @Override
    protected void onPause() {
        try{
            super.onPause();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }p
    }

    //endregion

    //region AUX

    public void clear(View view){
        txtFill.setText("");
        lblProd.setText("");
    }

    public void showDateDialog1(View view) {
        try{
            obtenerFecha();
            dateTxt=false;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void showDateDialog2(View view) {
        try{
            obtenerFecha();
            dateTxt=true;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion
}
