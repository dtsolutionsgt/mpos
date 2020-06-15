package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_movdObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_movd";
    private String sql;
    public ArrayList<clsClasses.clsT_movd> items= new ArrayList<clsClasses.clsT_movd>();

    public clsT_movdObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_movd item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_movd item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_movd item) {
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

    public clsClasses.clsT_movd first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_movd item) {

        ins.init("T_movd");

        ins.add("CORELDET",item.coreldet);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("LOTE",item.lote);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("PRECIO",item.precio);
        ins.add("RAZON",item.razon);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_movd item) {

        upd.init("T_movd");

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
        upd.add("RAZON",item.razon);

        upd.Where("(CORELDET="+item.coreldet+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_movd item) {
        sql="DELETE FROM T_movd WHERE (CORELDET="+item.coreldet+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_movd WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_movd item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_movd();

            item.coreldet=dt.getInt(0);
            item.corel=dt.getString(1);
            item.producto=dt.getInt(2);
            item.cant=dt.getDouble(3);
            item.cantm=dt.getDouble(4);
            item.peso=dt.getDouble(5);
            item.pesom=dt.getDouble(6);
            item.lote=dt.getString(7);
            item.codigoliquidacion=dt.getInt(8);
            item.unidadmedida=dt.getString(9);
            item.precio=dt.getDouble(10);
            item.razon=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsT_movd item) {

        ins.init("T_movd");

        ins.add("CORELDET",item.coreldet);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PESOM",item.pesom);
        ins.add("LOTE",item.lote);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("UNIDADMEDIDA",item.unidadmedida);
        ins.add("PRECIO",item.precio);
        ins.add("RAZON",item.razon);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_movd item) {

        upd.init("T_movd");

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
        upd.add("MOTIVO_AJUSTE",item.razon);

        upd.Where("(CORELDET="+item.coreldet+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

