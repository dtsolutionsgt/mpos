package com.dtsgt.fel;


// InFile FEL Factura

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.DateUtils;
import com.dtsgt.classes.clsP_tipo_contribuyenteObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class clsFELInFile {

    public String error,fecha_factura;
    public Boolean errorflag,errorcon,constat,duplicado,modoiduni,iduniflag;
    public Boolean errfirma = false;
    public Boolean errcert =false;
    public Boolean halt=false;
    public boolean autocancel=false;
    public boolean factura_credito=false;
    public String factura_abono_venc;
    public int errlevel, responsecode;
    public long idcontingencia;

    public String xml,xmlanul,tipo_nit;
    public String fact_uuid,fact_serie,fact_numero;
    public String ret_uuid,ret_serie,ret_numero;

    public int timeout=2000;
    public FELFactura owner;

    public long ftime1=1,ftime2=-1,ftime3=-1;

    private DateUtils DU = new DateUtils();;

    // Parametrizacion FEL

    public String fel_codigo_establecimiento,
            fel_usuario_firma,
            fel_usuario_certificacion,
            fel_llave_firma,
            fel_llave_certificacion,
            fel_afiliacion_iva,
            fel_tipo_documento,
            codigo_postal,
            mpos_identificador_fact,
            fel_nit,
            fel_correo,
            fel_nombre_comercial, correo_sucursal;
    public int fraseIVA,fraseISR;
    public long fechaf_y,fechaf_m,fechaf_d;

    // Private declarations

    private Context cont;
    private PBase parent;

    private JSONObject jsonf = new JSONObject();
    private JSONObject jsonc = new JSONObject();
    private JSONObject jsonfa = new JSONObject();
    private JSONObject jsona = new JSONObject();
    private JSONObject jsoniu = new JSONObject();

    private AsyncCallWS wstask;

    private Handler  halttimer;
    private Runnable haltrunner;

    private String s64, jsfirm,jscert,jsanul,jsidu,firma,str_propina;
    private double imp,totmonto,totiva,monto_propina;
    private int linea;
    private boolean firmcomplete,certcomplete;
    boolean factura_credito=false;

    // Configuracion

    private double iva=12;

    private String WSURL="https://signer-emisores.feel.com.gt/sign_solicitud_firmas/firma_xml";
    private String WSURLCert="https://certificador.feel.com.gt/fel/certificacion/v2/dte/";
    private String WSURLAnul="https://certificador.feel.com.gt/fel/anulacion/v2/dte/";
    private String WSURLIdUni="https://certificador.feel.com.gt/fel/consulta/dte/v2/identificador_unico";
    private TextView lblProgress;

    public clsFELInFile(Context context, PBase Parent,int conTimeout) {

        cont=context;
        parent=Parent;
        timeout=conTimeout;

        errlevel=0;
        error="";
        errorflag=false;
        errorcon=false;

        factura_credito=false;

        try {
            lblProgress = ((Activity)context).findViewById(R.id.msgHeader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLabel(String msg) {

        try {

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = () -> handler.postDelayed(() -> {
                if (lblProgress!=null){
                    try {
                        lblProgress.setText(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },200);
            new Thread(runnable).start();

            //Looper.loop();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void certificacion() {

        try {

            fact_uuid="";
            fact_serie="";
            fact_numero="";
            errlevel=1;
            error="";
            errorflag=false;
            constat=true;
            errorcon=false;
            duplicado=false;

            sendJSONFirm();

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }

    }

    public void anulacion(String fuuid) {
        try {
            fact_uuid=fuuid;
            errlevel=3;
            error="";
            errorflag=false;
            constat=true;
            sendJSONFirmAnul();
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    //region Firma

    private void sendJSONFirm() {

        try {

            s64=toBase64();

            jsonf = new JSONObject();
            jsonf.put("llave", fel_llave_firma);
            jsonf.put("archivo",s64);
            jsonf.put("codigo",fel_codigo_establecimiento);
            jsonf.put("alias",fel_usuario_certificacion);
            jsonf.put("es_anulacion","N");

            Date currentTime = Calendar.getInstance().getTime();

            executeWSFirm();

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    private void executeWSFirm() throws Exception {

        try {

            jsfirm = jsonf.toString();
            errorflag=false;
            error="";

            firmcomplete=false;
            halt=false;

            updateLabel("Firmando documento ...");

            wstask = new AsyncCallWS();
            wstask.execute();

        } catch (Exception e) {
            throw e;
        }

    }

    private Boolean wsExecuteF(){

        URL url;
        HttpURLConnection connection = null;
        JSONObject jObj = null;
        responsecode =0;
        error = "";
        errfirma=false;
        errorflag = false;
        modoiduni=false;

        try {

            url = new URL(WSURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
            connection.setRequestProperty("Content-Length",""+ jsfirm.getBytes().length);
            connection.setRequestProperty("usuario",fel_usuario_certificacion);
            connection.setRequestProperty("llave", fel_llave_certificacion);
            connection.setRequestProperty("identificador", mpos_identificador_fact);
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //#EJC202212131029: Agregué SocketTimeoutException.
            updateLabel("Firma: Conectando a Infile...");

            try {
                connection.connect();
            } catch (SocketTimeoutException s){
                error="ERROR_202212140931 " + s.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            } catch (IOException e) {
                error=e.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            }

            DataOutputStream wr = null;

            updateLabel("Firma: Procesando respuesta...");

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (SocketTimeoutException s){
                error="ERROR_202212140931A " + s.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            } catch (IOException e) {
                error=e.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return null;
            }

            wr.writeBytes (jsfirm);
            wr.flush ();
            wr.close ();

            InputStream is;

            updateLabel("Firma: Respuesta obtenida...");

            try {
                is= connection.getInputStream();
            } catch (SocketTimeoutException s){
                error="ERROR_202212140931B " + s.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            } catch (IOException e) {
                is= connection.getInputStream();
            }

            responsecode =connection.getResponseCode();

            if (responsecode ==200) {

                updateLabel("Firma: Respuesta: Ok.");

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();
                jObj = new JSONObject(jstr);

                error= jObj.getString("descripcion");

                if (jObj.getBoolean("resultado")) {
                    errorflag=false;
                    firma=jObj.getString("archivo");
                } else {

                    errorflag=true;

                    try {

                        String vResultado="";
                        String vDescripcion="";

                        try {
                            vResultado =jObj.getString("resultado");
                            vDescripcion =jObj.getString("descripcion");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!vResultado.equalsIgnoreCase("") || !vDescripcion.equalsIgnoreCase("")){
                            error+=vDescripcion;
                        }else{
                            //#EJC20200707: Obtener mensaje de error específico en respuesta.
                            JSONArray ArrayError=jObj.getJSONArray("descripcion_errores");

                            for (int i=0; i<ArrayError.length(); i++) {
                                JSONObject theJsonObject = ArrayError.getJSONObject(i);
                                String name = theJsonObject.getString("mensaje_error");
                                error = name;
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                error=""+ responsecode;errorflag=true;errfirma=true;
                updateLabel("Firma: Error en respuesta: " + responsecode);
                return errorflag;
            }

        } catch (SocketTimeoutException s){
            error="ERROR_202212140931C " + s.getMessage();
            errorcon=true;errorflag=true;constat=false;
            return errorflag;
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;errfirma=true;
            return errorflag;
        } finally {

        }
        return errorflag;
    }

    private void wsFinishedF() {

        try  {

            firmcomplete=true;

            if (halt) {
                errorflag=true;error="Interrupido por usuario";
                parent.felCallBack();
            } else {

                if (!errorflag && !errcert) {

                    Date currentTime = Calendar.getInstance().getTime();

                    updateLabel("Enviando documento firmado");

                    sendJSONCert();

                    try {
                        updateLabel("Enviando Certificación ...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    parent.felCallBack();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)  {
            try  {
                wsExecuteF();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorflag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (!errorflag){
                    wsFinishedF();
                }else{
                    errcert = errorflag;;
                    parent.felCallBack();
                    return;
                }
            } catch (Exception e)  {
               e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            try {
                updateLabel("Procesando Certificación: " + DU.sfecha(DU.getActDateTime())+" "+DU.shora(DU.getActDateTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                updateLabel("Certificando: "+ DU.getActDateTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            try {
                firmcomplete=true;
                errorflag=true;error+="Se agotó tiempo de certificación";
                parent.felCallBack();
            } catch (Exception e) {
                String ss=e.getMessage();
                ss=ss+"";
            }

            super.onCancelled();

        }

    }

    //endregion

    //region Certificacion

    public void sendJSONCert() {

        errlevel=2;

        try {
            Date systemTime=Calendar.getInstance().getTime();
            ftime2=systemTime.getTime();
        } catch (Exception e) {}

        try {

            jsonc = new JSONObject();
            jsonc.put("nit_emisor",fel_nit);
            jsonc.put("correo_copia",fel_correo);
            jsonc.put("xml_dte",firma);

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                try {
                    executeWSCert();
                } catch (Exception e) {
                    error=e.getMessage();errorflag=true;;
                }
            };
            mtimer.postDelayed(mrunner,100);

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    public void executeWSCert() throws Exception {

        try {

            jscert = jsonc.toString();
            errorflag=false;
            error="";

            updateLabel("Certificando Factura...");

            AsyncCallWSCert wstask = new AsyncCallWSCert();
            wstask.execute();

        } catch (Exception e) {
            throw e;
        }
    }

    public Boolean wsExecuteC(){

        URL url;
        HttpsURLConnection connection = null;
        JSONObject jObj = null;
        responsecode =0;

        errcert=false;
        errorflag =false;
        duplicado=false;

        try {

            url = new URL(WSURLCert);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("usuario",fel_usuario_certificacion);
            connection.setRequestProperty("llave", fel_llave_certificacion);
            connection.setRequestProperty("identificador", mpos_identificador_fact);
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = null;

            updateLabel("Cert: Conectando a Infile.");

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (SocketTimeoutException s){
                error="ERROR_202212140931D " + s.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            } catch (IOException e) {
                error="No hay conexión al internet";
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            }

            updateLabel("Cert: Enviando documento.");

            wr.writeBytes (jscert);
            wr.flush ();
            wr.close ();

            InputStream is=null;

            updateLabel("Cert: Respuesta obtenida.");

            try {
                is= connection.getInputStream();
            } catch (SocketTimeoutException s){
                error="ERROR_202212140931E " + s.getMessage();
                errorcon=true;errorflag=true;constat=false;
                return errorflag;
            } catch (IOException e) {
                try {
                    is= connection.getInputStream();
                } catch (IOException ee) {
                    try {
                        is= connection.getInputStream();
                    } catch (IOException eee) {
                        errcert=true;
                    }
                }
            }

            responsecode =connection.getResponseCode();

            if (errcert) return errcert;

            if (responsecode ==200) {

                updateLabel("Cert: Respuesta: OK." );

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();

                int duplidx=jstr.indexOf("Documento enviado previamente.");

                jObj = new JSONObject(jstr);

                error=jObj.getString("descripcion");

                fact_uuid =jObj.getString("uuid");
                fact_serie =jObj.getString("serie");
                fact_numero =jObj.getString("numero");
                ret_uuid=jObj.getString("uuid");
                ret_serie=jObj.getString("serie");
                ret_numero=jObj.getString("numero");
                errcert = false;
                errorflag =false;

                updateLabel("Factura Certificada: " + fact_numero);

                if (duplidx>1) {
                    //#EJC20200710: No firmar factura con un identificador previamente enviado...
                    duplicado=true;
                    errorflag =false;
                    errcert =false;
                    error ="El identificador interno: "+ mpos_identificador_fact + " fue enviado previamente.";
                    return errcert;
                }

                if (!jObj.getBoolean("resultado")) {

                    errorflag=true;
                    errcert=true;

                    try {
                        //#EJC20200707: Obtener mensaje de error específico en respuesta.
                        JSONArray ArrayError=jObj.getJSONArray("descripcion_errores");
                        for (int i=0; i<ArrayError.length(); i++) {
                            JSONObject theJsonObject = ArrayError.getJSONObject(i);
                            String name = theJsonObject.getString("mensaje_error");
                            error += name;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return errorflag;
                }

                fact_uuid =jObj.getString("uuid");
                fact_serie =jObj.getString("serie");
                fact_numero =jObj.getString("numero");

                errorflag=false;
                errcert=false;
                duplicado=false;

            } else {
                updateLabel("Error al certificar: " + responsecode);
                error=""+ responsecode;errorflag=true;errcert=true;
                return errorflag;
            }
        } catch (SocketTimeoutException s){
            error= "ERROR_202212140931F " + s.getMessage();
            errorcon=true;errorflag=true;constat=false;
            return errorflag;
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;errcert=true;
        } finally {
            //if (connection!=null) connection.disconnect();
        }
        return errorflag;
    }

    public void wsFinishedC() throws Exception {

        try  {
            if (!errcert) {
                //#EJC202212131330:Redundancia en la consulta, de todas formas da error dentro.
                //sendJSONIDUnico();
                errorflag=errcert;error="";
                parent.felCallBack();
            } else if(errorflag||errcert) {
                parent.felCallBack();
                throw new Exception("Error al certificar documento: " + error);
            }
        } catch (Exception e)  {
            throw e;
        }
    }

    private class AsyncCallWSCert extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)  {

            try  {
                wsExecuteC();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorflag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (!errcert && !result){
                    wsFinishedC();
                }else{
                    parent.felCallBack();
                }
            } catch (Exception e)  {
                errcert = result;
                errorflag = result;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

    //endregion

    //region ID Unico

    public void sendJSONIDUnico() {

        String fnit=fel_nit.replace("-","");

        errlevel=4;

        //mpos_identificador_fact="A026610";
        modoiduni=true;iduniflag=false;

        try {

            jsoniu = new JSONObject();
            jsoniu.put("nit_emisor",fnit);
            jsoniu.put("tipo_operacion","CERTIFICACION");

            if (factura_credito) {
                jsoniu.put("tipo_documento","FCAM");
            } else {
                jsoniu.put("tipo_documento","FACT");
            }
            jsoniu.put("codigo_establecimiento",""+fel_codigo_establecimiento);
            jsoniu.put("identificador_unico_dte",mpos_identificador_fact);
            jsoniu.put("anio_documento",""+fechaf_y);
            jsoniu.put("mes_documento",""+fechaf_m);
            jsoniu.put("dia_documento",""+fechaf_d);

            Handler mtimer = new Handler();

            Runnable mrunner= () -> executeWSIDUnico();
            mtimer.postDelayed(mrunner,1000);

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    public void executeWSIDUnico() {

        jsidu = jsoniu.toString();
        errorflag=false;error="";

        AsyncCallWSIDUnico wstask = new AsyncCallWSIDUnico();
        wstask.execute();
    }

    public void wsExecuteIDu(){

        URL url;
        HttpsURLConnection connection = null;
        JSONObject jObj = null;
        responsecode =0;

        errcert=false;

        try {

            url = new URL(WSURLIdUni);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout *2);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("usuario",fel_usuario_certificacion);
            connection.setRequestProperty("llave", fel_llave_certificacion);
            connection.setRequestProperty("identificador", mpos_identificador_fact);
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (IOException e) {
                error="No hay conexión al internet";
                errorcon=true;errorflag=true;constat=false;return;
            }

            wr.writeBytes (jsidu);
            wr.flush ();
            wr.close ();

            InputStream is= connection.getInputStream();

            responsecode =connection.getResponseCode();

            if (responsecode ==202) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();

                jObj = new JSONObject(jstr);

                String rslt=jObj.getString("resultado");

                if (rslt.equalsIgnoreCase("false")) {
                    errorflag=true;return;
                } else iduniflag=true;

                fact_uuid =jObj.getString("uuid");
                fact_serie =jObj.getString("serie");
                fact_numero =jObj.getString("numero");
                errorflag=false;

            } else {
                errorflag=true;return;
            }
        } catch (Exception e) {
            //errorflag=true;
        } finally {
            //if (connection!=null) connection.disconnect();
        }
    }

    public void wsFinishedIDu() {
        try  {
            parent.felCallBack();
        } catch (Exception e)  { }
    }

    private class AsyncCallWSIDUnico extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)  {
            try  {
                wsExecuteIDu();
            } catch (Exception e) {
                throw e;
            }
            return errorflag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                wsFinishedIDu();
            } catch (Exception e)  {
                throw e;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

    //region Firma Anulacion

    private void sendJSONFirmAnul() {
        try {

            s64=anulToBase64();

            jsonf = new JSONObject();
            jsonf.put("llave", fel_llave_firma);
            jsonf.put("archivo",s64);
            jsonf.put("codigo",fel_codigo_establecimiento);
            jsonf.put("alias",fel_usuario_certificacion);
            jsonf.put("es_anulacion","S");

            executeWSFirmAnul();

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    private void executeWSFirmAnul() {

        jsfirm = jsonf.toString();
        errorflag=false;error="";

        AsyncCallWSFA wstask = new AsyncCallWSFA();
        wstask.execute();
    }

    private void wsExecuteFA(){

        URL url;
        HttpURLConnection connection = null;
        JSONObject jObj = null;

        try {

            url = new URL(WSURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Content-Length",""+Integer.toString(jsfirm.getBytes().length));
            connection.setRequestProperty("usuario",fel_usuario_certificacion);
            connection.setRequestProperty("llave", fel_llave_certificacion);
            connection.setRequestProperty("identificador", mpos_identificador_fact);

            //#EJC20200707: Set Timeout
            //connection.setConnectTimeout(mTimeOut);
            //connection.setReadTimeout(mTimeOut);

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (IOException e) {
                error="No hay conexión al internet";
                errorcon=true;errorflag=true;constat=false;return;
            }

            wr.writeBytes(jsfirm);
            wr.flush ();
            wr.close ();

            InputStream is = connection.getInputStream();

            int response=connection.getResponseCode();

            if (response==200) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();
                jObj = new JSONObject(jstr);

                error= jObj.getString("descripcion");

                if (jObj.getBoolean("resultado")) {
                    errorflag=false;
                    firma=jObj.getString("archivo");
                } else {
                    errorflag=true;
                    try {
                        //#EJC20200707: Obtener mensaje de error específico en respuesta.
                        JSONArray ArrayError=jObj.getJSONArray("descripcion_errores");

                        for (int i=0; i<ArrayError.length(); i++) {
                            JSONObject theJsonObject = ArrayError.getJSONObject(i);
                            String name = theJsonObject.getString("mensaje_error");
                            error = name;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                error=""+response;errorflag=true;return;
            }
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    private void wsFinishedFA() {
        try  {
            if (!errorflag) {
                sendJSONAnul();
            } else {
                parent.felCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncCallWSFA extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecuteFA();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinishedFA();
            } catch (Exception e)  {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

    //region Anulacion

    public void sendJSONAnul() {

        try {

            errlevel=4;

            s64= anulToBase64();

            jsona = new JSONObject();
            jsona.put("nit_emisor",fel_nit);
            jsona.put("correo_copia","demo@demo.com.gt");
            jsona.put("xml_dte",firma);

            executeWSAnul();

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    public void executeWSAnul() {

        jsanul = jsona.toString();
        errorflag=false;error="";

        AsyncCallWSAnul wstask = new AsyncCallWSAnul();
        wstask.execute();
    }

    public void wsExecuteA(){

        URL url;
        HttpsURLConnection connection = null;
        JSONObject jObj = null;

        try {
            //Create connection
            url = new URL(WSURLAnul);

            connection = (HttpsURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout*2);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
            connection.setRequestProperty("Content-Length",""+Integer.toString(jsanul.getBytes().length));
            connection.setRequestProperty("usuario",fel_usuario_certificacion);
            connection.setRequestProperty("llave", fel_llave_certificacion);
            connection.setRequestProperty("identificador", mpos_identificador_fact);

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (IOException e) {
                error="No hay conexión al internet";
                errorcon=true;errorflag=true;constat=false;return;
            }

            wr.writeBytes (jsanul);
            wr.flush ();
            wr.close ();

            InputStream is = connection.getInputStream();

            int response=connection.getResponseCode();

            if (response==200) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();
                jObj = new JSONObject(jstr);

                try {
                    error= jObj.getString("descripcion");
                } catch (Exception e) {
                    error= e.getMessage();
                }

                if (jObj.getBoolean("resultado")) {
                    errorflag=false;
                } else {

                    errorflag=true;

                    try {
                        //#EJC20200707: Obtener mensaje de error específico en respuesta.
                        JSONArray ArrayError=jObj.getJSONArray("descripcion_errores");

                        for (int i=0; i<ArrayError.length(); i++) {
                            JSONObject theJsonObject = ArrayError.getJSONObject(i);
                            String name = theJsonObject.getString("mensaje_error");
                            error = name;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                error=""+response;errorflag=true;return;
            }
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    public void wsFinishedA() {
        try  {
            parent.felCallBack();
        } catch (Exception e)  { }
    }

    private class AsyncCallWSAnul extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecuteA();
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinishedA();
            } catch (Exception e)  {}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

    //region XML

    public void iniciar(long fecha_emision,String idconting) {
        String sf=parseDate(fecha_emision);
        String tipodoc;

        fecha_factura=sf;
        errlevel=1;
        imp=0;linea=0;totmonto=0;totiva=0;

        //#EJC20200527: Versión 1.5.3 de FEL
        xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml+="<dte:GTDocumento xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:dte=\"http://www.sat.gob.gt/dte/fel/0.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"0.1\" xsi:schemaLocation=\"http://www.sat.gob.gt/dte/fel/0.2.0 \">";
        xml+="<dte:SAT ClaseDocumento=\"dte\">";
        xml+="<dte:DTE ID=\"DatosCertificados\">";
        xml+="<dte:DatosEmision ID=\"DatosEmision\">";

        tipodoc=fel_tipo_documento;

        /*
        String tipodoc = "FACT";
        if (fel_afiliacion_iva.equals("PEQ")) tipodoc = "FPEQ";
        */

        if (idconting.isEmpty() || idconting.equalsIgnoreCase("0") ) { // sin contingencia
            if (factura_credito) {
                xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" Tipo=\"FCAM\"></dte:DatosGenerales>";
            } else {

                if (fel_afiliacion_iva.equals("PEQ")) {
                    xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" Tipo=\"FPEQ\"></dte:DatosGenerales>";
                } else {
                    xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" Tipo=\"FACT\"></dte:DatosGenerales>";
                }
            }
        } else { // con contingencia
            if (factura_credito) {
                xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" NumeroAcceso=\""+idconting+"\" Tipo=\"FCAM\"></dte:DatosGenerales>";
            } else {
                if (fel_afiliacion_iva.equals("PEQ")) {
                    xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" NumeroAcceso=\""+idconting+"\" Tipo=\"FPEQ\"></dte:DatosGenerales>";
                } else {
                    xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" NumeroAcceso=\""+idconting+"\" Tipo=\"FACT\"></dte:DatosGenerales>";
                }
             }
         }

    }

    public void completar(String serie,long numero) {

        String totIvaStr = String.format("%.2f", totiva);
        totIvaStr=totIvaStr.replaceAll(",",".");

        serie=serie+"-"+numero;
        totmonto=round2(totmonto);
        String totmontoStr=String.format("%.2f", totmonto);
        totmontoStr=totmontoStr.replaceAll(",",".");

        xml+="</dte:Items>";

        xml+="<dte:Totales>";

        if (fel_afiliacion_iva.equals("GEN")){
            xml+="<dte:TotalImpuestos>";
            xml+="<dte:TotalImpuesto NombreCorto=\"IVA\" TotalMontoImpuesto=\""+totIvaStr+"\"></dte:TotalImpuesto>";
            xml+="</dte:TotalImpuestos>";
        }

        xml+="<dte:GranTotal>"+totmontoStr+"</dte:GranTotal>";
        xml+="</dte:Totales>";

        if (factura_credito) {
            xml+="<dte:Complementos>";
            xml+="<dte:Complemento NombreComplemento=\"AbonosFacturaCambiaria\" URIComplemento=\"http://www.sat.gob.gt/dte/fel/0.2.0\">";
            xml+="<cfc:AbonosFacturaCambiaria Version=\"1\" xsi:schemaLocation=\"http://www.sat.gob.gt/dte/fel/CompCambiaria/0.1.0\" xmlns:cfc=\"http://www.sat.gob.gt/dte/fel/CompCambiaria/0.1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
            xml+="<cfc:Abono>";
            xml+="<cfc:NumeroAbono>1</cfc:NumeroAbono>";
            xml+="<cfc:FechaVencimiento>"+factura_abono_venc+"</cfc:FechaVencimiento>";
            xml+="<cfc:MontoAbono>"+totmontoStr+"</cfc:MontoAbono>";
            xml+="</cfc:Abono>";
            xml+="</cfc:AbonosFacturaCambiaria>";
            xml+="</dte:Complemento>";
            xml+="</dte:Complementos>";
        }

        xml+="</dte:DatosEmision>";
        xml+="</dte:DTE>";

        if (monto_propina > 0) {
            xml += "<dte:Adenda>";
            xml += "<Propina>" + str_propina + "</Propina>";
            xml += "</dte:Adenda>";
        }

        xml+="</dte:SAT>";
        xml+="</dte:GTDocumento>";

        String xs=xml;
    }

    public String toBase64() {
        String s64;
        byte[] bytes= new byte[0];

        bytes = xml.getBytes(StandardCharsets.UTF_8);
        s64= Base64.encodeToString(bytes, Base64.DEFAULT);
        return s64;
    }

    public String anulToBase64() {

        String s64;
        byte[] bytes= new byte[0];

        bytes = xmlanul.getBytes(StandardCharsets.UTF_8);
        s64= Base64.encodeToString(bytes, Base64.DEFAULT);
        return s64;

    }

    public static String escape_caracteres_sat(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public void emisor(String afiliacionIVA,
                       String codigoEstablecimiento,
                       String correoEmisor,
                       String nitEmisor,
                       String nombreComercial,
                       String nombreEmisor) {

        //#EJC20200708: Quitar guión de NIT emisor.
        nitEmisor= nitEmisor.replace("-","");

        String nom=escape_caracteres_sat(nombreEmisor);
        String nomcom=escape_caracteres_sat(nombreComercial);

        xml+="<dte:Emisor AfiliacionIVA=\""+afiliacionIVA+"\" " +
                "CodigoEstablecimiento=\""+codigoEstablecimiento+"\" " +
                "CorreoEmisor=\""+correoEmisor+"\" " +
                "NITEmisor=\""+nitEmisor+"\" " +
                "NombreComercial=\""+nomcom+"\" " +
                "NombreEmisor=\""+nom+"\">";
    }

    public void emisorDireccion(String direccion,
                                String codigoPostal,
                                String municipio,
                                String departamento,
                                String pais) {

        xml+="<dte:DireccionEmisor>";
        xml+="<dte:Direccion>"+direccion+"</dte:Direccion>";
        xml+="<dte:CodigoPostal>"+codigoPostal+"</dte:CodigoPostal>";
        xml+="<dte:Municipio>"+municipio+"</dte:Municipio>";
        xml+="<dte:Departamento>"+departamento+"</dte:Departamento>";
        xml+="<dte:Pais>"+pais+"</dte:Pais>";
        xml+="</dte:DireccionEmisor>";

        xml+="</dte:Emisor>";
    }

    public void receptor(String pNITCliente,
                         String pNombreCliente,
                         String pDireccionCliente,
                         String pCorreo,
                         String codigoPostal,
                         String municipio,
                         String departamento,
                         String pais,
                         String tipoNIT) {

        try {

            String stt=pCorreo,TE;

            pCorreo=pCorreo.replace(" ","");
            if (pCorreo.indexOf(".")<3)  pCorreo="";
            if (pCorreo.indexOf("@")<3)  pCorreo="";
            if (pCorreo.isEmpty())  pCorreo = correo_sucursal;
            if (pCorreo.length()<8) pCorreo = correo_sucursal;

            try {
                if (pNITCliente.length()<6) pNITCliente="CF";
            } catch (Exception e) {
                pNITCliente="CF";
            }

            pNombreCliente=pNombreCliente.trim();
            if (pNombreCliente.isEmpty() | pNombreCliente.equalsIgnoreCase("\n") |
                    pNombreCliente.equalsIgnoreCase(" ") | pNombreCliente.equalsIgnoreCase("  "))  pNombreCliente="Consumidor final";

            String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
            pDireccionCliente = pDireccionCliente.replaceAll(characterFilter,"");

            if (tipoNIT.equalsIgnoreCase("C")) {
                TE="CUI";
                xml+="<dte:Receptor CorreoReceptor=\""+pCorreo+"\" IDReceptor=\""+pNITCliente+"\" TipoEspecial=\""+TE+"\" NombreReceptor=\""+pNombreCliente+"\">";
            } else if (tipoNIT.equalsIgnoreCase("E")) {
                TE="EXT";
                xml+="<dte:Receptor CorreoReceptor=\""+pCorreo+"\" IDReceptor=\""+pNITCliente+"\" TipoEspecial=\""+TE+"\" NombreReceptor=\""+pNombreCliente+"\">";
            } else {
                TE="NIT";
                xml+="<dte:Receptor CorreoReceptor=\""+pCorreo+"\" IDReceptor=\""+pNITCliente+"\" NombreReceptor=\""+pNombreCliente+"\">";
            }

            xml+="<dte:DireccionReceptor>";
            xml+="<dte:Direccion>"+pDireccionCliente+"</dte:Direccion>";
            xml+="<dte:CodigoPostal>"+codigoPostal+"</dte:CodigoPostal>";
            xml+="<dte:Municipio>"+municipio+"</dte:Municipio>";
            xml+="<dte:Departamento>"+departamento+"</dte:Departamento>";
            xml+="<dte:Pais>"+pais+"</dte:Pais>";
            xml+="</dte:DireccionReceptor>";
            xml+="</dte:Receptor>";

            xml+="<dte:Frases>";

            if (fraseISR==4) {
                xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"1\"></dte:Frase>";
                xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"2\"></dte:Frase>";
            } else if (fraseISR==3) {
                xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"3\"></dte:Frase>";
            } else {

                if (fraseIVA==3) {
                    xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"3\"></dte:Frase>";
                } else {
                    if (fraseISR!=0) {
                        xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"" + fraseISR +"\"></dte:Frase>";
                    }
                    if (fraseIVA!=0) {
                        xml+="<dte:Frase CodigoEscenario=\"2\" TipoFrase=\"" + fraseIVA +"\"></dte:Frase>";
                    }
                }

            }
            xml+="</dte:Frases>";
            xml+="<dte:Items>";

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
            e.printStackTrace();
        }

    }


    public void detalle(String descrip,double cant,String unid,double precuni,double total,double desc,String lcombo) {
        double imp,impbase,tottot;

        linea++;

        desc=Math.round(desc*100);desc=desc/100;

        double parametroiva1 =(iva/100);
        double parametroiva2 = (1+parametroiva1);
        impbase=total/parametroiva2;
        imp=impbase*parametroiva1;
        if (!lcombo.isEmpty()) descrip+=lcombo;

        tottot=total+desc;

        totmonto+=total;

        String cantstr = String.format("%.2f",cant);
        cantstr=cantstr.replaceAll(",",".");
        String precunistr = String.format("%.2f",precuni);
        precunistr=precunistr.replaceAll(",",".");
        String tottotstr = String.format("%.2f",tottot);
        tottotstr=tottotstr.replaceAll(",",".");
        String descstr = String.format("%.2f",desc);
        descstr=descstr.replaceAll(",",".");

        xml+="<dte:Item BienOServicio=\"B\" NumeroLinea=\""+linea+"\">";
        xml+="<dte:Cantidad>"+cantstr+"</dte:Cantidad>";
        xml+="<dte:UnidadMedida>"+unid+"</dte:UnidadMedida>";
        xml+="<dte:Descripcion>"+descrip+"</dte:Descripcion>";
        xml+="<dte:PrecioUnitario>"+precunistr+"</dte:PrecioUnitario>";
        xml+="<dte:Precio>"+tottotstr+"</dte:Precio>";
        xml+="<dte:Descuento>"+descstr+"</dte:Descuento>";

        String impbasestr = String.format("%.2f",impbase);
        impbasestr=impbasestr.replaceAll(",",".");
        String impstr = String.format("%.2f",imp);
        impstr=impstr.replaceAll(",",".");


        if (fel_afiliacion_iva.equals("GEN")){

            xml+="<dte:Impuestos>";
            xml+="<dte:Impuesto>";
            xml+="<dte:NombreCorto>IVA</dte:NombreCorto>";
            xml+="<dte:CodigoUnidadGravable>1</dte:CodigoUnidadGravable>";
            xml+="<dte:MontoGravable>"+impbasestr+"</dte:MontoGravable>";
            xml+="<dte:MontoImpuesto>"+impstr+"</dte:MontoImpuesto>";
            xml+="</dte:Impuesto>";
            xml+="</dte:Impuestos>";

        }

        String totalstr = String.format("%.2f",total);
        totalstr=totalstr.replaceAll(",",".");

        xml+="<dte:Total>"+totalstr+"</dte:Total>";
        xml+="</dte:Item>";

        totiva+=Double.valueOf(impstr);

    }

    public void detalle_propina(double cant) {
        monto_propina=cant;
        str_propina= String.format("%.2f",monto_propina);
    }

    public void guardar(String filename) throws IOException {

        BufferedWriter writer = null,lwriter = null;
        FileWriter wfile,lfile;

        wfile=new FileWriter(filename,false);
        writer = new BufferedWriter(wfile);
        writer.write(xml);
        writer.close();

    }

    public void anulfact(String uuid,String NITEmisor,String Idreceptor,long fechaemis,long fechaanul) {

        String sf=parseDate(fechaemis);
        String sa=parseDate(fechaanul);

        //#EJC20200708: Quitar guion a NIT.
        NITEmisor = NITEmisor.replace("-","");

        xmlanul="<dte:GTAnulacionDocumento xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:dte=\"http://www.sat.gob.gt/dte/fel/0.1.0\" xmlns:n1=\"http://www.altova.com/samplexml/other-namespace\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"0.1\" xsi:schemaLocation=\"http://www.sat.gob.gt/dte/fel/0.1.0\">" +
        "<dte:SAT>" +
        "<dte:AnulacionDTE ID=\"DatosCertificados\">" +
        "<dte:DatosGenerales FechaEmisionDocumentoAnular=\""+sf+"\" FechaHoraAnulacion=\""+sa+"\" ID=\"DatosAnulacion\" " +
        "IDReceptor=\""+Idreceptor+"\" MotivoAnulacion=\"PRUEBA DE ANULACIÓN\" NITEmisor=\""+NITEmisor+"\" NumeroDocumentoAAnular=\""+uuid+"\"></dte:DatosGenerales>" +
        "</dte:AnulacionDTE>" +
        "</dte:SAT>" +
        "</dte:GTAnulacionDocumento>";
    }

    //endregion

    //region Private

    private String parseDate(long dd) {
        String s1,s2,s3,s4,s5,sf,ss=""+dd;
        // "2019-10-07T07:08:29-06:00"

        s1="20"+ss.substring(0,2);
        s2=ss.substring(2,4);
        s3=ss.substring(4,6);
        s4=ss.substring(6,8);
        s5=ss.substring(8,10);

        sf=s1+"-"+s2+"-"+s3+"T"+s4+":"+s5+":00-06:00";
        return sf;
    }

    protected void toast(String msg) {
        Toast toast= Toast.makeText(cont,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void toastlong(String msg) {
        Toast toast= Toast.makeText(cont,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public double round2(double val){
        int ival;

        val=(double) (100*val);
        double rslt=Math.round(val);
        rslt=Math.floor(rslt);

        ival=(int) rslt;
        rslt=(double) ival;

        return (double) (rslt/100);
    }

    //endregion

}
