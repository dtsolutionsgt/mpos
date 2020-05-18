package com.dtsgt.fel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class FelFactura extends PBase {

    // gl.peFEL  "SIN FEL", "INFILE"

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturapObj D_facturapObj;

    private clsClasses.clsD_factura fact=clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd=clsCls.new clsD_facturad();
    private clsClasses.clsD_facturap factp=clsCls.new clsD_facturap();

    private ArrayList<String> facts = new ArrayList<String>();

    private String felcorel;
    private boolean multiflag;
    private int ftot,ffail,fidx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fel_factura);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);lbl1.setText("");
        lbl2 = (TextView) findViewById(R.id.textView150);lbl2.setText("");
        lbl3 = (TextView) findViewById(R.id.textView152);lbl3.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        felcorel=gl.felcorel;multiflag=felcorel.isEmpty();
        gl.feluuid="";

        fel=new clsFELInFile(this,this);

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);

        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        buildList();

        ftot=0;ffail=0;fidx=0;

        if (facts.size()>0) {
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    if (multiflag) {
                        contingencia();
                    } else {
                        certificacion();
                    }
                }
            };
            mtimer.postDelayed(mrunner,200);
        } else {
            finish();return;
        }

    }

    //region Events

    //endregion

    //region Main

    private void procesafactura() {

        updateLabel();

        Handler mtimer = new Handler();
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                buildFactXML();
                fel.certificacion();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void  certificacion() {
        lbl1.setText("Procesando factura electronica");
        lbl3.setText("Espere, por favor . . .");

        buildFactXML();
        fel.certificacion();
    }

    private void contingencia() {
        lbl1.setText("Procesando factura ");
        lbl3.setText("Sin procesar : 0");
        buildFactXML();
        procesafactura();
    }

    @Override
    public void felCallBack()  {
        if (multiflag) {
            fidx++;
            //if (fidx<ftot-1) {
            if (fidx<10) {
                if (fel.errorflag) ffail++;
                procesafactura();
            } else {
                toast("Completo");
            }
        } else {
            callBackSingle();
        }
    }

    private void callBackSingle() {
        pbar.setVisibility(View.INVISIBLE);
        if (!fel.errorflag) {
            gl.feluuid=fel.fact_uuid;
        } else {
            gl.feluuid="";
            toastlong("Ocurrio error en FEL :\n\n"+ fel.error);
        }
        finish();
    }

    private void buildFactXML() {
        try {
            fel.iniciar(2005041102);
            fel.emisor("GEN","1","","1000000000K","DEMO");
            fel.emisorDireccion("Direccion","GUATEMALA","GUATEMALA","GT");
            fel.receptor("CF","Consumidor Final","Ciudad");

            fel.detalle("Producto 1",1,"UNI",10,10,0);
            //fact.detalle("Producto 2",2,"UNI",15,30,0);

            fel.completar("ZR37-46");


        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void buildList() {
        ftot=0;

        try {
            D_facturaObj.fill("WHERE (FEELNUMERO=0) AND (ANULADO='N')");

            facts.clear();
            for (int i = 0; i <D_facturaObj.count; i++) {
                facts.add(D_facturaObj.items.get(i).corel);
            }
            ftot=facts.size();
        } catch (Exception e) {
            msgexit(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateLabel() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable(){
                    public void run() {
                        lbl1.setText("Factura "+(fidx+1)+" / "+ftot);
                        lbl3.setText("Fail "+(ffail)+" / "+(fidx+1));
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    //endregion

    //region Dialogs

    public void msgexit(String msg) {

        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setCancelable(false);

            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.feluuid="";finish();
                }
            });
            dialog.show();

        } catch (Exception ex) {
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            D_facturaObj.reconnect(Con,db);
            D_facturadObj.reconnect(Con,db);
            D_facturapObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //endregion


}
