package com.dtsgt.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.dtsgt.mpos.PBase;

import org.kobjects.util.Strings;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class WebService {

    public String xmlresult;
    public String error;
    public int callback=-1;
    public Boolean errorflag;

    private PBase parent;

    private URL mUrl;
    private int mTimeOut;
    private String mMethodName,mResult,argstr;

    public WebService(PBase Parent,String Url, int TimeOut) {
        parent=Parent;
        try {
            mUrl = new URL(Url);
            mTimeOut =TimeOut;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        errorflag=false;error="";
        AsyncCallWS wstask = new AsyncCallWS();
        wstask.execute();
    }

    public void wsExecute(){ }

    public void wsFinished() {
        try {
            parent.wsCallBack(errorflag,error,0);
        } catch (Exception e)  {}
    }

    public void callEmptyMethod()  {
        try {
            parent.wsCallBack(errorflag,error,0);
        } catch (Exception e)  {}
    }

    public void callMethod(String methodName, Object... args) throws Exception {

       try{

           String ss = "",line="";

           mMethodName = methodName;mResult = "";xmlresult="";

           URLConnection conn = mUrl.openConnection();
           conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
           conn.addRequestProperty("SOAPAction", "http://tempuri.org/" + methodName);

           //#EJC 20200601: Set Timeout
           conn.setConnectTimeout(mTimeOut);
           conn.setReadTimeout(mTimeOut);

           conn.setDoOutput(true);

           OutputStream ostream = conn.getOutputStream();

           OutputStreamWriter wr = new OutputStreamWriter(ostream);

           String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                   "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:" +
                   "xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:" +
                   "soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                   "<soap:Body>" +
                   "<" + methodName + " xmlns=\"http://tempuri.org/\">";
           body += buildArgs(args);
           body= Strings.replace(body,"&","Y");
           body += "</" + methodName + ">" +
                   "</soap:Body>" +
                   "</soap:Envelope>";
           wr.write(body);
           wr.flush();

           int responsecode = ((HttpURLConnection) conn).getResponseCode();

           //#EJC20200702:Capturar excepcion de SQL (No se sabe el error pero sabemos que no se proceso)
           if (responsecode==500) {
               //JP 20210201 Este error tira tambien cuando la consulta no regresa ninguno registro, por eso lo deshabilite
               /*
               throw new Exception("Error 500: Esto es poco usual pero algún problema ocurrió del lado del motor de BD al ejecutar sentencia SQL: \n" +
                       "\n[" +methodName+"] , "+ args[1].toString());
               */
           } else if (responsecode!=299 && responsecode!=400  && responsecode!=404 && responsecode!=500) {

               BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
               while ((line = rd.readLine()) != null) mResult += line;
               rd.close();rd.close();

               mResult=mResult.replace("ñ","n");
               xmlresult=mResult;

           } if (responsecode==299) {

               BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
               while ((line = rd.readLine()) != null) mResult += line;
               rd.close();rd.close();

               mResult=mResult.replace("ñ","n");
               xmlresult=mResult;

               throw new Exception("Error al procesar la solicitud :\n " + parseError());

           } if (responsecode==404) {
               throw new Exception("Error 404: No se obtuvo acceso a: \n" + mUrl.toURI() +
                       "\n" + "Verifique que el WS Existe y es accesible desde el explorador.");
           } if (responsecode==400) {
               throw new Exception("Error 400:  \n\n" + mUrl.toURI()  );
           }
       } catch (Exception e) {
           errorflag=true;error=e.getMessage();
           throw new Exception(error);
       }
    }

    private String buildArgs(Object... args) throws IllegalArgumentException, IllegalAccessException    {
        String result = "";
        String argName = "";
        for (int i = 0; i < args.length; i++)   {
            if (i % 2 == 0) {
                argName = args[i].toString();
            } else {
                result += "<" + argName + ">";
                argstr = result;
                result += buildArgValue(args[i]);
                argstr = result;
                result += "</" + argName + ">";
                argstr = result;
            }
        }
        return result;
    }

    private String buildArgValue(Object obj) throws IllegalArgumentException, IllegalAccessException   {

        Class<?> cl = null;

        try  {
            cl = obj.getClass();
        } catch (Exception e) {
            return "";
        }

        String result = "";

        if (cl.isPrimitive()) return obj.toString();
        if (cl.getName().contains("java.lang.")) return obj.toString();
        if (cl.getName().equals("java.util.Date"))  {
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            return dfm.format((Date) obj);
        }

        if (cl.isArray())  {
            String xmlName = cl.getName().substring(cl.getName().lastIndexOf(".") + 1);
            xmlName = xmlName.replace(";", "");
            Object[] arr = (Object[]) obj;

            for (int i = 0; i < arr.length; i++) {
                result += "<" + xmlName + ">";
                result += buildArgValue(arr[i]);
                result += "</" + xmlName + ">";
            }

            return result;
        }

        Field[] fields = cl.getDeclaredFields();

        for (int i = 0; i < fields.length - 1; i++) {
            result += "<" + fields[i].getName() + ">";
            result += buildArgValue(fields[i].get(obj));
            result += "</" + fields[i].getName() + ">";
        }

        return result;
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params)  {
            try  {
                wsExecute();
            } catch (Exception e) {
                Log.e("wsExecute", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinished();
            } catch (Exception e)  {
                Log.e("wsFinished", e.getMessage());
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    public String parseError()  {
        try {
            int p1=xmlresult.indexOf("<Error>")+7;
            int p2=xmlresult.indexOf("</Error>");
            return xmlresult.substring(p1,p2);
        } catch (Exception e) {
            return "";
        }
    }

}