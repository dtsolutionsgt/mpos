package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbPedidoCombo extends fbBase {

    public clsClasses.clsD_domicilio_combo item;
    public ArrayList<clsClasses.clsD_domicilio_combo> items= new ArrayList<clsClasses.clsD_domicilio_combo>();
    public ArrayList<String> doms= new ArrayList<String>();

    public fbPedidoCombo(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsD_domicilio_combo item) {
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

                                        item=clsCls.new clsD_domicilio_combo();

                                        item.codigo=snap.child("codigo").getValue(Integer.class);
                                        item.corel=corel;
                                        item.codigo_detalle=snap.child("codigo_detalle").getValue(Integer.class);
                                        item.codigo_producto=snap.child("codigo_producto").getValue(Integer.class);
                                        item.cant=snap.child("cant").getValue(Double.class);

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
