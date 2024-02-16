package com.dtsgt.felesa;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.AsyncTask;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_factura_svObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FELESATest extends PBase {

    private clsFELClases fclas=new clsFELClases();
    private clsFELClases.JSONFactura jfact;
    private clsFELClases.JSONCredito jcred;
    private clsFELClases.JSONAnulacion janul;

    private clsFactESA FactESA;
    private clsAnulESA AnulESA;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_factura_svObj D_factura_svObj;

    private JSONObject jsdoc;

    private String FELestabl,FELUsuario,FELClave;
    private String corel,dnum,cnombre,cnit,cdir, ccorreo,cgiro,cdep,cmuni;
    private long fcor,cornum;
    private int callbackmode;

    private String fileUrl;
    private File pdffile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_felesatest);

            super.InitBase();

            D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturadObj=new clsD_facturadObj(this,Con,db);
            D_facturafObj=new clsD_facturafObj(this,Con,db);
            D_factura_svObj=new clsD_factura_svObj(this,Con,db);

            //Valores de la tabla P_SUCURSAL (FEL_CODIGO_ESTABLECIMIENTO,FEL_USUARIO_FIRMA,FEL_LLAVE_FIRMA )
            FELestabl="0001";
            FELUsuario="06141106141147";
            FELClave="df3b5497c338a7e78d659a468e72a670";

            FactESA=new clsFactESA(this,FELUsuario,FELClave);
            AnulESA=new clsAnulESA(this,FELUsuario,FELClave);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doUltimaFactura(View view) {
        try {

            boolean esFactura=true;

            D_facturaObj.fill("ORDER BY COREL DESC");
            corel=D_facturaObj.first().corel;

            D_facturafObj.fill("WHERE (COREL='"+corel+"')");

            fcor=D_facturaObj.first().corelativo;
            cornum=11129000000000L;cornum=cornum+fcor;dnum=cornum+"";

            cnombre=D_facturafObj.first().nombre;
            cnit=D_facturafObj.first().nit;cnit=cnit.replace("-","");
            cdir=D_facturafObj.first().direccion;
            ccorreo =D_facturafObj.first().correo;


            if (!esFactura) {
                D_factura_svObj.fill("WHERE (COREL='" + corel + "')");
                cgiro = D_factura_svObj.first().codigo_tipo_negocio + "";
                cdep = D_factura_svObj.first().codigo_departamento;
                cdep = cdep.substring(1, 3);
                cmuni = D_factura_svObj.first().codigo_municipio;
                cmuni = cmuni.substring(3, 5);
            }

            //cnombre="Prueba Receptor";
            //cnit="2247806";
            //cdir="Calle 2";
            //ccorreo ="pruebas@pruebas.com";
            //cgiro="61101";
            //cgiro="10101";
            //dnum="11119000000109";

            //dnum="100003008";
            //if (creaJSONFijoCredito()) FactESA.Certifica(dnum, jsdoc.toString());

            callbackmode=1;
            if (esFactura) {
                if (JSOND_Factura()) FactESA.Certifica("100004001",jfact.json);
            } else {
                if (JSOND_Credito()) FactESA.Certifica(dnum, jcred.json);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doAnular(View view) {
        try {
            clsFELClases.anulacionDatos ad=fclas.new anulacionDatos();

            ad.establecimiento="0001";
            ad.uuid="08961DF8-948F-4E8A-B51F-5139EFEA0FA9";

            ad.responsable_nom="INFILE DEMO";
            ad.responsable_nit="06141106141147";
            ad.solicitante_nom="Prueba Receptor";
            ad.solicitante_nit="11111111105011";
            ad.solicitante_correo="implementaciones2@infile.com.gt";

            clsFELClases.JSONAnulacion janul=fclas.new JSONAnulacion();
            janul.Anulacion(ad);

            callbackmode=2;
            AnulESA.Anular(janul.json);
        } catch (Exception e) {
            felCallBack();
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doFactura(View view) {
        try {
            callbackmode=1;
            if (creaJSONFijoFactura()) FactESA.Certifica("100002005",jfact.json);
            //if (creaJSONCredito()) FactESA.Certifica(jfact.json);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doDownload(View view) {
        try {
            fileUrl="https://sandbox-certificador.infile.com.sv/api/v1/reporte/reporte_documento?uuid=CDB772D1-2B69-4369-A194-E9A08ECCFDE8&formato=pdf\n";
            String fileName="/download/pdfdwn.pdf";
            pdffile= new java.io.File(Environment.getExternalStorageDirectory(),"/download/pdfdwn.pdf");

            downloadFile();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private boolean JSOND_Credito() {
        try {
            jcred=fclas.new JSONCredito();

            jcred.Credito(FELestabl);

            jcred.Receptor(dnum,cnit,cnombre,cgiro,cnombre);
            jcred.Direccion(cdep,cmuni,cdir, ccorreo);

            D_facturadObj.fill("WHERE (COREL='"+corel+"')");

            for (int i = 0; i <D_facturadObj.count; i++) {
                jcred.agregarProducto("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).preciodoc,  // precio sin iva
                        D_facturadObj.items.get(i).imp);
            }

            jcred.json();

            String sj= jcred.json;
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean JSOND_Factura() {
        try {
            jfact=fclas.new JSONFactura();

            jfact.Factura(FELestabl);

            D_facturadObj.fill("WHERE (COREL='"+corel+"')");
            for (int i = 0; i <D_facturadObj.count; i++) {
                jfact.agregarServicio("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio con iva
                        D_facturadObj.items.get(i).desmon);
            }

            jfact.agregarAdenda(" ");

            jfact.json();
            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean JSONCredito() {
        try {
            jcred=fclas.new JSONCredito();

            jcred.Credito(FELestabl);

            jcred.Receptor("11111111111128","2247806","Receptor Prueba","61101","Prueba Receptor");
            jcred.Direccion("06","14","Calle 2","pruebas@pruebas.com");

            jcred.agregarProducto("Prueba item 1",1,100,13);

            /*
            D_facturadObj.fill("WHERE (COREL='"+corel+"')");
            for (int i = 0; i <D_facturadObj.count; i++) {
                jfact.agregarServicio("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio con iva
                        D_facturadObj.items.get(i).desmon);
            }
            */

            jcred.json();
            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean creaJSONFijoFactura()  {
        try {

            jfact=fclas.new JSONFactura();

            jfact.Factura("0001");
            //jfact.agregarProducto("Prueba item 1",1,250);
            jfact.agregarServicio("Prueba item 2",1,200,50);
            jfact.agregarAdenda("01");

            jfact.json();
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean creaJSONFijoCredito() throws JSONException {
        JSONObject jsitem;

        jsdoc = new JSONObject();

        JSONObject jshead = new JSONObject();
        jshead.put("tipo_dte","03");
        jshead.put("establecimiento","0001");
        jshead.put("condicion_pago",2);

        JSONObject jsr = new JSONObject();
        jsr.put("tipo_documento","36");
        jsr.put("numero_documento","11111111111128");
        jsr.put("nrc","2247806");
        jsr.put("nombre","Receptor Prueba");
        jsr.put("codigo_actividad","61101");
        jsr.put("nombre_comercial","Prueba Receptor");

        JSONObject jsrd = new JSONObject();
        jsrd.put("departamento","06");
        jsrd.put("municipio","14");
        jsrd.put("complemento","Calle 2");
        jsr.put("direccion",jsrd);

        jsr.put("correo","pruebas@pruebas.com");
        jshead.put("receptor",jsr);


        JSONArray jsitems=new JSONArray();

        jsitem = new JSONObject();

        jsitem.put("tipo", 1);
        jsitem.put("cantidad", 1);
        jsitem.put("unidad_medida", 59);
        //jsitem.put("descuento", 25);
        jsitem.put("descripcion", "Prueba item 1");
        jsitem.put("precio_unitario", 100);

        JSONArray jstrib=new JSONArray();

        double tmonto=13;

        JSONObject jst = new JSONObject();
        jst.put("codigo","20");
        jst.put("monto",tmonto);
        jstrib.put(jst);

        jsitem.put("tributos",jstrib);

        jsitems.put(jsitem);

        jshead.put("items",jsitems);

        jsdoc.put("documento",jshead);

        String js=jsdoc.toString();
        return true;
    }

    @Override
    public void felCallBack()  {
        try {

            switch (callbackmode) {
                case 1:
                    callbackCert();break;
                case 2:
                    callbackAnul();break;
                case 3:
                    callbackCont();break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void callbackCert() {
        try {
            if (!FactESA.errorflag) {
                msgbox(FactESA.estado+"\n"+FactESA.respuesta.totalPagar);
                //marcaFactura();
            } else {
                msgbox("Error: "+FactESA.error);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void callbackAnul() {
        try {
            if (!AnulESA.errorflag) {
                msgbox(AnulESA.mensaje);
                //MarcaAnulado
            } else {
                msgbox(AnulESA.error);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void callbackCont() {
        try {
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    //region Download


    private void downloadFile() {
        try {
            DownloadFileTask downloadFileTask = new DownloadFileTask();
            downloadFileTask.execute();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public class DownloadFileTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                FileOutputStream fileOutput = new FileOutputStream(pdffile);
                InputStream inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[1024];
                int bufferLength;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }

                fileOutput.close();
                inputStream.close();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            shareFile();
            //toast("DWN");
        }
    }

    private void shareFile() {
        String ftype;

        try {

            ftype="application/pdf";

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            if (!pdffile.exists()) {
                msgbox("Could not open report file");return;
            }

            Uri uri = FileProvider.getUriForFile(this,getPackageName()+".provider",pdffile);

            Intent intent = ShareCompat.IntentBuilder.from(FELESATest.this)
                    .setType(ftype)
                    .setStream(uri)
                    .setChooserTitle("Seleccione...")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    private void shareFileo() {
        try {
            toast("downloaded");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void downloadFileOld(String fileUrl, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();
        try {
            D_facturaObj.reconnect(Con,db);
            D_facturadObj.reconnect(Con,db);
            D_facturafObj.reconnect(Con,db);
            D_factura_svObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

}