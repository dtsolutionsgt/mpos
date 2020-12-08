package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_linea_impresoraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_linea_impresora";
    private String sql;
    public ArrayList<clsClasses.clsP_linea_impresora> items= new ArrayList<clsClasses.clsP_linea_impresora>();

    public clsP_linea_impresoraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_linea_impresora item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_linea_impresora item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_linea_impresora item) {
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

    public clsClasses.clsP_linea_impresora first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_linea_impresora item) {

        ins.init("P_linea_impresora");

        ins.add("CODIGO_LINEA_IMPRESORA",item.codigo_linea_impresora);
        ins.add("CODIGO_LINEA",item.codigo_linea);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_linea_impresora item) {

        upd.init("P_linea_impresora");

        upd.add("CODIGO_LINEA",item.codigo_linea);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_IMPRESORA",item.codigo_impresora);

        upd.Where("(CODIGO_LINEA_IMPRESORA="+item.codigo_linea_impresora+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_linea_impresora item) {
        sql="DELETE FROM P_linea_impresora WHERE (CODIGO_LINEA_IMPRESORA="+item.codigo_linea_impresora+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_linea_impresora WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_linea_impresora item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_linea_impresora();

            item.codigo_linea_impresora=dt.getInt(0);
            item.codigo_linea=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.empresa=dt.getInt(3);
            item.codigo_impresora=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsP_linea_impresora item) {

        ins.init("P_linea_impresora");

        ins.add("CODIGO_LINEA_IMPRESORA",item.codigo_linea_impresora);
        ins.add("CODIGO_LINEA",item.codigo_linea);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_linea_impresora item) {

        upd.init("P_linea_impresora");

        upd.add("CODIGO_LINEA",item.codigo_linea);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_IMPRESORA",item.codigo_impresora);

        upd.Where("(CODIGO_LINEA_IMPRESORA="+item.codigo_linea_impresora+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

