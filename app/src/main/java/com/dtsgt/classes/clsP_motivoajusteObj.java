package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsP_motivoajusteObj {


    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_MOTIVO_AJUSTE";
    private String sql;
    public ArrayList<clsClasses.clsP_motivoajuste> items = new ArrayList<clsClasses.clsP_motivoajuste>();

    public clsP_motivoajusteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_motivoajuste item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_motivoajuste item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_motivoajuste item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_motivoajuste first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_motivoajuste item) {

        ins.init("P_MOTIVO_AJUSTE");

        ins.add("CODIGO_MOTIVO_AJUSTE", item.codigo_motivo_ajuste);
        ins.add("EMPRESA", item.empresa);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_motivoajuste item) {

        upd.init("P_MOTIVO_AJUSTE");

        upd.add("EMPRESA", item.activo);
        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO_MOTIVO_AJUSTE=" + item.codigo_motivo_ajuste + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_motivoajuste item) {
        sql = "DELETE FROM P_MOTIVO_AJUSTE WHERE (CODIGO_MOTIVO_AJUSTE=" + item.codigo_motivo_ajuste + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM P_MOTIVO_AJUSTE WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_motivoajuste item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_motivoajuste();

            item.codigo_motivo_ajuste = dt.getInt(0);
            item.empresa = dt.getInt(1);
            item.nombre = dt.getString(2);
            item.activo = (dt.getInt(3)==1?true:false);

            items.add(item);

            dt.moveToNext();
        }

        if (dt != null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt = null;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        if (dt != null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_motivoajuste item) {

        ins.init("P_MOTIVO_AJUSTE");

        ins.add("CODIGO_MOTIVO_AJUSTE", item.codigo_motivo_ajuste);
        ins.add("EMPRESA", item.empresa);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_motivoajuste item) {

        upd.init("P_MOTIVO_AJUSTE");

        upd.add("EMPRESA", item.empresa);
        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO_MOTIVO_AJUSTE=" + item.codigo_motivo_ajuste + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}
