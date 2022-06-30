package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordenerrorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordenerror";
    private String sql;
    public ArrayList<clsClasses.clsT_ordenerror> items= new ArrayList<clsClasses.clsT_ordenerror>();

    public clsT_ordenerrorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordenerror item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordenerror item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordenerror item) {
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

    public clsClasses.clsT_ordenerror first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordenerror item) {

        ins.init("T_ordenerror");

        ins.add("GODIGO_ORDEN",item.godigo_orden);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("ERROR",item.error);
        ins.add("ESTADO",item.estado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordenerror item) {

        upd.init("T_ordenerror");

        upd.add("TIPO",item.tipo);
        upd.add("ERROR",item.error);
        upd.add("ESTADO",item.estado);

        upd.Where("(GODIGO_ORDEN='"+item.godigo_orden+"') AND (FECHA="+item.fecha+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordenerror item) {
        sql="DELETE FROM T_ordenerror WHERE (GODIGO_ORDEN='"+item.godigo_orden+"') AND (FECHA="+item.fecha+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ordenerror WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordenerror item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordenerror();

            item.godigo_orden=dt.getString(0);
            item.fecha=dt.getLong(1);
            item.tipo=dt.getInt(2);
            item.error=dt.getString(3);
            item.estado=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsT_ordenerror item) {

        ins.init("T_ordenerror");

        ins.add("GODIGO_ORDEN",item.godigo_orden);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("ERROR",item.error);
        ins.add("ESTADO",item.estado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordenerror item) {

        upd.init("T_ordenerror");

        upd.add("TIPO",item.tipo);
        upd.add("ERROR",item.error);
        upd.add("ESTADO",item.estado);

        upd.Where("(GODIGO_ORDEN='"+item.godigo_orden+"') AND (FECHA="+item.fecha+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

