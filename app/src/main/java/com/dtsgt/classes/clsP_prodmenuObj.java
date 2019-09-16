package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodmenuObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_prodmenu";
    private String sql;
    public ArrayList<clsClasses.clsP_prodmenu> items = new ArrayList<clsClasses.clsP_prodmenu>();

    public clsP_prodmenuObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodmenu item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodmenu item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodmenu item) {
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

    public clsClasses.clsP_prodmenu first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodmenu item) {

        ins.init("P_prodmenu");

        ins.add("CODIGO", item.codigo);
        ins.add("ITEM", item.item);
        ins.add("NOMBRE", item.nombre);
        ins.add("IDOPCION", item.idopcion);
        ins.add("CANT", item.cant);
        ins.add("ORDEN", item.orden);
        ins.add("BANDERA", item.bandera);
        ins.add("NOTA", item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodmenu item) {

        upd.init("P_prodmenu");

        upd.add("NOMBRE", item.nombre);
        upd.add("IDOPCION", item.idopcion);
        upd.add("CANT", item.cant);
        upd.add("ORDEN", item.orden);
        upd.add("BANDERA", item.bandera);
        upd.add("NOTA", item.nota);

        upd.Where("(CODIGO='" + item.codigo + "') AND (ITEM=" + item.item + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodmenu item) {
        sql = "DELETE FROM P_prodmenu WHERE (CODIGO='" + item.codigo + "') AND (ITEM=" + item.item + ")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_prodmenu WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodmenu item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodmenu();

            item.codigo = dt.getString(0);
            item.item = dt.getInt(1);
            item.nombre = dt.getString(2);
            item.idopcion = dt.getInt(3);
            item.cant = dt.getInt(4);
            item.orden = dt.getInt(5);
            item.bandera = dt.getInt(6);
            item.nota = dt.getString(7);

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

    public String addItemSql(clsClasses.clsP_prodmenu item) {

        ins.init("P_prodmenu");

        ins.add("CODIGO", item.codigo);
        ins.add("ITEM", item.item);
        ins.add("NOMBRE", item.nombre);
        ins.add("IDOPCION", item.idopcion);
        ins.add("CANT", item.cant);
        ins.add("ORDEN", item.orden);
        ins.add("BANDERA", item.bandera);
        ins.add("NOTA", item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodmenu item) {

        upd.init("P_prodmenu");

        upd.add("NOMBRE", item.nombre);
        upd.add("IDOPCION", item.idopcion);
        upd.add("CANT", item.cant);
        upd.add("ORDEN", item.orden);
        upd.add("BANDERA", item.bandera);
        upd.add("NOTA", item.nota);

        upd.Where("(CODIGO='" + item.codigo + "') AND (ITEM=" + item.item + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

