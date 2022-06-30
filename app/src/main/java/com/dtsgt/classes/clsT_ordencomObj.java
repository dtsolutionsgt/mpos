package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencomObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencom";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencom> items= new ArrayList<clsClasses.clsT_ordencom>();

    public clsT_ordencomObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordencom item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencom item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencom item) {
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

    public clsClasses.clsT_ordencom first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencom item) {

        ins.init("T_ordencom");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencom item) {

        upd.init("T_ordencom");

        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("COREL_ORDEN",item.corel_orden);
        upd.add("COREL_LINEA",item.corel_linea);
        upd.add("COMANDA",item.comanda);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencom item) {
        sql="DELETE FROM T_ordencom WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_ordencom WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordencom item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencom();

            item.codigo=dt.getInt(0);
            item.codigo_ruta=dt.getInt(1);
            item.corel_orden=dt.getString(2);
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

    public String addItemSql(clsClasses.clsT_ordencom item) {

        ins.init("T_ordencom");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencom item) {

        upd.init("T_ordencom");

        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("COREL_ORDEN",item.corel_orden);
        upd.add("COREL_LINEA",item.corel_linea);
        upd.add("COMANDA",item.comanda);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

