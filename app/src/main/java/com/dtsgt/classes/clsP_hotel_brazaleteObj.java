package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_hotel_brazaleteObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_hotel_brazalete";
    private String sql;
    public ArrayList<clsClasses.clsP_hotel_brazalete> items= new ArrayList<clsClasses.clsP_hotel_brazalete>();

    public clsP_hotel_brazaleteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_hotel_brazalete item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_hotel_brazalete item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_hotel_brazalete item) {
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

    public clsClasses.clsP_hotel_brazalete first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsP_hotel_brazalete item) {

        ins.init("P_hotel_brazalete");

        ins.add("CODIGO",item.codigo);
        ins.add("BARRA",item.barra);
        ins.add("HABITACION",item.habitacion);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_hotel_brazalete item) {

        upd.init("P_hotel_brazalete");

        upd.add("BARRA",item.barra);
        upd.add("HABITACION",item.habitacion);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsP_hotel_brazalete item) {
        sql="DELETE FROM P_hotel_brazalete WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_hotel_brazalete WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_hotel_brazalete item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_hotel_brazalete();

            item.codigo=dt.getInt(0);
            item.barra=dt.getString(1);
            item.habitacion=dt.getString(2);
            item.codigo_cliente=dt.getInt(3);

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

    public String addItemSql(clsClasses.clsP_hotel_brazalete item) {

        ins.init("P_hotel_brazalete");

        ins.add("CODIGO",item.codigo);
        ins.add("BARRA",item.barra);
        ins.add("HABITACION",item.habitacion);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_hotel_brazalete item) {

        upd.init("P_hotel_brazalete");

        upd.add("BARRA",item.barra);
        upd.add("HABITACION",item.habitacion);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();


    }

    //endregion
}

