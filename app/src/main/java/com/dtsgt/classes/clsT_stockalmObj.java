package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_stockalmObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_stockalm";
    private String sql;
    public ArrayList<clsClasses.clsT_stockalm> items= new ArrayList<clsClasses.clsT_stockalm>();

    public clsT_stockalmObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_stockalm item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_stockalm item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_stockalm item) {
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

    public clsClasses.clsT_stockalm first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_stockalm item) {

        ins.init("T_stockalm");

        ins.add("ID",item.id);
        ins.add("IDPROD",item.idprod);
        ins.add("IDALM",item.idalm);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_stockalm item) {

        upd.init("T_stockalm");

        upd.add("IDPROD",item.idprod);
        upd.add("IDALM",item.idalm);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_stockalm item) {
        sql="DELETE FROM T_stockalm WHERE (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_stockalm WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_stockalm item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_stockalm();

            item.id=dt.getInt(0);
            item.idprod=dt.getInt(1);
            item.idalm=dt.getInt(2);
            item.cant=dt.getDouble(3);
            item.um=dt.getString(4);

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

    public String addItemSql(clsClasses.clsT_stockalm item) {

        ins.init("T_stockalm");

        ins.add("ID",item.id);
        ins.add("IDPROD",item.idprod);
        ins.add("IDALM",item.idalm);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_stockalm item) {

        upd.init("T_stockalm");

        upd.add("IDPROD",item.idprod);
        upd.add("IDALM",item.idalm);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

