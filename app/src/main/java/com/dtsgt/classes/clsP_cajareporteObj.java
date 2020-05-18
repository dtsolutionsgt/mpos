package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.INotificationSideChannel;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsP_cajareporteObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cajareporte";
    private String sql;
    public ArrayList<clsClasses.clsP_cajareporte> items= new ArrayList<clsClasses.clsP_cajareporte>();

    public clsP_cajareporteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cajareporte item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cajareporte item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cajareporte item) {
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

    public clsClasses.clsP_cajareporte first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cajareporte item) {

        ins.init("P_cajareporte");

        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("LINEA",item.linea);
        ins.add("TEXTO",item.texto);
        ins.add("STATCOM",item.statcom);
        ins.add("CODIGO_CAJAREPORTE",item.codigo_cajareporte);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cajareporte item) {

        upd.init("P_cajareporte");

        upd.add("TEXTO",item.texto);
        upd.add("STATCOM",item.statcom);

        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //upd.Where("(SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (LINEA="+item.linea+")");
        upd.Where("(CODIGO_CAJAREPORTE'"+item.codigo_cajareporte+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cajareporte item) {
        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //sql="DELETE FROM P_cajareporte " +
        //    "WHERE (SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (LINEA="+item.linea+")";

        sql="DELETE FROM P_cajareporte " +
            "WHERE (CODIGO_CAJAREPORTE'"+item.codigo_cajareporte+"')";

        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_cajareporte WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cajareporte item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cajareporte();

            item.empresa=dt.getInt(0);
            item.sucursal=dt.getInt(1);
            item.ruta=dt.getInt(2);
            item.corel=dt.getInt(3);
            item.linea=dt.getInt(4);
            item.texto=dt.getString(5);
            item.statcom=dt.getString(6);
            item.codigo_cajareporte=dt.getString(7);

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

    public String addItemSql(clsClasses.clsP_cajareporte item) {

        ins.init("P_cajareporte");

        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("LINEA",item.linea);
        ins.add("TEXTO",item.texto);
        ins.add("STATCOM",item.statcom);
        ins.add("CODIGO_CAJAREPORTE",item.codigo_cajareporte);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cajareporte item) {

        upd.init("P_cajareporte");

        upd.add("TEXTO",item.texto);
        upd.add("STATCOM",item.statcom);

        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //upd.Where("(SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (LINEA="+item.linea+")");
        upd.Where("(CODIGO_CAJAREPORTE'"+item.codigo_cajareporte+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

