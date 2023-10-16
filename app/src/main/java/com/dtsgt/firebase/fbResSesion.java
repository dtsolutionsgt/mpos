package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//#FIREBASE_SESSION_20231016
public class fbResSesion extends fbBase {

    public DatabaseReference refResSesion;
    public ValueEventListener listResSesion=null;

    public clsClasses.clsfbResSesion item,litem;
    public ArrayList<clsClasses.clsfbResSesion> items= new ArrayList<clsClasses.clsfbResSesion>();

    private Runnable rnListener;

    public fbResSesion(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;

        refResSesion=fdb.getReference(root+"/"+idsuc+"/");
    }

    public void setListener(Runnable rnfbListener) {
        rnListener=rnfbListener;

        listResSesion=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rnListener.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError derror) {
                errflag=true;error=derror.getMessage();
            }
        };
    }

    public void setItem(clsClasses.clsfbResSesion item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.id);
        fdt.setValue(item);
    }

    public void updateEstado(String itemid,int value) {
        fdb.getReference(root+"/"+idsuc+"/"+itemid).child("estado").setValue(value);
    }

    public void removeValue(String itemid) {
        fdb.getReference(root).child(idsuc+"").child(itemid).setValue(null);
    }

    public void getItem(String itemid,Runnable rnCallback) {

        fdb.getReference(root+"/"+idsuc+"/"+itemid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                errflag=false;error="";itemexists=false;

                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    item=clsCls.new clsfbResSesion();

                    item.id=res.child("id").getValue(String.class);
                    item.codigo_mesa=res.child("codigo_mesa").getValue(Integer.class);
                    item.vendedor=res.child("vendedor").getValue(Integer.class);
                    item.estado=res.child("estado").getValue(Integer.class);
                    item.cantp=res.child("cantp").getValue(Integer.class);
                    item.cantc=res.child("cantc").getValue(Integer.class);
                    item.fechaini=res.child("fechaini").getValue(Long.class);
                    item.fechafin=res.child("fechafin").getValue(Long.class);
                    item.fechault=res.child("fechault").getValue(Long.class);

                    itemexists=true;

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

    public void listItemsActivos(Runnable rnCallback) {
        try {

            fdb.getReference(root+"/"+idsuc+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    int estado;
                    items.clear();

                    if (task.isSuccessful()) {
                        DataSnapshot res=task.getResult();
                        if (res.exists()) {

                            for (DataSnapshot snap : res.getChildren()) {

                                estado=snap.child("estado").getValue(Integer.class);

                                if (estado>0) {

                                    try {

                                        litem = clsCls.new clsfbResSesion();

                                        litem.id = snap.child("id").getValue(String.class);
                                        litem.codigo_mesa = snap.child("codigo_mesa").getValue(Integer.class);
                                        litem.vendedor = snap.child("vendedor").getValue(Integer.class);
                                        litem.estado = snap.child("estado").getValue(Integer.class);
                                        litem.cantp = snap.child("cantp").getValue(Integer.class);
                                        litem.cantc = snap.child("cantc").getValue(Integer.class);
                                        litem.fechaini = snap.child("fechaini").getValue(Long.class);
                                        litem.fechafin = snap.child("fechafin").getValue(Long.class);
                                        litem.fechault = snap.child("fechault").getValue(Long.class);

                                        items.add(litem);
                                    } catch (Exception e) {
                                        error = e.getMessage();errflag = true;break;
                                    }

                                } // if
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

        int nc=items.size();

    }

    public void listItems(Runnable rnCallback) {
        try {

            fdb.getReference(root+"/"+idsuc+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            items.clear();

                            if (task.isSuccessful()) {
                                DataSnapshot res=task.getResult();
                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {
                                        try {

                                            litem = clsCls.new clsfbResSesion();

                                            litem.id = snap.child("id").getValue(String.class);
                                            litem.codigo_mesa = snap.child("codigo_mesa").getValue(Integer.class);
                                            litem.vendedor = snap.child("vendedor").getValue(Integer.class);
                                            litem.estado = snap.child("estado").getValue(Integer.class);
                                            litem.cantp = snap.child("cantp").getValue(Integer.class);
                                            litem.cantc = snap.child("cantc").getValue(Integer.class);
                                            litem.fechaini = snap.child("fechaini").getValue(Long.class);
                                            litem.fechafin = snap.child("fechafin").getValue(Long.class);
                                            litem.fechault = snap.child("fechault").getValue(Long.class);

                                            items.add(litem);
                                        } catch (Exception e) {
                                            error = e.getMessage();errflag = true;break;
                                        }


                                    } // if
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

        int nc=items.size();

    }

}
