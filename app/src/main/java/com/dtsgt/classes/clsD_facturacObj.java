package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturacObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturac";
    private String sql;
    public ArrayList<clsClasses.clsD_facturac> items= new ArrayList<clsClasses.clsD_facturac>();

    public clsD_facturacObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturac item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturac item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturac item) {
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

    public clsClasses.clsD_facturac first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturac item) {

        ins.init("D_facturac");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturac item) {

        upd.init("D_facturac");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturac item) {
        sql="DELETE FROM D_facturac WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturac WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturac item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturac();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.codigo_menu=dt.getInt(2);
            item.idcombo=dt.getInt(3);
            item.unid=dt.getInt(4);
            item.cant=dt.getInt(5);
            item.idseleccion=dt.getInt(6);
            item.orden=dt.getInt(7);
            item.nombre=dt.getString(8);

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

    public String addItemSql(clsClasses.clsD_facturac item) {

        ins.init("D_facturac");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturac item) {

        upd.init("D_facturac");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

