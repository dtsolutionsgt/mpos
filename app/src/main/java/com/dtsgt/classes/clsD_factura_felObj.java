package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_factura_felObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura_fel";
    private String sql;
    public ArrayList<clsClasses.clsD_factura_fel> items= new ArrayList<clsClasses.clsD_factura_fel>();

    public clsD_factura_felObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura_fel item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura_fel item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura_fel item) {
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

    public clsClasses.clsD_factura_fel first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_factura_fel item) {

        ins.init("D_factura_fel");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("NIT",item.nit);
        ins.add("RAZON_SOCIAL",item.razon_social);
        ins.add("NOMBRE_COMERCIAL",item.nombre_comercial);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura_fel item) {

        upd.init("D_factura_fel");

        upd.add("NIT",item.nit);
        upd.add("RAZON_SOCIAL",item.razon_social);
        upd.add("NOMBRE_COMERCIAL",item.nombre_comercial);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("RUTA",item.ruta);

        upd.Where("(COREL='"+item.corel+"') AND (EMPRESA="+item.empresa+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_factura_fel item) {
        sql="DELETE FROM D_factura_fel WHERE (COREL='"+item.corel+"') AND (EMPRESA="+item.empresa+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_factura_fel WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura_fel item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura_fel();

            item.corel=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.nit=dt.getString(2);
            item.razon_social=dt.getString(3);
            item.nombre_comercial=dt.getString(4);
            item.sucursal=dt.getInt(5);
            item.ruta=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsD_factura_fel item) {

        ins.init("D_factura_fel");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("NIT",item.nit);
        ins.add("RAZON_SOCIAL",item.razon_social);
        ins.add("NOMBRE_COMERCIAL",item.nombre_comercial);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura_fel item) {

        upd.init("D_factura_fel");

        upd.add("NIT",item.nit);
        upd.add("RAZON_SOCIAL",item.razon_social);
        upd.add("NOMBRE_COMERCIAL",item.nombre_comercial);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("RUTA",item.ruta);

        upd.Where("(COREL='"+item.corel+"') AND (EMPRESA="+item.empresa+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

