package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_stockObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_stock";
    private String sql;
    public ArrayList<clsClasses.clsP_stock> items= new ArrayList<clsClasses.clsP_stock>();

    public clsP_stockObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_stock item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_stock item) {
        updateItem(item);
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

    public clsClasses.clsP_stock first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_stock item) {

        ins.init("P_stock");

        ins.add("CODIGO",item.codigo);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PLIBRA",item.plibra);
        ins.add("LOTE",item.lote);
        ins.add("DOCUMENTO",item.documento);
        ins.add("FECHA",item.fecha);
        ins.add("ANULADO",item.anulado);
        ins.add("CENTRO",item.centro);
        ins.add("STATUS",item.status);
        ins.add("ENVIADO",item.enviado);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("COREL_D_MOV",item.corel_d_mov);
        ins.add("UNIDADMEDIDA",item.unidadmedida);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_stock item) {

        upd.init("P_stock");

        upd.add("CODIGO",item.codigo);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PLIBRA",item.plibra);
        upd.add("LOTE",item.lote);
        upd.add("DOCUMENTO",item.documento);
        upd.add("FECHA",item.fecha);
        upd.add("ANULADO",item.anulado);
        upd.add("CENTRO",item.centro);
        upd.add("STATUS",item.status);
        upd.add("ENVIADO",item.enviado);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("COREL_D_MOV",item.corel_d_mov);
        upd.add("UNIDADMEDIDA",item.unidadmedida);

        upd.Where("");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }


    private void deleteItem(int id) {
        sql="DELETE FROM P_stock WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_stock item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_stock();

            item.codigo=dt.getInt(0);
            item.cant=dt.getDouble(1);
            item.cantm=dt.getDouble(2);
            item.peso=dt.getDouble(3);
            item.plibra=dt.getDouble(4);
            item.lote=dt.getString(5);
            item.documento=dt.getString(6);
            item.fecha=dt.getInt(7);
            item.anulado=dt.getInt(8);
            item.centro=dt.getString(9);
            item.status=dt.getString(10);
            item.enviado=dt.getInt(11);
            item.codigoliquidacion=dt.getInt(12);
            item.corel_d_mov=dt.getString(13);
            item.unidadmedida=dt.getString(14);

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

    public String addItemSql(clsClasses.clsP_stock item) {

        ins.init("P_stock");

        ins.add("CODIGO",item.codigo);
        ins.add("CANT",item.cant);
        ins.add("CANTM",item.cantm);
        ins.add("PESO",item.peso);
        ins.add("PLIBRA",item.plibra);
        ins.add("LOTE",item.lote);
        ins.add("DOCUMENTO",item.documento);
        ins.add("FECHA",item.fecha);
        ins.add("ANULADO",item.anulado);
        ins.add("CENTRO",item.centro);
        ins.add("STATUS",item.status);
        ins.add("ENVIADO",item.enviado);
        ins.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        ins.add("COREL_D_MOV",item.corel_d_mov);
        ins.add("UNIDADMEDIDA",item.unidadmedida);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_stock item) {

        upd.init("P_stock");

        upd.add("CODIGO",item.codigo);
        upd.add("CANT",item.cant);
        upd.add("CANTM",item.cantm);
        upd.add("PESO",item.peso);
        upd.add("PLIBRA",item.plibra);
        upd.add("LOTE",item.lote);
        upd.add("DOCUMENTO",item.documento);
        upd.add("FECHA",item.fecha);
        upd.add("ANULADO",item.anulado);
        upd.add("CENTRO",item.centro);
        upd.add("STATUS",item.status);
        upd.add("ENVIADO",item.enviado);
        upd.add("CODIGOLIQUIDACION",item.codigoliquidacion);
        upd.add("COREL_D_MOV",item.corel_d_mov);
        upd.add("UNIDADMEDIDA",item.unidadmedida);

        upd.Where("");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

