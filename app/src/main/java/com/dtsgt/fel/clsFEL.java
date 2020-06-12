package com.dtsgt.fel;

// InFile FEL Factura

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.mpos.PBase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class clsFEL {

    public String  error;
    public Boolean errorflag;
    public int errlevel;

    public String xml;
    public String fact_uuid;
    public String fact_serie;
    public int fact_numero;

    // Private declarations

    private Context cont;
    private PBase parent;

    private JSONObject jsonf = new JSONObject();
    private JSONObject jsonc = new JSONObject();
    private JSONObject jsona = new JSONObject();

    private String s64, jsfirm,jscert,jsanul,firma;
    private double imp,totmonto,totiva;
    private int linea;

    // Configuracion

    private double iva=12;
    private String WSURL="https://signer-emisores.feel.com.gt/sign_solicitud_firmas/firma_xml";
    private String WSURLCert="https://certificador.feel.com.gt/fel/certificacion/dte/";
    private String WSURLAnul="https://certificador.feel.com.gt/fel/anulacion/dte/";

    //private String fileName= Environment.getExternalStorageDirectory() + "/zzxmltest.xml";
    //private String jsonName= Environment.getExternalStorageDirectory() + "/zzxmltest.txt";


    public clsFEL(Context context, PBase Parent) {
        cont=context;
        parent=Parent;
        errlevel=0;error="";errorflag=false;
    }

    public void certificacion() {}

    //region XML

    public void iniciar(long fecha_emision) {}

    public void completar(String serie) {}

    public String toBase64() {
        String s64;
        byte[] bytes= new byte[0];

        bytes = xml.getBytes(StandardCharsets.UTF_8);
        s64= Base64.encodeToString(bytes, Base64.DEFAULT);
        return s64;
    }

    public void emisor(String p1,String p2,String p3,String p4,String p5) {}

    public void emisorDireccion(String p1,String p2,String p3,String p4) {}

    public void receptor(String p1,String p2,String p3) {}

    public void detalle (String descrip,double cant,String unid,double precuni,double total,double desc) {}

    public void guardar(String filename) throws IOException {}

    public void anulfact(String uuid,String nit,String Idreceptor,long fechaemis,long fechaanul) {}

    //endregion

    //region Aux

    public String parseDate(long dd) {
        String s1,s2,s3,s4,s5,sf,ss=""+dd;

        s1="20"+ss.substring(0,2);
        s2=ss.substring(2,4);
        s3=ss.substring(4,6);
        s4=ss.substring(6,8);
        s5=ss.substring(8,10);

        sf=s1+"-"+s2+"-"+s3+"T"+s4+":"+s5+":00-06:00";
        return sf;
    }

    public void toast(String msg) {
        Toast toast= Toast.makeText(cont,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    //endregion
}
