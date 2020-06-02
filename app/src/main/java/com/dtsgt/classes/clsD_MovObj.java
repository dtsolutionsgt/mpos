package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_MovObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_MOV";
    private String sql;
    public ArrayList<clsClasses.clsD_Mov> items= new ArrayList<clsClasses.clsD_Mov>();

    public clsD_MovObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_Mov item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_Mov item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_Mov item) {
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

    public clsClasses.clsD_Mov first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_Mov item) {

        ins.init("D_MOV");

        ins.add("COREL",item.COREL);
        ins.add("RUTA",item.RUTA);
        ins.add("ANULADO",item.ANULADO);
        ins.add("FECHA",item.FECHA);
        ins.add("TIPO",item.TIPO);
        ins.add("USUARIO",item.USUARIO);
        ins.add("REFERENCIA",item.REFERENCIA);
        ins.add("STATCOM",item.STATCOM);
        ins.add("IMPRES",item.IMPRES);
        ins.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        ins.add("CODIGO_PROVEEDOR",item.CODIGO_PROVEEDOR);
        ins.add("TOTAL",item.TOTAL);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_Mov item) {

        upd.init("D_MOV");

        upd.add("RUTA",item.RUTA);
        upd.add("ANULADO",item.ANULADO);
        upd.add("FECHA",item.FECHA);
        upd.add("TIPO",item.TIPO);
        upd.add("USUARIO",item.USUARIO);
        upd.add("REFERENCIA",item.REFERENCIA);
        upd.add("STATCOM",item.STATCOM);
        upd.add("IMPRES",item.IMPRES);
        upd.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        upd.add("CODIGO_PROVEEDOR",item.CODIGO_PROVEEDOR);

        upd.Where("(COREL="+item.COREL+")");
        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_Mov item) {
        sql="DELETE FROM D_MOV WHERE (COREL='"+item.COREL+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_MOV WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_Mov item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_Mov();

            item.COREL=dt.getString(0);
            item.RUTA=dt.getInt(1);
            item.ANULADO=dt.getInt(2);
            item.FECHA=dt.getInt(3);
            item.TIPO=dt.getString(4);
            item.USUARIO=dt.getInt(5);
            item.REFERENCIA=dt.getString(6);
            item.STATCOM=dt.getString(7);
            item.IMPRES=dt.getInt(8);
            item.CODIGOLIQUIDACION=dt.getInt(9);
            item.CODIGO_PROVEEDOR=dt.getInt(10);

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

    public String addItemSql(clsClasses.clsD_Mov item) {

        ins.init("D_MOV");

        ins.add("COREL",item.COREL);
        ins.add("RUTA",item.RUTA);
        ins.add("ANULADO",item.ANULADO);
        ins.add("FECHA",item.FECHA);
        ins.add("TIPO",item.TIPO);
        ins.add("USUARIO",item.USUARIO);
        ins.add("REFERENCIA",item.REFERENCIA);
        ins.add("STATCOM",item.STATCOM);
        ins.add("IMPRES",item.IMPRES);
        ins.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        ins.add("CODIGO_PROVEEDOR",item.CODIGO_PROVEEDOR);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_Mov item) {

        upd.init("D_MOV");

        upd.add("RUTA",item.RUTA);
        upd.add("ANULADO",item.ANULADO);
        upd.add("FECHA",item.FECHA);
        upd.add("TIPO",item.TIPO);
        upd.add("USUARIO",item.USUARIO);
        upd.add("REFERENCIA",item.REFERENCIA);
        upd.add("STATCOM",item.STATCOM);
        upd.add("IMPRES",item.IMPRES);
        upd.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        upd.add("CODIGO_PROVEEDOR",item.CODIGO_PROVEEDOR);

        upd.Where("(COREL="+item.COREL+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

