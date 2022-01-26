package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_costoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_costo";
    private String sql;
    public ArrayList<clsClasses.clsT_costo> items= new ArrayList<clsClasses.clsT_costo>();

    public clsT_costoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_costo item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_costo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_costo item) {
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

    public clsClasses.clsT_costo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_costo item) {

        ins.init("T_costo");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_COSTO",item.codigo_costo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("COSTO",item.costo);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_costo item) {

        upd.init("T_costo");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("FECHA",item.fecha);
        upd.add("COSTO",item.costo);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"') AND (CODIGO_COSTO="+item.codigo_costo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_costo item) {
        sql="DELETE FROM T_costo WHERE (COREL='"+item.corel+"') AND (CODIGO_COSTO="+item.codigo_costo+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_costo WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_costo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_costo();

            item.corel=dt.getString(0);
            item.codigo_costo=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
            item.fecha=dt.getLong(3);
            item.costo=dt.getDouble(4);
            item.codigo_proveedor=dt.getInt(5);
            item.statcom=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsT_costo item) {

        ins.init("T_costo");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_COSTO",item.codigo_costo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("FECHA",item.fecha);
        ins.add("COSTO",item.costo);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_costo item) {

        upd.init("T_costo");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("FECHA",item.fecha);
        upd.add("COSTO",item.costo);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"') AND (CODIGO_COSTO="+item.codigo_costo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

