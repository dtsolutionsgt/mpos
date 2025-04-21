package com.dtsgt.felpana;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_fel_sv_ambObj;
import com.dtsgt.classes.clsP_sucursalObj;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class clsFELClasesPA {

    public ArrayList<clsClasses.clsP_fel_impuesto> cod_impuesto = new ArrayList<clsClasses.clsP_fel_impuesto>();

    public class JSONFactura {

        public String json;

        private JSONObject jsdoc,jso,jsitem,jsad,jsrec;
        private JSONArray jsitems,jsformas;

        private String ss,llave_cont,forma_pago,tipo_documento,num_doc,estab,caja;
        private boolean contingencia,zona_franca;
        private int tipo_factura;


        public void Factura(int numero,int idcaja,String establecimiento,boolean zonafranca, int tipofactura) throws JSONException {

            tipo_factura=tipofactura;// 1 - RUC, 2 - Cedula, 3 - CF
            zona_franca=zonafranca;
            if (zona_franca) tipo_documento="08";else tipo_documento="01";

            ss=""+numero;num_doc=StringUtils.leftPad(ss,10,'0');
            idcaja=idcaja-200;ss=""+idcaja;
            caja= StringUtils.leftPad(ss,3,'0');
            estab=StringUtils.leftPad(establecimiento,4,'0');

            forma_pago="02";  // efectivo
            contingencia=false;

            jsdoc = new JSONObject();
            jsitems = new JSONArray();
            jsformas = new JSONArray();

            generaEncabezado();
        }

        private void generaEncabezado() throws JSONException {
            jsdoc.put("tipo_emision","01");    // fijo
            jsdoc.put("tipo_documento",tipo_documento);  // 01 Operacion interna , 08 - Zona franca
            jsdoc.put("numero_documento_fiscal",num_doc); //
            jsdoc.put("punto_facturacion",caja); //  (codigo ruta - 200) sirka 3 "00X"
            jsdoc.put("tipo_transaccion_venta", 1); // fijo
            jsdoc.put("emisor_codigo_sucursal",estab); //  sirka 4 "000X"
            jsdoc.put("naturaleza_operacion","01"); // fijo
        }

        public void agregarReceptorRUC(String nombre,String ruc,String DV,
                    String dir,String ubic,String correo,String tel) throws JSONException {
            jsrec = new JSONObject();

            String[] ub = ubic.split("-");

            jsrec.put("receptor_tipo","01");
            jsrec.put("receptor_tipo_contribuyente",2);
            jsrec.put("receptor_ruc",ruc);
            jsrec.put("receptor_digito_verificador",DV);
            jsrec.put("receptor_nombre",nombre);
            jsrec.put("receptor_direccion",dir);
            jsrec.put("receptor_codigo_ubicacion_corregimiento",ub[0]);
            jsrec.put("receptor_codigo_ubicacion_distrito",ub[1]);
            jsrec.put("receptor_codigo_ubicacion_provincia",ub[2]);
            jsrec.put("receptor_telefono",tel);
            jsrec.put("receptor_correo",correo);

        }

        public void agregarReceptor(String nombre,String nit,String correo,String tel) throws JSONException {
            switch (tipo_factura) {
                case 2:
                    agregarReceptorCedula(nombre,nit,correo,tel);break;
                case 3:
                    agregarReceptorCedula("Consumidor final","CF",correo,tel);break;
            }
        }

        public void agregarReceptorCedula(String nombre,String nit,String correo,String tel) throws JSONException {
            jsrec = new JSONObject();

            jsrec.put("receptor_tipo","02");
            jsrec.put("receptor_nombre",nombre);
            jsrec.put("receptor_telefono",tel);
            jsrec.put("receptor_correo",correo);

        }

        public void agregarProducto(String codigo,String descripcion,double cantidad,
                                    double precio_unitario,double impuesto,double descuento_monto) throws JSONException {

            jsitem = new JSONObject();

            jsitem.put("descripcion_producto", descripcion);
            jsitem.put("codigo_interno", codigo);
            jsitem.put("unidad_medida", "und");
            jsitem.put("cantidad_producto", cantidad);
            jsitem.put("precio_unitario", precio_unitario);
            jsitem.put("descuento", descuento_monto);

            double imp_unit=impuesto/cantidad;
            String codImp=codigoImpuesto(precio_unitario,imp_unit);
            jsitem.put("tasa_itbms",codImp);

            jsitems.put(jsitem);
        }

        public void agregarPago(double monto) throws JSONException {

            jsitem = new JSONObject();

            jsitem.put("forma_pago", forma_pago);
            jsitem.put("forma_pago_monto", monto);

            jsformas.put(jsitem);
        }


        public void json() throws JSONException {

            jsdoc.put("receptor",jsrec);
            jsdoc.put("items",jsitems);
            jsdoc.put("formas_pago_factura",jsformas);

            json = jsdoc.toString();
            json +="";
        }

    }

    public class FELAmbiente {
        public String URL,URLAnul,URL_ruc;
        public String usuario_api,llave_api,llave_firma,establecimiento;

        public FELAmbiente(Context context, BaseDatos dbconnection, SQLiteDatabase dbase, int codigo_sucursal) {

            clsP_fel_sv_ambObj P_fel_sv_ambObj=new clsP_fel_sv_ambObj(context,dbconnection,dbase);
            P_fel_sv_ambObj.fill();
            clsClasses.clsP_fel_sv_amb item=P_fel_sv_ambObj.first();


            URL_ruc="https://certificador-unificado.infilepac.com/api/v1/consultas/unificado/test/json/ruc_dv";

            URL="https://certificador-unificado.infilepac.com/api/v1/certificacion/unificado/test/json/simple";
            URLAnul="";

            switch (item.ambiente) {
                case 0:
                    URL="https://certificador-unificado.infilepac.com/api/v1/certificacion/unificado/test/json/simple";
                    URLAnul = "";
                    break;
                case 1:
                    URL = "https://certificador-unificado.infilepac.com/api/v1/certificacion/unificado/prod/json/simple";
                    URLAnul = "";
                    break;
            }

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(context,dbconnection,dbase);
            P_sucursalObj.fill("WHERE (CODIGO_SUCURSAL="+codigo_sucursal+")");
            clsClasses.clsP_sucursal suc=P_sucursalObj.first();

            usuario_api=suc.fel_usuario_firma;
            llave_api=suc.fel_llave_certificacion;
            llave_firma=suc.fel_llave_firma;
            establecimiento=suc.fel_codigo_establecimiento;
        }

    }


    //region Aux

    public  double round2dec(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String codigoImpuesto(double precio,double impuesto) {
        String ci="";
        if (precio==0) return ci;

        double valimpd=100*impuesto/precio;
        int valimp=roundint(valimpd);
        int vimp;

        for (clsClasses.clsP_fel_impuesto itm : cod_impuesto) {
            vimp=(int) itm.valor;
            if (vimp==valimp) return itm.codigo_fel;
        }
        return ci;
    }

    public int roundint(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        int rslt=(int) bd.doubleValue();
        return rslt;
    }

    //endregion
}
