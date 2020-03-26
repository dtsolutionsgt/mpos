package com.dtsgt.classes;

import android.os.AsyncTask;

import com.dtsgt.mpos.PBase;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebService {

    public String xmlresult;
    public String error;
    public int callback=-1;
    public Boolean errorflag;

    private PBase parent;

    private URL mUrl;
    private String mMethodName,mResult,argstr;

    public WebService(PBase Parent,String Url) {
        parent=Parent;
        try {
            mUrl = new URL(Url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //region Public methods

    public void execute() {
        errorflag =false;error="";
        AsyncCallWS wstask = new AsyncCallWS();
        wstask.execute();
    }

    public void wsExecute(){ }

    public void wsFinished() {
        try {
            parent.wsCallBack(errorflag,error,0);
        } catch (Exception e) {
        }
    }

    public void callMethod(String methodName, Object... args) throws Exception {

       try{

           String ss = "",line="";

           mMethodName = methodName;mResult = "";xmlresult="";

           URLConnection conn = mUrl.openConnection();
           conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
           conn.addRequestProperty("SOAPAction", "http://tempuri.org/" + methodName);
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
           body += "</" + methodName + ">" +
                   "</soap:Body>" +
                   "</soap:Envelope>";
           wr.write(body);
           wr.flush();

           // Get the response
           BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

           while ((line = rd.readLine()) != null) mResult += line;

           wr.close();rd.close();

           mResult=mResult.replace("ñ","n");
           xmlresult=mResult;

       }catch (Exception e){
           throw new Exception(" WebService callMethod : "+ e.getMessage());
       }
    }

    //endregion

    //region Arguments

    private String buildArgs(Object... args) throws IllegalArgumentException, IllegalAccessException {
        String result = "";
        String argName = "";
        for (int i = 0; i < args.length; i++) {
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

    private String buildArgValue(Object obj) throws IllegalArgumentException, IllegalAccessException {
        //Class<?> cl = obj.getClass();

        Class<?> cl = null;
        try {
            cl = obj.getClass();
        } catch (Exception e) {
            return "";
        }

        String result = "";

        if (cl.isPrimitive()) return obj.toString();
        if (cl.getName().contains("java.lang.")) return obj.toString();
        if (cl.getName().equals("java.util.Date")) {
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            return dfm.format((Date) obj);
        }

        if (cl.isArray()) {
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

    //endregion

    //region WebService Async Call Class

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                wsExecute();
            } catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinished();
            } catch (Exception e) {
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

}
