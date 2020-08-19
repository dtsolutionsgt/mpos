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

public class FelFactura extends PBase {

    // gl.peFEL  "SIN FEL", "INFILE"

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;
    private WebServiceHandler ws;
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
        setContentView(R.layout.activity_fel_factura);

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

        ws = new WebServiceHandler(FelFactura.this, gl.wsurl, gl.timeout);
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
                    if (multiflag) {
                        contingencia();
                    } else {
                        Date currentTime = Calendar.getInstance().getTime();
                        Log.i("FEL_INI: ", currentTime.toString());
                        //#EJC20200622: Inicial FEL
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
                contingenciaFactura();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void contingenciaFactura() {
        buildFactXML();
        fel.certificacion();
    }

    private void certificacion() {

        lbl1.setText("Certificando factura . . .");lbl3.setText("");

        contmode=false;
        buildFactXML();
        fel.certificacion();
    }

    private void contingencia() {

        lbl1.setText("Certificando factura  . . ."); lbl3.setText("");ffail=0;
        contmode=true;
        procesafactura();
    }

    @Override
    public void felCallBack()  {

        try {

            if (multiflag) {

                if (fel.errorcon) {
                    msgexit(fel.error);return;
                }
                if (fel.errorflag && !fel.duplicado) {
                    ffail++;
                    guardaError();
                    marcaFacturaContingencia();
                } if (fel.errorflag && fel.duplicado) {

                    //Hacer algo con la factura y su identificador interno...
                    //#EJC20200710: Incremente en 1 nuestro correlativo interno para alcanzar el de infile.

                    clsClasses.clsP_corel citem;
                    int corcont;
                    clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

                    if (fel.idcontingencia ==0){
                        P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)");
                    }else{
                        P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
                    }

                    citem=P_corelObj.first();
                    corcont=citem.corelult+1;
                    citem.corelult=corcont;
                    P_corelObj.update(citem);

                    if (fel.idcontingencia ==0){
                        fact.corelativo = corcont;
                        D_facturaObj.update(fact);
                    }else{
                        fact.feelcontingencia = ""+corcont;
                        D_facturaObj.update(fact);
                    }

                    toastlong("Correlativo interno incrementado a: " + corcont + " por previo envío, reintente por favor");
                }  else {
                    marcaFactura();
                }

                callBackMulti();

            } else {

                if (fel.duplicado) toastcent("Documento enviado previamente." + fel.mpos_identificador_fact);

                if (!fel.errorflag && !fel.duplicado) {
                    marcaFactura();
                    callBackSingle();
                } else {
                    if (!fel.duplicado){
                        marcaFacturaContingencia();
                        guardaError();
                        callBackSingle();
                    }else{

                        //Hacer algo con la factura y su identificador interno...
                        //#EJC20200710: Incremente en 1 nuestro correlativo interno para alcanzar el de infile.

                        clsClasses.clsP_corel citem;
                        int corcont;
                        clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

                        if (fel.idcontingencia ==0){
                            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)");
                        }else{
                            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
                        }

                        citem=P_corelObj.first();
                        corcont=citem.corelult+1;
                        citem.corelult=corcont;
                        P_corelObj.update(citem);

                        if (fel.idcontingencia ==0){
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

    private void callBackMulti() {

        fidx++;
        if (fidx<ftot) {
            if (fel.errorflag) ffail++;
            procesafactura();
        } else {
            if (ffail==0) {
                endstr="Certificados : "+ftot;
                if (gl.peEnvio) envioFacturas();else {toast(endstr); finish();}
            } else {
                endstr="Certificados : "+(ftot-ffail)+"\nSin certificacion : "+ffail;
                if (gl.peEnvio) envioFacturas(); else msgexit(endstr);
            }
        }
    }

    private void callBackSingle() {

        if (!fel.errorflag) {
            gl.feluuid=fel.fact_uuid;

            if (gl.peEnvio) {
                envioFactura(felcorel);
            } else {
                finish();
            }
        } else {

            gl.feluuid="";
            gl.FELmsg="Ocurrió error en FEL :\n\n"+ fel.error;
            startActivity(new Intent(this,FELmsgbox.class));
            finish();
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

            db.beginTransaction();

            clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);
            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
            citem=P_corelObj.first();
            corcont=citem.corelult+1;

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            //fact.feelcontingencia=fel.fact_serie+"-"+fel.fact_numero;
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

    //region Envio una factura

    private void envioFactura(String cor) {

        scorel=cor;

        Handler mtimer = new Handler();
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                ws.callback = 1;
                ws.execute();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void processFactura() {
        clsClasses.clsD_facturad item;
        String tipo_producto,vsql;
        int contingencia,uruta;

        try {
            AppMethods f = new AppMethods(this,null,Con,db);

            D_facturaObj.fill("WHERE COREL='"+scorel+"'");
            D_facturadObj.fill("WHERE COREL='"+scorel+"'");
            D_facturapObj.fill("WHERE COREL='"+scorel+"'");
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);

            cliid=D_facturaObj.first().cliente;
            try {
                contingencia=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (contingencia<1) contingencia=0;
            } catch (Exception e) {
                contingencia=0;
            }

            //idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;

            CSQL="DELETE FROM D_FACTURA WHERE COREL='"+scorel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+scorel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+scorel+"';";

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

                //UpdateToStock =D_facturadObj.addItemUpdateStockSql(D_facturadObj.items.get(i), gl.tienda) + ";";
                //if (!UpdateToStock.isEmpty()) CSQL=CSQL+ UpdateToStock;

            }

            for (int i = 0; i < D_facturapObj.count; i++) {
                CSQL=CSQL+D_facturapObj.addItemSql(D_facturapObj.items.get(i)) + ";";
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
        ins.add("FECHAENTR",fs);
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
            toast("Error de envio");
        } else {
            toast("Envio completo "+ gl.feluuid);
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
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                ws.callback = 2;
                ws.execute();
            }
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

            /*
            if (contingencia==0) {
                CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+D_facturaObj.first().corelativo+"  " +
                        "WHERE SERIE='"+D_facturaObj.first().serie+"' AND ACTIVA=1 AND RUTA=" + gl.codigo_ruta + ";";
            } else {
                CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+D_facturaObj.first().corelativo+",RESGUARDO="+contingencia+"  " +
                        "WHERE SERIE='"+D_facturaObj.first().serie+"' AND ACTIVA=1 AND RUTA=" + gl.codigo_ruta + ";";
            }
            */

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
                    //#EJC20200622: Fin single FEL
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

    private void updateLabelSend() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable(){
                    public void run() {
                        lbl1.setText("Enviando factura "+(fidx+2)+" / "+ftot);lbl3.setText("");
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