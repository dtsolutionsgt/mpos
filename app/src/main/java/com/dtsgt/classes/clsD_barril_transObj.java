package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_barril_transObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_barril_trans";
    private String sql;
    public ArrayList<clsClasses.clsD_barril_trans> items= new ArrayList<clsClasses.clsD_barril_trans>();

    public clsD_barril_transObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_barril_trans item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_barril_trans item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_barril_trans item) {
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

    public clsClasses.clsD_barril_trans first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_barril_trans item) {

        ins.init("D_barril_trans");

        ins.add("CODIGO_TRANS",item.codigo_trans);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHAHORA",item.fechahora);
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UM",item.um);
        ins.add("MESERO",item.mesero);
        ins.add("TIPO_MOV",item.tipo_mov);
        ins.add("IDTRANS",item.idtrans);
        ins.add("STATCOM",item.statcom);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_barril_trans item) {

        upd.init("D_barril_trans");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("FECHAHORA",item.fechahora);
        upd.add("CODIGO_BARRIL",item.codigo_barril);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("UM",item.um);
        upd.add("MESERO",item.mesero);
        upd.add("TIPO_MOV",item.tipo_mov);
        upd.add("IDTRANS",item.idtrans);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_TRANS="+item.codigo_trans+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_barril_trans item) {
        sql="DELETE FROM D_barril_trans WHERE (CODIGO_TRANS="+item.codigo_trans+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_barril_trans WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_barril_trans item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_barril_trans();

            item.codigo_trans=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.fechahora=dt.getLong(3);
            item.codigo_barril=dt.getString(4);
            item.codigo_producto=dt.getInt(5);
            item.cantidad=dt.getDouble(6);
            item.um=dt.getString(7);
            item.mesero=dt.getInt(8);
            item.tipo_mov=dt.getInt(9);
            item.idtrans=dt.getString(10);
            item.statcom=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsD_barril_trans item) {

        ins.init("D_barril_trans");

        ins.add("CODIGO_TRANS",item.codigo_trans);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHAHORA",item.fechahora);
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UM",item.um);
        ins.add("MESERO",item.mesero);
        ins.add("TIPO_MOV",item.tipo_mov);
        ins.add("IDTRANS",item.idtrans);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_barril_trans item) {

        upd.init("D_barril_trans");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("FECHAHORA",item.fechahora);
        upd.add("CODIGO_BARRIL",item.codigo_barril);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("CANTIDAD",item.cantidad);
        upd.add("UM",item.um);
        upd.add("MESERO",item.mesero);
        upd.add("TIPO_MOV",item.tipo_mov);
        upd.add("IDTRANS",item.idtrans);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_TRANS="+item.codigo_trans+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

