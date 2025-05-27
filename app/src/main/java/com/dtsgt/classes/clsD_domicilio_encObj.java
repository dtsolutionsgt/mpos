package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_domicilio_encObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_domicilio_enc";
    private String sql;
    public ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();

    public clsD_domicilio_encObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_domicilio_enc item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_domicilio_enc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_domicilio_enc item) {
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

    public clsClasses.clsD_domicilio_enc first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_domicilio_enc item) {

        ins.init("D_domicilio_enc");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHA_HORA",item.fecha_hora);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("CLIENTE_NOMBRE",item.cliente_nombre);
        ins.add("DIRECCION_TEXT",item.direccion_text);
        ins.add("TEXTO",item.texto);
        ins.add("TELEFONO",item.telefono);
        ins.add("CAMBIO",item.cambio);
        ins.add("FORMA_PAGO",item.forma_pago);
        ins.add("NIT",item.nit);
        ins.add("IDDIRECCION",item.iddireccion);
        ins.add("IMPORTADO",item.importado);
        ins.add("ESTADO",item.estado);
        ins.add("IDORDEN",item.idorden);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_domicilio_enc item) {

        upd.init("D_domicilio_enc");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("FECHA_HORA",item.fecha_hora);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("CLIENTE_NOMBRE",item.cliente_nombre);
        upd.add("DIRECCION_TEXT",item.direccion_text);
        upd.add("TEXTO",item.texto);
        upd.add("TELEFONO",item.telefono);
        upd.add("CAMBIO",item.cambio);
        upd.add("FORMA_PAGO",item.forma_pago);
        upd.add("NIT",item.nit);
        upd.add("IDDIRECCION",item.iddireccion);
        upd.add("IMPORTADO",item.importado);
        upd.add("ESTADO",item.estado);
        upd.add("IDORDEN",item.idorden);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_domicilio_enc item) {
        sql="DELETE FROM D_domicilio_enc WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_domicilio_enc WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_domicilio_enc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_domicilio_enc();

            item.corel=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.fecha_hora=dt.getLong(3);
            item.vendedor=dt.getInt(4);
            item.codigo_cliente=dt.getInt(5);
            item.cliente_nombre=dt.getString(6);
            item.direccion_text=dt.getString(7);
            item.texto=dt.getString(8);
            item.telefono=dt.getString(9);
            item.cambio=dt.getInt(10);
            item.forma_pago=dt.getInt(11);
            item.nit=dt.getString(12);
            item.iddireccion=dt.getInt(13);
            item.importado=dt.getInt(14);
            item.estado=dt.getInt(15);
            item.idorden=dt.getInt(16);

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

    public String addItemSql(clsClasses.clsD_domicilio_enc item) {

        ins.init("D_domicilio_enc");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHA_HORA",item.fecha_hora);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("CLIENTE_NOMBRE",item.cliente_nombre);
        ins.add("DIRECCION_TEXT",item.direccion_text);
        ins.add("TEXTO",item.texto);
        ins.add("TELEFONO",item.telefono);
        ins.add("CAMBIO",item.cambio);
        ins.add("FORMA_PAGO",item.forma_pago);
        ins.add("NIT",item.nit);
        ins.add("IDDIRECCION",item.iddireccion);
        ins.add("IMPORTADO",item.importado);
        ins.add("ESTADO",item.estado);
        ins.add("IDORDEN",item.idorden);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_domicilio_enc item) {

        upd.init("D_domicilio_enc");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("FECHA_HORA",item.fecha_hora);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("CLIENTE_NOMBRE",item.cliente_nombre);
        upd.add("DIRECCION_TEXT",item.direccion_text);
        upd.add("TEXTO",item.texto);
        upd.add("TELEFONO",item.telefono);
        upd.add("CAMBIO",item.cambio);
        upd.add("FORMA_PAGO",item.forma_pago);
        upd.add("NIT",item.nit);
        upd.add("IDDIRECCION",item.iddireccion);
        upd.add("IMPORTADO",item.importado);
        upd.add("ESTADO",item.estado);
        upd.add("IDORDEN",item.idorden);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();


    }

    //endregion
}

