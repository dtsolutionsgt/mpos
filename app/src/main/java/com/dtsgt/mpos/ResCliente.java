package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInventCompartido;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResCliente extends PBase {

    private EditText txtNIT,txtNom,txtRef, txtCorreo;
    private TextView lblPed;
    private RelativeLayout relped,relcli;
    private ProgressBar pbar;

    private ArrayList<String> pedidos =new ArrayList<String>();

    private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente, wspnerror;
    private boolean consFinal=false,idleped=true;
    private boolean request_exit=false,bloqueado,nrslt;
    private int cantped;

    private TimerTask ptask;
    private int period=10000,delay=50;

    private static String urlNit = "https://consultareceptores.feel.com.gt/rest/action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_res_cliente);
        } else {
            setContentView(R.layout.activity_res_cliente_ver);
        }

        super.InitBase();

        txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.setText("");txtNIT.requestFocus();
        txtNom = (EditText) findViewById(R.id.editText2);txtNom.setText("");
        txtRef = (EditText) findViewById(R.id.editText1);txtRef.setText("Ciudad");
        txtCorreo= (EditText) findViewById(R.id.txtCorreo);txtCorreo.setText("");
        lblPed = (TextView) findViewById(R.id.textView177);lblPed.setText("");
        relped = (RelativeLayout) findViewById(R.id.relPed);relped.setVisibility(View.INVISIBLE);
        relcli = (RelativeLayout) findViewById(R.id.relclipos);
        pbar = (ProgressBar) findViewById(R.id.progressBar4);pbar.setVisibility(View.INVISIBLE);

        setHandlers();

        gl.pedcorel="";

        bloqueado=false;

        cargaCliente();

    }

    //region  Events

    public void consFinal(View view) {
        try {
            consFinal=true;
            if (agregaCliente("C.F.","Consumidor final","Ciudad","")) procesaCF() ;
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void clienteNIT(View view) {

        try{

            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();

            if (sDireccionCliente.isEmpty()) {
                toast("Falta definir la direccion");return;
            }

            sDireccionCliente=sDireccionCliente+" ";

            if (!existeCliente()){

                if (!validaNIT(sNITCliente)) {
                    msgbox("NIT incorrecto");return;
                }

                if (mu.emptystr(sNombreCliente)) {
                    msgbox("Nombre incorrecto");return;
                }

                if (!sCorreoCliente.isEmpty()) {
                    if (sCorreoCliente.indexOf("@")<3) {
                        msgbox("Correo incorrecto");return;
                    }
                }

                if (agregaCliente(sNITCliente,
                                  sNombreCliente,
                                  sDireccionCliente,
                                   sCorreoCliente))
                    procesaNIT(sNITCliente);

            } else {
                actualizaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente);
                procesaNIT(sNITCliente);
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void buscarCliente(View view) {
        gl.cliente="";
        browse=1;
        startActivity(new Intent(this,Clientes.class));
    }

    private void setHandlers() {

        try{

            txtNIT.setOnKeyListener((v, keyCode, event) -> {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //if (!existeCliente()) txtNom.requestFocus();
                    consultaNITInfile();
                    return true;
                } else {
                    //existeCliente();
                    return false;
                }
            });

            txtNIT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) return;
                    if (!txtNIT.getText().toString().isEmpty()){
                        if (!existeCliente()) 	txtNom.requestFocus();
                    }
                }
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Main

    private void procesaCF() {

        try{

            gl.codigo_cliente = 10*gl.emp;
            gl.rutatipo="V";
            gl.cliente="0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente=gl.cliente;

            gl.gNombreCliente ="Consumidor final";
            gl.gNITCliente ="CF";
            gl.gDirCliente ="Ciudad";
            gl.gCorreoCliente ="Ciudad";
            gl.media=1;

            consFinal=false;
            guardaDatos(1);
            limpiaCampos();

            finish();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void procesaNIT(String snit) {

        int codigo=nitnum(snit);

        try {

            gl.rutatipo="V";
            gl.cliente=""+codigo; if (codigo==-1) gl.cliente=gl.emp+"0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

            gl.gNombreCliente = sNombreCliente;
            gl.gNITCliente =snit;
            gl.gDirCliente = sDireccionCliente;
            gl.gCorreoCliente = sCorreoCliente;

            gl.media=1;

            limpiaCampos();

            guardaDatos(0);

            finish();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void cargaCliente() {
        try {
            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+gl.ordcorel+"') AND (ID="+gl.pricuenta+")");

            if (T_ordencuentaObj.count>0) {
                gl.gNombreCliente = T_ordencuentaObj.first().nombre;
                gl.gNITCliente = T_ordencuentaObj.first().nit;
                gl.gDirCliente = T_ordencuentaObj.first().direccion;
                gl.gCorreoCliente = T_ordencuentaObj.first().correo;
                gl.gNITcf=T_ordencuentaObj.first().cf==1;
            } else {
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="C.F.";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;
            }

            txtNIT.setText(gl.gNITCliente);txtNIT.requestFocus();txtNIT.selectAll();
            txtNom.setText(gl.gNombreCliente);
            txtRef.setText(gl.gDirCliente);
            txtCorreo.setText(gl.gCorreoCliente);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void guardaDatos(int iscf) {
        try {
            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+gl.ordcorel+"') AND (ID="+gl.pricuenta+")");

            T_ordencuentaObj.first().nombre=gl.gNombreCliente;
            T_ordencuentaObj.first().nit=gl.gNITCliente;
            T_ordencuentaObj.first().direccion=gl.gDirCliente;
            T_ordencuentaObj.first().correo=gl.gCorreoCliente;
            T_ordencuentaObj.first().cf=iscf;

            T_ordencuentaObj.update(T_ordencuentaObj.first());
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean NitValidadoInfile =false;

    private void consultaNITInfile() {

        nrslt=false;
        NitValidadoInfile= false;

        if (!gl.codigo_pais.trim().equalsIgnoreCase("GT")) return ;

        if  (!mu.emptystr(gl.felUsuarioCertificacion) && ! mu.emptystr(gl.felLlaveCertificacion) && !mu.emptystr(txtNIT.getText().toString())) {

            JSONObject params = new JSONObject();

            try {

                String nit = txtNIT.getText().toString().replace("-", "").toUpperCase();
                params.put("emisor_codigo", gl.felUsuarioCertificacion);
                params.put("emisor_clave", gl.felLlaveCertificacion);
                params.put("nit_consulta", nit);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(ResCliente.this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlNit, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (!response.getString("nombre").equals("")) {
                            txtNom.setText(response.getString("nombre").replace(",", " ").trim());
                            nrslt=true;
                            NitValidadoInfile=true;
                        } else {
                            toast("No se obtuvieron datos del cliente en Infile con el NIT proporcionado");
                            txtNom.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    msgbox("Error consulta NIT Infile");
                }
            });

            queue.add(request);
        }

    }

    //endregion

    //region Aux

    private boolean validaNIT(String N)  {

        if (N.isEmpty()) return false;
        if (NitValidadoInfile) return true;
        if (!N.contains("-")) return false;

        try{

            String P, C, s, NC;
            int[] v = {0,0,0,0,0,0,0,0,0,0};
            int j, mp, sum, d11, m11, r11, cn, ll;

            N=N.trim();
            N=N.replaceAll(" ","");
            if (N.isEmpty()) return false;

            N=N.toUpperCase();
            if (N.equalsIgnoreCase("CF")) N="C.F.";
            if (N.equalsIgnoreCase("C/F")) N="C.F.";
            if (N.equalsIgnoreCase("C.F")) N="C.F.";
            if (N.equalsIgnoreCase("CF.")) N="C.F.";

            if (N.equalsIgnoreCase("C.F.")) return true;

            ll = N.length();
            if (ll<5) return false;

            P = N.substring(0,ll-2);
            C = N.substring(ll-1, ll);

            ll = ll - 1;
            sum = 0;

            try {

                for (int i = 0; i <ll-1; i++) {
                    s =P.substring( i, i+1);
                    j=Integer.parseInt(s);
                    mp = ll + 1 - i-1;
                    sum = sum + j * mp;
                }

                d11 =(int) Math.floor(sum/11);
                m11 = d11 * 11;
                r11 = sum - m11;
                cn = 11 - r11;

                if (cn == 10) s = "K"; else s=""+cn;

                if (cn>10) {
                    cn = cn % 11;
                    s =""+cn;
                }

                NC = P+"-"+s;

                if (N.equalsIgnoreCase(NC)) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
        return true;

    }

    private boolean existeCliente() {

        Cursor DT;
        boolean resultado=false;

        try{

            String NIT=txtNIT.getText().toString();

            if (mu.emptystr(NIT)) {
                txtNIT.requestFocus();
                resultado=false;
            } else {

                sql="SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO,TIPO_CONTRIBUYENTE,CODIGO_CLIENTE, EMAIL FROM P_CLIENTE " +
                        "WHERE NIT = '" + NIT + "'";
                DT=Con.OpenDT(sql);

                if (DT != null){

                    if (DT.getCount()>0){

                        DT.moveToFirst();

                        txtNom.setText(DT.getString(1));
                        txtRef.setText(DT.getString(2));
                        txtCorreo.setText(DT.getString(8));

                        gl.rutatipo="V";
                        gl.cliente=DT.getString(0);
                        gl.nivel=gl.nivel_sucursal;
                        gl.percepcion=0;
                        gl.contrib=DT.getString(6);;
                        gl.scancliente = gl.cliente;
                        gl.gNombreCliente =txtNom.getText().toString();
                        gl.gNITCliente =NIT;
                        gl.gDirCliente =DT.getString(4);

                        gl.media=DT.getInt(5);
                        gl.codigo_cliente=DT.getInt(7);

                        resultado=true;

                    } else {

                        //txtNom.setText("");
                        //txtRef.setText("");
                        //txtCorreo.setText("");

                    }
                }
                if (DT!=null) DT.close();
            }

        } catch (Exception e){
            mu.toast("Ocurrió un error buscando al cliente");
            resultado=false;
        }
        return resultado;
    }

    private boolean agregaCliente(String NIT,String Nom,String dir, String Correo) {

        if (consFinal) {
            gl.codigo_cliente = 10*gl.emp;
            agregaClienteCF(NIT,Nom,dir,Correo);return true;
        }

        int codigo=nitnum(NIT);
        gl.codigo_cliente=codigo;

        dir=dir+" ";

        try {

            if (codigo==0){
                toast("NIT no es válido");return false;
            }

            ins.init("P_CLIENTE");
            ins.add("CODIGO_CLIENTE",codigo);
            ins.add("CODIGO",""+codigo);
            ins.add("EMPRESA",gl.emp);
            ins.add("NOMBRE",Nom);
            ins.add("BLOQUEADO",0);
            ins.add("NIVELPRECIO",1);
            ins.add("MEDIAPAGO","1");
            ins.add("LIMITECREDITO",0);
            ins.add("DIACREDITO",0);
            ins.add("DESCUENTO",1);
            ins.add("BONIFICACION",1);
            ins.add("ULTVISITA",du.getActDate());
            ins.add("IMPSPEC",0);
            ins.add("NIT",NIT.toUpperCase());
            ins.add("EMAIL",Correo);
            ins.add("ESERVICE","N"); // estado envio
            ins.add("TELEFONO"," ");
            ins.add("DIRECCION",dir);
            ins.add("COORX",0);
            ins.add("COORY",0);
            ins.add("BODEGA",""+gl.sucur);
            ins.add("COD_PAIS","");
            ins.add("CODBARRA","");
            ins.add("PERCEPCION",0);
            ins.add("TIPO_CONTRIBUYENTE","");
            ins.add("EMPRESA",gl.emp);
            ins.add("IMAGEN","");
            db.execSQL(ins.sql());

            return true;

        } catch (Exception e) {

            try {

                upd.init("P_CLIENTE");
                upd.add("NOMBRE",Nom);
                upd.add("NIT",NIT);
                upd.add("DIRECCION",dir);
                upd.add("EMAIL",Correo);
                upd.add("ESERVICE","N");
                upd.add("CODIGO","0");
                upd.Where("CODIGO_CLIENTE="+codigo);
                db.execSQL(upd.sql());

                return true;

            } catch (SQLException e1) {
                mu.msgbox2(e1.getMessage());return false;
            }

        }

    }

    private void agregaClienteCF(String NIT,String Nom,String dir, String Correo) {

        int codigo=10*gl.emp;

        try {

            ins.init("P_CLIENTE");
            ins.add("CODIGO_CLIENTE",codigo);
            ins.add("CODIGO","0");
            ins.add("EMPRESA",gl.emp);
            ins.add("NOMBRE",Nom);
            ins.add("BLOQUEADO",0);
            ins.add("NIVELPRECIO",1);
            ins.add("MEDIAPAGO","1");
            ins.add("LIMITECREDITO",0);
            ins.add("DIACREDITO",0);
            ins.add("DESCUENTO",1);
            ins.add("BONIFICACION",1);
            ins.add("ULTVISITA",du.getActDate());
            ins.add("IMPSPEC",0);
            ins.add("NIT",NIT.toUpperCase());
            ins.add("EMAIL",Correo);
            ins.add("ESERVICE","N"); // estado envio
            ins.add("TELEFONO"," ");
            ins.add("DIRECCION",dir);
            ins.add("COORX",0);
            ins.add("COORY",0);
            ins.add("BODEGA",""+gl.sucur);
            ins.add("COD_PAIS","");
            ins.add("CODBARRA","");
            ins.add("PERCEPCION",0);
            ins.add("TIPO_CONTRIBUYENTE","");
            ins.add("EMPRESA",gl.emp);
            ins.add("IMAGEN","");
            db.execSQL(ins.sql());

        } catch (Exception e) {}

    }

    private boolean actualizaCliente(String NIT,String Nom,String dir, String Correo) {

        int codigo=nitnum(NIT);

        gl.codigo_cliente=codigo;

        try {

            upd.init("P_CLIENTE");
            upd.add("NOMBRE",Nom);
            upd.add("DIRECCION",dir);
            upd.add("EMAIL",Correo);
            upd.Where("CODIGO='"+gl.cliente+"'");
            db.execSQL(upd.sql());

            return true;

        } catch (Exception e) {
            msgbox2(e.getMessage());return false;
        }

    }

    private int nitnum(String nit) {

        int pp;

        try {

            //#EJC202210222150
            int nnit =0;

            nit=nit.toUpperCase();
            pp=nit.indexOf("-");

            if (pp<0){
                nnit=Integer.parseInt(nit);
            }else{
                int A=(int) nit.charAt(pp+1);
                String snit=nit.substring(0,pp)+A;
                nnit=Integer.parseInt(snit);
            }

            return nnit;

        } catch (Exception e) {
            return gl.emp*10;
        }
    }

    private void limpiaCampos() {
        try {
            txtNIT.setText("");
            txtNom.setText("");
            txtNIT.requestFocus();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void cliente() {
        Cursor DT;

        try {
            sql = "SELECT NOMBRE,NIT,DIRECCION,EMAIL FROM P_CLIENTE WHERE CODIGO='"+gl.cliente+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            gl.gNombreCliente =DT.getString(0);
            gl.gNITCliente =DT.getString(1);
            gl.gDirCliente =DT.getString(2);
            gl.gCorreoCliente =  DT.getString(3);

            txtNIT.setText(gl.gNITCliente);txtNIT.requestFocus();
            txtNom.setText(gl.gNombreCliente);
            txtRef.setText(gl.gDirCliente);
            txtCorreo.setText(gl.gCorreoCliente);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try{
            super.onResume();

            if (browse==1) {
                browse=0;
                if (!gl.cliente.isEmpty()) cliente();
                return;
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

}
