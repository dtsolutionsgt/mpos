package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_res_mesaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_res_mesa";
    private String sql;
    public ArrayList<clsClasses.clsP_res_mesa> items= new ArrayList<clsClasses.clsP_res_mesa>();

    public clsP_res_mesaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_res_mesa item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_res_mesa item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_res_mesa item) {
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

    public clsClasses.clsP_res_mesa first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_res_mesa item) {

        ins.init("P_res_mesa");

        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_SALA",item.codigo_sala);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("NOMBRE",item.nombre);
        ins.add("LARGO",item.largo);
        ins.add("ANCHO",item.ancho);
        ins.add("POSX",item.posx);
        ins.add("POSY",item.posy);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_res_mesa item) {

        upd.init("P_res_mesa");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_SALA",item.codigo_sala);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);
        upd.add("NOMBRE",item.nombre);
        upd.add("LARGO",item.largo);
        upd.add("ANCHO",item.ancho);
        upd.add("POSX",item.posx);
        upd.add("POSY",item.posy);

        upd.Where("(CODIGO_MESA="+item.codigo_mesa+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_res_mesa item) {
        sql="DELETE FROM P_res_mesa WHERE (CODIGO_MESA="+item.codigo_mesa+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_res_mesa WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_res_mesa item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_res_mesa();

            item.codigo_mesa=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_sala=dt.getInt(3);
            item.codigo_grupo=dt.getInt(4);
            item.nombre=dt.getString(5);
            item.largo=dt.getDouble(6);
            item.ancho=dt.getDouble(7);
            item.posx=dt.getDouble(8);
            item.posy=dt.getDouble(9);

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

    public String addItemSql(clsClasses.clsP_res_mesa item) {

        ins.init("P_res_mesa");

        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_SALA",item.codigo_sala);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);
        ins.add("NOMBRE",item.nombre);
        ins.add("LARGO",item.largo);
        ins.add("ANCHO",item.ancho);
        ins.add("POSX",item.posx);
        ins.add("POSY",item.posy);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_res_mesa item) {

        upd.init("P_res_mesa");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_SALA",item.codigo_sala);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);
        upd.add("NOMBRE",item.nombre);
        upd.add("LARGO",item.largo);
        upd.add("ANCHO",item.ancho);
        upd.add("POSX",item.posx);
        upd.add("POSY",item.posy);

        upd.Where("(CODIGO_MESA="+item.codigo_mesa+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

