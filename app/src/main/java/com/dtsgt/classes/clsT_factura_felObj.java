package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_factura_felObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_factura_fel";
    private String sql;
    public ArrayList<clsClasses.clsT_factura_fel> items= new ArrayList<clsClasses.clsT_factura_fel>();

    public clsT_factura_felObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_factura_fel item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_factura_fel item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_factura_fel item) {
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

    public clsClasses.clsT_factura_fel first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_factura_fel item) {

        ins.init("T_factura_fel");

        ins.add("COREL",item.corel);
        ins.add("FEELSERIE",item.feelserie);
        ins.add("FEELNUMERO",item.feelnumero);
        ins.add("FEELUUID",item.feeluuid);
        ins.add("FEELFECHAPROCESADO",item.feelfechaprocesado);
        ins.add("FEELCONTINGENCIA",item.feelcontingencia);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_factura_fel item) {

        upd.init("T_factura_fel");

        upd.add("FEELSERIE",item.feelserie);
        upd.add("FEELNUMERO",item.feelnumero);
        upd.add("FEELUUID",item.feeluuid);
        upd.add("FEELFECHAPROCESADO",item.feelfechaprocesado);
        upd.add("FEELCONTINGENCIA",item.feelcontingencia);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_factura_fel item) {
        sql="DELETE FROM T_factura_fel WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_factura_fel WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_factura_fel item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_factura_fel();

            item.corel=dt.getString(0);
            item.feelserie=dt.getString(1);
            item.feelnumero=dt.getString(2);
            item.feeluuid=dt.getString(3);
            item.feelfechaprocesado=dt.getLong(4);
            item.feelcontingencia=dt.getString(5);

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

    public String addItemSql(clsClasses.clsT_factura_fel item) {

        ins.init("T_factura_fel");

        ins.add("COREL",item.corel);
        ins.add("FEELSERIE",item.feelserie);
        ins.add("FEELNUMERO",item.feelnumero);
        ins.add("FEELUUID",item.feeluuid);
        ins.add("FEELFECHAPROCESADO",item.feelfechaprocesado);
        ins.add("FEELCONTINGENCIA",item.feelcontingencia);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_factura_fel item) {

        upd.init("T_factura_fel");

        upd.add("FEELSERIE",item.feelserie);
        upd.add("FEELNUMERO",item.feelnumero);
        upd.add("FEELUUID",item.feeluuid);
        upd.add("FEELFECHAPROCESADO",item.feelfechaprocesado);
        upd.add("FEELCONTINGENCIA",item.feelcontingencia);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

