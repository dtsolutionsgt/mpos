package com.dtsgt.webapi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {

    public int retcode = 0;
    public String data = "";

    private OkHttpClient client;

    private Runnable rnCallBack;

    public HttpClient() {
        client = new OkHttpClient();
    }

    public void processRequest(String URL, Runnable callback) {

        Request request = new Request.Builder()
                .url(URL)
                .build();

        rnCallBack = callback;

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                data = e.getMessage();
                retcode = -1;
                rnCallBack.run();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    data = response.body().string();
                    retcode = 1;
                } else {
                    data = response.code() + " " + response.message();
                    retcode = 0;
                }
                rnCallBack.run();
            }
        });
    }

    public List<String> splitJsonArray() throws JSONException {
        String jos = "";

        String jss = data; // assuming `data` is a String variable
        jss = jss.replace("$type", "type");

        JSONArray jsonArray = new JSONArray(jss);
        List<String> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            jos = jsonArray.getString(i).trim();
            list.add(jos);
        }

        return list;
    }


}
