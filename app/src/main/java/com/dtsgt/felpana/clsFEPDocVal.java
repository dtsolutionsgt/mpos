package com.dtsgt.felpana;

import android.content.Context;
import android.os.AsyncTask;

import com.dtsgt.felesa.clsFELClases;
import com.dtsgt.mpos.PBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class clsFEPDocVal {

    public boolean errorflag;
    public String  error="",WSURL,RUC="";
    public int value;

    private clsFELClases fclas=new clsFELClases();
    public clsFELClases.respuesta respuesta =fclas.new respuesta();
    public ArrayList<String> erritems= new ArrayList<String>();

    private JSONObject jso = new JSONObject();

    private PBase parent;
    private Context cont;

    Pattern rucpattern;

    private String jsruc,usuario, clave_api,clave_firma ;

    private int responsecode,timeout=45000;

    private String regexruc_patt="(([P][E][-](([-]|[0-9]){1,17})|[N][-](([-]|[0-9]){1,18})|[E][-](([-]|[0-9]){1,18})|(([-]|[0-9]){5,20}))|(((([0-9]{1})[-][A][V][-](([-]|[0-9]){1,15}))|(([0-9]{2})[-][A][V][-](([-]|[0-9]){1,14})))|((([0-9]{1,2})[-][N][T][-](([-]|[0-9]){1,15}))|(([0-9]{1,2})[-][N][T][-](([-]|[0-9]){1,14}))|([N][T][-](([-]|[0-9]){1,14}))|(([0-9]{1,2})[-][P][I][-](([-]|[0-9]){1,14}))|([P][I][-](([-]|[0-9]){1,14}))|(([0-9]){1,2}[P][I][-](([-]|[0-9]){1,14})))))?";

    public clsFEPDocVal(PBase Parent, String Usuario, String Clave_Api, String Clave_Firma, String URL) {
        parent = Parent;
        cont = Parent;
        usuario = Usuario;
        clave_api = Clave_Api;
        clave_firma = Clave_Firma;
        WSURL=URL;

        rucpattern = Pattern.compile(regexruc_patt);
    }

    public int validaCedula(String nced) {
        // 1 Regular , 2 Panameño nacido en el extranjero, 3 - Extranjero con cédula
        // 4 Naturalizado , 5 - Panameños nacidos antes de la vigencia , 6 Población indigena

        int rslt=0,np,ps;
        String pp,p1,p2;

        try {
            String[] cp = nced.split("-");
            if (cp.length!=3) return 0;

            pp=cp[0];pp=pp.toUpperCase();
            if (pp.length()>4) return 0;
            if (cp[1].length()>6) return 0;
            if (cp[2].length()>6) return 0;

            try {
                np=Integer.parseInt(pp);
                if ((np>0) && (np<14)) return 1;
            } catch (Exception e) {}

            ps=pp.indexOf("PE");
            if (ps==0) {
                if (pp.length()==2) return 2;
            }

            ps=pp.indexOf("E");
            if (ps==0) {
                if (pp.length()==1) return 3;
            }

            ps=pp.indexOf("N");
            if (ps==0) {
                if (pp.length()==1) return 4;
            }

            ps=pp.indexOf("AV");
            if (ps>0) {
                if (pp.length()==3) {
                    p1=pp.substring(0,1);
                    p2=pp.substring(1);
                    try {
                        np=Integer.parseInt(p1);
                    } catch (Exception e) {
                        return 0;
                    }
                    if ((np<1) | (np>13)) return 0;
                    return 5;
                }
                if (pp.length()==4) {
                    p1=pp.substring(0,2);
                    p2=pp.substring(2);
                    try {
                        np=Integer.parseInt(p1);
                    } catch (Exception e) {
                        return 0;
                    }
                    if ((np<1) | (np>13)) return 0;
                    return 5;
                }
            }

            ps=pp.indexOf("PI");
            if (ps>0) {
                if (pp.length()==3) {
                    p1=pp.substring(0,1);
                    p2=pp.substring(1);
                    try {
                        np=Integer.parseInt(p1);
                    } catch (Exception e) {
                        return 0;
                    }
                    if ((np<1) | (np>13)) return 0;
                    return 6;
                }
                if (pp.length()==4) {
                    p1=pp.substring(0,2);
                    p2=pp.substring(2);
                    try {
                        np=Integer.parseInt(p1);
                    } catch (Exception e) {
                        return 0;
                    }
                    if ((np<1) | (np>13)) return 0;
                    return 6;
                }
            }

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean validaRUC(String vRUC) {
        RUC=vRUC;
        Matcher matcher = rucpattern.matcher(vRUC);
        return matcher.matches();
    }

    public void ValidaRUC_API(String vRUC)  {
        RUC=vRUC;

        try {
            value=-1;

            JSONObject jsdoc;
            jsdoc = new JSONObject();

            jsdoc.put("ruc",vRUC);
            jsdoc.put("tipo_contribuyente",2);

            jsruc=jsdoc.toString();

            AsyncCallWS wstask = new AsyncCallWS();
            wstask.execute();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean wsExecute() {
        HttpURLConnection connection = null;
        JSONObject jObj=null;
        URL url;

        try  {

            responsecode=0;error="";errorflag=false;
            erritems.clear();

            timeout=45000;

            url = new URL(WSURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Usuario-api", usuario);
            connection.setRequestProperty("Llave-api", clave_api);
            connection.setRequestProperty("Llave-firma",clave_firma);
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
                error=e.getMessage();errorflag=true;return errorflag;
            }

            wr.writeBytes (jsruc);
            wr.flush ();
            wr.close ();

            InputStream is;

            try {
                is= connection.getInputStream();
            } catch (SocketTimeoutException s) {
                error="No responde3: " + s.getMessage();
                errorflag=true;return errorflag;
            } catch (Exception e) {
                try {
                    InputStream ise =connection.getErrorStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(ise));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();

                    String jstr=sb.toString();
                    jObj = new JSONObject(jstr);

                    String jsm=jObj.getString("mensaje");
                    error=""+jsm+"\n";
                    erritems.add(jsm);

                    error=error+"";
                    errorflag=true;return errorflag;
                } catch (Exception ee) {
                    error=e.getMessage();
                    errorflag=true;return errorflag;
                }
            }

            try {
                responsecode =connection.getResponseCode();
            } catch (Exception e) {
                error=e.getMessage();
                errorflag=true;return errorflag;
            }

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

                value=0;errorflag=false;
                Boolean rslt=jObj.getBoolean("valido");

                if (rslt) value=1;
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

    private void wsFinished() {
        try  {
            parent.felCallBack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params)  {
            try  {
                wsExecute();
            } catch (Exception e) {}
            return errorflag;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (!errorflag){
                    wsFinished();
                } else{
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
                errorflag=true;error="Se agotó tiempo de validación";
                parent.felCallBack();
            } catch (Exception e) {
                String ss=e.getMessage();
                ss=ss+"";
            }
            super.onCancelled();
        }

    }

}
