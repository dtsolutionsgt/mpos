package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenCombo extends fbBase {

    public clsClasses.clsT_ordencombo item,litem;
    public ArrayList<clsClasses.clsT_ordencombo> items= new ArrayList<clsClasses.clsT_ordencombo>();

    public String idorden;

    public fbOrdenCombo(String troot, int idsucursal) {
        super(troot);
        idsuc=idsucursal;
    }

    public void setItem(clsClasses.clsT_ordencombo item) {
        fdt=fdb.getReference(root+"/"+idsuc+"/"+item.corel+"/"+item.idcombo+"/"+item.codigo_menu);
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

                                            litem = clsCls.new clsT_ordencombo();

                                            litem.corel = snap.child("corel").getValue(String.class);
                                            litem.idcombo = snap.child("idcombo").getValue(Integer.class);
                                            litem.codigo_menu = snap.child("codigo_menu").getValue(Integer.class);
                                            litem.unid = snap.child("unid").getValue(Integer.class);
                                            litem.cant = snap.child("cant").getValue(Integer.class);
                                            litem.idseleccion = snap.child("idseleccion").getValue(Integer.class);
                                            litem.orden = snap.child("orden").getValue(Integer.class);

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
