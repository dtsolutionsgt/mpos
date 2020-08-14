package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidoDir extends wsBase {

    private String direccion;
    private int codigo;
    private Runnable callback;

    public wsPedidoDir(String Url, Runnable afterfinish) {
        super(Url);
        callback=afterfinish;
    }

    public void execute(int codigo_cliente,String correlativo) {
        direccion =correlativo;
        codigo =codigo_cliente;

        execute(callback);
    }

    @Override
    protected void wsExecute() {
        pedEstado();
    }

    private void pedEstado() {
        String METHOD_NAME = "Pedido_Direccion";
        String str;

        items.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param1 = new PropertyInfo();
            param1.setType(String.class);
            param1.setName("codigo");param1.setValue(codigo);
            request.addProperty(param1);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(Integer.class);
            param2.setName("direccion");param2.setValue(direccion);
            request.addProperty(param2);

            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+METHOD_NAME, envelope);

            SoapObject result = (SoapObject) envelope.bodyIn;

            str=result.getPropertyAsString(0);
            if (!str.equalsIgnoreCase("#")) throw new Exception(str);
        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }
    }

}
