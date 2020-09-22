package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_res_grupoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_res_grupo";
    private String sql;
    public ArrayList<clsClasses.clsP_res_grupo> items= new ArrayList<clsClasses.clsP_res_grupo>();

    public clsP_res_grupoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_res_grupo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_res_grupo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_res_grupo item) {
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

    public clsClasses.clsP_res_grupo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_res_grupo item) {

        ins.init("P_res_grupo");

        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("TELEFONO",item.telefono);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_res_grupo item) {

        upd.init("P_res_grupo");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("TELEFONO",item.telefono);

        upd.Where("(CODIGO_GRUPO="+item.codigo_grupo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_res_grupo item) {
        sql="DELETE FROM P_res_grupo WHERE (CODIGO_GRUPO="+item.codigo_grupo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_res_grupo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_res_grupo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_res_grupo();

            item.codigo_grupo=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.telefono=dt.getString(4);

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

    public String addItemSql(clsClasses.clsP_res_grupo item) {

        ins.init("P_res_grupo");

        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("TELEFONO",item.telefono);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_res_grupo item) {

        upd.init("P_res_grupo");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("TELEFONO",item.telefono);

        upd.Where("(CODIGO_GRUPO="+item.codigo_grupo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

