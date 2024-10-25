package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_domicilio_entregaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_domicilio_entrega";
    private String sql;
    public ArrayList<clsClasses.clsD_domicilio_entrega> items= new ArrayList<clsClasses.clsD_domicilio_entrega>();

    public clsD_domicilio_entregaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_domicilio_entrega item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_domicilio_entrega item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_domicilio_entrega item) {
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

    public clsClasses.clsD_domicilio_entrega first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_domicilio_entrega item) {

        ins.init("D_domicilio_entrega");

        ins.add("COREL",item.corel);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("ESTADO",item.estado);
        ins.add("IDREPAR",item.idrepar);
        ins.add("NOMBRE",item.nombre);
        ins.add("PLACA",item.placa);
        ins.add("IDEMPRESA",item.idempresa);
        ins.add("TOTAL",item.total);
        ins.add("PAGO",item.pago);
        ins.add("VUELTO",item.vuelto);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("PARAM2",item.param2);
        ins.add("PARAM1",item.param1);
        ins.add("PARAM3",item.param3);
        ins.add("PARAM4",item.param4);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_domicilio_entrega item) {

        upd.init("D_domicilio_entrega");

        upd.add("COREL_ORDEN",item.corel_orden);
        upd.add("ESTADO",item.estado);
        upd.add("IDREPAR",item.idrepar);
        upd.add("NOMBRE",item.nombre);
        upd.add("PLACA",item.placa);
        upd.add("IDEMPRESA",item.idempresa);
        upd.add("TOTAL",item.total);
        upd.add("PAGO",item.pago);
        upd.add("VUELTO",item.vuelto);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("PARAM2",item.param2);
        upd.add("PARAM1",item.param1);
        upd.add("PARAM3",item.param3);
        upd.add("PARAM4",item.param4);

        upd.Where("(COREL='"+item.corel+"')");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_domicilio_entrega item) {
        sql="DELETE FROM D_domicilio_entrega WHERE (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_domicilio_entrega WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_domicilio_entrega item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_domicilio_entrega();

            item.corel=dt.getString(0);
            item.corel_orden=dt.getString(1);
            item.estado=dt.getInt(2);
            item.idrepar=dt.getInt(3);
            item.nombre=dt.getString(4);
            item.placa=dt.getString(5);
            item.idempresa=dt.getInt(6);
            item.total=dt.getDouble(7);
            item.pago=dt.getDouble(8);
            item.vuelto=dt.getDouble(9);
            item.fechaini=dt.getLong(10);
            item.fechafin=dt.getLong(11);
            item.param2=dt.getString(12);
            item.param1=dt.getString(13);
            item.param3=dt.getInt(14);
            item.param4=dt.getLong(15);

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

    public String addItemSql(clsClasses.clsD_domicilio_entrega item) {

        ins.init("D_domicilio_entrega");

        ins.add("COREL",item.corel);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("ESTADO",item.estado);
        ins.add("IDREPAR",item.idrepar);
        ins.add("NOMBRE",item.nombre);
        ins.add("PLACA",item.placa);
        ins.add("IDEMPRESA",item.idempresa);
        ins.add("TOTAL",item.total);
        ins.add("PAGO",item.pago);
        ins.add("VUELTO",item.vuelto);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("PARAM2",item.param2);
        ins.add("PARAM1",item.param1);
        ins.add("PARAM3",item.param3);
        ins.add("PARAM4",item.param4);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_domicilio_entrega item) {

        upd.init("D_domicilio_entrega");

        upd.add("COREL_ORDEN",item.corel_orden);
        upd.add("ESTADO",item.estado);
        upd.add("IDREPAR",item.idrepar);
        upd.add("NOMBRE",item.nombre);
        upd.add("PLACA",item.placa);
        upd.add("IDEMPRESA",item.idempresa);
        upd.add("TOTAL",item.total);
        upd.add("PAGO",item.pago);
        upd.add("VUELTO",item.vuelto);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("PARAM2",item.param2);
        upd.add("PARAM1",item.param1);
        upd.add("PARAM3",item.param3);
        upd.add("PARAM4",item.param4);

        upd.Where("(COREL='"+item.corel+"')");

        return upd.sql();


    }

    //endregion
}

