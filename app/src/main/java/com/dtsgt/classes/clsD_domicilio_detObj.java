package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_domicilio_detObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_domicilio_det";
    private String sql;
    public ArrayList<clsClasses.clsD_domicilio_det> items= new ArrayList<clsClasses.clsD_domicilio_det>();

    public clsD_domicilio_detObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_domicilio_det item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_domicilio_det item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_domicilio_det item) {
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

    public clsClasses.clsD_domicilio_det first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_domicilio_det item) {

        ins.init("D_domicilio_det");

        ins.add("CODIGO",item.codigo);
        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("UM",item.um);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("NOTA",item.nota);
        ins.add("TIPO_PRODUCTO",item.tipo_producto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_domicilio_det item) {

        upd.init("D_domicilio_det");

        upd.add("COREL",item.corel);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("UM",item.um);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("NOTA",item.nota);
        upd.add("TIPO_PRODUCTO",item.tipo_producto);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_domicilio_det item) {
        sql="DELETE FROM D_domicilio_det WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_domicilio_det WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_domicilio_det item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_domicilio_det();

            item.codigo=dt.getInt(0);
            item.corel=dt.getString(1);
            item.empresa=dt.getInt(2);
            item.codigo_producto=dt.getInt(3);
            item.cant=dt.getDouble(4);
            item.precio=dt.getDouble(5);
            item.um=dt.getString(6);
            item.imp=dt.getDouble(7);
            item.des=dt.getDouble(8);
            item.desmon=dt.getDouble(9);
            item.total=dt.getDouble(10);
            item.nota=dt.getString(11);
            item.tipo_producto=dt.getString(12);

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

    public String addItemSql(clsClasses.clsD_domicilio_det item) {

        ins.init("D_domicilio_det");

        ins.add("CODIGO",item.codigo);
        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANT",item.cant);
        ins.add("PRECIO",item.precio);
        ins.add("UM",item.um);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("NOTA",item.nota);
        ins.add("TIPO_PRODUCTO",item.tipo_producto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_domicilio_det item) {

        upd.init("D_domicilio_det");

        upd.add("COREL",item.corel);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANT",item.cant);
        upd.add("PRECIO",item.precio);
        upd.add("UM",item.um);
        upd.add("IMP",item.imp);
        upd.add("DES",item.des);
        upd.add("DESMON",item.desmon);
        upd.add("TOTAL",item.total);
        upd.add("NOTA",item.nota);
        upd.add("TIPO_PRODUCTO",item.tipo_producto);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();


    }

    //endregion
}

