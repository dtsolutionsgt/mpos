package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidosRecibidos extends wsBase {

    public String error;

    private String corel;
    private Runnable callback;

    public wsPedidosRecibidos(String Url,Runnable afterfinish) {
        super(Url);
        callback=afterfinish;
    }

    public void execute(String correlativo) {
        corel=correlativo;
        execute(callback);
    }

    @Override
    protected void wsExecute() {
        pedidosRecepcion(corel);
    }

    public int pedidosRecepcion(String corlist) {
        String METHOD_NAME = "Pedidos_Recepcion";
        String s;

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");
            param.setValue(corlist);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            s = response.toString();
            if (s.equalsIgnoreCase("#")) return 1; else error=s;

        } catch (Exception e) {
            error = e.getMessage();
        }

        return 0;
    }


}
