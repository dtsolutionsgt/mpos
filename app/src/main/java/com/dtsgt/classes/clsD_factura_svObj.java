package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_factura_svObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura_sv";
    private String sql;
    public ArrayList<clsClasses.clsD_factura_sv> items= new ArrayList<clsClasses.clsD_factura_sv>();

    public clsD_factura_svObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura_sv item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura_sv item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura_sv item) {
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

    public clsClasses.clsD_factura_sv first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_factura_sv item) {

        ins.init("D_factura_sv");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_TIPO_FACTURA",item.codigo_tipo_factura);
        ins.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        ins.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        ins.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura_sv item) {

        upd.init("D_factura_sv");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_TIPO_FACTURA",item.codigo_tipo_factura);
        upd.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        upd.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        upd.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_factura_sv item) {
        sql="DELETE FROM D_factura_sv WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_factura_sv WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura_sv item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura_sv();

            item.corel=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.codigo_tipo_factura=dt.getInt(2);
            item.codigo_departamento=dt.getString(3);
            item.codigo_municipio=dt.getString(4);
            item.codigo_tipo_negocio=dt.getInt(5);

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

    public String addItemSql(clsClasses.clsD_factura_sv item) {

        ins.init("D_factura_sv");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_TIPO_FACTURA",item.codigo_tipo_factura);
        ins.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        ins.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        ins.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura_sv item) {

        upd.init("D_factura_sv");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_TIPO_FACTURA",item.codigo_tipo_factura);
        upd.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        upd.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        upd.add("CODIGO_TIPO_NEGOCIO",item.codigo_tipo_negocio);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

