package com.dtsgt.classes;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

public class clsExport {

    private BaseDatos.Insert ins;
    private BaseDatos.Update upd;

    private String emp;

    public clsExport(BaseDatos dbconnection,String empresa) {
        ins = dbconnection.Ins;
        upd = dbconnection.Upd;
        emp=empresa;
    }


    //region Banco

    public String banco_ins(clsClasses.clsP_banco item) {

        ins.init("P_banco");

        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.tipo);
        ins.add("NOMBRE",item.nombre);
        ins.add("CUENTA",item.cuenta);
        ins.add("EMPRESA",item.empresa);
        ins.add("ACTIVO",item.activo);

        return ins.sql();
    }

    public String banco_upd(clsClasses.clsP_banco item) {

        upd.init("P_banco");

        upd.add("TIPO",item.tipo);
        upd.add("NOMBRE",item.nombre);
        upd.add("CUENTA",item.cuenta);
        upd.add("EMPRESA",item.empresa);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"') AND (EMPRESA='"+emp+"')");


        return upd.sql();
    }

    //endregion

    //region Caja

    public String ruta_ins(clsClasses.clsP_ruta item) {

        ins.init("P_ruta");

        ins.add("EMPRESA", emp);
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

    public String ruta_upd(clsClasses.clsP_ruta item) {

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

        upd.Where("(CODIGO='" + item.codigo + "') AND (EMPRESA='"+emp+"')");

        String ss=upd.sql();

        return upd.sql();
    }

    //endregion

    //region Cliente

    public String cliente_ins(clsClasses.clsP_cliente item) {

        ins.init("P_cliente");

        ins.add("CODIGO", item.codigo);
        ins.add("EMPRESA", emp);
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

    public String cliente_upd(clsClasses.clsP_cliente item) {

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

        upd.Where("(CODIGO='" + item.codigo + "') AND (EMPRESA='"+emp+"') ");

        return upd.sql();
    }

    //endregion

    //region Descuento

    public String desc_ins(clsClasses.clsP_descuento item) {

        ins.init("P_descuento");

        ins.add("CLIENTE", item.cliente);
        ins.add("CTIPO", item.ctipo);
        ins.add("PRODUCTO", item.producto);
        ins.add("PTIPO", item.ptipo);
        ins.add("TIPORUTA", item.tiporuta);
        ins.add("RANGOINI", item.rangoini);
        ins.add("RANGOFIN", item.rangofin);
        ins.add("DESCTIPO", item.desctipo);
        ins.add("VALOR", item.valor);
        ins.add("GLOBDESC", item.globdesc);
        ins.add("PORCANT", item.porcant);
        ins.add("FECHAINI", item.fechaini);
        ins.add("FECHAFIN", item.fechafin);
        ins.add("CODDESC", item.coddesc);
        ins.add("NOMBRE", item.nombre);
        ins.add("EMP", emp);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String desc_upd(clsClasses.clsP_descuento item) {

        upd.init("P_descuento");

        upd.add("RANGOFIN", item.rangofin);
        upd.add("DESCTIPO", item.desctipo);
        upd.add("VALOR", item.valor);
        upd.add("GLOBDESC", item.globdesc);
        upd.add("PORCANT", item.porcant);
        upd.add("FECHAINI", item.fechaini);
        upd.add("FECHAFIN", item.fechafin);
        upd.add("CODDESC", item.coddesc);
        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CLIENTE='" + item.cliente + "') AND (CTIPO=" + item.ctipo + ") AND (PRODUCTO='" + item.producto + "') AND (PTIPO=" + item.ptipo + ") AND (TIPORUTA=" + item.tiporuta + ") AND (RANGOINI=" + item.rangoini + ") AND (EMP='"+emp+"')");

        return upd.sql();
    }

    //endregion

    //region Empresa

    public String empresa_ins(clsClasses.clsP_empresa item) {

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

    public String empresa_upd(clsClasses.clsP_empresa item) {

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
    }

    //endregion

    //region Familia

    public String linea_ins(clsClasses.clsP_linea item) {

        ins.init("P_linea");

        ins.add("CODIGO",item.codigo);
        ins.add("NOMBRE",item.nombre);
        ins.add("MARCA",item.marca);
        ins.add("IMAGEN","");
        ins.add("ACTIVO",item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();
    }

    public String linea_upd(clsClasses.clsP_linea item) {

        upd.init("P_linea");

        upd.add("MARCA",item.marca);
        upd.add("NOMBRE",item.nombre);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"') AND (EMPRESA='"+emp+"')");

        return upd.sql();
    }

    //endregion

    //region Impuesto

    public String impuesto_ins(clsClasses.clsP_impuesto item) {

        ins.init("P_impuesto");

        ins.add("CODIGO",item.codigo);
        ins.add("VALOR",item.valor);
        ins.add("ACTIVO",item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();
    }

    public String impuesto_upd(clsClasses.clsP_impuesto item) {

        upd.init("P_impuesto");

        upd.add("VALOR",item.valor);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO="+item.codigo+") AND (EMPRESA='"+emp+"')");

        return upd.sql();
    }

    //endregion

    //region Media Pago

    public String media_ins(clsClasses.clsP_mediapago item) {

        ins.init("P_mediapago");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("NIVEL", item.nivel);
        ins.add("PORCOBRO", item.porcobro);
        ins.add("EMPRESA", emp);

        return ins.sql();
    }

    public String media_upd(clsClasses.clsP_mediapago item) {

        upd.init("P_mediapago");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("NIVEL", item.nivel);
        upd.add("PORCOBRO", item.porcobro);

        upd.Where("(CODIGO=" + item.codigo + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();
    }

    //endregion


    //region Nivel Precio

    public String nivelprecio_ins(clsClasses.clsP_nivelprecio item) {
        ins.init("P_nivelprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();
    }

    public String nivelprecio_upd(clsClasses.clsP_nivelprecio item) {
        upd.init("P_nivelprecio");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO=" + item.codigo + ")  AND (EMPRESA='"+emp+"')");

        return upd.sql();
    }

    //endregion



}