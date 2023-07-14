package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsTx_res_sesionObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM Tx_res_sesion";
    private String sql;
    public ArrayList<clsClasses.clsTx_res_sesion> items= new ArrayList<clsClasses.clsTx_res_sesion>();

    public clsTx_res_sesionObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsTx_res_sesion item) {
        addItem(item);
    }

    public void update(clsClasses.clsTx_res_sesion item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsTx_res_sesion item) {
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

    public clsClasses.clsTx_res_sesion first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsTx_res_sesion item) {

        ins.init("Tx_res_sesion");

        ins.add("ID",item.id);
        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("ESTADO",item.estado);
        ins.add("CANTP",item.cantp);
        ins.add("CANTC",item.cantc);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("FECHAULT",item.fechault);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsTx_res_sesion item) {

        upd.init("Tx_res_sesion");

        upd.add("CODIGO_MESA",item.codigo_mesa);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("ESTADO",item.estado);
        upd.add("CANTP",item.cantp);
        upd.add("CANTC",item.cantc);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("FECHAULT",item.fechault);

        upd.Where("(ID='"+item.id+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsTx_res_sesion item) {
        sql="DELETE FROM Tx_res_sesion WHERE (ID='"+item.id+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM Tx_res_sesion WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsTx_res_sesion item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsTx_res_sesion();

            item.id=dt.getString(0);
            item.codigo_mesa=dt.getInt(1);
            item.vendedor=dt.getInt(2);
            item.estado=dt.getInt(3);
            item.cantp=dt.getInt(4);
            item.cantc=dt.getInt(5);
            item.fechaini=dt.getLong(6);
            item.fechafin=dt.getLong(7);
            item.fechault=dt.getLong(8);

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

    public String addItemSql(clsClasses.clsTx_res_sesion item) {

        ins.init("Tx_res_sesion");

        ins.add("ID",item.id);
        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("ESTADO",item.estado);
        ins.add("CANTP",item.cantp);
        ins.add("CANTC",item.cantc);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("FECHAULT",item.fechault);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsTx_res_sesion item) {

        upd.init("Tx_res_sesion");

        upd.add("CODIGO_MESA",item.codigo_mesa);
        upd.add("VENDEDOR",item.vendedor);
        upd.add("ESTADO",item.estado);
        upd.add("CANTP",item.cantp);
        upd.add("CANTC",item.cantc);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("FECHAULT",item.fechault);

        upd.Where("(ID='"+item.id+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}


