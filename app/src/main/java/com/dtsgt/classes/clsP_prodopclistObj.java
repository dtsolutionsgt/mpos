package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodopclistObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_prodopclist";
    private String sql;
    public ArrayList<clsClasses.clsP_prodopclist> items = new ArrayList<clsClasses.clsP_prodopclist>();

    public clsP_prodopclistObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_prodopclist item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodopclist item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodopclist item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_prodopclist first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodopclist item) {

        ins.init("P_prodopclist");

        ins.add("ID", item.id);
        ins.add("PRODUCTO", item.producto);
        ins.add("CANT", item.cant);
        ins.add("IDRECETA", item.idreceta);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodopclist item) {

        upd.init("P_prodopclist");

        upd.add("CANT", item.cant);
        upd.add("IDRECETA", item.idreceta);

        upd.Where("(ID=" + item.id + ") AND (PRODUCTO='" + item.producto + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodopclist item) {
        sql = "DELETE FROM P_prodopclist WHERE (ID=" + item.id + ") AND (PRODUCTO='" + item.producto + "')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM P_prodopclist WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodopclist item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodopclist();

            item.id = dt.getInt(0);
            item.producto = dt.getString(1);
            item.cant = dt.getDouble(2);
            item.idreceta = dt.getInt(3);

            items.add(item);

            dt.moveToNext();
        }

        if (dt != null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt = null;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        if (dt != null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_prodopclist item) {

        ins.init("P_prodopclist");

        ins.add("ID", item.id);
        ins.add("PRODUCTO", item.producto);
        ins.add("CANT", item.cant);
        ins.add("IDRECETA", item.idreceta);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodopclist item) {

        upd.init("P_prodopclist");

        upd.add("CANT", item.cant);
        upd.add("IDRECETA", item.idreceta);

        upd.Where("(ID=" + item.id + ") AND (PRODUCTO='" + item.producto + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

