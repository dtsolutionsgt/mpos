package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_usuarioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_usuario";
    private String sql;
    public ArrayList<clsClasses.clsP_usuario> items = new ArrayList<clsClasses.clsP_usuario>();

    public clsP_usuarioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_usuario item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_usuario item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_usuario item) {
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

    public clsClasses.clsP_usuario first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_usuario item) {

        ins.init("P_usuario");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("COD_GRUPO", item.cod_grupo);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("CLAVE", item.clave);
        ins.add("EMPRESA", item.empresa);
        ins.add("COD_ROL", item.cod_rol);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_usuario item) {

        upd.init("P_usuario");

        upd.add("NOMBRE", item.nombre);
        upd.add("COD_GRUPO", item.cod_grupo);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("CLAVE", item.clave);
        upd.add("EMPRESA", item.empresa);
        upd.add("COD_ROL", item.cod_rol);

        upd.Where("(CODIGO='" + item.codigo + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_usuario item) {
        sql = "DELETE FROM P_usuario WHERE (CODIGO='" + item.codigo + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_usuario WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_usuario item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_usuario();

            item.codigo = dt.getString(0);
            item.nombre = dt.getString(1);
            item.cod_grupo = dt.getString(2);
            item.sucursal = dt.getString(3);
            item.clave = dt.getString(4);
            item.empresa = dt.getString(5);
            item.cod_rol = dt.getInt(6);

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

    public String addItemSql(clsClasses.clsP_usuario item) {

        ins.init("P_usuario");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("COD_GRUPO", item.cod_grupo);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("CLAVE", item.clave);
        ins.add("EMPRESA", item.empresa);
        ins.add("COD_ROL", item.cod_rol);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_usuario item) {

        upd.init("P_usuario");

        upd.add("NOMBRE", item.nombre);
        upd.add("COD_GRUPO", item.cod_grupo);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("CLAVE", item.clave);
        upd.add("EMPRESA", item.empresa);
        upd.add("COD_ROL", item.cod_rol);

        upd.Where("(CODIGO='" + item.codigo + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}