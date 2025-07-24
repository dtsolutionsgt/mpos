package com.dtsgt.firebase;

import com.dtsgt.base.clsClasses;

public class fbVersion extends fbBase {

    public fbVersion(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsfbVersion item) {
        fdt=fdb.getReference(root+"/"+item.rid);
        fdt.setValue(item);
    }

}
