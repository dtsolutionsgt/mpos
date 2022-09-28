package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_barril_tipoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_barril_tipo";
    private String sql;
    public ArrayList<clsClasses.clsP_barril_tipo> items= new ArrayList<clsClasses.clsP_barril_tipo>();

    public clsP_barril_tipoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_barril_tipo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_barril_tipo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_barril_tipo item) {
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

    public clsClasses.clsP_barril_tipo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_barril_tipo item) {

        ins.init("P_barril_tipo");

        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("EMPRESA",item.empresa);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("CAPACIDAD",item.capacidad);
        ins.add("MERMAMIN",item.mermamin);
        ins.add("MERMAMAX",item.mermamax);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_barril_tipo item) {

        upd.init("P_barril_tipo");

        upd.add("EMPRESA",item.empresa);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("CAPACIDAD",item.capacidad);
        upd.add("MERMAMIN",item.mermamin);
        upd.add("MERMAMAX",item.mermamax);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_TIPO="+item.codigo_tipo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_barril_tipo item) {
        sql="DELETE FROM P_barril_tipo WHERE (CODIGO_TIPO="+item.codigo_tipo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_barril_tipo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_barril_tipo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_barril_tipo();

            item.codigo_tipo=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.descripcion=dt.getString(2);
            item.capacidad=dt.getDouble(3);
            item.mermamin=dt.getDouble(4);
            item.mermamax=dt.getDouble(5);
            item.activo=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsP_barril_tipo item) {

        ins.init("P_barril_tipo");

        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("EMPRESA",item.empresa);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("CAPACIDAD",item.capacidad);
        ins.add("MERMAMIN",item.mermamin);
        ins.add("MERMAMAX",item.mermamax);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_barril_tipo item) {

        upd.init("P_barril_tipo");

        upd.add("EMPRESA",item.empresa);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("CAPACIDAD",item.capacidad);
        upd.add("MERMAMIN",item.mermamin);
        upd.add("MERMAMAX",item.mermamax);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_TIPO="+item.codigo_tipo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

