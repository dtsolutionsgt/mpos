package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_pedidoordenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_pedidoorden";
    private String sql;
    public ArrayList<clsClasses.clsD_pedidoorden> items= new ArrayList<clsClasses.clsD_pedidoorden>();

    public clsD_pedidoordenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_pedidoorden item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_pedidoorden item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_pedidoorden item) {
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

    public clsClasses.clsD_pedidoorden first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_pedidoorden item) {

        ins.init("D_pedidoorden");

        ins.add("COREL",item.corel);
        ins.add("ORDEN",item.orden);
        ins.add("TIPO",item.tipo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_pedidoorden item) {

        upd.init("D_pedidoorden");

        upd.add("ORDEN",item.orden);
        upd.add("TIPO",item.tipo);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_pedidoorden item) {
        sql="DELETE FROM D_pedidoorden WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_pedidoorden WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_pedidoorden item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_pedidoorden();

            item.corel=dt.getString(0);
            item.orden=dt.getString(1);
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

    public String addItemSql(clsClasses.clsD_pedidoorden item) {

        ins.init("D_pedidoorden");

        ins.add("COREL",item.corel);
        ins.add("ORDEN",item.orden);
        ins.add("TIPO",item.tipo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_pedidoorden item) {

        upd.init("D_pedidoorden");

        upd.add("ORDEN",item.orden);
        upd.add("TIPO",item.tipo);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

