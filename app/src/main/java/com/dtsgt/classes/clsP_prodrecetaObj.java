package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodrecetaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodreceta";
    private String sql;
    public ArrayList<clsClasses.clsP_prodreceta> items= new ArrayList<clsClasses.clsP_prodreceta>();

    public clsP_prodrecetaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodreceta item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodreceta item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodreceta item) {
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

    public clsClasses.clsP_prodreceta first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodreceta item) {

        ins.init("P_prodreceta");

        ins.add("CODIGO_RECETA",item.codigo_receta);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CODIGO_ARTICULO",item.codigo_articulo);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodreceta item) {

        upd.init("P_prodreceta");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CODIGO_ARTICULO",item.codigo_articulo);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(CODIGO_RECETA="+item.codigo_receta+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodreceta item) {
        sql="DELETE FROM P_prodreceta WHERE (CODIGO_RECETA="+item.codigo_receta+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodreceta WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodreceta item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodreceta();

            item.codigo_receta=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
            item.codigo_articulo=dt.getInt(3);
            item.cant=dt.getDouble(4);
            item.um=dt.getString(5);

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

    public String addItemSql(clsClasses.clsP_prodreceta item) {

        ins.init("P_prodreceta");

        ins.add("CODIGO_RECETA",item.codigo_receta);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CODIGO_ARTICULO",item.codigo_articulo);
        ins.add("CANT",item.cant);
        ins.add("UM",item.um);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodreceta item) {

        upd.init("P_prodreceta");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CODIGO_ARTICULO",item.codigo_articulo);
        upd.add("CANT",item.cant);
        upd.add("UM",item.um);

        upd.Where("(CODIGO_RECETA="+item.codigo_receta+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

