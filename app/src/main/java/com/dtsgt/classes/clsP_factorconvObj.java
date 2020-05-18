package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_factorconvObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_factorconv";
    private String sql;
    public ArrayList<clsClasses.clsP_factorconv> items = new ArrayList<clsClasses.clsP_factorconv>();

    public clsP_factorconvObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_factorconv item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_factorconv item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_factorconv item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsP_factorconv first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_factorconv item) {

        ins.init("P_factorconv");

        ins.add("PRODUCTO", item.producto);
        ins.add("UNIDADSUPERIOR", item.unidadsuperior);
        ins.add("FACTORCONVERSION", item.factorconversion);
        ins.add("UNIDADMINIMA", item.unidadminima);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_factorconv item) {

        upd.init("P_factorconv");

        upd.add("FACTORCONVERSION", item.factorconversion);

        upd.Where("(PRODUCTO='" + item.producto + "') AND (UNIDADSUPERIOR='" + item.unidadsuperior + "') AND (UNIDADMINIMA='" + item.unidadminima + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_factorconv item) {
        sql = "DELETE FROM P_factorconv WHERE (PRODUCTO='" + item.producto + "') AND (UNIDADSUPERIOR='" + item.unidadsuperior + "') AND (UNIDADMINIMA='" + item.unidadminima + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_factorconv WHERE PRODUCTO='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_factorconv item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_factorconv();

            item.producto = dt.getInt(0);
            item.unidadsuperior = dt.getString(1);
            item.factorconversion = dt.getDouble(2);
            item.unidadminima = dt.getString(3);
            item.codigo_factorconv = dt.getInt(4);

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

    public String addItemSql(clsClasses.clsP_factorconv item) {

        ins.init("P_factorconv");

        ins.add("PRODUCTO", item.producto);
        ins.add("UNIDADSUPERIOR", item.unidadsuperior);
        ins.add("FACTORCONVERSION", item.factorconversion);
        ins.add("UNIDADMINIMA", item.unidadminima);
        ins.add("CODIGO_FACTORCONV", item.codigo_factorconv);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_factorconv item) {

        upd.init("P_factorconv");

        upd.add("FACTORCONVERSION", item.factorconversion);

        //#CKFK 20200517 Modifiqué la llave compuesta por una llave única
        //upd.Where("(PRODUCTO='" + item.producto + "') AND (UNIDADSUPERIOR='" + item.unidadsuperior + "') AND (UNIDADMINIMA='" + item.unidadminima + "')");
        upd.Where("(CODIGO_FACTORCONV='" + item.codigo_factorconv + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

