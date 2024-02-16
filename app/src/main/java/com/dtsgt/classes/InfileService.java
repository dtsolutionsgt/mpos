package com.dtsgt.classes;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InfileService {

    @Headers("Content-Type: application/json")
    @POST("certificacion/test/documento/invalidacion")
    Call<String> invalidateDocument(
            @Header("usuario") String usuario,
            @Header("llave") String llave,
            @Body String body
    );
}