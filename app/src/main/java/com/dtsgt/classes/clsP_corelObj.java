package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_corelObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_corel";
    private String sql;
    public ArrayList<clsClasses.clsP_corel> items= new ArrayList<clsClasses.clsP_corel>();

    public clsP_corelObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_corel item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_corel item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_corel item) {
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

    public clsClasses.clsP_corel first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_corel item) {

        ins.init("P_corel");

        ins.add("CODIGO_COREL",item.codigo_corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("RESOL",item.resol);
        ins.add("SERIE",item.serie);
        ins.add("CORELINI",item.corelini);
        ins.add("CORELFIN",item.corelfin);
        ins.add("CORELULT",item.corelult);
        ins.add("FECHARES",item.fechares);
        ins.add("RUTA",item.ruta);
        ins.add("ACTIVA",item.activa);
        ins.add("HANDHELD",item.handheld);
        ins.add("FECHAVIG",item.fechavig);
        ins.add("RESGUARDO",item.resguardo);
        ins.add("VALOR1",item.valor1);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_corel item) {

        upd.init("P_COREL");
        upd.add("EMPRESA",item.empresa);
        upd.add("RESOL",item.resol);
        upd.add("SERIE",item.serie);
        upd.add("CORELINI",item.corelini);
        upd.add("CORELFIN",item.corelfin);
        upd.add("CORELULT",item.corelult);
        upd.add("FECHARES",item.fechares);
        upd.add("RUTA",item.ruta);
        upd.add("ACTIVA",item.activa);
        upd.add("HANDHELD",item.handheld);
        upd.add("FECHAVIG",item.fechavig);
        upd.add("RESGUARDO",item.resguardo);
        upd.add("VALOR1",item.valor1);

        upd.Where("(CODIGO_COREL="+item.codigo_corel+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_corel item) {
        sql="DELETE FROM P_corel WHERE (CODIGO_COREL="+item.codigo_corel+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_corel WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_corel item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_corel();

            item.codigo_corel=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.resol=dt.getString(2);
            item.serie=dt.getString(3);
            item.corelini=dt.getInt(4);
            item.corelfin=dt.getInt(5);
            item.corelult=dt.getInt(6);
            item.fechares=dt.getLong(7);
            item.ruta=dt.getInt(8);
            item.activa=dt.getInt(9);
            item.handheld=dt.getString(10);
            item.fechavig=dt.getLong(11);
            item.resguardo=dt.getInt(12);
            item.valor1=dt.getInt(13);

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

    public String addItemSql(clsClasses.clsP_corel item) {

        ins.init("P_corel");

        ins.add("CODIGO_COREL",item.codigo_corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("RESOL",item.resol);
        ins.add("SERIE",item.serie);
        ins.add("CORELINI",item.corelini);
        ins.add("CORELFIN",item.corelfin);
        ins.add("CORELULT",item.corelult);
        ins.add("FECHARES",item.fechares);
        ins.add("RUTA",item.ruta);
        ins.add("ACTIVA",item.activa);
        ins.add("HANDHELD",item.handheld);
        ins.add("FECHAVIG",item.fechavig);
        ins.add("RESGUARDO",item.resguardo);
        ins.add("VALOR1",item.valor1);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_corel item) {

        upd.init("P_corel");

        upd.add("EMPRESA",item.empresa);
        upd.add("RESOL",item.resol);
        upd.add("SERIE",item.serie);
        upd.add("CORELINI",item.corelini);
        upd.add("CORELFIN",item.corelfin);
        upd.add("CORELULT",item.corelult);
        upd.add("FECHARES",item.fechares);
        upd.add("RUTA",item.ruta);
        upd.add("ACTIVA",item.activa);
        upd.add("HANDHELD",item.handheld);
        upd.add("FECHAVIG",item.fechavig);
        upd.add("RESGUARDO",item.resguardo);
        upd.add("VALOR1",item.valor1);

        upd.Where("(CODIGO_COREL="+item.codigo_corel+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

