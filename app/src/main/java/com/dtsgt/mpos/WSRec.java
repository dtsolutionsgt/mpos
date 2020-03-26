package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_bonifObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_descuentoObj;
import com.dtsgt.classes.clsP_empresaObj;
import com.dtsgt.classes.clsP_factorconvObj;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_lineaObj;
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
import com.dtsgt.classesws.clsBeP_FACTORCONV;
import com.dtsgt.classesws.clsBeP_FACTORCONVList;
import com.dtsgt.classesws.clsBeP_IMPUESTO;
import com.dtsgt.classesws.clsBeP_IMPUESTOList;
import com.dtsgt.classesws.clsBeP_LINEA;
import com.dtsgt.classesws.clsBeP_LINEAList;

import java.util.ArrayList;

public class WSRec extends PBase {

    private TextView lbl1;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;
    private ArrayList<String> script= new ArrayList<String>();

    private String plabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsrec);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);lbl1.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);pbar.setVisibility(View.INVISIBLE);

        getURL();
        ws= new WebServiceHandler(WSRec.this, gl.wsurl);
        xobj= new XMLObject(ws);

    }

    //region Events

    public void doStart(View view) {
        script.clear();
        pbar.setVisibility(View.VISIBLE);
        execws(1);
    }

    //endregion

    //region WebService handler

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent,String Url) {
            super(Parent,Url);
        }

        @Override
        public void wsExecute(){
            try {
                switch (ws.callback) {
                    case 1:
                        callMethod("GetP_EMPRESA","EMPRESA",gl.emp);break;
                    case 2:
                        callMethod("GetP_BANCO","EMPRESA",gl.emp);break;
                    case 3:
                        callMethod("GetP_ARCHIVOCONF","EMPRESA",gl.emp,"RUTA",gl.ruta);break;
                    case 4:
                        callMethod("GetP_BONIF","EMPRESA",gl.emp);break;
                    case 5:
                        callMethod("GetP_COREL","EMPRESA",gl.emp);break;
                    case 6:
                        callMethod("GetP_DESCUENTO","EMPRESA",gl.emp);break;
                    case 7:
                        callMethod("GetP_FACTORCONV","EMPRESA",gl.emp);break;
                    case 8:
                        callMethod("GetP_IMPUESTO","EMPRESA",gl.emp);break;
                    case 9:
                        callMethod("GetP_LINEA","EMPRESA",gl.emp);break;
                    case 10:
                        callMethod("GetP_CLIENTE","EMPRESA",gl.emp);break;
                    }
            } catch (Exception e) {
                error=e.getMessage();errorflag=true;
            }
        }
    }

    @Override
    public void wsCallBack(Boolean throwing,String errmsg,int errlevel) {
        try {
            if (throwing) throw new Exception(errmsg);

            if (ws.errorflag) {
                processComplete();return;
            }

            switch (ws.callback) {
                case 1:
                    processEmpresas();execws(3);break;
                case 2:
                    /*processBancos();execws(3);*/break;
                case 3:
                    processConfig();execws(4);break;
                case 4:
                    processBonif();execws(5);break;
                case 5:
                    processCorel();execws(6);break;
                case 6:
                    processDescuento();execws(7);break;
                case 7:
                    processFactor();execws(8);break;
                case 8:
                    processImpuesto();execws(9);break;
                case 9:
                    processLinea();execws(10);break;

                case 10:
                    processCliente(); processComplete();break;
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
                plabel="Cargando empresas";break;
            case 2:
                plabel="Cargando bancos";break;
            case 3:
                plabel="Cargando Configuración";break;
            case 4:
                plabel="Cargando bonificaciones";break;
            case 5:
                plabel="Cargando correlativos";break;
            case 6:
                plabel="Cargando descuentos";break;
            case 7:
                plabel="Cargando factores";break;
            case 8:
                plabel="Cargando impuestos";break;
            case 9:
                plabel="Cargando familias";break;
            case 10:
                plabel="Cargando clientes";break;
        }

        updateLabel();

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                ws.callback=callbackvalue;
                ws.execute();
            }
        };
        mtimer.postDelayed(mrunner,200);
    }

    private void processComplete() {
        pbar.setVisibility(View.INVISIBLE);
        plabel="";updateLabel();

        if (ws.errorflag) {
            msgboxwait("Error :\n"+ws.error);
        } else {
            processData();
        }
    }

    private boolean processData() {

        try {
            db.beginTransaction();

            for (int i = 0; i <script.size(); i++) {
                sql=script.get(i);
                db.execSQL(sql);
                //msgbox(sql);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            msgboxwait("Recepción completa");

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox("DB Commit Error\n"+e.getMessage()+"\n"+sql);return false;
        }
    }

    //endregion

    //region Recepción

    private void processEmpresas() {
        try {
            clsBeP_EMPRESA item=new clsBeP_EMPRESA();
            clsClasses.clsP_empresa var=clsCls.new clsP_empresa();

            script.add("DELETE FROM P_EMPRESA");

            item=xobj.getresult(clsBeP_EMPRESA.class,"GetP_EMPRESA");

            var.empresa=item.EMPRESA;
            var.nombre=item.NOMBRE;
            var.col_imp=item.COL_IMP;

            clsP_empresaObj P_empresaObj=new clsP_empresaObj(this,Con,db);

            script.add(P_empresaObj.addItemSql(var));

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processBancos() {
        try {
            clsBeP_BANCOList items = new clsBeP_BANCOList();
            clsBeP_BANCO item=new clsBeP_BANCO();
            clsClasses.clsP_banco var=clsCls.new clsP_banco();

            script.add("DELETE FROM P_BANCO");

            items=xobj.getresult(clsBeP_BANCOList.class,"GetP_BANCO");
        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processConfig() {

        try {
            clsP_archivoconfObj handler=new clsP_archivoconfObj(this,Con,db);
            clsBeP_ARCHIVOCONFList items = new clsBeP_ARCHIVOCONFList();
            clsBeP_ARCHIVOCONF item=new clsBeP_ARCHIVOCONF();
            clsClasses.clsP_archivoconf var;

            script.add("DELETE FROM P_ARCHIVOCONF");

            items=xobj.getresult(clsBeP_ARCHIVOCONFList.class,"GetP_ARCHIVOCONF");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_archivoconf();

                var.ruta = item.RUTA;
                var.tipo_hh = item.TIPO_HH+"";
                var.idioma = item.IDIOMA+"";
                var.tipo_impresora = item.TIPO_IMPRESORA+"";
                var.serial_hh = item.SERIAL_HH+"";
                var.modif_peso =item.MODIF_PESO+"";
                var.puerto_impresion = item.PUERTO_IMPRESION+"";
                var.lbs_o_kgs = item.LBS_O_KGS+"";
                var.nota_credito =mu.bool(item.NOTA_CREDITO);

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processBonif() {
        try {
            clsP_bonifObj handler=new clsP_bonifObj(this,Con,db);
            clsBeP_BONIFList items = new clsBeP_BONIFList();
            clsBeP_BONIF item=new clsBeP_BONIF();
            clsClasses.clsP_bonif var;

            script.add("DELETE FROM P_BONIF");

            items=xobj.getresult(clsBeP_BONIFList.class,"GetP_BONIF");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_bonif();

                var.cliente=item.CLIENTE;
                var.ctipo=item.CTIPO;
                var.producto=item.PRODUCTO;
                var.ptipo=item.PTIPO;
                var.tiporuta=item.TIPORUTA;
                var.tipobon=item.TIPOBON;
                var.rangoini=item.RANGOINI;
                var.rangofin=item.RANGOFIN;
                var.tipolista=item.TIPOLISTA;
                var.tipocant=item.TIPOCANT;
                var.valor=item.VALOR;
                var.lista=item.LISTA;
                var.cantexact=item.CANTEXACT;
                var.globbon=item.GLOBBON;
                var.porcant=item.PORCANT;
                var.fechaini=item.FECHAINI;
                var.fechafin=item.FECHAFIN;
                var.coddesc=item.CODDESC;
                var.nombre=item.NOMBRE+"";
                var.emp=item.EMP;
                var.umproducto=item.UMPRODUCTO+"";
                var.umbonificacion=item.UMBONIFICACION+"";

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processCorel() {
        try {
            clsP_corelObj handler=new clsP_corelObj(this,Con,db);
            clsBeP_CORELList items = new clsBeP_CORELList();
            clsBeP_COREL item=new clsBeP_COREL();
            clsClasses.clsP_corel var;

            script.add("DELETE FROM P_COREL");

            items=xobj.getresult(clsBeP_CORELList.class,"GetP_COREL");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_corel();

                var.resol=item.RESOL;
                var.serie=item.ACTIVA;
                var.corelini=item.CORELINI;
                var.corelfin=item.CORELFIN;
                var.corelult=item.CORELULT;
                var.fechares=item.FECHARES;
                var.ruta=item.RUTA;
                var.fechavig=item.FECHAVIG;
                var.resguardo=item.RESGUARDO;
                var.valor1=item.VALOR1;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processDescuento() {
        try {
            clsP_descuentoObj handler=new clsP_descuentoObj(this,Con,db);
            clsBeP_DESCUENTOList items = new clsBeP_DESCUENTOList();
            clsBeP_DESCUENTO item=new clsBeP_DESCUENTO();
            clsClasses.clsP_descuento var;

            script.add("DELETE FROM P_DESCUENTO");

            items=xobj.getresult(clsBeP_DESCUENTOList.class,"GetP_DESCUENTO");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_descuento();

                var.cliente = item.CLIENTE;
                var.ctipo = item.CTIPO;
                var.producto = item.PRODUCTO;
                var.ptipo = item.PTIPO;
                var.tiporuta = item.TIPORUTA;
                var.rangoini = item.RANGOINI;
                var.rangofin = item.RANGOFIN;
                var.desctipo = item.DESCTIPO;
                var.valor = item.VALOR;
                var.globdesc = item.GLOBDESC;
                var.porcant = item.PORCANT;
                var.fechaini = item.FECHAINI;
                var.fechafin = item.FECHAFIN;
                var.coddesc = item.CODDESC;
                var.nombre = item.NOMBRE;
                var.activo = item.ACTIVO;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processFactor() {
        try {
            clsP_factorconvObj handler=new clsP_factorconvObj(this,Con,db);
            clsBeP_FACTORCONVList items = new clsBeP_FACTORCONVList();
            clsBeP_FACTORCONV item=new clsBeP_FACTORCONV();
            clsClasses.clsP_factorconv var;

            script.add("DELETE FROM P_FACTORCONV");

            items=xobj.getresult(clsBeP_FACTORCONVList.class,"GetP_FACTORCONV");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_factorconv();

                var.producto = item.PRODUCTO;
                var.unidadsuperior = item.UNIDADSUPERIOR;
                var.factorconversion = item.FACTORCONVERSION;
                var.unidadminima = item.UNIDADMINIMA;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processImpuesto() {
        try {
            clsP_impuestoObj handler=new clsP_impuestoObj(this,Con,db);
            clsBeP_IMPUESTOList items = new clsBeP_IMPUESTOList();
            clsBeP_IMPUESTO item=new clsBeP_IMPUESTO();
            clsClasses.clsP_impuesto var;

            script.add("DELETE FROM P_IMPUESTO");

            items=xobj.getresult(clsBeP_IMPUESTOList.class,"GetP_IMPUESTO");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_impuesto();

                var.codigo=item.CODIGO;
                var.valor=item.VALOR;
                var.activo=item.Activo;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processLinea() {
        try {
            clsP_lineaObj handler=new clsP_lineaObj(this,Con,db);
            clsBeP_LINEAList items = new clsBeP_LINEAList();
            clsBeP_LINEA item=new clsBeP_LINEA();
            clsClasses.clsP_linea var;

            script.add("DELETE FROM P_LINEA");

            items=xobj.getresult(clsBeP_LINEAList.class,"GetP_LINEA");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_linea();

                var.codigo=item.CODIGO;
                var.marca=item.MARCA;
                var.nombre=item.NOMBRE;
                var.activo=item.ACTIVO;
                var.codigo_linea=item.CODIGO_LINEA;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }

    private void processCliente() {
        try {
            clsP_clienteObj handler=new clsP_clienteObj(this,Con,db);
            clsBeP_CLIENTEList items = new clsBeP_CLIENTEList();
            clsBeP_CLIENTE item=new clsBeP_CLIENTE();
            clsClasses.clsP_cliente var;

            script.add("DELETE FROM P_CLIENTE");

            items=xobj.getresult(clsBeP_CLIENTEList.class,"GetP_CLIENTE");

            for (int i = 0; i <items.items.size(); i++) {

                item=items.items.get(i);

                var=clsCls.new clsP_cliente();

                var.codigo=item.CODIGO;
                var.nombre=item.NOMBRE;
                var.bloqueado=item.BLOQUEADO;
                var.nivelprecio=item.NIVELPRECIO;
                var.mediapago=item.MEDIAPAGO;
                var.limitecredito=item.LIMITECREDITO;
                var.diacredito=item.DIACREDITO;
                var.descuento=item.DESCUENTO;
                var.bonificacion=item.BONIFICACION;
                var.ultvisita=item.ULTVISITA;
                var.impspec=item.IMPSPEC;
                var.nit=item.NIT;
                var.email=item.EMAIL+"";
                var.eservice=item.ESERVICE+"";
                var.telefono=item.TELEFONO+"";
                var.direccion=item.DIRECCION;
                var.coorx=item.COORX;
                var.coory=item.COORY;
                var.bodega=item.BODEGA+"";;
                var.cod_pais=item.COD_PAIS+"";
                var.codbarra=item.CODBARRA+"";
                var.percepcion=item.PERCEPCION;
                var.tipo_contribuyente=item.TIPO_CONTRIBUYENTE+"";
                var.codigo_cliente=item.CODIGO_CLIENTE;

                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error=e.getMessage();ws.errorflag=true;
        }
    }


    //endregion

    //region Aux

    private void getURL() {
        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";

        /*
        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/tomws.txt");
            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                gl.wsurl = myReader.readLine();
                myReader.close();
            }

        } catch (Exception e) {
            gl.wsurl ="";
        }

        if (!gl.wsurl.isEmpty()) lblurl.setText(gl.wsurl);else lblurl.setText("Falta archivo con URL");

        */
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
