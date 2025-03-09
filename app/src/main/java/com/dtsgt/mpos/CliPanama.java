package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsT_cli_corelObj;
import com.dtsgt.felpana.clsFELClasesPA;
import com.dtsgt.felpana.clsFEPDocVal;
import com.dtsgt.firebase.fbCliCorel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CliPanama extends PBase {

    private EditText txtNIT,txtNom,txtRef,txtCorreo,txtTel,txtDV;
    private TextView lblPed,lblDom,lblNIT,lblDir,btnNIT,btnCF,lblMuni;
    private ImageView imgmuni;
    private RelativeLayout relcli;
    private ProgressBar pbar;
    private CheckBox cbllevar,cbdomicilio, cbRUC;

    private fbCliCorel fbcc;

    private clsFEPDocVal fdval;
    private clsFELClasesPA.FELAmbiente FELAmb;

    private clsP_clienteObj P_clienteObj;
    private clsT_cli_corelObj T_cli_corelObj;

    private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente,
            sTelCliente,sDV,Usuario_FEL,LLave_API,LLave_FIRMA,URL_FEL;
    private int clicorel;

    private boolean consFinal=false,request_exit=false,idle=true,
            bloqueado,domicilio,nrslt,NitValidadoInfile =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_panama);

            super.InitBase();

            txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.setText("");txtNIT.requestFocus();
            txtNom = (EditText) findViewById(R.id.editText2);txtNom.setText("");
            txtRef = (EditText) findViewById(R.id.editText1);
            txtCorreo= (EditText) findViewById(R.id.txtCorreo);txtCorreo.setText("");
            txtTel= (EditText) findViewById(R.id.editTextNumber4);txtTel.setText("");
            txtDV= (EditText) findViewById(R.id.editTextNumber13);txtDV.setText("");
            lblMuni = (TextView) findViewById(R.id.textView317);lblMuni.setText("");
            lblDom = (TextView) findViewById(R.id.textView237);
            lblDir= (TextView) findViewById(R.id.textView238);
            lblNIT = (TextView) findViewById(R.id.textView1);lblNIT.setText("NIT");
            imgmuni= findViewById(R.id.imageView148);
            relcli = (RelativeLayout) findViewById(R.id.relclipos);
            pbar = (ProgressBar) findViewById(R.id.progressBar4);pbar.setVisibility(View.INVISIBLE);
            cbllevar = findViewById(R.id.checkBox21);
            cbdomicilio = findViewById(R.id.chkPickup);
            cbRUC = findViewById(R.id.checkBox30);
            btnNIT= findViewById(R.id.textView6);
            btnCF= findViewById(R.id.textView4);

            setHandlers();

            gl.InvCompSend=false;
            gl.cli_muni = "";
            gl.cli_depto = "";

            fbcc=new fbCliCorel("CliCorel");
            fbcc.getItem(gl.codigo_ruta,() -> { corelCliente(); });

            getURL();

            gl.pedcorel="";gl.parallevar=false;gl.cf_domicilio=false;
            gl.mododocesa=-1;
            bloqueado=false;
            domicilio=gl.peVentaDomicilio;

            P_clienteObj=new clsP_clienteObj(this,Con,db);
            T_cli_corelObj=new clsT_cli_corelObj(this,Con,db);

            clsFELClasesPA FELPA=new clsFELClasesPA();
            FELAmb= FELPA.new FELAmbiente(this,Con,db,gl.tienda);

            cbllevar.setEnabled(true); cbllevar.setChecked(false);
            cbdomicilio.setEnabled(true); cbdomicilio.setChecked(false);

            lblDir.setVisibility(View.GONE);

            NitValidadoInfile =false;

            //if (gl.cliente_dom!=0) cargaCliente();
            txtRef.setText("Ciudad");
            txtNom.setText("Nombre");
            txtCorreo.setText("jpospichal@dts.com.gt");

            txtNIT.setText("894-57-103790");txtDV.setText("67");cbRUC.setChecked(true);
            //txtNIT.setText("N-1234-1234");

            parametrosPanama();
            fdval= new clsFEPDocVal(this,Usuario_FEL,LLave_API,LLave_FIRMA,URL_FEL) ;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    public void felCallBack()  {
        try {
            pbar.setVisibility(View.INVISIBLE);idle=true;
            if (fdval.errorflag) throw new Exception(fdval.error);

            if (fdval.value==1) {
                txtDV.setText(fdval.DV);
                txtNom.setText(fdval.Nombre);
                procesaCliente();
            } else {
                msgbox("RUC "+fdval.RUC+" incorrecto");
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void clienteNIT(View view) {
        try {
            purgeNIT();
            sNITCliente =txtNIT.getText().toString();
            if (sNITCliente.isEmpty()) {
                txtNIT.requestFocus();toast("Falta ingresar cedula o RUC.");return;
            }

            sNITCliente =txtNIT.getText().toString()+"";
            sNombreCliente =txtNom.getText().toString()+"";
            sDireccionCliente =txtRef.getText().toString()+"";
            sCorreoCliente = txtCorreo.getText().toString()+"";
            sTelCliente=txtTel.getText().toString()+"";
            sDV=txtDV.getText().toString()+"";

            if (sNombreCliente.isEmpty()) {
                msgbox("Falta definir nombre");return;
            }
            if (sNombreCliente.length()<5) {
                msgbox("Nombre debe tener minimo 5 letras");return;
            }

            if (sCorreoCliente.isEmpty()) {
                msgbox("Falta definir nombre");return;
            }

            if (cbRUC.isChecked()) {

                if (sDV.isEmpty()) {
                    toast("Falta definir DV");return;
                }

                if (sDireccionCliente.isEmpty()) {
                    toast("Falta definir direccion");return;
                }

                if (gl.cli_muni.isEmpty()) {
                    toast("Falta definir ubicación");return;
                }

                if (fdval.validaRUC(sNITCliente)) {
                    if (app.isOnWifi()>0) {
                        pbar.setVisibility(View.VISIBLE);idle=false;
                        fdval.ValidaRUC_API(sNITCliente);
                    } else {
                        procesaCliente();
                    }
                } else {
                    msgbox("RUC "+fdval.RUC+" incorrecto.");
                }
            } else {
                if (fdval.validaCedula(sNITCliente)>0) {
                    procesaCliente();
                } else {
                    msgbox("Cedula incorrecta.");
                }
            }

        } catch (Exception e) {
            pbar.setVisibility(View.INVISIBLE);idle=true;
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void consFinal(View view) {
        String ss=txtNIT.getText().toString();
        String ddnom,ddir,dcor;

        try {
            ddnom =txtNom.getText().toString();if (ddnom.isEmpty()) ddnom="Consumidor final";
            ddir =txtRef.getText().toString();if (ddir.isEmpty()) ddir="Ciudad";
            dcor="consumidorfinal@gmail.com";

            gl.mododocesa=0;

            consFinal=true;
            gl.sal_PER=false;
            gl.sal_NRC=false;
            gl.sal_NIT=false;

            if (agregaCliente("C.F.",ddnom,ddir,dcor,""+txtTel.getText().toString())) procesaCF() ;

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doDomicilio(View view) {
        cbllevar.setChecked(false);
    }

    public void doLlevar(View view) {
        cbdomicilio.setChecked(false);
    }

    public void doUbic(View view) {
        browse=1;
        startActivity(new Intent(this,PanamaUbic.class));
    }

    public void buscarCliente(View view) {
        gl.cliente="";
        browse=2;
        startActivity(new Intent(this,Clientes.class));
    }

    private void setHandlers() {

        txtNIT.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    existeCliente();
                    return true;
                } else {
                    return false;
                }
            }
        });

        cbdomicilio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) cbllevar.setChecked(false);
                    }
                });

        cbllevar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                        if (isChecked){
                            gl.parallevar = true;cbdomicilio.setChecked(false);
                        } else{
                            gl.parallevar = false;
                        }
                    }
                } );

    }

    //endregion

    //region Main

    private void procesaCliente() {
        try {

            purgeNIT();

            sNITCliente =txtNIT.getText().toString()+"";
            sNombreCliente =txtNom.getText().toString()+"";
            sDireccionCliente =txtRef.getText().toString()+"";
            sCorreoCliente = txtCorreo.getText().toString()+"";
            sTelCliente=txtTel.getText().toString()+"";
            sDV=txtDV.getText().toString()+"";

            if (sNombreCliente.isEmpty()) {
                msgbox("Falta definir nombre");return;
            }
            if (sNombreCliente.length()<5) {
                msgbox("Nombre debe tener minimo 5 letras");return;
            }

            if (sCorreoCliente.isEmpty()) {
                msgbox("Falta definir nombre");return;
            }

            if (cbRUC.isChecked()) {
                if (sDV.isEmpty()) {
                    toast("Falta definir DV");return;
                }

                if (sDireccionCliente.isEmpty()) {
                    toast("Falta definir direccion");return;
                }

                if (gl.cli_muni.isEmpty()) {
                    toast("Falta definir ubicación");return;
                }
            }

            sDireccionCliente=sDireccionCliente+" ";

            gl.nit_tipo="N";

            if (mu.emptystr(sNombreCliente)) {
                msgbox("Nombre incorrecto");return;
            }

            if (!sCorreoCliente.isEmpty()) {
                if (sCorreoCliente.indexOf("@")<3) {
                    msgbox2("Correo incorrecto, falta '@' ");return;
                }
                if (sCorreoCliente.indexOf(".")<0) {
                    msgbox2("Correo incorrecto, falta '.' ");return;
                }
            } else {
                msgbox("Falta correo");return;
            }

            if (sTelCliente.isEmpty()) sTelCliente="";

            if (!existeCliente()){
                if (agregaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente)) procesaNIT(sNITCliente);
            } else {
                actualizaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente);
                procesaNIT(sNITCliente);
            }


        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean agregaCliente(String NIT,String Nom,String dir, String Correo,String tel) {
        int codigo;

        if (consFinal) {
            codigo = 10*gl.emp;
            gl.cliente_dom=gl.codigo_cliente;
            agregaClienteCF(NIT,Nom,dir,Correo);
            return true;
        } else {
            codigo=clicorel;
        }

        if (codigo==0){
            toast("No se pudo generar codigo de cliente");return false;
        }
        guardaCorelCliente();

        int lcodigo=20000+gl.codigo_ruta;
        lcodigo=lcodigo*100000;
        codigo=lcodigo+codigo;

        gl.codigo_cliente=codigo;
        dir=dir+" ";

        try {

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
            ins.add("TELEFONO",tel);
            ins.add("DIRECCION",dir);
            ins.add("COORX",0);
            ins.add("COORY",0);
            ins.add("BODEGA",""+gl.sucur);
            ins.add("COD_PAIS","");
            ins.add("CODBARRA",sDV);
            ins.add("PERCEPCION",0);
            ins.add("TIPO_CONTRIBUYENTE","");
            ins.add("EMPRESA",gl.emp);
            ins.add("IMAGEN","");
            db.execSQL(ins.sql());

            gl.cliente_dom=codigo;

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
                upd.add("CODBARRA",sDV);
                upd.Where("CODIGO_CLIENTE="+codigo);
                db.execSQL(upd.sql());

                return true;

            } catch (SQLException e1) {
                mu.msgbox2(e1.getMessage());return false;
            }

        }

    }

    private void procesaNIT(String snit) {
        int codigo=clicorel;

        try {

            gl.cliente_dom=codigo;

            gl.rutatipo="V";
            gl.cliente=""+codigo; if (codigo<=0) gl.cliente=gl.emp+"0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

            gl.gNombreCliente = sNombreCliente;
            gl.gNITCliente =snit;
            gl.gDirCliente = sDireccionCliente;
            gl.gCorreoCliente = sCorreoCliente;
            gl.gTelCliente=sTelCliente;
            gl.gDVCliente=sDV;

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=sNombreCliente;
            gl.dom_dir =sDireccionCliente;gl.dom_ref="";
            gl.dom_tel=sTelCliente;

            gl.media=1;
            limpiaCampos();

            gl.cf_domicilio=false;
            terminaCliente();

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private boolean actualizaCliente(String NIT,String Nom,String dir, String Correo,String tel) {

        int codigo=clicorel;

        gl.codigo_cliente=codigo;

        if (consFinal) return true;
        if (gl.codigo_cliente==10*gl.emp) return true;

        try {

            upd.init("P_CLIENTE");
            upd.add("NOMBRE",Nom);
            upd.add("DIRECCION",dir);
            upd.add("EMAIL",Correo);
            upd.add("TELEFONO",tel);
            upd.add("CODBARRA",sDV);
            upd.add("ESERVICE","N"); //
            upd.Where("CODIGO_CLIENTE="+codigo);

            db.execSQL(upd.sql());

            return true;

        } catch (Exception e) {
            msgbox2(e.getMessage());return false;
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

    private void procesaCF() {

        try{

            gl.codigo_cliente = 10*gl.emp;gl.cliente_dom=gl.codigo_cliente;
            gl.rutatipo="V";
            gl.cliente="0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente=gl.cliente;

            gl.gNITCliente ="CF";
            sNombreCliente =txtNom.getText().toString()+"";
            sDireccionCliente =txtRef.getText().toString().trim()+"";
            sCorreoCliente = txtCorreo.getText().toString()+"";
            sTelCliente=txtTel.getText().toString()+"";
            sDV="";

            if (sNombreCliente.isEmpty()) gl.gNombreCliente ="Consumidor final";else gl.gNombreCliente=sNombreCliente;
            if (sDireccionCliente.isEmpty()) gl.gDirCliente ="Ciudad";else gl.gDirCliente=sDireccionCliente;
            if (sTelCliente.isEmpty()) gl.gTelCliente =""; else gl.gTelCliente=sTelCliente;

            gl.gNITCliente ="CF";
            gl.nit_tipo="N";


            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=sNombreCliente;
            gl.dom_dir =sDireccionCliente;gl.dom_ref="";
            gl.dom_tel=sTelCliente;

            gl.media=1;

            consFinal=false;
            limpiaCampos();

            gl.cf_domicilio=true;
            terminaCliente();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private boolean existeCliente() {
        Cursor DT;
        boolean resultado=false;
        int nitcf=gl.emp*10;

        try {

            String NIT=txtNIT.getText().toString();

            if (mu.emptystr(NIT)) {
                txtNIT.requestFocus();
                resultado=false;
            } else {
                sql = "SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO, TIPO_CONTRIBUYENTE," +
                      "CODIGO_CLIENTE, EMAIL,TELEFONO,CODBARRA FROM P_CLIENTE " +
                      "WHERE (NIT='" + NIT + "') AND (CODIGO_CLIENTE<>" + nitcf + ")";
                DT = Con.OpenDT(sql);

                if (DT != null) {

                    if (DT.getCount() > 0) {

                        DT.moveToFirst();

                        txtNom.setText(DT.getString(1));
                        txtRef.setText(DT.getString(2));
                        txtCorreo.setText(DT.getString(8));
                        txtTel.setText(DT.getString(9));
                        txtDV.setText(DT.getString(10));

                        gl.rutatipo = "V";
                        gl.cliente = DT.getString(0);
                        gl.nivel = gl.nivel_sucursal;
                        gl.percepcion = 0;
                        gl.contrib = DT.getString(6);
                        gl.clienteDV = DT.getString(10);
                        gl.scancliente = gl.cliente;
                        gl.gNombreCliente = txtNom.getText().toString();
                        gl.gNITCliente = NIT;
                        gl.gDirCliente = DT.getString(4);

                        gl.media = DT.getInt(5);
                        gl.codigo_cliente = DT.getInt(7);clicorel=gl.codigo_cliente;
                        gl.cliente_dom = gl.codigo_cliente;

                        resultado = true;
                    } else {
                        if (gl.codigo_cliente==gl.emp*10) {
                            txtNom.setText("Consumidor Final");
                            txtRef.setText("");
                            txtCorreo.setText("");
                            txtTel.setText("");
                            txtDV.setText("");

                            resultado = true;
                        } else {
                            resultado = false;
                        }
                    }
                }
                if (DT != null) DT.close();
            }
        } catch (Exception e){
            mu.toast("Ocurrió un error buscando al cliente");
            resultado=false;
        }
        return resultado;
    }

    private void terminaCliente() {

        gl.parallevar=cbllevar.isChecked();
        gl.domicilio =cbdomicilio.isChecked();

        gl.cliente_credito=false;gl.limite_credito=0;gl.dias_credito=0;
        try {
            sql="SELECT LIMITECREDITO,DIACREDITO FROM P_CLIENTE WHERE CODIGO_CLIENTE="+gl.codigo_cliente;
            Cursor dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                gl.limite_credito=dt.getDouble(0);
                gl.dias_credito=dt.getInt(1);

                if (gl.limite_credito>0 && gl.dias_credito>0)  gl.cliente_credito=true;
            }
        } catch (Exception e) {
            gl.cliente_credito=false;
        }

        gl.cliente_credito=false;
        gl.ventalock=false;

        finish();

    }

    //endregion

    //region Dialogs


    //endregion

    //region Aux

    private void getURL() {
        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 6000;

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 6000; else gl.timeout = Integer.valueOf(line);
                myReader.close();
            }
        } catch (Exception e) {}

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

    private void parametrosPanama() {
        try {
            Usuario_FEL=FELAmb.usuario_api;
            LLave_API=FELAmb.llave_api;
            LLave_FIRMA=FELAmb.llave_firma;
            URL_FEL=FELAmb.URL_ruc;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void corelCliente() {
        int cc1,cc2;

        try {
            if (fbcc.errflag) cc1=0;else cc1=fbcc.item.corel;

            T_cli_corelObj.fill();
            if (T_cli_corelObj.count>0) cc2=T_cli_corelObj.first().corel; else cc2=0;

            clicorel=cc1;
            if (cc2>cc1) clicorel=cc2;
            clicorel++;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void guardaCorelCliente() {
        try {
            clsClasses.clsFbCliCorel ccor=clsCls.new clsFbCliCorel();
            ccor.id=gl.codigo_ruta;
            ccor.corel=clicorel;
            fbcc.setItem(ccor);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        clsClasses.clsT_cli_corel item = clsCls.new clsT_cli_corel();
        item.id=gl.codigo_ruta;
        item.corel=clicorel;

        try {
            T_cli_corelObj.add(item);
        } catch (Exception e) {
            T_cli_corelObj.update(item);
        }

    }

    private void limpiaCampos() {
        try {
            txtNIT.setText("");
            txtNom.setText("");
            txtCorreo.setText("");
            txtTel.setText("");
            txtDV.setText("");
            lblMuni.setText("");
            cbRUC.setChecked(false);
            gl.cli_depto="";
            gl.cli_muni="";

            txtNIT.requestFocus();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();

            P_clienteObj.reconnect(Con,db);
            T_cli_corelObj.reconnect(Con,db);

            if (browse==1) {
                browse=0;
                lblMuni.setText(gl.gstr);
                return;
            }

            if (browse==2) {
                browse = 0;
                if (!gl.cliente.isEmpty()) {
                    txtNIT.setText(gl.gNITCliente);
                    existeCliente();
                }
                return;
            }

            } catch (Exception e){
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (!idle) {
            toast("Espere . . .");
        } else {
            super.onBackPressed();
        }
    }

    //endregion

}