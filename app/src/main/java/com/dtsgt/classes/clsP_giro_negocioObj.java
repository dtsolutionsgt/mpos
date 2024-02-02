package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_giro_negocioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_giro_negocio";
    private String sql;
    public ArrayList<clsClasses.clsP_giro_negocio> items= new ArrayList<clsClasses.clsP_giro_negocio>();

    public clsP_giro_negocioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_giro_negocio item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_giro_negocio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_giro_negocio item) {
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

    public clsClasses.clsP_giro_negocio first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_giro_negocio item) {

        ins.init("P_giro_negocio");

        ins.add("CODIGO_GIRO_NEGOCIO",item.codigo_giro_negocio);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("CODIGO",item.codigo);
        ins.add("DESCRIPCION",item.descripcion);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_giro_negocio item) {

        upd.init("P_giro_negocio");

        upd.add("COD_PAIS",item.cod_pais);
        upd.add("CODIGO",item.codigo);
        upd.add("DESCRIPCION",item.descripcion);

        upd.Where("(CODIGO_GIRO_NEGOCIO="+item.codigo_giro_negocio+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_giro_negocio item) {
        sql="DELETE FROM P_giro_negocio WHERE (CODIGO_GIRO_NEGOCIO="+item.codigo_giro_negocio+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_giro_negocio WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_giro_negocio item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_giro_negocio();

            item.codigo_giro_negocio=dt.getInt(0);
            item.cod_pais=dt.getString(1);
            item.codigo=dt.getInt(2);
            item.descripcion=dt.getString(3);

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

    public String addItemSql(clsClasses.clsP_giro_negocio item) {

        ins.init("P_giro_negocio");

        ins.add("CODIGO_GIRO_NEGOCIO",item.codigo_giro_negocio);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("CODIGO",item.codigo);
        ins.add("DESCRIPCION",item.descripcion);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_giro_negocio item) {

        upd.init("P_giro_negocio");

        upd.add("COD_PAIS",item.cod_pais);
        upd.add("CODIGO",item.codigo);
        upd.add("DESCRIPCION",item.descripcion);

        upd.Where("(CODIGO_GIRO_NEGOCIO="+item.codigo_giro_negocio+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

