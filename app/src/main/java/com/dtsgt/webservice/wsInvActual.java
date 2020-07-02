package com.dtsgt.webservice;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

import com.dtsgt.base.BaseDatos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class wsInvActual {

    public Cursor cursor =null;
    public String error="";
    public boolean errflag;

    private ArrayList<String> results=new ArrayList<String>();

    private String URL,sql;
    private int crows,ccols;

    private BaseDatos Con;
    private SQLiteDatabase db;

    private Runnable callBack;
    private String METHOD_NAME,NAMESPACE ="http://tempuri.org/";

    public wsInvActual(String Url, int codigo_empresa, int codigo_ruta, SQLiteDatabase dbase, BaseDatos dbCon) {
         URL=Url;
         db=dbase;
         Con=dbCon;

         sql="SELECT CODIGO_PRODUCTO, CANTIDAD, CODIGO_STOCK, UNIDAD_MEDIDA FROM P_STOCK_UPDATE " +
             "WHERE (PROCESADO=0) AND (EMPRESA="+codigo_empresa+") AND (RUTA="+codigo_ruta+")";
    }

    public void actualizaInventario(Runnable afterfinish) {
        errflag=false;error="";
        callBack=afterfinish;
        execute();
    }

    //region Main

    private void procesaInventario() {
        int prid,idstock;
        double cant;
        String um;

        try {

            Cursor dt=cursor;

            if ((dt==null) | (dt.getCount()==0)) {
                runCallBack();
                return;
            }

            dt.moveToFirst();
            while (!dt.isAfterLast()) {

                prid=dt.getInt(0);
                cant=dt.getDouble(1);
                idstock=dt.getInt(2);
                um=dt.getString(3);

                if (updateStock(prid,cant,um)) {
                    try {
                        wsInvProcesado invproc=new wsInvProcesado(URL,idstock);
                    } catch (Exception e) {}
                }

                dt.moveToNext();
            }
        } catch (Exception e) {
            errflag=true;error=e.getMessage();
        }

        runCallBack();
    }

    private boolean updateStock(int pcod,double pcant,String um) {

        BaseDatos.Insert ins=Con.Ins;

        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",pcod);
            ins.add("CANT",0);
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
        String str;
        int rc;

        METHOD_NAME = "getDT";

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

    private void runCallBack() {
        final Handler cbhandler = new Handler();
        cbhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.run();
            }
        }, 50);
    }

    //endregion

    //region WebService Core

    private void execute() {
       try {
            AsyncCallInv invtask = new AsyncCallInv();
            invtask.execute();
       } catch (Exception e) {
            error=e.getMessage();errflag=true;
       }
    }

    public void wsExecute(){
        try {
            OpenDT(sql);
        } catch (Exception e) {
            error=e.getMessage();errflag=true;
        }
    }

    public void wsFinished()  {
        procesaInventario();
    }

    private class AsyncCallInv extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                wsExecute();
            } catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                wsFinished();
            } catch (Exception e) {}
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    //endregion

    public class wsInvProcesado {
        String idstock;

        public wsInvProcesado(String Url,int codigo_stock) {
            idstock=""+codigo_stock;
            URL=Url;

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    execute();
                }
            };
            mtimer.postDelayed(mrunner,200);

        }

        private void execute() {
            try {
                AsyncCallInvProc invproctask = new AsyncCallInvProc();
                invproctask.execute();
            } catch (Exception e) {

            }
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

            } catch (Exception e) {
            }

        }

        private class AsyncCallInvProc extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    invProcesado();
                } catch (Exception e) {}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {}

            @Override
            protected void onPreExecute() {}

            @Override
            protected void onProgressUpdate(Void... values) {}

        }

    }


}
