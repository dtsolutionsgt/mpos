package com.dtsgt.webservice;

import android.database.Cursor;
import android.database.MatrixCursor;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class wsInventRecibir extends wsBase {

    public Cursor cursor =null;

    private ArrayList<String> results=new ArrayList<String>();

    private int crows,ccols;
    private String sql;

    public wsInventRecibir(String Url, int codigo_empresa, int codigo_sucursal) {
        super(Url);

        sql="SELECT CODIGO_PRODUCTO,CANT,UNIDADMEDIDA FROM P_STOCK " +
            "WHERE (EMPRESA="+codigo_empresa+") AND (SUCURSAL="+codigo_sucursal+") AND (ANULADO=0) ";
    }

    public void execute(Runnable afterexecute) {
        super.execute(afterexecute);
    }

    @Override
    protected void wsExecute() {
        try {
            OpenDT(sql);
        } catch (Exception e) {
            error=e.getMessage();errflag=true;
        }
    }

    //region Main

    private void OpenDT(String sql) {
        String METHOD_NAME = "getDT";
        String str;
        int rc;

        results.clear();

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");param.setValue(sql);

            request.addProperty(param);
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
                    results.add(str);
                    if (i==1) crows = Integer.parseInt(str);
                    if (i==2) ccols = Integer.parseInt(str);
                }
            }

            createCursor();
        } catch (Exception e) {
            errflag=true;error=e.getMessage();
            createVoidCursor();
        }
    }

    private void createCursor() {
        String[] mRow = new String[ccols];
        MatrixCursor cur = new MatrixCursor(mRow);
        int pos;
        String ss;

        try {
            createVoidCursor();
            if (crows ==0) return;

            pos=2;
            for (int i = 0; i < crows; i++) {
                for (int j = 0; j < ccols; j++) {
                    try {
                        ss=results.get(pos);
                        if (ss.equalsIgnoreCase("anyType{}")) ss = "";
                        mRow[j]=ss;
                    } catch (Exception e) {
                        mRow[j]="";
                    }
                    pos++;
                }
                cur.addRow(mRow);
            }
            cursor=cur;
        } catch (Exception e) {
            errflag=true;error=e.getMessage();
            createVoidCursor();
        }
    }

    private void createVoidCursor() {
        String[] mRow = new String[ccols];
        MatrixCursor cur = new MatrixCursor(mRow);

        try {
            cursor=cur;
        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }
    }

    //endregion

}
