package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsT_mov_almacenObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM T_mov_almacen";
    private String sql;
    public ArrayList<clsClasses.clsT_mov_almacen> items= new ArrayList<clsClasses.clsT_mov_almacen>();

    public clsT_mov_almacenObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsT_mov_almacen item) {
        addItem(item);
    }

    public void update(clsClasses.clsT_mov_almacen item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsT_mov_almacen item) {
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

    public clsClasses.clsT_mov_almacen first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsT_mov_almacen item) {

        ins.init("T_mov_almacen");

        ins.add("COREL",item.corel);
        ins.add("ALMACEN_ORIGEN",item.almacen_origen);
        ins.add("ALMACEN_DESTINO",item.almacen_destino);
        ins.add("REFERENCIA",item.referencia);
        ins.add("TOTAL",item.total);
        ins.add("IDTRASALMACEN",item.idtrasalmacen);
        ins.add("ESTADO",item.estado);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("USRINI",item.usrini);
        ins.add("USRFIN",item.usrfin);
        ins.add("IDALMTRANS",item.idalmtrans);
        ins.add("COMPLETO",item.completo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsT_mov_almacen item) {

        upd.init("T_mov_almacen");

        upd.add("ALMACEN_ORIGEN",item.almacen_origen);
        upd.add("ALMACEN_DESTINO",item.almacen_destino);
        upd.add("REFERENCIA",item.referencia);
        upd.add("TOTAL",item.total);
        upd.add("IDTRASALMACEN",item.idtrasalmacen);
        upd.add("ESTADO",item.estado);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("USRINI",item.usrini);
        upd.add("USRFIN",item.usrfin);
        upd.add("IDALMTRANS",item.idalmtrans);
        upd.add("COMPLETO",item.completo);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsT_mov_almacen item) {
        sql="DELETE FROM T_mov_almacen WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM T_mov_almacen WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsT_mov_almacen item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsT_mov_almacen();

            item.corel=dt.getString(0);
            item.almacen_origen=dt.getInt(1);
            item.almacen_destino=dt.getInt(2);
            item.referencia=dt.getString(3);
            item.total=dt.getDouble(4);
            item.idtrasalmacen=dt.getInt(5);
            item.estado=dt.getInt(6);
            item.fechaini=dt.getLong(7);
            item.fechafin=dt.getLong(8);
            item.usrini=dt.getInt(9);
            item.usrfin=dt.getInt(10);
            item.idalmtrans=dt.getInt(11);
            item.completo=dt.getInt(12);

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

    public String addItemSql(clsClasses.clsT_mov_almacen item) {

        ins.init("T_mov_almacen");

        ins.add("COREL",item.corel);
        ins.add("ALMACEN_ORIGEN",item.almacen_origen);
        ins.add("ALMACEN_DESTINO",item.almacen_destino);
        ins.add("REFERENCIA",item.referencia);
        ins.add("TOTAL",item.total);
        ins.add("IDTRASALMACEN",item.idtrasalmacen);
        ins.add("ESTADO",item.estado);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("USRINI",item.usrini);
        ins.add("USRFIN",item.usrfin);
        ins.add("IDALMTRANS",item.idalmtrans);
        ins.add("COMPLETO",item.completo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsT_mov_almacen item) {

        upd.init("T_mov_almacen");

        upd.add("ALMACEN_ORIGEN",item.almacen_origen);
        upd.add("ALMACEN_DESTINO",item.almacen_destino);
        upd.add("REFERENCIA",item.referencia);
        upd.add("TOTAL",item.total);
        upd.add("IDTRASALMACEN",item.idtrasalmacen);
        upd.add("ESTADO",item.estado);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("USRINI",item.usrini);
        upd.add("USRFIN",item.usrfin);
        upd.add("IDALMTRANS",item.idalmtrans);
        upd.add("COMPLETO",item.completo);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

