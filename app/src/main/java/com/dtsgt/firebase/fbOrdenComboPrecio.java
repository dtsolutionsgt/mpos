package com.dtsgt.firebase;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class fbOrdenComboPrecio extends fbBase {

    public clsClasses.clsT_ordencomboprecio item;
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

    public void getItem(String idorden,int idcombo,Runnable rnCallback) {
        errflag=false;error="";itemexists=false;

        fdb.getReference(root+"/"+idsuc+"/"+idorden+"/"+idcombo).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                errflag=false;error="";itemexists=false;

                if (task.isSuccessful()) {
                    DataSnapshot res=task.getResult();

                    try {

                        item=clsCls.new clsT_ordencomboprecio();

                        item.corel=res.child("corel").getValue(String.class);
                        item.idcombo=res.child("idcombo").getValue(Integer.class);
                        item.precorig=res.child("precorig").getValue(Double.class);
                        item.precitems=res.child("precitems").getValue(Double.class);
                        item.precdif=res.child("precdif").getValue(Double.class);
                        item.prectotal=res.child("prectotal").getValue(Double.class);

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



}
