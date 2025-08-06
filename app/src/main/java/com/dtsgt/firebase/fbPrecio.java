package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbPrecio extends fbBase {

    public clsClasses.clsfbPrecio item,litem;
    public ArrayList<clsClasses.clsfbPrecio> items= new ArrayList<clsClasses.clsfbPrecio>();

    private int empresa;

    public fbPrecio(String troot,int idempresa) {
        super(troot);
        empresa=idempresa;
    }

    public void listItems(int nivel,Runnable rnCallback) {
        try {

            fdb.getReference(root+"/"+empresa+"/"+nivel+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int estado;
                            items.clear();

                            if (task.isSuccessful()) {
                                DataSnapshot res=task.getResult();
                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {

                                        try {

                                            litem = clsCls.new clsfbPrecio();

                                            litem.codigo = snap.child("codigo").getValue(Integer.class);
                                            litem.um = snap.child("um").getValue(String.class);
                                            litem.precio = snap.child("precio").getValue(Double.class);

                                            items.add(litem);
                                        } catch (Exception e) {
                                            error = e.getMessage();errflag = true;break;
                                        }

                                    }
                                }

                                errflag=false;
                            } else {
                                error=task.getException().getMessage();
                                errflag=true;
                            }

                            callBack=rnCallback;
                            runCallBack();
                        }
                    });
        } catch (Exception e) {
            errflag=true;
            String ss=e.getMessage();
        }

    }

}
