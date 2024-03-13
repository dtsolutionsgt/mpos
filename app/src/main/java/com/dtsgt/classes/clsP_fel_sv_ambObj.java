package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_fel_sv_ambObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_fel_sv_amb";
    private String sql;
    public ArrayList<clsClasses.clsP_fel_sv_amb> items= new ArrayList<clsClasses.clsP_fel_sv_amb>();

    public clsP_fel_sv_ambObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_fel_sv_amb item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_fel_sv_amb item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_fel_sv_amb item) {
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

    public clsClasses.clsP_fel_sv_amb first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_fel_sv_amb item) {

        ins.init("P_fel_sv_amb");

        ins.add("ID",item.id);
        ins.add("ambiente",item.ambiente);
        ins.add("archivo",item.archivo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_fel_sv_amb item) {

        upd.init("P_fel_sv_amb");

        upd.add("ambiente",item.ambiente);
        upd.add("archivo",item.archivo);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_fel_sv_amb item) {
        sql="DELETE FROM P_fel_sv_amb WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_fel_sv_amb WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_fel_sv_amb item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_fel_sv_amb();

            item.id=dt.getInt(0);
            item.ambiente=dt.getInt(1);
            item.archivo=dt.getString(2);

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

    public String addItemSql(clsClasses.clsP_fel_sv_amb item) {

        ins.init("P_fel_sv_amb");

        ins.add("ID",item.id);
        ins.add("ambiente",item.ambiente);
        ins.add("archivo",item.archivo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_fel_sv_amb item) {

        upd.init("P_fel_sv_amb");

        upd.add("ambiente",item.ambiente);
        upd.add("archivo",item.archivo);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

