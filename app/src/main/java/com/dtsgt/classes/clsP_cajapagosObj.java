package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_cajapagosObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cajapagos";
    private String sql;
    public ArrayList<clsClasses.clsP_cajapagos> items= new ArrayList<clsClasses.clsP_cajapagos>();

    public clsP_cajapagosObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cajapagos item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cajapagos item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cajapagos item) {
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

    public clsClasses.clsP_cajapagos first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cajapagos item) {

        ins.init("P_cajapagos");

        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("PROVEEDOR",item.proveedor);
        ins.add("MONTO",item.monto);
        ins.add("NODOCUMENTO",item.nodocumento);
        ins.add("REFERENCIA",item.referencia);
        ins.add("OBSERVACION",item.observacion);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("STATCOM",item.statcom);
        ins.add("CODIGO_CAJAPAGOS",item.codigo_cajapagos);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cajapagos item) {

        upd.init("P_cajapagos");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);
        upd.add("PROVEEDOR",item.proveedor);
        upd.add("MONTO",item.monto);
        upd.add("NODOCUMENTO",item.nodocumento);
        upd.add("REFERENCIA",item.referencia);
        upd.add("OBSERVACION",item.observacion);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("STATCOM",item.statcom);

        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //upd.Where("(EMPRESA='"+item.empresa+"') AND (SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (ITEM="+item.item+")");
         upd.Where("(CODIGO_CAJAPAGOS'"+item.codigo_cajapagos+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cajapagos item) {

        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //sql="DELETE FROM P_cajapagos WHERE (EMPRESA='"+item.empresa+"') AND (SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (ITEM="+item.item+")";

        sql="DELETE FROM P_cajapagos " +
                "WHERE (CODIGO_CAJAPAGOS'"+item.codigo_cajapagos+"')";
        db.execSQL(sql);

    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_cajapagos WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cajapagos item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cajapagos();

            item.empresa=dt.getInt(0);
            item.sucursal=dt.getInt(1);
            item.ruta=dt.getInt(2);
            item.corel=dt.getInt(3);
            item.item=dt.getInt(4);
            item.anulado=dt.getInt(5);
            item.fecha=dt.getLong(6);
            item.tipo=dt.getInt(7);
            item.proveedor=dt.getInt(8);
            item.monto=dt.getDouble(9);
            item.nodocumento=dt.getString(10);
            item.referencia=dt.getString(11);
            item.observacion=dt.getString(12);
            item.vendedor=dt.getInt(13);
            item.statcom=dt.getString(14);
            item.codigo_cajapagos=dt.getString(15);

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

    public String addItemSql(clsClasses.clsP_cajapagos item) {

        ins.init("P_cajapagos");

        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("RUTA",item.ruta);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("TIPO",item.tipo);
        ins.add("PROVEEDOR",item.proveedor);
        ins.add("MONTO",item.monto);
        ins.add("NODOCUMENTO",item.nodocumento);
        ins.add("REFERENCIA",item.referencia);
        ins.add("OBSERVACION",item.observacion);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("STATCOM",item.statcom);
        ins.add("CODIGO_CAJAPAGOS",item.codigo_cajapagos);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cajapagos item) {

        upd.init("P_cajapagos");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("TIPO",item.tipo);
        upd.add("PROVEEDOR",item.proveedor);
        upd.add("MONTO",item.monto);
        upd.add("NODOCUMENTO",item.nodocumento);
        upd.add("REFERENCIA",item.referencia);
        upd.add("OBSERVACION",item.observacion);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("STATCOM",item.statcom);

        //#CKFK 20200516 se cambio la llave compuesta por una llave única
        //upd.Where("(EMPRESA='"+item.empresa+"') AND (SUCURSAL='"+item.sucursal+"') AND (RUTA='"+item.ruta+"') AND (COREL="+item.corel+") AND (ITEM="+item.item+")");
        upd.Where("(CODIGO_CAJAPAGOS'"+item.codigo_cajapagos+"')");

        return upd.sql();

    }

}

