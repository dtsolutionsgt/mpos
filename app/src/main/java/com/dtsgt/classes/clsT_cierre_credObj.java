package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_cierre_credObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_cierre_cred";
    private String sql;
    public ArrayList<clsClasses.clsT_cierre_cred> items= new ArrayList<clsClasses.clsT_cierre_cred>();

    public clsT_cierre_credObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_cierre_cred item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_cierre_cred item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_cierre_cred item) {
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

    public clsClasses.clsT_cierre_cred first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_cierre_cred item) {

        ins.init("T_cierre_cred");

        ins.add("ID",item.id);
        ins.add("NOMBRE",item.nombre);
        ins.add("TOTAL",item.total);
        ins.add("CAJA",item.caja);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_cierre_cred item) {

        upd.init("T_cierre_cred");

        upd.add("NOMBRE",item.nombre);
        upd.add("TOTAL",item.total);
        upd.add("CAJA",item.caja);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_cierre_cred item) {
        sql="DELETE FROM T_cierre_cred WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_cierre_cred WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_cierre_cred item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_cierre_cred();

            item.id=dt.getInt(0);
            item.nombre=dt.getString(1);
            item.total=dt.getDouble(2);
            item.caja=dt.getDouble(3);

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

    public String addItemSql(clsClasses.clsT_cierre_cred item) {

        ins.init("T_cierre_cred");

        ins.add("ID",item.id);
        ins.add("NOMBRE",item.nombre);
        ins.add("TOTAL",item.total);
        ins.add("CAJA",item.caja);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_cierre_cred item) {

        upd.init("T_cierre_cred");

        upd.add("NOMBRE",item.nombre);
        upd.add("TOTAL",item.total);
        upd.add("CAJA",item.caja);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

