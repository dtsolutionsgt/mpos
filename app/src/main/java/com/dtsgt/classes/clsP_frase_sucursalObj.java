package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_frase_sucursalObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_frase_sucursal";
    private String sql;
    public ArrayList<clsClasses.clsP_frase_sucursal> items= new ArrayList<clsClasses.clsP_frase_sucursal>();

    public clsP_frase_sucursalObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_frase_sucursal item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_frase_sucursal item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_frase_sucursal item) {
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

    public clsClasses.clsP_frase_sucursal first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_frase_sucursal item) {

        ins.init("P_frase_sucursal");

        ins.add("CODIGO_FRASE_SUCURSAL",item.codigo_frase_sucursal);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_FRASE_FEL",item.codigo_frase_fel);
        ins.add("TEXTO",item.texto);
        ins.add("ES_FRASE_ISR",item.es_frase_isr);
        ins.add("ES_FRASE_IVA",item.es_frase_iva);
        ins.add("USUARIO_AGREGO",item.usuario_agrego);
        ins.add("FECHA_AGREGADO",item.fecha_agregado);
        ins.add("USUARIO_MODIFICO",item.usuario_modifico);
        ins.add("FECHA_MODIFICADO",item.fecha_modificado);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_frase_sucursal item) {

        upd.init("P_frase_sucursal");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_FRASE_FEL",item.codigo_frase_fel);
        upd.add("TEXTO",item.texto);
        upd.add("ES_FRASE_ISR",item.es_frase_isr);
        upd.add("ES_FRASE_IVA",item.es_frase_iva);
        upd.add("USUARIO_AGREGO",item.usuario_agrego);
        upd.add("FECHA_AGREGADO",item.fecha_agregado);
        upd.add("USUARIO_MODIFICO",item.usuario_modifico);
        upd.add("FECHA_MODIFICADO",item.fecha_modificado);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_FRASE_SUCURSAL="+item.codigo_frase_sucursal+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_frase_sucursal item) {
        sql="DELETE FROM P_frase_sucursal WHERE (CODIGO_FRASE_SUCURSAL="+item.codigo_frase_sucursal+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_frase_sucursal WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_frase_sucursal item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_frase_sucursal();

            item.codigo_frase_sucursal=dt.getInt(0);
            item.codigo_empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_frase_fel=dt.getInt(3);
            item.texto=dt.getString(4);
            item.es_frase_isr=dt.getInt(5);
            item.es_frase_iva=dt.getInt(6);
            item.usuario_agrego=dt.getInt(7);
            item.fecha_agregado=dt.getInt(8);
            item.usuario_modifico=dt.getInt(9);
            item.fecha_modificado=dt.getInt(10);
            item.activo=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsP_frase_sucursal item) {

        ins.init("P_frase_sucursal");

        ins.add("CODIGO_FRASE_SUCURSAL",item.codigo_frase_sucursal);
        ins.add("CODIGO_EMPRESA",item.codigo_empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_FRASE_FEL",item.codigo_frase_fel);
        ins.add("TEXTO",item.texto);
        ins.add("ES_FRASE_ISR",item.es_frase_isr);
        ins.add("ES_FRASE_IVA",item.es_frase_iva);
        ins.add("USUARIO_AGREGO",item.usuario_agrego);
        ins.add("FECHA_AGREGADO",item.fecha_agregado);
        ins.add("USUARIO_MODIFICO",item.usuario_modifico);
        ins.add("FECHA_MODIFICADO",item.fecha_modificado);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_frase_sucursal item) {

        upd.init("P_frase_sucursal");

        upd.add("CODIGO_EMPRESA",item.codigo_empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_FRASE_FEL",item.codigo_frase_fel);
        upd.add("TEXTO",item.texto);
        upd.add("ES_FRASE_ISR",item.es_frase_isr);
        upd.add("ES_FRASE_IVA",item.es_frase_iva);
        upd.add("USUARIO_AGREGO",item.usuario_agrego);
        upd.add("FECHA_AGREGADO",item.fecha_agregado);
        upd.add("USUARIO_MODIFICO",item.usuario_modifico);
        upd.add("FECHA_MODIFICADO",item.fecha_modificado);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_FRASE_SUCURSAL="+item.codigo_frase_sucursal+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

