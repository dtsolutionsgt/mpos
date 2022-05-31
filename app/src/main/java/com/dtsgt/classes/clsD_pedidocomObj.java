package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_pedidocomObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_pedidocom";
    private String sql;
    public ArrayList<clsClasses.clsD_pedidocom> items= new ArrayList<clsClasses.clsD_pedidocom>();

    public clsD_pedidocomObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_pedidocom item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_pedidocom item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_pedidocom item) {
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

    public clsClasses.clsD_pedidocom first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_pedidocom item) {

        ins.init("D_pedidocom");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_PEDIDO",item.corel_pedido);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_pedidocom item) {

        upd.init("D_pedidocom");

        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("COREL_PEDIDO",item.corel_pedido);
        upd.add("COREL_LINEA",item.corel_linea);
        upd.add("COMANDA",item.comanda);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_pedidocom item) {
        sql="DELETE FROM D_pedidocom WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_pedidocom WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_pedidocom item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_pedidocom();

            item.codigo=dt.getInt(0);
            item.codigo_ruta=dt.getInt(1);
            item.corel_pedido=dt.getString(2);
            item.corel_linea=dt.getInt(3);
            item.comanda=dt.getString(4);

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

    public String addItemSql(clsClasses.clsD_pedidocom item) {

        ins.init("D_pedidocom");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_PEDIDO",item.corel_pedido);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_pedidocom item) {

        upd.init("D_pedidocom");

        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("COREL_PEDIDO",item.corel_pedido);
        upd.add("COREL_LINEA",item.corel_linea);
        upd.add("COMANDA",item.comanda);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

