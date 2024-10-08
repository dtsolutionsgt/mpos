package com.dtsgt.firebase;

import com.dtsgt.base.clsClasses;

public class fbPedidoLog extends fbBase {

    public fbPedidoLog(String troot) {
        super(troot);
    }

    public void setItem(clsClasses.clsD_domicilio_log item) {
        fdt=fdb.getReference(root+item.corel);
        fdt.setValue(item);
    }

}
