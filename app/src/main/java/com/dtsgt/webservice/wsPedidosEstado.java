package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidosEstado extends wsBase {

    private String corel;
    private int estado,valor;
    private Runnable callback;

    public wsPedidosEstado(String Url,Runnable afterfinish) {
        super(Url);
        callback=afterfinish;
    }

    public void execute(String correlativo,int estado_pedido,int valor_estado) {
        corel=correlativo;
        estado=estado_pedido;
        valor=valor_estado;

        execute(callback);
    }

    @Override
    protected void wsExecute() {
        pedEstado();
    }

    private void pedEstado() {
        String METHOD_NAME = "Pedido_Estado";
        String str;

        items.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param1 = new PropertyInfo();
            param1.setType(String.class);
            param1.setName("Corel");param1.setValue(corel);
            request.addProperty(param1);

            PropertyInfo param2 = new PropertyInfo();
            param2.setType(Integer.class);
            param2.setName("Estado");param2.setValue(estado);
            request.addProperty(param2);

            PropertyInfo param3 = new PropertyInfo();
            param3.setType(Integer.class);
            param3.setName("Valor");param3.setValue(valor);
            request.addProperty(param3);

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
