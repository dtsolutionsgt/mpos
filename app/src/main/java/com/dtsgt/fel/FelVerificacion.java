package com.dtsgt.fel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsD_fel_errorObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FelVerificacion extends PBase {

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;
     private XMLObject xobj;

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
    private ArrayList<String> factlist = new ArrayList<String>();
    private ArrayList<String> rutas= new ArrayList<String>();

    private String felcorel,corel,ffcorel,scorel,CSQL,endstr,idfact;
    private boolean ddemomode,multiflag,factsend,contmode;
    private int ftot,ffail,fidx,cliid,felnivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fel_verificacion);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.msgHeader);lbl1.setText("");
        lbl2 = (TextView) findViewById(R.id.lblWS);lbl2.setText("");
        lbl3 = (TextView) findViewById(R.id.textView152);lbl3.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        felcorel=gl.felcorel;ffcorel=felcorel;
        multiflag=felcorel.isEmpty();
        gl.feluuid="";

        getURL();
        fel=new clsFELInFile(this,this,gl.timeout);

        clsP_sucursalObj sucursal=new clsP_sucursalObj(this,Con,db);
        sucursal.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
        clsClasses.clsP_sucursal suc=sucursal.first();

        fel.fel_llave_certificacion =suc.fel_llave_certificacion; // fel_llavews ="E5DC9FFBA5F3653E27DF2FC1DCAC824D"
        fel.fel_llave_firma=suc.fel_llave_firma; // fel_token ="5b174fb0e23645b65ef88277d654603d"
        fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;  //  1
        fel.fel_usuario_certificacion=suc.fel_usuario_certificacion; // COMERGUA

        //fel.fel_usuario_firma=suc.fel_usuario_firma; //JP2020071
        fel.fel_usuario_firma=suc.texto;

        fel.codigo_postal =suc.codigo_postal;
        fel.fel_nit=suc.nit; // NIT  96038888
        fel.fel_correo=suc.correo;  //

        //fel.fel_nombre_comercial = gl.tiendanom;  //JP2020071
        fel.fel_nombre_comercial = suc.nombre;

        fel.fraseIVA=suc.codigo_escenario_iva;
        fel.fraseISR=suc.codigo_escenario_isr;
        fel.fel_afiliacion_iva=suc.fel_afiliacion_iva;

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturafObj=new clsD_facturafObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);
        prod=new clsP_productoObj(this,Con,db);

        app.parametrosExtra();
        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        buildList();

        ffail=0;fidx=0;

        if (facts.size()>0) {
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    contingencia();
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
                contingenciaFactura();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void contingenciaFactura() {
        buildFactXML();
        fel.certificacion();
    }

    private void contingencia() {

        lbl1.setText("Verificando factura  . . ."); lbl3.setText("");ffail=0;
        contmode=true;
        procesafactura();
    }

    @Override
    public void felCallBack()  {
        boolean skipflag=false;

        try {

            if (fel.errorcon) {
                msgexit(fel.error);return;
            }
            if (fel.errorflag && !fel.duplicado) {
                ffail++;
                guardaError();
                marcaFacturaContingencia();
                skipflag=true;
            } if (fel.errorflag && fel.duplicado) {

                //Hacer algo con la factura y su identificador interno...
                //#EJC20200710: Incremente en 1 nuestro correlativo interno para alcanzar el de infile.

                clsClasses.clsP_corel citem;
                int corcont;
                clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

                if (fel.idcontingencia==0){
                    P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)");
                }else{
                    P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
                }

                citem=P_corelObj.first();
                if (citem.corelult==0) {
                    corcont=citem.corelini;
                } else {
                    corcont=citem.corelult+1;
                }

                citem.corelult=corcont;
                P_corelObj.update(citem);

                if (fel.idcontingencia ==0){
                    fact.corelativo = corcont;
                    D_facturaObj.update(fact);
                } else {
                    fact.feelcontingencia = ""+corcont;
                    D_facturaObj.update(fact);
                }

                toastlong("Correlativo interno incrementado a: " + corcont + " por previo envío, reintente por favor");
            }  else {
                //JP20200918 - evita procesar marcaFactura en caso que (fel.errorflag && !fel.duplicado)
                if (!skipflag)  marcaFactura();
            }

            callBackMulti();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callBackMulti() {

        fidx++;
        if (fidx<ftot) {
            if (fel.errorflag) ffail++;
            procesafactura();
        } else {
            if (ffail==0) {
                endstr="Verificados : "+ftot;
                if (gl.peEnvio) {
                    //envioFacturas();
                } else {
                    toast(endstr); finish();
                }
            } else {
                endstr="Verificados : "+(ftot-ffail);
                //if (gl.peEnvio) envioFacturas(); else msgexit(endstr);
            }
        }
    }

    private void buildFactXML() {

        String dir,muni,dep,iddep,idmuni;
        int idcont;

        corel=facts.get(fidx);

        try {

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            fel.idcontingencia=0;
            try {
                idcont=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (idcont>0) fel.idcontingencia=idcont;
            } catch (Exception e) {
            }

            cliid=fact.cliente;

            D_facturafObj.fill("WHERE Corel='"+corel+"'");
            factf=D_facturafObj.first();

            //#EJC20200706: Colocar If aquí para validar si el documento fue en contingencia.
            if (fel.idcontingencia==0) {
                fel.mpos_identificador_fact =fact.serie+fact.corelativo;
            } else {
                fel.mpos_identificador_fact =""+fel.idcontingencia;
            }

            fel.iniciar(fact.fecha);
            fel.emisor(fel.fel_afiliacion_iva,fel.fel_codigo_establecimiento,fel.fel_correo,
                    fel.fel_nit,fel.fel_nombre_comercial, fel.fel_usuario_firma);

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);

            fel.codigo_postal=P_sucursalObj.first().codigo_postal;
            fel.correo_sucursal=P_sucursalObj.first().correo;
            dir=P_sucursalObj.first().direccion;

            idmuni=P_sucursalObj.first().codigo_municipio;

            clsP_municipioObj P_municipioObj=new clsP_municipioObj(this,Con,db);
            P_municipioObj.fill("WHERE CODIGO='"+idmuni+"'");

            if (P_municipioObj.count>0) {

                muni=P_municipioObj.first().nombre;
                iddep=P_municipioObj.first().codigo_departamento;

                clsP_departamentoObj P_departamentoObj=new clsP_departamentoObj(this,Con,db);
                P_departamentoObj.fill("WHERE CODIGO='"+iddep+"'");

                if (P_departamentoObj.count>0) dep=P_departamentoObj.first().nombre;else dep=" ";

            } else {
                muni=" ";dep=" ";
            }

            fel.emisorDireccion(dir,fel.codigo_postal,muni,dep,gl.codigo_pais);

            //#EJC20200527: Quitar "-" del nit
            factf.nit =factf.nit.replace("-","");
            factf.nit =factf.nit.replace(".","");

            fel.receptor(factf.nit, factf.nombre, factf.direccion, factf.correo, fel.codigo_postal,muni,dep,gl.codigo_pais);

            D_facturadObj.fill("WHERE Corel='"+corel+"'");

            for (int i = 0; i <D_facturadObj.count; i++) {
                factd=D_facturadObj.items.get(i);
                fel.detalle(prodName(factd.producto),factd.cant,"UNI",
                        factd.precio,factd.total,factd.desmon);
            }

            fel.completar(fact.serie,fact.corelativo);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void marcaFactura() {

        try {

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            //fact.serie=fel.fact_serie;
            //fact.corelativo=fel.fact_numero;
            fact.feelserie=fel.fact_serie;
            fact.feelnumero=""+fel.fact_numero;
            fact.feeluuid=fel.fact_uuid;
            fact.feelfechaprocesado=du.getActDateTime();

            D_facturaObj.update(fact);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void marcaFacturaContingencia() {

        clsClasses.clsP_corel citem;
        int corcont;

        try {

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            //JP20200918 - si ya esta marcada como contingencia, no lo marca de nuevo
            String fcont=fact.feelcontingencia;if (fcont.equalsIgnoreCase(" ")) fcont="";

            if (!fcont.isEmpty()) {
                return ;
            }

            db.beginTransaction();

            clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);
            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
            citem=P_corelObj.first();
            if (citem.corelult==0) {
                corcont=citem.corelini;
            } else {
                corcont=citem.corelult+1;
            }


            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            fact.feelcontingencia=""+corcont;

            D_facturaObj.update(fact);

            citem.corelult=corcont;
            P_corelObj.update(citem);

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void buildList() {
        String cor;
        ftot=0;

        try {
            D_facturaObj.fill("WHERE (FEELUUID=' ') AND (ANULADO=0)");

            facts.clear();
            for (int i = 0; i <D_facturaObj.count; i++) {
                cor=D_facturaObj.items.get(i).corel;ffcorel=cor;
                if (felcorel.isEmpty()) {
                    facts.add(cor);
                } else {
                    if (cor.equalsIgnoreCase(felcorel)) {
                        facts.add(cor);
                    }
                }
            }

            ftot=facts.size();

            rutas.clear();

            if (gl.peInvCompart) {
                clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
                P_rutaObj.fill("WHERE (SUCURSAL="+gl.tienda+") AND (CODIGO_RUTA<>"+gl.codigo_ruta+")");
                for (int i = 0; i <P_rutaObj.count; i++) rutas.add(""+P_rutaObj.items.get(i).codigo_ruta);
            }
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
                        lbl1.setText("Certificando factura "+(fidx+1)+" / "+ftot);lbl3.setText("");
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

    private void getURL() {

        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 6000;

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {

                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();

                if(line.isEmpty()) {
                    gl.timeout = 6000;
                }  else {
                    gl.timeout = Integer.valueOf(line);
                }

                myReader.close();
            }

        } catch (Exception e) {}

        if (gl.wsurl.isEmpty()) lbl2.setText("Falta archivo con URL");
    }

    private void guardaError() {
        clsD_fel_errorObj D_fel_errorObj=new clsD_fel_errorObj(this,Con,db);
        clsClasses.clsD_fel_error item=clsCls.new clsD_fel_error();

        String err=fel.error;
        String cor=ffcorel;
        int nivel=fel.errlevel; // nivel=1 - firma  , 2 - certificacion

        try {
            int iditem=D_fel_errorObj.newID("SELECT MAX(Item) FROM D_fel_error");

            item.empresa=gl.emp;
            item.corel=cor;
            item.item=iditem;
            item.fecha=du.getActDateTime();
            item.nivel=nivel;
            item.error=err;
            item.enviado=0;

            D_fel_errorObj.add(item);
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Dialogs

    public void msgexit(String msg) {

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                showMsgExit("Ocurrio error en FEL :\n\n"+ fel.error);
            }
        };
        mtimer.postDelayed(mrunner,500);

    }

    public void showMsgExit(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
            msgbox2(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //endregion
}