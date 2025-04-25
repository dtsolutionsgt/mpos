package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_depositoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_deposito";
    private String sql;
    public ArrayList<clsClasses.clsP_deposito> items= new ArrayList<clsClasses.clsP_deposito>();

    public clsP_depositoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_deposito item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_deposito item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_deposito item) {
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

    public clsClasses.clsP_deposito first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsP_deposito item) {

        ins.init("P_deposito");

        ins.add("CODIGO_DEPOSITO",item.codigo_deposito);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_BANCO",item.codigo_banco);
        ins.add("CUENTA",item.cuenta);
        ins.add("BOLETA",item.boleta);
        ins.add("MONTO_EFECTIVO",item.monto_efectivo);
        ins.add("MONTO_CHEQUES",item.monto_cheques);
        ins.add("MONTO_TOTAL",item.monto_total);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("STATCOM",item.statcom);
        ins.add("REFERENCIA",item.referencia);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_deposito item) {

        upd.init("P_deposito");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_BANCO",item.codigo_banco);
        upd.add("CUENTA",item.cuenta);
        upd.add("BOLETA",item.boleta);
        upd.add("MONTO_EFECTIVO",item.monto_efectivo);
        upd.add("MONTO_CHEQUES",item.monto_cheques);
        upd.add("MONTO_TOTAL",item.monto_total);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("STATCOM",item.statcom);
        upd.add("REFERENCIA",item.referencia);

        upd.Where("(CODIGO_DEPOSITO="+item.codigo_deposito+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsP_deposito item) {
        sql="DELETE FROM P_deposito WHERE (CODIGO_DEPOSITO="+item.codigo_deposito+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_deposito WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_deposito item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_deposito();

            item.codigo_deposito=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_sucursal=dt.getInt(2);
            item.codigo_ruta=dt.getInt(3);
            item.fecha=dt.getLong(4);
            item.codigo_banco=dt.getInt(5);
            item.cuenta=dt.getString(6);
            item.boleta=dt.getString(7);
            item.monto_efectivo=dt.getDouble(8);
            item.monto_cheques=dt.getDouble(9);
            item.monto_total=dt.getDouble(10);
            item.codigo_vendedor=dt.getInt(11);
            item.statcom=dt.getString(12);
            item.referencia=dt.getInt(13);

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

    public String addItemSql(clsClasses.clsP_deposito item) {

        ins.init("P_deposito");

        ins.add("CODIGO_DEPOSITO",item.codigo_deposito);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("FECHA",item.fecha);
        ins.add("CODIGO_BANCO",item.codigo_banco);
        ins.add("CUENTA",item.cuenta);
        ins.add("BOLETA",item.boleta);
        ins.add("MONTO_EFECTIVO",item.monto_efectivo);
        ins.add("MONTO_CHEQUES",item.monto_cheques);
        ins.add("MONTO_TOTAL",item.monto_total);
        ins.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        ins.add("STATCOM",item.statcom);
        ins.add("REFERENCIA",item.referencia);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_deposito item) {

        upd.init("P_deposito");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("FECHA",item.fecha);
        upd.add("CODIGO_BANCO",item.codigo_banco);
        upd.add("CUENTA",item.cuenta);
        upd.add("BOLETA",item.boleta);
        upd.add("MONTO_EFECTIVO",item.monto_efectivo);
        upd.add("MONTO_CHEQUES",item.monto_cheques);
        upd.add("MONTO_TOTAL",item.monto_total);
        upd.add("CODIGO_VENDEDOR",item.codigo_vendedor);
        upd.add("STATCOM",item.statcom);
        upd.add("REFERENCIA",item.referencia);

        upd.Where("(CODIGO_DEPOSITO="+item.codigo_deposito+")");

        return upd.sql();


    }

    //endregion
}

