package com.dtsgt.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.dtsgt.firebase.fbPrecio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class PreciosFb extends PBase {

    private DatabaseReference fbpref;
    private ValueEventListener fbplist;

    private fbPrecio fbp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_precios_fb);

            super.InitBase();

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                if (!app.tieneInternet()) {
                    finish();
                } else {
                    fbp=new fbPrecio("Precios",gl.emp);
                    fbpref = fbp.fdb.getReference("Precios/"+gl.emp);
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

    private void addValueListener() {
        try {

            fbplist=fbpref.addValueEventListener(new ValueEventListener() {

                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String snapkey;

                    GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> myMap = null;

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        try {
                            snapkey = childSnapshot.getKey();
                            myMap = childSnapshot.getValue(t);
                        } catch (Exception e) {
                            String snm=e.getMessage();
                            snm=snm+"";
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaPrecios() {
        try {
            gl.precios.clear();
            fbp.listItems(this::procesaPrecios);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void procesaPrecios() {
        try {
            if (fbp.errflag) throw new Exception(fbp.error);

            for (clsClasses.clsfbPrecio itm : fbp.items) {
                gl.precios.add(itm);
            }

            int prn=gl.precios.size();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private double precioActual(int codpr,double precorig) {
        return precorig;
    }

    //endregion

    //region Dialogs


    //endregion

    //region Aux


    //endregion

    //region Activity Events


    //endregion

}