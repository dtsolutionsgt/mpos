package com.dtsgt.classes;


public class clsP_prodprecioBL {

    private clsP_prodprecioObj parent;

    public clsP_prodprecioBL(clsP_prodprecioObj parent_class) {
        parent = parent_class;
    }

    public void delete(String cod)     {
        //parent..db.execSQL("DELETE FROM P_PRODPRECIO WHERE CODIGO='"+cod+"'");
    }

}

