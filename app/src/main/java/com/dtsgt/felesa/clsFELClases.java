package com.dtsgt.felesa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_fel_sv_ambObj;
import com.infile.generador.Generador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class clsFELClases {

    public class JSONFactura {

        public String json;

        private JSONObject jsdoc,jso,jsitem,jshead,jsad,jsrec;
        private JSONArray jsitems;

        private String llave_cont;
        private boolean receptor,contingencia;

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

            receptor=false;
            contingencia=false;
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

        public void agregarReceptor(String nombre,String nit,String correo) throws JSONException {
            jsrec = new JSONObject();

            jsrec.put("tipo","02");
            jsrec.put("nombre",nombre);
            jsrec.put("correo",correo);
            jsrec.put("numero_documento",nit);

            receptor =true;
        }

        public void agregarContingencia(String cllave) {
            contingencia=true;
            llave_cont=cllave;
        }

        public void json() throws JSONException {

            //if (receptor) jshead.put("receptor",jsrec);
            jshead.put("items",jsitems);
            jshead.put("adendas",jsad);
            if (contingencia) jshead.put("documento_firmado",llave_cont);
            jsdoc.put("documento",jshead);

            json = jsdoc.toString();
            json+="";
        }

    }

    public class JSONCredito {
        public MiscUtils mu;
        public String json;

        private JSONObject jsdoc,jso,jsitem,jshead,jsr,jsrd,jst;
        private JSONArray jsitems;

        private String llave_cont;
        private boolean contingencia;


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

            contingencia=false;
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

            double precio;

            jsitem = new JSONObject();

            jsitem.put("tipo", 1);
            jsitem.put("cantidad", cantidad);
            jsitem.put("unidad_medida", 59);
            //jsitem.put("descuento", 25);
            jsitem.put("descripcion", descripcion);

            precio=precio_unitario*cantidad-impuesto_monto;
            precio=precio/cantidad;precio=mu.round2dec(precio);
            //jsitem.put("precio_unitario", precio_unitario);
            jsitem.put("precio_unitario", precio);

            JSONArray jstrib = new JSONArray();

            impuesto_monto=mu.round2(impuesto_monto);

            if (impuesto_monto >0) {

                jst = new JSONObject();
                jst.put("codigo", "20");

                jst.put("monto", impuesto_monto);
                jstrib.put(jst);

                jsitem.put("tributos", jstrib);

            } else {
                jsitem.put("tipo_venta", "3");
            }

            jsitems.put(jsitem);
        }

        public void agregarContingencia(String cllave) {
            contingencia=true;
            llave_cont=cllave;
        }

        public void json() throws JSONException {
            jshead.put("items",jsitems);
            if (contingencia) jshead.put("documento_firmado",llave_cont);
            jsdoc.put("documento",jshead);

            json = jsdoc.toString();
            json+="";
        }

    }

    public class JSONCreditoCont {
        public MiscUtils mu;
        public String json;
        public String idDTE,iddocumento;

        private JSONObject jsdoc,jso,jsitem,jshead,jsr,jsrd,jst;

        private JSONArray jsitems;


        public void CreditoCont(String iddoc,String establecimiento,int idruta,boolean produccion) throws JSONException {
            CreditoCont(iddoc,establecimiento,idruta,produccion,2);
        }

        public void CreditoCont(String iddoc,String establecimiento,int idruta,boolean produccion,int condicion_pago) throws JSONException {
            iddocumento=iddoc;
            idDTE=numControlFEL(false,establecimiento,iddoc,idruta,mu);

            jsdoc = new JSONObject();
            jsitems=new JSONArray();
            jshead = new JSONObject();

            jshead.put("tipo_contingencia",3);
            jshead.put("motivo_contingencia","Pérdida de conexión a internet");
            jshead.put("percibir_iva",true);
            if (produccion) jshead.put("ambiente","01");else jshead.put("ambiente","00");
            jshead.put("tipo_dte","03");
            jshead.put("establecimiento",establecimiento);
            jshead.put("numero_control",idDTE);
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
            double precio;

            jsitem = new JSONObject();

            jsitem.put("tipo", 1);
            jsitem.put("cantidad", cantidad);
            jsitem.put("unidad_medida", 59);
            //jsitem.put("descuento", 25);
            jsitem.put("descripcion", descripcion);

            precio=precio_unitario*cantidad-impuesto_monto;
            precio=precio/cantidad;precio=mu.round2dec(precio);
            //jsitem.put("precio_unitario", precio_unitario);
            jsitem.put("precio_unitario", precio);

            JSONArray jstrib = new JSONArray();

            jst = new JSONObject();
            jst.put("codigo", "20");
            impuesto_monto=mu.round2(impuesto_monto);
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

    public class JSONFacturaCont {
        public MiscUtils mu;
        public String json;
        public String idDTE,iddocumento;

        private JSONObject jsdoc,jso,jsitem,jshead,jsr,jsrd,jst;
        private JSONArray jsitems;

        public void FacturaCont(String iddoc,String establecimiento,int idruta,boolean produccion) throws JSONException {
            FacturaCont(iddoc,establecimiento,idruta,produccion,2);
        }

        public void FacturaCont(String iddoc,String establecimiento,int idruta,boolean produccion,int condicion_pago) throws JSONException {
            iddocumento=iddoc;
            idDTE=numControlFEL(false,establecimiento,iddoc,idruta,mu);

            jsdoc = new JSONObject();
            jsitems=new JSONArray();
            jshead = new JSONObject();

            jshead.put("tipo_contingencia",3);
            jshead.put("motivo_contingencia","Pérdida de conexión a internet");
            jshead.put("percibir_iva",false);
            if (produccion) jshead.put("ambiente","01");else jshead.put("ambiente","00");
            jshead.put("tipo_dte","01");
            jshead.put("establecimiento",establecimiento);
            jshead.put("numero_control",idDTE);
            jshead.put("condicion_pago",condicion_pago);

        }

        public void Receptor(String nit,String nombre) throws JSONException {
            jsr = new JSONObject();

            jsr.put("tipo","02");
            jsr.put("nombre",nombre);
            jsr.put("numero_documento",nit);

            jshead.put("receptor",jsr);

        }

        /*
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

         */

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

        public void json() throws JSONException {
            jshead.put("items",jsitems);
            jsdoc.put("documento",jshead);

            json = jsdoc.toString();
        }

    }

    public class JSONAnulacion {
        public String json;

        private JSONObject jsdoc,jshead,jsres,jssol;
        private anulacionDatos ad;

        private int tipoanul;

        public void Anulacion(anulacionDatos datos) throws JSONException {
            Anulacion(datos,2);
        }

        public void Anulacion(anulacionDatos datos,int tipo_anulacion) throws JSONException {
            ad=datos;
            tipoanul=tipo_anulacion;

            jsdoc  = new JSONObject();
            jshead = new JSONObject();
            jsres  = new JSONObject();
            jssol  = new JSONObject();

            jshead.put("establecimiento",ad.establecimiento);
            jshead.put("uuid",ad.uuid);
            jshead.put("tipo_anulacion",tipoanul);
            //jshead.put("motivo","Requerimiento de cliente");
            jshead.put("motivo","Prueba Anulación");

            jsres.put("nombre",ad.responsable_nom);
            jsres.put("tipo_documento","36");
            jsres.put("numero_documento",ad.responsable_nit);
            if (!ad.responsable_correo.isEmpty()) jsres.put("correo",ad.responsable_correo);

            jssol.put("nombre",ad.solicitante_nom);
            jssol.put("tipo_documento","36");
            jssol.put("numero_documento",ad.solicitante_nit);
            jssol.put("correo",ad.solicitante_correo);

            jshead.put("responsable",jsres);
            jshead.put("solicitante",jssol);
            jsdoc.put("invalidacion",jshead);

            json = jsdoc.toString();
            json+="";
        }

    }

    public class JSONContingencia {
        public String conterr;

        private Generador contgen;
        private String archivo_llave;

        public JSONContingencia(String Usuario,String Clave,String Ambiente,String Archivo_llave) {
            archivo_llave=Archivo_llave;
            contgen=new Generador(Usuario, Clave, Ambiente); // 01 Sucursal, 02 Casa matriz
        }

        public String certifica(String dtejson) throws Exception {
            String json="",jsonFirmado="";

            conterr="";

            try {
                File fcert = new File(Environment.getExternalStorageDirectory(), "/"+archivo_llave);
                InputStream certStream = new FileInputStream(fcert);
                String cert = new BufferedReader(new InputStreamReader(certStream)).lines().collect(Collectors.joining());

                try {
                   json = contgen.generarJson(dtejson);
                } catch (Exception e) {
                    throw new Exception("json incorrecto: "+e.getMessage());
                }
                try {
                    jsonFirmado = contgen.firmar(json,cert);
                } catch (Exception e) {
                    throw new Exception("error de firma: "+e.getMessage());
                }

                if (jsonFirmado==null) {
                    throw new Exception("no se logro firmar.");
                }

                JSONObject jObj = new JSONObject(jsonFirmado);

                Boolean rslt=jObj.getBoolean("ok");

                if (rslt) {
                    String token=jObj.getString("token");
                    return token;
                } else {
                    conterr=jObj.toString();return "";
                }

            } catch (Exception e) {
                throw new Exception("Contingencia: "+e.getMessage());
           }
        }

    }

    public class respuesta  {
        String mensaje;
        boolean duplicado;
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

    public class anulacionDatos {
        public String establecimiento;
        public String uuid;
        public String responsable_nom;
        public String responsable_nit;
        public String responsable_correo;
        public String solicitante_nom;
        public String solicitante_nit;
        public String solicitante_correo;
    }

    public class FELAmbiente {
        public String ArchivoLlave,URL,URLAnul;

        // +++++ Certificacion
        //URL Sandbox https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
        //URL Prueba https://certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
        //URL Producción https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/certificar

        // +++++ Anulacion
        //URL Sandbox https://sandbox-certificador.infile.com.sv/api/v1/
        //URL Prueba https://certificador.infile.com.sv/api/v1/certificacion/test/documento/invalidacion
        //URL Producción https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/invalidacion


        public FELAmbiente(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {

            clsP_fel_sv_ambObj P_fel_sv_ambObj=new clsP_fel_sv_ambObj(context,dbconnection,dbase);

            P_fel_sv_ambObj.fill();

            if (P_fel_sv_ambObj.count==0) {
                ArchivoLlave="";return;
            }

            clsClasses.clsP_fel_sv_amb item=P_fel_sv_ambObj.first();

            ArchivoLlave=item.archivo;

            URL="https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar";
            URLAnul="https://sandbox-certificador.infile.com.sv/api/v1/";

            switch (item.ambiente) {
                case 1:
                    URL = "https://certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar";
                    URLAnul = "https://certificador.infile.com.sv/api/v1/certificacion/test/documento/invalidacion";
                    break;
                case 2:
                    URL = "https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/certificar";
                    URLAnul = "https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/invalidacion";
                    break;
            }

        }

    }

    //region Aux

    public String numControlFEL(boolean esFactura,String codEstab,String uid,int idruta,MiscUtils mu) {
        String nc="DTE-",cpos;

        if (esFactura) nc=nc+"01-";else nc=nc+"03-";

        codEstab=mu.leftPad(codEstab,"0",4);
        cpos=""+idruta;
        cpos=mu.leftPad(cpos,"0",4);
        nc=nc+codEstab+cpos+"-";

        uid=mu.leftPad(uid,"0",15);
        nc=nc+uid;

        return nc;
    }

    //endregion

}