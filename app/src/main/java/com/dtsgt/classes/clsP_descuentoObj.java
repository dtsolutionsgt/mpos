package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_descuentoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_descuento";
    private String sql;
    public ArrayList<clsClasses.clsP_descuento> items = new ArrayList<clsClasses.clsP_descuento>();

    public clsP_descuentoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_descuento item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_descuento item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_descuento item) {
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

    public clsClasses.clsP_descuento first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_descuento item) {

        ins.init("P_descuento");

        ins.add("CODIGO_DESCUENTO", item.codigo_descuento);
        ins.add("CLIENTE", item.cliente);
        ins.add("CTIPO", item.ctipo);
        ins.add("PRODUCTO", item.producto);
        ins.add("PTIPO", item.ptipo);
        ins.add("TIPORUTA", item.tiporuta);
        ins.add("RANGOINI", item.rangoini);
        ins.add("RANGOFIN", item.rangofin);
        ins.add("DESCTIPO", item.desctipo);
        ins.add("VALOR", item.valor);
        ins.add("GLOBDESC", item.globdesc);
        ins.add("PORCANT", item.porcant);
        ins.add("FECHAINI", item.fechaini);
        ins.add("FECHAFIN", item.fechafin);
        ins.add("CODDESC", item.coddesc);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_descuento item) {

        upd.init("P_descuento");

        upd.add("RANGOFIN", item.rangofin);
        upd.add("DESCTIPO", item.desctipo);
        upd.add("VALOR", item.valor);
        upd.add("GLOBDESC", item.globdesc);
        upd.add("PORCANT", item.porcant);
        upd.add("FECHAINI", item.fechaini);
        upd.add("FECHAFIN", item.fechafin);
        upd.add("CODDESC", item.coddesc);
        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO_DESCUENTO=" + item.codigo_descuento + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_descuento item) {
        sql = "DELETE FROM P_descuento WHERE (CODIGO_DESCUENTO=" + item.codigo_descuento + ")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_descuento WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_descuento item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_descuento();

            item.cliente = dt.getInt(0);
            item.ctipo = dt.getInt(1);
            item.producto = dt.getInt(2);
            item.ptipo = dt.getInt(3);
            item.tiporuta = dt.getInt(4);
            item.rangoini = dt.getDouble(5);
            item.rangofin = dt.getDouble(6);
            item.desctipo = dt.getString(7);
            item.valor = dt.getDouble(8);
            item.globdesc = dt.getString(9);
            item.porcant = dt.getString(10);
            item.fechaini = dt.getLong(11);
            item.fechafin = dt.getLong(12);
            item.coddesc = dt.getInt(13);
            item.nombre = dt.getString(14);
            item.activo = dt.getInt(15);
            item.codigo_descuento = dt.getInt(16);

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

    public String addItemSql(clsClasses.clsP_descuento item) {

        ins.init("P_descuento");

        ins.add("CLIENTE", item.cliente);
        ins.add("CTIPO", item.ctipo);
        ins.add("PRODUCTO", item.producto);
        ins.add("PTIPO", item.ptipo);
        ins.add("TIPORUTA", item.tiporuta);
        ins.add("RANGOINI", item.rangoini);
        ins.add("RANGOFIN", item.rangofin);
        ins.add("DESCTIPO", item.desctipo);
        ins.add("VALOR", item.valor);
        ins.add("GLOBDESC", item.globdesc);
        ins.add("PORCANT", item.porcant);
        ins.add("FECHAINI", item.fechaini);
        ins.add("FECHAFIN", item.fechafin);
        ins.add("CODDESC", item.coddesc);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_descuento item) {

        upd.init("P_descuento");

        upd.add("RANGOFIN", item.rangofin);
        upd.add("DESCTIPO", item.desctipo);
        upd.add("VALOR", item.valor);
        upd.add("GLOBDESC", item.globdesc);
        upd.add("PORCANT", item.porcant);
        upd.add("FECHAINI", item.fechaini);
        upd.add("FECHAFIN", item.fechafin);
        upd.add("CODDESC", item.coddesc);
        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CLIENTE='" + item.cliente + "') AND (CTIPO=" + item.ctipo + ") AND (PRODUCTO='" + item.producto + "') AND (PTIPO=" + item.ptipo + ") AND (TIPORUTA=" + item.tiporuta + ") AND (RANGOINI=" + item.rangoini + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

