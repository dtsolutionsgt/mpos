package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_pedidodObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_pedidod";
    private String sql;
    public ArrayList<clsClasses.clsT_pedidod> items= new ArrayList<clsClasses.clsT_pedidod>();

    public clsT_pedidodObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_pedidod item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_pedidod item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_pedidod item) {
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

    public clsClasses.clsT_pedidod first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_pedidod item) {

        ins.init("T_pedidod");

        ins.add("COREL",item.corel);
        ins.add("COREL_DET",item.corel_det);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UMVENTA",item.umventa);
        ins.add("CANT",item.cant);
        ins.add("TOTAL",item.total);
        ins.add("NOTA",item.nota);
        ins.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_pedidod item) {

        upd.init("T_pedidod");

        upd.add("COREL",item.corel);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("UMVENTA",item.umventa);
        upd.add("CANT",item.cant);
        upd.add("TOTAL",item.total);
        upd.add("NOTA",item.nota);
        upd.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);

        upd.Where("(COREL_DET="+item.corel_det+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_pedidod item) {
        sql="DELETE FROM T_pedidod WHERE (COREL_DET="+item.corel_det+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_pedidod WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_pedidod item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_pedidod();

            item.corel=dt.getString(0);
            item.corel_det=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
            item.umventa=dt.getString(3);
            item.cant=dt.getDouble(4);
            item.total=dt.getDouble(5);
            item.nota=dt.getString(6);
            item.codigo_tipo_producto=dt.getString(7);

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

    public String addItemSql(clsClasses.clsT_pedidod item) {

        ins.init("T_pedidod");

        ins.add("COREL",item.corel);
        ins.add("COREL_DET",item.corel_det);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("UMVENTA",item.umventa);
        ins.add("CANT",item.cant);
        ins.add("TOTAL",item.total);
        ins.add("NOTA",item.nota);
        ins.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_pedidod item) {

        upd.init("T_pedidod");

        upd.add("COREL",item.corel);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("UMVENTA",item.umventa);
        upd.add("CANT",item.cant);
        upd.add("TOTAL",item.total);
        upd.add("NOTA",item.nota);
        upd.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);

        upd.Where("(COREL_DET="+item.corel_det+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

