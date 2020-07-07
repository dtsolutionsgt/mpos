package com.dtsgt.fel;


// InFile FEL Factura

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.mpos.PBase;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class clsFELInFile {

    public String  error, fecha_factura;
    public Boolean errorflag,errorcon,constat,duplicado;
    public int errlevel, idcontingencia;

    public String xml,xmlanul;
    public String fact_uuid;
    public String fact_serie;
    public int fact_numero;

    // Parametrizacion FEL

    public String fel_codigo_establecimiento,
            fel_usuario_firma,
            fel_usuario_certificacion,
            fel_llave_firma,
            fel_llave_certificacion,
            fel_afiliacion_iva,
            codigo_postal,
            mpos_identificador_fact,
            fel_nit,
            fel_correo,
            fel_nombre_comercial;
    public int fraseIVA,fraseISR;

    // Private declarations

    private Context cont;
    private PBase parent;

    private JSONObject jsonf = new JSONObject();
    private JSONObject jsonc = new JSONObject();
    private JSONObject jsonfa = new JSONObject();
    private JSONObject jsona = new JSONObject();

    private String s64, jsfirm,jscert,jsanul,firma;
    private double imp,totmonto,totiva;
    private int linea;

    // Configuracion

    private double iva=12;
    private String WSURL="https://signer-emisores.feel.com.gt/sign_solicitud_firmas/firma_xml";
    private String WSURLCert="https://certificador.feel.com.gt/fel/certificacion/v2/dte/";
    private String WSURLAnul="https://certificador.feel.com.gt/fel/anulacion/v2/dte/";

    //#EJC20200527:Versión ant (1)
    //private String WSURLCert="https://certificador.feel.com.gt/fel/certificacion/dte/";
    //private String WSURLAnul="https://certificador.feel.com.gt/fel/anulacion/dte/";

    public clsFELInFile(Context context, PBase Parent) {
        cont=context;
        parent=Parent;
        errlevel=0;error="";errorflag=false;errorcon=false;
    }

    public void certificacion() {
        fact_uuid="";fact_serie="";fact_numero=0;
        errlevel=1;error="";errorflag=false;constat=true;errorcon=false;duplicado=false;
        sendJSONFirm();
    }

    public void anulacion(String fuuid) {
        fact_uuid=fuuid;
        errlevel=3;error="";errorflag=false;constat=true;
        sendJSONFirmAnul();
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
            Log.i("FEL_FIRM: ", currentTime.toString());

            executeWSFirm();

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    private void executeWSFirm() {

        jsfirm = jsonf.toString();
        errorflag=false;error="";

        AsyncCallWS wstask = new AsyncCallWS();
        wstask.execute();
    }

    private void wsExecuteF(){

        URL url;
        HttpURLConnection connection = null;
        JSONObject jObj = null;

        try {

            url = new URL(WSURL);
            connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
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

            wr.writeBytes (jsfirm);
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
                    //parent.felCallBack();return;
                }
            } else {
                error=""+response;errorflag=true;return;
            }

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        } finally {
            //if (connection!=null) connection.disconnect();
        }
    }

    private void wsFinishedF() {
        try  {
            if (!errorflag) {
                Date currentTime = Calendar.getInstance().getTime();
                Log.i("FEL_CERT: ", currentTime.toString());
                sendJSONCert();
            } else {
                parent.felCallBack();
            }
        } catch (Exception e) {}
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecuteF();
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinishedF();
            } catch (Exception e)  {}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

    //region Certificacion

    public void sendJSONCert() {

        errlevel=2;

        try {

            jsonc = new JSONObject();
            jsonc.put("nit_emisor",fel_nit);
            jsonc.put("correo_copia",fel_correo);
            jsonc.put("xml_dte",firma);

            Handler mtimer = new Handler();

            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    executeWSCert();
                }
            };

            mtimer.postDelayed(mrunner,1000);

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        }
    }

    public void executeWSCert() {
        jscert = jsonc.toString();
        errorflag=false;error="";

        AsyncCallWSCert wstask = new AsyncCallWSCert();
        wstask.execute();
    }

    public void wsExecuteC(){

        URL url;
        HttpsURLConnection connection = null;
        JSONObject jObj = null;

        try {

            url = new URL(WSURLCert);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            //connection.setRequestProperty("Content-Length",""+Integer.toString(jscert.getBytes().length));
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

            wr.writeBytes (jscert);
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

                int duplidx=jstr.indexOf("Documento enviado previamente.");
                if (duplidx>1) duplicado=true;

                jObj = new JSONObject(jstr);

                error= jObj.getString("descripcion");
                if (!jObj.getBoolean("resultado")) {
                    errorflag=true;return;
                }

                fact_uuid =jObj.getString("uuid");
                fact_serie =jObj.getString("serie");
                fact_numero =jObj.getInt("numero");

                errorflag=false;

            } else {
                error=""+response;errorflag=true;return;
            }
        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        } finally {
            //if (connection!=null) connection.disconnect();
        }
    }

    public void wsFinishedC() {
        try  {
            parent.felCallBack();
        } catch (Exception e)  { }
    }

    private class AsyncCallWSCert extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecuteC();
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinishedC();
            } catch (Exception e)  {}
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
                    //parent.felCallBack();
                }

            } else {
                error=""+response;errorflag=true;return;
            }

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        } finally {
            //if (connection!=null) connection.disconnect();
        }
    }

    private void wsFinishedFA() {
        try  {
            if (!errorflag) {
                sendJSONAnul();
            } else {
                parent.felCallBack();
            }
        } catch (Exception e) {}
    }

    private class AsyncCallWSFA extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecuteFA();
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinishedFA();
            } catch (Exception e)  {}
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
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
            connection.setRequestProperty("Content-Length",""+Integer.toString(jsanul.getBytes().length));
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

                error= jObj.getString("descripcion");
                if (jObj.getBoolean("resultado")) {
                    errorflag=false;
                } else {
                    errorflag=true;
                    error=jObj.getString("mensaje_error");
                }

            } else {
                error=""+response;errorflag=true;return;
            }

        } catch (Exception e) {
            error=e.getMessage();errorflag=true;
        } finally {
            //if (connection!=null) connection.disconnect();
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

    public void iniciar(long fecha_emision) {

        String sf=parseDate(fecha_emision);
        fecha_factura=sf;

        errlevel=1;

        imp=0;linea=0;totmonto=0;totiva=0;

        //#EJC20200527: Funciona para la versión 1 de fel con el url https://certificador.feel.com.gt/fel/certificacion/dte/
//        xml="";
//        xml+="<dte:GTDocumento xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:dte=\"http://www.sat.gob.gt/dte/fel/0.1.0\" xmlns:n1=\"http://www.altova.com/samplexml/other-namespace\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"0.4\" xsi:schemaLocation=\"http://www.sat.gob.gt/dte/fel/0.1.0 \">";
//        xml+="<dte:SAT ClaseDocumento=\"dte\">";
//        xml+="<dte:DTE ID=\"DatosCertificados\">";
//        xml+="<dte:DatosEmision ID=\"DatosEmision\">";
//        xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" Tipo=\"FACT\"></dte:DatosGenerales>";

        //#EJC20200527: Versión 1.5.3 de FEL
        xml="";
        xml+="<dte:GTDocumento xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:dte=\"http://www.sat.gob.gt/dte/fel/0.2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Version=\"0.1\" xsi:schemaLocation=\"http://www.sat.gob.gt/dte/fel/0.2.0 \">";
        xml+="<dte:SAT ClaseDocumento=\"dte\">";
        xml+="<dte:DTE ID=\"DatosCertificados\">";
        xml+="<dte:DatosEmision ID=\"DatosEmision\">";
        xml+="<dte:DatosGenerales CodigoMoneda=\"GTQ\" FechaHoraEmision=\""+sf+"\" Tipo=\"FACT\"></dte:DatosGenerales>";
    }

    public void completar(String serie,int numero) {

        //#CKFK 20200619 puse esto en comentario porque el total del iva no se debe calcular asi
        //#CKFK 20200619 puse esto en comentario porque el total del iva no se debe calcular asi
        //totiva=Math.round(totiva*100);
        //totiva=totiva/100;

        String totIvaStr = String.format("%.2f", totiva);

        serie=serie+"-"+numero;

        xml+="</dte:Items>";

        xml+="<dte:Totales>";
        xml+="<dte:TotalImpuestos>";
        xml+="<dte:TotalImpuesto NombreCorto=\"IVA\" TotalMontoImpuesto=\""+totIvaStr+"\"></dte:TotalImpuesto>";
        xml+="</dte:TotalImpuestos>";
        xml+="<dte:GranTotal>"+totmonto+"</dte:GranTotal>";
        xml+="</dte:Totales>";

        xml+="</dte:DatosEmision>";
        xml+="</dte:DTE>";

        //#EJC20200706: Colocar If aquí para validar si el documento fue en contingencia.
        xml+="<dte:Adenda>";
        if (idcontingencia==0) {
            xml+="<Documento>"+serie+"</Documento>";
        } else {
            xml+="<Documento>"+idcontingencia+"</Documento>";
        }
        xml+="</dte:Adenda>";

        xml+="</dte:SAT>";
        xml+="</dte:GTDocumento>";

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

    public void emisor(String afiliacionIVA,
                       String codigoEstablecimiento,
                       String correoEmisor,
                       String nitEmisor,
                       String nombreComercial,
                       String nombreEmisor) {

        xml+="<dte:Emisor AfiliacionIVA=\""+afiliacionIVA+"\" " +
                "CodigoEstablecimiento=\""+codigoEstablecimiento+"\" " +
                "CorreoEmisor=\""+correoEmisor+"\" " +
                "NITEmisor=\""+nitEmisor+"\" " +
                "NombreComercial=\""+nombreComercial+"\" " +
                "NombreEmisor=\""+nombreEmisor+"\">";
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

    public void receptor(String p1,String p2,String p3) {

        String p4="correo@gmail.com";

        xml+="<dte:Receptor CorreoReceptor=\""+p4+"\" IDReceptor=\""+p1+"\" NombreReceptor=\""+p2+"\">";
        xml+="<dte:DireccionReceptor>";
        xml+="<dte:Direccion>"+p3+"</dte:Direccion>";
        xml+="<dte:CodigoPostal>0</dte:CodigoPostal>";
        xml+="<dte:Municipio>Guatemala</dte:Municipio>";
        xml+="<dte:Departamento>Guatemala</dte:Departamento>";
        xml+="<dte:Pais>GT</dte:Pais>";
        xml+="</dte:DireccionReceptor>";
        xml+="</dte:Receptor>";

        xml+="<dte:Frases>";
        //xml+="<dte:Frase CodigoEscenario=\"1\" TipoFrase=\"1\"></dte:Frase>";
        //xml+="<dte:Frase CodigoEscenario=\"2\" TipoFrase=\"2\"></dte:Frase>";
        if (fraseIVA!=0) xml+="<dte:Frase CodigoEscenario=\""+fraseIVA+"\" TipoFrase=\"1\"></dte:Frase>";
        if (fraseISR!=0) xml+="<dte:Frase CodigoEscenario=\""+fraseISR+"\" TipoFrase=\"2\"></dte:Frase>";
        xml+="</dte:Frases>";

        xml+="<dte:Items>";

    }

    public void detalle (String descrip,double cant,String unid,double precuni,double total,double desc) {
        double imp,impbase;

        linea++;

//        impbase=total/(1+0.01*iva);
//        impbase=Math.floor(impbase*100);impbase=impbase/100;
//        imp=total-impbase;
//        imp=Math.round(imp*100);imp=imp/100;

        double parametroiva1 =(iva/100);
        double parametroiva2 = (1+parametroiva1);
        impbase=total/parametroiva2;
        imp=impbase* parametroiva1;

        totmonto+=total;
        //totiva+=imp;

        xml+="<dte:Item BienOServicio=\"S\" NumeroLinea=\""+linea+"\">";
        xml+="<dte:Cantidad>"+cant+"</dte:Cantidad>";
        xml+="<dte:UnidadMedida>"+unid+"</dte:UnidadMedida>";
        xml+="<dte:Descripcion>"+descrip+"</dte:Descripcion>";
        xml+="<dte:PrecioUnitario>"+precuni+"</dte:PrecioUnitario>";
        xml+="<dte:Precio>"+total+"</dte:Precio>";
        xml+="<dte:Descuento>"+desc+"</dte:Descuento>";

        String impbasestr = String.format("%.2f", impbase);
        String impstr = String.format("%.2f", imp);

        xml+="<dte:Impuestos>";
        xml+="<dte:Impuesto>";
        xml+="<dte:NombreCorto>IVA</dte:NombreCorto>";
        xml+="<dte:CodigoUnidadGravable>1</dte:CodigoUnidadGravable>";
        xml+="<dte:MontoGravable>"+impbasestr+"</dte:MontoGravable>";
        xml+="<dte:MontoImpuesto>"+impstr+"</dte:MontoImpuesto>";
        xml+="</dte:Impuesto>";
        xml+="</dte:Impuestos>";

        xml+="<dte:Total>"+total+"</dte:Total>";
        xml+="</dte:Item>";

        totiva+=Double.valueOf(impstr);

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

    //endregion
}
