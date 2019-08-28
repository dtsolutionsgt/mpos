package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsVendedoresObj {

    public int count;

    private Context cont;
    public BaseDatos Con;
    public SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    public clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM Vendedores";
    private String sql;
    public ArrayList<clsClasses.clsVendedores> items = new ArrayList<clsClasses.clsVendedores>();

    public clsVendedoresObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsVendedores item) {
        addItem(item);
    }

    public void update(clsClasses.clsVendedores item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsVendedores item) {
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

    public clsClasses.clsVendedores first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsVendedores item) {

        ins.init("Vendedores");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("CLAVE", item.clave);
        ins.add("RUTA", item.ruta);
        ins.add("NIVEL", item.nivel);
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("BODEGA", item.bodega);
        ins.add("SUBBODEGA", item.subbodega);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("NOMBRE", item.nombre);
        upd.add("CLAVE", item.clave);
        upd.add("NIVEL", item.nivel);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("BODEGA", item.bodega);
        upd.add("SUBBODEGA", item.subbodega);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO='" + item.codigo + "') AND (RUTA='" + item.ruta + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsVendedores item) {
        sql = "DELETE FROM Vendedores WHERE (CODIGO='" + item.codigo + "') AND (RUTA='" + item.ruta + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM Vendedores WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsVendedores item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsVendedores();

            item.codigo = dt.getString(0);
            item.nombre = dt.getString(1);
            item.clave = dt.getString(2);
            item.ruta = dt.getString(3);
            item.nivel = dt.getInt(4);
            item.nivelprecio = dt.getInt(5);
            item.bodega = dt.getString(6);
            item.subbodega = dt.getString(7);
            item.activo = dt.getInt(8);

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

    public String addItemSql(clsClasses.clsVendedores item) {

        ins.init("Vendedores");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("CLAVE", item.clave);
        ins.add("RUTA", item.ruta);
        ins.add("NIVEL", item.nivel);
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("BODEGA", item.bodega);
        ins.add("SUBBODEGA", item.subbodega);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("NOMBRE", item.nombre);
        upd.add("CLAVE", item.clave);
        upd.add("NIVEL", item.nivel);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("BODEGA", item.bodega);
        upd.add("SUBBODEGA", item.subbodega);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO='" + item.codigo + "') AND (RUTA='" + item.ruta + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

