package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturacorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturacor";
    private String sql;
    public ArrayList<clsClasses.clsD_facturacor> items= new ArrayList<clsClasses.clsD_facturacor>();

    public clsD_facturacorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturacor item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturacor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturacor item) {
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

    public clsClasses.clsD_facturacor first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_facturacor item) {

        ins.init("D_facturacor");

        ins.add("ID",item.id);
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("PRODUCTO",item.producto);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("TOTAL",item.total);
        ins.add("AUTORIZO",item.autorizo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturacor item) {

        upd.init("D_facturacor");

        upd.add("EMPRESA",item.empresa);
        upd.add("COREL",item.corel);
        upd.add("ANULADO",item.anulado);
        upd.add("PRODUCTO",item.producto);
        upd.add("UM",item.um);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("TOTAL",item.total);
        upd.add("AUTORIZO",item.autorizo);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_facturacor item) {
        sql="DELETE FROM D_facturacor WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturacor WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturacor item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturacor();

            item.id=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.corel=dt.getString(2);
            item.anulado=dt.getInt(3);
            item.producto=dt.getInt(4);
            item.um=dt.getString(5);
            item.cant=dt.getDouble(6);
            item.precio=dt.getDouble(7);
            item.total=dt.getDouble(8);
            item.autorizo=dt.getInt(9);

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

    public String addItemSql(clsClasses.clsD_facturacor item) {

        ins.init("D_facturacor");

        ins.add("ID",item.id);
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("PRODUCTO",item.producto);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("TOTAL",item.total);
        ins.add("AUTORIZO",item.autorizo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturacor item) {

        upd.init("D_facturacor");

        upd.add("EMPRESA",item.empresa);
        upd.add("COREL",item.corel);
        upd.add("ANULADO",item.anulado);
        upd.add("PRODUCTO",item.producto);
        upd.add("UM",item.um);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("TOTAL",item.total);
        upd.add("AUTORIZO",item.autorizo);

        upd.Where("(ID="+item.id+")");

        return upd.sql();


    }

    //endregion
}

