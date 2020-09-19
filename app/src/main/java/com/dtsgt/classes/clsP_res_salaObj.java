package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_res_salaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_res_sala";
    private String sql;
    public ArrayList<clsClasses.clsP_res_sala> items= new ArrayList<clsClasses.clsP_res_sala>();

    public clsP_res_salaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_res_sala item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_res_sala item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_res_sala item) {
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

    public clsClasses.clsP_res_sala first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_res_sala item) {

        ins.init("P_res_sala");

        ins.add("CODIGO_SALA",item.codigo_sala);
        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("ESCALA",item.escala);
        ins.add("TAM_LETRA",item.tam_letra);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_res_sala item) {

        upd.init("P_res_sala");

        upd.add("EMPRESA",item.empresa);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("ESCALA",item.escala);
        upd.add("TAM_LETRA",item.tam_letra);

        upd.Where("(CODIGO_SALA="+item.codigo_sala+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_res_sala item) {
        sql="DELETE FROM P_res_sala WHERE (CODIGO_SALA="+item.codigo_sala+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_res_sala WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_res_sala item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_res_sala();

            item.codigo_sala=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.sucursal=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.activo=dt.getInt(4);
            item.escala=dt.getDouble(5);
            item.tam_letra=dt.getDouble(6);

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

    public String addItemSql(clsClasses.clsP_res_sala item) {

        ins.init("P_res_sala");

        ins.add("CODIGO_SALA",item.codigo_sala);
        ins.add("EMPRESA",item.empresa);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("ESCALA",item.escala);
        ins.add("TAM_LETRA",item.tam_letra);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_res_sala item) {

        upd.init("P_res_sala");

        upd.add("EMPRESA",item.empresa);
        upd.add("SUCURSAL",item.sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("ESCALA",item.escala);
        upd.add("TAM_LETRA",item.tam_letra);

        upd.Where("(CODIGO_SALA="+item.codigo_sala+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

