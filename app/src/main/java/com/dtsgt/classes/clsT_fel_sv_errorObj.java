package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_fel_sv_errorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_fel_sv_error";
    private String sql;
    public ArrayList<clsClasses.clsT_fel_sv_error> items= new ArrayList<clsClasses.clsT_fel_sv_error>();

    public clsT_fel_sv_errorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_fel_sv_error item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_fel_sv_error item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_fel_sv_error item) {
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

    public clsClasses.clsT_fel_sv_error first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_fel_sv_error item) {

        ins.init("T_fel_sv_error");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("FECHA",item.fecha);
        ins.add("BANDERA",item.bandera);
        ins.add("TEXTO",item.texto);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_fel_sv_error item) {

        upd.init("T_fel_sv_error");

        upd.add("FECHA",item.fecha);
        upd.add("BANDERA",item.bandera);
        upd.add("TEXTO",item.texto);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_fel_sv_error item) {
        sql="DELETE FROM T_fel_sv_error WHERE (COREL='"+item.corel+"') AND (ID="+item.id+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_fel_sv_error WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_fel_sv_error item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_fel_sv_error();

            item.corel=dt.getString(0);
            item.id=dt.getInt(1);
            item.fecha=dt.getLong(2);
            item.bandera=dt.getInt(3);
            item.texto=dt.getString(4);

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

    public String addItemSql(clsClasses.clsT_fel_sv_error item) {

        ins.init("T_fel_sv_error");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("FECHA",item.fecha);
        ins.add("BANDERA",item.bandera);
        ins.add("TEXTO",item.texto);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_fel_sv_error item) {

        upd.init("T_fel_sv_error");

        upd.add("FECHA",item.fecha);
        upd.add("BANDERA",item.bandera);
        upd.add("TEXTO",item.texto);

        upd.Where("(COREL='"+item.corel+"') AND (ID="+item.id+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

