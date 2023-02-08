package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stock_inv_detObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stock_inv_det";
    private String sql;
    public ArrayList<clsClasses.clsP_stock_inv_det> items= new ArrayList<clsClasses.clsP_stock_inv_det>();

    public clsP_stock_inv_detObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_stock_inv_det item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_stock_inv_det item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_stock_inv_det item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel+ " "+specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_stock_inv_det first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_stock_inv_det item) {

        ins.init("P_stock_inv_det");

        ins.add("CODIGO_INVENTARIO_ENC",item.codigo_inventario_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("CANT",item.cant);
        ins.add("COSTO",item.costo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_stock_inv_det item) {

        upd.init("P_stock_inv_det");

        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("CANT",item.cant);
        upd.add("COSTO",item.costo);

        upd.Where("(CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_stock_inv_det item) {
        sql="DELETE FROM P_stock_inv_det WHERE (CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_stock_inv_det WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_stock_inv_det item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_stock_inv_det();

            item.codigo_inventario_enc=dt.getInt(0);
            item.codigo_producto=dt.getInt(1);
            item.unidadmedida=dt.getString(2);
            item.cant=dt.getDouble(3);
            item.costo=dt.getDouble(4);

            items.add(item);

            dt.moveToNext();
        }

        if (dt!=null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt=null;
        int nid;

        try {
            dt=Con.OpenDT(idsql);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_stock_inv_det item) {

        ins.init("P_stock_inv_det");

        ins.add("CODIGO_INVENTARIO_ENC",item.codigo_inventario_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("CANT",item.cant);
        ins.add("COSTO",item.costo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_stock_inv_det item) {

        upd.init("P_stock_inv_det");

        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("CANT",item.cant);
        upd.add("COSTO",item.costo);

        upd.Where("(CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

