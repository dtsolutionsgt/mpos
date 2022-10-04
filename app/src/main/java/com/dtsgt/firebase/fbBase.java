package com.dtsgt.firebase;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class fbBase {

    public FirebaseDatabase fdb;
    public DatabaseReference fdt;

    public Runnable callBack;

    public String value,root;

    public fbBase(String troot) {
        fdb = FirebaseDatabase.getInstance();
        root=troot;
        callBack=null;
    }

    public void setSingleValue(String node,String val) {
        fdt=fdb.getReference(root+node);
        fdt.setValue(val);
    }

    public void getSingleValue(String node,Runnable rnCallback) {
        fdb.getReference(root+node).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    value=String.valueOf(task.getResult().getValue());
                    callBack=rnCallback;
                    runCallBack();
                }  else {
                    try {
                        throw new Exception(task.getException());
                    } catch (Exception e) {}
                }
            }
        });
    }

    public void runCallBack() {
        if (callBack==null) return;

        final Handler cbhandler = new Handler();
        cbhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.run();
            }
        }, 50);
    }


}
