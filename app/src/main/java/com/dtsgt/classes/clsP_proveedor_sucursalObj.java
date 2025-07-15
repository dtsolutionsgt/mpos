package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_proveedor_sucursalObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_proveedor_sucursal";
    private String sql;
    public ArrayList<clsClasses.clsP_proveedor_sucursal> items= new ArrayList<clsClasses.clsP_proveedor_sucursal>();

    public clsP_proveedor_sucursalObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_proveedor_sucursal item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_proveedor_sucursal item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_proveedor_sucursal item) {
        deleteItem(item);
    }

    public void delete(int id) {
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

    public clsClasses.clsP_proveedor_sucursal first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsP_proveedor_sucursal item) {

        ins.init("P_proveedor_sucursal");

        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_proveedor_sucursal item) {

        upd.init("P_proveedor_sucursal");

        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_PROVEEDOR="+item.codigo_proveedor+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsP_proveedor_sucursal item) {
        sql="DELETE FROM P_proveedor_sucursal WHERE (CODIGO_PROVEEDOR="+item.codigo_proveedor+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_proveedor_sucursal WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_proveedor_sucursal item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_proveedor_sucursal();

            item.codigo_proveedor=dt.getLong(0);
            item.nombre=dt.getString(1);
            item.activo=dt.getInt(2);
            item.statcom=dt.getString(3);

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

    public String addItemSql(clsClasses.clsP_proveedor_sucursal item) {

        ins.init("P_proveedor_sucursal");

        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_proveedor_sucursal item) {

        upd.init("P_proveedor_sucursal");

        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_PROVEEDOR="+item.codigo_proveedor+")");

        return upd.sql();


    }

    //endregion
}

