package com.dtsgt.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_lineaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_linea";
    private String sql;
    public ArrayList<clsClasses.clsP_linea> items= new ArrayList<clsClasses.clsP_linea>();

    public clsP_lineaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_linea item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_linea item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_linea item) {
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

    public clsClasses.clsP_linea first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_linea item) {

        ins.init("P_linea");

        ins.add("CODIGO",item.codigo);
        ins.add("MARCA",item.marca);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_LINEA",item.codigo_linea);
        ins.add("IMAGEN", item.imagen);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_linea item) {

        upd.init("P_linea");

        upd.add("CODIGO",item.codigo);
        upd.add("MARCA",item.marca);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("IMAGEN",item.imagen);

        upd.Where("(CODIGO_LINEA="+item.codigo_linea+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_linea item) {
        sql="DELETE FROM P_linea WHERE (CODIGO_LINEA="+item.codigo_linea+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_linea WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_linea item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_linea();

            item.codigo=dt.getString(0);
            item.marca=dt.getString(1);
            item.nombre=dt.getString(2);
            item.activo=dt.getInt(3);
            item.codigo_linea=dt.getInt(4);
            item.imagen=dt.getString(5);

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

    public String addItemSql(clsClasses.clsP_linea item) {

        ins.init("P_linea");

        ins.add("CODIGO",item.codigo);
        ins.add("MARCA",item.marca);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_LINEA",item.codigo_linea);
        ins.add("IMAGEN", (item.imagen==null?"":item.imagen));

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_linea item) {

        upd.init("P_linea");

        upd.add("CODIGO",item.codigo);
        upd.add("MARCA",item.marca);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("IMAGEN",item.imagen);

        upd.Where("(CODIGO_LINEA="+item.codigo_linea+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

