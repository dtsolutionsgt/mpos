package com.dtsgt.felesa;

import android.content.Context;

import com.dtsgt.classes.InfileService;
import com.dtsgt.classes.RetrofitClient;
import com.dtsgt.mpos.PBase;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class clsAnulESA {

    public boolean errorflag;
    public String error="",estado;
    public String codigoGeneracion,selloRecepcion,mensaje;
    public boolean resultado;
    public int anulresult;

    private clsFELClases fclas=new clsFELClases();

    private JSONObject jso = new JSONObject();

    private PBase parent;
    private Context cont;


    private String jsanul,usuario,clave;

    //URL Sandbox https://sandbox-certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
    //URL Prueba https://certificador.infile.com.sv/api/v1/certificacion/test/documento/certificar
    //URL Producción https://certificador.infile.com.sv/api/v1/certificacion/prod/documento/certificar

    private String WSURL="https://sandbox-certificador.infile.com.sv/api/v1/";

    public clsAnulESA(PBase Parent, String Usuario, String Clave) {
        parent = Parent;
        cont = Parent;
        usuario = Usuario;
        clave = Clave;
    }

    public void Anular(String json) throws Exception {
        jsanul=json;

        anulresult=-1;error="";errorflag=false;
        mensaje="";codigoGeneracion="";selloRecepcion="";resultado=false;

        try {

            Retrofit retrofit = RetrofitClient.getClient(WSURL);
            InfileService service = retrofit.create(InfileService.class);

            Call<String> call = service.invalidateDocument(usuario,clave,jsanul);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {

                            String responseBody = response.body();

                            JSONObject jsonObj = new JSONObject(responseBody);
                            mensaje = jsonObj.getString("mensaje");

                            JSONObject respuesta = jsonObj.getJSONObject("respuesta");
                            codigoGeneracion = respuesta.getString("codigoGeneracion");
                            selloRecepcion = respuesta.getString("selloRecepcion");
                            resultado = jsonObj.getBoolean("ok");

                            if (resultado) anulresult=1;else anulresult=0;

                        } else {

                            if (response.errorBody() != null) {
                                ResponseBody errorBody = response.errorBody();
                                String errorString = errorBody.string();

                                JSONObject jsonObj = new JSONObject(errorString);
                                JSONObject errores = jsonObj.getJSONObject("errores");
                                String ss=errores.toString();
                                mensaje = errores.getString("uuid");

                                anulresult=0;
                            } else {
                                errorflag=true;error="Anular onResponse: Response error unknown";
                                throw new RuntimeException(error);
                            }
                        }

                    } catch (Exception e) {
                        error="Anular onResponse: "+e.getMessage();
                        errorflag=true;
                    }

                    try {
                        parent.felCallBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    errorflag=true;error="Anular onFailure: "+t.getMessage();
                    try {
                        parent.felCallBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //throw new RuntimeException("Error de conexion al internet\n"+t.getMessage());
                }
            });

        } catch (Exception e) {
            errorflag=true;error=e.getMessage();
            throw new RuntimeException(e);
        }

    }

}


