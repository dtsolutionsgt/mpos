package com.dtsgt.fel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_facturadObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturapObj;
import com.dtsgt.classes.clsD_fel_errorObj;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_departamentoObj;
import com.dtsgt.classes.clsP_municipioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FELContAnul extends PBase {

    private TextView lbl1,lbl2,lbl3;
    private ProgressBar pbar;

    private clsFELInFile fel;
    private XMLObject xobj;

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsD_facturafObj D_facturafObj;
    private clsD_facturapObj D_facturapObj;
    private clsP_productoObj prod;

    private clsClasses.clsD_factura fact=clsCls.new clsD_factura();
    private clsClasses.clsD_facturad factd=clsCls.new clsD_facturad();
    private clsClasses.clsD_facturaf factf=clsCls.new clsD_facturaf();
    private clsClasses.clsD_facturap factp=clsCls.new clsD_facturap();

    private ArrayList<String> facts = new ArrayList<String>();

    private String felcorel,corel,ffcorel,scorel,CSQL,endstr,idfact,uuid;
    private boolean ddemomode,multiflag,factsend,contmode,conerrflag;
    private int ftot,ffail,fidx,cliid,felnivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_e_l_cont_anul);
        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.msgHeader);lbl1.setText("");
        lbl2 = (TextView) findViewById(R.id.lblWS);lbl2.setText("");
        lbl3 = (TextView) findViewById(R.id.textView152);lbl3.setText("");
        pbar = (ProgressBar) findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);

        felcorel=gl.felcorel;ffcorel=felcorel;
        multiflag=felcorel.isEmpty();
        gl.feluuid="";

        getURL();
        fel=new clsFELInFile(this,this,gl.timeout);

        clsP_sucursalObj sucursal=new clsP_sucursalObj(this,Con,db);
        sucursal.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
        clsClasses.clsP_sucursal suc=sucursal.first();

        fel.fel_llave_certificacion =suc.fel_llave_certificacion; // fel_llavews ="E5DC9FFBA5F3653E27DF2FC1DCAC824D"
        fel.fel_llave_firma=suc.fel_llave_firma; // fel_token ="5b174fb0e23645b65ef88277d654603d"
        fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;  //  1
        fel.fel_usuario_certificacion=suc.fel_usuario_certificacion; // COMERGUA

        //fel.fel_usuario_firma=suc.fel_usuario_firma; //JP2020071
        fel.fel_usuario_firma=suc.texto;

        fel.codigo_postal =suc.codigo_postal;
        fel.fel_nit=suc.nit; // NIT  96038888
        fel.fel_correo=suc.correo;  //

        //fel.fel_nombre_comercial = gl.tiendanom;  //JP2020071
        fel.fel_nombre_comercial = suc.nombre;

        fel.fraseIVA=suc.codigo_escenario_iva;
        fel.fraseISR=suc.codigo_escenario_isr;
        fel.fel_afiliacion_iva=suc.fel_afiliacion_iva;

        D_facturaObj=new clsD_facturaObj(this,Con,db);
        D_facturadObj=new clsD_facturadObj(this,Con,db);
        D_facturafObj=new clsD_facturafObj(this,Con,db);
        D_facturapObj=new clsD_facturapObj(this,Con,db);
        prod=new clsP_productoObj(this,Con,db);

        app.parametrosExtra();
        lbl2.setText("Certificador : "+gl.peFEL);
        pbar.setVisibility(View.VISIBLE);

        buildList();

        ffail=0;fidx=0;conerrflag=false;

        if (facts.size()>0) {
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    contingencia();
                }
            };
            mtimer.postDelayed(mrunner,200);
        } else {
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    gl.felcorel="";gl.feluuid="";
                    startActivity(new Intent(FELContAnul.this,FELFactura.class));
                    finish();return;
                }
            };
            mtimer.postDelayed(mrunner,500);
        }

    }

    //region Events

    //endregion

    //region Main

    private void procesafactura() {

        updateLabel();

        Handler mtimer = new Handler();
        Runnable mrunner = new Runnable() {
            @Override
            public void run() {
                contingenciaFactura();
            }
        };
        mtimer.postDelayed(mrunner, 200);
    }

    private void contingenciaFactura() {
        //buildFactXML();
        //fel.certificacion();
        int ii=0;
        if (buildAnulXML())  fel.anulacion(uuid);
    }

    private void contingencia() {

        lbl1.setText("Procesando factura  . . ."); lbl3.setText("");ffail=0;
        contmode=true;
        procesafactura();
    }

    @Override
    public void felCallBack()  {

        try {

            boolean errd=true;

            if (fel.errorcon) {
                conerrflag=true;
                msgexit(fel.error);return;
            }

            if (!fel.errorflag) {
                fact.codigoliquidacion=3;

                fact.feeluuid=" ";
                fact.feelserie=" ";
                fact.feelnumero=" ";

                D_facturaObj.update(fact);
            }

            callBackMulti();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callBackMulti() {

        fidx++;
        if (fidx<ftot) {
            if (fel.errorflag) ffail++;
            procesafactura();
        } else {
            gl.felcorel="";gl.feluuid="";
            startActivity(new Intent(FELContAnul.this,FELFactura.class));
            finish();
        }
    }

    private boolean buildAnulXML() {

        try {

            corel=facts.get(fidx);

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();

            uuid=fact.feeluuid;

            String NITReceptor = Get_NIT_Cliente(fact.cliente);

            if(NITReceptor.isEmpty()) NITReceptor="CF";

            //#EJC20200527: Quitar estos caracteres del NIT.
            NITReceptor = NITReceptor.replace("-","");
            NITReceptor = NITReceptor.replace(".","");

            fel.anulfact(uuid, fel.fel_nit,NITReceptor, fact.fechaentr, fact.fechaentr);

            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private String Get_NIT_Cliente(int Codigo_Cliente) {

        Cursor dt;

        String NIT="";

        try {

            sql="SELECT NIT FROM P_CLIENTE WHERE CODIGO='"+Codigo_Cliente+"'";
            dt=Con.OpenDT(sql);

            if(dt!=null){
                if (dt.getCount()>0){
                    dt.moveToFirst();
                    NIT = dt.getString(0);
                    if (dt!=null) dt.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  NIT;
    }

    //endregion

    //region Aux

    private void buildList() {
        String cor;
        ftot=0;

        try {
            D_facturaObj.fill("WHERE (ANULADO=0) AND ((CODIGOLIQUIDACION=1) OR (CODIGOLIQUIDACION=2)) ORDER BY FEELCONTINGENCIA");

            facts.clear();
            for (int i = 0; i <D_facturaObj.count; i++) {
                cor=D_facturaObj.items.get(i).corel;ffcorel=cor;
                if (felcorel.isEmpty()) {
                    facts.add(cor);
                } else {
                    if (cor.equalsIgnoreCase(felcorel)) {
                        facts.add(cor);
                    }
                }
            }

            ftot=facts.size();
        } catch (Exception e) {
            msgexit(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateLabel() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                handler.post(new Runnable(){
                    public void run() {
                        lbl1.setText("Procesando factura "+(fidx+1)+" / "+ftot);lbl3.setText("");
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private String prodName(int cod_prod) {
        try {
            prod.fill("WHERE codigo_producto="+cod_prod);
            return prod.first().desclarga;
        } catch (Exception e) {
            return ""+cod_prod;
        }
    }

    private void getURL() {

        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 20000;

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {

                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();

                if(line.isEmpty()) {
                    gl.timeout = 20000;
                }  else {
                    gl.timeout = Integer.valueOf(line);
                }

                myReader.close();
            }

        } catch (Exception e) {}

        if (gl.wsurl.isEmpty()) lbl2.setText("Falta archivo con URL");
    }

    //endregion

    //region Dialogs

    public void msgexit(String msg) {

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                showMsgExit("Ocurrio error en FEL :\n\n"+ fel.error);
            }
        };
        mtimer.postDelayed(mrunner,500);

    }

    public void showMsgExit(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(msg);
            dialog.setCancelable(false);

            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.feluuid="";finish();
                }
            });
            dialog.show();

        } catch (Exception ex) {
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            D_facturaObj.reconnect(Con,db);
            D_facturadObj.reconnect(Con,db);
            D_facturafObj.reconnect(Con,db);
            D_facturapObj.reconnect(Con,db);
            prod.reconnect(Con,db);
        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //endregion
}