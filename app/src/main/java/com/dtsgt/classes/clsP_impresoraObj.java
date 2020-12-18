package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_impresoraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_impresora";
    private String sql;
    public ArrayList<clsClasses.clsP_impresora> items= new ArrayList<clsClasses.clsP_impresora>();

    public clsP_impresoraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_impresora item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_impresora item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_impresora item) {
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

    public clsClasses.clsP_impresora first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_impresora item) {

        ins.init("P_impresora");

        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("NUMERO_SERIE",item.numero_serie);
        ins.add("CODIGO_MARCA",item.codigo_marca);
        ins.add("CODIGO_MODELO",item.codigo_modelo);
        ins.add("TIPO_IMPRESORA",item.tipo_impresora);
        ins.add("MAC",item.mac);
        ins.add("IP",item.ip);
        ins.add("FECHA_AGR",item.fecha_agr);
        ins.add("IMPRESIONES",item.impresiones);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_impresora item) {

        upd.init("P_impresora");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("NUMERO_SERIE",item.numero_serie);
        upd.add("CODIGO_MARCA",item.codigo_marca);
        upd.add("CODIGO_MODELO",item.codigo_modelo);
        upd.add("TIPO_IMPRESORA",item.tipo_impresora);
        upd.add("MAC",item.mac);
        upd.add("IP",item.ip);
        upd.add("FECHA_AGR",item.fecha_agr);
        upd.add("IMPRESIONES",item.impresiones);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_IMPRESORA="+item.codigo_impresora+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_impresora item) {
        sql="DELETE FROM P_impresora WHERE (CODIGO_IMPRESORA="+item.codigo_impresora+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_impresora WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_impresora item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_impresora();

            item.codigo_impresora=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.numero_serie=dt.getString(4);
            item.codigo_marca=dt.getInt(5);
            item.codigo_modelo=dt.getInt(6);
            item.tipo_impresora=dt.getString(7);
            item.mac=dt.getString(8);
            item.ip=dt.getString(9);
            item.fecha_agr=dt.getInt(10);
            item.impresiones=dt.getInt(11);
            item.activo=dt.getInt(12);

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

    public String addItemSql(clsClasses.clsP_impresora item) {

        ins.init("P_impresora");

        ins.add("CODIGO_IMPRESORA",item.codigo_impresora);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("NUMERO_SERIE",item.numero_serie);
        ins.add("CODIGO_MARCA",item.codigo_marca);
        ins.add("CODIGO_MODELO",item.codigo_modelo);
        ins.add("TIPO_IMPRESORA",item.tipo_impresora);
        ins.add("MAC",item.mac);
        ins.add("IP",item.ip);
        ins.add("FECHA_AGR",item.fecha_agr);
        ins.add("IMPRESIONES",item.impresiones);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_impresora item) {

        upd.init("P_impresora");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("NUMERO_SERIE",item.numero_serie);
        upd.add("CODIGO_MARCA",item.codigo_marca);
        upd.add("CODIGO_MODELO",item.codigo_modelo);
        upd.add("TIPO_IMPRESORA",item.tipo_impresora);
        upd.add("MAC",item.mac);
        upd.add("IP",item.ip);
        upd.add("FECHA_AGR",item.fecha_agr);
        upd.add("IMPRESIONES",item.impresiones);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_IMPRESORA="+item.codigo_impresora+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

