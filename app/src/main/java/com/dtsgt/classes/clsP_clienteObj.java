package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsP_clienteObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM P_cliente";
    private String sql;
    public ArrayList<clsClasses.clsP_cliente> items= new ArrayList<clsClasses.clsP_cliente>();

    public clsP_clienteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_cliente item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_cliente item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_cliente item) {
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

    public clsClasses.clsP_cliente first() {
        return items.get(0);
    }

    public int maxID() {

        int maximo=0;
        Cursor dt;

        try{
            sql = "SELECT MAX(CAST(CODIGO AS INTEGER))+1 FROM P_cliente";
            dt = Con.OpenDT(sql);
            count = dt.getCount();
            if (dt.getCount() > 0) {
                dt.moveToFirst();
                maximo =  dt.getInt(0);
            }
        }catch (Exception ex){
            maximo=0;
        }

        return maximo;
    }

    private int nitnum(String nit) {
        int pp;

        try {
            nit=nit.toUpperCase();
            pp=nit.indexOf("-");
            if (pp<0) return 0;

            int A=(int) nit.charAt(pp+1);
            String snit=nit.substring(0,pp)+A;
            int nnit=Integer.parseInt(snit);

            return nnit;
        } catch (Exception e) {
            return -1;
        }
    }

    // Private

    private void addItem(clsClasses.clsP_cliente item) {

        int codigo=nitnum(item.nit);

        if (codigo!=0){

            ins.init("P_cliente");

            ins.add("CODIGO_CLIENTE",codigo);
            ins.add("CODIGO",item.codigo);
            ins.add("EMPRESA",item.empresa);
            ins.add("NOMBRE",item.nombre);
            ins.add("BLOQUEADO",item.bloqueado);
            ins.add("NIVELPRECIO",item.nivelprecio);
            ins.add("MEDIAPAGO",item.mediapago);
            ins.add("LIMITECREDITO",item.limitecredito);
            ins.add("DIACREDITO",item.diacredito);
            ins.add("DESCUENTO",item.descuento);
            ins.add("BONIFICACION",item.bonificacion);
            ins.add("ULTVISITA",item.ultvisita);
            ins.add("IMPSPEC",item.impspec);
            ins.add("NIT",item.nit);
            ins.add("EMAIL",item.email);
            ins.add("ESERVICE",item.eservice);
            ins.add("TELEFONO",item.telefono);
            ins.add("DIRECCION",item.direccion);
            ins.add("COORX",item.coorx);
            ins.add("COORY",item.coory);
            ins.add("BODEGA",item.bodega);
            ins.add("COD_PAIS",item.cod_pais);
            ins.add("CODBARRA",item.codbarra);
            ins.add("PERCEPCION",item.percepcion);
            ins.add("TIPO_CONTRIBUYENTE",item.tipo_contribuyente);
            ins.add("IMAGEN",item.imagen);

            db.execSQL(ins.sql());

        }

    }

    private void updateItem(clsClasses.clsP_cliente item) {

        upd.init("P_cliente");

        upd.add("CODIGO",item.codigo);
        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("BLOQUEADO",item.bloqueado);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("MEDIAPAGO",item.mediapago);
        upd.add("LIMITECREDITO",item.limitecredito);
        upd.add("DIACREDITO",item.diacredito);
        upd.add("DESCUENTO",item.descuento);
        upd.add("BONIFICACION",item.bonificacion);
        upd.add("ULTVISITA",item.ultvisita);
        upd.add("IMPSPEC",item.impspec);
        upd.add("NIT",item.nit);
        upd.add("EMAIL",item.email);
        upd.add("ESERVICE",item.eservice);
        upd.add("TELEFONO",item.telefono);
        upd.add("DIRECCION",item.direccion);
        upd.add("COORX",item.coorx);
        upd.add("COORY",item.coory);
        upd.add("BODEGA",item.bodega);
        upd.add("COD_PAIS",item.cod_pais);
        upd.add("CODBARRA",item.codbarra);
        upd.add("PERCEPCION",item.percepcion);
        upd.add("TIPO_CONTRIBUYENTE",item.tipo_contribuyente);
        upd.add("IMAGEN",item.imagen);

        upd.Where("(CODIGO_CLIENTE="+item.codigo_cliente+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cliente item) {
        sql="DELETE FROM P_cliente WHERE (CODIGO_CLIENTE="+item.codigo_cliente+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM P_cliente WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cliente item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cliente();
            item.codigo_cliente=dt.getInt(0);
            item.codigo=dt.getString(1);
            item.empresa=dt.getInt(2);
            item.nombre=dt.getString(3);
            item.bloqueado=dt.getInt(4);
            item.nivelprecio=dt.getInt(5);
            item.mediapago=dt.getInt(6);
            item.limitecredito=dt.getDouble(7);
            item.diacredito=dt.getInt(8);
            item.descuento=dt.getInt(9);
            item.bonificacion=dt.getInt(10);
            item.ultvisita=dt.getLong(11);
            item.impspec=dt.getDouble(12);
            item.nit=dt.getString(13);
            item.email=dt.getString(14);
            item.eservice=dt.getString(15);
            item.telefono=dt.getString(16);
            item.direccion=dt.getString(17);
            item.coorx=dt.getDouble(18);
            item.coory=dt.getDouble(19);
            item.bodega=dt.getString(20);
            item.cod_pais=dt.getString(21);
            item.codbarra=dt.getString(22);
            item.percepcion=dt.getDouble(23);
            item.tipo_contribuyente=dt.getString(24);
            item.imagen=dt.getString(25);
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

    public String addItemSql(clsClasses.clsP_cliente item) {

        ins.init("P_cliente");

        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("CODIGO",item.codigo);
        ins.add("EMPRESA",item.empresa);
        ins.add("NOMBRE",item.nombre);
        ins.add("BLOQUEADO",item.bloqueado);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("MEDIAPAGO",item.mediapago);
        ins.add("LIMITECREDITO",item.limitecredito);
        ins.add("DIACREDITO",item.diacredito);
        ins.add("DESCUENTO",item.descuento);
        ins.add("BONIFICACION",item.bonificacion);
        ins.add("ULTVISITA",item.ultvisita);
        ins.add("IMPSPEC",item.impspec);
        ins.add("NIT",item.nit);
        ins.add("EMAIL",item.email);
        ins.add("ESERVICE",item.eservice);
        ins.add("TELEFONO",item.telefono);
        ins.add("DIRECCION",item.direccion);
        ins.add("COORX",item.coorx);
        ins.add("COORY",item.coory);
        ins.add("BODEGA",item.bodega);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("CODBARRA",item.codbarra);
        ins.add("PERCEPCION",item.percepcion);
        ins.add("TIPO_CONTRIBUYENTE",item.tipo_contribuyente);
        ins.add("IMAGEN",item.imagen);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cliente item) {

        upd.init("P_cliente");

        upd.add("CODIGO",item.codigo);
        upd.add("EMPRESA",item.empresa);
        upd.add("NOMBRE",item.nombre);
        upd.add("BLOQUEADO",item.bloqueado);
        upd.add("NIVELPRECIO",item.nivelprecio);
        upd.add("MEDIAPAGO",item.mediapago);
        upd.add("LIMITECREDITO",item.limitecredito);
        upd.add("DIACREDITO",item.diacredito);
        upd.add("DESCUENTO",item.descuento);
        upd.add("BONIFICACION",item.bonificacion);
        upd.add("ULTVISITA",item.ultvisita);
        upd.add("IMPSPEC",item.impspec);
        upd.add("NIT",item.nit);
        upd.add("EMAIL",item.email);
        upd.add("ESERVICE",item.eservice);
        upd.add("TELEFONO",item.telefono);
        upd.add("DIRECCION",item.direccion);
        upd.add("COORX",item.coorx);
        upd.add("COORY",item.coory);
        upd.add("BODEGA",item.bodega);
        upd.add("COD_PAIS",item.cod_pais);
        upd.add("CODBARRA",item.codbarra);
        upd.add("PERCEPCION",item.percepcion);
        upd.add("TIPO_CONTRIBUYENTE",item.tipo_contribuyente);
        upd.add("IMAGEN",item.imagen);

        upd.Where("(CODIGO_CLIENTE="+item.codigo_cliente+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }
    public String addItemSql(clsClasses.clsP_cliente item,int idemp) {

        ins.init("P_cliente");

        ins.add("EMPRESA",idemp);
        ins.add("CODIGO",item.codigo);



        ins.add("NOMBRE",item.nombre);
        ins.add("BLOQUEADO",item.bloqueado);
        ins.add("NIVELPRECIO",item.nivelprecio);
        ins.add("MEDIAPAGO",item.mediapago);
        ins.add("LIMITECREDITO",item.limitecredito);
        ins.add("DIACREDITO",item.diacredito);
        ins.add("DESCUENTO",item.descuento);
        ins.add("BONIFICACION",item.bonificacion);
        ins.add("ULTVISITA",item.ultvisita);
        ins.add("IMPSPEC",item.impspec);
        ins.add("NIT",item.nit);
        ins.add("EMAIL",item.email);
        ins.add("ESERVICE",item.eservice);
        ins.add("TELEFONO",item.telefono);
        ins.add("DIRECCION",item.direccion);
        ins.add("COORX",item.coorx);
        ins.add("COORY",item.coory);
        ins.add("BODEGA",item.bodega);
        ins.add("COD_PAIS",item.cod_pais);
        ins.add("CODBARRA",item.codbarra);
        ins.add("PERCEPCION",item.percepcion);
        ins.add("TIPO_CONTRIBUYENTE",item.tipo_contribuyente);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);

        return ins.sql();

    }


}

