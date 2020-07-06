package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsPedidosNuevos extends wsBase {

    private int idemp,idsuc;

    public wsPedidosNuevos(String Url, int codigo_empresa, int codigo_sucursal) {
        super(Url);
        idemp=codigo_empresa;idsuc=codigo_sucursal;
    }

    @Override
    protected void wsExecute() {
        pedNuevos();
    }

    private void pedNuevos() {
        String METHOD_NAME = "Pedidos_Nuevos";
        String str;
        int rc;

        items.clear();

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
            if (resSoap==null) return;
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc=resSoap.getPropertyCount()-1;

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
