package com.dtsgt.mpos;

import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.classes.clsP_clienteObj;

public class CliPosSVCons extends PBase {

    private EditText txtNom,txtEmail;
    private TextView lblNIT;

    private clsP_clienteObj P_clienteObj;

    private boolean clinue;
    private String nom,email;
    private int codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_pos_svcons);

            super.InitBase();

            txtNom =  findViewById(R.id.editTextNumber4);
            txtEmail =  findViewById(R.id.editTextNumber9);
            lblNIT =  findViewById(R.id.textView336);

            P_clienteObj=new clsP_clienteObj(this,Con,db);

            clinue=gl.sv_cli_nue;
            lblNIT.setText(gl.gNITCliente);

            txtNom.requestFocus();
            txtNom.setText("");
            txtEmail.setText("");

            if (clinue) {
                codigo=nitnumsv(gl.gNITCliente);
            } else {
                try {
                    P_clienteObj.fill("WHERE (NIT='"+gl.gNITCliente+"')");
                    if (P_clienteObj.count>0) {
                        codigo=(int) P_clienteObj.first().codigo_cliente;
                        txtNom.setText(P_clienteObj.first().nombre);
                        txtEmail.setText(P_clienteObj.first().email);
                    } else {
                        codigo=nitnumsv(gl.gNITCliente);
                    }
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }

            //txtNom.setText("Jethro Paredes");
            //txtEmail.setText("jpospichal@dts.com.gt");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doSave(View view) {
        if (validaDatos()) {
            agregaCliente(gl.gNITCliente, nom, email);
            procesaNIT();
        }
    }

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void procesaNIT() {

        try {
            nom=app.purgeString(nom);
            email=app.purgeEmail(email);

            gl.cliente_dom=codigo;

            gl.rutatipo="V";
            gl.cliente=""+codigo; if (codigo<=0) gl.cliente=gl.emp+"0";
            gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

            gl.gNombreCliente = nom;
            gl.gNITCliente =gl.gNITCliente;
            gl.gDirCliente = " ";
            gl.gCorreoCliente = email;
            gl.gTelCliente= " ";

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=nom;
            gl.dom_dir = " ";gl.dom_ref="";
            gl.dom_tel= " ";

            gl.sal_NRC=false;
            gl.sal_NIT=true;
            gl.sal_PER=false;
            gl.mododocesa=1;

            gl.media=1;
            gl.cf_domicilio=false;

            gl.cliente_credito=false;gl.limite_credito=0;gl.dias_credito=0;
            gl.ventalock=false;

            finish();

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private boolean agregaCliente(String NIT,String Nom,String Correo) {
        String dir=" ",tel=" ";

        try {
            Nom=app.purgeString(Nom);
            Correo=app.purgeEmail(Correo);

            gl.codigo_cliente=codigo;

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
            ins.add("TELEFONO",tel);
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
                upd.Where("CODIGO_CLIENTE="+codigo);
                db.execSQL(upd.sql());

                return true;

            } catch (SQLException e1) {
                mu.msgbox2(e1.getMessage());return false;
            }

        }

    }

    //endregion

    //region Aux

    private int nitnumsv(String nit) {
        try {
            nit =nit.replaceAll("-","");
            if (nit.length()==14) nit=nit.substring(9);
            int val=Integer.parseInt(nit);
            return val;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return gl.emp*10;
        }
    }

    private boolean validaDatos() {
        String ss;

        try {
            nom="";
            ss=txtNom.getText().toString();
            if (ss.isEmpty() | ss.length()<6) {
                msgbox("Nombre incorrecto");
                txtNom.requestFocus();txtNom.selectAll();return false;
            }
            nom=ss;

            email=txtEmail.getText().toString()+"";

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