package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_cajapagosObj;


public class Caja extends PBase {

    private TextView lblTit, lblMontoIni, lblMontoFin,lblMontoCred;
    private EditText Vendedor, Fecha, MontoIni, MontoFin, MontoCred;

    private clsClasses.clsP_cajacierre itemC;

    private double fondoCaja=0, montoIni=0, montoFin=0, montoDif=0, montoCred=0;

    private int acc=1,msgAcc=0,cred=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        super.InitBase();
        addlog("Caja",""+du.getActDateTime(),gl.vend);

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

            sql="SELECT * FROM D_FACTURAP P INNER JOIN D_FACTURA F ON P.COREL=F.COREL WHERE F.KILOMETRAJE=0 AND P.CODPAGO!=1";
            dt=Con.OpenDT(sql);

            if(dt.getCount()>0){
                lblMontoCred.setVisibility(View.VISIBLE);
                MontoCred.setVisibility(View.VISIBLE);cred=1;
            }else {
                lblMontoCred.setVisibility(View.INVISIBLE);
                MontoCred.setVisibility(View.INVISIBLE);cred=0;
            }

            if(dt!=null) dt.close();
        }catch (Exception e){
            msgbox("Error en consulta 'onCreate Caja': "+e);
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if(gl.cajaid==1){
            lblMontoFin.setVisibility(View.INVISIBLE);
            MontoFin.setVisibility(View.INVISIBLE);
        }else if(gl.cajaid==3){
            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

            caja.fill(" WHERE ESTADO=0");
            MontoIni.setEnabled(false);
            MontoIni.setText(""+caja.last().fondocaja);
        }

        itemC = clsCls.new clsP_cajacierre();
    }

    //region Main

    public void save(View view){

        try{
            if(gl.cajaid==1 && !MontoIni.getText().toString().trim().isEmpty()){
                fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());

                if(fondoCaja>0){
                    saveMontoIni();
                }else{
                    msgbox("El monto inicial debe ser mayor a 0");
                }

            } else if(gl.cajaid==3 && !MontoFin.getText().toString().trim().isEmpty()){
                montoFin = Double.parseDouble(MontoFin.getText().toString().trim());

                if(montoFin>0){

                    if(cred==1 && !MontoCred.getText().toString().trim().isEmpty()){
                        montoCred = Double.parseDouble(MontoCred.getText().toString().trim());
                    }else  if(cred==1 && MontoCred.getText().toString().trim().isEmpty()){
                        msgbox("Se realizaron ventas con crédito, no puede dejar el monto crédito vacío");return;
                    }

                    fondoCaja = Double.parseDouble(MontoIni.getText().toString().trim());

                    montoDif();

                    if(montoDif!=0){
                        if(acc==1){
                            msgboxValidaMonto("El monto no cuadra, ¿Está seguro de continuar?");
                            acc=0;
                        }else {
                            saveMontoIni();
                        }

                    }else {
                        saveMontoIni();
                    }

                }else{
                    msgbox("El monto final debe ser mayor a 0");
                }

            }else {
                msgbox("El monto no puede ir vacío");
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error en save: "+e);return;
        }

    }

    public void montoDif(){
        Cursor dt;
        double tot,pago;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);


            if(gl.cajaid==3){
                caja.fill(" WHERE ESTADO = 0");
                fondoCaja = caja.last().fondocaja;
            }

            sql="SELECT P.CODPAGO, SUM(P.VALOR) " +
                    "FROM D_FACTURAP P " +
                    "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    "WHERE F.KILOMETRAJE=0 AND P.CODPAGO=1 " +
                    "GROUP BY P.CODPAGO";

            dt=Con.OpenDT(sql);

            if(dt==null) throw new Exception();
            if(dt.getCount()==0) tot=0; else tot = fondoCaja+dt.getDouble(1);
            if(dt!=null) dt.close();


            sql="SELECT SUM(MONTO) FROM P_cajapagos WHERE COREL=0";
            dt = Con.OpenDT(sql);

            if(dt.getCount()==0) pago=0; else pago = dt.getDouble(0);
            if(dt!=null) dt.close();

            montoDif = tot - pago;
            montoDif = montoDif - montoFin;

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error montoDif: "+e);return;
        }
    }


    public void saveMontoIni(){
        Cursor dt;
        int fecha=0;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);


            if(gl.cajaid==1) {
                caja.fill();

                if (caja.count != 0) {
                    gl.corelZ = caja.last().corel + 1;
                } else {
                    gl.corelZ = 1;
                }

            }else if(gl.cajaid==3){
                caja.fill(" WHERE ESTADO = 0");
                gl.corelZ = caja.last().corel;
                fecha = caja.last().fecha;
                fondoCaja = caja.last().fondocaja;
            }

            itemC.sucursal =  gl.tienda;
            itemC.ruta = gl.caja;
            itemC.corel = gl.corelZ;
            itemC.vendedor =  gl.vend;
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

                msgAcc=1;
                msgAskExit("Inicio de caja correcto");

            }else if(gl.cajaid==3){

                sql="SELECT P.CODPAGO, P.TIPO, SUM(P.VALOR) " +
                        "FROM D_FACTURAP P " +
                        "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                        "WHERE F.KILOMETRAJE=0 " +
                        "GROUP BY P.CODPAGO";

                dt=Con.OpenDT(sql);

                if(dt==null) throw new Exception();

                dt.moveToFirst();

                while(!dt.isAfterLast()){

                    itemC.codpago=dt.getInt(0);
                    itemC.fecha = fecha;
                    itemC.estado = 1;

                    if(dt.getInt(0)==1){
                        montoIni = fondoCaja+dt.getDouble(2);
                        itemC.montoini = montoIni;
                        itemC.montofin = montoFin;
                        itemC.montodif = montoFin-montoIni;

                        sql="UPDATE P_CAJACIERRE SET CODPAGO="+itemC.codpago+" WHERE (SUCURSAL="+itemC.sucursal+") AND (RUTA="+itemC.ruta+") AND (COREL="+itemC.corel+")";
                        db.execSQL(sql);

                        caja.update(itemC);
                    }

                    if(cred==1){
                        if(dt.getInt(0)!=1){
                            itemC.montoini=0;
                            itemC.montofin = montoCred;
                            itemC.montodif = montoCred;

                            caja.add(itemC);
                        }
                    }

                    dt.moveToNext();
                }

                sql="UPDATE D_FACTURA SET KILOMETRAJE = "+ gl.corelZ +" WHERE KILOMETRAJE = 0";
                db.execSQL(sql);

                sql="UPDATE D_DEPOS SET CODIGOLIQUIDACION = "+ gl.corelZ +" WHERE CODIGOLIQUIDACION = 0";
                db.execSQL(sql);

                sql="UPDATE D_MOV SET CODIGOLIQUIDACION = "+ gl.corelZ +" WHERE CODIGOLIQUIDACION = 0";
                db.execSQL(sql);

                Toast.makeText(this, "Fin de turno correcto", Toast.LENGTH_LONG).show();

                gl.reportid=10;
                startActivity(new Intent(this, CierreX.class));
                super.finish();
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Error saveMontoIni: "+e);return;
        }
    }


    public void doExit(View view) {
        msgAskExit("¿Salir?");
    }

    //endregion

    //region msgbox

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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
                    finish();
                }
            });
            msgAcc=0;
        }


        dialog.show();

    }

    public void msgboxValidaMonto(String msg) {

        try{

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
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

    //endregion

}
