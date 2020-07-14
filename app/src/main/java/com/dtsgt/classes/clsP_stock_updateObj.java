package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stock_updateObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stock_update";
    private String sql;
    public ArrayList<clsClasses.clsP_stock_update> items= new ArrayList<clsClasses.clsP_stock_update>();

    public clsP_stock_updateObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_stock_update item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_stock_update item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_stock_update item) {
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

    public clsClasses.clsP_stock_update first() {
        return items.get(0);
    }

    // Private

    private void addItem(clsClasses.clsP_stock_update item) {

        ins.init("P_stock_update");

        ins.add("CODIGO_STOCK",item.codigo_stock);
        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UNIDAD_MEDIDA",item.unidad_medida);
        ins.add("REFERENCIA",item.referencia);
        ins.add("FECHA_TRANSACCION",item.fecha_transaccion);
        ins.add("FECHA_PROCESADO",item.fecha_procesado);
        ins.add("PROCESADO",item.procesado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_stock_update item) {

        upd.init("P_stock_update");

        upd.add("EMPRESA",item.empresa);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("RUTA",item.ruta);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("UNIDAD_MEDIDA",item.unidad_medida);
        upd.add("REFERENCIA",item.referencia);
        upd.add("FECHA_TRANSACCION",item.fecha_transaccion);
        upd.add("FECHA_PROCESADO",item.fecha_procesado);
        upd.add("PROCESADO",item.procesado);

        upd.Where("(CODIGO_STOCK="+item.codigo_stock+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_stock_update item) {
        sql="DELETE FROM P_stock_update WHERE (CODIGO_STOCK="+item.codigo_stock+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_stock_update WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_stock_update item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_stock_update();

            item.codigo_stock=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.sucursal=dt.getInt(2);
            item.ruta=dt.getInt(3);
            item.codigo_producto=dt.getInt(4);
            item.cantidad=dt.getDouble(5);
            item.unidad_medida=dt.getString(6);
            item.referencia=dt.getString(7);
            item.fecha_transaccion=dt.getLong(8);
            item.fecha_procesado=dt.getLong(9);
            item.procesado=dt.getInt(10);

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

    public String addItemSql(clsClasses.clsP_stock_update item) {

        ins.init("P_stock_update");

        ins.add("CODIGO_STOCK",item.codigo_stock);
        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UNIDAD_MEDIDA",item.unidad_medida);
        ins.add("REFERENCIA",item.referencia);
        ins.add("FECHA_TRANSACCION",item.fecha_transaccion);
        ins.add("FECHA_PROCESADO",item.fecha_procesado);
        ins.add("PROCESADO",item.procesado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_stock_update item) {

        upd.init("P_stock_update");

        upd.add("EMPRESA",item.empresa);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("RUTA",item.ruta);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("UNIDAD_MEDIDA",item.unidad_medida);
        upd.add("REFERENCIA",item.referencia);
        upd.add("FECHA_TRANSACCION",item.fecha_transaccion);
        upd.add("FECHA_PROCESADO",item.fecha_procesado);
        upd.add("PROCESADO",item.procesado);

        upd.Where("(CODIGO_STOCK="+item.codigo_stock+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

