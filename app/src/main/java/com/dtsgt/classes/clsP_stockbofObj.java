package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stockbofObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stockbof";
    private String sql;
    public ArrayList<clsClasses.clsP_stockbof> items= new ArrayList<clsClasses.clsP_stockbof>();

    public clsP_stockbofObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;
    }

    public String addItemSql(clsClasses.clsP_stockbof item) {

        ins.init("P_stock");

        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("LOTE",item.lote);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("ANULADO",item.anulado);
        ins.add("ENVIADO",item.enviado);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("DOCUMENTO",item.documento);

        return ins.sql();

    }


}

