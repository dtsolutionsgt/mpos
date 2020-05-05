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
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsDataBuilder;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_bonifObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_descuentoObj;
import com.dtsgt.classes.clsP_empresaObj;
import com.dtsgt.classes.clsP_encabezado_reporteshhObj;
import com.dtsgt.classes.clsP_factorconvObj;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_monedaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_prodcomboObj;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodopcObj;
import com.dtsgt.classes.clsP_prodopclistObj;
import com.dtsgt.classes.clsP_prodprecioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsP_usgrupoObj;
import com.dtsgt.classes.clsP_usgrupoopcObj;
import com.dtsgt.classes.clsP_usopcionObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classesws.clsBeP_ARCHIVOCONF;
import com.dtsgt.classesws.clsBeP_ARCHIVOCONFList;
import com.dtsgt.classesws.clsBeP_BANCO;
import com.dtsgt.classesws.clsBeP_BANCOList;
import com.dtsgt.classesws.clsBeP_BONIF;
import com.dtsgt.classesws.clsBeP_BONIFList;
import com.dtsgt.classesws.clsBeP_CLIENTE;
import com.dtsgt.classesws.clsBeP_CLIENTEList;
import com.dtsgt.classesws.clsBeP_COREL;
import com.dtsgt.classesws.clsBeP_CORELList;
import com.dtsgt.classesws.clsBeP_DESCUENTO;
import com.dtsgt.classesws.clsBeP_DESCUENTOList;
import com.dtsgt.classesws.clsBeP_EMPRESA;
import com.dtsgt.classesws.clsBeP_ENCABEZADO_REPORTESHH;
import com.dtsgt.classesws.clsBeP_ENCABEZADO_REPORTESHHList;
import com.dtsgt.classesws.clsBeP_FACTORCONV;
import com.dtsgt.classesws.clsBeP_FACTORCONVList;
import com.dtsgt.classesws.clsBeP_IMPUESTO;
import com.dtsgt.classesws.clsBeP_IMPUESTOList;
import com.dtsgt.classesws.clsBeP_LINEA;
import com.dtsgt.classesws.clsBeP_LINEAList;
import com.dtsgt.classesws.clsBeP_MEDIAPAGO;
import com.dtsgt.classesws.clsBeP_MEDIAPAGOList;
import com.dtsgt.classesws.clsBeP_MONEDA;
import com.dtsgt.classesws.clsBeP_MONEDAList;
import com.dtsgt.classesws.clsBeP_NIVELPRECIO;
import com.dtsgt.classesws.clsBeP_NIVELPRECIOList;
import com.dtsgt.classesws.clsBeP_PRODCOMBO;
import com.dtsgt.classesws.clsBeP_PRODCOMBOList;
import com.dtsgt.classesws.clsBeP_PRODMENU;
import com.dtsgt.classesws.clsBeP_PRODMENUList;
import com.dtsgt.classesws.clsBeP_PRODOPC;
import com.dtsgt.classesws.clsBeP_PRODOPCLIST;
import com.dtsgt.classesws.clsBeP_PRODOPCLISTList;
import com.dtsgt.classesws.clsBeP_PRODOPCListx;
import com.dtsgt.classesws.clsBeP_PRODPRECIO;
import com.dtsgt.classesws.clsBeP_PRODPRECIOList;
import com.dtsgt.classesws.clsBeP_PRODUCTO;
import com.dtsgt.classesws.clsBeP_PRODUCTOList;
import com.dtsgt.classesws.clsBeP_PROVEEDOR;
import com.dtsgt.classesws.clsBeP_PROVEEDORList;
import com.dtsgt.classesws.clsBeP_RUTA;
import com.dtsgt.classesws.clsBeP_RUTAList;
import com.dtsgt.classesws.clsBeP_SUCURSAL;
import com.dtsgt.classesws.clsBeP_SUCURSALList;
import com.dtsgt.classesws.clsBeP_USGRUPO;
import com.dtsgt.classesws.clsBeP_USGRUPOList;
import com.dtsgt.classesws.clsBeP_USGRUPOOPC;
import com.dtsgt.classesws.clsBeP_USGRUPOOPCList;
import com.dtsgt.classesws.clsBeP_USOPCION;
import com.dtsgt.classesws.clsBeP_USOPCIONList;
import com.dtsgt.classesws.clsBeVENDEDORES;
import com.dtsgt.classesws.clsBeVENDEDORESList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WSEnv extends PBase {

    private TextView lbl1,lbl2;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturapObj D_facturapObj;

    private ArrayList<String> clients = new ArrayList<String>();
    private ArrayList<String> fact = new ArrayList<String>();

    private String CSQL,plabel,rs,corel,ferr,idfact;
    private int ftot,fsend,fidx;
    private boolean factsend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_s_env);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);lbl1.setText("");
        lbl2 = (TextView) findViewById(R.id.textView151);lbl2.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        getURL();
        ws = new WebServiceHandler(WSEnv.this, gl.wsurl);
        xobj = new XMLObject(ws);

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);

    }


    //region Events

    public void doStart(View view) {
        prepareSend();
        pbar.setVisibility(View.VISIBLE);
        execws(1);
    }

    //endregion

    //region WebService handler

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent, String Url) {
            super(Parent, Url);
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
                        if (ftot>0) {
                            callMethod("Commit", "SQL", CSQL);
                        }
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
                    statusFactura();
                    if (fidx>=ftot) {
                        //processComplete();
                    } else {
                        //execws(2);
                    }
                    processComplete();
                    break;
        }

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    //endregion

    //region Main

    private void execws(int callbackvalue) {

        switch (callbackvalue) {
            case 1:
                plabel = "Enviando Clientes";break;
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
        pbar.setVisibility(View.INVISIBLE);
        plabel = "";
        updateLabel();

        if (ws.errorflag) {
            msgboxwait(ws.error);
        } else {
            ss ="Envío completo\n";
            ss+="Facturas total : "+ftot+"\n";
            ss+="Facturas sin envio : "+(ftot-fsend);
            msgboxwait(ss);
            if (!ferr.isEmpty()) msgbox("Factura : "+idfact+"\n"+ferr);
        }
    }

    //endregion

    //region Envío

    private void processFactura() {
        fidx++;corel=fact.get(fidx);
        factsend=false;

        clients.clear();

        try {
            D_facturaObj.fill("WHERE COREL='"+corel+"'");
            D_facturadObj.fill("WHERE COREL='"+corel+"'");
            D_facturapObj.fill("WHERE COREL='"+corel+"'");

            idfact=D_facturaObj.first().serie+"-"+D_facturaObj.first().corelativo;

            CSQL=addFactheader(D_facturaObj.first())+ "*";

            for (int i = 0; i <D_facturadObj.count; i++) {
                CSQL=CSQL +D_facturadObj.addItemSql(D_facturadObj.items.get(i)) + "*";
            }

            for (int i = 0; i <D_facturapObj.count; i++) {
                CSQL=CSQL +D_facturapObj.addItemSql(D_facturapObj.items.get(i)) + "*";
            }

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

            try {
                sql="UPDATE D_Factura SET STATCOM='S' WHERE COREL='"+corel+"'";
                db.execSQL(sql);
            } catch (SQLException e) {
            }

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void processClients() {
        int ccli;

        clients.clear();

        try {
            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);
            P_clienteObj.fill("WHERE ESERVICE='N'");

            CSQL="";
            for (int i = 0; i <P_clienteObj.count; i++) {

                ccli=P_clienteObj.items.get(i).codigo_cliente;
                P_clienteObj.items.get(i).eservice="S";

                ss="DELETE FROM P_CLIENTE WHERE (Empresa="+gl.emp+") AND (CODIGO_CLIENTE="+ccli+")";
                CSQL = CSQL + ss + "*";
                ss=P_clienteObj.addItemSql(P_clienteObj.items.get(i),Integer.parseInt(gl.emp));
                CSQL = CSQL + ss + "*";

                clients.add(""+ccli);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
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
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void prepareSend() {
        ferr="";

        try {
            D_facturaObj.fill("WHERE STATCOM='N'");
            ftot=D_facturaObj.count;
            fsend=0;
            if (ftot>0) fidx=-1;else fidx=0;

            fact.clear();
            for (int i = 0; i <ftot; i++) {
                fact.add(D_facturaObj.items.get(i).corel);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void getURL() {
        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");
            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                gl.wsurl = myReader.readLine();
                myReader.close();
            }

        } catch (Exception e) {
            gl.wsurl ="";
        }

        if (!gl.wsurl.isEmpty()) lbl2.setText(gl.wsurl);else lbl2.setText("Falta archivo con URL");

    }

    private void updateLabel() {
        Handler handler = new Handler();
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

    //endregion

    //region Dialogs

    private void msgboxwait(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Recepción");
        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //endregion

}

