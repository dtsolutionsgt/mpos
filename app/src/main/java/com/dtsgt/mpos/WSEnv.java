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
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsP_clienteObj;

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

    private clsD_MovObj D_MovObj;
    private clsD_MovDObj D_MovDObj;

    private ArrayList<String> clients = new ArrayList<String>();
    private ArrayList<String> fact = new ArrayList<String>();
    private ArrayList<String> mov = new ArrayList<String>();

    private String CSQL,plabel,rs,corel,ferr,idfact, movErr, corelMov, idMov;
    private int ftot,fsend,fidx, fTotMov,fIdxMov, mSend;
    private boolean factsend, movSend;

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

        D_MovObj=new clsD_MovObj(this,Con,db);
        D_MovDObj=new clsD_MovDObj(this,Con,db);

        prepareSend();
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
                    case 3:
                        processMov();
                        if (fTotMov>0) {
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
                    if (fidx>=ftot-1) {
                        processComplete();
                    } else {
                        execws(2);
                    }
                    processComplete();
                   // execws(3);
                    break;
                case 3:
                    statusMov();
                    if (fIdxMov>=fTotMov-1) {
                        processCompleteMov();
                    } else {
                        execws(3);
                    }
                    processCompleteMov();
                    break;
        }

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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
    private void processCompleteMov() {
        pbar.setVisibility(View.INVISIBLE);
        plabel = "";
        updateLabel();

        if (ws.errorflag) {
            msgboxwait(ws.error);
        } else {
            ss ="Envío completo\n";
            ss+="Movimientos total : "+fTotMov+"\n";
            ss+="Movimientos sin envio : "+(fTotMov-mSend);
            msgboxwait(ss);
            if (!movErr.isEmpty()) msgbox("Movimientos : "+idMov+"\n"+movErr);
        }
    }
    //endregion

    //region Envío

    private void processFactura() {

        if (ftot==0) {
            fidx++;return;
        }

        fidx++;
        corel=fact.get(fidx);
        factsend=false;

        clients.clear();

        try {

            D_facturaObj.fill("WHERE COREL='"+corel+"'");
            D_facturadObj.fill("WHERE COREL='"+corel+"'");
            D_facturapObj.fill("WHERE COREL='"+corel+"'");

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
                CSQL = CSQL + ss + "\n";
                ss=P_clienteObj.addItemSql(P_clienteObj.items.get(i),gl.emp);
                CSQL = CSQL + ss + "\n";

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

    private void processMov() {

        if (fTotMov==0) {
            fIdxMov++;
            return;
        }

        fIdxMov++;
        corelMov=mov.get(fIdxMov);
        movSend=false;

        mov.clear();

        try {

            D_MovObj.fill("WHERE COREL='"+corelMov+"'");
            D_MovDObj.fill("WHERE COREL='"+corelMov+"'");

            idMov=D_MovObj.first().COREL;

            CSQL="DELETE FROM D_MOV WHERE COREL='"+corelMov+"';";
            CSQL=CSQL+"DELETE FROM D_MOVD WHERE COREL='"+corelMov+"';";

            CSQL=CSQL+addMovheader(D_MovObj.first())+ ";";

            for (int i = 0; i <D_MovDObj.count; i++) {
                CSQL=CSQL+D_MovDObj.addItemSql(D_MovDObj.items.get(i)) + ";";
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public String addMovheader(clsClasses.clsD_Mov item) {

        ins.init("D_MOV");

        ins.add("COREL",item.COREL);
        ins.add("RUTA",item.RUTA);
        ins.add("ANULADO",item.ANULADO);
        ins.add("FECHA",item.FECHA);
        ins.add("TIPO",item.TIPO);
        ins.add("USUARIO",item.USUARIO);
        ins.add("REFERENCIA",item.REFERENCIA);
        ins.add("STATCOM",item.STATCOM);
        ins.add("IMPRES",item.IMPRES);
        ins.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);

        return ins.sql();

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
                msgbox(e.getMessage());
            }

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void prepareSend() {
        ferr="";
        int ccant;

        try {

            clsP_clienteObj P_clienteObj=new clsP_clienteObj(this,Con,db);
            P_clienteObj.fill("WHERE ESERVICE='N'");ccant=P_clienteObj.count;

            D_facturaObj.fill("WHERE STATCOM='N'");
            ftot=D_facturaObj.count;
            fsend=0;
            if (ftot>0) fidx=-1;else fidx=0;

            fact.clear();
            for (int i = 0; i <ftot; i++) {
                fact.add(D_facturaObj.items.get(i).corel);
            }

            lbl1.setText("Pendientes envio : \nFacturas : "+ftot+"\nClientes : "+ccant);

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

