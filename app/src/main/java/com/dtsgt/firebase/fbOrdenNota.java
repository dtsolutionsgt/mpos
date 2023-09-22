package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenNota extends fbBase {

    public clsClasses.clsFbOrdenNota item,litem;
    public ArrayList<clsClasses.clsFbOrdenNota> items= new ArrayList<clsClasses.clsFbOrdenNota>();

    public String idorden;


    public fbOrdenNota(String troot, int idsucursal,String idorden) {
        super(troot);
        idsuc=idsucursal;
        node=idorden;

        refnode=fdb.getReference(root+"/"+idsuc+"/"+node+"/");
    }

    public void setItem(int prodid, clsClasses.clsFbOrdenNota item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+node+"/"+prodid);
        fdt.setValue(item);
    }

    public void removeItem(int prodid) {
        fdb.getReference(root).child(""+idsuc).child(node).child(""+prodid).removeValue();
    }

    public void removeKey() {
        fdb.getReference(root).child(""+idsuc).child(node).removeValue();
    }

    public void listItems(String orden,Runnable rnCallback) {
        try {
            idorden=orden;
            items.clear();errflag=false;error="";

            fdb.getReference(root+"/"+idsuc+"/"+idorden+"/").
                    get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String cor;

                            items.clear();errflag=false;error="";listresult=false;

                            if (task.isSuccessful()) {
                                DataSnapshot res=task.getResult();

                                if (res.exists()) {

                                    for (DataSnapshot snap : res.getChildren()) {

                                        try {

                                            litem = clsCls.new clsFbOrdenNota();

                                            litem.id = snap.child("id").getValue(Integer.class);
                                            litem.nota = snap.child("nota").getValue(String.class);

                                            items.add(litem);
                                        } catch (Exception e) {
                                            error = e.getMessage(); errflag = true;break;
                                        }
                                    }

                                    listresult=true;
                                } else {
                                    listresult=false;error="Not syncronized";
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
