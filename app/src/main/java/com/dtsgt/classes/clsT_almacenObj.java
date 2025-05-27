package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_almacen";
    private String sql;
    public ArrayList<clsClasses.clsT_almacen> items= new ArrayList<clsClasses.clsT_almacen>();

    public clsT_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_almacen item) {
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

    public clsClasses.clsT_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_almacen item) {

        ins.init("T_almacen");

        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ACTIVO",item.activo);
        ins.add("NOMBRE",item.nombre);
        ins.add("ES_PRINCIPAL",item.es_principal);
        ins.add("ES_DE_TRANSITO",item.es_de_transito);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_almacen item) {

        upd.init("T_almacen");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ACTIVO",item.activo);
        upd.add("NOMBRE",item.nombre);
        upd.add("ES_PRINCIPAL",item.es_principal);
        upd.add("ES_DE_TRANSITO",item.es_de_transito);

        upd.Where("(CODIGO_ALMACEN="+item.codigo_almacen+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_almacen item) {
        sql="DELETE FROM T_almacen WHERE (CODIGO_ALMACEN="+item.codigo_almacen+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_almacen WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_almacen();

            item.codigo_almacen=dt.getInt(0);
            item.codigo_sucursal=dt.getInt(1);
            item.activo=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.es_principal=dt.getInt(4);
            item.es_de_transito=dt.getInt(5);

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

    public String addItemSql(clsClasses.clsT_almacen item) {

        ins.init("T_almacen");

        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ACTIVO",item.activo);
        ins.add("NOMBRE",item.nombre);
        ins.add("ES_PRINCIPAL",item.es_principal);
        ins.add("ES_DE_TRANSITO",item.es_de_transito);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_almacen item) {

        upd.init("T_almacen");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ACTIVO",item.activo);
        upd.add("NOMBRE",item.nombre);
        upd.add("ES_PRINCIPAL",item.es_principal);
        upd.add("ES_DE_TRANSITO",item.es_de_transito);

        upd.Where("(CODIGO_ALMACEN="+item.codigo_almacen+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

