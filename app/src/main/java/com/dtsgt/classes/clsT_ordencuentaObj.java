package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencuentaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencuenta";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencuenta> items= new ArrayList<clsClasses.clsT_ordencuenta>();

    public clsT_ordencuentaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordencuenta item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencuenta item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencuenta item) {
        deleteItem(item);
    }

    public void delete(String id) {
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

    public clsClasses.clsT_ordencuenta first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencuenta item) {

        ins.init("T_ordencuenta");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("CF",item.cf);
        ins.add("NOMBRE",item.nombre);
        ins.add("NIT",item.nit);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencuenta item) {

        upd.init("T_ordencuenta");
        upd.add("CF",item.cf);
        upd.add("NOMBRE",item.nombre);
        upd.add("NIT",item.nit);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);
        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");
        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencuenta item) {
        sql="DELETE FROM T_ordencuenta WHERE (COREL='"+item.corel+"') AND (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ordencuenta WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsT_ordencuenta item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencuenta();

            item.corel=dt.getString(0);
            item.id=dt.getInt(1);
            item.cf=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.nit=dt.getString(4);
            item.direccion=dt.getString(5);
            item.correo=dt.getString(6);

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

    public String addItemSql(clsClasses.clsT_ordencuenta item,int idempresa) {

        ins.init("T_ordencuenta");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("EMPRESA",idempresa);
        ins.add("CF",item.cf);
        ins.add("NOMBRE",item.nombre);
        ins.add("NIT",item.nit);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencuenta item) {

        upd.init("T_ordencuenta");

        upd.add("CF",item.cf);
        upd.add("NOMBRE",item.nombre);
        upd.add("NIT",item.nit);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

