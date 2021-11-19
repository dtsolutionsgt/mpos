package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_ordencomboprecioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_ordencomboprecio";
    private String sql;
    public ArrayList<clsClasses.clsT_ordencomboprecio> items= new ArrayList<clsClasses.clsT_ordencomboprecio>();

    public clsT_ordencomboprecioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_ordencomboprecio item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_ordencomboprecio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_ordencomboprecio item) {
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

    public clsClasses.clsT_ordencomboprecio first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_ordencomboprecio item) {

        ins.init("T_ordencomboprecio");
        ins.add("COREL",item.corel);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("PRECORIG",item.precorig);
        ins.add("PRECITEMS",item.precitems);
        ins.add("PRECDIF",item.precdif);
        ins.add("PRECTOTAL",item.prectotal);

        String ss=ins.sql();

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_ordencomboprecio item) {

        upd.init("T_ordencomboprecio");
        upd.add("PRECORIG",item.precorig);
        upd.add("PRECITEMS",item.precitems);
        upd.add("PRECDIF",item.precdif);
        upd.add("PRECTOTAL",item.prectotal);
        upd.Where("(COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")");
        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_ordencomboprecio item) {
        sql="DELETE FROM T_ordencomboprecio WHERE (COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_ordencomboprecio WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsT_ordencomboprecio item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_ordencomboprecio();
            item.corel=dt.getString(0);
            item.idcombo=dt.getInt(1);
            item.precorig=dt.getDouble(2);
            item.precitems=dt.getDouble(3);
            item.precdif=dt.getDouble(4);
            item.prectotal=dt.getDouble(5);
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

    public String addItemSql(clsClasses.clsT_ordencomboprecio item) {

        ins.init("T_ordencomboprecio");

        ins.add("COREL",item.corel);
        ins.add("IDCOMBO",item.idcombo);
        ins.add("PRECORIG",item.precorig);
        ins.add("PRECITEMS",item.precitems);
        ins.add("PRECDIF",item.precdif);
        ins.add("PRECTOTAL",item.prectotal);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_ordencomboprecio item) {

        upd.init("T_ordencomboprecio");

        upd.add("PRECORIG",item.precorig);
        upd.add("PRECITEMS",item.precitems);
        upd.add("PRECDIF",item.precdif);
        upd.add("PRECTOTAL",item.prectotal);

        upd.Where("(COREL='"+item.corel+"') AND (IDCOMBO="+item.idcombo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

