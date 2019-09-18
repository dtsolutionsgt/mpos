package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_prodmenuObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM T_prodmenu";
    private String sql;
    public ArrayList<clsClasses.clsT_prodmenu> items = new ArrayList<clsClasses.clsT_prodmenu>();

    public clsT_prodmenuObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsT_prodmenu item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_prodmenu item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_prodmenu item) {
        deleteItem(item);
    }

    public void delete(int id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsT_prodmenu first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_prodmenu item) {

        ins.init("T_prodmenu");

        ins.add("ID", item.id);
        ins.add("IDSESS", item.idsess);
        ins.add("IDITEM", item.iditem);
        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("DESCRIP", item.descrip);
        ins.add("NOTA", item.nota);
        ins.add("BANDERA", item.bandera);
        ins.add("IDLISTA", item.idlista);
        ins.add("CANT", item.cant);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_prodmenu item) {

        upd.init("T_prodmenu");

        upd.add("CODIGO", item.codigo);
        upd.add("NOMBRE", item.nombre);
        upd.add("DESCRIP", item.descrip);
        upd.add("NOTA", item.nota);
        upd.add("BANDERA", item.bandera);
        upd.add("IDLISTA", item.idlista);
        upd.add("CANT", item.cant);

        upd.Where("(ID=" + item.id + ") AND (IDSESS=" + item.idsess + ") AND (IDITEM=" + item.iditem + ")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_prodmenu item) {
        sql = "DELETE FROM T_prodmenu WHERE (ID=" + item.id + ") AND (IDSESS=" + item.idsess + ") AND (IDITEM=" + item.iditem + ")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql = "DELETE FROM T_prodmenu WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_prodmenu item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_prodmenu();

            item.id = dt.getInt(0);
            item.idsess = dt.getInt(1);
            item.iditem = dt.getInt(2);
            item.codigo = dt.getString(3);
            item.nombre = dt.getString(4);
            item.descrip = dt.getString(5);
            item.nota = dt.getString(6);
            item.bandera = dt.getInt(7);
            item.idlista = dt.getInt(8);
            item.cant = dt.getInt(9);

            items.add(item);

            dt.moveToNext();
        }

        if (dt != null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt = null;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        if (dt != null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsT_prodmenu item) {

        ins.init("T_prodmenu");

        ins.add("ID", item.id);
        ins.add("IDSESS", item.idsess);
        ins.add("IDITEM", item.iditem);
        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("DESCRIP", item.descrip);
        ins.add("NOTA", item.nota);
        ins.add("BANDERA", item.bandera);
        ins.add("IDLISTA", item.idlista);
        ins.add("CANT", item.cant);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_prodmenu item) {

        upd.init("T_prodmenu");

        upd.add("CODIGO", item.codigo);
        upd.add("NOMBRE", item.nombre);
        upd.add("DESCRIP", item.descrip);
        upd.add("NOTA", item.nota);
        upd.add("BANDERA", item.bandera);
        upd.add("IDLISTA", item.idlista);
        upd.add("CANT", item.cant);

        upd.Where("(ID=" + item.id + ") AND (IDSESS=" + item.idsess + ") AND (IDITEM=" + item.iditem + ")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

