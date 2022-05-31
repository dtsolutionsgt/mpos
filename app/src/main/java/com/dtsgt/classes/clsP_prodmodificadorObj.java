package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodmodificadorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodmodificador";
    private String sql;
    public ArrayList<clsClasses.clsP_prodmodificador> items= new ArrayList<clsClasses.clsP_prodmodificador>();

    public clsP_prodmodificadorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodmodificador item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodmodificador item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodmodificador item) {
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

    public clsClasses.clsP_prodmodificador first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodmodificador item) {

        ins.init("P_prodmodificador");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_MODIF",item.codigo_modif);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodmodificador item) {

        upd.init("P_prodmodificador");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_MODIF",item.codigo_modif);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodmodificador item) {
        sql="DELETE FROM P_prodmodificador WHERE (CODIGO_PRODUCTO="+item.codigo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodmodificador WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodmodificador item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodmodificador();

            item.codigo_producto=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_modif=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsP_prodmodificador item) {

        ins.init("P_prodmodificador");

        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_MODIF",item.codigo_modif);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodmodificador item) {

        upd.init("P_prodmodificador");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_MODIF",item.codigo_modif);

        upd.Where("(CODIGO_PRODUCTO="+item.codigo_producto+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

