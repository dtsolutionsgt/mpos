package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stock_inv_errObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stock_inv_err";
    private String sql;
    public ArrayList<clsClasses.clsP_stock_inv_err> items= new ArrayList<clsClasses.clsP_stock_inv_err>();

    public clsP_stock_inv_errObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_stock_inv_err item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_stock_inv_err item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_stock_inv_err item) {
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

    public clsClasses.clsP_stock_inv_err first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_stock_inv_err item) {

        ins.init("P_stock_inv_err");

        ins.add("CODIGO_INVENTARIO_ENC",item.codigo_inventario_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_stock_inv_err item) {

        upd.init("P_stock_inv_err");

        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_stock_inv_err item) {
        sql="DELETE FROM P_stock_inv_err WHERE (CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_stock_inv_err WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_stock_inv_err item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_stock_inv_err();

            item.codigo_inventario_enc=dt.getInt(0);
            item.codigo_producto=dt.getInt(1);
            item.nota=dt.getString(2);

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

    public String addItemSql(clsClasses.clsP_stock_inv_err item) {

        ins.init("P_stock_inv_err");

        ins.add("CODIGO_INVENTARIO_ENC",item.codigo_inventario_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_stock_inv_err item) {

        upd.init("P_stock_inv_err");

        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_INVENTARIO_ENC="+item.codigo_inventario_enc+") AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

