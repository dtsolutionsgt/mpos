package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_venta_corObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_venta_cor";
    private String sql;
    public ArrayList<clsClasses.clsT_venta_cor> items= new ArrayList<clsClasses.clsT_venta_cor>();

    public clsT_venta_corObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_venta_cor item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_venta_cor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_venta_cor item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsT_venta_cor first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_venta_cor item) {

        ins.init("T_venta_cor");

        ins.add("PRODUCTO",item.producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("TOTAL",item.total);
        ins.add("AUTORIZO",item.autorizo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_venta_cor item) {

        upd.init("T_venta_cor");

        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("TOTAL",item.total);
        upd.add("AUTORIZO",item.autorizo);

        upd.Where("(PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_venta_cor item) {
        sql="DELETE FROM T_venta_cor WHERE (PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_venta_cor WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_venta_cor item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_venta_cor();

            item.producto=dt.getString(0);
            item.empresa=dt.getString(1);
            item.um=dt.getString(2);
            item.cant=dt.getDouble(3);
            item.precio=dt.getDouble(4);
            item.total=dt.getDouble(5);
            item.autorizo=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsT_venta_cor item) {

        ins.init("T_venta_cor");

        ins.add("PRODUCTO",item.producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("TOTAL",item.total);
        ins.add("AUTORIZO",item.autorizo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_venta_cor item) {

        upd.init("T_venta_cor");

        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("TOTAL",item.total);
        upd.add("AUTORIZO",item.autorizo);

        upd.Where("(PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')");

        return upd.sql();


    }

    //endregion
}

