package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_proveedorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_proveedor";
    private String sql;
    public ArrayList<clsClasses.clsP_proveedor> items = new ArrayList<clsClasses.clsP_proveedor>();

    public clsP_proveedorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_proveedor item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_proveedor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_proveedor item) {
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

    public clsClasses.clsP_proveedor first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_proveedor item) {

        ins.init("P_proveedor");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_proveedor item) {

        upd.init("P_proveedor");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO=" + item.codigo + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_proveedor item) {
        sql = "DELETE FROM P_proveedor WHERE (CODIGO=" + item.codigo + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM P_proveedor WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_proveedor item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_proveedor();

            item.codigo = dt.getInt(0);
            item.nombre = dt.getString(1);
            item.activo = dt.getInt(2);

            items.add(item);

            dt.moveToNext();
        }

    }

    public int newID(String idsql) {
        Cursor dt;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        return nid;
    }

    public String addItemSql(clsClasses.clsP_proveedor item) {

        ins.init("P_proveedor");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_proveedor item) {

        upd.init("P_proveedor");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO=" + item.codigo + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}