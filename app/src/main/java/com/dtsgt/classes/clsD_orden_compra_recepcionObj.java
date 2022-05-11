package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_compra_recepcionObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_compra_recepcion";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_compra_recepcion> items= new ArrayList<clsClasses.clsD_orden_compra_recepcion>();

    public clsD_orden_compra_recepcionObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_compra_recepcion item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_compra_recepcion item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_compra_recepcion item) {
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

    public clsClasses.clsD_orden_compra_recepcion first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_orden_compra_recepcion item) {

        ins.init("D_orden_compra_recepcion");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CORRELATIVO",item.correlativo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("FECHA_RECEPCION",item.fecha_recepcion);
        ins.add("CORREL_D_MOVD",item.correl_d_movd);
        ins.add("REFERENCIA",item.referencia);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("BANDERA",item.bandera);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_compra_recepcion item) {

        upd.init("D_orden_compra_recepcion");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("FECHA_RECEPCION",item.fecha_recepcion);
        upd.add("CORREL_D_MOVD",item.correl_d_movd);
        upd.add("REFERENCIA",item.referencia);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("BANDERA",item.bandera);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"') AND (CORRELATIVO="+item.correlativo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_orden_compra_recepcion item) {
        sql="DELETE FROM D_orden_compra_recepcion WHERE (CODIGO_COMPRA='"+item.codigo_compra+"') AND (CORRELATIVO="+item.correlativo+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_orden_compra_recepcion WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_compra_recepcion item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_compra_recepcion();

            item.codigo_compra=dt.getString(0);
            item.correlativo=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
            item.cantidad=dt.getDouble(3);
            item.fecha_recepcion=dt.getLong(4);
            item.correl_d_movd=dt.getString(5);
            item.referencia=dt.getString(6);
            item.codigo_almacen=dt.getInt(7);
            item.bandera=dt.getInt(8);

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

    public String addItemSql(clsClasses.clsD_orden_compra_recepcion item) {

        ins.init("D_orden_compra_recepcion");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CORRELATIVO",item.correlativo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("FECHA_RECEPCION",item.fecha_recepcion);
        ins.add("CORREL_D_MOVD",item.correl_d_movd);
        ins.add("REFERENCIA",item.referencia);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("BANDERA",item.bandera);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_compra_recepcion item) {

        upd.init("D_orden_compra_recepcion");

        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("FECHA_RECEPCION",item.fecha_recepcion);
        upd.add("CORREL_D_MOVD",item.correl_d_movd);
        upd.add("REFERENCIA",item.referencia);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("BANDERA",item.bandera);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"') AND (CORRELATIVO="+item.correlativo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

