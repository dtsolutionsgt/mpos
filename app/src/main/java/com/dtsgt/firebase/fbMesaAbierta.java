package com.dtsgt.firebase;

import androidx.annotation.NonNull;
import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

public class fbMesaAbierta extends fbBase {

    public clsClasses.clsfbMesaAbierta item;

    public fbMesaAbierta(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    public void setItem(int itemid, clsClasses.clsfbMesaAbierta item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+itemid);
        fdt.setValue(item);
    }

    public void getItem(int itemid,Runnable rnCallback) {
        errflag=false;error="";itemexists=false;

        fdb.getReference(root+"/"+idsuc+"/"+itemid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    try {
                        item=clsCls.new clsfbMesaAbierta();

                        item.codigo_mesa=res.child("codigo_mesa").getValue(Integer.class);
                        item.estado=res.child("estado").getValue(Integer.class);

                        itemexists=true;

                    } catch (Exception ee) {}

                    callBack=rnCallback;
                    runCallBack();
                } else {
                    try {
                        error=task.getException().getMessage();errflag=true;
                        throw new Exception(task.getException().getMessage());
                    } catch (Exception e) {}
                }
            }
        });
    }

}
