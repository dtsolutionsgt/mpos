package com.dtsgt.webapi;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class HttpCommit {


    public boolean errflag = true;
    public String error = null;

    private String URL;
    private final OkHttpClient client = new OkHttpClient();
    private Runnable rnCallback = null;


    public HttpCommit(String URL) {
        this.URL = URL;
    }

    public void commit(final String sql, final Runnable callback) {
        rnCallback = callback;

        // Run in background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                processCommit(sql);
            }
        }).start();
    }

    private void processCommit(String sql) {
        String json = "{\"sql\":\"" + sql + "\"}";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                error = e.getMessage();
                errflag = true;
                if (rnCallback != null) {
                    rnCallback.run();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    error = response.body().string();
                    errflag = false;
                } else {
                    error = response.code() + " " + response.message();
                    errflag = true;
                }
                if (rnCallback != null) {
                    rnCallback.run();
                }
            }
        });
    }

}
