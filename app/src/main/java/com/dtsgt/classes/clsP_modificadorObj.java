package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_modificadorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_modificador";
    private String sql;
    public ArrayList<clsClasses.clsP_modificador> items= new ArrayList<clsClasses.clsP_modificador>();

    public clsP_modificadorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_modificador item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_modificador item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_modificador item) {
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

    public clsClasses.clsP_modificador first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_modificador item) {

        ins.init("P_modificador");

        ins.add("CODIGO_MODIF",item.codigo_modif);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_modificador item) {

        upd.init("P_modificador");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_MODIF="+item.codigo_modif+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_modificador item) {
        sql="DELETE FROM P_modificador WHERE (CODIGO_MODIF="+item.codigo_modif+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_modificador WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_modificador item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_modificador();

            item.codigo_modif=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_grupo=dt.getInt(2);
            item.nombre=dt.getString(3);

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

    public String addItemSql(clsClasses.clsP_modificador item) {

        ins.init("P_modificador");

        ins.add("CODIGO_MODIF",item.codigo_modif);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_modificador item) {

        upd.init("P_modificador");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_MODIF="+item.codigo_modif+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

