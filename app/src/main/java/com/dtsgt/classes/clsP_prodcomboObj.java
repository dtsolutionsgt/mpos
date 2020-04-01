package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_prodcomboObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_prodcombo";
    private String sql;
    public ArrayList<clsClasses.clsP_prodcombo> items= new ArrayList<clsClasses.clsP_prodcombo>();

    public clsP_prodcomboObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_prodcombo item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_prodcombo item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_prodcombo item) {
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

    public clsClasses.clsP_prodcombo first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_prodcombo item) {

        ins.init("P_prodcombo");

        ins.add("CODIGO",item.codigo);
        ins.add("PRODUCTO",item.producto);
        ins.add("TIPO",item.tipo);
        ins.add("CANTMIN",item.cantmin);
        ins.add("CANTTOT",item.canttot);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_prodcombo item) {

        upd.init("P_prodcombo");

        upd.add("TIPO",item.tipo);
        upd.add("CANTMIN",item.cantmin);
        upd.add("CANTTOT",item.canttot);

        upd.Where("(CODIGO='"+item.codigo+"') AND (PRODUCTO='"+item.producto+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_prodcombo item) {
        sql="DELETE FROM P_prodcombo WHERE (CODIGO='"+item.codigo+"') AND (PRODUCTO='"+item.producto+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_prodcombo WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_prodcombo item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_prodcombo();

            item.codigo=dt.getString(0);
            item.producto=dt.getString(1);
            item.tipo=dt.getString(2);
            item.cantmin=dt.getDouble(3);
            item.canttot=dt.getDouble(4);

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

    public String addItemSql(clsClasses.clsP_prodcombo item) {

        ins.init("P_prodcombo");

        ins.add("CODIGO",item.codigo);
        ins.add("PRODUCTO",item.producto);
        ins.add("TIPO",item.tipo);
        ins.add("CANTMIN",item.cantmin);
        ins.add("CANTTOT",item.canttot);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_prodcombo item) {

        upd.init("P_prodcombo");

        upd.add("TIPO",item.tipo);
        upd.add("CANTMIN",item.cantmin);
        upd.add("CANTTOT",item.canttot);

        upd.Where("(CODIGO='"+item.codigo+"') AND (PRODUCTO='"+item.producto+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

