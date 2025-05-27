package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsT_movd_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_movd_almacen";
    private String sql;
    public ArrayList<clsClasses.clsT_movd_almacen> items= new ArrayList<clsClasses.clsT_movd_almacen>();

    public clsT_movd_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_movd_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_movd_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_movd_almacen item) {
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

    public clsClasses.clsT_movd_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_movd_almacen item) {

        ins.init("T_movd_almacen");

        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);
        ins.add("CANTACT",item.cantact);
        ins.add("ESTADO",item.estado);
        ins.add("PRECIO",item.precio);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_movd_almacen item) {

        upd.init("T_movd_almacen");

        upd.add("CANT",item.cant);
        upd.add("UM",item.um);
        upd.add("CANTACT",item.cantact);
        upd.add("ESTADO",item.estado);
        upd.add("PRECIO",item.precio);

        upd.Where("(COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_movd_almacen item) {
        sql="DELETE FROM T_movd_almacen WHERE (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_movd_almacen WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_movd_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_movd_almacen();

            item.corel=dt.getString(0);
            item.producto=dt.getInt(1);
            item.cant=dt.getDouble(2);
            item.um=dt.getString(3);
            item.cantact=dt.getDouble(4);
            item.estado=dt.getInt(5);
            item.precio=dt.getDouble(6);

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

    public String addItemSql(clsClasses.clsT_movd_almacen item) {

        ins.init("T_movd_almacen");

        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);
        ins.add("CANTACT",item.cantact);
        ins.add("ESTADO",item.estado);
        ins.add("PRECIO",item.precio);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_movd_almacen item) {

        upd.init("T_movd_almacen");

        upd.add("CANT",item.cant);
        upd.add("UM",item.um);
        upd.add("CANTACT",item.cantact);
        upd.add("ESTADO",item.estado);
        upd.add("PRECIO",item.precio);

        upd.Where("(COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

