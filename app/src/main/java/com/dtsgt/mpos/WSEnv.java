package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_cajapagosObj;
import com.dtsgt.classes.clsP_cajareporteObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_stockObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WSEnv extends PBase {

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturapObj D_facturapObj;

    private clsD_facturaObj D_factura_SC_Obj;

    private clsD_MovObj D_MovObj;
    private clsD_MovDObj D_MovDObj;

    private clsP_cajacierreObj P_cjCierreObj;
    private clsP_cajapagosObj P_cjPagosObj;
    private clsP_cajareporteObj P_cjReporteObj;

    private ArrayList<String> clients = new ArrayList<String>();
    private ArrayList<String> rutas= new ArrayList<String>();
    private ArrayList<String> fact = new ArrayList<String>();
    private ArrayList<String> fact_sc = new ArrayList<String>();
    private ArrayList<String> mov = new ArrayList<String>();
    private ArrayList<String> cjCierre = new ArrayList<String>();
    private ArrayList<String> cjReporte = new ArrayList<String>();
    private ArrayList<String> cjPagos = new ArrayList<String>();
    private ArrayList<String> cStock= new ArrayList<String>();

    private String CSQL,plabel,rs, corel,ferr,idfact,corelMov, movErr, idMov,
            corelCjCierre, cjCierreError, corelCjReporte, cjReporteError,corelCjPagos, cjPagosError,cStockError;
    private int ftot,fsend,fidx,fTotMov,fIdxMov, mSend,cjCierreTot, cjCierreSend,
                cjReporteTot, cjReporteSend,cjPagosTot, cjPagosSend, cStockTot, cStockSend;
    private boolean factsend, movSend, cjCierreSendB,cjReporteSendB,cjPagosSendB,cStockSendB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_s_env);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.msgHeader);lbl1.setText("");
        lbl2 = (TextView) findViewById(R.id.textView151);lbl2.setText("");
        lbl3 = (TextView) findViewById(R.id.cmdRecibir);
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        app.getURL();

        ws = new WebServiceHandler(WSEnv.this, gl.wsurl, gl.timeout);
        xobj = new XMLObject(ws);

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);

        D_MovObj=new clsD_MovObj(this,Con,db);
        D_MovDObj=new clsD_MovDObj(this,Con,db);

        P_cjCierreObj = new clsP_cajacierreObj(this,Con,db);
        P_cjPagosObj = new clsP_cajapagosObj(this,Con,db);
        P_cjReporteObj = new clsP_cajareporteObj(this,Con,db);

        preparaEnvio();

        if (gl.autocom==1) {

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.VISIBLE);
                    lbl3.setVisibility(View.INVISIBLE);
                    execws(1);
                }
            };
            mtimer.postDelayed(mrunner,200);

        }
    }


    //region Events

    public void doStart(View view) {
        preparaEnvio();
        pbar.setVisibility(View.VISIBLE);
        execws(1);
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
                        processClients();
                        if (clients.size()>0) callMethod("Commit", "SQL", CSQL);
                        break;
                    case 2:
                        processFactura();
                        if (ftot>0) callMethod("Commit", "SQL", CSQL);

                         break;
                    case 3:
                        processMov();
                        if (fTotMov>0) callMethod("Commit", "SQL", CSQL);
                        break;
                    case 4:
                        processCajaCierre();
                        cjCierreTot=cjCierre.size();
                        if (cjCierreTot>0) callMethod("Commit", "SQL", CSQL);
                        break;
                    case 5:
                        processCajaPagos();
                        cjPagosTot=cjPagos.size();
                        if (cjPagosTot>0) callMethod("Commit", "SQL", CSQL);
                        break;
                    case 6:
                        processCajaReporte();
                        cjReporteTot=cjReporte.size();
                        if (cjReporteTot>0) callMethod("Commit", "SQL", CSQL);
                        break;
                    case 7:
                        processStock();
                        cStockTot=cStock.size();
                        if (cStockTot>0) callMethod("Commit", "SQL", CSQL);
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
                    if (clients.size()>0) statusClients();
                    execws(2);
                    break;
                case 2:
                    if (ftot>0) statusFactura();
                    if (fidx>=ftot-1) {
                        execws(3);
                    } else {
                        execws(2);
                    }
                    break;
                case 3:
                    if (fTotMov>0) statusMov();
                    if (fIdxMov>=fTotMov-1) {
                        execws(4);
                    } else {
                        execws(3);
                    }
                    break;
                case 4:
                    if (cjCierreTot>0) statusCajaCierre();
                    execws(5);
                    break;
                case 5:
                    if (cjPagosTot>0) statusCajaPagos();
                    execws(6);
                    break;
                case 6:
                    if (cjReporteTot>0) statusCajaReporte();
                    execws(7);
                    break;
                case 7:
                    if (cStockTot>0) statusStock();
                    processComplete();
                    break;
            }

        } catch (Exception e) {
            msgbox2(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    //endregion

    //region Main

    private void execws(final int callbackvalue) {

        switch (callbackvalue) {
            case 1:
                plabel = "Enviando Clientes";break;
            case 2:
                plabel = "Enviando Facturas";break;
            case 3:
                plabel = "Enviando Movimientos de inventario";break;
            case 4:
                plabel = "Enviando Caja Cierre";break;
            case 5:
                plabel = "Enviando Caja Pagos";break;
            case 6:
                plabel = "Enviando Caja Reporte";break;
            case 7:
                plabel = "Enviando Stock";break;
        }

        updateLabel();

        Handler mtimer = new Handler();
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                ws.callback = callbackvalue;
                ws.execute();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void processComplete() {
        try{
            pbar.setVisibility(View.INVISIBLE);
            plabel = "";
            updateLabel();

            if (ws.errorflag) {
                msgboxwait(ws.error);
            } else {

                if (gl.autocom==1) {
                    if (ferr.isEmpty() && movErr.isEmpty()) {
                        finish();return;
                    }
                }

                ss ="Envío completo\n";

                ss+="Facturas total: "+ftot+"\n";
                ss+="Facturas sin envio: "+(ftot-fsend)+"\n";

                ss+="Movimientos total: "+fTotMov+"\n";
                ss+="Movimientos sin envio: "+(fTotMov-mSend)+"\n";

                ss+="Caja cierre total: "+cjCierreTot+"\n";
                ss+="Caja cierre sin envio: "+(cjCierreTot-cjCierreSend)+"\n";

                ss+="Caja pagos total: "+cjPagosTot+"\n";
                ss+="Caja pagos sin envio: "+(cjPagosTot-cjPagosSend)+"\n";

                ss+="Caja reporte total: "+cjReporteTot+"\n";
                ss+="Caja reporte sin envio: "+(cjReporteTot-cjReporteSend)+"\n";

                ss+="Stock total: "+cStockTot+"\n";
                ss+="Stock sin envio: "+(cStockTot-cStockSend)+"\n";

                msgboxwait(ss);

                if (!ferr.isEmpty()) {
                    if (!idfact.isEmpty()){
                        msgbox2("Factura : "+idfact+"\n"+ferr);
                    }
                }

                if (!movErr.isEmpty()) {
                    if(!idMov.isEmpty()){
                        msgbox2("Movimientos : "+idMov+"\n"+movErr);
                    }
                }

            }

        } catch (Exception ex){
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ex.getMessage());
        }
    }

    //endregion

    //region Envío

    private void processFactura() {
        clsClasses.clsD_facturad item;
        String tipo_producto,vsql;
        int contingencia,uruta;

        if (ftot==0) {
            fidx++;return;
        }

        fidx++;
        corel=fact.get(fidx);
        factsend=false;

        clients.clear();

        //try {


        AppMethods f = new AppMethods(this,null,Con,db);
        clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);


        D_facturaObj.fill("WHERE COREL='"+corel+"'");
        D_facturadObj.fill("WHERE (COREL='"+corel+"') AND (PRODUCTO<>0)");
        D_facturapObj.fill("WHERE COREL='"+corel+"'");

        idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;
        int cliid=D_facturaObj.first().cliente;

        try {
            contingencia=Integer.parseInt(D_facturaObj.first().feelcontingencia);
            if (contingencia<1) contingencia=0;
        } catch (Exception e) {
            contingencia=0;
        }

        CSQL="DELETE FROM D_FACTURA WHERE COREL='"+corel+"';";
        CSQL=CSQL+"DELETE FROM D_FACTURAD WHERE COREL='"+corel+"';";
        CSQL=CSQL+"DELETE FROM D_FACTURAP WHERE COREL='"+corel+"';";

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

        CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+D_facturaObj.first().corelativo+"  " +
                "WHERE (SERIE='"+D_facturaObj.first().serie+"') AND (ACTIVA=1) AND (RESGUARDO=0) AND (RUTA=" + gl.codigo_ruta + ");";

        if (contingencia>0) {
            CSQL = CSQL+"UPDATE P_COREL SET CORELULT="+contingencia+"  " +
                    "WHERE (ACTIVA=1) AND (RESGUARDO=1) AND (RUTA=" + gl.codigo_ruta + ");";
        }

        P_clienteObj.fill("WHERE CODIGO_CLIENTE="+cliid);

        //#CKFK 20200920 Agregué el Delete de la direcciones del cliente
        ss="DELETE FROM P_CLIENTE_DIR WHERE (CODIGO_CLIENTE="+cliid+")";
        CSQL = CSQL + ss + ";";

        ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+cliid+")";
        CSQL = CSQL + ss + ";";
        ss=P_clienteObj.addItemSql(P_clienteObj.first(),gl.emp);
        CSQL = CSQL + ss + ";";

        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    public String addFactheader(clsClasses.clsD_factura item) {

        String fst;

        //String fs=""+du.univfechalong(item.fecha);
        //#EJC20200702: Formato fecha corregido.
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

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {
                ferr=rs;factsend=true;return;
            } else {
                factsend=true;
            }

            fsend++;

            try {
                sql="UPDATE D_Factura SET STATCOM='S' WHERE COREL='"+corel+"'";
                db.execSQL(sql);
            } catch (SQLException e) {
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processClients() {

        int ccli;

        clients.clear();

        //try {

            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);
            P_clienteObj.fill("WHERE ESERVICE='N'");

            CSQL="";

            for (int i = 0; i <P_clienteObj.count; i++) {

                ccli=P_clienteObj.items.get(i).codigo_cliente;
                P_clienteObj.items.get(i).eservice="S";

                ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+ccli+")";
                CSQL = CSQL + ss + ";";
                ss=P_clienteObj.addItemSql(P_clienteObj.items.get(i),gl.emp);
                CSQL = CSQL + ss + ";";

                clients.add(""+ccli);
            }

        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusClients() {

        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);
            if (!rs.equalsIgnoreCase("#")) {
                ws.error=rs;ws.errorflag=true;return;
            }

            for (int i = 0; i <clients.size(); i++) {
                try {
                    sql="UPDATE P_CLIENTE SET ESERVICE='S' WHERE CODIGO_CLIENTE="+clients.get(i);
                    db.execSQL(sql);
                } catch (SQLException e) {
                }
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processMov() {
        String vsql ="",tipomov;
        int uruta;
        double ucant;

        //try {

            if (fTotMov==0) {
                fIdxMov++;return;
            }

            fIdxMov++;
            corelMov=mov.get(fIdxMov);
            movSend=false;

            //mov.clear();

            D_MovObj.fill("WHERE COREL='"+corelMov+"'");
            D_MovDObj.fill("WHERE COREL='"+corelMov+"'");

            idMov=D_MovObj.first().COREL;
            tipomov=D_MovObj.first().TIPO;
            boolean Send_Stocks_Updates=tipomov.equalsIgnoreCase("R");

            CSQL="DELETE FROM D_MOV WHERE COREL='"+corelMov+"';";
            CSQL=CSQL+"DELETE FROM D_MOVD WHERE COREL='"+corelMov+"';";

            CSQL=CSQL+D_MovObj.addMovHeader(D_MovObj.first())+ ";";

            clsClasses.clsD_MovD item;

            for (int i = 0; i <D_MovDObj.count; i++) {

                item =D_MovDObj.items.get(i);

                CSQL=CSQL+D_MovDObj.addItemSqlWS(D_MovDObj.items.get(i)) + ";";

                AppMethods f = new AppMethods(this,null,Con,db);
                String tipo_producto = f.prodTipo(item.producto);

                if (Send_Stocks_Updates) {
                    if (tipo_producto.equalsIgnoreCase("P")){
                        vsql = "UPDATE P_STOCK SET CANT = CANT - " + item.cant;
                        vsql +=" WHERE (EMPRESA="+gl.emp+")  AND (CODIGO_PRODUCTO="+item.producto+") " +
                                " AND (UNIDADMEDIDA='"+item.unidadmedida+"')" + "AND (SUCURSAL='"+gl.tienda+"')";
                        CSQL=CSQL+ vsql;
                    }
                }

                if (gl.peInvCompart && tipo_producto.equalsIgnoreCase("P") ){
                    for (int r = 0; r <rutas.size(); r++) {
                        ucant=item.cant;if (tipomov.equalsIgnoreCase("D")) ucant=-ucant;
                        uruta=Integer.parseInt(rutas.get(r));

                        vsql=addUpdateItem(uruta,item.producto,ucant,item.unidadmedida);
                        CSQL=CSQL+ vsql;
                    }
                }

            }

        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusMov() {
        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {

                movErr=rs;
                movSend=true;

                return;

            } else {
                movSend=true;
            }

            mSend++;

            try {

                sql="UPDATE D_MOV SET STATCOM='S' WHERE COREL='"+corelMov+"'";
                db.execSQL(sql);

            } catch (SQLException e) {
                msgbox2(e.getMessage());
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processCajaCierre() {
        String cCjCierre;

        cjCierre.clear();

        //try {

            clsP_cajacierreObj P_cajacierreObj=new clsP_cajacierreObj(this,Con,db);
            P_cajacierreObj.fill("WHERE STATCOM='N'");

            CSQL="";

            for (int i = 0; i <P_cajacierreObj.count; i++) {

                cCjCierre=P_cajacierreObj.items.get(i).codigo_cajacierre;
                P_cajacierreObj.items.get(i).statcom="S";

                ss="DELETE FROM P_CAJACIERRE WHERE (CODIGO_CAJACIERRE='"+cCjCierre+"')";
                CSQL = CSQL + ss + ";";
                ss=P_cajacierreObj.addItemSqlFecha(P_cajacierreObj.items.get(i));
                CSQL = CSQL + ss + ";";

                cjCierre.add(""+cCjCierre);
            }

        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusCajaCierre() {
        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {

                cjCierreError=rs;
                cjCierreSendB=true;

                return;

            } else {
                cjCierreSendB=true;
            }

            cjCierreSend++;

            try {

                sql="UPDATE P_CAJACIERRE SET STATCOM='S' WHERE STATCOM='N'";
                db.execSQL(sql);

            } catch (SQLException e) {
                msgbox2(e.getMessage());
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processCajaPagos() {
        String cCjPago;

        cjPagos.clear();

        //try {
            clsP_cajapagosObj P_cajapagosObj=new clsP_cajapagosObj(this,Con,db);
            P_cajapagosObj.fill("WHERE STATCOM='N'");

            CSQL="";
            for (int i = 0; i <P_cajapagosObj.count; i++) {

                cCjPago=P_cajapagosObj.items.get(i).codigo_cajapagos;
                P_cajapagosObj.items.get(i).statcom="S";

                ss="DELETE FROM P_CAJAPAGOS WHERE (CODIGO_CAJAPAGOS='"+cCjPago+"')";
                CSQL = CSQL + ss + ";";
                ss=P_cajapagosObj.addItemSqlFecha(P_cajapagosObj.items.get(i));
                CSQL = CSQL + ss + ";";

                cjPagos.add(""+cCjPago);
            }
        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusCajaPagos() {
        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {

                cjPagosError=rs;
                cjPagosSendB=true;

                return;

            } else {
                cjPagosSendB=true;
            }

            cjPagosSend++;

            try {

                sql="UPDATE P_CAJAPAGOS SET STATCOM='S' WHERE STATCOM='N'";
                db.execSQL(sql);

            } catch (SQLException e) {
                msgbox2(e.getMessage());
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processCajaReporte() {

        String cCjReporte;

        cjReporte.clear();

        //try {
            clsP_cajareporteObj P_cajareporteObj=new clsP_cajareporteObj(this,Con,db);
            P_cajareporteObj.fill("WHERE STATCOM='N'");

            CSQL="";
            for (int i = 0; i <P_cajareporteObj.count; i++) {

                cCjReporte=P_cajareporteObj.items.get(i).codigo_cajareporte;
                P_cajareporteObj.items.get(i).statcom="S";

                ss="DELETE FROM P_CAJAREPORTE WHERE (CODIGO_CAJAPAGOS='"+cCjReporte+"')";
                CSQL = CSQL + ss + ";";
                ss=P_cajareporteObj.addItemSql(P_cajareporteObj.items.get(i));
                CSQL = CSQL + ss + ";";

                cjReporte.add(""+cCjReporte);
            }
        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusCajaReporte() {
        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {

                cjReporteError=rs;
                cjReporteSendB=true;

                return;

            } else {
                cjReporteSendB=true;
            }

            cjReporteSend++;

            try {

                sql="UPDATE P_CAJAREPORTE SET STATCOM='S' WHERE STATCOM='N'";
                db.execSQL(sql);

            } catch (SQLException e) {
                msgbox2(e.getMessage());
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    private void processStock() {
        String codStock;

        cjReporte.clear();

        //try {
            clsP_stockObj P_stockObj=new clsP_stockObj(this,Con,db);
            P_stockObj.fill("WHERE enviado=1");

            CSQL="";

            for (int i = 0; i <P_stockObj.count; i++) {

                P_stockObj.items.get(i).enviado=1;
                codStock = P_stockObj.items.get(i).documento;
                ss=P_stockObj.addItemSql(P_stockObj.items.get(i));
                CSQL = CSQL + ss + ";";

                cStock.add(""+codStock);
            }

        //} catch (Exception e) {
        //    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        //}
    }

    private void statusStock() {
        try {

            rs =(String) xobj.getSingle("CommitResult",String.class);

            if (!rs.equalsIgnoreCase("#")) {

                cStockError=rs;
                cStockSendB=true;

                return;

            } else {
                cStockSendB=true;
            }

            cStockSend++;

            try {

                sql="UPDATE P_STOCK SET ENVIADO=1 WHERE ENVIADO=0";
                db.execSQL(sql);

            } catch (SQLException e) {
                msgbox2(e.getMessage());
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void preparaEnvio() {

        ferr="";
        movErr="";
        int ccant;
        int total_enviar=0;

        try {

            rutas.clear();

            if (gl.peInvCompart) {
                clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
                P_rutaObj.fill("WHERE (SUCURSAL="+gl.tienda+") AND (CODIGO_RUTA<>"+gl.codigo_ruta+")");
                for (int i = 0; i <P_rutaObj.count; i++) rutas.add(""+P_rutaObj.items.get(i).codigo_ruta);
            }

            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);
            P_clienteObj.fill("WHERE ESERVICE='N'");
            ccant=P_clienteObj.count;
            total_enviar+=ccant;

            String idfel=gl.peFEL;

            if (app.usaFEL()) {
                D_facturaObj.fill("WHERE (STATCOM='N')  AND (FEELUUID<>' ') ");
            } else {
                D_facturaObj.fill("WHERE (STATCOM='N') ");
            }

            ftot=D_facturaObj.count;
            fsend=0;
            if (ftot>0) fidx=-1;else fidx=0;

            fact.clear();
            for (int i = 0; i <ftot; i++) {
                fact.add(D_facturaObj.items.get(i).corel);
            }
            total_enviar+=ftot;

            clsD_MovObj D_MovObj = new clsD_MovObj(this,Con,db);
            D_MovObj.fill("WHERE STATCOM = 'N'");
            fTotMov=D_MovObj.count;
            total_enviar+=fTotMov;

            mSend=0;

            fIdxMov=(fTotMov>0?-1:0);

            mov.clear();
            for (int i = 0; i <fTotMov; i++) {
                mov.add(D_MovObj.items.get(i).COREL);
            }

            clsP_cajacierreObj P_cjCierreObj=new clsP_cajacierreObj(this,Con,db);
            P_cjCierreObj.fill("WHERE STATCOM='N'");
            cjCierreTot=P_cjCierreObj.count;

            total_enviar+=cjCierreTot;

            clsP_cajapagosObj P_cjPagoObj=new clsP_cajapagosObj(this,Con,db);
            P_cjPagoObj.fill("WHERE STATCOM='N'");
            cjPagosTot=P_cjPagoObj.count;

            total_enviar+=cjPagosTot;

            clsP_cajareporteObj P_cjReporteObj=new clsP_cajareporteObj(this,Con,db);
            P_cjReporteObj.fill("WHERE STATCOM='N'");
            cjReporteTot=P_cjReporteObj.count;

            total_enviar+=cjReporteTot;

            clsP_stockObj P_cStockObj=new clsP_stockObj(this,Con,db);
            P_cStockObj.fill("WHERE ENVIADO=0");

            cStockTot=P_cStockObj.count;

            total_enviar+=cStockTot;

            if(total_enviar>0){

                lbl1.setText("Pendientes envio : \nFacturas: "+ftot+
                            "\nClientes: "+ccant+
                            "\nMovimientos inventario: "+fTotMov+
                            "\nCierres de caja: "+cjCierreTot+
                            "\nPagos de caja: "+cjPagosTot+
                            "\nReportes caja: "+cjReporteTot+
                            "\nStock: "+cStockTot);

            } else {
                msgboxwait("No hay datos pendientes de envío");
            }


        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateLabel() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable(){
                    public void run() {
                        lbl1.setText(plabel);
                    }
                });
            }
        };
        new Thread(runnable).start();
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

    private void msgboxwait(String msg) {
        try {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setCancelable(false);

            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();

        } catch (Exception ex) {
            toast(ex.getMessage());
        }

        /*
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
         */
    }

    //endregion

    //region Activity Events

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //endregion

}

