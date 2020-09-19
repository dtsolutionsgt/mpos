package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_bitacoraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_bitacora";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_bitacora> items= new ArrayList<clsClasses.clsD_orden_bitacora>();

    public clsD_orden_bitacoraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_bitacora item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_bitacora item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_bitacora item) {
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

    public clsClasses.clsD_orden_bitacora first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_orden_bitacora item) {

        ins.init("D_orden_bitacora");

        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("CANT_ORDENES",item.cant_ordenes);
        ins.add("CANT_RETRASADOS",item.cant_retrasados);
        ins.add("TPPO",item.tppo);
        ins.add("EFICIENCIA",item.eficiencia);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_bitacora item) {

        upd.init("D_orden_bitacora");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CANT_ORDENES",item.cant_ordenes);
        upd.add("CANT_RETRASADOS",item.cant_retrasados);
        upd.add("TPPO",item.tppo);
        upd.add("EFICIENCIA",item.eficiencia);
        upd.add("STATCOM",item.statcom);

        upd.Where("(FECHA="+item.fecha+") AND (CODIGO_VENDEDOR="+item.codigo_vendedor+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_orden_bitacora item) {
        sql="DELETE FROM D_orden_bitacora WHERE (FECHA="+item.fecha+") AND (CODIGO_VENDEDOR="+item.codigo_vendedor+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_orden_bitacora WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_bitacora item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_bitacora();

            item.fecha=dt.getLong(0);
            item.codigo_sucursal=dt.getInt(1);
            item.codigo_vendedor=dt.getInt(2);
            item.cant_ordenes=dt.getInt(3);
            item.cant_retrasados=dt.getInt(4);
            item.tppo=dt.getDouble(5);
            item.eficiencia=dt.getDouble(6);
            item.statcom=dt.getInt(7);

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

    public String addItemSql(clsClasses.clsD_orden_bitacora item) {

        ins.init("D_orden_bitacora");

        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("CANT_ORDENES",item.cant_ordenes);
        ins.add("CANT_RETRASADOS",item.cant_retrasados);
        ins.add("TPPO",item.tppo);
        ins.add("EFICIENCIA",item.eficiencia);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_bitacora item) {

        upd.init("D_orden_bitacora");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CANT_ORDENES",item.cant_ordenes);
        upd.add("CANT_RETRASADOS",item.cant_retrasados);
        upd.add("TPPO",item.tppo);
        upd.add("EFICIENCIA",item.eficiencia);
        upd.add("STATCOM",item.statcom);

        upd.Where("(FECHA="+item.fecha+") AND (CODIGO_VENDEDOR="+item.codigo_vendedor+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

