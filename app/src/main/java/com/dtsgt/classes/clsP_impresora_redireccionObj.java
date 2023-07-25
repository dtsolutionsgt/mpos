package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_impresora_redireccionObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_impresora_redireccion";
    private String sql;
    public ArrayList<clsClasses.clsP_impresora_redireccion> items= new ArrayList<clsClasses.clsP_impresora_redireccion>();

    public clsP_impresora_redireccionObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_impresora_redireccion item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_impresora_redireccion item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_impresora_redireccion item) {
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

    public clsClasses.clsP_impresora_redireccion first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_impresora_redireccion item) {

        ins.init("P_impresora_redireccion");

        ins.add("CODIGO_REDIR",item.codigo_redir);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);
        ins.add("CODIGO_IMPRESORA_FINAL",item.codigo_impresora_final);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_impresora_redireccion item) {

        upd.init("P_impresora_redireccion");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("CODIGO_IMPRESORA",item.codigo_impresora);
        upd.add("CODIGO_IMPRESORA_FINAL",item.codigo_impresora_final);

        upd.Where("(CODIGO_REDIR="+item.codigo_redir+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_impresora_redireccion item) {
        sql="DELETE FROM P_impresora_redireccion WHERE (CODIGO_REDIR="+item.codigo_redir+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_impresora_redireccion WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_impresora_redireccion item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_impresora_redireccion();

            item.codigo_redir=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_ruta=dt.getInt(2);
            item.codigo_impresora=dt.getInt(3);
            item.codigo_impresora_final=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsP_impresora_redireccion item) {

        ins.init("P_impresora_redireccion");

        ins.add("CODIGO_REDIR",item.codigo_redir);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);
        ins.add("CODIGO_IMPRESORA_FINAL",item.codigo_impresora_final);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_impresora_redireccion item) {

        upd.init("P_impresora_redireccion");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("CODIGO_IMPRESORA",item.codigo_impresora);
        upd.add("CODIGO_IMPRESORA_FINAL",item.codigo_impresora_final);

        upd.Where("(CODIGO_REDIR="+item.codigo_redir+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

