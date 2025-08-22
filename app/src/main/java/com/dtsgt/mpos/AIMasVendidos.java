package com.dtsgt.mpos;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_ai_masvend_listaObj;
import com.dtsgt.classes.clsT_ai_masvend_tablaObj;
import com.dtsgt.classes.clsT_ai_masvendidosObj;
import com.dtsgt.webservice.wsOpenDT;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIMasVendidos extends PBase {

    private TextView lblrslt,lblqry;
    private ProgressBar pBar;

    private final OkHttpClient client = new OkHttpClient();

    private clsT_ai_masvend_listaObj T_ai_masvend_listaObj;
    private clsP_productoObj P_productoObj;

    private wsOpenDT wso;

    private List<String> lines = new ArrayList<>();

    private String query, result, slistr, slistp, slistf;
    private int idsuc, idemp;
    private boolean completo = false;
    long   ff1, ff2, ff3;

    private String ID = "   ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_aimas_vendidos);

            super.InitBase();

            lblrslt = findViewById(R.id.textView386);lblrslt.setText("");
            lblqry = findViewById(R.id.textView387);lblqry.setText("");
            pBar = findViewById(R.id.progressBar11);

            T_ai_masvend_listaObj = new clsT_ai_masvend_listaObj(this, Con, db);
            P_productoObj = new clsP_productoObj(this, Con, db);
            P_productoObj.fill();

            idsuc = gl.tienda;
            idemp = gl.emp;
            //idsuc = 100; idemp = 2;


            getChatGPTKey();
            app.getURL();
            wso = new wsOpenDT(gl.wsurl);

            buildAIRequestAndCall();

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }


    //region Events


    //endregion

    //region Main

    private void processQuery() {
        try {

            result = "";

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
                    result = "API call failed " + e.getMessage();
                    showResult();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        result = response.toString();
                        showResult();
                        return;
                    }

                    String responseBody = response.body().string();
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                    try {
                        JsonArray jsonOutput = jsonResponse.getAsJsonArray("output");

                        if (jsonOutput.size() > 0) {
                            JsonObject json0 = jsonOutput.get(0).getAsJsonObject();
                            JsonArray jsonContent = json0.getAsJsonArray("content");
                            JsonObject jsonText = jsonContent.get(0).getAsJsonObject();
                            result = jsonText.get("text").getAsString();

                            new Thread(new Runnable() {
                                @Override
                                public void run() { buildResult(); }
                            }).start();

                            completo = true;
                            showResult();
                        }
                    } catch (Exception e) {
                        result = e.getMessage();
                        showResult();
                    }

                }
            });

        } catch (Exception e) {
            result = new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage();
            showResult();
        }
    }

    private void buildResult() {
        int pcod=0,pval=0;
        double dval=0;
        String scod,sval;

        try {
            if (result == null || result.isEmpty()) {
               completo = true;showResult();return;
            }

            db.execSQL("DELETE FROM T_ai_masvendidos");

            clsT_ai_masvendidosObj T_ai_masvendidosObj=new clsT_ai_masvendidosObj(this,Con,db);
            clsClasses.clsT_ai_masvendidos mitem;


            String[] parts = result.split("\\r?\\n");
            for (String line : parts) {
                if (line.indexOf("|")>=0) {
                    String[] parts2 = line.split("\\|");

                    try {
                        scod=parts2[1].trim();
                        sval=parts2[2].trim();

                        pcod=Integer.parseInt(scod);
                        dval=Double.parseDouble(sval);
                        pval=(int) dval;

                        mitem = clsCls.new clsT_ai_masvendidos();

                        mitem.codigo_producto=pcod;
                        mitem.dia=0;
                        mitem.hora=0;
                        mitem.cant=pval;
                        mitem.nombre=getProdName(pcod);

                        T_ai_masvendidosObj.add(mitem);

                        lines.add(pcod+" - "+pval);
                    } catch (Exception e) {
                        String sa=e.getMessage();
                        sa=pcod+" - "+pval +" : "+sa;
                    }
                }
            }

            result = "";
            for (String line : lines) {
                result += line + "\n";
            }
            showResult();
        } catch (Exception e) {
            result = new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage();
            showResult();
        }
    }

    //endregion

    //region AI Request

    private void buildAIRequestAndCall() {
        try {
            completo = false;

            sql = "SELECT CODIGO_RUTA FROM P_RUTA WHERE SUCURSAL=" + idsuc;
            wso.execute(sql, () -> {
                cbListRutas();
            });
        } catch (Exception e) {
            result = e.getMessage();
            showResult();
        }
    }

    private void cbListRutas() {
        long ff;
        String sff;

        try {
            if (wso.errflag) throw new Exception(wso.error);

            Cursor dt = wso.openDTCursor;
            if (dt.getCount() == 0)
                throw new Exception("El sucursal no tiene definida ninguna caja");

            dt.moveToFirst();

            slistr = "D_FACTURA.RUTA IN (";
            while (!dt.isAfterLast()) {
                slistr += "" + dt.getInt(0);
                if (!dt.isLast()) slistr += ",";

                dt.moveToNext();
            }
            slistr += ")";


            ff = du.getActDate();
            ff = du.addDays(ff, -3);
            sff = du.univfechasql(ff);

            sql = "SELECT  TOP (20) D_FACTURAD.PRODUCTO AS PCOD " +
                    "FROM  D_FACTURA INNER JOIN .P_RUTA ON D_FACTURA.EMPRESA = P_RUTA.EMPRESA " +
                    "INNER JOIN D_FACTURAD ON D_FACTURA.COREL = D_FACTURAD.COREL " +
                    "WHERE  (P_RUTA.SUCURSAL = " + idsuc + ") AND (D_FACTURA.FECHA >= '" + sff + "') " +
                    "GROUP BY D_FACTURA.EMPRESA, D_FACTURAD.PRODUCTO " +
                    "HAVING (D_FACTURA.EMPRESA = " + idemp + ") ORDER BY SUM(D_FACTURAD.CANT) DESC ";

            wso.execute(sql, () -> {
                cbListaProductos();
            });

        } catch (Exception e) {
            result = e.getMessage();
            showResult();
        }
    }

    private void cbListaProductos() {
        long ff;

        try {
            if (wso.errflag) throw new Exception(wso.error);

            Cursor dt = wso.openDTCursor;
            if (dt.getCount() == 0) {
                completo = true;
                result = "El sucursal no tiene venta en ultimos 3 dias.";
                showResult();
            }

            ff = du.getActDate();
            ff1 = du.addDays(ff, -3);
            ff2 = du.addDays(ff, -2);
            ff3 = du.addDays(ff, -1);

            slistf = "D_FACTURA.FECHA IN (";
            slistf += "'" + du.univfechasql(ff1) + "',";
            slistf += "'" + du.univfechasql(ff2) + "',";
            slistf += "'" + du.univfechasql(ff3)+ "'";
            slistf += ")";

            dt.moveToFirst();
            try {

                slistp = "D_FACTURAD.PRODUCTO IN (";
                while (!dt.isAfterLast()) {
                    slistp += "" + dt.getInt(0);
                    if (!dt.isLast()) slistp += ",";

                    dt.moveToNext();
                }
                slistp += ")";

                sql = "SELECT  TOP (200) D_FACTURAD.PRODUCTO, dbo.AndrDate(D_FACTURA.FECHA) AS Fecha, SUM(D_FACTURAD.CANT) AS PCANT " +
                        "FROM D_FACTURA INNER JOIN  D_FACTURAD ON D_FACTURA.COREL = D_FACTURAD.COREL " +
                        "WHERE  (" + slistf + ") " +
                        "GROUP BY D_FACTURA.EMPRESA, dbo.D_FACTURA.RUTA, D_FACTURAD.PRODUCTO, D_FACTURA.FECHA " +
                        "HAVING  (D_FACTURA.EMPRESA = " + idemp + ") AND (" + slistr + ") AND  (" + slistp + ") " +
                        "ORDER BY D_FACTURAD.PRODUCTO, Fecha";

                wso.execute(sql, () -> {
                    cbTablaVentas();
                });
            } catch (Exception e) {
                result = e.getMessage();
                showResult();
            }

        } catch (Exception e) {
            result = e.getMessage();
            showResult();
        }
    }

    private void cbTablaVentas() {
        clsClasses.clsT_ai_masvend_lista item;
        clsClasses.clsT_ai_masvend_tabla titem;
        int pcod;
        String qr;

        try {
            if (wso.errflag) throw new Exception(wso.error);

            Cursor dt = wso.openDTCursor;
            if (dt.getCount() == 0) {
                completo = true;
                result = "El sucursal no tiene venta en ultimos 3 dias.";
                showResult();
            }

            db.execSQL("DELETE FROM T_ai_masvend_lista");

            dt.moveToFirst();
            while (!dt.isAfterLast()) {
                item = clsCls.new clsT_ai_masvend_lista();

                item.codigo_producto = dt.getInt(0);
                item.fecha = dt.getLong(1);
                item.cant = dt.getInt(2);

                T_ai_masvend_listaObj.add(item);

                dt.moveToNext();
            }

            db.execSQL("DELETE FROM T_ai_masvend_tabla");
            clsT_ai_masvend_tablaObj T_ai_masvend_tablaObj=new clsT_ai_masvend_tablaObj(this,Con,db);

            sql="SELECT DISTINCT CODIGO_PRODUCTO FROM T_ai_masvend_lista";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {

                query ="Calculate sale forecast from following values.   \n";
                query+="Values are separated by commas, first column is code, second column is date1, third column is date2, fourth column is date3  \n";
                query+="Answer with the final values only in a table of two columns, first column is code, second column is forecast  \n";
                query+="  \n" ;

                dt.moveToFirst();
                while (!dt.isAfterLast()) {
                    pcod = dt.getInt(0);

                    titem = clsCls.new clsT_ai_masvend_tabla();
                    titem.codigo_producto = pcod;
                    titem.val1 = 0;
                    titem.val2 = 0;
                    titem.val3 = 0;

                    T_ai_masvend_listaObj.fill("WHERE (CODIGO_PRODUCTO="+pcod+") ORDER BY FECHA");
                    for (int i=0;i<T_ai_masvend_listaObj.count;i++) {
                        item=T_ai_masvend_listaObj.items.get(i);

                        if (item.fecha==ff1) titem.val1=item.cant;
                        if (item.fecha==ff2) titem.val2=item.cant;
                        if (item.fecha==ff3) titem.val3=item.cant;
                    }

                    T_ai_masvend_tablaObj.add(titem);

                    qr=""+titem.codigo_producto+","+titem.val1+","+titem.val2+","+titem.val3;
                    query+=qr+"\n";

                    dt.moveToNext();
                }

                query+="  \n" ;

                lblqry.setText(query);

                Handler mtimer = new Handler();
                Runnable mrunner = () -> {
                    processQuery();
                };
                mtimer.postDelayed(mrunner, 200);
            }

        } catch (Exception e) {
            result = e.getMessage();
            showResult();
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
            serror = serror + "";
        }
    }

    private void getChatGPTKey() {

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/apikey.txt");
            if (!file1.exists()) return;

            FileInputStream fIn = new FileInputStream(file1);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            ID = myReader.readLine();
            myReader.close();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private String getProdName(int pid) {
        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+pid);
            return P_productoObj.first().desclarga;
        } catch (Exception e) {
            return "Producto "+pid;
        }
    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            T_ai_masvend_listaObj.reconnect(Con, db);
            P_productoObj.reconnect(Con,db);P_productoObj.fill();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}