package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenCuenta extends fbBase {

    public clsClasses.clsT_ordencuenta item,litem;
    public ArrayList<clsClasses.clsT_ordencuenta> items= new ArrayList<clsClasses.clsT_ordencuenta>();

    public String idorden;

    public fbOrdenCuenta(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;

        refnode=fdb.getReference(root+"/"+idsuc+"/"+node+"/");
    }

    public void setItem(clsClasses.clsT_ordencuenta item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.corel+"/"+item.id);
        fdt.setValue(item);
    }

    public void removeKey(String idorden) {
        fdb.getReference(root).child(""+idsuc).child(idorden).removeValue();
    }

    public void removeItem(String idorden,int idcuenta) {
        fdb.getReference(root).child(""+idsuc).child(idorden).child(""+idcuenta).removeValue();
    }

    public void getItem(String idorden,String itemid,Runnable rnCallback) {

        fdb.getReference(root+"/"+idsuc+"/"+idorden+"/"+itemid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                errflag=false;error="";itemexists=false;

                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    try {

                        item=clsCls.new clsT_ordencuenta();

                        item.corel=res.child("corel").getValue(String.class);
                        item.id=res.child("id").getValue(Integer.class);
                        item.cf=res.child("cf").getValue(Integer.class);
                        item.nombre=res.child("nombre").getValue(String.class);
                        item.nit=res.child("nit").getValue(String.class);
                        item.direccion=res.child("direccion").getValue(String.class);
                        item.correo=res.child("correo").getValue(String.class);

                        itemexists=true;

                    } catch (Exception ee) {}

                    callBack=rnCallback;
                    runCallBack();
                } else {
                    error=task.getException().getMessage();errflag=true;
                    try {
                        throw new Exception(task.getException().getMessage());
                    } catch (Exception e) {}
                }
            }
        });
    }

    public void listItemsOrden(String orden,Runnable rnCallback) {
        try {
            idorden=orden;
            items.clear();errflag=false;error="";

            fdb.getReference(root+"/"+idsuc+"/"+idorden+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String cor;

                            items.clear();
                            errflag=false;error="";

                            if (task.isSuccessful()) {
                                DataSnapshot res=task.getResult();

                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {
                                        try {
                                            cor=snap.child("corel").getValue(String.class);

                                            if (cor.equalsIgnoreCase(idorden)) {
                                                litem = clsCls.new clsT_ordencuenta();

                                                litem.corel = snap.child("corel").getValue(String.class);
                                                litem.id = snap.child("id").getValue(Integer.class);
                                                litem.cf = snap.child("cf").getValue(Integer.class);
                                                litem.nombre = snap.child("nombre").getValue(String.class);
                                                litem.nit = snap.child("nit").getValue(String.class);
                                                litem.direccion = snap.child("direccion").getValue(String.class);
                                                litem.correo = snap.child("correo").getValue(String.class);

                                                items.add(litem);
                                            }
                                        } catch (Exception e) {
                                            error = e.getMessage();
                                            errflag = true;break;
                                        }
                                    }
                                }
                            } else {
                                error=task.getException().getMessage();errflag=true;
                            }

                            callBack=rnCallback;
                            runCallBack();
                        }
                    });
        } catch (Exception e) {
            String ss=e.getMessage();
            errflag=true;
        }
    }

}
