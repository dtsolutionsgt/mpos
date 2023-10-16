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

//#FIREBASE_ORDEN_ESTADO_20231016
public class fbOrdenEstado extends fbBase {

    public DatabaseReference refOrdenEstado;
    public ValueEventListener listOrdenEstado=null;

    public clsClasses.clsFbOrdenEstado item,litem;
    public ArrayList<clsClasses.clsFbOrdenEstado> items= new ArrayList<clsClasses.clsFbOrdenEstado>();

    private Runnable rnListener;

    public fbOrdenEstado(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;

        refnode=fdb.getReference(root+"/"+idsuc+"/"+node+"/");
        refOrdenEstado=fdb.getReference(root+"/"+idsuc+"/");

    }

    public void setListener(Runnable rnfbListener) {
        rnListener=rnfbListener;

        listOrdenEstado=new ValueEventListener() {
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

    public void setItem(clsClasses.clsFbOrdenEstado item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.corel+"_"+item.id);
        fdt.setValue(item);
    }

    public void estadoPagado(String itemid,int cuenta) {
        fdb.getReference(root+"/"+idsuc+"/"+itemid+"_"+cuenta).child("estado").setValue(2);
    }

    public void removeKey(String idorden) {
        fdb.getReference(root).child(""+idsuc).child(idorden).removeValue();
    }

    public void listItems(Runnable rnCallback) {
        try {
            items.clear();errflag=false;error="";

            fdb.getReference(root+"/"+idsuc+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            items.clear();
                            errflag=false;error="";

                            if (task.isSuccessful()) {
                                DataSnapshot res=task.getResult();

                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {
                                        try {
                                            litem = clsCls.new clsFbOrdenEstado();

                                            litem.corel = snap.child("corel").getValue(String.class);
                                            litem.id = snap.child("id").getValue(Integer.class);
                                            litem.estado = snap.child("estado").getValue(Integer.class);
                                            litem.idmesa = snap.child("idmesa").getValue(Integer.class);
                                            litem.nombre = snap.child("nombre").getValue(String.class);
                                            litem.fecha = snap.child("fecha").getValue(Long.class);
                                            litem.mesero = snap.child("mesero").getValue(Integer.class);

                                            items.add(litem);

                                        } catch (Exception e) {
                                            error = "fbOrdenEstado.listItems: "+ e.getMessage();
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
