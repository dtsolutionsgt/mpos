package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ai_masvendidosObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ai_masvendidos";
    private String sql;
    public ArrayList<clsClasses.clsT_ai_masvendidos> items= new ArrayList<clsClasses.clsT_ai_masvendidos>();

    public clsT_ai_masvendidosObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ai_masvendidos item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ai_masvendidos item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ai_masvendidos item) {
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

    public clsClasses.clsT_ai_masvendidos first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_ai_masvendidos item) {

        ins.init("T_ai_masvendidos");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("DIA",item.dia);
        ins.add("HORA",item.hora);
        ins.add("CANT",item.cant);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ai_masvendidos item) {

        upd.init("T_ai_masvendidos");

        upd.add("CANT",item.cant);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+") And (DIA="+item.dia+") And (HORA="+item.hora+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_ai_masvendidos item) {
        sql="DELETE FROM T_ai_masvendidos WHERE (CODIGO_PRODUCTO="+item.codigo_producto+") And (DIA="+item.dia+") And (HORA="+item.hora+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ai_masvendidos WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ai_masvendidos item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ai_masvendidos();

            item.codigo_producto=dt.getInt(0);
            item.dia=dt.getInt(1);
            item.hora=dt.getInt(2);
            item.cant=dt.getInt(3);
            item.nombre=dt.getString(4);

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

    public int newCor(String idsql) {
        Cursor dt=null;
        int nid;

        try {
            String sel="SELECT MAX( + idsql + ) FROM T_ai_masvendidos";
            dt=Con.OpenDT(sel);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_ai_masvendidos item) {

        ins.init("T_ai_masvendidos");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("DIA",item.dia);
        ins.add("HORA",item.hora);
        ins.add("CANT",item.cant);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ai_masvendidos item) {

        upd.init("T_ai_masvendidos");

        upd.add("CANT",item.cant);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+") And (DIA="+item.dia+") And (HORA="+item.hora+")");

        return upd.sql();


    }

    //endregion
}

