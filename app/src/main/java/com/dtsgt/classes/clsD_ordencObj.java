package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_ordencObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_ordenc";
    private String sql;
    public ArrayList<clsClasses.clsD_ordenc> items= new ArrayList<clsClasses.clsD_ordenc>();

    public clsD_ordencObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_ordenc item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_ordenc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_ordenc item) {
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

    public clsClasses.clsD_ordenc first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_ordenc item) {

        ins.init("D_ordenc");

        ins.add("CODIGO_ORDEN",item.codigo_orden);
        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("COMBOID",item.comboid);
        ins.add("ITEMID",item.itemid);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);
        ins.add("MODIF",item.modif);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_ordenc item) {

        upd.init("D_ordenc");

        upd.add("COREL",item.corel);
        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);
        upd.add("MODIF",item.modif);

        upd.Where("(CODIGO_ORDEN="+item.codigo_orden+") AND (COMBOID="+item.comboid+") AND (ITEMID="+item.itemid+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_ordenc item) {
        sql="DELETE FROM D_ordenc WHERE (CODIGO_ORDEN="+item.codigo_orden+") AND (COMBOID="+item.comboid+") AND (ITEMID="+item.itemid+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_ordenc WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_ordenc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_ordenc();

            item.codigo_orden=dt.getInt(0);
            item.corel=dt.getString(1);
            item.empresa=dt.getInt(2);
            item.comboid=dt.getInt(3);
            item.itemid=dt.getInt(4);
            item.nombre=dt.getString(5);
            item.nota=dt.getString(6);
            item.modif=dt.getString(7);

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

    public String addItemSql(clsClasses.clsD_ordenc item) {

        ins.init("D_ordenc");

        ins.add("CODIGO_ORDEN",item.codigo_orden);
        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("COMBOID",item.comboid);
        ins.add("ITEMID",item.itemid);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);
        ins.add("MODIF",item.modif);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_ordenc item) {

        upd.init("D_ordenc");

        upd.add("COREL",item.corel);
        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);
        upd.add("MODIF",item.modif);

        upd.Where("(CODIGO_ORDEN="+item.codigo_orden+") AND (COMBOID="+item.comboid+") AND (ITEMID="+item.itemid+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

