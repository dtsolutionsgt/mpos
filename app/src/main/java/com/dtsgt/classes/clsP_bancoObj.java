package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;

public class clsP_bancoObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();
    private MiscUtils mu;

    private String sel="SELECT * FROM P_banco";
    private String sql;
    public ArrayList<clsClasses.clsP_banco> items= new ArrayList<clsClasses.clsP_banco>();

    public clsP_bancoObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_banco item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_banco item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_banco item) {
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

    public clsClasses.clsP_banco first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_banco item) {

        ins.init("P_banco");

        ins.add("CODIGO_BANCO",maxId());
        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.tipo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CUENTA",item.cuenta);
        ins.add("EMPRESA",item.empresa);
        ins.add("ACTIVO",item.activo);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_banco item) {

        upd.init("P_banco");

        upd.add("CODIGO",item.codigo);
        upd.add("TIPO",item.tipo);
        upd.add("NOMBRE",item.nombre);
        upd.add("CUENTA",item.cuenta);
        upd.add("EMPRESA",item.empresa);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_BANCO='"+item.codigo_banco+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_banco item) {
        sql="DELETE FROM P_banco WHERE (CODIGO_BANCO='"+item.codigo_banco+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_banco WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_banco item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (count>0){
            dt.moveToFirst();
        }

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_banco();

            item.codigo=dt.getString(0);
            item.tipo=dt.getString(1);
            item.nombre=dt.getString(2);
            item.cuenta=dt.getString(3);
            item.empresa=dt.getInt(4);
            item.activo=dt.getInt(5);
            item.codigo_banco=dt.getInt(6);

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

    public String addItemSql(clsClasses.clsP_banco item) {

        ins.init("P_banco");

        ins.add("CODIGO_BANCO",maxId());
        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.tipo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CUENTA",item.cuenta);
        ins.add("EMPRESA",item.empresa);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_banco item) {

        upd.init("P_banco");

        upd.add("TIPO",item.tipo);
        upd.add("NOMBRE",item.nombre);
        upd.add("CUENTA",item.cuenta);
        upd.add("EMPRESA",item.empresa);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO_BANCO='"+item.codigo_banco+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private int maxId(){

        Cursor DT = null;
        int resultado = 0;

        try{
            String sql = "SELECT IFNULL(Max(CODIGO),1)+1 AS MAX FROM P_BANCO";
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

