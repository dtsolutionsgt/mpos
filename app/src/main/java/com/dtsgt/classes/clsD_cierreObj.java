package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_cierreObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_cierre";
    private String sql;
    public ArrayList<clsClasses.clsD_cierre> items= new ArrayList<clsClasses.clsD_cierre>();

    public clsD_cierreObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_cierre item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_cierre item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_cierre item) {
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

    public clsClasses.clsD_cierre first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_cierre item) {

        ins.init("D_cierre");

        ins.add("ID",item.id);
        ins.add("CIERRE",item.cierre);
        ins.add("FECHA",item.fecha);
        ins.add("TEXT",item.text);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_cierre item) {

        upd.init("D_cierre");

        upd.add("CIERRE",item.cierre);
        upd.add("FECHA",item.fecha);
        upd.add("TEXT",item.text);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_cierre item) {
        sql="DELETE FROM D_cierre WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_cierre WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_cierre item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_cierre();

            item.id=dt.getInt(0);
            item.cierre=dt.getInt(1);
            item.fecha=dt.getLong(2);
            item.text=dt.getString(3);

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

    public String addItemSql(clsClasses.clsD_cierre item) {

        ins.init("D_cierre");

        ins.add("ID",item.id);
        ins.add("CIERRE",item.cierre);
        ins.add("FECHA",item.fecha);
        ins.add("TEXT",item.text);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_cierre item) {

        upd.init("D_cierre");

        upd.add("CIERRE",item.cierre);
        upd.add("FECHA",item.fecha);
        upd.add("TEXT",item.text);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

