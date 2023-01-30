package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocObj;
import com.dtsgt.classes.clsD_pedidocomboObj;
import com.dtsgt.classes.clsD_pedidodObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_pedidodObj;
import com.dtsgt.classes.clsT_ventaObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class CliPos extends PBase {

	private EditText txtNIT,txtNom,txtRef,txtCorreo,txtTel;
	private TextView lblPed,lblDom,lblDir,btnNIT;
    private RelativeLayout relped,relcli;
	private ProgressBar pbar;
	private CheckBox cbllevar;
    private CheckBox cbpickup;

    //private wsInventCompartido wsi;

    private Runnable rnRecibeInventario;

    private ArrayList<String> pedidos =new ArrayList<String>();
    private ArrayList<String> peditems = new ArrayList<String>();

	private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente,
            sTelCliente,wspnerror,pedcorel,corelorden;
	private boolean consFinal=false,idleped=true;
	private boolean request_exit=false,bloqueado,domicilio,nrslt;
	private int cantped;

    private TimerTask ptask;
    private int period=10000,delay=50;

    private static String urlNit = "https://consultareceptores.feel.com.gt/rest/action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_cli_pos);
        } else {
            setContentView(R.layout.activity_cli_pos_ver);
        }

		super.InitBase();

		txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.setText("");txtNIT.requestFocus();
		txtNom = (EditText) findViewById(R.id.editText2);txtNom.setText("");
		txtRef = (EditText) findViewById(R.id.editText1);txtRef.setText("Ciudad");
        txtCorreo= (EditText) findViewById(R.id.txtCorreo);txtCorreo.setText("");
        txtTel= (EditText) findViewById(R.id.editTextNumber4);txtTel.setText("");
        lblPed = (TextView) findViewById(R.id.textView177);lblPed.setText("");
        lblDom = (TextView) findViewById(R.id.textView237);
        lblDir= (TextView) findViewById(R.id.textView238);
        relped = (RelativeLayout) findViewById(R.id.relPed);relped.setVisibility(View.INVISIBLE);
        relcli = (RelativeLayout) findViewById(R.id.relclipos);
        pbar = (ProgressBar) findViewById(R.id.progressBar4);pbar.setVisibility(View.INVISIBLE);
        cbllevar = findViewById(R.id.checkBox21);
        cbpickup = findViewById(R.id.chkPickup);
        btnNIT= findViewById(R.id.textView6);

        setHandlers();

        gl.InvCompSend=false;

        getURL();
        //wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        gl.pedcorel="";gl.parallevar=false;gl.cf_domicilio=false;
        bloqueado=false;

        domicilio=gl.modo_domicilio;
        cbllevar.setEnabled(true);cbpickup.setEnabled(true);
        if (domicilio) {
            lblDom.setVisibility(View.VISIBLE);
            lblDir.setVisibility(View.VISIBLE);
            cbllevar.setChecked(true);cbllevar.setEnabled(false);
        } else {
            lblDom.setVisibility(View.INVISIBLE);
            lblDir.setVisibility(View.GONE);
            cbllevar.setChecked(false);cbllevar.setEnabled(true);
        }

        if (gl.codigo_pais.equalsIgnoreCase("GT")) {
            btnNIT.setText("Cliente con NIT");
        } else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
            btnNIT.setText("Cliente con RTN");
        }

        if (!gl.peVentaDomicilio) cbllevar.setEnabled(false);
        if (!gl.peVentaEntrega)   cbpickup.setEnabled(false);

        NitValidadoInfile =false;

        /*
        if (gl.InvCompSend) {
            gl.InvCompSend=false;
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    gl.autocom = 1;
                    startActivity(new Intent(CliPos.this,WSEnv.class));
                }
            };
            mtimer.postDelayed(mrunner,200);
        }
         */

        if (gl.cliente_dom!=0) cargaCliente();

	}

    public interface ExtRunnable extends Runnable {
        public void run(String data);
    }

	//region  Events

    public void consFinal(View view) {

        String ss=txtNIT.getText().toString();
        String ddnom,ddir,dcor;

        if (ss.length()>4) {
            msgAskCF("Está seguro de continuar con el consumidor final");
        } else {
            try {
                ddnom =txtNom.getText().toString();if (ddnom.isEmpty()) ddnom="Consumidor final";
                ddir =txtRef.getText().toString();if (ddir.isEmpty()) ddir="Ciudad";
                dcor="consumidorfinal@gmail.com";

                consFinal=true;
                if (agregaCliente("C.F.",ddnom,ddir,
                        dcor,""+txtTel.getText().toString())) procesaCF() ;
            } catch (Exception e) {
                msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        }
    }

	public void clienteNIT(View view) {

		try{

            sNITCliente =txtNIT.getText().toString();
            sNombreCliente =txtNom.getText().toString();
            sDireccionCliente =txtRef.getText().toString();
            sCorreoCliente = txtCorreo.getText().toString();
            sTelCliente=txtTel.getText().toString();

            if (sDireccionCliente.isEmpty()) {
                toast("Falta definir la direccion");return;
            }

            sDireccionCliente=sDireccionCliente+" ";

            gl.nit_tipo="N";

            if (gl.codigo_pais.equalsIgnoreCase("GT")) {
                if (!validaNIT(sNITCliente)) {
                    msgbox("NIT incorrecto");return;
                }
            }  if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                if (!validaNITHon(sNITCliente)) {
                    msgbox("NIT incorrecto");return;
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

			if (!existeCliente()){
        		if (agregaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente)) procesaNIT(sNITCliente);
			} else {
                actualizaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente);
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

    public void doDomicilio(View view) {
        if (hasProducts()) {
            if (gl.cliente_dom>0) {

            } else {
                msgbox("Falta definir cliente");
            }
        } else {
            msgExit("LISTA DE PRODUCTOS ESTÁ VACÍA");
        }
    }

    public void doDireccion(View view) {

        String snit =txtNIT.getText().toString();

        if (snit.isEmpty()) {
            msgbox("Falta el NIT de cliente");txtNIT.requestFocus();return;
        }
        if (esCF(snit)) {
            msgbox("Al consumidor final no se puede aplicar direccion de lista");txtRef.requestFocus();return;
        }

        if (clientePorNIT(snit)==0) {
            msgbox("Al cliente nuevo no se puede aplicar direccion de lista");txtRef.requestFocus();return;
        } else {
            if (gl.codigo_cliente!=0) {
                gl.gstr=txtNom.getText().toString();
                if (gl.gstr.isEmpty()) {
                    msgbox("Falta nombre de cliente");return;
                }


                browse=2;gl.dom_ddir="";
                startActivity(new Intent(this,CliDirList.class));
            } else {
                msgbox("No se logro identificar cliente.\n Ingrese de nuevo el NIT");
                txtNIT.selectAll();txtNIT.requestFocus();return;
            }
        }
    }

    public void doWspnError(View view) {
        toast(wspnerror);
    }

	private void setHandlers() {

        rnRecibeInventario = new Runnable() {
            public void run() {
                bloqueado=false;
                pbar.setVisibility(View.INVISIBLE);
               // if (wsi.errflag) {
                    //msgbox2("wsi"+wsi.error);
                //} else {
                    //confirmaInventario();
                    gl.ventalock=false;
                    gl.parallevar=cbllevar.isChecked();
                    finish();
                //}
            }
        };

		try{

			txtNIT.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
                    int i=0;
					if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                        consultaNITInfile();
						return true;
					} else {
         			    return false;
					}
				}
			});

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

		cbpickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (isChecked){
                        gl.pickup = true;
                    }else{
                        gl.pickup = false;
                    }
                }
            }
        );

    }

    //endregion

	//region Main

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
            sNombreCliente =txtNom.getText().toString();
            //#EJC20210130: Una dirección tenía enter... quitar espacios vacíos con Trim.
            sDireccionCliente =txtRef.getText().toString().trim();
            sCorreoCliente = txtCorreo.getText().toString();
            sTelCliente=txtTel.getText().toString();


            if (sNombreCliente.isEmpty()) gl.gNombreCliente ="Consumidor final";else gl.gNombreCliente=sNombreCliente;
            if (sDireccionCliente.isEmpty()) gl.gDirCliente ="Ciudad";else gl.gDirCliente=sDireccionCliente;
            if (sTelCliente.isEmpty()) gl.gTelCliente =""; else gl.gTelCliente=sTelCliente;

            gl.gNombreCliente ="Consumidor final";
            gl.gDirCliente ="Ciudad";
            gl.gTelCliente ="";
            gl.gNITCliente ="CF";
            gl.nit_tipo="N";

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=sNombreCliente;
            gl.dom_dir =sDireccionCliente;gl.dom_ref="";
            gl.dom_tel=sTelCliente;

            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			consFinal=false;
			limpiaCampos();

            gl.cf_domicilio=true;
            terminaCliente();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void procesaNIT(String snit) {

        int codigo=nitnum(snit);

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

            gl.dom_nit= gl.gNITCliente;
            gl.dom_nom=sNombreCliente;
            gl.dom_dir =sDireccionCliente;gl.dom_ref="";
            gl.dom_tel=sTelCliente;

            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			limpiaCampos();

            gl.cf_domicilio=false;

            terminaCliente();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void terminaCliente() {

        gl.parallevar=cbllevar.isChecked();
        gl.pickup = cbpickup.isChecked();

        if (gl.peInvCompart) {
            //bloqueado=true;
            //wsi.idstock="";
            //wsi.execute(rnRecibeInventario);
        } else {
            gl.ventalock=false;

            String ss=gl.gNombreCliente;

            if (!hasProducts()) {
                finish();return;
            }

            if (domicilio) {
                crearPedido();
                //msgAskOrden("Convertir a órden");
            } else {
                finish();
            }
         }
    }

    private void confirmaInventario() {
        try {
            /*
            Intent intent = new Intent(CliPos.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);

             */
        } catch (Exception e) {}
    }

    private void cargaCliente() {

        Cursor DT;

        try{
            sql="SELECT NIT FROM P_CLIENTE WHERE CODIGO_CLIENTE = " +gl.cliente_dom;
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                DT.moveToFirst();
                txtNIT.setText(DT.getString(0));
                existeCliente();
            }
        } catch (Exception e){
            mu.toast("Ocurrió un error buscando cliente");
        }
    }

    //endregion

    //region Pedidos Nube

    private void iniciaPedidos() {

        if (gl.pePedidos) {

            lblPed.setText("-");
            relped.setVisibility(View.VISIBLE);

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(ptask=new TimerTask() {
                public void run() {
                     runOnUiThread(new Runnable() {
                        @Override
                        public synchronized void run() {
                            recibePedidos();
                        }
                    });
                }
            }, delay, period);
        }

    }

    private void cancelaPedidos() {
        try {
             ptask.cancel();
        } catch (Exception e) {}
    }

    private void recibePedidos() {

        int pp;
        String fname;
        long tact,tlim,tbot;

        idleped=false;
        tact=du.getActDateTime();tlim=tact+100;tbot=du.getActDate();

        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/mposordser";
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                fname=files[i].getName();
                pp=fname.indexOf(".txt");
                if (pp>0){
                    if (!app.agregaPedido(path+"/"+fname,path+"/error/"+fname,du.getActDateTime(),fname)) {
                        msgbox2("Ocurrio error en recepción de órden :\n"+app.errstr);
                    }
                }
            }
        } catch (Exception e) {
            msgbox2("recibePedidos : "+e.getMessage());
        }

        try {
            clsD_pedidoObj D_pedidoObj=new clsD_pedidoObj(this,Con,db);
            //D_pedidoObj.fill("WHERE EMPRESA=0");
            String fsql="WHERE (ANULADO=0) AND (FECHA_ENTREGA=0) AND (FECHA_PEDIDO<="+tlim+") AND (FECHA_PEDIDO>="+tbot+") AND (FECHA_SALIDA_SUC=0) ";
            D_pedidoObj.fill(fsql);

            cantped=D_pedidoObj.count;
            lblPed.setText(""+cantped);
        } catch (Exception e) {
            msgbox2("recibePedidos : "+e.getMessage());
        }

        idleped=true;
    }

    private void hidePanel() {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                pbar.setVisibility(View.VISIBLE);
                relcli.setEnabled(false);
            }
        });
    }

    private void showPanel() {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                pbar.setVisibility(View.INVISIBLE);
                relcli.setEnabled(true);
            }
        });
    }

    //endregiong

    //region Pedidos Telefono

    private void crearPedido() {
        String ss="";

        try {
            peditems.clear();
            gl.peditems.clear();

            if (crearPedidoEncabezado()) {
                crearPedidoDetalle();

                try {
                    if(sNITCliente.isEmpty()) sNITCliente="C.F.";
                } catch (Exception e) {
                    sNITCliente="C.F.";
                }

                agregaClientePedido(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente,sTelCliente);
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());return;
        }

        try {
             db.beginTransaction();

            for (int i = 0; i <peditems.size(); i++) {
                ss=peditems.get(i);
                db.execSQL(ss);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            /*
            try  {
                db.execSQL("DELETE FROM T_VENTA");
                db.execSQL("DELETE FROM T_COMBO");
            } catch (SQLException e){
                mu.msgbox2("Error : " + e.getMessage());
            }
           */

            menuPedidos();

            if (domicilio) finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox2(e.getMessage()+" : "+ss);
            toastcentlong(e.getMessage()+" : "+ss);
        }
    }

    private boolean crearPedidoEncabezado() {

        try {
            clsD_pedidoObj D_pedidoObj=new clsD_pedidoObj(this,Con,db);
            clsClasses.clsD_pedido item = clsCls.new clsD_pedido();

            pedcorel=gl.codigo_ruta+"-"+mu.getCorelBase();

            int pedcor=D_pedidoObj.newID("SELECT MAX(EMPRESA) FROM D_PEDIDO");
            //corelorden=app.prefijoCaja(gl.codigo_ruta)+pedcor;

            item.empresa=pedcor;
            item.corel=pedcorel;
            item.fecha_sistema=du.getActDateTime();
            item.fecha_pedido=du.getActDateTime();
            item.fecha_recepcion_suc=du.getActDateTime();
            item.fecha_salida_suc=0;
            item.fecha_entrega=0;
            item.codigo_cliente=gl.codigo_cliente;
            item.codigo_direccion=0;
            item.codigo_sucursal=gl.tienda;
            item.total=gl.dom_total;
            if (gl.pelCajaRecep) item.codigo_estado=0;else item.codigo_estado=1;
            item.codigo_usuario_creo=gl.codigo_vendedor;
            item.codigo_usuario_proceso=gl.codigo_vendedor;
            item.codigo_usuario_entrego=0;
            item.anulado=0;

            String ss=D_pedidoObj.addItemSql(item);
            peditems.add(ss);

            ss=addItemSqlNoImage(item);
            //gl.peditems.add(ss);

            return true;
        } catch (Exception e) {
            String ss=e.getMessage();
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

    }

    public String addItemSqlNoImage(clsClasses.clsD_pedido item) {
        String fs=""+du.univfecha(item.fecha_sistema);

        ins.init("D_pedido");

        ins.add("EMPRESA",gl.emp);
        ins.add("COREL",item.corel);
        ins.add("FECHA_SISTEMA",fs);
        ins.add("FECHA_PEDIDO",fs);
        //ins.add("FECHA_RECEPCION_SUC",item.fecha_recepcion_suc);
        //ins.add("FECHA_SALIDA_SUC",item.fecha_salida_suc);
        //ins.add("FECHA_ENTREGA",item.fecha_entrega);
        ins.add("CODIGO_CLIENTE",item.codigo_cliente);
        ins.add("CODIGO_DIRECCION",item.codigo_direccion);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("TOTAL",item.total);
        ins.add("CODIGO_ESTADO",item.codigo_estado);
        ins.add("CODIGO_USUARIO_CREO",item.codigo_usuario_creo);
        ins.add("CODIGO_USUARIO_PROCESO",item.codigo_usuario_proceso);
        ins.add("CODIGO_USUARIO_ENTREGO",item.codigo_usuario_entrego);
        ins.add("ANULADO",item.anulado);

        return ins.sql();

    }

    private void  crearPedidoDetalle() {
        clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
        clsD_pedidodObj D_pedidodObj=new clsD_pedidodObj(this,Con,db);
        clsD_pedidocomboObj D_pedidocomboObj=new clsD_pedidocomboObj(this,Con,db);
        clsT_comboObj T_comboObj=new clsT_comboObj(this,Con,db);
        clsT_pedidodObj T_pedidodObj=new clsT_pedidodObj(this,Con,db);
        clsClasses.clsT_venta venta;
        clsClasses.clsD_pedidod item;
        clsClasses.clsD_pedidocombo combo;
        clsClasses.clsT_pedidod tpitem;
        int corid,comboid;
        String ss,pt,comid,nt;


        db.execSQL("DELETE FROM T_pedidod");
        T_ventaObj.fill();

        corid=D_pedidodObj.newID("SELECT MAX(COREL_DET) FROM D_pedidod");
        comboid=D_pedidocomboObj.newID("SELECT MAX(COREL_COMBO) FROM D_pedidocombo");

        for (int i = 0; i <T_ventaObj.count; i++) {
            venta=T_ventaObj.items.get(i);

            nt=venta.val2;
            if (!nt.isEmpty()) {
                if (nt.length()>150) nt=nt.substring(0,149);
            }

            item = clsCls.new clsD_pedidod();

            item.corel=pedcorel;
            item.corel_det=corid;
            item.codigo_producto=app.codigoProducto(venta.producto);
            item.umventa=venta.um;
            item.cant=venta.cant;
            item.total=venta.total;
            item.nota=nt;
            item.codigo_tipo_producto=app.prodTipo(item.codigo_producto);pt=item.codigo_tipo_producto;

            ss=D_pedidodObj.addItemSql(item);
            peditems.add(ss);
            gl.peditems.add(ss);

            tpitem = clsCls.new clsT_pedidod();

            tpitem.corel=" ";
            tpitem.corel_det=Integer.parseInt(venta.empresa);
            tpitem.codigo_producto=item.codigo_producto;
            tpitem.umventa=item.umventa;
            tpitem.cant=item.cant;
            tpitem.total=item.total;
            tpitem.nota=item.nota;
            tpitem.codigo_tipo_producto=item.codigo_tipo_producto;

            T_pedidodObj.add(tpitem);

            if (pt.equalsIgnoreCase("M")) {
                comid=venta.empresa;
                T_comboObj.fill("WHERE (IDCOMBO="+comid+")");

                for (int j = 0; j <T_comboObj.count; j++) {

                    combo = clsCls.new clsD_pedidocombo();

                    combo.corel_det=corid;
                    combo.corel_combo=comboid;comboid++;
                    combo.seleccion=corid;
                    combo.codigo_producto=T_comboObj.items.get(j).idseleccion;
                    combo.cant=T_comboObj.items.get(j).cant;
                    combo.nota="";

                    ss=D_pedidocomboObj.addItemSql(combo);
                    peditems.add(ss);
                    gl.peditems.add(ss);
                }
            }


            corid++;
        }
    }

    private boolean agregaClientePedido(String NIT,String Nom,String dir, String Correo,String tel) {

        try {
            clsD_pedidocObj D_pedidocObj=new clsD_pedidocObj(this,Con,db);
            clsClasses.clsD_pedidoc item = clsCls.new clsD_pedidoc();

            if (NIT.isEmpty()) NIT="C.F.";

            item.corel=pedcorel;
            item.nombre=Nom;
            item.telefono=tel;
            item.direccion=dir;
            item.referencia=Correo;
            item.nit=NIT;

            //D_pedidocObj.add(item);
            ss=D_pedidocObj.addItemSql(item);
            peditems.add(ss);
            //gl.peditems.add(ss);

            return true;
        } catch (Exception e) {
            mu.msgbox2(e.getMessage());return false;
        }
    }

    public void menuPedidos() {
        try {
            if (gl.peDomEntEnvio | gl.pePedidos) {
                gl.pedid=pedcorel;
                startActivity(new Intent(this, PedidoEnviar.class));
            } else {
                gl.closePedido = false;
                startActivity(new Intent(this, Pedidos.class));
            }
        } catch (Exception e) {
        }
    }

    //endregion

	//region Aux

	private boolean validaNIT(String N)  {
        String P, C, s, NC;
        int[] v = {0,0,0,0,0,0,0,0,0,0};
        int j, mp, sum, d11, m11, r11, cn, ll;

        if (N.isEmpty()) return false;
        if (NitValidadoInfile) return true;
        //if (!N.contains("-")) return false;

        try {
            ll = N.length();
            if (ll<5) return false;

            if (!N.contains("-")) {
                P = N.substring(0,ll-1);
                C = N.substring(ll-1,ll);
                N=P+"-"+C;
            }

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

            ll = ll - 1; sum = 0;

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
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
        return true;

	}

    private boolean validaNITHon(String N)  {
        return true;
    }

    private void buscaCliente() {

		Cursor DT;

		try{

			String NIT=txtNIT.getText().toString();

			if (mu.emptystr(NIT)) {
				txtNIT.requestFocus();return;
			}

			int nnit=nitnum(NIT);

			sql="SELECT Nombre,Direccion FROM P_CLIENTE " +
				"WHERE  NIT = '"+NIT+"'";

			DT=Con.OpenDT(sql);
			DT.moveToFirst();

			if (nnit==0) {
				throw new Exception("NIT no es válido");
			}

			txtNom.setText(DT.getString(0));
			txtRef.setText(DT.getString(1));

            if (DT!=null) DT.close();

			procesaNIT(NIT);

            if (DT!=null) DT.close();
		} catch (Exception e){
		    txtNom.setText("");txtRef.setText("");
		}

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

				sql="SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO,TIPO_CONTRIBUYENTE,CODIGO_CLIENTE, EMAIL,TELEFONO FROM P_CLIENTE " +
					"WHERE NIT = '" + NIT + "'";
				DT=Con.OpenDT(sql);

				if (DT != null){

					if (DT.getCount()>0){

						DT.moveToFirst();

						txtNom.setText(DT.getString(1));
						txtRef.setText(DT.getString(2));
						txtCorreo.setText(DT.getString(8));
                        txtTel.setText(DT.getString(9));

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
                        gl.cliente_dom=gl.codigo_cliente;

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

	private boolean agregaCliente(String NIT,String Nom,String dir, String Correo,String tel) {

        if (consFinal) {
            gl.codigo_cliente = 10*gl.emp;
            gl.cliente_dom=gl.codigo_cliente;
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

    private boolean actualizaCliente(String NIT,String Nom,String dir, String Correo,String tel) {

        int codigo=nitnum(NIT);

        gl.codigo_cliente=codigo;

        if (consFinal) return true;
        if (gl.codigo_cliente==10*gl.emp) return true;

        try {

            upd.init("P_CLIENTE");
            upd.add("NOMBRE",Nom);
            upd.add("DIRECCION",dir);
            upd.add("EMAIL",Correo);
            upd.add("TELEFONO",tel);
            upd.Where("CODIGO_CLIENTE="+codigo);
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

            if (pp<=0){
                nnit=Integer.parseInt(nit);
            }else{
                int A=(int) nit.charAt(pp+1);
                String snit=nit.substring(0,pp)+A;
                nnit=Integer.parseInt(snit);
            }

            if (nnit<=0) nnit=gl.emp*10;

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

    private boolean hasProducts(){
        Cursor dt;

        try {
            sql="SELECT PRODUCTO FROM T_VENTA";
            dt=Con.OpenDT(sql);

            int i=dt.getCount();
            if (dt!=null) dt.close();
            return i>0;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }

    private boolean esCF(String ss) {
        String N=ss.toUpperCase();

        if (N.equalsIgnoreCase("CF")) N="C.F.";
        if (N.equalsIgnoreCase("C/F")) N="C.F.";
        if (N.equalsIgnoreCase("C.F")) N="C.F.";
        if (N.equalsIgnoreCase("CF.")) N="C.F.";

        return N.equalsIgnoreCase("C.F.");
    }

    public int clientePorNIT(String snit) {
        Cursor DT;

        try {
            sql ="SELECT codigo_cliente FROM P_CLIENTE WHERE (NIT='"+snit+"')  COLLATE NOCASE";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            return DT.getInt(0);
        } catch (Exception e) {
            return 0;
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

    //region Dialogos

    private void msgAskCF(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Consumidor final");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    consFinal=true;
                    if (agregaCliente("C.F.","Consumidor final","Ciudad","","")) procesaCF() ;
                } catch (Exception e) {
                    msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskOrden(String msg) {

        if (!hasProducts()) {
            finish();return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Orden para llevar");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                crearPedido();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Orden a domicilio");
        dialog.setMessage( msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });

        dialog.show();
    }

    private boolean NitValidadoInfile =false;

    private void consultaNITInfile() {
        String nc;
        nrslt=false;
        NitValidadoInfile = false;

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

            RequestQueue queue = Volley.newRequestQueue(CliPos.this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlNit, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (!response.getString("nombre").equals("")) {
                            txtNom.setText(response.getString("nombre").replace(",", " ").trim());
                            nrslt=true;
                            NitValidadoInfile= true;
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

    //region Activity Events

    @Override
    protected void onResume() {
        try{
            super.onResume();

            if (gl.pePedidos) iniciaPedidos();

            if (browse==1) {
                browse=0;
                if (!gl.cliente.isEmpty()) {
                    if (domicilio) {
                        txtNIT.setText(gl.gNITCliente);
                        existeCliente();
                    } else {
                        //terminaCliente();
                        txtNIT.setText(gl.gNITCliente);
                        existeCliente();
                    }
                }
                return;
            }

            if (browse==2) {
                browse=0;
                if (!gl.dom_ddir.isEmpty()) txtRef.setText(gl.dom_ddir);return;
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    @Override
    protected void onPause() {
        if (gl.pePedidos) cancelaPedidos();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}
