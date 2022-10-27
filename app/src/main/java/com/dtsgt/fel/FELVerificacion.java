package com.dtsgt.fel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturacObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_factura_felObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import com.dtsgt.mpos.WSEnv;
import com.dtsgt.webservice.wsFacturasFEL;
import com.dtsgt.webservice.wsInventEnvio;
import com.dtsgt.webservice.wsInventRecibir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FELVerificacion extends PBase {

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;
    private XMLObject xobj;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_facturapObj D_facturapObj;
    private clsP_productoObj prod;

    private wsFacturasFEL ffel;
    private Runnable rnFacturasFEL;

    private clsClasses.clsD_factura fact=clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd=clsCls.new clsD_facturad();
    private clsClasses.clsD_facturaf factf=clsCls.new clsD_facturaf();
    private clsClasses.clsD_facturap factp=clsCls.new clsD_facturap();

    private ArrayList<String> facts = new ArrayList<String>();

    private String felcorel,corel,ffcorel,scorel,CSQL,endstr,idfact,lcombo;
    private boolean conerrflag,ddemomode,multiflag,factsend,contmode;
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

        ffel=new wsFacturasFEL(gl.wsurl);

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
        fel.fel_afiliacion_iva=suc.fel_afiliacion_iva;

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturafObj=new clsD_facturafObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);
        prod=new clsP_productoObj(this,Con,db);

        app.parametrosExtra();
        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        marcaFacturaContingencia();

        buildList();

        ffail=0;fidx=0;conerrflag=false;
        fel.errorflag=false;fel.halt=false;

        rnFacturasFEL=new Runnable() {
            @Override
            public void run() {
                if (ffel.items.size()>0) {
                    actualizaValidadas();
                } else {
                    contingencia();
                }
            }
        };

        if (facts.size()>0) {
            procesaValidacion();
        } else {
            msgexit("No existen facturas pendientes de certificación");
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

        try {

            int ii=0;

            if (fel.errorcon) {
                conerrflag=true;
                msgexit(fel.error);return;
            }

            if (fel.errorflag) {
                if (fel.duplicado) {
                    fact.codigoliquidacion=1;
                    fact.feeluuid=fel.ret_uuid;
                    fact.feelserie=fel.ret_serie;
                    fact.feelnumero=fel.ret_numero;
                    fact.feelfechaprocesado = du.getActDateTime(); //#EJC20200921: Marcar como enviada a FEL.
                    D_facturaObj.update(fact);
                }
            } else  {
                //#EJC20200921: What does codigoliquidacion 2 means ?
                fact.codigoliquidacion=2;
                fact.feeluuid=fel.ret_uuid;
                fact.feelserie=fel.ret_serie;
                fact.feelnumero=fel.ret_numero;
                fact.feelfechaprocesado = du.getActDateTime(); //#EJC20200921: Marcar como enviada a FEL.
                D_facturaObj.update(fact);
            }

            callBackMulti();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void felProgress(String msg) {
        Handler mtimer = new Handler();
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                lbl1.setText(msg);
            }
        };
        mtimer.postDelayed(mrunner,50);
    }

    private void callBackMulti() {

        fidx++;
        if (fidx<ftot) {
            if (fel.errorflag) ffail++;
            procesafactura();
        } else {
            //#EJC20200921: Ya no es necesario
            //if (!conerrflag) startActivity(new Intent(this,FELContAnul.class));
            gl.autocom = 1;
            startActivity(new Intent(FELVerificacion.this, WSEnv.class));
            finish();
        }
    }

    private void buildFactXML() {

        String dir,muni,dep,iddep,idmuni;
        int idcont;
        double lprec,ltot,ldesc;

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

            fel.mpos_identificador_fact =fact.serie+fact.corelativo;

            //fact.fecha=(long) (2206071000f);
            fel.iniciar(fact.fecha,""+fel.idcontingencia);

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

            fel.emisorDireccion(dir,
                                fel.codigo_postal,
                                muni,
                                dep,
                                gl.codigo_pais);

            //#EJC20200527: Quitar "-" del nit
            factf.nit=factf.nit.trim();
            factf.nit =factf.nit.replace("-","");
            factf.nit =factf.nit.replace(".","");
            factf.nit =factf.nit.replace(" ","");
            factf.nit=factf.nit.toUpperCase();

            //#EJC20210130: Una dirección tenía enter... quitar espacios vacíos.
            factf.direccion = factf.direccion.trim();

            //#EJC20210130: Si la direccion es vacia, enviar ciudad o valor parametrizado por defecto
            if (factf.direccion.isEmpty()){
                factf.direccion =muni;
            }

            //#EJC20211222: Chapusería de navidad, Jaros por favor corregir esto.
            if(fel.fraseIVA==3){
                fel.fraseIVA=1;
            }

            /*
            if(fel.fraseIVA==4) fel.fraseIVA=2;
            if(fel.fraseISR==4) fel.fraseISR=2;
            */

            //#EJC202210261721:Hot Fix por error al guardar el Cliente.
            if(factf.nit.contains("Consumidor final")|| factf.nit.contains("CONSUMIDORFINAL")){
                factf.nit ="CF";
                factf.nombre = "Consumidor Final";
                factf.direccion = "Ciudad";
                factf.correo =fel.correo_sucursal;
            }

            fel.receptor(factf.nit,
                         factf.nombre,
                         factf.direccion,
                         factf.correo,
                         fel.codigo_postal,
                         muni,
                         dep,
                         gl.codigo_pais);

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
                            lcombo);
            }

            fel.completar(fact.serie,
                          fact.corelativo);

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void marcaFacturaContingencia() {

        clsClasses.clsP_corel citem;
        long corcont;

        try {

            //ss="where anulado=0 and feelfechaprocesado=0 and feeluuid=' ' and feelcontingencia=' 'and fecha>2009230000";
            ss="WHERE anulado=0 AND feelfechaprocesado=0 " +
                    " AND feeluuid=' ' AND feelcontingencia=' ' AND fecha>='2010010000'";
            D_facturaObj.fill(ss);
            if (D_facturaObj.count == 0) return;

            clsP_corelObj P_corelObj = new clsP_corelObj(this, Con, db);
            P_corelObj.fill("WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=1)");
            citem = P_corelObj.first();
            if (citem.corelult == 0) {
                corcont = citem.corelini;
            } else {
                corcont = citem.corelult + 1;
            }

            for (int i = 0; i < D_facturaObj.count; i++) {

                try {
                    db.beginTransaction();

                    fact = D_facturaObj.items.get(i);
                    fact.feelcontingencia = "" + corcont;
                    D_facturaObj.update(fact);

                    citem.corelult = corcont;
                    P_corelObj.update(citem);

                    db.setTransactionSuccessful();
                    db.endTransaction();
                } catch (Exception e) {
                    db.endTransaction();
                    msgbox2(new Object() {}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }

                corcont++;
            }

        } catch(Exception ee){
            try {
                db.endTransaction();
            } catch (Exception e) { }

            msgbox2(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + ee.getMessage());
        }
    }


    //endregion

    //region Validacion BD Nube

    private void procesaValidacion() {
        String ss="SELECT COREL, FEELSERIE, FEELNUMERO, FEELUUID, FEELFECHAPROCESADO, FEELCONTINGENCIA " +
                "FROM D_FACTURA WHERE (FEELUUID<>' ') AND COREL IN  (";

        for (int i = 0; i <facts.size(); i++) {
            ss=ss+"'"+facts.get(i)+"'";
            if (i<facts.size()-1) ss=ss+",";else ss=ss+")";
        }

        ffel.execute(ss,rnFacturasFEL);
    }

    private void actualizaValidadas() {
        clsClasses.clsD_factura fact;

        try {
            clsT_factura_felObj T_factura_felObj=new clsT_factura_felObj(this,Con,db);

            for (int i = 0; i <ffel.items.size(); i++) {
                sql=ffel.items.get(i);
                db.execSQL(sql);
            }

            T_factura_felObj.fill();

            for (int i = 0; i <T_factura_felObj.count; i++) {

                D_facturaObj.fill("WHERE COREL='"+T_factura_felObj.items.get(i).corel+"'");
                fact=D_facturaObj.first();

                fact.feelserie=T_factura_felObj.items.get(i).feelserie;
                fact.feelnumero=T_factura_felObj.items.get(i).feelnumero;
                fact.feeluuid=T_factura_felObj.items.get(i).feeluuid;
                fact.feelfechaprocesado=T_factura_felObj.items.get(i).feelfechaprocesado;
                fact.feelcontingencia=T_factura_felObj.items.get(i).feelcontingencia;
                fact.statcom="S";

                D_facturaObj.update(fact);

            }

            buildList();

            if (facts.size()>0) {
                contingencia();
            } else {
                showMsgExit2("Certificacion completa");
            }

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
            long flim=du.addDays(du.getActDate(),-5);

            D_facturaObj.fill("WHERE (FEELUUID=' ') AND (ANULADO=0) " +
              "AND (FECHA>="+flim+") ORDER BY FEELCONTINGENCIA");
            //sql="where feelcontingencia>0  and anulado=0 and " +
            //        "feelfechaprocesado=0 and feeluuid=' ' and fecha>2009230000";
            //sql="where anulado=0 and feelfechaprocesado=0 and feeluuid=' ' and fecha>2203090000";

            //D_facturaObj.fill(sql);

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

    private String listaCombo(String ccorel,String idcombo) {
        clsD_facturacObj D_facturacObj=new clsD_facturacObj(this,Con,db);
        String lc,nombre;

        try {
            D_facturacObj.fill("WHERE (COREL='"+ccorel+"') AND (IDCombo="+idcombo+")");

            lc="";
            for (int i = 0; i <D_facturacObj.count; i++) {
                nombre = D_facturacObj.items.get(i).nombre;
                lc="|@"+nombre;
            }

            return lc;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return "";
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

    public void showMsgExit2(String msg) {
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
            msgbox2(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //endregion
}