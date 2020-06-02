package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

public class clsP_proveedorObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM P_proveedor";
    private String sql;
    public ArrayList<clsClasses.clsP_proveedor> items= new ArrayList<clsClasses.clsP_proveedor>();

    public clsP_proveedorObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
        count = 0;

        mu = new MiscUtils(context);

    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;
    }

    public void add(clsClasses.clsP_proveedor item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_proveedor item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_proveedor item) {
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

    public clsClasses.clsP_proveedor first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_proveedor item) {

        ins.init("P_proveedor");

        ins.add("CODIGO_PROVEEDOR",maxId());
        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_proveedor item) {

        upd.init("P_proveedor");

        upd.add("CODIGO",item.codigo);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_PROVEEDOR="+item.codigo_proveedor+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_proveedor item) {
        sql="DELETE FROM P_proveedor WHERE (CODIGO_PROVEEDOR="+item.codigo_proveedor+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_proveedor WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_proveedor item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_proveedor();

            item.codigo_proveedor=dt.getInt(0);
            item.codigo=dt.getString(1);
            item.nombre=dt.getString(2);
            item.activo=dt.getInt(3);

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

    public String addItemSql(clsClasses.clsP_proveedor item) {

        ins.init("P_proveedor");

        ins.add("CODIGO_PROVEEDOR",item.codigo_proveedor);
        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_proveedor item) {

        upd.init("P_proveedor");

        upd.add("CODIGO",item.codigo);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_PROVEEDOR="+item.codigo_proveedor+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT IFNULL(MAX(CODIGO_PROVEEDOR),0)+1 AS MAX FROM P_PROVEEDOR";
            DT = Con.OpenDT(sql);

            if (DT != null){
                DT.moveToFirst();

                resultado=DT.getInt(0);
            }

        } catch (Exception e) {
            mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return resultado;
    }
}

