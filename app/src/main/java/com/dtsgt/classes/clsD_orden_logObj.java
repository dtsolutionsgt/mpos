package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_logObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_log";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_log> items= new ArrayList<clsClasses.clsD_orden_log>();

    public clsD_orden_logObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_log item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_log item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_log item) {
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

    public clsClasses.clsD_orden_log first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_orden_log item) {

        ins.init("D_orden_log");

        ins.add("COREL",item.corel);
        ins.add("FECHA",item.fecha);
        ins.add("METODO",item.metodo);
        ins.add("ERROR",item.error);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_log item) {

        upd.init("D_orden_log");

        upd.add("FECHA",item.fecha);
        upd.add("METODO",item.metodo);
        upd.add("ERROR",item.error);
        upd.add("NOTA",item.nota);

        upd.Where("(COREL="+item.corel+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_orden_log item) {
        sql="DELETE FROM D_orden_log WHERE (COREL="+item.corel+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_orden_log WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_log item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_log();

            item.corel=dt.getInt(0);
            item.fecha=dt.getLong(1);
            item.metodo=dt.getString(2);
            item.error=dt.getString(3);
            item.nota=dt.getString(4);

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

    public String addItemSql(clsClasses.clsD_orden_log item) {

        ins.init("D_orden_log");

        ins.add("COREL",item.corel);
        ins.add("FECHA",item.fecha);
        ins.add("METODO",item.metodo);
        ins.add("ERROR",item.error);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_log item) {

        upd.init("D_orden_log");

        upd.add("FECHA",item.fecha);
        upd.add("METODO",item.metodo);
        upd.add("ERROR",item.error);
        upd.add("NOTA",item.nota);

        upd.Where("(COREL="+item.corel+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

