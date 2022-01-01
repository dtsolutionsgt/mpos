package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_usuario_asistenciaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_usuario_asistencia";
    private String sql;
    public ArrayList<clsClasses.clsD_usuario_asistencia> items= new ArrayList<clsClasses.clsD_usuario_asistencia>();

    public clsD_usuario_asistenciaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_usuario_asistencia item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_usuario_asistencia item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_usuario_asistencia item) {
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

    public clsClasses.clsD_usuario_asistencia first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_usuario_asistencia item) {

        ins.init("D_usuario_asistencia");

        ins.add("CODIGO_ASISTENCIA",item.codigo_asistencia);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("FECHA",item.fecha);
        ins.add("INICIO",item.inicio);
        ins.add("FIN",item.fin);
        ins.add("BANDERA",item.bandera);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_usuario_asistencia item) {

        upd.init("D_usuario_asistencia");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("FECHA",item.fecha);
        upd.add("INICIO",item.inicio);
        upd.add("FIN",item.fin);
        upd.add("BANDERA",item.bandera);

        upd.Where("(CODIGO_ASISTENCIA="+item.codigo_asistencia+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_usuario_asistencia item) {
        sql="DELETE FROM D_usuario_asistencia WHERE (CODIGO_ASISTENCIA="+item.codigo_asistencia+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_usuario_asistencia WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_usuario_asistencia item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_usuario_asistencia();

            item.codigo_asistencia=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_vendedor=dt.getInt(3);
            item.fecha=dt.getLong(4);
            item.inicio=dt.getLong(5);
            item.fin=dt.getLong(6);
            item.bandera=dt.getInt(7);

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

    public String addItemSql(clsClasses.clsD_usuario_asistencia item) {

        ins.init("D_usuario_asistencia");
        //ins.add("CODIGO_ASISTENCIA",item.codigo_asistencia);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("INICIO",item.inicio);
        ins.add("FIN",item.fin);
        long ff=(long) (item.fecha/10000);
        ins.add("BANDERA",ff);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_usuario_asistencia item) {

        upd.init("D_usuario_asistencia");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("FECHA",item.fecha);
        upd.add("INICIO",item.inicio);
        upd.add("FIN",item.fin);
        upd.add("BANDERA",item.bandera);

        upd.Where("(CODIGO_ASISTENCIA="+item.codigo_asistencia+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

