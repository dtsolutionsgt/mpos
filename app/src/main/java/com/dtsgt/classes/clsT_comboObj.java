package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsT_comboObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_combo";
    private String sql;
    public ArrayList<clsClasses.clsT_combo> items= new ArrayList<clsClasses.clsT_combo>();

    public clsT_comboObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_combo item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_combo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_combo item) {
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

    public clsClasses.clsT_combo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_combo item) {

        ins.init("T_combo");

        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_combo item) {

        upd.init("T_combo");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);

        upd.Where("(CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_combo item) {
        sql="DELETE FROM T_combo WHERE (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM T_combo WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_combo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_combo();

            item.codigo_menu=dt.getInt(0);
            item.idcombo=dt.getInt(1);
            item.unid=dt.getInt(2);
            item.cant=dt.getInt(3);
            item.idseleccion=dt.getInt(4);
            item.orden=dt.getInt(5);

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

    public String addItemSql(clsClasses.clsT_combo item) {

        ins.init("T_combo");

        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_combo item) {

        upd.init("T_combo");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);

        upd.Where("(CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

