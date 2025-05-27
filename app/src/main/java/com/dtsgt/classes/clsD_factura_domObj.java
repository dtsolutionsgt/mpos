package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_factura_domObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura_dom";
    private String sql;
    public ArrayList<clsClasses.clsD_factura_dom> items= new ArrayList<clsClasses.clsD_factura_dom>();

    public clsD_factura_domObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura_dom item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura_dom item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura_dom item) {
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

    public clsClasses.clsD_factura_dom first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_factura_dom item) {

        ins.init("D_factura_dom");

        ins.add("COREL",item.corel);
        ins.add("LINEA",item.linea);
        ins.add("TEXTO",item.texto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura_dom item) {

        upd.init("D_factura_dom");

        upd.add("TEXTO",item.texto);

        upd.Where("(COREL='"+item.corel+"') AND (LINEA="+item.linea+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_factura_dom item) {
        sql="DELETE FROM D_factura_dom WHERE (COREL='"+item.corel+"') AND (LINEA="+item.linea+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_factura_dom WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura_dom item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura_dom();

            item.corel=dt.getString(0);
            item.linea=dt.getInt(1);
            item.texto=dt.getString(2);

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

    public String addItemSql(clsClasses.clsD_factura_dom item) {

        ins.init("D_factura_dom");

        ins.add("COREL",item.corel);
        ins.add("LINEA",item.linea);
        ins.add("TEXTO",item.texto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura_dom item) {

        upd.init("D_factura_dom");

        upd.add("TEXTO",item.texto);

        upd.Where("(COREL='"+item.corel+"') AND (LINEA="+item.linea+")");

        return upd.sql();


    }

    //endregion
}

