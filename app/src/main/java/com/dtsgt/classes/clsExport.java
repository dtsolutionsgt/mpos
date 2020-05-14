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

    //region Archivo Configuracion

    public String archivo_ins(clsClasses.clsP_archivoconf item) {

        ins.init("P_archivoconf");

        ins.add("RUTA", item.ruta);
        ins.add("TIPO_HH", item.tipo_hh);
        ins.add("IDIOMA", item.idioma);
        ins.add("TIPO_IMPRESORA", item.tipo_impresora);
        ins.add("SERIAL_HH", item.serial_hh);
        ins.add("MODIF_PESO", item.modif_peso);
        ins.add("PUERTO_IMPRESION", item.puerto_impresion);
        ins.add("LBS_O_KGS", item.lbs_o_kgs);
        ins.add("NOTA_CREDITO", item.nota_credito);
        ins.add("EMPRESA",emp);

        return ins.sql();

    }

    public String archivo_upd(clsClasses.clsP_archivoconf item) {

        upd.init("P_archivoconf");

        upd.add("TIPO_HH", item.tipo_hh);
        upd.add("IDIOMA", item.idioma);
        upd.add("TIPO_IMPRESORA", item.tipo_impresora);
        upd.add("SERIAL_HH", item.serial_hh);
        upd.add("MODIF_PESO", item.modif_peso);
        upd.add("PUERTO_IMPRESION", item.puerto_impresion);
        upd.add("LBS_O_KGS", item.lbs_o_kgs);
        upd.add("NOTA_CREDITO", item.nota_credito);

        upd.Where("(RUTA='" + item.ruta + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

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

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("SUCURSAL", item.sucursal);

        return ins.sql();
    }

    public String ruta_upd(clsClasses.clsP_ruta item) {

        upd.init("P_ruta");

        upd.add("NOMBRE", item.nombre);
        upd.add("SUCURSAL", item.sucursal);
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
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("MEDIAPAGO", item.mediapago);
        ins.add("LIMITECREDITO", item.limitecredito);
        ins.add("DIACREDITO", item.diacredito);
        ins.add("DESCUENTO", item.descuento);
        ins.add("BONIFICACION", item.bonificacion);
        ins.add("ULTVISITA", item.ultvisita);
        ins.add("IMPSPEC", item.impspec);
        ins.add("NIT", item.nit);
        ins.add("EMAIL", item.email);
        ins.add("ESERVICE", item.eservice);
        ins.add("TELEFONO", item.telefono);
        ins.add("DIRECCION", item.direccion);
        ins.add("COORX", item.coorx);
        ins.add("COORY", item.coory);
        ins.add("BODEGA", item.bodega);
        ins.add("COD_PAIS", item.cod_pais);
        ins.add("CODBARRA", item.codbarra);
        ins.add("PERCEPCION", item.percepcion);
        ins.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);

        return ins.sql();

    }

    public String cliente_upd(clsClasses.clsP_cliente item) {

        upd.init("P_cliente");

        upd.add("NOMBRE", item.nombre);
        upd.add("BLOQUEADO", item.bloqueado);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("MEDIAPAGO", item.mediapago);
        upd.add("LIMITECREDITO", item.limitecredito);
        upd.add("DIACREDITO", item.diacredito);
        upd.add("DESCUENTO", item.descuento);
        upd.add("BONIFICACION", item.bonificacion);
        upd.add("ULTVISITA", item.ultvisita);
        upd.add("IMPSPEC", item.impspec);
        upd.add("NIT", item.nit);
        upd.add("EMAIL", item.email);
        upd.add("ESERVICE", item.eservice);
        upd.add("TELEFONO", item.telefono);
        upd.add("DIRECCION", item.direccion);
        upd.add("COORX", item.coorx);
        upd.add("COORY", item.coory);
        upd.add("BODEGA", item.bodega);
        upd.add("COD_PAIS", item.cod_pais);
        upd.add("CODBARRA", item.codbarra);
        upd.add("PERCEPCION", item.percepcion);
        upd.add("TIPO_CONTRIBUYENTE", item.tipo_contribuyente);

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
        ins.add("COL_IMP", item.col_imp);

        return ins.sql();
    }

    public String empresa_upd(clsClasses.clsP_empresa item) {

        upd.init("P_empresa");

        upd.add("NOMBRE", item.nombre);
        upd.add("COL_IMP", item.col_imp);

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

    //region Moneda

    public String moneda_ins(clsClasses.clsP_moneda item) {

        ins.init("P_moneda");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("SYMBOLO", item.symbolo);
        ins.add("CAMBIO", item.cambio);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String moneda_upd(clsClasses.clsP_moneda item) {

        upd.init("P_moneda");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("SYMBOLO", item.symbolo);
        upd.add("CAMBIO", item.cambio);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO=" + item.codigo + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Producto

    public String producto_ins(clsClasses.clsP_producto item) {

        ins.init("P_producto");

        ins.add("CODIGO",item.codigo);
        ins.add("TIPO",item.codigo_tipo);
        ins.add("LINEA",item.linea);
        ins.add("EMPRESA",item.empresa);
        ins.add("MARCA",item.marca);
        ins.add("CODBARRA",item.codbarra);
        ins.add("DESCCORTA",item.desccorta);
        ins.add("DESCLARGA",item.desclarga);
        ins.add("COSTO",item.costo);
        ins.add("FACTORCONV",item.factorconv);
        ins.add("UNIDBAS",item.unidbas);
        ins.add("UNIDMED",item.unidmed);
        ins.add("UNIMEDFACT",item.unimedfact);
        ins.add("UNIGRA",item.unigra);
        ins.add("UNIGRAFACT",item.unigrafact);
        ins.add("DESCUENTO",item.descuento);
        ins.add("BONIFICACION",item.bonificacion);
        ins.add("IMP1",item.imp1);
        ins.add("IMP2",item.imp2);
        ins.add("IMP3",item.imp3);
        ins.add("VENCOMP",item.vencomp);
        ins.add("DEVOL",item.devol);
        ins.add("OFRECER",item.ofrecer);
        ins.add("RENTAB",item.rentab);
        ins.add("DESCMAX",item.descmax);
        ins.add("PESO_PROMEDIO",item.peso_promedio);
        ins.add("MODIF_PRECIO",item.modif_precio);
        ins.add("IMAGEN",item.imagen);
        ins.add("VIDEO",item.video);
        ins.add("VENTA_POR_PESO",item.venta_por_peso);
        ins.add("ES_PROD_BARRA",item.es_prod_barra);
        ins.add("UNID_INV",item.unid_inv);
        ins.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        ins.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        ins.add("ES_SERIALIZADO",item.es_serializado);
        ins.add("PARAM_CADUCIDAD",item.param_caducidad);
        ins.add("PRODUCTO_PADRE",item.producto_padre);
        ins.add("FACTOR_PADRE",item.factor_padre);
        ins.add("TIENE_INV",item.tiene_inv);
        ins.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        ins.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        ins.add("ES_VENDIBLE",item.es_vendible);
        ins.add("UNIGRASAP",item.unigrasap);
        ins.add("UM_SALIDA",item.um_salida);
        ins.add("ACTIVO",item.activo);

        return ins.sql();

    }

    public String producto_upd(clsClasses.clsP_producto item) {

        upd.init("P_producto");

        upd.add("TIPO",item.codigo_tipo);
        upd.add("LINEA",item.linea);
        upd.add("EMPRESA",item.empresa);
        upd.add("MARCA",item.marca);
        upd.add("CODBARRA",item.codbarra);
        upd.add("DESCCORTA",item.desccorta);
        upd.add("DESCLARGA",item.desclarga);
        upd.add("COSTO",item.costo);
        upd.add("FACTORCONV",item.factorconv);
        upd.add("UNIDBAS",item.unidbas);
        upd.add("UNIDMED",item.unidmed);
        upd.add("UNIMEDFACT",item.unimedfact);
        upd.add("UNIGRA",item.unigra);
        upd.add("UNIGRAFACT",item.unigrafact);
        upd.add("DESCUENTO",item.descuento);
        upd.add("BONIFICACION",item.bonificacion);
        upd.add("IMP1",item.imp1);
        upd.add("IMP2",item.imp2);
        upd.add("IMP3",item.imp3);
        upd.add("VENCOMP",item.vencomp);
        upd.add("DEVOL",item.devol);
        upd.add("OFRECER",item.ofrecer);
        upd.add("RENTAB",item.rentab);
        upd.add("DESCMAX",item.descmax);
        upd.add("PESO_PROMEDIO",item.peso_promedio);
        upd.add("MODIF_PRECIO",item.modif_precio);
        upd.add("IMAGEN",item.imagen);
        upd.add("VIDEO",item.video);
        upd.add("VENTA_POR_PESO",item.venta_por_peso);
        upd.add("ES_PROD_BARRA",item.es_prod_barra);
        upd.add("UNID_INV",item.unid_inv);
        upd.add("VENTA_POR_PAQUETE",item.venta_por_paquete);
        upd.add("VENTA_POR_FACTOR_CONV",item.venta_por_factor_conv);
        upd.add("ES_SERIALIZADO",item.es_serializado);
        upd.add("PARAM_CADUCIDAD",item.param_caducidad);
        upd.add("PRODUCTO_PADRE",item.producto_padre);
        upd.add("FACTOR_PADRE",item.factor_padre);
        upd.add("TIENE_INV",item.tiene_inv);
        upd.add("TIENE_VINETA_O_TUBO",item.tiene_vineta_o_tubo);
        upd.add("PRECIO_VINETA_O_TUBO",item.precio_vineta_o_tubo);
        upd.add("ES_VENDIBLE",item.es_vendible);
        upd.add("UNIGRASAP",item.unigrasap);
        upd.add("UM_SALIDA",item.um_salida);
        upd.add("ACTIVO",item.activo);

        upd.Where("(CODIGO='"+item.codigo+"') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Factor Conversion

    public String factorconv_ins(clsClasses.clsP_factorconv item) {

        ins.init("P_factorconv");

        ins.add("PRODUCTO", item.producto);
        ins.add("UNIDADSUPERIOR", item.unidadsuperior);
        ins.add("FACTORCONVERSION", item.factorconversion);
        ins.add("UNIDADMINIMA", item.unidadminima);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String factorconv_upd(clsClasses.clsP_factorconv item) {

        upd.init("P_factorconv");

        upd.add("FACTORCONVERSION", item.factorconversion);

        upd.Where("(PRODUCTO='" + item.producto + "') AND (UNIDADSUPERIOR='" + item.unidadsuperior + "') AND (UNIDADMINIMA='" + item.unidadminima + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Paramext

    public String paramext_ins(clsClasses.clsP_paramext item) {

        ins.init("P_paramext");

        ins.add("ID", item.id);
        ins.add("Nombre", item.nombre);
        ins.add("Valor", item.valor);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String paramext_upd(clsClasses.clsP_paramext item) {

        upd.init("P_paramext");

        upd.add("Nombre", item.nombre);
        upd.add("Valor", item.valor);
        upd.add("EMPRESA", emp);

        upd.Where("(ID=" + item.id + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Precio

    public String prodprecio_ins(clsClasses.clsP_prodprecio item) {

        ins.init("P_prodprecio");

        ins.add("CODIGO", item.codigo);
        ins.add("NIVEL", item.nivel);
        ins.add("PRECIO", item.precio);
        ins.add("UNIDADMEDIDA", item.unidadmedida);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String prodprecio_upd(clsClasses.clsP_prodprecio item) {

        upd.init("P_prodprecio");

        upd.add("PRECIO", item.precio);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO='" + item.codigo + "') AND (NIVEL=" + item.nivel + ") AND (UNIDADMEDIDA='" + item.unidadmedida + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Proveedor

    public String proveedor_ins(clsClasses.clsP_proveedor item) {

        ins.init("P_proveedor");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String proveedor_upd(clsClasses.clsP_proveedor item) {

        upd.init("P_proveedor");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO=" + item.codigo + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Sucursal

    public String sucursal_ins(clsClasses.clsP_sucursal item) {

        ins.init("P_sucursal");

        ins.add("CODIGO", item.codigo);
        ins.add("EMPRESA", item.empresa);
        ins.add("DESCRIPCION", item.descripcion);
        ins.add("NOMBRE", item.nombre);
        ins.add("DIRECCION", item.direccion);
        ins.add("TELEFONO", item.telefono);
        ins.add("NIT", item.nit);
        ins.add("TEXTO", item.texto);
        ins.add("ACTIVO", item.activo);

        return ins.sql();

    }

    public String sucursal_upd(clsClasses.clsP_sucursal item) {

        upd.init("P_sucursal");

        upd.add("EMPRESA", item.empresa);
        upd.add("DESCRIPCION", item.descripcion);
        upd.add("NOMBRE", item.nombre);
        upd.add("DIRECCION", item.direccion);
        upd.add("TELEFONO", item.telefono);
        upd.add("NIT", item.nit);
        upd.add("TEXTO", item.texto);
        upd.add("ACTIVO", item.activo);

        upd.Where("(CODIGO='" + item.codigo + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region Vendedores

    public String addItemSql(clsClasses.clsVendedores item) {

        ins.init("Vendedores");

        ins.add("CODIGO", item.codigo);
        ins.add("NOMBRE", item.nombre);
        ins.add("CLAVE", item.clave);
        ins.add("RUTA", item.ruta);
        ins.add("NIVEL", item.nivel);
        ins.add("NIVELPRECIO", item.nivelprecio);
        ins.add("BODEGA", item.bodega);
        ins.add("SUBBODEGA", item.subbodega);
        ins.add("ACTIVO", item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String updateItemSql(clsClasses.clsVendedores item) {

        upd.init("Vendedores");

        upd.add("NOMBRE", item.nombre);
        upd.add("CLAVE", item.clave);
        upd.add("NIVEL", item.nivel);
        upd.add("NIVELPRECIO", item.nivelprecio);
        upd.add("BODEGA", item.bodega);
        upd.add("SUBBODEGA", item.subbodega);
        upd.add("ACTIVO", item.activo);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO='" + item.codigo + "') AND (RUTA='" + item.ruta + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

    //endregion

    //region ProdMenu

    /*
    public String prodmenu_ins(clsClasses.clsP_prodmenu item) {

        ins.init("P_prodmenu");

        ins.add("CODIGO", item.codigo);
        ins.add("ITEM", item.item);
        ins.add("NOMBRE", item.nombre);
        ins.add("IDOPCION", item.idopcion);
        ins.add("CANT", item.cant);
        ins.add("ORDEN", item.orden);
        ins.add("BANDERA", item.bandera);
        ins.add("NOTA", item.nota);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String prodmenu_upd(clsClasses.clsP_prodmenu item) {

        upd.init("P_prodmenu");

        upd.add("NOMBRE", item.nombre);
        upd.add("IDOPCION", item.idopcion);
        upd.add("CANT", item.cant);
        upd.add("ORDEN", item.orden);
        upd.add("BANDERA", item.bandera);
        upd.add("NOTA", item.nota);
        upd.add("EMPRESA", emp);

        upd.Where("(CODIGO='" + item.codigo + "') AND (ITEM=" + item.item + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

     */

    //endregion

    //region ProdOpc

    /*
    public String prodopc_ins(clsClasses.clsP_prodopc item) {

        ins.init("P_prodopc");

        ins.add("ID", item.id);
        ins.add("NOMBRE", item.nombre);
        ins.add("ACTIVO", item.activo);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String prodopc_upd(clsClasses.clsP_prodopc item) {

        upd.init("P_prodopc");

        upd.add("NOMBRE", item.nombre);
        upd.add("ACTIVO", item.activo);
        upd.add("EMPRESA", emp);

        upd.Where("(ID=" + item.id + ") AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

     */
    //endregion

    //region ProdOpcList

    /*
    public String prodopclist_ins(clsClasses.clsP_prodopclist item) {

        ins.init("P_prodopclist");

        ins.add("ID", item.id);
        ins.add("PRODUCTO", item.producto);
        ins.add("CANT", item.cant);
        ins.add("IDRECETA", item.idreceta);
        ins.add("EMPRESA", emp);

        return ins.sql();

    }

    public String prodopclist_upd(clsClasses.clsP_prodopclist item) {

        upd.init("P_prodopclist");

        upd.add("CANT", item.cant);
        upd.add("IDRECETA", item.idreceta);
        upd.add("EMPRESA", emp);

        upd.Where("(ID=" + item.id + ") AND (PRODUCTO='" + item.producto + "') AND (EMPRESA='"+emp+"')");

        return upd.sql();

    }

     */

    //endregion

}
