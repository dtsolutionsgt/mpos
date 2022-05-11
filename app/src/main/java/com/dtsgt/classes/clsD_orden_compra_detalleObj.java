package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_compra_detalleObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_compra_detalle";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_compra_detalle> items= new ArrayList<clsClasses.clsD_orden_compra_detalle>();

    public clsD_orden_compra_detalleObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_compra_detalle item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_compra_detalle item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_compra_detalle item) {
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

    public clsClasses.clsD_orden_compra_detalle first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_orden_compra_detalle item) {

        ins.init("D_orden_compra_detalle");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UNIDAD_MEDIDA",item.unidad_medida);
        ins.add("COSTO",item.costo);
        ins.add("TOTAL",item.total);
        ins.add("ANULADO",item.anulado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_compra_detalle item) {

        upd.init("D_orden_compra_detalle");

        upd.add("CANTIDAD",item.cantidad);
        upd.add("UNIDAD_MEDIDA",item.unidad_medida);
        upd.add("COSTO",item.costo);
        upd.add("TOTAL",item.total);
        upd.add("ANULADO",item.anulado);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"') AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_orden_compra_detalle item) {
        sql="DELETE FROM D_orden_compra_detalle WHERE (CODIGO_COMPRA='"+item.codigo_compra+"') AND (CODIGO_PRODUCTO="+item.codigo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_orden_compra_detalle WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_compra_detalle item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_compra_detalle();

            item.codigo_compra=dt.getString(0);
            item.codigo_producto=dt.getInt(1);
            item.cantidad=dt.getDouble(2);
            item.unidad_medida=dt.getString(3);
            item.costo=dt.getDouble(4);
            item.total=dt.getDouble(5);
            item.anulado=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsD_orden_compra_detalle item) {

        ins.init("D_orden_compra_detalle");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UNIDAD_MEDIDA",item.unidad_medida);
        ins.add("COSTO",item.costo);
        ins.add("TOTAL",item.total);
        ins.add("ANULADO",item.anulado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_compra_detalle item) {

        upd.init("D_orden_compra_detalle");

        upd.add("CANTIDAD",item.cantidad);
        upd.add("UNIDAD_MEDIDA",item.unidad_medida);
        upd.add("COSTO",item.costo);
        upd.add("TOTAL",item.total);
        upd.add("ANULADO",item.anulado);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"') AND (CODIGO_PRODUCTO="+item.codigo_producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

