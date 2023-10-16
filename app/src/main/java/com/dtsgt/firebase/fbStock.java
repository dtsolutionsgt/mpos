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

//#FIREBASE_STOCK_20231016
public class fbStock extends fbBase {

    public clsClasses.clsFbStock item,litem;
    public ArrayList<clsClasses.clsFbStock> items= new ArrayList<clsClasses.clsFbStock>();
    public ArrayList<clsClasses.clsT_stock> sitems= new ArrayList<clsClasses.clsT_stock>();
    public ArrayList<clsClasses.clsT_stockalm> saitems= new ArrayList<clsClasses.clsT_stockalm>();

    public boolean transresult;
    public int transstatus;
    public double total;
    public String unimed,transerr;
    public long retlongvalue;

    private clsClasses.clsFbStock tritem;
    private clsClasses.clsT_stock sitem;
    private clsClasses.clsT_stockalm saitem;


    public fbStock(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    public fbStock(String troot) {
        super(troot);
    }

    public void addItem(String node, clsClasses.clsFbStock item) {
        String key = fdt.push().getKey();
        fdt=fdb.getReference(root+node+key);
        fdt.setValue(item);
    }

    public void setItem(String node, clsClasses.clsFbPStock item) {
        fdt=fdb.getReference(root+node);
        fdt.setValue(item);
    }

    public void getIntValue(String node,String childnode,Runnable rnCallback) {
        retlongvalue =-1;
        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String sv="";
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    try {
                        sv=res.child(childnode).getValue(String.class);
                        retlongvalue=Long.parseLong(sv);
                    } catch (Exception e) {
                        String ss=e.getMessage()+" : "+sv;
                        retlongvalue=-1;
                    }

                    callBack=rnCallback;
                    runCallBack();
                } else {
                    retlongvalue =-1;
                }
            }
        });
    }

    public void getItem(String node,Runnable rnCallback) {

        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    item=clsCls.new clsFbStock();

                    item.idprod=res.child("idprod").getValue(Integer.class);
                    item.idalm=res.child("idalm").getValue(Integer.class);
                    item.cant=res.child("cant").getValue(Double.class);
                    item.bandera =res.child("bandera").getValue(Integer.class);
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

    public void updateItem(String node, clsClasses.clsFbStock val) {
        fdb.getReference(root+node).child("idprod").setValue(val.idprod);
        fdb.getReference(root+node).child("idalm").setValue(val.idalm);
        fdb.getReference(root+node).child("cant").setValue(val.cant);
        fdb.getReference(root+node).child("bandera").setValue(val.bandera);
        fdb.getReference(root+node).child("um").setValue(val.um);
    }

    public void updateValue(String node,String child, String value) {
        fdb.getReference(root+node).child(child).setValue(value);
    }

    public void removeValue(String node) {
        fdb.getReference(root).child(node).setValue(null);
    }

    public void listItems(String node,int idalm, Runnable rnCallback) {
        try {

            fdb.getReference(root+node).orderByChild("idalm").equalTo(idalm).
                    //fdb.getReference(root+node).orderByChild(field).equalTo(filter).
                            get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    items.clear();

                    if (task.isSuccessful()) {

                        DataSnapshot res=task.getResult();

                        if (res.exists()) {

                            long ii=res.getChildrenCount();

                            for (DataSnapshot snap : res.getChildren()) {

                                litem=clsCls.new clsFbStock();

                                litem.idprod=snap.child("idprod").getValue(Integer.class);
                                litem.idalm=snap.child("idalm").getValue(Integer.class);
                                litem.cant=snap.child("cant").getValue(Double.class);
                                litem.bandera=snap.child("bandera").getValue(Integer.class);
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

    public void calculaTotal(String node,int idalm,int idprod, Runnable rnCallback) {
        try {

            fdb.getReference(root+node).orderByChild("idprod").equalTo(idprod).
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    double tot;
                    int alm;

                    total=0;items.clear();unimed="";

                    if (task.isSuccessful()) {

                        DataSnapshot res=task.getResult();

                        if (res.exists()) {
                            for (DataSnapshot snap : res.getChildren()) {
                                alm=snap.child("idalm").getValue(Integer.class);
                                if (alm==idalm) {
                                    tot=snap.child("cant").getValue(Double.class);total+=tot;
                                    unimed=snap.child("um").getValue(String.class);
                                }
                            }
                        }

                        errflag=false;
                    } else {
                        String se=task.getException().getMessage();
                        errflag=true;
                    }

                    total=total+0;
                    callBack=rnCallback;
                    runCallBack();
                }
            });
        } catch (Exception e) {
            errflag=true;
            String ss=e.getMessage();
        }

    }

    public void listExist(String node,int idalm, Runnable rnCallback) {
        try {

            fdb.getReference(root+node).orderByChild("idalm").equalTo(idalm).
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    int ii=0,palm;
                    sitems.clear();

                    if (task.isSuccessful()) {

                        DataSnapshot res=task.getResult();

                        if (res.exists()) {

                            for (DataSnapshot snap : res.getChildren()) {

                                palm=snap.child("idalm").getValue(Integer.class);
                                if (palm==idalm) {

                                    sitem=clsCls.new clsT_stock();

                                    sitem.id=ii;
                                    sitem.idprod=snap.child("idprod").getValue(Integer.class);
                                    sitem.cant=snap.child("cant").getValue(Double.class);
                                    sitem.um=snap.child("um").getValue(String.class);
                                    sitem.key=snap.getKey();

                                    sitems.add(sitem);ii++;

                                }
                            } // Datasnap
                        } // Exists

                        errflag=false;
                    } else {
                        String se=task.getException().getMessage();
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

        int nc=items.size();

    }

    public void listStock(String node, Runnable rnCallback) {
        try {

            fdb.getReference(root+node).orderByChild("idalm").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int ii=0,palm;
                            saitems.clear();

                            if (task.isSuccessful()) {

                                DataSnapshot res=task.getResult();

                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {

                                        saitem=clsCls.new clsT_stockalm();

                                        saitem.id=ii;
                                        saitem.idprod=snap.child("idprod").getValue(Integer.class);
                                        saitem.idalm=snap.child("idalm").getValue(Integer.class);
                                        saitem.cant=snap.child("cant").getValue(Double.class);
                                        saitem.um=snap.child("um").getValue(String.class);
                                        saitem.key=snap.getKey();

                                        saitems.add(saitem);ii++;

                                    } // Datasnap
                                } // Exists

                                errflag=false;
                            } else {
                                String se=task.getException().getMessage();
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

        int nc=items.size();

    }

    public boolean transBatchStock(Runnable rnCallback) {

        try {
            transerr="";transresult=false;transstatus=-1;

            fdt.runTransaction(new Transaction.Handler() {
                public Transaction.Result doTransaction(MutableData mutableData) {

                    try {
                        removeValue("/"+idsuc+"/");

                        for (int i = 0; i <items.size(); i++) {
                            addItem("/"+idsuc+"/",items.get(i));
                        }

                        transresult=true;
                    } catch (Exception e) {
                        transerr=e.getMessage();transresult=false;
                    }

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

    public boolean transUpdateCant(clsClasses.clsFbStock item, Runnable rnCallback) {

        try {
            tritem =item;
            transerr="";transresult=false;transstatus=-1;
            String rnode="/"+idsuc+"/"+idsuc+"_"+item.idalm+"_"+item.idprod;

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


}

