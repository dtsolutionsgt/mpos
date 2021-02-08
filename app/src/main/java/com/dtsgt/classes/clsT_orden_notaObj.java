package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_orden_notaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_orden_nota";
    private String sql;
    public ArrayList<clsClasses.clsT_orden_nota> items= new ArrayList<clsClasses.clsT_orden_nota>();

    public clsT_orden_notaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_orden_nota item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_orden_nota item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_orden_nota item) {
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

    public clsClasses.clsT_orden_nota first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_orden_nota item) {

        ins.init("T_orden_nota");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_orden_nota item) {

        upd.init("T_orden_nota");

        upd.add("NOTA",item.nota);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_orden_nota item) {
        sql="DELETE FROM T_orden_nota WHERE (ID="+item.id+") AND (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_orden_nota WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_orden_nota item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_orden_nota();

            item.id=dt.getInt(0);
            item.corel=dt.getString(1);
            item.nota=dt.getString(2);

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

    public String addItemSql(clsClasses.clsT_orden_nota item) {

        ins.init("T_orden_nota");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_orden_nota item) {

        upd.init("T_orden_nota");

        upd.add("NOTA",item.nota);

        upd.Where("(ID="+item.id+") AND (COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

