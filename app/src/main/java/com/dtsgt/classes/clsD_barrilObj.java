package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_barrilObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_barril";
    private String sql;
    public ArrayList<clsClasses.clsD_barril> items= new ArrayList<clsClasses.clsD_barril>();

    public clsD_barrilObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_barril item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_barril item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_barril item) {
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

    public clsClasses.clsD_barril first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_barril item) {

        ins.init("D_barril");
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("LOTE",item.lote);
        ins.add("FECHA_INICIO",item.fecha_inicio);
        ins.add("FECHA_CIERRE",item.fecha_cierre);
        ins.add("FECHA_VENCE",item.fecha_vence);
        ins.add("FECHA_ENTRADA",item.fecha_entrada);
        ins.add("FECHA_SALIDA",item.fecha_salida);
        ins.add("USUARIO_INICIO",item.usuario_inicio);
        ins.add("USUARIO_CIERRE",item.usuario_cierre);
        ins.add("STATCOM",item.statcom);
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_barril item) {

        upd.init("D_barril");
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);
        upd.add("ACTIVO",item.activo);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("LOTE",item.lote);
        upd.add("FECHA_INICIO",item.fecha_inicio);
        upd.add("FECHA_CIERRE",item.fecha_cierre);
        upd.add("FECHA_VENCE",item.fecha_vence);
        upd.add("FECHA_ENTRADA",item.fecha_entrada);
        upd.add("FECHA_SALIDA",item.fecha_salida);
        upd.add("USUARIO_INICIO",item.usuario_inicio);
        upd.add("USUARIO_CIERRE",item.usuario_cierre);
        upd.add("STATCOM",item.statcom);
        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");
        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_barril item) {
        sql="DELETE FROM D_barril WHERE (CODIGO_BARRIL='"+item.codigo_barril+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_barril WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsD_barril item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();

        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_barril();
            item.codigo_barril=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_tipo=dt.getInt(3);
            item.codigo_interno=dt.getString(4);
            item.activo=dt.getInt(5);
            item.codigo_producto=dt.getInt(6);
            item.lote=dt.getString(7);
            item.fecha_inicio=dt.getLong(8);
            item.fecha_cierre=dt.getLong(9);
            item.fecha_vence=dt.getLong(10);
            item.fecha_entrada=dt.getLong(11);
            item.fecha_salida=dt.getLong(12);
            item.usuario_inicio=dt.getInt(13);
            item.usuario_cierre=dt.getInt(14);
            item.statcom=dt.getInt(15);
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

    public String addItemSql(clsClasses.clsD_barril item) {

        ins.init("D_barril");
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("LOTE",item.lote);
        ins.add("FECHA_INICIO",item.fecha_inicio);
        ins.add("FECHA_CIERRE",item.fecha_cierre);
        ins.add("FECHA_VENCE",item.fecha_vence);
        ins.add("FECHA_ENTRADA",item.fecha_entrada);
        ins.add("FECHA_SALIDA",item.fecha_salida);
        ins.add("USUARIO_INICIO",item.usuario_inicio);
        ins.add("USUARIO_CIERRE",item.usuario_cierre);
        ins.add("STATCOM",item.statcom);
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_barril item) {

        upd.init("D_barril");
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);
        upd.add("ACTIVO",item.activo);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("LOTE",item.lote);
        upd.add("FECHA_INICIO",item.fecha_inicio);
        upd.add("FECHA_CIERRE",item.fecha_cierre);
        upd.add("FECHA_VENCE",item.fecha_vence);
        upd.add("FECHA_ENTRADA",item.fecha_entrada);
        upd.add("FECHA_SALIDA",item.fecha_salida);
        upd.add("USUARIO_INICIO",item.usuario_inicio);
        upd.add("USUARIO_CIERRE",item.usuario_cierre);
        upd.add("STATCOM",item.statcom);
        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");
        return upd.sql();

    }

}

