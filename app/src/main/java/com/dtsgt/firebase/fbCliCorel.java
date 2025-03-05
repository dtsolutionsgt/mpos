package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;


public class fbCliCorel extends fbBase {

    public clsClasses.clsFbCliCorel item;

    public fbCliCorel(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsFbCliCorel item) {
        fdt=fdb.getReference(root+"/"+item.id);
        fdt.setValue(item);
    }

    public void getItem(int cajaid,Runnable rnCallback) {

        fdb.getReference(root+"/"+cajaid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                try {

                    errflag=false;error="";itemexists=false;
                    callBack=rnCallback;

                    if (task.isSuccessful()) {
                        DataSnapshot res=task.getResult();

                        item=clsCls.new clsFbCliCorel();

                        item.id=res.child("id").getValue(Integer.class);
                        item.corel=res.child("corel").getValue(Integer.class);

                        itemexists=true;
                        runCallBack();
                    } else {
                        itemexists=false;
                        runCallBack();
                    }

                } catch (Exception e) {
                    errflag=true;itemexists=false;
                    runCallBack();
                }
            }
        });
    }


}
