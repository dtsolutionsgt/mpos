package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_costoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_costo";
    private String sql;
    public ArrayList<clsClasses.clsD_costo> items= new ArrayList<clsClasses.clsD_costo>();

    public clsD_costoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_costo item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_costo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_costo item) {
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

    public clsClasses.clsD_costo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_costo item) {

        ins.init("D_costo");

        ins.add("CODIGO_COSTO",item.codigo_costo);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("COSTO",item.costo);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_costo item) {

        upd.init("D_costo");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("FECHA",item.fecha);
        upd.add("COSTO",item.costo);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);

        upd.Where("(CODIGO_COSTO="+item.codigo_costo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_costo item) {
        sql="DELETE FROM D_costo WHERE (CODIGO_COSTO="+item.codigo_costo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_costo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_costo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_costo();

            item.codigo_costo=dt.getInt(0);
            item.codigo_empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.fecha=dt.getLong(4);
            item.costo=dt.getDouble(5);
            item.codigo_proveedor=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsD_costo item) {

        ins.init("D_costo");

        ins.add("CODIGO_COSTO",item.codigo_costo);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("COSTO",item.costo);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_costo item) {

        upd.init("D_costo");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("FECHA",item.fecha);
        upd.add("COSTO",item.costo);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);

        upd.Where("(CODIGO_COSTO="+item.codigo_costo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

