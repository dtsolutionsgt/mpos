package com.dtsgt.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsFacturasFEL extends wsBase {

    private String command;

    public wsFacturasFEL(String Url) {
        super(Url);
    }

    public void execute(String commanlist,Runnable afterfinish) {
        command=commanlist;
        super.execute(afterfinish);
    }

    @Override
    protected void wsExecute() {
        facturas_FEL_validacion();
    }

    public void facturas_FEL_validacion() {
        String METHOD_NAME = "facturas_FEL_validacion";
        int rc;
        String str = "";

        error="";errflag=false;
        items.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");
            param.setValue(command);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

            rc = resSoap.getPropertyCount() - 1;

            for (int i = 0; i < rc; i++) {

                str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);

                if (i == 0) {
                   if (str.equalsIgnoreCase("#")) {
                       items.add("DELETE FROM T_FACTURA_FEL");
                   } else {
                       error=str;errflag=true;return;
                   }
                } else {
                    try {
                        items.add(str);
                    } catch (Exception e) {
                        error=str;errflag=true;return;
                    }
                }
            }
        } catch (Exception e) {
            error=e.getMessage();errflag=true;
        }
    }

}
