package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_factura_anulacionObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura_anulacion";
    private String sql;
    public ArrayList<clsClasses.clsD_factura_anulacion> items= new ArrayList<clsClasses.clsD_factura_anulacion>();

    public clsD_factura_anulacionObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura_anulacion item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura_anulacion item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura_anulacion item) {
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

    public clsClasses.clsD_factura_anulacion first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_factura_anulacion item) {

        ins.init("D_factura_anulacion");

        ins.add("CODIGO_FACTURA_ANULACION",item.codigo_factura_anulacion);
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_PAIS",item.codigo_pais);
        ins.add("FECHA_ANULACION",item.fecha_anulacion);
        ins.add("SV_UUID",item.sv_uuid);
        ins.add("SV_CODIGO_GENERACION",item.sv_codigo_generacion);
        ins.add("SV_SELLO_RECEPCION",item.sv_sello_recepcion);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura_anulacion item) {

        upd.init("D_factura_anulacion");

        upd.add("EMPRESA",item.empresa);
        upd.add("COREL",item.corel);
        upd.add("CODIGO_PAIS",item.codigo_pais);
        upd.add("FECHA_ANULACION",item.fecha_anulacion);
        upd.add("SV_UUID",item.sv_uuid);
        upd.add("SV_CODIGO_GENERACION",item.sv_codigo_generacion);
        upd.add("SV_SELLO_RECEPCION",item.sv_sello_recepcion);

        upd.Where("(CODIGO_FACTURA_ANULACION="+item.codigo_factura_anulacion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_factura_anulacion item) {
        sql="DELETE FROM D_factura_anulacion WHERE (CODIGO_FACTURA_ANULACION="+item.codigo_factura_anulacion+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_factura_anulacion WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura_anulacion item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura_anulacion();

            item.codigo_factura_anulacion=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.corel=dt.getString(2);
            item.codigo_pais=dt.getString(3);
            item.fecha_anulacion=dt.getLong(4);
            item.sv_uuid=dt.getString(5);
            item.sv_codigo_generacion=dt.getString(6);
            item.sv_sello_recepcion=dt.getString(7);

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

    public String addItemSql(clsClasses.clsD_factura_anulacion item) {

        ins.init("D_factura_anulacion");

        ins.add("CODIGO_FACTURA_ANULACION",item.codigo_factura_anulacion);
        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("CODIGO_PAIS",item.codigo_pais);
        ins.add("FECHA_ANULACION",item.fecha_anulacion);
        ins.add("SV_UUID",item.sv_uuid);
        ins.add("SV_CODIGO_GENERACION",item.sv_codigo_generacion);
        ins.add("SV_SELLO_RECEPCION",item.sv_sello_recepcion);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura_anulacion item) {

        upd.init("D_factura_anulacion");

        upd.add("EMPRESA",item.empresa);
        upd.add("COREL",item.corel);
        upd.add("CODIGO_PAIS",item.codigo_pais);
        upd.add("FECHA_ANULACION",item.fecha_anulacion);
        upd.add("SV_UUID",item.sv_uuid);
        upd.add("SV_CODIGO_GENERACION",item.sv_codigo_generacion);
        upd.add("SV_SELLO_RECEPCION",item.sv_sello_recepcion);

        upd.Where("(CODIGO_FACTURA_ANULACION="+item.codigo_factura_anulacion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}


