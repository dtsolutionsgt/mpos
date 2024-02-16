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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.XMLObject;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_factura_anulacionObj;
import com.dtsgt.classes.clsD_facturafObj;
import com.dtsgt.classes.clsD_facturarObj;
import com.dtsgt.classes.clsD_facturasObj;
import com.dtsgt.classes.clsD_fel_errorObj;
import com.dtsgt.classes.clsDocDevolucion;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.fel.clsFELInFile;
import com.dtsgt.felesa.clsAnulESA;
import com.dtsgt.felesa.clsFELClases;
import com.dtsgt.felesa.clsFactESA;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.ListAdaptCFDV;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInventCompartido;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class Anulacion extends PBase {

	private ListView listView;
	private TextView lblTipo, lblRegs, lblTotal;
	private CheckBox cbcer;
    private ProgressBar pbar;
	
	private ArrayList<clsClasses.clsCFDV> items= new ArrayList<>();
	private ListAdaptCFDV adapter;
	private clsClasses.clsCFDV selitem;

    private wsInventCompartido wsi;
    private Runnable recibeInventario;

	private fbStock fbs;

	private Runnable printotrodoc,printclose;
	private printer prn;
    private printer prn_nc;
    private clsRepBuilder rep;
	private clsDocAnul doc;
	private clsDocFactura fdoc;

	private clsClasses.clsCFDV sitem;
	private AppMethods app;

    private clsFELInFile fel;
	private Anulacion.WebServiceHandler ws;
	private XMLObject xobj;

	private clsAnulESA AnulESA;
	private clsClasses.clsD_factura_anulacion factanul;

	private String CSQL;
	private boolean factsend,bloqueado=false;

	private int tipo,depparc,fcorel,fTotAnul;
	private String selid,itemid,fserie,fres,felcorel,uuid;
	private boolean modoapr=false,demomode,modo_sv,modo_gt;

	// impresion nota credito
	
	private ArrayList<String> lines= new ArrayList<String>();
	private String pserie,pnumero,pruta,pvend,pcli,presol,presfecha,pfser,pfcor;
	private String presvence,presrango,pvendedor,pcliente,pclicod,pclidir;
	private double ptot;
	private int residx,idcliente;

	//Fecha
	private boolean dateTxt,report;
	private TextView lblDateini,lblDatefin;
	public final Calendar c = Calendar.getInstance();
	private static final String BARRA = "/";
	final int mes = c.get(Calendar.MONTH);
	final int dia = c.get(Calendar.DAY_OF_MONTH);
	final int anio = c.get(Calendar.YEAR);
	public int cyear, cmonth, cday, validCB=0;
	private long datefin,dateini,fecha_menor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anulacion);
		
		super.InitBase();

		listView = findViewById(R.id.listView1);
		lblTipo= findViewById(R.id.lblDescrip);
		lblDateini = findViewById(R.id.lblDateini2);
		lblDatefin = findViewById(R.id.lblDatefin2);
		lblRegs = findViewById(R.id.lblRegs);
		lblTotal = findViewById(R.id.lblTotal);
		cbcer = findViewById(R.id.checkBox27);cbcer.setVisibility(View.INVISIBLE);

        pbar=findViewById(R.id.progressBar7);pbar.setVisibility(View.INVISIBLE);

		app = new AppMethods(this, gl, Con, db);
		gl.validimp=app.validaImpresora();
		if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

		tipo=gl.tipo;
		if (gl.peModal.equalsIgnoreCase("APR")) modoapr=true;

		if (gl.dias_anul<5) gl.dias_anul=5;
		fecha_menor=du.addDays(du.getActDate(),-gl.dias_anul);
		fecha_menor=du.ffecha00(fecha_menor);

		if (tipo==0) lblTipo.setText("Pedido");
		if (tipo==1) lblTipo.setText("Recibo");
		if (tipo==2) lblTipo.setText("Depósito");
		if (tipo==3) lblTipo.setText((gl.peMFact?"Factura":"Ticket"));
		if (tipo==4) lblTipo.setText("Recarga");
		if (tipo==5) lblTipo.setText("Devolución a bodega");

		if (tipo==3) cbcer.setVisibility(View.VISIBLE);

		itemid="*";

        getURL();

		fbs =new fbStock("Stock",gl.tienda);

		wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,3,db,Con);
		xobj = new XMLObject(ws);

        clsP_sucursalObj sucursal=new clsP_sucursalObj(this,Con,db);
        sucursal.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
        clsClasses.clsP_sucursal suc=sucursal.first();

		fel=new clsFELInFile(this,this,gl.timeout);

		if (gl.peFEL.equalsIgnoreCase(gl.felInfile)) {

			fel.fel_llave_certificacion =suc.fel_llave_certificacion;
			fel.fel_llave_firma=suc.fel_llave_firma;
			fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;
			fel.fel_usuario_certificacion=suc.fel_usuario_certificacion;
			fel.fel_nit=suc.nit;
			fel.fel_nombre_comercial=suc.texto;
			fel.fel_correo=suc.correo;
			fel.fraseIVA=suc.codigo_escenario_iva;
			fel.fraseISR=suc.codigo_escenario_isr;

		} else if (gl.peFEL.equalsIgnoreCase(gl.felSal)) {

			fel=new clsFELInFile(this,this,gl.timeout);

			fel.fel_llave_certificacion =suc.fel_llave_firma;
			fel.fel_llave_firma=suc.fel_llave_firma;
			fel.fel_usuario_certificacion=suc.fel_usuario_firma;
			fel.fel_codigo_establecimiento=suc.fel_codigo_establecimiento;
			fel.fel_nit=suc.nit;
			fel.fel_nombre_comercial=suc.texto;
			fel.fel_correo=suc.correo;

			//fel.fel_codigo_establecimiento="0001";
			//fel.fel_usuario_certificacion="06141106141147";
			//fel.fel_llave_certificacion="df3b5497c338a7e78d659a468e72a670";

			AnulESA=new clsAnulESA(this,fel.fel_usuario_certificacion,fel.fel_llave_certificacion);

		} else {
			fel=new clsFELInFile(this,this,gl.timeout);
		}

        printotrodoc = () -> askPrint();
		
		printclose= () -> {};
		
		prn=new printer(this,printclose,gl.validimp);
        prn_nc=new printer(this,printclose,gl.validimp);

		setHandlers();

		setFechaAct();

		modo_sv=false;modo_gt=false;
		if (gl.codigo_pais.equalsIgnoreCase("GT")) modo_gt=true;
		if (gl.codigo_pais.equalsIgnoreCase("SV")) modo_sv=true;

		listItems();

		doc=new clsDocAnul(this,prn.prw,"");

		fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"",gl.peComboDet);

        app.getURL();

        wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        recibeInventario = () -> {
			bloqueado=false;
			if (wsi.errflag) msgbox(wsi.error); else confirmaInventario();
			pbar.setVisibility(View.INVISIBLE);
		};

        bloqueado=false;
        if (gl.peInvCompart) {
            pbar.setVisibility(View.VISIBLE);
            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
				bloqueado=true;
				wsi.execute(recibeInventario);
			};
            mtimer.postDelayed(mrunner,200);
        }
	}

    private void confirmaInventario() {
        try {
            Intent intent = new Intent(Anulacion.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);
        } catch (Exception e) {}
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

	public void anulDoc(View view) {
		long fa,ff,ma,mf;

        if (bloqueado) {
            toast("Actualizando inventario . . .");return;
        }

		fa=du.getActDate();ma=du.getmonth(fa);

        try {
			if (itemid.equalsIgnoreCase("*")) {
				mu.msgbox("Debe seleccionar un documento.");return;
			}

			//boolean flag=gl.peAnulSuper;
			//if ((gl.rol==2 || gl.rol==3)) flag=false;

			if (gl.codigo_pais.equalsIgnoreCase("GT")) {
				if (tipo==3) {
					clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
					D_facturaObj.fill("WHERE COREL='"+itemid+"'");
					ff=D_facturaObj.first().fecha;mf=du.getmonth(ff);
					if (ma!=mf) {
						msgAnul("La factura se puede anular únicamente en el transcurso de mes de la emisión");return;
					}
				}
			} else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
				if (tipo==3) {
					clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
					D_facturaObj.fill("WHERE COREL='"+itemid+"'");
					ff=D_facturaObj.first().fecha;mf=du.getmonth(ff);
					if (ma!=mf) {
						msgAnul("La factura se puede anular únicamente en el transcurso de mes de la emisión");return;
					}
				}
			} else if (gl.codigo_pais.equalsIgnoreCase("SV")) {

			}

			msgAsk("Anular documento");

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

			listView.setOnItemClickListener((parent, view, position, id) -> {

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
			});

			listView.setOnItemLongClickListener((parent, view, position, id) -> {

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
			});

			cbcer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					listItems();
				}
			});

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	//endregion

	//region Main

	public void listItems() {
		Cursor DT;
		clsClasses.clsCFDV vItem;	
		int vP,f,regs=0;
		double val, total=0;
		String id,sf,sval,td;
		long dfi,dff,ff,fsvf,fsvc;
		boolean guardar;

		items.clear();
		selidx=-1;vP=0;
		
		try {

			ff=du.getActDate();fsvf=du.addDays(ff,-29);fsvc=du.addDays(ff,-1);

			if (tipo==2) {

				sql="SELECT D_DEPOS.COREL,P_BANCO.NOMBRE,D_DEPOS.FECHA,D_DEPOS.TOTAL,D_DEPOS.CUENTA "+
					 "FROM D_DEPOS INNER JOIN P_BANCO ON D_DEPOS.BANCO=P_BANCO.CODIGO_BANCO "+
					 "WHERE (D_DEPOS.ANULADO=0)  AND (D_DEPOS.CODIGOLIQUIDACION=0) " +
					 "AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
					 "ORDER BY D_DEPOS.COREL DESC ";
			}
			
			if (tipo==3) {

				if (cbcer.isChecked()) {
					sql="SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO, "+
							"D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR, D_FACTURA.AYUDANTE "+
							"FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE "+
							"WHERE (D_FACTURA.FEELUUID=' ')  ORDER BY D_FACTURA.FECHAENTR DESC ";
				} else {
					dfi=dateini;if (dfi<fecha_menor) dfi=fecha_menor;
					dff=datefin;if (dff<fecha_menor) dff=fecha_menor;

					sql="SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO, "+
							"D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR, D_FACTURA.AYUDANTE "+
							"FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE "+
							"WHERE (D_FACTURA.ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
							"ORDER BY D_FACTURA.FECHAENTR  DESC";
				}
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
				  
					id=DT.getString(0);guardar=true;
					
					vItem =clsCls.new clsCFDV();
			  	
					vItem.Cod=DT.getString(0);vItem.tipodoc="";
					if (tipo==3 && modo_sv) {
						td=DT.getString(7);vItem.tipodoc="F";
						if (td.equalsIgnoreCase("T")) vItem.tipodoc="T";
						if (td.equalsIgnoreCase("C")) vItem.tipodoc="C";
					}

					vItem.Desc=DT.getString(1);
					if (tipo==2) vItem.Desc+=" - "+DT.getString(4);
					
					if (tipo==3) {
						sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;
					} else if(tipo==1||tipo==6){
						sf=DT.getString(0);
					} else{
						f=DT.getInt(2);sf=du.sfecha(f)+" "+du.shora(f);
					}
					
					vItem.Fecha=sf;
					val=DT.getDouble(3);
					total += val;
					try {
						sval=mu.frmcur(val);
					} catch (Exception e) {
						sval=""+val;
					}					
					
					vItem.Valor=sval;	  

					if (tipo==4 || tipo==5) vItem.Valor="";

					if (modo_sv) {
						ff=DT.getLong(6);guardar=true;
						if (vItem.tipodoc.equalsIgnoreCase("C")) {
							if (ff<fsvc) guardar=false;
						} else {
							if (ff<fsvf) guardar=false;
			    		}
					}

					if (guardar) {
						items.add(vItem);

						if (id.equalsIgnoreCase(selid)) selidx=vP;vP+=1;

						if (tipo==3) {
							vItem.UUID=DT.getString(5);
							ff=DT.getLong(6);
							vItem.FechaFactura=du.sfecha(ff)+" "+du.shora(ff);
						} else {
							vItem.UUID="";
							vItem.FechaFactura="";
						}
					}

					DT.moveToNext();
				}

				regs=items.size();
			}

			if (tipo == 3) {
				lblRegs.setVisibility(View.VISIBLE);
				lblTotal.setVisibility(View.VISIBLE);
				lblRegs.setText("Registros: " + regs);
				lblTotal.setText("Total: " + mu.frmcur(total));
			}

            if (DT!=null) DT.close();

		} catch (Exception e) {
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
                //#JP20201008 - llena variable uuid antes de validar
                clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
                D_facturaObj.fill("WHERE COREL='"+itemid+"'");
                uuid=D_facturaObj.first().feeluuid;
				idcliente=D_facturaObj.first().cliente;

                String idfel=gl.peFEL;
                if (idfel.isEmpty() || idfel.equalsIgnoreCase("SIN FEL")) {
                    anulFactura(itemid);
                } else {
		          	//#EJC20200712: Si la factura fue generada en contingencia no anular en FEL.
                	if (uuid!=null) {
						//msgAskFacturaSAT("La factura está anulada en portalSAT");
						if (idfel.equalsIgnoreCase(gl.felInfile)) {
							anulacionFEL();
						} else if (idfel.equalsIgnoreCase(gl.felSal)) {
							anulacionFELSal();
						}
					} else {
						anulFactura(itemid);
					}
                }
			}
			
			if (tipo==4) anulRecarga(itemid);
			
			if (tipo==5) if (!anulDevol(itemid)) return;

			if (fel.errorflag) {
				throw new Exception(fel.error);
			}

			//#CKFK 20200520 Quité la anulación de NC porque aquí no existe la tabla
			db.setTransactionSuccessful();
			db.endTransaction();
			
			if (tipo!=3) mu.msgbox("Documento anulado.");

			listItems();

            if (gl.peInvCompart) {
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            }
			
		} catch (Exception e) {
			db.endTransaction();
		   	mu.msgbox(e.getMessage());
		}
	}

	private void msgAskFacturaSAT(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Anular factura");
		dialog.setMessage("¿" + msg + "?");
		dialog.setPositiveButton("Si", (dialog1, which) -> anulFactura(itemid));
		dialog.setNegativeButton("No", (dialog12, which) -> anulacionFEL());
		dialog.setNeutralButton("Salir", (dialog13, which) -> {});
		dialog.show();
	}

	@Override
	public void wsCallBack(Boolean throwing, String errmsg, int errlevel) {
		try {

			String ss=errmsg+" "+errlevel;
			ss=ss+"";

			if (throwing | ws.errorflag) {
				guardaError();
			}

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

	@Override
	public void felCallBack()  {
		if (gl.peFEL.equalsIgnoreCase(gl.felInfile)) {
			felCallBackInfile();
		} else if (gl.peFEL.equalsIgnoreCase(gl.felSal)) {
			felCallBackInfileSal();
		}
	}
	//endregion

    //region FEL GT

    private void anulacionFEL() {
		try {
			if (buildAnulXML()) {
				fel.anulacion(uuid);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void felCallBackInfile() {
		String ss=fel.error+" "+fel.errlevel;
		ss=ss+"";

		if (!fel.errorflag) {
			try {
				anulFactura(itemid);

				db.setTransactionSuccessful();
				db.endTransaction();

				envioFactura();

				msgbox(String.format("Se anuló la factura %d correctamente",itemid));

				listItems();
			} catch (SQLException e) {
				db.endTransaction();
				mu.msgbox(e.getMessage());
			}
		} else {
			try {
				guardaError();
				db.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
            if (uuid.length()<5) {
	           anulFactura(itemid);return false;
            }

			if (uuid.equalsIgnoreCase(" ")) {
				anulFactura(itemid);return false;
			}

			if (uuid.isEmpty()) {
				anulFactura(itemid);return false;
			}

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

	//region FEL SV

	private void anulacionFELSal() {
		String tipodoc;
		boolean nitvalido;

		try {
			clsFELClases fclas=new clsFELClases();
			clsFELClases.anulacionDatos ad=fclas.new anulacionDatos();

			ad.uuid=uuid;
			ad.establecimiento=fel.fel_codigo_establecimiento;

			ad.solicitante_nom=fel.fel_nombre_comercial;
			ad.solicitante_nit=fel.fel_nit;
			ad.solicitante_correo=fel.fel_correo;

			ad.responsable_nom=fel.fel_nombre_comercial;
			ad.responsable_nit=fel.fel_nit;
			ad.responsable_correo="";

			clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
			clsD_facturafObj D_facturafObj=new clsD_facturafObj(this,Con,db);

			D_facturaObj.fill("WHERE Corel='"+itemid+"'");

			tipodoc=D_facturaObj.first().ayudante;

			if (tipodoc.equalsIgnoreCase("N")) {
				try {
					D_facturafObj.fill("WHERE Corel='"+itemid+"'");
					ad.responsable_nom=D_facturafObj.first().nombre;
					if (!D_facturafObj.first().correo.isEmpty()) ad.responsable_correo=D_facturafObj.first().correo;
				} catch (Exception e) {
					msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
				}
			} else if (tipodoc.equalsIgnoreCase("C")) {
				try {
					D_facturafObj.fill("WHERE Corel='"+itemid+"'");
					ad.responsable_nom=D_facturafObj.first().nombre;
					if (!D_facturafObj.first().correo.isEmpty()) ad.responsable_correo=D_facturafObj.first().correo;
				} catch (Exception e) {
					msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
				}
			} else if (tipodoc.equalsIgnoreCase("T")) {
				ad.responsable_nom=fel.fel_nombre_comercial;
				ad.responsable_nit=fel.fel_nit;
			}

			clsFELClases.JSONAnulacion janul=fclas.new JSONAnulacion();
			janul.Anulacion(ad);

			AnulESA.Anular(janul.json);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void felCallBackInfileSal() {

		if (!AnulESA.errorflag) {

			if (AnulESA.anulresult<0) {
				msgbox(AnulESA.mensaje);return;
			}

			try {

				try {
   					clsD_factura_anulacionObj D_factura_anulacionObj=new clsD_factura_anulacionObj(this,Con,db);

					factanul = clsCls.new clsD_factura_anulacion();

					int newid=D_factura_anulacionObj.newID("SELECT MAX(codigo_factura_anulacion) FROM D_factura_anulacion");

					factanul.codigo_factura_anulacion=newid;
					factanul.empresa=gl.emp;
					factanul.corel=itemid;
					factanul.codigo_pais=gl.codigo_pais;
					factanul.fecha_anulacion=du.getActDateTime();
					factanul.sv_uuid=uuid;
					factanul.sv_codigo_generacion=AnulESA.codigoGeneracion;
					factanul.sv_sello_recepcion=AnulESA.selloRecepcion;

					D_factura_anulacionObj.add(factanul);
				} catch (Exception e) {
					msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
				}

				anulFactura(itemid);

				db.setTransactionSuccessful();
				db.endTransaction();

				envioFactura();

				msgbox("El documento se anuló correctamente");

				listItems();

			} catch (SQLException e) {
				db.endTransaction();
				mu.msgbox(e.getMessage());
			}
		} else {
			try {
				guardaError();
        		db.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
			msgbox("Ocurrió un error en anulacion FEL :\n\n"+ AnulESA.error);
		}
	}

	//endregion

	//region WebService handler

	private void envioFactura() {

		Handler mtimer = new Handler();
		Runnable mrunner = () -> {
			ws.callback = 1;
			ws.execute();
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

	public void wsCallBackGT(Boolean throwing, String errmsg, int errlevel) {
		try {

			String ss=errmsg+" "+errlevel;
			ss=ss+"";

			if (throwing | ws.errorflag) {
				guardaError();
			}

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

	private void guardaError() {
		clsD_fel_errorObj D_fel_errorObj=new clsD_fel_errorObj(this,Con,db);
		clsClasses.clsD_fel_error item=clsCls.new clsD_fel_error();

		String err=fel.responsecode +" "+fel.error;
		String cor=itemid;
		int nivel=fel.errlevel; // nivel=3 - firma anulacion , 4 - anulacion

		try {
			int iditem=D_fel_errorObj.newID("SELECT MAX(Item) FROM D_fel_error");

			item.empresa=gl.emp;
			item.corel=cor;
			item.item=iditem;
			item.fecha=du.getActDateTime();
			item.nivel=nivel;
			item.error=err;
			item.enviado=0;

			D_fel_errorObj.add(item);
		} catch (Exception e) {
			msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

        clsD_facturarObj D_facturarObj=new clsD_facturarObj(this,Con,db);
        clsD_facturasObj D_facturasObj=new clsD_facturasObj(this,Con,db);

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

            // Inventario recetas

            D_facturarObj.fill("WHERE COREL='"+itemid+"'");
            for (int i = 0; i <D_facturarObj.count; i++) {
                revertProd(D_facturarObj.items.get(i).producto,
                           D_facturarObj.items.get(i).um,
                           D_facturarObj.items.get(i).cant);
            }

            listItems();

			msgbox("El documento se anuló correctamente");

			vAnulFactura=true;

			enviaAnulaciones();

            if (DT!=null) DT.close();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+"\n"+e.getMessage()+"\n"+sql);
			vAnulFactura=false;
		}

		return vAnulFactura;

	}

	private void enviaAnulaciones() {
		try {
			CSQL="";
			processAnul();

			if (fTotAnul>0) {

				Intent intent = new Intent(Anulacion.this, srvCommit.class);
				intent.putExtra("URL", gl.wsurl);
				intent.putExtra("command", CSQL);
				startService(intent);
			}
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void processAnul() {
		String corr, ssql;

		try {
			clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);

			long fan = du.addDays(du.getActDate(), -5);
			D_facturaObj.fill("WHERE (ANULADO=1) AND (FECHA>" + fan + ") ");
			fTotAnul = D_facturaObj.count;

			for (int i = 0; i < D_facturaObj.count; i++) {

				corr = D_facturaObj.items.get(i).corel;
				ssql = "UPDATE D_FACTURA SET ANULADO=1 WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + corr + "')";

				CSQL = CSQL + ssql + ";";
			}
		} catch (Exception e) {
			fTotAnul=0;
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void revertProd(int pcod,String um,double pcant) {
		try {
			clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

			ritem.idprod=pcod;
			ritem.idalm=0;
			ritem.cant=pcant;
			ritem.um=um.trim();
			ritem.bandera=0;

			fbs.addItem("/"+gl.tienda+"/",ritem);
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void revertProd(int pcod,String um,int pcant) {
		revertProd(pcod,um,(double) pcant);
	}

	/*

    private void revertProd(int pcod,String um,int pcant) {

        try {

            ins.init("P_STOCK");
            ins.add("CODIGO",""+pcod);
            ins.add("CANT",pcant);
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

    private void revertProd(int pcod,String um,double pcant) {

        try {

            ins.init("P_STOCK");
            ins.add("CODIGO",""+pcod);
            ins.add("CANT",pcant);
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

	private void anulBonif(String itemid) {

		Cursor DT;
		String prod,um;

		try{

			sql = "UPDATE D_BONIF SET Anulado=1 WHERE COREL='" + itemid + "'";
			db.execSQL(sql);

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
	*/

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

            sql="DELETE FROM T_COSTO WHERE COREL='"+itemid+"'";
            db.execSQL(sql);

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

					fdoc=new clsDocFactura(this,prn.prw,gl.peMon,gl.peDecImp,"",gl.peComboDet);
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
			dialog.setTitle("mPos");
			dialog.setMessage("¿Impresión correcta?");
			dialog.setPositiveButton("Si", (dialog1, which) -> {
				if (tipo==3 || tipo==6){
					ImprimeNC_Fact();
				}
			});
			dialog.setNegativeButton("No", (dialog12, which) -> {
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
			nombre_cliente ="";

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
			listItems();
			dateTxt=false;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void showDateDialog2(View view) {
		try{
			obtenerFecha();
			listItems();
			dateTxt=true;
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void obtenerFecha(){

		try{

			DatePickerDialog recogerFecha = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

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

				if (dateTxt) {
					datefin = du.cfechaRep(cyear, cmonth, cday, false);
					if (modo_gt) {
						if (datefin < fecha_menor) datefin = fecha_menor;
					}
				}

				if (!dateTxt){
					dateini  = du.cfechaRep(cyear, cmonth, cday, true);
					if (modo_gt) {
						if (dateini < fecha_menor) dateini = fecha_menor;
					}
				}

				long fechaSel=du.cfechaSinHora(cyear, cmonth, cday)*10000;

				if (tipo==3){
					if (modo_gt) {
						if (fechaSel < fecha_menor) {
							if (gl.codigo_pais.equalsIgnoreCase("GT")) {
								msgbox("La fecha permitida de anulación es 5 días atras");
								fechaSel = fecha_menor;
								//return;
							}
						}
					}
				}

				//listar nuevamente los documentos
				listItems();

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
		} catch (Exception e){
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

			sql="SELECT RESOL,FECHARES,FECHAVIG,SERIE,CORELINI,CORELFIN FROM P_COREL WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)";
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
			dialog.setTitle("mPos");
			dialog.setMessage("¿" + msg  + "?");
			dialog.setIcon(R.drawable.ic_quest);
			dialog.setPositiveButton("Si", (dialog1, which) -> anulDocument());
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

	private void msgAnul(String msg) {
		try{

			ExDialog dialog = new ExDialog(this);
			dialog.setMessage(msg);
			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	//endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        if (browse==1) {
            browse=0;
            if (gl.checksuper) msgAsk("Anular documento");
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}