package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_MovDObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_MOVD";
    private String sql;
    public ArrayList<clsClasses.clsD_MovD> items= new ArrayList<clsClasses.clsD_MovD>();

    public clsD_MovDObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_MovD item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_MovD item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_MovD item) {
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

    public clsClasses.clsD_MovD first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_MovD item) {

        ins.init("D_MOVD");

        ins.add("COREL",item.COREL);
        ins.add("PRODUCTO",item.PRODUCTO);
        ins.add("CANT",item.CANT);
        ins.add("CANTM",item.CANTM);
        ins.add("PESO",item.PESO);
        ins.add("PESOM",item.PESOM);
        ins.add("LOTE",item.LOTE);
        ins.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        ins.add("UNIDADMEDIDA",item.UNIDADMEDIDA);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_MovD item) {

        upd.init("D_MOVD");

        upd.add("COREL",item.COREL);
        upd.add("PRODUCTO",item.PRODUCTO);
        upd.add("CANT",item.CANT);
        upd.add("CANTM",item.CANTM);
        upd.add("PESO",item.PESO);
        upd.add("PESOM",item.PESOM);
        upd.add("LOTE",item.LOTE);
        upd.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        upd.add("UNIDADMEDIDA",item.UNIDADMEDIDA);

        upd.Where("(CORELDET="+item.CORELDET+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_MovD item) {
        sql="DELETE FROM D_MOVD WHERE (CORELDET="+item.CORELDET+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_MOVD WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_MovD item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_MovD();

            item.COREL=dt.getString(0);
            item.PRODUCTO=dt.getInt(1);
            item.CANT=dt.getDouble(2);
            item.CANTM=dt.getDouble(3);
            item.PESO=dt.getDouble(4);
            item.PESOM=dt.getDouble(5);
            item.LOTE=dt.getString(6);
            item.CODIGOLIQUIDACION=dt.getInt(7);
            item.UNIDADMEDIDA=dt.getString(8);

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

    public String addItemSql(clsClasses.clsD_MovD item) {

        ins.init("D_MOVDD");

        ins.add("COREL",item.COREL);
        ins.add("PRODUCTO",item.PRODUCTO);
        ins.add("CANT",item.CANT);
        ins.add("CANTM",item.CANTM);
        ins.add("PESO",item.PESO);
        ins.add("PESOM",item.PESOM);
        ins.add("LOTE",item.LOTE);
        ins.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        ins.add("UNIDADMEDIDA",item.UNIDADMEDIDA);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_MovD item) {

        upd.init("D_MOVD");

        upd.add("COREL",item.COREL);
        upd.add("PRODUCTO",item.PRODUCTO);
        upd.add("CANT",item.CANT);
        upd.add("CANTM",item.CANTM);
        upd.add("PESO",item.PESO);
        upd.add("PESOM",item.PESOM);
        upd.add("LOTE",item.LOTE);
        upd.add("CODIGOLIQUIDACION",item.CODIGOLIQUIDACION);
        upd.add("UNIDADMEDIDA",item.UNIDADMEDIDA);

        upd.Where("(CORELDET="+item.CORELDET+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

