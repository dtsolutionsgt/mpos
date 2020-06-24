package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_fel_errorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_fel_error";
    private String sql;
    public ArrayList<clsClasses.clsD_fel_error> items= new ArrayList<clsClasses.clsD_fel_error>();

    public clsD_fel_errorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_fel_error item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_fel_error item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_fel_error item) {
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

    public clsClasses.clsD_fel_error first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_fel_error item) {

        ins.init("D_fel_error");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("FECHA",item.fecha);
        ins.add("NIVEL",item.nivel);
        ins.add("ERROR",item.error);
        ins.add("ENVIADO",item.enviado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_fel_error item) {

        upd.init("D_fel_error");

        upd.add("FECHA",item.fecha);
        upd.add("NIVEL",item.nivel);
        upd.add("ERROR",item.error);
        upd.add("ENVIADO",item.enviado);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_fel_error item) {
        sql="DELETE FROM D_fel_error WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_fel_error WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_fel_error item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_fel_error();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.item=dt.getInt(2);
            item.fecha=dt.getInt(3);
            item.nivel=dt.getInt(4);
            item.error=dt.getString(5);
            item.enviado=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsD_fel_error item) {

        ins.init("D_fel_error");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("FECHA",item.fecha);
        ins.add("NIVEL",item.nivel);
        ins.add("ERROR",item.error);
        ins.add("ENVIADO",item.enviado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_fel_error item) {

        upd.init("D_fel_error");

        upd.add("FECHA",item.fecha);
        upd.add("NIVEL",item.nivel);
        upd.add("ERROR",item.error);
        upd.add("ENVIADO",item.enviado);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

