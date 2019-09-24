package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsP_cajaajusteObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_cajaajuste";
    private String sql;
    public ArrayList<clsClasses.clsP_cajaajuste> items = new ArrayList<clsClasses.clsP_cajaajuste>();

    public clsP_cajaajusteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cajaajuste item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cajaajuste item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cajaajuste item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsP_cajaajuste first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cajaajuste item) {

        ins.init("P_cajaajuste");

        ins.add("SUCURSAL", item.sucursal);
        ins.add("RUTA", item.ruta);
        ins.add("COREL", item.corel);
        ins.add("ITEM", item.item);
        ins.add("TIPO", item.tipo);
        ins.add("FECHA", item.fecha);
        ins.add("MONTO", item.monto);
        ins.add("VENDEDOR", item.vendedor);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cajaajuste item) {

        upd.init("P_cajaajuste");

        upd.add("TIPO", item.tipo);
        upd.add("FECHA", item.fecha);
        upd.add("MONTO", item.monto);
        upd.add("VENDEDOR", item.vendedor);

        upd.Where("(SUCURSAL='" + item.sucursal + "') AND (RUTA='" + item.ruta + "') AND (COREL=" + item.corel + ") AND (ITEM=" + item.item + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cajaajuste item) {
        sql = "DELETE FROM P_cajaajuste WHERE (SUCURSAL='" + item.sucursal + "') AND (RUTA='" + item.ruta + "') AND (COREL=" + item.corel + ") AND (ITEM=" + item.item + ")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_cajaajuste WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cajaajuste item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cajaajuste();

            item.sucursal = dt.getString(0);
            item.ruta = dt.getString(1);
            item.corel = dt.getInt(2);
            item.item = dt.getInt(3);
            item.tipo = dt.getInt(4);
            item.fecha = dt.getInt(5);
            item.monto = dt.getDouble(6);
            item.vendedor = dt.getString(7);

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

    public String addItemSql(clsClasses.clsP_cajaajuste item) {

        ins.init("P_cajaajuste");

        ins.add("SUCURSAL", item.sucursal);
        ins.add("RUTA", item.ruta);
        ins.add("COREL", item.corel);
        ins.add("ITEM", item.item);
        ins.add("TIPO", item.tipo);
        ins.add("FECHA", item.fecha);
        ins.add("MONTO", item.monto);
        ins.add("VENDEDOR", item.vendedor);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cajaajuste item) {

        upd.init("P_cajaajuste");

        upd.add("TIPO", item.tipo);
        upd.add("FECHA", item.fecha);
        upd.add("MONTO", item.monto);
        upd.add("VENDEDOR", item.vendedor);

        upd.Where("(SUCURSAL='" + item.sucursal + "') AND (RUTA='" + item.ruta + "') AND (COREL=" + item.corel + ") AND (ITEM=" + item.item + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

