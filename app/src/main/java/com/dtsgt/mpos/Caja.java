package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_cajahoraObj;
import com.dtsgt.classes.clsP_sucursalObj;

import java.util.Random;

public class Caja extends PBase {

    private TextView lblTit, lblMontoIni, lblMontoFin,lblMontoCred;
    private EditText Vendedor, Fecha, MontoIni, MontoFin, MontoCred;

    private clsClasses.clsP_cajacierre itemC;
    private clsClasses.clsP_cajahora itemH;

    private double fondoCaja=0, montoIni=0, montoFin=0, montoDif=0, montoDifCred=0, montoCred=0;
    private String cap;
    private int acc=1,msgAcc=0,cred=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        super.InitBase();
        addlog("Caja",""+du.getActDateTime(), String.valueOf(gl.vend));

        lblTit = (TextView) findViewById(R.id.lblTit);
        lblMontoIni = (TextView) findViewById(R.id.textView133);
        MontoIni = (EditText) findViewById(R.id.editText19);
        Fecha = (EditText) findViewById(R.id.editText16);
        Vendedor = (EditText) findViewById(R.id.editText18);
        lblMontoFin = (TextView) findViewById(R.id.textView134);
        MontoFin = (EditText) findViewById(R.id.editText20);
        MontoCred = (EditText) findViewById(R.id.editText17);
        lblMontoCred = (TextView) findViewById(R.id.textView130);

        Vendedor.setText(gl.vendnom);
        Fecha.setText(du.getActDateStr());
        lblTit.setText(gl.titReport);

        try{
            Cursor dt;

            sql="SELECT * " +
                "FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                "INNER JOIN P_MEDIAPAGO M ON P.CODPAGO = M.CODIGO "+
                "WHERE F.KILOMETRAJE=0 AND M.NIVEL <> 1";

            dt=Con.OpenDT(sql);

            if(dt.getCount()>0){
                lblMontoCred.setVisibility(View.VISIBLE);
                MontoCred.setVisibility(View.VISIBLE);
                cred=1;
            }else {
                lblMontoCred.setVisibility(View.INVISIBLE);
                MontoCred.setVisibility(View.INVISIBLE);
                cred=0;
            }

            if(dt!=null) dt.close();
        }catch (Exception e){
            msgbox("Error en consulta 'onCreate Caja': "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (gl.cajaid==1){ //Inicio de Caja

            lblMontoFin.setVisibility(View.INVISIBLE);
            MontoFin.setVisibility(View.INVISIBLE);
            MontoIni.requestFocus();

            validacionesInicio();

        } else if(gl.cajaid==3) { // Cierre de Caja

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
            caja.fill(" WHERE ESTADO=0");
            gl.fondoCaja = caja.last().fondocaja;

            MontoIni.setEnabled(false);
            MontoIni.setText(""+gl.fondoCaja);

            if (app.pendientesPago("")>0)  msgbox("Existen facturas pendientes de pago");
            MontoFin.requestFocus();
        }

        itemC = clsCls.new clsP_cajacierre();
        itemH = clsCls.new clsP_cajahora();

        setHandlers();

        validaFacturas();
    }

    //region Events

    public void save(View view){
        guardar();
    }

    public void doExit(View view) {
        msgAskExit("¿Salir?");
    }

    //endregion

    //region Main

    private void setHandlers(){

        try{
            MontoIni.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        guardar();
                        return true;
                    }
                    return false;
                }
            });
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void guardar(){

        try{

            app.logoutUser(du.getActDateTime());

            if(gl.cajaid==1 && !MontoIni.getText().toString().trim().isEmpty()){

                fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());

                if(fondoCaja>0){
                    saveMontoIni();
                }else{
                    msgbox("El monto inicial debe ser mayor a 0");
                }

            } else if(gl.cajaid==3 && !MontoFin.getText().toString().trim().isEmpty()){
                montoFin = Double.parseDouble(MontoFin.getText().toString().trim());

                if(montoFin>=0){

                    if(cred==1 && !MontoCred.getText().toString().trim().isEmpty()){
                        montoCred = Double.parseDouble(MontoCred.getText().toString().trim());
                        if(montoCred<0){ msgbox("Se realizaron ventas con crédito, el monto crédito no puede ser menor a 0");return;}
                        if(montoCred==0){ msgbox("Se realizaron ventas con crédito, el monto crédito no puede ser 0");return;}
                    }else  if(cred==1 && MontoCred.getText().toString().trim().isEmpty()){
                        msgbox("Se realizaron ventas con crédito, no puede dejar el monto crédito vacío");return;
                    }

                    fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());

                    montoDif();

                    if(montoDif!=0){
                        if(acc==1){
                            msgboxValidaMonto("El monto de efectivo no cuadra");
                            //acc=0;
                        }else {
                            saveMontoIni();
                        }

                    }else {

                        if(cred==1){

                            if(montoDifCred!=0){
                                if(acc==1){
                                    msgboxValidaMonto("El monto de crédito no cuadra,");
                                    //acc=0;
                                } else {
                                    saveMontoIni();
                                }
                            }else {
                                saveMontoIni();
                            }
                        }else {
                            saveMontoIni();
                        }
                    }

                }else if(montoFin<0){
                    msgbox("El monto final no puede ser menor a 0");
                }

            }else {
                msgbox("El monto no puede ir vacío");
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error al guardar el inicio de caja: "+e);return;
        }

    }

    public void montoDif(){
        Cursor dt;
        double tot,totCred,pago;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);


            if(gl.cajaid==3){
                caja.fill(" WHERE ESTADO = 0");
                fondoCaja = caja.last().fondocaja;
            }

            /*
            sql="SELECT P.CODPAGO, SUM(P.VALOR) FROM D_FACTURAP P " +
                    "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    "WHERE F.KILOMETRAJE=0 AND P.CODPAGO=1 " +
                    "GROUP BY P.CODPAGO";
             */

            sql="SELECT P.CODPAGO, SUM(P.VALOR) FROM D_FACTURAP P " +
                "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                "WHERE F.KILOMETRAJE=0 AND P.TIPO='E' AND F.ANULADO=0 GROUP BY P.TIPO";

            dt=Con.OpenDT(sql);

            if(dt==null) throw new Exception();
            if(dt.getCount()==0) {
                tot=fondoCaja;
            } else {
                double vval=dt.getDouble(1);
                tot = fondoCaja+vval;
            }
            if(dt!=null) dt.close();

            if(cred==1){
                sql="SELECT P.CODPAGO, SUM(P.VALOR) " +
                        "FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                        "WHERE F.KILOMETRAJE=0 AND P.TIPO='K' AND F.ANULADO=0 " +
                        "GROUP BY P.CODPAGO";

                dt=Con.OpenDT(sql);

                if(dt==null) throw new Exception();
                if(dt.getCount()==0) {
                    totCred=0;
                } else{
                    totCred = dt.getDouble(1);
                }
                if(dt!=null) dt.close();

                montoDifCred = montoCred - totCred;
            }


            sql="SELECT SUM(MONTO) FROM P_cajapagos WHERE COREL=0";
            dt = Con.OpenDT(sql);

            if(dt.getCount()==0) {
                pago=0;
            } else {
                pago = dt.getDouble(0);
            }
            if(dt!=null) dt.close();

            montoDif = tot - pago;
            montoDif = montoFin - montoDif;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error montoDif: "+e);return;
        }
    }

    public void saveMontoIni(){
        Cursor dt, dt2, dt3;
        int fecha=0, codpago=0;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
            clsP_cajahoraObj cajah = new clsP_cajahoraObj(this,Con,db);

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

            } else if (gl.cajaid==3){
                caja.fill(" WHERE ESTADO = 0");
                gl.corelZ = caja.last().corel;
                fecha = caja.last().fecha;
                fondoCaja = caja.last().fondocaja;
                itemC.codigo_cajacierre =  caja.first().codigo_cajacierre;
            }

            itemC.empresa=gl.emp;
            itemC.sucursal =  gl.tienda;
            itemC.ruta = gl.codigo_ruta;
            itemC.corel = gl.corelZ;
            itemC.vendedor =  gl.codigo_vendedor;
            itemC.fondocaja = fondoCaja;
            itemC.statcom = "N";

            if(gl.cajaid==1){

                itemC.fecha = (int) du.getFechaActual();

                itemC.estado = 0;
                itemC.codpago = 0;
                itemC.montoini = 0;
                itemC.montofin = 0;
                itemC.montodif = 0;

                caja.add(itemC);

                itemH.corel= gl.corelZ;
                itemH.fechaini=du.getActDateTime();
                itemH.fechafin=0;
                cajah.add(itemH);

                msgAcc=1;

                gl.inicio_caja_correcto = true;

                msgAskExit("Inicio de caja correcto");

            } else if(gl.cajaid==3) {

                //#CKFK 20200711 Agregué la condicion de que sume las que no están anuladas AND F.ANULADO = 0
                sql="SELECT P.CODPAGO, P.TIPO, SUM(P.VALOR),M.NIVEL " +
                        "FROM D_FACTURAP P " +
                        "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                        "INNER JOIN P_MEDIAPAGO M ON P.CODPAGO = M.CODIGO "+
                        "WHERE F.KILOMETRAJE=0 AND F.ANULADO = 0  " +
                        "GROUP BY P.CODPAGO, P.TIPO, M.NIVEL";

                dt=Con.OpenDT(sql);

                if(dt==null) throw new Exception();

                if(dt.getCount()!=0){

                    dt.moveToFirst();

                    while(!dt.isAfterLast()){

                        itemC.codpago=dt.getInt(0);
                        itemC.fecha = fecha;
                        itemC.estado = 1;

                        if(dt.getInt(3)==1){ //#CKFK 20200623 Cuando la forma de pago es Contado

                            montoIni = fondoCaja+dt.getDouble(2);
                            itemC.montoini = montoIni;
                            itemC.montofin = montoFin;
                            itemC.montodif = montoFin-montoIni;
                            //itemC.codpago=1;

                            sql="UPDATE P_CAJACIERRE SET CODPAGO="+itemC.codpago+" WHERE (SUCURSAL="+itemC.sucursal+") AND (RUTA="+itemC.ruta+") AND (COREL="+itemC.corel+")";
                            db.execSQL(sql);

                            caja.update(itemC);
                        }

                        if(cred==1){

                            if(dt.getInt(3)==4){ //#CKFK 20200623 Cuando la forma de pago es Crédito

                                itemC.codigo_cajacierre=itemC.codigo_cajacierre+"C";
                                montoIni = dt.getDouble(2);
                                itemC.montoini = montoIni;
                                itemC.montofin = montoCred;
                                itemC.montodif = montoCred - montoIni;
                                //itemC.codpago=5;

                                caja.add(itemC);
                            }
                        }

                        dt.moveToNext();
                    }

                } else if(dt.getCount()==0){

                    sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 1";
                    dt2=Con.OpenDT(sql);

                    if(dt2!=null) {
                        if(dt2.getCount()>0){
                            dt2.moveToFirst();
                            codpago=dt2.getInt(0);
                        }

                        dt2.close();
                    }

                    itemC.codpago=codpago;
                    itemC.fecha = fecha;
                    itemC.estado = 1;
                    montoIni = fondoCaja;
                    itemC.montoini = montoIni;
                    itemC.montofin = montoFin;
                    itemC.montodif = montoFin-montoIni;

                    sql="UPDATE P_CAJACIERRE SET CODPAGO="+itemC.codpago+" WHERE (SUCURSAL="+itemC.sucursal+") AND (RUTA="+itemC.ruta+") AND (COREL="+itemC.corel+")";
                    db.execSQL(sql);

                    caja.update(itemC);
                }

                sql="UPDATE D_FACTURA SET KILOMETRAJE = "+ gl.corelZ +" WHERE KILOMETRAJE = 0";
                db.execSQL(sql);

                /*
                try {
                    sql="SELECT COREL FROM D_FACTURA WHERE KILOMETRAJE = 0";
                    dt3=Con.OpenDT(sql);
                    if (dt3.getCount()>0) toastlong("Ocurrio inconsistencia en cierre del dia, informe el soporte.");
                } catch (Exception e) {
                    toastlong("saveMontoIni : "+e.getMessage());
                }
                */

                //sql="UPDATE D_DEPOS SET CODIGOLIQUIDACION = "+ gl.corelZ +" WHERE CODIGOLIQUIDACION = 0";
                //db.execSQL(sql);

                sql="UPDATE D_MOV SET CODIGOLIQUIDACION = "+ gl.corelZ +" WHERE CODIGOLIQUIDACION = 0";
                db.execSQL(sql);

                Toast.makeText(this, "Fin de turno correcto", Toast.LENGTH_LONG).show();

                gl.reportid=10;
                gl.FinMonto=montoFin;
                startActivity(new Intent(this, CierreX.class));

                Handler mtimer = new Handler();
                Runnable mrunner=new Runnable() {
                    @Override
                    public void run() {
                        gl.autocom = 1;
                        startActivity(new Intent(Caja.this,WSEnv.class));
                    }
                };
                mtimer.postDelayed(mrunner,200);


                super.finish();
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error saveMontoIni: "+e);return;
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

    //region Dialogs

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        if(msgAcc==0){
            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });
        }else if(msgAcc==1){
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (isNetworkAvailable()) {
                        gl.recibir_automatico = true;
                        startActivity(new Intent(Caja.this,WSRec.class));
                    } else {
                        toast("No hay conexión a internet");
                    }
                    finish();
                }
            });
            msgAcc=0;
        }


        dialog.show();

    }

    private void msgAskSend(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enviaAvizo();
            }
        });

        dialog.show();
    }

    public void msgboxValidaMonto(String msg) {
        String mm=msg;

        try{
            captcha();

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(msg+"\nIngrese valor "+cap+" para continuar");

            final EditText input = new EditText(this);
            alert.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER );
            input.setText("");
            input.requestFocus();

            alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        String s=input.getText().toString();
                        if (!s.equalsIgnoreCase(cap)) throw new Exception();

                        acc=0;
                        saveMontoIni();
                    } catch (Exception e) {
                        acc=1;
                        mu.msgbox("Valor incorrecto");
                    }
                }
            });

            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();

        } catch (Exception ex) {
            msgbox("Error msgboxValidaMonto: "+ex);return;
        }
    }

    /*
    public void msgboxValidaMonto(String msg) {

        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setCancelable(false);

            dialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    saveMontoIni();
                }
            });

            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();


        } catch (Exception ex) {
            msgbox("Error msgboxValidaMonto: "+ex);return;
        }
    }

     */

    //endregion

    //region Aux

    private void validaFacturas() {
        long fi,ff;

        if (!app.usaFEL()) return;

        try {
            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.addDays(du.getActDate(),-5);fi=du.ffecha00(fi);
            ff=du.addDays(ff,-1);ff=du.ffecha00(ff);
            sql="WHERE (FECHA>="+fi+") AND (FECHA<="+ff+") AND (FEELUUID=' ')";

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

        try {
            subject="Facturas pendientes de certificacion : "+gl.rutanom+" ID : "+gl.codigo_ruta;
            body="Estimado usuario,\n\nMPos reporta facturas pendientes de certificaciones de mas de un dia.\n" +
                    "Por favor comuniquese con el soporte para solucionar el problema.\n" +
                    "En el caso de que una factura supere 4 días sin certificacion la aplicacion no permite vender.\n\n" +
                    "Saludos\nDT Solutions S.A.\n";

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            String cor=P_sucursalObj.first().correo;if (cor.indexOf("@")<2) cor="";

            String[] TO = {"jpospichal@dts.com.gt"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"jpospichal@dts.com.gt"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            if (!cor.isEmpty()) emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
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

    //endregion

}
