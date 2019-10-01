package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsP_archivoconfObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_archivoconf";
    private String sql;
    public ArrayList<clsClasses.clsP_archivoconf> items = new ArrayList<clsClasses.clsP_archivoconf>();

    public clsP_archivoconfObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_archivoconf item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_archivoconf item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_archivoconf item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsP_archivoconf first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_archivoconf item) {

        ins.init("P_archivoconf");

        ins.add("RUTA", item.ruta);
        ins.add("TIPO_HH", item.tipo_hh);
        ins.add("IDIOMA", item.idioma);
        ins.add("TIPO_IMPRESORA", item.tipo_impresora);
        ins.add("SERIAL_HH", item.serial_hh);
        ins.add("MODIF_PESO", item.modif_peso);
        ins.add("PUERTO_IMPRESION", item.puerto_impresion);
        ins.add("LBS_O_KGS", item.lbs_o_kgs);
        ins.add("NOTA_CREDITO", item.nota_credito);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_archivoconf item) {

        upd.init("P_archivoconf");

        upd.add("TIPO_HH", item.tipo_hh);
        upd.add("IDIOMA", item.idioma);
        upd.add("TIPO_IMPRESORA", item.tipo_impresora);
        upd.add("SERIAL_HH", item.serial_hh);
        upd.add("MODIF_PESO", item.modif_peso);
        upd.add("PUERTO_IMPRESION", item.puerto_impresion);
        upd.add("LBS_O_KGS", item.lbs_o_kgs);
        upd.add("NOTA_CREDITO", item.nota_credito);

        upd.Where("(RUTA='" + item.ruta + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_archivoconf item) {
        sql = "DELETE FROM P_archivoconf WHERE (RUTA='" + item.ruta + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_archivoconf WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_archivoconf item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_archivoconf();

            item.ruta = dt.getString(0);
            item.tipo_hh = dt.getString(1);
            item.idioma = dt.getString(2);
            item.tipo_impresora = dt.getString(3);
            item.serial_hh = dt.getString(4);
            item.modif_peso = dt.getString(5);
            item.puerto_impresion = dt.getString(6);
            item.lbs_o_kgs = dt.getString(7);
            item.nota_credito = dt.getInt(8);

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

    public String addItemSql(clsClasses.clsP_archivoconf item) {

        ins.init("P_archivoconf");

        ins.add("RUTA", item.ruta);
        ins.add("TIPO_HH", item.tipo_hh);
        ins.add("IDIOMA", item.idioma);
        ins.add("TIPO_IMPRESORA", item.tipo_impresora);
        ins.add("SERIAL_HH", item.serial_hh);
        ins.add("MODIF_PESO", item.modif_peso);
        ins.add("PUERTO_IMPRESION", item.puerto_impresion);
        ins.add("LBS_O_KGS", item.lbs_o_kgs);
        ins.add("NOTA_CREDITO", item.nota_credito);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_archivoconf item) {

        upd.init("P_archivoconf");

        upd.add("TIPO_HH", item.tipo_hh);
        upd.add("IDIOMA", item.idioma);
        upd.add("TIPO_IMPRESORA", item.tipo_impresora);
        upd.add("SERIAL_HH", item.serial_hh);
        upd.add("MODIF_PESO", item.modif_peso);
        upd.add("PUERTO_IMPRESION", item.puerto_impresion);
        upd.add("LBS_O_KGS", item.lbs_o_kgs);
        upd.add("NOTA_CREDITO", item.nota_credito);

        upd.Where("(RUTA='" + item.ruta + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

