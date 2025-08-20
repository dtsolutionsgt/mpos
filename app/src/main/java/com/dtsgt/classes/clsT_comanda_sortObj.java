package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_comanda_sortObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_comanda_sort";
    private String sql;
    public ArrayList<clsClasses.clsT_comanda_sort> items= new ArrayList<clsClasses.clsT_comanda_sort>();

    public clsT_comanda_sortObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_comanda_sort item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_comanda_sort item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_comanda_sort item) {
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

    public clsClasses.clsT_comanda_sort first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsT_comanda_sort item) {

        ins.init("T_comanda_sort");

        ins.add("LINEA",item.linea);
        ins.add("ID",item.id);
        ins.add("GRUPO",item.grupo);
        ins.add("TEXTO",item.texto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_comanda_sort item) {

        upd.init("T_comanda_sort");

        upd.add("ID",item.id);
        upd.add("GRUPO",item.grupo);
        upd.add("TEXTO",item.texto);

        upd.Where("(LINEA="+item.linea+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsT_comanda_sort item) {
        sql="DELETE FROM T_comanda_sort WHERE (LINEA="+item.linea+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_comanda_sort WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_comanda_sort item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_comanda_sort();

            item.linea=dt.getInt(0);
            item.id=dt.getInt(1);
            item.grupo=dt.getInt(2);
            item.texto=dt.getString(3);

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
            String sel="SELECT MAX( + idsql + ) FROM T_comanda_sort";
            dt=Con.OpenDT(sel);
            dt.moveToFirst();
            nid=dt.getInt(0)+1;
        } catch (Exception e) {
            nid=1;
        }

        if (dt!=null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_comanda_sort item) {

        ins.init("T_comanda_sort");

        ins.add("LINEA",item.linea);
        ins.add("ID",item.id);
        ins.add("GRUPO",item.grupo);
        ins.add("TEXTO",item.texto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_comanda_sort item) {

        upd.init("T_comanda_sort");

        upd.add("ID",item.id);
        upd.add("GRUPO",item.grupo);
        upd.add("TEXTO",item.texto);

        upd.Where("(LINEA="+item.linea+")");

        return upd.sql();


    }

    //endregion
}

