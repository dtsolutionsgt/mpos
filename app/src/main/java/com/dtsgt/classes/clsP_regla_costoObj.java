package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_regla_costoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_regla_costo";
    private String sql;
    public ArrayList<clsClasses.clsP_regla_costo> items= new ArrayList<clsClasses.clsP_regla_costo>();

    public clsP_regla_costoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_regla_costo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_regla_costo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_regla_costo item) {
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

    public clsClasses.clsP_regla_costo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_regla_costo item) {

        ins.init("P_regla_costo");

        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("DIAS",item.dias);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_regla_costo item) {

        upd.init("P_regla_costo");

        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("DIAS",item.dias);

        upd.Where("(CODIGO_EMPRESA="+item.codigo_empresa+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_regla_costo item) {
        sql="DELETE FROM P_regla_costo WHERE (CODIGO_EMPRESA="+item.codigo_empresa+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_regla_costo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_regla_costo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_regla_costo();

            item.codigo_empresa=dt.getInt(0);
            item.codigo_tipo=dt.getInt(1);
            item.dias=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsP_regla_costo item) {

        ins.init("P_regla_costo");

        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("DIAS",item.dias);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_regla_costo item) {

        upd.init("P_regla_costo");

        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("DIAS",item.dias);

        upd.Where("(CODIGO_EMPRESA="+item.codigo_empresa+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

