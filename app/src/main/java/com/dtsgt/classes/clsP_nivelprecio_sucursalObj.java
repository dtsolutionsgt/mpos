package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_nivelprecio_sucursalObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_nivelprecio_sucursal";
    private String sql;
    public ArrayList<clsClasses.clsP_nivelprecio_sucursal> items= new ArrayList<clsClasses.clsP_nivelprecio_sucursal>();

    public clsP_nivelprecio_sucursalObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_nivelprecio_sucursal item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_nivelprecio_sucursal item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_nivelprecio_sucursal item) {
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

    public clsClasses.clsP_nivelprecio_sucursal first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_nivelprecio_sucursal item) {

        ins.init("P_nivelprecio_sucursal");

        ins.add("CODIGO_NIVEL_SUCURSAL",item.codigo_nivel_sucursal);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        ins.add("USUARIO_AGREGO",item.usuario_agrego);
        ins.add("FECHA_AGREGADO",item.fecha_agregado);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_nivelprecio_sucursal item) {

        upd.init("P_nivelprecio_sucursal");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        upd.add("USUARIO_AGREGO",item.usuario_agrego);
        upd.add("FECHA_AGREGADO",item.fecha_agregado);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_NIVEL_SUCURSAL="+item.codigo_nivel_sucursal+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_nivelprecio_sucursal item) {
        sql="DELETE FROM P_nivelprecio_sucursal WHERE (CODIGO_NIVEL_SUCURSAL="+item.codigo_nivel_sucursal+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_nivelprecio_sucursal WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_nivelprecio_sucursal item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_nivelprecio_sucursal();

            item.codigo_nivel_sucursal=dt.getInt(0);
            item.codigo_empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_nivel_precio=dt.getInt(3);
            item.usuario_agrego=dt.getInt(4);
            item.fecha_agregado=dt.getLong(5);
            item.activo=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsP_nivelprecio_sucursal item) {

        ins.init("P_nivelprecio_sucursal");

        ins.add("CODIGO_NIVEL_SUCURSAL",item.codigo_nivel_sucursal);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        ins.add("USUARIO_AGREGO",item.usuario_agrego);
        ins.add("FECHA_AGREGADO",item.fecha_agregado);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_nivelprecio_sucursal item) {

        upd.init("P_nivelprecio_sucursal");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        upd.add("USUARIO_AGREGO",item.usuario_agrego);
        upd.add("FECHA_AGREGADO",item.fecha_agregado);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_NIVEL_SUCURSAL="+item.codigo_nivel_sucursal+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

