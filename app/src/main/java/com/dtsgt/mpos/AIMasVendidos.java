package com.dtsgt.mpos;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.webservice.wsOpenDT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIMasVendidos extends PBase {

    private TextView lblrslt;
    private ProgressBar pBar;

    private final OkHttpClient client = new OkHttpClient();

    private wsOpenDT wso;

    private String query,result,slistp;
    private int idsuc,idemp;
    private boolean completo=false;

    private String ID="   ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_aimas_vendidos);

            super.InitBase();

            lblrslt = findViewById(R.id.textView386); lblrslt.setText("");
            pBar  = findViewById(R.id.progressBar11);

            idsuc=gl.tienda;idemp=gl.emp;
            idsuc=100;idemp=2;

            app.getURL();
            wso=new wsOpenDT(gl.wsurl);


            buildAIRequestAndCall();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events


    //endregion

    //region Main

    private void processQuery() {
        try {

            result="";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-4.1");
            jsonBody.put("input", query);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/responses")
                    .header("Authorization", "Bearer " + ID)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result="API call failed "+e.getMessage();
                    showResult();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        result= response.toString();
                        showResult();
                        return;
                    }

                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                    try {
                        JsonArray jsonOutput = jsonResponse.getAsJsonArray("output");
                        if (jsonOutput.size()>0) {
                            JsonObject json0= jsonOutput.get(0).getAsJsonObject();
                            JsonArray jsonContent = json0.getAsJsonArray("content");
                            JsonObject jsonText= jsonContent.get(0).getAsJsonObject();
                            result= jsonText.get("text").getAsString();

                            completo=true;
                            showResult();
                        }
                    } catch (Exception e) {
                        result= e.getMessage();
                        showResult();
                    }

                }
            });

        } catch (Exception e) {
            result=new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage();
            showResult();
        }
    }

    //endregion

    //region AI Request

    private void buildAIRequestAndCall() {
        long ff;
        String sff;

        try {
            completo=false;

            ff=du.getActDate();ff=du.addDays(ff,-3);
            sff=du.univfechasql(ff);

            sql="SELECT  TOP (20) D_FACTURAD.PRODUCTO AS PCOD " +
                "FROM  D_FACTURA INNER JOIN .P_RUTA ON D_FACTURA.EMPRESA = P_RUTA.EMPRESA " +
                "INNER JOIN D_FACTURAD ON dD_FACTURA.COREL = D_FACTURAD.COREL " +
                "WHERE  (P_RUTA.SUCURSAL = "+idsuc+") AND (D_FACTURA.FECHA >= '"+sff+"') " +
                "GROUP BY D_FACTURA.EMPRESA, D_FACTURAD.PRODUCTO" +
                "HAVING   (D_FACTURA.EMPRESA = "+idemp+") ORDER BY SUM(D_FACTURAD.CANT) DESC ";

            wso.execute(sql,() -> { cbListaRutas(); });
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbListaRutas() {
        try {
            if (wso.errflag) throw new Exception(wso.error);

            Cursor dt=wso.openDTCursor;

            if (dt.getCount()==0) throw new Exception("El sucursal no tiene definida ninguna caja");

            wso.openDTCursor.moveToFirst();



            try {

                slistp="";
                dt=Con.OpenDT(sql);

                if (dt.getCount()>0) {
                    dt.moveToFirst();
                    while (!dt.isAfterLast()) {

                        dt.moveToNext();
                    }

                    if (!dt.isLast()) {

                    }
                }

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }



        } catch (Exception e) {
            result=wso.error;
            showResult();
        }
    }

    private void cbListaRutas3() {
        try {

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbListaRutas4() {
        try {

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbListaRutasx() {
        try {
            buildQuery();

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                processQuery();
            };
            mtimer.postDelayed(mrunner,200);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildQuery() {
        try {
            query=" Calculate sale forecast from following   \n" +
                    "  \n" +
                    "CODE,DATE1,DATE2,DATE3  \n" +
                    "40,155,284,77  \n" +
                    "41,119,153,85  \n" +
                    "42,129,169,90 \n" +
                    "43,111,254,56  \n" +
                    "44,10,28  \n" +
                    "  \n" +
                    "answer with the final values only in csv format";
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Dialogs


    //endregion

    //region Aux

    private void showResult() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lblrslt.setText(result);
                            pBar.setVisibility(TextView.INVISIBLE);
                        }
                    });

                }
            }).start();
        } catch (Exception e) {
            String serror = e.getMessage();
            serror=serror+"";
        }
    }

    //endregion

    //region Activity Events


    //endregion

}