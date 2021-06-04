package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturaprObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturapr";
    private String sql;
    public ArrayList<clsClasses.clsD_facturapr> items= new ArrayList<clsClasses.clsD_facturapr>();

    public clsD_facturaprObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturapr item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturapr item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturapr item) {
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

    public clsClasses.clsD_facturapr first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturapr item) {

        ins.init("D_facturapr");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("PROPINA",item.propina);
        ins.add("PROPPERC",item.propperc);
        ins.add("PROPEXTRA",item.propextra);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturapr item) {

        upd.init("D_facturapr");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("PROPINA",item.propina);
        upd.add("PROPPERC",item.propperc);
        upd.add("PROPEXTRA",item.propextra);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturapr item) {
        sql="DELETE FROM D_facturapr WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturapr WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturapr item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturapr();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.anulado=dt.getInt(2);
            item.fecha=dt.getInt(3);
            item.codigo_sucursal=dt.getInt(4);
            item.codigo_vendedor=dt.getInt(5);
            item.propina=dt.getDouble(6);
            item.propperc=dt.getDouble(7);
            item.propextra=dt.getDouble(8);

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

    public String addItemSql(clsClasses.clsD_facturapr item) {

        ins.init("D_facturapr");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("PROPINA",item.propina);
        ins.add("PROPPERC",item.propperc);
        ins.add("PROPEXTRA",item.propextra);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturapr item) {

        upd.init("D_facturapr");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("PROPINA",item.propina);
        upd.add("PROPPERC",item.propperc);
        upd.add("PROPEXTRA",item.propextra);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}



