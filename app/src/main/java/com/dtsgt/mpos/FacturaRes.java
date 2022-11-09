package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.core.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_factura_felObj;
import com.dtsgt.classes.clsD_facturaprObj;
import com.dtsgt.classes.clsD_facturarObj;
import com.dtsgt.classes.clsD_facturasObj;
import com.dtsgt.classes.clsD_ordencObj;
import com.dtsgt.classes.clsD_ordendObj;
import com.dtsgt.classes.clsDescGlob;
import com.dtsgt.classes.clsDocCuenta;
import com.dtsgt.classes.clsDocDevolucion;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.classes.clsP_cortesiaObj;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_linea_impresoraObj;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_prodrecetaObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_comandaObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_factrecetaObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.fel.FELFactura;
import com.dtsgt.ladapt.ListAdaptTotals;
import com.dtsgt.webservice.srvCommit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FacturaRes extends PBase {

	private ListView listView;
	private TextView lblPago,lblFact,lblMPago,lblCred, lblCard,lblPend,lblMonto,lblKeyDP;
	private TextView lblTotal,lblPEfect,lblPCard,lblPreimp,lblProp,lblVuelto;
	private ImageView imgBon,imgMPago,imgCred, imgCard, imgPend,imgPreimp,imgProp;
	private EditText txtVuelto;
	private RelativeLayout rl_facturares;

	private List<String> spname = new ArrayList<String>();
	private ArrayList<clsClasses.clsCDB> items= new ArrayList<clsClasses.clsCDB>();
	private ListAdaptTotals adapter;
	
	private Runnable printcallback,printclose,printexit;

    private clsP_prodrecetaObj P_prodrecetaObj;
    private clsT_factrecetaObj T_factrecetaObj;
    private clsT_comboObj T_comboObj;
    private clsP_productoObj P_productoObj;
    private clsD_ordendObj D_ordendObj;
    private clsD_ordencObj D_ordencObj;
    private clsP_linea_impresoraObj P_linea_impresoraObj;
    private clsP_impresoraObj P_impresoraObj;
    private clsT_comandaObj T_comandaObj;
	
	private clsDescGlob clsDesc;
	private printer prn;
	private printer prn_nc;
	private clsDocFactura fdoc;
	private clsDocDevolucion fdev;
	private AppMethods app;
    private clsKeybHandler khand;
	private clsRepBuilder rep;

	private long fecha,fechae;
	private int fcorel,clidia, Nivel_Media_Pago,idtransbar;
	private boolean EsNivelPrecioDelivery =false;
	private String itemid,cliid,corel,sefect,fserie,desc1,svuelt,corelNC,idfel,osql;
	private int cyear, cmonth, cday, dweek,stp=0,brw=0,notaC,impres,recid,ordennum,prodlinea;

	private double dmax,dfinmon,descpmon,descg,descgmon,descgtotal,tot,propina,propinaperc,propinaext,pend,stot,stot0;
	private double dispventa,falt,descimpstot,descmon,descimp,totimp,totperc,credito,descaddmonto;
	private boolean acum,cleandprod,peexit,pago,saved,rutapos,porpeso,pendiente,pagocompleto=false;
    private boolean horiz=true;

	//@SuppressLint("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {

            appGlobals ggl=((appGlobals) this.getApplication());

            if (ggl.mesero_precuenta) {
                if (pantallaHorizontal()) {
                    setContentView(R.layout.activity_factura_res_precue);horiz=true;
                } else {
                    setContentView(R.layout.activity_factura_res_ver);horiz=false;
                }
            } else {
                if (pantallaHorizontal()) {
                    setContentView(R.layout.activity_factura_res);horiz=true;
                } else {
                    setContentView(R.layout.activity_factura_res_ver);horiz=false;
                }
            }
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

		super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
		lblPago = (TextView) findViewById(R.id.TextView01);
		lblFact = (TextView) findViewById(R.id.lblFact);
		lblMPago = (TextView) findViewById(R.id.lblCVence);
		lblCred = (TextView) findViewById(R.id.lblPend);
		lblCard = (TextView) findViewById(R.id.textView4);
        lblMonto = (TextView) findViewById(R.id.lblCant2);lblMonto.setText("");
        lblKeyDP = (TextView) findViewById(R.id.textView110);
        lblTotal = (TextView) findViewById(R.id.lblFact3);
        lblPEfect = (TextView) findViewById(R.id.textView166);
        lblPCard = (TextView) findViewById(R.id.textView167);
        lblPend = (TextView) findViewById(R.id.textView197);
        lblPreimp= (TextView) findViewById(R.id.textView220);
        lblProp= (TextView) findViewById(R.id.textView240);

		imgBon = (ImageView) findViewById(R.id.imageView6);
		imgMPago = (ImageView) findViewById(R.id.btnImp);
		imgCred = (ImageView) findViewById(R.id.imageView3);
		imgCard = (ImageView) findViewById(R.id.imageView2);
        imgPend = (ImageView) findViewById(R.id.imageView84);
        imgPreimp = (ImageView) findViewById(R.id.imageView105);
        imgProp = (ImageView) findViewById(R.id.imageView111);

		rl_facturares=(RelativeLayout)findViewById(R.id.relativeLayout1);
		rl_facturares.setVisibility(View.VISIBLE);

        T_factrecetaObj=new clsT_factrecetaObj(this,Con,db);
        P_prodrecetaObj=new clsP_prodrecetaObj(this,Con,db);
        T_comboObj=new clsT_comboObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);
        P_linea_impresoraObj=new clsP_linea_impresoraObj(this,Con,db);
        P_impresoraObj=new clsP_impresoraObj(this,Con,db);
        T_comandaObj=new clsT_comandaObj(this,Con,db);

		rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

		lblVuelto = new TextView(this,null);
		txtVuelto = new EditText(this,null);

		cliid=gl.cliente;
		rutapos=gl.rutapos;
		Nivel_Media_Pago =gl.media;
		EsNivelPrecioDelivery = gl.EsNivelPrecioDelivery;

		credito=gl.credito;
        idfel=gl.peFEL;

		try {
			if (!gl.nummesapedido.equalsIgnoreCase("0")) {
			}
		} catch (Exception e) {
			gl.nummesapedido="";
		}

		gl.cobroPendiente = false;
		dispventa = gl.dvdispventa;dispventa=mu.round(dispventa,2);
		notaC = gl.tiponcredito;
        pendiente=false;

        if (!gl.pelCaja) {
            imgPreimp.setVisibility(View.INVISIBLE);lblPreimp.setVisibility(View.INVISIBLE);
        }

        if (!gl.numero_orden.equalsIgnoreCase(" ")) {
            imgPreimp.setVisibility(View.VISIBLE);lblPreimp.setVisibility(View.VISIBLE);
        }

        imgProp.setVisibility(View.INVISIBLE);lblProp.setVisibility(View.INVISIBLE);
        if (gl.peRest) {
            imgProp.setVisibility(View.VISIBLE);lblProp.setVisibility(View.VISIBLE);
            //if (gl.pePropinaFija && gl.pePropinaPerc<=0) {
            //    imgProp.setVisibility(View.INVISIBLE);lblProp.setVisibility(View.INVISIBLE);
            //}
        }

        mu.currsymb(gl.peMon);

		app = new AppMethods(this, gl, Con, db);
        khand=new clsKeybHandler(this,lblMonto,lblKeyDP);

        app.parametrosExtra();

        imgCred.setVisibility(View.VISIBLE);lblCred.setVisibility(View.VISIBLE);
        imgMPago.setVisibility(View.INVISIBLE);lblMPago.setVisibility(View.INVISIBLE);

        if (!gl.pePedidos) {
            imgPend.setVisibility(View.INVISIBLE);lblPend.setVisibility(View.INVISIBLE);
        }

        if (Nivel_Media_Pago ==4) {
            if (credito<=0 || gl.facturaVen != 0) {
                imgCred.setVisibility(View.INVISIBLE);lblCred.setVisibility(View.INVISIBLE);
            } else if(credito > 0){
                imgCred.setVisibility(View.VISIBLE);lblCred.setVisibility(View.VISIBLE);
            }
        }

        fecha=du.getActDateTime();
		fechae=fecha;

		dweek=mu.dayofweek();

		clsDesc=new clsDescGlob(this);

		descpmon=totalDescProd(); //descpmon=0;
		dmax=clsDesc.dmax;
		acum=clsDesc.acum;
        descaddmonto=0;

		try {
			db.execSQL("DELETE FROM T_PAGO");
		} catch (SQLException e) {}

		processFinalPromo();
        pagoPendiente();

		printcallback= new Runnable() {

		    public void run() {

				if (notaC==2){

					String vModo=(gl.peModal.equalsIgnoreCase("TOL")?"TOL":"*");
					fdev.buildPrint(gl.dvcorrelnc,0, vModo);

					SystemClock.sleep(3000);
					prn_nc.printnoask(printclose, "printnc.txt");
					SystemClock.sleep(3000);
					if (impres>0) prn_nc.printnoask(printclose, "printnc.txt");
				}

				askPrint();
		    }
		};

		printclose= new Runnable() {
		    public void run() {
		    	//FacturaRes.super.finish();
		    }
		};

		printexit= new Runnable() {
			public void run() {
                gl.ventalock=false;
			    FacturaRes.super.finish();
			}
		};

		prn=new printer(this,printexit,gl.validimp);
		prn_nc=new printer(this,printclose,gl.validimp);

		fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"",gl.peComboDet);
		fdoc.deviceid =gl.deviceId;

		fdev=new clsDocDevolucion(this,prn_nc.prw,gl.peMon,gl.peDecImp, "printnc.txt");
		fdev.deviceid =gl.deviceId;

		saved=false;

		assignCorel();

		cliPorDia();

		setHandlers();

        txtVuelto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        pend=tot;
        if (tot>credito) credito=0;

        if (credito<=0) {
            imgCred.setVisibility(View.INVISIBLE);
            lblCred.setVisibility(View.INVISIBLE);
        }

        //if (gl.peImpOrdCos) msgAskComanda("Imprimir comanda");

        //if (gl.mesero_precuenta) prnCuenta(null);

    }

	//region Events

	public void prevScreen(View view) {
		try{
			clearGlobals();
			if(gl.dvbrowse!=0){
				gl.dvbrowse =0;
			}
			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void paySelect(View view) {
        pendiente=false;
		try{

			if (fcorel==0) {
				msgbox("No existe un correlativo disponible, no se puede emitir factura");return;
			}

			gl.pagoval=tot;
			gl.pagolim=tot;
			gl.pagocobro=false;
			browse=1;

			Intent intent = new Intent(this,Pago.class);
			startActivity(intent);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("paySelect: " + e.getMessage());
		}
	}

	public void payCash(View view) {
        pendiente=false;
		try{

			if (fcorel==0) {
				msgbox("No existe un correlativo disponible, no se puede emitir factura");return;
			}

			//inputEfectivo();
			inputVuelto();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("payCash: " + e.getMessage());
		}
	}

    public void payCard(View view) {
        pendiente=false;
		gl.modo_cortesia=false;

        try {
            if (fcorel==0) {
                msgbox("No existe un correlativo disponible, no se puede emitir factura");return;
            }

            pagoPendiente();

            if (gl.total_pago>0) {
                browse=3;
                startActivity(new Intent(this,PagoTarjeta.class));
            } else {
                checkPago();
            }

         } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox("payCard: " + e.getMessage());
        }
    }

    public void payCred(View view) {
        pendiente=false;
		try{

			if (fcorel==0) {
				msgbox("No existe un correlativo disponible, no se puede emitir factura");return;
			}

            msgAskCredito();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("payCred: " + e.getMessage());
		}
	}

    public void pendientePago(View view){
        if (fcorel==0) {
            msgbox("No existe un correlativo disponible, no se puede emitir factura");return;
        }
        askPendientePago();
    }

    public void delPay(View view) {
	    askDelPago();
	}

	public void doDesc(View view) {
		browse=6;
		gl.total_factura_previo_descuento = stot;
		startActivity(new Intent(this,DescMonto.class));
		/*
        browse=5;
        startActivity(new Intent(this,ValidaSuper.class));
        */
    }

    public void prnCuenta(View view) {
        askPrecuenta();
    }

    public void showBon(View view) {
		try{
			Intent intent = new Intent(this,BonVenta.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

    public void DaVuelto(View view) {
        try{
            Davuelto();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void doPropina(View view) {
        ingresaPropina();
    }

    public void pago100(View view){
        khand.val="100";
        validaPagoEfectivo();
    }

    public void pago50(View view){
        khand.val="50";
        validaPagoEfectivo();
    }

    public void pago20(View view){
        khand.val="20";
        validaPagoEfectivo();
    }

    public void pago10(View view){
        khand.val="10";
        validaPagoEfectivo();
    }

    public void pago5(View view){
        khand.val="5";
        validaPagoEfectivo();
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            validaPagoEfectivo();
        }
    }

    private void setHandlers(){

		try{

			listView.setOnTouchListener(new SwipeListener(this) {
				public void onSwipeRight() {
					prevScreen(null);
				}
				public void onSwipeLeft() {
					if (imgCard.getVisibility()==View.VISIBLE) {
						payCash(null);
					}
				}
			});

			txtVuelto.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {}

				public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

				public void onTextChanged(CharSequence s, int start,int before, int count) {
					//Davuelto();
				}

			});

			txtVuelto.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {

					if ((keyCode == KeyEvent.KEYCODE_ENTER)) {

						DaVuelto(v);

						return true;

					}else if ((keyCode == KeyEvent.KEYCODE_DEL)){
						lblVuelto.setText("");
					}

					return false;
				}

			});

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion

	//region Main

	private void processFinalPromo(){

		try {

            descg=gl.descglob;
            descgtotal=gl.descgtotal;

		    descgmon=mu.round2(descg*descgtotal/100);
            if (descgmon>0) msgboxex("Se aplicó descuento adicional por volumen de venta.\n Monto descuento : "+mu.frmcur(descgmon));

			totalOrder();

			/*
			if (descg>0) {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						showPromo();
					}
				}, 300);
			}
			*/

		} catch (Exception e){
			mu.msgbox("processFinalPromo: " + e.getMessage());
		}

	}

	public void showPromo(){

		try {

			browse=1;
			gl.promprod="";
			gl.promcant=0;
			gl.promdesc=descg;

			Intent intent = new Intent(this,DescBon.class);
			startActivity(intent);

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox( e.getMessage());
		}

	}

	private void updDesc(){

		try{

			descg=gl.promdesc;
			//descgmon=(double) (stot0*descg/100);
			descgmon=(double) (descg*descgtotal/100);
			totalOrder();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void totalOrder(){

		double dmaxmon;

		cleandprod=false;

		try{

			if (acum) {
                descimp=descgmon+descaddmonto;
				dfinmon=descpmon+descgmon;
				cleandprod=false;
			} else {
				if (descpmon>=descgmon) {
					dfinmon=descpmon+descaddmonto;
					cleandprod=false;
				} else {
					dfinmon=descgmon+descaddmonto;
					cleandprod=true;
				}
				descimp=descaddmonto;
			}

			dmaxmon=(double) (stot0*dmax/100);
			if (dmax>0) {
				if (dfinmon>dmaxmon) dfinmon=dmax;
			}

			descmon=mu.round2(dfinmon);
			stot=mu.round2(stot0);

			if (!EsNivelPrecioDelivery){
				propinaperc=gl.pePropinaPerc;
				if (gl.pePropinaFija) {
					propina=stot*gl.pePropinaPerc/100;propina=mu.round2(propina);
				} else {
					propina=gl.propina_valor;
				}
				propina=mu.round2(propina+propinaext);
			}else{
				propina=0;
			}

			if (gl.parallevar) propina=0;
			if (gl.pickup) propina=0;
			if (gl.sin_propina) propina=0;

			fillTotals();

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("totalOrder: " + e.getMessage());
		}

	}

	private void fillTotals() {

		clsClasses.clsCDB item;

		items.clear();

		try {

			if (gl.sinimp) {

				totimp=mu.round2(totimp);
				stot=stot-totimp;

				totperc=stot*(gl.percepcion/100);
				totperc=mu.round2(totperc);

                descmon=descmon+descaddmonto;
				tot=stot+totimp-descmon+totperc;
				tot=tot+propina;
				tot=mu.round2(tot);

				item = clsCls.new clsCDB();
				item.Cod="Subtotal";item.Desc=mu.frmcur(stot);item.Bandera=0;
				items.add(item);

				item = clsCls.new clsCDB();
				item.Cod="Impuesto";item.Desc=mu.frmcur(totimp);item.Bandera=0;
				items.add(item);

				if (gl.contrib.equalsIgnoreCase("C")) {
					item = clsCls.new clsCDB();
					item.Cod="Percepción";item.Desc=mu.frmcur(totperc);item.Bandera=0;
					items.add(item);
				}

				item = clsCls.new clsCDB();
				item.Cod="Descuento";item.Desc=mu.frmcur(-descmon);item.Bandera=0;
				items.add(item);

                if (propina>0){

					item = clsCls.new clsCDB();
                    item.Cod="Propina";item.Desc=mu.frmcur(propina);item.Bandera=0;
					items.add(item);

					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot-dispventa);item.Bandera=1;
					items.add(item);

				} else {
					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot);item.Bandera=1;
					items.add(item);
				}

			} else {

				totimp=mu.round2(totimp);
                descmon=descmon+descaddmonto;
				tot=stot-descmon;
                tot=tot+propina;
				tot=mu.round2(tot);

				item = clsCls.new clsCDB();
				item.Cod="Subtotal";item.Desc=mu.frmcur(stot);item.Bandera=0;
				items.add(item);

				item = clsCls.new clsCDB();
				item.Cod="Descuento";item.Desc=mu.frmcur(-descmon);item.Bandera=0;
				items.add(item);

				if (propina>0){
					item = clsCls.new clsCDB();
					item.Cod="Propina";item.Desc=mu.frmcur(propina);item.Bandera=0;
					items.add(item);

					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot-dispventa);item.Bandera=1;
					items.add(item);
				} else{
				    item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot);item.Bandera=1;
					items.add(item);
				}
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

		selidx=items.size()-1;
		adapter=new ListAdaptTotals(this,items,horiz);
		listView.setAdapter(adapter);

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.smoothScrollToPosition(selidx);
        }

        lblTotal.setText(mu.frmcur(tot));
	}

 	private void finishOrder() {

        if (gl.rol==4) {
            toast("El mesero no puede realizar venta en esta pantalla");return;
        }

        if (!saved) {
			if (!saveOrder()) return;
		}

		if (!gl.numero_orden.isEmpty()) {
		    if (gl.numero_orden.length()>3) {
		        enviaPago(gl.caja_est_pago);
            }
        }

		completaEstadoOrden();

        gl.cliposflag=false;
        gl.InvCompSend=false;
        gl.delivery =false;
        gl.pickup = false;
		gl.sin_propina=false;

        if (gl.pelDespacho) generaOrdenDespacho();

        if (!app.usaFEL()) {

            if ( gl.peEnvio) {
                if (isNetworkAvailable()) {
                    impresionDocumento();
                } else {
                    toast("No hay conexion a internet");
                    impresionDocumento();
                }
            } else {
                gl.InvCompSend=true;
                impresionDocumento();
            }

            /*
            gl.InvCompSend=true;
            impresionDocumento();
            if ( gl.peEnvio) {
                browse=4;
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            }
            */
        } else {
            //if (isNetworkAvailable()) {
                browse=2;
                gl.felcorel=corel;gl.feluuid="";
                startActivity(new Intent(this, FELFactura.class));
				/*
            } else {
                marcaFacturaContingencia();
                toast("No hay conexion a internet");
                impresionDocumento();
            }
            */

        }

	}

	private void impresionDocumento() {

        String fname = Environment.getExternalStorageDirectory()+"/print.txt";

		try {

            gl.nombre_mesero="";

            //#CKFK 20210705
			fdoc.es_pickup=gl.pickup;
			fdoc.es_delivery=gl.delivery;

            if (gl.mesero_venta>0) {
                clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
                VendedoresObj.fill("WHERE CODIGO_VENDEDOR="+gl.mesero_venta);
                if (VendedoresObj.count>0) gl.nombre_mesero=VendedoresObj.first().nombre;
            }

			rl_facturares.setVisibility(View.INVISIBLE);

			if(gl.dvbrowse!=0) gl.dvbrowse =0;

			impres=0;

			if (app.impresora()) {

			    fdoc.impresionorden=gl.pelOrdenComanda;
				fdoc.parallevar=gl.parallevar;
				if (!gl.nummesapedido.equalsIgnoreCase("0")) {
					fdoc.impresionorden=true;
					fdoc.parallevar=true;
				}
			    fdoc.factsinpropina=gl.peFactSinPropina;
			    fdoc.propperc=gl.pePropinaPerc;
                fdoc.modorest=gl.peRest;
				fdoc.textopie=gl.peTextoPie;
                fdoc.nommesero=gl.nombre_mesero;

                fdoc.buildPrint(corel, 0,"",gl.peMFact);
				gl.QRCodeStr = fdoc.QRCodeStr;

             	app.doPrint(gl.peNumImp,0);

            }

            gl.impresion_comanda=false;
			if (gl.pelOrdenComanda) {
                gl.impresion_comanda=true;
			    imprimeComanda();
            }

	        gl.iniciaVenta=true;
            gl.ventalock=false;

            if (!app.usaFEL()) {
                if ( gl.peEnvio) {
                    Handler mtimer = new Handler();
                    Runnable mrunner=new Runnable() {
                        @Override
                        public void run() {
                            gl.autocom = 1;
                            startActivity(new Intent(FacturaRes.this,WSEnv.class));;
                        }
                    };
                    mtimer.postDelayed(mrunner,5000);
                }
            }

			try {
				db.execSQL("DELETE FROM T_ORDEN_MOD WHERE COREL='"+gl.ordcorel+"'");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_ORDEN_ING WHERE COREL='"+gl.ordcorel+"'");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_VENTA_MOD");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_VENTA_ING");
			} catch (Exception e) {}

			super.finish();

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("impresionDocumento : "  + e.getMessage());

            gl.ventalock=false;
			super.finish();
		}
	}

	private void askPrintSegunda() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Pos");
			dialog.setMessage("Imprimír copia?");

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					app.doPrint(1,0);
					finish();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			dialog.show();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void singlePrint() {
		try{
			prn.printask(printcallback);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

 	}

	private boolean saveOrder() {

        clsP_productoObj P_productoObj= new clsP_productoObj(this, Con, db);
        clsD_facturasObj D_facturas= new clsD_facturasObj(this, Con, db);
        clsD_facturaObj D_fact= new clsD_facturaObj(this, Con, db);
        clsClasses.clsD_facturas fsitem;

		Cursor dt,dtc;
		String vprod,vumstock,vumventa,vbarra,ssq;
		double vcant,vpeso,vfactor,peso,factpres,vtot,vprec,adescmon,adescv1;
		int mitem,bitem,prid,prcant,unid,unipr,dev_ins=1,fsid,counter,fpend,itemuid,cuid;
		boolean flag,pagocarta=false;

        corel=gl.codigo_ruta+"_"+mu.getCorelBase();
		//corel=gl.ruta+"_"+mu.getCorelBase();

        try {
            if (gl.numero_orden.isEmpty()) gl.numero_orden=" ";
        } catch (Exception e) {
            gl.numero_orden=" ";
        }

        sql="SELECT MAX(ITEM) FROM D_FACT_LOG";
        dt=Con.OpenDT(sql);

        if(dt.getCount()>0){
            dt.moveToFirst();
            mitem=dt.getInt(0);
        }else{
            mitem=0;
        }
		mitem++;

		try {
			dt=Con.OpenDT("SELECT MAX(CODIGO_TRANS) FROM D_barril_trans");
			dt.moveToFirst();
			idtransbar=dt.getInt(0)+1;
		} catch (Exception e) {
			idtransbar=1;
		}

		try {

            String ss="WHERE anulado=0 AND feelfechaprocesado=0 AND feeluuid=' ' AND fecha>2010010000";
            D_fact.fill(ss);
            fpend=D_fact.count;

			db.beginTransaction();

			//region D_FACTURA

			sql="SELECT SUM(TOTAL),SUM(DESMON),SUM(IMP),SUM(PESO) FROM T_VENTA";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			peso=dt.getDouble(3);

			ins.init("D_FACTURA");
            ins.add("EMPRESA",gl.emp);
         	ins.add("COREL",corel);
			ins.add("ANULADO",false);
			//if(gl.validDate) ins.add("FECHA",gl.lastDate); else ins.add("FECHA",fecha);
            ins.add("FECHA",fecha);
			ins.add("RUTA",gl.codigo_ruta);
			ins.add("VENDEDOR",gl.codigo_vendedor);
			ins.add("CLIENTE",gl.codigo_cliente);
			ins.add("KILOMETRAJE",0);
			ins.add("FECHAENTR",fecha);
			ins.add("FACTLINK",""+gl.numero_orden);
	   		ins.add("TOTAL",tot);
			ins.add("DESMONTO",descmon);
			ins.add("IMPMONTO",totimp);//ins.add("IMPMONTO",totimp+totperc);
			ins.add("PESO",peso);
			ins.add("BANDERA","N");
			ins.add("STATCOM","N");
			ins.add("CALCOBJ",false);
			ins.add("SERIE",fserie);
			ins.add("CORELATIVO",fcorel);
			ins.add("IMPRES",0);

			try {
				if (gl.nummesapedido.equalsIgnoreCase("0")) {
					ins.add("ADD1",gl.ref1);
				}else  {
					ins.add("ADD1",gl.nummesapedido);
				}
			} catch (Exception e) {
				ins.add("ADD1",gl.ref1);
			}
			ins.add("ADD2",gl.ref2);
            if (pendiente) ins.add("ADD3","P"); else ins.add("ADD3","");

			ins.add("DEPOS",false);
			ins.add("PEDCOREL",gl.pedcorel+"");
			ins.add("REFERENCIA","");
			if (gl.dvbrowse!=0)	ins.add("ASIGNACION",gl.dvcorreld); else ins.add("ASIGNACION","");

			ins.add("SUPERVISOR",""+fpend);
			ins.add("VEHICULO",gl.parVer);
			ins.add("AYUDANTE"," ");
			ins.add("CODIGOLIQUIDACION",0);
			ins.add("RAZON_ANULACION","");

            ins.add("FEELSERIE"," ");
            ins.add("FEELNUMERO"," ");
            ins.add("FEELUUID"," ");
            ins.add("FEELFECHAPROCESADO",0);
            ins.add("FEELCONTINGENCIA"," ");

			db.execSQL(ins.sql());

			//endregion

			//region Bonificacion

            /*
			clsBonifSave bonsave=new clsBonifSave(this,corel,"V");

			bonsave.ruta=gl.ruta;
			bonsave.cliente=gl.cliente;
			bonsave.fecha=fecha;
			bonsave.emp=gl.emp;

			bonsave.save();
            */
			//endregion

			//region D_FACTURAD


            if (gl.numero_orden.isEmpty() || gl.numero_orden.equalsIgnoreCase(" ")) {
                //toastlong("Venta directa ");
            } else {
                //toastlong("Venta orden ");
            }

			sql="SELECT PRODUCTO,CANT,PRECIO,IMP,DES,DESMON,TOTAL,PRECIODOC,PESO,VAL2,VAL4,UM,FACTOR,UMSTOCK,EMPRESA FROM T_VENTA";
			dt=Con.OpenDT(sql);

			dt.moveToFirst();counter=1;

			while (!dt.isAfterLast()) {

				porpeso=false;
				factpres=dt.getDouble(12);
				peso=dt.getDouble(8);
				vumstock=dt.getString(11);
                itemuid=dt.getInt(14);
                adescv1=dt.getDouble(1)*dt.getDouble(2);
                adescmon=adescv1*descaddmonto/stot;
                adescv1=dt.getDouble(5);

			  	ins.init("D_FACTURAD");
				ins.add("COREL",corel);
				ins.add("PRODUCTO",app.codigoProducto(dt.getString(0)));
				ins.add("EMPRESA",gl.emp);
				ins.add("ANULADO",false);
				ins.add("CANT",dt.getDouble(1));
				ins.add("PRECIO",dt.getDouble(2));
				ins.add("IMP",dt.getDouble(3));
				ins.add("DES",dt.getDouble(4));
				ins.add("DESMON",dt.getDouble(5)+adescmon);
				ins.add("TOTAL",dt.getDouble(6)-adescmon);
				ins.add("PRECIODOC",dt.getDouble(7));
				ins.add("PESO",dt.getDouble(8));
				ins.add("VAL1",dt.getDouble(9));
				ins.add("VAL2",""+itemuid);
				ins.add("UMVENTA",dt.getString(11));
				ins.add("FACTOR",dt.getDouble(12));
                vumstock=app.umVenta2(dt.getString(0));
				ins.add("UMSTOCK",vumstock);
				ins.add("UMPESO",dt.getString(13));

				ssq=ins.sql();
			    db.execSQL(ins.sql());

			    vprod=dt.getString(0);
				//vumstock=dt.getString(13);
				vcant=dt.getDouble(1);
				vpeso=dt.getDouble(8);
				vfactor=vpeso/(vcant*factpres);
				vumventa=dt.getString(11);

				if (esProductoConStock(dt.getString(0))) {
					//rebajaStockUM(vprod, vumstock, vcant, vfactor, vumventa,factpres,peso);
                    rebajaStockUM(app.codigoProducto(vprod),vumstock,vcant);
				}

				if (app.esProductoBarril(app.codigoProducto(vprod))) {
					agregaRegistroBarril(gl.bar_prod,gl.bar_cant, vcant);
				}

			    dt.moveToNext();counter++;ss="";

                try {

                    sql="SELECT CODIGO_MENU,IDCOMBO,UNID,CANT,IDSELECCION,ORDEN FROM T_COMBO WHERE IDCOMBO="+itemuid;
                    dtc=Con.OpenDT(sql);

                    if (dtc.getCount()>0) {

                        dtc.moveToFirst();cuid=0;

                        while (!dtc.isAfterLast()) {

                            ins.init("D_facturac");
                            ins.add("EMPRESA",cuid);
                            ins.add("COREL",corel);
                            ins.add("CODIGO_MENU",dtc.getInt(0));
                            ins.add("IDCOMBO",dtc.getInt(1));
                            ins.add("UNID",dtc.getInt(2));
                            ins.add("CANT",dtc.getInt(3));
                            ins.add("IDSELECCION",dtc.getInt(4));
                            ins.add("ORDEN",dtc.getInt(5));
                            ins.add("NOMBRE",app.prodNombre(dtc.getInt(4)));

                            String sn=app.prodNombre(dtc.getInt(4));

                            ss+=ins.sql()+"\n";

                            db.execSQL(ins.sql());

                            if (esProductoConStock(dtc.getInt(4))) {
                                P_prodrecetaObj.fill("WHERE (CODIGO_PRODUCTO="+dtc.getInt(4)+")");
                                if (P_prodrecetaObj.count==0) {
                                    rebajaStockUM(dtc.getInt(4),app.umVenta2(dtc.getInt(4)),dtc.getInt(3));
                                }
                            }

							if (app.esProductoBarril(dtc.getInt(4))) {
								agregaRegistroBarril(gl.bar_prod,gl.bar_cant, dtc.getInt(3));
							}

                            dtc.moveToNext();cuid++;
                        }
                    }

                } catch (Exception e) {
                    //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . FACTURAC : "+e.getMessage());
                }

            }

			//endregion

			//region D_FACTURAP

            sql = "SELECT ITEM,CODPAGO,TIPO,VALOR,DESC1,DESC2,DESC3 FROM T_PAGO";
            dt = Con.OpenDT(sql);

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                if (dt.getString(2).equalsIgnoreCase("K")) pagocarta=true;

                ins.init("D_FACTURAP");
                ins.add("COREL", corel);
                ins.add("ITEM", dt.getInt(0));
                ins.add("ANULADO", false);
                ins.add("EMPRESA", gl.emp);
                ins.add("CODPAGO", dt.getInt(1));
                ins.add("TIPO", dt.getString(2));
                ins.add("VALOR", dt.getDouble(3));
                ins.add("DESC1", dt.getString(4));
                ins.add("DESC2", dt.getString(5));
                ins.add("DESC3", dt.getString(6));
                ins.add("DEPOS", false);
                db.execSQL(ins.sql());

                dt.moveToNext();
            }

			//endregion

			//region D_FACTURAF

            try {
                if (gl.gCorreoCliente.isEmpty()) gl.gCorreoCliente=" ";
            } catch (Exception e) {
                gl.gCorreoCliente=" ";
            }

            try {
                if (gl.gDirCliente.isEmpty()) gl.gDirCliente=" ";
            } catch (Exception e) {
                gl.gDirCliente=" ";
            }

			if (gl.gNITCliente.equalsIgnoreCase("CF")) {

				clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
				P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
				String vCorreoDefecto =P_sucursalObj.first().correo;

				ins.init("D_FACTURAF");
				ins.add("COREL",corel);
				ins.add("NOMBRE","Consumidor Final");
				ins.add("NIT","CF");
				ins.add("DIRECCION","Ciudad");
                ins.add("CORREO",vCorreoDefecto);

            } else {

				ins.init("D_FACTURAF");
				ins.add("COREL",corel);
				ins.add("NOMBRE",gl.gNombreCliente);
				ins.add("NIT",gl.gNITCliente);
				ins.add("DIRECCION",gl.gDirCliente);
                ins.add("CORREO",gl.gCorreoCliente);
            }

			db.execSQL(ins.sql());

			//endregion

            //region D_FACTURAS

            //sql="SELECT SUM(UNID*CANT),IDSELECCION,IDCOMBO FROM T_COMBO GROUP BY IDSELECCION";
            sql="SELECT UNID,CANT,IDSELECCION,IDCOMBO FROM T_COMBO ";
            dt=Con.OpenDT(sql);

            String prcod="";int iidd=1;

            if (dt.getCount()>0) {

                dt.moveToFirst();

                while (!dt.isAfterLast()) {

                    prcant=dt.getInt(0);if (prcant==0) prcant=1;
                    unid=dt.getInt(1);
                    unipr=prcant*unid;
                    prid=dt.getInt(2);
                    fsid=dt.getInt(3);

                    if (prid!=0) {

                        P_productoObj.fill("WHERE CODIGO_PRODUCTO=" + prid);
                        if (P_productoObj.count>0) {
                            prcod = P_productoObj.first().codigo;

                            if (P_productoObj.first().codigo_tipo.equalsIgnoreCase("P")) {

                                fsitem = clsCls.new clsD_facturas();
                                fsitem.corel = corel;
                                fsitem.id = iidd;//fsitem.id=fsid;
                                fsitem.producto = "" + prid;
                                fsitem.cant = unipr;
                                fsitem.umstock = app.umVenta2(prcod);

                                D_facturas.add(fsitem);

                                //rebajaStockUM(prid, fsitem.umstock, fsitem.cant);
                            }
                        } else {
                            throw new Exception("El producto con identificador : "+prid+" no está definido o no está activo.");
                        }
                    }

                    dt.moveToNext();iidd++;
                }
            }

            //endregion

            //region D_FACTURAPR Propina por factura - solo modulo restaurante

            if (gl.peRest) {

                if (propina>0) {

                    propina=mu.round2(propina);

                    try {

                        clsD_facturaprObj D_facturaprObj = new clsD_facturaprObj(this, Con, db);
                        clsClasses.clsD_facturapr itempr = clsCls.new clsD_facturapr();

                        itempr.empresa = gl.emp;
                        itempr.corel = corel;
                        itempr.anulado = 0;
                        itempr.fecha = fecha;
                        itempr.codigo_sucursal = gl.tienda;
                        itempr.codigo_vendedor = gl.mesero_venta;
                        if (itempr.codigo_vendedor==0) itempr.codigo_vendedor=gl.codigo_vendedor;
                        itempr.propina = propina;
                        itempr.propperc =propinaperc;
                        itempr.propextra =propinaext;

                        if (pagocarta) {
                            if (gl.pePropinaCarta>0 && gl.pePropinaPerc>0) {
                                if (gl.pePropinaCarta>gl.pePropinaPerc) gl.pePropinaCarta=gl.pePropinaPerc;
                                itempr.propina = propina*gl.pePropinaCarta/gl.pePropinaPerc;
                                propinaperc=gl.pePropinaCarta; itempr.propperc =propinaperc;
                            }
                        }

                        D_facturaprObj.add(itempr);

                    } catch (Exception e) {
                        msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
                    }

                }
            }

            //endregion

            //region D_FACTURA_FEL

            clsD_factura_felObj D_factura_felObj=new clsD_factura_felObj(this,Con,db);
            clsClasses.clsD_factura_fel ffel=clsCls.new clsD_factura_fel();

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE (CODIGO_SUCURSAL="+gl.tienda+")");

            ffel.corel=corel;
            ffel.empresa=gl.emp;
            ffel.nit=P_sucursalObj.first().nit;
            ffel.razon_social=P_sucursalObj.first().texto;
            ffel.nombre_comercial=P_sucursalObj.first().nombre;
            ffel.sucursal=gl.tienda;
            ffel.ruta=gl.codigo_ruta;

            D_factura_felObj.add(ffel);

            //endregion

            procesaInventario();

			//region Actualizacion de ultimo correlativo

			sql="UPDATE P_COREL SET CORELULT="+fcorel+"  WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0) ";
			db.execSQL(sql);

			ins.init("D_FACT_LOG");
			ins.add("ITEM",mitem);
			ins.add("SERIE",fserie);
			ins.add("COREL",fcorel);
			ins.add("FECHA",0);
			ins.add("RUTA",gl.ruta);
			db.execSQL(ins.sql());

            try {
                int codcf=10*gl.emp;
                sql="UPDATE P_CLIENTE SET NOMBRE='Consumidor final' WHERE CODIGO_CLIENTE="+codcf;
                db.execSQL(sql);
            } catch (Exception e) {
                msgbox2(e.getMessage());return false;
            }

			//endregion

			db.setTransactionSuccessful();
			db.endTransaction();

			if (gl.dvbrowse!=0) {
				gl.dvbrowse =0;
				gl.tiponcredito=0;
			}

			saved=true;

            if (dt!=null) dt.close();

        } catch (Exception e) {
            db.endTransaction();
            mu.msgbox("Error (factura) " + e.getMessage());return false;
        }

		return true;
	}

	private void rebajaStockUM(String prid,String umstock,double cant,double factor, String umventa,double factpres,double ppeso) {

		Cursor dt;
		double cantapl,dispcant,actcant,pesoapl,disppeso,actpeso,speso,factlote;
		String lote,doc,stat;

		if (porpeso) {
			actcant=cant;
			actpeso=ppeso;
		} else {
			actcant=cant*factpres;
			actpeso=cant*factor;
		}

		try {

			sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
				"FROM P_STOCK WHERE (CANT>0) AND (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"') ORDER BY CANT";
			//sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
			//		"FROM P_STOCK WHERE (CANT>0) AND (CODIGO='"+prid+"') ORDER BY CANT";


			dt=Con.OpenDT(sql);

			if (dt.getCount()==0) return;

			dt.moveToFirst();
			while (!dt.isAfterLast()) {

				cant=dt.getDouble(0);
				speso=dt.getDouble(2);
				lote=dt.getString(4);
				doc=dt.getString(5);
				stat=dt.getString(9);

				if (actcant>cant) cantapl=cant;else cantapl=actcant;
				dispcant=cant-cantapl;if (dispcant<0) dispcant=0;
				actcant=actcant-cantapl;

				if (porpeso) {
					if (actpeso>speso) pesoapl=speso;else pesoapl=actpeso;
					actpeso=actpeso-pesoapl;
				} else {
					pesoapl=cantapl*factor;
				}
				disppeso=speso-pesoapl;if (disppeso<0) disppeso=0;

				// Stock

				sql="UPDATE P_STOCK SET CANT="+dispcant+",PESO="+disppeso+" WHERE (CODIGO='"+prid+"') AND (LOTE='"+lote+"') AND (DOCUMENTO='"+doc+"') AND (STATUS='"+stat+"') AND (UNIDADMEDIDA='"+umstock+"')";
				//sql="UPDATE P_STOCK SET CANT="+dispcant+",PESO="+disppeso+" WHERE (CODIGO='"+prid+"') AND (LOTE='"+lote+"') AND (DOCUMENTO='"+doc+"') AND (STATUS='"+stat+"')";
				db.execSQL(sql);


				// Factura Stock

				ins.init("D_FACTURA_STOCK");
				ins.add("COREL",corel);
				ins.add("CODIGO",prid );
				ins.add("CANT",cantapl );
				ins.add("CANTM",dt.getDouble(1));
				ins.add("PESO",pesoapl);
				ins.add("plibra",dt.getDouble(3));
				ins.add("LOTE",lote );
				ins.add("DOCUMENTO",doc);
				ins.add("FECHA",dt.getInt(6));
				ins.add("ANULADO",dt.getInt(7));
				ins.add("CENTRO",dt.getString(8));
				ins.add("STATUS",stat);
				ins.add("ENVIADO",dt.getInt(10));
				ins.add("CODIGOLIQUIDACION",dt.getInt(11));
				ins.add("COREL_D_MOV",dt.getString(12));
				ins.add("UNIDADMEDIDA",umstock);

				db.execSQL(ins.sql());

				// Factura lotes

				factlote=factpres;if (factlote<1) factlote=1/factlote;

				try {

					ins.init("D_FACTURAD_LOTES");

					ins.add("COREL",corel);
					ins.add("PRODUCTO",prid );
					ins.add("LOTE",lote );

					//if (porpeso) {
						ins.add("CANTIDAD",cantapl);
					//} else {
						//ins.add("CANTIDAD",cantapl*factpres);
					//}

					ins.add("PESO",pesoapl);
					ins.add("UMSTOCK",umstock);
					ins.add("UMPESO",gl.umpeso);
					ins.add("UMVENTA",umventa);

					db.execSQL(ins.sql());

				} catch (SQLException e) {
					addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);

					sql="UPDATE D_FACTURAD_LOTES SET CANTIDAD=CANTIDAD+"+cantapl+",PESO=PESO+"+pesoapl+"  " +
						"WHERE (COREL='"+corel+"') AND (PRODUCTO='"+prid+"') AND (LOTE='"+lote+"')";
					db.execSQL(sql);
					//mu.msgbox(e.getMessage()+"\n"+ins.sql());
				}

				//if (actcant<=0) return;

				dt.moveToNext();
			}

            db.execSQL("DELETE FROM D_FACTURAD_LOTES WHERE CANTIDAD<=0");

            if (dt!=null) dt.close();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("rebajaStockUM: "+e.getMessage());
		}
	}

    private void rebajaStockUM(int prid,String umstock,double cantapl) {
        Cursor dt;
        double dispcant,actcant;

        try {
            clsP_stockObj P_stockObj=new clsP_stockObj(this,Con,db);

            clsClasses.clsP_stock item = clsCls.new clsP_stock();

            item.codigo=prid;
            item.cant=0;
            item.cantm=0;
            item.peso=0;
            item.plibra=0;
            item.lote="";
            item.documento="";
            item.fecha=0;
            item.anulado=0;
            item.centro="";
            item.status="";
            item.enviado=1;
            item.codigoliquidacion=0;
            item.corel_d_mov="";
            item.unidadmedida=umstock;

            P_stockObj.add(item);

        } catch (Exception e) {}

        try {

            //sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
            //        "FROM P_STOCK WHERE (CANT>0) AND (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"') ORDER BY CANT";
            sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
                    "FROM P_STOCK WHERE (CODIGO="+prid+") ORDER BY CANT";
            dt=Con.OpenDT(sql);

            dispcant=dt.getDouble(0);
            actcant=dispcant-cantapl;

            //sql="UPDATE P_STOCK SET CANT="+actcant+",PESO=0 WHERE (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"')";
            sql="UPDATE P_STOCK SET CANT="+actcant+",PESO=0 WHERE (CODIGO="+prid+") ";
            db.execSQL(sql);

            if (dt!=null) dt.close();
        } catch (Exception e) {
            mu.msgbox("rebajaStockUM: "+e.getMessage());
            toastlong("rebajaStockUM: "+e.getMessage());
        }
    }

	private void rebajaStockBonif(String prid,String umstock,double cant,double factor, String umventa,double factpres,double ppeso) {
		Cursor dt;
		double cantapl,dispcant,actcant,pesoapl,disppeso,actpeso,speso;
		String lote,doc,stat;

		if (porpeso) {
			actcant=cant;
			actpeso=ppeso;
		} else {
			actcant=cant*factpres;
			actpeso=cant*factor;
		}

		try {

			sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
					"FROM P_STOCK WHERE (CANT>0) AND (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"') ORDER BY CANT";
			dt=Con.OpenDT(sql);

			if (dt.getCount()==0) return;

			dt.moveToFirst();
			while (!dt.isAfterLast()) {

				cant=dt.getDouble(0);
				speso=dt.getDouble(2);
				lote=dt.getString(4);
				doc=dt.getString(5);
				stat=dt.getString(9);

				if (actcant>cant) cantapl=cant;else cantapl=actcant;
				dispcant=cant-cantapl;if (dispcant<0) dispcant=0;
				actcant=actcant-cantapl;

				if (porpeso) {
					if (actpeso>speso) pesoapl=speso;else pesoapl=actpeso;
					actpeso=actpeso-pesoapl;
				} else {
					pesoapl=cantapl*factor;
				}
				disppeso=speso-pesoapl;if (disppeso<0) disppeso=0;

				// Stock

				sql="UPDATE P_STOCK SET CANT="+dispcant+",PESO="+disppeso+" WHERE (CODIGO='"+prid+"') AND (LOTE='"+lote+"') AND (DOCUMENTO='"+doc+"') AND (STATUS='"+stat+"') AND (UNIDADMEDIDA='"+umstock+"')";
				db.execSQL(sql);

				// Bonif Stock

				ins.init("D_BONIF_STOCK");

				ins.add("COREL",corel);
				ins.add("CODIGO",prid );
				ins.add("CANT",cantapl );
				ins.add("CANTM",dt.getDouble(1));
				ins.add("PESO",pesoapl);
				ins.add("plibra",dt.getDouble(3));
				ins.add("LOTE",lote );

				ins.add("DOCUMENTO",doc);
				ins.add("FECHA",dt.getInt(6));
				ins.add("ANULADO",dt.getInt(7));
				ins.add("CENTRO",dt.getString(8));
				ins.add("STATUS",stat);
				ins.add("ENVIADO",dt.getInt(10));
				ins.add("CODIGOLIQUIDACION",dt.getInt(11));
				ins.add("COREL_D_MOV",dt.getString(12));
				ins.add("UNIDADMEDIDA",umstock);

				db.execSQL(ins.sql());

				// Bonif lotes

				try {
					ins.init("D_BONIF_LOTES");

					ins.add("COREL",corel);
					ins.add("PRODUCTO",prid );
					ins.add("LOTE",lote );

					if (porpeso) {
						ins.add("CANT",cantapl);
					} else {
						ins.add("CANT",cantapl/factpres);
					}

					ins.add("PESO",pesoapl);
					ins.add("UMSTOCK",umstock);
					ins.add("UMPESO",gl.umpeso);
					ins.add("UMVENTA",umventa);
					ins.add("FACTOR",factor);

					db.execSQL(ins.sql());

					//Toast.makeText(this,ins.SQL(),Toast.LENGTH_LONG).show();

				} catch (SQLException e) {
					sql="UPDATE D_BONIF_LOTES SET CANT=CANT+"+cantapl+",PESO=PESO+"+pesoapl+"  " +
						"WHERE (COREL='"+corel+"') AND (PRODUCTO='"+prid+"') AND (LOTE='"+lote+"')";
					db.execSQL(sql);
				}

				//if (actcant<=0) return;

				dt.moveToNext();
			}

            if (dt!=null) dt.close();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("rebajaStockUM: "+e.getMessage());
		}

	}

	private void saveAtten(double tot) {
		long ti,tf,td;

		ti=gl.atentini;tf=du.getActDateTime();
		td=du.timeDiff(tf,ti);if (td<1) td=1;

		try {
			ins.init("D_ATENCION");

			ins.add("RUTA",gl.ruta);
			ins.add("FECHA",ti);
			ins.add("HORALLEG",gl.ateninistr);
			//ins.add("HORALLEG",DU.shora(ti)+":00");
			ins.add("HORASAL",du.shora(tf)+":00");
			ins.add("TIEMPO",td);

			ins.add("VENDEDOR",gl.vend);
			ins.add("CLIENTE",gl.cliente);
			ins.add("DIAACT",du.dayofweek(ti));
			ins.add("DIA",du.dayofweek(ti));
			ins.add("DIAFLAG","S");

			ins.add("SECUENCIA",1);
			ins.add("SECUENACT",1);
			ins.add("CODATEN","");
			ins.add("KILOMET",0);

			ins.add("VALORVENTA",tot);
			ins.add("VALORNEXT",0);
			ins.add("CLIPORDIA",clidia);
			ins.add("CODOPER","V");
			ins.add("COREL",corel);

			if (gl.gpspass) ins.add("SCANNED","G");else ins.add("SCANNED",gl.escaneo);
			ins.add("STATCOM","N");
			ins.add("LLEGO_COMPETENCIA_ANTES",0);

			ins.add("CoorX",gl.gpspx);
			ins.add("CoorY",gl.gpspy);
			ins.add("CliCoorX",gl.gpscpx);
			ins.add("CliCoorY",gl.gpscpy);
			ins.add("Dist",gl.gpscdist);

			db.execSQL(ins.sql());

		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			//String s=gl.ruta+" / "+ti+" / "+gl.ateninistr;
			mu.msgbox("Error (att) : " + e.getMessage());
		}

	}

	private double totalDescProd(){
		Cursor DT;

		try {
			sql="SELECT SUM(DESMON),SUM(TOTAL),SUM(IMP) FROM T_VENTA";
			DT=Con.OpenDT(sql);

			if(DT.getCount()>0){
                DT.moveToFirst();

                tot=DT.getDouble(1);
                stot0=tot+DT.getDouble(0);

                totimp=DT.getDouble(2);

                double d=DT.getDouble(0);
                if (DT!=null) DT.close();
                return d;
            }else {
			    return 0;
            }

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("totalDescProd: " + e.getMessage());

			return 0;
		}

	}

    private void assignCorel(){

        Cursor DT;
        int ca,ci,cf,ca1,ca2,cult;

        fcorel=0;fserie="";

        try {

            sql="SELECT MAX(CORELATIVO) FROM D_FACTURA";
            DT=Con.OpenDT(sql);


            try {
                if (DT.getCount()>0){
                    DT.moveToFirst();
                    cult=DT.getInt(0);
                } else {
                    cult=0;
                }
            } catch (Exception e) {
                cult=0;
            }

            sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN FROM P_COREL WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0) ";
            DT=Con.OpenDT(sql);

            try {
                if (DT.getCount()>0){
                    DT.moveToFirst();

                    fserie=DT.getString(0);
                    ca1=DT.getInt(1);
                    ci=DT.getInt(2);
                    cf=DT.getInt(3);
                } else {
                    fcorel=0;fserie="";
                    mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n");
                    return;
                }
            } catch (Exception e) {
                fcorel=0;fserie="";
                mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n");
                return;
            }

            //sql="SELECT MAX(COREL) FROM D_FACT_LOG WHERE RUTA="+gl.ruta+" AND SERIE='"+fserie+"'";
            sql="SELECT MAX(COREL) FROM D_FACT_LOG ";

            DT=Con.OpenDT(sql);

            try {
                if (DT.getCount()>0){
                    DT.moveToFirst();
                    ca2=DT.getInt(0);
                }else {
                    ca2=0;
                }
            } catch (Exception e) {
                ca2=0;
            }

            ca=ca1;

            if (ca2>ca) ca=ca2;
            fcorel=ca+1;

            sql="SELECT COREL FROM D_FACT_LOG WHERE COREL="+fcorel;
            DT=Con.OpenDT(sql);

            try {
                if (DT.getCount()>0) {
                    try {
                        sql="SELECT MAX(ITEM) FROM D_FACT_LOG";
                        DT=Con.OpenDT(sql);

                        DT.moveToFirst();

                        ins.init("D_FACT_LOG");
                        ins.add("ITEM",DT.getInt(0));
                        ins.add("SERIE","DUPL");
                        ins.add("COREL",fcorel);
                        ins.add("FECHA",0);
                        ins.add("RUTA",gl.ruta);
                        db.execSQL(ins.sql());
                    } catch (SQLException e) { }
                    fcorel++;
                }
            } catch (Exception e) {
            }

            if (!app.usaFEL()) {
                if (fcorel>cf) {
                    mu.msgbox("Se ha acabado el talonario de facturas. No se puede continuar con la venta.");
                    fcorel=0;return;
                }
                //#HS_20181128_1602 Cambie el texto del mensaje.
                if (fcorel==cf) mu.msgbox("Esta es la última factura disponible.");
            }

            if (gl.peMFact) s="Factura : ";else s="Ticket : ";
            lblFact.setText(s+fserie+" - "+fcorel+" , Talonario : "+fcorel+" / "+cf);

            if (DT!=null) DT.close();

            if (fcorel-cult>1) {
                if (cult>0) msgAskSend("Encontramos un inconveniente en los correlativos, por favor envie el siguiente correo al soporte.");
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("assignCorel: " + e.getMessage());
        }

	}

	private boolean esProductoConStock(String prcodd) {

		Cursor DT;

		try {
			sql="SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO='"+prcodd+"'";
           	DT=Con.OpenDT(sql);

           	if (DT.getCount()>0){
                DT.moveToFirst();
                return DT.getString(0).equalsIgnoreCase("P");
            } else {
           	    return false;
            }

		} catch (Exception e) {
			mu.msgbox("esProductoConStock: " + e.getMessage());
			return false;
	    }
	}

    private boolean esProductoConStock(int prcodd) {

        Cursor DT;

        try {
            sql="SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO_PRODUCTO="+prcodd;
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                DT.moveToFirst();
                return DT.getString(0).equalsIgnoreCase("P");
            } else {
                return false;
            }

        } catch (Exception e) {
            mu.msgbox("esProductoConStock: " + e.getMessage());
            return false;
        }
    }

	//endregion

    //region Inventario

    private void procesaInventario() {
        clsT_ventaObj T_ventaObj = new clsT_ventaObj(this, Con, db);
        int prodid;

        db.execSQL("DELETE FROM T_factreceta");

        recid=0;

        T_ventaObj.fill("WHERE (VAL4='0')"); // Productos individuales
        for (int i = 0; i < T_ventaObj.count; i++) {
            prodid=app.codigoProducto(T_ventaObj.items.get(i).producto);
            agregaReceta(prodid,T_ventaObj.items.get(i).cant);
        }

        T_ventaObj.fill("WHERE (VAL4<>'0')"); // Combos
        for (int i = 0; i < T_ventaObj.count; i++) {
            agregaCombo(T_ventaObj.items.get(i).val4,T_ventaObj.items.get(i).cant);
        }

        inventarioRecetas();

    }

    private void agregaCombo(String comboid,double ccant) {
        T_comboObj.fill("WHERE (IDCOMBO="+comboid+")");
        if (T_comboObj.count==0) return;

        for (int ij = 0; ij <T_comboObj.count; ij++) {
            agregaReceta(T_comboObj.items.get(ij).idseleccion,T_comboObj.items.get(ij).cant);
            //agregaReceta(T_comboObj.items.get(ij).idseleccion,T_comboObj.items.get(ij).cant*ccant);
        }
    }

    private void agregaReceta(int prodid,double rcant) {

        double reccant;
        int idart;
        String aum;

        P_prodrecetaObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
        if (P_prodrecetaObj.count==0) {
            //existenciaComboItem(prodid, rcant);
            return;
        }

        for (int ii = 0; ii <P_prodrecetaObj.count; ii++) {

            idart=P_prodrecetaObj.items.get(ii).codigo_articulo;
            reccant=P_prodrecetaObj.items.get(ii).cant*rcant;
            aum=P_prodrecetaObj.items.get(ii).um;

            agregaProductoReceta(idart,reccant,aum);
        }
    }

    private void agregaProductoReceta(int prodid,double pcant,String unid) {
        recid++;

        clsClasses.clsT_factreceta item = clsCls.new clsT_factreceta();

        item.id=recid;
        item.producto=prodid;
        item.cant=pcant;
        item.um=unid;

        T_factrecetaObj.add(item);
    }

    private void inventarioRecetas() {

        Cursor dt;
        clsD_facturarObj D_facturarObj=new clsD_facturarObj(this,Con,db);

        db.execSQL("DELETE FROM D_facturar WHERE Corel='"+corel+"'");

        sql="SELECT SUM(CANT),PRODUCTO,UM FROM T_factreceta GROUP BY PRODUCTO,UM";
        dt=Con.OpenDT(sql);

        if (dt.getCount()>0) {

            dt.moveToFirst();
            int newfrid=D_facturarObj.newID("SELECT MAX(EMPRESA) FROM D_facturar");

            while (!dt.isAfterLast()) {

                clsClasses.clsD_facturar item = clsCls.new clsD_facturar();


                item.empresa=newfrid;
                item.corel=corel;
                item.producto=dt.getInt(1);
                item.cant=dt.getDouble(0);
                item.um=dt.getString(2);

                D_facturarObj.add(item);

                existenciaReceta(item.producto,item.cant,item.um);

                dt.moveToNext();newfrid++;
            }
        }
    }

    private void existenciaReceta(int prodid,double pcant,String unid) {
        P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
        if (P_productoObj.count==0) return;
        if (!P_productoObj.first().codigo_tipo.equalsIgnoreCase("P")) return;
        rebajaStockUM(prodid,unid,pcant);
    }

    private void existenciaComboItem(int prodid,double pcant) {
        P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
        if (P_productoObj.count==0) return;
        if (!P_productoObj.first().codigo_tipo.equalsIgnoreCase("P")) return;
        rebajaStockUM(prodid,P_productoObj.first().unidbas,pcant);
    }

    //endregion

	//region Cortesia

	private void listaCortesias() {
		clsClasses.clsP_cortesia item;

		gl.modo_cortesia=false;
		gl.usuario_cortesia=0;

		try {

			clsP_cortesiaObj P_cortesiaObj=new clsP_cortesiaObj(this,Con,db);
			P_cortesiaObj.fill("WHERE (ACTIVO=1) ORDER BY NOMBRE");
			if (P_cortesiaObj.count==0) {
				msgbox("No está definido ningúno usuario con credenciales de cortesia");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(FacturaRes.this,"Cortesia","Salir");

			for (int i = 0; i <P_cortesiaObj.count; i++) {
				item=P_cortesiaObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listdlg.dismiss();
				}
			});

			listdlg.onEnterClick(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listdlg.getInput().isEmpty()) return;

					if (listdlg.validPassword()) {
						gl.usuario_cortesia=listdlg.validUserId();
						gl.nombre_cortesia=listdlg.validUserName();
						aplicaCortesia();
						listdlg.dismiss();
					} else {
						toast("Contraseña incorrecta");
					}
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void aplicaCortesia() {

		if (gl.usuario_cortesia==0) {
			msgbox("No está definido quién aplica la cortesia");return;
		}

		totalOrderCortesia();
		if (!guardaCortesia()) return;

		completaEstadoOrdenCortesia();

		gl.cliposflag=false;
		gl.InvCompSend=false;
		gl.delivery =false;
		gl.pickup = false;
		gl.sin_propina=false;
		gl.InvCompSend=true;

		impresionCortesia();

	}

	private void completaEstadoOrdenCortesia() {

		try {
			db.execSQL("UPDATE P_RES_SESION SET ESTADO=-1 WHERE ID='"+ gl.ordcorel+"'");
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		if (!gl.peActOrdenMesas) return;

		try {
			broadcastJournalFlag(99);
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void totalOrderCortesia(){

		double dmaxmon;

		cleandprod=false;

		try{

			if (acum) {
				descimp=descgmon;
				dfinmon=descpmon+descgmon;
				cleandprod=false;
			} else {
				if (descpmon>=descgmon) {
					dfinmon=descpmon;
					cleandprod=false;
				} else {
					dfinmon=descgmon;
					cleandprod=true;
				}
			}

			dmaxmon=(double) (stot0*dmax/100);
			if (dmax>0) {
				if (dfinmon>dmaxmon) dfinmon=dmax;
			}

			descmon=mu.round2(dfinmon);
			stot=mu.round2(stot0);

			propinaperc=0;
			propina=0;

			fillTotals();

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("totalOrder: " + e.getMessage());
		}

	}

	private boolean guardaCortesia() {
		Cursor dt,dtc;
		String vprod,vumstock;
		double vcant;
		int itemuid,cuid;

		try {
			clsClasses.clsD_Mov header;
			clsClasses.clsD_MovD item;

			db.beginTransaction();

			clsD_MovObj mov=new clsD_MovObj(this,Con,db);
			clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

			String cortcorel=gl.ruta+"_"+mu.getCorelBase();corel=cortcorel;

			header =clsCls.new clsD_Mov();

			header.COREL=cortcorel;
			header.RUTA=gl.codigo_ruta;
			header.ANULADO=0;
			header.FECHA=du.getActDateTime();
			header.TIPO="C";
			header.USUARIO=gl.usuario_cortesia;
			header.REFERENCIA= gl.nombre_cortesia;
			header.STATCOM="N";
			header.IMPRES=0;
			header.CODIGOLIQUIDACION=0;
			header.CODIGO_PROVEEDOR=3;
			header.TOTAL=tot;

			mov.add(header);

			int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

			sql="SELECT PRODUCTO,CANT,PRECIO,IMP,DES,DESMON,TOTAL,PRECIODOC,PESO,VAL2,VAL4,UM,FACTOR,UMSTOCK,EMPRESA FROM T_VENTA";
			dt=Con.OpenDT(sql);

			dt.moveToFirst();

			while (!dt.isAfterLast()) {

				porpeso=false;
				vumstock=dt.getString(11);
				itemuid=dt.getInt(14);

				item =clsCls.new clsD_MovD();

				corm++;
				item.coreldet=corm;
				item.corel=cortcorel;
				item.producto=app.codigoProducto(dt.getString(0));
				item.cant=-dt.getDouble(1);
				item.cantm=0;
				item.peso=0;
				item.pesom=0;
				item.lote="";
				item.codigoliquidacion=0;
				item.unidadmedida=vumstock;
				item.precio=dt.getDouble(2);
				item.motivo_ajuste=0;

				movd.add(item);

				vprod=dt.getString(0);
				vcant=dt.getDouble(1);

				if (esProductoConStock(dt.getString(0))) {
					rebajaStockUM(app.codigoProducto(vprod),vumstock,vcant);
				}

				dt.moveToNext();ss="";

				try {

					sql="SELECT CODIGO_MENU,IDCOMBO,UNID,CANT,IDSELECCION,ORDEN FROM T_COMBO WHERE IDCOMBO="+itemuid;
					dtc=Con.OpenDT(sql);

					if (dtc.getCount()>0) {

						dtc.moveToFirst();cuid=0;

						while (!dtc.isAfterLast()) {

							ins.init("D_facturac");
							ins.add("EMPRESA",cuid);
							ins.add("COREL",corel);
							ins.add("CODIGO_MENU",dtc.getInt(0));
							ins.add("IDCOMBO",dtc.getInt(1));
							ins.add("UNID",dtc.getInt(2));
							ins.add("CANT",dtc.getInt(3));
							ins.add("IDSELECCION",dtc.getInt(4));
							ins.add("ORDEN",dtc.getInt(5));
							ins.add("NOMBRE",app.prodNombre(dtc.getInt(4)));

							String sn=app.prodNombre(dtc.getInt(4));

							ss+=ins.sql()+"\n";

							db.execSQL(ins.sql());

							if (esProductoConStock(dtc.getInt(4))) {
								P_prodrecetaObj.fill("WHERE (CODIGO_PRODUCTO="+dtc.getInt(4)+")");
								if (P_prodrecetaObj.count==0) {
									rebajaStockUM(dtc.getInt(4),app.umVenta2(dtc.getInt(4)),dtc.getInt(3));
								}
							}

							dtc.moveToNext();cuid++;
						}
					}

				} catch (Exception e) {
					//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . FACTURAC : "+e.getMessage());
				}

			}

			procesaInventario();

			db.setTransactionSuccessful();
			db.endTransaction();

			return true;
		} catch (Exception e) {
			db.endTransaction();
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		return false;
	}

	private void impresionCortesia() {

		try {
			gl.nombre_mesero="";

			fdoc.es_pickup=gl.pickup;
			fdoc.es_delivery=gl.delivery;

			if (gl.mesero_venta>0) {
				clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
				VendedoresObj.fill("WHERE CODIGO_VENDEDOR="+gl.mesero_venta);
				if (VendedoresObj.count>0) gl.nombre_mesero=VendedoresObj.first().nombre;
			}

			rl_facturares.setVisibility(View.INVISIBLE);

			if(gl.dvbrowse!=0) gl.dvbrowse =0;

			impres=0;

			try {
				rep.clear();

				impresionEncabezado(0);
				impresionDetalle(0);

				rep.line();
				rep.empty();
				rep.addtote("Valor total: ",mu.frmcur(tot));
				rep.empty();
				rep.empty();
				rep.empty();

				rep.save();

				app.doPrint(gl.peNumImp,0);
			} catch (Exception e) {
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			}

			gl.iniciaVenta=true;
			gl.ventalock=false;

			try {
				db.execSQL("DELETE FROM T_ORDEN_MOD WHERE COREL='"+gl.ordcorel+"'");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_ORDEN_ING WHERE COREL='"+gl.ordcorel+"'");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_VENTA_MOD");
			} catch (Exception e) {}

			try {
				db.execSQL("DELETE FROM T_VENTA_ING");
			} catch (Exception e) {}

			super.finish();

		} catch (Exception e){
			mu.msgbox("impresionCortesia: "  + e.getMessage());
			gl.ventalock=false;
			super.finish();
		}
	}

	private void impresionEncabezado(int aid) {

		rep.empty();
		rep.empty();
		//rep.addc(gl.empnom);
		//rep.addc(gl.tiendanom);
		rep.empty();
		rep.addc("CONSUMO CORTESIA");
		rep.empty();
		//rep.add("Numero: "+corel+" ");

		rep.add("Fecha: "+du.sfecha(du.getActDate())+" "+du.shora(du.getActDate()));
		//rep.add("Operador: "+gl.vendnom);
		rep.add("Cortesia autorizo: ");
		rep.add(gl.nombre_cortesia);
		rep.empty();
		rep.add3lrr("Cantidad","Precio","Total");
		rep.line();

	}

	private void impresionDetalle(int aid) {
		Cursor dt;
		String dum;
		double dcant,dprec,dtot;

		try {

			sql="SELECT PRODUCTO,CANT,PRECIO,TOTAL,UM FROM T_VENTA";
			dt=Con.OpenDT(sql);

			dt.moveToFirst();
			while (!dt.isAfterLast()) {
				dum =  dt.getString(4);
				dcant = dt.getDouble(1);
				dprec = dt.getDouble(2);
				dtot = dt.getDouble(3);

				rep.add(app.prodNombre(app.codigoProducto(dt.getString(0))));
				rep.add3lrre(mu.frmdecno(dcant) + " " + dum, dprec, dtot);

				dt.moveToNext();
			}

		} catch (Exception e) {
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+e.getMessage());
		}
	}


	//endregion

	//region Pago

	private void inputEfectivo() {
		try{
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Pago Efectivo");
			alert.setMessage("Monto a pagar");

			final EditText input = new EditText(this);
			alert.setView(input);

			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			input.setText(""+tot);
			input.requestFocus();

			showkeyb();

			alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					peexit=false;
					sefect=input.getText().toString();
					closekeyb();
					applyCash();
					checkPago();
				}
			});

			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					peexit=true;
					closekeyb();
				}
			});

			alert.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void inputVuelto() {

		try{

			final AlertDialog.Builder alert = new AlertDialog.Builder(this);

            if (gl.dvbrowse!=0){
                double totdv;
                if (tot>=dispventa){
                    totdv = mu.round(tot-dispventa,2);
                    alert.setTitle("A pagar : "+mu.frmcur(totdv));
                }
            } else {
                alert.setTitle("A pagar : "+mu.frmcur(tot));
            }

			alert.setMessage("Pagado con billete : ");

			final LinearLayout layout   = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);

			if(txtVuelto.getParent()!= null){
				txtVuelto.setText("");
				((ViewGroup)txtVuelto.getParent()).removeView(txtVuelto);
			}

			if(lblVuelto.getParent()!= null){
				lblVuelto.setText("");
				((ViewGroup)lblVuelto.getParent()).removeView(lblVuelto);
			}

			layout.addView(txtVuelto);
			layout.addView(lblVuelto);lblVuelto.setTextSize(20);lblVuelto.setTextColor(Color.rgb(54,184,238));lblVuelto.setGravity(Gravity.LEFT);

			alert.setView(layout);

			showkeyb();
			alert.setCancelable(false);
			alert.create();

			alert.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				peexit=false;

				svuelt= txtVuelto.getText().toString();
				gl.brw=1;
				sefect=""+tot;

				if (!svuelt.equalsIgnoreCase("")){
					double vuel=Double.parseDouble(svuelt);
					falt=tot-vuel;
					vuel=vuel-tot-descaddmonto;
                    lblMonto.setText("");khand.val="";

					if (vuel<0.00) {
						msgbox("Pago insuficiente, faltan Q."+falt);return;
					}

					if (vuel==0.0){
						msgAskVuelto("Pago Realizado Correctamente");
					} else {
						msgAskVuelto("Su vuelto : "+mu.frmcur(vuel));
					}
				}

				svuelt=""+tot;
				sefect=""+tot;

				//applyCash();
				//checkPago();

				}
			});

			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					peexit=true;
					lblVuelto.setText("");
					txtVuelto.setText("");
					layout.removeAllViews();

				}
			});

			alert.show();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("InputVuelto: " + e.getMessage());
		}

	}

    private void msgAskVuelto(String msg) {

        ExDialog dialog = new ExDialog(this);

        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                applyCash();
                checkPago();
                if (!app.usaFEL()) {
                    gl.ventalock=false;
                    finish();
                }
            }
        });

        //dialog.show();

		AlertDialog adg=dialog.show();

		TextView textView = (TextView) adg.getWindow().findViewById(android.R.id.message);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(50);
		Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.inconsolata);
		textView.setTypeface(tf, Typeface.NORMAL);
		textView.setGravity(Gravity.CENTER);

    }

    public void Davuelto(){
        double pg,vuel;

		try{
			svuelt= txtVuelto.getText().toString();

			if (!svuelt.equals("")) {
				pg=Double.parseDouble(svuelt);

				if (pg > 0) {
                    if (gl.dvbrowse != 0) {
                        double totdv;
                        totdv = mu.round(tot - dispventa, 2);
                        if (pg < totdv) {
                            msgbox("Monto menor que total");
                        }

                        vuel = pg - tot;
                        lblVuelto.setText("    Vuelto: " + mu.frmcur(vuel));
                    } else {
                        if (pg < tot) {
                            msgbox("Monto menor que total");
                        }
                        vuel = pg - tot;
                        lblVuelto.setText("    Vuelto: " + mu.frmcur(vuel));
                    }
                }
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	public void vuelto(String msg) {

		try{

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage(msg);

			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					checkPago();
				}
			});
			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

    private void validaPagoEfectivo() {
        double aplcash;

        svuelt= khand.val;
        sefect="0";

        if (!svuelt.equalsIgnoreCase("")){

            double vuel=Double.parseDouble(svuelt);
            aplcash=vuel;
            //vuel=vuel-gl.total_pago+descaddmonto;
            vuel=vuel-gl.total_pago;
            lblMonto.setText("");khand.val="";

            if (vuel<0.00) {
                toast("Pago insuficiente, falta "+mu.frmcur(-vuel));
                sefect=svuelt;
                applyCash();
                checkPago();
                return;
            }

            if (vuel==0.0){
                //msgAskVuelto("Monto ingresado no genera vuelto");
                sefect=""+aplcash;
                applyCash();
                checkPago();
                if (!app.usaFEL()) {
                    gl.ventalock=false;
                    finish();
                }
            } else {
                aplcash=aplcash-vuel;
                sefect=""+aplcash;
                //applyCash();
                checkPagoNF();
                msgAskVuelto("Su vuelto : "+mu.frmcur(vuel));
            }
        }

        svuelt=""+tot;
        //sefect=""+tot;

    }

    private void applyCash() {

	    Cursor dt;
		double epago;
        int codpago=0;

		try {


			epago=Double.parseDouble(sefect);

			if (epago==0) return;

			if (epago<0) throw new Exception();

			sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 1";
			dt = Con.OpenDT(sql);
			if(dt!=null) {
				if(dt.getCount()>0){
					dt.moveToFirst();
					codpago=dt.getInt(0);
				}

                if (dt!=null) dt.close();
			}

            int item=1;

            sql="SELECT MAX(ITEM) FROM T_PAGO";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                item=dt.getInt(0)+1;
            }

			ins.init("T_PAGO");

			ins.add("ITEM",item);
			ins.add("CODPAGO",codpago);
			ins.add("TIPO","E");

			if(gl.dvbrowse!=0){
				if (epago>=dispventa) {
					epago=mu.round(epago-dispventa,2);
				}
			}

			ins.add("VALOR",epago);
			ins.add("DESC1","");
			ins.add("DESC2","");
			ins.add("DESC3","");

		    db.execSQL(ins.sql());

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("Pago incorrecto"+e.getMessage());
	    }

	}

    private void applyPendiente() {
        Cursor dt;
        int codpago=0;

        try {

            sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 1";
            dt = Con.OpenDT(sql);
            if(dt!=null) {
                if(dt.getCount()>0){
                    dt.moveToFirst();
                    codpago=dt.getInt(0);
                }

                if (dt!=null) dt.close();
            }

            int item=1;

            sql="SELECT MAX(ITEM) FROM T_PAGO";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                item=dt.getInt(0)+1;
            }

            ins.init("T_PAGO");

            ins.add("ITEM",item);
            ins.add("CODPAGO",codpago);
            ins.add("TIPO","E");
            ins.add("VALOR",0);
            ins.add("DESC1","");
            ins.add("DESC2","");
            ins.add("DESC3","");

            db.execSQL(ins.sql());

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Pago incorrecto"+e.getMessage());
        }

    }

    private void inputCredito() {

		try{

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Pago Crédito");
			alert.setMessage("Valor a pagar");

			final EditText input = new EditText(this);
			alert.setView(input);

			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			input.setText(""+tot);
			input.requestFocus();

			alert.setCancelable(false);
			showkeyb();

			alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					peexit=false;
					sefect=input.getText().toString();
					closekeyb();
					applyCredit();
					checkPago();
				}
			});

			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					peexit=true;
					closekeyb();
				}
			});

			alert.show();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    private void msgAskCredito() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Pago Crédito");
        dialog.setMessage("Monto a pagar : "+tot);

        dialog.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sefect=""+tot;
                applyCredit();
                checkPago();
            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        dialog.show();

    }

    private void applyCredit() {
		double epago;

		try {
			epago=Double.parseDouble(sefect);
			if (epago==0) return;

			if (epago<0) throw new Exception();

			//if (epago>plim) {
			//	MU.msgbox("Total de pago mayor que total de saldos.");return;
			//}

			//if (epago>tsel) {
			//	msgAskOverPayd("Total de pago mayor que saldo\nContinuar");return;
			//}

			sql="DELETE FROM T_PAGO";
			db.execSQL(sql);

			ins.init("T_PAGO");

			ins.add("ITEM",1);
			ins.add("CODPAGO",5);
			ins.add("TIPO","C");
			ins.add("VALOR",epago);
			ins.add("DESC1","");
			ins.add("DESC2","");
			ins.add("DESC3","");

		    db.execSQL(ins.sql());

			//msgAskSave("Aplicar pago y crear un recibo");

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			inputEfectivo();
			mu.msgbox("Pago incorrecto"+e.getMessage());
	    }

	}

	private void inputCard() {

		try{
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Número de tarjeta");

			final EditText input = new EditText(this);
			alert.setView(input);

			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText("");input.requestFocus();

			showkeyb();

			alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if (checkNum(input.getText().toString())) addPagoTar();
				}
			});

			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					closekeyb();
				}
			});

			alert.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private boolean checkNum(String s) {

		try{
			if (mu.emptystr(s)) {
				showkeyb();
				inputCard();
				mu.msgbox("Número incorrecto");showkeyb();
				return false;
			}

			desc1=s;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("checkNum: " + e.getMessage());
		}

		return true;

	}

	private void addPagoTar(){

		sql="DELETE FROM T_PAGO";
		db.execSQL(sql);

		try {

			ins.init("T_PAGO");

			ins.add("ITEM",1);
			ins.add("CODPAGO",3);
			ins.add("TIPO","K");
			ins.add("VALOR",tot);
			ins.add("DESC1",desc1);
			ins.add("DESC2","");
			ins.add("DESC3","");

	    	db.execSQL(ins.sql());

		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("Error : " + e.getMessage());
		}

		checkPago();

	}

	private void checkPago() {
		Cursor DT;
		double tpago;

        pagoPendiente();

        try {

            sql="SELECT SUM(VALOR) FROM T_PAGO";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                DT.moveToFirst();
                tpago=DT.getDouble(0);
                if (DT!=null) DT.close();
            } else {
                tpago=0;
            }

            if (pendiente) {
                tpago=tot;
            }
            s=mu.frmcur(tpago);

            pend=tot-tpago;if (pend<0) pend=0;
            lblTotal.setText(mu.frmcur(pend));

            if (gl.dvbrowse==1){
                if (gl.brw>0){
                    lblPago.setText("Pago COMPLETO.\n"+s);
                    pago=true;
                    pagocompleto=true;
                    //if (rutapos) askSavePos(); else askSave();
                    finishOrder();
                }
            }	else{
                if (tpago<tot) {
                    lblPago.setText("Pago incompleto."+s);
                    pago=false;
                } else {
                    lblPago.setText("Pago COMPLETO."+s);
                    pago=true;
                    pagocompleto=true;
                    //if (rutapos) askSavePos(); else askSave();
                    finishOrder();
                }
            }

            if (gl.mesero_precuenta) lblPago.setText("");
        } catch (Exception e) {
           addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

    }

    private void checkPagoNF() {
        Cursor DT;
        double tpago;

        pagoPendiente();

        try {

            sql="SELECT SUM(VALOR) FROM T_PAGO";
            DT=Con.OpenDT(sql);

            if(DT.getCount()>0){
                DT.moveToFirst();
                tpago=DT.getDouble(0);
                if (DT!=null) DT.close();
            } else {
                tpago=0;
            }

            s=mu.frmcur(tpago);
            pend=tot-tpago;if (pend<0) pend=0;
            lblTotal.setText(mu.frmcur(pend));

            if (gl.dvbrowse==1){
                if (gl.brw>0){
                    lblPago.setText("Pago COMPLETO.\n"+s);
                    pago=true;
                    pagocompleto=true;
                    //if (rutapos) askSavePos(); else askSave();
                }
            }	else{
                if (tpago<tot) {
                    lblPago.setText("Pago incompleto."+s);
                    pago=false;
                } else {
                    lblPago.setText("Pago COMPLETO."+s);
                    pago=true;
                    pagocompleto=true;
                    //if (rutapos) askSavePos(); else askSave();
                }
            }

            if (gl.mesero_precuenta) lblPago.setText("");
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

    }

    private void pagoPendiente() {
        Cursor DT;
        double tpago,pef,pcard;

        try {

            sql="SELECT SUM(VALOR) FROM T_PAGO WHERE TIPO='E'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                pef=DT.getDouble(0);
            } else  {
                pef=0;
            }

            sql="SELECT SUM(VALOR) FROM T_PAGO WHERE TIPO='K'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                pcard=DT.getDouble(0);
            } else  {
                pcard=0;
            }

        } catch (Exception e) {
            pef=0;pcard=0;
            mu.msgbox( e.getMessage());
        }

        lblPEfect.setText(mu.frmcur(pef));
        lblPCard.setText(mu.frmcur(pcard));

        try {

            sql="SELECT SUM(VALOR) FROM T_PAGO";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                tpago=DT.getDouble(0);
            } else  {
                tpago=0;
            }

            if (DT!=null) DT.close();

            gl.total_pago=tot-tpago;if (gl.total_pago<0) gl.total_pago=0;

        } catch (Exception e) {
            gl.total_pago=-1; mu.msgbox( e.getMessage());
        }
    }

	private String androidid() {
		String uniqueID="";
		try {
			uniqueID = Settings.Secure.getString(getContentResolver(),
					Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			uniqueID="0000000000";
		}

		return uniqueID;
	}

	//endregion

    //region Comanda Cocina

    private void imprimeComanda() {
        if (!divideComanda()) return;
        if (!generaArchivos()) return;
        //ejecutaImpresion();
    }

    private boolean divideComanda() {
        clsT_ventaObj T_ordenObj=new clsT_ventaObj(this,Con,db);
        clsClasses.clsT_venta venta;
        clsT_comboObj T_comboObj=new clsT_comboObj(this,Con,db);
        clsClasses.clsT_combo combo;

        String prname,cname;
        int prodid,prid,idcomb,linea=1;

        try {

            db.execSQL("DELETE FROM T_comanda");

            T_ordenObj.fill();
            P_productoObj.fill();

            for (int i = 0; i <T_ordenObj.count; i++) {
                venta=T_ordenObj.items.get(i);

                prodid = app.codigoProducto(venta.producto);
                prname=getProd(prodid);
                s = mu.frmdecno(venta.cant) + "  " + prname;

                if (!app.prodTipo(prodid).equalsIgnoreCase("M")) {

                    P_linea_impresoraObj.fill("WHERE CODIGO_LINEA="+prodlinea);
                    for (int k = 0; k <P_linea_impresoraObj.count; k++) {
                        prid=P_linea_impresoraObj.items.get(k).codigo_impresora;
                        agregaComanda(linea,prid,s);linea++;
                    }

                } else {

                    T_comboObj.fill("WHERE (IdCombo=" + venta.empresa+") AND (IdSeleccion<>0)");
                    idcomb=mu.CInt(venta.empresa);idcomb=idcomb % 100;
                    cname=s+" [#"+idcomb+"]";

                    for (int j = 0; j < T_comboObj.count; j++) {
                        prodid=T_comboObj.items.get(j).idseleccion;
                        s = " -  " + getProd(prodid);
                        P_linea_impresoraObj.fill("WHERE CODIGO_LINEA="+prodlinea);

                        for (int k = 0; k <P_linea_impresoraObj.count; k++) {
                            prid=P_linea_impresoraObj.items.get(k).codigo_impresora;
                            agregaComanda(linea,prid,cname);linea++;
                            agregaComanda(linea,prid,s);linea++;
                        }
                    }
                }

            }
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean agregaComanda(int linea,int prid,String texto) {
        try {
            clsClasses.clsT_comanda item = clsCls.new clsT_comanda();

            item.linea=linea;
            item.id=prid;
            item.texto=texto;

            T_comandaObj.add(item);
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean generaArchivos() {
        clsRepBuilder rep;
        int printid;
        String fname,plev="";
        File file;

        if (gl.parallevar) plev=" - PARA LLEVAR";

        try {
            P_impresoraObj.fill();
            for (int i = 0; i <P_impresoraObj.count; i++) {
                fname = Environment.getExternalStorageDirectory()+"/comanda_"+P_impresoraObj.items.get(i).codigo_impresora+".txt";
                file=new File(fname);
                try {
                    file.delete();
                } catch (Exception e) { }
            }
        } catch (Exception e) {
        }

        try {
            clsViewObj ViewObj=new clsViewObj(this,Con,db);
            ViewObj.fillSelect("SELECT DISTINCT ID, '','','','', '','','','' FROM T_comanda ORDER BY ID");

            for (int i = 0; i <ViewObj.count; i++) {
                printid=ViewObj.items.get(i).pk;
                P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA="+printid+")");

                rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"comanda_"+printid+".txt");

                rep.add(P_impresoraObj.first().tipo_impresora);
                rep.add(P_impresoraObj.first().nombre);
                rep.add(P_impresoraObj.first().ip);

                rep.add("");rep.add("");rep.add("");
                rep.add("ORDEN : "+gl.ref1.toUpperCase()+plev);
                rep.add("");
                rep.add("Fecha : "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
                rep.line();

                T_comandaObj.fill("WHERE ID="+printid+" ORDER BY LINEA");

                for (int j = 0; j <T_comandaObj.count; j++) {
                    rep.add(T_comandaObj.items.get(j).texto);
                }

                rep.line();
                rep.add("");
                rep.add("Caja : "+gl.rutanom);
                rep.add("Cajero : "+gl.vendnom);
                rep.add("");rep.add("");

                rep.save();rep.clear();
            }
            //mesa
            //rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");


            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private void ejecutaImpresion() {
        try {
            app.print3nstarw();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void imprimeComandaSimple() {
        app.doPrint(1);
    }

    //endregion

    //region Monitor despacho

    private void generaOrdenDespacho() {

        try {
            P_productoObj.fill();

            generaOrdenEncabezado();
            generaOrdenDetalle();

            Intent intent = new Intent(FacturaRes.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",osql);
            startService(intent);

        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void generaOrdenEncabezado() {
        clsClasses.clsD_orden item;
        int tlim;

        tlim=tiempoPreparacion();osql="";

        item = clsCls.new clsD_orden();

        item.codigo_orden=0;
        item.corel=corel;
        item.empresa=gl.emp;
        item.codigo_ruta=gl.tienda;
        item.tipo=0;
        item.num_orden=gl.ref1;
        item.estado=0;
        item.tiempo_limite=tlim;
        item.tiempo_total=0;

        if (gl.parallevar) {
            item.nota="PARA LLEVAR";
            if (gl.emp==14) item.nota="COMER EN MESA";
        } else {
            item.nota="";
        }

        sql=addOrdedEncItemSql(item);

        osql+=sql+";";

    }

    private void generaOrdenDetalle() {
        D_ordendObj=new clsD_ordendObj(this,Con,db);
        D_ordencObj=new clsD_ordencObj(this,Con,db);
        clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
        clsT_comboObj T_comboObj=new clsT_comboObj(this,Con,db);
        clsClasses.clsT_venta venta;
        clsClasses.clsT_combo combo;

        int prid,pp;
        String ss;

        T_ventaObj.fill();pp=0;

        for (int i = 0; i <T_ventaObj.count; i++) {
            venta=T_ventaObj.items.get(i);

            pp++;
            prid=app.codigoProducto(venta.producto);
            s=mu.frmdecno(venta.cant)+"  "+getProd(prid);
            if (app.prodTipo(prid).equalsIgnoreCase("M")) s+="  [ #"+venta.empresa+" ]";

            sql=addProducto(pp,s);osql+=sql+";";

            if (app.prodTipo(prid).equalsIgnoreCase("M")) {
                T_comboObj.fill("WHERE (IdCombo=" + venta.val4+") AND (IdSeleccion<>0)");
                for (int j = 0; j <T_comboObj.count; j++) {
                    pp++;
                    s=" -  "+getProd(T_comboObj.items.get(j).idseleccion);
                    sql=addProducto(pp,s);osql+=sql+";";
                }
            }
        }

        pp++;sql=addProducto(pp,"------    FIN DEL LISTADO   ------");osql+=sql+";";

        ss="(SELECT CODIGO_ORDEN FROM D_orden WHERE COREL='"+corel+"') ";
        sql="UPDATE D_ordend SET CODIGO_ORDEN="+ss+" WHERE (CODIGO_ORDEN=0) AND (EMPRESA="+gl.emp+") AND (COREL='"+corel+"')";
        osql+=sql+";";

    }

    private String addProducto(int itemid,String nom) {

        clsClasses.clsD_ordend item = clsCls.new clsD_ordend();

        item.codigo_orden=0;
        item.corel=corel;
        item.empresa=gl.emp;
        item.itemid=itemid;
        item.tipo=0;
        item.nombre=nom;
        item.nota="";
        item.modif="";

        return D_ordendObj.addItemSql(item);
    }

    public String addOrdedEncItemSql(clsClasses.clsD_orden item) {

        String fsi=du.univfechahora(du.getActDateTime());

        ins.init("D_orden");

        ins.add("COREL",item.corel);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("TIPO",item.tipo);
        ins.add("NUM_ORDEN",item.num_orden);
        ins.add("ESTADO",item.estado);
        ins.add("FECHA_INICIO",fsi);
        ins.add("FECHA_FIN","20000101 00:00:00");
        ins.add("TIEMPO_LIMITE",item.tiempo_limite);
        ins.add("TIEMPO_TOTAL",item.tiempo_total);
        ins.add("NOTA",item.nota);

        return ins.sql();

    }

    private int tiempoProd(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) return (int) P_productoObj.items.get(i).tiempo_preparacion;
            }
        } catch (Exception e) {}
        return 1;
    }

    private int tiempoPreparacion() {
        clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
        clsClasses.clsT_venta venta;

        int prid,tt,tmax=0,tprep=0;

        T_ventaObj.fill();
        tprep=1+1; // tiempo impresion + tiempo empaque
        tprep+=T_ventaObj.count-1;

        for (int i = 0; i <T_ventaObj.count; i++) {
            venta=T_ventaObj.items.get(i);

            prid=app.codigoProducto(venta.producto);
            tt=tiempoProd(prid);
            if (tt>tmax) tmax=tt;
        }

        tprep+=tmax;

        return tprep;
    }


    //endregion

	//region Aux

	private void clearGlobals() {

		try {

			db.execSQL("DELETE FROM T_PAGO");

			db.execSQL("DELETE FROM T_BONITEM WHERE PRODID='*'");

			//#CKFK 20210706
			gl.pickup=false;
			gl.delivery=false;

		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
	}
	
	private void checkPromo() {
		Cursor DT;
		
		imgBon.setVisibility(View.INVISIBLE);
		
		try {
			sql="SELECT ITEM FROM T_BONITEM";
           	DT=Con.OpenDT(sql);
			if (DT.getCount()>0) imgBon.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
	    }
	}
	
	private void cliPorDia() {
        clidia=0;
	}

	private boolean prodPorPeso(String prodid) {
		try {
			return app.ventaPeso(prodid);
		} catch (Exception e) {
			return false;
		}
	}

	private void hidekeyboard() {
		try{
			View sview = this.getCurrentFocus();

			if (sview != null) {
				InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sview.getWindowToken(), 0);
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    private boolean permiteTarjeta() {
        try {

            clsP_mediapagoObj P_mediapagoObj=new clsP_mediapagoObj(this,Con,db);
            P_mediapagoObj.fill("WHERE CODIGO=5");
            if (P_mediapagoObj.count==0) return false;

            return P_mediapagoObj.first().activo==1;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private void marcaFacturaContingencia() {

        clsClasses.clsP_corel citem;
        clsClasses.clsD_factura fact=clsCls.new clsD_factura();
        long corcont;

        try {

            db.beginTransaction();

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            clsP_corelObj P_corelObj=new clsP_corelObj(this,Con,db);

            P_corelObj.fill("WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)");
            citem=P_corelObj.first();
            if (citem.corelult==0) {
                corcont=citem.corelini;
            } else {
                corcont=citem.corelult+1;
            }

            D_facturaObj.fill("WHERE Corel='"+corel+"'");
            fact=D_facturaObj.first();
            fact.feelcontingencia=""+corcont;
            D_facturaObj.update(fact);

            citem.corelult=corcont;
            P_corelObj.update(citem);

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void impresionCuenta() {
        try {
            clsDocCuenta fdoc=new clsDocCuenta(this,prn.prw,gl.peMon,gl.peDecImp, "");

            if (true) {

                try {
                    db.execSQL("UPDATE P_RES_SESION SET ESTADO=3 WHERE ID='"+ gl.ordcorel+"'");
                } catch (SQLException e) { }

				clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
				P_res_sesionObj.fill("WHERE ID='"+gl.ordcorel+"'");
         		clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

                clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
                VendedoresObj.fill("WHERE (CODIGO_VENDEDOR="+sess.vendedor+")");
                if (VendedoresObj.count>0) gl.vendnom=VendedoresObj.first().nombre;else gl.vendnom="";

                clsP_res_mesaObj P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
                P_res_mesaObj.fill("WHERE (CODIGO_MESA="+sess.codigo_mesa+")");
                if (P_res_mesaObj.count>0) gl.mesanom=P_res_mesaObj.first().nombre;else gl.mesanom="";

                fdoc.vendedor=gl.vendnom;
                fdoc.rutanombre=gl.tiendanom;
				fdoc.buildPrint(gl.mesanom,gl.nocuenta_precuenta,tot,
						descimp,propinaperc,gl.pePropinaFija,propina);
				//fdoc.buildPrint(gl.mesanom,gl.nocuenta_precuenta,tot,descimp,propinaperc,gl.pePropinaFija,propina+propinaext);

                gl.QRCodeStr="";
                app.doPrint(1,0);

                Handler mtimer = new Handler();
                Runnable mrunner=new Runnable() {
                    @Override
                    public void run() {
                        gl.iniciaVenta=false;
                        finish();
                    }
                };
                mtimer.postDelayed(mrunner,200);
            }
        } catch (Exception e){
            mu.msgbox("impresionCuenta : "  + e.getMessage());
        }
    }

	private void completaEstadoOrden() {

		if (!todasCuentasPagadas()) return;

		try {
			db.execSQL("UPDATE P_RES_SESION SET ESTADO=-1 WHERE ID='"+ gl.ordcorel+"'");
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		if (!gl.peActOrdenMesas) return;


		try {
			broadcastJournalFlag(99);
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void broadcastJournalFlag(int flag) {
		clsClasses.clsT_ordencom pitem;
		int idruta;

		try {

			clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
			P_rutaObj.fill();

			String cmd="";

			for (int i = 0; i <P_rutaObj.count; i++) {

				idruta=P_rutaObj.items.get(i).codigo_ruta;

				//if (idruta!=gl.codigo_ruta) {

				pitem= clsCls.new clsT_ordencom();

				pitem.codigo_ruta=idruta;
				pitem.corel_orden=gl.ordcorel;
				pitem.corel_linea=flag;
				pitem.comanda="";

				if (flag==99) pitem.comanda=updItemSqlAndroid();

				cmd+=addItemSqlOrdenCom(pitem) + ";";
				//}

			}

			try {
				Intent intent = new Intent(FacturaRes.this, srvCommit.class);
				intent.putExtra("URL",gl.wsurl);
				intent.putExtra("command",cmd);
				startService(intent);
			} catch (Exception e) {
				toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
				app.addToOrdenLog(du.getActDateTime(),
						"FacturaRes."+new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),cmd);
			}

		} catch (Exception e) {
			toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private boolean todasCuentasPagadas() {
		int cc=1,cp=0,nc;

		try {
			clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
			T_ordencuentaObj.fill("WHERE COREL='"+gl.ordcorel+"'");
			cc=T_ordencuentaObj.count;

			for (int i = 0; i <cc; i++) {
				nc=i+1;
				if (cuentaPagada(gl.ordcorel,nc)) cp++;
			}

			return cp==cc;
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return false;
		}

	}

	private Boolean cuentaPagada(String corr,int id) {
		try {
			clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
			D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
			return D_facturaObj.count!=0;
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
		}
	}

	public String updItemSqlAndroid() {
		String corr="<>"+gl.ordcorel+"<>";
		return "UPDATE P_res_sesion SET ESTADO=-1 WHERE ID='"+corr+"'";
	}

	public String addItemSqlOrdenCom(clsClasses.clsT_ordencom item) {

		ins.init("T_ordencom");

		ins.add("CODIGO_RUTA",item.codigo_ruta);
		ins.add("COREL_ORDEN",item.corel_orden);
		ins.add("COREL_LINEA",item.corel_linea);
		ins.add("COMANDA",item.comanda);

		return ins.sql();

	}

	private void enviaAvizo() {
        String subject,body;

        try {
            subject="Correlativo inconsistente : "+gl.rutanom+" ID : "+gl.codigo_ruta;
            body="  ";
            String[] TO = {"jpospichal@dtsguatemala.onmicrosoft.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String getProd2(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) return P_productoObj.items.get(i).desclarga;
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

    private String getProd(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) {
                    prodlinea=P_productoObj.items.get(i).linea;
                    return P_productoObj.items.get(i).desclarga;
                }
            }
        } catch (Exception e) {}
        return ""+prodid;
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

    private void enviaPago(String csql) {

        try {
            db.execSQL(csql);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        try {
            Intent intent = new Intent(FacturaRes.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",csql);
            startService(intent);
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

	private void agregaRegistroBarril(int idprod,double factor,double cant) {

		if (app.barrilProd(idprod).isEmpty()) return;

		try {
			ins.init("D_barril_trans");

			ins.add("CODIGO_TRANS",idtransbar);
			ins.add("EMPRESA",gl.emp);
			ins.add("CODIGO_SUCURSAL",gl.tienda);
			ins.add("FECHAHORA",du.getActDateTime());
			ins.add("CODIGO_BARRIL",gl.bar_idbarril);
			ins.add("CODIGO_PRODUCTO",idprod);
			ins.add("CANTIDAD",factor*cant);
			ins.add("UM",gl.bar_um);
			ins.add("MESERO",gl.codigo_vendedor);
			ins.add("TIPO_MOV",2);
			ins.add("IDTRANS",corel);
			ins.add("STATCOM",0);

			db.execSQL(ins.sql());

		} catch (Exception e) {
			String ee=e.getMessage()+"\n"+ins.sql();
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		idtransbar++;
	}


	//endregion

    //region Dialogs

    public void askSave(View view) {
        try{
            checkPago();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void askSave() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Road");
            dialog.setMessage("¿Guardar la factura?");

            dialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finishOrder();
                }
            });

            dialog.setNegativeButton("Salir", null);

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void askSavePos() {
        double vuel;
        String sv="";

        try{
            try {
                vuel=Double.parseDouble(svuelt);

                if (vuel<tot) throw new Exception();

                if (vuel>tot) {
                    vuel=vuel-tot;
                    sv="Vuelto : "+mu.frmcur(vuel)+"\n\n";
                } else {
                    sv="SIN VUELTO";
                }

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                sv="";
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Road");
            dialog.setMessage(sv+"¿Guardar la factura?");

            dialog.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finishOrder();
                }
            });

            dialog.setNegativeButton("Salir", null);

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void askPendientePago() {
        try{
            ExDialog dialog = new ExDialog(this);

            dialog.setMessage("Esta factura quedará PENDIENTE DE PAGO. ¿Continuar?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    pendiente = true;
                    applyPendiente();
                    checkPago();
                }
            });

            dialog.setNegativeButton("Cancelar", null);
            dialog.setCancelable(false);
            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void askPrint() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Road");
            dialog.setMessage("¿Impresión correcta?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    impres++;toast("Impres "+impres);

                    try {
                        if (!gl.cobroPendiente){
                            sql="UPDATE D_FACTURA SET IMPRES=IMPRES+1 WHERE COREL='"+corel+"'";
                            db.execSQL(sql);
                        }
                    } catch (Exception e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }

                    try {
                        sql="UPDATE D_NOTACRED SET IMPRES=IMPRES+1 WHERE COREL='"+corelNC+"'";
                        db.execSQL(sql);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    }

                    if (impres>1) {

                        try {
                            if (!gl.cobroPendiente){
                                sql="UPDATE D_FACTURA SET IMPRES=IMPRES+1 WHERE COREL='"+corel+"'";
                                db.execSQL(sql);
                            }
                        } catch (Exception e) {
                            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }

                        try {
                            sql="UPDATE D_NOTACRED SET IMPRES=IMPRES+1 WHERE COREL='"+corelNC+"'";
                            db.execSQL(sql);
                        } catch (Exception e) {
                            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        }

                        gl.brw=0;

                        gl.ventalock=false;
                        FacturaRes.super.finish();
                    } else {

                        if (!gl.cobroPendiente) {
                            fdoc.buildPrint(corel, 10,gl.peFormatoFactura);
                        }else{
                            fdoc.buildPrint(corel,4,gl.peFormatoFactura);
                        }

                        prn.printask(printcallback);

                    }
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //singlePrint();
                    prn.printask(printcallback);
                    //finish();
                }
            });

            dialog.show();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void askDelPago() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Pagos");
            dialog.setMessage("¿Borrar todos los pagos?");

            dialog.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        db.execSQL("DELETE FROM T_PAGO");
                        checkPago();
                    } catch (SQLException e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }
                }
            });

            dialog.setNegativeButton("Salir", null);

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void askPrecuenta() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Precuenta");
            dialog.setMessage("¿Imprimir precuenta?");

            dialog.setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        impresionCuenta();
                    } catch (Exception e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }
                }
            });

            dialog.setNegativeButton("Salir", null);

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskComanda(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imprimeComandaSimple();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskSend(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enviaAvizo();
            }
        });

        dialog.show();

    }

    private void ingresaPropina() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Monto propina extra");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(mu.frmdecno(propinaext));
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    propinaext=Double.parseDouble(s);
                    if (propinaext<0) throw new Exception();
                    totalOrder();
                    pagoPendiente();
                } catch (Exception e) {
                    mu.msgbox("Valor incorrecto");return;
                }
            }
        });

		alert.setNeutralButton("Sin propina", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				msgAskSinPropina("Anular la propina");
			}
		});

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

	private void msgAskSinPropina(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage("¿" + msg + "?");

		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				totalOrderCortesia();
			}
		});

		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});

		dialog.show();

	}

	private void valorDescuentoMonto() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Monto descuento");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    double val=Double.parseDouble(s);
                    if (val<0) throw new Exception();
                    if (val>tot) throw new Exception();

                    descaddmonto=val;
                    totalOrder();
                } catch (Exception e) {
                    mu.msgbox("Monto incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    //endregion
	
	//region Activity Events

	@Override
	protected void onResume() {

		try {

			super.onResume();

            P_prodrecetaObj.reconnect(Con,db);
            T_factrecetaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            P_linea_impresoraObj.reconnect(Con,db);
            P_impresoraObj.reconnect(Con,db);
            T_comandaObj.reconnect(Con,db);

            if (browse==6) {
                browse=0;
                if (gl.desc_monto>=0) {
                    descaddmonto=gl.desc_monto;
                    totalOrder();
                }
                return;
            }

            if (browse==5) {
                browse=0;
                if (gl.checksuper) {
                    browse=6;
                    startActivity(new Intent(this,DescMonto.class));
                }
                return;
            }

            if (browse==4) {
                browse=0;
                gl.ventalock=false;
                finish();
                return;
            }

            if (browse==3) {
				browse=0;
				if (gl.modo_cortesia) {
					listaCortesias();
				} else {
					checkPago();
				}

				return;
            }

            if (browse==2) {
                browse=0;
                if (gl.feluuid.isEmpty()) {
                    toastlongtop("No se logró certificación FEL");
                } else {
                    toastlongtop("Factura certificada : \n"+gl.feluuid);
                }
                impresionDocumento();
                return;
            }

            if (browse!=2) {
                checkPromo();
                checkPago();
            }

			if (browse==1) {
				browse=0;
				if (gl.promapl) updDesc();
				return;
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}	

	@Override
	public void onBackPressed() {
		try{
			clearGlobals();
			super.onBackPressed();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	//endregion

}
