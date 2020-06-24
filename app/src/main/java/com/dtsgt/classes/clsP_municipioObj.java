package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

public class clsP_municipioObj {
    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM P_municipio";
    private String sql;
    public ArrayList<clsClasses.clsP_municipio> items= new ArrayList<clsClasses.clsP_municipio>();

    public clsP_municipioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_municipio item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_municipio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_municipio item) {
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

    public clsClasses.clsP_municipio first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_municipio item) {

        ins.init("P_MUNICIPIO");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        ins.add("NOMBRE",item.nombre);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_municipio item) {

        upd.init("P_MUNICIPIO");

        upd.add("CODIGO",item.codigo);
        upd.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO="+item.codigo+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_municipio item) {
        sql="DELETE FROM P_MUNICIPIO WHERE (CODIGO="+item.codigo+")";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_MUNICIPIO WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {

        Cursor dt;
        clsClasses.clsP_municipio item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_municipio();

            item.codigo=dt.getString(0);
            item.codigo_departamento=dt.getString(1);
            item.nombre=dt.getString(2);

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

    public String addItemSql(clsClasses.clsP_municipio item) {

        ins.init("P_municipio");

        ins.add("CODIGO",item.codigo);
        ins.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        ins.add("NOMBRE",item.nombre);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_municipio item) {

        upd.init("P_MUNICIPIO");

        upd.add("CODIGO",item.codigo);
        upd.add("CODIGO_DEPARTAMENTO",item.codigo_departamento);
        upd.add("NOMBRE",item.nombre);

        upd.Where("(CODIGO="+item.codigo+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT IFNULL(Max(CODIGO_LINEA),0)+1 AS MAX FROM P_MUNICIPIO";
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
