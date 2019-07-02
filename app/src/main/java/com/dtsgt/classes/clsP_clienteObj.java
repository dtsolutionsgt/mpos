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

    private String sel = "SELECT * FROM P_cliente";
    private String sql;
    public ArrayList<clsClasses.clsP_cliente> items = new ArrayList<clsClasses.clsP_cliente>();

    public clsP_clienteObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont = context;
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
        db = dbase;
        count = 0;
    }

    public void reconnect(BaseDatos dbconnection, SQLiteDatabase dbase) {
        Con = dbconnection;
        ins = Con.Ins;
        upd = Con.Upd;
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

    public void delete(String id) {
        deleteItem(id);
    }

    public void fill() {
        fillItems(sel);
    }

    public void fill(String specstr) {
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_cliente first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_cliente item) {

        ins.init("P_cliente");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("BLOQUEADO", item.bloqueado);
        ins.add("TIPONEG", item.tiponeg);
        ins.add("TIPO", item.tipo);
        ins.add("SUBTIPO", item.subtipo);
        ins.add("CANAL", item.canal);
        ins.add("SUBCANAL", item.subcanal);
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("MEDIAPAGO", item.mediapago);
        ins.add("LIMITECREDITO", item.limitecredito);
        ins.add("DIACREDITO", item.diacredito);
        ins.add("DESCUENTO", item.descuento);
        ins.add("BONIFICACION", item.bonificacion);
        ins.add("ULTVISITA", item.ultvisita);
        ins.add("IMPSPEC", item.impspec);
        ins.add("INVTIPO", item.invtipo);
        ins.add("INVEQUIPO", item.invequipo);
        ins.add("INV1", item.inv1);
        ins.add("INV2", item.inv2);
        ins.add("INV3", item.inv3);
        ins.add("NIT", item.nit);
        ins.add("MENSAJE", item.mensaje);
        ins.add("EMAIL", item.email);
        ins.add("ESERVICE", item.eservice);
        ins.add("TELEFONO", item.telefono);
        ins.add("DIRTIPO", item.dirtipo);
        ins.add("DIRECCION", item.direccion);
        ins.add("REGION", item.region);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("MUNICIPIO", item.municipio);
        ins.add("CIUDAD", item.ciudad);
        ins.add("ZONA", item.zona);
        ins.add("COLONIA", item.colonia);
        ins.add("AVENIDA", item.avenida);
        ins.add("CALLE", item.calle);
        ins.add("NUMERO", item.numero);
        ins.add("CARTOGRAFICO", item.cartografico);
        ins.add("COORX", item.coorx);
        ins.add("COORY", item.coory);
        ins.add("BODEGA", item.bodega);
        ins.add("COD_PAIS", item.cod_pais);
        ins.add("FIRMADIG", item.firmadig);
        ins.add("CODBARRA", item.codbarra);
        ins.add("VALIDACREDITO", item.validacredito);
        ins.add("FACT_VS_FACT", item.fact_vs_fact);
        ins.add("CHEQUEPOST", item.chequepost);
        ins.add("PRECIO_ESTRATEGICO", item.precio_estrategico);
        ins.add("NOMBRE_PROPIETARIO", item.nombre_propietario);
        ins.add("NOMBRE_REPRESENTANTE", item.nombre_representante);
        ins.add("PERCEPCION", item.percepcion);
        ins.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);
        ins.add("ID_DESPACHO", item.id_despacho);
        ins.add("ID_FACTURACION", item.id_facturacion);
        ins.add("MODIF_PRECIO", item.modif_precio);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_cliente item) {

        upd.init("P_cliente");

        upd.add("NOMBRE", item.nombre);
        upd.add("BLOQUEADO", item.bloqueado);
        upd.add("TIPONEG", item.tiponeg);
        upd.add("TIPO", item.tipo);
        upd.add("SUBTIPO", item.subtipo);
        upd.add("CANAL", item.canal);
        upd.add("SUBCANAL", item.subcanal);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("MEDIAPAGO", item.mediapago);
        upd.add("LIMITECREDITO", item.limitecredito);
        upd.add("DIACREDITO", item.diacredito);
        upd.add("DESCUENTO", item.descuento);
        upd.add("BONIFICACION", item.bonificacion);
        upd.add("ULTVISITA", item.ultvisita);
        upd.add("IMPSPEC", item.impspec);
        upd.add("INVTIPO", item.invtipo);
        upd.add("INVEQUIPO", item.invequipo);
        upd.add("INV1", item.inv1);
        upd.add("INV2", item.inv2);
        upd.add("INV3", item.inv3);
        upd.add("NIT", item.nit);
        upd.add("MENSAJE", item.mensaje);
        upd.add("EMAIL", item.email);
        upd.add("ESERVICE", item.eservice);
        upd.add("TELEFONO", item.telefono);
        upd.add("DIRTIPO", item.dirtipo);
        upd.add("DIRECCION", item.direccion);
        upd.add("REGION", item.region);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("MUNICIPIO", item.municipio);
        upd.add("CIUDAD", item.ciudad);
        upd.add("ZONA", item.zona);
        upd.add("COLONIA", item.colonia);
        upd.add("AVENIDA", item.avenida);
        upd.add("CALLE", item.calle);
        upd.add("NUMERO", item.numero);
        upd.add("CARTOGRAFICO", item.cartografico);
        upd.add("COORX", item.coorx);
        upd.add("COORY", item.coory);
        upd.add("BODEGA", item.bodega);
        upd.add("COD_PAIS", item.cod_pais);
        upd.add("FIRMADIG", item.firmadig);
        upd.add("CODBARRA", item.codbarra);
        upd.add("VALIDACREDITO", item.validacredito);
        upd.add("FACT_VS_FACT", item.fact_vs_fact);
        upd.add("CHEQUEPOST", item.chequepost);
        upd.add("PRECIO_ESTRATEGICO", item.precio_estrategico);
        upd.add("NOMBRE_PROPIETARIO", item.nombre_propietario);
        upd.add("NOMBRE_REPRESENTANTE", item.nombre_representante);
        upd.add("PERCEPCION", item.percepcion);
        upd.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);
        upd.add("ID_DESPACHO", item.id_despacho);
        upd.add("ID_FACTURACION", item.id_facturacion);
        upd.add("MODIF_PRECIO", item.modif_precio);

        upd.Where("(CODIGO='" + item.codigo + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_cliente item) {
        sql = "DELETE FROM P_cliente WHERE (CODIGO='" + item.codigo + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_cliente WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_cliente item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_cliente();

            item.codigo = dt.getString(0);
            item.nombre = dt.getString(1);
            item.bloqueado = dt.getString(2);
            item.tiponeg = dt.getString(3);
            item.tipo = dt.getString(4);
            item.subtipo = dt.getString(5);
            item.canal = dt.getString(6);
            item.subcanal = dt.getString(7);
            item.nivelprecio = dt.getInt(8);
            item.mediapago = dt.getString(9);
            item.limitecredito = dt.getDouble(10);
            item.diacredito = dt.getInt(11);
            item.descuento = dt.getString(12);
            item.bonificacion = dt.getString(13);
            item.ultvisita = dt.getInt(14);
            item.impspec = dt.getDouble(15);
            item.invtipo = dt.getString(16);
            item.invequipo = dt.getString(17);
            item.inv1 = dt.getString(18);
            item.inv2 = dt.getString(19);
            item.inv3 = dt.getString(20);
            item.nit = dt.getString(21);
            item.mensaje = dt.getString(22);
            item.email = dt.getString(23);
            item.eservice = dt.getString(24);
            item.telefono = dt.getString(25);
            item.dirtipo = dt.getString(26);
            item.direccion = dt.getString(27);
            item.region = dt.getString(28);
            item.sucursal = dt.getString(29);
            item.municipio = dt.getString(30);
            item.ciudad = dt.getString(31);
            item.zona = dt.getInt(32);
            item.colonia = dt.getString(33);
            item.avenida = dt.getString(34);
            item.calle = dt.getString(35);
            item.numero = dt.getString(36);
            item.cartografico = dt.getString(37);
            item.coorx = dt.getDouble(38);
            item.coory = dt.getDouble(39);
            item.bodega = dt.getString(40);
            item.cod_pais = dt.getString(41);
            item.firmadig = dt.getString(42);
            item.codbarra = dt.getString(43);
            item.validacredito = dt.getString(44);
            item.fact_vs_fact = dt.getString(45);
            item.chequepost = dt.getString(46);
            item.precio_estrategico = dt.getString(47);
            item.nombre_propietario = dt.getString(48);
            item.nombre_representante = dt.getString(49);
            item.percepcion = dt.getDouble(50);
            item.tipo_contribuyente = dt.getString(51);
            item.id_despacho = dt.getInt(52);
            item.id_facturacion = dt.getInt(53);
            item.modif_precio = dt.getInt(54);

            items.add(item);

            dt.moveToNext();
        }

    }

    public int newID(String idsql) {
        Cursor dt;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        return nid;
    }

    public String addItemSql(clsClasses.clsP_cliente item) {

        ins.init("P_cliente");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("BLOQUEADO", item.bloqueado);
        ins.add("TIPONEG", item.tiponeg);
        ins.add("TIPO", item.tipo);
        ins.add("SUBTIPO", item.subtipo);
        ins.add("CANAL", item.canal);
        ins.add("SUBCANAL", item.subcanal);
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("MEDIAPAGO", item.mediapago);
        ins.add("LIMITECREDITO", item.limitecredito);
        ins.add("DIACREDITO", item.diacredito);
        ins.add("DESCUENTO", item.descuento);
        ins.add("BONIFICACION", item.bonificacion);
        ins.add("ULTVISITA", item.ultvisita);
        ins.add("IMPSPEC", item.impspec);
        ins.add("INVTIPO", item.invtipo);
        ins.add("INVEQUIPO", item.invequipo);
        ins.add("INV1", item.inv1);
        ins.add("INV2", item.inv2);
        ins.add("INV3", item.inv3);
        ins.add("NIT", item.nit);
        ins.add("MENSAJE", item.mensaje);
        ins.add("EMAIL", item.email);
        ins.add("ESERVICE", item.eservice);
        ins.add("TELEFONO", item.telefono);
        ins.add("DIRTIPO", item.dirtipo);
        ins.add("DIRECCION", item.direccion);
        ins.add("REGION", item.region);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("MUNICIPIO", item.municipio);
        ins.add("CIUDAD", item.ciudad);
        ins.add("ZONA", item.zona);
        ins.add("COLONIA", item.colonia);
        ins.add("AVENIDA", item.avenida);
        ins.add("CALLE", item.calle);
        ins.add("NUMERO", item.numero);
        ins.add("CARTOGRAFICO", item.cartografico);
        ins.add("COORX", item.coorx);
        ins.add("COORY", item.coory);
        ins.add("BODEGA", item.bodega);
        ins.add("COD_PAIS", item.cod_pais);
        ins.add("FIRMADIG", item.firmadig);
        ins.add("CODBARRA", item.codbarra);
        ins.add("VALIDACREDITO", item.validacredito);
        ins.add("FACT_VS_FACT", item.fact_vs_fact);
        ins.add("CHEQUEPOST", item.chequepost);
        ins.add("PRECIO_ESTRATEGICO", item.precio_estrategico);
        ins.add("NOMBRE_PROPIETARIO", item.nombre_propietario);
        ins.add("NOMBRE_REPRESENTANTE", item.nombre_representante);
        ins.add("PERCEPCION", item.percepcion);
        ins.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);
        ins.add("ID_DESPACHO", item.id_despacho);
        ins.add("ID_FACTURACION", item.id_facturacion);
        ins.add("MODIF_PRECIO", item.modif_precio);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_cliente item) {

        upd.init("P_cliente");

        upd.add("NOMBRE", item.nombre);
        upd.add("BLOQUEADO", item.bloqueado);
        upd.add("TIPONEG", item.tiponeg);
        upd.add("TIPO", item.tipo);
        upd.add("SUBTIPO", item.subtipo);
        upd.add("CANAL", item.canal);
        upd.add("SUBCANAL", item.subcanal);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("MEDIAPAGO", item.mediapago);
        upd.add("LIMITECREDITO", item.limitecredito);
        upd.add("DIACREDITO", item.diacredito);
        upd.add("DESCUENTO", item.descuento);
        upd.add("BONIFICACION", item.bonificacion);
        upd.add("ULTVISITA", item.ultvisita);
        upd.add("IMPSPEC", item.impspec);
        upd.add("INVTIPO", item.invtipo);
        upd.add("INVEQUIPO", item.invequipo);
        upd.add("INV1", item.inv1);
        upd.add("INV2", item.inv2);
        upd.add("INV3", item.inv3);
        upd.add("NIT", item.nit);
        upd.add("MENSAJE", item.mensaje);
        upd.add("EMAIL", item.email);
        upd.add("ESERVICE", item.eservice);
        upd.add("TELEFONO", item.telefono);
        upd.add("DIRTIPO", item.dirtipo);
        upd.add("DIRECCION", item.direccion);
        upd.add("REGION", item.region);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("MUNICIPIO", item.municipio);
        upd.add("CIUDAD", item.ciudad);
        upd.add("ZONA", item.zona);
        upd.add("COLONIA", item.colonia);
        upd.add("AVENIDA", item.avenida);
        upd.add("CALLE", item.calle);
        upd.add("NUMERO", item.numero);
        upd.add("CARTOGRAFICO", item.cartografico);
        upd.add("COORX", item.coorx);
        upd.add("COORY", item.coory);
        upd.add("BODEGA", item.bodega);
        upd.add("COD_PAIS", item.cod_pais);
        upd.add("FIRMADIG", item.firmadig);
        upd.add("CODBARRA", item.codbarra);
        upd.add("VALIDACREDITO", item.validacredito);
        upd.add("FACT_VS_FACT", item.fact_vs_fact);
        upd.add("CHEQUEPOST", item.chequepost);
        upd.add("PRECIO_ESTRATEGICO", item.precio_estrategico);
        upd.add("NOMBRE_PROPIETARIO", item.nombre_propietario);
        upd.add("NOMBRE_REPRESENTANTE", item.nombre_representante);
        upd.add("PERCEPCION", item.percepcion);
        upd.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);
        upd.add("ID_DESPACHO", item.id_despacho);
        upd.add("ID_FACTURACION", item.id_facturacion);
        upd.add("MODIF_PRECIO", item.modif_precio);

        upd.Where("(CODIGO='" + item.codigo + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

