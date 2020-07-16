package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsInventConfirm extends wsBase {

    String idstock;

    public  wsInventConfirm(String Url,String codigo_stock) {
        super(Url);
        idstock=codigo_stock;
    }

    public void execute() {
        super.execute(null);
    }

    @Override
    protected void wsExecute() {
        invProcesado();
    }

    private void invProcesado() {
        String mNAME = "invProcesado";

        try {

            SoapObject request = new SoapObject(NAMESPACE, mNAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("IDStock");param.setValue(idstock);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE+mNAME, envelope);

            SoapObject resSoap =(SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

        } catch (Exception e) {}

    }

}
