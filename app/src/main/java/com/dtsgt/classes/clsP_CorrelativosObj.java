package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsP_CorrelativosObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_CORREL_OTROS";
    private String sql;
    public ArrayList<clsClasses.clsP_correlativos> items = new ArrayList<clsClasses.clsP_correlativos>();

    public clsP_CorrelativosObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_correlativos item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_correlativos item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_correlativos item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_correlativos first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_correlativos item) {

        ins.init("P_CORREL_OTROS");

        ins.add("EMPRESA", item.empresa);
        ins.add("RUTA", item.ruta);
        ins.add("SERIE", item.serie);
        ins.add("TIPO", item.tipo);
        ins.add("INICIAL", item.inicial);
        ins.add("FINAL", item.fin);
        ins.add("ACTUAL", item.actual);
        ins.add("ENVIADO", item.enviado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_correlativos item) {

        upd.init("P_CORREL_OTROS");

        upd.add("SERIE", item.serie);
        upd.add("TIPO", item.tipo);
        upd.add("INICIAL", item.inicial);
        upd.add("FINAL", item.fin);
        upd.add("ACTUAL", item.actual);

        upd.Where("(EMPRESA=" + item.empresa + "), (RUTA=" + item.empresa + "),  (TIPO=" + item.tipo + "),  (ENVIADO=" + item.enviado + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_correlativos item) {
        sql = "DELETE FROM P_CORREL_OTROS WHERE (EMPRESA=" + item.empresa + "), (RUTA=" + item.empresa + "),  (TIPO=" + item.tipo + "),  (ENVIADO=" + item.enviado + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM P_CORREL_OTROS WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_correlativos item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_correlativos();

            item.empresa = dt.getString(0);
            item.ruta = dt.getString(1);
            item.serie = dt.getString(2);
            item.tipo = dt.getString(3);
            item.inicial = dt.getInt(4);
            item.fin = dt.getInt(5);
            item.actual = dt.getInt(6);
            item.enviado = dt.getString(6);

            items.add(item);

            dt.moveToNext();
        }

        if (dt != null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt = null;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        if (dt != null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_correlativos item) {

        ins.init("P_CORREL_OTROS");

        ins.add("EMPRESA", item.empresa);
        ins.add("RUTA", item.ruta);
        ins.add("SERIE", item.serie);
        ins.add("TIPO", item.tipo);
        ins.add("INICIAL", item.inicial);
        ins.add("FINAL", item.fin);
        ins.add("ACTUAL", item.actual);
        ins.add("ENVIADO", item.enviado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_correlativos item) {

        upd.init("P_CORREL_OTROS");

        upd.add("SERIE", item.serie);
        upd.add("TIPO", item.tipo);
        upd.add("INICIAL", item.inicial);
        upd.add("FINAL", item.fin);
        upd.add("ACTUAL", item.actual);

        upd.Where("(EMPRESA=" + item.empresa + "), (RUTA=" + item.empresa + "),  (TIPO=" + item.tipo + "),  (ENVIADO=" + item.enviado + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}