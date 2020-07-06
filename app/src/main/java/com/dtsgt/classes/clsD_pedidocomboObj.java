package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_pedidocomboObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_pedidocombo";
    private String sql;
    public ArrayList<clsClasses.clsD_pedidocombo> items= new ArrayList<clsClasses.clsD_pedidocombo>();

    public clsD_pedidocomboObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_pedidocombo item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_pedidocombo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_pedidocombo item) {
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

    public clsClasses.clsD_pedidocombo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_pedidocombo item) {

        ins.init("D_pedidocombo");

        ins.add("COREL_DET",item.corel_det);
        ins.add("COREL_COMBO",item.corel_combo);
        ins.add("SELECCION",item.seleccion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_pedidocombo item) {

        upd.init("D_pedidocombo");

        upd.add("COREL_DET",item.corel_det);
        upd.add("SELECCION",item.seleccion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("NOTA",item.nota);

        upd.Where("(COREL_COMBO="+item.corel_combo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_pedidocombo item) {
        sql="DELETE FROM D_pedidocombo WHERE (COREL_COMBO="+item.corel_combo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_pedidocombo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_pedidocombo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_pedidocombo();

            item.corel_det=dt.getInt(0);
            item.corel_combo=dt.getInt(1);
            item.seleccion=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.cant=dt.getInt(4);
            item.nota=dt.getString(5);

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

    public String addItemSql(clsClasses.clsD_pedidocombo item) {

        ins.init("D_pedidocombo");

        ins.add("COREL_DET",item.corel_det);
        ins.add("COREL_COMBO",item.corel_combo);
        ins.add("SELECCION",item.seleccion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_pedidocombo item) {

        upd.init("D_pedidocombo");

        upd.add("COREL_DET",item.corel_det);
        upd.add("SELECCION",item.seleccion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("NOTA",item.nota);

        upd.Where("(COREL_COMBO="+item.corel_combo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

