package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

public class clsP_rutaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM P_RUTA";
    private String sql;
    public ArrayList<clsClasses.clsP_ruta> items= new ArrayList<clsClasses.clsP_ruta>();

    public clsP_rutaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {

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

    public void add(clsClasses.clsP_ruta item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_ruta item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_ruta item) {
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

    public clsClasses.clsP_ruta first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_ruta item) {

        ins.init("P_RUTA");
        ins.add("CODIGO",item.codigo);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("CODIGO_RUTA",maxId());
        ins.add("ACTIVO",(item.activo?1:0));
        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_ruta item) {

        upd.init("P_RUTA");
        upd.add("SUCURSAL",item.sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("CODIGO_RUTA",item.codigo_ruta);
        upd.add("ACTIVO",(item.activo?1:0));
        upd.Where("(CODIGO_RUTA='"+item.codigo_ruta+"')");
        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_ruta item) {
        sql="DELETE FROM P_ruta WHERE (codigo='"+item.codigo_ruta+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_ruta WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_ruta item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_ruta();
            item.codigo=dt.getString(0);
            item.sucursal=dt.getString(1);
            item.nombre=dt.getString(2);
            item.codigo_ruta=dt.getInt(3);
            item.activo = (dt.getInt(4)==1?true:false);
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

    public String addItemSql(clsClasses.clsP_ruta item) {

        ins.init("P_RUTA");
        ins.add("CODIGO",item.codigo);
        ins.add("SUCURSAL",item.sucursal);
        ins.add("NOMBRE",item.nombre);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("ACTIVO",(item.activo?1:0));
        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_ruta item) {

        upd.init("P_RUTA");
        upd.add("SUCURSAL",item.sucursal);
        upd.add("NOMBRE",item.nombre);
        upd.add("CODIGO",item.codigo);
        upd.add("ACTIVO",(item.activo?1:0));
        upd.Where("(CODIGO_RUTA='"+item.codigo_ruta+"')");
        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT IFNULL(Max(CODIGO_RUTA),0)+1 AS MAX FROM P_RUTA";
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

