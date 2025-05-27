package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_cxcObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_cxc";
    private String sql;
    public ArrayList<clsClasses.clsD_cxc> items= new ArrayList<clsClasses.clsD_cxc>();

    public clsD_cxcObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_cxc item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_cxc item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_cxc item) {
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

    public clsClasses.clsD_cxc first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_cxc item) {

        ins.init("D_cxc");

        ins.add("NoFactura",item.nofactura);
        ins.add("Empresa",item.empresa);
        ins.add("IdCliente",item.idcliente);
        ins.add("Fecha",item.fecha);
        ins.add("Monto_Total",item.monto_total);
        ins.add("Saldo",item.saldo);
        ins.add("IdMoneda",item.idmoneda);
        ins.add("Tipo_Cambio",item.tipo_cambio);
        ins.add("Estado",item.estado);
        ins.add("Referencia",item.referencia);
        ins.add("IdUsuario",item.idusuario);
        ins.add("DiasCredito",item.diascredito);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_cxc item) {

        upd.init("D_cxc");

        upd.add("Empresa",item.empresa);
        upd.add("IdCliente",item.idcliente);
        upd.add("Fecha",item.fecha);
        upd.add("Monto_Total",item.monto_total);
        upd.add("Saldo",item.saldo);
        upd.add("IdMoneda",item.idmoneda);
        upd.add("Tipo_Cambio",item.tipo_cambio);
        upd.add("Estado",item.estado);
        upd.add("Referencia",item.referencia);
        upd.add("IdUsuario",item.idusuario);
        upd.add("DiasCredito",item.diascredito);

        upd.Where("(NoFactura='"+item.nofactura+"')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_cxc item) {
        sql="DELETE FROM D_cxc WHERE (NoFactura='"+item.nofactura+"')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql="DELETE FROM D_cxc WHERE id='" + id+"'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_cxc item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_cxc();

            item.nofactura=dt.getString(0);
            item.empresa=dt.getInt(1);
            item.idcliente=dt.getInt(2);
            item.fecha=dt.getLong(3);
            item.monto_total=dt.getDouble(4);
            item.saldo=dt.getDouble(5);
            item.idmoneda=dt.getInt(6);
            item.tipo_cambio=dt.getDouble(7);
            item.estado=dt.getString(8);
            item.referencia=dt.getString(9);
            item.idusuario=dt.getInt(10);
            item.diascredito=dt.getInt(11);

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

    public String addItemSql(clsClasses.clsD_cxc item) {

        ins.init("D_cxc");

        ins.add("NoFactura",item.nofactura);
        ins.add("Empresa",item.empresa);
        ins.add("IdCliente",item.idcliente);
        ins.add("Fecha",item.fecha);
        ins.add("Monto_Total",item.monto_total);
        ins.add("Saldo",item.saldo);
        ins.add("IdMoneda",item.idmoneda);
        ins.add("Tipo_Cambio",item.tipo_cambio);
        ins.add("Estado",item.estado);
        ins.add("Referencia",item.referencia);
        ins.add("IdUsuario",item.idusuario);
        ins.add("DiasCredito",item.diascredito);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_cxc item) {

        upd.init("D_cxc");

        upd.add("Empresa",item.empresa);
        upd.add("IdCliente",item.idcliente);
        upd.add("Fecha",item.fecha);
        upd.add("Monto_Total",item.monto_total);
        upd.add("Saldo",item.saldo);
        upd.add("IdMoneda",item.idmoneda);
        upd.add("Tipo_Cambio",item.tipo_cambio);
        upd.add("Estado",item.estado);
        upd.add("Referencia",item.referencia);
        upd.add("IdUsuario",item.idusuario);
        upd.add("DiasCredito",item.diascredito);

        upd.Where("(NoFactura='"+item.nofactura+"')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

