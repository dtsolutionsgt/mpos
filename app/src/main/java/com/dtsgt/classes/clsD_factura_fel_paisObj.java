package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsD_factura_fel_paisObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel="SELECT * FROM D_factura_fel_pais";
    private String sql;
    public ArrayList<clsClasses.clsD_factura_fel_pais> items= new ArrayList<clsClasses.clsD_factura_fel_pais>();

    public clsD_factura_fel_paisObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsD_factura_fel_pais item) {
        addItem(item);
    }

    public void update(clsClasses.clsD_factura_fel_pais item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsD_factura_fel_pais item) {
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

    public clsClasses.clsD_factura_fel_pais first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsD_factura_fel_pais item) {

        ins.init("D_factura_fel_pais");

        ins.add("codigo_factura",item.codigo_factura);
        ins.add("empresa",item.empresa);
        ins.add("corel",item.corel);
        ins.add("codigo_pais",item.codigo_pais);
        ins.add("codigo_moneda",item.codigo_moneda);
        ins.add("fec_agr",item.fec_agr);
        ins.add("SV_mensaje",item.sv_mensaje);
        ins.add("SV_pdf_path",item.sv_pdf_path);
        ins.add("SV_identificador",item.sv_identificador);
        ins.add("SV_codigoGeneracion",item.sv_codigogeneracion);
        ins.add("SV_selloRecepcion",item.sv_sellorecepcion);
        ins.add("SV_numeroControl",item.sv_numerocontrol);
        ins.add("SV_status",item.sv_status);
        ins.add("SV_fechaEmision",item.sv_fechaemision);
        ins.add("SV_estado",item.sv_estado);
        ins.add("SV_totalNoSuj",item.sv_totalnosuj);
        ins.add("SV_totalExenta",item.sv_totalexenta);
        ins.add("SV_totalGravada",item.sv_totalgravada);
        ins.add("SV_subTotalVentas",item.sv_subtotalventas);
        ins.add("SV_descuNoSuj",item.sv_descunosuj);
        ins.add("SV_descuExenta",item.sv_descuexenta);
        ins.add("SV_descuGravada",item.sv_descugravada);
        ins.add("SV_porcentajeDescuento",item.sv_porcentajedescuento);
        ins.add("SV_totalDescu",item.sv_totaldescu);
        ins.add("SV_subTotal",item.sv_subtotal);
        ins.add("SV_ivaRete1",item.sv_ivarete1);
        ins.add("SV_reteRenta",item.sv_reterenta);
        ins.add("SV_montoTotalOperacion",item.sv_montototaloperacion);
        ins.add("SV_totalNoGravado",item.sv_totalnogravado);
        ins.add("SV_totalPagar",item.sv_totalpagar);
        ins.add("SV_totalLetras",item.sv_totalletras);
        ins.add("SV_saldoFavor",item.sv_saldofavor);
        ins.add("SV_totalIva",item.sv_totaliva);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsD_factura_fel_pais item) {

        upd.init("D_factura_fel_pais");

        upd.add("empresa",item.empresa);
        upd.add("corel",item.corel);
        upd.add("codigo_pais",item.codigo_pais);
        upd.add("codigo_moneda",item.codigo_moneda);
        upd.add("fec_agr",item.fec_agr);
        upd.add("SV_mensaje",item.sv_mensaje);
        upd.add("SV_pdf_path",item.sv_pdf_path);
        upd.add("SV_identificador",item.sv_identificador);
        upd.add("SV_codigoGeneracion",item.sv_codigogeneracion);
        upd.add("SV_selloRecepcion",item.sv_sellorecepcion);
        upd.add("SV_numeroControl",item.sv_numerocontrol);
        upd.add("SV_status",item.sv_status);
        upd.add("SV_fechaEmision",item.sv_fechaemision);
        upd.add("SV_estado",item.sv_estado);
        upd.add("SV_totalNoSuj",item.sv_totalnosuj);
        upd.add("SV_totalExenta",item.sv_totalexenta);
        upd.add("SV_totalGravada",item.sv_totalgravada);
        upd.add("SV_subTotalVentas",item.sv_subtotalventas);
        upd.add("SV_descuNoSuj",item.sv_descunosuj);
        upd.add("SV_descuExenta",item.sv_descuexenta);
        upd.add("SV_descuGravada",item.sv_descugravada);
        upd.add("SV_porcentajeDescuento",item.sv_porcentajedescuento);
        upd.add("SV_totalDescu",item.sv_totaldescu);
        upd.add("SV_subTotal",item.sv_subtotal);
        upd.add("SV_ivaRete1",item.sv_ivarete1);
        upd.add("SV_reteRenta",item.sv_reterenta);
        upd.add("SV_montoTotalOperacion",item.sv_montototaloperacion);
        upd.add("SV_totalNoGravado",item.sv_totalnogravado);
        upd.add("SV_totalPagar",item.sv_totalpagar);
        upd.add("SV_totalLetras",item.sv_totalletras);
        upd.add("SV_saldoFavor",item.sv_saldofavor);
        upd.add("SV_totalIva",item.sv_totaliva);

        upd.Where("(codigo_factura="+item.codigo_factura+")");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsD_factura_fel_pais item) {
        sql="DELETE FROM D_factura_fel_pais WHERE (codigo_factura="+item.codigo_factura+")";
        db.execSQL(sql);
    }

    private void deleteItem(int id) {
        sql="DELETE FROM D_factura_fel_pais WHERE id=" + id;
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsD_factura_fel_pais item;

        items.clear();

        dt=Con.OpenDT(sq);
        count =dt.getCount();
        if (dt.getCount()>0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsD_factura_fel_pais();

            item.codigo_factura=dt.getInt(0);
            item.empresa=dt.getInt(1);
            item.corel=dt.getString(2);
            item.codigo_pais=dt.getString(3);
            item.codigo_moneda=dt.getInt(4);
            item.fec_agr=dt.getLong(5);
            item.sv_mensaje=dt.getString(6);
            item.sv_pdf_path=dt.getString(7);
            item.sv_identificador=dt.getString(8);
            item.sv_codigogeneracion=dt.getString(9);
            item.sv_sellorecepcion=dt.getString(10);
            item.sv_numerocontrol=dt.getString(11);
            item.sv_status=dt.getString(12);
            item.sv_fechaemision=dt.getString(13);
            item.sv_estado=dt.getString(14);
            item.sv_totalnosuj=dt.getDouble(15);
            item.sv_totalexenta=dt.getDouble(16);
            item.sv_totalgravada=dt.getDouble(17);
            item.sv_subtotalventas=dt.getDouble(18);
            item.sv_descunosuj=dt.getDouble(19);
            item.sv_descuexenta=dt.getDouble(20);
            item.sv_descugravada=dt.getDouble(21);
            item.sv_porcentajedescuento=dt.getDouble(22);
            item.sv_totaldescu=dt.getDouble(23);
            item.sv_subtotal=dt.getDouble(24);
            item.sv_ivarete1=dt.getDouble(25);
            item.sv_reterenta=dt.getDouble(26);
            item.sv_montototaloperacion=dt.getDouble(27);
            item.sv_totalnogravado=dt.getDouble(28);
            item.sv_totalpagar=dt.getDouble(29);
            item.sv_totalletras=dt.getString(30);
            item.sv_saldofavor=dt.getDouble(31);
            item.sv_totaliva=dt.getDouble(32);

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

    public String addItemSql(clsClasses.clsD_factura_fel_pais item) {

        ins.init("D_factura_fel_pais");

        ins.add("codigo_factura",item.codigo_factura);
        ins.add("empresa",item.empresa);
        ins.add("corel",item.corel);
        ins.add("codigo_pais",item.codigo_pais);
        ins.add("codigo_moneda",item.codigo_moneda);
        ins.add("fec_agr",item.fec_agr);
        ins.add("SV_mensaje",item.sv_mensaje);
        ins.add("SV_pdf_path",item.sv_pdf_path);
        ins.add("SV_identificador",item.sv_identificador);
        ins.add("SV_codigoGeneracion",item.sv_codigogeneracion);
        ins.add("SV_selloRecepcion",item.sv_sellorecepcion);
        ins.add("SV_numeroControl",item.sv_numerocontrol);
        ins.add("SV_status",item.sv_status);
        ins.add("SV_fechaEmision",item.sv_fechaemision);
        ins.add("SV_estado",item.sv_estado);
        ins.add("SV_totalNoSuj",item.sv_totalnosuj);
        ins.add("SV_totalExenta",item.sv_totalexenta);
        ins.add("SV_totalGravada",item.sv_totalgravada);
        ins.add("SV_subTotalVentas",item.sv_subtotalventas);
        ins.add("SV_descuNoSuj",item.sv_descunosuj);
        ins.add("SV_descuExenta",item.sv_descuexenta);
        ins.add("SV_descuGravada",item.sv_descugravada);
        ins.add("SV_porcentajeDescuento",item.sv_porcentajedescuento);
        ins.add("SV_totalDescu",item.sv_totaldescu);
        ins.add("SV_subTotal",item.sv_subtotal);
        ins.add("SV_ivaRete1",item.sv_ivarete1);
        ins.add("SV_reteRenta",item.sv_reterenta);
        ins.add("SV_montoTotalOperacion",item.sv_montototaloperacion);
        ins.add("SV_totalNoGravado",item.sv_totalnogravado);
        ins.add("SV_totalPagar",item.sv_totalpagar);
        ins.add("SV_totalLetras",item.sv_totalletras);
        ins.add("SV_saldoFavor",item.sv_saldofavor);
        ins.add("SV_totalIva",item.sv_totaliva);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsD_factura_fel_pais item) {

        upd.init("D_factura_fel_pais");

        upd.add("empresa",item.empresa);
        upd.add("corel",item.corel);
        upd.add("codigo_pais",item.codigo_pais);
        upd.add("codigo_moneda",item.codigo_moneda);
        upd.add("fec_agr",item.fec_agr);
        upd.add("SV_mensaje",item.sv_mensaje);
        upd.add("SV_pdf_path",item.sv_pdf_path);
        upd.add("SV_identificador",item.sv_identificador);
        upd.add("SV_codigoGeneracion",item.sv_codigogeneracion);
        upd.add("SV_selloRecepcion",item.sv_sellorecepcion);
        upd.add("SV_numeroControl",item.sv_numerocontrol);
        upd.add("SV_status",item.sv_status);
        upd.add("SV_fechaEmision",item.sv_fechaemision);
        upd.add("SV_estado",item.sv_estado);
        upd.add("SV_totalNoSuj",item.sv_totalnosuj);
        upd.add("SV_totalExenta",item.sv_totalexenta);
        upd.add("SV_totalGravada",item.sv_totalgravada);
        upd.add("SV_subTotalVentas",item.sv_subtotalventas);
        upd.add("SV_descuNoSuj",item.sv_descunosuj);
        upd.add("SV_descuExenta",item.sv_descuexenta);
        upd.add("SV_descuGravada",item.sv_descugravada);
        upd.add("SV_porcentajeDescuento",item.sv_porcentajedescuento);
        upd.add("SV_totalDescu",item.sv_totaldescu);
        upd.add("SV_subTotal",item.sv_subtotal);
        upd.add("SV_ivaRete1",item.sv_ivarete1);
        upd.add("SV_reteRenta",item.sv_reterenta);
        upd.add("SV_montoTotalOperacion",item.sv_montototaloperacion);
        upd.add("SV_totalNoGravado",item.sv_totalnogravado);
        upd.add("SV_totalPagar",item.sv_totalpagar);
        upd.add("SV_totalLetras",item.sv_totalletras);
        upd.add("SV_saldoFavor",item.sv_saldofavor);
        upd.add("SV_totalIva",item.sv_totaliva);

        upd.Where("(codigo_factura="+item.codigo_factura+")");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

