package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_prodprecioObj;
import com.dtsgt.firebase.fbPrecio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class PreciosFb extends PBase {

    private clsP_prodprecioObj P_prodprecioObj;

    private DatabaseReference fbpref;
    private ValueEventListener fbplist;
    private fbPrecio fbp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_precios_fb);

            super.InitBase();

            P_prodprecioObj=new clsP_prodprecioObj(this,Con,db);

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                if (!app.tieneInternet()) {
                    finish();
                } else {
                    fbp=new fbPrecio("Precios",gl.emp);
                    cargaPrecios();
                }
            };
            mtimer.postDelayed(mrunner,200);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }



    //region Events


    //endregion

    //region Main

    private void cargaPrecios() {
        try {
            fbp.listItems(this::procesaPrecios);
        } catch (Exception e) {
            msgboxexit(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void procesaPrecios() {
        clsClasses.clsP_prodprecio item;
        int corr;

        try {
            if (fbp.errflag) throw new Exception(fbp.error);

            corr=P_prodprecioObj.newID("SELECT MAX(CODIGO_PRECIO) FROM P_prodprecio");


            for (clsClasses.clsfbPrecio itm : fbp.items) {

                item = clsCls.new clsP_prodprecio();

                item.codigo_precio=corr;
                item.empresa=gl.emp;
                item.codigo_producto=itm.codigo;
                item.nivel=itm.nivel;
                item.precio=itm.precio;
                item.unidadmedida=itm.um;

                sql="UPDATE P_prodprecio SET precio="+item.precio+" " +
                    "WHERE (codigo_producto="+item.codigo_producto+") AND (nivel="+item.nivel+")";

                try {
                    P_prodprecioObj.add(item);
                } catch (Exception e) {
                    db.execSQL(sql);
                }

            }

            closeSession();
        } catch (Exception e) {
            msgboxexit(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    //region Dialogs

    private void showUIToast(String msg) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { toast(msg);}
                    });
                }
            }).start();
        } catch (Exception e) {}
    }

    private void msgboxexit(String msg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("MPos");
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }

    //endregion

    //region Aux

    private void closeSession() {
        try {

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                showUIToast("Los precios actualizados.");
                finish();
            };
            mtimer.postDelayed(mrunner,200);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Activity Events


    //endregion

}