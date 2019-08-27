package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodprecioObj {

    public int count;

    public Context cont;
    public BaseDatos Con;
    public SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    public clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_prodprecio";
    private String sql;
    public ArrayList<clsClasses.clsP_prodprecio> items = new ArrayList<clsClasses.clsP_prodprecio>();

    public clsP_prodprecioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodprecio item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodprecio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodprecio item) {
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

    public clsClasses.clsP_prodprecio first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodprecio item) {

        ins.init("P_prodprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NIVEL", item.nivel);
        ins.add("PRECIO", item.precio);
        ins.add("UNIDADMEDIDA", item.unidadmedida);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodprecio item) {

        upd.init("P_prodprecio");

        upd.add("PRECIO", item.precio);

        upd.Where("(CODIGO='" + item.codigo + "') AND (NIVEL=" + item.nivel + ") AND (UNIDADMEDIDA='" + item.unidadmedida + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodprecio item) {
        sql = "DELETE FROM P_prodprecio WHERE (CODIGO='" + item.codigo + "') AND (NIVEL=" + item.nivel + ") AND (UNIDADMEDIDA='" + item.unidadmedida + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_prodprecio WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodprecio item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodprecio();

            item.codigo = dt.getString(0);
            item.nivel = dt.getInt(1);
            item.precio = dt.getDouble(2);
            item.unidadmedida = dt.getString(3);

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

    public String addItemSql(clsClasses.clsP_prodprecio item) {

        ins.init("P_prodprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NIVEL", item.nivel);
        ins.add("PRECIO", item.precio);
        ins.add("UNIDADMEDIDA", item.unidadmedida);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodprecio item) {

        upd.init("P_prodprecio");

        upd.add("PRECIO", item.precio);

        upd.Where("(CODIGO='" + item.codigo + "') AND (NIVEL=" + item.nivel + ") AND (UNIDADMEDIDA='" + item.unidadmedida + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

