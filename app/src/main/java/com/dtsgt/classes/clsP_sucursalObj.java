package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_sucursalObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_sucursal";
    private String sql;
    public ArrayList<clsClasses.clsP_sucursal> items = new ArrayList<clsClasses.clsP_sucursal>();

    public clsP_sucursalObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_sucursal item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_sucursal item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_sucursal item) {
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

    public clsClasses.clsP_sucursal first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_sucursal item) {

        ins.init("P_sucursal");

        ins.add("CODIGO", item.codigo);
        ins.add("EMPRESA", item.empresa);
        ins.add("DESCRIPCION", item.descripcion);
        ins.add("NOMBRE", item.nombre);
        ins.add("DIRECCION", item.direccion);
        ins.add("TELEFONO", item.telefono);
        ins.add("NIT", item.nit);
        ins.add("TEXTO", item.texto);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_sucursal item) {

        upd.init("P_sucursal");

        upd.add("EMPRESA", item.empresa);
        upd.add("DESCRIPCION", item.descripcion);
        upd.add("NOMBRE", item.nombre);
        upd.add("DIRECCION", item.direccion);
        upd.add("TELEFONO", item.telefono);
        upd.add("NIT", item.nit);
        upd.add("TEXTO", item.texto);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO='" + item.codigo + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_sucursal item) {
        sql = "DELETE FROM P_sucursal WHERE (CODIGO='" + item.codigo + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_sucursal WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_sucursal item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_sucursal();

            item.codigo = dt.getString(0);
            item.empresa = dt.getString(1);
            item.descripcion = dt.getString(2);
            item.nombre = dt.getString(3);
            item.direccion = dt.getString(4);
            item.telefono = dt.getString(5);
            item.nit = dt.getString(6);
            item.texto = dt.getString(7);
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

    public String addItemSql(clsClasses.clsP_sucursal item) {

        ins.init("P_sucursal");

        ins.add("CODIGO", item.codigo);
        ins.add("EMPRESA", item.empresa);
        ins.add("DESCRIPCION", item.descripcion);
        ins.add("NOMBRE", item.nombre);
        ins.add("DIRECCION", item.direccion);
        ins.add("TELEFONO", item.telefono);
        ins.add("NIT", item.nit);
        ins.add("TEXTO", item.texto);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_sucursal item) {

        upd.init("P_sucursal");

        upd.add("EMPRESA", item.empresa);
        upd.add("DESCRIPCION", item.descripcion);
        upd.add("NOMBRE", item.nombre);
        upd.add("DIRECCION", item.direccion);
        upd.add("TELEFONO", item.telefono);
        upd.add("NIT", item.nit);
        upd.add("TEXTO", item.texto);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO='" + item.codigo + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

