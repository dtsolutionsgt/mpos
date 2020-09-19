package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidosBitacora extends wsBase {

    private String params;
    private Runnable callback;

    public wsPedidosBitacora(String Url, Runnable afterfinish) {
        super(Url);
        callback=afterfinish;
    }

    public void execute(String datosbitacora) {
        params=datosbitacora;

        execute(callback);
    }

    @Override
    protected void wsExecute() {
        pedEstado();
    }

    private void pedEstado() {
        String METHOD_NAME = "Pedido_Bitacora";
        String str;

        items.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param1 = new PropertyInfo();
            param1.setType(String.class);
            param1.setName("Params");param1.setValue(params);
            request.addProperty(param1);

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
