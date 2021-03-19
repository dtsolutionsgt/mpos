package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_unidad_convObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_unidad_conv";
    private String sql;
    public ArrayList<clsClasses.clsP_unidad_conv> items= new ArrayList<clsClasses.clsP_unidad_conv>();

    public clsP_unidad_convObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_unidad_conv item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_unidad_conv item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_unidad_conv item) {
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

    public clsClasses.clsP_unidad_conv first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_unidad_conv item) {

        ins.init("P_unidad_conv");

        ins.add("CODIGO_CONVERSION",item.codigo_conversion);
        ins.add("CODIGO_UNIDAD1",item.codigo_unidad1);
        ins.add("CODIGO_UNIDAD2",item.codigo_unidad2);
        ins.add("FACTOR",item.factor);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_unidad_conv item) {

        upd.init("P_unidad_conv");

        upd.add("CODIGO_UNIDAD1",item.codigo_unidad1);
        upd.add("CODIGO_UNIDAD2",item.codigo_unidad2);
        upd.add("FACTOR",item.factor);

        upd.Where("(CODIGO_CONVERSION="+item.codigo_conversion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_unidad_conv item) {
        sql="DELETE FROM P_unidad_conv WHERE (CODIGO_CONVERSION="+item.codigo_conversion+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_unidad_conv WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_unidad_conv item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_unidad_conv();

            item.codigo_conversion=dt.getInt(0);
            item.codigo_unidad1=dt.getString(1);
            item.codigo_unidad2=dt.getString(2);
            item.factor=dt.getDouble(3);

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

    public String addItemSql(clsClasses.clsP_unidad_conv item) {

        ins.init("P_unidad_conv");

        ins.add("CODIGO_CONVERSION",item.codigo_conversion);
        ins.add("CODIGO_UNIDAD1",item.codigo_unidad1);
        ins.add("CODIGO_UNIDAD2",item.codigo_unidad2);
        ins.add("FACTOR",item.factor);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_unidad_conv item) {

        upd.init("P_unidad_conv");

        upd.add("CODIGO_UNIDAD1",item.codigo_unidad1);
        upd.add("CODIGO_UNIDAD2",item.codigo_unidad2);
        upd.add("FACTOR",item.factor);

        upd.Where("(CODIGO_CONVERSION="+item.codigo_conversion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

