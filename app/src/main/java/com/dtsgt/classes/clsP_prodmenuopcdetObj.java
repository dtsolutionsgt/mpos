package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsP_prodmenuopcdetObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_PRODMENUOPC_DET";
    private String sql;
    public ArrayList<clsClasses.clsp_prodmenuopc_det> items= new ArrayList<clsClasses.clsp_prodmenuopc_det>();

    public clsP_prodmenuopcdetObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsp_prodmenuopc_det item) {
        addItem(item);
    }

    public void update(clsClasses.clsp_prodmenuopc_det item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsp_prodmenuopc_det item) {
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

    public clsClasses.clsp_prodmenuopc_det first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsp_prodmenuopc_det item) {

        ins.init("P_PRODMENUOPC_DET");
        ins.add("CODIGO_MENUOPC_DET",item.codigo_menuopc_det);
        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        db.execSQL(ins.sql());
    }

    private void updateItem(clsClasses.clsp_prodmenuopc_det item) {

        upd.init("P_PRODMENUOPC_DET");
        upd.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.Where("(CODIGO_MENUOPC_DET="+item.codigo_menu_opcion+")");
        db.execSQL(upd.sql());
        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();
    }

    private void deleteItem(clsClasses.clsp_prodmenuopc_det item) {
        sql="DELETE FROM P_PRODMENUOPC_DET WHERE (CODIGO_MENUOPC_DET="+item.codigo_menuopc_det+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_PRODMENUOPC_DET WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsp_prodmenuopc_det item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();

        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsp_prodmenuopc_det();
            item.codigo_menuopc_det=dt.getInt(0);
            item.codigo_menu_opcion=dt.getInt(1);
            item.codigo_producto=dt.getInt(2);
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

    public String addItemSql(clsClasses.clsp_prodmenuopc_det item) {

        ins.init("P_PRODMENUOPC_DET");
        ins.add("CODIGO_MENUOPC_DET",item.codigo_menuopc_det);
        ins.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsp_prodmenuopc_det item) {

        upd.init("P_PRODMENUOPC_DET");
        upd.add("CODIGO_MENU_OPCION",item.codigo_menu_opcion);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.Where("(CODIGO_MENUOPC_DET="+item.codigo_menu_opcion+")");
        return upd.sql();

    }
}