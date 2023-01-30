package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturahnObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_facturahn";
    private String sql;
    public ArrayList<clsClasses.clsD_facturahn> items= new ArrayList<clsClasses.clsD_facturahn>();

    public clsD_facturahnObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_facturahn item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_facturahn item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_facturahn item) {
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

    public clsClasses.clsD_facturahn first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_facturahn item) {

        ins.init("D_facturahn");

        ins.add("COREL",item.corel);
        ins.add("SUBTOTAL",item.subtotal);
        ins.add("EXON",item.exon);
        ins.add("EXENTO",item.exento);
        ins.add("GRAVADO",item.gravado);
        ins.add("IMP1",item.imp1);
        ins.add("IMP2",item.imp2);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_facturahn item) {

        upd.init("D_facturahn");

        upd.add("SUBTOTAL",item.subtotal);
        upd.add("EXON",item.exon);
        upd.add("EXENTO",item.exento);
        upd.add("GRAVADO",item.gravado);
        upd.add("IMP1",item.imp1);
        upd.add("IMP2",item.imp2);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_facturahn item) {
        sql="DELETE FROM D_facturahn WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_facturahn WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_facturahn item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_facturahn();

            item.corel=dt.getString(0);
            item.subtotal=dt.getDouble(1);
            item.exon=dt.getDouble(2);
            item.exento=dt.getDouble(3);
            item.gravado=dt.getDouble(4);
            item.imp1=dt.getDouble(5);
            item.imp2=dt.getDouble(6);
            item.val1=dt.getDouble(7);
            item.val2=dt.getDouble(8);

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

    public String addItemSql(clsClasses.clsD_facturahn item) {

        ins.init("D_facturahn");

        ins.add("COREL",item.corel);
        ins.add("SUBTOTAL",item.subtotal);
        ins.add("EXON",item.exon);
        ins.add("EXENTO",item.exento);
        ins.add("GRAVADO",item.gravado);
        ins.add("IMP1",item.imp1);
        ins.add("IMP2",item.imp2);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_facturahn item) {

        upd.init("D_facturahn");

        upd.add("SUBTOTAL",item.subtotal);
        upd.add("EXON",item.exon);
        upd.add("EXENTO",item.exento);
        upd.add("GRAVADO",item.gravado);
        upd.add("IMP1",item.imp1);
        upd.add("IMP2",item.imp2);
        upd.add("VAL1",item.val1);
        upd.add("VAL2",item.val2);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

