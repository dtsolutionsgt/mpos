package com.dtsgt.firebase;

import android.os.Handler;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class fbBase {

    public FirebaseDatabase fdb;
    public DatabaseReference fdt;

    public Runnable callBack;

    public String value,root;

    public fbBase(String troot) {
        fdb = FirebaseDatabase.getInstance();
        //fdb.setPersistenceEnabled(true); declarar en PBase.onCreate
        root=troot;
        fdt=fdb.getReference(root);
        fdt.keepSynced(true);
        callBack=null;
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
