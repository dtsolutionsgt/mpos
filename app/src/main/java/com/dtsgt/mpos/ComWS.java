package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsDataBuilder;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsExport;
import com.dtsgt.classes.clsFinDia;
import com.dtsgt.classes.clsLicence;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_bancoObj;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_descuentoObj;
import com.dtsgt.classes.clsP_empresaObj;
import com.dtsgt.classes.clsP_factorconvObj;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_monedaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodopcObj;
import com.dtsgt.classes.clsP_prodopclistObj;
import com.dtsgt.classes.clsP_prodprecioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.ladapt.ListAdaptEnvio;

import org.apache.commons.lang.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class ComWS extends PBase {

	private TextView lblInfo, lblParam, lblRec, lblEnv, lblExis,lblEnvM;
	private ProgressBar barInfo;
	private EditText txtWS, txtEmp;
	private ImageView imgRec, imgEnv, imgExis,imgEnvM;
	private RelativeLayout ralBack;
    private TextView lblUser,lblPassword,txtVersion;
    private EditText txtUser, txtPassword;

	private int isbusy, fecha, lin, reccnt, ultcor, ultcor_ant, licResult, licResultRuta;
	private String emp, ActRuta, mac,rootdir,err, ruta, rutatipo, sp, docstock, ultSerie, ultSerie_ant,rrs;
	private String licSerial,licRuta,licSerialEnc,licRutaEnc,parImprID,acterr,actproc;
	private boolean fFlag, showprogress, pendientes, envioparcial, findiaactivo, errflag,esEnvioManual=false;

	private SQLiteDatabase dbT;
	private BaseDatos ConT;
	private BaseDatos.Insert insT;
	private AppMethods clsAppM;
	private clsExport exp;

	private ArrayList<String> listItems = new ArrayList<String>();
	private ArrayList<String> results = new ArrayList<String>();

	private ArrayList<clsClasses.clsEnvio> items = new ArrayList<clsClasses.clsEnvio>();
	private ListAdaptEnvio adapter;

	private clsDataBuilder dbld;
	private clsLicence lic;
	private clsFinDia claseFindia;
    private DateUtils DU;
    private String jsonWS;
	private CryptUtil cu=new CryptUtil();

	protected PowerManager.WakeLock wakeLock;

	// Web Service -

	public AsyncCallRec wsRtask;
	public AsyncCallSend wsStask;
	public AsyncCallConfirm wsCtask;

	private static String sstr, fstr, fprog, finf, ferr, fterr, idbg, dbg, ftmsg, esql, ffpos;
	private int scon, running, pflag, stockflag, conflag,sendmode;
	private String ftext, slsync, senv;
	private String fsql, fsqli, fsqlf, strliqid;
	private boolean  ftflag, esvacio, liqid;

	private final String NAMESPACE = "http://tempuri.org/";
	private String METHOD_NAME, URL;

	protected PowerManager.WakeLock wakelock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_com_ws);

		super.InitBase();
		addlog("ComWS", "" + du.getActDateTime(), gl.vend);

		System.setProperty("line.separator", "\r\n");
        rootdir=Environment.getExternalStorageDirectory()+"/RoadFotos/";

        ruta=gl.ruta;
        ActRuta = ruta;
        emp = "3";
        rutatipo = gl.rutatipog;

		dbld = new clsDataBuilder(this);
		claseFindia = new clsFinDia(this);
		clsAppM = new AppMethods(this, gl, Con, db);
		exp =new clsExport(Con,emp);

		lblInfo = (TextView) findViewById(R.id.lblETipo);
		lblParam = (TextView) findViewById(R.id.lblProd);
		txtVersion = (TextView) findViewById(R.id.txtVersion);
		barInfo = (ProgressBar) findViewById(R.id.progressBar2);
		txtWS = (EditText) findViewById(R.id.txtWS);txtWS.setEnabled(false);
		txtEmp = (EditText) findViewById(R.id.txtEmp);txtEmp.setEnabled(false);

		lblRec = (TextView) findViewById(R.id.btnRec);
		lblEnv = (TextView) findViewById(R.id.btnSend);
        lblEnvM = (TextView) findViewById(R.id.btnSenHand);

		imgEnv = (ImageView) findViewById(R.id.imageView6);
        imgEnvM = (ImageView) findViewById(R.id.imageView34);
		imgRec = (ImageView) findViewById(R.id.imageView5);

		ralBack = (RelativeLayout) findViewById(R.id.relwsmail);

		isbusy = 0;

		lblInfo.setText("");
		lblParam.setText("");
		barInfo.setVisibility(View.INVISIBLE);

        lblUser = new TextView(this,null);
        lblPassword = new TextView(this,null);
		//txtVersion=new TextView(this, null);

        txtUser = new EditText(this,null);
        txtPassword = new EditText(this,null);

		txtVersion.setText("14-Junio-2019");

		this.setTitle("Comunicación");

		licSerial=gl.deviceId;
		licRuta=ruta;

		/*
		try {
			licSerialEnc=cu.encrypt(licSerial);
			licRutaEnc=cu.encrypt(licRuta);
		} catch (Exception e) {
			licSerialEnc="";
			licRutaEnc="";
		}
        */

		mac = getMac();
		fsql = du.univfechasql(du.getActDate());
		fsqli = du.univfechasql(du.ffecha00(du.getActDate())) + " 00:00:00";
		fsqlf = du.univfechasql(du.ffecha24(du.getActDate())) + " 23:59:59";

		pendientes = validaPendientes();
		envioparcial = gl.peEnvioParcial;

		setHandlers();

        URL = gl.urlglob;
        URL="http://192.168.1.52/wsmpos/wsAndr.asmx";
        URL="http://192.168.1.94/mpos/wsMpos.asmx";
        txtWS.setText(URL);txtEmp.setText(emp);
        if (gl.debug) {
            txtWS.setEnabled(true);txtEmp.setEnabled(true);
        }
	}

	//region Events

	public void askRec(View view) {

		if (isbusy == 1) {
			toastcent("Por favor, espere que se termine la tarea actual.");return;
		}

		licSerial=gl.deviceId;
		licRuta=ruta;

		try {
			licSerialEnc=cu.encrypt(licSerial);
			licRutaEnc=cu.encrypt(licRuta);
		} catch (Exception e) {
			licSerialEnc="";
			licRutaEnc="";
		}

        msgAskConfirmaRecibido();
	}

	public void askSend(View view) {

		try {
			if (isbusy == 1) {
				toastcent("Por favor, espere que se termine la tarea actual.");
				return;
			}

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Envio");
			dialog.setMessage("¿Enviar datos?");

			dialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
                    sendmode=0;
					runSend();
				}
			});

			dialog.setNegativeButton("Cancelar", null);

			dialog.show();
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

    public void askActualize(View view) {
        try {
            if (isbusy == 1) {
                toastcent("Por favor, espere que se termine la tarea actual.");
                return;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Actualización");
            dialog.setMessage("¿Actualizar catalogos?");

            dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sendmode=1;
                    runSend();
                }
            });

            dialog.setNegativeButton("Cancelar", null);

            dialog.show();

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

	public void doFotos(View view) {
		startActivity(new Intent(this,ComWSFotos.class));
	}

	private void setHandlers() {
		ralBack.setOnTouchListener(new SwipeListener(this) {
			public void onSwipeRight() {
				onBackPressed();
			}

			public void onSwipeLeft() {
			}
		});
	}

	//endregion

	//region Main

	private void runRecep() {

		try {
			if (isbusy == 1) return;

			if (!setComParams()) return;

			try{
				//#CKFK 20190313 Agregué esto para ocultar el teclado durante la carga de los datos
				View view = this.getCurrentFocus();
				view.clearFocus();
				if (view != null) {
					keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}catch (Exception e){

			}

			isbusy = 1;

			if (!esvacio) {
				ultcor_ant = ultCorel();
				ultSerie_ant = ultSerie();
			}

			barInfo.setVisibility(View.VISIBLE);
			barInfo.invalidate();
			lblInfo.setText("Iniciando proceso de carga..");

			lblInfo.setText("Conectando ...");

			wsRtask = new AsyncCallRec();
			wsRtask.execute();

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void runSend() {

		try {
			if (isbusy == 1) return;

			if (!setComParams()) return;

			isbusy = 1;

			barInfo.setVisibility(View.VISIBLE);
			barInfo.invalidate();
			lblInfo.setText("Conectando ...");

			showprogress = true;
			wsStask = new AsyncCallSend();
			wsStask.execute();

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void runExist() {
		try {
			super.finish();
			startActivity(new Intent(this, ComWSExist.class));
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void runPrecios() {
		try {
			super.finish();
			startActivity(new Intent(this, ComWSPrec.class));
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void runRecarga() {
		try {
			super.finish();
			startActivity(new Intent(this, ComWSRec.class));
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	public void writeData(View view) {

		try {
			dbld.clear();
			dbld.insert("D_PEDIDO", "WHERE 1=1");
			dbld.insert("D_PEDIDOD", "WHERE 1=1");
			dbld.save();
			dbld.saveArchivo(du.getActDateStr());
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
		}


	}

	private boolean validaPendientes() {
		int pend = 0;

		sp = "";

		try {

			pend = pend + getDocCount("SELECT IFNULL(COUNT(COREL),0) FROM D_FACTURA WHERE STATCOM<>'S'", "Fact: ");
			pend = pend + getDocCount("SELECT IFNULL(COUNT(COREL),0) FROM D_PEDIDO WHERE STATCOM<>'S'", "Ped: ");
			pend = pend + getDocCount("SELECT IFNULL(COUNT(COREL),0) FROM D_COBRO WHERE STATCOM<>'S'", "Rec: ");
			pend = pend + getDocCount("SELECT IFNULL(COUNT(COREL),0) FROM D_DEPOS WHERE STATCOM<>'S'", "Dep: ");
			pend = pend + getDocCount("SELECT IFNULL(COUNT(COREL),0) FROM D_MOV WHERE STATCOM<>'S'", "Inv : ");

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
		}

		return pend > 0;
	}

	//endregion

	//region Common Web Service Methods

	public int fillTable(String value, String delcmd) {

		int rc;
		String s, ss;

		METHOD_NAME = "getIns";

		sstr = "OK";

		try {

			idbg = idbg + " filltable ";

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("SQL");
			param.setValue(value);

			request.addProperty(param);
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapObject resSoap = (SoapObject) envelope.getResponse();
			SoapObject result = (SoapObject) envelope.bodyIn;

			rc = resSoap.getPropertyCount() - 1;
			idbg = idbg + " rec " + rc + "  ";

			s = "";
			if (delcmd.equalsIgnoreCase("DELETE FROM P_STOCK")) {
				if (rc == 1) {
					stockflag = 0;//return 1;
				} else {
					stockflag = 1;
				}
			}

			// if (delcmd.equalsIgnoreCase("DELETE FROM P_COBRO")) {
			// 	idbg=idbg+" RC ="+rc+"---";
			//}


			for (int i = 0; i < rc; i++) {
				String str = "";
				try {
					str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);
					//s=s+str+"\n";
				} catch (Exception e) {
					mu.msgbox("error: " + e.getMessage());
				}

				if (i == 0) {

					idbg = idbg + " ret " + str + "  ";

					if (str.equalsIgnoreCase("#")) {
						listItems.add(delcmd);
					} else {
						idbg = idbg + str;
						ftmsg = ftmsg + "\n" + str;
						ftflag = true;
						sstr = str;
						return 0;
					}
				} else {
					try {
						sql = str;
						listItems.add(sql);
						sstr = str;
					} catch (Exception e) {
						addlog(new Object() {
						}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
						sstr = e.getMessage();
					}
				}
			}

			return 1;
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			//#EJC20190226: Evitar que se muestre OK después del nombre de la tabla cuando da error de timeOut.
			sstr = e.getMessage();
			idbg = idbg + " ERR " + e.getMessage();
			return 0;
		}
	}

	public int fillTableImpresora() {

		int rc;
		String s, ss, delcmd="DELETE FROM P_IMPRESORA";

		METHOD_NAME = "getInsImpresora";

		sstr = "OK";

		try {

			idbg = idbg + " filltableImpresora ";

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			/*
			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("SQL");
			param.setValue(value);
			request.addProperty(param);
			*/
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapObject resSoap = (SoapObject) envelope.getResponse();
			SoapObject result = (SoapObject) envelope.bodyIn;

			rc = resSoap.getPropertyCount() - 1;
			idbg = idbg + " rec " + rc + "  ";

			s = "";

			for (int i = 0; i < rc; i++) {
				String str = "";
				try {
					str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);
					//s=s+str+"\n";
				} catch (Exception e) {
					mu.msgbox("error: " + e.getMessage());
				}

				if (i == 0) {

					idbg = idbg + " ret " + str + "  ";

					if (str.equalsIgnoreCase("#")) {
						listItems.add(delcmd);
					} else {
						idbg = idbg + str;
						ftmsg = ftmsg + "\n" + str;
						ftflag = true;
						sstr = str;
						return 0;
					}
				} else {
					try {
						sql = str;
						listItems.add(sql);
						sstr = str;
					} catch (Exception e) {
						addlog(new Object() {
						}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
						sstr = e.getMessage();
					}
				}
			}

			return 1;
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			sstr = e.getMessage();
			idbg = idbg + " ERR " + e.getMessage();
			return 0;
		}
	}

	public int commitSQL() {
		int rc;
		String s, ss;

		int vCommit=0;

		METHOD_NAME = "Commit";
		sstr = "OK";

		if (dbld.size() == 0) vCommit =1;//return 1

		s = "";
		for (int i = 0; i < dbld.size(); i++) {
			ss = dbld.items.get(i);
			s = s + ss + "\n";
		}
		/*
		if (showprogress) {
			fprog = "Enviando ...";
			wsStask.onProgressUpdate();
		}*/

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("SQL");
			param.setValue(s);

			request.addProperty(param);
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			s = response.toString();

			sstr = "#";
			if (s.equalsIgnoreCase("#")) vCommit =1;// return 1;

			sstr = s;
			//return 0;

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			sstr = e.getMessage();
			vCommit=0;
		}

		return vCommit;
	}

    public int actualize() {
	    int rc,vCommit=0;

        METHOD_NAME = "Actualize";sstr = "OK";
        if (dbld.size() == 0) return 1;

        s = "";
        for (int i = 0; i < dbld.size(); i++) {
            ss = dbld.items.get(i);
            ss.replace("#","");
            s = s + ss + "#";
        }

        /*
        if (showprogress) {
            fprog = "Enviando ...";
            wsStask.onProgressUpdate();
        }*/

        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            PropertyInfo param = new PropertyInfo();
            param.setType(String.class);
            param.setName("SQL");
            param.setValue(s);

            request.addProperty(param);
            envelope.setOutputSoapObject(request);

            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(NAMESPACE + METHOD_NAME, envelope);

            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            s = response.toString();

            sstr = "#";
            if (s.equalsIgnoreCase("#")) vCommit =1;// return 1;

            sstr = s;
            //return 0;

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            sstr = e.getMessage();
            vCommit=0;
        }

        return vCommit;
    }

    public int OpenDTt(String sql) {
		int rc;

		METHOD_NAME = "OpenDT";

		results.clear();

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("SQL");
			param.setValue(sql);

			request.addProperty(param);
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapObject resSoap = (SoapObject) envelope.getResponse();
			SoapObject result = (SoapObject) envelope.bodyIn;

			rc = resSoap.getPropertyCount() - 1;

			for (int i = 0; i < rc + 1; i++) {
				String str = ((SoapObject) result.getProperty(0)).getPropertyAsString(i);

				if (i == 0) {
					sstr = str;
					if (!str.equalsIgnoreCase("#")) {
						sstr = str;
						return 0;
					}
				} else {
					results.add(str);
				}
			}

			return 1;
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			sstr = e.getMessage();
		}

		return 0;

	}

	public int getTest() {

		METHOD_NAME = "TestWS";

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("Value");
			param.setValue("OK");

			request.addProperty(param);
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);

			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			sstr = response.toString() + "..";

			return 1;
		} catch (Exception e) {
			sstr = e.getMessage();
		}

		return 0;
	}

	public int envioFachada() {
		String METHOD_NAME = "GuardaFachada";
		s = "";

		fprog = "Enviando ...";
		wsStask.onProgressUpdate();

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("JSONFachadas");
			param.setValue(jsonWS);

			request.addProperty(param);
			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			s = response.toString();

			sstr = "#";
			if (s.equalsIgnoreCase("#")) return 1;

			sstr = s;
			return 0;
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
			sstr = e.getMessage();
		}

		return 0;

	}

	public int checkLicence(String serial) {
		int rc;
		String s, ss;

		METHOD_NAME = "checkLicence";
		sstr = "OK";

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("Serial");
			param.setValue(serial);
			request.addProperty(param);

			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			s = response.toString();
			if (s.equalsIgnoreCase("1")) return 1;

			sstr = s;
			return 0;
		} catch (Exception e) {
			sstr = e.getMessage();
		}

		return 0;
	}

	public int checkLicenceRuta(String ruta) {
		int rc;
		String s, ss;

		METHOD_NAME = "checkLicenceRuta";
		sstr = "OK";

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("Ruta");
			param.setValue(ruta);
			request.addProperty(param);

			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			s = response.toString();
			if (s.equalsIgnoreCase("1")) return 1;

			sstr = s;
			return 0;
		} catch (Exception e) {
			sstr = e.getMessage();
		}

		return 0;
	}

	public int guardaImagen(String idprod) {
		int rc;
		String s, ss,resstr;

		METHOD_NAME = "getImage";
		sstr = "OK";

		try {

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			PropertyInfo param = new PropertyInfo();
			param.setType(String.class);
			param.setName("idprod");
			param.setValue(idprod);
			request.addProperty(param);

			envelope.setOutputSoapObject(request);

			HttpTransportSE transport = new HttpTransportSE(URL);
			transport.call(NAMESPACE + METHOD_NAME, envelope);

			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

			resstr = response.toString();

			try {
				//byte[] imgbytes = resstr.getBytes();

				byte[] imgbytes=Base64.decode(resstr, Base64.DEFAULT);

				int bs=imgbytes.length;

				FileOutputStream fos = new FileOutputStream(rootdir+"0006.jpg");
                BufferedOutputStream outputStream = new BufferedOutputStream(fos);
                outputStream.write(imgbytes);
                outputStream.close();

			} catch (Exception ee) {
				sstr = ee.getMessage();return 0;
			}

			sstr =""+resstr.length();
			return resstr.length();
		} catch (Exception e) {
			sstr = e.getMessage();
		}

		return 0;
	}

	//endregion

	//region WS Recepcion Methods

	private boolean getData() {
		Cursor DT;
		BufferedWriter writer = null;
		FileWriter wfile;
		int rc, scomp, prn, jj;
		String s, val = "";

		try {

			String fname = Environment.getExternalStorageDirectory() + "/roadcarga.txt";
			wfile = new FileWriter(fname, false);
			writer = new BufferedWriter(wfile);

			//db.execSQL("DELETE FROM P_LIQUIDACION");

			sql = "SELECT VALOR FROM P_PARAMEXT WHERE ID=2";
			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				val = DT.getString(0);
			} else {
				val = "N";
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			val = "N";
		}

		if (val.equalsIgnoreCase("S")) gl.peStockItf = true;
		else gl.peStockItf = false;

		listItems.clear();
		scomp = 0;idbg = "";stockflag = 0;ftmsg = "";ftflag = false;rrs="...";

		try {
            if (!AddTable("P_EMPRESA")) return false;
			if (!AddTable("P_RUTA")) return false;
			if (!AddTable("P_CLIENTE")) return false;
			if (!AddTable("P_PRODUCTO")) return false;
            if (!AddTable("P_FACTORCONV")) return false;
            if (!AddTable("P_LINEA")) return false;
            if (!AddTable("P_PRODPRECIO")) return false;
    		if (!AddTable("P_DESCUENTO")) return false;
  			if (!AddTable("P_SUCURSAL")) return false;
            if (!AddTable("P_BANCO")) return false;
            if (!AddTable("P_NIVELPRECIO")) return false;
            if (!AddTable("P_COREL")) return false;
			if (!AddTable("P_MEDIAPAGO")) return false;
			if (!AddTable("P_IMPUESTO")) return false;
			if (!AddTable("VENDEDORES")) return false;
            if (!AddTable("P_ARCHIVOCONF")) return false;
            if (!AddTable("P_ENCABEZADO_REPORTESHH")) return false;
            if (!AddTable("P_BONIF")) return false;
            if (!AddTable("P_PARAMEXT")) return false;
            if (!AddTable("P_MONEDA")) return false;
            if (!AddTable("P_PROVEEDOR")) return false;

            if (!AddTable("P_PRODMENU")) return false;
            if (!AddTable("P_PRODOPC")) return false;
            if (!AddTable("P_PRODOPCLIST")) return false;

           //procesaParamsExt();

            /*
           	//if (!AddTable("P_CLIENTE_FACHADA")) return false;
 			//if (!AddTable("P_STOCKINV")) return false;
            //if (!AddTable("P_CLIDIR")) return false;
            //if (!AddTable("TMP_PRECESPEC")) return false;
		    //if (!AddTable("P_STOCK_APR")) return false;
			//if (!AddTable("P_STOCK")) return false;
			//if (!AddTable("P_STOCKB")) return false;
			//if (!AddTable("P_STOCK_PALLET"))
			//if (!AddTable("P_COBRO")) return false;
			//if (!AddTable("P_CLIGRUPO")) return false;
            //if (!AddTable("P_TRANSERROR")) return false;
            //if (!AddTable("P_PRODGRUP")) return false;
            //if (!AddTable("P_VEHICULO")) return false;
           	//if (!AddTable("P_PORCMERMA")) return false;
           	//if (!AddTable("P_REF1")) return false;
			//if (!AddTable("P_REF2")) return false;
			//if (!AddTable("P_REF3")) return false;
			//if (!AddTable("P_CODATEN")) return false;
			//if (!AddTable("P_CODDEV")) return false;
			//if (!AddTable("P_CODNOLEC")) return false;
			//if (!AddTable("P_MUNI")) return false;
 			//if (!AddTable("P_HANDHELD")) return false;
			//if (!AddTable("P_CLIRUTA")) return false;
			//if (!AddTable("O_RUTA")) return false;
			//if (!AddTable("O_COBRO")) return false;
			//if (!AddTable("O_PROD")) return false;
			//if (!AddTable("O_LINEA")) return false;
			//if (!AddTable("P_BONLIST")) return false;
			//if (!AddTable("P_CORELNC")) return false;
			//if (!AddTable("P_CORRELREC")) return false;
			//if (!AddTable("P_CORREL_OTROS")) return false;


            //fillTableImpresora();
            */

			//licResult=checkLicence(licSerial);
			//licResultRuta=checkLicenceRuta(licRuta);



		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			return false;
		}

		ferr = "";

		try {

			rc = listItems.size();
			reccnt = rc;
			if (rc == 0) return true;

			fprog = "Procesando ...";
			wsRtask.onProgressUpdate();

			ConT = new BaseDatos(this);
			dbT = ConT.getWritableDatabase();
			ConT.vDatabase = dbT;
			insT = ConT.Ins;

			prn = 0;jj = 0;
			Log.d("M", "So far so good");

			dbT.beginTransaction();

			for (int i = 0; i < rc; i++) {

				sql = listItems.get(i);esql = sql;
				sql = sql.replace("INTO P_RAZONNOSCAN", "INTO P_CODNOLEC");
                sql = sql.replace("INTO P_ENCABEZADO_REPORTESHH_II", "INTO P_ENCABEZADO_REPORTESHH");

				try {
					//writer.write(sql);writer.write("\r\n");
				} catch (Exception e) {}

				try {
					dbT = ConT.getWritableDatabase();
					dbT.execSQL(sql);
				} catch (Exception e) {
					Log.d("M", "Something happend there " + e.getMessage());
					addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(),sql);
				}

				try {

					if (i % 10 == 0) {
						fprog = "Procesando: " + i + " de: " + (rc - 1);
						wsRtask.onProgressUpdate();
						SystemClock.sleep(20);
					}
				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					Log.e("z", e.getMessage());
				}
			}

			fprog = "Procesando: " + (rc - 1) + " de: " + (rc - 1);
			wsRtask.onProgressUpdate();

			Actualiza_FinDia();
			//encodePrinters();
			//encodeLicence();
			//encodeLicenceRuta();

			//fechaCarga();

            SetStatusRecToTrans("1");

			dbT.setTransactionSuccessful();
			dbT.endTransaction();

			fprog = "Documento de inventario recibido en BOF...";
			wsRtask.onProgressUpdate();

			//Actualiza_Documentos();

			fprog = "Fin de actualización";
			wsRtask.onProgressUpdate();

			scomp = 1;

			try {
				ConT.close();
			} catch (Exception e) {
				//addlog(new Object() {	}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			}

			try {
				writer.close();
			} catch (Exception e) {
				//addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
				//msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
			}

			return true;

		} catch (Exception e) {
			addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fprog = "Actualización incompleta";
			wsRtask.onProgressUpdate();

			try {
				ConT.close();
			} catch (Exception ee) {
				addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			}

			sstr = e.getMessage();ferr = sstr + "\n" + sql;esql = sql;
			return false;
		}

	}

	private void procesaParamsExt() {
		Cursor dt;
		String sql, val = "";
		int ival, rc;

		try {

			rc = listItems.size();
			reccnt = rc;
			if (rc == 0) return;

			ConT = new BaseDatos(this);
			dbT = ConT.getWritableDatabase();
			ConT.vDatabase = dbT;
			insT = ConT.Ins;

			dbT.beginTransaction();

			for (int i = 0; i < rc; i++) {

				sql = listItems.get(i);
				esql = sql;
				dbT.execSQL(sql);

				try {
					if (i % 10 == 0) {

						SystemClock.sleep(20);
					}
				} catch (Exception e) {
					Log.e("z", e.getMessage());
				}
			}

			dbT.setTransactionSuccessful();
			dbT.endTransaction();

			try {
				sql = "SELECT VALOR FROM P_PARAMEXT WHERE ID=2";
				dt = Con.OpenDT(sql);
				dt.moveToFirst();
				val = dt.getString(0);
			} catch (Exception e) {
				val = "N";
			}
			if (val.equalsIgnoreCase("S")) gl.peStockItf = true;
			else gl.peStockItf = false;

			try {
				sql = "SELECT VALOR FROM P_PARAMEXT WHERE ID=3";
				dt = Con.OpenDT(sql);
				dt.moveToFirst();
				gl.peModal = dt.getString(0).toUpperCase();
			} catch (Exception e) {
				gl.peModal = "-";
			}

			try {
				ConT.close();
			} catch (Exception e) {
			}

		} catch (Exception e) {
			try {
				ConT.close();
			} catch (Exception ee) {
			}
		}

	}

	private boolean Actualiza_FinDia() {

		Cursor DT1;
		int vCorelZ = 0;
		float vGrandTotal = 0;

		boolean vActualizaFD = true;

		try {

			sql = "SELECT CORELZ, GRANDTOTAL FROM P_HANDHELD";
			DT1 = ConT.OpenDT(sql);

			if (DT1.getCount() > 0) {

				DT1.moveToFirst();

				vCorelZ = DT1.getInt(0);
				vGrandTotal = DT1.getFloat(1);

				sql = "UPDATE FINDIA SET COREL = " + vCorelZ + ", VAL1=0, VAL2=0, VAL3=0, VAL4=0,VAL5=0, VAL6=0, VAL7=0, VAL8 = " + vGrandTotal;
				dbT.execSQL(sql);
			}

			DT1.close();

		} catch (Exception ex) {
			vActualizaFD = false;
		}

		return vActualizaFD;

	}

	private boolean update_Corel_GrandTotal() {

		Cursor DT1;
		int vCorelZ = 0;
		float vGrandTotal = 0;
        String vNumPlaca;

		boolean vActualizaFD = true;

		try {

			sql = "SELECT NUMPLACA, CORELZ, GRANDTOTAL FROM P_HANDHELD";
			DT1 = Con.OpenDT(sql);

			if (DT1.getCount() > 0) {

				DT1.moveToFirst();

                vNumPlaca = DT1.getString(0);
				vCorelZ = DT1.getInt(1);
				vGrandTotal = DT1.getFloat(2);

				sql = "UPDATE P_HANDHELD SET CORELZ = " + vCorelZ + ", GRANDTOTAL = " + vGrandTotal + " WHERE NUMPLACA = '" + vNumPlaca + "'";
				dbld.add(sql);
			}

			DT1.close();

		} catch (Exception ex) {
			vActualizaFD = false;
			fstr=ex.getMessage();
		}

		return vActualizaFD;

	}

	private boolean AddTable(String TN) {
		String SQL;

		try {

			fprog = TN;idbg = TN;
			wsRtask.onProgressUpdate();
			SQL = getTableSQL(TN);

			if (fillTable(SQL, "DELETE FROM " + TN) == 1) {
				if (TN.equalsIgnoreCase("P_STOCK")) dbg = dbg + " ok ";
				idbg = idbg + SQL + "#" + "PASS OK";
				return true;
			} else {
				if (TN.equalsIgnoreCase("P_STOCK")) dbg = dbg + " fail " + sstr;
				idbg = idbg + SQL + "#" + " PASS FAIL  ";
				fstr = "Tab:" + TN + " " + sstr;
				return false;
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = "Tab:" + TN + ", " + e.getMessage();
			idbg = idbg + e.getMessage();
			return false;
		}
	}

	private boolean AddTableVL(String TN) {
		String SQL;

		try {

			fprog = TN;
			idbg = TN;
			//wsRtask.onProgressUpdate();
			SQL = getTableSQL(TN);

			if (fillTable(SQL, "DELETE FROM " + TN) == 1) {
				if (TN.equalsIgnoreCase("P_STOCK")) dbg = dbg + " ok ";
				idbg = idbg + SQL + "#" + "PASS OK";
				return true;
			} else {
				if (TN.equalsIgnoreCase("P_STOCK")) dbg = dbg + " fail " + sstr;
				idbg = idbg + SQL + "#" + " PASS FAIL  ";
				fstr = "Tab:" + TN + " " + sstr;
				return false;
			}

		} catch (Exception e) {
			fstr = "Tab:" + TN + ", " + e.getMessage();
			idbg = idbg + e.getMessage();
			return false;
		}
	}

	private String getTableSQL(String TN) {
		String SQL = "";
		long fi, ff;

		fi = du.ffecha00(du.getActDate());
		ff = du.ffecha24(du.getActDate());
		long ObjAno = du.getyear(du.getActDate());
		long ObjMes = du.getmonth(du.getActDate());

        if (TN.equalsIgnoreCase("P_RUTA")) {
            SQL = "SELECT CODIGO, NOMBRE, ACTIVO, VENDEDOR, VENTA, FORANIA, SUCURSAL, TIPO, SUBTIPO, " +
                    "BODEGA, SUBBODEGA, DESCUENTO, BONIF, KILOMETRAJE, IMPRESION, RECIBOPROPIO, CELULAR, RENTABIL, OFERTA, " +
                    "PERCRENT, PASARCREDITO, TECLADO, EDITDEVPREC, EDITDESC, PARAMS, SEMANA, OBJANO, OBJMES, SYNCFOLD, WLFOLD, " +
                    "FTPFOLD, EMAIL, LASTIMP, LASTCOM, LASTEXP, IMPSTAT, EXPSTAT, COMSTAT, PARAM1, " +
                    "PARAM2, PESOLIM, INTERVALO_MAX, LECTURAS_VALID, INTENTOS_LECT, HORA_INI, HORA_FIN, " +
                    "APLICACION_USA, PUERTO_GPS, ES_RUTA_OFICINA, DILUIR_BON, PREIMPRESION_FACTURA, MODIFICAR_MEDIA_PAGO, " +
                    "IDIMPRESORA, NUMVERSION,0 AS FECHAVERSION, ARQUITECTURA " +
                    "FROM P_RUTA WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CLIENTE")) {
            SQL = " SELECT CODIGO, NOMBRE, BLOQUEADO, TIPONEG, TIPO, SUBTIPO, CANAL, SUBCANAL, NIVELPRECIO, MEDIAPAGO, LIMITECREDITO, DIACREDITO, DESCUENTO, BONIFICACION, ULTVISITA, IMPSPEC, INVTIPO, INVEQUIPO,  ";
            SQL += "INV1, INV2, INV3, NIT, MENSAJE, EMAIL, ESERVICE, TELEFONO, DIRTIPO, DIRECCION, REGION, SUCURSAL, MUNICIPIO, CIUDAD, ZONA, COLONIA, AVENIDA, CALLE, NUMERO, CARTOGRAFICO, COORX, COORY, BODEGA,  ";
            SQL += "COD_PAIS, FIRMADIG, CODBARRA, VALIDACREDITO, FACT_VS_FACT, CHEQUEPOST, PRECIO_ESTRATEGICO, NOMBRE_PROPIETARIO, NOMBRE_REPRESENTANTE, PERCEPCION, TIPO_CONTRIBUYENTE, ID_DESPACHO,  ";
            SQL += "ID_FACTURACION, MODIF_PRECIO FROM P_CLIENTE WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODUCTO")) {
            SQL = "SELECT CODIGO, TIPO, LINEA, SUBLINEA, EMPRESA, MARCA, CODBARRA, DESCCORTA, DESCLARGA, COSTO, ";
            SQL += "FACTORCONV, UNIDBAS, UNIDMED, UNIMEDFACT, UNIGRA, UNIGRAFACT, ISNULL(DESCUENTO,'N') AS DESCUENTO, ISNULL(BONIFICACION,'N') AS BONIFICACION, ";
            SQL += "IMP1, IMP2, IMP3, VENCOMP, ISNULL(DEVOL,'S') AS DEVOL, OFRECER, RENTAB, DESCMAX, PESO_PROMEDIO,MODIF_PRECIO,IMAGEN, ";
            SQL += "VIDEO,VENTA_POR_PESO,ES_PROD_BARRA,UNID_INV,VENTA_POR_PAQUETE,VENTA_POR_FACTOR_CONV,ES_SERIALIZADO,PARAM_CADUCIDAD, ";
            SQL += "PRODUCTO_PADRE,FACTOR_PADRE,TIENE_INV,TIENE_VINETA_O_TUBO,PRECIO_VINETA_O_TUBO,ES_VENDIBLE,UNIGRASAP,UM_SALIDA,Activo ";
            SQL += "FROM P_PRODUCTO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_FACTORCONV")) {
             SQL = " SELECT PRODUCTO, UNIDADSUPERIOR, FACTORCONVERSION, UNIDADMINIMA " +
             "FROM P_FACTORCONV WHERE EMPRESA='"+emp+"'";
             return SQL;
        }

        if (TN.equalsIgnoreCase("P_LINEA")) {
            SQL = "SELECT CODIGO,MARCA,NOMBRE,Activo FROM P_LINEA WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODPRECIO")) {
            SQL = "SELECT CODIGO,NIVEL,PRECIO,UNIDADMEDIDA " +
            "FROM P_PRODPRECIO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_DESCUENTO")) {
            SQL = "SELECT CLIENTE,CTIPO,PRODUCTO,PTIPO,TIPORUTA,RANGOINI,RANGOFIN,DESCTIPO,VALOR,GLOBDESC,PORCANT,FECHAINI,FECHAFIN,CODDESC,NOMBRE,ACTIVO ";
            SQL += "FROM P_DESCUENTO WHERE EMP='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_EMPRESA")) {
            SQL = "SELECT * FROM P_EMPRESA WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_SUCURSAL")) {
            SQL = " SELECT CODIGO, EMPRESA, DESCRIPCION, NOMBRE, DIRECCION, TELEFONO, NIT, TEXTO, ACTIVO " +
                  " FROM P_SUCURSAL WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_BANCO")) {
            SQL = "SELECT * FROM P_BANCO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_NIVELPRECIO")) {
            SQL = "SELECT CODIGO, NOMBRE, ACTIVO FROM P_NIVELPRECIO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_COREL")) {
            SQL = "SELECT RESOL,SERIE,CORELINI,CORELFIN,CORELULT,dbo.AndrDate(FECHARES),RUTA,dbo.AndrDate(FECHAVIG),RESGUARDO,VALOR1 " +
                  "FROM P_COREL WHERE EMP='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MEDIAPAGO")) {
            SQL = "SELECT CODIGO,NOMBRE,ACTIVO,NIVEL,PORCOBRO FROM P_MEDIAPAGO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_IMPUESTO")) {
            SQL = "SELECT CODIGO,VALOR,Activo FROM P_IMPUESTO WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("VENDEDORES")) {
            SQL = "SELECT CODIGO,NOMBRE,CLAVE,RUTA,NIVEL,NIVELPRECIO,ISNULL(BODEGA,' ') AS BODEGA,ISNULL(SUBBODEGA,' ') AS SUBBODEGA, ACTIVO  " +
                  "FROM VENDEDORES WHERE EMPRESA='"+emp+"'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_ARCHIVOCONF")) {
            SQL = "SELECT RUTA,TIPO_HH,IDIOMA,TIPO_IMPRESORA,SERIAL_HH,MODIF_PESO,PUERTO_IMPRESION, " +
                  "LBS_O_KGS,NOTA_CREDITO FROM P_ARCHIVOCONF WHERE (RUTA='"+ActRuta+"') AND (EMPRESA='" + emp + "')  ";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_ENCABEZADO_REPORTESHH")) {
            SQL = "SELECT CODIGO,TEXTO,SUCURSAL FROM P_ENCABEZADO_REPORTESHH " +
                  "WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_BONIF")) {
             SQL = "SELECT  CLIENTE, CTIPO, PRODUCTO, PTIPO, TIPORUTA, TIPOBON, RANGOINI, RANGOFIN, TIPOLISTA, TIPOCANT, VALOR," +
                    "LISTA, CANTEXACT, GLOBBON, PORCANT, dbo.AndrDate(FECHAINI), dbo.AndrDate(FECHAFIN), CODDESC, NOMBRE, EMP, UMPRODUCTO , UMBONIFICACION " +
                    "FROM P_BONIF WHERE (EMP='" + emp + "')";;
             return SQL;
        }

        if (TN.equalsIgnoreCase("P_PARAMEXT")) {
            SQL = "SELECT ID,Nombre,Valor FROM P_PARAMEXT WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PROVEEDOR")) {
            SQL = "SELECT CODIGO, NOMBRE, ACTIVO FROM P_PROVEEDOR WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MONEDA")) {
            SQL = "SELECT CODIGO,NOMBRE,ACTIVO,SYMBOLO,CAMBIO FROM P_MONEDA WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODMENU")) {
            SQL = "SELECT CODIGO,ITEM,NOMBRE,IDOPCION,CANT,ORDEN,BANDERA,NOTA FROM P_PRODMENU WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODOPC")) {
            SQL = "SELECT ID,NOMBRE,ACTIVO FROM P_PRODOPC WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODOPCLIST")) {
            SQL = "SELECT ID,PRODUCTO,CANT,IDRECETA FROM P_PRODOPCLIST WHERE (EMPRESA='" + emp + "')";
            return SQL;
        }


        return SQL;
	}

    private String getTableSQLOrig(String TN) {
        String SQL = "";
        long fi, ff;

        fi = du.ffecha00(du.getActDate());
        ff = du.ffecha24(du.getActDate());
        long ObjAno = du.getyear(du.getActDate());
        long ObjMes = du.getmonth(du.getActDate());

        if (TN.equalsIgnoreCase("P_STOCK")) {

            if (gl.peModal.equalsIgnoreCase("TOL")) {
                SQL = "SELECT CODIGO, CANT, CANTM, PESO, plibra, LOTE, DOCUMENTO, dbo.AndrDate(FECHA), ANULADO, CENTRO, STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, UNIDADMEDIDA " +
                        "FROM P_STOCK WHERE RUTA='" + ActRuta + "'  AND (FECHA>='" + fsqli + "') AND (FECHA<='" + fsqlf + "') " +
                        "AND (STATUS='A') AND (COREL_D_MOV='') AND (CODIGOLIQUIDACION=0) AND (ANULADO=0) AND (ENVIADO = 0)";
            } else if (gl.peModal.equalsIgnoreCase("APR")) {
                SQL = "SELECT CODIGO, CANT, CANTM, PESO, plibra, LOTE, DOCUMENTO, dbo.AndrDate(FECHA), ANULADO, CENTRO, STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, UNIDADMEDIDA " +
                        "FROM P_STOCK WHERE RUTA='" + ActRuta + "' AND (FECHA>='" + fsql + "') ";
            } else {
                SQL = "SELECT CODIGO, CANT, CANTM, PESO, plibra, LOTE, DOCUMENTO, dbo.AndrDate(FECHA), ANULADO, CENTRO, STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, UNIDADMEDIDA " +
                        "FROM P_STOCK WHERE RUTA='" + ActRuta + "' AND (FECHA>='" + fsql + "') ";
            }

            SQL = "SELECT CODIGO, CANT, CANTM, PESO, plibra, LOTE, DOCUMENTO, dbo.AndrDate(FECHA), ANULADO, CENTRO, STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, UNIDADMEDIDA " +
                    "FROM P_STOCK WHERE RUTA='" + ActRuta + "' ";

            esql = SQL;
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_STOCKB")) {
            SQL = "SELECT RUTA, BARRA, CODIGO, CANT, COREL, PRECIO, PESO, DOCUMENTO,dbo.AndrDate(FECHA), ANULADO, CENTRO, " +
                    "STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, UNIDADMEDIDA, DOC_ENTREGA " +
                    "FROM P_STOCKB WHERE RUTA='" + ActRuta + "' AND (FECHA>='" + fsqli + "') AND (FECHA<='" + fsqlf + "') " +
                    "AND (STATUS='A') AND (COREL_D_MOV='') AND (CODIGOLIQUIDACION=0) AND (ANULADO=0) ";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_STOCK_PALLET")) {
            SQL = "SELECT DOCUMENTO, RUTA, BARRAPALLET, CODIGO, BARRAPRODUCTO, LOTEPRODUCTO, CANT, COREL, PRECIO, PESO, " +
                    "UNIDADMEDIDA,dbo.AndrDate(FECHA), ANULADO, CENTRO, STATUS, ENVIADO, CODIGOLIQUIDACION, COREL_D_MOV, DOC_ENTREGA  " +
                    "FROM P_STOCK_PALLET WHERE RUTA='" + ActRuta + "' AND (FECHA>='" + fsqli + "') AND (FECHA<='" + fsqlf + "') " +
                    "AND (STATUS='A') AND (COREL_D_MOV='') AND (CODIGOLIQUIDACION=0) AND (ANULADO=0) ";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CLIRUTA")) {
            SQL = "SELECT RUTA,CLIENTE,SEMANA,DIA,SECUENCIA,-1 AS BANDERA FROM P_CLIRUTA WHERE RUTA='" + ActRuta + "'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CLIENTE_FACHADA")) {
            SQL = " SELECT * FROM P_CLIENTE_FACHADA ";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CLIDIR")) {
            SQL = " SELECT * FROM P_CLIDIR ";
            SQL += " WHERE (P_CLIDIR.CODIGO_CLIENTE IN (SELECT CLIENTE FROM P_CLIRUTA WHERE (RUTA='" + ActRuta + "') ))";
            return SQL;
        }

        if (TN.equalsIgnoreCase("TMP_PRECESPEC")) {
            SQL = "SELECT CODIGO,VALOR,PRODUCTO,PRECIO,UNIDADMEDIDA FROM TMP_PRECESPEC ";
            SQL += " WHERE RUTA='" + ActRuta + "' AND (FECHA>='" + fsqli + "') AND (FECHA<='" + fsqlf + "') ";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_STOCKINV")) {
            SQL = "SELECT * FROM P_STOCKINV";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CODATEN")) {
            SQL = "SELECT * FROM P_CODATEN";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CODNOLEC")) {
            SQL = "SELECT CODIGO, DESCRIPCION AS NOMBRE  FROM P_RAZONNOSCAN";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CODDEV")) {
            SQL = "SELECT * FROM P_CODDEV";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CLIGRUPO")) {
            SQL = "SELECT CODIGO,CLIENTE FROM P_CLIGRUPO WHERE (CLIENTE IN (SELECT CLIENTE FROM P_CLIRUTA WHERE RUTA='" + ActRuta + "'))";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_STOCK_APR")) {
            SQL = "SELECT CODIGO, CANT, PESO " +
                    "FROM P_STOCK_APR WHERE RUTA='" + ActRuta + "' ";
            //SQL = "SELECT CODIGO,CANT,0 AS CANTM,PESO FROM P_STOCK WHERE RUTA='" + ActRuta + "'";
            //idbg=SQL;
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_COBRO")) {
            SQL = "SELECT  DOCUMENTO, EMPRESA, RUTA, CLIENTE, TIPODOC, VALORORIG, SALDO, CANCELADO, dbo.AndrDate(FECHAEMIT),dbo.AndrDate(FECHAV),'' AS CONTRASENA, ID_TRANSACCION, REFERENCIA, ASIGNACION ";
            SQL += "FROM P_COBRO WHERE (RUTA='" + ActRuta + "') AND CLIENTE IN (SELECT CLIENTE FROM P_CLIRUTA WHERE (RUTA='" + ActRuta + "')) ";
            //idbg=SQL;
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CORELNC")) {
            SQL = "SELECT RESOL,SERIE,CORELINI,CORELFIN,CORELULT,dbo.AndrDate(FECHARES),RUTA,dbo.AndrDate(FECHAVIG),RESGUARDO,VALOR1 FROM P_CORELNC WHERE RUTA='" + ActRuta + "'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CORRELREC")) {
            SQL = "SELECT RUTA,SERIE,INICIAL,FINAL,ACTUAL,ENVIADO FROM P_CORRELREC WHERE RUTA='" + ActRuta + "'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_CORREL_OTROS")) {
            SQL = "SELECT RUTA,SERIE,TIPO,INICIAL,FINAL,ACTUAL,ENVIADO FROM P_CORREL_OTROS WHERE RUTA='" + ActRuta + "'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_BONLIST")) {
            SQL = "SELECT CODIGO,PRODUCTO,CANT,CANTMIN,NOMBRE FROM P_BONLIST "+
                    "	WHERE CODIGO IN (SELECT LISTA FROM P_BONIF WHERE TIPOLISTA in (1,2) "+
                    "	AND DATEDIFF(D, FECHAINI,GETDATE()) >=0 AND DATEDIFF(D,GETDATE(), FECHAFIN) >=0  AND EMP = '" + gl.emp +"')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PRODGRUP")) {
            SQL = "SELECT CODIGO,PRODUCTO,NOMBRE FROM P_PRODGRUP";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_VEHICULO")) {
            SQL = "SELECT CODIGO,MARCA,PLACA,PESO,KM_MILLAS,TIPO FROM P_VEHICULO";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_HANDHELD")) {
            SQL = " SELECT NUMPLACA, NUMSERIE, TIPO, ISNULL(CREADA,'') AS CREADA, " +
                    " ISNULL(MODIFICADA,'') AS MODIFICADA, ISNULL(FECHA_CREADA, GETDATE()) AS FECHA_CREADA," +
                    "	ISNULL(FECHA_MODIFICADA, GETDATE()) AS FECHA_MODIFICADA, CORELZ, GRANDTOTAL FROM P_HANDHELD" +
                    " WHERE NUMPLACA IN (SELECT HANDHELD FROM P_COREL WHERE RUTA = '" + ActRuta + "' AND ACTIVA = 'S')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_IMPRESORA")) {
            SQL = " SELECT IDIMPRESORA, NUMSERIE, MARCA, ISNULL(CREADA,'') AS CREADA, " +
                    " ISNULL(MODIFICADA,'') AS MODIFICADA, " +
                    "0 AS FECHA_CREADA,0 AS FECHA_MODIFICADA,MACADDRESS FROM P_IMPRESORA";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_TRANSERROR")) {
            SQL = " SELECT IDTRANSERROR, TRANSERROR FROM P_TRANSERROR";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MUNI")) {
            SQL = "SELECT * FROM P_MUNI";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_REF1")) {
            SQL = "SELECT * FROM P_REF1";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_REF2")) {
            SQL = "SELECT * FROM P_REF2";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_REF3")) {
            SQL = "SELECT * FROM P_REF3";
            return SQL;
        }

        if (TN.equalsIgnoreCase("O_PROD")) {
            SQL = "SELECT RUTA, CODIGO, METAV, METAU, ACUMV, ACUMU FROM O_PROD WHERE (RUTA='" + ActRuta + "') AND (OBJANO=" + ObjAno + ") AND (OBJMES=" + ObjMes + ")";
            return SQL;
        }

        if (TN.equalsIgnoreCase("O_LINEA")) {
            SQL = "SELECT RUTA, CODIGO, METAV, METAU, ACUMV, ACUMU FROM O_LINEA WHERE (RUTA='" + ActRuta + "') AND (OBJANO=" + ObjAno + ") AND (OBJMES=" + ObjMes + ")";
            return SQL;
        }

        if (TN.equalsIgnoreCase("O_RUTA")) {
            SQL = "SELECT * FROM O_RUTA WHERE (RUTA='" + ActRuta + "') AND (OBJANO=" + ObjAno + ") AND (OBJMES=" + ObjMes + ")";
            return SQL;
        }

        if (TN.equalsIgnoreCase("O_COBRO")) {
            SQL = "SELECT * FROM O_COBRO WHERE (RUTA='" + ActRuta + "') AND (OBJANO=" + ObjAno + ") AND (OBJMES=" + ObjMes + ")";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MEREQTIPO")) {
            SQL = "SELECT * FROM P_MEREQTIPO";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MEREQUIPO")) {
            SQL = "SELECT * FROM P_MEREQUIPO ";
            SQL = SQL + "WHERE (CLIENTE IN  (SELECT CLIENTE FROM P_CLIRUTA WHERE RUTA='" + ActRuta + "' ) )";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MERESTADO")) {
            SQL = "SELECT * FROM P_MERESTADO";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MERPREGUNTA")) {
            SQL = "SELECT * FROM P_MERPREGUNTA";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MERRESP")) {
            SQL = "SELECT * FROM P_MERRESP";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MERMARCACOMP")) {
            SQL = "SELECT * FROM P_MERMARCACOMP";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_MERPRODCOMP")) {
            SQL = "SELECT * FROM P_MERPRODCOMP";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_PORCMERMA")) {
            SQL = "SELECT EMPRESA,SUCURSAL,RUTA,PRODUCTO,PORCENTAJEMERMA,PORCMINIMO,PORCMAXIMO FROM P_PORCMERMA WHERE (RUTA='" + ActRuta + "')";
            return SQL;
        }

        if (TN.equalsIgnoreCase("LIC_CLIENTE")) {
            SQL = "SELECT * FROM LIC_CLIENTE WHERE ID='" + mac + "'";
            return SQL;
        }

        if (TN.equalsIgnoreCase("P_LIQUIDACION")) {
            SQL = "SELECT RUTA,ESTADO FROM P_LIQUIDACION WHERE (RUTA='" + ActRuta + "') AND (FECHA>='" + fsql + "') ";
            return SQL;
        }

        return SQL;
    }

	private void comparaCorrel() {
		Cursor DT;
		String ss;
		try {
			try {
				sql = "SELECT VENTA FROM P_RUTA";
				DT = Con.OpenDT(sql);

				if (DT.getCount() == 0) {
					msgbox("La ruta no existe. Por favor informe su supervisor !");
				}

				DT.moveToFirst();
				ss = DT.getString(0);
				if (ss.equalsIgnoreCase("T")) ss = "V";
			} catch (Exception e) {
				addlog(new Object() {
				}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
				ss = "X";
			}

			if (!ss.equalsIgnoreCase("V")) return;

			ultcor = ultCorel();
			if (ultcor == 0) {
				//msgbox("No está definido correlativo de las facturas!\n Por favor informe a su supervisor.");
			}

			ultSerie = ultSerie(); //#HS_20181129_1005 Agregue ultSerie.
			if (ultcor_ant != ultcor) {
				//#HS_20181129_1005 Agregue comparacion para las series.
				if (ultSerie_ant != ultSerie) {
					msgbox("Nueva serie de facturación");
				} else if (ultcor_ant > 0) {
					msgbox("El último correlativo actualizado ( " + ultcor + " ) no coincide con último emitido ( " + ultcor_ant + " )!\n Por favor infore a su supervisor.");
					return;
				}
			}
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
		}

	}

	private boolean FinDia() {

		try {

			if (commitSQL() == 1) {

				db.beginTransaction();
				db.execSQL("UPDATE D_FACTURA SET STATCOM='S'");
				db.execSQL("UPDATE D_PEDIDO SET STATCOM='S'");
				db.execSQL("UPDATE D_NOTACRED SET STATCOM='S'");
				db.execSQL("UPDATE D_CXC SET STATCOM='S'");
				db.execSQL("UPDATE D_COBRO SET STATCOM='S'");
				db.execSQL("UPDATE D_DEPOS SET STATCOM='S'");
				db.execSQL("UPDATE D_MOV SET STATCOM='S'");
				db.execSQL("UPDATE D_CLINUEVO SET STATCOM='S'");
				db.execSQL("UPDATE D_ATENCION SET STATCOM='S'");
				db.execSQL("UPDATE D_CLICOORD SET STATCOM='S'");
				db.execSQL("UPDATE D_SOLICINV SET STATCOM='S'");
                db.execSQL("UPDATE D_RATING SET STATCOM='S'");
				db.execSQL("UPDATE D_MOVD SET CODIGOLIQUIDACION=0");
				db.execSQL("UPDATE FINDIA SET VAL5=0, VAL4=0,VAL3=0, VAL2=0");
				db.setTransactionSuccessful();
				db.endTransaction();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			msgbox("FinDia(): " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean ActualizaStatcom() {

		try {

			db.beginTransaction();
			db.execSQL("UPDATE D_FACTURA SET STATCOM='S'");
			db.execSQL("UPDATE D_PEDIDO SET STATCOM='S'");
			db.execSQL("UPDATE D_NOTACRED SET STATCOM='S'");
			db.execSQL("UPDATE D_CXC SET STATCOM='S'");
			db.execSQL("UPDATE D_COBRO SET STATCOM='S'");
			db.execSQL("UPDATE D_DEPOS SET STATCOM='S'");
			db.execSQL("UPDATE D_MOV SET STATCOM='S'");
			db.execSQL("UPDATE D_CLINUEVO SET STATCOM='S'");
			db.execSQL("UPDATE D_ATENCION SET STATCOM='S'");
			db.execSQL("UPDATE D_CLICOORD SET STATCOM='S'");
			db.execSQL("UPDATE D_SOLICINV SET STATCOM='S'");
			db.execSQL("UPDATE D_MOVD SET CODIGOLIQUIDACION=0");
            db.execSQL("UPDATE D_RATING SET STATCOM='S'");
			db.execSQL("UPDATE P_RUTA SET PARAM2 = ''");
			db.setTransactionSuccessful();
			db.endTransaction();

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			msgbox("ActualizaStatcom(): " + e.getMessage());
			return false;
		}
		return true;
	}

	private void encodePrinters() {
		Cursor dt;
		String prid,ser,mac,se,sm;

		try {
			sql="SELECT IDIMPRESORA,NUMSERIE,MACADDRESS FROM P_IMPRESORA";
			dt=ConT.OpenDT(sql);

			if (dt.getCount() > 0) dt.moveToFirst();
			while (!dt.isAfterLast()) {

				prid=dt.getString(0);
				ser=dt.getString(1);
				mac=dt.getString(2);

				se=cu.encrypt(ser);
				sm=cu.encrypt(mac);

				sql="UPDATE P_IMPRESORA SET NUMSERIE='"+se+"',MACADDRESS='"+sm+"' WHERE IDIMPRESORA='"+prid+"'";
				dbT.execSQL(sql);

				dt.moveToNext();
			}

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void encodeLicence() {
		String lic;

		try {
			if (licResult==1) lic=licSerialEnc; else lic="";
			sql = "UPDATE Params SET lic='" + lic + "'";
			dbT.execSQL(sql);
		} catch (Exception e) {
			msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
		}

	}

	private void encodeLicenceRuta() {
		String lic;

		try {
			if (licResultRuta==1) lic=licRutaEnc; else lic="";
			sql = "UPDATE Params SET licparam='" + lic + "'";
			dbT.execSQL(sql);
		} catch (Exception e) {
			msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
		}

	}

	private boolean validaLicencia() {
		CryptUtil cu=new CryptUtil();
		Cursor dt;
		String lic,lickey,licruta,rutaencrypt;
		Integer msgLic=0;

		try {
			lickey=cu.encrypt(gl.deviceId);
			rutaencrypt=cu.encrypt(gl.ruta);

			sql="SELECT lic, licparam FROM Params";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			lic=dt.getString(0);
			licruta=dt.getString(1);
			String ss=cu.decrypt(licruta);

			if (lic.equalsIgnoreCase(lickey) && licruta.equalsIgnoreCase(rutaencrypt)) return true;
			else if (!lic.equalsIgnoreCase(lickey) && !licruta.equalsIgnoreCase(rutaencrypt)){msgLic=1;}
			else if(!lic.equalsIgnoreCase(lickey)){msgLic=2;}
			else if(!licruta.equalsIgnoreCase(rutaencrypt)){msgLic=3;}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" : "+e.getMessage());
		}

		if(msgLic==1)toastlong("El dispositivo no tiene licencia válida de handheld, ni de ruta");
		else if(msgLic==2){toastlong("El dispositivo no tiene licencia valida de handheld");}
		else if(msgLic==3){toastlong("El dispositivo no tiene licencia valida de ruta");}

		return false;
	}

	private void fechaCarga() {

		try {
			dbT.beginTransaction();

			dbT.execSQL("DELETE FROM P_FECHA");

			sql="INSERT INTO P_FECHA VALUES('"+gl.ruta+"',"+du.getActDate()+")";
			dbT.execSQL(sql);

			dbT.setTransactionSuccessful();
			dbT.endTransaction();
		} catch (Exception e) {
			dbT.endTransaction();
		}

	}

	//endregion

	//region WS Recepcion Handling Methods

	public void wsExecute() {

		running = 1;
		fstr = "No connect";
		errflag=false;
		scon = 0;

		try {

			if (getTest() == 1) scon = 1;

			idbg = idbg + sstr;

			if (scon == 1) {
				fstr = "Sync OK";
				if (!getData()) {
				    fstr = "Recepcion incompleta : " + fstr;errflag=true;
                }
			} else {
				fstr = "No se puede conectar al web service : " + sstr;errflag=true;
			}

		} catch (Exception e) {
            errflag=true;
			fstr = "No se puede conectar al web service. " + e.getMessage();
		}

	}

	public void wsFinished() {

		barInfo.setVisibility(View.INVISIBLE);
		lblParam.setVisibility(View.INVISIBLE);
		running = 0;

		try {
			if (!errflag) {
				lblInfo.setText(" ");
	            msgAskExit("Recepción completa.");
			} else {
				lblInfo.setText(fstr);isbusy = 0;
				mu.msgbox("Ocurrió error : \n" + fstr + " (" + reccnt + ") "+sql);
				barInfo.setVisibility(View.INVISIBLE);
				addlog("Recepcion", fstr, esql);
				return;
			}

			visibilidadBotones();
			isbusy = 0;
			paramsExtra();

			if (ftflag) msgbox(ftmsg);
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

		//if (!validaLicencia()) restartApp();

	}

	private class AsyncCallRec extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			try {
				wsExecute();
			} catch (Exception e) {
				if (scon == 0) {
					fstr = "No se puede conectar al web service : " + sstr;
					//lblInfo.setText(fstr);
				}
				//msgbox(fstr);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				wsFinished();
			} catch (Exception e) {
				Log.d("onPostExecute", e.getMessage());
			}

		}

		@Override
		protected void onPreExecute() {
			try {
				SetStatusRecTo("");
			} catch (Exception e) {
				Log.d("onPreExecute", e.getMessage());
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			try {
				synchronized (this) {
					if (!lblInfo.getText().toString().matches("")) lblInfo.setText(fprog);
				}
			} catch (Exception e) {
				Log.d("onProgressUpdate", e.getMessage());
			}
		}

	}

	//endregion

	//region WS Envio Methods

	private boolean sendData() {

		errflag = false;senv = "Envío terminado \n \n";
		items.clear();dbld.clearlog();

		try {

			envioFacturas();
			if (errflag){
				dbld.savelog();
				return true;
			}

			/*
			envioDepositos();
			if (!fstr.equals("Sync OK")){
				dbld.savelog();
				return true;
			}
			*/

			/*
			updateAcumulados();
			if (!fstr.equals("Sync OK")){
				dbld.savelog();
				return true;
			}
			*/

			dbld.savelog();
			dbld.saveArchivo(du.getActDateStr());

			/*
            if (commitSQL() == 1) {
                errflag = false;
            } else {
                errflag = true;
                fterr += "\n" + sstr;
            }
            */

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

		return errflag;
	}

	private boolean validaLiquidacion() {
		Cursor DT;
		String ss;

		try {

			db.execSQL("DELETE FROM P_LIQUIDACION");

			AddTableVL("P_LIQUIDACION");

			if (listItems.size() < 2) {
				return true;
			}

			sql = listItems.get(0);
			db.execSQL(sql);

			sql = listItems.get(1);
			db.execSQL(sql);

			sql = "SELECT ESTADO FROM P_LIQUIDACION";
			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				ss = DT.getString(0);
				if (ss.equalsIgnoreCase("Cerrada")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			strliqid = e.getMessage();
			return false;
		}
	}

	public void envioFacturas() {
		Cursor DT;
		String cor, fruta, tt;
		int i, pc = 0, pcc = 0, ccorel;

		fterr = "";
		err="";

		try {

			sql = "SELECT COREL,RUTA,CORELATIVO FROM D_FACTURA WHERE STATCOM='N' ORDER BY CORELATIVO";
			DT = Con.OpenDT(sql);

			if (DT.getCount() == 0) {
				senv += "Facturas : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();

			dbld.clear();

			while (!DT.isAfterLast()) {

				cor = DT.getString(0);
				fruta = DT.getString(1);
				ccorel = DT.getInt(2);

				dbg = "::";

				try {

					i += 1;
					fprog = "Factura " + i;
					wsStask.onProgressUpdate();

					dbld.clear();

					dbld.insert("D_FACTURA", "WHERE COREL='" + cor + "'");
					dbld.insert("D_FACTURAD", "WHERE COREL='" + cor + "'");
					dbld.insert("D_FACTURAP", "WHERE COREL='" + cor + "'");
					dbld.insert("D_FACTURAF", "WHERE COREL='" + cor + "'");
					dbld.insert("D_BONIF", "WHERE COREL='" + cor + "'");

					dbld.add("UPDATE P_COREL SET CORELULT=" + ccorel + "  WHERE RUTA='" + fruta + "'");

                    if (commitSQL() == 1) {
                        sql = "UPDATE D_FACTURA SET STATCOM='S' WHERE COREL='" + cor + "'";
                        db.execSQL(sql);
                        pc += 1;
                    } else {
                        errflag = true;
                        fterr += "\nFactura : " + sstr;
                        dbg = sstr;
                    }

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
					dbg = e.getMessage();
				}

				DT.moveToNext();
			}

			DT.close();


		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
			dbg = fstr;
		}

        if (pc != pcc) {
            int pf = pcc - pc;
            senv += "Facturas : " + pc + " , NO ENVIADO : " + pf + "\n";
        } else {
            senv += "Facturas : " + pc + "\n";
        }

	}

	public void envioPedidos() {
		Cursor DT;
		String cor;
		int i, pc = 0, pcc = 0;

		try {
			sql = "SELECT COREL FROM D_PEDIDO WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) {
				senv += "Pedidos : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);

				try {

					i += 1;
					fprog = "Pedido " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}


					if (envioparcial) dbld.clear();

					dbld.insert("D_PEDIDO", "WHERE COREL='" + cor + "'");
					dbld.insert("D_PEDIDOD", "WHERE COREL='" + cor + "'");

					dbld.insert("D_BONIF", "WHERE COREL='" + cor + "'");
					dbld.insert("D_BONIF_LOTES", "WHERE COREL='" + cor + "'");
					dbld.insert("D_REL_PROD_BON", "WHERE COREL='" + cor + "'");
					dbld.insert("D_BONIFFALT", "WHERE COREL='" + cor + "'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_PEDIDO SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							Toast.makeText(this, "Envio correcto", Toast.LENGTH_SHORT).show();
							pc += 1;
						} else {
							errflag = true;
							fterr += "\n" + sstr;
						}
					}else pc += 1;

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					//fterr+="\n"+e.getMessage();
				}
				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		//#CKFK 20190325 Sea el envío parcial o no se deben mostrar las facturas comunicadas
		//if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Pedidos : " + pc + " , NO ENVIADO : " + pf + "\n";
			} else {
				senv += "Pedidos : " + pc + "\n";
			}
		//}
	}

	public void envioCobros() {
		Cursor DT;
		String cor, fruta;
		int i, pc = 0, pcc = 0, corult;

		try {
			sql = "SELECT COREL,CORELATIVO,RUTA FROM D_COBRO WHERE STATCOM='N' ORDER BY COREL";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) {
				senv += "Cobros : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);
				corult = DT.getInt(1);
				fruta = DT.getString(2);

				try {

					i += 1;
					fprog = "Cobro " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) dbld.clear();

					dbld.insert("D_COBRO", "WHERE COREL='" + cor + "'");
					dbld.insert("D_COBROD", "WHERE COREL='" + cor + "'");
					dbld.insert("D_COBROD_SR", "WHERE COREL='" + cor + "'");
					dbld.insert("D_COBROP", "WHERE COREL='" + cor + "'");

					dbld.add("UPDATE P_CORRELREC SET Actual=" + corult + "  WHERE RUTA='" + fruta + "'");

					if (envioparcial && !esEnvioManual){
						if (commitSQL() == 1) {
							sql = "UPDATE D_COBRO SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							pc += 1;
						} else {
							errflag = true;
							fterr += "\nCobro: " + sstr;
						}
					}else pc += 1;

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					//fterr+="\n"+e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		//#CKFK 20190325 Sea el envío parcial o no se deben mostrar las facturas comunicadas
		//if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Cobros : " + pc + " , NO ENVIADO : " + pf + " \n";
			} else {
				senv += "Cobros : " + pc + "\n";
			}
		//}
	}

	public void envioNotasCredito() {
		Cursor DT;
		String cor, fruta;
		int i, pc = 0, pcc = 0, ccorel;

		try {

			sql = "SELECT COREL,RUTA,CORELATIVO FROM D_NOTACRED WHERE STATCOM='N' ORDER BY CORELATIVO";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) {
				senv += "Notas crédito : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);
				fruta = DT.getString(1);
				ccorel = DT.getInt(2);

				try {

					i += 1;
					fprog = "Nota crédito " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}


					if (envioparcial) dbld.clear();

					dbld.insert("D_NOTACRED", "WHERE COREL='" + cor + "'");
					dbld.insert("D_NOTACREDD", "WHERE COREL='" + cor + "'");

					dbld.add("UPDATE P_CORELNC SET CORELULT=" + ccorel + "  WHERE RUTA='" + fruta + "'");
					dbld.add("UPDATE P_CORREL_OTROS SET ACTUAL=" + ccorel + "  WHERE RUTA='" + fruta + "' AND TIPO = 'NC'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_NOTACRED SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							pc += 1;
						} else {
							fterr += "\n" + sstr;
						}
					}else pc += 1;

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		//#CKFK 20190325 Sea el envío parcial o no se deben mostrar las facturas comunicadas
		//if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Notas crédito : " + pc + " , NO ENVIADO : " + pf + "\n";
			} else {
				senv += "Notas crédito : " + pc + "\n";
			}
		//}
	}

	public void envioNotasDevolucion() {
		Cursor DT;
		String cor, fruta = "", serie="";
		int i, pc = 0, pcc = 0, ccorel=0;

		try {

			sql = "SELECT COREL,RUTA FROM D_CXC WHERE STATCOM='N' ORDER BY COREL";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) {
				senv += "Notas de devolución : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);
				fruta = DT.getString(1);

				try {

					i += 1;
					fprog = "Nota de devolución " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) dbld.clear();

					dbld.insert("D_CXC", "WHERE COREL='" + cor + "'");
					dbld.insert("D_CXCD", "WHERE COREL='" + cor + "'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_CXC SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							pc += 1;
						} else {
							fterr += "\n" + sstr;
						}
					}else pc += 1;

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

			sql = "SELECT ACTUAL, SERIE FROM P_CORREL_OTROS WHERE TIPO = 'D'";
			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				ccorel = DT.getInt(0);
				serie = DT.getString(1);

				dbld.add("UPDATE P_CORREL_OTROS SET ACTUAL=" + ccorel + "  WHERE RUTA='" + fruta + "' AND SERIE = '"+serie +"' AND TIPO = 'D' " +
						" AND ACTUAL < "+ccorel);

			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		//#CKFK 20190325 Sea el envío parcial o no se deben mostrar las facturas comunicadas
		//if (envioparcial) {
		if (pc != pcc) {
			int pf = pcc - pc;
			senv += "Notas crédito : " + pc + " , NO ENVIADO : " + pf + "\n";
		} else {
			senv += "Notas crédito : " + pc + "\n";
		}
		//}
	}

	public void envioDepositos() {
		Cursor DT;
		String cor;
		int i, pc = 0, pcc = 0;

		try {
			sql = "SELECT COREL FROM D_DEPOS WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);

			if (DT.getCount() == 0) {
				senv += "Depósitos : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);

				try {

					i += 1;
					fprog = "Depósito " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) dbld.clear();

					dbld.insert("D_DEPOS", "WHERE COREL='" + cor + "'");
					dbld.insert("D_DEPOSD", "WHERE COREL='" + cor + "'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_DEPOS SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							pc += 1;
						} else {
							fterr += "\n" + sstr;
						}
					}else pc += 1;

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		//#CKFK 20190325 Sea el envío parcial o no se deben mostrar las facturas comunicadas
		//if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Depósitos : " + pc + " , NO ENVIADO : " + pf + " \n";
			} else {
				senv += "Depósitos : " + pc + "\n";
			}
		//}
	}

	public String Get_Corel_D_Mov() {
		Cursor DT;
		String cor = "";

		try {

			sql = "SELECT COREL FROM D_MOV WHERE (TIPO='D') ORDER BY COREL DESC ";
			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				cor = DT.getString(0);
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			msgbox(e.getMessage());
		}
		return cor;
	}

	public void envio_D_MOV() {
		Cursor DT;
		String cor;
		int i, pc = 0, pcc = 0;

		try {

			sql = "SELECT COREL FROM D_MOV WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);

			if (DT.getCount() == 0) {
				senv += "Inventario : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();

			while (!DT.isAfterLast()) {

				cor = DT.getString(0);

				try {

					i += 1;
					fprog = "Inventario " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) dbld.clear();

					dbld.insert("D_MOV", "WHERE COREL='" + cor + "'");
					dbld.insert("D_MOVD", "WHERE COREL='" + cor + "'");
					dbld.insert("D_MOVDB", "WHERE COREL='" + cor + "'");
					dbld.insert("D_MOVDCAN", "WHERE COREL='" + cor + "'");
					dbld.insert("D_MOVDPALLET", "WHERE COREL='" + cor + "'");

					//#CKFK 20190412 Corregido error
					dbld.add("INSERT INTO P_DEVOLUCIONES_SAP " +
							" SELECT D.COREL, E.COREL, 0, E.RUTA, E.FECHA, D.PRODUCTO,'', D.LOTE, 'N', GETDATE(), D.CANT, 'N'" +
							" FROM D_MOV E INNER JOIN D_MOVD D ON E.COREL = D.COREL" +
							" WHERE E.COREL = '" + cor + "'" +
							" UNION" +
							" SELECT D.COREL, E.COREL, 0, E.RUTA, E.FECHA, D.PRODUCTO,D.BARRA, '', 'N', GETDATE(), 1, 'N'" +
							" FROM D_MOV E INNER JOIN D_MOVDB D ON E.COREL = D.COREL" +
							" WHERE E.COREL = '" + cor + "'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_MOV SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);

							sql = "UPDATE D_MOVD SET CODIGOLIQUIDACION=0 WHERE COREL='" + cor + "'";
							db.execSQL(sql);

							pc += 1;

						} else {
							fterr += "\n" + sstr;
						}
					}

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Inventario : " + pc + " , NO ENVIADO : " + pf + " \n";
			} else {
				senv += "Inventario : " + pc + "\n";
			}
		}
	}

	public void envioCli() {
		Cursor DT;
		String cor;
		int i, pc = 0, pcc = 0;

		try {
			sql = "SELECT CODIGO FROM D_CLINUEVO WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) {
				senv += "Inventario : " + pc + "\n";
				return;
			}

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);

				try {

					i += 1;
					fprog = "Inventario " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) {
						dbld.clear();
					}

					dbld.insert("D_CLINUEVO", "WHERE CODIGO='" + cor + "'");
					if (gl.peModal.equalsIgnoreCase("APR")) {
						dbld.insert("D_CLINUEVO_APR", "WHERE CODIGO='" + cor + "'");
					}

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {

							sql = "UPDATE D_CLINUEVO SET STATCOM='S' WHERE CODIGO='" + cor + "'";
							db.execSQL(sql);
							if (gl.peModal.equalsIgnoreCase("APR")) {
								sql = "UPDATE D_CLINUEVO_APR SET STATCOM='S' WHERE CODIGO='" + cor + "'";
								db.execSQL(sql);
							}

							pc += 1;
						} else {
							fterr += "\n" + sstr;
						}
					}

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Cli. nuevos : " + pc + " , NO ENVIADO : " + pf + " \n";
			} else {
				senv += "Cli. nuevos : " + pc + "\n";
			}
		}
	}

	public void envioAtten() {
		Cursor DT;
		String cor, hora;
		int fecha;

		fprog = " ";
		if (!esEnvioManual){
			wsStask.onProgressUpdate();
		}

		try {
			sql = "SELECT RUTA,FECHA,HORALLEG FROM D_ATENCION WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) return;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);
				fecha = DT.getInt(1);
				hora = DT.getString(2);

				try {

					if (envioparcial) dbld.clear();

					dbld.insert("D_ATENCION", "WHERE (RUTA='" + cor + "') AND (FECHA=" + fecha + ") AND (HORALLEG='" + hora + "') ");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_ATENCION SET STATCOM='S' WHERE (RUTA='" + cor + "') AND (FECHA=" + fecha + ") AND (HORALLEG='" + hora + "') ";
							db.execSQL(sql);
						} else {
							//fterr+="\n"+sstr;
						}
					}

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					//fterr+="\n"+e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

	}

	public void envioCoord() {
		Cursor DT;
		String cod, ss;
		int stp;
		double px, py;
		fprog = " ";
		if (!esEnvioManual){
			wsStask.onProgressUpdate();
		}

		try {
			sql = "SELECT CODIGO,COORX,COORY,STAMP FROM D_CLICOORD WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) return;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cod = DT.getString(0);
				px = DT.getDouble(1);
				py = DT.getDouble(2);
				stp = DT.getInt(3);

				try {

					if (envioparcial) dbld.clear();

					ss = "UPDATE P_CLIENTE SET COORX=" + px + ",COORY=" + py + " WHERE (CODIGO='" + cod + "')";
					dbld.add(ss);

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_CLICOORD SET STATCOM='S' WHERE (CODIGO='" + cod + "') AND (STAMP=" + stp + ") ";
							db.execSQL(sql);
						} else {
							fterr += "\n" + sstr;
						}
					}

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

	}

	public void envioSolicitud() {
		Cursor DT;
		String cor;
		int i, pc = 0, pcc = 0;

		try {

			sql = "SELECT * FROM D_SOLICINVD";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) return;

			sql = "SELECT COREL FROM D_SOLICINV WHERE STATCOM='N'";
			DT = Con.OpenDT(sql);
			if (DT.getCount() == 0) return;

			pcc = DT.getCount();
			pc = 0;
			i = 0;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cor = DT.getString(0);

				try {

					i += 1;
					fprog = "Solicitud " + i;
					if (!esEnvioManual){
						wsStask.onProgressUpdate();
					}

					if (envioparcial) dbld.clear();

					dbld.insert("D_SOLICINV", "WHERE COREL='" + cor + "'");
					dbld.insert("D_SOLICINVD", "WHERE COREL='" + cor + "'");

					if (envioparcial && !esEnvioManual) {
						if (commitSQL() == 1) {
							sql = "UPDATE D_SOLICINV SET STATCOM='S' WHERE COREL='" + cor + "'";
							db.execSQL(sql);
							pc += 1;
						} else {
							fterr += "\n" + sstr;
						}
					}

				} catch (Exception e) {
					addlog(new Object() {
					}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
					fterr += "\n" + e.getMessage();
				}

				DT.moveToNext();
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		if (envioparcial) {
			if (pc != pcc) {
				int pf = pcc - pc;
				senv += "Solicitud : " + pc + " , NO ENVIADO : " + pf + "\n";
			} else {
				senv += "Solicitud : " + pc + "\n";
			}
		}
	}

	public void envioFinDia() {

		int pc = 0, pcc=3;

		fprog = " ";
		if (!esEnvioManual){
			wsStask.onProgressUpdate();
		}

		try {

			pc = 0;

			if (envioparcial) dbld.clear();

			ss = "UPDATE P_RUTA SET IDIMPRESORA='"+parImprID+"',NUMVERSION='"+gl.parVer+"',ARQUITECTURA='ANDR' WHERE CODIGO='" + gl.ruta + "'";
            dbld.add(ss);

            dbld.add("DELETE FROM D_REPFINDIA WHERE RUTA='" + gl.ruta + "'");
			dbld.insert("D_REPFINDIA", "WHERE (LINEA>=0)");

			if (envioparcial && !esEnvioManual) {
				if (commitSQL() == 1) {
					pc = 3;
				} else {
					errflag = true;
					fterr += "\nFinDia : " + sstr;
					dbg = sstr;
				}
			}else pc = 3;

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
		}

		if (pc != pcc) {
			int pf = pcc - pc;
			senv += "Envío Fin día : " + pc + " , NO ENVIADO : " + pf + "\n";
		} else {
			senv += "Envío Fin día : " + pc + "\n";
		}
	}

    public void envioRating() {
        Cursor DT;
        String ruta, vendedor, comentario, fecha;
        int id, idtranserror;
        float rating;

        if (!esEnvioManual){
            wsStask.onProgressUpdate();
        }

        try {
            sql = " SELECT IDRATING, RUTA, VENDEDOR, RATING, COMENTARIO, IDTRANSERROR, FECHA, STATCOM " +
                  " FROM D_RATING WHERE STATCOM='N'";
            DT = Con.OpenDT(sql);
            if (DT.getCount() == 0) return;

            DT.moveToFirst();
            while (!DT.isAfterLast()) {

                id = DT.getInt(0);
                ruta = DT.getString(1);
                vendedor = DT.getString(2);
                rating = DT.getFloat(3);
                comentario = DT.getString(4);
                idtranserror=DT.getInt(5);
                fecha=DT.getString(6);

                try {

                    if (envioparcial) dbld.clear();

                    ss = "INSERT INTO D_RATING (RUTA, VENDEDOR, RATING, COMENTARIO, IDTRANSERROR, FECHA, STATCOM)"+
                         " VALUES('" + ruta +"','" + vendedor +"'," + rating +",'" + comentario +"'," +
                         "" + idtranserror +",'" + fecha +"','N')";
                    dbld.add(ss);

                    if (envioparcial && !esEnvioManual) {
                        if (commitSQL() == 1) {
                            sql = "UPDATE D_RATING SET STATCOM='S' WHERE IDRATING='" + id + "'";
                            db.execSQL(sql);
                        }else{
                            fterr += "\n" + sstr;
                        }
                    }

                } catch (Exception e) {
                    addlog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
                    fterr += "\n" + e.getMessage();
                }

                DT.moveToNext();
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            fstr = e.getMessage();
        }

    }

	public void updateInventario() {
		DU = new DateUtils();
		String sFecha;
		int rslt;
		long vfecha = clsAppM.fechaFactTol(du.getActDate());
		sFecha = DU.univfechasql(vfecha);
		String corel_d_mov = Get_Corel_D_Mov();

		try {

			if (envioparcial) dbld.clear();

			ss = " UPDATE P_STOCK SET ENVIADO = 1, COREL_D_MOV = '" + corel_d_mov + "' " +
					" WHERE RUTA  = '" + gl.ruta + "' AND (FECHA ='" + sFecha + "') AND ENVIADO = 0 " +
					" AND DOCUMENTO IN (SELECT DOCUMENTO FROM P_DOC_ENVIADOS_HH WHERE RUTA = '" + gl.ruta + "' AND FECHA = '" + sFecha + "' )";
			dbld.add(ss);

			ss = " UPDATE P_STOCKB SET ENVIADO = 1, COREL_D_MOV = '" + corel_d_mov + "' " +
					" WHERE RUTA  = '" + gl.ruta + "' AND FECHA = '" + sFecha + "' AND ENVIADO = 0 " +
					" AND DOCUMENTO IN (SELECT DOCUMENTO FROM P_DOC_ENVIADOS_HH WHERE RUTA = '" + gl.ruta + "' AND FECHA = '" + sFecha + "')";
			dbld.add(ss);

			ss = " UPDATE P_STOCK_PALLET SET ENVIADO = 1, COREL_D_MOV = '" + corel_d_mov + "' " +
					" WHERE RUTA  = '" + gl.ruta + "' AND FECHA = '" + sFecha + "' AND ENVIADO = 0 " +
					" AND DOCUMENTO IN (SELECT DOCUMENTO FROM P_DOC_ENVIADOS_HH WHERE RUTA = '" + gl.ruta + "' AND FECHA = '" + sFecha + "')";
			dbld.add(ss);

			if (envioparcial && !esEnvioManual) {
				//fterr=ss+"\n";
				rslt = commitSQL();
				//fterr=fterr+rslt+"\n";
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
			//fterr=fterr+fstr;
		}

	}

	public void updateAcumulados() {
		long ff;
		int oyear, omonth, rslt;

		ff = du.getActDate();
		oyear = du.getyear(ff);
		omonth = du.getmonth(ff);

		try {

			if (envioparcial) dbld.clear();

			ss = "exec AcumuladoObjetivos '" + gl.ruta + "'," + oyear + "," + omonth;
			dbld.add(ss);

			if (envioparcial && !esEnvioManual) {
				rslt = commitSQL();
				fterr = fterr + rslt + "\n";
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = e.getMessage();
			fterr = fterr + fstr;
		}
	}

	public void updateLicence() {
		String SQL;
		String TN = "LIC_CLIENTE";

		try {

			fprog = TN;
			idbg = TN;
			listItems.clear();


			SQL = "SELECT * FROM LIC_CLIENTE WHERE ID='" + mac + "'";
			if (fillTable(SQL, "DELETE FROM LIC_CLIENTE") == 1) {
				idbg = idbg + SQL + "#" + "PASS OK";
			} else {
				idbg = idbg + SQL + "#" + " PASS FAIL  ";
				fstr = sstr;
			}
			idbg = idbg + " :: " + listItems.size();
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			fstr = "Tab:" + TN + ", " + e.getMessage();
			idbg = idbg + e.getMessage();
		}
	}

	public void addItem(String nombre,int env,int pend) {
		clsClasses.clsEnvio item;
		
		try {
			item=clsCls.new clsEnvio();
			
			item.Nombre=nombre;
			item.env=env;
			item.pend=pend;
			
			items.add(item);
			
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
	}
	
	public void updateLicencePush() {
		String ss;
		
		try {
			ss=listItems.get(1);
			if (mu.emptystr(ss)) return;
			db.execSQL(ss);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			//msgbox(e.getMessage());
		}
	}

	//endregion

    //region WS Actualiza methods

    private boolean Actualiza() {
	    boolean haserror=false;

        errflag = false;senv ="Actualización terminada.\n \n";
        items.clear();dbld.clearlog();acterr="";

        //---if (!AddTable("P_COREL")) return false;

        try {

            if (!actArchConfig()) haserror=true;
            if (!actBanco()) haserror=true;
            if (!actCaja()) haserror=true;
            if (!actCliente()) haserror=true;
            if (!actDesc()) haserror=true;
            if (!actEmpresa()) haserror=true;
            if (!actFamilia()) haserror=true;
            if (!actImpuesto()) haserror=true;
            if (!actMedia()) haserror=true;
            if (!actNivelPrecio()) haserror=true;
            if (!actMoneda()) haserror=true;
            if (!actProducto()) haserror=true;
            if (!actFactorconv()) haserror=true;
            if (!actParamext()) haserror=true;
            if (!actProdprecio()) haserror=true;
            if (!actProveedor()) haserror=true;
            if (!actSucursal()) haserror=true;
            if (!actProdMenu()) haserror=true;
            if (!actProdOpc()) haserror=true;
            if (!actProdOpcList()) haserror=true;

            dbld.savelog();
            dbld.saveArchivo(du.getActDateStr());

        } catch (Exception e){
        }

        errflag=haserror;
        return errflag;
    }

    public boolean actArchConfig() {
        clsP_archivoconfObj holder=new clsP_archivoconfObj(this,Con,db);

        actproc="Archivo de configuración";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.archivo_ins(holder.items.get(i)));
                dbld.add(exp.archivo_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProdOpcList() {
        clsP_prodopclistObj holder=new clsP_prodopclistObj(this,Con,db);

        actproc="Producto opcion";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.prodopclist_ins(holder.items.get(i)));
                dbld.add(exp.prodopclist_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProdOpc() {
        clsP_prodopcObj holder=new clsP_prodopcObj(this,Con,db);

        actproc="Producto opcion";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.prodopc_ins(holder.items.get(i)));
                dbld.add(exp.prodopc_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProdMenu() {
        clsP_prodmenuObj holder=new clsP_prodmenuObj(this,Con,db);

        actproc="Producto menu";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.prodmenu_ins(holder.items.get(i)));
                dbld.add(exp.prodmenu_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actSucursal() {
        clsP_sucursalObj holder=new clsP_sucursalObj(this,Con,db);

        actproc="Tienda";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();

            String ss;

            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.sucursal_ins(holder.items.get(i)));
                //ss=exp.sucursal_ins(holder.items.get(i));
                dbld.add(exp.sucursal_upd(holder.items.get(i)));
                //ss=exp.sucursal_upd(holder.items.get(i));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProveedor() {
        clsP_proveedorObj holder=new clsP_proveedorObj(this,Con,db);

        actproc="Proveedor";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.proveedor_ins(holder.items.get(i)));
                dbld.add(exp.proveedor_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProdprecio() {
        clsP_prodprecioObj holder=new clsP_prodprecioObj(this,Con,db);

        actproc="Precios";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.prodprecio_ins(holder.items.get(i)));
                dbld.add(exp.prodprecio_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actParamext() {
        clsP_paramextObj holder=new clsP_paramextObj(this,Con,db);

        actproc="Parametros";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.paramext_ins(holder.items.get(i)));
                dbld.add(exp.paramext_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actFactorconv() {
        clsP_factorconvObj holder=new clsP_factorconvObj(this,Con,db);

        actproc="Factor conversion";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.factorconv_ins(holder.items.get(i)));
                dbld.add(exp.factorconv_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actProducto() {
        clsP_productoObj holder=new clsP_productoObj(this,Con,db);

        actproc="Producto";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.producto_ins(holder.items.get(i)));
                dbld.add(exp.producto_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actMoneda() {
        clsP_monedaObj holder=new clsP_monedaObj(this,Con,db);

        actproc="Moneda";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.moneda_ins(holder.items.get(i)));
                dbld.add(exp.moneda_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actNivelPrecio() {
        clsP_nivelprecioObj holder=new clsP_nivelprecioObj(this,Con,db);

        actproc="NivelPrecio";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
               dbld.add(exp.nivelprecio_ins(holder.items.get(i)));
               dbld.add(exp.nivelprecio_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actBanco() {
        clsP_bancoObj holder=new clsP_bancoObj(this,Con,db);

        actproc="Banco";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) {
                return true;
            }

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.banco_ins(holder.items.get(i)));
                dbld.add(exp.banco_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actCaja() {
        clsP_rutaObj holder=new clsP_rutaObj(this,Con,db);

        actproc="Caja";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.ruta_ins(holder.items.get(i)));
                dbld.add(exp.ruta_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actCliente() {
        clsP_clienteObj holder=new clsP_clienteObj(this,Con,db);

        actproc="Cliente";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.cliente_ins(holder.items.get(i)));
                dbld.add(exp.cliente_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actDesc() {
        clsP_descuentoObj holder=new clsP_descuentoObj(this,Con,db);

        actproc="Descuento";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.desc_ins(holder.items.get(i)));
                dbld.add(exp.desc_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actEmpresa() {
        clsP_empresaObj holder=new clsP_empresaObj(this,Con,db);

        actproc="Empresa";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.empresa_ins(holder.items.get(i)));
                dbld.add(exp.empresa_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actFamilia() {
        clsP_lineaObj holder=new clsP_lineaObj(this,Con,db);

        actproc="Familia";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.linea_ins(holder.items.get(i)));
                dbld.add(exp.linea_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actImpuesto() {
        clsP_impuestoObj holder=new clsP_impuestoObj(this,Con,db);

        actproc="Impuesto";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.impuesto_ins(holder.items.get(i)));
                dbld.add(exp.impuesto_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }

    public boolean actMedia() {
        clsP_mediapagoObj holder=new clsP_mediapagoObj(this,Con,db);

        actproc="Media pago";acterr=actproc;

        try {
            fprog = actproc;wsStask.onProgressUpdate();

            holder.fill();
            if (holder.count==0) return true;

            dbld.clear();
            for (int i = 0; i <holder.count; i++) {
                dbld.add(exp.media_ins(holder.items.get(i)));
                dbld.add(exp.media_upd(holder.items.get(i)));
            }

            if (actualize()==0) {
                fstr=sstr;return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            fstr = e.getMessage();return false;
        }
    }


    //endregion

	//region WS Envio Handling Methods
	
	public void wsSendExecute(){

		running=1;fstr="No connect";scon=0;
        errflag=false;

		try {

			if (getTest()==1) scon=1;

			if (scon==1) {
				fstr="Envío completo.";
                if (sendmode==0) {
                    if (!sendData()) fstr="Envio incompleto : "+sstr;
                } else {
                    if (!Actualiza()) fstr="Actualizacion incompleta : "+sstr;
                }
			} else {
				fstr="No se puede conectar al web service : "+sstr;
			}
		} catch (Exception e) {
			scon=0;fstr="No se puede conectar al web service. "+e.getMessage();
		}
	}
			
	public void wsSendFinished(){
				
		barInfo.setVisibility(View.INVISIBLE);
		lblParam.setVisibility(View.INVISIBLE);
		running=0;
		
		//senv="Envio completo\n";

		try{
            if (scon==0) {
                lblInfo.setText(fstr);writeErrLog(fstr);
                mu.msgbox(fstr);
				lblInfo.setText(fstr);
				isbusy = 0;
				barInfo.setVisibility(View.INVISIBLE);
				addlog("Envío", fterr + " " + fstr, esql);
				return;
            }

			if (!errflag) {
				lblInfo.setText(" ");
				msgResultEnvio(senv);
			} else {

                if (sendmode==0) {
                    lblInfo.setText(fterr);
                    barInfo.setVisibility(View.INVISIBLE);
                    mu.msgbox("Ocurrió error : \n" + fterr );
                    addlog("Envío", fterr, esql);
                } else {
                    lblInfo.setText("Error de actualización");
                    barInfo.setVisibility(View.INVISIBLE);
                    mu.msgbox("Ocurrió error : \n"+acterr+"\n" +fstr );
                    addlog("Actualizacion", acterr, fstr);
                }
			}

			visibilidadBotones();
			//if (!errflag) ComWS.super.finish();

			isbusy=0;

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}
			
	private class AsyncCallSend extends AsyncTask<String, Void, Void> {

		@Override
	    protected Void doInBackground(String... params) {

			try {
				Looper.prepare();
				wsSendExecute();
			} catch (Exception e) {
				if (scon == 0) {
					fstr = "No se puede conectar al web service : " + sstr;
					//lblInfo.setText(fstr);
				}
			}

	        return null;
	    }
	 
	    @Override
	    protected void onPostExecute(Void result) {
			try {
				wsSendFinished();
				Looper.loop();
			}catch (Exception e) {
				Log.d("onPostExecute", e.getMessage());
			}
		}
	 
        @Override
        protected void onPreExecute() {
    		try {
    		} catch (Exception e) {}
        }

        @Override
        protected void onProgressUpdate(Void... values) {
    		try {
    			lblInfo.setText(fprog);
    		} catch (Exception e) { }
        }
	 
    }
	
	//endregion

	//region WS Confirm Methods

	private void sendConfirm() {
		Cursor dt;

		try {
			try {
				sql = "SELECT DOCUMENTO FROM P_STOCK";
				dt = Con.OpenDT(sql);

				if (dt.getCount() > 0) {
					dt.moveToFirst();
					docstock = dt.getString(0);
				} else {
					docstock = "";
				}
			} catch (Exception e) {
				addlog(new Object() {
				}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
				msgbox(new Object() {
				}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
			}

			//#HS_20181126_1603 Cambie el getActDate por getFechaActual
			sql = "UPDATE P_RUTA SET EMAIL='" + du.getActDate() + "'";
			db.execSQL(sql);

			Handler mtimer = new Handler();
			Runnable mrunner = new Runnable() {
				@Override
				public void run() {
					showprogress = false;
					wsCtask = new AsyncCallConfirm();
					wsCtask.execute();
				}
			};
			mtimer.postDelayed(mrunner, 500);
		}catch (Exception  e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(), "");
		}
	}

	//endregion
	
	//region WS Confirm Handling Methods

	public void wsConfirmExecute(){
		String univdate=du.univfecha(du.getActDate());
		isbusy=1;
		
		try {
			conflag=0;
					
			dbld.clear();
			dbld.add("DELETE FROM P_DOC_ENVIADOS_HH WHERE DOCUMENTO='"+docstock+"'");
			dbld.add("INSERT INTO P_DOC_ENVIADOS_HH VALUES ('"+docstock+"','"+ActRuta+"','"+univdate+"',1)");
			dbld.add("UPDATE P_RUTA SET IDIMPRESORA='"+parImprID+"',NUMVERSION='"+gl.parVer+"',ARQUITECTURA='ANDR' WHERE CODIGO='" + gl.ruta + "'");
			dbld.add("INSERT INTO P_BITACORA_VERSIONHH (RUTA,FECHA,NUMVERSION,ARQUITECTURA) " +
					"VALUES('"+gl.ruta+"','"+ du.univfechaseg() +"','"+gl.parVer+"','ANDR')");

			if (commitSQL()==1) conflag=1; else conflag=0;
					
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			fterr+="\n"+e.getMessage();
			dbg=e.getMessage();
		}
	}

	public void wsConfirmFinished(){
		try {
			isbusy = 0;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private class AsyncCallConfirm extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			try {
				wsConfirmExecute();
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				wsConfirmFinished();
			}catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(), "");
			}

		}

		@Override
		protected void onPreExecute() {
			try {
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			try {
			} catch (Exception e) {}
		}

	}	

	//endregion
	
	//region Aux

    private boolean setComParams() {
		String ss;

		try{

			ss=txtEmp.getText().toString().trim();
			if (mu.emptystr(ss)) {
				mu.msgbox("La empresa no esta definida.");return false;
			}
			emp =ss;

			ss=txtWS.getText().toString().trim();
			//ss="http://192.168.1.137/MPos/wsMPos.asmx";
			if (mu.emptystr(ss) || ss.equalsIgnoreCase("*")) {
				mu.msgbox("La dirección de Web service no esta definida.");return false;
			}
			URL=ss;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
		return true;
	}

	private int getDocCount(String ss,String pps) {

	    Cursor DT;
		int cnt = 0;
		String st;

		try {

			sql=ss;
			DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
				DT.moveToFirst();
            	cnt=DT.getInt(0);
            }

            if (cnt>0) {
				st=pps+" "+cnt;
				sp=sp+st+", ";
			}

		} catch (Exception e) {
			//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			//mu.msgbox(sql+"\n"+e.getMessage());
		}

        return cnt;

    }

	private String getMac() {
		WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		return info.getMacAddress();
	}
	
	private int ultCorel() {
		Cursor DT;
		int crl = 0;
		
		try {
			sql="SELECT CORELULT FROM P_COREL";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0){
				DT.moveToFirst();
				crl=DT.getInt(0);
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}	
		
		return crl;
	}

	private String ultSerie(){
		Cursor DT;
		String serie="";

		try{
			sql="SELECT SERIE FROM P_COREL";
			DT=Con.OpenDT(sql);

			if(DT.getCount()>0) {
				DT.moveToFirst();
				serie = DT.getString(0);
			}

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox("ultSerie(): "+e.getMessage());
		}

		return serie;
	}

	private int Get_Fecha_Inventario() 	{
		Cursor DT;
		int fecha = 0;

		try {

			sql="SELECT IFNULL(EMAIL,0) AS FECHA FROM P_RUTA";
			DT=Con.OpenDT(sql);

			if(DT.getCount()>0){
                DT.moveToFirst();
				fecha=DT.getInt(0);
				if (fecha==0) 				{
					fecha = 1001010000 ;//#HS_20181129_0945 Cambie los valores de fecha porque deben se yymmdd hhmm
				}
			}

		} catch (Exception e) {
			//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
		return fecha;
	}

    private int ultimoCierreFecha() {
        Cursor DT;
        int rslt=0;

        try {
            sql="SELECT val1 FROM FinDia";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            rslt=DT.getInt(0);
        } catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            rslt=0;
        }

        return rslt;
    }

	private void visibilidadBotones() {

		try {
			lblEnv.setVisibility(View.VISIBLE);
			imgEnv.setVisibility(View.VISIBLE);

			lblRec.setVisibility(View.VISIBLE);
			imgRec.setVisibility(View.VISIBLE);

            lblEnvM.setVisibility(View.VISIBLE);
            imgEnvM.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
		}
	}
	
	private void paramsExtra() {
		try {
			AppMethods app=new AppMethods(this,gl,Con,db);
			app.parametrosExtra();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(e.getMessage());
		}
	}

	private void writeErrLog(String errstr) {
		BufferedWriter writer = null;
		FileWriter wfile;

		try {
			String fname = Environment.getExternalStorageDirectory()+"/roaderror.txt";

			wfile=new FileWriter(fname,false);
			writer = new BufferedWriter(wfile);
			writer.write(errstr);writer.write("\r\n");
			writer.close();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void restartApp(){
		try{
			PackageManager packageManager = this.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
			ComponentName componentName = intent.getComponent();
			Intent mainIntent =Intent.makeRestartActivityTask(componentName);
			//Intent mainIntent = IntentCompat..makeRestartActivityTask(componentName);
			this.startActivity(mainIntent);
			System.exit(0);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public boolean ExistenDatosSinEnviar(){

        try {

            int cantFact,CantPedidos,CantCobros,CantDevol,CantInventario;

            clsAppM = new AppMethods(this, gl, Con, db);

            cantFact = clsAppM.getDocCountTipo("Facturas",true);
            CantPedidos = clsAppM.getDocCountTipo("Pedidos",true);
            CantCobros = clsAppM.getDocCountTipo("Cobros",true);
            CantDevol = clsAppM.getDocCountTipo("Devoluciones",true);
            CantInventario = clsAppM.getDocCountTipo("Inventario",true);

           return  ((cantFact>0) || (CantCobros>0) || (CantDevol>0) || (CantPedidos>0) || (CantInventario>0));

        } catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(e.getMessage());
            return false;
        }

    };

	public boolean ExisteInventario(){

		try {

			int CantInventario;

			clsAppM = new AppMethods(this, gl, Con, db);

			CantInventario = clsAppM.getDocCountTipo("Inventario",false);

			return  ((CantInventario>0));

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(e.getMessage());
			return false;
		}

	};

	public boolean ExistenDatos(){

		try {

			int cantFact,CantPedidos,CantCobros,CantDevol,CantInventario;

			clsAppM = new AppMethods(this, gl, Con, db);

			cantFact = clsAppM.getDocCountTipo("Facturas",false);
			CantPedidos = clsAppM.getDocCountTipo("Pedidos",false);
			CantCobros = clsAppM.getDocCountTipo("Cobros",false);
			CantDevol = clsAppM.getDocCountTipo("Devoluciones",false);
			CantInventario = clsAppM.getDocCountTipo("Inventario",false);

			return  ((cantFact>0) || (CantCobros>0) || (CantDevol>0) || (CantPedidos>0) || (CantInventario>0));

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(e.getMessage());
			return false;
		}

	};

	private void BorraDatosAnteriores(String msg) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage(msg);
			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					claseFindia.eliminarTablasD();
					msgAskConfirmaRecibido();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

	public void SetStatusRecTo(String estado) 	{
		try
		{
			sql = "UPDATE P_RUTA SET PARAM2='" + StringUtils.trim(estado) + "'";
			db.execSQL(sql);
		}
		catch (Exception ex)
		{
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),ex.getMessage(),"");
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+ " " + ex.getMessage());
		}

	}

    public void SetStatusRecToTrans(String estado)    {
        try         {
            sql = "UPDATE P_RUTA SET PARAM2='" + StringUtils.trim(estado) + "'";
            dbT.execSQL(sql);
        } catch (Exception ex)  {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),ex.getMessage(),"");
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+ " " + ex.getMessage());
        }
    }

	public String GetStatusRec() 	{
		Cursor DT;
		String vGetStatusRec = "";

		try 			{
				sql = "SELECT PARAM2 FROM P_RUTA ";
				DT = Con.OpenDT(sql);

				if (DT.getCount()> 0) 				{
					DT.moveToFirst();
					vGetStatusRec = DT.getString(0);
				}
			}

		catch (Exception ex) {
				Log.d("GetStatusRec","Something happend here " + ex.getMessage());
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+ " " + ex.getMessage());
		}

		return  vGetStatusRec;
	}

    private void Eliminatablas(){
        boolean Eliminadas = false;
        try{

            claseFindia.updateFinDia(du.getActDate());
            claseFindia.updateComunicacion(2);

            ActualizaStatcom();
            Eliminadas = claseFindia.eliminarTablasD();

            if (Eliminadas){
                mu.msgbox("Envío de datos correcto");
            }

            visibilidadBotones();

        }catch (Exception e){

        }
    }

    private boolean puedeComunicar() {

        boolean vPuedeCom = false;

        try {

            //#CKFK 20190304 Agregué validación para verificar si ya se realizó la comunicación de los datos.
            if (gl.banderafindia) {

                return ((claseFindia.getImprimioCierreZ() == 7));

            } else {
                return true;
            }

        } catch (Exception ex) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage(), "");
        }

        return vPuedeCom;
    }

    //endregion

    //region Dialogs

    public void askSendCorrect() {

        try {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Envio");
            dialog.setMessage("¿Comunicación correcta?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askOkCom();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    askIncorrect();
                }
            });

            dialog.show();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    public void askIncorrect() {

        try {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Envio");
            dialog.setMessage("¿Está seguro de que la comunicacion NO fue correcta?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Eliminatablas();
                }
            });

            dialog.show();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    private void askOk() {

        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Envio");//	alert.setMessage("Serial");

            final TextView input = new TextView(this);
            alert.setView(input);

            input.setText("Archivo de datos creado conecte el dispotivo al ordenador");
            input.requestFocus();

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    askSendCorrect();
                }
            });

            alert.show();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }


    }

    private void askOkCom() {

        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Envio");//	alert.setMessage("Serial");

            final TextView input = new TextView(this);
            alert.setView(input);

            input.setText("Está seguro de que la comunicación fue correcta");
            input.requestFocus();

            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Eliminatablas();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    return;
                }
            });

            alert.show();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }


    }

    private void msgResultEnvio(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskExit(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    if (gl.modoadmin) {
                        finish();
                        //restartApp();
                    } else {
                        finish();
                    };
                }
            });

            dialog.show();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskExitComplete() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage("Está seguro de salir de la aplicación?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });

            dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ;
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskConfirmaRecibido(){

        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Recepción");
            dialog.setMessage("¿Recibir datos nuevos?");

            dialog.setPositiveButton("Recibir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    runRecep();
                }
            });

            dialog.setNegativeButton("Cancelar", null);

            dialog.show();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskSinLicencia(){

        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Licencia");
            dialog.setMessage("El dispositivo no tiene licencia válida");

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    restartApp();
                }
            });

            dialog.show();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    //endregion

	//region Activity Events
	
	@Override
	public void onBackPressed() {
		try{
			if (isbusy==0) {
				if (gl.modoadmin) {
					msgAskExitComplete();
				} else {
					super.onBackPressed();
				}
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	/*@Override
	protected void onResume() {
		super.onResume();
		try {
			this.wakeLock.acquire();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"wakelock");
		}
	}

	@Override
	protected void onPause() {
		try {
			this.wakeLock.release();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"wakelock");
		}
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();

		this.wakelock.release();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		this.wakelock.release();
	}
	*/

	//endregion

}
