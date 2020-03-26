package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_bonifObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_bonif";
    private String sql;
    public ArrayList<clsClasses.clsP_bonif> items= new ArrayList<clsClasses.clsP_bonif>();

    public clsP_bonifObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_bonif item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_bonif item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_bonif item) {
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

    public clsClasses.clsP_bonif first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_bonif item) {

        ins.init("P_bonif");

        ins.add("CLIENTE",item.cliente);
        ins.add("CTIPO",item.ctipo);
        ins.add("PRODUCTO",item.producto);
        ins.add("PTIPO",item.ptipo);
        ins.add("TIPORUTA",item.tiporuta);
        ins.add("TIPOBON",item.tipobon);
        ins.add("RANGOINI",item.rangoini);
        ins.add("RANGOFIN",item.rangofin);
        ins.add("TIPOLISTA",item.tipolista);
        ins.add("TIPOCANT",item.tipocant);
        ins.add("VALOR",item.valor);
        ins.add("LISTA",item.lista);
        ins.add("CANTEXACT",item.cantexact);
        ins.add("GLOBBON",item.globbon);
        ins.add("PORCANT",item.porcant);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("CODDESC",item.coddesc);
        ins.add("NOMBRE",item.nombre);
        ins.add("EMP",item.emp);
        ins.add("UMPRODUCTO",item.umproducto);
        ins.add("UMBONIFICACION",item.umbonificacion);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_bonif item) {

        upd.init("P_bonif");

        upd.add("RANGOFIN",item.rangofin);
        upd.add("TIPOLISTA",item.tipolista);
        upd.add("TIPOCANT",item.tipocant);
        upd.add("VALOR",item.valor);
        upd.add("LISTA",item.lista);
        upd.add("CANTEXACT",item.cantexact);
        upd.add("GLOBBON",item.globbon);
        upd.add("PORCANT",item.porcant);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("CODDESC",item.coddesc);
        upd.add("NOMBRE",item.nombre);
        upd.add("UMPRODUCTO",item.umproducto);
        upd.add("UMBONIFICACION",item.umbonificacion);

        upd.Where("(CLIENTE='"+item.cliente+"') AND (CTIPO="+item.ctipo+") AND (PRODUCTO='"+item.producto+"') AND (PTIPO="+item.ptipo+") AND (TIPORUTA="+item.tiporuta+") AND (TIPOBON='"+item.tipobon+"') AND (RANGOINI="+item.rangoini+") AND (EMP='"+item.emp+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_bonif item) {
        sql="DELETE FROM P_bonif WHERE (CLIENTE='"+item.cliente+"') AND (CTIPO="+item.ctipo+") AND (PRODUCTO='"+item.producto+"') AND (PTIPO="+item.ptipo+") AND (TIPORUTA="+item.tiporuta+") AND (TIPOBON='"+item.tipobon+"') AND (RANGOINI="+item.rangoini+") AND (EMP='"+item.emp+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM P_bonif WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_bonif item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_bonif();

            item.cliente=dt.getString(0);
            item.ctipo=dt.getInt(1);
            item.producto=dt.getString(2);
            item.ptipo=dt.getInt(3);
            item.tiporuta=dt.getInt(4);
            item.tipobon=dt.getString(5);
            item.rangoini=dt.getDouble(6);
            item.rangofin=dt.getDouble(7);
            item.tipolista=dt.getInt(8);
            item.tipocant=dt.getString(9);
            item.valor=dt.getDouble(10);
            item.lista=dt.getString(11);
            item.cantexact=dt.getString(12);
            item.globbon=dt.getString(13);
            item.porcant=dt.getString(14);
            item.fechaini=dt.getInt(15);
            item.fechafin=dt.getInt(16);
            item.coddesc=dt.getInt(17);
            item.nombre=dt.getString(18);
            item.emp=dt.getString(19);
            item.umproducto=dt.getString(20);
            item.umbonificacion=dt.getString(21);

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

    public String addItemSql(clsClasses.clsP_bonif item) {

        ins.init("P_bonif");

        ins.add("CLIENTE",item.cliente);
        ins.add("CTIPO",item.ctipo);
        ins.add("PRODUCTO",item.producto);
        ins.add("PTIPO",item.ptipo);
        ins.add("TIPORUTA",item.tiporuta);
        ins.add("TIPOBON",item.tipobon);
        ins.add("RANGOINI",item.rangoini);
        ins.add("RANGOFIN",item.rangofin);
        ins.add("TIPOLISTA",item.tipolista);
        ins.add("TIPOCANT",item.tipocant);
        ins.add("VALOR",item.valor);
        ins.add("LISTA",item.lista);
        ins.add("CANTEXACT",item.cantexact);
        ins.add("GLOBBON",item.globbon);
        ins.add("PORCANT",item.porcant);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("CODDESC",item.coddesc);
        ins.add("NOMBRE",item.nombre);
        ins.add("EMP",item.emp);
        ins.add("UMPRODUCTO",item.umproducto);
        ins.add("UMBONIFICACION",item.umbonificacion);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_bonif item) {

        upd.init("P_bonif");

        upd.add("RANGOFIN",item.rangofin);
        upd.add("TIPOLISTA",item.tipolista);
        upd.add("TIPOCANT",item.tipocant);
        upd.add("VALOR",item.valor);
        upd.add("LISTA",item.lista);
        upd.add("CANTEXACT",item.cantexact);
        upd.add("GLOBBON",item.globbon);
        upd.add("PORCANT",item.porcant);
        upd.add("FECHAINI",item.fechaini);
        upd.add("FECHAFIN",item.fechafin);
        upd.add("CODDESC",item.coddesc);
        upd.add("NOMBRE",item.nombre);
        upd.add("UMPRODUCTO",item.umproducto);
        upd.add("UMBONIFICACION",item.umbonificacion);

        upd.Where("(CLIENTE='"+item.cliente+"') AND (CTIPO="+item.ctipo+") AND (PRODUCTO='"+item.producto+"') AND (PTIPO="+item.ptipo+") AND (TIPORUTA="+item.tiporuta+") AND (TIPOBON='"+item.tipobon+"') AND (RANGOINI="+item.rangoini+") AND (EMP='"+item.emp+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

