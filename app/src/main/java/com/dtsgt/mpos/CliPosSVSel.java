package com.dtsgt.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dtsgt.classes.clsP_clienteObj;

public class CliPosSVSel extends PBase {

    private EditText txtNIT,txtNRC;
    private CheckBox cbllevar,cbdomicilio;

    private clsP_clienteObj P_clienteObj;

    private String nit,nrc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_pos_svsel);

            super.InitBase();

            txtNIT =  findViewById(R.id.editTextNumber4);txtNIT.setText("");txtNIT.requestFocus();
            txtNRC =  findViewById(R.id.editTextNumber9);txtNRC.setText("");
            cbllevar = findViewById(R.id.checkBox28);
            cbdomicilio = findViewById(R.id.chkPickup3);

            P_clienteObj=new clsP_clienteObj(this,Con,db);

            txtNIT.setText("12345678901111");
            txtNRC.setText("12345678");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doNIT(View view) {

        try {
            nit=txtNIT.getText().toString();

            if (testNIT()) {
                buscaNIT();

                gl.gNITCliente=nit;
                gl.sv_flag_nit=true;
                gl.sv_flag_ncr=false;
                gl.parallevar=cbllevar.isChecked();
                gl.domicilio=cbdomicilio.isChecked();

                startActivity(new Intent(this,CliPosSVCons.class));
                finish();
            } else {
                msgbox("NIT incorrecto.");return;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doNRC(View view) {

        try {
            nrc=txtNRC.getText().toString();

            if (testNRC()) {
                buscaNRC();

                gl.gNITCliente=nrc;
                gl.sv_flag_nit=false;
                gl.sv_flag_ncr=true;
                gl.parallevar=cbllevar.isChecked();
                gl.domicilio =cbdomicilio.isChecked();

                startActivity(new Intent(this,CliPosSVCred.class));
                finish();
            } else {
                msgbox("NRC incorrecto");return;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void buscaNIT() {
        try {
            gl.sv_cli_nue=true;
            P_clienteObj.fill("WHERE (NIT='"+nit+"')");
            if (P_clienteObj.count>0) gl.sv_cli_nue=false;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buscaNRC() {
        try {
            gl.sv_cli_nue=true;
            P_clienteObj.fill("WHERE (NIT='"+nrc+"')");
            if (P_clienteObj.count>0) gl.sv_cli_nue=false;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private int nitnumsv(String nit) {
        int val;

        try {
            val=gl.emp*10;
            nit =nit.replaceAll("-","");

            if (nit.length()>9) nit=nit.substring(9);

            val=Integer.parseInt(nit);
            return val;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return gl.emp*10;
    }

    private boolean testNIT() {
        try {
            if (nit.isEmpty()) return false;
            if (nit.length()!=14)  return false;
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean testNRC() {
        try {
            if (nrc.isEmpty()) return false;
            int ll=nrc.length();
            if (ll<3 | ll>8 )  return false;
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_clienteObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion



}