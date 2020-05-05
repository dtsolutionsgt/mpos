package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_facturaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura";
    private String sql;
    public ArrayList<clsClasses.clsD_factura> items= new ArrayList<clsClasses.clsD_factura>();

    public clsD_facturaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura item) {
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

    public clsClasses.clsD_factura first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_factura item) {

        ins.init("D_factura");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("RUTA",item.ruta);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CLIENTE",item.cliente);
        ins.add("KILOMETRAJE",item.kilometraje);
        ins.add("FECHAENTR",item.fechaentr);
        ins.add("FACTLINK",item.factlink);
        ins.add("TOTAL",item.total);
        ins.add("DESMONTO",item.desmonto);
        ins.add("IMPMONTO",item.impmonto);
        ins.add("PESO",item.peso);
        ins.add("BANDERA",item.bandera);
        ins.add("STATCOM",item.statcom);
        ins.add("CALCOBJ",item.calcobj);
        ins.add("SERIE",item.serie);
        ins.add("CORELATIVO",item.corelativo);
        ins.add("IMPRES",item.impres);
        ins.add("ADD1",item.add1);
        ins.add("ADD2",item.add2);
        ins.add("ADD3",item.add3);
        ins.add("DEPOS",item.depos);
        ins.add("PEDCOREL",item.pedcorel);
        ins.add("REFERENCIA",item.referencia);
        ins.add("ASIGNACION",item.asignacion);
        ins.add("SUPERVISOR",item.supervisor);
        ins.add("AYUDANTE",item.ayudante);
        ins.add("VEHICULO",item.vehiculo);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("RAZON_ANULACION",item.razon_anulacion);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura item) {

        upd.init("D_factura");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("RUTA",item.ruta);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("CLIENTE",item.cliente);
        upd.add("KILOMETRAJE",item.kilometraje);
        upd.add("FECHAENTR",item.fechaentr);
        upd.add("FACTLINK",item.factlink);
        upd.add("TOTAL",item.total);
        upd.add("DESMONTO",item.desmonto);
        upd.add("IMPMONTO",item.impmonto);
        upd.add("PESO",item.peso);
        upd.add("BANDERA",item.bandera);
        upd.add("STATCOM",item.statcom);
        upd.add("CALCOBJ",item.calcobj);
        upd.add("SERIE",item.serie);
        upd.add("CORELATIVO",item.corelativo);
        upd.add("IMPRES",item.impres);
        upd.add("ADD1",item.add1);
        upd.add("ADD2",item.add2);
        upd.add("ADD3",item.add3);
        upd.add("DEPOS",item.depos);
        upd.add("PEDCOREL",item.pedcorel);
        upd.add("REFERENCIA",item.referencia);
        upd.add("ASIGNACION",item.asignacion);
        upd.add("SUPERVISOR",item.supervisor);
        upd.add("AYUDANTE",item.ayudante);
        upd.add("VEHICULO",item.vehiculo);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("RAZON_ANULACION",item.razon_anulacion);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_factura item) {
        sql="DELETE FROM D_factura WHERE (EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_factura WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura();

            item.empresa=dt.getInt(0);
            item.corel=dt.getString(1);
            item.anulado=dt.getString(2);
            item.fecha=dt.getLong(3);
            item.ruta=dt.getInt(4);
            item.vendedor=dt.getInt(5);
            item.cliente=dt.getInt(6);
            item.kilometraje=dt.getDouble(7);
            item.fechaentr=dt.getLong(8);
            item.factlink=dt.getString(9);
            item.total=dt.getDouble(10);
            item.desmonto=dt.getDouble(11);
            item.impmonto=dt.getDouble(12);
            item.peso=dt.getDouble(13);
            item.bandera=dt.getString(14);
            item.statcom=dt.getString(15);
            item.calcobj=dt.getString(16);
            item.serie=dt.getString(17);
            item.corelativo=dt.getInt(18);
            item.impres=dt.getInt(19);
            item.add1=dt.getString(20);
            item.add2=dt.getString(21);
            item.add3=dt.getString(22);
            item.depos=dt.getString(23);
            item.pedcorel=dt.getString(24);
            item.referencia=dt.getString(25);
            item.asignacion=dt.getString(26);
            item.supervisor=dt.getString(27);
            item.ayudante=dt.getString(28);
            item.vehiculo=dt.getString(29);
            item.codigoliquidacion=dt.getInt(30);
            item.razon_anulacion=dt.getString(31);

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

    public String addItemSql(clsClasses.clsD_factura item) {

        ins.init("D_factura");

        ins.add("EMPRESA",item.empresa);
        ins.add("COREL",item.corel);
        ins.add("ANULADO",item.anulado);
        ins.add("FECHA",item.fecha);
        ins.add("RUTA",item.ruta);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("CLIENTE",item.cliente);
        ins.add("KILOMETRAJE",item.kilometraje);
        ins.add("FECHAENTR",item.fechaentr);
        ins.add("FACTLINK",item.factlink);
        ins.add("TOTAL",item.total);
        ins.add("DESMONTO",item.desmonto);
        ins.add("IMPMONTO",item.impmonto);
        ins.add("PESO",item.peso);
        ins.add("BANDERA",item.bandera);
        ins.add("STATCOM",item.statcom);
        ins.add("CALCOBJ",item.calcobj);
        ins.add("SERIE",item.serie);
        ins.add("CORELATIVO",item.corelativo);
        ins.add("IMPRES",item.impres);
        ins.add("ADD1",item.add1);
        ins.add("ADD2",item.add2);
        ins.add("ADD3",item.add3);
        ins.add("DEPOS",item.depos);
        ins.add("PEDCOREL",item.pedcorel);
        ins.add("REFERENCIA",item.referencia);
        ins.add("ASIGNACION",item.asignacion);
        ins.add("SUPERVISOR",item.supervisor);
        ins.add("AYUDANTE",item.ayudante);
        ins.add("VEHICULO",item.vehiculo);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("RAZON_ANULACION",item.razon_anulacion);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura item) {

        upd.init("D_factura");

        upd.add("ANULADO",item.anulado);
        upd.add("FECHA",item.fecha);
        upd.add("RUTA",item.ruta);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("CLIENTE",item.cliente);
        upd.add("KILOMETRAJE",item.kilometraje);
        upd.add("FECHAENTR",item.fechaentr);
        upd.add("FACTLINK",item.factlink);
        upd.add("TOTAL",item.total);
        upd.add("DESMONTO",item.desmonto);
        upd.add("IMPMONTO",item.impmonto);
        upd.add("PESO",item.peso);
        upd.add("BANDERA",item.bandera);
        upd.add("STATCOM",item.statcom);
        upd.add("CALCOBJ",item.calcobj);
        upd.add("SERIE",item.serie);
        upd.add("CORELATIVO",item.corelativo);
        upd.add("IMPRES",item.impres);
        upd.add("ADD1",item.add1);
        upd.add("ADD2",item.add2);
        upd.add("ADD3",item.add3);
        upd.add("DEPOS",item.depos);
        upd.add("PEDCOREL",item.pedcorel);
        upd.add("REFERENCIA",item.referencia);
        upd.add("ASIGNACION",item.asignacion);
        upd.add("SUPERVISOR",item.supervisor);
        upd.add("AYUDANTE",item.ayudante);
        upd.add("VEHICULO",item.vehiculo);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("RAZON_ANULACION",item.razon_anulacion);

        upd.Where("(EMPRESA="+item.empresa+") AND (COREL='"+item.corel+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

