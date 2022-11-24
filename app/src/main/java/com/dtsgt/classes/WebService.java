package com.dtsgt.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.dtsgt.mpos.PBase;

import org.kobjects.util.Strings;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

           conn.setDoInput(true);
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
           body= Strings.replace(body,"&","Y");
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

    public void callMethod2(String methodName, Object... args) throws Exception {

        URLConnection conn = mUrl.openConnection();
        String ss = "",line="";
        int TIMEOUT = 6000;
        int READTIMEOUT = 0;

        mMethodName = methodName; mResult = "";xmlresult="";
        error="";errorflag=false;

        try{

            String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:" +
                    "xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:" +
                    "soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soap:Body>" +
                    "<" + methodName + " xmlns=\"http://tempuri.org/\">";

            body += buildArgValue2(args);
            body += "</" + methodName + ">" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.addRequestProperty("SOAPAction", "http://tempuri.org/" + methodName);
            conn.setReadTimeout(READTIMEOUT);
            conn.setConnectTimeout(TIMEOUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));

            OutputStream ostream = null;

            try {
                ostream = conn.getOutputStream();
            } catch (IOException e) {

                mResult=mResult.replace("ñ","n");
                xmlresult=mResult;

                errorflag=true;error=e.getMessage();
                throw new Exception("Error al conectar con el webservice:\n " + error);

            }

            OutputStreamWriter wr = new OutputStreamWriter(ostream);

            wr.write(body);
            wr.flush();

            int responsecode = ((HttpURLConnection) conn).getResponseCode();
            String responsemsg = ((HttpURLConnection) conn).getResponseMessage();

            if (responsecode!=299 && responsecode!=404 && responsecode!=500) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) mResult += line;
                rd.close();rd.close();

                mResult=mResult.replace("ñ","n");
                xmlresult=mResult;

                if(xmlresult.isEmpty()){
                    Log.i("vacio","no creo");
                }

            } if (responsecode==299) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null) mResult += line;
                rd.close();rd.close();

                mResult=mResult.replace("ñ","n");
                xmlresult=mResult;

                errorflag=true;error=parseError();
                throw new Exception("Error al procesar la solicitud :\n " + parseError());

            } if (responsecode==404) {
                errorflag=true;error="Error 404: No se obtuvo acceso a: \n" + mUrl.toURI() + "\n" + "Verifique que el WS Existe y es accesible desde el explorador.";
                throw new Exception(error);
            }if (responsecode==500) {

                errorflag=true;error=parseError();
                throw new Exception("Error al procesar la solicitud :\n " + methodName + " Code: 500");

            }

        } catch (Exception e)  {
            errorflag=true;error=e.getMessage();
            throw new Exception(e.getMessage());
        }
    }

    private String buildArgValue2(Object... obj) throws IllegalArgumentException, IllegalAccessException{

        String result = "";
        String argName = "";

        try{

            for (int i = 0; i < obj.length; i++)
            {
                Class<?> cl = obj[i].getClass();
                if (i % 2 == 0)
                {
                    argName = obj[i].toString();
                } else
                {
                    if(cl.isPrimitive() || (cl.getName().contains("java.lang.")) || (cl.getName().contains("java.int")))
                    {

                        if (obj!=null)
                        {
                            result += "<" + argName + ">";
                            argstr = result;
                            result += buildArgValue(obj[i].toString(),argName);
                            argstr = result;
                            result += "</" + argName + ">";
                            argstr = result;
                        }else
                        {
                            result += "<" + argName + ">";
                            result += "</" + argName + ">";
                            argstr = result;
                        }

                    }
                    if(cl.getName().equals("java.util.Date"))
                    {
                        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                        result += "<" + argName + ">";
                        argstr = result;
                        result += dfm.format((Date)obj[i]);
                        argstr = result;
                        result += "</" + argName + ">";
                        argstr = result;
                        //return argstr;
                    }else
                    {//Is a strong type class
                        String vResultObjects =buildArgs(obj);
                        return vResultObjects;
                    }
                }
            }

        }catch (Exception ex)
        {
            try
            {
                throw new Exception(" WebService callMethod : "+ ex.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private String buildArgValue(Object obj, String pFieldName) throws IllegalArgumentException, IllegalAccessException {
        //Class<?> cl = obj.getClass();

        Class<?> cl = null;

        try
        {
            cl = obj.getClass();
        } catch (Exception e)
        {
            return "";
        }

        String result = "";

        if(result.contains("elementData"))
        {
            Log.e("Pausa", "Debug");
        }

        try
        {
            if(cl.isPrimitive() || (cl.getName().contains("java.lang.")) || (cl.getName().contains("java.int")))
            {
                if (obj!=null)
                {
                    return obj.toString();
                }else
                {
                    Log.e("#EJC_Null","#EJC20200320_NullPointer Esto no debería suceder...");
                    return null;
                }

            }
            if (cl.getName().equals("java.util.Date"))
            {

                DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date convertedDate = new Date();

                try
                {

                    if (obj!=null)
                    {
                        convertedDate = dfm.parse(obj.toString());
                        result += buildArgValue(convertedDate,pFieldName);
                        return obj.toString();
                    }else
                    {
                        return "1900-01-01T00:00:01";
                    }

                } catch (ParseException e)
                {
                    Log.e("#EJC_Invalid_Format","ParamName: " + pFieldName + " Se aplicó valor x Defecto REF#1");
                    result += buildArgValue("1900-01-01T00:00:01",pFieldName);
                }
            }

            if (cl.isArray())
            {
                String xmlName = cl.getName().substring(cl.getName().lastIndexOf(".") + 1);
                xmlName = xmlName.replace(";", "");
                Object[] arr = (Object[]) obj;

                for (int i = 0; i < arr.length; i++) {
                    result += "<" + xmlName + ">";
                    result += buildArgValue(arr[i],xmlName);
                    result += "</" + xmlName + ">";
                }
                return result;
            }

            if(result.contains("elementData"))
            {
                Log.e("Pausa", "Debug");
            }

            Field[] fields = cl.getDeclaredFields();

            String fieldname ="";

            String ClassName = cl.getName();

            for (int i = 0; i <= fields.length-1; i++)
            {

                fieldname =fields[i].getName();

                if(fieldname == "Tag" || fieldname == "Fec_agr" || fieldname.startsWith("Fecha") )
                {
                    Log.d("tag",fieldname);
                }

                result += "<" + fieldname  + ">";

                if(result.contains("elementData"))
                {
                    Log.e("Pausa", "Debug");
                }

                if(fieldname!= "Tag")
                {
                    if ((fieldname.startsWith("Fec_") || fieldname.startsWith("Fech") || fieldname.startsWith("Hora")) &&
                            (!fieldname.contains("especifica")) )
                    {
                        if (obj!=null)
                        {

                            Object oFecha =fields[i].get(obj);
                            String vFecha="1900-01-01T00:00:01";

                            if (oFecha!=null)
                            {
                                vFecha=oFecha.toString();
                            }

                            if(vFecha.contains("T"))
                            {
                                try
                                {
                                    result += buildArgValue(vFecha,fieldname);
                                } catch (Exception e)
                                {
                                    Log.e("#EJC_Invalid_Format","ParamName: " + fieldname + " Se aplicó valor x Defecto REF#2");
                                    result += buildArgValue("1900-01-01T00:00:01",fieldname);
                                }
                            }else
                            {

                                DateFormat dfm = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                                Date convertedDate = new Date();

                                try
                                {
                                    convertedDate = dfm.parse(vFecha);
                                    DateFormat destDf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                                    // format the date into another format
                                    String dateStr = destDf.format(convertedDate);
                                    result += buildArgValue(dateStr,fieldname);
                                } catch (ParseException e)
                                {
                                    Log.e("#EJC_Invalid_Format","ParamName: " + fieldname + " Se aplicó valor x Defecto REF#3");
                                    if (fieldname=="Fechamanufactura")
                                    {
                                        result += buildArgValue(false,fieldname);
                                    }else
                                    {
                                        result += buildArgValue("1900-01-01T00:00:01",fieldname);
                                    }
                                }
                            }

                        }else
                        {
                            Object vobj = fields[i].get(obj);
                            if (vobj!=null)
                            {
                                result += buildArgValue(vobj,fieldname);
                            }
                        }

                    }else
                    {
                        Object vobj =null;

                        if (obj instanceof ArrayList){
                            vobj =obj;
                        }else
                        {
                            vobj = fields[i].get(obj);
                        }

                        if (vobj!=null)
                        {
                            if (vobj instanceof ArrayList)
                            {
                                Log.i("islist","vojb");

                                try{
                                    result="";
                                    for (Object vItem: ((ArrayList) vobj).toArray())
                                    {
                                        System.out.println(vItem);

                                        Class<?> clchild = null;

                                        try
                                        {
                                            //#EJC20200325: No colocar inicio, hasta no definir el objeto completo
                                            //result=""; #CKFK 20200512 tuve que poner esto en comentario para poder enviar la lista completa
                                            String ClassNamechild = vItem.getClass().getSimpleName();
                                            System.out.println(ClassNamechild);
                                            result+=buildArgs(ClassNamechild,vItem);

                                        } catch (Exception e)
                                        {
                                            return "";
                                        }

                                    }

                                }catch (Exception ex)
                                {
                                    Log.e("Nosepudo",ex.getMessage());
                                }

                                return result;

                            }else
                            {
                                result += buildArgValue(vobj,fieldname);
                            }

                        }
                    }

                }else
                {
                    Log.e("#EJC_Invalid_Format","ParamName: " + fieldname + " Se aplicó valor x Defecto REF#4");
                    //Si el valor es obj.java en campo tag....
                    result += buildArgValue("",fieldname);
                }

                result += "</" + fields[i].getName() + ">";
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

}