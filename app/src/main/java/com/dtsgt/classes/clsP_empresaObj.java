package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;


public class clsP_empresaObj {

    public int count;

    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    public BaseDatos.Insert ins;
    public BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private String sel = "SELECT * FROM P_empresa";
    private String sql;
    public ArrayList<clsClasses.clsP_empresa> items = new ArrayList<clsClasses.clsP_empresa>();

    public clsP_empresaObj(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
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

    public void add(clsClasses.clsP_empresa item) {
        addItem(item);
    }

    public void update(clsClasses.clsP_empresa item) {
        updateItem(item);
    }

    public void delete(clsClasses.clsP_empresa item) {
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

    public clsClasses.clsP_empresa first() {
        return items.get(0);
    }


    // Private

    private void addItem(clsClasses.clsP_empresa item) {

        ins.init("P_empresa");

        ins.add("EMPRESA", item.empresa);
        ins.add("NOMBRE", item.nombre);
        ins.add("INITPATH", item.initpath);
        ins.add("FTPPATH", item.ftppath);
        ins.add("NUMREIMPRES", item.numreimpres);
        ins.add("MODDESC", item.moddesc);
        ins.add("USARPESO", item.usarpeso);
        ins.add("DEVCONPREC", item.devconprec);
        ins.add("ACUMDESC", item.acumdesc);
        ins.add("DESCMAX", item.descmax);
        ins.add("BONVOLTOL", item.bonvoltol);
        ins.add("COD_PAIS", item.cod_pais);
        ins.add("BOLETA_DEPOSITO", item.boleta_deposito);
        ins.add("EDITAR_DIRECCION", item.editar_direccion);
        ins.add("DEPOSITO_PARCIAL", item.deposito_parcial);
        ins.add("COL_IMP", item.col_imp);
        ins.add("INV_ENLINEA", item.inv_enlinea);
        ins.add("FIN_DIA", item.fin_dia);
        ins.add("PRESENTACION_MULTIPLE", item.presentacion_multiple);
        ins.add("PRECIOS_ESPECIALES", item.precios_especiales);
        ins.add("AUTORIZ_MODIF_DESCBON", item.autoriz_modif_descbon);
        ins.add("CAMBIO_POR_CAMBIO", item.cambio_por_cambio);
        ins.add("DEVOLUCION_MERCANCIA", item.devolucion_mercancia);
        ins.add("COBROS_SIN_REFERENCIA", item.cobros_sin_referencia);
        ins.add("PORCENTAJE_NC", item.porcentaje_nc);
        ins.add("PORC_MERMA", item.porc_merma);
        ins.add("PRODUCTO_ERROR_SUMA", item.producto_error_suma);
        ins.add("UNIDAD_MEDIDA_PESO", item.unidad_medida_peso);
        ins.add("LOTE_POR_DEFECTO", item.lote_por_defecto);
        ins.add("INCIDENCIA_NO_LECTURA", item.incidencia_no_lectura);

        db.execSQL(ins.sql());

    }

    private void updateItem(clsClasses.clsP_empresa item) {

        upd.init("P_empresa");

        upd.add("NOMBRE", item.nombre);
        upd.add("INITPATH", item.initpath);
        upd.add("FTPPATH", item.ftppath);
        upd.add("NUMREIMPRES", item.numreimpres);
        upd.add("MODDESC", item.moddesc);
        upd.add("USARPESO", item.usarpeso);
        upd.add("DEVCONPREC", item.devconprec);
        upd.add("ACUMDESC", item.acumdesc);
        upd.add("DESCMAX", item.descmax);
        upd.add("BONVOLTOL", item.bonvoltol);
        upd.add("COD_PAIS", item.cod_pais);
        upd.add("BOLETA_DEPOSITO", item.boleta_deposito);
        upd.add("EDITAR_DIRECCION", item.editar_direccion);
        upd.add("DEPOSITO_PARCIAL", item.deposito_parcial);
        upd.add("COL_IMP", item.col_imp);
        upd.add("INV_ENLINEA", item.inv_enlinea);
        upd.add("FIN_DIA", item.fin_dia);
        upd.add("PRESENTACION_MULTIPLE", item.presentacion_multiple);
        upd.add("PRECIOS_ESPECIALES", item.precios_especiales);
        upd.add("AUTORIZ_MODIF_DESCBON", item.autoriz_modif_descbon);
        upd.add("CAMBIO_POR_CAMBIO", item.cambio_por_cambio);
        upd.add("DEVOLUCION_MERCANCIA", item.devolucion_mercancia);
        upd.add("COBROS_SIN_REFERENCIA", item.cobros_sin_referencia);
        upd.add("PORCENTAJE_NC", item.porcentaje_nc);
        upd.add("PORC_MERMA", item.porc_merma);
        upd.add("PRODUCTO_ERROR_SUMA", item.producto_error_suma);
        upd.add("UNIDAD_MEDIDA_PESO", item.unidad_medida_peso);
        upd.add("LOTE_POR_DEFECTO", item.lote_por_defecto);
        upd.add("INCIDENCIA_NO_LECTURA", item.incidencia_no_lectura);

        upd.Where("(EMPRESA='" + item.empresa + "')");

        db.execSQL(upd.sql());

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

    private void deleteItem(clsClasses.clsP_empresa item) {
        sql = "DELETE FROM P_empresa WHERE (EMPRESA='" + item.empresa + "')";
        db.execSQL(sql);
    }

    private void deleteItem(String id) {
        sql = "DELETE FROM P_empresa WHERE id='" + id + "'";
        db.execSQL(sql);
    }

    private void fillItems(String sq) {
        Cursor dt;
        clsClasses.clsP_empresa item;

        items.clear();

        dt = Con.OpenDT(sq);
        count = dt.getCount();
        if (dt.getCount() > 0) dt.moveToFirst();

        while (!dt.isAfterLast()) {

            item = clsCls.new clsP_empresa();

            item.empresa = dt.getString(0);
            item.nombre = dt.getString(1);
            item.initpath = dt.getString(2);
            item.ftppath = dt.getString(3);
            item.numreimpres = dt.getInt(4);
            item.moddesc = dt.getString(5);
            item.usarpeso = dt.getString(6);
            item.devconprec = dt.getString(7);
            item.acumdesc = dt.getString(8);
            item.descmax = dt.getDouble(9);
            item.bonvoltol = dt.getDouble(10);
            item.cod_pais = dt.getString(11);
            item.boleta_deposito = dt.getInt(12);
            item.editar_direccion = dt.getInt(13);
            item.deposito_parcial = dt.getInt(14);
            item.col_imp = dt.getInt(15);
            item.inv_enlinea = dt.getInt(16);
            item.fin_dia = dt.getInt(17);
            item.presentacion_multiple = dt.getInt(18);
            item.precios_especiales = dt.getInt(19);
            item.autoriz_modif_descbon = dt.getInt(20);
            item.cambio_por_cambio = dt.getInt(21);
            item.devolucion_mercancia = dt.getInt(22);
            item.cobros_sin_referencia = dt.getInt(23);
            item.porcentaje_nc = dt.getDouble(24);
            item.porc_merma = dt.getDouble(25);
            item.producto_error_suma = dt.getString(26);
            item.unidad_medida_peso = dt.getString(27);
            item.lote_por_defecto = dt.getString(28);
            item.incidencia_no_lectura = dt.getInt(29);

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

    public String addItemSql(clsClasses.clsP_empresa item) {

        ins.init("P_empresa");

        ins.add("EMPRESA", item.empresa);
        ins.add("NOMBRE", item.nombre);
        ins.add("INITPATH", item.initpath);
        ins.add("FTPPATH", item.ftppath);
        ins.add("NUMREIMPRES", item.numreimpres);
        ins.add("MODDESC", item.moddesc);
        ins.add("USARPESO", item.usarpeso);
        ins.add("DEVCONPREC", item.devconprec);
        ins.add("ACUMDESC", item.acumdesc);
        ins.add("DESCMAX", item.descmax);
        ins.add("BONVOLTOL", item.bonvoltol);
        ins.add("COD_PAIS", item.cod_pais);
        ins.add("BOLETA_DEPOSITO", item.boleta_deposito);
        ins.add("EDITAR_DIRECCION", item.editar_direccion);
        ins.add("DEPOSITO_PARCIAL", item.deposito_parcial);
        ins.add("COL_IMP", item.col_imp);
        ins.add("INV_ENLINEA", item.inv_enlinea);
        ins.add("FIN_DIA", item.fin_dia);
        ins.add("PRESENTACION_MULTIPLE", item.presentacion_multiple);
        ins.add("PRECIOS_ESPECIALES", item.precios_especiales);
        ins.add("AUTORIZ_MODIF_DESCBON", item.autoriz_modif_descbon);
        ins.add("CAMBIO_POR_CAMBIO", item.cambio_por_cambio);
        ins.add("DEVOLUCION_MERCANCIA", item.devolucion_mercancia);
        ins.add("COBROS_SIN_REFERENCIA", item.cobros_sin_referencia);
        ins.add("PORCENTAJE_NC", item.porcentaje_nc);
        ins.add("PORC_MERMA", item.porc_merma);
        ins.add("PRODUCTO_ERROR_SUMA", item.producto_error_suma);
        ins.add("UNIDAD_MEDIDA_PESO", item.unidad_medida_peso);
        ins.add("LOTE_POR_DEFECTO", item.lote_por_defecto);
        ins.add("INCIDENCIA_NO_LECTURA", item.incidencia_no_lectura);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsP_empresa item) {

        upd.init("P_empresa");

        upd.add("NOMBRE", item.nombre);
        upd.add("INITPATH", item.initpath);
        upd.add("FTPPATH", item.ftppath);
        upd.add("NUMREIMPRES", item.numreimpres);
        upd.add("MODDESC", item.moddesc);
        upd.add("USARPESO", item.usarpeso);
        upd.add("DEVCONPREC", item.devconprec);
        upd.add("ACUMDESC", item.acumdesc);
        upd.add("DESCMAX", item.descmax);
        upd.add("BONVOLTOL", item.bonvoltol);
        upd.add("COD_PAIS", item.cod_pais);
        upd.add("BOLETA_DEPOSITO", item.boleta_deposito);
        upd.add("EDITAR_DIRECCION", item.editar_direccion);
        upd.add("DEPOSITO_PARCIAL", item.deposito_parcial);
        upd.add("COL_IMP", item.col_imp);
        upd.add("INV_ENLINEA", item.inv_enlinea);
        upd.add("FIN_DIA", item.fin_dia);
        upd.add("PRESENTACION_MULTIPLE", item.presentacion_multiple);
        upd.add("PRECIOS_ESPECIALES", item.precios_especiales);
        upd.add("AUTORIZ_MODIF_DESCBON", item.autoriz_modif_descbon);
        upd.add("CAMBIO_POR_CAMBIO", item.cambio_por_cambio);
        upd.add("DEVOLUCION_MERCANCIA", item.devolucion_mercancia);
        upd.add("COBROS_SIN_REFERENCIA", item.cobros_sin_referencia);
        upd.add("PORCENTAJE_NC", item.porcentaje_nc);
        upd.add("PORC_MERMA", item.porc_merma);
        upd.add("PRODUCTO_ERROR_SUMA", item.producto_error_suma);
        upd.add("UNIDAD_MEDIDA_PESO", item.unidad_medida_peso);
        upd.add("LOTE_POR_DEFECTO", item.lote_por_defecto);
        upd.add("INCIDENCIA_NO_LECTURA", item.incidencia_no_lectura);

        upd.Where("(EMPRESA='" + item.empresa + "')");

        return upd.sql();

        //Toast toast= Toast.makeText(cont,upd.sql(), Toast.LENGTH_LONG);toast.show();

    }

}

