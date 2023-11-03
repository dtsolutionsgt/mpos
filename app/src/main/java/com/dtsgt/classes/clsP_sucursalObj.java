package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_sucursalObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_sucursal";
    private String sql;
    public ArrayList<clsClasses.clsP_sucursal> items= new ArrayList<clsClasses.clsP_sucursal>();

    public clsP_sucursalObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_sucursal item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_sucursal item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_sucursal item) {
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

    public clsClasses.clsP_sucursal first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_sucursal item) {

        ins.init("P_sucursal");

        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO",item.codigo);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("NOMBRE",item.nombre);
        ins.add("DIRECCION",item.direccion);
        ins.add("TELEFONO",item.telefono);
        ins.add("CORREO",item.correo);
        ins.add("NIT",item.nit);
        ins.add("TEXTO",item.texto);
        ins.add("ACTIVO",item.activo);
        ins.add("FEL_CODIGO_ESTABLECIMIENTO",item.fel_codigo_establecimiento);
        ins.add("FEL_USUARIO_FIRMA",item.fel_usuario_firma);
        ins.add("FEL_USUARIO_CERTIFICACION",item.fel_usuario_certificacion);
        ins.add("FEL_LLAVE_FIRMA",item.fel_llave_firma);
        ins.add("FEL_LLAVE_CERTIFICACION",item.fel_llave_certificacion);
        ins.add("FEL_AFILIACION_IVA",item.fel_afiliacion_iva);
        ins.add("CODIGO_POSTAL",item.codigo_postal);
        ins.add("CODIGO_ESCENARIO_ISR",item.codigo_escenario_isr);
        ins.add("CODIGO_ESCENARIO_IVA",item.codigo_escenario_iva);
        ins.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("FECHA_CONTR",item.fecha_contr);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_sucursal item) {

        upd.init("P_sucursal");

        upd.add("CODIGO",item.codigo);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("NOMBRE",item.nombre);
        upd.add("DIRECCION",item.direccion);
        upd.add("TELEFONO",item.telefono);
        upd.add("CORREO",item.correo);
        upd.add("NIT",item.nit);
        upd.add("TEXTO",item.texto);
        upd.add("ACTIVO",item.activo);
        upd.add("FEL_CODIGO_ESTABLECIMIENTO",item.fel_codigo_establecimiento);
        upd.add("FEL_USUARIO_FIRMA",item.fel_usuario_firma);
        upd.add("FEL_USUARIO_CERTIFICACION",item.fel_usuario_certificacion);
        upd.add("FEL_LLAVE_FIRMA",item.fel_llave_firma);
        upd.add("FEL_LLAVE_CERTIFICACION",item.fel_llave_certificacion);
        upd.add("FEL_AFILIACION_IVA",item.fel_afiliacion_iva);
        upd.add("CODIGO_POSTAL",item.codigo_postal);
        upd.add("CODIGO_ESCENARIO_ISR",item.codigo_escenario_isr);
        upd.add("CODIGO_ESCENARIO_IVA",item.codigo_escenario_iva);
        upd.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("FECHA_CONTR",item.fecha_contr);

        upd.Where("(CODIGO_SUCURSAL="+item.codigo_sucursal+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_sucursal item) {
        sql="DELETE FROM P_sucursal WHERE (CODIGO_SUCURSAL="+item.codigo_sucursal+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_sucursal WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_sucursal item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_sucursal();

            item.codigo_sucursal=dt.getInt(0);
            item.codigo=dt.getString(1);
            item.empresa=dt.getInt(2);
            item.codigo_nivel_precio=dt.getInt(3);
            item.descripcion=dt.getString(4);
            item.nombre=dt.getString(5);
            item.direccion=dt.getString(6);
            item.telefono=dt.getString(7);
            item.correo=dt.getString(8);
            item.nit=dt.getString(9);
            item.texto=dt.getString(10);
            item.activo=dt.getInt(11);
            item.fel_codigo_establecimiento=dt.getString(12);
            item.fel_usuario_firma=dt.getString(13);
            item.fel_llave_certificacion=dt.getString(14);
            item.fel_llave_firma=dt.getString(15);
            item.fel_afiliacion_iva=dt.getString(16);
            item.fel_usuario_certificacion=dt.getString(17);
            item.codigo_postal=dt.getString(18);
            item.codigo_escenario_isr=dt.getInt(19);
            item.codigo_escenario_iva=dt.getInt(20);
            item.codigo_municipio=dt.getString(21);
            item.codigo_proveedor=dt.getInt(22);
            item.fecha_contr=dt.getLong(23);

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

    public String addItemSql(clsClasses.clsP_sucursal item) {

        ins.init("P_sucursal");

        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO",item.codigo);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        ins.add("DESCRIPCION",item.descripcion);
        ins.add("NOMBRE",item.nombre);
        ins.add("DIRECCION",item.direccion);
        ins.add("TELEFONO",item.telefono);
        ins.add("CORREO",item.correo);
        ins.add("NIT",item.nit);
        ins.add("TEXTO",item.texto);
        ins.add("ACTIVO",item.activo);
        ins.add("FEL_CODIGO_ESTABLECIMIENTO",item.fel_codigo_establecimiento);
        ins.add("FEL_USUARIO_FIRMA",item.fel_usuario_firma);
        ins.add("FEL_USUARIO_CERTIFICACION",item.fel_usuario_certificacion);
        ins.add("FEL_LLAVE_FIRMA",item.fel_llave_firma);
        ins.add("FEL_LLAVE_CERTIFICACION",item.fel_llave_certificacion);
        ins.add("FEL_AFILIACION_IVA",item.fel_afiliacion_iva);
        ins.add("CODIGO_POSTAL",item.codigo_postal);
        ins.add("CODIGO_ESCENARIO_ISR",item.codigo_escenario_isr);
        ins.add("CODIGO_ESCENARIO_IVA",item.codigo_escenario_iva);
        ins.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("FECHA_CONTR",item.fecha_contr);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_sucursal item) {

        upd.init("P_sucursal");

        upd.add("CODIGO",item.codigo);
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_NIVEL_PRECIO",item.codigo_nivel_precio);
        upd.add("DESCRIPCION",item.descripcion);
        upd.add("NOMBRE",item.nombre);
        upd.add("DIRECCION",item.direccion);
        upd.add("TELEFONO",item.telefono);
        upd.add("CORREO",item.correo);
        upd.add("NIT",item.nit);
        upd.add("TEXTO",item.texto);
        upd.add("ACTIVO",item.activo);
        upd.add("FEL_CODIGO_ESTABLECIMIENTO",item.fel_codigo_establecimiento);
        upd.add("FEL_USUARIO_FIRMA",item.fel_usuario_firma);
        upd.add("FEL_USUARIO_CERTIFICACION",item.fel_usuario_certificacion);
        upd.add("FEL_LLAVE_FIRMA",item.fel_llave_firma);
        upd.add("FEL_LLAVE_CERTIFICACION",item.fel_llave_certificacion);
        upd.add("FEL_AFILIACION_IVA",item.fel_afiliacion_iva);
        upd.add("CODIGO_POSTAL",item.codigo_postal);
        upd.add("CODIGO_ESCENARIO_ISR",item.codigo_escenario_isr);
        upd.add("CODIGO_ESCENARIO_IVA",item.codigo_escenario_iva);
        upd.add("CODIGO_MUNICIPIO",item.codigo_municipio);
        upd.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        upd.add("FECHA_CONTR",item.fecha_contr);

        upd.Where("(CODIGO_SUCURSAL="+item.codigo_sucursal+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

