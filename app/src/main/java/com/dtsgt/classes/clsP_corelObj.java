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

    public clsClasses.clsP_corel first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_corel item) {

        ins.init("P_corel");

        ins.add("RESOL",item.resol);
        ins.add("SERIE",item.serie);
        ins.add("CORELINI",item.corelini);
        ins.add("CORELFIN",item.corelfin);
        ins.add("CORELULT",item.corelult);
        ins.add("FECHARES",item.fechares);
        ins.add("RUTA",item.ruta);
        ins.add("FECHAVIG",item.fechavig);
        ins.add("RESGUARDO",item.resguardo);
        ins.add("VALOR1",item.valor1);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_corel item) {
        /*
        upd.init("P_corel");

        upd.add("RESOL",item.resol);
        upd.add("SERIE",item.serie);
        upd.add("CORELINI",item.corelini);
        upd.add("CORELFIN",item.corelfin);

        db.execSQL(upd.sql());
        */

        String ss="UPDATE P_corel SET  RESOL='"+item.resol+"',SERIE='"+item.serie+"'," +
                  "CORELINI="+item.corelini+",CORELFIN="+item.corelfin;
        db.execSQL(ss);

    }

    private void deleteItem(clsClasses.clsP_corel item) {
        sql="DELETE FROM P_corel WHERE (RESOL='"+item.resol+"') AND (SERIE='"+item.serie+"') AND (CORELINI="+item.corelini+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_corel WHERE id='" + id+"'";
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

            item.resol=dt.getString(0);
            item.serie=dt.getString(1);
            item.corelini=dt.getInt(2);
            item.corelfin=dt.getInt(3);
            item.corelult=dt.getInt(4);
            item.fechares=dt.getInt(5);
            item.ruta=dt.getString(6);
            item.fechavig=dt.getInt(7);
            item.resguardo=dt.getInt(8);
            item.valor1=dt.getInt(9);

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

        ins.add("RESOL",item.resol);
        ins.add("SERIE",item.serie);
        ins.add("CORELINI",item.corelini);
        ins.add("CORELFIN",item.corelfin);
        ins.add("CORELULT",item.corelult);
        ins.add("FECHARES",item.fechares);
        ins.add("RUTA",item.ruta);
        ins.add("FECHAVIG",item.fechavig);
        ins.add("RESGUARDO",item.resguardo);
        ins.add("VALOR1",item.valor1);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_corel item) {

        upd.init("P_corel");

        upd.add("CORELFIN",item.corelfin);
        upd.add("CORELULT",item.corelult);
        upd.add("FECHARES",item.fechares);
        upd.add("RUTA",item.ruta);
        upd.add("FECHAVIG",item.fechavig);
        upd.add("RESGUARDO",item.resguardo);
        upd.add("VALOR1",item.valor1);

        upd.Where("(RESOL='"+item.resol+"') AND (SERIE='"+item.serie+"') AND (CORELINI="+item.corelini+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

