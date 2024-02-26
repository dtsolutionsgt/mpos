package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_contingencia_svObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_contingencia_sv";
    private String sql;
    public ArrayList<clsClasses.clsT_contingencia_sv> items= new ArrayList<clsClasses.clsT_contingencia_sv>();

    public clsT_contingencia_svObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_contingencia_sv item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_contingencia_sv item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_contingencia_sv item) {
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

    public clsClasses.clsT_contingencia_sv first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_contingencia_sv item) {

        ins.init("T_contingencia_sv");

        ins.add("COREL",item.corel);
        ins.add("BANDERA",item.bandera);
        ins.add("LLAVE",item.llave);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_contingencia_sv item) {

        upd.init("T_contingencia_sv");

        upd.add("BANDERA",item.bandera);
        upd.add("LLAVE",item.llave);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_contingencia_sv item) {
        sql="DELETE FROM T_contingencia_sv WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_contingencia_sv WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_contingencia_sv item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_contingencia_sv();

            item.corel=dt.getString(0);
            item.bandera=dt.getInt(1);
            item.llave=dt.getString(2);

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

    public String addItemSql(clsClasses.clsT_contingencia_sv item) {

        ins.init("T_contingencia_sv");

        ins.add("COREL",item.corel);
        ins.add("BANDERA",item.bandera);
        ins.add("LLAVE",item.llave);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_contingencia_sv item) {

        upd.init("T_contingencia_sv");

        upd.add("BANDERA",item.bandera);
        upd.add("LLAVE",item.llave);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

