package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodlistaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodlista";
    private String sql;
    public ArrayList<clsClasses.clsP_prodlista> items= new ArrayList<clsClasses.clsP_prodlista>();

    public clsP_prodlistaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodlista item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodlista item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodlista item) {
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

    public clsClasses.clsP_prodlista first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodlista item) {

        ins.init("P_prodlista");

        ins.add("CODIGO_PRODLISTA",item.codigo_prodlista);
        ins.add("CODIGO_LISTA",item.codigo_lista);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("CANT_MIN",item.cant_min);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodlista item) {

        upd.init("P_prodlista");

        upd.add("CODIGO_LISTA",item.codigo_lista);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("CANT_MIN",item.cant_min);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_PRODLISTA="+item.codigo_prodlista+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodlista item) {
        sql="DELETE FROM P_prodlista WHERE (CODIGO_PRODLISTA="+item.codigo_prodlista+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodlista WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodlista item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodlista();

            item.codigo_prodlista=dt.getInt(0);
            item.codigo_lista=dt.getInt(1);
            item.empresa=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.cant=dt.getDouble(4);
            item.cant_min=dt.getDouble(5);
            item.nombre=dt.getString(6);

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

    public String addItemSql(clsClasses.clsP_prodlista item) {

        ins.init("P_prodlista");

        ins.add("CODIGO_PRODLISTA",item.codigo_prodlista);
        ins.add("CODIGO_LISTA",item.codigo_lista);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("CANT_MIN",item.cant_min);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodlista item) {

        upd.init("P_prodlista");

        upd.add("CODIGO_LISTA",item.codigo_lista);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("CANT_MIN",item.cant_min);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_PRODLISTA="+item.codigo_prodlista+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

