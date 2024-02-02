package com.dtsgt.felesa;

import android.os.Bundle;
import android.view.View;

import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_factura_svObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FELESATest extends PBase {

    private clsFELClases fclas=new clsFELClases();
    private clsFELClases.JSONFactura jfact;
    private clsFELClases.JSONCredito jcred;

    private clsFactESA FactESA;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_factura_svObj D_factura_svObj;

    private JSONObject jsdoc;

    private String FELestabl,FELUsuario,FELClave;
    private String corel,dnum,cnombre,cnit,cdir, ccorreo,cgiro,cdep,cmuni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_felesatest);

            super.InitBase();

            D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturadObj=new clsD_facturadObj(this,Con,db);
            D_facturafObj=new clsD_facturafObj(this,Con,db);
            D_factura_svObj=new clsD_factura_svObj(this,Con,db);

            //Valores de la tabla P_SUCURSAL (FEL_CODIGO_ESTABLECIMIENTO,FEL_USUARIO_FIRMA,FEL_LLAVE_FIRMA )
            FELestabl="0001";
            FELUsuario="06141106141147";
            FELClave="df3b5497c338a7e78d659a468e72a670";

            FactESA=new clsFactESA(this,FELUsuario,FELClave);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doUltimaFactura(View view) {
        try {
            D_facturaObj.fill("ORDER BY COREL DESC");

            corel=D_facturaObj.first().corel;
            dnum=D_facturaObj.first().serie+D_facturaObj.first().corelativo+"";

            D_facturafObj.fill("WHERE (COREL='"+corel+"')");

            cnombre=D_facturafObj.first().nombre;
            cnit=D_facturafObj.first().nit;cnit=cnit.replace("-","");
            cdir=D_facturafObj.first().direccion;
            ccorreo =D_facturafObj.first().correo;

            D_factura_svObj.fill("WHERE (COREL='"+corel+"')");
            cgiro=D_factura_svObj.first().codigo_tipo_negocio+"";
            cdep=D_factura_svObj.first().codigo_departamento;
            cmuni=D_factura_svObj.first().codigo_municipio;


            //cnombre="Prueba Receptor";
            //cnit="2247806";
            //cdir="Calle 2";
            //ccorreo ="pruebas@pruebas.com";

            cgiro="61101";
            cdep="06";cmuni="14";

            //dnum="100003008";
            //if (creaJSONFijoCredito()) FactESA.Certifica(dnum, jsdoc.toString());

            dnum="11119000000008";

            if (JSOND_Credito()) FactESA.Certifica(dnum, jcred.json);
            //if (JSOND_Factura()) FactESA.Certifica(num,jfact.json);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doFactura(View view) {
        try {
            if (creaJSONFijoFactura()) FactESA.Certifica("100002005",jfact.json);
            //if (creaJSONCredito()) FactESA.Certifica(jfact.json);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private boolean JSOND_Credito() {
        try {
            jcred=fclas.new JSONCredito();

            jcred.Credito(FELestabl);

            jcred.Receptor(dnum,cnit,cnombre,cgiro,cnombre);
            jcred.Direccion(cdep,cmuni,cdir, ccorreo);

            D_facturadObj.fill("WHERE (COREL='"+corel+"')");


            for (int i = 0; i <D_facturadObj.count; i++) {
                jcred.agregarProducto("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).preciodoc,  // precio sin iva
                        D_facturadObj.items.get(i).imp);
            }

            jcred.json();

            String sj= jcred.json;
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean JSOND_Factura() {
        try {
            jfact=fclas.new JSONFactura();

            jfact.Factura(FELestabl);

            D_facturadObj.fill("WHERE (COREL='"+corel+"')");
            for (int i = 0; i <D_facturadObj.count; i++) {
                jfact.agregarServicio("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio con iva
                        D_facturadObj.items.get(i).desmon);
            }

            jfact.agregarAdenda(" ");

            jfact.json();
            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean JSONCredito() {
        try {
            jcred=fclas.new JSONCredito();

            jcred.Credito(FELestabl);

            jcred.Receptor("11111111111128","2247806","Receptor Prueba","61101","Prueba Receptor");
            jcred.Direccion("06","14","Calle 2","pruebas@pruebas.com");

            jcred.agregarProducto("Prueba item 1",1,100,13);

            /*
            D_facturadObj.fill("WHERE (COREL='"+corel+"')");
            for (int i = 0; i <D_facturadObj.count; i++) {
                jfact.agregarServicio("Producto "+(i+1),
                        D_facturadObj.items.get(i).cant,
                        D_facturadObj.items.get(i).precio,  // precio con iva
                        D_facturadObj.items.get(i).desmon);
            }
            */

            jcred.json();
            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean creaJSONFijoFactura()  {
        try {

            jfact=fclas.new JSONFactura();

            jfact.Factura("0001");
            //jfact.agregarProducto("Prueba item 1",1,250);
            jfact.agregarServicio("Prueba item 2",1,200,50);
            jfact.agregarAdenda("01");

            jfact.json();
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean creaJSONFijoCredito() throws JSONException {
        JSONObject jsitem;

        jsdoc = new JSONObject();

        JSONObject jshead = new JSONObject();
        jshead.put("tipo_dte","03");
        jshead.put("establecimiento","0001");
        jshead.put("condicion_pago",2);

        JSONObject jsr = new JSONObject();
        jsr.put("tipo_documento","36");
        jsr.put("numero_documento","11111111111128");
        jsr.put("nrc","2247806");
        jsr.put("nombre","Receptor Prueba");
        jsr.put("codigo_actividad","61101");
        jsr.put("nombre_comercial","Prueba Receptor");

        JSONObject jsrd = new JSONObject();
        jsrd.put("departamento","06");
        jsrd.put("municipio","14");
        jsrd.put("complemento","Calle 2");
        jsr.put("direccion",jsrd);

        jsr.put("correo","pruebas@pruebas.com");
        jshead.put("receptor",jsr);


        JSONArray jsitems=new JSONArray();

        jsitem = new JSONObject();

        jsitem.put("tipo", 1);
        jsitem.put("cantidad", 1);
        jsitem.put("unidad_medida", 59);
        //jsitem.put("descuento", 25);
        jsitem.put("descripcion", "Prueba item 1");
        jsitem.put("precio_unitario", 100);

        JSONArray jstrib=new JSONArray();

        double tmonto=13;

        JSONObject jst = new JSONObject();
        jst.put("codigo","20");
        jst.put("monto",tmonto);
        jstrib.put(jst);

        jsitem.put("tributos",jstrib);

        jsitems.put(jsitem);

        jshead.put("items",jsitems);

        jsdoc.put("documento",jshead);

        String js=jsdoc.toString();
        return true;
    }

    @Override
    public void felCallBack()  {
        try {
            if (!FactESA.errorflag) {
                msgbox(FactESA.estado+"\n"+FactESA.respuesta.totalPagar);
                //marcaFactura();
            } else {
                msgbox("Error: "+FactESA.error);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();
        try {
            D_facturaObj.reconnect(Con,db);
            D_facturadObj.reconnect(Con,db);
            D_facturafObj.reconnect(Con,db);
            D_factura_svObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

}