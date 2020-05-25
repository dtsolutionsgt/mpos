package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodmenuObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodmenu ";
    private String sql;
    public ArrayList<clsClasses.clsP_prodmenu> items= new ArrayList<clsClasses.clsP_prodmenu>();

    public clsP_prodmenuObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodmenu item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodmenu item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodmenu item) {
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

    public void fill_by_idproducto(int IdProducto){
    fillItems_by_idproducto(IdProducto);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_prodmenu first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodmenu item) {

        ins.init("P_PRODMENU");
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodmenu item) {

        upd.init("p_prodmenu");
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);
        upd.Where("(CODIGO_MENU="+item.codigo_menu+")");
        db.execSQL(upd.sql());

    }

    private void deleteItem(clsClasses.clsP_prodmenu item) {
        sql="DELETE FROM P_prodmenu WHERE (CODIGO_MENU="+item.codigo_menu+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodmenu WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_prodmenu item;

        items.clear();

        dt=Con.OpenDT(sq);

        if (dt!=null){

            count =dt.getCount();

            if (dt.getCount()>0) dt.moveToFirst();

            while (!dt.isAfterLast()) {

                item = clsCls.new clsP_prodmenu();
                item.codigo_menu=dt.getInt(0);
                item.empresa=dt.getInt(1);
                item.codigo_producto=dt.getInt(2);
                item.nombre=dt.getString(3);
                item.nota=dt.getString(4);
                items.add(item);
                dt.moveToNext();
            }

            if (dt!=null) dt.close();

        }else{
            Log.e("NoItems","OnMenu by EJC");
        }
    }

    private void fillItems_by_idproducto(int idProducto) {

        Cursor dt;
        clsClasses.clsP_prodmenu item;

        items.clear();

        String vsql = " SELECT P_PRODMENU.CODIGO_MENU, \n " +
                      " P_PRODMENU.EMPRESA, P_PRODMENU.CODIGO_PRODUCTO AS CODIGO_PRODUCTO_MENU, \n" +
                      " P_PRODMENU.NOMBRE, P_PRODMENU.NOTA, \n" +
                      " P_PRODMENUOPC.CODIGO_MENU_OPCION, P_PRODMENUOPC.CODIGO_MENU AS CODIGO_OPCION, P_PRODMENUOPC.NOMBRE AS NOMBRE_OPCION, P_PRODMENUOPC.CANT, \n" +
                      " P_PRODMENUOPC.ORDEN\n" +
                      " FROM P_PRODMENU INNER JOIN\n" +
                      " P_PRODMENUOPC ON P_PRODMENU.CODIGO_MENU = P_PRODMENUOPC.CODIGO_MENU\n" +
                      " WHERE P_PRODMENU.CODIGO_PRODUCTO = " + idProducto + "\n" +
                      " ORDER BY P_PRODMENUOPC.ORDEN, P_PRODMENUOPC.NOMBRE";

        dt=Con.OpenDT(vsql);

        if (dt!=null){

            count =dt.getCount();

            if (dt.getCount()>0) dt.moveToFirst();

            while (!dt.isAfterLast()) {

                item = clsCls.new clsP_prodmenu();
                item.codigo_menu=dt.getInt(5);
                item.empresa=dt.getInt(1);
                item.codigo_producto=dt.getInt(2);
                item.nombre=dt.getString(7);
                item.nota=dt.getString(4);
                items.add(item);
                dt.moveToNext();
            }

            if (dt!=null) dt.close();

        }else{
            Log.e("NoItems","OnMenu by EJC");
        }
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

    public String addItemSql(clsClasses.clsP_prodmenu item) {

        ins.init("P_PRODMENU");
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("NOMBRE",item.nombre);
        ins.add("NOTA",item.nota);
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodmenu item) {

        upd.init("p_prodmenu");
        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("NOMBRE",item.nombre);
        upd.add("NOTA",item.nota);
        upd.Where("(CODIGO_MENU="+item.codigo_menu+")");
        return upd.sql();

    }

}

