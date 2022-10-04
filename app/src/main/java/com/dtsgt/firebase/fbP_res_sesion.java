package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class fbP_res_sesion extends fbBase{

    public clsClasses.clsP_res_sesion item,litem;
    public ArrayList<clsClasses.clsP_res_sesion> items= new ArrayList<clsClasses.clsP_res_sesion>();

    private clsClasses clsCls=new clsClasses();

    public fbP_res_sesion(String troot) {
        super(troot);
    }


    //region Public

    public void setItem(String node, clsClasses.clsP_res_sesion item) {
        fdt=fdb.getReference(root+node);
        fdt.setValue(item);
    }

    public void getItem(String node,Runnable rnCallback) {

        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    item=clsCls.new clsP_res_sesion();

                    item.id=res.child("id").getValue(String.class);
                    item.codigo_mesa=res.child("codigo_mesa").getValue(Integer.class);
                    item.vendedor=res.child("vendedor").getValue(Integer.class);
                    item.estado=res.child("estado").getValue(Integer.class);
                    item.cantp=res.child("cantp").getValue(Integer.class);
                    item.cantc=res.child("cantc").getValue(Integer.class);
                    item.fechaini=res.child("fechaini").getValue(Long.class);
                    item.fechaini=res.child("fechaini").getValue(Long.class);
                    item.fechault=res.child("fechaultres").getValue(Long.class);

                    callBack=rnCallback;
                    runCallBack();
                } else {
                    try {
                        throw new Exception(task.getException().getMessage());
                    } catch (Exception e) {}
                }
            }
        });
    }

    public void updateItem(String node, clsClasses.clsP_res_sesion val) {
        fdb.getReference(root+node).child("id").setValue(val.id);
        fdb.getReference(root+node).child("codigo_mesa").setValue(val.codigo_mesa);
        fdb.getReference(root+node).child("vendedor").setValue(val.vendedor);
        fdb.getReference(root+node).child("estado").setValue(val.estado);
        fdb.getReference(root+node).child("cantp").setValue(val.cantp);
        fdb.getReference(root+node).child("cantc").setValue(val.cantc);
        fdb.getReference(root+node).child("fechaini").setValue(val.fechaini);
        fdb.getReference(root+node).child("fechafin").setValue(val.fechafin);
        fdb.getReference(root+node).child("fechault").setValue(val.fechault);
    }

    public void updateValue(String node, String value) {
        fdb.getReference(root+node).setValue(value);
    }

    public void removeValue(String node) {
        fdb.getReference(root+node).removeValue();
    }

    public void listItems(String node,Runnable rnCallback) {
        try {


            fdb.getReference(node).
                    //fdb.getReference(root).
                    //fdb.getReference(root+node).orderByChild(field).equalTo(filter).
                            get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    items.clear();
                    if (task.isSuccessful()) {

                        DataSnapshot res=task.getResult();

                        if (res.exists()) {

                            items.clear();
                            for (DataSnapshot snap : res.getChildren()) {

                                litem=clsCls.new clsP_res_sesion();

                                litem.id=snap.child("id").getValue(String.class);
                                litem.codigo_mesa=snap.child("codigo_mesa").getValue(Integer.class);
                                litem.vendedor=snap.child("vendedor").getValue(Integer.class);
                                litem.estado=snap.child("estado").getValue(Integer.class);
                                litem.cantp=snap.child("cantp").getValue(Integer.class);
                                litem.cantc=snap.child("cantc").getValue(Integer.class);
                                litem.fechaini=snap.child("fechaini").getValue(Long.class);
                                litem.fechaini=snap.child("fechaini").getValue(Long.class);
                                litem.fechault=snap.child("fechault").getValue(Long.class);

                                items.add(litem);
                            }
                        }

                        callBack=rnCallback;
                        runCallBack();
                    } else {
                        try {
                            throw new Exception(task.getException().getMessage());
                        } catch (Exception e) {}
                    }
                }
            });
        } catch (Exception e) {
            String ss=e.getMessage();
        }
    }

    public void orderByCodigo_mesa() {
        Collections.sort(items, new ItemComparatorcodigo_mesa());
    }

    //endregion


    //region Private

    private  class ItemComparatorcodigo_mesa implements Comparator<clsClasses.clsP_res_sesion> {
        public int compare(clsClasses.clsP_res_sesion left, clsClasses.clsP_res_sesion right) {
            return Integer.compare(left.codigo_mesa, right.codigo_mesa);
        }
    }

    //endregion

}
