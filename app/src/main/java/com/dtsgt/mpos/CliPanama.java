package com.dtsgt.mpos;

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

import com.dtsgt.felpana.clsFEPDocVal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CliPanama extends PBase {

    private EditText txtNIT,txtNom,txtRef,txtCorreo,txtTel;
    private TextView lblPed,lblDom,lblNIT,lblDir,btnNIT,btnCF,lblMuni;
    private ImageView imgmuni;
    private RelativeLayout relcli;
    private ProgressBar pbar;
    private CheckBox cbllevar,cbdomicilio, cbRUC;

    private clsFEPDocVal fdval;

    private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente,
            sTelCliente,wspnerror,corelorden;

    private String Usuario_FEL,LLave_API,LLave_FIRMA,URL_FEL;

    private boolean consFinal=false,request_exit=false,bloqueado,domicilio,nrslt,NitValidadoInfile =false;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_panama);

            super.InitBase();

            txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.setText("");txtNIT.requestFocus();
            txtNom = (EditText) findViewById(R.id.editText2);txtNom.setText("");
            txtRef = (EditText) findViewById(R.id.editText1);txtRef.setText("Ciudad");
            txtCorreo= (EditText) findViewById(R.id.txtCorreo);txtCorreo.setText("");
            txtTel= (EditText) findViewById(R.id.editTextNumber4);txtTel.setText("");
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

            getURL();

            gl.pedcorel="";gl.parallevar=false;gl.cf_domicilio=false;
            gl.mododocesa=-1;

            bloqueado=false;

            domicilio=gl.peVentaDomicilio;

            cbllevar.setEnabled(true); cbllevar.setChecked(false);
            cbdomicilio.setEnabled(true); cbdomicilio.setChecked(false);

            lblDir.setVisibility(View.GONE);

            NitValidadoInfile =false;

            //if (gl.cliente_dom!=0) cargaCliente();

            txtNIT.setText(" 1AV-1234-12345 ");
            parametrosPanama();
            fdval= new clsFEPDocVal(this,Usuario_FEL,LLave_API,LLave_FIRMA,URL_FEL) ;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    public void felCallBack()  {
        try {
            if (fdval.errorflag) throw new Exception(fdval.error);

            RUC_result();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void clienteNIT(View view) {
        boolean flag_NRC;

        purgeNIT();
        sNITCliente =txtNIT.getText().toString();

        fdval.ValidaRUC_API(sNITCliente);


        /*
        try {

            purgeNIT();

            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();
            sTelCliente=txtTel.getText().toString();

            if (sNITCliente.isEmpty()) {
                msgbox("Identificación incorrecta");return;
            }

            if (sNITCliente.length()<3) {
                msgbox("Identificación incorrecta");return;
            }

            if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                if (sNombreCliente.isEmpty()) {
                    msgbox("Falta definir nombre");return;
                }
                if (sNombreCliente.length()<5) {
                    msgbox("Nombre debe tener minimo 5 letras");return;
                }
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
                        msgbox("NIT incorrecto");txtNIT.selectAll();txtNIT.requestFocus();return;
                    }
                }
            }  else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                if (!app.validaNITHon(sNITCliente)) {
                    msgbox("RTN incorrecto");return;
                }
            } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                if (!app.validaNITSal(sNITCliente)) {
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

            if (sTelCliente.isEmpty()) sTelCliente="";

            flag_NRC=false;gl.sal_PER=false;
            if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                if (gl.sal_NRC) flag_NRC = true;
            }

            if (flag_NRC) {
                msgAskCG("Gran contribuyente ");
            } else {
                gl.mododocesa=1;
                if (!existeCliente()){
                    if (agregaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente)) procesaNIT(sNITCliente);
                } else {
                    actualizaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente);
                    procesaNIT(sNITCliente);
                }
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

         */
    }

    public void consFinal(View view) {
        String ss=txtNIT.getText().toString();
        String ddnom,ddir,dcor;

        purgeNIT();
        sNITCliente =txtNIT.getText().toString();

        int rslt=fdval.validaCedula(sNITCliente);
        if (rslt>0) msgbox("OK");else msgbox("FAIL");


        //testCedula();

        /*
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

         */

    }

    private void setHandlers() {

        try {

            txtNIT.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    int i=0;
                    if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                        //consultaNITInfile();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        } catch (Exception e){ }

        cbdomicilio.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) cbllevar.setChecked(false);
                    }
                }
        );

        cbllevar.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                        if (isChecked){
                            gl.parallevar = true;cbdomicilio.setChecked(false);
                        } else{
                            gl.parallevar = false;
                        }
                    }
                }
        );

    }

    //endregion

    //region Main


    //endregion

    //region Dialogs


    //endregion

    //region Aux

    private void RUC_result() {
        if (fdval.value==1) msgbox("RUC "+fdval.RUC+" correcto");else msgbox("RUC "+fdval.RUC+" incorrecto");
    }

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
            Usuario_FEL="12345-05-123456";
            LLave_API="bf3f86f74dbb706f42749939c";
            LLave_FIRMA="SIOB5IYVWO3PIZ94FRDSPAK4B4LN5HT5";
            URL_FEL="https://certificador-unificado.infilepac.com/api/v1/consultas/unificado/test/json/ruc_dv";
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }



    }

    //endregion

    //region Activity Events


    //endregion


}