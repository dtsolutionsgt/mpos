package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_compraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_compra";
    private String sql;
    public ArrayList<clsClasses.clsD_compra> items= new ArrayList<clsClasses.clsD_compra>();

    public clsD_compraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_compra item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_compra item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_compra item) {
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

    public clsClasses.clsD_compra first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_compra item) {

        ins.init("D_compra");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("REFERENCIA",item.referencia);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_compra item) {

        upd.init("D_compra");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("REFERENCIA",item.referencia);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_compra item) {
        sql="DELETE FROM D_compra WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_compra WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_compra item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_compra();

            item.corel=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.codigo_ruta=dt.getInt(2);
            item.anulado=dt.getInt(3);
            item.fecha=dt.getLong(4);
            item.codigo_proveedor=dt.getInt(5);
            item.codigo_usuario=dt.getInt(6);
            item.referencia=dt.getString(7);
            item.statcom=dt.getInt(8);

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

    public String addItemSql(clsClasses.clsD_compra item) {

        ins.init("D_compra");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("REFERENCIA",item.referencia);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_compra item) {

        upd.init("D_compra");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("REFERENCIA",item.referencia);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

