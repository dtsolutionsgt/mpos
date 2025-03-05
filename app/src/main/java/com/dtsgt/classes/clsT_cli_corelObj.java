package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_cli_corelObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_cli_corel";
    private String sql;
    public ArrayList<clsClasses.clsT_cli_corel> items= new ArrayList<clsClasses.clsT_cli_corel>();

    public clsT_cli_corelObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_cli_corel item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_cli_corel item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_cli_corel item) {
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

    public clsClasses.clsT_cli_corel first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_cli_corel item) {

        ins.init("T_cli_corel");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_cli_corel item) {

        upd.init("T_cli_corel");

        upd.add("COREL",item.corel);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_cli_corel item) {
        sql="DELETE FROM T_cli_corel WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_cli_corel WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_cli_corel item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_cli_corel();

            item.id=dt.getInt(0);
            item.corel=dt.getInt(1);

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

    public String addItemSql(clsClasses.clsT_cli_corel item) {

        ins.init("T_cli_corel");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_cli_corel item) {

        upd.init("T_cli_corel");

        upd.add("COREL",item.corel);

        upd.Where("(ID="+item.id+")");

        return upd.sql();


    }

    //endregion
}

