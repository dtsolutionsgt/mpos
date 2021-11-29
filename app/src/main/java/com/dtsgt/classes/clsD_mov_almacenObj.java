package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_mov_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_mov_almacen";
    private String sql;
    public ArrayList<clsClasses.clsD_mov_almacen> items= new ArrayList<clsClasses.clsD_mov_almacen>();

    public clsD_mov_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_mov_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_mov_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_mov_almacen item) {
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

    public clsClasses.clsD_mov_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_mov_almacen item) {

        ins.init("D_mov_almacen");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ALMACEN_ORIGEN",item.almacen_origen);
        ins.add("ALMACEN_DESTINO",item.almacen_destino);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("USUARIO",item.usuario);
        ins.add("REFERENCIA",item.referencia);
        ins.add("STATCOM",item.statcom);
        ins.add("IMPRES",item.impres);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("TOTAL",item.total);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_mov_almacen item) {

        upd.init("D_mov_almacen");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ALMACEN_ORIGEN",item.almacen_origen);
        upd.add("ALMACEN_DESTINO",item.almacen_destino);
        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);
        upd.add("USUARIO",item.usuario);
        upd.add("REFERENCIA",item.referencia);
        upd.add("STATCOM",item.statcom);
        upd.add("IMPRES",item.impres);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("TOTAL",item.total);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_mov_almacen item) {
        sql="DELETE FROM D_mov_almacen WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_mov_almacen WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_mov_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_mov_almacen();

            item.corel=dt.getString(0);
            item.codigo_sucursal=dt.getInt(1);
            item.almacen_origen=dt.getInt(2);
            item.almacen_destino=dt.getInt(3);
            item.anulado=dt.getInt(4);
            item.fecha=dt.getLong(5);
            item.tipo=dt.getString(6);
            item.usuario=dt.getInt(7);
            item.referencia=dt.getString(8);
            item.statcom=dt.getString(9);
            item.impres=dt.getInt(10);
            item.codigoliquidacion=dt.getInt(11);
            item.codigo_proveedor=dt.getInt(12);
            item.total=dt.getDouble(13);

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

    public String addItemSql(clsClasses.clsD_mov_almacen item) {

        ins.init("D_mov_almacen");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ALMACEN_ORIGEN",item.almacen_origen);
        ins.add("ALMACEN_DESTINO",item.almacen_destino);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("USUARIO",item.usuario);
        ins.add("REFERENCIA",item.referencia);
        ins.add("STATCOM",item.statcom);
        ins.add("IMPRES",item.impres);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("TOTAL",item.total);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_mov_almacen item) {

        upd.init("D_mov_almacen");

        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ALMACEN_ORIGEN",item.almacen_origen);
        upd.add("ALMACEN_DESTINO",item.almacen_destino);
        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);
        upd.add("USUARIO",item.usuario);
        upd.add("REFERENCIA",item.referencia);
        upd.add("STATCOM",item.statcom);
        upd.add("IMPRES",item.impres);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("TOTAL",item.total);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

