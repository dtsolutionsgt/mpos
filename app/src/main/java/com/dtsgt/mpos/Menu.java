package com.dtsgt.mpos;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_sucursalObj;;
import com.dtsgt.classes.clsT_cierreObj;
import com.dtsgt.ladapt.ListAdaptMenuGrid;
import com.dtsgt.mant.Lista;
import com.dtsgt.mant.MantConfig;
import com.dtsgt.mant.MantConfigRes;
import com.dtsgt.mant.MantCorel;
import com.dtsgt.mant.MantRepCierre;
import com.dtsgt.mant.MantRol;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Menu extends PBase {

	private GridView gridView;
	private RelativeLayout relbotpan;
	private TextView lblVendedor,lblRuta;
	
	private ArrayList<clsMenu> items= new ArrayList<clsMenu>();

	private ListAdaptMenuGrid adaptergrid;
	private ExDialog menudlg;
	
	private int selId,selIdx,menuid,iicon;
	private String rutatipo,sdoc;
	private boolean rutapos,horizpos,porcentaje;
	private boolean listo=true;
	private long contcorini,contcorult;
	
	private final int mRequestCode = 1001;
	private Exist Existencia = new Exist();

	@Override
	protected void onCreate(Bundle savedInstanceState) 	{

		try {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_menu);

			super.InitBase();
			addlog("Menu",""+du.getActDateTime(),gl.vend);

			gridView = (GridView) findViewById(R.id.gridView1);
			relbotpan = (RelativeLayout) findViewById(R.id.relbotpan);

			lblVendedor = (TextView) findViewById(R.id.lblVendedor);
			lblRuta = (TextView) findViewById(R.id.textView9);

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

			setHandlers();

			int ori=this.getResources().getConfiguration().orientation; // 1 - portrait , 2 - landscape
			horizpos=ori==2;

			if (horizpos) {
				gridView.setNumColumns(4);relbotpan.setVisibility(View.GONE);
			} else {
				gridView.setNumColumns(3);relbotpan.setVisibility(View.VISIBLE);
			}

			this.setTitle("ROAD");

			listItems();

			cajaCerrada();

		} catch (Exception e) {
			msgbox(e.getMessage());
		}

		//#CKFK20200524_FIX_BY_OPENDT Quité esta función porque la tabla P_CORREL_OTROS ya no existe
		//insertCorrel();
	}

	//region Events


    //endregion

	//region  Main
	
	public void showClients(View view) {
		try{
			Intent intent;
			intent = new Intent(this,Clientes.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void listItems() {

        clsMenu item;

        lblVendedor.setText(gl.vendnom);
        lblRuta.setText(gl.ruta);

        items.clear();selIdx=-1;

        try{

            if (gl.modoinicial) {

                addMenuItem(11, "Mantenimientos");
                addMenuItem(2, "Comunicación");
                addMenuItem(9, "Utilerias");
                addMenuItem(10, "Cambio usuario");

            } else {

                if (gl.peMCent) {

                    if (app.grant(1,gl.rol)) addMenuItem(1,"Venta");
                    if (app.grant(2,gl.rol)) addMenuItem(6,"Caja");
                    if (app.grant(3,gl.rol)) addMenuItem(3,"Reimpresión");
                    if (app.grant(4,gl.rol)) addMenuItem(7,"Inventario");
                    if (app.grant(5,gl.rol)) addMenuItem(2,"Comunicación");
                    if (app.grant(6,gl.rol)) addMenuItem(9,"Utilerias");
                    if (app.grant(7,gl.rol)) addMenuItem(11,"Mantenimientos");
                    if (app.grant(8,gl.rol)) addMenuItem(12,"Reportes");
                    if (app.grant(9,gl.rol)) addMenuItem(4,"Anulación");

                } else {

                    addMenuItem(1,"Venta");
                    addMenuItem(6,"Caja");
                    addMenuItem(3,"Reimpresión");
                    addMenuItem(7,"Inventario");
                    addMenuItem(2,"Comunicación");
                    if (gl.rol>1) addMenuItem(9,"Utilerias");
                    if (gl.rol>1) addMenuItem(11,"Mantenimientos");
                    if (gl.rol>1) addMenuItem(12,"Reportes");
                    addMenuItem(4,"Anulación");

                }

                addMenuItem(10,"Cambio usuario");
				//addMenuItem(13,"Apagar");

            }

			adaptergrid=new ListAdaptMenuGrid(this, items);
			gridView.setAdapter(adaptergrid);
			adaptergrid.setSelectedIndex(selIdx);

		}catch (Exception e){
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
		}catch (Exception ex)
		{ ex.printStackTrace(); }
	}
		
	public void setHandlers(){
	    try{
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

					clsMenu vItem = (clsMenu) adaptergrid.getItem(position);
					menuid=vItem.ID;

					adaptergrid.setSelectedIndex(position);

					if (menuid==1) {
                        if (validaFacturas()) showMenuItem();
                    } else {
                        showMenuItem();
                    }
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
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                listo=true;
            }
        };
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
						if (!validaVenta()) return;//Se valida si hay correlativos de factura para la venta
						gl.iniciaVenta=true;gl.exitflag=false;gl.forcedclose=false;
                        gl.preimpresion=false;

						if (impresoraInstalada()) {
                            startActivity(new Intent(this, Venta.class));
                        } else {
                            msgAskImpresora();
                        }
					} else {

                        writeCorelLog(103,gl.cajaid,"showMenuItem gl.cajaid");

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
				    showVoidMenuTodo();break;
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
				    if (gl.peMCent) {
                        if (app.grant(11,gl.rol)) {
                            showMantMenu();
                        } else {
                            if (app.grant(12,gl.rol)) showMantCliente();
                       }
                    } else {
                        if (gl.rol>2) showMantMenu(); else showMantCliente();
                    }
					break;
				case 12:
					showReportMenu();break;
				case 13:
					apagar();break;
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion

	//region Reimpresion
	
	public void showPrintMenuTodo() {

		try {

			final AlertDialog Dialog;
			final String[] selitems  = {(gl.peMFact?"Factura":"Ticket"),"Depósito","Pagos","Recarga","Devolución a bodega"};;

			menudlg = new ExDialog(this);


			menudlg.setItems(selitems , new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
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

					dialog.cancel();
				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();

			Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbutton.setTextColor(Color.WHITE);
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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
	
	public void showVoidMenuTodo() {
		try{
			final AlertDialog Dialog;
			//final String[] selitems = {"Factura","Pedido","Recibo","Deposito","Recarga","Devolución a bodega", "Nota crédito"};
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Depósito","Ingreso de mercancía","Ajuste de inventario"};


			menudlg = new ExDialog(this);

			menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
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
					dialog.cancel();
				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();

			Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbutton.setTextColor(Color.WHITE);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

        final AlertDialog Dialog;
        final String[] selitems = {"Envío de datos","Recepción de parámetros"};
        //final String[] selitems = {"Envío de datos","Recepción de parámetros","Envío de huellas"};

        ExDialog menudlg = new ExDialog(this);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
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

                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();
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

		try{
			final AlertDialog Dialog;
			final String[] selitems = {"Objetivos por producto","Objetivos por familia","Objetivo por ruta","Objetivo por cobro","Inventario bodega","Consulta de precios"};

			menudlg = new ExDialog(this);

			menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
						case 0:
							menuObjProd();break;
						case 1:
							menuObjFamilia();break;
						case 2:
							menuObjRuta();break;
						case 3:
							menuObjCobro();break;
						case 4:
							menuInvBod();dialog.cancel();break;
						case 5:
							menuPrecios();dialog.cancel();break;
					}

					//dialog.cancel();
				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

		try{
			final AlertDialog Dialog;
			int itemcnt=1,itempos=0;

			if (gl.peAceptarCarga) 	{
				itemcnt+=1;
			} else {
				if (gl.peStockItf) {
					if (gl.peModal.equalsIgnoreCase("TOL")) {
						itemcnt+=1;
					} else {
						itemcnt+=1;
					}
				} else {
					itemcnt+=2;
				}

			}
            itemcnt++;
			if (gl.peSolicInv) itemcnt++;

			itemcnt=3;
			final String[] selitems = new String[itemcnt];

			selitems[itempos]="Existencias";itempos++;
			selitems[itempos]="Ajuste de inventario";itempos++;
			selitems[itempos]="Ingreso de mercancía";itempos++;
            //selitems[itempos]="Inventario inicial";itempos++;

			menudlg = new ExDialog (this);

			menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					String mt=selitems[item];

					if (mt.equalsIgnoreCase("Existencias")) menuExist();
					if (mt.equalsIgnoreCase("Ajuste de inventario")) menuDevBod();
					if (mt.equalsIgnoreCase("Ingreso de mercancía")) menuRecarga();
                    if (mt.equalsIgnoreCase("Inventario inicial")) menuInvIni();

                    dialog.cancel();
				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();

			Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbutton.setTextColor(Color.WHITE);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void menuExist() {
		try{
			gl.tipo=0;
			Intent intent = new Intent(this,Exist.class);
			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void menuRecarga() {
		try{
            gl.invregular=true;
			gl.tipo = 0;
			Intent intent = new Intent(this,lista_ingreso_inventario.class);
			startActivity(intent);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void menuDevBod() {
		try {
			gl.tipo = 1;
			Intent intent = new Intent(this,lista_ingreso_inventario.class);
			startActivity(intent);
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

    private void menuInvIni() {
        try {
            gl.tipo = 2;
            Intent intent = new Intent(this,lista_ingreso_inventario.class);
            startActivity(intent);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

	//endregion

	//region Utilerias
	
	public void showInvMenuUtils() {
		try{
			final AlertDialog Dialog;
			final String[] selitems = {"Configuracion de impresora","Tablas","Correlativo CierreZ","Soporte","Serial del dipositivo","Impresión de barras", "Rating ROAD"};

			menudlg = new ExDialog (this);

			menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
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
                            startActivity(new Intent(Menu.this,MarcarFacturas.class));break;
                        case 5:
                            msgAskActualizar("Actualizar correlativos de contingencia");break;
                        case 6:
                            infoSystem();break;
                        case 7:
                            msgAskImprimir();break;
                        case 8:
                            msgAskCF();break;
					}

					dialog.cancel();
				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();

			Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbutton.setTextColor(Color.WHITE);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void configuracionImpresora() {
        startActivity(new Intent(Menu.this, UtilPrint.class));
        /*
        try {
            gl.climode=false;
            gl.listaedit=true;
            gl.mantid =30;

            try {
                gl.climode=false;
                if (gl.codigo_ruta>0) db.execSQL("DELETE FROM P_archivoconf WHERE RUTA<>'"+gl.codigo_ruta+"'");
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            startActivity(new Intent(Menu.this, Lista.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        */

    }

	private void actualizaVersion() {
        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.dts.mposupd");
            intent.putExtra("filename","mpos.apk");
            this.startActivity(intent);
        } catch (Exception e) {
            msgbox("No está instalada aplicación para actualización de versiónes, por favor informe soporte.");
        }

        /*
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        */
    }

	private void askCambUsuario() {
		try{
            ExDialog dialog = new ExDialog(this);
			dialog.setMessage("¿Cambiar usuario?");

			dialog.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				    app.logoutUser(du.getActDateTime());
					Menu.super.finish();
				}
			});

			dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
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

        try {
            File f1 = new File(dir + "/posdts.db");
            File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
            File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
            FileUtils.copyFile(f1, f2);
            Uri uri = Uri.fromFile(f3);

            app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

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

            /*
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:dtsolutionsgt@gmail.com"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Base de datos : "+gl.tiendanom+" caja : "+gl.codigo_ruta);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Adjunto base de datos");
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(emailIntent);
            */


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

			sd="Almacenamiento disponible  :  "+availStorage+" %   ,   "+mu.round(freeStorage/1024,1)+" / "+mu.round(totalStorage/1024,1)+" [ GB ]";
		} catch (Exception e) {
			msgbox(e.getMessage());
			sd="Almacenamiento disponible  :  -";
		}

		ss=sb+"\n"+sm+"\n"+sd;

        ExDialog dialog = new ExDialog(this);
		dialog.setMessage(ss);
		dialog.setCancelable(false);

		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
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
            //flim=2009260000;

            cact=contcorult;
            if (cact<contcorini) cact=contcorini;
            /*
            long cini=200000000+100000*gl.codigo_ruta;
            contcorini=cini;
            cact=cini;
             */

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


	//endregion

	//region Mantenimientos

	public void showMantMenu() {

	    gl.climode=false;
	    gl.listaedit=true;

        try {
            final AlertDialog Dialog;
            final String[] selitems;

            final String[] selitems1 = {"Banco", "Caja", "Cliente", "Empresa", "Familia",
					                   "Forma pago", "Impuesto", "Concepto pago", "Nivel precio",
					                   "Motivo ajuste","Producto", "Proveedor", "Tienda", "Usuario","Roles",
                                       "Impresora","Impresora marca","Impresora modelo",
					                   "Resolución de facturas", "Configuración","Configuración reportes Cierre"};

            final String[] selitems2 = {"Banco", "Caja", "Cliente", "Empresa", "Familia",
                    "Forma pago", "Impuesto", "Concepto pago", "Nivel precio",
                    "Motivo ajuste","Producto", "Proveedor", "Tienda", "Usuario","Roles",
                    "Impresora","Impresora marca","Impresora modelo",
                    "Resolución de facturas","Restaurante","Configuración","Configuración reportes Cierre"};

            if (gl.peRest) selitems=selitems2;else selitems=selitems1;

            menudlg = new ExDialog(this);

            menudlg.setItems(selitems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss = selitems[item];

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
                    } else {
                        startActivity(new Intent(Menu.this, Lista.class));
                    }

                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

            Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbuttonp.setTextColor(Color.WHITE);

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

	}

    public void showMantRestMenu() {

        try {
            final AlertDialog Dialog;

            final String[] selitems = {"Configuración", "Sala", "Mesa", "Grupo de mesas"};
            //final String[] selitems = { "Sala", "Mesa", "Grupo de mesas", "Diseño de sala"};

            menudlg = new ExDialog(this);

            menudlg.setItems(selitems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss = selitems[item];

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

                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

            Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbuttonp.setTextColor(Color.WHITE);

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    public void showMantCliente() {

        gl.climode=false;

        gl.listaedit=true;

        try{
            final AlertDialog Dialog;

            //final String[] selitems = {"Banco","Caja","Cliente","Combo","Combo Opción","Descuento","Empresa","Familia","Forma pago","Impuesto","Moneda","Nivel precio","Producto","Proveedor","Tienda","Usuario","Configuración"};
            //final String[] selitems = {"Banco","Caja","Cliente","Empresa","Familia","Forma pago","Impuesto","Nivel precio","Producto","Proveedor","Tienda","Usuario","Configuración","Formato de impresión"};
            final String[] selitems = {"Cliente","Proveedor","Familia","Producto"};

            menudlg = new ExDialog(this);
            menudlg.setTitle("Mantenimientos");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss=selitems[item];

                    if (ss.equalsIgnoreCase("Almacen")) gl.mantid=0;
                    if (ss.equalsIgnoreCase("Banco")) gl.mantid=1;
                    if (ss.equalsIgnoreCase("Caja")) gl.mantid=13;
                    if (ss.equalsIgnoreCase("Cliente")) gl.mantid=2;
                    if (ss.equalsIgnoreCase("Combo")) gl.mantid=17;
                    if (ss.equalsIgnoreCase("Combo Opción")) gl.mantid=18;
                    if (ss.equalsIgnoreCase("Descuento")) gl.mantid=15;
                    if (ss.equalsIgnoreCase("Empresa")) gl.mantid=3;
                    if (ss.equalsIgnoreCase("Familia")) gl.mantid=4;
                    if (ss.equalsIgnoreCase("Forma pago")) gl.mantid=5;
                    if (ss.equalsIgnoreCase("Impuesto")) gl.mantid=6;
                    if (ss.equalsIgnoreCase("Moneda")) gl.mantid=7;
                    if (ss.equalsIgnoreCase("Concepto Pago")) gl.mantid=19;
                    if (ss.equalsIgnoreCase("Nivel precio")) gl.mantid=14;
					if (ss.equalsIgnoreCase("Motivo ajuste")) gl.mantid = 22;
                    if (ss.equalsIgnoreCase("Producto")) gl.mantid=8;
                    if (ss.equalsIgnoreCase("Proveedor")) gl.mantid=9;
                    if (ss.equalsIgnoreCase("Tienda")) gl.mantid=12;
                    if (ss.equalsIgnoreCase("Usuario")) gl.mantid=11;
                    if (ss.equalsIgnoreCase("Correlativos")) gl.mantid=20;
                    if (ss.equalsIgnoreCase("Configuración")) gl.mantid=16;
                    if (ss.equalsIgnoreCase("Formato de impresión")) gl.mantid=17;


                    if (gl.mantid==16) {
                        startActivity(new Intent(Menu.this, MantConfig.class));
                    } else if (gl.mantid==15) {
                        startActivity(new Intent(Menu.this, Lista.class));
                    } else {
                        startActivity(new Intent(Menu.this, Lista.class));
                    }
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

            Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbuttonp.setTextColor(Color.WHITE);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

	//endregion

    //region Reportes

	public void showReportMenu() {

		try{

			final AlertDialog Dialog;

			final String[] selitems = {"Reporte de Documentos por Día", "Reporte Venta por Día", "Reporte Venta por Producto",
                    "Reporte por Forma de Pago", "Reporte por Familia", "Reporte Ventas por Vendedor",
                    "Consumo materia prima","Reporte de Ventas por Cliente",
                    "Margen y Beneficio por Producto", "Margen y Beneficio por Familia", "Cierre X", "Cierre Z","Ultimo cierre"};

            menudlg = new ExDialog(this);
            menudlg.setTitle("Reportes");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss=selitems[item];

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
                    if (ss.equalsIgnoreCase("Ultimo cierre")) gl.reportid=12;
                    if (ss.equalsIgnoreCase("Consumo materia prima")) gl.reportid=13;
                    //if (ss.equalsIgnoreCase("Consumo materia prima por producto")) gl.reportid=14;

					gl.titReport = ss;

					if (gl.reportid == 9 || gl.reportid == 10) {
                        startActivity(new Intent(Menu.this, CierreX.class));
                    } else if (gl.reportid == 12) {
                        msgAskUltimoCierre();
					} else {
						startActivity(new Intent(Menu.this,Reportes.class));
					}

				}
			});

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

            Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbuttonp.setTextColor(Color.WHITE);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Caja

	public void showCajaMenu() {
		try{

			final AlertDialog Dialog;

			final String[] selitems = {"Inicio de Caja", "Pagos de Caja", "Depósitos","Cierre de Caja"};

			menudlg = new ExDialog(this);
			menudlg.setTitle("Caja");
			menudlg.setCancelable(false);

			menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					ss=selitems[item];

					if (ss.equalsIgnoreCase("Inicio de Caja")) gl.cajaid=1;
					if (ss.equalsIgnoreCase("Pagos de Caja")) gl.cajaid=2;
					if (ss.equalsIgnoreCase("Depósitos")) gl.cajaid=4;
					if (ss.equalsIgnoreCase("Cierre de Caja")) gl.cajaid=3;

					gl.titReport = ss;

					if(valida()){

						if(gl.cajaid!=2){
							gl.inicio_caja_correcto =false;
							browse=1;
							startActivity(new Intent(Menu.this,Caja.class));
						} else {
							startActivity(new Intent(Menu.this,CajaPagos.class));
						}

					} else {
						String txt="";

						if(gl.cajaid==0 || gl.cajaid==2) txt = "La caja no se ha abierto, si desea iniciar turno o realizar pagos debe realizar el inicio de caja.";
						if(gl.cajaid==1) txt = "La caja ya está abierta, si desea iniciar otro turno debe realizar el fin de caja.";
						//if(gl.cajaid==2) txt = "Pendiente implementación.";
						if(gl.cajaid==4) txt = "Pendiente implementación.";
						if(gl.cajaid==3) txt = "La caja está cerrada, si desea iniciar operaciones o realizar pagos debe realizar el inicio de caja.";
						msgAskValid(txt);
					}

				}
			});

			menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			Dialog = menudlg.create();
			Dialog.show();

			Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbutton.setTextColor(Color.WHITE);

			Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
			nbuttonp.setTextColor(Color.WHITE);
		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	//endregion

	//region Aux

	public void CierreZ(){
		gl.cajaid=3;
		startActivity(new Intent(Menu.this, Caja.class));
	}

	public void VentaDate(){
		if (!validaVenta()){
			return;//Se valida si hay correlativos de factura para la venta
		}
		startActivity(new Intent(Menu.this,Venta.class));//#CKFK 20200518 Quité esto porque estaba en comentario
	}

	public boolean valida(){
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
  						if (gl.lastDate==0) gl.lastDate=caja.last().fecha;
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
		long fecha_vigencia, diferencia;
		double dd;
		boolean resguardo=false;

		try {

			sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL ";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();

			ca1=DT.getInt(1);
			ci=DT.getInt(2);
			cf=DT.getInt(3);
			fecha_vigencia=DT.getLong(4);
			resguardo=DT.getInt(5)==1;

			if(resguardo==false){
				if(fecha_vigencia< du.getActDate()){
					//#HS_20181128_1556 Cambie el contenido del mensaje.
					mu.msgbox("La resolución esta vencida. No se puede continuar con la venta.");
					return false;
				}
			}

			if(resguardo==false){
				diferencia = fecha_vigencia - du.getActDate();
				if( diferencia <= 30){
					//#HS_20181128_1556 Cambie el contenido del mensaje.
					mu.msgbox("La resolución vence en "+diferencia+". No se puede continuar con la venta.");
					return false;
				}
			}

			if (ca1>=cf) {
				//#HS_20181128_1556 Cambie el contenido del mensaje.
				mu.msgbox("Se han terminado los correlativos de facturas. No se puede continuar con la venta.");
				return false;
			}

			dd=cf-ci;dd=0.75*dd;
			ca2=ci+((int) dd);

			if (ca1>ca2) {
				//toastcent("Queda menos que 25% de talonario de facturas.");
				//#HS_20181129_1040 agregue nuevo tipo de mensaje
				porcentaje = true;
			}


            if (DT!=null) DT.close();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n"); //+e.getMessage());
			return false;
		}

		return true;
	}

	private boolean cajaCerrada(){

		boolean resultado=false;

		try{

			gl.cajaid=5;

			if(!valida()){
				if (gl.cajaid==5){
					msgAskIniciarCaja("Caja cerrada. ¿Inicializar?");
				}
			}

		}catch (Exception ex){
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
            /*
            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.ffecha00(fi);
            ff=du.addDays(ff,-3);ff=du.ffecha24(ff);
            if (fi>ff) {
                fi=du.addDays(ff,-2);fi=du.ffecha00(fi);
            }
            */

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
                msgAskSend("Existen facturas pendientes de certificacion de mas que 3 días. Por favor envie siguente correo al soporte.");
                return false;
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

            String[] TO = {"jpospichal@dts.com.gt"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"jpospichal@dts.com.gt"};

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

            String[] TO = {"jpospichal@dts.com.gt"};if (!cor.isEmpty()) TO[0]=cor;
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

            clsT_cierreObj T_cierreObj=new clsT_cierreObj(this,Con,db);
            T_cierreObj.fill("ORDER BY ID");

            if (T_cierreObj.items.size()==0) {
                msgbox("No existe respaldo del ultimo cierre");return;
            }

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

    //endregion

    //region Dialogs

    private void msgAskValid(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskIniciarCaja(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if(gl.cajaid==5){

                    gl.cajaid=1;

                    if (valida()){

                        if (gl.cajaid!=2){

                            gl.inicio_caja_correcto =false;

                            startActivity(new Intent(Menu.this,Caja.class));

                        }

                    }
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskValidUltZ(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Continuar Venta", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                VentaDate();
            }
        });

        dialog.setNegativeButton("Realizar Cierre Z", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CierreZ();
            }
        });

        dialog.show();

    }

    private void msgAskValidaCierre(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                CierreZ();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    private void msgAskImpresora() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(gl.prndrvmsg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Continuar Venta", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Menu.this, Venta.class));
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskDatabase(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendDB();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskActualizar(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                actualizarCont();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskActualizar2(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (isNetworkAvailable()) {
                    gl.recibir_automatico = true;
                    startActivity(new Intent(Menu.this,WSRec.class));
                } else {
                    toast("No hay conexión a internet");
                }
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
                showMenuItem();
                enviaAvizo();
            }
        });

        dialog.show();

    }

    private void msgAskUltimoCierre() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Ultimo cierre");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imprimirCierre(false);
            }
        });

        dialog.setNeutralButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imprimirCierre(true);
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskImprimir() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Imprimir documento");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                app.doPrint(1);
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskCF() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Corregir consumidor final");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Corregir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String ccf=gl.emp+"0";
                    sql="UPDATE P_CLIENTE SET NOMBRE='Consumidor final',NIT='C.F.' WHERE CODIGO_CLIENTE="+ccf;
                    db.execSQL(sql);
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }

            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

	//region Activity Events
	
	@Override
 	protected void onResume() {

		String ms="";

		try{
			super.onResume();

			setPrintWidth();

			if(browse==1 && gl.inicio_caja_correcto && !gl.inicia_caja_primera_vez){
				gl.recibir_automatico = false;
				/*if (gl.peCajaRec) {
					if (isNetworkAvailable()) {
						gl.recibir_automatico = true;
						startActivity(new Intent(Menu.this,WSRec.class));
					} else {
						if (!ms.isEmpty()) ms+="\n\n";
						ms+="No hay conexión a internet";
					}
				}*/

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
