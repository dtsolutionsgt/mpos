package com.dtsgt.fel;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;


public class FelAnulacion extends PBase {

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;

    private String felcorel,uuid;
    private int ftot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fel_anulacion);

        vApp=this.getApplication();
        gl=((appGlobals) this.getApplication());

        mu=new MiscUtils(this);
        du=new DateUtils();

        lbl1 = (TextView) findViewById(R.id.textView7);lbl1.setText("Procesando anulacion");
        lbl2 = (TextView) findViewById(R.id.textView150);lbl2.setText("");
        lbl3 = (TextView) findViewById(R.id.textView152);lbl3.setText("Espere, por favor . . .");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        felcorel=gl.felcorel;gl.feluuid="";

        fel=new clsFELInFile(this,this);

        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                anulacion();
            }
        };
        mtimer.postDelayed(mrunner,200);

    }

    //region Events

    //endregion

    //region Main

    private void anulacion() {
        buildAnulXML();
        fel.anulacion(uuid);
    }

    @Override
    public void felCallBack()  {
        if (!fel.errorflag) {
            gl.feluuid="Anulado";
            finish();
        } else {
            gl.feluuid="";

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    msgexit("Ocurrio error en anulacion FEL :\n\n"+ fel.error);
                }
            };
            mtimer.postDelayed(mrunner,500);
        }
    }

    private void buildAnulXML() {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            clsClasses.clsD_factura fact=clsCls.new clsD_factura();

            D_facturaObj.fill("WHERE Corel='"+felcorel+"'");
            fact=D_facturaObj.first();

            uuid=fact.feeluuid;
            fel.anulfact(uuid,"1000000000K","CF", fact.fecha, fact.fecha);
         } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

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
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //endregion

}
