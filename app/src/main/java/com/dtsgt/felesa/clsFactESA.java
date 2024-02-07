package com.dtsgt.felesa;

import android.content.Context;
import android.os.AsyncTask;

import com.dtsgt.mpos.PBase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class clsFactESA {

    public boolean errorflag;
    public String  error="",estado;


    private clsFELClases fclas=new clsFELClases();
    public clsFELClases.respuesta respuesta =fclas.new respuesta();

    private JSONObject jso = new JSONObject();

    private PBase parent;
    private Context cont;

    private String jsfirm,corel;

    private int responsecode,timeout=45000;

    private String usuario, clave;

    //URL Sandbox https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
    //URL Prueba https://certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
    //URL Producción https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/certificar

    //URL Sandbox https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/invalidacion
    //URL Prueba https://certificador.infile.com.sv/api/v1/certificacion/test/documento/invalidacion
    //URL Producción https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/invalidacion




    public clsFactESA(PBase Parent, String Usuario, String Clave) {
        parent = Parent;
        cont = Parent;
        usuario = Usuario;
        clave = Clave;
    }

    public void Certifica(String Corel,String json)  {
        corel=Corel;jsfirm=json;

        AsyncCallWS wstask = new AsyncCallWS();
        wstask.execute();
    }

    private Boolean wsExecuteF(){
        HttpURLConnection connection = null;
        JSONObject jObj=null;
        URL url;

        try {
            responsecode=0;error="";errorflag=false;

            String WSURL="https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar";

            timeout=45000;

            url = new URL(WSURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("usuario","06141106141147");
            connection.setRequestProperty("llave","df3b5497c338a7e78d659a468e72a670");
            //connection.setRequestProperty("identificador",corel);
            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            try {
                connection.connect();
            } catch (SocketTimeoutException s){
                error="No responde1: " + s.getMessage();
                errorflag=true;return errorflag;
            } catch (IOException e) {
                error="Probléma de conexión: "+e.getMessage();
                errorflag=true;return errorflag;
            } catch (Exception e) {
                error=e.getMessage();
                errorflag=true;return errorflag;
            }

            DataOutputStream wr = null;

            try {
                wr = new DataOutputStream(connection.getOutputStream ());
            } catch (SocketTimeoutException s){
                error="No responde2: " + s.getMessage();
                errorflag=true;return errorflag;
            } catch (IOException e) {
                error=e.getMessage();errorflag=true;return null;
            }

            wr.writeBytes (jsfirm);
            wr.flush ();
            wr.close ();

            InputStream is;

            try {
                is= connection.getInputStream();
            } catch (SocketTimeoutException s){
                error="No responde3: " + s.getMessage();
                errorflag=true;return errorflag;
            }

            responsecode =connection.getResponseCode();

            if (responsecode==200 | responsecode==201) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();

                String jstr=sb.toString();
                jObj = new JSONObject(jstr);

                Boolean rslt=jObj.getBoolean("ok");

                if (rslt) {
                    errorflag=false;

                    try {
                        respuesta =fclas.new respuesta();

                        respuesta.mensaje=jObj.getString("mensaje");estado= respuesta.mensaje;
                        respuesta.pathpdf=jObj.getString("pdf_path");

                        jso=jObj.getJSONObject("respuesta");
                        respuesta.identificador=jso.getString("identificador");
                        respuesta.codigoGeneracion=jso.getString("codigoGeneracion");
                        respuesta.selloRecepcion=jso.getString("selloRecepcion");
                        respuesta.numeroControl=jso.getString("numeroControl");
                        respuesta.status=jso.getString("status");
                        respuesta.fechaEmision=jso.getString("fechaEmision");

                        jso=jObj.getJSONObject("respuesta_dgi");

                        respuesta.estado=jso.getString("estado");
                        respuesta.descripcionMsg=jso.getString("descripcionMsg");
                        respuesta.comentarios=jso.getString("comentarios");

                        JSONObject jsjs=jObj.getJSONObject("json");
                        jso=jsjs.getJSONObject("resumen");

                        respuesta.totalNoSuj=jso.getDouble("totalNoSuj");
                        respuesta.totalExenta=jso.getDouble("totalExenta");
                        respuesta.totalGravada=jso.getDouble("totalGravada");
                        respuesta.subTotalVentas=jso.getDouble("subTotalVentas");
                        respuesta.descuNoSuj=jso.getDouble("descuNoSuj");
                        respuesta.descuExenta=jso.getDouble("descuExenta");
                        respuesta.descuGravada=jso.getDouble("descuGravada");
                        respuesta.porcentajeDescuento=jso.getDouble("porcentajeDescuento");
                        respuesta.totalDescu=jso.getDouble("totalDescu");
                        respuesta.subTotal=jso.getDouble("subTotal");
                        respuesta.ivaRete1=jso.getDouble("ivaRete1");
                        respuesta.reteRenta=jso.getDouble("reteRenta");
                        respuesta.montoTotalOperacion=jso.getDouble("montoTotalOperacion");
                        respuesta.totalNoGravado-=jso.getDouble("totalNoGravado");
                        respuesta.totalPagar=jso.getDouble("totalPagar");
                        respuesta.totalLetras=jso.getString("totalLetras");
                        try {
                            respuesta.totalIva=jso.getDouble("totalIva");
                        } catch (Exception e) {
                            respuesta.totalIva=0;
                        }
                        respuesta.saldoFavor=jso.getDouble("saldoFavor");
                    } catch (Exception se){
                        error="JSON error1: " + se.getMessage();
                        errorflag=true;return errorflag;
                    }

                } else {
                    errorflag=true;
                    try {
                        //error+=vDescripcion;

                    } catch (Exception se) {
                        error="JSON error2: " + se.getMessage();
                        errorflag=true;return errorflag;
                    }
                }
            } else {
                error=""+ responsecode;errorflag=true;return errorflag;
            }
        } catch (SocketTimeoutException s){
            error="No responde4: " + s.getMessage();
            errorflag=true;return errorflag;
        } catch (Exception e) {
            error=e.getMessage();
            errorflag=true;return errorflag;
        }

        return errorflag;
    }

    private void wsFinishedF() {
        try  {
            if (estado.isEmpty()) estado="Certificado";
            parent.felCallBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)  {
            try  {
                wsExecuteF();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorflag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (!errorflag){
                    wsFinishedF();
                }else{
                    parent.felCallBack();
                }
            } catch (Exception e)  {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onCancelled() {
            try {
                errorflag=true;error+="Se agotó tiempo de certificación";
                parent.felCallBack();
            } catch (Exception e) {
                String ss=e.getMessage();
                ss=ss+"";
            }
            super.onCancelled();
        }

    }

}


