package com.dtsgt.fel;

import android.app.AlertDialog;
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
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_factura_felObj;
import com.dtsgt.classes.clsD_facturacObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturamuniObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsD_facturaprObj;
import com.dtsgt.classes.clsD_facturarObj;
import com.dtsgt.classes.clsD_fel_bitacoraObj;
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
import com.dtsgt.mpos.WSEnv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FELFactura extends PBase {

    // gl.peFEL  "SIN FEL", "INFILE"

    private TextView lbl1,lbl2,lbl3,lblHalt;
    private ProgressBar pbar;

    private clsFELInFile fel;
    private WebServiceHandler ws;
    private XMLObject xobj;

    private clsD_facturaObj  D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_facturapObj D_facturapObj;
    private clsD_facturacObj D_facturacObj;
    private clsD_facturarObj D_facturarObj;
    private clsD_facturaprObj D_facturaprObj;
    private clsD_factura_felObj D_factura_felObj;
    private clsP_productoObj prod;
    private clsD_fel_bitacoraObj D_fel_bitacoraObj;

    private clsClasses.clsD_factura fact=clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd=clsCls.new clsD_facturad();
    private clsClasses.clsD_facturaf factf=clsCls.new clsD_facturaf();
    private clsClasses.clsD_facturap factp=clsCls.new clsD_facturap();
    private clsClasses.clsD_fel_bitacora fbita=clsCls.new clsD_fel_bitacora();

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    private Date systemTime,stime;

    private ArrayList<String> facts = new ArrayList<String>();
    private ArrayList<String> factlist = new ArrayList<String>();
    private ArrayList<String> rutas= new ArrayList<String>();

    private String felcorel,corel,ffcorel,scorel,CSQL,endstr,idfact,prod_BS;
    private boolean ddemomode,multiflag,factsend,contmode;
    private int ftot,ffail,fidx,cliid,felnivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_fel_factura);

            super.InitBase();

            lbl1 = findViewById(R.id.msgHeader);lbl1.setText("");
            lbl2 = findViewById(R.id.lblWS);lbl2.setText("");
            lbl3 = findViewById(R.id.textView152);lbl3.setText("");
            lblHalt = findViewById(R.id.textView218);lblHalt.setVisibility(View.VISIBLE);
            pbar = findViewById(R.id.progressBar);
            pbar.setVisibility(View.INVISIBLE);

            felcorel=gl.felcorel;ffcorel=felcorel;
            multiflag=felcorel.isEmpty();
            gl.feluuid="";

            getURL();

            fel=new clsFELInFile(this,this,gl.timeout);
            fel.halt=false;
            fel.autocancel=true;
            fel.owner=FELFactura.this;

            ws = new WebServiceHandler(FELFactura.this, gl.wsurl, gl.timeout);
            xobj = new XMLObject(ws);

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
            fel.fel_nit=suc.nit.toUpperCase(); // NIT  96038888
            fel.fel_correo=suc.correo;  //

            //fel.fel_nombre_comercial = gl.tiendanom;  //JP2020071
            fel.fel_nombre_comercial = suc.nombre;

            fel.fraseIVA=suc.codigo_escenario_iva;
            fel.fraseISR=suc.codigo_escenario_isr;

            if(fel.fraseIVA==3) fel.fraseIVA=1;

            fel.fel_afiliacion_iva=suc.fel_afiliacion_iva;
            fel.fel_tipo_documento=app.felTipoDocumento(fel.fel_afiliacion_iva);
            fel.iduniflag=false;fel.halt=false;

            D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturadObj=new clsD_facturadObj(this,Con,db);
            D_facturafObj=new clsD_facturafObj(this,Con,db);
            D_facturapObj=new clsD_facturapObj(this,Con,db);
            D_facturarObj=new clsD_facturarObj(this,Con,db);
            D_facturacObj=new clsD_facturacObj(this,Con,db);
            D_facturaprObj=new clsD_facturaprObj(this,Con,db);
            D_factura_felObj=new clsD_factura_felObj(this,Con,db);

            D_fel_bitacoraObj=new clsD_fel_bitacoraObj(this,Con,db);

            prod=new clsP_productoObj(this,Con,db);

            app.parametrosExtra();
            lbl2.setText("CERTIFICADOR: "+gl.peFEL);
            pbar.setVisibility(View.VISIBLE);

            facts.add(felcorel);

            ffail=0;fidx=0;

            try {

                if (facts.size()>0) {
                    Handler mtimer = new Handler();
                    Runnable mrunner= () -> {
                        if (multiflag) {
                            multipleFacturas();
                        } else {
                            Date currentTime = Calendar.getInstance().getTime();
                            procesaFirma();
                        }
                    };
                    mtimer.postDelayed(mrunner,100);
                } else {
                    finish();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doHalt(View viev) {
        fel.halt=true;
        lblHalt.setVisibility(View.INVISIBLE);

        Handler mtimer = new Handler();
        Runnable mrunner= () -> finish();
        mtimer.postDelayed(mrunner,200);

    }

    //endregion

    //region Main

    private void procesafactura() {

        updateLabel();

        try {

            Handler mtimer = new Handler();
            Runnable mrunner = () -> certificaFactura();
            mtimer.postDelayed(mrunner, 200);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void certificaFactura() {
        try {
            lbl1.setText("Construyendo XML...");
            buildFactXML();
            lbl1.setText("Certificando Factura...");
            fel.certificacion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesaFirma() {

        lbl1.setText("Procesando firma . . .");
        lbl3.setText("");

        try {

            fbita.empresa=gl.emp;
            fbita.codigo_sucursal=gl.tienda;
            fbita.codigo_ruta=gl.codigo_ruta;
            fbita.corel=felcorel;
            fbita.fecha=du.getActDateTime();
            fbita.tiempo_firma=-1;
            fbita.tiempo_cert=-1;
            fbita.estado=-1;
            fbita.codigo_vendedor=gl.codigo_vendedor;
            fbita.statcom=0;

            D_fel_bitacoraObj.add(fbita);

            systemTime=Calendar.getInstance().getTime();
            fel.ftime1=systemTime.getTime();

        } catch (Exception e) {
            String ee=e.getMessage();
        }

        contmode=false;

        fel.errcert=false;fel.errfirma=false;

        try {

            lbl1.setText("Construyendo XML...");
            buildFactXML();
            lbl1.setText("Certificando Factura...");

            fel.certificacion();

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void multipleFacturas() {
        try {
            lbl1.setText("Guardando factura  . . .");
            lbl3.setText("");
            ffail=0;
            contmode=true;
            procesafactura();
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    public void felCallBack()  {

        boolean skipflag=false;
        long corcont,mcont;

        int ii=0;
        long tdiff;
        double tsec;

        try {

            Date systemTime=Calendar.getInstance().getTime();
            fel.ftime3=systemTime.getTime();

            if (fel.ftime1>0) {

                if (fel.ftime2>0) {

                    tdiff=fel.ftime2-fel.ftime1;
                    tsec=((double) tdiff)/1000;
                    fbita.tiempo_firma=tsec;

                    if (fel.ftime3>0) {
                        tdiff=fel.ftime3-fel.ftime2;
                        tsec=((double) tdiff)/1000;
                        fbita.tiempo_cert=tsec;
                    }

                    fbita.estado=1;

                    if (fel.halt) fbita.estado=0;

                    D_fel_bitacoraObj.update(fbita);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            if (multiflag) {  // Verificacion

                if (fel.errorcon) {
                    msgexit("Factura: "+fel.mpos_identificador_fact+"\n"+fel.error);return;
                }
                if (fel.errorflag && !fel.duplicado) {
                    marcaFactura();
                    skipflag=true;
                } if (fel.errorflag && fel.duplicado) {
                    clsClasses.clsP_corel citem;
                    clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

                    if (fel.idcontingencia==0){
                        P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)");
                    }else{
                        P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
                    }

                    mcont=maxconting();
                    citem=P_corelObj.first();
                    if (citem.corelult==0) corcont=citem.corelini; else corcont=citem.corelult+1;
                    if (mcont>corcont) corcont=mcont;

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

            } else {  // Certificacion - una factura despues de venta

                if (fel.duplicado) {
                    //toastcentlong("Documento enviado previamente." + fel.mpos_identificador_fact);
                    marcaFactura();
                    msgbox("Documento enviado previamente.");
                    callBackSingle();
                    finish();
                    return;
                }

                if (!fel.errorflag && !fel.duplicado) {
                    marcaFactura();
                    callBackSingle();
                } else {

                    if (fel.iduniflag) {
                        marcaFactura();
                        callBackSingle();
                        return;
                    }

                    if (!fel.duplicado){
                        marcaFacturaContingencia();
                        guardaError();
                        callBackSingle();
                    } else {
                        clsClasses.clsP_corel citem;
                        clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

                        if (fel.idcontingencia ==0){
                            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)");
                        }else{
                            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
                        }

                        mcont=maxconting();
                        citem=P_corelObj.first();
                        corcont=citem.corelult+1;
                        if (mcont>corcont) corcont=mcont;

                        citem.corelult=corcont;
                        P_corelObj.update(citem);

                        if (fel.idcontingencia==0){
                            fact.corelativo = corcont;
                            D_facturaObj.update(fact);
                        }else{
                            fact.feelcontingencia = ""+corcont;
                            D_facturaObj.update(fact);
                        }

                        toastlong("Correlativo interno incrementado a: " + corcont + " por previo envío, reintente por favor");
                        callBackSingle();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void felProgress(String msg) {
        Handler mtimer = new Handler();
        Runnable mrunner = () -> lbl1.setText(msg);
        mtimer.postDelayed(mrunner,50);
    }

    private void callBackMulti() {

        fidx++;
        if (fidx<ftot) {
            if (fel.errorflag) ffail++;
            procesafactura();
        } else {
            if (ffail==0) {
                endstr="Certificados : "+ftot;
                if (gl.peEnvio) {
                    envioFacturas();
                } else {
                    toast(endstr); finish();
                }
            } else {
                endstr="Certificados : "+(ftot-ffail)+"\nSin certificacion : "+ffail;
                if (gl.peEnvio) envioFacturas(); else msgexit(endstr);
            }
        }
    }

    private void callBackSingle() {

        try {

            if (!fel.errorflag) {
                gl.feluuid=fel.fact_uuid;
                toastlong("Envio completo "+ gl.feluuid);
                if (gl.peEnvio) {
                    if (!gl.feluuid.isEmpty()) {
                        if (gl.feluuid.length()>10) {
                            if (gl.feluuid.indexOf("-")>1) {
                                envioFactura(felcorel);
                            }
                        }
                    }
                } else {
                    finish();
                }

            } else {
                gl.feluuid="";
                gl.FELmsg="Ocurrió error en FEL :\n\n"+"Factura: "+fel.mpos_identificador_fact+"\n"+ fel.error;
                startActivity(new Intent(this,FELmsgbox.class));
                toastlong(gl.FELmsg);
                finish();
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildFactXML() {

        String dir,muni,dep,iddep,idmuni,lcombo;
        int idcont;
        double ldesc,propina;

        corel=facts.get(fidx);

        try {

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            fel.factura_credito=false;
            try {
                D_facturapObj.fill("WHERE Corel='"+corel+"'");
                if (D_facturapObj.count>0) {
                    if (D_facturapObj.first().tipo.equalsIgnoreCase("C")) {
                        fel.factura_credito=true;
                        fel.factura_abono_venc=D_facturapObj.first().desc3+"";
                        if (fel.factura_abono_venc.isEmpty()) fel.factura_credito=false;
                    }
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                fel.factura_credito=false;
            }

            fel.idcontingencia=0;

            try {
                idcont=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (idcont>0) fel.idcontingencia=idcont;
            } catch (Exception e) {
            }

            cliid=fact.cliente;

            D_facturafObj.fill("WHERE Corel='"+corel+"'");
            factf=D_facturafObj.first();

            //#EJC20200921: Enviar siempre el mismo identificador de factura.
            fel.mpos_identificador_fact =fact.serie+fact.corelativo;

            fel.fechaf_y=du.getyear(fact.fecha);
            fel.fechaf_m=du.getmonth(fact.fecha);
            fel.fechaf_d=du.getday(fact.fecha);
            fel.tipo_nit=fact.ayudante;

            fel.iniciar(fact.fecha,"");

            fel.emisor(fel.fel_afiliacion_iva,
                       fel.fel_codigo_establecimiento,
                       fel.fel_correo,
                       fel.fel_nit,
                       fel.fel_nombre_comercial,
                       fel.fel_usuario_firma);

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

            clsD_facturamuniObj D_facturamuniObj=new clsD_facturamuniObj(this,Con,db);
            D_facturamuniObj.fill("WHERE Corel='"+corel+"'");
            if (D_facturamuniObj.count>0) {
                idmuni=D_facturamuniObj.first().idmuni;
                iddep=D_facturamuniObj.first().iddepto;

                P_municipioObj.fill("WHERE CODIGO='"+idmuni+"'");
                muni=P_municipioObj.first().nombre;

                clsP_departamentoObj P_departamentoObj=new clsP_departamentoObj(this,Con,db);
                P_departamentoObj.fill("WHERE CODIGO='"+iddep+"'");
                if (P_departamentoObj.count>0) dep=P_departamentoObj.first().nombre;else dep=" ";
            }

            fel.emisorDireccion(dir,fel.codigo_postal,muni,dep,gl.codigo_pais);

            //#EJC20200527: Quitar "-" del nit
            factf.nit=factf.nit.trim();
            factf.nit=factf.nit.replace("-","");
            factf.nit=factf.nit.replace(".","");
            factf.nit=factf.nit.replace(" ","");
            factf.nit=factf.nit.toUpperCase();

            //#EJC20211222: Chapusería de navidad, Jaros por favor corregir esto.
            if(fel.fraseIVA==3) {
                fel.fraseIVA=1;
            }

            fel.receptor(factf.nit,
                         factf.nombre,
                         factf.direccion,
                         factf.correo,
                         fel.codigo_postal,
                         muni,
                         dep,
                         gl.codigo_pais,
                         fel.tipo_nit);

            propina=0;
            D_facturaprObj.fill("WHERE Corel='"+corel+"'");
            if (D_facturaprObj.count>0) {
                propina=D_facturaprObj.first().propina;
            }

            D_facturadObj.fill("WHERE Corel='"+corel+"'");

            for (int i = 0; i <D_facturadObj.count; i++) {

                factd=D_facturadObj.items.get(i);
                if (gl.peComboDet) lcombo=listaCombo(factd.corel,factd.val2); else lcombo="";

                factd.total=mu.round2(factd.total);
                ldesc=factd.cant*factd.precio-factd.total;
                factd.desmon=ldesc;

                fel.detalle(prodName(factd.producto),
                            factd.cant,
                            "UNI",
                            factd.precio,
                            mu.round2(factd.total),
                            factd.desmon,
                            lcombo,
                            prod_BS);
            }

            //if (propina>0) {
                fel.detalle_propina(propina);
            //}

            fel.completar(fact.serie,fact.corelativo);

            String sxml=fel.xml;
            sxml=sxml+"";

            guardarXMLComoArchivo(sxml,corel);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void guardarXMLComoArchivo(String sxml, String corel) {
        // Formato de la fecha para el nombre del archivo
        String fileName = "dte_fact.txt";

        // Directorio en almacenamiento externo
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);

        // Escritura del contenido XML al archivo
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(sxml.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
            fact.statcom="N";

            D_facturaObj.update(fact);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void marcaFacturaContingencia() {

        clsClasses.clsP_corel citem;
        long corcont;

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
            fact.statcom="N";

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

    //region Envio una factura

    private void envioFactura(String cor) {

        scorel=cor;

        Handler mtimer = new Handler();
        Runnable mrunner = () -> {
            ws.callback = 1;
            ws.execute();
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void processFactura() {

        clsClasses.clsD_facturad item;
        String tipo_producto,vsql;
        int contingencia,uruta;

        try {

            corel=scorel;

            AppMethods f = new AppMethods(this,null,Con,db);
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);

            D_facturaObj.fill("WHERE COREL='"+corel+"'");
            D_facturadObj.fill("WHERE (COREL='"+corel+"') AND (PRODUCTO<>0)");
            D_facturapObj.fill("WHERE COREL='"+corel+"'");
            D_facturarObj.fill("WHERE COREL='"+corel+"'");
            D_facturacObj.fill("WHERE COREL='"+corel+"'");
            D_facturaprObj.fill("WHERE COREL='"+corel+"'");
            D_factura_felObj.fill("WHERE COREL='"+corel+"'");

            idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;
            cliid=D_facturaObj.first().cliente;
            try {
                contingencia=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (contingencia<1) contingencia=0;
            } catch (Exception e) {
                contingencia=0;
            }

            CSQL="DELETE FROM D_FACTURA WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAC WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAPR WHERE COREL='"+corel+"';";
            CSQL=CSQL+addFactheader(D_facturaObj.first())+ ";";

            String UpdateToStock = "";

            for (int i = 0; i <D_facturadObj.count; i++) {

                item=D_facturadObj.items.get(i);

                CSQL=CSQL+D_facturadObj.addItemSql(D_facturadObj.items.get(i)) + ";";

                tipo_producto = f.prodTipo(D_facturadObj.items.get(i).producto);

                if (tipo_producto.equalsIgnoreCase("P")) {

                    UpdateToStock =D_facturadObj.addItemUpdateStockSql(D_facturadObj.items.get(i), gl.tienda) + ";";
                    if (!UpdateToStock.isEmpty()) CSQL=CSQL+ UpdateToStock;

                    if (gl.peInvCompart){
                        for (int r = 0; r <rutas.size(); r++) {
                            uruta=Integer.parseInt(rutas.get(r));

                            vsql=addUpdateItem(uruta,item.producto,-item.cant,item.umstock);
                            CSQL=CSQL+ vsql;
                        }
                    }
                }
            }

            for (int i = 0; i < D_facturapObj.count; i++) {
                CSQL=CSQL+D_facturapObj.addItemSql(D_facturapObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_facturarObj.count; i++) {
                CSQL=CSQL+D_factRaddItemSql(D_facturarObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_facturacObj.count; i++) {
                CSQL=CSQL+D_factCaddItemSql(D_facturacObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_facturaprObj.count; i++) {
                CSQL=CSQL+addPropinaItem(D_facturaprObj.items.get(i)) + ";";
            }


            CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+D_facturaObj.first().corelativo+"  " +
                    "WHERE (SERIE='"+D_facturaObj.first().serie+"') AND (ACTIVA=1) AND (RESGUARDO=0) AND (RUTA=" + gl.codigo_ruta + ");";

            if (contingencia>0) {
                CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+contingencia+"  " +
                        "WHERE (ACTIVA=1) AND (RESGUARDO=1) AND (RUTA=" + gl.codigo_ruta + ");";
            }

            P_clienteObj.fill("WHERE CODIGO_CLIENTE="+cliid);

            ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+cliid+")";
            CSQL = CSQL + ss + ";";
            ss=P_clienteObj.addItemSql(P_clienteObj.first(),gl.emp);
            CSQL = CSQL + ss + ";";

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public String addFactheader(clsClasses.clsD_factura item) {

        String fst;

        //#EJC20200702:Correccion formato de fechas
        String fs=""+du.univfecha(item.fecha);
        String fse=""+du.univfechahora(item.fecha);
        if (item.feelfechaprocesado>0) fst=du.univfecha(item.feelfechaprocesado);else fst="20000101 00:00:00";

        ins.init("D_factura");
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",fs);
        ins.add("RUTA",item.ruta);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CLIENTE",item.cliente);
        ins.add("KILOMETRAJE",item.kilometraje);
        ins.add("FECHAENTR",fse);
        ins.add("FACTLINK",item.factlink);
        ins.add("TOTAL",item.total);
        ins.add("DESMONTO",item.desmonto);
        ins.add("IMPMONTO",item.impmonto);
        ins.add("PESO",item.peso);
        ins.add("BANDERA",item.bandera);
        ins.add("STATCOM",item.statcom);
        ins.add("CALCOBJ",item.calcobj);
        ins.add("SERIE",item.serie);
        ins.add("CORELATIVO",item.corelativo);
        ins.add("IMPRES",item.impres);
        ins.add("ADD1",item.add1);
        ins.add("ADD2",item.add2);
        ins.add("ADD3",item.add3);
        ins.add("DEPOS",item.depos);
        ins.add("PEDCOREL",item.pedcorel);
        ins.add("REFERENCIA",item.referencia);
        ins.add("ASIGNACION",item.asignacion);
        ins.add("SUPERVISOR",item.supervisor);
        ins.add("AYUDANTE",item.ayudante);
        ins.add("VEHICULO",item.vehiculo);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("RAZON_ANULACION",item.razon_anulacion);
        ins.add("FEELSERIE",item.feelserie);
        ins.add("FEELNUMERO",item.feelnumero);
        ins.add("FEELUUID",item.feeluuid);
        ins.add("FEELFECHAPROCESADO",fst);
        ins.add("FEELCONTINGENCIA",item.feelcontingencia);

        return ins.sql();

    }

    public String addPropinaItem(clsClasses.clsD_facturapr item) {

        String fs=""+du.univfecha(item.fecha);

        ins.init("D_facturapr");
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",fs);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("PROPINA",item.propina);
        ins.add("PROPPERC",item.propperc);
        ins.add("PROPEXTRA",item.propextra);
        return ins.sql();

    }

    public String D_factCaddItemSql(clsClasses.clsD_facturac item) {

        ins.init("D_facturac");
        ins.add("EMPRESA",gl.emp);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);
        ins.add("NOMBRE",item.nombre);
        return ins.sql();

    }

    public String D_factRaddItemSql(clsClasses.clsD_facturar item) {

        ins.init("D_facturar");
        ins.add("EMPRESA",gl.emp);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);
        return ins.sql();

    }

    private void statusFactura() {

        try {

            String rs =(String) xobj.getSingle("CommitResult",String.class);
            if (!rs.equalsIgnoreCase("#")) {
                factsend=false;return;
            } else {
                factsend=true;
            }

            try {
                sql="UPDATE D_Factura SET STATCOM='S' WHERE COREL='"+corel+"'";
                db.execSQL(sql);
            } catch (SQLException e) {
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processComplete() {

        Date currentTime = Calendar.getInstance().getTime();
        Log.i("FEL_FINISH: ", currentTime.toString());

        if (ws.errorflag) {
            toastlong("Error de envio");
        } else {
            toastlong("Envio completo "+ gl.feluuid);
        }
        finish();
    }

    //endregion

    //region Envio facturas multiplo

    private void envioFacturas() {

        prepareSend();

        if (ftot==0) {
            if (ffail==0) {
                toast(endstr);finish();
            } else msgexit(endstr);
            return;
        }

        fidx=-1;
        envioMultiFactura();
    }

    private void envioMultiFactura() {

        updateLabelSend();

        Handler mtimer = new Handler();
        Runnable mrunner = () -> {
            gl.autocom = 1;
            startActivity(new Intent(FELFactura.this, WSEnv.class));
            finish();
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void processMultiFactura() {

        int contingencia;

        fidx++;
        corel=factlist.get(fidx);
        factsend=false;

        try {

            D_facturaObj.fill("WHERE COREL='"+corel+"'");
            D_facturadObj.fill("WHERE COREL='"+corel+"'");
            D_facturapObj.fill("WHERE COREL='"+corel+"'");
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);

            cliid=D_facturaObj.first().cliente;
            try {
                contingencia=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (contingencia<1) contingencia=0;
            } catch (Exception e) {
                contingencia=0;
            }

            idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;

            CSQL="DELETE FROM D_FACTURA WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+corel+"';";

            CSQL=CSQL+addFactheader(D_facturaObj.first())+ ";";

            for (int i = 0; i <D_facturadObj.count; i++) {
                CSQL=CSQL+D_facturadObj.addItemSql(D_facturadObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_facturapObj.count; i++) {
                CSQL=CSQL+D_facturapObj.addItemSql(D_facturapObj.items.get(i)) + ";";
            }

            CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+D_facturaObj.first().corelativo+"  " +
                    " WHERE (SERIE='"+D_facturaObj.first().serie+"') AND (ACTIVA=1) AND (RESGUARDO=0) AND (RUTA=" + gl.codigo_ruta + ")" +
                    " AND CORELULT < "+D_facturaObj.first().corelativo+" ;";

            if (contingencia>0) {
                CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+contingencia+"  " +
                        "WHERE (ACTIVA=1) AND (RESGUARDO=1) AND (RUTA=" + gl.codigo_ruta + ");";
            }

            P_clienteObj.fill("WHERE CODIGO_CLIENTE="+cliid);

            ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+cliid+")";
            CSQL = CSQL + ss + "\n";
            ss=P_clienteObj.addItemSql(P_clienteObj.first(),gl.emp);
            CSQL = CSQL + ss + "\n";

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void prepareSend() {

        try {

            D_facturaObj.fill("WHERE (STATCOM='N')  AND (FEELUUID<>' ') ");

            ftot=D_facturaObj.count;
            if (ftot>0) fidx=-1;else fidx=0;

            factlist.clear();
            for (int i = 0; i <ftot; i++) {
                factlist.add(D_facturaObj.items.get(i).corel);
            }

            lbl1.setText("Pendientes envio : "+ftot);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region WebService handler

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent, String Url, int TimeOut) {
            super(Parent, Url, TimeOut);
        }

        @Override
        public void wsExecute() {
            try {
                switch (ws.callback) {
                    case 1:
                        processFactura();
                        callMethod("Commit", "SQL", CSQL);
                        break;
                    case 2:
                        processMultiFactura();
                        callMethod("Commit", "SQL", CSQL);
                        break;
                }
            } catch (Exception e) {
                error = e.getMessage();errorflag=true;
            }
        }
    }

    @Override
    public void wsCallBack(Boolean throwing, String errmsg, int errlevel) {

        try {

            if (throwing) throw new Exception(errmsg);

            if (ws.errorflag) {
                processComplete();
                return;
            }

            switch (ws.callback) {
                case 1:
                    statusFactura();
                    processComplete();
                    break;
                case 2:
                    if (ftot>0) statusFactura();
                    if (fidx>=ftot-1) {
                        processComplete();
                    } else {
                        envioMultiFactura();
                    }
                    break;
            }

        } catch (Exception e) {
            msgbox2(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    //endregion

    //region Aux

    private void buildList() {

        String cor;
        ftot=0;

        try {

            sql = "SELECT * FROM d_factura WHERE anulado =0   " +
                    "AND feelfechaprocesado=0 AND feeluuid = ' ' ORDER BY fecha desc";
            D_facturaObj.fill(sql);

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
        Runnable runnable = () -> handler.post(() -> {
            lbl1.setText("Certificando factura "+(fidx+1)+" / "+ftot);lbl3.setText("");
        });
        new Thread(runnable).start();
    }

    private void updateLabelSend() {
        Handler handler = new Handler();
        Runnable runnable = () -> handler.post(() -> {
            lbl1.setText("Enviando factura "+(fidx+2)+" / "+ftot);lbl3.setText("");
        });
        new Thread(runnable).start();
    }

    private String prodName(int cod_prod) {
        try {
            prod.fill("WHERE codigo_producto="+cod_prod);
            prod_BS=prod.first().um_salida;
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

        String err=fel.responsecode +" "+fel.error;
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

    public String addUpdateItem(int idruta,int idproducto,double cant,String um) {
        clsClasses.clsP_stock_update item=clsCls.new clsP_stock_update();

        String fs=""+du.univfechalong(du.getActDateTime());

        ins.init("P_stock_update");

        //ins.add("CODIGO_STOCK",item.codigo_stock);
        ins.add("EMPRESA",gl.emp);
        ins.add("SUCURSAL",gl.tienda);
        ins.add("RUTA",idruta);
        ins.add("CODIGO_PRODUCTO",idproducto);
        ins.add("CANTIDAD",cant);
        ins.add("UNIDAD_MEDIDA",um);
        ins.add("REFERENCIA","");
        ins.add("FECHA_TRANSACCION",fs);
        //ins.add("FECHA_PROCESADO",item.fecha_procesado);
        ins.add("PROCESADO",0);

        return ins.sql();

    }

    private long maxconting() {
        try {
            D_facturaObj.fill("ORDER BY FEELCONTINGENCIA DESC");
            String ss=D_facturaObj.first().feelcontingencia;
            long val=Integer.parseInt(ss);
            return val+1;
        } catch (Exception e) {
            return 1;
        }
    }

    private String listaCombo(String ccorel,String idcombo) {
        clsD_facturacObj D_facturacObj=new clsD_facturacObj(this,Con,db);
        String lc,nombre;

        try {
            D_facturacObj.fill("WHERE (COREL='"+ccorel+"') AND (IDCombo="+idcombo+")");

            lc="";
            for (int i = 0; i <D_facturacObj.count; i++) {
                nombre = D_facturacObj.items.get(i).nombre;
                lc+="|@"+nombre;
            }

            return lc;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return "";
        }
    }

    public void closeForm() {
        toast("closeForm");
        try {
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void msgexit(String msg) {

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                showMsgExit("Ocurrio error en FEL :\n\n"+"Factura: "+fel.mpos_identificador_fact+"\n"+ fel.error);
            }
        };
        mtimer.postDelayed(mrunner,500);

    }

    public void showMsgExit(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(msg);
            dialog.setCancelable(false);
            dialog.setNeutralButton("OK", (dialog1, which) -> {
                gl.feluuid="";finish();
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
            D_fel_bitacoraObj.reconnect(Con,db);
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