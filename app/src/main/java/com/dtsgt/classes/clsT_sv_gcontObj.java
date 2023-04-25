package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_sv_gcontObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_sv_gcont";
    private String sql;
    public ArrayList<clsClasses.clsT_sv_gcont> items= new ArrayList<clsClasses.clsT_sv_gcont>();

    public clsT_sv_gcontObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_sv_gcont item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_sv_gcont item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_sv_gcont item) {
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

    public clsClasses.clsT_sv_gcont first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_sv_gcont item) {

        ins.init("T_sv_gcont");

        ins.add("ID",item.id);
        ins.add("IDDEP",item.iddep);
        ins.add("IDMUNI",item.idmuni);
        ins.add("IDNEG",item.idneg);
        ins.add("DEP",item.dep);
        ins.add("MUNI",item.muni);
        ins.add("NEG",item.neg);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_sv_gcont item) {

        upd.init("T_sv_gcont");

        upd.add("IDDEP",item.iddep);
        upd.add("IDMUNI",item.idmuni);
        upd.add("IDNEG",item.idneg);
        upd.add("DEP",item.dep);
        upd.add("MUNI",item.muni);
        upd.add("NEG",item.neg);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_sv_gcont item) {
        sql="DELETE FROM T_sv_gcont WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_sv_gcont WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_sv_gcont item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_sv_gcont();

            item.id=dt.getInt(0);
            item.iddep=dt.getString(1);
            item.idmuni=dt.getString(2);
            item.idneg=dt.getInt(3);
            item.dep=dt.getString(4);
            item.muni=dt.getString(5);
            item.neg=dt.getString(6);

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

    public String addItemSql(clsClasses.clsT_sv_gcont item) {

        ins.init("T_sv_gcont");

        ins.add("ID",item.id);
        ins.add("IDDEP",item.iddep);
        ins.add("IDMUNI",item.idmuni);
        ins.add("IDNEG",item.idneg);
        ins.add("DEP",item.dep);
        ins.add("MUNI",item.muni);
        ins.add("NEG",item.neg);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_sv_gcont item) {

        upd.init("T_sv_gcont");

        upd.add("IDDEP",item.iddep);
        upd.add("IDMUNI",item.idmuni);
        upd.add("IDNEG",item.idneg);
        upd.add("DEP",item.dep);
        upd.add("MUNI",item.muni);
        upd.add("NEG",item.neg);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

