package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_cajacierreObj;


public class Caja extends PBase {

    private TextView lblTit, lblMontoIni;
    private EditText Vendedor, Fecha, MontoIni;

    private clsClasses.clsP_cajacierre itemC;

    private double montoIni=0;

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

        if(gl.cajaid==1) lblMontoIni.setText("MONTO INICIAL");

        Vendedor.setText(gl.vendnom);
        Fecha.setText(du.getActDateStr());
        lblTit.setText(gl.titReport);


        itemC = clsCls.new clsP_cajacierre();

    }

    //region Main

    public void save(View view){

        try{
            if(!MontoIni.getText().toString().trim().isEmpty()){
                montoIni = Double.parseDouble(MontoIni.getText().toString().trim());

                if(montoIni>0){
                    msgboxValidaMonto(montoIni);
                }else{
                    msgbox("El monto debe ser mayor a 0");
                }

            }else {
                msgbox("El monto no puede ir vacío");
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }


    public void saveMontoIni(){
        int corel;

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
            caja.fill();

            if(caja.count!=0){
                corel = caja.last().corel+1;
            }else {
                corel = 1;
            }

            itemC.sucursal =  gl.tienda;
            itemC.ruta = gl.caja;
            itemC.corel = corel;
            itemC.estado = 0;
            itemC.fecha = (int) du.getFechaActual();
            itemC.vendedor =  gl.vend;
            itemC.montoini = montoIni;
            itemC.montofin = 0;
            itemC.montodif = 0;
            itemC.statcom = "N";

            caja.add(itemC);

            Toast.makeText(this, "Inicio de turno correcto", Toast.LENGTH_LONG).show();

            super.finish();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }


    public void doExit(View view) {
        msgAskExit("Salir");
    }

    public void valida(){
        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

            if(gl.cajaid==1){
                caja.fill();
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region msgbox

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

    public void msgboxValidaMonto(double monto) {

        try{

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage("El monto "+ monto + " es correcto?");
            dialog.setCancelable(false);

            dialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(gl.cajaid==1){
                        saveMontoIni();
                    }else if(gl.cajaid==3){
                        msgbox("Código en proceso");
                    }
                }
            });

            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();


        } catch (Exception ex) {
        }
    }

    //endregion

}
