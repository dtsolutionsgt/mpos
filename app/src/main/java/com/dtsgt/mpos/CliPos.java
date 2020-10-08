package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInventCompartido;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CliPos extends PBase {

	private EditText txtNIT,txtNom,txtRef, txtCorreo;
	private TextView lblPed;
    private RelativeLayout relped,relcli;
	private ProgressBar pbar;

    private wsInventCompartido wsi;

    private Runnable rnRecibeInventario;

    private ArrayList<String> pedidos =new ArrayList<String>();

	private String sNITCliente, sNombreCliente, sDireccionCliente, sCorreoCliente, wspnerror;
	private boolean consFinal=false,idleped=true;
	private boolean request_exit=false,bloqueado;
	private int cantped;

    private TimerTask ptask;
    private int period=10000,delay=50;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cli_pos);

		super.InitBase();
		addlog("CliPos",""+du.getActDateTime(),String.valueOf(gl.vend));

		txtNIT = (EditText) findViewById(R.id.txt1);txtNIT.setText("");txtNIT.requestFocus();
		txtNom = (EditText) findViewById(R.id.editText2);txtNom.setText("");
		txtRef = (EditText) findViewById(R.id.editText1);txtRef.setText("Ciudad");
        txtCorreo= (EditText) findViewById(R.id.txtCorreo);txtCorreo.setText("");
        lblPed = (TextView) findViewById(R.id.textView177);lblPed.setText("");
        relped = (RelativeLayout) findViewById(R.id.relPed);relped.setVisibility(View.INVISIBLE);
        relcli = (RelativeLayout) findViewById(R.id.relclipos);
        pbar = (ProgressBar) findViewById(R.id.progressBar4);pbar.setVisibility(View.INVISIBLE);

        setHandlers();

        getURL();
        wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        gl.pedcorel="";

        bloqueado=false;

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

	}

    public interface ExtRunnable extends Runnable {
        public void run(String data);
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

				if (agregaCliente(sNITCliente, sNombreCliente, sDireccionCliente,sCorreoCliente)) procesaNIT(sNITCliente);

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

    public void doWspnError(View view) {
        toast(wspnerror);
    }

	private void setHandlers() {

        rnRecibeInventario = new Runnable() {
            public void run() {
                bloqueado=false;
                pbar.setVisibility(View.INVISIBLE);
                if (wsi.errflag) {
                    msgbox2("wsi"+wsi.error);
                } else {
                    confirmaInventario();
                    gl.ventalock=false;
                    finish();
                }
            }
        };

		try{

            /*txtNIT.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                     buscaCliente();
                }
            });*/

			txtNIT.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
						if (!existeCliente()) txtNom.requestFocus();
						return true;
					} else {
                        existeCliente();
   					    return false;
					}
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
            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			consFinal=false;
			limpiaCampos();

            terminaCliente();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void procesaNIT(String snit) {

        int codigo=nitnum(snit);

		try {

			gl.rutatipo="V";
			gl.cliente=""+codigo;
			gl.nivel=gl.nivel_sucursal;
            gl.percepcion=0;
            gl.contrib="";
            gl.scancliente = gl.cliente;

            gl.gNombreCliente = sNombreCliente;
            gl.gNITCliente =snit;
            gl.gDirCliente = sDireccionCliente;
            gl.gCorreoCliente = sCorreoCliente;

            gl.media=1;

			//Intent intent = new Intent(this,Venta.class);
			//startActivity(intent);

			limpiaCampos();

            terminaCliente();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void terminaCliente() {

        if (gl.peInvCompart) {
            bloqueado=true;
            wsi.idstock="";
            wsi.execute(rnRecibeInventario);
        } else {
            gl.ventalock=false;
            finish();
        }
    }

    private void confirmaInventario() {
        try {
            Intent intent = new Intent(CliPos.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);
        } catch (Exception e) {}
    }

    //endregion

    //region Pedidos

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
                        msgbox2("Ocurrio error en recepción de orden :\n"+app.errstr);
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

    //endregion

	//region Aux

	private boolean validaNIT(String N)  {

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
                //mu.msgbox(NC);

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
            nit=nit.toUpperCase();
            pp=nit.indexOf("-");
            if (pp<0) return 0;

            int A=(int) nit.charAt(pp+1);
            String snit=nit.substring(0,pp)+A;
            int nnit=Integer.parseInt(snit);

            return nnit;
        } catch (Exception e) {
            return -1;
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
        gl.timeout = 20000;

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 20000; else gl.timeout = Integer.valueOf(line);
                myReader.close();
            }
        } catch (Exception e) {}

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
                if (!gl.cliente.isEmpty()) terminaCliente();
                return;
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
