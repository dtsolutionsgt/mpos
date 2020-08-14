package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ventaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_venta";
    private String sql;
    public ArrayList<clsClasses.clsT_venta> items= new ArrayList<clsClasses.clsT_venta>();

    public clsT_ventaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_venta item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_venta item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_venta item) {
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

    public clsClasses.clsT_venta first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_venta item) {

        ins.init("T_venta");

        ins.add("PRODUCTO",item.producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("UMSTOCK",item.umstock);
        ins.add("FACTOR",item.factor);
        ins.add("PRECIO",item.precio);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("PRECIODOC",item.preciodoc);
        ins.add("PESO",item.peso);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("VAL3",item.val3);
        ins.add("VAL4",item.val4);
        ins.add("PERCEP",item.percep);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_venta item) {

        upd.init("T_venta");

        upd.add("CANT",item.cant);
        upd.add("UMSTOCK",item.umstock);
        upd.add("FACTOR",item.factor);
        upd.add("PRECIO",item.precio);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("PRECIODOC",item.preciodoc);
        upd.add("PESO",item.peso);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("VAL3",item.val3);
        upd.add("VAL4",item.val4);
        upd.add("PERCEP",item.percep);

        upd.Where("(PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_venta item) {
        sql="DELETE FROM T_venta WHERE (PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_venta WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_venta item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_venta();

            item.producto=dt.getString(0);
            item.empresa=dt.getString(1);
            item.um=dt.getString(2);
            item.cant=dt.getDouble(3);
            item.umstock=dt.getString(4);
            item.factor=dt.getDouble(5);
            item.precio=dt.getDouble(6);
            item.imp=dt.getDouble(7);
            item.des=dt.getDouble(8);
            item.desmon=dt.getDouble(9);
            item.total=dt.getDouble(10);
            item.preciodoc=dt.getDouble(11);
            item.peso=dt.getDouble(12);
            item.val1=dt.getDouble(13);
            item.val2=dt.getString(14);
            item.val3=dt.getDouble(15);
            item.val4=dt.getString(16);
            item.percep=dt.getDouble(17);

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

    public String addItemSql(clsClasses.clsT_venta item) {

        ins.init("T_venta");

        ins.add("PRODUCTO",item.producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("UMSTOCK",item.umstock);
        ins.add("FACTOR",item.factor);
        ins.add("PRECIO",item.precio);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("PRECIODOC",item.preciodoc);
        ins.add("PESO",item.peso);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("VAL3",item.val3);
        ins.add("VAL4",item.val4);
        ins.add("PERCEP",item.percep);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_venta item) {

        upd.init("T_venta");

        upd.add("CANT",item.cant);
        upd.add("UMSTOCK",item.umstock);
        upd.add("FACTOR",item.factor);
        upd.add("PRECIO",item.precio);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("PRECIODOC",item.preciodoc);
        upd.add("PESO",item.peso);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);
        upd.add("VAL3",item.val3);
        upd.add("VAL4",item.val4);
        upd.add("PERCEP",item.percep);

        upd.Where("(PRODUCTO='"+item.producto+"') AND (EMPRESA='"+item.empresa+"') AND (UM='"+item.um+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

