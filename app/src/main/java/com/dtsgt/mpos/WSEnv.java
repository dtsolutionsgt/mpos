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

import java.util.ArrayList;

public class WSEnv extends PBase {

    private TextView lbl1;
    private ProgressBar pbar;

    private WebServiceHandler ws;
    private XMLObject xobj;
    private ArrayList<String> script = new ArrayList<String>();

    private String plabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_s_env);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView7);
        lbl1.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        getURL();
        ws = new WebServiceHandler(WSEnv.this, gl.wsurl);
        xobj = new XMLObject(ws);
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

        public WebServiceHandler(PBase Parent, String Url) {
            super(Parent, Url);
        }

        @Override
        public void wsExecute() {
            try {
                switch (ws.callback) {
                    case 1:
                        callMethod("Commit", "SQL", "12345");
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
                    processEmpresas();if (ws.errorflag) { processComplete();break;}
                    //execws(3);
                    processComplete();
                    break;
        }

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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
            processData();
        }
    }

    private boolean processData() {

        try {
            db.beginTransaction();

            for (int i = 0; i < script.size(); i++) {
                sql = script.get(i);
                db.execSQL(sql);
                //msgbox(sql);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            msgboxwait("Recepción completa");

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox("DB Commit Error\n" + e.getMessage() + "\n" + sql);
            return false;
        }
    }

    //endregion

    //region Envío

    private void processEmpresas() {
        try {

            /*
            clsBeP_EMPRESA item = new clsBeP_EMPRESA();
            clsClasses.clsP_empresa var = clsCls.new clsP_empresa();

            script.add("DELETE FROM P_EMPRESA");

            item = xobj.getresult(clsBeP_EMPRESA.class, "GetP_EMPRESA");

            var.empresa =""+ item.EMPRESA;
            var.nombre = item.NOMBRE;
            var.col_imp = item.COL_IMP;
            var.logo="";
            var.razon_social=item.RAZON_SOCIAL+"";
            var.identificacion_tributaria=item.IDENTIFICACION_TRIBUTARIA+"";
            var.telefono=item.TELEFONO+"";
            var.cod_pais=item.COD_PAIS+"";
            var.nombre_contacto=item.NOMBRE_CONTACTO+"";
            var.apellido_contacto=item.APELLIDO_CONTACTO+"";
            var.direccion=item.DIRECCION+"";
            var.correo=item.CORREO+"";
            var.codigo_activacion=item.CODIGO_ACTIVACION+"";
            var.cod_cant_emp=item.COD_CANT_EMP;
            var.cantidad_puntos_venta=item.CANTIDAD_PUNTOS_VENTA;

            clsP_empresaObj P_empresaObj = new clsP_empresaObj(this, Con, db);

            script.add(P_empresaObj.addItemSql(var));
*/
        } catch (Exception e) {
            ws.error = e.getMessage();ws.errorflag = true;
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

