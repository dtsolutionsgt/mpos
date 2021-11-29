package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_movd_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_movd_almacen";
    private String sql;
    public ArrayList<clsClasses.clsD_movd_almacen> items= new ArrayList<clsClasses.clsD_movd_almacen>();

    public clsD_movd_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_movd_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_movd_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_movd_almacen item) {
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

    public clsClasses.clsD_movd_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_movd_almacen item) {

        ins.init("D_movd_almacen");

        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("LOTE",item.lote);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("CORELDET",item.coreldet);
        ins.add("PRECIO",item.precio);
        ins.add("MOTIVO_AJUSTE",item.motivo_ajuste);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_movd_almacen item) {

        upd.init("D_movd_almacen");

        upd.add("COREL",item.corel);
        upd.add("PRODUCTO",item.producto);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PESOM",item.pesom);
        upd.add("LOTE",item.lote);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("PRECIO",item.precio);
        upd.add("MOTIVO_AJUSTE",item.motivo_ajuste);

        upd.Where("(CORELDET="+item.coreldet+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_movd_almacen item) {
        sql="DELETE FROM D_movd_almacen WHERE (CORELDET="+item.coreldet+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_movd_almacen WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_movd_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_movd_almacen();

            item.corel=dt.getString(0);
            item.producto=dt.getInt(1);
            item.cant=dt.getDouble(2);
            item.cantm=dt.getDouble(3);
            item.peso=dt.getDouble(4);
            item.pesom=dt.getDouble(5);
            item.lote=dt.getString(6);
            item.codigoliquidacion=dt.getInt(7);
            item.unidadmedida=dt.getString(8);
            item.coreldet=dt.getInt(9);
            item.precio=dt.getDouble(10);
            item.motivo_ajuste=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsD_movd_almacen item) {

        ins.init("D_movd_almacen");

        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("LOTE",item.lote);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("CORELDET",item.coreldet);
        ins.add("PRECIO",item.precio);
        ins.add("MOTIVO_AJUSTE",item.motivo_ajuste);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_movd_almacen item) {

        upd.init("D_movd_almacen");

        upd.add("COREL",item.corel);
        upd.add("PRODUCTO",item.producto);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PESOM",item.pesom);
        upd.add("LOTE",item.lote);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("PRECIO",item.precio);
        upd.add("MOTIVO_AJUSTE",item.motivo_ajuste);

        upd.Where("(CORELDET="+item.coreldet+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

