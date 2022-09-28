package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_orden_modObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_orden_mod";
    private String sql;
    public ArrayList<clsClasses.clsT_orden_mod> items= new ArrayList<clsClasses.clsT_orden_mod>();

    public clsT_orden_modObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_orden_mod item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_orden_mod item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_orden_mod item) {
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

    public clsClasses.clsT_orden_mod first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_orden_mod item) {

        ins.init("T_orden_mod");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("IDMOD",item.idmod);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_orden_mod item) {

        upd.init("T_orden_mod");

        upd.add("NOMBRE",item.nombre);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+") AND (IDMOD="+item.idmod+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_orden_mod item) {
        sql="DELETE FROM T_orden_mod WHERE (COREL='"+item.corel+"') AND (ID="+item.id+") AND (IDMOD="+item.idmod+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_orden_mod WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_orden_mod item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_orden_mod();

            item.corel=dt.getString(0);
            item.id=dt.getInt(1);
            item.idmod=dt.getInt(2);
            item.nombre=dt.getString(3);

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

    public String addItemSql(clsClasses.clsT_orden_mod item) {

        ins.init("T_orden_mod");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("IDMOD",item.idmod);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_orden_mod item) {

        upd.init("T_orden_mod");

        upd.add("NOMBRE",item.nombre);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+") AND (IDMOD="+item.idmod+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

