package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturafObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_FACTURAF";
    private String sql;
    public ArrayList<clsClasses.clsD_facturaf> items= new ArrayList<clsClasses.clsD_facturaf>();

    public clsD_facturafObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturaf item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturaf item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturaf item) {
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

    public clsClasses.clsD_facturaf first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturaf item) {

        ins.init("D_FACTURAF");
        ins.add("COREL",item.corel);
        ins.add("NOMBRE",item.nombre);
        ins.add("NIT",item.nit);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturaf item) {

        upd.init("D_FACTURAF");
        upd.add("NOMBRE",item.nombre);
        upd.add("NIT",item.nit);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);
        upd.Where("(COREL='"+item.corel+"')");
        db.execSQL(upd.sql());

    }

    private void deleteItem(clsClasses.clsD_facturaf item) {
        sql="DELETE FROM D_facturaf WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_facturaf WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsD_facturaf item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturaf();
            item.corel=dt.getString(0);
            item.nombre=dt.getString(1);
            item.nit=dt.getString(2);
            item.direccion=dt.getString(3);
            item.correo = dt.getString(4);
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

    public String addItemSql(clsClasses.clsD_facturaf item) {

        ins.init("D_FACTURAF");
        ins.add("COREL",item.corel);
        ins.add("NOMBRE",item.nombre);
        ins.add("NIT",item.nit);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturaf item) {

        upd.init("D_facturaf");
        upd.add("NOMBRE",item.nombre);
        upd.add("NIT",item.nit);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);
        upd.Where("(COREL='"+item.corel+"')");
        return upd.sql();

    }

}