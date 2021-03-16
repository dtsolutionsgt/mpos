package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_cajahoraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cajahora";
    private String sql;
    public ArrayList<clsClasses.clsP_cajahora> items= new ArrayList<clsClasses.clsP_cajahora>();

    public clsP_cajahoraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cajahora item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cajahora item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cajahora item) {
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

    public clsClasses.clsP_cajahora first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cajahora item) {

        ins.init("P_cajahora");
        ins.add("COREL",item.corel);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cajahora item) {

        upd.init("P_cajahora");

        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);

        upd.Where("(COREL="+item.corel+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cajahora item) {
        sql="DELETE FROM P_cajahora WHERE (COREL="+item.corel+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_cajahora WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cajahora item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cajahora();

            item.corel=dt.getInt(0);
            item.fechaini=dt.getLong(1);
            item.fechafin=dt.getLong(2);

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

    public String addItemSql(clsClasses.clsP_cajahora item) {

        ins.init("P_cajahora");

        ins.add("COREL",item.corel);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cajahora item) {

        upd.init("P_cajahora");

        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);

        upd.Where("(COREL="+item.corel+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

