package com.dtsgt.classes;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsP_rutaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_ruta";
    private String sql;
    public ArrayList<clsClasses.clsP_ruta> items = new ArrayList<clsClasses.clsP_ruta>();

    public clsP_rutaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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
        fillItems(sel + " " + specstr);
    }

    public void fillSelect(String sq) {
        fillItems(sq);
    }

    public clsClasses.clsP_ruta first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_ruta item) {

        ins.init("P_ruta");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("VENDEDOR", item.vendedor);
        ins.add("VENTA", item.venta);
        ins.add("FORANIA", item.forania);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("TIPO", item.tipo);
        ins.add("SUBTIPO", item.subtipo);
        ins.add("BODEGA", item.bodega);
        ins.add("SUBBODEGA", item.subbodega);
        ins.add("DESCUENTO", item.descuento);
        ins.add("BONIF", item.bonif);
        ins.add("KILOMETRAJE", item.kilometraje);
        ins.add("IMPRESION", item.impresion);
        ins.add("RECIBOPROPIO", item.recibopropio);
        ins.add("CELULAR", item.celular);
        ins.add("RENTABIL", item.rentabil);
        ins.add("OFERTA", item.oferta);
        ins.add("PERCRENT", item.percrent);
        ins.add("PASARCREDITO", item.pasarcredito);
        ins.add("TECLADO", item.teclado);
        ins.add("EDITDEVPREC", item.editdevprec);
        ins.add("EDITDESC", item.editdesc);
        ins.add("PARAMS", item.params);
        ins.add("SEMANA", item.semana);
        ins.add("OBJANO", item.objano);
        ins.add("OBJMES", item.objmes);
        ins.add("SYNCFOLD", item.syncfold);
        ins.add("WLFOLD", item.wlfold);
        ins.add("FTPFOLD", item.ftpfold);
        ins.add("EMAIL", item.email);
        ins.add("LASTIMP", item.lastimp);
        ins.add("LASTCOM", item.lastcom);
        ins.add("LASTEXP", item.lastexp);
        ins.add("IMPSTAT", item.impstat);
        ins.add("EXPSTAT", item.expstat);
        ins.add("COMSTAT", item.comstat);
        ins.add("PARAM1", item.param1);
        ins.add("PARAM2", item.param2);
        ins.add("PESOLIM", item.pesolim);
        ins.add("INTERVALO_MAX", item.intervalo_max);
        ins.add("LECTURAS_VALID", item.lecturas_valid);
        ins.add("INTENTOS_LECT", item.intentos_lect);
        ins.add("HORA_INI", item.hora_ini);
        ins.add("HORA_FIN", item.hora_fin);
        ins.add("APLICACION_USA", item.aplicacion_usa);
        ins.add("PUERTO_GPS", item.puerto_gps);
        ins.add("ES_RUTA_OFICINA", item.es_ruta_oficina);
        ins.add("DILUIR_BON", item.diluir_bon);
        ins.add("PREIMPRESION_FACTURA", item.preimpresion_factura);
        ins.add("MODIFICAR_MEDIA_PAGO", item.modificar_media_pago);
        ins.add("IDIMPRESORA", item.idimpresora);
        ins.add("NUMVERSION", item.numversion);
        ins.add("FECHAVERSION", item.fechaversion);
        ins.add("ARQUITECTURA", item.arquitectura);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_ruta item) {

        upd.init("P_ruta");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("VENDEDOR", item.vendedor);
        upd.add("VENTA", item.venta);
        upd.add("FORANIA", item.forania);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("TIPO", item.tipo);
        upd.add("SUBTIPO", item.subtipo);
        upd.add("BODEGA", item.bodega);
        upd.add("SUBBODEGA", item.subbodega);
        upd.add("DESCUENTO", item.descuento);
        upd.add("BONIF", item.bonif);
        upd.add("KILOMETRAJE", item.kilometraje);
        upd.add("IMPRESION", item.impresion);
        upd.add("RECIBOPROPIO", item.recibopropio);
        upd.add("CELULAR", item.celular);
        upd.add("RENTABIL", item.rentabil);
        upd.add("OFERTA", item.oferta);
        upd.add("PERCRENT", item.percrent);
        upd.add("PASARCREDITO", item.pasarcredito);
        upd.add("TECLADO", item.teclado);
        upd.add("EDITDEVPREC", item.editdevprec);
        upd.add("EDITDESC", item.editdesc);
        upd.add("PARAMS", item.params);
        upd.add("SEMANA", item.semana);
        upd.add("OBJANO", item.objano);
        upd.add("OBJMES", item.objmes);
        upd.add("SYNCFOLD", item.syncfold);
        upd.add("WLFOLD", item.wlfold);
        upd.add("FTPFOLD", item.ftpfold);
        upd.add("EMAIL", item.email);
        upd.add("LASTIMP", item.lastimp);
        upd.add("LASTCOM", item.lastcom);
        upd.add("LASTEXP", item.lastexp);
        upd.add("IMPSTAT", item.impstat);
        upd.add("EXPSTAT", item.expstat);
        upd.add("COMSTAT", item.comstat);
        upd.add("PARAM1", item.param1);
        upd.add("PARAM2", item.param2);
        upd.add("PESOLIM", item.pesolim);
        upd.add("INTERVALO_MAX", item.intervalo_max);
        upd.add("LECTURAS_VALID", item.lecturas_valid);
        upd.add("INTENTOS_LECT", item.intentos_lect);
        upd.add("HORA_INI", item.hora_ini);
        upd.add("HORA_FIN", item.hora_fin);
        upd.add("APLICACION_USA", item.aplicacion_usa);
        upd.add("PUERTO_GPS", item.puerto_gps);
        upd.add("ES_RUTA_OFICINA", item.es_ruta_oficina);
        upd.add("DILUIR_BON", item.diluir_bon);
        upd.add("PREIMPRESION_FACTURA", item.preimpresion_factura);
        upd.add("MODIFICAR_MEDIA_PAGO", item.modificar_media_pago);
        upd.add("IDIMPRESORA", item.idimpresora);
        upd.add("NUMVERSION", item.numversion);
        upd.add("FECHAVERSION", item.fechaversion);
        upd.add("ARQUITECTURA", item.arquitectura);

        upd.Where("(CODIGO='" + item.codigo + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_ruta item) {
        sql = "DELETE FROM P_ruta WHERE (CODIGO='" + item.codigo + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_ruta WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_ruta item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_ruta();

            item.codigo = dt.getString(0);
            item.nombre = dt.getString(1);
            item.activo = dt.getString(2);
            item.vendedor = dt.getString(3);
            item.venta = dt.getString(4);
            item.forania = dt.getString(5);
            item.sucursal = dt.getString(6);
            item.tipo = dt.getString(7);
            item.subtipo = dt.getString(8);
            item.bodega = dt.getString(9);
            item.subbodega = dt.getString(10);
            item.descuento = dt.getString(11);
            item.bonif = dt.getString(12);
            item.kilometraje = dt.getString(13);
            item.impresion = dt.getString(14);
            item.recibopropio = dt.getString(15);
            item.celular = dt.getString(16);
            item.rentabil = dt.getString(17);
            item.oferta = dt.getString(18);
            item.percrent = dt.getDouble(19);
            item.pasarcredito = dt.getString(20);
            item.teclado = dt.getString(21);
            item.editdevprec = dt.getString(22);
            item.editdesc = dt.getString(23);
            item.params = dt.getString(24);
            item.semana = dt.getInt(25);
            item.objano = dt.getInt(26);
            item.objmes = dt.getInt(27);
            item.syncfold = dt.getString(28);
            item.wlfold = dt.getString(29);
            item.ftpfold = dt.getString(30);
            item.email = dt.getString(31);
            item.lastimp = dt.getInt(32);
            item.lastcom = dt.getInt(33);
            item.lastexp = dt.getInt(34);
            item.impstat = dt.getString(35);
            item.expstat = dt.getString(36);
            item.comstat = dt.getString(37);
            item.param1 = dt.getString(38);
            item.param2 = dt.getString(39);
            item.pesolim = dt.getDouble(40);
            item.intervalo_max = dt.getInt(41);
            item.lecturas_valid = dt.getInt(42);
            item.intentos_lect = dt.getInt(43);
            item.hora_ini = dt.getInt(44);
            item.hora_fin = dt.getInt(45);
            item.aplicacion_usa = dt.getInt(46);
            item.puerto_gps = dt.getInt(47);
            item.es_ruta_oficina = dt.getInt(48);
            item.diluir_bon = dt.getInt(49);
            item.preimpresion_factura = dt.getInt(50);
            item.modificar_media_pago = dt.getInt(51);
            item.idimpresora = dt.getString(52);
            item.numversion = dt.getString(53);
            item.fechaversion = dt.getInt(54);
            item.arquitectura = dt.getString(55);

            items.add(item);

            dt.moveToNext();
        }

        if (dt != null) dt.close();

    }

    public int newID(String idsql) {
        Cursor dt = null;
        int nid;

        try {
            dt = Con.OpenDT(idsql);
            dt.moveToFirst();
            nid = dt.getInt(0) + 1;
        } catch (Exception e) {
            nid = 1;
        }

        if (dt != null) dt.close();

        return nid;
    }

    public String addItemSql(clsClasses.clsP_ruta item) {

        ins.init("P_ruta");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("VENDEDOR", item.vendedor);
        ins.add("VENTA", item.venta);
        ins.add("FORANIA", item.forania);
        ins.add("SUCURSAL", item.sucursal);
        ins.add("TIPO", item.tipo);
        ins.add("SUBTIPO", item.subtipo);
        ins.add("BODEGA", item.bodega);
        ins.add("SUBBODEGA", item.subbodega);
        ins.add("DESCUENTO", item.descuento);
        ins.add("BONIF", item.bonif);
        ins.add("KILOMETRAJE", item.kilometraje);
        ins.add("IMPRESION", item.impresion);
        ins.add("RECIBOPROPIO", item.recibopropio);
        ins.add("CELULAR", item.celular);
        ins.add("RENTABIL", item.rentabil);
        ins.add("OFERTA", item.oferta);
        ins.add("PERCRENT", item.percrent);
        ins.add("PASARCREDITO", item.pasarcredito);
        ins.add("TECLADO", item.teclado);
        ins.add("EDITDEVPREC", item.editdevprec);
        ins.add("EDITDESC", item.editdesc);
        ins.add("PARAMS", item.params);
        ins.add("SEMANA", item.semana);
        ins.add("OBJANO", item.objano);
        ins.add("OBJMES", item.objmes);
        ins.add("SYNCFOLD", item.syncfold);
        ins.add("WLFOLD", item.wlfold);
        ins.add("FTPFOLD", item.ftpfold);
        ins.add("EMAIL", item.email);
        ins.add("LASTIMP", item.lastimp);
        ins.add("LASTCOM", item.lastcom);
        ins.add("LASTEXP", item.lastexp);
        ins.add("IMPSTAT", item.impstat);
        ins.add("EXPSTAT", item.expstat);
        ins.add("COMSTAT", item.comstat);
        ins.add("PARAM1", item.param1);
        ins.add("PARAM2", item.param2);
        ins.add("PESOLIM", item.pesolim);
        ins.add("INTERVALO_MAX", item.intervalo_max);
        ins.add("LECTURAS_VALID", item.lecturas_valid);
        ins.add("INTENTOS_LECT", item.intentos_lect);
        ins.add("HORA_INI", item.hora_ini);
        ins.add("HORA_FIN", item.hora_fin);
        ins.add("APLICACION_USA", item.aplicacion_usa);
        ins.add("PUERTO_GPS", item.puerto_gps);
        ins.add("ES_RUTA_OFICINA", item.es_ruta_oficina);
        ins.add("DILUIR_BON", item.diluir_bon);
        ins.add("PREIMPRESION_FACTURA", item.preimpresion_factura);
        ins.add("MODIFICAR_MEDIA_PAGO", item.modificar_media_pago);
        ins.add("IDIMPRESORA", item.idimpresora);
        ins.add("NUMVERSION", item.numversion);
        ins.add("FECHAVERSION", item.fechaversion);
        ins.add("ARQUITECTURA", item.arquitectura);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_ruta item) {

        upd.init("P_ruta");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("VENDEDOR", item.vendedor);
        upd.add("VENTA", item.venta);
        upd.add("FORANIA", item.forania);
        upd.add("SUCURSAL", item.sucursal);
        upd.add("TIPO", item.tipo);
        upd.add("SUBTIPO", item.subtipo);
        upd.add("BODEGA", item.bodega);
        upd.add("SUBBODEGA", item.subbodega);
        upd.add("DESCUENTO", item.descuento);
        upd.add("BONIF", item.bonif);
        upd.add("KILOMETRAJE", item.kilometraje);
        upd.add("IMPRESION", item.impresion);
        upd.add("RECIBOPROPIO", item.recibopropio);
        upd.add("CELULAR", item.celular);
        upd.add("RENTABIL", item.rentabil);
        upd.add("OFERTA", item.oferta);
        upd.add("PERCRENT", item.percrent);
        upd.add("PASARCREDITO", item.pasarcredito);
        upd.add("TECLADO", item.teclado);
        upd.add("EDITDEVPREC", item.editdevprec);
        upd.add("EDITDESC", item.editdesc);
        upd.add("PARAMS", item.params);
        upd.add("SEMANA", item.semana);
        upd.add("OBJANO", item.objano);
        upd.add("OBJMES", item.objmes);
        upd.add("SYNCFOLD", item.syncfold);
        upd.add("WLFOLD", item.wlfold);
        upd.add("FTPFOLD", item.ftpfold);
        upd.add("EMAIL", item.email);
        upd.add("LASTIMP", item.lastimp);
        upd.add("LASTCOM", item.lastcom);
        upd.add("LASTEXP", item.lastexp);
        upd.add("IMPSTAT", item.impstat);
        upd.add("EXPSTAT", item.expstat);
        upd.add("COMSTAT", item.comstat);
        upd.add("PARAM1", item.param1);
        upd.add("PARAM2", item.param2);
        upd.add("PESOLIM", item.pesolim);
        upd.add("INTERVALO_MAX", item.intervalo_max);
        upd.add("LECTURAS_VALID", item.lecturas_valid);
        upd.add("INTENTOS_LECT", item.intentos_lect);
        upd.add("HORA_INI", item.hora_ini);
        upd.add("HORA_FIN", item.hora_fin);
        upd.add("APLICACION_USA", item.aplicacion_usa);
        upd.add("PUERTO_GPS", item.puerto_gps);
        upd.add("ES_RUTA_OFICINA", item.es_ruta_oficina);
        upd.add("DILUIR_BON", item.diluir_bon);
        upd.add("PREIMPRESION_FACTURA", item.preimpresion_factura);
        upd.add("MODIFICAR_MEDIA_PAGO", item.modificar_media_pago);
        upd.add("IDIMPRESORA", item.idimpresora);
        upd.add("NUMVERSION", item.numversion);
        upd.add("FECHAVERSION", item.fechaversion);
        upd.add("ARQUITECTURA", item.arquitectura);

        upd.Where("(CODIGO='" + item.codigo + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

