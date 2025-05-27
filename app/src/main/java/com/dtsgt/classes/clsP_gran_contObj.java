package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_gran_contObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_gran_cont";
    private String sql;
    public ArrayList<clsClasses.clsP_gran_cont> items= new ArrayList<clsClasses.clsP_gran_cont>();

    public clsP_gran_contObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_gran_cont item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_gran_cont item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_gran_cont item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsP_gran_cont first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_gran_cont item) {

        ins.init("P_gran_cont");

        ins.add("NRC",item.nrc);
        ins.add("IDDEP",item.iddep);
        ins.add("IDMUNI",item.idmuni);
        ins.add("IDNEG",item.idneg);
        ins.add("DEP",item.dep);
        ins.add("MUNI",item.muni);
        ins.add("NIT",item.nit);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_gran_cont item) {

        upd.init("P_gran_cont");

        upd.add("IDDEP",item.iddep);
        upd.add("IDMUNI",item.idmuni);
        upd.add("IDNEG",item.idneg);
        upd.add("DEP",item.dep);
        upd.add("MUNI",item.muni);
        upd.add("NIT",item.nit);

        upd.Where("(NRC='"+item.nrc+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_gran_cont item) {
        sql="DELETE FROM P_gran_cont WHERE (NRC='"+item.nrc+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_gran_cont WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_gran_cont item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_gran_cont();

            item.nrc=dt.getString(0);
            item.iddep=dt.getString(1);
            item.idmuni=dt.getString(2);
            item.idneg=dt.getString(3);
            item.dep=dt.getString(4);
            item.muni=dt.getString(5);
            item.nit=dt.getString(6);

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

    public String addItemSql(clsClasses.clsP_gran_cont item) {

        ins.init("P_gran_cont");

        ins.add("NRC",item.nrc);
        ins.add("IDDEP",item.iddep);
        ins.add("IDMUNI",item.idmuni);
        ins.add("IDNEG",item.idneg);
        ins.add("DEP",item.dep);
        ins.add("MUNI",item.muni);
        ins.add("NIT",item.nit);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_gran_cont item) {

        upd.init("P_gran_cont");

        upd.add("IDDEP",item.iddep);
        upd.add("IDMUNI",item.idmuni);
        upd.add("IDNEG",item.idneg);
        upd.add("DEP",item.dep);
        upd.add("MUNI",item.muni);
        upd.add("NIT",item.nit);

        upd.Where("(NRC='"+item.nrc+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

