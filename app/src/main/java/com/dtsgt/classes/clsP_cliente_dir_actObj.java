package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_cliente_dir_actObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cliente_dir_act";
    private String sql;
    public ArrayList<clsClasses.clsP_cliente_dir_act> items= new ArrayList<clsClasses.clsP_cliente_dir_act>();

    public clsP_cliente_dir_actObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cliente_dir_act item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cliente_dir_act item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cliente_dir_act item) {
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

    public clsClasses.clsP_cliente_dir_act first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cliente_dir_act item) {

        ins.init("P_cliente_dir_act");

        ins.add("CODIGO_DIRECCION",item.codigo_direccion);
        ins.add("ESTADO",item.estado);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("DIRECCION",item.direccion);
        ins.add("REFERENCIA",item.referencia);
        ins.add("TELEFONO",item.telefono);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cliente_dir_act item) {

        upd.init("P_cliente_dir_act");

        upd.add("ESTADO",item.estado);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("DIRECCION",item.direccion);
        upd.add("REFERENCIA",item.referencia);
        upd.add("TELEFONO",item.telefono);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_DIRECCION="+item.codigo_direccion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cliente_dir_act item) {
        sql="DELETE FROM P_cliente_dir_act WHERE (CODIGO_DIRECCION="+item.codigo_direccion+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_cliente_dir_act WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cliente_dir_act item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cliente_dir_act();

            item.codigo_direccion=dt.getInt(0);
            item.estado=dt.getInt(1);
            item.codigo_cliente=dt.getInt(2);
            item.direccion=dt.getString(3);
            item.referencia=dt.getString(4);
            item.telefono=dt.getString(5);
            item.statcom=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsP_cliente_dir_act item) {

        ins.init("P_cliente_dir_act");

        ins.add("CODIGO_DIRECCION",item.codigo_direccion);
        ins.add("ESTADO",item.estado);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("DIRECCION",item.direccion);
        ins.add("REFERENCIA",item.referencia);
        ins.add("TELEFONO",item.telefono);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cliente_dir_act item) {

        upd.init("P_cliente_dir_act");

        upd.add("ESTADO",item.estado);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("DIRECCION",item.direccion);
        upd.add("REFERENCIA",item.referencia);
        upd.add("TELEFONO",item.telefono);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_DIRECCION="+item.codigo_direccion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

