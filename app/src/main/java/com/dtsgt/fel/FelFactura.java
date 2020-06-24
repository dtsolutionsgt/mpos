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

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsD_fel_errorObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_productoObj;
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

        fel=new clsFELInFile(this,this);

        getURL();
        ws = new WebServiceHandler(FelFactura.this, gl.wsurl, gl.timeout);
        xobj = new XMLObject(ws);

        clsP_sucursalObj sucursal=new clsP_sucursalObj(this,Con,db);
        sucursal.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
        clsClasses.clsP_sucursal suc=sucursal.first();

        fel.fel_llave_certificacion =suc.fel_llave_certificacion; // fel_llavews ="E5DC9FFBA5F3653E27DF2FC1DCAC824D"
        fel.fel_llave_firma=suc.fel_llave_firma; // fel_token ="5b174fb0e23645b65ef88277d654603d"
        fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;  //  1
        fel.fel_usuario_certificacion=suc.fel_usuario_certificacion; // COMERGUA
        fel.fel_usuario_firma=suc.fel_usuario_firma; // COMERGUA
        fel.fel_nit=suc.nit; // NIT  96038888
        fel.fel_correo=suc.correo;  //
        fel.fel_nombre_comercial = gl.tiendanom;
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
        if (multiflag) {
            if (fel.errorcon) {
                msgexit(fel.error);return;
            }
            if (fel.errorflag) {
                ffail++;
                guardaError();
            } else marcaFactura();

            callBackMulti();
         } else {
            if (!fel.errorflag) {
                marcaFactura();
            } else {
                guardaError();
            }

            callBackSingle();
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

        corel=facts.get(fidx);

        try {

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            cliid=fact.cliente;

            D_facturafObj.fill("WHERE Corel='"+corel+"'");
            factf=D_facturafObj.first();

            fel.mpos_identificador_fact =fact.serie+fact.corelativo;

            fel.iniciar(fact.fecha);
            fel.emisor(fel.fel_afiliacion_iva,fel.fel_codigo_establecimiento,fel.fel_correo,
                      fel.fel_nit,fel.fel_nombre_comercial, fel.fel_usuario_firma);
            fel.emisorDireccion("Direccion",fel.codigo_postal,"GUATEMALA","GUATEMALA","GT");

            //#EJC20200527: Quitar "-" del nit
            factf.nit =factf.nit.replace("-","");
            factf.nit =factf.nit.replace(".","");

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

        try {

            D_facturaObj.fill("WHERE COREL='"+scorel+"'");
            D_facturadObj.fill("WHERE COREL='"+scorel+"'");
            D_facturapObj.fill("WHERE COREL='"+scorel+"'");
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);

            cliid=D_facturaObj.first().cliente;

            //idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;

            CSQL="DELETE FROM D_FACTURA WHERE COREL='"+scorel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+scorel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+scorel+"';";

            CSQL=CSQL+addFactheader(D_facturaObj.first())+ ";";

            for (int i = 0; i <D_facturadObj.count; i++) {
                CSQL=CSQL+D_facturadObj.addItemSql(D_facturadObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_facturapObj.count; i++) {
                CSQL=CSQL+D_facturapObj.addItemSql(D_facturapObj.items.get(i)) + ";";
            }

            P_clienteObj.fill("WHERE CODIGO_CLIENTE="+cliid);

            ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+cliid+")";
            CSQL = CSQL + ss + "\n";
            ss=P_clienteObj.addItemSql(P_clienteObj.first(),gl.emp);
            CSQL = CSQL + ss + "\n";

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public String addFactheader(clsClasses.clsD_factura item) {

        String fs=""+du.univfechalong(item.fecha);

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
        ins.add("FEELFECHAPROCESADO",item.feelfechaprocesado);
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
            msgbox(e.getMessage());
        }
    }

    private void processComplete() {

        Date currentTime = Calendar.getInstance().getTime();
        Log.i("FEL_FINISH: ", currentTime.toString());

        if (ws.errorflag) {
            toast("Error de envio");
        } else {
            toast("Envio completo");
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
        fidx++;
        corel=factlist.get(fidx);
        factsend=false;

        try {

            D_facturaObj.fill("WHERE COREL='"+corel+"'");
            D_facturadObj.fill("WHERE COREL='"+corel+"'");
            D_facturapObj.fill("WHERE COREL='"+corel+"'");
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);

            cliid=D_facturaObj.first().cliente;

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

            P_clienteObj.fill("WHERE CODIGO_CLIENTE="+cliid);

            ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+cliid+")";
            CSQL = CSQL + ss + "\n";
            ss=P_clienteObj.addItemSql(P_clienteObj.first(),gl.emp);
            CSQL = CSQL + ss + "\n";

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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
                }
                else {
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
                showMsgExit("Ocurrio error en FEL :\n\n"+ fel.error);
            }
        };
        mtimer.postDelayed(mrunner,500);

    }

    public void showMsgExit(String msg) {
        try {
            ExDialog dialog = new ExDialog(this);
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