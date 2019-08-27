package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_nivelprecioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_nivelprecio";
    private String sql;
    public ArrayList<clsClasses.clsP_nivelprecio> items = new ArrayList<clsClasses.clsP_nivelprecio>();

    public clsP_nivelprecioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_nivelprecio item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_nivelprecio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_nivelprecio item) {
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

    public clsClasses.clsP_nivelprecio first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_nivelprecio item) {

        ins.init("P_nivelprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_nivelprecio item) {

        upd.init("P_nivelprecio");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO=" + item.codigo + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_nivelprecio item) {
        sql = "DELETE FROM P_nivelprecio WHERE (CODIGO=" + item.codigo + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM P_nivelprecio WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_nivelprecio item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_nivelprecio();

            item.codigo = dt.getInt(0);
            item.nombre = dt.getString(1);
            item.activo = dt.getInt(2);

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

    public String addItemSql(clsClasses.clsP_nivelprecio item) {

        ins.init("P_nivelprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_nivelprecio item) {

        upd.init("P_nivelprecio");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO=" + item.codigo + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}