package com.dtsgt.firebase;

import com.dtsgt.base.clsClasses;

public class fbCierre extends fbBase {

    public fbCierre(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsfbCierre item) {
        fdt=fdb.getReference(root+"/"+item.rid);
        fdt.setValue(item);
    }

}
