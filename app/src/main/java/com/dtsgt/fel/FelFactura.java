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
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsP_productoObj;
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
    private clsD_facturafObj D_facturafObj;
    private clsD_facturapObj D_facturapObj;
    private clsP_productoObj prod;

    private clsClasses.clsD_factura fact=clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd=clsCls.new clsD_facturad();
    private clsClasses.clsD_facturaf factf=clsCls.new clsD_facturaf();
    private clsClasses.clsD_facturap factp=clsCls.new clsD_facturap();

    private ArrayList<String> facts = new ArrayList<String>();

    private String felcorel,corel;
    private boolean demomode,multiflag;
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

        demomode=true;

        if (demomode) {

            fel.llave_cert ="E5DC9FFBA5F3653E27DF2FC1DCAC824D";
            fel.llave_firma ="b21b063dec8367a4d15f4fa6dc0975bc";
            fel.fel_codigo ="0";
            fel.fel_alias="DEMO_FEL";
            fel.fel_nit="1000000000K";
            fel.fel_correo="";

        } else {

            fel.llave_cert ="7493B422E3CE97FFAB537CD6291787ED";
            fel.llave_firma ="5d1d699b6a2bef08d9960cbf7d265f41";
            fel.fel_codigo="PEXPRESS";
            fel.fel_alias="COMERCIALIZADORA EXPRESS DE ORIENTE, SOCIEDAD ANONIMA";
            fel.fel_nit="96049340";

        }


        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturafObj=new clsD_facturafObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);
        prod=new clsP_productoObj(this,Con,db);

        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        buildList();

        ffail=0;fidx=0;

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

        //buildFactXML();
        procesafactura();
    }

    @Override
    public void felCallBack()  {
        if (multiflag) {

            //if (!fel.errorflag) marcaFactura();

            fidx++;
            //if (fidx<ftot-1) {
            if (fidx<10) {
                if (fel.errorflag) ffail++;
                procesafactura();
            } else {
                toast("Completo");
            }

        } else {
            //pbar.setVisibility(View.INVISIBLE);

            if (!fel.errorflag) marcaFactura();

            if (!fel.errorflag) {
                gl.feluuid=fel.fact_uuid;
                finish();
            } else {
                gl.feluuid="";

                Handler mtimer = new Handler();
                Runnable mrunner=new Runnable() {
                    @Override
                    public void run() {
                        msgexit("Ocurrio error en FEL :\n\n"+ fel.error);
                    }
                };
                mtimer.postDelayed(mrunner,500);
            }
        }
    }

    private void buildFactXML() {

        corel=facts.get(fidx);

        try {
            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            D_facturafObj.fill("WHERE Corel='"+corel+"'");
            factf=D_facturafObj.first();

            if (demomode) {
                fel.fel_ident="abc123";
            } else {
                fel.fel_ident=fact.serie+fact.corelativo;
            }

            fel.iniciar(fact.fecha);
            fel.emisor("GEN",fel.fel_codigo,"",fel.fel_nit,fel.fel_alias);
            fel.emisorDireccion("Direccion","GUATEMALA","GUATEMALA","GT");
            fel.receptor(factf.nit,factf.nombre,factf.direccion);

            D_facturadObj.fill("WHERE Corel='"+corel+"'");

            for (int i = 0; i <D_facturadObj.count; i++) {
                factd=D_facturadObj.items.get(i);
                fel.detalle(prodName(factd.producto),factd.cant,"UNI",
                        factd.precio,factd.total,factd.desmon);
             }

            fel.completar(fact.serie,fact.corelativo);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        /*

        try {
            fel.iniciar(2005041102);
            fel.emisor("GEN","1","",fel_nit,"DEMO");
            fel.emisorDireccion("Direccion","GUATEMALA","GUATEMALA","GT");
            fel.receptor("CF","Consumidor Final","Ciudad");

            fel.detalle("Producto 1",1,"UNI",10,10,0);
            //fact.detalle("Producto 2",2,"UNI",15,30,0);

            fel.completar("ZR37-46");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

         */
    }

    private void marcaFactura() {

        try {
            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            fact.serie=fel.fact_serie;
            fact.corelativo=fel.fact_numero;
            fact.feelserie=fel.fact_serie;
            fact.feelnumero=""+fel.fact_numero;
            fact.feeluuid=fel.fact_uuid;

            D_facturaObj.update(fact);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux

    private void buildList() {
        String cor;
        ftot=0;

        try {
            D_facturaObj.fill("WHERE (FEELUUID=' ') AND (ANULADO='N')");

            facts.clear();
            for (int i = 0; i <D_facturaObj.count; i++) {
                cor=D_facturaObj.items.get(i).corel;
                if (felcorel.isEmpty()) {
                    facts.add(cor);
                } else {
                    if (cor.equalsIgnoreCase(felcorel)) {
                        facts.add(cor);
                    }
                }
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

    private String prodName(int cod_prod) {
        try {
            prod.fill("WHERE codigo_producto="+cod_prod);
            return prod.first().desclarga;
        } catch (Exception e) {
            return ""+cod_prod;
        }
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
            D_facturafObj.reconnect(Con,db);
            D_facturapObj.reconnect(Con,db);
            prod.reconnect(Con,db);
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
