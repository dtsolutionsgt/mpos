package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_compradObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_comprad";
    private String sql;
    public ArrayList<clsClasses.clsD_comprad> items= new ArrayList<clsClasses.clsD_comprad>();

    public clsD_compradObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_comprad item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_comprad item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_comprad item) {
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

    public clsClasses.clsD_comprad first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_comprad item) {

        ins.init("D_comprad");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_comprad item) {

        upd.init("D_comprad");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_comprad item) {
        sql="DELETE FROM D_comprad WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_comprad WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_comprad item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_comprad();

            item.corel=dt.getString(0);
            item.codigo_producto=dt.getInt(1);
            item.cant=dt.getDouble(2);
            item.um=dt.getString(3);

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

    public String addItemSql(clsClasses.clsD_comprad item) {

        ins.init("D_comprad");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_comprad item) {

        upd.init("D_comprad");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

