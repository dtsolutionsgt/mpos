package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_vendedorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_vendedor";
    private String sql;
    public ArrayList<clsClasses.clsP_vendedor> items= new ArrayList<clsClasses.clsP_vendedor>();

    public clsP_vendedorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_vendedor item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_vendedor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_vendedor item) {
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

    public clsClasses.clsP_vendedor first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_vendedor item) {

        ins.init("P_vendedor");

        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CLAVE",item.clave);
        ins.add("RUTA",item.ruta);
        ins.add("NIVEL",item.nivel);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("BODEGA",item.bodega);
        ins.add("SUBBODEGA",item.subbodega);
        ins.add("COD_VEHICULO",item.cod_vehiculo);
        ins.add("LIQUIDANDO",item.liquidando);
        ins.add("ULTIMA_FECHA_LIQ",item.ultima_fecha_liq);
        ins.add("BLOQUEADO",item.bloqueado);
        ins.add("DEVOLUCION_SAP",item.devolucion_sap);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_vendedor item) {

        upd.init("P_vendedor");

        upd.add("NOMBRE",item.nombre);
        upd.add("CLAVE",item.clave);
        upd.add("RUTA",item.ruta);
        upd.add("NIVEL",item.nivel);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("BODEGA",item.bodega);
        upd.add("SUBBODEGA",item.subbodega);
        upd.add("COD_VEHICULO",item.cod_vehiculo);
        upd.add("LIQUIDANDO",item.liquidando);
        upd.add("ULTIMA_FECHA_LIQ",item.ultima_fecha_liq);
        upd.add("BLOQUEADO",item.bloqueado);
        upd.add("DEVOLUCION_SAP",item.devolucion_sap);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_vendedor item) {
        sql="DELETE FROM P_vendedor WHERE (CODIGO='"+item.codigo+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_vendedor WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_vendedor item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_vendedor();

            item.codigo=dt.getString(0);
            item.nombre=dt.getString(1);
            item.clave=dt.getString(2);
            item.ruta=dt.getString(3);
            item.nivel=dt.getInt(4);
            item.nivelprecio=dt.getInt(5);
            item.bodega=dt.getString(6);
            item.subbodega=dt.getString(7);
            item.cod_vehiculo=dt.getString(8);
            item.liquidando=dt.getString(9);
            item.ultima_fecha_liq=dt.getInt(10);
            item.bloqueado=dt.getInt(11);
            item.devolucion_sap=dt.getInt(12);
            item.activo=dt.getInt(13);

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

    public String addItemSql(clsClasses.clsP_vendedor item) {

        ins.init("P_vendedor");

        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CLAVE",item.clave);
        ins.add("RUTA",item.ruta);
        ins.add("NIVEL",item.nivel);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("BODEGA",item.bodega);
        ins.add("SUBBODEGA",item.subbodega);
        ins.add("COD_VEHICULO",item.cod_vehiculo);
        ins.add("LIQUIDANDO",item.liquidando);
        ins.add("ULTIMA_FECHA_LIQ",item.ultima_fecha_liq);
        ins.add("BLOQUEADO",item.bloqueado);
        ins.add("DEVOLUCION_SAP",item.devolucion_sap);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_vendedor item) {

        upd.init("P_vendedor");

        upd.add("NOMBRE",item.nombre);
        upd.add("CLAVE",item.clave);
        upd.add("RUTA",item.ruta);
        upd.add("NIVEL",item.nivel);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("BODEGA",item.bodega);
        upd.add("SUBBODEGA",item.subbodega);
        upd.add("COD_VEHICULO",item.cod_vehiculo);
        upd.add("LIQUIDANDO",item.liquidando);
        upd.add("ULTIMA_FECHA_LIQ",item.ultima_fecha_liq);
        upd.add("BLOQUEADO",item.bloqueado);
        upd.add("DEVOLUCION_SAP",item.devolucion_sap);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

