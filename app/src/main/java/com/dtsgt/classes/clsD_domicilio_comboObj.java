package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_domicilio_comboObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_domicilio_combo";
    private String sql;
    public ArrayList<clsClasses.clsD_domicilio_combo> items= new ArrayList<clsClasses.clsD_domicilio_combo>();

    public clsD_domicilio_comboObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_domicilio_combo item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_domicilio_combo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_domicilio_combo item) {
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

    public clsClasses.clsD_domicilio_combo first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_domicilio_combo item) {

        ins.init("D_domicilio_combo");

        ins.add("CODIGO",item.codigo);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_DETALLE",item.codigo_detalle);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_domicilio_combo item) {

        upd.init("D_domicilio_combo");

        upd.add("COREL",item.corel);
        upd.add("CODIGO_DETALLE",item.codigo_detalle);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_domicilio_combo item) {
        sql="DELETE FROM D_domicilio_combo WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_domicilio_combo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_domicilio_combo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_domicilio_combo();

            item.codigo=dt.getInt(0);
            item.corel=dt.getString(1);
            item.codigo_detalle=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.cant=dt.getDouble(4);

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

    public String addItemSql(clsClasses.clsD_domicilio_combo item) {

        ins.init("D_domicilio_combo");

        ins.add("CODIGO",item.codigo);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_DETALLE",item.codigo_detalle);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_domicilio_combo item) {

        upd.init("D_domicilio_combo");

        upd.add("COREL",item.corel);
        upd.add("CODIGO_DETALLE",item.codigo_detalle);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();


    }

    //endregion
}

