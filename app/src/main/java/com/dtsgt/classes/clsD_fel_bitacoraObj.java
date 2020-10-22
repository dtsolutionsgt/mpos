package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_fel_bitacoraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_fel_bitacora";
    private String sql;
    public ArrayList<clsClasses.clsD_fel_bitacora> items= new ArrayList<clsClasses.clsD_fel_bitacora>();

    public clsD_fel_bitacoraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_fel_bitacora item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_fel_bitacora item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_fel_bitacora item) {
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

    public clsClasses.clsD_fel_bitacora first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_fel_bitacora item) {

        ins.init("D_fel_bitacora");

        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL",item.corel);
        ins.add("FECHA",item.fecha);
        ins.add("TIEMPO_FIRMA",item.tiempo_firma);
        ins.add("TIEMPO_CERT",item.tiempo_cert);
        ins.add("ESTADO",item.estado);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_fel_bitacora item) {

        upd.init("D_fel_bitacora");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("FECHA",item.fecha);
        upd.add("TIEMPO_FIRMA",item.tiempo_firma);
        upd.add("TIEMPO_CERT",item.tiempo_cert);
        upd.add("ESTADO",item.estado);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_fel_bitacora item) {
        sql="DELETE FROM D_fel_bitacora WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_fel_bitacora WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_fel_bitacora item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_fel_bitacora();

            item.empresa=dt.getInt(0);
            item.codigo_sucursal=dt.getInt(1);
            item.codigo_ruta=dt.getInt(2);
            item.corel=dt.getString(3);
            item.fecha=dt.getLong(4);
            item.tiempo_firma=dt.getDouble(5);
            item.tiempo_cert=dt.getDouble(6);
            item.estado=dt.getInt(7);
            item.codigo_vendedor=dt.getInt(8);
            item.statcom=dt.getInt(9);

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

    public String addItemSql(clsClasses.clsD_fel_bitacora item) {

        ins.init("D_fel_bitacora");

        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL",item.corel);
        ins.add("FECHA",item.fecha);
        ins.add("TIEMPO_FIRMA",item.tiempo_firma);
        ins.add("TIEMPO_CERT",item.tiempo_cert);
        ins.add("ESTADO",item.estado);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        //ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_fel_bitacora item) {

        upd.init("D_fel_bitacora");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("FECHA",item.fecha);
        upd.add("TIEMPO_FIRMA",item.tiempo_firma);
        upd.add("TIEMPO_CERT",item.tiempo_cert);
        upd.add("ESTADO",item.estado);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("STATCOM",item.statcom);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

