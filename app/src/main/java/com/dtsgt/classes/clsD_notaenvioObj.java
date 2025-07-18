package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_notaenvioObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_notaenvio";
    private String sql;
    public ArrayList<clsClasses.clsD_notaenvio> items= new ArrayList<clsClasses.clsD_notaenvio>();

    public clsD_notaenvioObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_notaenvio item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_notaenvio item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_notaenvio item) {
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

    public clsClasses.clsD_notaenvio first() {
        return items.get(0);
    }


    //region Private

    private void addItem(clsClasses.clsD_notaenvio item) {

        ins.init("D_notaenvio");

        ins.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        ins.add("CODIGO_NOTA_ENVIO_ESTATUS",item.codigo_nota_envio_estatus);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("REFERENCIA",item.referencia);
        ins.add("PERSONA_ENTREGA",item.persona_entrega);
        ins.add("FECHA",item.fecha);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_notaenvio item) {

        upd.init("D_notaenvio");

        upd.add("CODIGO_NOTA_ENVIO_ESTATUS",item.codigo_nota_envio_estatus);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("REFERENCIA",item.referencia);
        upd.add("PERSONA_ENTREGA",item.persona_entrega);
        upd.add("FECHA",item.fecha);

        upd.Where("(CODIGO_NOTA_ENVIO_ENC="+item.codigo_nota_envio_enc+")");

        db.execSQL(upd.sql());


    }

    private void deleteItem(clsClasses.clsD_notaenvio item) {
        sql="DELETE FROM D_notaenvio WHERE (CODIGO_NOTA_ENVIO_ENC="+item.codigo_nota_envio_enc+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_notaenvio WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_notaenvio item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_notaenvio();

            item.codigo_nota_envio_enc=dt.getLong(0);
            item.codigo_nota_envio_estatus=dt.getInt(1);
            item.codigo_cliente=dt.getInt(2);
            item.referencia=dt.getString(3);
            item.persona_entrega=dt.getInt(4);
            item.fecha=dt.getLong(5);

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

    public String addItemSql(clsClasses.clsD_notaenvio item) {

        ins.init("D_notaenvio");

        ins.add("CODIGO_NOTA_ENVIO_ENC",item.codigo_nota_envio_enc);
        ins.add("CODIGO_NOTA_ENVIO_ESTATUS",item.codigo_nota_envio_estatus);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("REFERENCIA",item.referencia);
        ins.add("PERSONA_ENTREGA",item.persona_entrega);
        ins.add("FECHA",item.fecha);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_notaenvio item) {

        upd.init("D_notaenvio");

        upd.add("CODIGO_NOTA_ENVIO_ESTATUS",item.codigo_nota_envio_estatus);
        upd.add("CODIGO_CLIENTE",item.codigo_cliente);
        upd.add("REFERENCIA",item.referencia);
        upd.add("PERSONA_ENTREGA",item.persona_entrega);
        upd.add("FECHA",item.fecha);

        upd.Where("(CODIGO_NOTA_ENVIO_ENC="+item.codigo_nota_envio_enc+")");

        return upd.sql();


    }

    //endregion
}

