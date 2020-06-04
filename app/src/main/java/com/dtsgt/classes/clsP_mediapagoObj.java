package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_mediapagoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_mediapago";
    private String sql;
    public ArrayList<clsClasses.clsP_mediapago> items= new ArrayList<clsClasses.clsP_mediapago>();

    public clsP_mediapagoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_mediapago item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_mediapago item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_mediapago item) {
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

    public clsClasses.clsP_mediapago first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_mediapago item) {

        ins.init("P_mediapago");

        ins.add("CODIGO",item.codigo);
        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("NIVEL",item.nivel);
        ins.add("PORCOBRO",item.porcobro);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_mediapago item) {

        upd.init("P_mediapago");

        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("NIVEL",item.nivel);
        upd.add("PORCOBRO",item.porcobro);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_mediapago item) {
        sql="DELETE FROM P_mediapago WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_mediapago WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_mediapago item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_mediapago();

            item.codigo=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.nombre=dt.getString(2);
            item.activo=dt.getInt(3);
            item.nivel=dt.getInt(4);
            item.porcobro=dt.getInt(5);

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

    public String addItemSql(clsClasses.clsP_mediapago item) {

        ins.init("P_mediapago");

        ins.add("CODIGO",item.codigo);
        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);
        ins.add("NIVEL",item.nivel);
        ins.add("PORCOBRO",item.porcobro);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_mediapago item) {

        upd.init("P_mediapago");

        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);
        upd.add("NIVEL",item.nivel);
        upd.add("PORCOBRO",item.porcobro);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

