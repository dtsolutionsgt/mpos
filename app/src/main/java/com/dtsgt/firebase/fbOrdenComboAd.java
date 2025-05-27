package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenComboAd extends fbBase {

    public clsClasses.clsT_ordencomboad item,litem;
    public ArrayList<clsClasses.clsT_ordencomboad> items= new ArrayList<clsClasses.clsT_ordencomboad>();

    public String idorden;

    public fbOrdenComboAd(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    public void setItem(clsClasses.clsT_ordencomboad item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.corel+"/"+item.idcombo+"/"+item.id);
        fdt.setValue(item);
    }

    public void removeKey(String idorden) {
        fdb.getReference(root).child(""+idsuc).child(idorden).removeValue();
    }

    public void removeCombo(String idorden,int idcombo) {
        fdb.getReference(root).child(""+idsuc).child(idorden).child(""+idcombo).removeValue();
    }

    public void removeComboItem(String idorden,int idcombo,int iditem) {
        fdb.getReference(root).child(""+idsuc).child(idorden).child(""+idcombo).child(""+iditem).removeValue();
    }

    public void listItems(String orden,int idcombo,Runnable rnCallback) {
        try {
            idorden=orden;
            items.clear();errflag=false;error="";

            fdb.getReference(root+"/"+idsuc+"/"+idorden+"/"+idcombo+"/").
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
                                            litem = clsCls.new clsT_ordencomboad();

                                            litem.id = snap.child("id").getValue(Integer.class);
                                            litem.corel = snap.child("corel").getValue(String.class);
                                            litem.idcombo = snap.child("idcombo").getValue(Integer.class);
                                            litem.nombre = snap.child("nombre").getValue(String.class);
                                            litem.cant = snap.child("cant").getValue(Integer.class);

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
