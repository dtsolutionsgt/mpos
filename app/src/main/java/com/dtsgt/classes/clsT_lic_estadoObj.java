package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_lic_estadoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_lic_estado";
    private String sql;
    public ArrayList<clsClasses.clsT_lic_estado> items= new ArrayList<clsClasses.clsT_lic_estado>();

    public clsT_lic_estadoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_lic_estado item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_lic_estado item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_lic_estado item) {
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

    public clsClasses.clsT_lic_estado first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_lic_estado item) {

        ins.init("T_lic_estado");

        ins.add("ID",item.id);
        ins.add("FECHA",item.fecha);
        ins.add("ESTADO",item.estado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_lic_estado item) {

        upd.init("T_lic_estado");

        upd.add("FECHA",item.fecha);
        upd.add("ESTADO",item.estado);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_lic_estado item) {
        sql="DELETE FROM T_lic_estado WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_lic_estado WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_lic_estado item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_lic_estado();

            item.id=dt.getInt(0);
            item.fecha=dt.getLong(1);
            item.estado=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsT_lic_estado item) {

        ins.init("T_lic_estado");

        ins.add("ID",item.id);
        ins.add("FECHA",item.fecha);
        ins.add("ESTADO",item.estado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_lic_estado item) {

        upd.init("T_lic_estado");

        upd.add("FECHA",item.fecha);
        upd.add("ESTADO",item.estado);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

