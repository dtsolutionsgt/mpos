package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_cortesiaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cortesia";
    private String sql;
    public ArrayList<clsClasses.clsP_cortesia> items= new ArrayList<clsClasses.clsP_cortesia>();

    public clsP_cortesiaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cortesia item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cortesia item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cortesia item) {
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

    public clsClasses.clsP_cortesia first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cortesia item) {

        ins.init("P_cortesia");

        ins.add("CODIGO_CORTESIA",item.codigo_cortesia);
        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("ACTIVO",item.activo);
        ins.add("CLAVE",item.clave);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cortesia item) {

        upd.init("P_cortesia");

        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("ACTIVO",item.activo);
        upd.add("CLAVE",item.clave);

        upd.Where("(CODIGO_CORTESIA="+item.codigo_cortesia+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cortesia item) {
        sql="DELETE FROM P_cortesia WHERE (CODIGO_CORTESIA="+item.codigo_cortesia+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_cortesia WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cortesia item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cortesia();

            item.codigo_cortesia=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.nombre=dt.getString(2);
            item.codigo_vendedor=dt.getInt(3);
            item.activo=dt.getInt(4);
            item.clave=dt.getString(5);

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

    public String addItemSql(clsClasses.clsP_cortesia item) {

        ins.init("P_cortesia");

        ins.add("CODIGO_CORTESIA",item.codigo_cortesia);
        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("ACTIVO",item.activo);
        ins.add("CLAVE",item.clave);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cortesia item) {

        upd.init("P_cortesia");

        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("ACTIVO",item.activo);
        upd.add("CLAVE",item.clave);

        upd.Where("(CODIGO_CORTESIA="+item.codigo_cortesia+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

