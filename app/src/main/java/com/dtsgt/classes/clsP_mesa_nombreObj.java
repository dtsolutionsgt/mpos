package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_mesa_nombreObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_mesa_nombre";
    private String sql;
    public ArrayList<clsClasses.clsP_mesa_nombre> items= new ArrayList<clsClasses.clsP_mesa_nombre>();

    public clsP_mesa_nombreObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_mesa_nombre item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_mesa_nombre item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_mesa_nombre item) {
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

    public clsClasses.clsP_mesa_nombre first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_mesa_nombre item) {

        ins.init("P_mesa_nombre");

        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_mesa_nombre item) {

        upd.init("P_mesa_nombre");

        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_MESA="+item.codigo_mesa+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_mesa_nombre item) {
        sql="DELETE FROM P_mesa_nombre WHERE (CODIGO_MESA="+item.codigo_mesa+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_mesa_nombre WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_mesa_nombre item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_mesa_nombre();

            item.codigo_mesa=dt.getInt(0);
            item.nombre=dt.getString(1);

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

    public String addItemSql(clsClasses.clsP_mesa_nombre item) {

        ins.init("P_mesa_nombre");

        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_mesa_nombre item) {

        upd.init("P_mesa_nombre");

        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO_MESA="+item.codigo_mesa+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

