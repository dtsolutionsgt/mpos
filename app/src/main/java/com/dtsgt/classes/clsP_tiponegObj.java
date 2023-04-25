package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_tiponegObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_tiponeg";
    private String sql;
    public ArrayList<clsClasses.clsP_tiponeg> items= new ArrayList<clsClasses.clsP_tiponeg>();

    public clsP_tiponegObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_tiponeg item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_tiponeg item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_tiponeg item) {
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

    public clsClasses.clsP_tiponeg first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_tiponeg item) {

        ins.init("P_tiponeg");

        ins.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);
        ins.add("EMPRESA",item.empresa);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("ACTIVO",item.activo);
        ins.add("fec_agr",item.fec_agr);
        ins.add("user_agr",item.user_agr);
        ins.add("fec_mod",item.fec_mod);
        ins.add("user_mod",item.user_mod);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_tiponeg item) {

        upd.init("P_tiponeg");

        upd.add("EMPRESA",item.empresa);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("ACTIVO",item.activo);
        upd.add("fec_agr",item.fec_agr);
        upd.add("user_agr",item.user_agr);
        upd.add("fec_mod",item.fec_mod);
        upd.add("user_mod",item.user_mod);

        upd.Where("(CODIGO_TIPO_NEGOCIO="+item.codigo_tipo_negocio+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_tiponeg item) {
        sql="DELETE FROM P_tiponeg WHERE (CODIGO_TIPO_NEGOCIO="+item.codigo_tipo_negocio+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_tiponeg WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_tiponeg item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_tiponeg();

            item.codigo_tipo_negocio=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.descripcion=dt.getString(2);
            item.activo=dt.getInt(3);
            item.fec_agr=dt.getInt(4);
            item.user_agr=dt.getInt(5);
            item.fec_mod=dt.getInt(6);
            item.user_mod=dt.getInt(7);

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

    public String addItemSql(clsClasses.clsP_tiponeg item) {

        ins.init("P_tiponeg");

        ins.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);
        ins.add("EMPRESA",item.empresa);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("ACTIVO",item.activo);
        ins.add("fec_agr",item.fec_agr);
        ins.add("user_agr",item.user_agr);
        ins.add("fec_mod",item.fec_mod);
        ins.add("user_mod",item.user_mod);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_tiponeg item) {

        upd.init("P_tiponeg");

        upd.add("EMPRESA",item.empresa);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("ACTIVO",item.activo);
        upd.add("fec_agr",item.fec_agr);
        upd.add("user_agr",item.user_agr);
        upd.add("fec_mod",item.fec_mod);
        upd.add("user_mod",item.user_mod);

        upd.Where("(CODIGO_TIPO_NEGOCIO="+item.codigo_tipo_negocio+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

