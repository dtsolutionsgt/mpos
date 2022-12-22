package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_vendedor_rolObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_vendedor_rol";
    private String sql;
    public ArrayList<clsClasses.clsP_vendedor_rol> items= new ArrayList<clsClasses.clsP_vendedor_rol>();

    public clsP_vendedor_rolObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_vendedor_rol item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_vendedor_rol item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_vendedor_rol item) {
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

    public clsClasses.clsP_vendedor_rol first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_vendedor_rol item) {

        ins.init("P_vendedor_rol");

        ins.add("CODIGO_VENDEDOR_ROL",item.codigo_vendedor_rol);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("CODIGO_ROL",item.codigo_rol);
        ins.add("fec_agr",item.fec_agr);
        ins.add("user_agr",item.user_agr);
        ins.add("fec_mod",item.fec_mod);
        ins.add("user_mod",item.user_mod);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_vendedor_rol item) {

        upd.init("P_vendedor_rol");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("CODIGO_ROL",item.codigo_rol);
        upd.add("fec_agr",item.fec_agr);
        upd.add("user_agr",item.user_agr);
        upd.add("fec_mod",item.fec_mod);
        upd.add("user_mod",item.user_mod);

        upd.Where("(CODIGO_VENDEDOR_ROL="+item.codigo_vendedor_rol+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_vendedor_rol item) {
        sql="DELETE FROM P_vendedor_rol WHERE (CODIGO_VENDEDOR_ROL="+item.codigo_vendedor_rol+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_vendedor_rol WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_vendedor_rol item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_vendedor_rol();

            item.codigo_vendedor_rol=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_vendedor=dt.getInt(3);
            item.codigo_rol=dt.getInt(4);
            item.fec_agr=dt.getInt(5);
            item.user_agr=dt.getInt(6);
            item.fec_mod=dt.getInt(7);
            item.user_mod=dt.getInt(8);

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

    public String addItemSql(clsClasses.clsP_vendedor_rol item) {

        ins.init("P_vendedor_rol");

        ins.add("CODIGO_VENDEDOR_ROL",item.codigo_vendedor_rol);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("CODIGO_ROL",item.codigo_rol);
        ins.add("fec_agr",item.fec_agr);
        ins.add("user_agr",item.user_agr);
        ins.add("fec_mod",item.fec_mod);
        ins.add("user_mod",item.user_mod);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_vendedor_rol item) {

        upd.init("P_vendedor_rol");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("CODIGO_ROL",item.codigo_rol);
        upd.add("fec_agr",item.fec_agr);
        upd.add("user_agr",item.user_agr);
        upd.add("fec_mod",item.fec_mod);
        upd.add("user_mod",item.user_mod);

        upd.Where("(CODIGO_VENDEDOR_ROL="+item.codigo_vendedor_rol+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

