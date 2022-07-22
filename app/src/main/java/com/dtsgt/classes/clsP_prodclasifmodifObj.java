package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodclasifmodifObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodclasifmodif";
    private String sql;
    public ArrayList<clsClasses.clsP_prodclasifmodif> items= new ArrayList<clsClasses.clsP_prodclasifmodif>();

    public clsP_prodclasifmodifObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodclasifmodif item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodclasifmodif item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodclasifmodif item) {
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

    public clsClasses.clsP_prodclasifmodif first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodclasifmodif item) {

        ins.init("P_prodclasifmodif");

        ins.add("CODIGO_CLASIFICACION",item.codigo_clasificacion);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodclasifmodif item) {

        upd.init("P_prodclasifmodif");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);

        upd.Where("(CODIGO_CLASIFICACION="+item.codigo_clasificacion+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodclasifmodif item) {
        sql="DELETE FROM P_prodclasifmodif WHERE (CODIGO_CLASIFICACION="+item.codigo_clasificacion+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_prodclasifmodif WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodclasifmodif item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodclasifmodif();

            item.codigo_clasificacion=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.codigo_grupo=dt.getInt(2);

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

    public String addItemSql(clsClasses.clsP_prodclasifmodif item) {

        ins.init("P_prodclasifmodif");

        ins.add("CODIGO_CLASIFICACION",item.codigo_clasificacion);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_GRUPO",item.codigo_grupo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodclasifmodif item) {

        upd.init("P_prodclasifmodif");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_GRUPO",item.codigo_grupo);

        upd.Where("(CODIGO_CLASIFICACION="+item.codigo_clasificacion+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

