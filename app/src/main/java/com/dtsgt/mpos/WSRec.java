package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsP_Producto_TipoObj;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_bancoObj;
import com.dtsgt.classes.clsP_barril_barraObj;
import com.dtsgt.classes.clsP_barril_tipoObj;
import com.dtsgt.classes.clsP_bonifObj;
import com.dtsgt.classes.clsP_caja_impresoraObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_conceptopagoObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_cortesiaObj;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_descuentoObj;
import com.dtsgt.classes.clsP_empresaObj;
import com.dtsgt.classes.clsP_encabezado_reporteshhObj;
import com.dtsgt.classes.clsP_factorconvObj;
import com.dtsgt.classes.clsP_fraseObj;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_impresora_marcaObj;
import com.dtsgt.classes.clsP_impresora_modeloObj;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_linea_impresoraObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_modificadorObj;
import com.dtsgt.classes.clsP_modificador_grupoObj;
import com.dtsgt.classes.clsP_monedaObj;
import com.dtsgt.classes.clsP_motivoajusteObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_nivelprecio_sucursalObj;
import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.classes.clsP_prodclasifmodifObj;
import com.dtsgt.classes.clsP_prodcomboObj;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodmenuopcObj;
import com.dtsgt.classes.clsP_prodmenuopcdetObj;
import com.dtsgt.classes.clsP_prodprecioObj;
import com.dtsgt.classes.clsP_prodrecetaObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_regla_costoObj;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_salaObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsP_tiponegObj;
import com.dtsgt.classes.clsP_unidadObj;
import com.dtsgt.classes.clsP_unidad_convObj;
import com.dtsgt.classes.clsP_usgrupoObj;
import com.dtsgt.classes.clsP_usgrupoopcObj;
import com.dtsgt.classes.clsP_usopcionObj;
import com.dtsgt.classes.clsP_vendedor_rolObj;
import com.dtsgt.classes.clsP_vendedor_sucursalObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classesws.*;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class WSRec extends PBase {

    private TextView lbl1, lbl2, lblIdDispositivo;
    private TextView lblTitulo,cmdRecibir;
    private EditText txtEmpresa, txtClave, txtURLWS;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;
    private ArrayList<String> script = new ArrayList<String>();
    private boolean pbd_vacia = false,nueva_version=false;
    private String plabel, fechasync;
    private String rootdir = Environment.getExternalStorageDirectory() + "/mPosFotos/";

    private String idversion,clave;

    public boolean automatico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsrec);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);
        lbl1.setText("");
        lblTitulo = (TextView) findViewById(R.id.lblTit6);

        txtClave = (EditText) findViewById(R.id.txtClave);
        txtEmpresa = (EditText) findViewById(R.id.txtEmpresa);
        txtURLWS = (EditText) findViewById(R.id.txtURLWS);

        cmdRecibir = (TextView) findViewById(R.id.textView45);

        lblIdDispositivo = (TextView) findViewById(R.id.lblIdDispositivo);

        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        app.getURL();
        app.parametrosExtra();
        setHandlers();

        ws = new WebServiceHandler(WSRec.this, gl.wsurl, gl.timeout);
        xobj = new XMLObject(ws);

        long fs = app.getDateRecep();
        if (fs > 0) fs = du.addDays(fs, -1);

        pbd_vacia = getIntent().getBooleanExtra("bd_vacia", false);

        if (pbd_vacia) fs=2001010000;
        if (fs==0) fs=2001010000;

        fechasync = "" + fs;

        lblIdDispositivo.setText("ID - " + gl.deviceId);

        if (pbd_vacia) {

            lblTitulo.setText("B.D. Vacía");

            txtURLWS.setEnabled(true);
            txtClave.setEnabled(true);
            txtEmpresa.setEnabled(true);

            txtURLWS.setText(gl.wsurl);if (gl.wsurl.isEmpty()) txtURLWS.setText("http://192.168.0.12/mposws/mposws.asmx");
            txtClave.setText("");
            txtEmpresa.setText("");

            showkeyb();
            txtEmpresa.requestFocus();

        } else {

            txtURLWS.setEnabled(false);
            txtClave.setEnabled(false);
            txtEmpresa.setEnabled(false);

            txtURLWS.setText(gl.wsurl);
            txtClave.setText(gl.clave);
            txtEmpresa.setText(String.valueOf(gl.emp));
        }

        if (gl.recibir_automatico){
            cmdRecibir.setVisibility(View.INVISIBLE);
            browse=2;
        }else{
            cmdRecibir.setVisibility(View.VISIBLE);
        }

        //if (gl.emp==2) gl.peRest=false;

    }

    //region  Events

    public void doStart(View view) {
        Recibir();
    }

    //endregion

    //region Web Service

    public class WebServiceHandler extends com.dtsgt.classes.WebService {

        public WebServiceHandler(PBase Parent, String Url, int TimeOut) {
            super(Parent, Url, TimeOut);
        }

        @Override
        public void wsExecute() {
            try {
                switch (ws.callback) {
                    case 1:
                        callMethod("GetP_EMPRESA", "EMPRESA", gl.emp, "CLAVE", gl.clave);
                        break;
                    case 2:
                        callMethod("GetP_BANCO", "EMPRESA", gl.emp);
                        break;
                    case 3:
                         callMethod("GetP_ARCHIVOCONF", "EMPRESA", gl.emp, "RUTA", gl.codigo_ruta);
                         break;
                    case 4:
                        //callMethod("GetP_BONIF", "EMPRESA", gl.emp);
                        callEmptyMethod();
                        break;
                    case 5:
                        callMethod("GetP_COREL", "EMPRESA", gl.emp);
                        break;
                    case 6:
                        callMethod("GetP_DESCUENTO", "EMPRESA", gl.emp);
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
                        if (gl.Sincronizar_Clientes){
                            callMethod("GetP_CLIENTE", "EMPRESA", gl.emp, "FECHA", fechasync);
                        }
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
                        callMethod("GetP_PRODMENUOPC_DET", "EMPRESA", gl.emp);
                        break;
                    case 29:
                        callMethod("GetP_CONCEPTOPAGO", "EMPRESA", gl.emp);
                        break;
                    case 30:
                        callMethod("GetP_PARAMEXT", "EMPRESA", gl.emp);
                        break;
                    case 31:
                        callMethod("GetP_BANCO", "EMPRESA", gl.emp);
                        break;
                    case 32:
                        callMethod("GetP_MOTIVO_AJUSTE", "EMPRESA", gl.emp);
                        break;
                    case 33:
                        callMethod("GetP_DEPARTAMENTO");
                        break;
                    case 34:
                        callMethod("GetP_MUNICIPIO");
                        break;
                    case 35:
                        callMethod("GetP_PRODUCTO_TIPO");
                        break;
                    case 36:
                        callMethod("GetP_FRASE");
                        break;
                    case 37:
                        if (gl.peRest) {
                            callMethod("GetP_RES_SALA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        } else
                            callEmptyMethod();
                        break;
                    case 38:
                        if (gl.peRest) {
                            callMethod("GetP_RES_MESA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        } else callEmptyMethod();
                        break;
                    case 39:
                        if (gl.peRest) {
                            callMethod("GetP_RES_GRUPO", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        } else callEmptyMethod();
                        break;
                    case 40:
                        //if (gl.peRest) {
                            callMethod("GetP_LINEA_IMPRESORA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        //} else callEmptyMethod();
                        break;
                    case 41:
                        //if (gl.peRest) {
                            callMethod("GetP_IMPRESORA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        //} else callEmptyMethod();
                        break;
                    case 42:
                        //if (gl.peRest) {
                            callMethod("GetP_IMPRESORA_MARCA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        //} else callEmptyMethod();
                        break;
                    case 43:
                        //if (gl.peRest) {
                            callMethod("GetP_IMPRESORA_MODELO", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        // else callEmptyMethod();
                        break;
                    case 44:
                        callMethod("getIns", "SQL","SELECT * FROM P_EMPRESA_VERSION WHERE Empresa="+gl.emp);
                        break;
                    case 45:
                        if (gl.peRest) {
                            callMethod("GetP_CAJA_IMPRESORA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        } else callEmptyMethod();
                        break;
                    case 46:
                        callMethod("GetP_UNIDAD", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 47:
                        callMethod("GetP_UNIDAD_CONV", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                         break;
                    case 48:
                        callMethod("GetP_PRODRECETA", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 49:
                        callMethod("GetP_NIVELPRECIO_SUCURSAL","SUCURSAL",gl.tienda);
                        break;
                    case 50:
                        callMethod("GetP_PARAMEXT_RUTA","RUTA", gl.codigo_ruta);
                        break;
                    case 51:
                        callMethod("GetP_REGLA_COSTO", "EMPRESA", gl.emp);
                        break;
                    case 52:
                        callMethod("GetP_ALMACEN","SUCURSAL",gl.tienda);
                        break;
                    case 53:
                        callMethod("GetP_MODIFICADOR", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 54:
                        callMethod("GetP_MODIFICADOR_GRUPO", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 55:
                        callMethod("GetP_PRODCLASIFMODIF", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 56:
                        callMethod("GetP_BARRIL_TIPO", "EMPRESA", gl.emp);
                        break;
                    case 57:
                        callMethod("GetP_BARRIL_BARRA", "EMPRESA", gl.emp);
                        break;
                    case 58:
                        callMethod("GetP_CORTESIA", "EMPRESA", gl.emp);
                        break;
                    case 59:
                        callMethod("GetP_VENDEDOR_ROL", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 60:
                        callMethod("GetP_VENDEDOR_SUCURSAL", "EMPRESA", gl.emp,"SUCURSAL",gl.tienda);
                        break;
                    case 61:
                        callMethod("GetP_TIPO_NEGOCIO", "EMPRESA", gl.emp);
                        break;
                }
            } catch (Exception e) {
                error=e.getMessage();errorflag=true;
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
                        processComplete();break;
                    }
                    execws(3);
                    break;
                case 3:
                    processConfig();
                    execws(4);break;
                case 4:
                    //processBonif();if (ws.errorflag) { processComplete();break;}
                    execws(5);break;
                case 5:
                    processCorel();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(6);break;
                case 6:
                    processDescuento();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(7);break;
                case 7:
                    processFactor();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(8);break;
                case 8:
                    processImpuesto();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(9);break;
                case 9:
                    processLinea();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(10);break;
                case 10:
                    if (gl.Sincronizar_Clientes) {
                        processCliente();
                    }
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(11);break;
                case 11:
                    processEncabezado();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(12);break;
                case 12:
                    processMedia();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(13);break;
                case 13:
                    processMoneda();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(14);break;
                case 14:
                    processNivel();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(15);break;
                case 15:
                    processCombo();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(16);break;
                case 16:
                    processProdMenu();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(17);break;
                case 17:
                    processPrecios();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(18);break;
                case 18:
                    processOpciones();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(28);break;
                case 28:
                    processOpcionesdet();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(20);break;
                case 20:
                    processProveedores();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(21);break;
                case 21:
                    processProductos();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(22);break;
                case 22:
                    processRutas();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(23);break;
                case 23:
                    processSucursales();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(24);break;
                case 24:
                    processUsrGrupos();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(25);break;
                case 25:
                    processUsrGrOpc();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(26);break;
                case 26:
                    processGrOpciones();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(27);break;
                case 27:
                    processVendedores();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                   execws(29);break;
                case 29:
                    processConceptoPago();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(30);
                    break;
                case 30:
                    processParametrosExtra();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(31);break;
                case 31:
                    processBancos();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(32);break;
                case 32:
                    processMotivoAjustes();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(33);break;
                case 33:
                    processDepartamentos();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(34);break;
                case 34:
                    processMunicipios();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(36);break;
                case 35:
                    processProductoTipo();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(36);break;
                case 36:
                    processFrases();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(37);break;
                case 37:
                    processResSala();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(38);break;
                case 38:
                    processResMesa();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(39);break;
                case 39:
                    processResGrupo();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(40);break;
                case 40:
                    processLineaImpresora();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(41);break;
                case 41:
                    processImpresora();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(42);break;
                case 42:
                    processMarcaImpresora();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(43);break;
                case 43:
                    processModeloImpresora();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(44);break;
                case 44:
                    processVersion();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(45);break;
                case 45:
                    processCajaImpresora();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(46);break;
                case 46:
                    processUnidad();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(47);break;
                case 47:
                    processUnidadConv();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(48);break;
                case 48:
                    processProdReceta();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(49);break;
                case 49:
                    processNivelSucursal();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(50);break;
                case 50:
                    processParametrosExtraRuta();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(51);break;
                case 51:
                    processReglaCosto();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(52);break;
                case 52:
                    processAlmacen();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(53);
                    break;
                case 53:
                    processModificador();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(54);break;
                case 54:
                    processModificadorGrupo();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(55);break;
                case 55:
                    processProdClasifModif();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(56);
                    break;
                case 56:
                    processBarrilTipo();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(57);
                    break;
                case 57:
                    processBarrilBarra();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(58);
                    break;
                case 58:
                    processCortesia();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(59);
                    break;
                case 59:
                    processVendedorRol();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(60);
                case 60:
                    processVendedorSucursal();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    execws(61);
                case 61:
                    processTipoNegocio();
                    if (ws.errorflag) {
                        processComplete();break;
                    }
                    processComplete();
            }
        } catch (Exception e) {
            msgbox2(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            processComplete();
        }
    }

    private void execws(int callbackvalue) {

        switch (callbackvalue) {
            case 1:
                plabel = "Cargando empresas";break;
            case 2:
                plabel = "Cargando bancos";break;
            case 3:
                plabel = "Cargando Configuración";break;
            case 4:
                plabel = "Cargando bonificaciones";break;
            case 5:
                plabel = "Cargando correlativos";break;
            case 6:
                plabel = "Cargando descuentos";break;
            case 7:
                plabel = "Cargando factores";break;
            case 8:
                plabel = "Cargando impuestos";break;
            case 9:
                plabel = "Cargando familias";break;
            case 10:
                plabel = "Cargando clientes";break;
            case 11:
                plabel = "Cargando encabezados";break;
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
            case 29:
                plabel = "Cargando conceptos de pago";
                break;
            case 30:
                plabel = "Cargando parámetros extras";
                break;
            case 31:
                plabel = "Cargando bancos";
                break;
            case 32:
                plabel = "Cargando motivos de ajuste";
                break;
            case 33:
                plabel = "Cargando departamentos";
                break;
            case 34:
                plabel = "Cargando municipios";
                break;
            case 35:
                plabel = "Cargando tipos de producto";
                break;
            case 36:
                plabel = "Frases";
                break;
            case 37:
                plabel = "Salas";
                break;
            case 38:
                plabel = "Mesas";
                break;
            case 39:
                plabel = "Grupos de mesas";
                break;
            case 40:
                plabel = "Impresora por familia";
                break;
            case 41:
                plabel = "Impresoras";
                break;
            case 42:
                plabel = "Marcas de impresora";break;
            case 43:
                plabel = "Modelo de impresora";break;
            case 44:
                plabel = "Version";break;
            case 45:
                plabel = "Impresoras";break;
            case 46:
                plabel = "Unidades";break;
            case 47:
                plabel = "Conversion";break;
            case 48:
                plabel = "Recetas";break;
            case 49:
                plabel = "NIVELPRECIO SUCURSAL";break;
            case 50:
                plabel = "PARAMEXT RUTA";break;
            case 51:
                plabel = "REGLA COSTO";break;
            case 52:
                plabel = "ALMACEN";break;
            case 53:
                plabel = "MODIFICADOR";break;
            case 54:
                plabel = "MODIFICADOR GRUPO";break;
            case 55:
                plabel = "PRODUCTO MODIFICADOR";break;
            case 56:
                plabel = "BARRIL TIPO";break;
            case 57:
                plabel = "BARRIL BARRA";break;
            case 58:
                plabel = "CORTESIA";break;
            case 59:
                plabel = "VENDEDOR ROL";break;
            case 60:
                plabel = "VENDEDOR SUCURSAL";break;
            case 61:
                plabel = "TIPO NEGOCIO";break;
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

    private void setHandlers() {

        try{

            txtEmpresa.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
                            (event.getAction() == KeyEvent.ACTION_DOWN)) {

                        gl.emp = Integer.valueOf(txtEmpresa.getText().toString());

                        showkeyb();

                        txtClave.requestFocus();

                        return true;
                    } else {
                        return false;
                    }
                }
            });

            txtClave.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
                            (event.getAction() == KeyEvent.ACTION_DOWN)) {

                        if (txtClave.getText().toString().isEmpty()){

                            msgbox("Debe ingresar la clave para recibir los datos");

                            showkeyb();

                            txtClave.requestFocus();

                        }else{

                            clave = txtClave.getText().toString();

                            showkeyb();

                            txtURLWS.requestFocus();

                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });

            txtClave.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {

                    if(!hasFocus)
                    {

                        if (txtClave.getText().toString().isEmpty()){

                            msgbox("Debe ingresar la clave para recibir los datos");

                            showkeyb();

                            final Handler cbhandler = new Handler();
                            cbhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    txtClave.requestFocus();
                                }
                            }, 500);
                        }

                    }
                }
            });

            txtURLWS.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
                            (event.getAction() == KeyEvent.ACTION_DOWN)) {

                        gl.wsurl = txtURLWS.getText().toString();

                        hidekeyb();

                        Recibir();

                        return true;
                    } else {
                        return false;
                    }
                }
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Main

    public void Recibir(){

        script.clear();

        if (!validaDatos()) return;

        cmdRecibir.setVisibility(View.INVISIBLE);
        pbar.setVisibility(View.VISIBLE);

        gl.emp=Integer.valueOf(txtEmpresa.getText().toString());
        gl.clave=txtClave.getText().toString();
        gl.wsurl=txtURLWS.getText().toString();

        guardaDatosConexion();

        app.getURL();
        ws = new WebServiceHandler(WSRec.this, gl.wsurl, gl.timeout);
        xobj = new XMLObject(ws);

        execws(1);
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

        try {
            long ff=du.getActDate();ff=du.addDays(ff,-7);
            db.execSQL("DELETE FROM D_orden_log WHERE FECHA<"+ff);
        } catch (Exception e) {
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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

            if (validaParametros()) {
                app.setDateRecep(du.getActDate());
                msgboxwait("Recepción completa");

            } else {
                msgboxexit("Configuración de tienda o caja incorrecta");
                //finish();
            }
            validaCombos();

            return true;

        } catch (Exception e) {
            db.endTransaction();
            msgboxwait("DB Commit Error\n" + e.getMessage() + "\n" + sql);
            return false;
        }
    }

    private void validaCombos() {
        Cursor dt;
        String cp="";

        app.citems.clear();

        try {
            sql="SELECT P_PRODMENU.NOMBRE,P_PRODMENUOPC.NOMBRE,P_PRODMENUOPC_DET.CODIGO_PRODUCTO " +
                    "FROM  P_PRODMENUOPC_DET INNER JOIN P_PRODMENUOPC ON P_PRODMENUOPC_DET.CODIGO_MENU_OPCION = P_PRODMENUOPC.CODIGO_MENU_OPCION INNER JOIN " +
                    "P_PRODMENU ON P_PRODMENUOPC.CODIGO_MENU = P_PRODMENU.CODIGO_MENU " +
                    "WHERE (P_PRODMENUOPC_DET.CODIGO_PRODUCTO NOT IN " +
                    "(SELECT CODIGO_PRODUCTO FROM P_PRODUCTO))";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {
                    cp=dt.getString(0)+" - "+dt.getString(1)+", Cod: "+dt.getInt(2);
                    if (!app.citems.contains(cp)) app.citems.add(cp);
                    dt.moveToNext();
                }
            }

            if (app.citems.size()>0) mostrarLista();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void mostrarLista() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(WSRec.this,"Producto de combo inactivo");

            for (int i = 0; i <app.citems.size(); i++) {
                listdlg.add(app.citems.get(i));
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void processEmpresas() {

        try {

            clsBeP_EMPRESA item = new clsBeP_EMPRESA();
            clsClasses.clsP_empresa var = clsCls.new clsP_empresa();

            script.add("DELETE FROM P_EMPRESA");

            item = xobj.getresult(clsBeP_EMPRESA.class, "GetP_EMPRESA");

            if(item==null){
                throw new Exception("El código o la clave ingresados no son válidos para la empresa, o la empresa está inactiva.");
            }

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
            var.clave = item.CLAVE;

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

            } catch (Exception ee)   {
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
                var.activo = item.getACTIVO();
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

    private void processMotivoAjustes() {

        try {

            clsP_motivoajusteObj handler = new clsP_motivoajusteObj(this, Con, db);
            clsBeP_MOTIVO_AJUSTEList items = new clsBeP_MOTIVO_AJUSTEList();
            clsBeP_MOTIVO_AJUSTE item = new clsBeP_MOTIVO_AJUSTE();
            clsClasses.clsP_motivoajuste var = clsCls.new clsP_motivoajuste();

            script.add("DELETE FROM P_MOTIVO_AJUSTE");

            items = xobj.getresult(clsBeP_MOTIVO_AJUSTEList.class, "GetP_MOTIVO_AJUSTE");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);

                var = clsCls.new clsP_motivoajuste();

                var.codigo_motivo_ajuste = item.getCODIGO_MOTIVO_AJUSTE();
                var.activo = item.getACTIVO();
                var.empresa = item.getEMPRESA();
                var.nombre = item.getNOMBRE() + "";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processDepartamentos() {

        try {

            clsP_departamentoObj handler = new clsP_departamentoObj(this, Con, db);
            clsBeP_DEPARTAMENTOList items = new clsBeP_DEPARTAMENTOList();
            clsBeP_DEPARTAMENTO item = new clsBeP_DEPARTAMENTO();
            clsClasses.clsP_departamento var = clsCls.new clsP_departamento();

            script.add("DELETE FROM P_DEPARTAMENTO");

            items = xobj.getresult(clsBeP_DEPARTAMENTOList.class, "GetP_DEPARTAMENTO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);

                var = clsCls.new clsP_departamento();

                var.codigo = item.getCODIGO();
                var.codigo_area = item.getCODIGO_AREA();
                var.nombre = item.getNOMBRE() + "";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processMunicipios() {

        try {

            clsP_municipioObj handler = new clsP_municipioObj(this, Con, db);
            clsBeP_MUNICIPIOList items = new clsBeP_MUNICIPIOList();
            clsBeP_MUNICIPIO item = new clsBeP_MUNICIPIO();
            clsClasses.clsP_municipio var = clsCls.new clsP_municipio();

            script.add("DELETE FROM P_MUNICIPIO");

            items = xobj.getresult(clsBeP_MUNICIPIOList.class, "GetP_MUNICIPIO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);

                var = clsCls.new clsP_municipio();

                var.codigo = item.getCODIGO();
                var.codigo_departamento = item.getCODIGO_DEPARTAMENTO();
                var.nombre = item.getNOMBRE() + "";
                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processProductoTipo() {

        try {

            clsP_Producto_TipoObj handler = new clsP_Producto_TipoObj(this, Con, db);
            clsBeP_PRODUCTO_TIPOList items = new clsBeP_PRODUCTO_TIPOList();
            clsBeP_PRODUCTO_TIPO item = new clsBeP_PRODUCTO_TIPO();
            clsClasses.clsP_Producto_Tipo var = clsCls.new clsP_Producto_Tipo();

            script.add("DELETE FROM P_PRODUCTO_TIPO");

            items = xobj.getresult(clsBeP_PRODUCTO_TIPOList.class, "GetP_PRODUCTO_TIPO");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                var = clsCls.new clsP_Producto_Tipo();
                var.codigo_tipo_producto = item.getCODIGO_TIPO_PRODUCTO();
                var.nombre = item.getNOMBRE() + "";
                var.utiliza_stock = item.getUTILIZA_STOCK();
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

                var.codigo_archivoconf=item.CODIGO_ARCHIVOCONF;
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
                var.empresa= Integer.parseInt(item.EMPRESA);
                var.empresa = Integer.parseInt(item.EMPRESA);
                var.corelini = item.CORELINI;
                var.corelfin = item.CORELFIN;
                var.corelult = item.CORELULT;
                var.fechares = item.FECHARES;
                var.ruta = item.RUTA;
                var.fechavig = item.FECHAVIG;
                var.resguardo = item.RESGUARDO;
                var.handheld = item.HANDHELD;
                var.valor1 = item.VALOR1;
                var.activa = mu.bool(item.ACTIVA);
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
                var.cliente = item.CODIGO_CLIENTE;
                var.ctipo = item.CTIPO;
                var.producto = item.CODIGO_PRODUCTO;
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

    private void processFrases() {
        try {
            clsP_fraseObj handler = new clsP_fraseObj(this, Con, db);
            clsBeP_FRASEList items = new clsBeP_FRASEList();
            clsBeP_FRASE item = new clsBeP_FRASE();
            clsClasses.clsP_frase var;

            script.add("DELETE FROM P_FRASE");

            items = xobj.getresult(clsBeP_FRASEList.class, "GetP_FRASE");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_frase();
                var.codigo_frase = item.CODIGO_FRASE;
                var.texto = item.TEXTO;
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
                var.bloqueado = mu.bool(item.BLOQUEADO);
                var.nivelprecio = item.NIVELPRECIO;
                var.mediapago = item.MEDIAPAGO;
                var.limitecredito = item.LIMITECREDITO;
                var.diacredito = item.DIACREDITO;
                var.descuento = mu.bool(item.DESCUENTO);
                var.bonificacion = mu.bool(item.BONIFICACION);
                var.ultvisita = 0; // item.ULTVISITA;
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
                var.imagen = item.IMAGEN+"";

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

            //#EJC20210705: Agregué propina por media_pago.
            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_mediapago();
                var.codigo = item.CODIGO;
                var.nombre = item.NOMBRE;
                var.activo = mu.bool(item.ACTIVO);
                var.nivel = item.NIVEL;
                var.porcobro = mu.bool(item.PORCOBRO);
                var.propina = item.PROPINA;
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
            if (items==null) return;

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
            if (items==null) return;

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
            if (items==null) return;

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
            if (items==null) return;

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
                var.activo = mu.bool(item.ACTIVO);
                var.correo = item.CORREO;

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
                var.descuento = mu.bool(item.DESCUENTO);
                var.bonificacion = mu.bool(item.BONIFICACION);
                var.imp1 = item.IMP1;
                var.imp2 = item.IMP2;
                var.imp3 = item.IMP3;
                var.vencomp = item.VENCOMP + "";
                var.devol = mu.bool(item.DEVOL);
                var.ofrecer = mu.bool(item.OFRECER);
                var.rentab = mu.bool(item.RENTAB);
                var.descmax = mu.bool(item.DESCMAX);
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

                try {
                    var.producto_padre = Integer.parseInt(item.PRODUCTO_PADRE);
                } catch (Exception e) {
                    var.producto_padre=0;
                }


                var.factor_padre = item.FACTOR_PADRE;
                var.tiene_inv = mu.bool(item.TIENE_INV);
                var.tiene_vineta_o_tubo = mu.bool(item.TIENE_VINETA_O_TUBO);
                var.precio_vineta_o_tubo = item.PRECIO_VINETA_O_TUBO;
                var.es_vendible = mu.bool(item.ES_VENDIBLE);
                var.unigrasap = item.UNIGRASAP;
                var.um_salida = item.UM_SALIDA + "";
                var.activo = 1;
                var.tiempo_preparacion = item.TIEMPO_PREPARACION;

                try {

                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }


                script.add(handler.addItemSql(var));

                String img = var.imagen;

                if (img != null) {

                    String filePathImg = rootdir + var.codigo + ".jpg";
                    try {

                        File file = new File(filePathImg);

                        if (!file.exists()) {

                            byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                            int bs = imgbytes.length;

                            try {

                                FileOutputStream fos = new FileOutputStream(filePathImg);
                                BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                                outputStream.write(imgbytes);
                                outputStream.close();
                                imgbytes = null;

                            } catch (Exception ex) {
                                file.delete();
                                ws.error = ex.getMessage();ws.errorflag = true;
                            }
                        }
                    } catch (Exception e) {
                        ws.error = e.getMessage();ws.errorflag = true;
                    }
                }
            }
        } catch (Exception e) {
            ws.error = e.getMessage();ws.errorflag = true;
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
                var.correo = item.CORREO;
                var.texto = item.TEXTO;
                var.activo = mu.bool(item.ACTIVO);
                var.fel_codigo_establecimiento=item.FEL_CODIGO_ESTABLECIMIENTO;
                var.fel_usuario_firma=item.FEL_USUARIO_FIRMA;
                var.fel_usuario_certificacion=item.FEL_USUARIO_CERTIFICACION;
                var.fel_llave_firma=item.FEL_LLAVE_FIRMA;
                var.fel_llave_certificacion =item.FEL_LLAVE_CERTIFICACION;
                var.fel_afiliacion_iva=item.FEL_AFILIACION_IVA;
                var.codigo_postal=item.CODIGO_POSTAL;
                var.codigo_escenario_isr=item.CODIGO_ESCENARIO_ISR;
                var.codigo_escenario_iva=item.CODIGO_ESCENARIO_IVA;
                var.codigo_municipio=item.CODIGO_MUNICIPIO;
                var.codigo_proveedor=item.CODIGO_PROVEEDOR;

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

        String dirvendedores = rootdir + "/Vendedor/";

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
                var.ruta = item.RUTA;
                var.nombre = item.NOMBRE;
                var.clave = item.CLAVE + "";
                var.nivel = item.NIVEL;
                var.nivelprecio = item.NIVELPRECIO;
                var.bodega = item.BODEGA + "";
                var.subbodega = item.SUBBODEGA + "";
                var.activo = mu.bool(item.ACTIVO);
                var.codigo_vendedor = item.CODIGO_VENDEDOR;
                var.imagen=item.IMAGEN;
                var.fecha_inicio_labores=item.FECHA_INICIO_LABORES;
                var.fecha_fin_labores=item.FECHA_FIN_LABORES;

                script.add(handler.addItemSql(var));

                try {

                    String img = var.imagen;

                    if (img != null) {

                        if (i==0){
                            File f = new File(dirvendedores);
                            if(!f.isDirectory()) f.mkdir();
                        }

                        String filePathImg = dirvendedores + var.codigo + ".png";
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

    private void processConceptoPago() {

        try {
            clsP_conceptopagoObj handler = new clsP_conceptopagoObj(this, Con, db);
            clsBeP_CONCEPTOPAGOList items = new clsBeP_CONCEPTOPAGOList();
            clsBeP_CONCEPTOPAGO item = new clsBeP_CONCEPTOPAGO();
            clsClasses.clsP_conceptopago var;

            script.add("DELETE FROM P_CONCEPTOPAGO");

            items = xobj.getresult(clsBeP_CONCEPTOPAGOList.class, "GetP_CONCEPTOPAGO");
            if (items==null) return;

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

    private void processParametrosExtra() {
        try {
            clsP_paramextObj handler = new clsP_paramextObj(this, Con, db);
            clsBeP_PARAMEXTList items = new clsBeP_PARAMEXTList();
            clsBeP_PARAMEXT item = new clsBeP_PARAMEXT();
            clsClasses.clsP_paramext var;

            script.add("DELETE FROM P_PARAMEXT");

            items = xobj.getresult(clsBeP_PARAMEXTList.class, "GetP_PARAMEXT");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_paramext();
                var.id = item.ID;
                var.nombre = item.Nombre;
                var.valor = item.Valor;
                var.ruta  = item.IdRuta;
                script.add(handler.addItemSql(var));
            }

            script.add("UPDATE P_PARAMEXT SET VALOR='S' WHERE ID=109");

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processParametrosExtraRuta() {
        try {
            clsP_paramextObj handler = new clsP_paramextObj(this, Con, db);
            clsBeP_PARAMEXTList items = new clsBeP_PARAMEXTList();
            clsBeP_PARAMEXT item = new clsBeP_PARAMEXT();
            clsClasses.clsP_paramext var;
            String sqlaa;

            items = xobj.getresult(clsBeP_PARAMEXTList.class, "GetP_PARAMEXT_RUTA");
            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {

                item = items.items.get(i);
                //if (item.IdRuta.equalsIgnoreCase(sruta)) {
                var = clsCls.new clsP_paramext();
                var.id = item.ID;
                var.nombre = item.Nombre;
                var.valor = item.Valor;
                var.ruta  = item.IdRuta;

                //try {
                    //int cr=Integer.parseInt(var.ruta);
                    //if (cr==gl.codigo_ruta) {
                        script.add("DELETE FROM P_PARAMEXT WHERE ID=" + item.ID);
                        sqlaa = addItemSqlParamExtRuta(var);
                        script.add(sqlaa);
                    //}
                //} catch (Exception e) {
                //}

                //}
           }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    public String addItemSqlParamExtRuta(clsClasses.clsP_paramext item) {

        ins.init("P_paramext");

        ins.add("ID", item.id);
        ins.add("Nombre", item.nombre);
        ins.add("Valor", item.valor);
        ins.add("Ruta", item.ruta);

        return ins.sql();

    }

    private void processResSala() {

        try {
            clsP_res_salaObj handler = new clsP_res_salaObj(this, Con, db);
            clsBeP_RES_SALAList items = new clsBeP_RES_SALAList();
            clsBeP_RES_SALA item = new clsBeP_RES_SALA();
            clsClasses.clsP_res_sala var;

            script.add("DELETE FROM P_RES_SALA");

            items = xobj.getresult(clsBeP_RES_SALAList.class, "GetP_RES_SALA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_res_sala();

                var.codigo_sala=item.CODIGO_SALA;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.nombre=item.NOMBRE;
                var.activo=mu.bool(item.ACTIVO);
                var.escala=item.ESCALA;
                var.tam_letra=item.TAM_LETRA;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }

    }

    private void processResMesa() {

        try {
            clsP_res_mesaObj handler = new clsP_res_mesaObj(this, Con, db);
            clsBeP_RES_MESAList items = new clsBeP_RES_MESAList();
            clsBeP_RES_MESA item = new clsBeP_RES_MESA();
            clsClasses.clsP_res_mesa var;

            script.add("DELETE FROM P_RES_MESA");

            items = xobj.getresult(clsBeP_RES_MESAList.class, "GetP_RES_MESA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_res_mesa();

                var.codigo_mesa=item.CODIGO_MESA;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.codigo_sala=item.CODIGO_SALA;
                var.codigo_grupo=item.CODIGO_GRUPO;
                var.nombre=item.NOMBRE;
                var.largo=item.LARGO;
                var.ancho=item.ANCHO;
                var.posx=item.POSX;
                var.posy=item.POSY;
                var.codigo_qr=item.CODIGO_QR;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }

    }

    private void processResGrupo() {

        try {
            clsP_res_grupoObj handler = new clsP_res_grupoObj(this, Con, db);
            clsBeP_RES_GRUPOList items = new clsBeP_RES_GRUPOList();
            clsBeP_RES_GRUPO item = new clsBeP_RES_GRUPO();
            clsClasses.clsP_res_grupo var;

            script.add("DELETE FROM P_RES_GRUPO");

            items = xobj.getresult(clsBeP_RES_GRUPOList.class, "GetP_RES_GRUPO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_res_grupo();

                var.codigo_grupo=item.CODIGO_GRUPO;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.nombre=item.NOMBRE;
                var.telefono=""+item.TELEFONO;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }

    }

    private void processLineaImpresora() {

        try {
            clsP_linea_impresoraObj handler = new clsP_linea_impresoraObj(this, Con, db);
            clsBeP_LINEA_IMPRESORAList items = new clsBeP_LINEA_IMPRESORAList();
            clsBeP_LINEA_IMPRESORA item = new clsBeP_LINEA_IMPRESORA();
            clsClasses.clsP_linea_impresora var;

            script.add("DELETE FROM P_LINEA_IMPRESORA");

            items = xobj.getresult(clsBeP_LINEA_IMPRESORAList.class, "GetP_LINEA_IMPRESORA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_linea_impresora();

                var.codigo_linea_impresora=item.CODIGO_LINEA_IMPRESORA;
                var.codigo_linea=item.CODIGO_LINEA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.empresa=item.EMPRESA;
                var.codigo_impresora=item.CODIGO_IMPRESORA;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processImpresora() {

        try {
            clsP_impresoraObj handler = new clsP_impresoraObj(this, Con, db);
            clsBeP_IMPRESORAList items = new clsBeP_IMPRESORAList();
            clsBeP_IMPRESORA item = new clsBeP_IMPRESORA();
            clsClasses.clsP_impresora var;

            script.add("DELETE FROM P_IMPRESORA");

            items = xobj.getresult(clsBeP_IMPRESORAList.class, "GetP_IMPRESORA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_impresora();

                var.codigo_impresora=item.CODIGO_IMPRESORA;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.nombre=item.NOMBRE+"";
                var.numero_serie=item.NUMERO_SERIE+"";
                var.codigo_marca=item.CODIGO_MARCA;
                var.codigo_modelo=item.CODIGO_MODELO;
                var.tipo_impresora=item.TIPO_IMPRESORA;
                var.mac=item.MAC+"";
                var.ip=item.IP+"";
                var.fecha_agr=0;
                var.impresiones=item.IMPRESIONES;
                var.activo=mu.bool(item.ACTIVO);

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processMarcaImpresora() {

        try {
            clsP_impresora_marcaObj handler = new clsP_impresora_marcaObj(this, Con, db);
            clsBeP_IMPRESORA_MARCAList items = new clsBeP_IMPRESORA_MARCAList();
            clsBeP_IMPRESORA_MARCA item = new clsBeP_IMPRESORA_MARCA();
            clsClasses.clsP_impresora_marca var;

            script.add("DELETE FROM P_IMPRESORA_MARCA");

            items = xobj.getresult(clsBeP_IMPRESORA_MARCAList.class, "GetP_IMPRESORA_MARCA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_impresora_marca();

                var.codigo_impresora_marca=item.CODIGO_IMPRESORA_MARCA;
                var.nombre=item.NOMBRE;
                var.activo=mu.bool(item.ACTIVO);

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processModeloImpresora() {

        try {
            clsP_impresora_modeloObj handler = new clsP_impresora_modeloObj(this, Con, db);
            clsBeP_IMPRESORA_MODELOList items = new clsBeP_IMPRESORA_MODELOList();
            clsBeP_IMPRESORA_MODELO item = new clsBeP_IMPRESORA_MODELO();
            clsClasses.clsP_impresora_modelo var;

            script.add("DELETE FROM P_IMPRESORA_MODELO");

            items = xobj.getresult(clsBeP_IMPRESORA_MODELOList.class, "GetP_IMPRESORA_MODELO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_impresora_modelo();

                var.codigo_impresora_modelo=item.CODIGO_IMPRESORA_MODELO;
                var.codigo_impresora_marca=item.CODIGO_IMPRESORA_MARCA;
                var.nombre=item.NOMBRE;
                var.activo=mu.bool(item.ACTIVO);

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processVersion() {
        String ss;
        int p1,p2;

        try {
            p1=ws.xmlresult.indexOf(",'")+2;
            p2=ws.xmlresult.indexOf("');");
            if (p1<1 | p2<1) {
                idversion="";return;
            }

            idversion=ws.xmlresult.substring(p1,p2);
            idversion=idversion.trim();
            nueva_version=!idversion.equalsIgnoreCase(gl.parVer);
        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processCajaImpresora() {

        try {
            clsP_caja_impresoraObj handler = new clsP_caja_impresoraObj(this, Con, db);
            clsBeP_CAJA_IMPRESORAList items = new clsBeP_CAJA_IMPRESORAList();
            clsBeP_CAJA_IMPRESORA item = new clsBeP_CAJA_IMPRESORA();
            clsClasses.clsP_caja_impresora var;

            script.add("DELETE FROM P_CAJA_IMPRESORA");

            items = xobj.getresult(clsBeP_CAJA_IMPRESORAList.class, "GetP_CAJA_IMPRESORA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_caja_impresora();

                var.codigo_caja_impresora=item.CODIGO_CAJA_IMPRESORA;
                var.codigo_caja=item.CODIGO_CAJA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.empresa=item.EMPRESA;
                var.codigo_impresora=item.CODIGO_IMPRESORA;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processUnidad() {

        try {
            clsP_unidadObj handler = new clsP_unidadObj(this, Con, db);
            clsBeP_UNIDADList items = new clsBeP_UNIDADList();
            clsBeP_UNIDAD item = new clsBeP_UNIDAD();
            clsClasses.clsP_unidad var;

            script.add("DELETE FROM P_UNIDAD");

            items = xobj.getresult(clsBeP_UNIDADList.class, "GetP_UNIDAD");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_unidad();

                var.codigo_unidad=item.CODIGO_UNIDAD;
                var.nombre=item.NOMBRE;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processUnidadConv() {

        try {
            clsP_unidad_convObj handler = new clsP_unidad_convObj(this, Con, db);
            clsBeP_UNIDAD_CONVList items = new clsBeP_UNIDAD_CONVList();
            clsBeP_UNIDAD_CONV item = new clsBeP_UNIDAD_CONV();
            clsClasses.clsP_unidad_conv var;

            script.add("DELETE FROM P_UNIDAD_CONV");

            items = xobj.getresult(clsBeP_UNIDAD_CONVList.class, "GetP_UNIDAD_CONV");

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_unidad_conv();

                var.codigo_conversion=item.CODIGO_CONVERSION;
                var.codigo_unidad1=item.CODIGO_UNIDAD1;
                var.codigo_unidad2=item.CODIGO_UNIDAD2;
                var.factor=item.FACTOR;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processProdReceta() {

        try {
            clsP_prodrecetaObj handler = new clsP_prodrecetaObj(this, Con, db);
            clsBeP_PRODRECETAList items = new clsBeP_PRODRECETAList();
            clsBeP_PRODRECETA item = new clsBeP_PRODRECETA();
            clsClasses.clsP_prodreceta var;

            script.add("DELETE FROM P_PRODRECETA");

            items = xobj.getresult(clsBeP_PRODRECETAList.class, "GetP_PRODRECETA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);
                var = clsCls.new clsP_prodreceta();

                var.codigo_receta=item.CODIGO_RECETA;
                var.empresa=item.EMPRESA;
                var.codigo_producto=item.CODIGO_PRODUCTO;
                var.codigo_articulo=item.CODIGO_ARTICULO;
                var.cant=item.CANT;
                var.um=item.UM;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processNivelSucursal() {

        try {
            clsP_nivelprecio_sucursalObj handler = new clsP_nivelprecio_sucursalObj(this, Con, db);
            clsBeP_NIVELPRECIO_SUCURSALList items = new clsBeP_NIVELPRECIO_SUCURSALList();
            clsBeP_NIVELPRECIO_SUCURSAL item = new clsBeP_NIVELPRECIO_SUCURSAL();
            clsClasses.clsP_nivelprecio_sucursal var;

            script.add("DELETE FROM P_NIVELPRECIO_SUCURSAL");

            items = xobj.getresult(clsBeP_NIVELPRECIO_SUCURSALList.class, "GetP_NIVELPRECIO_SUCURSAL");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_nivelprecio_sucursal();

                var.codigo_nivel_sucursal=item.CODIGO_NIVEL_SUCURSAL;
                var.codigo_empresa=item.CODIGO_EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.codigo_nivel_precio=item.CODIGO_NIVEL_PRECIO;
                var.usuario_agrego=0;
                var.fecha_agregado=0;
                var.activo=1;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processReglaCosto() {
        try {
            clsP_regla_costoObj handler = new clsP_regla_costoObj(this, Con, db);
            clsBeP_REGLA_COSTOList items = new clsBeP_REGLA_COSTOList();
            clsBeP_REGLA_COSTO item = new clsBeP_REGLA_COSTO();
            clsClasses.clsP_regla_costo var;

            script.add("DELETE FROM P_REGLA_COSTO");

            items = xobj.getresult(clsBeP_REGLA_COSTOList.class, "GetP_REGLA_COSTO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_regla_costo();

                var.codigo_empresa=item.CODIGO_EMPRESA;
                var.codigo_tipo=item.CODIGO_TIPO;
                var.dias=item.DIAS;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processAlmacen() {
        try {
            clsP_almacenObj handler = new clsP_almacenObj(this, Con, db);
            clsBeP_ALMACENList items = new clsBeP_ALMACENList();
            clsBeP_ALMACEN item = new clsBeP_ALMACEN();
            clsClasses.clsP_almacen var;

            script.add("DELETE FROM P_ALMACEN");

            items = xobj.getresult(clsBeP_ALMACENList.class, "GetP_ALMACEN");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_almacen();

                var.codigo_almacen=item.CODIGO_ALMACEN;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.activo=mu.bool(item.ACTIVO);
                var.nombre=item.NOMBRE;
                var.es_principal=mu.bool(item.ES_PRINCIPAL);

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage();
            ws.errorflag = true;
        }
    }

    private void processModificador() {
        try {
            clsP_modificadorObj handler = new clsP_modificadorObj(this, Con, db);
            clsBeP_MODIFICADORList items = new clsBeP_MODIFICADORList();
            clsBeP_MODIFICADOR item = new clsBeP_MODIFICADOR();
            clsClasses.clsP_modificador var;

            script.add("DELETE FROM P_MODIFICADOR");

            items = xobj.getresult(clsBeP_MODIFICADORList.class, "GetP_MODIFICADOR");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_modificador();

                var.codigo_modif=item.CODIGO_MODIF;
                var.empresa=item.EMPRESA;
                var.codigo_grupo=item.CODIGO_GRUPO;
                var.nombre=item.NOMBRE;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processModificadorGrupo() {
        try {
            clsP_modificador_grupoObj handler = new clsP_modificador_grupoObj(this, Con, db);
            clsBeP_MODIFICADOR_GRUPOList items = new clsBeP_MODIFICADOR_GRUPOList();
            clsBeP_MODIFICADOR_GRUPO item = new clsBeP_MODIFICADOR_GRUPO();
            clsClasses.clsP_modificador_grupo var;

            script.add("DELETE FROM P_MODIFICADOR_GRUPO");

            items = xobj.getresult(clsBeP_MODIFICADOR_GRUPOList.class, "GetP_MODIFICADOR_GRUPO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_modificador_grupo();

                var.codigo_grupo=item.CODIGO_GRUPO;
                var.empresa=item.EMPRESA;
                var.nombre=item.NOMBRE;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processProdClasifModif() {
        try {
            clsP_prodclasifmodifObj handler = new clsP_prodclasifmodifObj(this, Con, db);
            clsBeP_PRODCLASIFMODIFList items = new clsBeP_PRODCLASIFMODIFList();
            clsBeP_PRODCLASIFMODIF item = new clsBeP_PRODCLASIFMODIF();
            clsClasses.clsP_prodclasifmodif var;

            script.add("DELETE FROM P_PRODCLASIFMODIF");

            items = xobj.getresult(clsBeP_PRODCLASIFMODIFList.class, "GetP_PRODCLASIFMODIF");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_prodclasifmodif();

                var.codigo_clasificacion=item.CODIGO_CLASIFICACION;
                var.empresa=item.EMPRESA;
                var.codigo_grupo=item.CODIGO_GRUPO;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processBarrilTipo() {
        try {
            clsP_barril_tipoObj handler = new clsP_barril_tipoObj(this, Con, db);
            clsBeP_BARRIL_TIPOList items = new clsBeP_BARRIL_TIPOList();
            clsBeP_BARRIL_TIPO item = new clsBeP_BARRIL_TIPO();
            clsClasses.clsP_barril_tipo var;

            script.add("DELETE FROM P_BARRIL_TIPO");

            items = xobj.getresult(clsBeP_BARRIL_TIPOList.class, "GetP_BARRIL_TIPO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_barril_tipo();

                var.codigo_tipo=item.CODIGO_TIPO;
                var.empresa=item.EMPRESA;
                var.descripcion=item.DESCRIPCION;
                var.capacidad=item.CAPACIDAD;
                var.mermamin=item.MERMAMIN;
                var.mermamax=item.MERMAMAX;
                var.activo=item.ACTIVO;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processBarrilBarra() {
        try {
            clsP_barril_barraObj handler = new clsP_barril_barraObj(this, Con, db);
            clsBeP_BARRIL_BARRAList items = new clsBeP_BARRIL_BARRAList();
            clsBeP_BARRIL_BARRA item = new clsBeP_BARRIL_BARRA();
            clsClasses.clsP_barril_barra var;

            script.add("DELETE FROM P_BARRIL_BARRA");

            items = xobj.getresult(clsBeP_BARRIL_BARRAList.class, "GetP_BARRIL_BARRA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_barril_barra();

                var.codigo_barra=item.CODIGO_BARRA;
                var.empresa=item.EMPRESA;
                var.barra=item.BARRA;
                var.codigo_tipo=item.CODIGO_TIPO;
                var.codigo_interno=item.CODIGO_INTERNO;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processCortesia() {
        try {
            clsP_cortesiaObj handler = new clsP_cortesiaObj(this, Con, db);
            clsBeP_CORTESIAList items = new clsBeP_CORTESIAList();
            clsBeP_CORTESIA item = new clsBeP_CORTESIA();
            clsClasses.clsP_cortesia var;

            script.add("DELETE FROM P_CORTESIA");

            items = xobj.getresult(clsBeP_CORTESIAList.class, "GetP_CORTESIA");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_cortesia();

                var.codigo_cortesia=item.CODIGO_CORTESIA;
                var.empresa=item.EMPRESA;
                var.nombre=item.NOMBRE;
                var.codigo_vendedor=item.CODIGO_VENDEDOR;
                var.activo=item.ACTIVO;
                var.clave=item.CLAVE;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processVendedorRol() {
        try {
            clsP_vendedor_rolObj handler = new clsP_vendedor_rolObj(this, Con, db);
            clsBeP_VENDEDOR_ROLList items = new clsBeP_VENDEDOR_ROLList();
            clsBeP_VENDEDOR_ROL item = new clsBeP_VENDEDOR_ROL();
            clsClasses.clsP_vendedor_rol var;

            script.add("DELETE FROM P_VENDEDOR_ROL");

            items = xobj.getresult(clsBeP_VENDEDOR_ROLList.class, "GetP_VENDEDOR_ROL");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_vendedor_rol();

                var.codigo_vendedor_rol=item.CODIGO_VENDEDOR_ROL;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.codigo_vendedor=item.CODIGO_VENDEDOR;
                var.codigo_rol=item.CODIGO_ROL;
                var.fec_agr=0;
                var.user_agr=0;
                var.fec_mod=0;
                var.user_mod=0;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    private void processVendedorSucursal() {
        try {
            clsP_vendedor_sucursalObj handler = new clsP_vendedor_sucursalObj(this, Con, db);
            clsBeP_VENDEDOR_SUCURSALList items = new clsBeP_VENDEDOR_SUCURSALList();
            clsBeP_VENDEDOR_SUCURSAL item = new clsBeP_VENDEDOR_SUCURSAL();
            clsClasses.clsP_vendedor_sucursal var;

            script.add("DELETE FROM P_VENDEDOR_SUCURSAL");

            items = xobj.getresult(clsBeP_VENDEDOR_SUCURSALList.class, "GetP_VENDEDOR_SUCURSAL");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_vendedor_sucursal();

                var.codigo_vendedor_sucursal=item.CODIGO_VENDEDOR_SUCURSAL;
                var.empresa=item.EMPRESA;
                var.codigo_sucursal=item.CODIGO_SUCURSAL;
                var.codigo_vendedor=item.CODIGO_VENDEDOR;
                var.fec_agr=0;
                var.user_agr=0;
                var.fec_mod=0;
                var.user_mod=0;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }

    }

    private void processTipoNegocio() {
        try {
            clsP_tiponegObj handler = new clsP_tiponegObj(this, Con, db);
            clsBeP_TIPONEGList items = new clsBeP_TIPONEGList();
            clsBeP_TIPONEG item = new clsBeP_TIPONEG();
            clsClasses.clsP_tiponeg var;

            script.add("DELETE FROM P_TIPONEG");

            items = xobj.getresult(clsBeP_TIPONEGList.class, "GetP_TIPO_NEGOCIO");
            if (items==null) return;

            try {
                if (items.items.size() == 0) return;
            } catch (Exception e) {
                return;
            }

            for (int i = 0; i < items.items.size(); i++) {
                item = items.items.get(i);

                var = clsCls.new clsP_tiponeg();

                var.codigo_tipo_negocio=item.CODIGO_TIPO_NEGOCIO;
                var.empresa=item.EMPRESA;
                var.descripcion=item.DESCRIPCION;
                var.activo=1;
                var.fec_agr=0;
                var.user_agr=0;
                var.fec_mod=0;
                var.user_mod=0;

                script.add(handler.addItemSql(var));
            }

        } catch (Exception e) {
            ws.error = e.getMessage(); ws.errorflag = true;
        }
    }

    //endregion

    //region Aux

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

        dialog.setTitle("MPos");
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (nueva_version) {
                    //msgAskVersion("¿Existe nueva version, proceder con la instalación?");
                    finish();
                } else
                    finish();
            }
        });
        dialog.show();
    }

    private void msgAskVersion(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("MPos");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent =WSRec.this.getPackageManager().getLaunchIntentForPackage("com.dts.mposupd");
                    intent.putExtra("filename","mpos.apk");
                    WSRec.this.startActivity(intent);
                } catch (Exception e) {
                    msgbox("No está instalada aplicación para actualización de versiónes, por favor informe soporte.");
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }

    private void msgboxexit(String msg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("MPos");
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                restartApp();
            }
        });
        dialog.show();
    }

    private boolean validaDatos(){

        boolean resultado=true;

        try{
            if (txtEmpresa.getText().toString().isEmpty()){

                msgbox("Debe ingresar la empresa para recibir los datos");

                showkeyb();

                final Handler cbhandler = new Handler();
                cbhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtEmpresa.requestFocus();
                    }
                }, 500);

                resultado = false;

            }else if (txtClave.getText().toString().isEmpty()){

                msgbox("Debe ingresar la clave para recibir los datos");

                showkeyb();

                final Handler cbhandler = new Handler();
                cbhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtClave.requestFocus();
                    }
                }, 500);

                resultado = false;

            }if (txtURLWS.getText().toString().isEmpty()){

                msgbox("Debe ingresar la URL para recibir los datos");

                showkeyb();

                final Handler cbhandler = new Handler();
                cbhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtURLWS.requestFocus();
                    }
                }, 500);

                resultado = false;

            }

        }catch (Exception ex){
            msgbox2("Ocurrió un error validando los datos " + ex.getMessage());
            resultado = false;
        }

        return resultado;
    }

    private boolean validaParametros() {
        Cursor dt;

        try {

            sql="SELECT * FROM P_SUCURSAL";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) {
                db.execSQL("DELETE FROM P_RUTA");return false;
            }

            sql="SELECT * FROM P_RUTA";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) return false;

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }

        return true;
    }

    protected void guardaDatosConexion() {

        BufferedWriter writer = null;
        FileWriter wfile;

        if (gl.timeout==0) gl.timeout=6000;

        try {

            String fname = Environment.getExternalStorageDirectory()+"/mposws.txt";
            File archivo= new File(fname);

            if (archivo.exists()){
                archivo.delete();
            }

            wfile=new FileWriter(fname,true);
            writer = new BufferedWriter(wfile);

            writer.write(gl.wsurl + "\n");
            writer.write(""+gl.timeout);

            writer.close();

        } catch (Exception e) {
            msgbox2("Error " + e.getMessage());
        }

    }

    private void restartApp(){
        try{
            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent =Intent.makeRestartActivityTask(componentName);
            //Intent mainIntent = IntentCompat..makeRestartActivityTask(componentName);
            this.startActivity(mainIntent);
            System.exit(0);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Activity Events

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

            if(browse==2){
                browse=0;
                Recibir();
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion
}