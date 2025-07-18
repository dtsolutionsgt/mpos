package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_notaenviodObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_notaenviod";
    private String sql;
    public ArrayList<clsClasses.clsD_notaenviod> items= new ArrayList<clsClasses.clsD_notaenviod>();

    public clsD_notaenviodObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_notaenviod item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_notaenviod item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_notaenviod item) {
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

    public clsClasses.clsD_notaenviod first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_notaenviod item) {

        ins.init("D_notaenviod");

        ins.add("CODIGO_NOTA_ENVIO_DET",item.codigo_nota_envio_det);
        ins.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("PRECIO_VENTA",item.precio_venta);
        ins.add("TOTAL",item.total);
        ins.add("DESCUENTO",item.descuento);
        ins.add("DESCUENTO_PORCENTAJE",item.descuento_porcentaje);
        ins.add("NOMBRE_PRODUCTO",item.nombre_producto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_notaenviod item) {

        upd.init("D_notaenviod");

        upd.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("PRECIO_VENTA",item.precio_venta);
        upd.add("TOTAL",item.total);
        upd.add("DESCUENTO",item.descuento);
        upd.add("DESCUENTO_PORCENTAJE",item.descuento_porcentaje);
        upd.add("NOMBRE_PRODUCTO",item.nombre_producto);

        upd.Where("(CODIGO_NOTA_ENVIO_DET="+item.codigo_nota_envio_det+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_notaenviod item) {
        sql="DELETE FROM D_notaenviod WHERE (CODIGO_NOTA_ENVIO_DET="+item.codigo_nota_envio_det+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_notaenviod WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_notaenviod item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_notaenviod();

            item.codigo_nota_envio_det=dt.getInt(0);
            item.codigo_nota_envio_enc=dt.getLong(1);
            item.codigo_producto=dt.getInt(2);
            item.cantidad=dt.getDouble(3);
            item.precio_venta=dt.getDouble(4);
            item.total=dt.getDouble(5);
            item.descuento=dt.getDouble(6);
            item.descuento_porcentaje=dt.getDouble(7);
            item.nombre_producto=dt.getString(8);

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

    public String addItemSql(clsClasses.clsD_notaenviod item) {

        ins.init("D_notaenviod");

        ins.add("CODIGO_NOTA_ENVIO_DET",item.codigo_nota_envio_det);
        ins.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("PRECIO_VENTA",item.precio_venta);
        ins.add("TOTAL",item.total);
        ins.add("DESCUENTO",item.descuento);
        ins.add("DESCUENTO_PORCENTAJE",item.descuento_porcentaje);
        ins.add("NOMBRE_PRODUCTO",item.nombre_producto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_notaenviod item) {

        upd.init("D_notaenviod");

        upd.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("PRECIO_VENTA",item.precio_venta);
        upd.add("TOTAL",item.total);
        upd.add("DESCUENTO",item.descuento);
        upd.add("DESCUENTO_PORCENTAJE",item.descuento_porcentaje);
        upd.add("NOMBRE_PRODUCTO",item.nombre_producto);

        upd.Where("(CODIGO_NOTA_ENVIO_DET="+item.codigo_nota_envio_det+")");

        return upd.sql();


    }

    //endregion
}


