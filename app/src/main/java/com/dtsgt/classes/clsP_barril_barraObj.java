package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_barril_barraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_barril_barra";
    private String sql;
    public ArrayList<clsClasses.clsP_barril_barra> items= new ArrayList<clsClasses.clsP_barril_barra>();

    public clsP_barril_barraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_barril_barra item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_barril_barra item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_barril_barra item) {
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

    public clsClasses.clsP_barril_barra first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_barril_barra item) {

        ins.init("P_barril_barra");

        ins.add("CODIGO_BARRA",item.codigo_barra);
        ins.add("EMPRESA",item.empresa);
        ins.add("BARRA",item.barra);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_barril_barra item) {

        upd.init("P_barril_barra");

        upd.add("EMPRESA",item.empresa);
        upd.add("BARRA",item.barra);
        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);

        upd.Where("(CODIGO_BARRA="+item.codigo_barra+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_barril_barra item) {
        sql="DELETE FROM P_barril_barra WHERE (CODIGO_BARRA="+item.codigo_barra+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_barril_barra WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_barril_barra item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_barril_barra();

            item.codigo_barra=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.barra=dt.getString(2);
            item.codigo_tipo=dt.getInt(3);
            item.codigo_interno=dt.getString(4);

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

    public String addItemSql(clsClasses.clsP_barril_barra item) {

        ins.init("P_barril_barra");

        ins.add("CODIGO_BARRA",item.codigo_barra);
        ins.add("EMPRESA",item.empresa);
        ins.add("BARRA",item.barra);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_barril_barra item) {

        upd.init("P_barril_barra");

        upd.add("EMPRESA",item.empresa);
        upd.add("BARRA",item.barra);
        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);

        upd.Where("(CODIGO_BARRA="+item.codigo_barra+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

