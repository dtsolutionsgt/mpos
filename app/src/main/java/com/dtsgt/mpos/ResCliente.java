package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.firebase.fbOrdenCuenta;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.TimerTask;

public class ResCliente extends PBase {

    private EditText txtNIT,txtNom,txtRef, txtCorreo;
    private TextView lblPed,btnCF,btnNIT;
    private RelativeLayout relped,relcli;
    private ProgressBar pbar;

    private ArrayList<String> pedidos =new ArrayList<String>();
    private clsClasses.clsT_ordencuenta fbitem;

    private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente, wspnerror;
    private boolean consFinal=false,idleped=true;
    private boolean request_exit=false,bloqueado,nrslt;
    private int cantped;

    private fbOrdenCuenta fboc;
    private Runnable rnFbCuenta;


    private TimerTask ptask;
    private int period=10000,delay=50;

    private static String urlNit = "https://consultareceptores.feel.com.gt/rest/action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

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
            btnCF = findViewById(R.id.textView4);
            btnNIT = findViewById(R.id.textView6);

            setHandlers();

            gl.pedcorel="";

            bloqueado=false;

            fboc=new fbOrdenCuenta("OrdenCuenta",gl.tienda);

            rnFbCuenta = () -> { showCliente();};

            cargaCliente();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region  Events

    public void consFinal(View view) {
        String ss=txtNIT.getText().toString();
        String ddnom,ddir,dcor;


        try {
            ddnom =txtNom.getText().toString();if (ddnom.isEmpty()) ddnom="Consumidor final";
            ddir =txtRef.getText().toString();if (ddir.isEmpty()) ddir="Ciudad";
            dcor="consumidorfinal@gmail.com";

            consFinal=true;
            gl.sal_PER=false;
            gl.sal_NRC=false;
            gl.sal_NIT=false;

            if (agregaCliente("CF",ddnom,ddir,"")) procesaCF() ;
            //if (agregaCliente("C.F.","Consumidor final","Ciudad","")) procesaCF() ;
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void clienteNIT(View view) {

        try {

            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();

            if (!correctoNIT()) {
                toast("Identificación incorrecta");return;
            }

            if (mu.emptystr(sNombreCliente)) {
                msgbox("Nombre incorrecto");return;
            }

            if (sDireccionCliente.isEmpty()) {
                toast("Falta definir la direccion");return;
            }

            sDireccionCliente=sDireccionCliente+" ";

            if (!existeCliente()){

                if (!validaNIT(sNITCliente)) {
                    //msgbox("NIT incorrecto");return;
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
        try {
            txtNIT.setOnKeyListener((v, keyCode, event) -> {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    //if (!existeCliente()) txtNom.requestFocus();
                    if (gl.codigo_pais.equalsIgnoreCase("GT")) {
                        consultaNITInfile();
                    } else existeCliente();
                    return true;
                } else {
                    return false;
                }
            });

            /*
            txtNIT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) return;
                    if (!txtNIT.getText().toString().isEmpty()){
                        if (!existeCliente()) 	txtNom.requestFocus();
                    }
                }
            });
            */

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

            gl.gNITCliente ="CF";
            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();

            if (sNombreCliente.isEmpty()) gl.gNombreCliente ="Consumidor final";else gl.gNombreCliente=sNombreCliente;
            if (sDireccionCliente.isEmpty()) gl.gDirCliente ="Ciudad";else gl.gDirCliente=sDireccionCliente;
            gl.gTelCliente ="";

            /*
            gl.gNombreCliente ="Consumidor final";
            gl.gDirCliente ="Ciudad";
            gl.gCorreoCliente ="Ciudad";
            */

            gl.gNombreCliente ="Consumidor final";
            gl.gDirCliente ="Ciudad";
            gl.gTelCliente ="";
            gl.gNITCliente ="CF";
            gl.nit_tipo="N";

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=sNombreCliente;
            gl.dom_dir =sDireccionCliente;gl.dom_ref="";
            gl.dom_tel="";

            gl.media=1;
            consFinal=false;

            guardaDatos(1);

            limpiaCampos();

            finish();

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private boolean correctoNIT() {
        sNITCliente =txtNIT.getText().toString();

        if (sNITCliente.isEmpty()) return false;
        if (sNITCliente.length()<3) return false;

        if (gl.codigo_pais.equalsIgnoreCase("GT")) {
            if (sNITCliente.length()>13) return false;
            if (sNITCliente.length()!=13) {
                if (!validaNIT(sNITCliente)) return false;
            }
        }  else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
            if (!validaNITHon(sNITCliente)) return false;
        } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
            if (!validaNITSal(sNITCliente)) return false;
        }

        return true;
    }

    private void procesaNIT(String snit) {
        boolean flag_NRC;

        try {
            purgeNIT();

            int codigo=nitnum(snit);

            gl.rutatipo="V";
            gl.cliente=""+codigo; if (codigo==-1) gl.cliente=gl.emp+"0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();

            if (sNITCliente.isEmpty()) {
                msgbox("Identificación incorrecta");return;
            }

            if (sNITCliente.length()<3) {
                msgbox("Identificación incorrecta");return;
            }

            if (sDireccionCliente.isEmpty()) {
                toast("Falta definir la direccion");return;
            }

            sDireccionCliente=sDireccionCliente+" ";

            gl.nit_tipo="N";

            if (gl.codigo_pais.equalsIgnoreCase("GT")) {
                if (sNITCliente.length()>13) {
                    msgbox("Identificación incorrecta");return;
                }
                if (sNITCliente.length()!=13) {
                    if (!validaNIT(sNITCliente)) {
                        msgbox("NIT incorrecto");return;
                    }
                }
            }  else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                if (!validaNITHon(sNITCliente)) {
                    msgbox("RTN incorrecto");return;
                }
            } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                if (!validaNITSal(sNITCliente)) {
                    msgbox("NIT/NRC incorrecto");return;
                }
            }

            if (mu.emptystr(sNombreCliente)) {
                msgbox("Nombre incorrecto");return;
            }

            if (!sCorreoCliente.isEmpty()) {
                if (sCorreoCliente.indexOf("@")<3) {
                    msgbox2("Correo incorrecto, falta '@' ");return;
                }
                if (sCorreoCliente.indexOf(".")<0) {
                    msgbox2("Correo incorrecto falta '.' ");return;
                }
            }

            gl.gNombreCliente = sNombreCliente;
            gl.gNITCliente =sNITCliente;
            gl.gDirCliente = sDireccionCliente;
            gl.gCorreoCliente = sCorreoCliente;
            gl.gTelCliente = "";

            gl.media=1;

            flag_NRC=false;gl.sal_PER=false;
            if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                if (gl.sal_NRC) flag_NRC = true;
            }

            if (flag_NRC) {
                //msgAskCG("Grande contribuyente ");
            } else {
                /*
                if (!existeCliente()){
                    if (agregaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente)) {
                    }
                } else {
                    actualizaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente);
                }
                */
            }

            limpiaCampos();

            guardaDatos(0);

            finish();

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void purgeNIT() {
        try {
            String ss=txtNIT.getText().toString();

            ss=ss.trim();

            ss=ss.replace("!","");
            ss=ss.replace("#","");
            ss=ss.replace("$","");
            ss=ss.replace("%","");
            ss=ss.replace("/","");
            ss=ss.replace("(","");
            ss=ss.replace(")","");
            ss=ss.replace("=","");
            ss=ss.replace("?","");
            ss=ss.replace("'","");
            ss=ss.replace("+","");
            ss=ss.replace("*","");
            ss=ss.replace(":","");
            ss=ss.replace(";","");
            ss=ss.replace(".","");
            ss=ss.replace("@","");
            ss=ss.replace("&","");
            ss=ss.replace("_","");

            txtNIT.setText(ss);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaCliente() {
        try {
            fboc.getItem(gl.ordcorel,gl.pricuenta,rnFbCuenta);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showCliente() {
        try {
            if (fboc.errflag) throw new Exception(fboc.error);

            if (fboc.itemexists) {
                fbitem=fboc.item;

                gl.gNombreCliente = fboc.item.nombre;
                gl.gNITCliente = fboc.item.nit;
                gl.gDirCliente = fboc.item.direccion;
                gl.gCorreoCliente = fboc.item.correo;
                gl.gNITcf=fboc.item.cf==1;
            } else {
                fbitem=clsCls.new clsT_ordencuenta();
                fbitem.corel=gl.ordcorel;
                try {
                    fbitem.id=Integer.parseInt(gl.pricuenta);
                } catch (Exception e) {
                    fbitem.id=0;msgbox("showCliente . Numero cuenta incorrecto");return;
                }
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="CF";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;
            }

            txtNIT.setText(gl.gNITCliente);txtNIT.requestFocus();txtNIT.selectAll();
            txtNom.setText(gl.gNombreCliente);
            txtRef.setText(gl.gDirCliente);
            txtCorreo.setText(gl.gCorreoCliente);

            btnCF.setText("Consumidor Final");
            if (gl.codigo_pais.equalsIgnoreCase("GT")) {
                btnNIT.setText("Cliente con NIT");
            } else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                btnNIT.setText("Cliente con RTN");
            } else if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                btnNIT.setText("Cliente con NIT/NRC");
                btnCF.setText("Ticket");
            }

            if (!fboc.itemexists)  throw new Exception("Error a leer registro");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    private void guardaDatos(int iscf) {

        clsClasses.clsD_facturaf item;
        clsD_facturafObj D_facturafObj = new clsD_facturafObj(this, Con, db);

        try {

            fbitem.nombre=gl.gNombreCliente;
            fbitem.nit=gl.gNITCliente;
            fbitem.direccion=gl.gDirCliente;
            fbitem.correo=gl.gCorreoCliente;
            fbitem.cf=iscf;

            fboc.setItem(fbitem);

            //#EJC202212092141:Validar si ya existe o no el registro en D_FACTURAF si no existe lo inserta.
            String sql = "SELECT * FROM D_FACTURAF WHERE COREL='"+gl.ordcorel+"'";
            Cursor dt = Con.OpenDT(sql);

            if (dt.getCount()==0) {

                item = clsCls.new clsD_facturaf();
                item.corel = gl.ordcorel;
                item.nombre=gl.gNombreCliente;
                item.nit=gl.gNITCliente;
                item.direccion=gl.gDirCliente;
                item.correo=gl.gCorreoCliente;

                D_facturafObj.add(item);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean NitValidadoInfile =false;

    private void consultaNITInfile() {
        String nc;
        nrslt=false;
        NitValidadoInfile= false;

        if (!gl.codigo_pais.trim().equalsIgnoreCase("GT")) return ;

        if  (!mu.emptystr(gl.felUsuarioCertificacion) && ! mu.emptystr(gl.felLlaveCertificacion) && !mu.emptystr(txtNIT.getText().toString())) {

            nc=txtNIT.getText().toString();
            if (nc.length()==13) return;

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

    private boolean validaNITHon(String N)  {
        if (N.isEmpty()) return false;
        if (N.length()<13) return false;

        try {
            long l=Long.parseLong(N);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validaNITSal(String N) {
        int guc,val,valm,vald;
        String NN;

        gl.sal_NIT=false;gl.sal_NRC=false;NN=N;

        try {
            if (!N.contains("-")) return false;
            guc = N.length() - NN.replaceAll("-","").length();
            if (guc==3) {
                String[] sp = N.split("-");

                if (sp[0].length()!=4) return false;
                try {
                    val=Integer.parseInt(sp[0]);
                } catch (Exception e) { return false; }

                if (sp[1].length()!=6) return false;
                if (!du.fechaNIT_SV(sp[1])) return false;

                if (sp[2].length()!=3) return false;
                try {
                    val=Integer.parseInt(sp[2]);
                } catch (Exception e) { return false; }

                if (sp[3].length()!=1) return false;
                try {
                    val=Integer.parseInt(sp[3]);
                } catch (Exception e) { return false; }

                gl.sal_NIT=true;return true;

            } else if (guc==1) {
                String[] sp = N.split("-");

                if (sp[1].length()!=1) return false;
                try {
                    val=Integer.parseInt(sp[1]);
                } catch (Exception e) { return false; }

                if (sp[0].length()>7) return false;
                if (sp[0].length()<2) return false;
                try {
                    val=Integer.parseInt(sp[0]);
                } catch (Exception e) { return false; }

                gl.sal_NRC=true;return true;
            } else return false;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private boolean existeCliente() {
        Cursor DT;

        try {
            String NIT=txtNIT.getText().toString();

            if (mu.emptystr(NIT)) {
                txtNIT.requestFocus();return false;
            } else {
                sql="SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO,TIPO_CONTRIBUYENTE," +
                    "CODIGO_CLIENTE, EMAIL FROM P_CLIENTE WHERE NIT='"+NIT+"'";
                DT=Con.OpenDT(sql);

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

                    return true;
                } else {
                    //txtNom.setText("");txtRef.setText("");txtCorreo.setText("");
                    return false;
                }
            }
        } catch (Exception e){
            mu.toast("Ocurrió un error buscando al cliente");return true;
        }
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

            upd.Where("NIT='"+NIT+"'");

            //upd.Where("CODIGO='"+gl.cliente+"'");
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
