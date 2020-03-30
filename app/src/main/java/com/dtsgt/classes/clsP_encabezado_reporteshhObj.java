package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_encabezado_reporteshhObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_encabezado_reporteshh";
    private String sql;
    public ArrayList<clsClasses.clsP_encabezado_reporteshh> items= new ArrayList<clsClasses.clsP_encabezado_reporteshh>();

    public clsP_encabezado_reporteshhObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_encabezado_reporteshh item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_encabezado_reporteshh item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_encabezado_reporteshh item) {
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

    public clsClasses.clsP_encabezado_reporteshh first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_encabezado_reporteshh item) {

        ins.init("P_encabezado_reporteshh");

        ins.add("CODIGO",item.codigo);
        ins.add("TEXTO",item.texto);
        ins.add("SUCURSAL",item.sucursal);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_encabezado_reporteshh item) {

        upd.init("P_encabezado_reporteshh");

        upd.add("TEXTO",item.texto);
        upd.add("SUCURSAL",item.sucursal);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_encabezado_reporteshh item) {
        sql="DELETE FROM P_encabezado_reporteshh WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_encabezado_reporteshh WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_encabezado_reporteshh item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_encabezado_reporteshh();

            item.codigo=dt.getInt(0);
            item.texto=dt.getString(1);
            item.sucursal=dt.getString(2);

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

    public String addItemSql(clsClasses.clsP_encabezado_reporteshh item) {

        ins.init("P_encabezado_reporteshh");

        ins.add("CODIGO",item.codigo);
        ins.add("TEXTO",item.texto);
        ins.add("SUCURSAL",item.sucursal);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_encabezado_reporteshh item) {

        upd.init("P_encabezado_reporteshh");

        upd.add("TEXTO",item.texto);
        upd.add("SUCURSAL",item.sucursal);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

