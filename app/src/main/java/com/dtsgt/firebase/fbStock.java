package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class fbStock extends fbBase {

    public clsClasses.clsFbPStock item,litem;
    public ArrayList<clsClasses.clsFbPStock> items= new ArrayList<clsClasses.clsFbPStock>();
    public boolean errflag,transresult;
    public int transstatus;

    private clsClasses clsCls=new clsClasses();
    private clsClasses.clsFbPStock tritem;

    private String transerr;
    private int idsuc;

    public fbStock(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    //region Public

    public void setItem(String node, clsClasses.clsFbPStock item) {
        fdt=fdb.getReference(root+node);
        fdt.setValue(item);
    }

    public void getItem(String node,Runnable rnCallback) {

        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    item=clsCls.new clsFbPStock();

                    item.id=res.child("id").getValue(Integer.class);
                    item.idalm=res.child("idalm").getValue(Integer.class);
                    item.cant=res.child("cant").getValue(Double.class);
                    item.nombre =res.child("nombre").getValue(String.class);
                    item.um =res.child("um").getValue(String.class);

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

    public void updateItem(String node, clsClasses.clsFbPStock val) {
        fdb.getReference(root+node).child("id").setValue(val.id);
        fdb.getReference(root+node).child("idalm").setValue(val.idalm);
        fdb.getReference(root+node).child("cant").setValue(val.cant);
        fdb.getReference(root+node).child("nombre").setValue(val.nombre);
        fdb.getReference(root+node).child("um").setValue(val.um);
    }

    public void updateValue(String node, String value) {
        fdb.getReference(root+node).setValue(value);
    }

    public void removeValue(String node) {
        fdb.getReference(root+node).removeValue();
    }

    public void listItems(String node,int idalm, Runnable rnCallback) {
        try {
            items.clear();

            fdb.getReference(root+node).orderByChild("idalm").equalTo(idalm).
                    //fdb.getReference(root+node).orderByChild(field).equalTo(filter).
                            get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    if (task.isSuccessful()) {

                        DataSnapshot res=task.getResult();

                        if (res.exists()) {

                            long ii=res.getChildrenCount();

                            for (DataSnapshot snap : res.getChildren()) {

                                litem=clsCls.new clsFbPStock();

                                litem.id=snap.child("id").getValue(Integer.class);
                                litem.idalm=snap.child("idalm").getValue(Integer.class);
                                litem.cant=snap.child("cant").getValue(Double.class);
                                litem.nombre=snap.child("nombre").getValue(String.class);
                                litem.um=snap.child("um").getValue(String.class);

                                items.add(litem);
                            }
                        }

                        errflag=false;
                    } else {
                        String se=task.getException().getMessage();
                        errflag=true;
                    }

                    int nc=items.size();

                    callBack=rnCallback;
                    runCallBack();
                }
            });
        } catch (Exception e) {
            errflag=true;
            String ss=e.getMessage();
        }

        int nc=items.size();

    }

    public boolean transUpdateCant(clsClasses.clsFbPStock item, Runnable rnCallback) {

        try {
            tritem =item;
            transerr="";transresult=false;transstatus=-1;
            String rnode="/"+idsuc+"/"+idsuc+"_"+item.idalm+"_"+item.id;

            fdt.runTransaction(new Transaction.Handler() {
                public Transaction.Result doTransaction(MutableData mutableData) {

                    fdb.getReference(root+rnode).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            double actcant,addcant;
                            boolean isnew;

                            addcant=tritem.cant;

                            if (task.isSuccessful()) {

                                try {
                                    DataSnapshot res=task.getResult();
                                    isnew=res.getChildrenCount()==0;

                                    if (isnew) {
                                        tritem.cant=addcant;
                                        updateItem(rnode, tritem);
                                    } else {
                                        actcant=res.child("cant").getValue(Double.class);
                                        tritem.cant=actcant+addcant;
                                        fdb.getReference(root+rnode).child("cant").setValue(item.cant);
                                    }

                                    transresult=true;
                                } catch (Exception e) {
                                    transerr=e.getMessage();
                                    transresult=false;
                                }
                            } else {
                                transerr=task.getException().getMessage();
                                transresult=false;
                            }
                        }
                    });

                    return Transaction.success(mutableData);
                }
                public void onComplete(DatabaseError databaseError, boolean complete, DataSnapshot dataSnapshot) {
                    if (complete) transstatus=1;else transstatus=0;
                    callBack=rnCallback;
                    runCallBack();
                }
            });

            return transresult;
        } catch (Exception e) {
            return false;
        }
    }

    public void orderByNombre() {
        Collections.sort(items, new ItemComparatorNombre());
    }

    //endregion

    //region Private

    private  class ItemComparatorNombre implements Comparator<clsClasses.clsFbPStock> {
        public int compare(clsClasses.clsFbPStock left, clsClasses.clsFbPStock right) {
            return left.nombre.compareTo(right.nombre);
        }
    }

    //endregion

}

