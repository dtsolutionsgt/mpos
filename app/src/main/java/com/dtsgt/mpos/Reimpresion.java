package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsD_facturahnObj;
import com.dtsgt.classes.clsDocCanastaBod;
import com.dtsgt.classes.clsDocCobro;
import com.dtsgt.classes.clsDocDepos;
import com.dtsgt.classes.clsDocDevolucion;
import com.dtsgt.classes.clsDocCajaPagos;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsDocMov;
import com.dtsgt.classes.clsDocPedido;
import com.dtsgt.classes.clsP_gran_contObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.ladapt.ListAdaptCFDV;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class Reimpresion extends PBase {

	private ListView listView;
	private TextView lblTipo;
	
	private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
	private ListAdaptCFDV adapter;
	private clsClasses.clsCFDV selitem;

	private AppMethods app;
	private printer prn;
	private printer prn_nc;
	private printer  prn_can,prn_paseante;
	private Runnable printclose,printcallback,printvoid;
	private clsRepBuilder rep;
	
	private clsDocFactura fdoc;
	private clsDocMov mdoc;
	private clsDocDepos ddoc;
	private clsDocCobro cdoc;
	private clsDocDevolucion fdev;
	private clsDocCajaPagos fcpag;
	private clsDocCanastaBod fcanastabod;
	private clsDocCanastaBod fpaseantebod;
	private clsDocPedido docPed;

	private int tipo,impres;
	private String selid,itemid,corelNC,asignFact;
	//Para reimpresión de devolución de canastas y paseante
	private String  corel,existenciaC,existenciaP;

	//Fecha
	private boolean dateTxt,report;
	private TextView lblDateini,lblDatefin;
	public final Calendar c = Calendar.getInstance();
	private static final String BARRA = "/";
	final int mes = c.get(Calendar.MONTH);
	final int dia = c.get(Calendar.DAY_OF_MONTH);
	final int anio = c.get(Calendar.YEAR);
	public int cyear, cmonth, cday, validCB=0;
	private long datefin,dateini;

	private ArrayList<String> lines= new ArrayList<String>();
	private String pserie,pnumero,pruta,pvend,pcli,presol,presfecha,pfser,pfcor;
	private String presvence,presrango,pvendedor,pcliente,pclicod,pclidir;
	private double ptot;
	private boolean imprimecan=false,modo_sv,modo_gt;
	private int residx,ncFact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_reimpresion);

			super.InitBase();
			addlog("Reimpresion",""+du.getActDateTime(),gl.vend);

			listView = (ListView) findViewById(R.id.listView1);
			lblTipo= (TextView) findViewById(R.id.lblFecha);
			lblDateini = (TextView) findViewById(R.id.lblDateini2);
			lblDatefin = (TextView) findViewById(R.id.lblDatefin2);

			app = new AppMethods(this, gl, Con, db);
			gl.validimp=app.validaImpresora();
			if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

			tipo=gl.tipo;
			itemid="*";

			ProgressDialog("Cargando forma...");

			setHandlers();

			setFechaAct();

			modo_sv=false;modo_gt=false;
			if (gl.codigo_pais.equalsIgnoreCase("GT")) modo_gt=true;
			if (gl.codigo_pais.equalsIgnoreCase("SV")) modo_sv=true;

			listItems();

			prn=new printer(this,printclose,gl.validimp);
			prn_nc=new printer(this,printclose,gl.validimp);

			prn_can=new printer(this,printclose,gl.validimp);
			prn_paseante=new printer(this,printclose,gl.validimp);

			fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"",gl.peComboDet);
			fdoc.deviceid =gl.deviceId;

			fdev=new clsDocDevolucion(this,prn_nc.prw,gl.peMon,gl.peDecImp, "printnc.txt");
			fdev.deviceid =gl.deviceId;

			fcpag=new clsDocCajaPagos(this,prn.prw,"Pago de caja",gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
			fcpag.deviceid =gl.deviceId;

			printclose = new Runnable() {
			public void run() {
				int ii=1;
				try {
					if (ncFact == 1) {
						String corelFactura = getCorelFact(itemid);
						fdoc.buildPrint(corelFactura, 1, "TOL");
						prn.printnoask(printvoid, "print.txt");
					}
				} catch (Exception e) {
				}
			}
		};

		printvoid= new Runnable() {
			public void run() {
			}
		};

		printcallback = new Runnable() {
			public void run() {

				try {
					//#CKFK_20190401 03:43 PM Agregué esto para imprimir la NC cuando la factura está asociada a una
					corelNC = getCorelNotaCred(itemid);

					if (!corelNC.isEmpty()) {

						fdev.buildPrint(corelNC, 1, "TOL");
						prn_nc.printnoask(printvoid, "printnc.txt");
					}

					if (imprimecan){
						prn_can.printnoask(printclose, "printdevcan.txt");
						imprimecan=false;
					}

					askPrint();
				} catch (Exception e) {
					msgbox(e.getMessage());
				}

			}
		};

		switch (tipo) {
			//#CKFK 20200520 Quité la reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito
			case 2:
				ddoc=new clsDocDepos(this,prn.prw,gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
				lblTipo.setText("Depósito");break;
			case 3:
				fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"",gl.peComboDet);
				lblTipo.setText((gl.peMFact?"Factura":"Ticket"));break;
			case 4:
				mdoc=new clsDocMov(this,prn.prw,"Recarga",gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
				lblTipo.setText("Recarga");break;
			case 5:
				mdoc=new clsDocMov(this,prn.prw,"Dvolucion a bodega",gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
				lblTipo.setText("Devolución a bodega");break;
			case 7:
				fcpag=new clsDocCajaPagos(this,prn.prw,"Pago de caja",gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
				fcpag.deviceid =gl.deviceId;
				lblTipo.setText("Pagos de Caja");break;
			case 99:
				lblTipo.setText("Cierre de día");break;
		}

		}catch (Exception ex){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),ex.getMessage(),"");
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName() + " " + ex.getMessage());
		}
	}

	// Events
	
	public void printDoc(View view){
		try{
			if (itemid.equalsIgnoreCase("*")) {
				mu.msgbox("Debe seleccionar un documento.");return;
			}

			printDocument();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void setHandlers(){
		try{

			listView.setOnTouchListener(new SwipeListener(this) {
				public void onSwipeRight() {
					onBackPressed();
				}
				public void onSwipeLeft() {}
			});

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

						itemid=vItem.Cod;
						adapter.setSelectedIndex(position);
						//printDocument();
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox( e.getMessage());
					}
			};
			});

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

						itemid=vItem.Cod;
						adapter.setSelectedIndex(position);

						printDocument();
						//printDoc(view);
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox( e.getMessage());
					}
					return true;
				}
			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	// Main

	public void listItems() {
		Cursor DT = null;
		clsClasses.clsCFDV vItem;	
		int vP,f;
		double val;
		String id,sf,sval,tm,td,cont;
		long ff;
		boolean cont_flag;

		items.clear();
		
		selidx=-1;vP=0;
		
		try {

			switch (tipo) {
				//#CKFK 20200520 Quité la reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito
				case 2:
					progress.setMessage("Cargando lista de depósitos...");
					sql = "SELECT D_DEPOS.COREL,P_BANCO.NOMBRE,D_DEPOS.FECHA,D_DEPOS.TOTAL,D_DEPOS.CUENTA " +
						  "FROM D_DEPOS INNER JOIN P_BANCO ON D_DEPOS.BANCO=P_BANCO.CODIGO_BANCO " +
						  "WHERE (D_DEPOS.ANULADO='N') AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"')" +
						  "ORDER BY D_DEPOS.COREL DESC ";
					break;
				case 3:
					progress.setMessage("Cargando lista de facturas...");
					progress.show();
					//(D_FACTURA.STATCOM='N') AND
					sql = "SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO," +
						  "D_FACTURA.IMPRES, D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR,D_FACTURA.FEELCONTINGENCIA, D_FACTURA.AYUDANTE " +
						  "FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE " +
						  "WHERE (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
						  "ORDER BY D_FACTURA.COREL DESC";
					break;
				case 4:
					progress.setMessage("Cargando lista de recargas de inventario...");
					sql = "SELECT COREL,COREL,FECHA,0 AS TOTAL " +
						  "FROM D_MOV WHERE (TIPO='R') AND (ANULADO='N') AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
						  "ORDER BY COREL DESC ";
					break;
				case 5:
					progress.setMessage("Cargando lista de devoluciones de inventario...");
					sql = "SELECT COREL,COREL,FECHA,0 AS TOTAL " +
						  "FROM D_MOV WHERE (TIPO='D') AND (ANULADO='N') AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
						  "ORDER BY COREL DESC ";
					break;
				case 7:
					progress.setMessage("Cargando lista de pagos de caja...");
					sql = "SELECT COREL,OBSERVACION,'',MONTO,'',0 " +
						  "FROM P_CAJAPAGOS " +
						  "WHERE (STATCOM='N') AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
						  "ORDER BY COREL DESC";
					break;
			}

			if (tipo<99) {
				
				DT=Con.OpenDT(sql);

				if (DT != null){

					if (DT.getCount()>0) {

						DT.moveToFirst();
						while (!DT.isAfterLast()) {

							id=DT.getString(0);

							vItem =clsCls.new clsCFDV();

							vItem.Cod=DT.getString(0);vItem.tipodoc="";cont_flag=false;

							vItem.Desc=DT.getString(1);
							if (tipo==2) vItem.Desc+=" - "+DT.getString(4);

							if (tipo==3) {
								sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;
								if (tipo==3 && modo_sv) {
									td=DT.getString(9);vItem.tipodoc="F";
									if (td.equalsIgnoreCase("T")) vItem.tipodoc="T";
									if (td.equalsIgnoreCase("C")) vItem.tipodoc="C";
								}

								vItem.UUID=DT.getString(6)+"";
								cont=DT.getString(8)+"";
								if (vItem.UUID.length()<5) {
									if (cont.length()>5) cont_flag=true;
								}

							} else if (tipo==1||tipo==6||tipo==7){
								sf=DT.getString(0);
							} else {
								f=DT.getInt(2);sf=du.sfecha(f)+" "+du.shora(f);
							}

							vItem.flag=cont_flag;
							vItem.Fecha=sf;

							val=DT.getDouble(3);sval=""+val;
							vItem.Valor=sval;

							if (tipo==4 || tipo==5) {
								vItem.Valor="";
							} else {
								vItem.Valor=mu.frmcur(val);
							}

							if (tipo==3 || tipo==6) {
								if (gl.peModal.equalsIgnoreCase("TOL")) {
									items.add(vItem);
								} else {
									if (DT.getInt(5)<=1) items.add(vItem);
								}
							} else {
								items.add(vItem);
							}

							if (tipo==3) {
								vItem.UUID=DT.getString(6);
								ff=DT.getLong(7);
								vItem.FechaFactura=du.sfecha(ff)+" "+du.shora(ff);
								//vItem.FechaFactura=du.univfechalong(DT.getLong(7));
							}else{
								vItem.UUID="";
								vItem.FechaFactura="";
							}

							if (id.equalsIgnoreCase(selid)) selidx=vP;
							vP+=1;

							DT.moveToNext();

						}
					}
				}

			} else {	
				
				if (tipo==99) {

					vItem =clsCls.new clsCFDV();

					vItem.Cod="";
					vItem.Desc="";
					vItem.Fecha="Ultimo Cierre de dia";
					vItem.Valor="";

					items.add(vItem);
				}
			}

			if (DT != null)	DT.close();

		} catch (Exception e) {
			progress.cancel();
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		   	mu.msgbox("listItems: "+ e.getMessage());
	    }
			 
		adapter=new ListAdaptCFDV(this, items);
		listView.setAdapter(adapter);
		
		if (selidx>-1) {
			adapter.setSelectedIndex(selidx);
			listView.setSelection(selidx);
		}

		progress.cancel();
	}
	
	private void printDocument() {

		try{
			switch (tipo) {
				//#CKFK 20200520 Quité la reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito
				case 2:
					imprDeposito();break;
				case 3:
					if (gl.peModal.equalsIgnoreCase("TOL")) {
						imprFactura();
					} else {
						imprUltFactura();
					}
					break;
				case 4:
					imprRecarga();break;
				case 5:
					imprDevol();break;
				case 7:
					imprPagoCaja();break;
				case 99:
					imprFindia();break;
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//#CKFK 20200520 Quité la funciones para reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito

	private void imprDeposito() {
		try {
			if (prn.isEnabled()){
				ddoc.buildPrint(itemid, 1,gl.peModal);
				prn.printask(printcallback);
			} else if (!prn.isEnabled()){
				ddoc.buildPrint(itemid, 1,gl.peModal);
				toast("Reimpresion de deposito generada");
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}	
	}

	private void imprPagoCaja() {
		try {
			fcpag.buildPrint(itemid, 1,gl.peModal);
			app.doPrint();

			toast("Reimpresion de deposito generada");
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}
	}
	
	private void imprFactura() {
		Cursor dt;
		int impr;
		String svnit="N";

		fdoc.deviceid =gl.deviceId;

		try {
			sql="SELECT IMPRES,RAZON_ANULACION,AYUDANTE FROM D_FACTURA WHERE COREL='"+itemid+"'";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			impr=dt.getInt(0);
			gl.parallevar=dt.getString(1).equalsIgnoreCase("P");
			gl.domicilio=dt.getString(1).equalsIgnoreCase("D");
			svnit=dt.getString(2);

            if (dt!=null) dt.close();
		} catch (Exception e) {
			impr=1;
			gl.parallevar=false;
		}

		try {
            fdoc.parallevar=gl.parallevar;
			fdoc.domicilio=gl.domicilio;

			fdoc.factsinpropina=gl.peFactSinPropina;
            fdoc.es_pickup=gl.domicilio;
            fdoc.es_delivery=gl.delivery;
			fdoc.pais=gl.codigo_pais;
			fdoc.textopie=gl.peTextoPie;
			fdoc.pais = gl.codigo_pais;
			fdoc.fraseIVA = gl.peFraseIVA;
			fdoc.fraseISR = gl.peFraseISR;
			fdoc.idpais=gl.codigo_pais;
			fdoc.PropinaAparte=gl.peFactPropinaAparte;

			fdoc.tiendanom=gl.tiendanom;
			fdoc.tiendanit=gl.tiendanit;

			if (gl.codigo_pais.equalsIgnoreCase("HN")) cargaTotalesHonduras();
			if (gl.codigo_pais.equalsIgnoreCase("SV")) {
				cargaTotalesSalvador();
				fdoc.sal_nit="NIT: ";
				fdoc.svcf_nit="";fdoc.svcf_dep="";fdoc.svcf_muni="";fdoc.svcf_neg="";
				if (svnit.equalsIgnoreCase("C")) fdoc.sal_nit="NRC: ";
			}

			fdoc.LANPrint=gl.peImpFactLan;
			if (gl.peImpFactLan) fdoc.LAN_IP=gl.peImpFactIP;else fdoc.LAN_IP="";

			if (fdoc.buildPrint(itemid,impr,gl.peFormatoFactura,gl.peMFact)) {
                gl.QRCodeStr = fdoc.QRCodeStr;
				if (gl.codigo_pais.equalsIgnoreCase("HN")) gl.QRCodeStr="";
				if (gl.codigo_pais.equalsIgnoreCase("SV")) gl.QRCodeStr="";

				app.doPrint();

				try {
					sql="UPDATE D_FACTURA SET IMPRES=2 WHERE COREL='"+itemid+"'";
					db.execSQL(sql);
				} catch (Exception e) {	}
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}
	}

	private void cargaTotalesHonduras() {
		try {
			clsD_facturahnObj D_facturahnObj=new clsD_facturahnObj(this,Con,db);
			D_facturahnObj.fill("WHERE (COREL='"+itemid+"')");

			fdoc.fh_stotal=D_facturahnObj.first().subtotal;
			fdoc.fh_exon=D_facturahnObj.first().exon;
			fdoc.fh_exent=D_facturahnObj.first().exento;
			fdoc.fh_grav=D_facturahnObj.first().gravado;
			fdoc.fh_imp1 =D_facturahnObj.first().imp1;
			fdoc.fh_imp2 =D_facturahnObj.first().imp2;
			fdoc.fh_val1 =D_facturahnObj.first().val1;
			fdoc.fh_val2 =D_facturahnObj.first().val2;

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			fdoc.fh_stotal=0;fdoc.fh_exon=0;fdoc.fh_exent=0;
			fdoc.fh_grav=0;fdoc.fh_imp1 =0;fdoc.fh_imp2 =0;
		}
	}

	private void cargaTotalesSalvador() {
		try {
			clsD_facturahnObj D_facturahnObj=new clsD_facturahnObj(this,Con,db);
			D_facturahnObj.fill("WHERE (COREL='"+itemid+"')");

			fdoc.fh_stotal=D_facturahnObj.first().subtotal;
			fdoc.fh_exon=D_facturahnObj.first().exon;
			fdoc.fh_exent=D_facturahnObj.first().exento;
			fdoc.fh_grav=D_facturahnObj.first().gravado;
			fdoc.fh_imp1 =D_facturahnObj.first().imp1;
			fdoc.fh_imp2 =D_facturahnObj.first().imp2;
			fdoc.fh_val1 =D_facturahnObj.first().val1;
			fdoc.fh_val2 =D_facturahnObj.first().val2;

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			fdoc.fh_stotal=0;fdoc.fh_exon=0;fdoc.fh_exent=0;
			fdoc.fh_grav=0;fdoc.fh_imp1 =0;fdoc.fh_imp2 =0;
		}
	}

	private void imprRecarga() {
		try {
            mdoc.buildPrint(itemid,1);
            app.doPrint();
 		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}
	}

	private void imprDevol() {
		try {

			corel = itemid;

			impres=0;

			existenciaC=tieneCanasta(corel);
			existenciaP=tienePaseante(corel);

			if(existenciaC.isEmpty() && !existenciaP.isEmpty()) impres=1;
			if(!existenciaC.isEmpty() && existenciaP.isEmpty()) impres=2;
			if(existenciaC.isEmpty() && existenciaP.isEmpty()) impres=3;

			if (prn_can.isEnabled()) {

				String vModo=(gl.peModal.equalsIgnoreCase("TOL")?"TOL":"*");
				try {
					if(impres==0 || impres==1){
						fpaseantebod.buildPrint(corel,0,vModo);
					}
				} catch (Exception e) {
				}

				try {
					if(impres==0 || impres==2){
						imprimecan=true;
						fcanastabod.buildPrint(corel,0, vModo);
					}
				} catch (Exception e) {
				}

				if(impres==0) {
					prn_paseante.printask(printcallback, "printpaseante.txt");
				}else if(impres==1) {
					prn_paseante.printask(printcallback, "printpaseante.txt");
				}else if(impres==2) {
					prn_can.printask(printcallback, "printdevcan.txt");
				}

			}else if(!prn_can.isEnabled()){

				String vModo=(gl.peModal.equalsIgnoreCase("TOL")?"TOL":"*");

				if(impres==0 || impres==1){
					fpaseantebod.buildPrint(corel,0,vModo);
				}

				if(impres==0 || impres==2){
					imprimecan=true;
					fcanastabod.buildPrint(corel,0, vModo);
				}

			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}
	}

	//CM_20190506: Valida si tiene canastas y devolución

	private String tieneCanasta(String vCorel){

		Cursor DT;
		String vtieneCanasta= "";

		try{

			sql = "SELECT COREL FROM D_MOVDCAN WHERE COREL = '" + vCorel + "' AND COREL IN (SELECT COREL FROM D_MOV)";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0){
				DT.moveToFirst();
				vtieneCanasta = DT.getString(0);

                if (DT!=null) DT.close();
			}

		}catch (Exception ex){
			mu.msgbox("Ocurrió un error "+ex.getMessage());
		}

		return vtieneCanasta;
	}

	private String tienePaseante(String vCorel){

		Cursor DT;
		String vtienePaseante= "";

		try{

			sql = "SELECT COREL FROM D_MOVD WHERE COREL = '" + vCorel + "' AND COREL IN (SELECT COREL FROM D_MOV)";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0){
				DT.moveToFirst();
				vtienePaseante = DT.getString(0);

                if (DT!=null) DT.close();
			}

		}catch (Exception ex){
			mu.msgbox("Ocurrió un error "+ex.getMessage());
		}

		return vtienePaseante;
	}

	private void imprFindia() {
		try {
			if(prn.isEnabled()){
				prn.printask("SyncFold/findia.txt");
			}else if(!prn.isEnabled()){
				toast("No hay impresora configurada");
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			mu.msgbox(e.getMessage());
		}
	}	

	//Fecha

	public void showDateDialog1(View view) {
		try{
			obtenerFecha();
			dateTxt=false;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void showDateDialog2(View view) {
		try{
			obtenerFecha();
			dateTxt=true;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void obtenerFecha(){
		try{
			DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

					final int mesActual = month + 1;

					String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
					String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

					if(dateTxt) {
						lblDatefin.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
					}

					if(!dateTxt) {
						lblDateini.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
					}

					cyear = year;
					cmonth = Integer.parseInt(mesFormateado);
					cday = Integer.parseInt(diaFormateado);

					if(dateTxt) {
						datefin = du.cfechaRep(cyear, cmonth, cday, false);
					}

					if(!dateTxt){
						dateini  = du.cfechaRep(cyear, cmonth, cday, true);
					}

					//listar nuevamente los documentos
					listItems();
				}
			},anio, mes, dia);

			/*itemR.clear();
			lblFact.setText("");
			lblImp.setText("GENERAR");*/
			report=false;

			recogerFecha.show();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void setFechaAct(){
		Long fecha;
		String date;

		try{
			fecha = du.getFechaActualReport();

			date = du.univfechaReport(fecha);

			lblDateini.setText(date);
			lblDatefin.setText(date);

			datefin = du.getFechaActualReport(false);
			dateini = du.getFechaActualReport(true);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	// Ultima factura
	private void imprUltFactura() {
		Cursor dt;
		String id,serie;
		int corel;

		try {

			sql="SELECT COREL,IMPRES,SERIE,CORELATIVO FROM D_FACTURA WHERE COREL='"+itemid+"'";
			dt=Con.OpenDT(sql);

			if (dt.getCount()==0) {
				msgbox("¡No existe ninguna factura elegible para reimpresion!");return;
			}
			
			dt.moveToFirst();
			
			id=dt.getString(0);
			serie=dt.getString(2);
			corel=dt.getInt(3);		
			
			if (dt.getInt(1)>1) {
				msgbox("¡La factura "+serie+" - "+corel+" no se puede imprimir porque ya fue reimpresa anteriormente!");return;
			}

			if(prn.isEnabled()){
				if (fdoc.buildPrint(id,1,gl.peFormatoFactura)) prn.printask();

				try {
					sql="UPDATE D_FACTURA SET IMPRES=2 WHERE COREL='"+itemid+"'";
					db.execSQL(sql);
				} catch (Exception e) {
				}

				//#CKFK_20190401 03:43 PM Agregué esto para imprimir la NC cuando la factura está asociada a una
				String corelNC=getCorelNotaCred(itemid);

				if (!corelNC.isEmpty()){
					fdev=new clsDocDevolucion(this,prn.prw,gl.peMon,gl.peDecImp, "printnc.txt");
					fdev.deviceid =gl.deviceId;
					fdev.buildPrint(corelNC, 1, "TOL"); prn_nc.printask(printclose, "printnc.txt");
				}

			}else if(!prn.isEnabled()){

				fdoc.buildPrint(id,1,gl.peFormatoFactura);

				try {
					sql="UPDATE D_FACTURA SET IMPRES=2 WHERE COREL='"+itemid+"'";
					db.execSQL(sql);
				} catch (Exception e) {
				}

				String corelNC=getCorelNotaCred(itemid);

				if (!corelNC.isEmpty()){
					fdev.buildPrint(corelNC, 1, "TOL");
					toast("Reimpresion de UltFactura generada");
				}
			}

            if (dt!=null) dt.close();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private String getCorelFact(String vCorel){

		Cursor DT;
		String vtieneFacturaNC= "";
		ncFact=0;

		try{

			sql = "SELECT F.COREL, F.ASIGNACION, N.COREL "+
				  "FROM D_FACTURA F INNER JOIN D_NOTACRED N ON F.COREL = N.FACTURA "+
				  "WHERE N.COREL = '"+vCorel+"'";

			DT=Con.OpenDT(sql);

			if(DT.getCount()==0){
				sql = "SELECT FACTURA FROM D_NOTACRED WHERE COREL = '"+ vCorel +"'";
				DT=Con.OpenDT(sql);

				if(DT.getCount()>0){
					DT.moveToFirst();
					vtieneFacturaNC = DT.getString(0);
					ncFact=2;

					return vtieneFacturaNC;
				}

			}else if(DT.getCount()>0){
				DT.moveToFirst();
				vtieneFacturaNC = DT.getString(0);
				asignFact = DT.getString(2);
				ncFact=1;
			}

            if (DT!=null) DT.close();
		}catch (Exception ex){
			mu.msgbox("tieneFacturaNC ocurrió un error: "+ex.getMessage());
		}

		return vtieneFacturaNC;
	}

	private String getCorelNotaCred(String vCorel){

		Cursor DT;
		String vCorelNC= "";

		try{

			sql = "SELECT N.COREL FROM D_FACTURA F  INNER JOIN D_NOTACRED N ON F.COREL = N.FACTURA "+
				  "WHERE F.COREL = '" + vCorel + "'";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0){
				DT.moveToFirst();
				vCorelNC = DT.getString(0);
                if (DT!=null) DT.close();
			}

		}catch (Exception ex){
			mu.msgbox("tieneNCFactura ocurrió un error "+ex.getMessage());
		}

		return vCorelNC;
	}

	private void msgAsk(String msg) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("mPos");
			dialog.setMessage(msg  + " ?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					printDocument();
				}
			});
			dialog.setNegativeButton("No", null);
			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void askPrint() {
		try {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("mPos");
			dialog.setMessage("¿Impresión correcta?");

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					try {

						switch (tipo) {
							//#CKFK 20200520 Quité la reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito

							case 2:
								sql = "UPDATE D_DEPOS SET IMPRES=IMPRES+1 WHERE COREL='" + itemid + "'";
								db.execSQL(sql);
								db.execSQL("UPDATE FinDia SET val3=1");
								break;
							case 3:
								sql = "UPDATE D_FACTURA SET IMPRES=IMPRES+1 WHERE COREL='" + itemid + "'";
								db.execSQL(sql);
								try {
									sql = "UPDATE D_NOTACRED SET IMPRES=IMPRES+1 WHERE COREL='" + corelNC + "'";
									db.execSQL(sql);
								} catch (Exception e) {}
								break;
							case 5:
								sql = "UPDATE D_MOV SET IMPRES=IMPRES+1 WHERE COREL='" + itemid + "' AND TIPO='D' ";
								db.execSQL(sql);
								break;
						}
					} catch (Exception e) {
						msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
					}
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});

			dialog.show();
		} catch (Exception e) {
			addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
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

	public void regresar(View v) {

		ExDialog dialog = new ExDialog(this);
		dialog.setMessage("¿Salir?");

		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});

		dialog.show();

	}

	private void msgAskExit(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage("¿" + msg + "?");

		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});

		dialog.show();

	}

}
