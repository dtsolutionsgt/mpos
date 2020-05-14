package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodmenuopcObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodmenuopc";
    private String sql;
    public ArrayList<clsClasses.clsP_prodmenuopc> items= new ArrayList<clsClasses.clsP_prodmenuopc>();

    public clsP_prodmenuopcObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodmenuopc item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodmenuopc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodmenuopc item) {
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

    public clsClasses.clsP_prodmenuopc first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodmenuopc item) {

        ins.init("P_prodmenuopc");

        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_OPCION",item.codigo_opcion);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CODIGO_RECETA",item.codigo_receta);


        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodmenuopc item) {

        upd.init("P_prodmenuopc");

        upd.add("CODIGO_OPCION",item.codigo_opcion);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CODIGO_RECETA",item.codigo_receta);

        upd.Where("(CODIGO_MENU_OPCION="+item.codigo_menu_opcion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodmenuopc item) {
        sql="DELETE FROM P_prodmenuopc WHERE (CODIGO_MENU_OPCION="+item.codigo_menu_opcion+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodmenuopc WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodmenuopc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodmenuopc();

            item.codigo_menu_opcion=dt.getInt(0);
            item.codigo_opcion=dt.getInt(1);
            item.empresa=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.codigo_receta=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsP_prodmenuopc item) {

        ins.init("P_prodmenuopc");

        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_OPCION",item.codigo_opcion);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CODIGO_RECETA",item.codigo_receta);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodmenuopc item) {

        upd.init("P_prodmenuopc");

        upd.add("CODIGO_OPCION",item.codigo_opcion);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CODIGO_RECETA",item.codigo_receta);

        upd.Where("(CODIGO_MENU_OPCION="+item.codigo_menu_opcion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

