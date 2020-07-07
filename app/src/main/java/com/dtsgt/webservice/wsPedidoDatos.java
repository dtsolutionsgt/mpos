package com.dtsgt.webservice;

import android.os.Handler;

import com.dtsgt.mpos.CliPos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidoDatos extends wsBase {

    private CliPos.ExtRunnable extCallBack;
    private String idped,url;
    private boolean statflag=true;

    public wsPedidoDatos(String Url, CliPos.ExtRunnable afterfinish) {
        super(Url);
        extCallBack=afterfinish;
        url=Url;
    }

    public void execute(String idpedido) {
        idped=idpedido;
        super.execute(null);
    }

    @Override
    protected void wsExecute() {
        pedDatos();
    }

    @Override
    protected void wsFinished() {
        if (extCallBack==null) return;

        final Handler cbhandler = new Handler();
        cbhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                extCallBack.run(idped);
            }
        }, 50);
    }

    private void pedDatos() {
        String METHOD_NAME = "Pedido_Datos";
        String str;
        int rc;

        items.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param1 = new PropertyInfo();
            param1.setType(String.class);
            param1.setName("Corel");param1.setValue(idped);
            request.addProperty(param1);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapObject resSoap =(SoapObject) envelope.getResponse();
            if (resSoap==null) return;
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc=resSoap.getPropertyCount();

            for (int i = 0; i < rc; i++) {
                str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);
                if (i==0) {
                    if (!str.equalsIgnoreCase("#")) throw new Exception(str);
                } else {
                    items.add(str);
                }
            }

        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }
    }


}
