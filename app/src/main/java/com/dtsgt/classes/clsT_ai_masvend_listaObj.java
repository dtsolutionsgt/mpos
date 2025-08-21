package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ai_masvend_listaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ai_masvend_lista";
    private String sql;
    public ArrayList<clsClasses.clsT_ai_masvend_lista> items= new ArrayList<clsClasses.clsT_ai_masvend_lista>();

    public clsT_ai_masvend_listaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ai_masvend_lista item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ai_masvend_lista item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ai_masvend_lista item) {
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

    public clsClasses.clsT_ai_masvend_lista first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_ai_masvend_lista item) {

        ins.init("T_ai_masvend_lista");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("CANT",item.cant);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ai_masvend_lista item) {

        upd.init("T_ai_masvend_lista");

        upd.add("CANT",item.cant);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+") And (FECHA="+item.fecha+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_ai_masvend_lista item) {
        sql="DELETE FROM T_ai_masvend_lista WHERE (CODIGO_PRODUCTO="+item.codigo_producto+") And (FECHA="+item.fecha+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ai_masvend_lista WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ai_masvend_lista item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ai_masvend_lista();

            item.codigo_producto=dt.getInt(0);
            item.fecha=dt.getLong(1);
            item.cant=dt.getInt(2);

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
            String sel="SELECT MAX( + idsql + ) FROM T_ai_masvend_lista";
            dt=Con.OpenDT(sel);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_ai_masvend_lista item) {

        ins.init("T_ai_masvend_lista");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("CANT",item.cant);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ai_masvend_lista item) {

        upd.init("T_ai_masvend_lista");

        upd.add("CANT",item.cant);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+") And (FECHA="+item.fecha+")");

        return upd.sql();


    }

    //endregion
}

