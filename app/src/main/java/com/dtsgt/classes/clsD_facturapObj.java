package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturapObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturap";
    private String sql;
    public ArrayList<clsClasses.clsD_facturap> items= new ArrayList<clsClasses.clsD_facturap>();

    public clsD_facturapObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturap item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturap item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturap item) {
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

    public clsClasses.clsD_facturap first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturap item) {

        ins.init("D_facturap");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("ANULADO",item.anulado);
        ins.add("CODPAGO",item.codpago);
        ins.add("TIPO",item.tipo);
        ins.add("VALOR",item.valor);
        ins.add("DESC1",item.desc1);
        ins.add("DESC2",item.desc2);
        ins.add("DESC3",item.desc3);
        ins.add("DEPOS",item.depos);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturap item) {

        upd.init("D_facturap");

        upd.add("ANULADO",item.anulado);
        upd.add("CODPAGO",item.codpago);
        upd.add("TIPO",item.tipo);
        upd.add("VALOR",item.valor);
        upd.add("DESC1",item.desc1);
        upd.add("DESC2",item.desc2);
        upd.add("DESC3",item.desc3);
        upd.add("DEPOS",item.depos);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturap item) {
        sql="DELETE FROM D_facturap WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_facturap WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturap item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturap();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.item=dt.getInt(2);
            item.anulado=dt.getString(3);
            item.codpago=dt.getInt(4);
            item.tipo=dt.getString(5);
            item.valor=dt.getDouble(6);
            item.desc1=dt.getString(7);
            item.desc2=dt.getString(8);
            item.desc3=dt.getString(9);
            item.depos=dt.getString(10);

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

    public String addItemSql(clsClasses.clsD_facturap item) {

        ins.init("D_facturap");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ITEM",item.item);
        ins.add("ANULADO",item.anulado);
        ins.add("CODPAGO",item.codpago);
        ins.add("TIPO",item.tipo);
        ins.add("VALOR",item.valor);
        ins.add("DESC1",item.desc1);
        ins.add("DESC2",item.desc2);
        ins.add("DESC3",item.desc3);
        ins.add("DEPOS",item.depos);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturap item) {

        upd.init("D_facturap");

        upd.add("ANULADO",item.anulado);
        upd.add("CODPAGO",item.codpago);
        upd.add("TIPO",item.tipo);
        upd.add("VALOR",item.valor);
        upd.add("DESC1",item.desc1);
        upd.add("DESC2",item.desc2);
        upd.add("DESC3",item.desc3);
        upd.add("DEPOS",item.depos);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"') AND (ITEM="+item.item+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

