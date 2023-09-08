package com.dtsgt.mpos;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.StrictMode;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_cierreObj;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_cajahoraObj;
import com.dtsgt.classes.clsP_cortesiaObj;
import com.dtsgt.classes.clsP_modo_emergenciaObj;
import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_sucursalObj;;
import com.dtsgt.classes.clsP_vendedor_rolObj;
import com.dtsgt.classes.clsT_cierreObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.classes.extWaitDlg;
import com.dtsgt.fel.FELFactura;
import com.dtsgt.fel.FELVerificacion;
import com.dtsgt.ladapt.ListAdaptMenuGrid;
import com.dtsgt.mant.Lista;
import com.dtsgt.mant.MantConfig;
import com.dtsgt.mant.MantConfigRes;
import com.dtsgt.mant.MantCorel;
import com.dtsgt.mant.MantImpRedir;
import com.dtsgt.mant.MantRepCierre;
import com.dtsgt.mant.MantRol;
import com.dtsgt.webservice.wsCommit;
import com.dtsgt.webservice.wsOpenDT;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends PBase {

	private GridView gridView;
	private RelativeLayout relbotpan;
	private TextView lblVendedor,lblRuta,lblTit;
	private ImageView imgnowifi;
	
	private ArrayList<clsMenu> items= new ArrayList<clsMenu>();

	private wsOpenDT wsic;
	private wsCommit wscom;

	private Runnable rnInvCent,rnNumOrden;

	private ListAdaptMenuGrid adaptergrid;
	private ExDialog menudlg;
	private extWaitDlg waitdlg,waitdlglimp;

    private clsP_cajacierreObj caja;
    private clsP_modo_emergenciaObj P_modo_emergenciaObj;
    private clsP_paramextObj P_paramextObj;

    private int selId,selIdx,menuid,iicon,idalm,idalmdpred,idcierre,modo_invcent;
	private String rutatipo,sdoc;
	private boolean rutapos,horizpos,porcentaje,modo_emerg;
	private boolean listo=true,almacenes,cortesias;
	private long contcorini,contcorult,fhoy,fayer;

    private final int mRequestCode = 1001;
	private Exist Existencia = new Exist();

	@Override
	protected void onCreate(Bundle savedInstanceState) 	{

		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_menu);

			super.InitBase();

			gridView = findViewById(R.id.gridView1);
			relbotpan = findViewById(R.id.relbotpan);
			lblVendedor = findViewById(R.id.lblVendedor);
			lblRuta = findViewById(R.id.textView9);
            imgnowifi = findViewById(R.id.imageView122);
			lblTit = findViewById(R.id.lblTit);

            P_modo_emergenciaObj=new clsP_modo_emergenciaObj(this,Con,db);
            P_paramextObj=new clsP_paramextObj(this,Con,db);

            gl.validDate=false;
			gl.lastDate=0;
            gl.QRCodeStr="";
			gl.dev = false;
			vApp=this.getApplication();
			rutatipo=gl.rutatipog;
			gl.devfindia=false;

            rutapos=false;gl.rutapos=false;
			sdoc="Venta";iicon=102;
			rutapos=true;gl.rutapos=true;
			selId=-1;selIdx=-1;
			almacenes=tieneAlmacenes();

			setHandlers();

            if (pantallaHorizontal()) horizpos=true; else horizpos=false;

			if (horizpos) {
				gridView.setNumColumns(4);relbotpan.setVisibility(View.GONE);
			} else {
				gridView.setNumColumns(3);relbotpan.setVisibility(View.VISIBLE);
			}

			this.setTitle("mPos");
			lblTit.setText("mPos   -   Versión: "+gl.parVer+"   -   Caja: "+gl.rutanom+" [ "+gl.codigo_ruta+" ] ," +
					       " -  Sucursal: "+gl.tiendanom+" [ "+gl.tienda+" ]");

			listItems();

			cajaCerrada();

			gl.ingreso_mesero=false;
            //gl.ingreso_mesero=gl.rol==4;
            //if (gl.ingreso_mesero && gl.after_login) autoLoginMesero();

			app.getURL();
			wsic=new wsOpenDT(gl.wsurl);
			wscom =new wsCommit(gl.wsurl);

			rnInvCent= () -> {
				//callbackInvCent();
			};
			rnNumOrden= () -> {	callBackNumOrden();};

			waitdlg= new extWaitDlg();
			waitdlglimp= new extWaitDlg();

		} catch (Exception e) {
			msgbox(e.getMessage());
		}

        validaModo();

	}

	//region Events


    //endregion

	//region  Main

	public void listItems() {
        clsMenu item;
		boolean modosuper=false;

        lblVendedor.setText(gl.vendnom);
        lblRuta.setText(gl.ruta);

        items.clear();selIdx=-1;

        try {

			clsP_vendedor_rolObj P_vendedor_rolObj=new clsP_vendedor_rolObj(this,Con,db);
			P_vendedor_rolObj.fill("WHERE (CODIGO_SUCURSAL="+gl.tienda+") AND " +
				   	               "(CODIGO_VENDEDOR="+gl.codigo_vendedor+") AND " +
					               "((CODIGO_ROL=2) OR (CODIGO_ROL=3))");
			modosuper=P_vendedor_rolObj.count>0;

			modosuper=true;

			addMenuItem(1,"Venta");
			addMenuItem(6,"Caja");
			addMenuItem(3,"Reimpresión");
			addMenuItem(7,"Inventario");
			addMenuItem(2,"Comunicación");
			if (modosuper) addMenuItem(9,"Utilerias");
			if (modosuper) addMenuItem(11,"Mantenimientos");
			if (modosuper) addMenuItem(12,"Reportes");
			addMenuItem(4,"Anulación");
			addMenuItem(10,"Cambio usuario");
			addMenuItem(14,"Modo de emergencia");

			adaptergrid=new ListAdaptMenuGrid(this, items,horizpos);
            gridView.setAdapter(adaptergrid);
            adaptergrid.setSelectedIndex(selIdx);

			clsP_cortesiaObj P_cortesiaObj=new clsP_cortesiaObj(this,Con,db);
			P_cortesiaObj.fill();
			cortesias=P_cortesiaObj.count>0;

        } catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void apagar(){

		try {
			Intent i = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
			i.putExtra("android.intent.extra.KEY_CONFIRM", false);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			//Needs root
			//Process proc = Runtime.getRuntime() .exec(new String[]{ "su", "-c", "reboot -p" }); proc.waitFor();
		} catch (Exception ex) { ex.printStackTrace(); }
	}
		
	public void setHandlers(){

	    try{

			gridView.setOnItemClickListener((parent, view, position, id) -> {

				clsMenu vItem = (clsMenu) adaptergrid.getItem(position);
				menuid=vItem.ID;

				adaptergrid.setSelectedIndex(position);

				if (menuid==1) {
					if (validaFacturas())
						showMenuItem();
					} else {
						showMenuItem();
					}
			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void showMenuItem() {

		int prtype;
		boolean epssetflag = false;
		Float cantidad;

		//JP 20200527
		if (!listo) return;

		listo=false;
        Handler mtimer = new Handler();
        Runnable mrunner= () -> listo=true;
        mtimer.postDelayed(mrunner,1000);
        //JP fin del cambio

        app.parametrosExtra();

		try{

			prtype = getPrinterType();
			if (prtype == 2) {
				if (gl.mPrinterSet) epssetflag = false;
				else epssetflag = true;
			}

			if (menuid == 2) epssetflag = false;
			if (menuid == 9) epssetflag = false;

			if (epssetflag) {
				Intent intent = new Intent(this, UtilPrint.class);
				startActivity(intent);
				return;
			}

			switch (menuid) {

				case 1:

					gl.cajaid=5;
                    gl.InvCompSend=false;
                    gl.ventalock=false;
                    gl.impresion_comanda=false;

                    if (valida()) {

					    gl.nivel_sucursal=app.nivelSucursal();gl.cliente="C.F.";
						gl.gNombreCliente ="Consumidor final";
						gl.gNITCliente ="C.F.";gl.gDirCliente ="Ciudad";
                        gl.cliposflag=false;gl.rutatipo="V";gl.rutatipog="V";

						if (!validaVenta()) {
							//return;//Se valida si hay correlativos de factura para la venta
						}

                        gl.iniciaVenta=true;gl.exitflag=false;gl.forcedclose=false;
                        gl.preimpresion=false;gl.codigo_cliente=0;

						if (impresoraInstalada()) {
                            startActivity(new Intent(this, Venta.class));
                        } else {
                            msgAskImpresora();
                        }
					} else {

                        writeCorelLog(103,gl.cajaid,"showMenuItem gl.cajaid");

						//gl.cierreDiario=false;

                        if(gl.cajaid==5) msgAskIniciarCaja("La caja está cerrada. ¿Realizar el inicio de caja?");
						//msgAskValid("La caja está cerrada, si desea iniciar operaciones debe realizar el inicio de caja");
						//#CKFK 20200521 Se modificó lo del cierre a través de un parámetro, si se utiliza FEL es obligatorio hacer el cierre de caja diario
						if (gl.cierreDiario){
							if(gl.cajaid==6) msgAskValidaCierre("No realizó el cierre de caja del día " + du.sfecha(gl.lastDate) + ". ¿Realizar cierre Z?");
						} else {
							if(gl.cajaid==6) msgAskValidUltZ("No se realizó el último cierre de caja, ¿Desea continuar la venta con la fecha: "+du.univfechaReport(gl.lastDate)+", o desea realizar el cierre Z?");
						}
					}
					break;
				case 2:  // Comunicacion
                    showMenuCom();break;
				case 3:  // Reimpresion
			        showPrintMenuTodo();break;
				case 4:  // Anulacion
				    //showVoidMenuTodo();
					validaSupervisor();
					break;
				case 5:  // Consultas
					//#HS_20181206 Verifica el usuario si es DTS.
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					} else showConsMenu();
					break;
				case 6:  // Deposito
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					} else showCajaMenu();
					break;
				case 7:  // Inventario
					//#HS_20181206 Verifica el usuario si es DTS.
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					} else showInvMenuVenta();
					break;
				case 8:  // Fin Dia
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					} else {
						Intent intent8 = new Intent(this, FinDia.class);
						startActivity(intent8);
					}
					break;
				case 9:  // Utilerias
					showInvMenuUtils();break;
				case 10:  // Cambio usuario
					askCambUsuario();break;
				case 11:
					if (gl.rol==3) showMantMenu(); else showMantCliente();
					break;
				case 12:
					showReportMenu();break;
				case 13:
					apagar();break;
                case 14:
                    showEmergMenu();break;
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion

	//region Reimpresion
	
	public void showPrintMenuTodo() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Reimpresión");

     		listdlg.add((gl.peMFact?"Factura":"Ticket"));
			//listdlg.add("Depósito");
			//listdlg.add("Pagos");
			//listdlg.add("Recarga");
			//listdlg.add("Devolución a bodega");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					switch (position) {
						case 0:
							menuImprDoc(3);break;
						case 1:
							menuImprDoc(2);break;
						case 2:
							menuImprDoc(7);break;
						case 3:
							menuImprDoc(4);break;
						case 4:
							menuImprDoc(5);break;
					}
					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	public void menuImprDoc(int doctipo) {
		try{
			gl.tipo=doctipo;

			Intent intent = new Intent(this,Reimpresion.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion
	
	//region Anulacion

	private void validaSupervisor() {

		clsClasses.clsVendedores item;

		try {
		    clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
			app.fillSuper(VendedoresObj);

			if (VendedoresObj.count==0) {
				msgbox("No está definido ningún supervisor");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(Menu.this,"Autorización","Salir");

			for (int i = 0; i <VendedoresObj.count; i++) {
				item=VendedoresObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.onEnterClick(v -> {

				if (listdlg.getInput().isEmpty()) return;

				if (listdlg.validPassword()) {
					showVoidMenuTodo();
					listdlg.dismiss();
				} else {
					toast("Contraseña incorrecta");
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	public void showVoidMenuTodo() {

		try {
			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Anulación");

			listdlg.add((gl.peMFact?"Factura":"Ticket"));
			listdlg.add("Depósito");
			listdlg.add("Ingreso de mercancía");
			listdlg.add("Ajuste de inventario");

			listdlg.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					try {
						switch (position) {
							case 0:
								gl.tipo=3;break;
							case 1:
								gl.tipo=2;break;
							case 2:
								gl.tipo=4;break;
							case 3:
								gl.tipo=5;break;
						}

						menuAnulDoc();
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

	private void menuAnulDoc() {
		try{
			Intent intent = new Intent(this,Anulacion.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	//endregion

    //region Comunicacion

    private void showMenuCom() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Comunicación");

			listdlg.add("Envío de datos");
			listdlg.add("Recepción de parámetros");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					switch (position) {
						case 0:
							gl.autocom = 0;
							startActivity(new Intent(Menu.this,WSEnv.class));
							break;
						case 1:
							gl.autocom = 0;
							startActivity(new Intent(Menu.this,WSRec.class));
							break;
						case 2:
							;break;
						case 3:
							comEnterior();break;
					}

					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

    }

    private void comEnterior() {
        gl.findiaactivo=false;
        gl.tipo = 0;
        gl.autocom = 0;
        gl.modoadmin = false;
        gl.comquickrec = false;
        Intent intent2 = new Intent(this, ComWS.class);
        startActivity(intent2);
    }

    //endregion

	//region Consultas
	
	public void showConsMenu() {

		try {
			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Consultas");

			listdlg.add("Objetivos por producto");
			listdlg.add("Objetivos por familia");
			listdlg.add("Objetivo por ruta");
			listdlg.add("Objetivo por cobro");
			listdlg.add("Inventario bodega");
			listdlg.add("Consulta de precios");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					switch (position) {
						case 0:
							menuObjProd();break;
						case 1:
							menuObjFamilia();break;
						case 2:
							menuObjRuta();break;
						case 3:
							menuObjCobro();break;
						case 4:
							menuInvBod();listdlg.dismiss();break;
						case 5:
							menuPrecios();listdlg.dismiss();break;
					}

				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.show();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}
	
	private void menuObjProd() {
		try{
			gl.tipo=0;

			Intent intent = new Intent(this,ObjProd.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void menuObjFamilia() {
		try{
			gl.tipo=1;

			Intent intent = new Intent(this,ObjProd.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void menuObjRuta() {
		try{
			gl.tipo=0;

			Intent intent = new Intent(this,ObjRuta.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void menuObjCobro() {
		try{
			gl.tipo=1;

			Intent intent = new Intent(this,ObjRuta.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void menuInvBod() {
		try{
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}	
	
	private void menuPrecios() {
		try{
			Intent intent = new Intent(this,ConsPrecio.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion

	//region Inventario
	
	public void showInvMenuVenta() 	{

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Menu inventario");

			listdlg.add("Existencias");
			listdlg.add("Ingreso de mercancía");
			listdlg.add("Ajuste de inventario");
			if (almacenes) {
				listdlg.add("Traslado entre almacénes");
				listdlg.add("Egreso de almacén");
			}

			//listdlg.add("Orden de compra");
			//listdlg.add("Barril");
			//listdlg.add("Inventario centralizado");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					String mt=listdlg.items.get(position).text;

					if (mt.equalsIgnoreCase("Existencias")) {
						menuExist();
					}
					if (mt.equalsIgnoreCase("Ajuste de inventario")) menuAjuste();
					if (mt.equalsIgnoreCase("Ingreso de mercancía")) menuRecarga();
					if (mt.equalsIgnoreCase("Inventario inicial")) menuInvIni();
					if (mt.equalsIgnoreCase("Orden de compra")) menuCompra();
					if (mt.equalsIgnoreCase("Traslado entre almacénes")) menuTraslado();
					if (mt.equalsIgnoreCase("Egreso de almacén")) menuEgreso();
					if (mt.equalsIgnoreCase("Barril")) menuBarril();
					if (mt.equalsIgnoreCase("Inventario centralizado")) validaSuperInvCent();

					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.show();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void menuExist() {
		try {
			gl.tipo=0;gl.idalm=0;gl.nom_alm="";
			Intent intent = new Intent(this,Exist.class);
			startActivity(intent);
		} catch (Exception e){}
	}

	private void menuRecarga() {
		try {
			gl.invregular=true;
			gl.tipo = 0;if (almacenes) gl.tipo=4;
			gl.idalm=0;gl.nom_alm="";
			Intent intent = new Intent(Menu.this, ListaInventario.class);
			startActivity(intent);
		} catch (Exception e){}
	}

	private void menuAjuste() {
		try {
			gl.tipo = 1;gl.idalm=0;gl.nom_alm="";
			Intent intent = new Intent(Menu.this, ListaInventario.class);
			startActivity(intent);
		} catch (Exception e){}
	}

    private void menuInvIni() {
        try {
            gl.tipo = 2;
            Intent intent = new Intent(this, ListaInventario.class);
            startActivity(intent);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void menuCompra() {
	    //toast("Pendiente implementación");
		try {
			Intent intent = new Intent(this, ListaCompras.class);
			startActivity(intent);
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
    }

	private void menuTraslado() {
		try {
			gl.tipo=8;
			Intent intent = new Intent(Menu.this, ListaInventarioT.class);
			startActivity(intent);
		} catch (Exception e){}
    }

    private void menuEgreso() {
		try {

			gl.tipo=6;
			Intent intent = new Intent(Menu.this, ListaInventario.class);
			startActivity(intent);
		} catch (Exception e){}
    }

	private void menuBarril() {
		try {
			Intent intent = new Intent(Menu.this, ListaBarril.class);
			startActivity(intent);
		} catch (Exception e){}
	}

	private void validaSuperInvCent() {

		clsClasses.clsVendedores item;

		try {
			clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
			app.fillSuper(VendedoresObj);

			if (VendedoresObj.count==0) {
				msgbox("No está definido ningún supervisor");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(Menu.this,"Autorización","Salir");

			for (int i = 0; i <VendedoresObj.count; i++) {
				item=VendedoresObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.onEnterClick(v -> {

				if (listdlg.getInput().isEmpty()) return;

				if (listdlg.validPassword()) {
					menuInvCentral();
					listdlg.dismiss();
				} else {
					toast("Contraseña incorrecta");
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}


	public void menuInvCentral() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Inventario centralizado");

			//listdlg.add("Ingreso de mercancía");
			//listdlg.add("Ajuste de inventario");
			listdlg.add("Inventario inicial");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {
				validaInventarioCentral();
				listdlg.dismiss();
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.show();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void validaInventarioCentral() {
		Cursor dt;

		try {

			sql="SELECT CANT FROM P_STOCK";
			dt=Con.OpenDT(sql);
			if (dt.getCount()>0) {
				msgbox("Antes de procesar inventario inicial debe iniciar inventario inicial");return;
			}

			sql="SELECT CANT FROM P_STOCK_ALMACEN";
			dt=Con.OpenDT(sql);
			if (dt.getCount()>0) {
				msgbox("Antes de procesar inventario inicial debe iniciar inventario inicial");return;
			}

			gl.cajaid=5;
			if(valida()){
				msgbox("El inventario inicial se puede realizar únicamente cuando la caja está cerrada-");return;
			}

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		msgAskInvInic("Este proceso eliminará todas las existencías y creará las existencias nuevas.\n¿Continuar? ");

	}

	private void consultaInvCentral(int modo) {

		try {
			modo_invcent=modo;

			switch (modo_invcent) {
				case 0:
					gl.invcent_tipo="R";break;
				case 1:
					gl.invcent_tipo="A";break;
				case 2:
					gl.invcent_tipo="I";break;
			}

			waitdlg.buildDialog(this,"Recibiendo inventario . . .","Ocultar");
			waitdlg.show();

			sql="SELECT CODIGO_INVENTARIO_ENC FROM P_STOCK_INVENTARIO_ENC WHERE (CODIGO_SUCURSAL="+gl.tienda+") " +
				"AND (ESTADO<2) AND (TIPO='"+gl.invcent_tipo+"') ORDER BY CODIGO_INVENTARIO_ENC";
			wsic.execute(sql,rnInvCent);

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void callbackInvCent() {
		try {

			try {
				waitdlg.wdhandle.dismiss();
			} catch (Exception e) {}

			if (wsic.errflag) {
				msgbox(wsic.error);return;
			}

			if (wsic.openDTCursor.getCount()==0) {
				msgbox("No existe ninguna tarea pendiente");return;
			}

			wsic.openDTCursor.moveToFirst();
			gl.invcent_cod=wsic.openDTCursor.getInt(0);

			//gl.invcent_tipo
			switch (modo_invcent) {
				case 0:
					;break;
				case 1:
					;break;
				case 2:
					startActivity(new Intent(this,InvCentral.class)); ;break;
			}
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	//endregion

	//region Utilerias

    public void showInvMenuUtils() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Utilerias");
			listdlg.setLines(6);

			listdlg.add("Configuración de impresora");
			listdlg.add("Tablas");
			listdlg.add("Actualizar versión");
			listdlg.add("Enviar base de datos");
			listdlg.add("Certificar facturas");
			listdlg.add("Limpiar tablas");
			listdlg.add("Prueba de bluetooth");
			listdlg.add("Marcar facturas certificadas");
			listdlg.add("Actualizar correlativos contingencia");
			listdlg.add("Información de sistema");
			listdlg.add("Impresion");
			listdlg.add("Consumidor final");
			listdlg.add("Actualizar fechas erroneas");
			listdlg.add("Inicio de caja");
			listdlg.add("Inicializar inventario");
			listdlg.add("Reinicializar numero de orden");


			listdlg.setOnItemClickListener((parent, view, position, id) -> {
				try {
					switch (position) {
						case 0:
							//startActivity(new Intent(Menu.this,UtilPrint.class));
							configuracionImpresora();
							break;
						case 1:
							startActivity(new Intent(Menu.this,Tablas.class));break;
						case 2:
							actualizaVersion();break;
						case 3:
							enviarBaseDeDatos();break;
						case 4:
							msgAskFEL("Certificar facturas pendientes");break;
						case 5:
							validaSuperLimpia();break;
						case 6:
							estadoBluTooth();break;
						case 7:
							startActivity(new Intent(Menu.this,MarcarFacturas.class));break;
						case 8:
							msgAskActualizar("Actualizar correlativos de contingencia");break;
						case 9:
							infoSystem();break;
						case 10:
							msgAskImprimir();break;
						case 11:
							msgAskCF();break;
						case 12:
							msgAskCorregirFechas();break;
						case 13:
							inicioDia();break;
						case 14:
							validaSuperInventario();break;
						case 15:
							validaSuperNumOrden();break;

					}
					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

    }

    private void configuracionImpresora() {
        startActivity(new Intent(Menu.this, UtilPrint.class));
    }

    private void actualizaVersion() {
        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.dts.mposupd");
            intent.putExtra("filename","mpos.apk");
            this.startActivity(intent);
        } catch (Exception e) {
            msgbox("No está instalada aplicación para actualización de versiónes, por favor informe soporte.");
        }

    }

    private void askCambUsuario() {

        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage("¿Cambiar usuario?");
            dialog.setPositiveButton("Cambiar", (dialog1, whichButton) -> {
				app.logoutUser(du.getActDateTime());
				Menu.super.finish();
			});
            dialog.setNegativeButton("Cancelar", (dialog12, whichButton) -> {
			});
            dialog.show();

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void enviarBaseDeDatos() {
        msgAskDatabase("Enviar la base de datos al centro de soporte");
    }

    private void sendDB() {

        String subject,body;
        String dir=Environment.getExternalStorageDirectory()+"";
		long fsize,fslim;

        try {
            File f1 = new File(dir + "/posdts.db");
            File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
            File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
            FileUtils.copyFile(f1, f2);
            Uri uri = Uri.fromFile(f3);

            app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

			try {
				fsize=f3.length();fslim=25*1024*1000;
				if (fsize>fslim) {
					msgbox("El tamaño de archivo mayor de 25MB, por favor avize a soporte ");return;
				}
			} catch (Exception e) {
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			}


			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            subject= "Base de datos : "+gl.tiendanom+" caja : "+gl.codigo_ruta;
            body="Adjunto base de datos";

            String[] TO = {"dtsolutionsgt@gmail.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void infoSystem() {

        String ss,sb="",sm="",sd="";

        try {
            BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            sb="Carga de batería  : "+batLevel+" %";
        } catch (Exception e) {
            msgbox(e.getMessage());
            sb="Carga de batería  :  -";
        }

        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            int availableMegs = (int) (mi.availMem / 0x100000L);
            int totalMegs = (int) (mi.totalMem / 0x100000L);
            int percentAvail =(int) (100*mi.availMem/(double)mi.totalMem );

            sm="Memoria disponible  :  "+percentAvail+" %  ,   "+availableMegs+" / "+totalMegs+" [ MB ]";
        } catch (Exception e) {
            msgbox(e.getMessage());
            sm="Memoria disponible  :  -";
        }

        try {

            File pathInternal = Environment.getDataDirectory();// Internal Storage
            StatFs statInternal = new StatFs(pathInternal.getPath());

            double totalStorage=(statInternal. getBlockCountLong()*statInternal.getBlockSizeLong())/ 0x100000L;
            double freeStorage=(statInternal. getAvailableBlocksLong()*statInternal.getBlockSizeLong())/ 0x100000L;
            int  availStorage=(int) (100*freeStorage/totalStorage );

            sd="almacénamiento disponible  :  "+availStorage+" %   ,   "+mu.round(freeStorage/1024,1)+" / "+mu.round(totalStorage/1024,1)+" [ GB ]";
        } catch (Exception e) {
            msgbox(e.getMessage());
            sd="almacénamiento disponible  :  -";
        }

        ss=sb+"\n"+sm+"\n"+sd;

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(ss);
        dialog.setCancelable(false);
        dialog.setNeutralButton("OK", (dialog1, which) -> {});
        dialog.show();

    }

    private void actualizarCont() {

        long cact,flim,ncont;
        clsClasses.clsD_factura fact;

        if (!validaCorel()) {
            msgAskActualizar2("Correlativo actual incorrecto. Por favor actualice los datos");return;
        }

        try {

            flim=du.addDays(du.getActDate(),-4);

            cact=contcorult;
            if (cact<contcorini) cact=contcorini;

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            sql="WHERE (FEELUUID=' ') AND (ANULADO=0) AND (FECHA>="+flim+")";
            D_facturaObj.fill(sql);

            for (int i = 0; i <D_facturaObj.count; i++) {

                try {
                    fact=D_facturaObj.items.get(i);

                    if (fact.feelfechaprocesado>0) {
                        ncont=Long.parseLong(fact.feelcontingencia);
                        if (ncont<contcorini) {
                            cact++;
                            sql="UPDATE D_FACTURA SET FEELCONTINGENCIA='"+cact+"' WHERE COREL='"+fact.corel+"' ";
                            db.execSQL(sql);
                        }
                    } else {
                        cact++;
                        sql="UPDATE D_FACTURA SET FEELCONTINGENCIA='"+cact+"' WHERE COREL='"+fact.corel+"' ";
                        db.execSQL(sql);
                    }
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            }

            sql="UPDATE P_COREL SET CORELULT="+cact+" WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)";
            db.execSQL(sql);

            msgbox("Proceso completo, por favor certifique las facturas pendientes");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private boolean validaCorel() {

        Cursor dt;

        long cini=100000000+100000*gl.codigo_ruta;

        try {

            sql="SELECT CORELINI,CORELULT FROM P_COREL WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=1)";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                contcorini=dt.getLong(0);
                contcorult=dt.getLong(1);
                if (contcorini==cini) return true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void inicioDia() {

        try {

            fhoy=du.getActDate();
            fayer=du.addDays(fhoy,-1);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Monto inicial de caja");

            final EditText input = new EditText(this);
            alert.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setText("");
            input.requestFocus();

            alert.setPositiveButton("Caja de hoy", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    double monto;
                    try {
                        String s=input.getText().toString();
                        monto=Double.parseDouble(s);
                        if (monto<0) throw new Exception();
                        msgAskValor(monto,fhoy);
                    } catch (Exception e) {
                        mu.msgbox("Monto incorrecto");return;
                    }
                }
            });

            alert.setNeutralButton("Caja de ayer", (dialog, whichButton) -> {
				double monto;
				try {
					String s=input.getText().toString();
					monto=Double.parseDouble(s);
					if (monto<0) throw new Exception();
					msgAskValor(monto,fayer);
				} catch (Exception e) {
					mu.msgbox("Monto incorrecto");return;
				}
			});
            alert.setNegativeButton("Cancelar", (dialog, whichButton) -> {});
            alert.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

	private void Limpiar_Tablas_No_Criticas() {
		try {

			ExDialog dialog = new ExDialog(this);
			dialog.setMessage("¿Limpiar tablas?");
			dialog.setCancelable(false);

			dialog.setPositiveButton("Si", (dialog12, which) -> {

				waitdlglimp.buildDialog(this,"Limpiando tablas, por favor espere . . .","Ocultar");
				waitdlglimp.show();

				Handler mtimer = new Handler();
				Runnable mrunner=new Runnable() {
					@Override
					public void run() {
						limpiaTablas();
					}
				};
				mtimer.postDelayed(mrunner,1000);

			});
			dialog.setNegativeButton("No", (dialog1, which) -> {});
			dialog.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void limpiaTablas() {
		long ff=du.getActDate();
		long fd=ff-100000000;
		String sqlfs;

		ff=du.addDays(ff,-7);

		try {
			sql="DELETE FROM D_fel_bitacora WHERE fecha<"+ff;
			db.execSQL(sql);
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		try {
			sql="DELETE FROM D_fel_error WHERE fecha<"+ff;
			db.execSQL(sql);
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		try {
			db.beginTransaction();

			sqlfs="SELECT COREL FROM D_FACTURA WHERE FECHA<"+fd;

			db.execSQL("DELETE FROM D_FACTURAD    WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURA_FEL WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAC    WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAF    WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAHN   WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAP    WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAPR   WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAR    WHERE COREL IN ("+sqlfs+")");
			db.execSQL("DELETE FROM D_FACTURAS    WHERE COREL IN ("+sqlfs+")");

			db.execSQL("DELETE FROM D_FACTURA WHERE FECHA<"+fd);

			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			db.endTransaction();msgbox(e.getMessage());
		}

		try {
			db.execSQL("VACUUM");
		} catch (Exception e) {}

		waitdlglimp.dismiss();

		try {
			ExDialog dialog = new ExDialog(this);
			dialog.setMessage("Limpieza de tablas completa.");
			dialog.setCancelable(false);

			dialog.setPositiveButton("OK", (dialog12, which) -> {});
			dialog.show();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

    private void msgAskValor(double monto,long ff) {

        try {
            caja = new clsP_cajacierreObj(this,Con,db);

            caja.fill("WHERE (FECHA="+ff+")");
            if (caja.count>0) {
                msgbox("No se puede continuar, ya existe registo de caja para el dia "+du.sfecha(ff)+" !");return;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Inicio de caja");
            dialog.setMessage("¿Iniciar caja dia "+du.sfecha(ff)+"?");
            dialog.setPositiveButton("Si", (dialog12, which) -> procesaInicioCaja(monto,ff));
            dialog.setNegativeButton("No", (dialog1, which) -> {});
            dialog.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void procesaInicioCaja(double monto,long ff) {

        clsClasses.clsP_cajacierre itemC;

        try {

            caja.fill();
            gl.corelZ = caja.last().corel + 1;

            itemC = clsCls.new clsP_cajacierre();
            itemC.codigo_cajacierre=gl.ruta+"_"+mu.getCorelBase();
            itemC.empresa=gl.emp;
            itemC.sucursal =  gl.tienda;
            itemC.ruta = gl.codigo_ruta;
            itemC.corel = gl.corelZ;
            itemC.vendedor =  gl.codigo_vendedor;
            itemC.fondocaja = monto;
            itemC.statcom = "N";
            itemC.fecha = ff;
            itemC.estado = 0;
            itemC.codpago = 0;
            itemC.montoini = 0;
            itemC.montofin = 0;
            itemC.montodif = 0;

            caja.add(itemC);

            msgbox("Inicio de caja correcto");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
	private void estadoBluTooth() {

        final int REQUEST_ENABLE_BT = 1;

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                msgbox("El dispositivo no soporta Bluetooth");
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    msgbox("Bluetooth encendido");
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

	private void borraInventario(){
		try {
			db.beginTransaction();

			sql="DELETE FROM P_STOCK";
			db.execSQL(sql);

			sql="DELETE FROM P_STOCK_ALMACEN";
			db.execSQL(sql);

			sql="DELETE FROM D_MOV";
			db.execSQL(sql);

			sql="DELETE FROM D_MOVD";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			db.endTransaction();

			toastcentlong("Se inicializó el inventario.");
		} catch (Exception e) {
			db.endTransaction();
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void validaSuperInventario() {

		clsClasses.clsVendedores item;

		try {
			clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
			app.fillSuper(VendedoresObj);

			if (VendedoresObj.count==0) {
				msgbox("No está definido ningún supervisor");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(Menu.this,"Autorización","Salir");

			for (int i = 0; i <VendedoresObj.count; i++) {
				item=VendedoresObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.onEnterClick(v -> {

				if (listdlg.getInput().isEmpty()) return;

				if (listdlg.validPassword()) {
					msgAskBorrarInventario("¿Inicializar inventario?");
					listdlg.dismiss();
				} else {
					toast("Contraseña incorrecta");
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void validaSuperLimpia() {

		clsClasses.clsVendedores item;

		try {
			clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
			app.fillSuper(VendedoresObj);

			if (VendedoresObj.count==0) {
				msgbox("No está definido ningún supervisor");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(Menu.this,"Autorización","Salir");

			for (int i = 0; i <VendedoresObj.count; i++) {
				item=VendedoresObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.onEnterClick(v -> {

				if (listdlg.getInput().isEmpty()) return;

				if (listdlg.validPassword()) {
					Limpiar_Tablas_No_Criticas();
					listdlg.dismiss();
				} else {
					toast("Contraseña incorrecta");
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void validaSuperNumOrden() {

		clsClasses.clsVendedores item;

		try {
			clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
			app.fillSuper(VendedoresObj);

			if (VendedoresObj.count==0) {
				msgbox("No está definido ningún supervisor");return;
			}

			extListPassDlg listdlg = new extListPassDlg();
			listdlg.buildDialog(Menu.this,"Autorización","Salir");

			for (int i = 0; i <VendedoresObj.count; i++) {
				item=VendedoresObj.items.get(i);
				listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
			}

			listdlg.setOnLeftClick(v -> listdlg.dismiss());

			listdlg.onEnterClick(v -> {

				if (listdlg.getInput().isEmpty()) return;

				if (listdlg.validPassword()) {
					reiiniciaNumeroOrden();
					listdlg.dismiss();
				} else {
					toast("Contraseña incorrecta");
				}
			});

			listdlg.setWidth(350);
			listdlg.setLines(4);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void reiiniciaNumeroOrden() {
		try {
			if (gl.peNumOrdCentral) {
				sql="DELETE FROM T_ORDEN_CODIGO WHERE CODIGO_SUCURSAL="+gl.tienda;
				wscom.execute(sql,rnNumOrden);
			} else {
				db.execSQL("DELETE FROM P_orden_numero ");
				msgbox("Número de orden reiniciado");
			}
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void callBackNumOrden() {
		if (!wscom.errflag) {
			msgbox("Número de orden reiniciado");
		} else {
			msgbox(wscom.error);
		}
	}

	//endregion

	//region Mantenimientos

	public void showMantMenu() {

	    gl.climode=false;
	    gl.listaedit=true;

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Mantenimientos");
			listdlg.setLines(10);

			listdlg.add("Banco");
			listdlg.add("Caja");
			listdlg.add("Cliente");
			listdlg.add("Empresa");
			listdlg.add("Familia");
			listdlg.add("Forma pago");
			listdlg.add("Impuesto");
			listdlg.add("Concepto pago");
			listdlg.add("Nivel precio");
			listdlg.add("Motivo ajuste");
			listdlg.add("Producto");
			listdlg.add("Proveedor");
			listdlg.add("Tienda");
			listdlg.add("Usuario");
			listdlg.add("Roles");
			listdlg.add("Impresora");
			listdlg.add("Impresora marca");
			listdlg.add("Impresora modelo");
			listdlg.add("Resolución de facturas");
			if (gl.peRest) listdlg.add("Restaurante");
			listdlg.add("Configuración");
			listdlg.add("Configuración reportes Cierre");
			listdlg.add("Redirección de impresoras LAN");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					ss = listdlg.items.get(position).text;
					if (ss.equalsIgnoreCase("Almacén")) gl.mantid = 0;
					if (ss.equalsIgnoreCase("Banco")) gl.mantid = 1;
					if (ss.equalsIgnoreCase("Caja")) gl.mantid = 13;
					if (ss.equalsIgnoreCase("Cliente")) gl.mantid = 2;
					if (ss.equalsIgnoreCase("Combo")) gl.mantid = 17;
					if (ss.equalsIgnoreCase("Combo opción")) gl.mantid = 18;
					if (ss.equalsIgnoreCase("Descuento")) gl.mantid = 15;
					if (ss.equalsIgnoreCase("Empresa")) gl.mantid = 3;
					if (ss.equalsIgnoreCase("Familia")) gl.mantid = 4;
					if (ss.equalsIgnoreCase("Forma pago")) gl.mantid = 5;
					if (ss.equalsIgnoreCase("Impuesto")) gl.mantid = 6;
					if (ss.equalsIgnoreCase("Moneda")) gl.mantid = 7;
					if (ss.equalsIgnoreCase("Concepto pago")) gl.mantid = 19;
					if (ss.equalsIgnoreCase("Nivel precio")) gl.mantid = 14;
					if (ss.equalsIgnoreCase("Motivo ajuste")) gl.mantid = 22;
					if (ss.equalsIgnoreCase("Producto")) gl.mantid = 8;
					if (ss.equalsIgnoreCase("Proveedor")) gl.mantid = 9;
					if (ss.equalsIgnoreCase("Tienda")) gl.mantid = 12;
					if (ss.equalsIgnoreCase("Usuario")) gl.mantid = 11;
					if (ss.equalsIgnoreCase("Resolución de facturas")) gl.mantid = 20;
					if (ss.equalsIgnoreCase("Configuración")) gl.mantid = 16;
					if (ss.equalsIgnoreCase("Formato de impresión")) gl.mantid = 17;
					if (ss.equalsIgnoreCase("Roles")) gl.mantid = 21;
					if (ss.equalsIgnoreCase("Configuración reportes Cierre")) gl.mantid = 23;
					if (ss.equalsIgnoreCase("Restaurante")) gl.mantid = 24;
					if (ss.equalsIgnoreCase("Impresora marca")) gl.mantid = 32;
					if (ss.equalsIgnoreCase("Impresora modelo")) gl.mantid = 33;
					if (ss.equalsIgnoreCase("Impresora")) gl.mantid = 34;
					//if (ss.equalsIgnoreCase("Redirección de impresoras LAN")) gl.mantid = 35;


					if (gl.mantid == 16) {
						startActivity(new Intent(Menu.this, MantConfig.class));
					} else if (gl.mantid == 15) {
						startActivity(new Intent(Menu.this, Lista.class));
					} else if (gl.mantid == 20) {
						startActivity(new Intent(Menu.this, MantCorel.class));
					} else if (gl.mantid == 21) {
						startActivity(new Intent(Menu.this, MantRol.class));
					} else if (gl.mantid == 23) {
						startActivity(new Intent(Menu.this, MantRepCierre.class));
					} else if (gl.mantid == 24) {
						showMantRestMenu();
						//startActivity(new Intent(Menu.this, SalaDis.class));
					} else if (gl.mantid == 35) {
						startActivity(new Intent(Menu.this, MantImpRedir.class));
					} else {
						startActivity(new Intent(Menu.this, Lista.class));
					}

					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

    public void showMantRestMenu() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Restaurante");

			listdlg.add("Configuración");
			listdlg.add("Sala");
			listdlg.add("Mesa");
			listdlg.add("Grupo de mesas");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {
					ss = listdlg.items.get(position).text;

					if (ss.equalsIgnoreCase("Sala")) gl.mantid = 26;
					if (ss.equalsIgnoreCase("Mesa")) gl.mantid = 27;
					if (ss.equalsIgnoreCase("Grupo de mesas")) gl.mantid = 28;
					if (ss.equalsIgnoreCase("Diseño de sala")) gl.mantid = 24;
					if (ss.equalsIgnoreCase("Configuración")) gl.mantid = 29;
					if (ss.equalsIgnoreCase("Estación cocina")) gl.mantid = 31;

					if (gl.mantid == 24) {
						startActivity(new Intent(Menu.this, SalaDis.class));
					} else if (gl.mantid == 29) {
						startActivity(new Intent(Menu.this, MantConfigRes.class));
					} else {
						startActivity(new Intent(Menu.this, Lista.class));
					}

					//listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

    }

    public void showMantCliente() {

        gl.climode=false;
        gl.listaedit=true;

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Cliente");

			listdlg.add("Cliente");
			listdlg.add("Proveedor");
			listdlg.add("Familia");
			listdlg.add("Producto");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {
					ss = listdlg.items.get(position).text;

					if (ss.equalsIgnoreCase("Cliente")) gl.mantid=2;
					if (ss.equalsIgnoreCase("Familia")) gl.mantid=4;
					if (ss.equalsIgnoreCase("Producto")) gl.mantid=8;
					if (ss.equalsIgnoreCase("Proveedor")) gl.mantid=9;

					if (gl.mantid==16) {
						startActivity(new Intent(Menu.this, MantConfig.class));
					} else if (gl.mantid==15) {
						startActivity(new Intent(Menu.this, Lista.class));
					} else {
						startActivity(new Intent(Menu.this, Lista.class));
					}

					listdlg.dismiss();
				} catch (Exception e) {}
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

	//endregion

    //region Reportes

	public void showReportMenu() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Reportes");

			listdlg.add("Reporte de Documentos por Día");
			listdlg.add("Reporte Venta por Día");
			listdlg.add("Reporte Venta por Producto");
			listdlg.add("Reporte por Forma de Pago");
			listdlg.add("Reporte por Familia");
			if (cortesias) listdlg.add("Cortesias");
			listdlg.add("Reporte Ventas por Vendedor");
			listdlg.add("Consumo materia prima");
			listdlg.add("Reporte de Ventas por Cliente");
			listdlg.add("Margen y Beneficio por Producto");
			listdlg.add("Margen y Beneficio por Familia");
			listdlg.add("Cierre X");
			listdlg.add("Cierre Z");
			listdlg.add("Cierre del día");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					ss = listdlg.items.get(position).text;

					if (ss.equalsIgnoreCase("Reporte de Documentos por Día")) gl.reportid=1;
					if (ss.equalsIgnoreCase("Reporte Venta por Día")) gl.reportid=2;
					if (ss.equalsIgnoreCase("Reporte Venta por Producto")) gl.reportid=3;
					if (ss.equalsIgnoreCase("Reporte por Forma de Pago")) gl.reportid=4;
					if (ss.equalsIgnoreCase("Reporte por Familia")) gl.reportid=5;
					if (ss.equalsIgnoreCase("Reporte Ventas por Vendedor")) gl.reportid=6;
					if (ss.equalsIgnoreCase("Margen y Beneficio por Productos")) gl.reportid=7;
					if (ss.equalsIgnoreCase("Margen y Beneficio por Familia")) gl.reportid=8;
					if (ss.equalsIgnoreCase("Cierre X")) gl.reportid=9;
					if (ss.equalsIgnoreCase("Cierre Z")) gl.reportid=10;
					if (ss.equalsIgnoreCase("Reporte de Ventas por Cliente")) gl.reportid=11;
					if (ss.equalsIgnoreCase("Cierre del día")) gl.reportid=12;
					if (ss.equalsIgnoreCase("Consumo materia prima")) gl.reportid=13;
					//if (ss.equalsIgnoreCase("Consumo materia prima por producto")) gl.reportid=14;
					if (ss.equalsIgnoreCase("Cortesias")) gl.reportid=15;


					gl.titReport = ss;

					if (gl.reportid == 9 || gl.reportid == 10) {
						startActivity(new Intent(Menu.this, CierreX.class));
					} else if (gl.reportid == 12) {
						//msgAskUltimoCierre();
						listaCierres();
					} else {
						startActivity(new Intent(Menu.this,Reportes.class));
					}

					listdlg.dismiss();
				} catch (Exception e) {}
			});

			listdlg.setOnLeftClick(v -> listdlg.dismiss());
			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

    }

	private void listaCierres() {
		String ss;

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Cierres","Salir");

			sql="SELECT DISTINCT CIERRE,FECHA FROM D_cierre ORDER BY CIERRE DESC";
			Cursor dt=Con.OpenDT(sql);
			if (dt.getCount()==0) {
				msgbox("No existe cierre en ultimos 7 días");return;
			}

			dt.moveToFirst();
			while (!dt.isAfterLast()) {
				ss=du.sfecha(dt.getLong(1))+" "+du.shora(dt.getLong(1));
				listdlg.add(dt.getInt(0),ss);
				dt.moveToNext();
			}


			listdlg.setOnLeftClick(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listdlg.dismiss();
				}
			});

			listdlg.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					try {
						idcierre=listdlg.getCodigoInt(position);
						msgAskUltimoCierre();
						listdlg.dismiss();
					} catch (Exception e) {}
				};
			});

			listdlg.setWidth(350);

			listdlg.show();

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

    //endregion

    //region Caja

	public void showCajaMenu() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Caja");

			listdlg.add("Inicio de Caja");
			listdlg.add("Pagos de Caja");
			listdlg.add("Depósitos");
			listdlg.add("Cierre de Caja");
			listdlg.add("Reportes Cierre del día");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					ss = listdlg.items.get(position).text;

					if (ss.equalsIgnoreCase("Inicio de Caja")) gl.cajaid=1;
					if (ss.equalsIgnoreCase("Pagos de Caja")) gl.cajaid=2;
					if (ss.equalsIgnoreCase("Depósitos")) gl.cajaid=4;
					if (ss.equalsIgnoreCase("Cierre de Caja")) gl.cajaid=3;
					if (ss.equalsIgnoreCase("Reportes Cierre del día")) gl.cajaid=5;

					int cid=gl.cajaid;
					gl.titReport = ss;

					if (gl.cajaid==5) {
						listaCierres();
					} else {
						if (valida()) {

							if (gl.cajaid==2) {
								startActivity(new Intent(Menu.this, CajaPagos.class));
							} else {
								validaCaja();
							}

						} else {
							String txt="";

							if (gl.cajaid == 0 || gl.cajaid == 2)
								txt = "La caja no se ha abierto, si desea iniciar turno o realizar pagos debe realizar el inicio de caja.";
							if (gl.cajaid == 1)
								txt = "La caja ya está abierta, si desea iniciar otro turno debe realizar el fin de caja.";
							if (gl.cajaid == 4) txt = "Pendiente implementación.";
							if (gl.cajaid == 3)
								txt = "La caja está cerrada, si desea iniciar operaciones o realizar pagos debe realizar el inicio de caja.";
							msgAskValid(txt);

						}

					}

					listdlg.dismiss();
				} catch (Exception e) {}
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

	private void validaCaja() {

		try {

			long flim = du.addHours(-12);
			clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
			P_res_sesionObj.fill("WHERE (Estado>0) AND (FECHAINI>=" + flim + ")");

			if (P_res_sesionObj.count>0) {
				msgAskMesas("Existen mesas abiertas.\n¿Continuar?");
			} else {
				gl.inicio_caja_correcto = false;
				browse = 1;
				startActivity(new Intent(Menu.this, Caja.class));
			}
			} catch (Exception e) {
			msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
		}
	}

	private void msgAskMesas(String msg) {

		ExDialog dialog = new ExDialog(this);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("Si", (dialog12, which) -> {
			gl.inicio_caja_correcto = false;
			browse = 1;
			startActivity(new Intent(Menu.this, Caja.class));
		});
		dialog.setNegativeButton("No", (dialog1, which) -> {});
		dialog.show();
	}


	//endregion

    //region Modo emergencia

	public void showEmergMenu() {

		try {

			extListDlg listdlg = new extListDlg();
			listdlg.buildDialog(Menu.this,"Modo de emergencia");

			listdlg.add("Sin internet - Activar única caja");
			listdlg.add("Sin internet - Enviar pendientes ");
			listdlg.add("Sin internet - Terminar modo sin internet");
			listdlg.add("Modo sin red");

			listdlg.setOnItemClickListener((parent, view, position, id) -> {

				try {

					switch (position) {
						case 0:
							;break;//supervisor
						case 1:
							;break;
						case 2:
							;break;//supervisor
						case 3:
							modoSinRed();break;
					}

					listdlg.dismiss();
				} catch (Exception e) {}
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



	public void modoSinRed() {

        try {

            P_modo_emergenciaObj.fill();
            modo_emerg=P_modo_emergenciaObj.count==0;

            if (modo_emerg) msgAskEmerg("Activar modo de emergencia");else msgAskEmerg("Desactivar modo de emergencia");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void activarSinRed() {
        clsP_paramextObj P_paramextObj=new clsP_paramextObj(this,Con,db);

        try {

            db.beginTransaction();
            db.execSQL("DELETE FROM P_modo_emergencia");

            activarItem(131,"S"); // 131  Modulo caja
            activarItem(132,"N"); // 132 Caja – recepcion de ordenes
            //activarItem(136,"S"); // 136 Ingresar contraseña cajero
            //activarItem(135,"S"); // 135 Ingresar contraseña mesero
            activarItem(137,"N"); // 137 Enviar ordenes a la caja
			//#EJC2022210222107: No redireccionar impresiones.
            //activarItem(129,"S"); // 129 Comandas con impresora BT
            activarItem(133,"N"); // 133 Generar ordenes para despacho

            db.setTransactionSuccessful();
            db.endTransaction();

            doLogOff();

            toastcentlong("Modo de emergencia ACTIVADO");

        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());
        }

        validaModo();
    }

    private void desactivarSinRed() {

        clsClasses.clsP_paramext item = clsCls.new clsP_paramext();
        clsP_paramextObj P_paramextObj=new clsP_paramextObj(this,Con,db);

        try {

            db.beginTransaction();

            restaurarItem(131);
            restaurarItem(132);
            //restaurarItem(136);
            //restaurarItem(135);
            restaurarItem(137);
            restaurarItem(129);
            restaurarItem(133);

            db.execSQL("DELETE FROM P_modo_emergencia");

            db.setTransactionSuccessful();
            db.endTransaction();

            doLogOff();

            toastcentlong("Modo de emergencia desactivado");

        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());
        }

        validaModo();
    }

    private void activarItem(int id,String val) {
        clsClasses.clsP_paramext item = clsCls.new clsP_paramext();
        clsClasses.clsP_modo_emergencia egitem = clsCls.new clsP_modo_emergencia();
        String saveval="";

        P_paramextObj.fill("WHERE ID="+id);
        if (P_paramextObj.count>0) {
            item=P_paramextObj.first();
            saveval=item.valor;
            item.valor=val;
            P_paramextObj.update(item);
        } else {
            item.id=id;
            item.valor=val;item.nombre=" ";item.ruta="";
            P_paramextObj.add(item);
        }

        egitem.codigo_opcion=id;
        egitem.valor=saveval;
        P_modo_emergenciaObj.add(egitem);
    }

    private void restaurarItem(int id) {
        clsClasses.clsP_paramext item = clsCls.new clsP_paramext();
        String val;

        P_modo_emergenciaObj.fill("WHERE CODIGO_OPCION="+id);
        if (P_modo_emergenciaObj.count==0) return;
        val=P_modo_emergenciaObj.first().valor;

        P_paramextObj.fill("WHERE ID="+id);
        if (P_paramextObj.count==0) return;
        item=P_paramextObj.first();
        item.valor=val;
        P_paramextObj.update(item);
    }

    private void doLogOff() {
        app.logoutUser(du.getActDateTime());
        finish();
    }

    private void validaModo() {
        if (app.modoSinInternet()) {
            imgnowifi.setVisibility(View.VISIBLE);
        } else {
            imgnowifi.setVisibility(View.INVISIBLE);
        }
    }

    //endregion

	//region Aux

	public void CierreZ(){
		gl.cajaid=3;
		startActivity(new Intent(Menu.this, Caja.class));
	}

	public void VentaDate(){
		if (!validaVenta()) {
			//return;//Se valida si hay correlativos de factura para la venta
		}
		startActivity(new Intent(Menu.this,Venta.class));//#CKFK 20200518 Quité esto porque estaba en comentario
	}

	public boolean valida(){

	    //if (gl.rol==3 | gl.rol==4) return true;
        //if (gl.rol==4) return true;

		try {
			clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
			caja.fill();

            writeCorelLog(101,gl.cajaid,"valida gl.cajaid");

			if (gl.cajaid==1 || gl.cajaid==2){
				if(gl.cajaid==1){
					if(caja.count==0) return true;
					if(caja.last().estado==0) {
                        writeCorelLog(106,gl.cajaid,"valida caja.last().estado==0");
					    return false;
                    }
				}
				if(gl.cajaid==2){
					if(caja.count==0) {
                        writeCorelLog(107,gl.cajaid,"gl.cajaid==2 , caja.count==0");
                        return false;
                    }
					if(caja.last().estado==0) return true;
				}
			} else if (gl.cajaid==3 || gl.cajaid==5){
				if(caja.count==0) {
                    writeCorelLog(108,gl.cajaid,"gl.cajaid==3 || gl.cajaid==5,  caja.count==0");
                    if (gl.cajaid==3) gl.cajaid=0;
					return false;
				}
				if (caja.last().estado==1) {
                    writeCorelLog(109,gl.cajaid,"caja.last().estado==1");
                    return false;
				} else if(gl.cajaid==5) {
				    long fc=caja.last().fecha;long fa=du.getActDate();
                    writeCorelLog(104,gl.cajaid,"valida gl.cajaid==5");

                    if(fc!=fa){

                        writeCorelLog(105,0,"fc: "+fc+"  fa:"+fa);

                        gl.validDate=true;
                        gl.cajaid=6;
  						if (gl.lastDate==0){

							clsP_cajahoraObj caja_hora = new clsP_cajahoraObj(this,Con,db);
							//#EJC202211240844: Obtener la fecha con hora a partir de la que se empezó a facturar.
							caja_hora.fill("order by corel desc");

							if (caja_hora!=null){
								if (caja_hora.count>1){
									gl.lastDate=caja_hora.items.get(1).fechaini;
									gl.cajaid=6;
									return false;
								}
							}

							gl.lastDate=caja.last().fecha;
						}
  						return false;
                    }
				}
			} else {
			    if(gl.cajaid==4) return false;
			}
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox("Ocurrió error (valida) "+e);
			return false;
		}

		return true;
	}

	private boolean validaVenta() {
		Cursor DT;
		int ci,cf,ca1,ca2;
		double dd;
		long fv,fa;

        //if (gl.rol==4) return true;

        try {

			sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                "WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0)";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();

			ca1=DT.getInt(1);
			ci=DT.getInt(2);
			cf=DT.getInt(3);

			if (ca1>=cf) {
				mu.msgbox("Se han terminado los correlativos de las facturas. No se puede continuar con la venta.");
				return false;
			}

			fa=du.getActDate();
			fv=DT.getLong(4);

			if (fa==fv) {
				msgbox("Último día de vigencia de autorización de las facturas.");
				toastlong("Último día de vigencia de autorización de las facturas.");
			}

			if (fa>fv) {
				mu.msgbox("Se ha acabado vigencia de autorización de las facturas. No se puede continuar con la venta.");
				return false;
			}

			dd=cf-ci;dd=0.90*dd;
			ca2=ci+((int) dd);
			if (ca1>ca2) porcentaje = true;

            if (DT!=null) DT.close();

		} catch (Exception e) {
			mu.msgbox("No esta definido correlativo de las facturas. No se puede continuar con la venta.\n"); //+e.getMessage());
			return false;
		}

		if (app.usaFEL()) {

            try {

                sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                        "WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=1) AND ACTIVA = 1 ";
                DT = Con.OpenDT(sql);

                DT.moveToFirst();

                ca1 = DT.getInt(1);
                ci = DT.getInt(2);
                cf = DT.getInt(3);

                if (ca1 >= cf) {
                    mu.msgbox("Se han terminado los correlativos de contingencias de las facturas. No se puede continuar con la venta.");
                    return false;
                }

                dd = cf - ci;
                dd = 0.75 * dd;
                ca2 = ci + ((int) dd);
                if (ca1 > ca2) porcentaje = true;

                if (DT != null) DT.close();

            } catch (Exception e) {
                mu.msgbox("No esta definido correlativo de contingencia para las facturas. No se puede continuar con la venta.\n"); //+e.getMessage());
                return false;
            }
        }

		if (gl.codigo_pais.equalsIgnoreCase("SV")) {
			return validaCorelSV();
		} else {
			return true;
		}
	}

	private boolean validaCorelSV() {
		if (validaCorelCredito()) {
			return validaCorelTicket();
		} else return false;
	}

	private boolean validaCorelCredito() {
		Cursor DT;
		int ci,cf,ca1,ca2;
		double dd;
		long fv,fa;

		//if (gl.rol==4) return true;

		try {

			sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
				"WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=2)";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();

			ca1=DT.getInt(1);
			ci=DT.getInt(2);
			cf=DT.getInt(3);

			if (ca1>=cf) {
				mu.msgbox("Se han terminado los correlativos de los creditos fiscales. No se puede continuar con la venta.");
				return false;
			}

			fa=du.getActDate();
			fv=DT.getLong(4);

			if (fa==fv) {
				msgbox("Último día de vigencia de autorización de los creditos fiscales.");
				toastlong("Último día de vigencia de autorización de los creditos fiscales.");
			}

			if (fa>fv) {
				mu.msgbox("Se ha acabado vigencia de autorización de los creditos fiscales. No se puede continuar con la venta.");
				return false;
			}

			dd=cf-ci;dd=0.90*dd;
			ca2=ci+((int) dd);
			if (ca1>ca2) porcentaje = true;

			if (DT!=null) DT.close();

		} catch (Exception e) {
			mu.msgbox("No esta definido correlativo de los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
			return false;
		}

		if (app.usaFEL()) {

			try {

				sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
						"WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=3) AND ACTIVA = 1 ";
				DT = Con.OpenDT(sql);

				DT.moveToFirst();

				ca1 = DT.getInt(1);
				ci = DT.getInt(2);
				cf = DT.getInt(3);

				if (ca1 >= cf) {
					mu.msgbox("Se han terminado los correlativos de contingencias de los creditos fiscales. No se puede continuar con la venta.");
					return false;
				}

				dd = cf - ci;
				dd = 0.75 * dd;
				ca2 = ci + ((int) dd);
				if (ca1 > ca2) porcentaje = true;

				if (DT != null) DT.close();

			} catch (Exception e) {
				mu.msgbox("No esta definido correlativo de contingencia para los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
				return false;
			}
		}

		return true;

	}

	private boolean validaCorelCredito2() {
		Cursor DT;
		int ci,cf,ca1,ca2;
		double dd;
		long fv,fa;

		//if (gl.rol==4) return true;

		try {

			sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
				"WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=2)";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();

			ca1=DT.getInt(1);
			ci=DT.getInt(2);
			cf=DT.getInt(3);

			if (ca1>=cf) {
				mu.msgbox("Se han terminado los correlativos de los creditos fiscales. No se puede continuar con la venta.");
				return false;
			}

			fa=du.getActDate();
			fv=DT.getLong(4);

			if (fa==fv) {
				msgbox("Último día de vigencia de autorización de los creditos fiscales.");
				toastlong("Último día de vigencia de autorización de los creditos fiscales.");
			}

			if (fa>fv) {
				mu.msgbox("Se ha acabado vigencia de autorización de los creditos fiscales. No se puede continuar con la venta.");
				return false;
			}

			dd=cf-ci;dd=0.90*dd;
			ca2=ci+((int) dd);
			if (ca1>ca2) porcentaje = true;

			if (DT!=null) DT.close();

		} catch (Exception e) {
			mu.msgbox("No esta definido correlativo de los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
			return false;
		}

		if (app.usaFEL()) {

			try {

				sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
						"WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=3) AND ACTIVA = 1 ";
				DT = Con.OpenDT(sql);

				DT.moveToFirst();

				ca1 = DT.getInt(1);
				ci = DT.getInt(2);
				cf = DT.getInt(3);

				if (ca1 >= cf) {
					mu.msgbox("Se han terminado los correlativos de contingencias de los creditos fiscales. No se puede continuar con la venta.");
					return false;
				}

				dd = cf - ci;
				dd = 0.75 * dd;
				ca2 = ci + ((int) dd);
				if (ca1 > ca2) porcentaje = true;

				if (DT != null) DT.close();

			} catch (Exception e) {
				mu.msgbox("No esta definido correlativo de contingencia para los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
				return false;
			}
		}

		return true;
	}

	private boolean validaCorelTicket() {
		Cursor DT;
		int ci,cf,ca1,ca2;
		double dd;
		long fv,fa;

		//if (gl.rol==4) return true;

		try {

			sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
					"WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=4)";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();

			ca1=DT.getInt(1);
			ci=DT.getInt(2);
			cf=DT.getInt(3);

			if (ca1>=cf) {
				mu.msgbox("Se han terminado los correlativos de los tickets. No se puede continuar con la venta.");
				return false;
			}

			fa=du.getActDate();
			fv=DT.getLong(4);

			if (fa==fv) {
				msgbox("Último día de vigencia de autorización de los tickets.");
				toastlong("Último día de vigencia de autorización de los tickets.");
			}

			if (fa>fv) {
				mu.msgbox("Se ha acabado vigencia de autorización de los tickets. No se puede continuar con la venta.");
				return false;
			}

			dd=cf-ci;dd=0.90*dd;
			ca2=ci+((int) dd);
			if (ca1>ca2) porcentaje = true;

			if (DT!=null) DT.close();

		} catch (Exception e) {
			mu.msgbox("No esta definido correlativo de los tickets. No se puede continuar con la venta.\n"); //+e.getMessage());
			return false;
		}

		if (app.usaFEL()) {

			try {

				sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
						"WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=5) AND ACTIVA = 1 ";
				DT = Con.OpenDT(sql);

				DT.moveToFirst();

				ca1 = DT.getInt(1);
				ci = DT.getInt(2);
				cf = DT.getInt(3);

				if (ca1 >= cf) {
					mu.msgbox("Se han terminado los correlativos de contingencias de los tickets. No se puede continuar con la venta.");
					return false;
				}

				dd = cf - ci;
				dd = 0.75 * dd;
				ca2 = ci + ((int) dd);
				if (ca1 > ca2) porcentaje = true;

				if (DT != null) DT.close();

			} catch (Exception e) {
				mu.msgbox("No esta definido correlativo de contingencia para los tickets. No se puede continuar con la venta.\n"); //+e.getMessage());
				return false;
			}
		}

		return true;
	}

	private boolean cajaCerrada(){
		boolean resultado=false;

		try{
			gl.cajaid=5;

			gl.cajaid=5;gl.cajaid=5;gl.cajaid=5;gl.cajaid=5;

			if(!valida()){
				if (gl.cajaid==5){
					msgAskIniciarCaja("Caja cerrada. ¿Inicializar?");
				}
			}
		} catch (Exception ex){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),ex.getMessage(),"");
			msgbox("Ocurrió error (valida) "+ex);
		}

		return resultado;
	}

	private void setPrintWidth() {

		Cursor DT;
		int prwd=32;
		
		try {
			sql="SELECT COL_IMP FROM P_EMPRESA";
			DT=Con.OpenDT(sql);
			DT.moveToFirst();
			prwd=DT.getInt(0);

            if (DT!=null) DT.close();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			prwd=32;
		}
		
		gl.prw=prwd;
		
	}

	private int getPrinterType() {

		Cursor DT;
		String prtipo;
		int prid=0;

		try {

			sql="SELECT TIPO_IMPRESORA FROM P_ARCHIVOCONF";
			DT=Con.OpenDT(sql);
			DT.moveToFirst();
			
			prtipo=DT.getString(0);
				
			if (prtipo.equalsIgnoreCase("DATAMAX")) prid=1;
			if (prtipo.equalsIgnoreCase("EPSON")) prid=2;

            if (DT!=null) DT.close();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			prid=0;
	    }
		
		return prid;
				
	}

	public void doWSTest(View view) {
		startActivity(new Intent(Menu.this,WSTest.class));
	}

	private void addMenuItem(int mid,String text) {
        clsMenu item = clsCls.new clsMenu();
        item.ID=mid;
        item.Name=text;
        item.Icon=mid;
        items.add(item);
    }

    private boolean impresoraInstalada() {

        app.loadPrintConfig();

        if (gl.prtipo.isEmpty() | gl.prtipo.equalsIgnoreCase("SIN IMPRESORA")) {
            return true;
        }

        if (gl.prtipo.equalsIgnoreCase("EPSON TM BlueTooth")) {
            try {
                if (isPackageInstalled("com.dts.epsonprint")) {
                    return true;
                } else {
                    gl.prndrvmsg="El controlador de Epson TM BT no está instalado\nNo se va a poder imprimir.";
                    return false;
                }
            } catch (Exception e) {
                msgbox("impresoraInstalada : "+e.getMessage());
            }
        }

        if (gl.prtipo.equalsIgnoreCase("HP Engage USB")) {
            try {
                if (isPackageInstalled("com.hp.retail.test")) {
                    return true;
                } else {
                    gl.prndrvmsg="El controlador de HP Engage USB no está instalado\nNo se va a poder imprimir.";
                    return false;
                }
            } catch (Exception e) {
                msgbox("impresoraInstalada : "+e.getMessage());
            }
        }

        return true;
    }

    private boolean isPackageInstalled(String packageName) {
         try {
            PackageManager packageManager= this.getPackageManager();
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean validaFacturas() {

        long fi,ff;

        if (!app.usaFEL()) return true;

        try {

            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.addDays(du.getActDate(),-5);fi=du.ffecha00(fi);
            ff=du.addDays(ff,-3);ff=du.ffecha00(ff);

            sql="WHERE (ANULADO=0) AND (FECHA>="+fi+") AND (FECHA<="+ff+") AND (FEELUUID=' ')";

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill(sql);
            int fc=D_facturaObj.count;

            if (fc==0) {
                return true;
            } else {
				if (gl.emp!=7) {
					msgAskSend("Existen facturas pendientes de certificacion con más de 3 días. Por favor envie siguente correo al soporte.");
					return false;
				} else return true;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

    }

    private void enviaAvizo() {

        String subject,body;
        String dir=Environment.getExternalStorageDirectory()+"";

        try {
            subject="Facturas pendientes de certificacion : Ruta ID - "+gl.codigo_ruta;
            body="Estimado usuario,\n\nMPos reporta facturas pendientes de certificaciones de mas de 3 dias.\n" +
                    "Por favor comuniquese con el soporte para solucionar el problema.\n" +
                    "En el caso de que una factura supere 4 días sin certificacion la aplicacion no permite vender.\n\n" +
                    "Saludos\nDT Solutions S.A.\n";

            Uri uri=null;
            try {
                File f1 = new File(dir + "/posdts.db");
                File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
                File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
                FileUtils.copyFile(f1, f2);
                uri = Uri.fromFile(f3);

                app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            } catch (IOException e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return;
            }

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            String cor=P_sucursalObj.first().correo;if (cor.indexOf("@")<2) cor="";

            String[] TO = {"dtsolutionsgt@gmail.com"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"dtsolutionsgt@gmail.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            if (!cor.isEmpty()) emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void enviaImpresion() {

        String subject,body;
        String dir=Environment.getExternalStorageDirectory()+"";

        try {
            subject="Cierre del dia : Ruta ID - "+gl.codigo_ruta;
            body="Adjunto cierre del dia";

            Uri uri=null;
            try {
                File f1 = new File(dir + "/print.txt");
                uri = Uri.fromFile(f1);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            String cor=P_sucursalObj.first().correo;if (cor.indexOf("@")<2) cor="";

            String[] TO = {"dtsolutionsgt@gmail.com"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"dtsolutionsgt@gmail.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            if (!cor.isEmpty()) emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void imprimirCierre(boolean envio) {

        String fname = Environment.getExternalStorageDirectory()+"/print.txt";

        try {
            FileWriter wfile=new FileWriter(fname,false);
            BufferedWriter writer = new BufferedWriter(wfile);

			clsD_cierreObj D_cierreObj=new clsD_cierreObj(this,Con,db);
			D_cierreObj.fill("WHERE (CIERRE="+idcierre+") ORDER BY ID");

			if (D_cierreObj.count==0) {
				msgbox("No existe respaldo del ultimo cierre");return;
			}

			writer.write("   ");writer.write("\r\n");
			writer.write(" REIMPRESION CIERRE DIA  ");writer.write("\r\n");
			writer.write("   ");writer.write("\r\n");

            for (int i = 0; i < D_cierreObj.count; i++) {
                writer.write(D_cierreObj.items.get(i).text);writer.write("\r\n");
            }

            writer.close();

            if (envio) {
                enviaImpresion();
            } else {
                app.doPrint();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

	private void imprimirCierreOrig(boolean envio) {

		String fname = Environment.getExternalStorageDirectory()+"/print.txt";

		try {
			FileWriter wfile=new FileWriter(fname,false);
			BufferedWriter writer = new BufferedWriter(wfile);

			clsT_cierreObj T_cierreObj=new clsT_cierreObj(this,Con,db);
			T_cierreObj.fill("ORDER BY ID");

			if (T_cierreObj.items.size()==0) {
				msgbox("No existe respaldo del ultimo cierre");return;
			}

			writer.write("   ");writer.write("\r\n");
			writer.write(" REIMPRESION CIERRE DIA  ");writer.write("\r\n");
			writer.write("   ");writer.write("\r\n");

			for (int i = 0; i < T_cierreObj.items.size(); i++) {
				writer.write(T_cierreObj.items.get(i).texto);writer.write("\r\n");
			}

			writer.close();

			if (envio) {
				enviaImpresion();
			} else {
				app.doPrint();
			}
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void writeCorelLog(int id,int corel,String text) {
        String ss;
        try {
            ss="INSERT INTO T_BARRA_BONIF VALUES ('"+du.getActDateTime()+"','"+id+"',"+corel+",0,0,'"+text+"')";
            db.execSQL(ss);
        } catch (Exception e) {
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

    public void autoLoginMesero() {
		/*
        gl.after_login=false;
        menuid=1;
        toast("Validando ingreso . . .");
        showMenuItem();

		 */
    }

    public boolean tieneAlmacenes() {

        idalmdpred=0;gl.idalmpred =0;

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1");

            if (P_almacenObj.count<2) return false;

            idalmdpred=P_almacenObj.first().codigo_almacen;gl.idalmpred =idalmdpred;

            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) {
                idalmdpred=P_almacenObj.first().codigo_almacen;
                gl.idalmpred =idalmdpred;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskValid(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setNeutralButton("OK", (dialog1, which) -> {});
        dialog.show();
    }

    private void msgAskIniciarCaja(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
		dialog.setPositiveButton("Si", (dialog12, which) -> {

			if(gl.cajaid==5){

				gl.cajaid=1;

				if (valida()){

					if (gl.cajaid!=2){

						gl.inicio_caja_correcto =false;

						startActivity(new Intent(Menu.this,Caja.class));

					}

				}
			}
		});

        dialog.setNegativeButton("No", (dialog1, which) -> {});

        dialog.show();

    }

    private void msgAskValidUltZ(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Continuar Venta", (dialog12, which) -> VentaDate());
        dialog.setNegativeButton("Realizar Cierre Z", (dialog1, which) -> CierreZ());
        dialog.show();

    }

    private void msgAskValidaCierre(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Si", (dialog1, which) -> CierreZ());
        dialog.setNegativeButton("No", (dialog12, which) -> {
		});
        dialog.show();
    }

    private void msgAskImpresora() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(gl.prndrvmsg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Continuar Venta", (dialog1, which) -> startActivity(new Intent(Menu.this, Venta.class)));
        dialog.setNegativeButton("Salir", (dialog12, which) -> {});
        dialog.show();
    }

    private void msgAskDatabase(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Si", (dialog1, which) -> sendDB());
        dialog.setNegativeButton("No", (dialog12, which) -> {});
        dialog.show();
    }

    private void msgAskActualizar(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Si", (dialog1, which) -> actualizarCont());
        dialog.setNegativeButton("No", (dialog12, which) -> {});
        dialog.show();
    }

    private void msgAskActualizar2(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Si", (dialog12, which) -> {
			if (isNetworkAvailable()) {
				gl.recibir_automatico = true;
				startActivity(new Intent(Menu.this,WSRec.class));
			} else {
				toast("No hay conexión a internet");
			}
		});

        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void msgAskSend(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setNeutralButton("OK", (dialog1, which) -> {
			showMenuItem();
			enviaAvizo();
		});
        dialog.show();
    }

    private void msgAskUltimoCierre() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Cierre");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Imprimir", (dialog1, which) -> imprimirCierre(false));
        dialog.setNeutralButton("Enviar", (dialog12, which) -> imprimirCierre(true));
        dialog.setNegativeButton("Salir", (dialog13, which) -> {});
        dialog.show();
    }

    private void msgAskImprimir() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Imprimir documento");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Imprimir", (dialog1, which) -> app.doPrint(1));
        dialog.setNegativeButton("Salir", (dialog12, which) -> {});
        dialog.show();

    }

    private void msgAskCF() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Corregir consumidor final");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Corregir", (dialog12, which) -> {
			try {
				String ccf=gl.emp+"0";
				sql="UPDATE P_CLIENTE SET NOMBRE='Consumidor final',NIT='C.F.' WHERE CODIGO_CLIENTE="+ccf;
				db.execSQL(sql);
			} catch (Exception e) {
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			}

		});

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});
        dialog.show();

    }

    public void msgAskCorregirFechas() {

		ExDialog dialog = new ExDialog(this);
		dialog.setMessage("¿Corregir  fechas erroneas por la fecha actual?");
		dialog.setCancelable(false);

		dialog.setPositiveButton("Corregir", (dialog12, which) -> {

			Long fActual;
			int ccorel;

			try {

				fActual = du.getFechaActual();
				sql="UPDATE P_CAJACIERRE SET FECHA="+fActual+" WHERE FECHA<=0";
				db.execSQL(sql);

				sql="UPDATE D_FACTURA SET FECHA=2201010000 WHERE FECHA>6900000000";
				db.execSQL(sql);

				fActual = du.getActDateTime();

				sql="UPDATE D_FACTURA SET FECHA="+fActual+" WHERE FECHA<=0";
				db.execSQL(sql);

				sql="UPDATE D_FACTURA SET FEELFECHAPROCESADO="+fActual+" WHERE FEELFECHAPROCESADO<=0";
				db.execSQL(sql);

			} catch (Exception e) {
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			}

		});

		dialog.setNegativeButton("Salir", (dialog1, which) -> {});

		dialog.show();
	}

    private void msgAskEmerg(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Continuar", (dialog1, which) -> msgAskEmerg2());
        dialog.setNegativeButton("Salir", (dialog12, which) -> {});
        dialog.show();

    }

    private void msgAskEmerg2() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Está seguro?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Confirmar", (dialog1, which) -> {
			if (modo_emerg) activarSinRed();else desactivarSinRed();
		});
        dialog.setNegativeButton("Salir", (dialog12, which) -> {});
        dialog.show();
    }

	private void msgAskFEL(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage("¿" + msg + "?");

		dialog.setPositiveButton("Si", (dialog1, which) -> {
			try {
				gl.felcorel="";gl.feluuid="";

				if (gl.peFEL.equalsIgnoreCase(gl.felInfile)) {
					startActivity(new Intent(Menu.this, FELVerificacion.class));
				} else if (gl.peFEL.equalsIgnoreCase(gl.felSal)) {
					startActivity(new Intent(Menu.this, FELVerificacion.class));
				}
			} catch (Exception e) {
				msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			}
		});

		dialog.setNegativeButton("No", (dialog12, which) -> {});

		dialog.show();

	}

	private void msgAskBorrarInventario(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("Si", (dialog1, which) -> msgAskBorrarInventario2("¿Está seguro?"));
		dialog.setNegativeButton("No", (dialog12, which) -> {});
		dialog.show();
	}

	private void msgAskBorrarInventario2(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("Si", (dialog1, which) -> borraInventario());
		dialog.setNegativeButton("No", (dialog12, which) -> {});
		dialog.show();
	}

	private void msgAskInvInic(String msg) {
		ExDialog dialog = new ExDialog(this);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		dialog.setPositiveButton("Si", (dialog1, which) -> consultaInvCentral(2));
		dialog.setNegativeButton("No", (dialog12, which) -> {});
		dialog.show();
	}


	//endregion

	//region Activity Events
	
	@Override
 	protected void onResume() {

		String ms="";

		try {
			super.onResume();

            P_paramextObj.reconnect(Con,db);
            P_modo_emergenciaObj.reconnect(Con,db);

			setPrintWidth();

			if(browse==1 && gl.inicio_caja_correcto && !gl.inicia_caja_primera_vez){
				gl.recibir_automatico = false;
				browse=0;
			}

 		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	@Override
	public void onBackPressed() {
		try{
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}
	//endregion

}