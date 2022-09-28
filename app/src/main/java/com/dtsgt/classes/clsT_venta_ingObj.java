package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_venta_ingObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_venta_ing";
    private String sql;
    public ArrayList<clsClasses.clsT_venta_ing> items= new ArrayList<clsClasses.clsT_venta_ing>();

    public clsT_venta_ingObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_venta_ing item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_venta_ing item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_venta_ing item) {
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

    public clsClasses.clsT_venta_ing first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_venta_ing item) {

        ins.init("T_venta_ing");

        ins.add("CODIGO_ING",item.codigo_ing);
        ins.add("ID",item.id);
        ins.add("IDING",item.iding);
        ins.add("NOMBRE",item.nombre);
        ins.add("PUID",item.puid);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_venta_ing item) {

        upd.init("T_venta_ing");

        upd.add("ID",item.id);
        upd.add("IDING",item.iding);
        upd.add("NOMBRE",item.nombre);
        upd.add("PUID",item.puid);

        upd.Where("(CODIGO_ING="+item.codigo_ing+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_venta_ing item) {
        sql="DELETE FROM T_venta_ing WHERE (CODIGO_ING="+item.codigo_ing+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_venta_ing WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_venta_ing item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_venta_ing();

            item.codigo_ing=dt.getInt(0);
            item.id=dt.getInt(1);
            item.iding=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.puid=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsT_venta_ing item) {

        ins.init("T_venta_ing");

        ins.add("CODIGO_ING",item.codigo_ing);
        ins.add("ID",item.id);
        ins.add("IDING",item.iding);
        ins.add("NOMBRE",item.nombre);
        ins.add("PUID",item.puid);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_venta_ing item) {

        upd.init("T_venta_ing");

        upd.add("ID",item.id);
        upd.add("IDING",item.iding);
        upd.add("NOMBRE",item.nombre);
        upd.add("PUID",item.puid);

        upd.Where("(CODIGO_ING="+item.codigo_ing+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

