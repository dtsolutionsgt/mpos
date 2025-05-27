package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_repartidorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_repartidor";
    private String sql;
    public ArrayList<clsClasses.clsP_repartidor> items= new ArrayList<clsClasses.clsP_repartidor>();

    public clsP_repartidorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_repartidor item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_repartidor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_repartidor item) {
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

    public clsClasses.clsP_repartidor first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsP_repartidor item) {

        ins.init("P_repartidor");

        ins.add("CODIGO",item.codigo);
        ins.add("ACTIVO",item.activo);
        ins.add("NOMBRE",item.nombre);
        ins.add("PLACA",item.placa);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_repartidor item) {

        upd.init("P_repartidor");

        upd.add("ACTIVO",item.activo);
        upd.add("NOMBRE",item.nombre);
        upd.add("PLACA",item.placa);
        upd.add("CODIGO_EMPRESA",item.codigo_empresa);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsP_repartidor item) {
        sql="DELETE FROM P_repartidor WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_repartidor WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_repartidor item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_repartidor();

            item.codigo=dt.getInt(0);
            item.activo=dt.getInt(1);
            item.nombre=dt.getString(2);
            item.placa=dt.getString(3);
            item.codigo_empresa=dt.getInt(4);

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

    public String addItemSql(clsClasses.clsP_repartidor item) {

        ins.init("P_repartidor");

        ins.add("CODIGO",item.codigo);
        ins.add("ACTIVO",item.activo);
        ins.add("NOMBRE",item.nombre);
        ins.add("PLACA",item.placa);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_repartidor item) {

        upd.init("P_repartidor");

        upd.add("ACTIVO",item.activo);
        upd.add("NOMBRE",item.nombre);
        upd.add("PLACA",item.placa);
        upd.add("CODIGO_EMPRESA",item.codigo_empresa);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();


    }

    //endregion
}

