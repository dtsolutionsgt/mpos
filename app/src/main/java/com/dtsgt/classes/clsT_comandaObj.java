package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_comandaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_comanda";
    private String sql;
    public ArrayList<clsClasses.clsT_comanda> items= new ArrayList<clsClasses.clsT_comanda>();

    public clsT_comandaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_comanda item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_comanda item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_comanda item) {
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

    public clsClasses.clsT_comanda first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_comanda item) {

        ins.init("T_comanda");

        ins.add("LINEA",item.linea);
        ins.add("ID",item.id);
        ins.add("TEXTO",item.texto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_comanda item) {

        upd.init("T_comanda");

        upd.add("ID",item.id);
        upd.add("TEXTO",item.texto);

        upd.Where("(LINEA="+item.linea+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_comanda item) {
        sql="DELETE FROM T_comanda WHERE (LINEA="+item.linea+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_comanda WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_comanda item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_comanda();

            item.linea=dt.getInt(0);
            item.id=dt.getInt(1);
            item.texto=dt.getString(2);

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

    public String addItemSql(clsClasses.clsT_comanda item) {

        ins.init("T_comanda");

        ins.add("LINEA",item.linea);
        ins.add("ID",item.id);
        ins.add("TEXTO",item.texto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_comanda item) {

        upd.init("T_comanda");

        upd.add("ID",item.id);
        upd.add("TEXTO",item.texto);

        upd.Where("(LINEA="+item.linea+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

