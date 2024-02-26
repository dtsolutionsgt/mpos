package com.dtsgt.felesa;

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
import com.dtsgt.classes.clsD_factura_fel_paisObj;
import com.dtsgt.classes.clsD_factura_svObj;
import com.dtsgt.classes.clsD_facturacObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsD_facturaprObj;
import com.dtsgt.classes.clsD_facturarObj;
import com.dtsgt.classes.clsD_fel_bitacoraObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_contingencia_svObj;
import com.dtsgt.fel.FELmsgbox;
import com.dtsgt.fel.clsFELInFile;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FELContingenciaSV extends PBase {

    private TextView lbl1, lbl2, lbl3, lblHalt;
    private ProgressBar pbar;

    private clsFELClases fclas = new clsFELClases();
    private clsFELClases.JSONFactura jfact;
    private clsFELClases.JSONCredito jcred;
    private clsFELClases.JSONContingencia jcont;
    private clsFactESA FactESA;

    private clsFELInFile fel;

    private WebServiceHandler ws;
    private XMLObject xobj;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_facturapObj D_facturapObj;
    private clsD_facturacObj D_facturacObj;
    private clsD_facturarObj D_facturarObj;
    private clsD_facturaprObj D_facturaprObj;
    private clsD_factura_felObj D_factura_felObj;
    private clsD_factura_fel_paisObj D_factura_fel_paisObj;
    private clsD_factura_svObj D_factura_svObj;
    private clsT_contingencia_svObj T_contingencia_svObj;
    private clsP_productoObj prod;
    private clsD_fel_bitacoraObj D_fel_bitacoraObj;

    private clsClasses.clsD_factura fact = clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd = clsCls.new clsD_facturad();
    private clsClasses.clsD_facturaf factf = clsCls.new clsD_facturaf();
    private clsClasses.clsD_facturap factp = clsCls.new clsD_facturap();
    private clsClasses.clsD_fel_bitacora fbita = clsCls.new clsD_fel_bitacora();

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
    private Date systemTime, stime;

    private ArrayList<String> facts = new ArrayList<String>();
    private ArrayList<String> factlist = new ArrayList<String>();
    private ArrayList<String> rutas = new ArrayList<String>();

    private String felcorel, corel, ffcorel, scorel, CSQL, idfact, tipodoc;
    private String dnum,cnombre,cnit,cdir, ccorreo,cgiro,cdep,cmuni,cllave;

    private boolean ddemomode, factsend, contmode;
    private int ftot, ffail, fidx, cliid, felnivel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_felcontingencia_sv);

            super.InitBase();

            lbl1 = findViewById(R.id.msgHeader);
            lbl1.setText("");
            lbl2 = findViewById(R.id.lblWS);
            lbl2.setText("");
            lbl3 = findViewById(R.id.textView152);
            lbl3.setText("");
            lblHalt = findViewById(R.id.textView218);
            lblHalt.setVisibility(View.VISIBLE);
            pbar = findViewById(R.id.progressBar);
            pbar.setVisibility(View.INVISIBLE);

            felcorel = gl.felcorel;
            ffcorel = felcorel;
            corel = felcorel;
            gl.feluuid = "";

            getURL();

            fel = new clsFELInFile(this, this, gl.timeout);
            fel.halt = false;
            fel.autocancel = true;
            xobj = new XMLObject(ws);

            clsP_sucursalObj sucursal = new clsP_sucursalObj(this, Con, db);

            sucursal.fill("WHERE CODIGO_SUCURSAL=" + gl.tienda);
            clsClasses.clsP_sucursal suc = sucursal.first();

            fel.fel_llave_certificacion = suc.fel_llave_firma;
            fel.fel_llave_firma = suc.fel_llave_firma;
            fel.fel_usuario_certificacion = suc.fel_usuario_firma;
            fel.fel_codigo_establecimiento = suc.fel_codigo_establecimiento;

            fel.fel_usuario_firma = suc.texto;
            fel.codigo_postal = suc.codigo_postal;
            fel.fel_nit = suc.nit.toUpperCase();
            fel.fel_correo = suc.correo;
            fel.fel_nombre_comercial = suc.nombre;

            fel.fraseIVA = 1;
            fel.fraseISR = 1;
            fel.fel_afiliacion_iva = " ";
            fel.fel_tipo_documento = " ";

            fel.iduniflag = false;
            fel.halt = false;

            D_facturaObj = new clsD_facturaObj(this, Con, db);
            D_facturaObj.fill("WHERE (COREL='" + felcorel + "')");

            D_facturadObj = new clsD_facturadObj(this, Con, db);
            D_facturafObj = new clsD_facturafObj(this, Con, db);
            D_facturapObj = new clsD_facturapObj(this, Con, db);
            D_facturarObj = new clsD_facturarObj(this, Con, db);
            D_facturacObj = new clsD_facturacObj(this, Con, db);
            D_facturaprObj = new clsD_facturaprObj(this, Con, db);
            D_factura_felObj = new clsD_factura_felObj(this, Con, db);
            D_factura_fel_paisObj = new clsD_factura_fel_paisObj(this, Con, db);
            D_factura_svObj = new clsD_factura_svObj(this, Con, db);
            T_contingencia_svObj=new clsT_contingencia_svObj(this,Con,db);

            D_fel_bitacoraObj = new clsD_fel_bitacoraObj(this, Con, db);

            prod = new clsP_productoObj(this, Con, db);

            app.parametrosExtra();
            lbl2.setText(" ");
            pbar.setVisibility(View.VISIBLE);

            facts.add(felcorel);

            String FELArchContLLave = "Certificado_06141106141147.crt";
            String FELAmbiente = "02";

            FactESA = new clsFactESA(this, fel.fel_usuario_certificacion, fel.fel_llave_certificacion);
            jcont = fclas.new JSONContingencia(fel.fel_usuario_certificacion, fel.fel_llave_certificacion,
                    FELAmbiente, FELArchContLLave);

            ws = new WebServiceHandler(FELContingenciaSV.this, gl.wsurl, 60000);

            ffail = 0;
            fidx = 0;

            if (facts.size() > 0) {
                Handler mtimer = new Handler();
                Runnable mrunner = () -> {
                    procesaDocumento();
                };
                mtimer.postDelayed(mrunner, 100);
            } else {
                finish();
                return;
            }

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //region Events

    public void doHalt(View viev) {
        fel.halt = true;
        lblHalt.setVisibility(View.INVISIBLE);

        Handler mtimer = new Handler();
        Runnable mrunner = () -> finish();
        mtimer.postDelayed(mrunner, 200);

    }

    //endregion

    //region Main

    private void procesaDocumento() {
        updateLabel();

        if (app.isOnWifi() == 0) {
            gl.FELmsg = "ERROR DE CONEXIÓN A INTERNET";
            startActivity(new Intent(this, FELmsgbox.class));finish();
        } else {
            try {
                Handler mtimer = new Handler();
                Runnable mrunner = () -> certificaDocumento();
                mtimer.postDelayed(mrunner, 200);
            } catch (Exception e) {
                msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            }
        }
    }

    private void certificaDocumento() {

        try {
            lbl1.setText("Construyendo XML...");
            creaBitacora();

            D_facturaObj.fill("WHERE (COREL='" + felcorel + "')");
            tipodoc = D_facturaObj.first().ayudante;

            if (tipodoc.equalsIgnoreCase("N")) {
                JSONfactura("N");
            } else if (tipodoc.equalsIgnoreCase("C")) {
                JSONcredito();
            } else if (tipodoc.equalsIgnoreCase("T")) {
                JSONfactura("T");
            }

            lbl1.setText("Certificando Factura...");

            if (!tipodoc.equalsIgnoreCase("C")) {
                FactESA.Certifica(felcorel, jfact.json);
            } else {
                FactESA.Certifica(felcorel, jcred.json);
            }
        } catch (Exception e) {
            msgbox(new Object() { }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void JSONfactura(String tipodoc) throws JSONException {
        String nprod, cnom, cnit, ccor;

        try {

            jfact = fclas.new JSONFactura();

            jfact.Factura(fel.fel_codigo_establecimiento);

            if (tipodoc.equalsIgnoreCase("N")) {
                try {
                    D_facturafObj.fill("WHERE (COREL='" + felcorel + "')");

                    cnom = D_facturafObj.first().nombre;
                    cnit = D_facturafObj.first().nit;
                    ccor = D_facturafObj.first().correo;

                    jfact.agregarReceptor(cnom, cnit, ccor);
                } catch (Exception e) {
                    msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
                }
            }

            D_facturadObj.fill("WHERE (COREL='" + felcorel + "')");
            for (int i = 0; i < D_facturadObj.count; i++) {
                nprod = prodName(D_facturadObj.items.get(i).producto);

                jfact.agregarServicio(nprod,
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio con iva
                        D_facturadObj.items.get(i).desmon);
            }

            jfact.agregarAdenda(" ");

            T_contingencia_svObj.fill("WHERE (COREL='" + felcorel + "')");
            if (T_contingencia_svObj.count>0) {
                cllave=T_contingencia_svObj.first().llave;
                jfact.agregarContingencia(cllave);
            }

            jfact.json();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void JSONcredito() throws JSONException {
        String dnum, nprod, cnombre, cnit, cdir, ccorreo, cgiro, cdep, cmuni;
        long cornum, fcor;

        try {

            jcred = fclas.new JSONCredito();
            jcred.mu = mu;

            jcred.Credito(fel.fel_codigo_establecimiento);

            D_facturafObj.fill("WHERE (COREL='" + felcorel + "')");

            fcor = D_facturaObj.first().corelativo;
            cornum = 11129000000000L;
            cornum = cornum + fcor;
            dnum = cornum + "";

            cnombre = D_facturafObj.first().nombre;
            cnit = D_facturafObj.first().nit;
            cnit = cnit.replace("-", "");
            cdir = D_facturafObj.first().direccion;
            ccorreo = D_facturafObj.first().correo;

            D_factura_svObj.fill("WHERE (COREL='" + felcorel + "')");
            cgiro = D_factura_svObj.first().codigo_tipo_negocio + "";
            cdep = D_factura_svObj.first().codigo_departamento;
            cdep = cdep.substring(1, 3);
            cmuni = D_factura_svObj.first().codigo_municipio;
            cmuni = cmuni.substring(3, 5);

            jcred.Receptor(dnum, cnit, cnombre, cgiro, cnombre);
            jcred.Direccion(cdep, cmuni, cdir, ccorreo);

            D_facturadObj.fill("WHERE (COREL='" + felcorel + "')");

            for (int i = 0; i < D_facturadObj.count; i++) {
                nprod = prodName(D_facturadObj.items.get(i).producto);

                jcred.agregarProducto(nprod,
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio sin iva
                        D_facturadObj.items.get(i).imp);
            }

            T_contingencia_svObj.fill("WHERE (COREL='" + felcorel + "')");
            if (T_contingencia_svObj.count>0) {
                cllave=T_contingencia_svObj.first().llave;
                jfact.agregarContingencia(cllave);
            }

            jcred.json();

            String sj = jcred.json;
            sj = sj + "";
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    @Override
    public void felCallBack() {
        long tdiff;
        double tsec;

        try {
            Date systemTime = Calendar.getInstance().getTime();
            fel.ftime3 = systemTime.getTime();

            if (fel.ftime1 > 0) {
                if (fel.ftime2 > 0) {
                    tdiff = fel.ftime2 - fel.ftime1;
                    tsec = ((double) tdiff) / 1000;
                    fbita.tiempo_firma = tsec;

                    if (fel.ftime3 > 0) {
                        tdiff = fel.ftime3 - fel.ftime2;
                        tsec = ((double) tdiff) / 1000;
                        fbita.tiempo_cert = tsec;
                    }

                    fbita.estado = 1;
                    if (fel.halt) fbita.estado = 0;
                    D_fel_bitacoraObj.update(fbita);
                }
            }
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

        try {
            if (!FactESA.errorflag) {
                felProgress(FactESA.respuesta.mensaje);
                if (!FactESA.respuesta.duplicado) {
                    guardaRespuestaFEL();
                    marcaFactura();
                }
                callBackSingle();
            } else {
                gl.FELmsg = "Ocurrió error en FEL :\n\n" + "Factura: " + felcorel + "\n" + fel.error;gl.feluuid = "";
                startActivity(new Intent(this, FELmsgbox.class));finish();
            }
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void callBackSingle() {
        try {
            if (!FactESA.errorflag) {
                gl.feluuid = FactESA.respuesta.codigoGeneracion;
                //toastlong("Envio completo "+ gl.feluuid);
                if (gl.peEnvio) {
                    if (!gl.feluuid.isEmpty()) {
                        if (gl.feluuid.length() > 10) {
                            if (gl.feluuid.indexOf("-") > 1) {
                                envioFactura(felcorel);
                                finish();
                            }
                        }
                    }
                } else {
                    finish();
                }
            } else {
                gl.FELmsg = "Ocurrió error en FEL :\n\n" + "Factura: " + fel.mpos_identificador_fact + "\n" + fel.error;
                startActivity(new Intent(this, FELmsgbox.class));finish();
            }
        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void marcaFactura() {

        try {

            D_facturaObj.fill("WHERE Corel='" + felcorel + "'");
            fact = D_facturaObj.first();

            fact.feeluuid = FactESA.respuesta.codigoGeneracion;
            fact.feelserie = FactESA.respuesta.selloRecepcion;
            fact.feelnumero = FactESA.respuesta.numeroControl;
            fact.feelfechaprocesado = du.getActDateTime();
            fact.statcom = "N";

            D_facturaObj.update(fact);

        } catch (Exception e) {
            msgbox2(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    private void guardaRespuestaFEL() {
        try {
            clsD_factura_fel_paisObj D_factura_fel_paisObj = new clsD_factura_fel_paisObj(this, Con, db);
            int newid = D_factura_fel_paisObj.newID("SELECT MAX(codigo_factura) FROM D_factura_fel_pais");

            clsClasses.clsD_factura_fel_pais item;

            item = clsCls.new clsD_factura_fel_pais();
            clsFELClases.respuesta r = fclas.new respuesta();
            r = FactESA.respuesta;

            item.codigo_factura = newid;
            item.empresa = gl.emp;
            item.corel = felcorel;
            item.codigo_pais = gl.codigo_pais;
            item.codigo_moneda = 1;
            item.fec_agr = 0;
            item.sv_mensaje = r.mensaje;
            item.sv_pdf_path = r.pathpdf;
            item.sv_identificador = r.identificador;
            item.sv_codigogeneracion = r.codigoGeneracion;
            item.sv_sellorecepcion = r.selloRecepcion;
            item.sv_numerocontrol = r.numeroControl;
            item.sv_status = r.status;
            item.sv_fechaemision = r.fechaEmision;
            item.sv_estado = r.estado;
            item.sv_totalnosuj = r.totalNoSuj;
            item.sv_totalexenta = r.totalExenta;
            item.sv_totalgravada = r.totalGravada;
            item.sv_subtotalventas = r.subTotalVentas;
            item.sv_descunosuj = r.descuNoSuj;
            item.sv_descuexenta = r.descuExenta;
            item.sv_descugravada = r.descuGravada;
            item.sv_porcentajedescuento = r.porcentajeDescuento;
            item.sv_totaldescu = r.totalDescu;
            item.sv_subtotal = r.subTotal;
            item.sv_ivarete1 = r.ivaRete1;
            item.sv_reterenta = r.reteRenta;
            item.sv_montototaloperacion = r.montoTotalOperacion;
            item.sv_totalnogravado = r.totalNoGravado;
            item.sv_totalpagar = r.totalPagar;
            item.sv_totalletras = r.totalLetras;
            item.sv_saldofavor = r.saldoFavor;
            item.sv_totaliva = r.totalIva;

            D_factura_fel_paisObj.add(item);

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Envio de la factura

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
            D_factura_fel_paisObj.fill("WHERE COREL='"+corel+"'");
            D_factura_svObj.fill("WHERE COREL='"+corel+"'");

            idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;
            cliid=D_facturaObj.first().cliente;
            try {
                contingencia=Integer.parseInt(D_facturaObj.first().feelcontingencia);
                if (contingencia<1) contingencia=0;
            } catch (Exception e) {
                contingencia=0;
            }

            D_factura_fel_paisObj.fill("WHERE COREL='"+corel+"'");
            D_factura_svObj.fill("WHERE COREL='"+corel+"'");

            CSQL="DELETE FROM D_FACTURA WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAC WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURAPR WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURA_FEL_PAIS WHERE COREL='"+corel+"';";
            CSQL=CSQL+"DELETE FROM D_FACTURA_SV WHERE COREL='"+corel+"';";


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

            for (int i = 0; i < D_facturaprObj.count; i++) {
                CSQL=CSQL+addPropinaItem(D_facturaprObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_factura_svObj.count; i++) {
                CSQL=CSQL+D_factSVItemSql(D_factura_svObj.items.get(i)) + ";";
            }

            for (int i = 0; i < D_factura_fel_paisObj.count; i++) {
                CSQL=CSQL+D_fact_fel_paisItemSql(D_factura_fel_paisObj.items.get(i)) + ";";
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

    public String D_factSVItemSql(clsClasses.clsD_factura_sv item) {

        ins.init("D_factura_sv");
        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_TIPO_FACTURA",item.codigo_tipo_factura);
        ins.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        ins.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        ins.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);

        return ins.sql();
    }

    public String D_fact_fel_paisItemSql(clsClasses.clsD_factura_fel_pais item) {

        ins.init("D_factura_fel_pais");

        ins.add("empresa",item.empresa);
        ins.add("corel",item.corel);
        ins.add("codigo_pais",item.codigo_pais);
        ins.add("codigo_moneda",item.codigo_moneda);
        ins.add("SV_mensaje",item.sv_mensaje);
        ins.add("SV_pdf_path",item.sv_pdf_path);
        ins.add("SV_identificador",item.sv_identificador);
        ins.add("SV_codigoGeneracion",item.sv_codigogeneracion);
        ins.add("SV_selloRecepcion",item.sv_sellorecepcion);
        ins.add("SV_numeroControl",item.sv_numerocontrol);
        ins.add("SV_status",item.sv_status);
        ins.add("SV_fechaEmision",item.sv_fechaemision);
        ins.add("SV_estado",item.sv_estado);
        ins.add("SV_totalNoSuj",item.sv_totalnosuj);
        ins.add("SV_totalExenta",item.sv_totalexenta);
        ins.add("SV_totalGravada",item.sv_totalgravada);
        ins.add("SV_subTotalVentas",item.sv_subtotalventas);
        ins.add("SV_descuNoSuj",item.sv_descunosuj);
        ins.add("SV_descuExenta",item.sv_descuexenta);
        ins.add("SV_descuGravada",item.sv_descugravada);
        ins.add("SV_porcentajeDescuento",item.sv_porcentajedescuento);
        ins.add("SV_totalDescu",item.sv_totaldescu);
        ins.add("SV_subTotal",item.sv_subtotal);
        ins.add("SV_ivaRete1",item.sv_ivarete1);
        ins.add("SV_reteRenta",item.sv_reterenta);
        ins.add("SV_montoTotalOperacion",item.sv_montototaloperacion);
        ins.add("SV_totalNoGravado",item.sv_totalnogravado);
        ins.add("SV_totalPagar",item.sv_totalpagar);
        ins.add("SV_totalLetras",item.sv_totalletras);
        ins.add("SV_saldoFavor",item.sv_saldofavor);
        ins.add("SV_totalIva",item.sv_totaliva);

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

    //region WebService handler

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent, String Url, int TimeOut) {
            super(Parent, Url, TimeOut);
        }

        @Override
        public void wsExecute() {
            try {
                processFactura();
                callMethod("Commit", "SQL", CSQL);
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
                processComplete();return;
            }

            statusFactura();
            processComplete();
        } catch (Exception e) {
            msgbox2(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    //endregion

    //region Aux

    private void updateLabel() {
        Handler handler = new Handler();
        Runnable runnable = () -> handler.post(() -> {
            lbl1.setText("Certificando factura ");lbl3.setText("");
        });
        new Thread(runnable).start();
    }

    @Override
    public void felProgress(String msg) {
        Handler mtimer = new Handler();
        Runnable mrunner = () -> lbl1.setText(msg);
        mtimer.postDelayed(mrunner, 50);
    }

    private void creaBitacora() {
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
            D_factura_fel_paisObj.reconnect(Con,db);
            D_factura_svObj.reconnect(Con,db);
            T_contingencia_svObj.reconnect(Con,db);

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