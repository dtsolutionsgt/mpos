package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_cajahoraObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_cierre_credObj;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.classes.extMontoDlg;
import com.dtsgt.ladapt.LA_T_cierre_cred;
import com.dtsgt.webservice.wsOpenDT;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Caja extends PBase {

    private ListView listView;
    private TextView lblTit, lblMontoIni, lblMontoFin,lblMontoCred;
    private EditText Vendedor, Fecha, MontoIni, MontoFin, MontoCred;
    private TextView FechaB;
    private ImageView imgpend;

    private wsOpenDT wso;
    private Runnable rnDateCallback;
    private extMontoDlg mdlg = new extMontoDlg();

    public ArrayList<clsClasses.clsT_cierre_cred> items= new ArrayList<clsClasses.clsT_cierre_cred>();

    private clsT_cierre_credObj T_cierre_credObj;

    private LA_T_cierre_cred adapter;

    private clsClasses.clsP_cajacierre itemC;
    private clsClasses.clsP_cajahora itemH;
    private clsClasses.clsT_cierre_cred itemCr;

    private double fondoCaja=0, montoIni=0, montoFin=0, gmontoDif =0, montoDifCred=0, montoCred=0,venta_total;
    private String cap;
    private int acc=1,msgAcc=0,cred=0,corelidx=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        super.InitBase();
        addlog("Caja",""+du.getActDateTime(), String.valueOf(gl.vend));

        listView =findViewById(R.id.listView1);
        lblTit = findViewById(R.id.lblTit);
        lblMontoIni = findViewById(R.id.textView133);
        MontoIni = findViewById(R.id.editText19);
        Fecha = findViewById(R.id.editText16);
        FechaB = findViewById(R.id.textView302);
        Vendedor = findViewById(R.id.editText18);
        lblMontoFin = findViewById(R.id.textView134);
        MontoFin = findViewById(R.id.editText20);
        MontoCred = findViewById(R.id.editText17);
        lblMontoCred = findViewById(R.id.textView130);
        imgpend=findViewById(R.id.imageView107);

        Vendedor.setText(gl.vendnom);
        lblTit.setText(gl.titReport);

        String sf=du.sfecha(du.getActDate());
        Fecha.setText(sf);FechaB.setText(sf);

        T_cierre_credObj=new clsT_cierre_credObj(this,Con,db);

        try {
            Cursor dt;

            sql="SELECT * " +
                "FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                "INNER JOIN P_MEDIAPAGO M ON P.CODPAGO = M.CODIGO "+
                "WHERE F.KILOMETRAJE=0 AND M.NIVEL <> 1";

            sql="SELECT SUM(P.VALOR)  ,P.CODPAGO, M.NOMBRE " +
                    "FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    "INNER JOIN P_MEDIAPAGO M ON P.CODPAGO = M.CODIGO " +
                    "WHERE F.KILOMETRAJE=0 AND M.NIVEL <> 1 " +
                    "GROUP BY P.CODPAGO, M.NOMBRE " +
                    "ORDER BY M.NOMBRE ";

            dt=Con.OpenDT(sql);

            MontoCred.setVisibility(View.INVISIBLE);
            items.clear();

            db.execSQL("DELETE FROM T_cierre_cred");

            if (dt.getCount()>0){

                clsT_cierre_credObj T_cierre_credObj=new clsT_cierre_credObj(this,Con,db);

                listView.setVisibility(View.VISIBLE);
                lblMontoCred.setVisibility(View.VISIBLE);
                cred=1;

                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    itemCr = clsCls.new clsT_cierre_cred();

                    itemCr.id=dt.getInt(1);
                    itemCr.nombre=dt.getString(2);
                    itemCr.total=dt.getDouble(0);
                    itemCr.caja=0;

                    items.add(itemCr);
                    T_cierre_credObj.add(itemCr);

                    dt.moveToNext();
                }

                listItems();

            } else {
                listView.setVisibility(View.INVISIBLE);
                lblMontoCred.setVisibility(View.INVISIBLE);
                cred=0;
            }

            if(dt!=null) dt.close();
        }catch (Exception e){
            msgbox("Error en consulta 'onCreate Caja': "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (gl.cajaid==1){ //Inicio de Caja

            //msgbox("Inicio caja");

            lblMontoFin.setVisibility(View.INVISIBLE);
            MontoFin.setVisibility(View.INVISIBLE);
            MontoIni.requestFocus();

            validacionesInicio();

        } else if(gl.cajaid==3) { // Cierre de Caja
            //msgbox("Cierre caja");
            try {
                clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
                caja.fill(" WHERE ESTADO=0 ORDER BY COREL");
                gl.fondoCaja = caja.last().fondocaja;

                MontoIni.setEnabled(false);
                MontoIni.setText(""+gl.fondoCaja);


                if (app.pendientesPago("")>0) msgbox("Existen facturas pendientes de pago");

                MontoFin.requestFocus();
            } catch (Exception e) {
                String ss=e.getMessage();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        }

        validaPendiente();

        itemC = clsCls.new clsP_cajacierre();
        itemH = clsCls.new clsP_cajahora();

        setHandlers();

        validaFacturas();

        rnDateCallback = () -> dateCallback();

        app.getURL();
        wso=new wsOpenDT(gl.wsurl);

    }

    //region Events

    public void save(View view) {
        if (gl.cajaid==1) {
            try {
                fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());
            } catch (Exception e) {
                fondoCaja=0;
            }
            if (fondoCaja==0){
                msgbox("El monto inicial incorrecto");return;
            }
            msgAskFechaIni("¿Iniciar caja con fecha\n"+FechaB.getText().toString()+"?");
            //guardar();
        } else {
            guardar();
        }
    }

    public void doExit(View view) {
        msgAskExit("¿Salir?");
    }

    public void pendiente(View view){
        browse=1;
        startActivity(new Intent(this,CajaPagosPend.class));
    }

    public void doFondoClick(View view) {
        inputValorFondo();
    }

    public void doEfectClick(View view) {
        inputValorEfect();
    }

    private void setHandlers(){

        try{
            MontoIni.setOnKeyListener((v, keyCode, event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    guardar();
                    return true;
                }
                return false;
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsT_cierre_cred item = (clsClasses.clsT_cierre_cred)lvObj;

                    adapter.setSelectedIndex(position);
                    inputValor(item.nombre,item.caja,position);
                };
            });
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            adapter=new LA_T_cierre_cred(this,this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void guardar(){

        try{

            app.logoutUser(du.getActDateTime());

            if(gl.cajaid==1 && !MontoIni.getText().toString().trim().isEmpty()){
                fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());
                if(fondoCaja>0){
                    saveMontoIni();
                } else {
                    msgbox("El monto inicial debe ser mayor a 0");
                }
            } else if(gl.cajaid==3 && !MontoFin.getText().toString().trim().isEmpty()){
                montoFin = mu.round2(Double.parseDouble(MontoFin.getText().toString().trim()));
                gl.monto_final_ingresado=montoFin;

                if (montoFin>=0){
                    montoCred=0;
                    for (int i = 0; i <items.size(); i++) {
                        montoCred=montoCred+items.get(i).caja;
                    }

                    if(cred==1 && montoCred>0){
                    //if(cred==1 && !MontoCred.getText().toString().trim().isEmpty()){
                        //montoCred = Double.parseDouble(MontoCred.getText().toString().trim());
                        if(montoCred<0){ msgbox("Se realizaron ventas con crédito, el monto crédito no puede ser menor a 0");return;}
                        if(montoCred==0){ msgbox("Se realizaron ventas con crédito, el monto crédito no puede ser 0");return;}
                    }else  if(cred==1 && montoCred==0){
                        msgbox("Se realizaron ventas con crédito, no puede dejar el monto crédito vacío");return;
                    }

                    fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());

                    montoDif();

                    if(gmontoDif !=0){
                        if(acc==1){
                            msgboxValidaMonto("El monto de efectivo no cuadra: diferencia (" + gmontoDif + ")");
                            //acc=0;
                        } else saveMontoIni();
                    } else {
                        if(cred==1){
                            if(montoDifCred!=0){
                                if(acc==1){
                                    msgboxValidaMonto("El monto de crédito no cuadra diferencia: (" + montoDifCred + ")");
                                    //acc=0;
                                } else saveMontoIni();
                           } else saveMontoIni();
                        } else saveMontoIni();
                     }
                } else if(montoFin<0) msgbox("El monto final no puede ser menor a 0");
            } else msgbox("El monto no puede ir vacío");
        }  catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error al guardar el inicio de caja: "+e);return;
        }

    }

    public void montoDif(){

        Cursor dt;
        double tot =0;
        double totCred =0;
        double pago=0;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

            if(gl.cajaid==3){
                caja.fill(" WHERE ESTADO = 0  ORDER BY COREL");
                fondoCaja = caja.last().fondocaja;
            }

            venta_total=-1;

            sql=" SELECT P.CODPAGO, SUM(P.VALOR) FROM D_FACTURAP P " +
                " INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                " WHERE F.KILOMETRAJE=0 AND P.TIPO='E' AND F.ANULADO=0 " +
                " AND F.FECHA >= " + gl.lastDate +
                " GROUP BY P.TIPO ";

            dt=Con.OpenDT(sql);

            if(dt==null) throw new Exception("ERROR_202212020719: No se encontraron ventas al contado.");

            venta_total=0;

            if(dt.getCount()==0) {
                tot=fondoCaja;
            } else {
                double vval=dt.getDouble(1);
                venta_total=vval;
                tot = fondoCaja+vval;
            }

            dt.close();

            if (cred==1){

                sql="SELECT P.CODPAGO, SUM(P.VALOR) " +
                    " FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    " WHERE F.KILOMETRAJE=0 AND P.TIPO='K' AND F.ANULADO=0 " +
                    " AND F.FECHA >= " + gl.lastDate +
                    " GROUP BY P.CODPAGO";

                dt=Con.OpenDT(sql);

                if(dt==null) throw new Exception("ERROR_202212020717: No se encontraron las ventas al crédito.");

                if(dt.getCount()==0) {
                    totCred=0;
                } else{

                    //#EJC202212020745: Ciclo para sumar las diferentes formas de crédito.
                    dt.moveToFirst();

                    while(!dt.isAfterLast()){
                        totCred += mu.round2(dt.getDouble(1));
                        dt.moveToNext();
                    }

                }

                dt.close();

                venta_total+=totCred;
                montoDifCred = mu.round2(montoCred - totCred);
            }


            sql="SELECT SUM(MONTO) FROM P_cajapagos WHERE COREL=0";
            dt = Con.OpenDT(sql);

            if(dt.getCount()==0) {
                pago=0;
            } else {
                pago = dt.getDouble(0);
            }

            dt.close();

            gmontoDif = tot - pago;
            gmontoDif =mu.round2(gmontoDif);
            montoFin=mu.round2(montoFin);
            gmontoDif = mu.round2(montoFin - gmontoDif);

            //#EJC202212060953: Un cagadal!
            if (Math.abs(gmontoDif) <0.01) gmontoDif =0;

            Log.d("MontoDif",String.valueOf(gmontoDif));

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error montoDif: "+e);
        }
    }

    public void saveMontoIni(){

        Cursor dt, dt2;
        int codpago=0,ecor;
        Long fecha=0L;
        boolean finalizo_transaccion =false;

        try{

            db.beginTransaction();

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
            clsP_cajahoraObj cajah = new clsP_cajahoraObj(this,Con,db);

            writeCorelLog(1,gl.corelZ,"");

            if (gl.cajaid==1) {

                caja.fill();

                if (caja.count != 0) {
                    gl.corelZ = caja.last().corel + 1;
                    gl.inicia_caja_primera_vez = false;
                    //#CKFK 20200608 Puse esto en comentario para dejar solo el Corel único
                   // itemC.codigo_cajacierre =  caja.first().codigo_cajacierre;
                    itemC.codigo_cajacierre=gl.ruta+"_"+mu.getCorelBase();
                } else {
                    gl.corelZ = 1;
                    gl.inicia_caja_primera_vez = true;
                    itemC.codigo_cajacierre=gl.ruta+"_"+mu.getCorelBase();
                }

                writeCorelLog(2,gl.corelZ,"");

            } else if (gl.cajaid==3){

                writeCorelLog(3,gl.corelZ,"");

                caja.fill(" WHERE ESTADO = 0  ORDER BY COREL");
                gl.corelZ = caja.last().corel;

                caja.fill(" WHERE ESTADO = 0  ORDER BY COREL");
                gl.corelZ = caja.last().corel;

                writeCorelLog(4,gl.corelZ,"");

                fecha = caja.last().fecha;
                fondoCaja = caja.last().fondocaja;

                itemC.codigo_cajacierre =  caja.last().codigo_cajacierre;
            }

            writeCorelLog(5,gl.corelZ,"");

            itemC.empresa=gl.emp;
            itemC.sucursal =  gl.tienda;
            itemC.ruta = gl.codigo_ruta;
            itemC.corel = gl.corelZ;
            itemC.vendedor =  gl.codigo_vendedor;
            itemC.fondocaja = fondoCaja;
            itemC.statcom = "N";

            if (gl.cajaid==1) {

                writeCorelLog(6,gl.corelZ,"");

                itemC.fecha = du.getActDate();

                itemC.estado = 0;
                itemC.codpago = 0;
                itemC.montoini = 0;
                itemC.montofin = 0;
                itemC.montodif = 0;

                caja.add(itemC);

                try {
                    itemH.corel= itemC.corel;
                    itemH.fechaini=du.getActDateTime();
                    itemH.fechafin=0;
                    cajah.add(itemH);
                } catch (Exception e) {
                    //msgbox("Error saveMontoIni: "+e.getMessage());
                    throw new Exception(e.getMessage());
                }

                msgAcc=1;

                gl.inicio_caja_correcto = true;

                //#EJC20221124: Commit de transacción en inicio de caja.
                db.setTransactionSuccessful();
                db.endTransaction();

                msgAskExit("Inicio de caja correcto");

            } else if(gl.cajaid==3) {

                writeCorelLog(7,gl.corelZ,"");

                //#CKFK 20200711 Agregué la condicion de que sume las que no están anuladas AND F.ANULADO = 0
                sql=" SELECT P.CODPAGO, P.TIPO, SUM(P.VALOR),M.NIVEL " +
                    " FROM D_FACTURAP P " +
                    " INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    " INNER JOIN P_MEDIAPAGO M ON P.CODPAGO = M.CODIGO "+
                    " WHERE F.KILOMETRAJE=0 AND F.ANULADO = 0  AND M.NIVEL=1 " +
                    " AND F.FECHA >= " + gl.lastDate +
                    " GROUP BY P.CODPAGO, P.TIPO, M.NIVEL";

                dt=Con.OpenDT(sql);

                if(dt==null) throw new Exception();

                if (dt.getCount()!=0){

                    dt.moveToFirst();

                    while(!dt.isAfterLast()){

                        itemC.codpago=dt.getInt(0);
                        itemC.fecha = fecha;
                        itemC.estado = 1;

                        if (dt.getInt(3)==1) { //#CKFK 20200623 Cuando la forma de pago es Contado

                            clsP_cajacierreObj caja_inicio_contado = new clsP_cajacierreObj(this,Con,db);
                            caja_inicio_contado.fill(" WHERE ESTADO = 0 AND CODPAGO =0 ORDER BY COREL");

                            if (caja_inicio_contado !=null){
                                if (caja_inicio_contado.count >0){
                                    montoIni = mu.round2(fondoCaja+dt.getDouble(2));
                                    itemC.montoini = montoIni;
                                    itemC.montofin = montoFin;
                                    itemC.montodif = mu.round2(montoFin-montoIni);
                                    itemC.codigo_cajacierre =caja_inicio_contado.last().codigo_cajacierre;
                                    caja.update(itemC);
                                }
                            }
                        }

                        /*
                        if (cred==1) {

                            if(dt.getInt(3)==4){ //#CKFK 20200623 Cuando la forma de pago es Crédito

                                corelidx++;

                                try {
                                    sql="DROP INDEX IX_P_CAJACIERRE ";
                                    db.execSQL(sql);
                                } catch (Exception e) {
                                    String ss=e.getMessage();
                                    ss=ss+"";
                                }

                                sql="SELECT EMPRESA FROM P_cajacierre";
                                dt2=Con.OpenDT(sql);
                                if (dt2.getCount()>0) {
                                    sql="SELECT MAX(EMPRESA) FROM P_cajacierre";
                                    dt2=Con.OpenDT(sql);
                                    ecor=dt2.getInt(0)+1;
                                } else {
                                    ecor=1;
                                }

                                itemC.empresa=ecor;
                                itemC.codigo_cajacierre=gl.ruta+"_"+mu.getCorelBase()+"C"+corelidx;
                                montoIni = mu.round2(dt.getDouble(2));
                                itemC.montoini = montoIni;
                                itemC.montofin = montoCred;
                                itemC.montodif = mu.round2(montoCred - montoIni);
                                itemC.estado=1;

                                caja.add(itemC);
                            }
                        }
                        */

                        dt.moveToNext();
                    }


                } else if(dt.getCount()==0) {

                    writeCorelLog(8,gl.corelZ,"");

                    sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 1";
                    dt2=Con.OpenDT(sql);

                    if(dt2!=null) {
                        if(dt2.getCount()>0){
                            dt2.moveToFirst();
                            codpago=dt2.getInt(0);
                        }
                        dt2.close();
                    } else {
                        codpago=7;
                    }

                    itemC.codpago=codpago;
                    itemC.fecha = fecha;
                    itemC.estado = 1;
                    montoIni = mu.round2(fondoCaja);
                    itemC.montoini = montoIni;
                    itemC.montofin = montoFin;
                    itemC.montodif = mu.round2(montoFin-montoIni);

                    if (itemC.codpago==0) itemC.codpago=7;
                    caja.update(itemC);

                }

                // Creditos

                T_cierre_credObj.fill();

                if (T_cierre_credObj.count>0) {

                    corelidx=0;
                    try {
                        sql="DROP INDEX IX_P_CAJACIERRE ";
                        db.execSQL(sql);
                    } catch (Exception e) {
                        String ss=e.getMessage();
                        ss=ss+"";
                    }

                    for (int i = 0; i < T_cierre_credObj.count; i++) {

                        corelidx++;

                        sql="SELECT EMPRESA FROM P_cajacierre";
                        dt2=Con.OpenDT(sql);
                        if (dt2.getCount()>0) {
                            sql="SELECT MAX(EMPRESA) FROM P_cajacierre";
                            dt2=Con.OpenDT(sql);
                            ecor=dt2.getInt(0)+1;
                        } else {
                            ecor=1;
                        }

                        itemC.empresa=ecor;
                        itemC.codigo_cajacierre=gl.ruta+"_"+mu.getCorelBase()+"C"+corelidx;
                        itemC.codpago=T_cierre_credObj.items.get(i).id;
                        montoIni=T_cierre_credObj.items.get(i).total;
                        montoCred=T_cierre_credObj.items.get(i).caja;
                        itemC.montoini =mu.round2(montoIni);
                        itemC.montofin = mu.round2(montoCred);
                        itemC.montodif = mu.round2(montoCred - montoIni);
                        itemC.estado=1;

                        caja.add(itemC);

                    }

                }






                sql="UPDATE D_FACTURA SET KILOMETRAJE = "+ gl.corelZ +" WHERE KILOMETRAJE = 0 AND FECHA >= " + gl.lastDate;
                db.execSQL(sql);

                sql="UPDATE P_CAJACIERRE SET ESTADO=1 WHERE COREL < " + gl.corelZ;
                db.execSQL(sql);

                writeCorelLog(9,gl.corelZ,"");

                if (gl.corelZ==0) {
                    setAddlog("saveMontoIni", "gl.corelZ=0 antes de UPDATE", "");
                    writeCorelLog(10,gl.corelZ,"gl.corelZ=0");
                    msgbox("NO se puede realizar fin del dia. Correlativo =0. Por favor, informe soporte.");
                    return;
                }

                db.setTransactionSuccessful();
                db.endTransaction();

                finalizo_transaccion =true;

                writeCorelLog(10,gl.corelZ,sql);

                Toast.makeText(this, "Fin de turno correcto", Toast.LENGTH_LONG).show();

                //#EJC20221202101: Función recursiva para actualizar las facturas que se quedaron sin su número de cierre.
                Actualizar_Facturas_Sin_Corel_Cierre2();

                writeCorelLog(12,gl.corelZ,"");

                gl.reportid=10;
                gl.FinMonto=montoFin;

                startActivity(new Intent(this, CierreX.class));

                Handler mtimer = new Handler();
                Runnable mrunner= () -> {
                    gl.autocom = 1;
                    startActivity(new Intent(Caja.this,WSEnv.class));
                };
                mtimer.postDelayed(mrunner,200);

                super.finish();
            }

        } catch (Exception e){
            if (!finalizo_transaccion) db.endTransaction();
            setAddlog("Error saveMontoIni", e.getMessage(), "");
            toastlong("Error saveMontoIni: "+e.getMessage());
            msgbox("Error saveMontoIni: "+e.getMessage());
        }
    }

    private void Actualizar_Facturas_Sin_Corel_Cierre2(){

        try {

            String vsql_cant_facturas_sin_cierre ="SELECT COUNT(KILOMETRAJE) AS CANTIDAD_FACTURAS_SIN_CIERRE FROM D_FACTURA WHERE KILOMETRAJE=0";
            Cursor dt_cant_facturas_sin_cierre=Con.OpenDT(vsql_cant_facturas_sin_cierre);

            if(dt_cant_facturas_sin_cierre!=null) {

                if (dt_cant_facturas_sin_cierre.getCount()>0) {

                    Log.d("Cantidad_Facturas_Sin_Cierre: ", String.valueOf(dt_cant_facturas_sin_cierre.getCount()));

                    dt_cant_facturas_sin_cierre.moveToFirst();

                    int vcant_facturas_sin_cierre=dt_cant_facturas_sin_cierre.getInt(0);

                    if  (vcant_facturas_sin_cierre>0) {

                        Cursor dtk_facturas_con_km_0=Con.OpenDT("SELECT * FROM D_FACTURA WHERE KILOMETRAJE=0 ORDER BY FECHA ");

                        if(dtk_facturas_con_km_0!=null) {

                            if (dtk_facturas_con_km_0.getCount()>0) {

                                Long fecha_inicial= Long.valueOf(0);
                                String sfecha_final="0";
                                String Ant_fecha_final="0";
                                String aux_sfecha_final="0";
                                String sfecha_inicial="0";
                                String sfecha_inicial_sin_hora="0";
                                double fecha_final =0;
                                Long corel_cierre = Long.valueOf(0);
                                String dia = "0";
                                String dia_sig="0";
                                int contador =0;

                                dtk_facturas_con_km_0.moveToFirst();

                                while (!dtk_facturas_con_km_0.isAfterLast()) {

                                    if (contador==0){
                                        fecha_inicial = dtk_facturas_con_km_0.getLong(3);
                                        sfecha_inicial = String.valueOf(fecha_inicial);
                                        sfecha_inicial = sfecha_inicial.substring(0,6);
                                        sfecha_inicial_sin_hora = sfecha_inicial + "0000";
                                    }

                                    Ant_fecha_final=String.valueOf(dtk_facturas_con_km_0.getLong(3));
                                    aux_sfecha_final = Ant_fecha_final.substring(0,6);
                                    dia = aux_sfecha_final.substring(4);
                                    //sfecha_final = sfecha_final + "235959";

                                    Log.d("fecha_sin_hora", sfecha_final);
                                    if (dia_sig.equalsIgnoreCase("0")){
                                        dia_sig=dia;
                                    }else if(!dia_sig.equalsIgnoreCase(dia)){
                                        break;
                                    }

                                    sfecha_final =Ant_fecha_final;

                                    contador+=1;

                                    dtk_facturas_con_km_0.moveToNext();
                                }

                                String sql_get_cierre = "SELECT * FROM P_CAJACIERRE WHERE FECHA=" + sfecha_inicial_sin_hora;
                                Cursor dt_cierre=Con.OpenDT(sql_get_cierre);
                                if(dt_cierre!=null) {
                                    if (dt_cierre.getCount()>0) {
                                        corel_cierre =dt_cierre.getLong(3);
                                        String sql_fact_sin_cierre_by_fecha = "SELECT * FROM D_FACTURA WHERE FECHA BETWEEN '" + fecha_inicial + "' AND '" +  sfecha_final + "' AND KILOMETRAJE =0";
                                        Cursor dtk_facturas_sin_cierre=Con.OpenDT(sql_fact_sin_cierre_by_fecha);
                                        if(dtk_facturas_sin_cierre!=null) {
                                            if (dtk_facturas_sin_cierre.getCount()>0) {
                                                Log.d("Cantidad_Facturas_Rango_Fechas: ", String.valueOf(dtk_facturas_sin_cierre.getCount()));
                                                String sql_update_cierre = "UPDATE D_FACTURA SET KILOMETRAJE = " + corel_cierre + " WHERE FECHA BETWEEN '" + fecha_inicial + "' AND '" +  sfecha_final + "' AND KILOMETRAJE=0";
                                                db.execSQL(sql_update_cierre);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        String vsql2 = "SELECT * FROM D_FACTURA WHERE KILOMETRAJE=0 ORDER BY FECHA";
                        Cursor dtk_upd1=Con.OpenDT(vsql2);

//                        else{
//                            writeCorelLog(11,gl.corelZ,"KILOMETRAJE=0 DESPUES de UPDATE");
//                            setAddlog("saveMontoIni", "KILOMETRAJE=0 DESPUES de UPDATE", "Cant :"+dt_cant_facturas_sin_cierre.getCount());
//                            msgAskExit2("El cierre se realizó correctamente, pero no fue posible asociar algunas facturas al cierre de caja. Por favor envíe la base de datos y notifique a soporte.");
//                            return;
//                        }

                        if(dtk_upd1!=null) {
                            if (dtk_upd1.getCount()>0) {
                                Actualizar_Facturas_Sin_Corel_Cierre2();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void validacionesInicio() {

        String ms="";

        try {

            if (app.usaFEL()) {
                if (gl.peAvizoFEL>0) {
                    long lf=du.getActDate();
                    lf=du.addDays(lf,-gl.peAvizoFEL);lf=du.ffecha24(lf);

                    clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
                    D_facturaObj.fill("WHERE (ANULADO=0) AND (FECHA<="+lf+") AND (FECHA>2009230000) AND (FEELUUID=' ') ");
                    if (D_facturaObj.count>0) ms="Existen facturas no certificadas por FEL próximas a expirar";
                }
            }

            if (!ms.isEmpty()) msgbox(ms);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Web service

    private void checkDate() {

        if (app.isOnWifi()==1) {
            try {
                sql="select Day(getdate()), Month(getdate()), Year(getdate())";
                wso.execute(sql,rnDateCallback);
                //toast("Validando fecha, espere . . . ");
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        } else {
            msgAskNoFecha("No se logró validar la fecha.\nPuede ser que el dispositivo tenga una fecha incorrecta.\n¿Continuar?");
        }
    }

    private void dateCallback() {

        long fa,fserv;

        if (wso.errflag) {
            msgbox("dateCallback: "+wso.error);
            msgAskNoFecha("No se logró validar la fecha.\nPuede ser que el dispositivo tenga una fecha incorrecta.\n¿Continuar?");
            return;
        } else {
            try {
                if (wso.openDTCursor.getCount()==0) {
                    msgAskNoFecha("No se logró validar la fecha.\nPuede ser que el dispositivo tenga una fecha incorrecta.\n¿Continuar?");
                    return;
                }

                wso.openDTCursor.moveToFirst();
                long fd=wso.openDTCursor.getInt(0);
                long fm=wso.openDTCursor.getInt(1);
                long fy=wso.openDTCursor.getInt(2);

                fserv=du.cfecha(fy,fm,fd);
                fa=du.getActDate();

                if (fserv==fa) {
                    guardar();
                } else {
                    msgAskNoFecha("Fecha de dispositivo incorrecta.\nLa fecha correcta deberia ser "+du.sfecha(fserv)+".\nContinuar puede causar serios problemas.\n¿Continuar?");
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        }
    }


    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        //#EJC20210224: Corrección de todos los males en el cierre.
        dialog.setCancelable(false);

        //EJC 20210223
        dialog.setCancelable(false);

        if(msgAcc==0){
            dialog.setPositiveButton("Si", (dialog1, which) -> finish());
            dialog.setNegativeButton("No", (dialog12, which) -> {});
        } else if(msgAcc==1) {
            dialog.setNeutralButton("OK", (dialog13, which) -> {
                if (isNetworkAvailable()) {
                    gl.recibir_automatico = true;
                    startActivity(new Intent(Caja.this,WSRec.class));
                } else {
                    toast("No hay conexión a internet");
                }
                finish();
            });
            msgAcc=0;
        }


        dialog.show();

    }

    private void msgAskExit2(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setNeutralButton("OK", (dialog13, which) -> {

            writeCorelLog(12,gl.corelZ,"");

            gl.reportid=10;
            gl.FinMonto=montoFin;

            startActivity(new Intent(this, CierreX.class));

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                gl.autocom = 1;
                startActivity(new Intent(Caja.this,WSEnv.class));
            };
            mtimer.postDelayed(mrunner,200);

            super.finish();

        });
        dialog.show();
    }

    private void msgAskSend(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        dialog.setNeutralButton("OK", (dialog1, which) -> {
            //enviaAvizo();
        });

        dialog.show();
    }

    private void msgAskFecha(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> checkDate());
        dialog.setNegativeButton("No", (dialog12, which) -> msgText("Por favor informe soporte"));
        dialog.show();
    }

    private void msgAskFechaIni(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> checkDate());
        dialog.setNegativeButton("No",(dialog1, which) -> {});
        dialog.show();
    }

    private void msgAskNoFecha(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> guardar());
        dialog.setNegativeButton("No", (dialog12, which) -> msgText("Por favor informe soporte"));
        dialog.show();
    }

    private void msgAskFechaContinuar(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> guardar());
        dialog.setNegativeButton("No", (dialog12, which) -> msgText("Por favor informe soporte"));
        dialog.show();

    }

    private void msgText(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> { });
        dialog.show();

    }

    private void inputValor(String titulo,double valor,int idx) {
        try {

            mdlg.buildDialog(Caja.this,titulo,"Salir");
            mdlg.setDPVisible(true);

            mdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdlg.dismiss();
                }
            });

            mdlg.onEnterClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        double val=Double.parseDouble(mdlg.getInput());
                        items.get(idx).caja=val;
                        T_cierre_credObj.update(items.get(idx));

                        listItems();
                    } catch (Exception e) {
                        mu.msgbox("Monto incorrecto");
                    }
                    mdlg.dismiss();
                }
            });

            mdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void inputValorFondo() {
        try {

            mdlg.buildDialog(Caja.this,"Monto inicial","Salir");
            mdlg.setDPVisible(true);

            mdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdlg.dismiss();
                }
            });

            mdlg.onEnterClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MontoIni.setText(mdlg.getInput());
                    mdlg.dismiss();
                }
            });

            mdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void inputValorEfect() {
        try {

            mdlg.buildDialog(Caja.this,"Efectivo","Salir");
            mdlg.setDPVisible(true);

            mdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdlg.dismiss();
                }
            });

            mdlg.onEnterClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MontoFin.setText(mdlg.getInput());
                    mdlg.dismiss();
                }
            });

            mdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void msgboxValidaMonto(String msg) {
        try {

            mdlg.buildDialog(Caja.this,msg,"Salir");
            mdlg.setDPVisible(false);

            captcha();

            mdlg.setTitle("Ingrese valor "+cap+" para continuar");

            mdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdlg.dismiss();
                }
            });

            mdlg.onEnterClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mdlg.getInput().equalsIgnoreCase(cap)) {
                        acc=0;
                        saveMontoIni();
                        mdlg.dismiss();
                    } else {
                        acc=1;
                        msgbox("Valor incorrecto");
                    }
                }
            });

            mdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        /*
        try{

            captcha();

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(msg+"\nIngrese valor "+cap+" para continuar");

            final EditText input = new EditText(this);
            alert.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER );
            input.setText("");
            input.requestFocus();

            alert.setPositiveButton("Aplicar", (dialog, whichButton) -> {
                try {
                    String s=input.getText().toString();
                    if (!s.equalsIgnoreCase(cap)) throw new Exception();

                    acc=0;
                    saveMontoIni();
                } catch (Exception e) {
                    acc=1;
                    mu.msgbox("Valor incorrecto");
                }
            });

            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();

        } catch (Exception ex) {
            msgbox("Error msgboxValidaMonto: "+ex);return;
        }

         */
    }

    //endregion

    //region Aux

    private void validaFacturas() {

        long fi,ff;

        if (!app.usaFEL()) return;

        try {

            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.addDays(du.getActDate(),-5);fi=du.ffecha00(fi);
            ff=du.addDays(ff,-1);ff=du.ffecha00(ff);
            sql="WHERE (ANULADO=0) AND (FECHA>="+fi+") AND (FECHA<="+ff+") AND (FEELUUID=' ')";

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill(sql);
            int fc=D_facturaObj.count;

            if (fc>0) msgAskSend("Existen facturas ("+fc+") pendientes de certificacion de mas que un dia. Deben informar el soporte.");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void enviaAvizo() {

        String subject,body;
        String dir=Environment.getExternalStorageDirectory()+"";

        try {
            subject="Facturas pendientes de certificacion : Ruta ID : "+gl.codigo_ruta;
            body="Estimado usuario,\n\nMPos reporta facturas pendientes de certificaciones de mas de un dia.\n" +
                    "Por favor comuniquese con el soporte para solucionar el problema.\n" +
                    "En el caso de que una factura supere 4 días sin certificacion la aplicacion no permite vender.\n\n" +
                    "Saludos\nDT Solutions S.A.\n";

            Uri uri=null;

            try {

                File f1 = new File(dir + "/posdts.db");
                File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
                File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
                FileUtils.copyFile(f1, f2);
                uri = Uri.fromFile(f3);

                app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

            } catch (IOException e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            String cor=P_sucursalObj.first().correo;if (cor.indexOf("@")<2) cor="";

            String[] TO = {"dtsolutionsgt@gmail.com"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"dtsolutionsgt@gmail.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setDataAndType(Uri.parse("mailto:"),"text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            if (!cor.isEmpty()) emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void captcha() {
        Random r = new Random();
        cap=""+(r.nextInt(8) + 1);
        cap+=""+(r.nextInt(8)  + 1);
        cap+=""+(r.nextInt(8)  + 1);
    }

    private void validaPendiente() {
        if (app.pendientesPago("")>0) imgpend.setVisibility(View.VISIBLE);else imgpend.setVisibility(View.INVISIBLE);
    }

    private void writeCorelLog(int id,int corel,String text) {
        String ss;
        try {
            ss="INSERT INTO T_BARRA_BONIF VALUES ('"+du.getActDateTime()+"','"+id+"',"+corel+",0,0,'"+text+"')";
            db.execSQL(ss);
        } catch (Exception e) {
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {
            T_cierre_credObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            validaPendiente();
        }
    }

    //endregion

}
