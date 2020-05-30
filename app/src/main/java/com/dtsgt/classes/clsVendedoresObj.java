package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

public class clsVendedoresObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    public SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM Vendedores";
    private String selDistinct="SELECT DISTINCT 0,CODIGO, EMPRESA, RUTA, NOMBRE, CLAVE, NIVEL, NIVELPRECIO, BODEGA, " +
                               "SUBBODEGA, ACTIVO FROM Vendedores";
    private String sql;
    public ArrayList<clsClasses.clsVendedores> items= new ArrayList<clsClasses.clsVendedores>();

    public clsVendedoresObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;

        mu = new MiscUtils(context);
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsVendedores item) {
        addItem(item);
    }

    public void update(clsClasses.clsVendedores item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsVendedores item) {
        deleteItem(item);
    }

    public void delete(String id) {
        deleteItem(id);
    }

    public void updateCajaVend(clsClasses.clsVendedores item) {
        actualizaCajaVend(item);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel+ " "+specstr);
    }

    public void fillDistinct(String specstr) {
        fillItems(selDistinct+ " "+specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsVendedores first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsVendedores item) {

        ins.init("Vendedores");

        ins.add("CODIGO",item.codigo);
        ins.add("RUTA",item.ruta);
        ins.add("NOMBRE",item.nombre);
        ins.add("CLAVE",item.clave);
        ins.add("NIVEL",item.nivel);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("BODEGA",item.bodega);
        ins.add("SUBBODEGA",item.subbodega);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_VENDEDOR",maxId());

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("CODIGO",item.codigo);
        upd.add("RUTA",item.ruta);
        upd.add("NOMBRE",item.nombre);
        upd.add("CLAVE",item.clave);
        upd.add("NIVEL",item.nivel);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("BODEGA",item.bodega);
        upd.add("SUBBODEGA",item.subbodega);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_VENDEDOR="+item.codigo_vendedor+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsVendedores item) {
        sql="DELETE FROM Vendedores WHERE (CODIGO_VENDEDOR="+item.codigo_vendedor+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM Vendedores WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsVendedores item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsVendedores();

            item.codigo_vendedor=dt.getInt(0);
            item.codigo=dt.getString(1);
            item.nombre=dt.getString(2);
            item.clave=dt.getString(3);
            item.ruta=dt.getInt(4);
            item.nivel=dt.getInt(5);
            item.nivelprecio=dt.getDouble(6);
            item.bodega=dt.getString(7);
            item.subbodega=dt.getString(8);
            item.activo=dt.getInt(9);

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

    public String addItemSql(clsClasses.clsVendedores item) {

        ins.init("Vendedores");

        ins.add("CODIGO",item.codigo);
        ins.add("RUTA",item.ruta);
        ins.add("NOMBRE",item.nombre);
        ins.add("CLAVE",item.clave);
        ins.add("NIVEL",item.nivel);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("BODEGA",item.bodega);
        ins.add("SUBBODEGA",item.subbodega);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("CODIGO",item.codigo);
        upd.add("RUTA",item.ruta);
        upd.add("NOMBRE",item.nombre);
        upd.add("CLAVE",item.clave);
        upd.add("NIVEL",item.nivel);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("BODEGA",item.bodega);
        upd.add("SUBBODEGA",item.subbodega);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_VENDEDOR="+item.codigo_vendedor+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    public void actualizaCajaVend(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("RUTA",item.ruta);
        upd.add("ACTIVO",item.activo);
        upd.add("NOMBRE",item.nombre);
        upd.add("CLAVE",item.clave);
        upd.add("NIVELPRECIO",item.nivelprecio);

        upd.Where("(CODIGO_VENDEDOR="+item.codigo_vendedor+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT IFNULL(MAX(CODIGO_VENDEDOR),1)+1 AS MAX FROM VENDEDORES";
            DT = Con.OpenDT(sql);

            if (DT != null){
                DT.moveToFirst();

                resultado=DT.getInt(0);
            }

        } catch (Exception e) {
            mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return resultado;
    }

    public int existVendCaja(String codvend, int codcaja){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT CODIGO_VENDEDOR FROM VENDEDORES WHERE CODIGO = '" + codvend + "' AND RUTA = " + codcaja ;
            DT = Con.OpenDT(sql);

            if (DT != null){
                DT.moveToFirst();

                resultado=DT.getInt(0);
            }

        } catch (Exception e) {
            mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return resultado;
    }
}

