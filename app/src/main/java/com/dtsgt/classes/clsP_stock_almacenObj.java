package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stock_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stock_almacen";
    private String sql;
    public ArrayList<clsClasses.clsP_stock_almacen> items= new ArrayList<clsClasses.clsP_stock_almacen>();

    public clsP_stock_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_stock_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_stock_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_stock_almacen item) {
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

    public clsClasses.clsP_stock_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_stock_almacen item) {

        ins.init("P_stock_almacen");

        ins.add("CODIGO_STOCK",item.codigo_stock);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("LOTE",item.lote);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("ANULADO",item.anulado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_stock_almacen item) {

        upd.init("P_stock_almacen");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("LOTE",item.lote);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PESOM",item.pesom);
        upd.add("ANULADO",item.anulado);

        upd.Where("(CODIGO_STOCK="+item.codigo_stock+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_stock_almacen item) {
        sql="DELETE FROM P_stock_almacen WHERE (CODIGO_STOCK="+item.codigo_stock+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_stock_almacen WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_stock_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_stock_almacen();

            item.codigo_stock=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_almacen=dt.getInt(3);
            item.codigo_producto=dt.getInt(4);
            item.unidadmedida=dt.getString(5);
            item.lote=dt.getString(6);
            item.cant=dt.getDouble(7);
            item.cantm=dt.getDouble(8);
            item.peso=dt.getDouble(9);
            item.pesom=dt.getDouble(10);
            item.anulado=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsP_stock_almacen item) {

        ins.init("P_stock_almacen");

        ins.add("CODIGO_STOCK",item.codigo_stock);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("LOTE",item.lote);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("ANULADO",item.anulado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_stock_almacen item) {

        upd.init("P_stock_almacen");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("UNIDADMEDIDA",item.unidadmedida);
        upd.add("LOTE",item.lote);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PESOM",item.pesom);
        upd.add("ANULADO",item.anulado);

        upd.Where("(CODIGO_STOCK="+item.codigo_stock+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    public String addItemSqlBOF(clsClasses.clsP_stock_almacen item) {

        ins.init("P_stock_almacen");

        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("LOTE",item.lote);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("ANULADO",item.anulado);

        return ins.sql();

    }

}

