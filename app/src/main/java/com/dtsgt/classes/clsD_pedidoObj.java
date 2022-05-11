package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_pedidoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_pedido";
    private String sql;
    public ArrayList<clsClasses.clsD_pedido> items= new ArrayList<clsClasses.clsD_pedido>();

    public clsD_pedidoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_pedido item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_pedido item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_pedido item) {
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

    public clsClasses.clsD_pedido first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_pedido item) {

        ins.init("D_pedido");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("FECHA_SISTEMA",item.fecha_sistema);
        ins.add("FECHA_PEDIDO",item.fecha_pedido);
        ins.add("FECHA_RECEPCION_SUC",item.fecha_recepcion_suc);
        ins.add("FECHA_SALIDA_SUC",item.fecha_salida_suc);
        ins.add("FECHA_ENTREGA",item.fecha_entrega);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("FIRMA_CLIENTE",item.firma_cliente);
        ins.add("CODIGO_DIRECCION",item.codigo_direccion);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("TOTAL",item.total);
        ins.add("CODIGO_ESTADO",item.codigo_estado);
        ins.add("CODIGO_USUARIO_CREO",item.codigo_usuario_creo);
        ins.add("CODIGO_USUARIO_PROCESO",item.codigo_usuario_proceso);
        ins.add("CODIGO_USUARIO_ENTREGO",item.codigo_usuario_entrego);
        ins.add("ANULADO",item.anulado);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_pedido item) {

        upd.init("D_pedido");

        upd.add("EMPRESA",item.empresa);
        upd.add("FECHA_SISTEMA",item.fecha_sistema);
        upd.add("FECHA_PEDIDO",item.fecha_pedido);
        upd.add("FECHA_RECEPCION_SUC",item.fecha_recepcion_suc);
        upd.add("FECHA_SALIDA_SUC",item.fecha_salida_suc);
        upd.add("FECHA_ENTREGA",item.fecha_entrega);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("FIRMA_CLIENTE",item.firma_cliente);
        upd.add("CODIGO_DIRECCION",item.codigo_direccion);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("TOTAL",item.total);
        upd.add("CODIGO_ESTADO",item.codigo_estado);
        upd.add("CODIGO_USUARIO_CREO",item.codigo_usuario_creo);
        upd.add("CODIGO_USUARIO_PROCESO",item.codigo_usuario_proceso);
        upd.add("CODIGO_USUARIO_ENTREGO",item.codigo_usuario_entrego);
        upd.add("ANULADO",item.anulado);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_pedido item) {
        sql="DELETE FROM D_pedido WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_pedido WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_pedido item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_pedido();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.fecha_sistema=dt.getLong(2);
            item.fecha_pedido=dt.getLong(3);
            item.fecha_recepcion_suc=dt.getLong(4);
            item.fecha_salida_suc=dt.getLong(5);
            item.fecha_entrega=dt.getLong(6);
            item.codigo_cliente=dt.getInt(7);
            item.firma_cliente=dt.getInt(8);
            item.codigo_direccion=dt.getInt(9);
            item.codigo_sucursal=dt.getInt(10);
            item.total=dt.getDouble(11);
            item.codigo_estado=dt.getInt(12);
            item.codigo_usuario_creo=dt.getInt(13);
            item.codigo_usuario_proceso=dt.getInt(14);
            item.codigo_usuario_entrego=dt.getInt(15);
            item.anulado=dt.getInt(16);

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

    public String addItemSql(clsClasses.clsD_pedido item) {

        ins.init("D_pedido");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("FECHA_SISTEMA",item.fecha_sistema);
        ins.add("FECHA_PEDIDO",item.fecha_pedido);
        ins.add("FECHA_RECEPCION_SUC",item.fecha_recepcion_suc);
        ins.add("FECHA_SALIDA_SUC",item.fecha_salida_suc);
        ins.add("FECHA_ENTREGA",item.fecha_entrega);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("FIRMA_CLIENTE",item.firma_cliente);
        ins.add("CODIGO_DIRECCION",item.codigo_direccion);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("TOTAL",item.total);
        ins.add("CODIGO_ESTADO",item.codigo_estado);
        ins.add("CODIGO_USUARIO_CREO",item.codigo_usuario_creo);
        ins.add("CODIGO_USUARIO_PROCESO",item.codigo_usuario_proceso);
        ins.add("CODIGO_USUARIO_ENTREGO",item.codigo_usuario_entrego);
        ins.add("ANULADO",item.anulado);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_pedido item) {

        upd.init("D_pedido");

        upd.add("EMPRESA",item.empresa);
        upd.add("FECHA_SISTEMA",item.fecha_sistema);
        upd.add("FECHA_PEDIDO",item.fecha_pedido);
        upd.add("FECHA_RECEPCION_SUC",item.fecha_recepcion_suc);
        upd.add("FECHA_SALIDA_SUC",item.fecha_salida_suc);
        upd.add("FECHA_ENTREGA",item.fecha_entrega);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("FIRMA_CLIENTE",item.firma_cliente);
        upd.add("CODIGO_DIRECCION",item.codigo_direccion);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("TOTAL",item.total);
        upd.add("CODIGO_ESTADO",item.codigo_estado);
        upd.add("CODIGO_USUARIO_CREO",item.codigo_usuario_creo);
        upd.add("CODIGO_USUARIO_PROCESO",item.codigo_usuario_proceso);
        upd.add("CODIGO_USUARIO_ENTREGO",item.codigo_usuario_entrego);
        upd.add("ANULADO",item.anulado);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}


