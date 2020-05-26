package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_bancoObj;
import com.dtsgt.classes.clsP_bonifObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_conceptopagoObj;
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
import com.dtsgt.classes.clsP_prodmenuopcObj;
import com.dtsgt.classes.clsP_prodmenuopcdetObj;
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
import com.dtsgt.classesws.clsBeP_CONCEPTOPAGO;
import com.dtsgt.classesws.clsBeP_CONCEPTOPAGOList;
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
import com.dtsgt.classesws.clsBeP_PRODMENUOPC;
import com.dtsgt.classesws.clsBeP_PRODMENUOPCList;
import com.dtsgt.classesws.clsBeP_PRODMENUOPC_DET;
import com.dtsgt.classesws.clsBeP_PRODMENUOPC_DETList;
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WSRec extends PBase {

    private TextView lbl1, lbl2;
    private TextView lblTitulo;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;
    private ArrayList<String> script = new ArrayList<String>();
    private boolean pbd_vacia = false;
    private String plabel, fechasync;
    private String rootdir = Environment.getExternalStorageDirectory() + "/mPosFotos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsrec);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);
        lbl1.setText("");
        lblTitulo = (TextView) findViewById(R.id.lblTit6);
        lbl2 = (TextView) findViewById(R.id.textView150);
        lbl2.setText("");

        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        getURL();

        ws = new WebServiceHandler(WSRec.this, gl.wsurl);
        xobj = new XMLObject(ws);

        long fs = app.getDateRecep();
        if (fs > 0) fs = du.addDays(fs, -1);
        fechasync = "" + fs;

        pbd_vacia = getIntent().getBooleanExtra("bd_vacia", false);

        if (pbd_vacia) {
            lblTitulo.setText("B.D. Vacía");
            lbl2.setText("Conectado a: " + gl.wsurl);
        }
    }

    public void doStart(View view) {
        script.clear();
        pbar.setVisibility(View.VISIBLE);
        execws(1);
    }

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent, String Url) {
            super(Parent, Url);
        }

        @Override
        public void wsExecute() {
            try {
                switch (ws.callback) {
                    case 1:
                        callMethod("GetP_EMPRESA", "EMPRESA", gl.emp);
                        break;
                    case 2:
                        callMethod("GetP_BANCO", "EMPRESA", gl.emp);
                        break;
                    case 3:
                        callMethod("GetP_ARCHIVOCONF", "EMPRESA", gl.emp, "RUTA", gl.ruta);
                        break;
                    case 4:
                        //callMethod("GetP_BONIF", "EMPRESA", gl.emp);
                        callEmptyMethod();
                        break;
                    case 5:
                        callMethod("GetP_COREL", "EMPRESA", gl.emp);
                        break;
                    case 6:
                        //callMethod("GetP_DESCUENTO", "EMPRESA", gl.emp);
                        callEmptyMethod();
                        break;
                    case 7:
                        callMethod("GetP_FACTORCONV", "EMPRESA", gl.emp);
                        break;
                    case 8:
                        callMethod("GetP_IMPUESTO", "EMPRESA", gl.emp);
                        break;
                    case 9:
                        callMethod("GetP_LINEA", "EMPRESA", gl.emp);
                        break;
                    case 10:
                        callMethod("GetP_CLIENTE", "EMPRESA", gl.emp, "FECHA", fechasync);
                        //callEmptyMethod();
                        break;
                    case 11:
                        callMethod("GetP_ENCABEZADO_REPORTESHH", "EMPRESA", gl.emp);
                        break;
                    case 12:
                        callMethod("GetP_MEDIAPAGO", "EMPRESA", gl.emp);
                        break;
                    case 13:
                        callMethod("GetP_MONEDA", "EMPRESA", gl.emp);
                        break;
                    case 14:
                        callMethod("GetP_NIVELPRECIO", "EMPRESA", gl.emp);
                        break;
                    case 15:
                        callMethod("GetP_PRODCOMBO", "EMPRESA", gl.emp);
                        break;
                    case 16:
                        callMethod("GetP_PRODMENU", "EMPRESA", gl.emp);
                        break;
                    case 17:
                        callMethod("GetP_PRODPRECIO", "EMPRESA", gl.emp);
                        break;
                    case 18:
                        callMethod("GetP_PRODMENUOPC", "EMPRESA", gl.emp);
                        break;
                    case 20:
                        callMethod("GetP_PROVEEDOR", "EMPRESA", gl.emp);
                        break;
                    case 21:
                        callMethod("GetP_PRODUCTO", "EMPRESA", gl.emp);
                        break;
                    case 22:
                        callMethod("GetP_RUTA", "EMPRESA", gl.emp);
                        break;
                    case 23:
                        callMethod("GetP_SUCURSAL", "EMPRESA", gl.emp);
                        break;
                    case 24:
                        callMethod("GetP_USGRUPO", "EMPRESA", gl.emp);
                        break;
                    case 25:
                        callMethod("GetP_USGRUPOOPC", "EMPRESA", gl.emp);
                        break;
                    case 26:
                        callMethod("GetP_USOPCION", "EMPRESA", gl.emp);
                        break;
                    case 27:
                        callMethod("GetVENDEDORES", "EMPRESA", gl.emp);
                        break;
                    case 28:
                        callMethod("GetCONCEPTOPAGO", "EMPRESA", gl.emp);
                        break;
                    case 28:
                        callMethod("GetP_PRODMENUOPC_DET", "EMPRESA", gl.emp);
                        break;
                }
            } catch (Exception e) {
                error = e.getMessage();
                errorflag = true;
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
                    processEmpresas();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(3);
                    break;
                case 3:
                    processConfig();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(4);
                    break;
                case 4:
                    //processBonif();if (ws.errorflag) { processComplete();break;}
                    execws(5);
                    break;
                case 5:
                    processCorel();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(6);
                    break;
                case 6:
                    //processDescuento();if (ws.errorflag) { processComplete();break;}
                    execws(7);
                    break;
                case 7:
                    processFactor();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(8);
                    break;
                case 8:
                    processImpuesto();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(9);
                    break;
                case 9:
                    processLinea();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(10);
                    break;
                case 10:
                    processCliente();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(11);
                    break;
                case 11:
                    processEncabezado();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(12);
                    break;
                case 12:
                    processMedia();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(13);
                    break;
                case 13:
                    processMoneda();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(14);
                    break;
                case 14:
                    processNivel();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(15);
                    break;
                case 15:
                    processCombo();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(16);
                    break;
                case 16:
                    processProdMenu();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(17);
                    break;
                case 17:
                    processPrecios();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(18);
                    break;
                case 18:
                    processOpciones();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(28);
                    break;
                case 28:
                    processOpcionesdet();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(20);
                    break;
                case 20:
                    processProveedores();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(21);
                    break;
                case 21:
                    processProductos();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(22);
                    break;
                case 22:
                    processRutas();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(23);
                    break;
                case 23:
                    processSucursales();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(24);
                    break;
                case 24:
                    processUsrGrupos();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(25);
                    break;
                case 25:
                    processUsrGrOpc();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(26);
                    break;
                case 26:
                    processGrOpciones();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(27);
                    break;
                case 27:
                    processVendedores();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    execws(28);
                    break;
                case 28:
                    processConceptoPago();
                    if (ws.errorflag) {
                        processComplete();
                        break;
                    }
                    processComplete();
                    break;
            }

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    private void execws(int callbackvalue) {

        switch (callbackvalue) {
            case 1:
                plabel = "Cargando empresas";
                break;
            case 2:
                plabel = "Cargando bancos";
                break;
            case 3:
                plabel = "Cargando Configuración";
                break;
            case 4:
                plabel = "Cargando bonificaciones";
                break;
            case 5:
                plabel = "Cargando correlativos";
                break;
            case 6:
                plabel = "Cargando descuentos";
                break;
            case 7:
                plabel = "Cargando factores";
                break;
            case 8:
                plabel = "Cargando impuestos";
                break;
            case 9:
                plabel = "Cargando familias";
                break;
            case 10:
                plabel = "Cargando clientes";
                break;
            case 11:
                plabel = "Cargando encabezados";
                break;
            case 12:
                plabel = "Cargando medias pago ";
                break;
            case 13:
                plabel = "Cargando monedas";
                break;
            case 14:
                plabel = "Cargando niveles precio";
                break;
            case 15:
                plabel = "Cargando combos";
                break;
            case 16:
                plabel = "Cargando menus";
                break;
            case 17:
                plabel = "Cargando precios";
                break;
            case 18:
                plabel = "Cargando opciones";
                break;
            case 19:
                plabel = "Cargando opciones";
                break;
            case 20:
                plabel = "Cargando proveedores";
                break;
            case 21:
                plabel = "Cargando productos";
                break;
            case 22:
                plabel = "Cargando cajas";
                break;
            case 23:
                plabel = "Cargando tienda";
                break;
            case 24:
                plabel = "Cargando permisos";
                break;
            case 25:
                plabel = "Cargando opciones";
                break;
            case 26:
                plabel = "Cargando opciones";
                break;
            case 27:
                plabel = "Cargando usuarios";
                break;
            case 28:
                plabel = "Cargando opciones det";
                break;
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
            processData();
            browse = 1;
        }
    }

    private boolean processData() {
        try {

            db.beginTransaction();

            for (int i = 0; i < script.size(); i++) {
                sql = script.get(i);
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            app.setDateRecep(du.getActDate());

            msgboxwait("Recepción completa");

            return true;

        } catch (Exception e) {
            db.endTransaction();
            msgbox("DB Commit Error\n" + e.getMessage() + "\n" + sql);
            return false;
        }
    }

    private void processEmpresas() {

        try {

            clsBeP_EMPRESA item = new clsBeP_EMPRESA();
            clsClasses.clsP_empresa var = clsCls.new clsP_empresa();

            script.add("DELETE FROM P_EMPRESA");

            item = xobj.getresult(clsBeP_EMPRESA.class, "GetP_EMPRESA");
            var.empresa = item.EMPRESA;
            var.nombre = item.NOMBRE;
            var.col_imp = item.COL_IMP;
            var.logo = item.LOGO+"";
            var.razon_social = item.RAZON_SOCIAL + "";
            var.identificacion_tributaria = item.IDENTIFICACION_TRIBUTARIA + "";
            var.telefono = item.TELEFONO + "";
            var.cod_pais = item.COD_PAIS + "";
            var.nombre_contacto = item.NOMBRE_CONTACTO + "";
            var.apellido_contacto = item.APELLIDO_CONTACTO + "";
            var.direccion = item.DIRECCION + "";
            var.correo = item.CORREO + "";
            var.codigo_activacion = item.CODIGO_ACTIVACION + "";
            var.cod_cant_emp = item.COD_CANT_EMP;
            var.cantidad_puntos_venta = item.CANTIDAD_PUNTOS_VENTA;
            clsP_empresaObj P_empresaObj = new clsP_empresaObj(this, Con, db);
            script.add(P_empresaObj.addItemSql(var));

            try {

                String img = var.logo;

                if (img != null) {

                    //#EJC20200518: Check if the folder for picturs exists, if not create!
                    File f = new File(rootdir);
                    if(!f.isDirectory()) f.mkdir();

                    String filePathImg = rootdir + "mposlogo.png";

                    File file = new File(filePathImg);

                    if (!file.exists()) {

                        byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                        int bs = imgbytes.length;

                        FileOutputStream fos = new FileOutputStream(filePathImg);
                        BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                        outputStream.write(imgbytes);
                        outputStream.close();
                    }
                }

                } catch (Exception ee)
                {
                    Log.e("ImgOp", ee.getMessage());
                }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processBancos() {

        try {

            clsP_bancoObj handler = new clsP_bancoObj(this, Con, db);
            clsBeP_BANCOList items = new clsBeP_BANCOList();
            clsBeP_BANCO item = new clsBeP_BANCO();
            clsClasses.clsP_banco var = clsCls.new clsP_banco();

            script.add("DELETE FROM P_BANCO");

            items = xobj.getresult(clsBeP_BANCOList.class, "GetP_BANCO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_banco();
                var.codigo_banco = item.getCODIGO_BANCO();
                var.codigo = item.getCODIGO() + "";
                var.activo = item.getActivo();
                var.cuenta = item.getCUENTA() + "";
                var.empresa = item.getEMPRESA();
                var.nombre = item.getNOMBRE() + "";
                var.tipo = item.getTIPO() + "";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processConfig() {

        try {

            clsP_archivoconfObj handler = new clsP_archivoconfObj(this, Con, db);
            clsBeP_ARCHIVOCONFList items = new clsBeP_ARCHIVOCONFList();
            clsBeP_ARCHIVOCONF item = new clsBeP_ARCHIVOCONF();
            clsClasses.clsP_archivoconf var;

            script.add("DELETE FROM P_ARCHIVOCONF");

            items = xobj.getresult(clsBeP_ARCHIVOCONFList.class, "GetP_ARCHIVOCONF");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_archivoconf();
                var.ruta = item.RUTA;
                var.tipo_hh = item.TIPO_HH + "";
                var.idioma = item.IDIOMA + "";
                var.tipo_impresora = item.TIPO_IMPRESORA + "";
                var.serial_hh = item.SERIAL_HH + "";
                var.modif_peso = item.MODIF_PESO + "";
                var.puerto_impresion = item.PUERTO_IMPRESION + "";
                var.lbs_o_kgs = item.LBS_O_KGS + "";
                var.nota_credito = mu.bool(item.NOTA_CREDITO);
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processBonif() {

        try {

            clsP_bonifObj handler = new clsP_bonifObj(this, Con, db);
            clsBeP_BONIFList items = new clsBeP_BONIFList();
            clsBeP_BONIF item = new clsBeP_BONIF();
            clsClasses.clsP_bonif var;

            script.add("DELETE FROM P_BONIF");

            items = xobj.getresult(clsBeP_BONIFList.class, "GetP_BONIF");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_bonif();
                var.cliente = item.CLIENTE;
                var.ctipo = item.CTIPO;
                var.producto = item.PRODUCTO;
                var.ptipo = item.PTIPO;
                var.tiporuta = item.TIPORUTA;
                var.tipobon = item.TIPOBON;
                var.rangoini = item.RANGOINI;
                var.rangofin = item.RANGOFIN;
                var.tipolista = item.TIPOLISTA;
                var.tipocant = item.TIPOCANT;
                var.valor = item.VALOR;
                var.lista = item.LISTA;
                var.cantexact = item.CANTEXACT;
                var.globbon = item.GLOBBON;
                var.porcant = item.PORCANT;
                var.fechaini = item.FECHAINI;
                var.fechafin = item.FECHAFIN;
                var.coddesc = item.CODDESC;
                var.nombre = item.NOMBRE + "";
                var.emp = item.EMP;
                var.umproducto = item.UMPRODUCTO + "";
                var.umbonificacion = item.UMBONIFICACION + "";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processCorel() {

        try {
            clsP_corelObj handler = new clsP_corelObj(this, Con, db);
            clsBeP_CORELList items = new clsBeP_CORELList();
            clsBeP_COREL item = new clsBeP_COREL();
            clsClasses.clsP_corel var;

            script.add("DELETE FROM P_COREL");

            items = xobj.getresult(clsBeP_CORELList.class, "GetP_COREL");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_corel();
                var.resol = item.RESOL;
                var.serie = item.SERIE;
                var.corelini = item.CORELINI;
                var.corelfin = item.CORELFIN;
                var.corelult = item.CORELULT;
                var.fechares = item.FECHARES;
                var.ruta = item.RUTA;
                var.fechavig = item.FECHAVIG;
                var.resguardo = item.RESGUARDO;
                var.valor1 = item.VALOR1;
                var.activa = item.ACTIVA;
                var.codigo_corel=item.CODIGO_COREL;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processDescuento() {
        try {
            clsP_descuentoObj handler = new clsP_descuentoObj(this, Con, db);
            clsBeP_DESCUENTOList items = new clsBeP_DESCUENTOList();
            clsBeP_DESCUENTO item = new clsBeP_DESCUENTO();
            clsClasses.clsP_descuento var;

            script.add("DELETE FROM P_DESCUENTO");

            items = xobj.getresult(clsBeP_DESCUENTOList.class, "GetP_DESCUENTO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_descuento();
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
                var.codigo_descuento = item.CODIGO_DESCUENTO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processFactor() {
        try {
            clsP_factorconvObj handler = new clsP_factorconvObj(this, Con, db);
            clsBeP_FACTORCONVList items = new clsBeP_FACTORCONVList();
            clsBeP_FACTORCONV item = new clsBeP_FACTORCONV();
            clsClasses.clsP_factorconv var;

            script.add("DELETE FROM P_FACTORCONV");

            items = xobj.getresult(clsBeP_FACTORCONVList.class, "GetP_FACTORCONV");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_factorconv();
                var.producto = item.PRODUCTO;
                var.unidadsuperior = item.UNIDADSUPERIOR;
                var.factorconversion = item.FACTORCONVERSION;
                var.unidadminima = item.UNIDADMINIMA;
                var.codigo_factorconv = item.CODIGO_FACTORCONV;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processImpuesto() {
        try {
            clsP_impuestoObj handler = new clsP_impuestoObj(this, Con, db);
            clsBeP_IMPUESTOList items = new clsBeP_IMPUESTOList();
            clsBeP_IMPUESTO item = new clsBeP_IMPUESTO();
            clsClasses.clsP_impuesto var;

            script.add("DELETE FROM P_IMPUESTO");

            items = xobj.getresult(clsBeP_IMPUESTOList.class, "GetP_IMPUESTO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_impuesto();
                var.codigo = item.CODIGO;
                var.valor = item.VALOR;
                var.activo = item.ACTIVO;
                var.codigo_impuesto = item.CODIGO_IMPUESTO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processLinea() {

        String dirfamilia = rootdir + "/familia/";

        try {

            clsP_lineaObj handler = new clsP_lineaObj(this, Con, db);
            clsBeP_LINEAList items = new clsBeP_LINEAList();
            clsBeP_LINEA item = new clsBeP_LINEA();
            clsClasses.clsP_linea var;

            script.add("DELETE FROM P_LINEA");

            items = xobj.getresult(clsBeP_LINEAList.class, "GetP_LINEA");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);

                var = clsCls.new clsP_linea();
                var.codigo_linea = item.CODIGO_LINEA;
                var.codigo = item.CODIGO;
                var.marca = item.MARCA;
                var.nombre = item.NOMBRE;
                var.activo = item.ACTIVO;
                var.imagen = item.IMAGEN;

                script.add(handler.addItemSql(var));

                try {

                    String img = var.imagen;

                    if (img != null) {

                        if (i==0){
                            File f = new File(dirfamilia);
                            if(!f.isDirectory()) f.mkdir();
                        }

                        String filePathImg = dirfamilia + var.codigo + ".png";
                        File file = new File(filePathImg);

                        if (!file.exists()) {
                            byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                            int bs = imgbytes.length;

                            FileOutputStream fos = new FileOutputStream(filePathImg);
                            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                            outputStream.write(imgbytes);
                            outputStream.close();
                        }
                    }

                } catch (Exception ee) {
                    Log.e("ImgOp", ee.getMessage());
                }
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processCliente() {

        try {

            clsP_clienteObj handler = new clsP_clienteObj(this, Con, db);
            clsBeP_CLIENTEList items = new clsBeP_CLIENTEList();
            clsBeP_CLIENTE item = new clsBeP_CLIENTE();
            clsClasses.clsP_cliente var;

            items = xobj.getresult(clsBeP_CLIENTEList.class, "GetP_CLIENTE");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_cliente();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.bloqueado = item.BLOQUEADO;
                var.nivelprecio = item.NIVELPRECIO;
                var.mediapago = item.MEDIAPAGO;
                var.limitecredito = item.LIMITECREDITO;
                var.diacredito = item.DIACREDITO;
                var.descuento = item.DESCUENTO;
                var.bonificacion = item.BONIFICACION;
                var.ultvisita = item.ULTVISITA;
                var.impspec = item.IMPSPEC;
                var.nit = item.NIT;
                var.email = item.EMAIL + "";
                var.eservice = "S";
                var.telefono = item.TELEFONO + "";
                var.direccion = item.DIRECCION + "";
                var.coorx = item.COORX;
                var.coory = item.COORY;
                var.bodega = item.BODEGA + "";
                var.cod_pais = item.COD_PAIS + "";
                var.codbarra = item.CODBARRA + "";
                var.percepcion = item.PERCEPCION;
                var.tipo_contribuyente = item.TIPO_CONTRIBUYENTE + "";
                var.codigo_cliente = item.CODIGO_CLIENTE;
                var.imagen = item.IMAGEN;
                ss = handler.addItemSql(var);

                script.add("DELETE FROM P_CLIENTE WHERE CODIGO_CLIENTE=" + var.codigo_cliente);
                script.add(handler.addItemSql(var));

                try {

                    String img = var.imagen;

                    if (img != null) {
                        String filePathImg = rootdir + var.codigo + ".jpg";
                        File file = new File(filePathImg);

                        if (!file.exists()) {
                            byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                            int bs = imgbytes.length;

                            FileOutputStream fos = new FileOutputStream(filePathImg);
                            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                            outputStream.write(imgbytes);
                            outputStream.close();
                            imgbytes = null;
                        }
                    }
                } catch (Exception ee) {
                    Log.e("ImgOp", ee.getMessage());
                }
            }
        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processEncabezado() {
        try {

            clsP_encabezado_reporteshhObj handler = new clsP_encabezado_reporteshhObj(this, Con, db);
            clsBeP_ENCABEZADO_REPORTESHHList items = new clsBeP_ENCABEZADO_REPORTESHHList();
            clsBeP_ENCABEZADO_REPORTESHH item = new clsBeP_ENCABEZADO_REPORTESHH();
            clsClasses.clsP_encabezado_reporteshh var;

            script.add("DELETE FROM P_ENCABEZADO_REPORTESHH");

            items = xobj.getresult(clsBeP_ENCABEZADO_REPORTESHHList.class, "GetP_ENCABEZADO_REPORTESHH");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_encabezado_reporteshh();
                var.codigo = item.CODIGO;
                var.texto = item.TEXTO + "";
                var.sucursal = item.SUCURSAL;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processMedia() {
        try {
            clsP_mediapagoObj handler = new clsP_mediapagoObj(this, Con, db);
            clsBeP_MEDIAPAGOList items = new clsBeP_MEDIAPAGOList();
            clsBeP_MEDIAPAGO item = new clsBeP_MEDIAPAGO();
            clsClasses.clsP_mediapago var;

            script.add("DELETE FROM P_MEDIAPAGO");

            items = xobj.getresult(clsBeP_MEDIAPAGOList.class, "GetP_MEDIAPAGO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_mediapago();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                if (item.ACTIVO) var.activo = "S";
                else var.activo = "N";
                var.nivel = item.NIVEL;
                var.porcobro = item.PORCOBRO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processMoneda() {
        try {

            clsP_monedaObj handler = new clsP_monedaObj(this, Con, db);
            clsBeP_MONEDAList items = new clsBeP_MONEDAList();
            clsBeP_MONEDA item = new clsBeP_MONEDA();
            clsClasses.clsP_moneda var;

            script.add("DELETE FROM P_MONEDA");

            items = xobj.getresult(clsBeP_MONEDAList.class, "GetP_MONEDA");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_moneda();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.activo = item.ACTIVO;
                var.symbolo = item.SYMBOLO;
                var.cambio = item.CAMBIO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processNivel() {
        try {
            clsP_nivelprecioObj handler = new clsP_nivelprecioObj(this, Con, db);
            clsBeP_NIVELPRECIOList items = new clsBeP_NIVELPRECIOList();
            clsBeP_NIVELPRECIO item = new clsBeP_NIVELPRECIO();
            clsClasses.clsP_nivelprecio var;

            script.add("DELETE FROM P_NIVELPRECIO");

            items = xobj.getresult(clsBeP_NIVELPRECIOList.class, "GetP_NIVELPRECIO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_nivelprecio();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.activo = item.ACTIVO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processCombo() {

        try {

            clsP_prodcomboObj handler = new clsP_prodcomboObj(this, Con, db);
            clsBeP_PRODCOMBOList items = new clsBeP_PRODCOMBOList();
            clsBeP_PRODCOMBO item = new clsBeP_PRODCOMBO();
            clsClasses.clsP_prodcombo var;

            script.add("DELETE FROM P_PRODCOMBO");

            items = xobj.getresult(clsBeP_PRODCOMBOList.class, "GetP_PRODCOMBO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_prodcombo();
                var.codigo = item.CODIGO;
                var.producto = item.PRODUCTO;
                var.tipo = item.TIPO;
                var.cantmin = item.CANTMIN;
                var.canttot = item.CANTTOT;
                var.codigo_combo = item.CODIGO_COMBO;
                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processProdMenu() {

        try {

            clsP_prodmenuObj handler = new clsP_prodmenuObj(this, Con, db);
            clsBeP_PRODMENUList items = new clsBeP_PRODMENUList();
            clsBeP_PRODMENU item = new clsBeP_PRODMENU();
            clsClasses.clsP_prodmenu var;

            script.add("DELETE FROM P_PRODMENU");

            items = xobj.getresult(clsBeP_PRODMENUList.class, "GetP_PRODMENU");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_prodmenu();
                var.codigo_menu=item.CODIGO_MENU;
                var.empresa=item.EMPRESA;
                var.codigo_producto=item.CODIGO_PRODUCTO;
                var.nombre=item.NOMBRE;
                var.nota=item.NOTA+"";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processOpciones() {

        try {

            clsP_prodmenuopcObj handler = new clsP_prodmenuopcObj(this, Con, db);
            clsBeP_PRODMENUOPCList items = new clsBeP_PRODMENUOPCList();
            clsBeP_PRODMENUOPC item = new clsBeP_PRODMENUOPC();
            clsClasses.clsP_prodmenuopc var;

            script.add("DELETE FROM P_PRODMENUOPC");

            items = xobj.getresult(clsBeP_PRODMENUOPCList.class, "GetP_PRODMENUOPC");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_prodmenuopc();
                var.codigo_menu_opcion=item.CODIGO_MENU_OPCION;
                var.codigo_menu =item.CODIGO_MENU;
                var.nombre=item.NOMBRE;
                var.cant =item.CANT;
                var.orden =item.ORDEN;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processOpcionesdet() {

        try {

            clsP_prodmenuopcdetObj handler = new clsP_prodmenuopcdetObj(this, Con, db);
            clsBeP_PRODMENUOPC_DETList items = new clsBeP_PRODMENUOPC_DETList();
            clsBeP_PRODMENUOPC_DET item = new clsBeP_PRODMENUOPC_DET();
            clsClasses.clsp_prodmenuopc_det var;

            script.add("DELETE FROM P_PRODMENUOPC_DET");

            items = xobj.getresult(clsBeP_PRODMENUOPC_DETList.class, "GetP_PRODMENUOPC_DET");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsp_prodmenuopc_det();
                var.codigo_menuopc_det=item.CODIGO_MENUOPC_DET;
                var.codigo_menu_opcion=item.CODIGO_MENU_OPCION;
                var.codigo_producto=item.CODIGO_PRODUCTO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processPrecios() {

        try {

            clsP_prodprecioObj handler = new clsP_prodprecioObj(this, Con, db);
            clsBeP_PRODPRECIOList items = new clsBeP_PRODPRECIOList();
            clsBeP_PRODPRECIO item = new clsBeP_PRODPRECIO();
            clsClasses.clsP_prodprecio var;

            script.add("DELETE FROM P_PRODPRECIO");

            items = xobj.getresult(clsBeP_PRODPRECIOList.class, "GetP_PRODPRECIO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_prodprecio();
                var.codigo_precio=item.CODIGO_PRECIO;
                var.codigo_producto = item.CODIGO_PRODUCTO;
                var.empresa=item.EMPRESA;
                var.nivel = item.NIVEL;
                var.precio = item.PRECIO;
                var.unidadmedida = item.UNIDADMEDIDA;
                script.add(handler.addItemSql(var));

            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processProveedores() {

        try {

            clsP_proveedorObj handler = new clsP_proveedorObj(this, Con, db);
            clsBeP_PROVEEDORList items = new clsBeP_PROVEEDORList();
            clsBeP_PROVEEDOR item = new clsBeP_PROVEEDOR();
            clsClasses.clsP_proveedor var;

            script.add("DELETE FROM P_PROVEEDOR");

            items = xobj.getresult(clsBeP_PROVEEDORList.class, "GetP_PROVEEDOR");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_proveedor();
                var.codigo_proveedor = item.CODIGO_PROVEEDOR;
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.activo = item.ACTIVO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processProductos() {

        try {

            clsP_productoObj handler = new clsP_productoObj(this, Con, db);
            clsBeP_PRODUCTOList items = new clsBeP_PRODUCTOList();
            clsBeP_PRODUCTO item = new clsBeP_PRODUCTO();
            clsClasses.clsP_producto var;

            script.add("DELETE FROM P_PRODUCTO");

            items = xobj.getresult(clsBeP_PRODUCTOList.class, "GetP_PRODUCTO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_producto();
                var.codigo_producto = item.CODIGO_PRODUCTO;
                var.codigo = item.CODIGO;
                var.codigo_tipo = item.CODIGO_TIPO;
                var.linea = item.LINEA;
                var.empresa = item.EMPRESA;
                var.marca = item.MARCA;
                var.codbarra = item.CODBARRA + "";
                var.desccorta = item.DESCCORTA;
                var.desclarga = item.DESCLARGA;
                var.costo = item.COSTO;
                var.factorconv = item.FACTORCONV;
                var.unidbas = item.UNIDBAS + "";
                var.unidmed = item.UNIDMED + "";
                var.unimedfact = item.UNIMEDFACT;
                var.unigra = item.UNIGRA + "";
                var.unigrafact = item.UNIGRAFACT;
                var.descuento = item.DESCUENTO;
                var.bonificacion = item.BONIFICACION;
                var.imp1 = item.IMP1;
                var.imp2 = item.IMP2;
                var.imp3 = item.IMP3;
                var.vencomp = item.VENCOMP + "";
                var.devol = item.DEVOL;
                var.ofrecer = item.OFRECER;
                var.rentab = item.RENTAB;
                var.descmax = item.DESCMAX;
                var.iva = item.IVA;
                var.codbarra2 = item.CODBARRA2 + "";
                var.cbconv = item.CBCONV;
                var.bodega = item.BODEGA + "";
                var.subbodega = item.SUBBODEGA + "";
                var.peso_promedio = item.PESO_PROMEDIO;
                var.modif_precio = mu.bool(item.MODIF_PRECIO);
                var.imagen = item.IMAGEN + "";
                var.video = item.VIDEO + "";
                var.venta_por_peso = mu.bool(item.VENTA_POR_PESO);
                var.es_prod_barra = mu.bool(item.ES_PROD_BARRA);
                var.unid_inv = item.UNID_INV + "";
                var.venta_por_paquete = mu.bool(item.VENTA_POR_PAQUETE);
                var.venta_por_factor_conv = mu.bool(item.VENTA_POR_FACTOR_CONV);
                var.es_serializado = mu.bool(item.ES_SERIALIZADO);
                var.param_caducidad = item.PARAM_CADUCIDAD;
                var.producto_padre = item.PRODUCTO_PADRE + "";
                var.factor_padre = item.FACTOR_PADRE;
                var.tiene_inv = mu.bool(item.TIENE_INV);
                var.tiene_vineta_o_tubo = mu.bool(item.TIENE_VINETA_O_TUBO);
                var.precio_vineta_o_tubo = item.PRECIO_VINETA_O_TUBO;
                var.es_vendible = mu.bool(item.ES_VENDIBLE);
                var.unigrasap = item.UNIGRASAP;
                var.um_salida = item.UM_SALIDA + "";
                var.activo = 1;
                script.add(handler.addItemSql(var));


                try {

                    String img = var.imagen;

                    if (img != null) {

                        String filePathImg = rootdir + var.codigo + ".jpg";
                        File file = new File(filePathImg);

                        if (!file.exists()) {
                            byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                            int bs = imgbytes.length;

                            FileOutputStream fos = new FileOutputStream(filePathImg);
                            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                            outputStream.write(imgbytes);
                            outputStream.close();
                            imgbytes = null;

                        }

                    }

                } catch (Exception ee) {
                    Log.e("ImgOp", ee.getMessage());
                }

            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processRutas() {

        try {

            clsP_rutaObj handler = new clsP_rutaObj(this, Con, db);
            clsBeP_RUTAList items = new clsBeP_RUTAList();
            clsBeP_RUTA item = new clsBeP_RUTA();
            clsClasses.clsP_ruta var;

            script.add("DELETE FROM P_RUTA");

            items = xobj.getresult(clsBeP_RUTAList.class, "GetP_RUTA");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_ruta();
                var.codigo = item.CODIGO;
                var.sucursal = "" + item.SUCURSAL;
                var.nombre = item.NOMBRE;
                var.codigo_ruta = item.CODIGO_RUTA;
                var.activo = item.ACTIVO;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processSucursales() {

        try {

            clsP_sucursalObj handler = new clsP_sucursalObj(this, Con, db);
            clsBeP_SUCURSALList items = new clsBeP_SUCURSALList();
            clsBeP_SUCURSAL item = new clsBeP_SUCURSAL();
            clsClasses.clsP_sucursal var;

            script.add("DELETE FROM P_SUCURSAL");

            items = xobj.getresult(clsBeP_SUCURSALList.class, "GetP_SUCURSAL");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_sucursal();
                var.codigo_sucursal = item.CODIGO_SUCURSAL;
                var.codigo = item.CODIGO;
                var.empresa = item.EMPRESA;
                var.codigo_nivel_precio = item.CODIGO_NIVEL_PRECIO;
                var.descripcion = item.DESCRIPCION;
                var.nombre = item.NOMBRE;
                var.direccion = item.DIRECCION;
                var.telefono = item.TELEFONO;
                var.nit = item.NIT;
                var.texto = item.TEXTO;
                var.activo = mu.bool(item.ACTIVO);
                var.pet_prefijo=item.PET_PREFIJO;
                var.pet_llave=item.PET_LLAVE;
                var.pet_alias_pfx=item.PET_ALIAS_PFX;
                var.pet_pfx_llave=item.PET_PFX_LLAVE;
                var.codigo_escenario_isr=item.CODIGO_ESCENARIO_ISR;
                var.codigo_escenario_iva=item.CODIGO_ESCENARIO_IVA;
                var.codigo_municipio=item.CODIGO_MUNICIPIO;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processUsrGrupos() {

        try {

            clsP_usgrupoObj handler = new clsP_usgrupoObj(this, Con, db);
            clsBeP_USGRUPOList items = new clsBeP_USGRUPOList();
            clsBeP_USGRUPO item = new clsBeP_USGRUPO();
            clsClasses.clsP_usgrupo var;

            script.add("DELETE FROM P_USGRUPO");

            items = xobj.getresult(clsBeP_USGRUPOList.class, "GetP_USGRUPO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_usgrupo();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.cuenta = item.CUENTA;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processUsrGrOpc() {

        try {

            clsP_usgrupoopcObj handler = new clsP_usgrupoopcObj(this, Con, db);
            clsBeP_USGRUPOOPCList items = new clsBeP_USGRUPOOPCList();
            clsBeP_USGRUPOOPC item = new clsBeP_USGRUPOOPC();
            clsClasses.clsP_usgrupoopc var;

            script.add("DELETE FROM P_USGRUPOOPC");

            items = xobj.getresult(clsBeP_USGRUPOOPCList.class, "GetP_USGRUPOOPC");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_usgrupoopc();
                var.grupo = item.GRUPO ;
                var.opcion = item.OPCION;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }

    }

    private void processGrOpciones() {

        try {

            clsP_usopcionObj handler = new clsP_usopcionObj(this, Con, db);
            clsBeP_USOPCIONList items = new clsBeP_USOPCIONList();
            clsBeP_USOPCION item = new clsBeP_USOPCION();
            clsClasses.clsP_usopcion var;

            script.add("DELETE FROM P_USOPCION");

            items = xobj.getresult(clsBeP_USOPCIONList.class, "GetP_USOPCION");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_usopcion();
                var.codigo = item.CODIGO;
                var.menugroup = item.MENUGROUP;
                var.nombre = item.NOMBRE;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processVendedores() {

        try {

            clsVendedoresObj handler = new clsVendedoresObj(this, Con, db);
            clsBeVENDEDORESList items = new clsBeVENDEDORESList();
            clsBeVENDEDORES item = new clsBeVENDEDORES();
            clsClasses.clsVendedores var;

            script.add("DELETE FROM VENDEDORES");

            items = xobj.getresult(clsBeVENDEDORESList.class, "GetVENDEDORES");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsVendedores();
                var.codigo = item.CODIGO;
                var.ruta = item.RUTA + "";
                var.nombre = item.NOMBRE;
                var.clave = item.CLAVE + "";
                var.nivel = item.NIVEL;
                var.nivelprecio = item.NIVELPRECIO;
                var.bodega = item.BODEGA + "";
                var.subbodega = item.SUBBODEGA + "";
                var.activo = mu.bool(item.ACTIVO);
                var.codigo_vendedor = item.CODIGO_VENDEDOR;
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processConceptoPago() {
        try {
            clsP_conceptopagoObj handler = new clsP_conceptopagoObj(this, Con, db);
            clsBeP_CONCEPTOPAGOList items = new clsBeP_CONCEPTOPAGOList();
            clsBeP_CONCEPTOPAGO item = new clsBeP_CONCEPTOPAGO();
            clsClasses.clsP_conceptopago var;

            script.add("DELETE FROM P_CONCEPTOPAGO");

            items = xobj.getresult(clsBeP_CONCEPTOPAGOList.class, "GetCONCEPTOPAGO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_conceptopago();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.activo = mu.bool(item.ACTIVO);
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
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
            gl.wsurl = "";
        }

        if (!gl.wsurl.isEmpty()) lbl2.setText(gl.wsurl);
        else lbl2.setText("Falta archivo con URL");

    }

    private void updateLabel() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (plabel != null)
                            lbl1.setText(plabel);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        try {

            super.onResume();

            if (browse == 1) {
                browse = 0;
                finish();
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }
}