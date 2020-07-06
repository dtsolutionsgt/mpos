package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsDocDevolucion;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.fel.FELmsgbox;
import com.dtsgt.fel.FelFactura;
import com.dtsgt.fel.clsFELInFile;
import com.dtsgt.ladapt.ListAdaptCFDV;
import com.dtsgt.webservice.wsInvActual;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Anulacion extends PBase {

	private ListView listView;
	private TextView lblTipo;
	
	private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
	private ListAdaptCFDV adapter;
	private clsClasses.clsCFDV selitem;

    private wsInvActual wsi;
    private Runnable recibeInventario;

    private Runnable printotrodoc,printclose;
	private printer prn;
    private printer prn_nc;
    private  clsRepBuilder rep;
	private clsDocAnul doc;
	private clsDocFactura fdoc;

	private clsClasses.clsCFDV sitem;
	private AppMethods app;

    private clsFELInFile fel;
	private Anulacion.WebServiceHandler ws;
	private XMLObject xobj;

	private String CSQL;
	private boolean factsend;

	private int tipo,depparc,fcorel;	
	private String selid,itemid,fserie,fres,felcorel,uuid;
	private boolean modoapr=false,demomode;

	// impresion nota credito
	
	private ArrayList<String> lines= new ArrayList<String>();
	private String pserie,pnumero,pruta,pvend,pcli,presol,presfecha,pfser,pfcor;
	private String presvence,presrango,pvendedor,pcliente,pclicod,pclidir;
	private double ptot;
	private int residx;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anulacion);
		
		super.InitBase();
		addlog("Anulacion",""+du.getActDateTime(),String.valueOf(gl.vend));
		
		listView = (ListView) findViewById(R.id.listView1);
		lblTipo= (TextView) findViewById(R.id.lblDescrip);
		lblDateini = (TextView) findViewById(R.id.lblDateini2);
		lblDatefin = (TextView) findViewById(R.id.lblDatefin2);

		app = new AppMethods(this, gl, Con, db);
		gl.validimp=app.validaImpresora();
		if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

		tipo=gl.tipo;
		if (gl.peModal.equalsIgnoreCase("APR")) modoapr=true;
		
		if (tipo==0) lblTipo.setText("Pedido");
		if (tipo==1) lblTipo.setText("Recibo");
		if (tipo==2) lblTipo.setText("Depósito");
		if (tipo==3) lblTipo.setText((gl.peMFact?"Factura":"Ticket"));
		if (tipo==4) lblTipo.setText("Recarga");
		if (tipo==5) lblTipo.setText("Devolución a bodega");
		//if (tipo==6) lblTipo.setText("Nota de crédito");

		itemid="*";

        fel=new clsFELInFile(this,this);
		getURL();
		ws = new Anulacion.WebServiceHandler(Anulacion.this, gl.wsurl, gl.timeout);
		xobj = new XMLObject(ws);

        clsP_sucursalObj sucursal=new clsP_sucursalObj(this,Con,db);
        sucursal.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
        clsClasses.clsP_sucursal suc=sucursal.first();

        fel.fel_llave_certificacion =suc.fel_llave_certificacion;
        fel.fel_llave_firma=suc.fel_llave_firma;
        fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;
        fel.fel_usuario_certificacion=suc.fel_usuario_certificacion;
        fel.fel_nit=suc.nit;
        fel.fel_correo=suc.correo;
        fel.fraseIVA=suc.codigo_escenario_iva;
        fel.fraseISR=suc.codigo_escenario_isr;

        printotrodoc = new Runnable() {
		    public void run() {
				askPrint();
		    }
		};
		
		printclose= new Runnable() {
		    public void run() {}
		};
		
		prn=new printer(this,printclose,gl.validimp);
        prn_nc=new printer(this,printclose,gl.validimp);

		setHandlers();

		setFechaAct();

		listItems();

		doc=new clsDocAnul(this,prn.prw,"");

		fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"");

        app.getURL();
        wsi=new wsInvActual(gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        recibeInventario = new Runnable() {
            public void run() {if (wsi.errflag) msgbox(wsi.error); }
        };

        if (gl.peInvCompart) {
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    wsi.actualizaInventario(recibeInventario);
                }
            };
            mtimer.postDelayed(mrunner,200);
        }
	}

	//region Events

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

				if(line.isEmpty()) {
					gl.timeout = 6000;
				}
				else {
					gl.timeout = Integer.valueOf(line);
				}

				myReader.close();
			}

		} catch (Exception e) {}

		if (gl.wsurl.isEmpty()) toast("Falta archivo con URL");
	}

	public void anulDoc(View view){
		try{
			if (itemid.equalsIgnoreCase("*")) {
				mu.msgbox("Debe seleccionar un documento.");return;
			}

			msgAsk("Anular documento");
		}catch (Exception e){
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

						sitem=vItem;
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
						sitem=vItem;

						anulDoc(view);
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

	//endregion

	//region Main

	public void listItems() {

		Cursor DT;
		clsClasses.clsCFDV vItem;	
		int vP,f;
		double val;
		String id,sf,sval;
			
		items.clear();
		selidx=-1;vP=0;
		
		try {

			//#CKFK 20200530 Quité la reimpresión de 1-recibos, 0-pedidos y 6-notas de crédito
			
			if (tipo==2) {

				sql="SELECT D_DEPOS.COREL,P_BANCO.NOMBRE,D_DEPOS.FECHA,D_DEPOS.TOTAL,D_DEPOS.CUENTA "+
					 "FROM D_DEPOS INNER JOIN P_BANCO ON D_DEPOS.BANCO=P_BANCO.CODIGO_BANCO "+
					 "WHERE (D_DEPOS.ANULADO=0)  AND (D_DEPOS.CODIGOLIQUIDACION=0) " +
					 "AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
					 "ORDER BY D_DEPOS.COREL DESC ";
			}
			
			if (tipo==3) {

				sql="SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO, "+
					"D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR "+
					"FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE "+
					"WHERE (D_FACTURA.ANULADO=0) AND (D_FACTURA.KILOMETRAJE=0)  " +
					"AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
					"ORDER BY D_FACTURA.COREL DESC ";
			}
			
			if (tipo==4) {
				sql="SELECT COREL,REFERENCIA,FECHA,0 "+
					"FROM D_MOV WHERE (TIPO='R') AND (ANULADO=0) AND (CODIGOLIQUIDACION=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
					"ORDER BY FECHA DESC ";
			}
			
			if (tipo==5) {
				sql="SELECT COREL,REFERENCIA,FECHA,0 "+
					"FROM D_MOV WHERE (TIPO='D') AND (ANULADO='N') AND (CODIGOLIQUIDACION=0)  AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
					"ORDER BY FECHA DESC ";
			}

			//#CKFK 20200520 Quité la anulación de NC porque aquí no existe la tabla
			    		
			DT=Con.OpenDT(sql);
			
			if (DT.getCount()>0) {
			
				DT.moveToFirst();

				while (!DT.isAfterLast()) {
				  
					id=DT.getString(0);
					
					vItem =clsCls.new clsCFDV();
			  	
					vItem.Cod=DT.getString(0);
					vItem.Desc=DT.getString(1);
					if (tipo==2) vItem.Desc+=" - "+DT.getString(4);
					
					if (tipo==3) {
						//sf=DT.getString(2) + " - " + Integer.toString(DT.getInt(4));
						//#CKFK 20200617 Modifique el formato en el que muestra el documento
						sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;
					}else if(tipo==1||tipo==6){
						sf=DT.getString(0);
					}else{
						f=DT.getInt(2);sf=du.sfecha(f)+" "+du.shora(f);
					}
					
					vItem.Fecha=sf;
					val=DT.getDouble(3);
					try {
						sval=mu.frmcur(val);
					} catch (Exception e) {
						sval=""+val;
					}					
					
					vItem.Valor=sval;	  
					
					if (tipo==4 || tipo==5) vItem.Valor="";
					
					items.add(vItem);	
			 
					if (id.equalsIgnoreCase(selid)) selidx=vP;
					vP+=1;

					if (tipo==3) {
						vItem.UUID=DT.getString(5);
						vItem.FechaFactura=du.univfechalong(DT.getLong(6));
					}else{
						vItem.UUID="";
						vItem.FechaFactura="";
					}

					DT.moveToNext();
				}	
			}

            if (DT!=null) DT.close();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		   	mu.msgbox(e.getMessage());
	    }
			 
		adapter=new ListAdaptCFDV(this, items);
		listView.setAdapter(adapter);
		
		if (selidx>-1) {
			adapter.setSelectedIndex(selidx);
			listView.setSelection(selidx);
		}
	    
		listView.setVisibility(View.VISIBLE);
	}
	
	private void anulDocument() {
		
		try {
			
			db.beginTransaction();
			
			if (tipo==0) anulPedido(itemid);
			
			if (tipo==1) anulRecib(itemid);
			
			if (tipo==2) {
				anulDepos(itemid);
			}
			
			if (tipo==3) {
				//if (checkFactDepos()) return;
                String idfel=gl.peFEL;
                if (idfel.isEmpty() | idfel.equalsIgnoreCase("SIN FEL")) {
                    anulFactura(itemid);
                } else {
                    anulacionFEL();
                    return;
                }
			}
			
			if (tipo==4) anulRecarga(itemid);
			
			if (tipo==5) if (!anulDevol(itemid)) return;

			//#CKFK 20200520 Quité la anulación de NC porque aquí no existe la tabla
			db.setTransactionSuccessful();
			db.endTransaction();
			
			mu.msgbox("Documento anulado.");

			/*
			if(tipo==3) {

				clsDocFactura fdoc;

				fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp, "");
				fdoc.deviceid =gl.deviceId;
				fdoc.buildPrint(itemid, 3, "TOL");

				String corelNotaCred=tieneNotaCredFactura(itemid);

				if (!corelNotaCred.isEmpty()){
					prn.printask(printotrodoc);
				}else {
					prn.printask(printclose);
				}

			} else if (tipo==6){

				clsDocDevolucion fdev;

				fdev=new clsDocDevolucion(this,prn_nc.prw,gl.peMon,gl.peDecImp, "printnc.txt");
				fdev.deviceid =gl.deviceId;

				fdev.buildPrint(itemid, 3, "TOL");

				String corelFactura=tieneFacturaNC(itemid);

				if (!corelFactura.isEmpty()){
					prn_nc.printask(printotrodoc, "printnc.txt");
				}else {
					prn_nc.printask(printclose, "printnc.txt");
				}

			}

			*/

			sql="DELETE FROM P_STOCK WHERE CANT=0 AND CANTM=0";
			db.execSQL(sql);

			listItems();
			
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			db.endTransaction();
		   	mu.msgbox(e.getMessage());
		}
	}

	//endregion

    //region FEL

    private void anulacionFEL() {
        if (buildAnulXML())  fel.anulacion(uuid);
    }

	@Override
    public void felCallBack()  {

        if (!fel.errorflag) {

			try {

				anulFactura(itemid);

				sql="DELETE FROM P_STOCK WHERE CANT=0 AND CANTM=0";
				db.execSQL(sql);

				//#EJC20200706: Commit transaction from Anuldocument.
				db.setTransactionSuccessful();
				db.endTransaction();

				envioFactura();

				toast(String.format("Se anuló la factura %d correctamente",itemid));

				listItems();

			} catch (SQLException e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
				db.endTransaction();
				mu.msgbox(e.getMessage());
				e.printStackTrace();
			}

		} else {
            msgbox("Ocurrió un error en anulacion FEL :\n\n"+ fel.error);
        }
    }

    private boolean buildAnulXML() {

        try {

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            clsClasses.clsD_factura fact=clsCls.new clsD_factura();

            D_facturaObj.fill("WHERE Corel='"+itemid+"'");
            fact=D_facturaObj.first();

            uuid=fact.feeluuid;
            if (uuid.equalsIgnoreCase(" ")) {
                anulFactura(itemid);
                return false;
            }

			String NITReceptor = Get_NIT_Cliente(fact.cliente);

			if(NITReceptor.isEmpty()) NITReceptor="CF";

			//#EJC20200527: Quitar estos caracteres del NIT.
			NITReceptor = NITReceptor.replace("-","");
			NITReceptor = NITReceptor.replace(".","");

            fel.anulfact(uuid, fel.fel_nit,NITReceptor, fact.fechaentr, fact.fecha);

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

	//region WebService handler


	private void envioFactura() {

		Handler mtimer = new Handler();
		Runnable mrunner = new Runnable() {
			@Override
			public void run() {
				ws.callback = 1;
				ws.execute();
			}
		};
		mtimer.postDelayed(mrunner, 200);
	}

	public class WebServiceHandler extends com.dtsgt.classes.WebService {

		public WebServiceHandler(PBase Parent, String Url, int TimeOut) {
			super(Parent, Url, TimeOut);
		}

		@Override
		public void wsExecute() {
			try {
				switch (ws.callback) {
					case 1:

						CSQL="UPDATE D_FACTURA SET ANULADO = 1 WHERE COREL='"+itemid+"';";
						CSQL+="UPDATE D_FACTURAD SET ANULADO = 1 WHERE COREL='"+itemid+"';";
						CSQL+="UPDATE D_FACTURAP SET ANULADO = 1 WHERE COREL='"+itemid+"';";

						callMethod("Commit", "SQL", CSQL);
						break;
				}
			} catch (Exception e) {
				error = e.getMessage();errorflag=true;
			}
		}
	}

	@Override
	public void wsCallBack(Boolean throwing, String errmsg, int errlevel) {
		try {
			if (throwing) throw new Exception(errmsg);

			if (ws.errorflag) {
				processComplete();
				return;
			}

			switch (ws.callback) {
				case 1:
					statusFactura();
					processComplete();
					break;
			}

		} catch (Exception e) {
			msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
			processComplete();
		}
	}

	private void processComplete() {

		if (ws.errorflag) {
			toast("Error de envío");
		} else {
			toast("Envío completo");
		}
		finish();
	}

	private void statusFactura() {
		try {

			String rs =(String) xobj.getSingle("CommitResult",String.class);

			if (!rs.equalsIgnoreCase("#")) {

				sql="UPDATE D_Factura SET STATCOM='P' WHERE COREL='"+itemid+"'";
				db.execSQL(sql);

				factsend=false;
			} else {
				factsend=true;
			}

		} catch (Exception e) {
			msgbox(e.getMessage());
		}
	}

	//endregion

	//region Documents
	
	private void anulPedido(String itemid) {

		try{

			sql="UPDATE D_PEDIDO SET Anulado='S' WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			sql="UPDATE D_PEDIDOD SET Anulado='S' WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			//anulBonif(itemid);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

	}	
	
	private boolean anulFactura(String itemid) {

		Cursor DT;
		String um;
		int prcant,prod;

		boolean vAnulFactura=false;

		try{

			sql="SELECT PRODUCTO,UMSTOCK,CANT FROM D_FACTURAD WHERE Corel='"+itemid+"'";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0) {
				DT.moveToFirst();
				while (!DT.isAfterLast()) {
					prod=DT.getInt(0);
					um=DT.getString(1);
                    prcant=DT.getInt(2);
					if (valexist2(prod)) revertProd(prod,um,prcant);

					DT.moveToNext();
				}
			}

            sql="SELECT PRODUCTO,UMSTOCK,CANT FROM D_FACTURAS WHERE Corel='"+itemid+"'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();

                while (!DT.isAfterLast()) {
                    prod=DT.getInt(0);
                    um=DT.getString(1);
                    prcant=DT.getInt(2);

                    if (app.prodTipo(prod).equalsIgnoreCase("P")) {
                        revertProd(prod,um,prcant);
                    }

                    DT.moveToNext();
                }
            }

			sql="UPDATE D_FACTURA  SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			sql="UPDATE D_FACTURAD SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			sql="UPDATE D_FACTURAP SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			//#CKFK 20200526 Puse esto en comentario porque esa tabla no se usa en MPos
			//anulBonif(itemid);

            sql="DELETE FROM P_STOCK WHERE CANT=0 AND CANTM=0";
            db.execSQL(sql);

            listItems();

            toast(String.format("Se anuló la factura %d correctamente",itemid));

			vAnulFactura=true;

            if (DT!=null) DT.close();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			vAnulFactura=false;
		}

		return vAnulFactura;

	}
	
	private void anulBonif(String itemid) {
		Cursor DT;
		String prod,um;

		try{

			sql = "UPDATE D_BONIF SET Anulado=1 WHERE COREL='" + itemid + "'";
			db.execSQL(sql);

			//sql = "UPDATE D_BONIFFALT SET Anulado='S' WHERE COREL='" + itemid + "'";
			//db.execSQL(sql);

			//sql="DELETE FROM D_REL_PROD_BON WHERE COREL='"+itemid+"'";
			//db.execSQL(sql);

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
		}
	}
	
	private void revertStock(String corel,String pcod,String um) {
		Cursor dt;
		String doc,stat,lot;
		double cant,ppeso;
		
		doc="";stat="";lot="";

		try{
			sql = "SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV FROM D_FACTURA_STOCK " +
					"WHERE (COREL='" + corel + "') AND (CODIGO='" + pcod + "') AND (UNIDADMEDIDA='" + um + "')";
			dt = Con.OpenDT(sql);

			if (dt.getCount()==0) return;

			dt.moveToFirst();

			while (!dt.isAfterLast()) {

				cant = dt.getInt(0);
				ppeso = dt.getDouble(2);
				lot = dt.getString(4);
				doc = dt.getString(5);
				stat = dt.getString(9);

				try {

                    ins.init("P_STOCK");

					ins.add("CODIGO", pcod);
					ins.add("CANT", 0);
					ins.add("CANTM", dt.getDouble(1));
					ins.add("PESO", 0);
					ins.add("plibra", dt.getDouble(3));
					ins.add("LOTE", lot);
					ins.add("DOCUMENTO", doc);

					ins.add("FECHA", dt.getInt(6));
					ins.add("ANULADO", dt.getInt(7));
					ins.add("CENTRO", dt.getString(8));
					ins.add("STATUS", stat);
					ins.add("ENVIADO", dt.getInt(10));
					ins.add("CODIGOLIQUIDACION", dt.getInt(11));
					ins.add("COREL_D_MOV", dt.getString(12));
					ins.add("UNIDADMEDIDA", um);

					db.execSQL(ins.sql());

				} catch (Exception e) {
					//#CKFK 20190308 Este addlog lo quité porque da error porque el registro ya existe y en ese caso solo se va a hacer el update.
					//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
					//mu.msgbox(e.getMessage());
				}

				sql = "UPDATE P_STOCK SET CANT=CANT+"+cant+",PESO=PESO+"+ppeso+"  WHERE (CODIGO='" + pcod + "') AND (UNIDADMEDIDA='" + um + "') ";
				db.execSQL(sql);

				dt.moveToNext();
			}

            if (dt!=null) dt.close();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

	}

    private void revertProd(int pcod,String um,int pcant) {

        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",""+pcod);
            ins.add("CANT",0);
            ins.add("CANTM",0);
            ins.add("PESO",0);
            ins.add("plibra",0);
            ins.add("LOTE","");
            ins.add("DOCUMENTO","");

            ins.add("FECHA",0);
            ins.add("ANULADO",0);
            ins.add("CENTRO","");
            ins.add("STATUS","");
            ins.add("ENVIADO",1);
            ins.add("CODIGOLIQUIDACION",0);
            ins.add("COREL_D_MOV", "");
            ins.add("UNIDADMEDIDA", um);

            db.execSQL(ins.sql());

        } catch (Exception e){
            try {
                sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE (CODIGO='"+pcod+"') AND (UNIDADMEDIDA='"+um+"')";
                db.execSQL(sql);
            } catch (Exception ee){
                msgbox(ee.getMessage());
            }
        }

    }

	private void revertStockBonif(String corel,String pcod,String um) {
		Cursor dt;
		String doc,stat,lot;
		double cant,ppeso;

		doc="";stat="";lot="";

		try{
			sql = "SELECT CANT,CANTM,PESO,plibra,LOTE,DOCUMENTO,FECHA,ANULADO,CENTRO,STATUS,ENVIADO,CODIGOLIQUIDACION,COREL_D_MOV FROM D_BONIF_STOCK " +
					"WHERE (COREL='" + corel + "') AND (CODIGO='" + pcod + "') AND (UNIDADMEDIDA='" + um + "')";
			dt = Con.OpenDT(sql);
			if (dt.getCount()==0) return;

			dt.moveToFirst();

			while (!dt.isAfterLast()) {

				cant = dt.getInt(0);
				ppeso = dt.getDouble(2);
				lot = dt.getString(4);
				doc = dt.getString(5);
				stat = dt.getString(9);

				try {

					ins.init("P_STOCK");

					ins.add("CODIGO", pcod);
					ins.add("CANT", 0);
					ins.add("CANTM", dt.getDouble(1));
					ins.add("PESO", 0);
					ins.add("plibra", dt.getDouble(3));
					ins.add("LOTE", lot);
					ins.add("DOCUMENTO", doc);

					ins.add("FECHA", dt.getInt(6));
					ins.add("ANULADO", dt.getInt(7));
					ins.add("CENTRO", dt.getString(8));
					ins.add("STATUS", stat);
					ins.add("ENVIADO", dt.getInt(10));
					ins.add("CODIGOLIQUIDACION", dt.getInt(11));
					ins.add("COREL_D_MOV", dt.getString(12));
					ins.add("UNIDADMEDIDA", um);

					db.execSQL(ins.sql());

				} catch (Exception e) {
					//#CKFK 20190308 Este addlog lo quité porque da error porque el registro ya existe y en ese caso solo se va a hacer el update.
					//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
					//mu.msgbox(e.getMessage());
				}

				sql = "UPDATE P_STOCK SET CANT=CANT+"+cant+",PESO=PESO+"+ppeso+"  WHERE (CODIGO='" + pcod + "') AND (UNIDADMEDIDA='" + um + "') AND (LOTE='" + lot + "') AND (DOCUMENTO='" + doc + "') AND (STATUS='" + stat + "')";
				db.execSQL(sql);

				dt.moveToNext();
			}

            if (dt!=null) dt.close();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

	}

	private void anulDepos(String itemid) {
		Cursor DT;
		String tdoc;

		try{

			db.beginTransaction();

			if (gl.depparc){
				sql="UPDATE D_DEPOS SET Anulado=1 WHERE COREL='"+itemid+"'";
				db.execSQL(sql);
			}

			sql="SELECT DISTINCT DOCCOREL,TIPODOC FROM D_DEPOSD WHERE (COREL='"+itemid+"')";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				tdoc=DT.getString(1);

				if (tdoc.equalsIgnoreCase("F")) {
					sql="UPDATE D_FACTURA SET DEPOS=0 WHERE (COREL='"+DT.getString(0)+"')";
				} else {
					sql="UPDATE D_COBRO SET DEPOS='N' WHERE (COREL='"+DT.getString(0)+"')";
				}

				db.execSQL(sql);

				DT.moveToNext();
			}

			if (!gl.depparc){
				sql="DELETE FROM D_DEPOS WHERE COREL='"+itemid+"'";
				db.execSQL(sql);
				sql="DELETE FROM D_DEPOSD WHERE COREL='"+itemid+"'";
				db.execSQL(sql);
				sql="DELETE FROM D_DEPOSB WHERE COREL='"+itemid+"'";
				db.execSQL(sql);
			}

			sql="UPDATE FinDia SET val3 = 0, val4=0";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			db.endTransaction();

		}catch (Exception e){

			db.endTransaction();
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
	}
	
	private void anulDeposParc(String itemid) {
		try{
			sql="UPDATE D_DEPOS SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

	}	
	
	private void anulRecarga(String itemid) {
		Cursor DT;
		String prod;
		double cant,cantm;

		try{
			sql="UPDATE D_MOV SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			sql="SELECT PRODUCTO,CANT,CANTM FROM D_MOVD WHERE (COREL='"+itemid+"')";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				prod=DT.getString(0);
				cant=DT.getDouble(1);
				cantm=DT.getDouble(2);

				try {
					sql="UPDATE P_STOCK SET CANT=CANT-"+cant+", CANTM=CANTM-"+cantm+" WHERE CODIGO='"+prod+"'";
					db.execSQL(sql);
				} catch (Exception e) {
					addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
					mu.msgbox(e.getMessage()+"\n"+sql);
				}

				DT.moveToNext();
			}

            if (DT!=null) DT.close();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
	}

	private boolean anulDevol(String itemid) {
		Cursor DT;
		String prod;
		double cant,cantm;

		boolean vAnulDevol=false;

		try{

			db.beginTransaction();

			sql="UPDATE D_MOV SET Anulado=1 WHERE COREL='"+itemid+"'";
			db.execSQL(sql);

			sql="SELECT PRODUCTO,CANT,CANTM, UNIDADMEDIDA FROM D_MOVD WHERE (COREL='"+itemid+"')";
			DT=Con.OpenDT(sql);

			if(DT.getCount()>0){
				sql="INSERT INTO P_STOCK SELECT PRODUCTO, CANT, CANTM, PESO, 0, LOTE, '',0,'N', '','',0,0,'', UNIDADMEDIDA " +
						"FROM D_MOVD WHERE (COREL='"+itemid+"')";
				db.execSQL(sql);
			}

			sql="SELECT PRODUCTO,UNIDADMEDIDA FROM D_MOVDB WHERE (COREL='"+itemid+"')";
			DT=Con.OpenDT(sql);



			sql="UPDATE FinDia SET val5 = 0";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			db.endTransaction();

			vAnulDevol=true;

		}catch (Exception e){

			db.endTransaction();
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

		return vAnulDevol;

	}

	private void anulRecib(String itemid) {
		try{
			sql="UPDATE D_COBRO  SET Anulado='S' WHERE COREL='"+itemid+"'";
			db.execSQL(sql);
			sql="UPDATE D_COBROD SET Anulado='S' WHERE COREL='"+itemid+"'";
			db.execSQL(sql);
			sql="UPDATE D_COBROP SET Anulado='S' WHERE COREL='"+itemid+"'";
			db.execSQL(sql);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
		
	}

	private boolean anulNotaCredito(String itemid) {

		Cursor DT2;
		String vCorelFactura = "";
		String vCorelDevol="";
		boolean vAnulNotaCredito=false;
		String vCorelNotaC = itemid;

		try{

			sql = "SELECT FACTURA FROM D_NOTACRED WHERE COREL = '" + itemid + "'";
			DT2=Con.OpenDT(sql);

			if (DT2.getCount()>0){
				DT2.moveToFirst();
				vCorelFactura = DT2.getString(0);
			}

			DT2.close();

			itemid = vCorelFactura;//En la variable vCorelFactura se guarda el corel de la Factura si es una NC con venta y sino el corel de D_CXC

			if (ExisteFactura(itemid)){
				vAnulNotaCredito = (anulFactura(itemid)?true:false);
			}else{
				vCorelDevol = itemid;

				sql = "UPDATE D_CXC SET ANULADO='S' WHERE COREL='" + vCorelDevol + "' ";
				db.execSQL(sql);

				sql = "UPDATE D_NOTACRED SET ANULADO='S' WHERE COREL='" + vCorelNotaC + "'";
				db.execSQL(sql);

				vAnulNotaCredito=true;
			}

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			vAnulNotaCredito=false;
		}

		return vAnulNotaCredito;
	}

	private String tieneFacturaNC(String vCorel){

		Cursor DT;
		String vtieneFacturaNC= "";

		try{

			sql = "SELECT FACTURA FROM D_NOTACRED WHERE COREL = '" + vCorel + "' AND FACTURA IN (SELECT COREL FROM D_FACTURA)";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0){
				DT.moveToFirst();
				vtieneFacturaNC = DT.getString(0);
                if (DT!=null) DT.close();
			}

		}catch (Exception ex){
		    mu.msgbox("Ocurrió un error "+ex.getMessage());
		}

		return vtieneFacturaNC;
	}

	private String tieneNotaCredFactura(String vCorel){

	Cursor DT;
	String vtieneNotaCredFactura= "";

	try{

		sql = "SELECT COREL FROM D_NOTACRED WHERE FACTURA = '" + vCorel + "' AND FACTURA IN (SELECT COREL FROM D_FACTURA)";
		DT=Con.OpenDT(sql);

		if (DT.getCount()>0){
			DT.moveToFirst();
			vtieneNotaCredFactura = DT.getString(0);
            if (DT!=null) DT.close();
		}

	}catch (Exception ex){
		mu.msgbox("Ocurrió un error "+ex.getMessage());
	}

	return vtieneNotaCredFactura;
}

    private boolean ExisteFactura(String vCorel){

        Cursor DT;
        boolean vExisteFactura = false;

        try{

            sql = "SELECT COREL FROM D_FACTURA WHERE COREL = '" + vCorel + "'";
            DT=Con.OpenDT(sql);

            vExisteFactura = (DT.getCount()>0?true:false);
            if (DT!=null) DT.close();
        } catch (Exception ex){
        }

        return vExisteFactura;
    }

    //endregion
	
	//region Impresion
	
	private void ImpresionFactura() {
		try{
			if (fdoc.buildPrint(itemid,3,gl.peFormatoFactura)) prn.printask();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}
	
	private void Impresion() {
		try{
			if (doc.buildPrintSimple("0",0)) prn.printask();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void ImprimeNC_Fact(){

		try{

			if (tipo==3){

				clsDocDevolucion fdev;
				String corelNotaCred=tieneNotaCredFactura(itemid);

				if (!corelNotaCred.isEmpty()){

					fdev=new clsDocDevolucion(this,prn_nc.prw,gl.peMon,gl.peDecImp, "printnc.txt");
					fdev.deviceid =gl.deviceId;

					fdev.buildPrint(corelNotaCred, 3, "TOL"); prn_nc.printnoask(printclose, "printnc.txt");

				}
			}else if (tipo==6){

				String corelFactura=tieneFacturaNC(itemid);

				if (!corelFactura.isEmpty()){
					clsDocFactura fdoc;

					fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp, "");
					fdoc.deviceid =gl.deviceId;

					fdoc.buildPrint(corelFactura, 3, "TOL"); prn.printnoask(printclose,"print.txt");
				}

			}

		}catch(Exception ex){

		}
	}

	private void askPrint() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Road");
			dialog.setMessage("¿Impresión correcta?");

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (tipo==3 || tipo==6){
						ImprimeNC_Fact();
					}

				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			dialog.show();
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

	private class clsDocAnul extends clsDocument {

		public clsDocAnul(Context context, int printwidth, String archivo) {
			super(context, printwidth ,gl.peMon,gl.peDecImp, archivo);

			nombre="Existencias";
			numero="";
			serie="";
			ruta=gl.ruta;
			vendedor=gl.vendnom;
			cliente="";

		}

		protected boolean buildDetail() {


			return true;
		}

		protected boolean buildFooter() {

			try {
				rep.add("");rep.add("");
				rep.addc("ANULACION");
				rep.add("");
				rep.add("Ruta : "+ruta);
				rep.add("Vendedor : "+vendedor);
				rep.add("");

				rep.add("");

				//if (tipo==1) lblTipo.setText("Recibo");
				if (gl.tipo==2) {
					rep.add("Deposito");
					rep.add("Fecha : "+sitem.Fecha);
					rep.add("Cuenta : "+sitem.Desc);
					rep.add("Total : "+sitem.Valor);
				}
				if (gl.tipo==3) {
					rep.add("Factura");
					rep.add("Numero : "+sitem.Fecha);
					rep.add("Total : "+sitem.Valor);
					rep.add("Cliente : "+sitem.Desc);
				}
				if (gl.tipo==4) {
					rep.add("Recarga");
					rep.add("Fecha : "+sitem.Fecha);
				}
				//if (tipo==5) lblTipo.setText("Devoluci�n a bodega");

				rep.add("");
				rep.add("");
				rep.add("");
				rep.add("");

				return true;
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
				return false;
			}

		}
		
	}

	//endregion

	//region Fecha

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

					long fechaSel=du.cfechaSinHora(cyear, cmonth, cday)*10000;

					if (tipo==3){
						long fecha_menor=du.addDays(du.getActDate(),-gl.dias_anul);

						if (fechaSel<fecha_menor){
							msgbox("La fecha de anulación debe ser mayor a la seleccionada");
							return;
						}
					}

					//listar nuevamente los documentos
					listItems();
				}
			},anio, mes, dia);

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

	//endregion

	//region Aprofam

	private boolean aprLoadHeadData(String corel) {
		Cursor DT;
		int ff;
					
		try {
			sql="SELECT SERIE,CORELATIVO,RUTA,VENDEDOR,CLIENTE,TOTAL FROM D_NOTACRED WHERE COREL='"+corel+"'";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			pserie=DT.getString(0);
			pnumero=""+DT.getInt(1);
			pruta=DT.getString(2);
			
			pvend=DT.getString(3);
			pcli=DT.getString(4);		
			ptot=DT.getDouble(5);

			sql="SELECT RESOL,FECHARES,FECHAVIG,SERIE,CORELINI,CORELFIN FROM P_COREL";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			presol="Resolucion No. : "+DT.getString(0);
			ff=DT.getInt(1);presfecha="De Fecha : "+du.sfecha(ff);
			ff=DT.getInt(2);presvence="Resolucion vence : "+du.sfecha(ff);		
			presrango="Serie : "+DT.getString(3)+" del "+DT.getInt(4)+" al "+DT.getInt(5);

			pvendedor="";

			sql="SELECT NOMBRE,PERCEPCION,TIPO_CONTRIBUYENTE,DIRECCION FROM P_CLIENTE WHERE CODIGO='"+pcli+"'";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			pcliente=DT.getString(0);       		
			pclicod=pcli;
			pclidir=DT.getString(3);
			
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox(e.getMessage());
			pcliente=pcli;
			pvendedor=pvend;
	    }

		return true;
		
	}

	private boolean buildHeader(String corel,int reimpres) {

		lines.clear();

		try {	
			loadHeadLines();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox(e.getMessage());return false;
		}		

		saveHeadLines(reimpres);

		return true;
	}

	private void saveHeadLines(int reimpres) {
		String s;

		rep.empty();rep.empty();

		try{
			for (int i = 0; i <lines.size(); i++) {
				s=lines.get(i);
				s=encabezado(s);
				if (residx==1) {
					rep.add(presol);
					rep.add(presfecha);
					rep.add(presvence);
					rep.add(presrango);
					residx=0;
				}
				if (!s.equalsIgnoreCase("@@")) rep.add(s);
			}

			if (!mu.emptystr(pclicod)) rep.add(pclicod);
			if (!mu.emptystr(pclidir)) rep.add(pclidir);

			if (reimpres==3) rep.add("--------  A N U L A C I O N  --------");
			if (reimpres==1) rep.add("------  R E I M P R E S I O N  ------");
			if (reimpres==2) rep.add("-----  C O N T A B I L I D A D  -----");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private String encabezado(String l) {
		String s,lu;
		int idx;

		residx=0;

		//lu=l.toUpperCase().trim();
		lu=l.trim();
		try{
			if (lu.length()==1 && lu.equalsIgnoreCase("N")) {
				s="NOTA CREDITO";s=rep.ctrim(s);return s;
			}

			if (l.indexOf("dd-MM-yyyy")>=0) {
				s=du.sfecha(du.getActDateTime());
				l=l.replace("dd-MM-yyyy",s);return l;
			}

			if (l.indexOf("HH:mm:ss")>=0) {
				s=du.shora(du.getActDateTime());
				l=l.replace("HH:mm:ss",s);return l;
			}

			idx=lu.indexOf("SS");
			if (idx>=0) {
				if (mu.emptystr(pserie)) return "@@";
				if (mu.emptystr(pnumero)) return "@@";

				s=lu.substring(0,idx);
				s="Nota credito serie : ";
				s=s+pserie+" numero : "+pnumero;
				residx=1;
				return s;
			}

			idx=lu.indexOf("VV");
			if (idx>=0) {
				if (mu.emptystr(pvendedor)) return "@@";
				l=l.replace("VV",pvendedor);return l;
			}

			idx=lu.indexOf("RR");
			if (idx>=0) {
				if (mu.emptystr(pruta)) return "@@";
				l=l.replace("RR",pruta);return l;
			}

			idx=lu.indexOf("CC");
			if (idx>=0) {
				if (mu.emptystr(pcliente)) return "@@";
				l=l.replace("CC",pcliente);return l;
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
		return l;
	}
	
	private boolean loadHeadLines() {
		Cursor DT;	
		String s;
		
		try {
			sql="SELECT TEXTO FROM P_ENCABEZADO_REPORTESHH ORDER BY CODIGO";
			DT=Con.OpenDT(sql);
			if (DT.getCount()==0) return false;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {
				s=DT.getString(0);	
				lines.add(s);	
				DT.moveToNext();
			}

            if (DT!=null) DT.close();

			return true;
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox(e.getMessage());return false;
		}				
	}

	//endregion
	
	//region Aux
	
	private void msgAsk(String msg) {

		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("ROAD");
			dialog.setMessage("¿" + msg  + "?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					anulDocument();
				}
			});
			dialog.setNegativeButton("No", null);
			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
			
	}
	
	private boolean checkFactDepos() {
		Cursor dt;

		try {

			sql="SELECT D_DEPOSD.DOCCOREL,D_DEPOS.ANULADO "+
				"FROM D_DEPOS INNER JOIN D_DEPOSD ON D_DEPOS.COREL=D_DEPOSD.COREL " +
				"WHERE D_DEPOSD.DOCCOREL='"+itemid+"' AND D_DEPOS.ANULADO=0";
			dt=Con.OpenDT(sql);

			if (dt.getCount()==0) return false;
			msgbox("La factura está depositada, no se puede anular");
			
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		return true;
	}
	
	private boolean valexist(String prcodd) {
		Cursor DT;
		
		try {
			//#CKFK20200524_FIX_BY_OPENDT Cambié el campo TIPO por CODIGO_TIPO
			sql="SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO='"+prcodd+"'";
           	DT=Con.OpenDT(sql);
           	if (DT.getCount()==0) return false;

           	DT.moveToFirst();
			
           	return DT.getString(0).equalsIgnoreCase("P");
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return false;
	    }
	}

    private boolean valexist2(int prcodd) {
        Cursor DT;

        try {
            sql="SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO_PRODUCTO="+prcodd;
            DT=Con.OpenDT(sql);
            if (DT.getCount()==0) return false;

            DT.moveToFirst();

            return DT.getString(0).equalsIgnoreCase("P");
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }


    //endregion


}
