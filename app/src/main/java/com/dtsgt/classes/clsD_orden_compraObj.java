package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_orden_compraObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_orden_compra";
    private String sql;
    public ArrayList<clsClasses.clsD_orden_compra> items= new ArrayList<clsClasses.clsD_orden_compra>();

    public clsD_orden_compraObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_orden_compra item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_orden_compra item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_orden_compra item) {
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

    public clsClasses.clsD_orden_compra first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_orden_compra item) {

        ins.init("D_orden_compra");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("ANULADO",item.anulado);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ESTATUS",item.estatus);
        ins.add("FECHA_EMISION",item.fecha_emision);
        ins.add("FECHA_RECEPCION",item.fecha_recepcion);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("SERIE",item.serie);
        ins.add("NUMERO",item.numero);
        ins.add("CORRELATIVO",item.correlativo);
        ins.add("TOTAL",item.total);
        ins.add("CREADO_EN_BOF",item.creado_en_bof);
        ins.add("ENVIADO",item.enviado);
        ins.add("NOTA",item.nota);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_orden_compra item) {

        upd.init("D_orden_compra");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("ANULADO",item.anulado);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ESTATUS",item.estatus);
        upd.add("FECHA_EMISION",item.fecha_emision);
        upd.add("FECHA_RECEPCION",item.fecha_recepcion);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("SERIE",item.serie);
        upd.add("NUMERO",item.numero);
        upd.add("CORRELATIVO",item.correlativo);
        upd.add("TOTAL",item.total);
        upd.add("CREADO_EN_BOF",item.creado_en_bof);
        upd.add("ENVIADO",item.enviado);
        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_orden_compra item) {
        sql="DELETE FROM D_orden_compra WHERE (CODIGO_COMPRA='"+item.codigo_compra+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_orden_compra WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_orden_compra item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_orden_compra();

            item.codigo_compra=dt.getString(0);
            item.codigo_empresa=dt.getInt(1);
            item.anulado=dt.getInt(2);
            item.codigo_sucursal=dt.getInt(3);
            item.estatus=dt.getString(4);
            item.fecha_emision=dt.getLong(5);
            item.fecha_recepcion=dt.getLong(6);
            item.codigo_usuario=dt.getInt(7);
            item.codigo_proveedor=dt.getInt(8);
            item.codigo_almacen=dt.getInt(9);
            item.serie=dt.getString(10);
            item.numero=dt.getInt(11);
            item.correlativo=dt.getInt(12);
            item.total=dt.getDouble(13);
            item.creado_en_bof=dt.getInt(14);
            item.enviado=dt.getInt(15);
            item.nota=dt.getString(16);

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

    public String addItemSql(clsClasses.clsD_orden_compra item) {

        ins.init("D_orden_compra");

        ins.add("CODIGO_COMPRA",item.codigo_compra);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("ANULADO",item.anulado);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("ESTATUS",item.estatus);
        ins.add("FECHA_EMISION",item.fecha_emision);
        ins.add("FECHA_RECEPCION",item.fecha_recepcion);
        ins.add("CODIGO_USUARIO",item.codigo_usuario);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("CODIGO_ALMACEN",item.codigo_almacen);
        ins.add("SERIE",item.serie);
        ins.add("NUMERO",item.numero);
        ins.add("CORRELATIVO",item.correlativo);
        ins.add("TOTAL",item.total);
        ins.add("CREADO_EN_BOF",item.creado_en_bof);
        ins.add("ENVIADO",item.enviado);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_orden_compra item) {

        upd.init("D_orden_compra");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("ANULADO",item.anulado);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("ESTATUS",item.estatus);
        upd.add("FECHA_EMISION",item.fecha_emision);
        upd.add("FECHA_RECEPCION",item.fecha_recepcion);
        upd.add("CODIGO_USUARIO",item.codigo_usuario);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("CODIGO_ALMACEN",item.codigo_almacen);
        upd.add("SERIE",item.serie);
        upd.add("NUMERO",item.numero);
        upd.add("CORRELATIVO",item.correlativo);
        upd.add("TOTAL",item.total);
        upd.add("CREADO_EN_BOF",item.creado_en_bof);
        upd.add("ENVIADO",item.enviado);
        upd.add("NOTA",item.nota);

        upd.Where("(CODIGO_COMPRA='"+item.codigo_compra+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

