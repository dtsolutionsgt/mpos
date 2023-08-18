package com.dtsgt.firebase;

import com.dtsgt.base.clsClasses;

public class fbOrdenNota  extends fbBase {

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

}
