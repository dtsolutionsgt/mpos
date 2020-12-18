package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_impresora_marcaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_impresora_marca";
    private String sql;
    public ArrayList<clsClasses.clsP_impresora_marca> items= new ArrayList<clsClasses.clsP_impresora_marca>();

    public clsP_impresora_marcaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_impresora_marca item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_impresora_marca item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_impresora_marca item) {
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

    public clsClasses.clsP_impresora_marca first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_impresora_marca item) {

        ins.init("P_impresora_marca");

        ins.add("CODIGO_IMPRESORA_MARCA",item.codigo_impresora_marca);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_impresora_marca item) {

        upd.init("P_impresora_marca");

        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_IMPRESORA_MARCA="+item.codigo_impresora_marca+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_impresora_marca item) {
        sql="DELETE FROM P_impresora_marca WHERE (CODIGO_IMPRESORA_MARCA="+item.codigo_impresora_marca+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_impresora_marca WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_impresora_marca item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_impresora_marca();

            item.codigo_impresora_marca=dt.getInt(0);
            item.nombre=dt.getString(1);
            item.activo=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsP_impresora_marca item) {

        ins.init("P_impresora_marca");

        ins.add("CODIGO_IMPRESORA_MARCA",item.codigo_impresora_marca);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_impresora_marca item) {

        upd.init("P_impresora_marca");

        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_IMPRESORA_MARCA="+item.codigo_impresora_marca+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

