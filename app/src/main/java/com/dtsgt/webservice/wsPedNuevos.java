package com.dtsgt.webservice;

import android.os.AsyncTask;
import android.os.Handler;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;

public class wsPedNuevos {

    public String error="";
    public boolean errflag;
    public ArrayList<String> pedidos=new ArrayList<String>();

    private int idemp,idsuc;


    // reserved for web service
    private Runnable callBack;

    private String URL, NAMESPACE ="http://tempuri.org/";

    public wsPedNuevos(String Url, int codigo_empresa, int codigo_sucursal) {
        URL=Url;
        idemp=codigo_empresa;idsuc=codigo_sucursal;
    }

    public void pedidosNuevos(Runnable afterfinish) {
        errflag=false;error="";
        callBack=afterfinish;
        execute();
    }

    //region Main

    private void pedNuevos() {
        String METHOD_NAME = "Pedidos_Nuevos";
        String str;
        int rc;

        pedidos.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param1 = new PropertyInfo();
            param1.setType(Integer.class);
            param1.setName("EMPRESA");param1.setValue(idemp);
            request.addProperty(param1);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(Integer.class);
            param2.setName("SUCURSAL");param2.setValue(idsuc);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapObject resSoap =(SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc=resSoap.getPropertyCount()-1;

            for (int i = 0; i < rc; i++) {
                str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);
                if (i==0) {
                    if (!str.equalsIgnoreCase("#")) throw new Exception(str);
                } else {
                    pedidos.add(str);
                }
            }

        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }
    }

    private void runCallBack() {
        final Handler cbhandler = new Handler();
        cbhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.run();
            }
        }, 50);
    }

    //endregion

    //region WebService Core

    private void execute() {
        try {
            AsyncCallPedNue pednuetask = new AsyncCallPedNue();
            pednuetask.execute();
        } catch (Exception e) {
            error=e.getMessage();errflag=true;
        }
    }

    public void wsExecute() {
        try {
            pedNuevos();
        } catch (Exception e) {
            error=e.getMessage();errflag=true;
        }
    }

    public void wsFinished() {
        runCallBack();
    }

    private class AsyncCallPedNue extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                wsExecute();
            } catch (Exception e) {
                error=e.getMessage();errflag=true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinished();
            } catch (Exception e) {}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

}
