package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordenpendObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordenpend";
    private String sql;
    public ArrayList<clsClasses.clsT_ordenpend> items= new ArrayList<clsClasses.clsT_ordenpend>();

    public clsT_ordenpendObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordenpend item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordenpend item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordenpend item) {
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

    public clsClasses.clsT_ordenpend first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordenpend item) {

        ins.init("T_ordenpend");

        ins.add("GODIGO_ORDEN",item.godigo_orden);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordenpend item) {

        upd.init("T_ordenpend");

        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);

        upd.Where("(GODIGO_ORDEN='"+item.godigo_orden+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordenpend item) {
        sql="DELETE FROM T_ordenpend WHERE (GODIGO_ORDEN='"+item.godigo_orden+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ordenpend WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordenpend item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordenpend();

            item.godigo_orden=dt.getString(0);
            item.fecha=dt.getLong(1);
            item.tipo=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsT_ordenpend item) {

        ins.init("T_ordenpend");

        ins.add("GODIGO_ORDEN",item.godigo_orden);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordenpend item) {

        upd.init("T_ordenpend");

        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);

        upd.Where("(GODIGO_ORDEN='"+item.godigo_orden+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

