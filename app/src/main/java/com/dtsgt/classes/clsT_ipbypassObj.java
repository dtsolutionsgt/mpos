package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ipbypassObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ipbypass";
    private String sql;
    public ArrayList<clsClasses.clsT_ipbypass> items= new ArrayList<clsClasses.clsT_ipbypass>();

    public clsT_ipbypassObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ipbypass item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ipbypass item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ipbypass item) {
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

    public clsClasses.clsT_ipbypass first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ipbypass item) {

        ins.init("T_ipbypass");

        ins.add("IPO",item.ipo);
        ins.add("IPR",item.ipr);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ipbypass item) {

        upd.init("T_ipbypass");

        upd.add("IPR",item.ipr);

        upd.Where("(IPO='"+item.ipo+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ipbypass item) {
        sql="DELETE FROM T_ipbypass WHERE (IPO='"+item.ipo+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ipbypass WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ipbypass item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ipbypass();

            item.ipo=dt.getString(0);
            item.ipr=dt.getString(1);

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

    public String addItemSql(clsClasses.clsT_ipbypass item) {

        ins.init("T_ipbypass");

        ins.add("IPO",item.ipo);
        ins.add("IPR",item.ipr);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ipbypass item) {

        upd.init("T_ipbypass");

        upd.add("IPR",item.ipr);

        upd.Where("(IPO='"+item.ipo+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

