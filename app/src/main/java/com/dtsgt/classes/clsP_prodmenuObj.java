package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodmenuObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodmenu";
    private String sql;
    public ArrayList<clsClasses.clsP_prodmenu> items= new ArrayList<clsClasses.clsP_prodmenu>();

    public clsP_prodmenuObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodmenu item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodmenu item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodmenu item) {
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

    public clsClasses.clsP_prodmenu first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodmenu item) {

        ins.init("P_prodmenu");

        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("OPCION_LISTA",item.opcion_lista);
        ins.add("OPCION_PRODUCTO",item.opcion_producto);
        ins.add("ORDEN",item.orden);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodmenu item) {

        upd.init("P_prodmenu");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("OPCION_LISTA",item.opcion_lista);
        upd.add("OPCION_PRODUCTO",item.opcion_producto);
        upd.add("ORDEN",item.orden);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_MENU="+item.codigo_menu+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodmenu item) {
        sql="DELETE FROM P_prodmenu WHERE (CODIGO_MENU="+item.codigo_menu+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodmenu WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodmenu item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodmenu();

            item.codigo_menu=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
            item.opcion_lista=dt.getInt(3);
            item.opcion_producto=dt.getInt(4);
            item.orden=dt.getInt(5);
            item.nombre=dt.getString(6);
            item.nota=dt.getString(7);

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

    public String addItemSql(clsClasses.clsP_prodmenu item) {

        ins.init("P_prodmenu");

        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("OPCION_LISTA",item.opcion_lista);
        ins.add("OPCION_PRODUCTO",item.opcion_producto);
        ins.add("ORDEN",item.orden);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodmenu item) {

        upd.init("P_prodmenu");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("OPCION_LISTA",item.opcion_lista);
        upd.add("OPCION_PRODUCTO",item.opcion_producto);
        upd.add("ORDEN",item.orden);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_MENU="+item.codigo_menu+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

