package com.dtsgt.firebase;

import com.dtsgt.base.clsClasses;

public class fbEnviodb extends fbBase {

    public fbEnviodb(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsfbVersion item) {
        fdt=fdb.getReference(root+"/"+item.rid);
        fdt.setValue(item);
    }

}
