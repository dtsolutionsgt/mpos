package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsP_fraseObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_frase";
    private String sql;
    public ArrayList<clsClasses.clsP_frase> items= new ArrayList<clsClasses.clsP_frase>();

    public clsP_fraseObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_frase item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_frase item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_frase item) {
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

    public clsClasses.clsP_frase first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_frase item) {

        ins.init("P_frase");

        ins.add("CODIGO_FRASE",item.codigo_frase);
        ins.add("TEXTO",item.texto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_frase item) {

        upd.init("P_frase");

        upd.add("TEXTO",item.texto);

        upd.Where("(CODIGO_FRASE="+item.codigo_frase+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_frase item) {
        sql="DELETE FROM P_frase WHERE (CODIGO_FRASE="+item.codigo_frase+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_frase WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_frase item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_frase();
            item.codigo_frase=dt.getInt(0);
            item.texto=dt.getString(1);
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

    public String addItemSql(clsClasses.clsP_frase item) {

        ins.init("P_frase");

        ins.add("CODIGO_FRASE",item.codigo_frase);
        ins.add("TEXTO",item.texto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_frase item) {

        upd.init("P_frase");

        upd.add("TEXTO",item.texto);

        upd.Where("(CODIGO_FRASE="+item.codigo_frase+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

