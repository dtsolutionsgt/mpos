package com.dtsgt.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;
import com.dtsgt.webapi.HttpClient;
import com.dtsgt.webapi.HttpCommit;

public class clsEnvioPendiente {

    public int estado;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    private BaseDatos.Insert ins;
    private BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private HttpClient httpcli;
    private HttpCommit httpcom;

    private clsEnvioUpdate EnvioUpdateObj;
    private clsD_notaenvioObj D_notaenvioObj;

    private String sql,apiurl,usql,url,corel_fact;
    private long corel_orig,corel;
    private int emp,sucursal,usuario,cliente;


    public clsEnvioPendiente(String URL,Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        url = URL;

        httpcli = new HttpClient();
        httpcom = new HttpCommit(url+"api/Orden/Commit");

        D_notaenvioObj=new clsD_notaenvioObj(cont,Con,db);
        EnvioUpdateObj= new clsEnvioUpdate(cont,Con,db);
    }

    public void procesaEnvio(long corel_envio,int cod_emp,int cod_sucursal,int cod_usuario,int cod_cliente) {
        try {
            corel_orig=corel_envio;
            emp=cod_emp;
            sucursal=cod_sucursal;
            usuario=cod_usuario;
            cliente=cod_cliente;

            D_notaenvioObj.fill("WHERE (CODIGO_NOTA_ENVIO_ENC="+corel_orig+")");
            corel_fact=D_notaenvioObj.first().referencia;

            corel=0;
            estado =0;

            apiurl=url+"mpos/Mpos/EnvioNuevo?pEmpresa="+emp+"&pSucursal="+sucursal+"&identificador="+corel_fact;

            httpcli.processRequest(apiurl, () -> {
                try {
                    cbCorelNotaEnvio();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            muestraMensaje(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbCorelNotaEnvio() throws Exception {
        if (httpcli.retcode!=1) return;

        corel=Integer.parseInt(httpcli.data);
        estado=-1;
        if (corel==0) return;

        usql=EnvioUpdateObj.generaSQL(corel_fact,corel,sucursal,usuario,emp,cliente);
        httpcom.commit(usql , () -> {
            try {
                cbActualizaNotaEnvio();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void cbActualizaNotaEnvio() throws Exception {
        if (httpcom.errflag) return;

        estado=-2;
        if (corel==0) return;

        try {
            db.beginTransaction();

            sql="UPDATE D_notaenvio SET CODIGO_NOTA_ENVIO_ENC="+corel+",CODIGO_NOTA_ENVIO_ESTATUS=1 " +
                    "WHERE (CODIGO_NOTA_ENVIO_ENC="+corel_orig+")";
            db.execSQL(sql);

            sql="UPDATE D_notaenviod SET CODIGO_NOTA_ENVIO_ENC="+corel+" " +
                    "WHERE (CODIGO_NOTA_ENVIO_ENC="+corel_orig+")";
            db.execSQL(sql);

            db.setTransactionSuccessful();
            db.endTransaction();

            estado=1;
        } catch (Exception e) {
            db.endTransaction();
            estado=-3;
            throw new Exception(e);
        }

    }

    private void muestraMensaje(String msg) {
        try {

            new Thread(() -> {
                Looper.prepare();

                Handler handler = new Handler(Looper.myLooper());
                handler.post(() -> {
                    Toast toast= Toast.makeText(cont,msg, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();;
                });

                Looper.loop();
            }).start();

        } catch (Exception e) {
            //showMsg(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

}
