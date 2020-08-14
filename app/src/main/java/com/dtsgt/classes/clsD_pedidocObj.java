package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_pedidocObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_pedidoc";
    private String sql;
    public ArrayList<clsClasses.clsD_pedidoc> items= new ArrayList<clsClasses.clsD_pedidoc>();

    public clsD_pedidocObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_pedidoc item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_pedidoc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_pedidoc item) {
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

    public clsClasses.clsD_pedidoc first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_pedidoc item) {

        ins.init("D_pedidoc");

        ins.add("COREL",item.corel);
        ins.add("NOMBRE",item.nombre);
        ins.add("TELEFONO",item.telefono);
        ins.add("DIRECCION",item.direccion);
        ins.add("REFERENCIA",item.referencia);
        ins.add("NIT",item.nit);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_pedidoc item) {

        upd.init("D_pedidoc");

        upd.add("NOMBRE",item.nombre);
        upd.add("TELEFONO",item.telefono);
        upd.add("DIRECCION",item.direccion);
        upd.add("REFERENCIA",item.referencia);
        upd.add("NIT",item.nit);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_pedidoc item) {
        sql="DELETE FROM D_pedidoc WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_pedidoc WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_pedidoc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_pedidoc();

            item.corel=dt.getString(0);
            item.nombre=dt.getString(1);
            item.telefono=dt.getString(2);
            item.direccion=dt.getString(3);
            item.referencia=dt.getString(4);
            item.nit=dt.getString(5);

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

    public String addItemSql(clsClasses.clsD_pedidoc item) {

        ins.init("D_pedidoc");

        ins.add("COREL",item.corel);
        ins.add("NOMBRE",item.nombre);
        ins.add("TELEFONO",item.telefono);
        ins.add("DIRECCION",item.direccion);
        ins.add("REFERENCIA",item.referencia);
        ins.add("NIT",item.nit);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_pedidoc item) {

        upd.init("D_pedidoc");

        upd.add("NOMBRE",item.nombre);
        upd.add("TELEFONO",item.telefono);
        upd.add("DIRECCION",item.direccion);
        upd.add("REFERENCIA",item.referencia);
        upd.add("NIT",item.nit);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

