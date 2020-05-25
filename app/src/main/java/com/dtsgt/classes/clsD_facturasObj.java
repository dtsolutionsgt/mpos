package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturasObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturas";
    private String sql;
    public ArrayList<clsClasses.clsD_facturas> items= new ArrayList<clsClasses.clsD_facturas>();

    public clsD_facturasObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturas item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturas item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturas item) {
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

    public clsClasses.clsD_facturas first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturas item) {

        ins.init("D_facturas");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UMSTOCK",item.umstock);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturas item) {

        upd.init("D_facturas");

        upd.add("PRODUCTO",item.producto);
        upd.add("CANT",item.cant);
        upd.add("UMSTOCK",item.umstock);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturas item) {
        sql="DELETE FROM D_facturas WHERE (COREL='"+item.corel+"') AND (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_facturas WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturas item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturas();

            item.corel=dt.getString(0);
            item.id=dt.getInt(1);
            item.producto=dt.getInt(2);
            item.cant=dt.getDouble(3);
            item.umstock=dt.getString(4);

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

    public String addItemSql(clsClasses.clsD_facturas item) {

        ins.init("D_facturas");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("UMSTOCK",item.umstock);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturas item) {

        upd.init("D_facturas");

        upd.add("PRODUCTO",item.producto);
        upd.add("CANT",item.cant);
        upd.add("UMSTOCK",item.umstock);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

