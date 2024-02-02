package com.dtsgt.felesa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class clsFELClases {


    public class JSONFactura {
        public String json;

        private JSONObject jsdoc,jso,jsitem,jshead,jsad;
        private JSONArray jsitems;

        public void Factura(String establecimiento) throws JSONException {
            Factura(establecimiento,2);
        }

        public void Factura(String establecimiento,int condicion_pago) throws JSONException {
            jsdoc = new JSONObject();
            jsitems=new JSONArray();

            jshead = new JSONObject();
            jshead.put("tipo_dte","01");
            jshead.put("establecimiento",establecimiento);
            jshead.put("condicion_pago",condicion_pago);

        }

        public void agregarProducto(String descripcion,double cantidad,
                                    double precio_unitario,double descuento_monto) throws JSONException {

            jsitem = new JSONObject();

            jsitem.put("tipo", 1);
            jsitem.put("cantidad",cantidad);
            jsitem.put("unidad_medida", 59);
            jsitem.put("descuento", descuento_monto);
            jsitem.put("descripcion", descripcion);
            jsitem.put("precio_unitario", precio_unitario);

            jsitems.put(jsitem);
        }

        public void agregarServicio(String descripcion,double cantidad,
                                    double precio_unitario,double descuento_monto) throws JSONException {

            jsitem = new JSONObject();

            jsitem.put("tipo", 2);
            jsitem.put("cantidad",cantidad);
            jsitem.put("unidad_medida", 59);
            jsitem.put("descuento", descuento_monto);
            jsitem.put("descripcion", descripcion);
            jsitem.put("precio_unitario", precio_unitario);

            jsitems.put(jsitem);
        }

        public void agregarAdenda(String adenda) throws JSONException {
            jsad = new JSONObject();
            jsad.put("adenda 1",adenda);
            //jsad.put("adenda 2",adenda2);
        }

        public void json() throws JSONException {
            jshead.put("items",jsitems);
            jshead.put("adendas",jsad);
            jsdoc.put("documento",jshead);

            json = jsdoc.toString();
        }

    }

    public class JSONCredito {
        public String json;

        private JSONObject jsdoc,jso,jsitem,jshead,jsr,jsrd,jst;
        private JSONArray jsitems;

        public void Credito(String establecimiento) throws JSONException {
            Credito(establecimiento,2);
        }

        public void Credito(String establecimiento,int condicion_pago) throws JSONException {
            jsdoc = new JSONObject();
            jsitems=new JSONArray();

            jshead = new JSONObject();
            jshead.put("tipo_dte","03");
            jshead.put("establecimiento",establecimiento);
            jshead.put("condicion_pago",condicion_pago);

        }

        public void Receptor(String numero_documento,String nrc,String nombre,
                             String codigo_actividad,String nombre_comercial) throws JSONException {
            jsr = new JSONObject();
            jsr.put("tipo_documento","36");
            jsr.put("numero_documento",numero_documento);
            jsr.put("nrc",nrc);
            jsr.put("nombre",nombre);
            jsr.put("codigo_actividad",codigo_actividad);
            jsr.put("nombre_comercial",nombre_comercial);
        }

        public void Direccion(String departamento,String municipio,
                              String complemento,String correo) throws JSONException {
            jsrd = new JSONObject();
            jsrd.put("departamento",departamento);
            jsrd.put("municipio",municipio);
            jsrd.put("complemento",complemento);

            jsr.put("direccion",jsrd);
            jsr.put("correo",correo);

            jshead.put("receptor",jsr);
        }

        public void agregarProducto(String descripcion,double cantidad,
                                    double precio_unitario,double impuesto_monto) throws JSONException {
            jsitem = new JSONObject();

            jsitem.put("tipo", 1);
            jsitem.put("cantidad", cantidad);
            jsitem.put("unidad_medida", 59);
            //jsitem.put("descuento", 25);
            jsitem.put("descripcion", descripcion);
            jsitem.put("precio_unitario", precio_unitario);

            JSONArray jstrib = new JSONArray();

            jst = new JSONObject();
            jst.put("codigo", "20");
            jst.put("monto", impuesto_monto);
            jstrib.put(jst);

            jsitem.put("tributos", jstrib);

            jsitems.put(jsitem);
        }

        public void json() throws JSONException {
            jshead.put("items",jsitems);
            jsdoc.put("documento",jshead);

            json = jsdoc.toString();
        }

    }

    public class respuesta  {
        String mensaje;
        String pathpdf;

        String codigoGeneracion;
        String status;
        String numeroControl;
        String identificador;
        String fechaEmision;
        String selloRecepcion;

        String estado;
        String descripcionMsg;
        String comentarios;

        double totalNoSuj;
        double totalExenta;
        double totalGravada;
        double subTotalVentas;
        double descuNoSuj;
        double descuExenta;
        double descuGravada;
        double porcentajeDescuento;
        double totalDescu;
        double subTotal;
        double ivaRete1;
        double reteRenta;
        double montoTotalOperacion;
        double totalNoGravado;
        double totalPagar;
        String totalLetras;
        double saldoFavor;
        double totalIva;

    }

}
