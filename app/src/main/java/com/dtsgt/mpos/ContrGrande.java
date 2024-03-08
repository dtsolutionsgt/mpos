package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_giro_negocioObj;
import com.dtsgt.classes.clsP_gran_contObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsP_tiponegObj;
import com.dtsgt.classes.clsT_sv_gcontObj;
import com.dtsgt.classes.extListDlg;

public class ContrGrande extends PBase {

    private TextView lblDep, lblMuni;
    private EditText txtGiro,txtNIT;

    private clsP_departamentoObj P_departamentoObj;
    private clsP_municipioObj P_municipioObj;
	private clsP_giro_negocioObj P_giro_negocioObj;
    private clsT_sv_gcontObj T_sv_gcontObj;
    private clsP_gran_contObj P_gran_contObj;

    private String iddep,idmuni,dep,muni,neg,nit,nrc;
    private int idneg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contr_grande);

            super.InitBase();

            lblDep  = findViewById(R.id.textView308);lblDep.setText("");
            lblMuni = findViewById(R.id.textView311);lblMuni.setText("");
            txtGiro  = findViewById(R.id.editTextNumber5);txtGiro.setText("");
            txtNIT  = findViewById(R.id.editTextNumber8);txtNIT.setText("");

            P_departamentoObj=new clsP_departamentoObj(this,Con,db);
            P_municipioObj=new clsP_municipioObj(this,Con,db);
            P_giro_negocioObj=new clsP_giro_negocioObj(this,Con,db);
            T_sv_gcontObj=new clsT_sv_gcontObj(this,Con,db);
            P_gran_contObj=new clsP_gran_contObj(this,Con,db);

            nrc=gl.gNITCliente;

            if (!cargaNRC()) cargaSeleccion();
            cargaDepartamentos();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doContinue(View view) {
        if (validaDatos()) {
            guardaSeleccion();
            guardaNRC();
            finish();
        }
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

    private void listDepartamentos() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ContrGrande.this,"Departamento");

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
            listdlg.buildDialog(ContrGrande.this,"Municipio");

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


    //endregion

    //region Aux

    private boolean cargaNRC() {
        try {
            iddep = "";
            idmuni = "";
            dep = "";
            muni = "";
            neg = "";
            idneg = 0;

            P_gran_contObj.fill("WHERE (NRC='"+nrc+"')");
            if (P_gran_contObj.count==0) return false;

            iddep = P_gran_contObj.first().iddep;
            idmuni = P_gran_contObj.first().idmuni;
            try {
                idneg = Integer.parseInt(P_gran_contObj.first().idneg);
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());idneg=0;
            }

            dep = P_gran_contObj.first().dep;
            muni = P_gran_contObj.first().muni;
            nit = P_gran_contObj.first().nit;

            lblDep.setText(dep);
            lblMuni.setText(muni);
            txtGiro.setText(""+idneg);
            txtNIT.setText(nit);

            return true;

        } catch (Exception e) {
            iddep="";idmuni="";idneg=0;
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
            item.idneg=""+idneg;
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
            idneg = 0;

            T_sv_gcontObj.fill();
            if (T_sv_gcontObj.count > 0) {

                iddep = T_sv_gcontObj.first().iddep;
                idmuni = T_sv_gcontObj.first().idmuni;
                idneg = T_sv_gcontObj.first().idneg;
                dep = T_sv_gcontObj.first().dep;
                muni = T_sv_gcontObj.first().muni;
                neg = T_sv_gcontObj.first().neg;

                lblDep.setText(dep);
                lblMuni.setText(muni);
                txtGiro.setText(""+idneg);
                txtNIT.setText("");

            }
        } catch (Exception e) {
            iddep="";idmuni="";idneg=0;
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void guardaSeleccion() {
        clsClasses.clsT_sv_gcont item = clsCls.new clsT_sv_gcont();

        try {
            item.id=1;
            item.iddep=iddep;
            item.idmuni=idmuni;
            item.idneg=idneg;
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

        gl.sal_idneg=idneg;
        gl.sal_iddep=iddep;
        gl.sal_idmun=idmuni;
        gl.sal_neg=neg;
        gl.sal_mun=muni;
        gl.sal_dep=dep;
    }

    private boolean validaDatos() {
        String gn;

        try {

            if (iddep.isEmpty()) {
                msgbox("Falta seleccionar un departamento");return false;
            }

            if (idmuni.isEmpty()) {
                msgbox("Falta seleccionar un municipio");return false;
            }

            gn=txtGiro.getText().toString();
            if (gn.isEmpty()) {
                msgbox("Falta ingresar un giro de negocio");txtGiro.requestFocus();return false;
            }

            if (!validaGiro(gn)) {
                msgbox("Giro de negocio incorrecto");
                txtGiro.requestFocus();txtGiro.selectAll();return false;
            }

            nit=txtNIT.getText().toString();

            if (nit.isEmpty()) {
                msgbox("NIT incorrecto");
                txtNIT.requestFocus();txtNIT.selectAll();return false;
            }

            if (nit.length()!=14) {
                msgbox("NIT incorrecto");
                txtNIT.requestFocus();txtNIT.selectAll();return false;
            }

            idneg=Integer.parseInt(gn);

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
            P_gran_contObj=new clsP_gran_contObj(this,Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}