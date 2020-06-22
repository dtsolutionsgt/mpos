package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
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
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsD_facturasObj;
import com.dtsgt.classes.clsDescGlob;
import com.dtsgt.classes.clsDocDevolucion;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_mediapagoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.fel.FelFactura;
import com.dtsgt.ladapt.ListAdaptTotals;

import java.util.ArrayList;
import java.util.List;

public class FacturaRes extends PBase {

	private ListView listView;
	private TextView lblPago,lblFact,lblMPago,lblCred, lblCard,lblMonto,lblKeyDP,lblTotal,lblPEfect,lblPCard;
	private ImageView imgBon,imgMPago,imgCred, imgCard;
	private TextView lblVuelto;
	private EditText txtVuelto;
	private RelativeLayout rl_facturares;

	private List<String> spname = new ArrayList<String>();
	private ArrayList<clsClasses.clsCDB> items= new ArrayList<clsClasses.clsCDB>();
	private ListAdaptTotals adapter;
	
	private Runnable printcallback,printclose,printexit;
	
	private clsDescGlob clsDesc;
	private printer prn;
	private printer prn_nc;
	private clsDocFactura fdoc;
	private clsDocDevolucion fdev;
	private AppMethods app;
    private clsKeybHandler khand;

	private long fecha,fechae;
	private int fcorel,clidia,media;
	private String itemid,cliid,corel,sefect,fserie,desc1,svuelt,corelNC,idfel;
	private int cyear, cmonth, cday, dweek,stp=0,brw=0,notaC,impres;

	private double dmax,dfinmon,descpmon,descg,descgmon,descgtotal,tot,pend,stot0,stot,descmon,totimp,totperc,credito;
	private double dispventa,falt;
	private boolean acum,cleandprod,peexit,pago,saved,rutapos,porpeso,pagocompleto=false;


	//@SuppressLint("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_factura_res);

		super.InitBase();
        addlog("FacturaRes",""+du.getActDateTime(),String.valueOf(gl.vend));

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

		imgBon = (ImageView) findViewById(R.id.imageView6);
		imgMPago = (ImageView) findViewById(R.id.btnImp);
		imgCred = (ImageView) findViewById(R.id.imageView3);
		imgCard = (ImageView) findViewById(R.id.imageView2);

		rl_facturares=(RelativeLayout)findViewById(R.id.relativeLayout1);
		rl_facturares.setVisibility(View.VISIBLE);

		lblVuelto = new TextView(this,null);
		txtVuelto = new EditText(this,null);

		cliid=gl.cliente;
		rutapos=gl.rutapos;
		media=gl.media;
		credito=gl.credito;
        idfel=gl.peFEL;

		gl.cobroPendiente = false;
		dispventa = gl.dvdispventa;dispventa=mu.round(dispventa,2);
		notaC = gl.tiponcredito;

		app = new AppMethods(this, gl, Con, db);
        khand=new clsKeybHandler(this,lblMonto,lblKeyDP);

        imgCred.setVisibility(View.VISIBLE);
        lblCred.setVisibility(View.VISIBLE);

        imgMPago.setVisibility(View.INVISIBLE);
        lblMPago.setVisibility(View.INVISIBLE);

        if (media==4) {
            if (credito<=0 || gl.facturaVen != 0) {
                imgCred.setVisibility(View.INVISIBLE);
                lblCred.setVisibility(View.INVISIBLE);
            } else if(credito > 0){
                imgCred.setVisibility(View.VISIBLE);
                lblCred.setVisibility(View.VISIBLE);
            }
        }

        fecha=du.getActDateTime();
		fechae=fecha;

		dweek=mu.dayofweek();

		clsDesc=new clsDescGlob(this);

		descpmon=totalDescProd();

		dmax=clsDesc.dmax;
		acum=clsDesc.acum;

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
				FacturaRes.super.finish();
			}
		};

		prn=new printer(this,printexit,gl.validimp);
		prn_nc=new printer(this,printclose,gl.validimp);

		fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp, "");
		fdoc.deviceid =gl.deviceId;

		fdev=new clsDocDevolucion(this,prn_nc.prw,gl.peMon,gl.peDecImp, "printnc.txt");
		fdev.deviceid =gl.deviceId;

		saved=false;
		assignCorel();

		cliPorDia();

		setHandlers();

        txtVuelto.setInputType(InputType.TYPE_CLASS_NUMBER  | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        pend=tot;
        if (tot>credito) credito=0;

        if (credito<=0) {
            imgCred.setVisibility(View.INVISIBLE);
            lblCred.setVisibility(View.INVISIBLE);
        }

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
        try{
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

    public void delPay(View view) {
	    askDelPago();
	}

    public void showBon(View view) {
		try{
			Intent intent = new Intent(this,BonVenta.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

	public void DaVuelto(View view) {
		try{
			Davuelto();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	public void pendientePago(View view){
		try{
			askPendientePago();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

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
        if (khand.isEnter) validaPagoEfectivo();
    }

	//endregion

	//region Main

	private void processFinalPromo(){

		descg=gl.descglob;
		descgtotal=gl.descgtotal;

		try{

			//descgmon=(double) (stot0*descg/100);
			descgmon=(double) (descg*descgtotal/100);
			totalOrder();

			if (descg>0) {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						showPromo();
					}
				}, 300);
			}

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

			fillTotals();
		}catch (Exception e){
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

				tot=stot+totimp-descmon+totperc;
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

				if (gl.dvbrowse!=0){
					item = clsCls.new clsCDB();
					item.Cod="Nota Crédito";item.Desc=mu.frmcur(-dispventa);item.Bandera=0;
					items.add(item);

					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot-dispventa);item.Bandera=1;
					items.add(item);

				}else{
					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot);item.Bandera=1;
					items.add(item);
				}

			} else {

				totimp=mu.round2(totimp);
				tot=stot-descmon;
				tot=mu.round2(tot);


				item = clsCls.new clsCDB();
				item.Cod="Subtotal";item.Desc=mu.frmcur(stot);item.Bandera=0;
				items.add(item);

				item = clsCls.new clsCDB();
				item.Cod="Descuento";item.Desc=mu.frmcur(-descmon);item.Bandera=0;
				items.add(item);

				if (gl.dvbrowse!=0){

					item = clsCls.new clsCDB();
					item.Cod="Nota Crédito";item.Desc=mu.frmcur(-dispventa);item.Bandera=0;
					items.add(item);

					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot-dispventa);item.Bandera=1;
					items.add(item);

				}else{

					item = clsCls.new clsCDB();
					item.Cod="TOTAL";item.Desc=mu.frmcur(tot);item.Bandera=1;
					items.add(item);

				}

			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

		adapter=new ListAdaptTotals(this,items);
		listView.setAdapter(adapter);

        lblTotal.setText(mu.frmcur(tot));
	}

 	private void finishOrder(){

		if (!saved) {
			if (!saveOrder()) return;
		}

        gl.cliposflag=false;

        if (!app.usaFEL()) {

            impressOrder();

        } else {
            if (isNetworkAvailable()) {
                browse=2;
                gl.felcorel=corel;gl.feluuid="";
                startActivity(new Intent(this, FelFactura.class));
            } else {
                toast("No hay conexion a internet");
                impressOrder();
            }
        }

	}

	private void impressOrder(){

		try{

			rl_facturares.setVisibility(View.INVISIBLE);

			if(gl.dvbrowse!=0) gl.dvbrowse =0;

			impres=0;

			if (app.impresora()) {
                fdoc.buildPrint(corel, 0,"",gl.peMFact);
                app.doPrint(2);
            }

			/*
			if (prn.isEnabled()) {

				if (gl.peModal.equalsIgnoreCase("APR")) {
					fdoc.buildPrintExt(corel,2,"APR");
				} else if (gl.peModal.equalsIgnoreCase("...")) {
					//
				} else if (gl.peModal.equalsIgnoreCase("TOL")) {

					if (!gl.cobroPendiente) {
						if (impres==0) {
							fdoc.buildPrint(corel, 0,gl.peFormatoFactura);
						} else {
							fdoc.buildPrint(corel, 10,gl.peFormatoFactura);
						}
					}else{
						fdoc.buildPrint(corel,4,gl.peFormatoFactura);
					}
				}

				if (gl.peImprFactCorrecta) {
					prn.printask(printcallback);
				} else {
					singlePrint();
				}

			}else if(!prn.isEnabled()){
				if (gl.peModal.equalsIgnoreCase("APR")) {
					fdoc.buildPrintExt(corel,2,"APR");
				} else if (gl.peModal.equalsIgnoreCase("...")) {
					//
				} else if (gl.peModal.equalsIgnoreCase("TOL")) {

					if (!gl.cobroPendiente) {
						if (impres==0) {
							fdoc.buildPrint(corel, 0,gl.peFormatoFactura);
						} else {
							fdoc.buildPrint(corel, 10,gl.peFormatoFactura);
						}
					}else{
						fdoc.buildPrint(corel,4,gl.peFormatoFactura);
					}
				}

				if (notaC==2){
					fdev.buildPrint(gl.dvcorrelnc,0);
				}
			}
			*/

			//gl.closeCliDet=true;
			//gl.closeVenta=true;
            gl.iniciaVenta=true;

			//if (!prn.isEnabled())

            super.finish();

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox("impressOrder: "  + e.getMessage());

			//gl.closeCliDet = true;
			//gl.closeVenta = true;

			super.finish();
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
        clsClasses.clsD_facturas fsitem;

		Cursor dt;
		String vprod,vumstock,vumventa,vbarra;
		double vcant,vpeso,vfactor,peso,factpres,vtot,vprec;
		int mitem,bitem,prid,prcant,unid,unipr,dev_ins=1,fsid;
		boolean flag;

		corel=gl.ruta+"_"+mu.getCorelBase();

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
			if(gl.validDate) ins.add("FECHA",gl.lastDate); else ins.add("FECHA",fecha);
			ins.add("RUTA",gl.ruta);
			ins.add("VENDEDOR",gl.codigo_vendedor);
			ins.add("CLIENTE",gl.codigo_cliente);

			ins.add("KILOMETRAJE",0);
			ins.add("FECHAENTR",fechae);
			ins.add("FACTLINK"," ");
	   		ins.add("TOTAL",tot);
			ins.add("DESMONTO",descmon);
			ins.add("IMPMONTO",totimp+totperc);
			ins.add("PESO",peso);

			ins.add("BANDERA","N");
			ins.add("STATCOM","N");
			ins.add("CALCOBJ",false);
			ins.add("SERIE",fserie);
			ins.add("CORELATIVO",fcorel);
			ins.add("IMPRES",0);

			ins.add("ADD1",gl.ref1);
			ins.add("ADD2",gl.ref2);
			ins.add("ADD3",gl.ref3);

			ins.add("DEPOS",false);
			ins.add("PEDCOREL","");
			ins.add("REFERENCIA","");

			if (gl.dvbrowse!=0){
				ins.add("ASIGNACION",gl.dvcorreld);
			}else{
				ins.add("ASIGNACION","");
			}

			ins.add("SUPERVISOR"," ");
			ins.add("VEHICULO"," ");
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

			sql="SELECT PRODUCTO,CANT,PRECIO,IMP,DES,DESMON,TOTAL,PRECIODOC,PESO,VAL2,VAL4,UM,FACTOR,UMSTOCK,EMPRESA FROM T_VENTA";
			dt=Con.OpenDT(sql);

			dt.moveToFirst();
			while (!dt.isAfterLast()) {

				porpeso=false;
				factpres=dt.getDouble(12);
				peso=dt.getDouble(8);
				vumstock=dt.getString(11);

			  	ins.init("D_FACTURAD");
				ins.add("COREL",corel);
				ins.add("PRODUCTO",app.codigoProducto(dt.getString(0)));
				ins.add("EMPRESA",gl.emp);
				ins.add("ANULADO",false);
				ins.add("CANT",dt.getDouble(1));
				ins.add("PRECIO",dt.getDouble(2));
				ins.add("IMP",dt.getDouble(3));
				ins.add("DES",dt.getDouble(4));
				ins.add("DESMON",dt.getDouble(5));
				ins.add("TOTAL",dt.getDouble(6));
				ins.add("PRECIODOC",dt.getDouble(7));
				ins.add("PESO",dt.getDouble(8));
				ins.add("VAL1",dt.getDouble(9));
				ins.add("VAL2",dt.getString(10));
				ins.add("UMVENTA",dt.getString(11));
				ins.add("FACTOR",dt.getDouble(12));
                vumstock=app.umVenta2(dt.getString(0));
				ins.add("UMSTOCK",vumstock);
				ins.add("UMPESO",dt.getString(13));

			    db.execSQL(ins.sql());

			    vprod=dt.getString(0);
				//vumstock=dt.getString(13);
				vcant=dt.getDouble(1);
				vpeso=dt.getDouble(8);
				vfactor=vpeso/(vcant*factpres);
				vumventa=dt.getString(11);

				if (esProductoConStock(dt.getString(0))) {
					//rebajaStockUM(vprod, vumstock, vcant, vfactor, vumventa,factpres,peso);
                    rebajaStockUM(vprod,vumstock,vcant);
				}

			    dt.moveToNext();
			}

			//endregion

			//region D_FACTURAP

            sql = "SELECT ITEM,CODPAGO,TIPO,VALOR,DESC1,DESC2,DESC3 FROM T_PAGO";
            dt = Con.OpenDT(sql);

            dt.moveToFirst();
            while (!dt.isAfterLast()) {

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

			ins.init("D_FACTURAF");

			ins.add("COREL",corel);
			ins.add("NOMBRE",gl.fnombre);
			ins.add("NIT",gl.fnit);
			ins.add("DIRECCION",gl.fdir);

			db.execSQL(ins.sql());

			//endregion

            //region D_FACTURAS

            //sql="SELECT SUM(UNID*CANT),IDSELECCION,IDCOMBO FROM T_COMBO GROUP BY IDSELECCION";
            sql="SELECT UNID,CANT,IDSELECCION,IDCOMBO FROM T_COMBO ";
            dt=Con.OpenDT(sql);

            String prcod="";

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    prcant=dt.getInt(0);
                    unid=dt.getInt(1);
                    unipr=prcant*unid;
                    prid=dt.getInt(2);
                    fsid=dt.getInt(3);

                    P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prid);
                    prcod=P_productoObj.first().codigo;

                    fsitem=clsCls.new clsD_facturas();

                    fsitem.corel=corel;
                    fsitem.id=fsid;
                    fsitem.producto=""+prid;
                    fsitem.cant=unipr;
                    fsitem.umstock=app.umVenta2(prcod);

                    D_facturas.add(fsitem);

                    rebajaStockUM(prcod,fsitem.umstock,fsitem.cant);

                    dt.moveToNext();
                }
            }

            //endregion

			//region Bonificaciones

            //region D_BONIF

            /*
			sql="SELECT ITEM,PRODID,BONIID,CANT,PRECIO,COSTO,UMVENTA,UMSTOCK,UMPESO,FACTOR FROM T_BONITEM";
			dt=Con.OpenDT(sql);

			if(dt.getCount()>0){

				dt.moveToFirst();bitem=1;
				while (!dt.isAfterLast()) {

					vcant= dt.getDouble(3);
					vprec= dt.getDouble(4);
					vtot= vcant*vprec;vtot=mu.roundr(vtot,2);

					ins.init("D_BONIF");

					ins.add("COREL",corel);
					ins.add("ITEM",bitem);
					ins.add("FECHA",fecha);
					ins.add("ANULADO","N");
					ins.add("EMPRESA",gl.emp);
					ins.add("RUTA",gl.ruta);
					ins.add("CLIENTE",gl.cliente);
					ins.add("PRODUCTO",dt.getString(2));
					ins.add("CANT",vcant);
					ins.add("VENPED","V");
					ins.add("TIPO","");
					ins.add("PRECIO",vprec);
					ins.add("COSTO",dt.getDouble(5));
					ins.add("TOTAL",vtot );
					ins.add("STATCOM","N");
					ins.add("UMVENTA",dt.getString(6));
					ins.add("UMSTOCK",dt.getString(7) );
					ins.add("UMPESO",dt.getString(8));
					ins.add("FACTOR",dt.getDouble(9));

					db.execSQL(ins.sql());

					vprod=dt.getString(2);
					vumstock=dt.getString(7);
					vumventa=dt.getString(6);
					vfactor=dt.getDouble(9);
					peso=vcant*vfactor;
					factpres=app.factorPres(vprod,vumventa,vumstock);

					rebajaStockBonif(vprod, vumstock, vcant, vfactor, vumventa,factpres,peso);

					dt.moveToNext();bitem++;
				}
			}


             */
            //endregion

			//endregion

			//region Actualizacion de ultimo correlativo

			sql="UPDATE P_COREL SET CORELULT="+fcorel+"  WHERE RUTA='"+gl.ruta+"'";
			db.execSQL(sql);

			ins.init("D_FACT_LOG");
			ins.add("ITEM",mitem);
			ins.add("SERIE",fserie);
			ins.add("COREL",fcorel);
			ins.add("FECHA",0);
			ins.add("RUTA",gl.ruta);
			db.execSQL(ins.sql());

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
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
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

				sql="DELETE FROM P_STOCK WHERE (CANT<=0) AND (CANTM<=0)";
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

    private void rebajaStockUM(String prid,String umstock,double cantapl) {
        Cursor dt;
        double dispcant,actcant;

        try {

            sql="SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV " +
                    "FROM P_STOCK WHERE (CANT>0) AND (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"') ORDER BY CANT";
            dt=Con.OpenDT(sql);

            if (dt.getCount()==0) return;

            dispcant=dt.getDouble(0);
            actcant=dispcant-cantapl;

            sql="UPDATE P_STOCK SET CANT="+actcant+",PESO=0 WHERE (CODIGO='"+prid+"') AND (UNIDADMEDIDA='"+umstock+"')";
            db.execSQL(sql);

            sql="DELETE FROM P_STOCK WHERE (CANT<=0) AND (CANTM<=0)";
            db.execSQL(sql);

            if (dt!=null) dt.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("rebajaStockUM: "+e.getMessage());
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

				sql="DELETE FROM P_STOCK WHERE (CANT<=0) AND (CANTM<=0)";
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
        int ca,ci,cf,ca1,ca2;

        fcorel=0;fserie="";

        try {

            sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN FROM P_COREL WHERE RUTA="+gl.codigo_ruta;
            DT=Con.OpenDT(sql);

            if(DT.getCount()>0){
                DT.moveToFirst();

                fserie=DT.getString(0);
                ca1=DT.getInt(1);
                ci=DT.getInt(2);
                cf=DT.getInt(3);
            } else {
                fcorel=0;fserie="";
                //mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n");
                return;
            }


            sql="SELECT MAX(COREL) FROM D_FACT_LOG WHERE RUTA="+gl.ruta+" AND SERIE='"+fserie+"'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                DT.moveToFirst();
                ca2=DT.getInt(0);
            }else {
                ca2=0;
            }

            ca=ca1;if (ca2>ca) ca=ca2;
            fcorel=ca+1;

            if (fcorel>cf) {
                mu.msgbox("Se ha acabado el talonario de facturas. No se puede continuar con la venta.");
                fcorel=0;return;
            }

            //#HS_20181128_1602 Cambie el texto del mensaje.
            if (fcorel==cf) mu.msgbox("Esta es la última factura disponible.");

            if (gl.peMFact) s="Factura : ";else s="Ticket : ";
            lblFact.setText(s+fserie+" - "+fcorel+" , Talonario : "+fcorel+" / "+cf);

            if (DT!=null) DT.close();

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
            }else{
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
					vuel=vuel-tot;
                    lblMonto.setText("");khand.val="";

					if (vuel<0.00) {
						msgbox("Pago insuficiente, faltan Q."+falt);return;
					}

					if (vuel==0.0){
						msgAskVuelto("Pago Realizado Correctamente");
					} else {
						msgAskVuelto("Su vuelto : "+mu.frmdec(vuel));
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

        dialog.setTitle("Vuelto");
        dialog.setMessage(msg);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                applyCash();
                checkPago();
                if (idfel.isEmpty()) {
                    finish();
                }
            }
        });

        dialog.show();

    }

    public void Davuelto(){

		try{

			double pg,vuel;

			svuelt= txtVuelto.getText().toString();

			if (!svuelt.equals("")){

				pg=Double.parseDouble(svuelt);

				if (pg > 0) {

					if (gl.dvbrowse!=0){
						double totdv;
						totdv = mu.round(tot-dispventa,2);

						if (pg<totdv) {
							msgbox("Monto menor que total");
						}

						vuel=pg-tot;

						lblVuelto.setText(String.format("    Vuelto: " + mu.frmcur(vuel)) );

					}else{

						if (pg<tot) {
							msgbox("Monto menor que total");
						}

						vuel=pg-tot;

						lblVuelto.setText(String.format("    Vuelto: " + mu.frmcur(vuel)) );

					}

				}

			}

		}catch (Exception e) {
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
                applyCash();
                checkPago();
                if (idfel.isEmpty()) finish();
            } else {
                aplcash=aplcash-vuel;
                sefect=""+aplcash;
                //applyCash();
                checkPagoNF();
                msgAskVuelto("Su vuelto : "+mu.frmdec(vuel));
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

			//sql="DELETE FROM T_PAGO WHERE CODPAGO=1";
			//db.execSQL(sql);
			//if (epago>plim) {
			//	MU.msgbox("Total de pago mayor que total de saldos.");return;
			//}

			//if (epago>tsel) {
			//	msgAskOverPayd("Total de pago mayor que saldo\nContinuar");return;
			//}

			sql="SELECT CODIGO FROM P_MEDIAPAGO WHERE NIVEL = 1";
			dt = Con.OpenDT(sql);
			if(dt!=null) {
				if(dt.getCount()>0){
					dt.moveToFirst();
					codpago=dt.getInt(0);
				}

                if (dt!=null) dt.close();
			}

			//sql="DELETE FROM T_PAGO";
			//db.execSQL(sql);

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

	//region Aux

	private void clearGlobals() {

		try {

			db.execSQL("DELETE FROM T_PAGO");

			db.execSQL("DELETE FROM T_BONITEM WHERE PRODID='*'");

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

	//endregion

    //region Dialogs

    public void askSave(View view) {
        try{
            checkPago();
        }catch (Exception e){
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

    //#HS_20181212 Dialogo para Pendiente de pago
    private void askPendientePago() {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Road");
            dialog.setMessage("Está factura quedará PENDIENTE DE PAGO, deberá realizar el pago posteriormente. ¿Está seguro?");

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.cobroPendiente = true;
                    finishOrder();
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


    //endregion
	
	//region Activity Events

	@Override
	protected void onResume() {

		try {

			super.onResume();

            if (browse==3) {
                browse=0;
                checkPago();
                return;
            }

            if (browse==2) {
                browse=0;
                if (gl.feluuid.isEmpty()) {
                    toastlongtop("No se logró certificación FEL");
                } else {
                    toastlongtop("Factura certificada : \n"+gl.feluuid);
                }
                impressOrder();
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
