package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_fel_impuestoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_fel_impuesto";
    private String sql;
    public ArrayList<clsClasses.clsP_fel_impuesto> items= new ArrayList<clsClasses.clsP_fel_impuesto>();

    public clsP_fel_impuestoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_fel_impuesto item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_fel_impuesto item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_fel_impuesto item) {
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

    public clsClasses.clsP_fel_impuesto first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsP_fel_impuesto item) {

        ins.init("P_fel_impuesto");

        ins.add("CODIGO_IMPUESTO",item.codigo_impuesto);
        ins.add("CODIGO_PAIS",item.codigo_pais);
        ins.add("VALOR",item.valor);
        ins.add("CODIGO_FEL",item.codigo_fel);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_fel_impuesto item) {

        upd.init("P_fel_impuesto");

        upd.add("CODIGO_PAIS",item.codigo_pais);
        upd.add("VALOR",item.valor);
        upd.add("CODIGO_FEL",item.codigo_fel);

        upd.Where("(CODIGO_IMPUESTO="+item.codigo_impuesto+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsP_fel_impuesto item) {
        sql="DELETE FROM P_fel_impuesto WHERE (CODIGO_IMPUESTO="+item.codigo_impuesto+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_fel_impuesto WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_fel_impuesto item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_fel_impuesto();

            item.codigo_impuesto=dt.getInt(0);
            item.codigo_pais=dt.getString(1);
            item.valor=dt.getDouble(2);
            item.codigo_fel=dt.getString(3);

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

    public String addItemSql(clsClasses.clsP_fel_impuesto item) {

        ins.init("P_fel_impuesto");

        ins.add("CODIGO_IMPUESTO",item.codigo_impuesto);
        ins.add("CODIGO_PAIS",item.codigo_pais);
        ins.add("VALOR",item.valor);
        ins.add("CODIGO_FEL",item.codigo_fel);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_fel_impuesto item) {

        upd.init("P_fel_impuesto");

        upd.add("CODIGO_PAIS",item.codigo_pais);
        upd.add("VALOR",item.valor);
        upd.add("CODIGO_FEL",item.codigo_fel);

        upd.Where("(CODIGO_IMPUESTO="+item.codigo_impuesto+")");

        return upd.sql();


    }

    //endregion
}

