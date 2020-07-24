package com.dtsgt.webservice;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.dtsgt.base.BaseDatos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class wsInventCompartido extends wsBase {

    public String idstock="";

    private Cursor cursor =null;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;

    private ArrayList<String> results=new ArrayList<String>();

    private int crows,ccols;
    private String sql;

    public wsInventCompartido(Context context, String Url, int codigo_empresa, int codigo_ruta, SQLiteDatabase dbase, BaseDatos dbCon) {
        super(Url);
        cont=context;
        db=dbase;Con=dbCon;

        sql="SELECT CODIGO_PRODUCTO, CANTIDAD, CODIGO_STOCK, UNIDAD_MEDIDA FROM P_STOCK_UPDATE " +
            "WHERE (PROCESADO=0) AND (EMPRESA="+codigo_empresa+") AND (RUTA="+codigo_ruta+")";
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

    @Override
    protected void wsFinished() {
        procesaInventario();
        super.wsFinished();
    }

    //region Main

    private void procesaInventario() {
        int prid;
        double cant;
        String um,sid;

        idstock="";

        try {
            Cursor dt=cursor;
            if ((dt==null) | (dt.getCount()==0)) return;

            dt.moveToFirst();
            while (!dt.isAfterLast()) {
                prid=dt.getInt(0);cant=dt.getDouble(1);
                sid=dt.getString(2);um=dt.getString(3);

                if (updateStock(prid,cant,um)) idstock+=sid+"#";

                dt.moveToNext();
            }

        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }
    }

    private boolean updateStock(int pcod,double pcant,String um) {

        BaseDatos.Insert ins=Con.Ins;

        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",pcod);
            ins.add("CANT",pcant);
            ins.add("CANTM",0);
            ins.add("PESO",0);
            ins.add("plibra",0);
            ins.add("LOTE","");
            ins.add("DOCUMENTO","");

            ins.add("FECHA",0);
            ins.add("ANULADO",0);
            ins.add("CENTRO","");
            ins.add("STATUS","");
            ins.add("ENVIADO",1);
            ins.add("CODIGOLIQUIDACION",0);
            ins.add("COREL_D_MOV","");
            ins.add("UNIDADMEDIDA",um);

            String sp=ins.sql();

            db.execSQL(ins.sql());

        } catch (Exception e) {
            try {
                sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE (CODIGO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
                db.execSQL(sql);
            } catch (Exception ee) {
                return false;
            }
        }

        return true;
    }

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
