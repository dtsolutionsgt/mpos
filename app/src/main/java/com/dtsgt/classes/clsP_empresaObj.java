package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_empresaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_empresa";
    private String sql;
    public ArrayList<clsClasses.clsP_empresa> items= new ArrayList<clsClasses.clsP_empresa>();

    public clsP_empresaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_empresa item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_empresa item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_empresa item) {
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

    public clsClasses.clsP_empresa first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_empresa item) {

        ins.init("P_empresa");

        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("COL_IMP",item.col_imp);
        ins.add("LOGO",item.logo);
        ins.add("RAZON_SOCIAL",item.razon_social);
        ins.add("IDENTIFICACION_TRIBUTARIA",item.identificacion_tributaria);
        ins.add("TELEFONO",item.telefono);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("NOMBRE_CONTACTO",item.nombre_contacto);
        ins.add("APELLIDO_CONTACTO",item.apellido_contacto);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);
        ins.add("CODIGO_ACTIVACION",item.codigo_activacion);
        ins.add("COD_CANT_EMP",item.cod_cant_emp);
        ins.add("CANTIDAD_PUNTOS_VENTA",item.cantidad_puntos_venta);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_empresa item) {

        upd.init("P_empresa");

        upd.add("NOMBRE",item.nombre);
        upd.add("COL_IMP",item.col_imp);
        upd.add("LOGO",item.logo);
        upd.add("RAZON_SOCIAL",item.razon_social);
        upd.add("IDENTIFICACION_TRIBUTARIA",item.identificacion_tributaria);
        upd.add("TELEFONO",item.telefono);
        upd.add("COD_PAIS",item.cod_pais);
        upd.add("NOMBRE_CONTACTO",item.nombre_contacto);
        upd.add("APELLIDO_CONTACTO",item.apellido_contacto);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);
        upd.add("CODIGO_ACTIVACION",item.codigo_activacion);
        upd.add("COD_CANT_EMP",item.cod_cant_emp);
        upd.add("CANTIDAD_PUNTOS_VENTA",item.cantidad_puntos_venta);

        upd.Where("(EMPRESA="+item.empresa+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_empresa item) {
        sql="DELETE FROM P_empresa WHERE (EMPRESA="+item.empresa+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_empresa WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_empresa item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_empresa();

            item.empresa=dt.getInt(0);
            item.nombre=dt.getString(1);
            item.col_imp=dt.getInt(2);
            item.logo=dt.getString(3);
            item.razon_social=dt.getString(4);
            item.identificacion_tributaria=dt.getString(5);
            item.telefono=dt.getString(6);
            item.cod_pais=dt.getString(7);
            item.nombre_contacto=dt.getString(8);
            item.apellido_contacto=dt.getString(9);
            item.direccion=dt.getString(10);
            item.correo=dt.getString(11);
            item.codigo_activacion=dt.getString(12);
            item.cod_cant_emp=dt.getInt(13);
            item.cantidad_puntos_venta=dt.getInt(14);

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

    public String addItemSql(clsClasses.clsP_empresa item) {

        ins.init("P_empresa");

        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("COL_IMP",item.col_imp);
        ins.add("LOGO",item.logo);
        ins.add("RAZON_SOCIAL",item.razon_social);
        ins.add("IDENTIFICACION_TRIBUTARIA",item.identificacion_tributaria);
        ins.add("TELEFONO",item.telefono);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("NOMBRE_CONTACTO",item.nombre_contacto);
        ins.add("APELLIDO_CONTACTO",item.apellido_contacto);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);
        ins.add("CODIGO_ACTIVACION",item.codigo_activacion);
        ins.add("COD_CANT_EMP",item.cod_cant_emp);
        ins.add("CANTIDAD_PUNTOS_VENTA",item.cantidad_puntos_venta);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_empresa item) {

        upd.init("P_empresa");

        upd.add("NOMBRE",item.nombre);
        upd.add("COL_IMP",item.col_imp);
        upd.add("LOGO",item.logo);
        upd.add("RAZON_SOCIAL",item.razon_social);
        upd.add("IDENTIFICACION_TRIBUTARIA",item.identificacion_tributaria);
        upd.add("TELEFONO",item.telefono);
        upd.add("COD_PAIS",item.cod_pais);
        upd.add("NOMBRE_CONTACTO",item.nombre_contacto);
        upd.add("APELLIDO_CONTACTO",item.apellido_contacto);
        upd.add("DIRECCION",item.direccion);
        upd.add("CORREO",item.correo);
        upd.add("CODIGO_ACTIVACION",item.codigo_activacion);
        upd.add("COD_CANT_EMP",item.cod_cant_emp);
        upd.add("CANTIDAD_PUNTOS_VENTA",item.cantidad_puntos_venta);

        upd.Where("(EMPRESA="+item.empresa+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

