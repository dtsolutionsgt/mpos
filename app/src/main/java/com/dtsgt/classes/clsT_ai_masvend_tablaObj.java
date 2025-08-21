package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ai_masvend_tablaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ai_masvend_tabla";
    private String sql;
    public ArrayList<clsClasses.clsT_ai_masvend_tabla> items= new ArrayList<clsClasses.clsT_ai_masvend_tabla>();

    public clsT_ai_masvend_tablaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ai_masvend_tabla item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ai_masvend_tabla item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ai_masvend_tabla item) {
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

    public clsClasses.clsT_ai_masvend_tabla first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_ai_masvend_tabla item) {

        ins.init("T_ai_masvend_tabla");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("Val3",item.val3);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ai_masvend_tabla item) {

        upd.init("T_ai_masvend_tabla");

        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("Val3",item.val3);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_ai_masvend_tabla item) {
        sql="DELETE FROM T_ai_masvend_tabla WHERE (CODIGO_PRODUCTO="+item.codigo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ai_masvend_tabla WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ai_masvend_tabla item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ai_masvend_tabla();

            item.codigo_producto=dt.getInt(0);
            item.val1=dt.getInt(1);
            item.val2=dt.getInt(2);
            item.val3=dt.getInt(3);

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

    public int newCor(String idsql) {
        Cursor dt=null;
        int nid;

        try {
            String sel="SELECT MAX( + idsql + ) FROM T_ai_masvend_tabla";
            dt=Con.OpenDT(sel);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_ai_masvend_tabla item) {

        ins.init("T_ai_masvend_tabla");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("Val3",item.val3);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ai_masvend_tabla item) {

        upd.init("T_ai_masvend_tabla");

        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("Val3",item.val3);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+")");

        return upd.sql();


    }

    //endregion
}

