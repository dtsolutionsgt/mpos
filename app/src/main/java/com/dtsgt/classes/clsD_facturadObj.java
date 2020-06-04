package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturadObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturad";
    private String sql;
    public ArrayList<clsClasses.clsD_facturad> items= new ArrayList<clsClasses.clsD_facturad>();

    public clsD_facturadObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturad item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturad item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturad item) {
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

    public clsClasses.clsD_facturad first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturad item) {

        ins.init("D_facturad");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("UMPESO",item.umpeso);
        ins.add("ANULADO",item.anulado);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("PRECIODOC",item.preciodoc);
        ins.add("PESO",item.peso);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("UMVENTA",item.umventa);
        ins.add("FACTOR",item.factor);
        ins.add("UMSTOCK",item.umstock);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturad item) {

        upd.init("D_facturad");

        upd.add("ANULADO",item.anulado);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("PRECIODOC",item.preciodoc);
        upd.add("PESO",item.peso);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("UMVENTA",item.umventa);
        upd.add("FACTOR",item.factor);
        upd.add("UMSTOCK",item.umstock);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+") AND (UMPESO='"+item.umpeso+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturad item) {
        sql="DELETE FROM D_facturad WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+") AND (UMPESO='"+item.umpeso+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturad WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturad item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturad();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.producto=dt.getInt(2);
            item.umpeso=dt.getString(3);
            item.anulado= (dt.getInt(4)==1?true:false);
            item.cant=dt.getDouble(5);
            item.precio=dt.getDouble(6);
            item.imp=dt.getDouble(7);
            item.des=dt.getDouble(8);
            item.desmon=dt.getDouble(9);
            item.total=dt.getDouble(10);
            item.preciodoc=dt.getDouble(11);
            item.peso=dt.getDouble(12);
            item.val1=dt.getDouble(13);
            item.val2=dt.getString(14);
            item.umventa=dt.getString(15);
            item.factor=dt.getDouble(16);
            item.umstock=dt.getString(17);

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

    public String addItemSql(clsClasses.clsD_facturad item) {

        ins.init("D_facturad");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("UMPESO",item.umpeso);
        ins.add("ANULADO",item.anulado);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("PRECIODOC",item.preciodoc);
        ins.add("PESO",item.peso);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("UMVENTA",item.umventa);
        ins.add("FACTOR",item.factor);
        ins.add("UMSTOCK",item.umstock);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturad item) {

        upd.init("D_facturad");

        upd.add("ANULADO",item.anulado);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("PRECIODOC",item.preciodoc);
        upd.add("PESO",item.peso);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("UMVENTA",item.umventa);
        upd.add("FACTOR",item.factor);
        upd.add("UMSTOCK",item.umstock);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (PRODUCTO="+item.producto+") AND (UMPESO='"+item.umpeso+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

