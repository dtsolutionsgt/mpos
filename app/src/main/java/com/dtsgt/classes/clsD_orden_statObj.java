package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_statObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_stat";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_stat> items= new ArrayList<clsClasses.clsD_orden_stat>();

    public clsD_orden_statObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_stat item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_stat item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_stat item) {
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

    public clsClasses.clsD_orden_stat first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_orden_stat item) {

        ins.init("D_orden_stat");

        ins.add("COREL",item.corel);
        ins.add("CAJA",item.caja);
        ins.add("FECHA",item.fecha);
        ins.add("COMENSALES",item.comensales);
        ins.add("CANT",item.cant);
        ins.add("TIEMPO",item.tiempo);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_stat item) {

        upd.init("D_orden_stat");

        upd.add("CAJA",item.caja);
        upd.add("FECHA",item.fecha);
        upd.add("COMENSALES",item.comensales);
        upd.add("CANT",item.cant);
        upd.add("TIEMPO",item.tiempo);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_orden_stat item) {
        sql="DELETE FROM D_orden_stat WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_orden_stat WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_stat item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_stat();

            item.corel=dt.getString(0);
            item.caja=dt.getInt(1);
            item.fecha=dt.getLong(2);
            item.comensales=dt.getInt(3);
            item.cant=dt.getInt(4);
            item.tiempo=dt.getDouble(5);
            item.statcom=dt.getString(6);

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

    public String addItemSql(clsClasses.clsD_orden_stat item) {

        ins.init("D_orden_stat");

        ins.add("COREL",item.corel);
        ins.add("CAJA",item.caja);
        ins.add("FECHA",item.fecha);
        ins.add("COMENSALES",item.comensales);
        ins.add("CANT",item.cant);
        ins.add("TIEMPO",item.tiempo);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_stat item) {

        upd.init("D_orden_stat");

        upd.add("CAJA",item.caja);
        upd.add("FECHA",item.fecha);
        upd.add("COMENSALES",item.comensales);
        upd.add("CANT",item.cant);
        upd.add("TIEMPO",item.tiempo);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();


    }

    //endregion
}

