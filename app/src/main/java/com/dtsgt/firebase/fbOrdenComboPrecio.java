package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenComboPrecio extends fbBase {

    public clsClasses.clsT_ordencomboprecio item,litem;
    public ArrayList<clsClasses.clsT_ordencomboprecio> items= new ArrayList<clsClasses.clsT_ordencomboprecio>();

    public String idorden;

    public fbOrdenComboPrecio(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    public void setItem(clsClasses.clsT_ordencomboprecio item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.corel+"/"+item.idcombo);
        fdt.setValue(item);
    }

    public void removeKey(String idorden) {
        fdb.getReference(root).child(""+idsuc).child(idorden).removeValue();
    }

    public void removeItem(String idorden,int idcombo) {
        fdb.getReference(root).child(""+idsuc).child(idorden).child(""+idcombo).removeValue();
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
                                            cor=snap.child("corel").getValue(String.class);

                                            if (cor.equalsIgnoreCase(idorden)) {

                                                litem = clsCls.new clsT_ordencomboprecio();

                                                litem.corel = snap.child("corel").getValue(String.class);
                                                litem.idcombo = snap.child("idcombo").getValue(Integer.class);
                                                litem.precorig = snap.child("precorig").getValue(Double.class);
                                                litem.precitems = snap.child("precitems").getValue(Double.class);
                                                litem.precdif = snap.child("precdif").getValue(Double.class);
                                                litem.prectotal = snap.child("prectotal").getValue(Double.class);

                                                items.add(litem);
                                            }

                                            listresult=true;

                                        } catch (Exception e) {
                                            error = e.getMessage();
                                            errflag = true;break;
                                        }
                                    }
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
