package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbPedidoDet extends fbBase {

    public clsClasses.clsD_domicilio_det item;
    public ArrayList<clsClasses.clsD_domicilio_det> items= new ArrayList<clsClasses.clsD_domicilio_det>();
    public ArrayList<String> doms= new ArrayList<String>();

    public fbPedidoDet(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsD_domicilio_det item) {
        fdt=fdb.getReference(root+item.codigo);
        fdt.setValue(item);
    }

    public void listItems(String corel, Runnable rnCallback ) {
        try {

            fdb.getReference(root).
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            items.clear();
                            if (task.isSuccessful()) {

                                DataSnapshot res=task.getResult();
                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {

                                        item=clsCls.new clsD_domicilio_det();

                                        item.codigo=snap.child("codigo").getValue(Integer.class);
                                        item.corel=corel;
                                        item.empresa=0;
                                        item.codigo_producto=snap.child("codigo_producto").getValue(String.class);
                                        item.cant=snap.child("cant").getValue(Double.class);
                                        item.precio=snap.child("precio").getValue(Double.class);
                                        item.um=""+snap.child("um").getValue(String.class);
                                        item.imp=snap.child("imp").getValue(Double.class);
                                        item.des=snap.child("des").getValue(Double.class);
                                        item.desmon=snap.child("desmon").getValue(Double.class);
                                        item.total=snap.child("total").getValue(Double.class);
                                        item.nota=""+snap.child("nota").getValue(String.class);
                                        item.tipo_producto=""+snap.child("tipo_producto").getValue(String.class);

                                        items.add(item);
                                    }
                                }
                                errflag=false;
                            } else {
                                errflag=true;
                            }

                            callBack=rnCallback;
                            runCallBack();
                        }
                    });
        } catch (Exception e) {
            errflag=true;
        }
    }

}
