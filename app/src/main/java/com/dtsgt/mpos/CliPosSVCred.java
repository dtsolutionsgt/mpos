package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_giro_negocioObj;
import com.dtsgt.classes.clsP_gran_contObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsT_sv_gcontObj;
import com.dtsgt.classes.extListDlg;

public class CliPosSVCred extends PBase {

    private TextView lblDep, lblMuni,lblNRC;
    private EditText txtGiro,txtNIT,txtNom,txtEmail,txtDir;

    private clsP_departamentoObj P_departamentoObj;
    private clsP_municipioObj P_municipioObj;
    private clsP_giro_negocioObj P_giro_negocioObj;
    private clsT_sv_gcontObj T_sv_gcontObj;
    private clsP_gran_contObj P_gran_contObj;
    private clsP_clienteObj P_clienteObj;

    private String iddep,idmuni,dep,muni,neg,nit,nrc,dir,nom,email;
    private int idgiro,codigo;
    private boolean clinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cli_pos_svcred);

            super.InitBase();

            lblDep  = findViewById(R.id.textView308);lblDep.setText("");
            lblMuni = findViewById(R.id.textView311);lblMuni.setText("");
            txtGiro  = findViewById(R.id.editTextNumber5);txtGiro.setText("");
            txtNIT  = findViewById(R.id.editTextNumber8);txtNIT.setText("");
            txtNom =  findViewById(R.id.editTextNumber10);txtNom.setText("");
            txtEmail =  findViewById(R.id.editTextNumber12);txtEmail.setText("");
            txtDir =  findViewById(R.id.editTextNumber11);txtDir.setText("");
            lblNRC =  findViewById(R.id.textView346);

            P_departamentoObj=new clsP_departamentoObj(this,Con,db);
            P_municipioObj=new clsP_municipioObj(this,Con,db);
            P_giro_negocioObj=new clsP_giro_negocioObj(this,Con,db);
            T_sv_gcontObj=new clsT_sv_gcontObj(this,Con,db);
            P_gran_contObj=new clsP_gran_contObj(this,Con,db);
            P_clienteObj=new clsP_clienteObj(this,Con,db);

            nrc=gl.gNITCliente;lblNRC.setText(nrc);
            clinue=gl.sv_cli_nue;

            if (!cargaNRC()) cargaSeleccion();
            cargaDepartamentos();

            if (clinue) {
                codigo=nitnumsv(gl.gNITCliente);
            } else {
                cargaCliente();
            }

            /*
            txtNIT.setText("12345678901222");
            txtNom.setText("DT solutions");
            txtEmail.setText("email@email.com");
            txtDir.setText("calle 12345");
            */

            txtNom.requestFocus();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doContinue(View view) {
        if (validaDatos()) guardaDatos();
    }

    public void doGiro(View view) {
        browse=1;
        startActivity(new Intent(this,CliPosSVGiro.class));
    }

    public void doExit(View view) {
        try {
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doDep(View view) {
        try {
            listDepartamentos();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doMuni(View view) {
        try {
            cargaMunicipios();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void guardaDatos() {
        guardaSeleccion();
        guardaNRC();

        agregaCliente(gl.gNITCliente, nom, email,dir);
        procesaNIT();

        gl.mododocesa=2;
        finish();
    }

    private void cargaCliente() {
        try {
            P_clienteObj.fill("WHERE (NIT='"+gl.gNITCliente+"')");
            if (P_clienteObj.count>0) {
                codigo=(int) P_clienteObj.first().codigo_cliente;
                txtNom.setText(P_clienteObj.first().nombre);
                txtEmail.setText(P_clienteObj.first().email);
                txtDir.setText(P_clienteObj.first().direccion);
            } else {
                codigo=nitnumsv(gl.gNITCliente);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listDepartamentos() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(CliPosSVCred.this,"Departamento");

            for (int i = 0; i <P_departamentoObj.count; i++) {
                listdlg.add(P_departamentoObj.items.get(i).codigo,P_departamentoObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        iddep=listdlg.getCodigo(position);
                        dep=listdlg.getText(position);
                        lblDep.setText(dep);
                        idmuni="";lblMuni.setText("");
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listMunicipios() {
        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(CliPosSVCred.this,"Municipio");

            for (int i = 0; i <P_municipioObj.count; i++) {
                listdlg.add(P_municipioObj.items.get(i).codigo,P_municipioObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idmuni=listdlg.getCodigo(position);
                        muni=listdlg.getText(position);
                        lblMuni.setText(muni);
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaDepartamentos() {
        try {
            P_departamentoObj.fill("WHERE (CODIGO_AREA='03') ORDER BY NOMBRE");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaMunicipios() {
        try {
            if (iddep.isEmpty()) {
                idmuni="";lblMuni.setText("");
                listDepartamentos();
                toast("Debe seleccionar un departamento");
            } else {
                P_municipioObj.fill("WHERE (CODIGO_DEPARTAMENTO='"+iddep+"') ORDER BY NOMBRE");
                listMunicipios();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean agregaCliente(String NIT,String Nom,String Correo,String dir) {

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
            ins.add("TELEFONO","");
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
            gl.gDirCliente = dir;
            gl.gCorreoCliente = email;
            gl.gTelCliente= " ";

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=nom;
            gl.dom_dir = " ";gl.dom_ref="";
            gl.dom_tel= " ";

            gl.sal_NRC=true;
            gl.sal_NIT=false;
            gl.sal_PER=true;
            gl.mododocesa=2;

            gl.media=1;
            gl.cf_domicilio=false;

            gl.cliente_credito=false;gl.limite_credito=0;gl.dias_credito=0;
            gl.ventalock=false;

            finish();

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux

    private boolean cargaNRC() {
        try {
            iddep = "";
            idmuni = "";
            dep = "";
            muni = "";
            neg = "";

            P_gran_contObj.fill("WHERE (NRC='"+nrc+"')");
            if (P_gran_contObj.count==0) return false;

            iddep = P_gran_contObj.first().iddep;
            idmuni = P_gran_contObj.first().idmuni;
            try {
                //idneg = Integer.parseInt(P_gran_contObj.first().idneg);
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            dep = P_gran_contObj.first().dep;
            muni = P_gran_contObj.first().muni;
            nit = P_gran_contObj.first().nit;
            idgiro = Integer.parseInt(P_gran_contObj.first().idneg);
            asignaGiro();

            lblDep.setText(dep);
            lblMuni.setText(muni);
            //txtGiro.setText(""+idneg);
            txtNIT.setText(nit);

            return true;

        } catch (Exception e) {
            iddep="";idmuni="";idgiro=0;
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void guardaNRC() {
        clsClasses.clsP_gran_cont item = clsCls.new clsP_gran_cont();

        try {
            item.nrc=nrc;
            item.iddep=iddep;
            item.idmuni=idmuni;
            item.idneg=""+idgiro;
            item.dep=dep;
            item.muni=muni;
            item.nit=nit;

            P_gran_contObj.add(item);

        } catch (Exception e) {
            try {
                P_gran_contObj.update(item);
            } catch (Exception ee) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());return;
            }
        }

    }

    private void cargaSeleccion() {
        try {
            iddep = "";
            idmuni = "";
            dep = "";
            muni = "";
            neg = "";


            T_sv_gcontObj.fill();
            if (T_sv_gcontObj.count > 0) {

                iddep = T_sv_gcontObj.first().iddep;
                idmuni = T_sv_gcontObj.first().idmuni;
                dep = T_sv_gcontObj.first().dep;
                muni = T_sv_gcontObj.first().muni;
                neg = T_sv_gcontObj.first().neg;

                lblDep.setText(dep);
                lblMuni.setText(muni);
                txtGiro.setText("");
                txtNIT.setText("");

            }
        } catch (Exception e) {
            iddep="";idmuni="";idgiro=0;
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void guardaSeleccion() {
        clsClasses.clsT_sv_gcont item = clsCls.new clsT_sv_gcont();

        try {
            item.id=1;
            item.iddep=iddep;
            item.idmuni=idmuni;
            item.dep=dep;
            item.muni=muni;
            item.neg=" ";
            //item.neg=neg;

            T_sv_gcontObj.add(item);

        } catch (Exception e) {
            try {
                T_sv_gcontObj.update(item);
            } catch (Exception ee) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());return;
            }
        }

        gl.sal_idneg=idgiro;
        gl.sal_iddep=iddep;
        gl.sal_idmun=idmuni;
        gl.sal_neg=""+idgiro;
        gl.sal_mun=muni;
        gl.sal_dep=dep;
    }

    private boolean validaDatos() {
        String gn;
        boolean emflag=true;

        try {

            nom="";
            ss=txtNom.getText().toString();
            if (ss.isEmpty() | ss.length()<6) {
                msgbox("Nombre incorrecto");txtNom.requestFocus();txtNom.selectAll();return false;
            }
            nom=ss;

            email=txtEmail.getText().toString();
            if (email.isEmpty()) {
                emflag=false;
            } else {
                if (email.length()<10) emflag=false;
                if (email.indexOf("@")<3) emflag=false;
                if (email.indexOf(".")<0) emflag=false;
            }
            if (!emflag) {
                msgbox("Correo incorrecto");txtEmail.requestFocus();txtEmail.selectAll();return false;
            }

            dir="";
            ss=txtDir.getText().toString();
            if (ss.isEmpty() | ss.length()<6) {
                msgbox("Dirección incorrecta");txtDir.requestFocus();txtDir.selectAll();return false;
            }
            dir=ss;

            if (iddep.isEmpty()) {
                msgbox("Falta seleccionar un departamento");return false;
            }

            if (idmuni.isEmpty()) {
                msgbox("Falta seleccionar un municipio");return false;
            }

            if (idgiro==0) {
                msgbox("Falta ingresar un giro de negocio");return false;
            }

            nit=txtNIT.getText().toString();
            if (nit.isEmpty()) {
                msgbox("NIT incorrecto");txtNIT.requestFocus();txtNIT.selectAll();return false;
            } else {
               if (!testNIT()) {
                   msgbox("NIT incorrecto");txtNIT.requestFocus();txtNIT.selectAll();return false;
               }
            }

            if (nit.length()!=14) {
                msgbox("NIT incorrecto");
                txtNIT.requestFocus();txtNIT.selectAll();return false;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean validaGiro(String gn) {
        try {
            P_giro_negocioObj.fill("WHERE (CODIGO="+gn+")");
            if (P_giro_negocioObj.count>0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return false;
    }

    private void asignaGiro() {
        try {
            P_giro_negocioObj.fill("WHERE (codigo="+idgiro+")");
            if (P_giro_negocioObj.count>0) {
                txtGiro.setText(P_giro_negocioObj.first().descripcion);
            } else {
                txtGiro.setText("");
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

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

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_departamentoObj.reconnect(Con,db);
            P_municipioObj.reconnect(Con,db);
            P_giro_negocioObj.reconnect(Con,db);
            T_sv_gcontObj.reconnect(Con,db);
            P_gran_contObj.reconnect(Con,db);
            P_clienteObj.reconnect(Con,db);

            if (browse==1) {
                browse=0;
                if (gl.gint!=0) {
                    idgiro=gl.gint;asignaGiro();
                }
            }

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}