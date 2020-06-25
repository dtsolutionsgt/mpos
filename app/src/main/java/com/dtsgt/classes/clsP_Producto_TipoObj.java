package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsP_Producto_TipoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM P_producto_tipo";
    private String sql;
    public ArrayList<clsClasses.clsP_Producto_Tipo> items= new ArrayList<clsClasses.clsP_Producto_Tipo>();

    public clsP_Producto_TipoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {

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

    public void add(clsClasses.clsP_Producto_Tipo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_Producto_Tipo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_Producto_Tipo item) {
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

    public clsClasses.clsP_Producto_Tipo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_Producto_Tipo item) {

        ins.init("P_PRODUCTO_TIPO");
        ins.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);
        ins.add("NOMBRE",item.nombre);
        ins.add("UTILIZA_STOCK",item.utiliza_stock);
        ins.add("CODIGO_TIPO_PRODUCTO_producto_tipo",maxId());
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_Producto_Tipo item) {

        upd.init("P_PRODUCTO_TIPO");
        upd.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);
        upd.add("NOMBRE",item.nombre);
        upd.add("UTILIZA_STOCK",item.utiliza_stock);
        upd.Where("(CODIGO_TIPO_PRODUCTO_producto_tipo="+item.codigo_tipo_producto+")");
        db.execSQL(upd.sql());

    }

    private void deleteItem(clsClasses.clsP_Producto_Tipo item) {
        sql="DELETE FROM P_producto_tipo WHERE (CODIGO_TIPO_PRODUCTO_producto_tipo="+item.codigo_tipo_producto+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_producto_tipo WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_Producto_Tipo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_Producto_Tipo();
            item.codigo_tipo_producto=dt.getString(0);
            item.nombre=dt.getString(1);
            item.utiliza_stock=(dt.getInt(2)==1?true:false);
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

    public String addItemSql(clsClasses.clsP_Producto_Tipo item) {

        ins.init("P_PRODUCTO_TIPO");
        ins.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);
        ins.add("NOMBRE",item.nombre);
        ins.add("UTILIZA_STOCK",item.utiliza_stock);
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_Producto_Tipo item) {

        upd.init("P_PRODUCTO_TIPO");
        upd.add("CODIGO_TIPO_PRODUCTO",item.codigo_tipo_producto);
        upd.add("NOMBRE",item.nombre);
        upd.add("UTILIZA_STOCK",item.utiliza_stock);
        upd.Where("(CODIGO_TIPO_PRODUCTO_producto_tipo="+item.codigo_tipo_producto+")");
        return upd.sql();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{

            String sql = "SELECT IFNULL(Max(CODIGO_TIPO_PRODUCTO),0)+1 AS MAX FROM P_PRODUCTO_TIPO";
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