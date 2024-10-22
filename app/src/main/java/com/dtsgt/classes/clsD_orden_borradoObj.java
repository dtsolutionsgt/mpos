package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_borradoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_borrado";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_borrado> items= new ArrayList<clsClasses.clsD_orden_borrado>();

    public clsD_orden_borradoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_borrado item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_borrado item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_borrado item) {
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

    public clsClasses.clsD_orden_borrado first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_orden_borrado item) {

        ins.init("D_orden_borrado");

        ins.add("CODIGO_BORRADO",item.codigo_borrado);
        ins.add("CODIGO_ORDEN",item.codigo_orden);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("FECHA",item.fecha);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_borrado item) {

        upd.init("D_orden_borrado");

        upd.add("CODIGO_ORDEN",item.codigo_orden);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("FECHA",item.fecha);

        upd.Where("(CODIGO_BORRADO="+item.codigo_borrado+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_orden_borrado item) {
        sql="DELETE FROM D_orden_borrado WHERE (CODIGO_BORRADO="+item.codigo_borrado+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_orden_borrado WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_borrado item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_borrado();

            item.codigo_borrado=dt.getInt(0);
            item.codigo_orden=dt.getString(1);
            item.codigo_producto=dt.getInt(2);
            item.cantidad=dt.getDouble(3);
            item.codigo_usuario=dt.getInt(4);
            item.fecha=dt.getLong(5);

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

    public String addItemSql(clsClasses.clsD_orden_borrado item) {

        ins.init("D_orden_borrado");

        ins.add("CODIGO_BORRADO",item.codigo_borrado);
        ins.add("CODIGO_ORDEN",item.codigo_orden);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("FECHA",item.fecha);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_borrado item) {

        upd.init("D_orden_borrado");

        upd.add("CODIGO_ORDEN",item.codigo_orden);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("FECHA",item.fecha);

        upd.Where("(CODIGO_BORRADO="+item.codigo_borrado+")");

        return upd.sql();


    }

    //endregion
}

