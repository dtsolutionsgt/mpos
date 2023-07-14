package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsTx_ordenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Tx_orden";
    private String sql;
    public ArrayList<clsClasses.clsTx_orden> items= new ArrayList<clsClasses.clsTx_orden>();

    public clsTx_ordenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsTx_orden item) {
        addItem(item);
    }

    public void update(clsClasses.clsTx_orden item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsTx_orden item) {
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

    public clsClasses.clsTx_orden first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsTx_orden item) {

        ins.init("Tx_orden");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
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
        ins.add("CUENTA",item.cuenta);
        ins.add("ESTADO",item.estado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsTx_orden item) {

        upd.init("Tx_orden");

        upd.add("PRODUCTO",item.producto);
        upd.add("EMPRESA",item.empresa);
        upd.add("UM",item.um);
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
        upd.add("CUENTA",item.cuenta);
        upd.add("ESTADO",item.estado);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsTx_orden item) {
        sql="DELETE FROM Tx_orden WHERE (ID="+item.id+") AND (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM Tx_orden WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsTx_orden item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsTx_orden();

            item.id=dt.getInt(0);
            item.corel=dt.getString(1);
            item.producto=dt.getString(2);
            item.empresa=dt.getString(3);
            item.um=dt.getString(4);
            item.cant=dt.getDouble(5);
            item.umstock=dt.getString(6);
            item.factor=dt.getDouble(7);
            item.precio=dt.getDouble(8);
            item.imp=dt.getDouble(9);
            item.des=dt.getDouble(10);
            item.desmon=dt.getDouble(11);
            item.total=dt.getDouble(12);
            item.preciodoc=dt.getDouble(13);
            item.peso=dt.getDouble(14);
            item.val1=dt.getDouble(15);
            item.val2=dt.getString(16);
            item.val3=dt.getDouble(17);
            item.val4=dt.getString(18);
            item.percep=dt.getDouble(19);
            item.cuenta=dt.getInt(20);
            item.estado=dt.getInt(21);

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

    public String addItemSql(clsClasses.clsTx_orden item) {

        ins.init("Tx_orden");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
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
        ins.add("CUENTA",item.cuenta);
        ins.add("ESTADO",item.estado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsTx_orden item) {

        upd.init("Tx_orden");

        upd.add("PRODUCTO",item.producto);
        upd.add("EMPRESA",item.empresa);
        upd.add("UM",item.um);
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
        upd.add("CUENTA",item.cuenta);
        upd.add("ESTADO",item.estado);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

