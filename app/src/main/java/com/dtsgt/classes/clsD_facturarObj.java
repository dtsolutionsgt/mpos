package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturarObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturar";
    private String sql;
    public ArrayList<clsClasses.clsD_facturar> items= new ArrayList<clsClasses.clsD_facturar>();

    public clsD_facturarObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturar item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturar item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturar item) {
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

    public clsClasses.clsD_facturar first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturar item) {

        ins.init("D_facturar");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturar item) {

        upd.init("D_facturar");

        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturar item) {
        sql="DELETE FROM D_facturar WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturar WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturar item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturar();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.producto=dt.getInt(2);
            item.cant=dt.getDouble(3);
            item.um=dt.getString(4);

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

    public String addItemSql(clsClasses.clsD_facturar item) {

        ins.init("D_facturar");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturar item) {

        upd.init("D_facturar");

        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

