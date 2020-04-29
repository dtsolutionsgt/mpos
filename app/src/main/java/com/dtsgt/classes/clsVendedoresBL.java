package com.dtsgt.classes;

public class clsVendedoresBL {

    private clsVendedoresObj parent;

    public clsVendedoresBL(clsVendedoresObj parent_class) {
        parent = parent_class;
    }

    public void delete(String cod) {
        //parent.db.execSQL("DELETE FROM VENDEDORES WHERE CODIGO='" + cod + "'");
    }

}
