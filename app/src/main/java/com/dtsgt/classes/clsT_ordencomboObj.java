package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencomboObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencombo";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencombo> items= new ArrayList<clsClasses.clsT_ordencombo>();

    public clsT_ordencomboObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordencombo item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencombo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencombo item) {
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

    public clsClasses.clsT_ordencombo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencombo item) {

        ins.init("T_ordencombo");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencombo item) {

        upd.init("T_ordencombo");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);

        upd.Where("(COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencombo item) {
        sql="DELETE FROM T_ordencombo WHERE (COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ordencombo WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_ordencombo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencombo();

            item.corel=dt.getString(0);
            item.codigo_menu=dt.getInt(1);
            item.idcombo=dt.getInt(2);
            item.unid=dt.getInt(3);
            item.cant=dt.getInt(4);
            item.idseleccion=dt.getInt(5);
            item.orden=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsT_ordencombo item) {

        ins.init("T_ordencombo");

        ins.add("COREL",item.corel);
        ins.add("CODIGO_MENU",item.codigo_menu);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("UNID",item.unid);
        ins.add("CANT",item.cant);
        ins.add("IDSELECCION",item.idseleccion);
        ins.add("ORDEN",item.orden);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencombo item) {

        upd.init("T_ordencombo");

        upd.add("UNID",item.unid);
        upd.add("CANT",item.cant);
        upd.add("IDSELECCION",item.idseleccion);
        upd.add("ORDEN",item.orden);

        upd.Where("(COREL='"+item.corel+"') AND (CODIGO_MENU="+item.codigo_menu+") AND (IDCOMBO="+item.idcombo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

