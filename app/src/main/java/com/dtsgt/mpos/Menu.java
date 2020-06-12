package com.dtsgt.mpos;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.ladapt.ListAdaptMenuGrid;
import com.dtsgt.mant.Lista;
import com.dtsgt.mant.MantConfig;
import com.dtsgt.mant.MantCorel;
import com.dtsgt.mant.MantRol;

import java.io.File;
import java.util.ArrayList;

public class Menu extends PBase {

	private GridView gridView;
	private RelativeLayout relbotpan;
	private TextView lblVendedor,lblRuta;
	
	private ArrayList<clsMenu> items= new ArrayList<clsMenu>();

	//#HS_20181207 Controles para seleccionar el ayudante y vehiculo.
    //#CKFK 20200525 Puse esto en comentario lo del vehículo y el vendedor porque en MPos no se utiliza
	/*private Spinner Ayudante,Vehiculo;
	private TextView lblAyudante,lblVehiculo;
	private ArrayList<String> listIDAyudante = new ArrayList<String>();
	private ArrayList<String> listAyudante = new ArrayList<String>();
	private ArrayList<String> listIDVehiculo = new ArrayList<String>();
	private ArrayList<String> listVehiculo = new ArrayList<String>();*/

	private ListAdaptMenuGrid adaptergrid;
	private AlertDialog.Builder menudlg;
	
	private int selId,selIdx,menuid,iicon;
	private String rutatipo,sdoc;
	private boolean rutapos,horizpos,porcentaje;
	// JP 20200527
	private boolean listo=true;
	
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

			//#HS_20181206_0945 Agregue lblVendedor que se muestra en el menú.
			lblVendedor = (TextView) findViewById(R.id.lblVendedor);
			lblRuta = (TextView) findViewById(R.id.textView9);

            //#CKFK 20200525 Puse esto en comentario lo del vehículo y el vendedor porque en MPos no se utiliza
            /*Ayudante = new Spinner(this);
			Vehiculo = new Spinner(this);
			lblAyudante = new TextView(this);
			lblAyudante.setText("Ayudante:");
			lblVehiculo = new TextView(this);
			lblVehiculo.setText("Vehículo:");*/
			///

			gl.validDate=false;
			gl.lastDate=0;

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

			//#CKFK 20200525  Puse esto en comentario porque en el MPos no se utiliza el ayudante ni el vehículo
			/*if (gl.peVehAyud) {
				AyudanteVehiculo();
			} else {
				gl.ayudanteID="";gl.vehiculoID="";
			}*/

		} catch (Exception e) {
			msgbox(e.getMessage());
		}

		//#CKFK20200524_FIX_BY_OPENDT Quité esta función porque la tabla P_CORREL_OTROS ya no existe
		//insertCorrel();
	}

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

	//#CKFK20200524_FIX_BY_OPENDT Quité la función InsertCorrel porque la tabla P_CORREL_OTROS ya no se utiliza

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

                if (gl.grantaccess) {

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
                    if (gl.rol>1) addMenuItem(4,"Anulación");

                }

                addMenuItem(10,"Cambio usuario");

            }

			adaptergrid=new ListAdaptMenuGrid(this, items);
			gridView.setAdapter(adaptergrid);
			adaptergrid.setSelectedIndex(selIdx);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}
		
	public void setHandlers(){
	    try{
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

					clsMenu vItem = (clsMenu) adaptergrid.getItem(position);
					menuid=vItem.ID;

					adaptergrid.setSelectedIndex(position);

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

					if(valida()){

					    gl.nivel_sucursal=app.nivelSucursal();
						gl.cliente="C.F.";
						gl.fnombre="Consumidor final";
						gl.fnit="C.F.";
						gl.fdir="Ciudad";

                        gl.cliposflag=false;
						gl.rutatipo="V";gl.rutatipog="V";
						if (!validaVenta()) return;//Se valida si hay correlativos de factura para la venta

						gl.iniciaVenta=true;
						gl.exitflag=false;
						gl.forcedclose=false;
						startActivity(new Intent(this, Venta.class));
					}else {
						if(gl.cajaid==5) msgAskIniciarCaja("La caja está cerrada. ¿Quiere realizar el inicio de caja?");
						//msgAskValid("La caja está cerrada, si desea iniciar operaciones debe realizar el inicio de caja");

						//#CKFK 20200521 Se modificó lo del cierre a través de un parámetro, si se utiliza FEL es obligatorio hacer el cierre de caja diario
						if (gl.cierreDiario){

							if(gl.cajaid==6) msgAskValidaCierre("No realizó el cierre de caja del día " + du.sfecha(gl.lastDate) + ". ¿Realizar cierre Z?");

						}else{

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
					}else {
						showConsMenu();
					}
					break;

				case 6:  // Deposito

					//getDepTipo();

					//#HS_20181206 Verifica el usuario si es DTS.
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					}else {
						showCajaMenu();
					}

					break;

				case 7:  // Inventario

					//#HS_20181206 Verifica el usuario si es DTS.
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					}else {
						showInvMenuVenta();
					}

					break;

				case 8:  // Fin Dia

					//#HS_20181206 Verifica el usuario si es DTS.
					if(gl.vendnom.equalsIgnoreCase("DTS") && gl.vend.equalsIgnoreCase("DTS")) {
						mu.msgbox("No puede realizar esta acción");
					}else {
						Intent intent8 = new Intent(this, FinDia.class);
						startActivity(intent8);
					}

					break;

				case 9:  // Utilerias
					showInvMenuUtils();
					break;

				case 10:  // Cambio usuario
					askCambUsuario();
					break;

				case 11:
				    if (gl.grantaccess) {
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
					showReportMenu();
					break;

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

			menudlg = new AlertDialog.Builder(this);
			menudlg.setIcon(R.drawable.reimpresion48);
			menudlg.setTitle("Reimpresión");

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
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Depósito","Recarga","Devolución a bodega"};


			menudlg = new AlertDialog.Builder(this);
			menudlg.setIcon(R.drawable.anulacion48);
			menudlg.setTitle("Anulación");

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

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Comunicación");

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

			menudlg = new AlertDialog.Builder(this);
			menudlg.setTitle("Consultas");

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

			final String[] selitems = new String[itemcnt];

			selitems[itempos]="Existencias";itempos++;
			selitems[itempos]="Devolución a bodega";itempos++;
			selitems[itempos]="Ingreso de mercancía";itempos++;
            selitems[itempos]="Inventario inicial";itempos++;

			menudlg = new AlertDialog.Builder(this);
			menudlg.setIcon(R.drawable.inventario48);
			menudlg.setTitle("Inventario");

			menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					String mt=selitems[item];

					if (mt.equalsIgnoreCase("Existencias")) menuExist();
					if (mt.equalsIgnoreCase("Devolución a bodega")) menuDevBod();
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
            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            gl.codigo_proveedor=suc.first().codigo_proveedor;
            gl.nombre_proveedor="Inventario inicial";

            gl.invregular=false;
            startActivity(new Intent(Menu.this,InvInicial.class));

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }


	//endregion

	//region Utilerias
	
	public void showInvMenuUtils() {
		try{
			final AlertDialog Dialog;
			//final String[] selitems = {"Configuracion de impresora","Tablas","Correlativo CierreZ","Soporte","Serial del dipositivo","Impresión de barras", "Rating ROAD"};
			final String[] selitems = {"Configuracion de impresora","Tablas","Información de sistema"};

			menudlg = new AlertDialog.Builder(this);
			menudlg.setIcon(R.drawable.utils48);
			menudlg.setTitle("Utilerias");

			menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					switch (item) {
						case 0:
                            startActivity(new Intent(Menu.this,UtilPrint.class));break;
						case 1:
							startActivity(new Intent(Menu.this,Tablas.class));break;
						case 2:
							infoSystem();break;
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

	private void menuCorelZ() {
		Cursor DT;
		int coract;

		try{
			try {
				sql="SELECT Corel FROM FinDia";
				DT=Con.OpenDT(sql);
				DT.moveToFirst();
				coract=DT.getInt(0);
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
				mu.msgbox(e.getMessage());
				coract=0;
			}

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Nuevo correlativo");
			alert.setMessage("Actual : "+coract);

			final EditText input = new EditText(this);
			alert.setView(input);

			input.setInputType(InputType.TYPE_CLASS_NUMBER );
			input.setText("");
			input.requestFocus();

			alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						String s=input.getText().toString();
						int icor=Integer.parseInt(s);

						if (icor<0) throw new Exception();
						askApplyCor(icor);
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox("Correlativo incorrecto");
						return;
					}
				}
			});

			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});

			alert.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}


		
	}
	
	private void askApplyCor(int ncor) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage("Aplicar nuevo correlativo ?");

			final int fncor=ncor;

			dialog.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						sql="UPDATE FinDia SET Corel="+fncor;
						db.execSQL(sql);
					} catch (SQLException e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
						mu.msgbox("Error : " + e.getMessage());
					}
				}
			});

			dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

	}

	private void askCambUsuario() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage("¿Cambiar usuario?");

			dialog.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
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

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Información de sistema");
		dialog.setMessage(ss);
		dialog.setCancelable(false);

		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialog.show();

	}

	//endregion

	//region Mantenimientos

	public void showMantMenu() {

	    gl.climode=false;

	    gl.listaedit=true;

        try {
            final AlertDialog Dialog;

            final String[] selitems = {"Banco", "Caja", "Cliente", "Empresa", "Familia", "Forma pago", "Impuesto", "Concepto pago", "Nivel precio", "Producto", "Proveedor", "Tienda", "Usuario","Roles", "Resolución de facturas", "Configuración"};

            menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Mantenimientos");

            menudlg.setItems(selitems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss = selitems[item];

                    if (ss.equalsIgnoreCase("Almacen")) gl.mantid = 0;
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
                    if (ss.equalsIgnoreCase("Producto")) gl.mantid = 8;
                    if (ss.equalsIgnoreCase("Proveedor")) gl.mantid = 9;
                    if (ss.equalsIgnoreCase("Tienda")) gl.mantid = 12;
                    if (ss.equalsIgnoreCase("Usuario")) gl.mantid = 11;
                    if (ss.equalsIgnoreCase("Resolución de facturas")) gl.mantid = 20;
                    if (ss.equalsIgnoreCase("Configuración")) gl.mantid = 16;
                    if (ss.equalsIgnoreCase("Formato de impresión")) gl.mantid = 17;
                    if (ss.equalsIgnoreCase("Roles")) gl.mantid = 21;

                    if (gl.mantid == 16) {
                        startActivity(new Intent(Menu.this, MantConfig.class));
                    } else if (gl.mantid == 15) {
                        startActivity(new Intent(Menu.this, Lista.class));
                    } else if (gl.mantid == 20) {
                        startActivity(new Intent(Menu.this, MantCorel.class));
                    } else if (gl.mantid == 21) {
                        startActivity(new Intent(Menu.this, MantRol.class));
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

            menudlg = new AlertDialog.Builder(this);
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

			final String[] selitems = {"Reporte de Documentos por Día", "Reporte Venta por Día", "Reporte Venta por Producto", "Reporte por Forma de Pago", "Reporte por Familia", "Reporte Ventas por Vendedor", "Reporte de Ventas por Cliente", "Margen y Beneficio por Productos", "Margen y Beneficio por Familia", "Cierre X", "Cierre Z"};

            menudlg = new AlertDialog.Builder(this);
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

					gl.titReport = ss;

					if (gl.reportid == 9 || gl.reportid == 10) {
						startActivity(new Intent(Menu.this,CierreX.class));
					}else{
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

			final String[] selitems = {"Inicio de Caja", "Pagos de Caja", "Depositos","Cierre de Caja"};

			menudlg = new AlertDialog.Builder(this);
			menudlg.setTitle("Caja");
			menudlg.setCancelable(false);

			menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					ss=selitems[item];

					if (ss.equalsIgnoreCase("Inicio de Caja")) gl.cajaid=1;
					if (ss.equalsIgnoreCase("Pagos de Caja")) gl.cajaid=2;
					if (ss.equalsIgnoreCase("Depositos")) gl.cajaid=4;
					if (ss.equalsIgnoreCase("Cierre de Caja")) gl.cajaid=3;

					gl.titReport = ss;

					if(valida()){

						if(gl.cajaid!=2){
							startActivity(new Intent(Menu.this,Caja.class));
						}else {
							startActivity(new Intent(Menu.this,CajaPagos.class));
						}

					}else {
						String txt="";

						if(gl.cajaid==0 || gl.cajaid==2)txt = "La caja no se ha abierto, si desea iniciar turno o realizar pagos debe realizar el inicio de caja.";
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

	//region Supervisor ayudante

	private void msgAskSupervisor1() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Road");
			dialog.setMessage("Este usuario es ADMINISTRADOR. ¿Está seguro de realizar la venta con este usuario?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					msgAskSupervisor2();
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

	private void msgAskSupervisor2() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Road");
			dialog.setMessage("Usted no debería realizar ventas con el Rol de ADMINISTRADOR. ¿Esta 100% seguro de realizar la venta con este usuario?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Menu.this, Clientes.class));
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

	//#HS_20181207 Mensaje que muestra los ayudantes y vehiculos disponibles.


	//#CKFK 20200525 Puse esto en comentario lo del vehículo y el vendedor porque en MPos no se utiliza
	/*private void AyudanteVehiculo() {

		try{

			inputAyudanteVehiculo();

			getlistAyudante();
			getlistVehiculo();
			getAyudante();
			getVehiculo();

            startActivity(new Intent(this,ayudante_vehiculo.class));

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    private void inputAyudanteVehiculo() {

		try{

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("AYUDANTE Y VEHÍCULO");

			final LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);

            //layout.addView(new TextView(this));
			layout.addView(lblAyudante);
			layout.addView(Ayudante);
			//layout.addView(new TextView(this));
			layout.addView(lblVehiculo);
			layout.addView(Vehiculo);

			alert.setView(layout);

			alert.setPositiveButton("Asignar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					getAyudanteVehiculo();
				}
			});

			alert.setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					gl.ayudante = "";gl.ayudanteID = "";
					gl.vehiculo = "";gl.vehiculoID= "";
					closekeyb();
				}
			});

			alert.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}*/

    //#CKFK 20200525 Puse esto en comentario lo del vehículo y el vendedor porque en MPos no se utiliza
    /*
	private void getAyudante(){

		try{
			Ayudante.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					TextView spinlabel;

					try {
						spinlabel=(TextView)parentView.getChildAt(0);
						spinlabel.setTextColor(Color.BLACK);
						spinlabel.setPadding(5, 0, 0, 0);
						spinlabel.setTextSize(18);

							gl.ayudanteID=listIDAyudante.get(position);
							gl.ayudante=listAyudante.get(position);

					} catch (Exception e) {
						mu.msgbox( e.getMessage());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					return;
				}

			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}



	}

	private void getVehiculo(){

		try{
			Vehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					TextView spinlabel;

					try {
						spinlabel=(TextView)parentView.getChildAt(0);
						spinlabel.setTextColor(Color.BLACK);
						spinlabel.setPadding(5, 0, 0, 0);
						spinlabel.setTextSize(18);

						gl.vehiculoID=listIDVehiculo.get(position);
						gl.vehiculo=listVehiculo.get(position);

					} catch (Exception e) {
						mu.msgbox( e.getMessage());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					return;
				}

			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}



	}
*/

	//#HS_20181207 Obtiene el ayudante y vehiculo asignado a la ruta.
	/*private void getAyudanteVehiculo(){

		try
		{
			if(gl.ayudanteID.equals("")){
				msgAskAyudante();
			}else if(gl.vehiculoID.equals("")){
				msgAskVehiculo();
			}else{
				mu.msgbox("Ayudante y Vehículo asignado a la ruta");
			}

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			//Log.d("getAyudanteVehiculo error: ", e.getMessage());
		}
	}

	private void msgAskAyudante() {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Road");
			dialog.setMessage("¿Continuar sin asignar un ayudante?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//inputAyudanteVehiculo();

				}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

	private void msgAskVehiculo() {

		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle("Road");
			dialog.setMessage("¿Continuar sin asignar un vehículo?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}*/

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
		try{

			clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

			caja.fill();

			if(gl.cajaid==1 || gl.cajaid==2){

				if(gl.cajaid==1){
					if(caja.count==0) return true;

					if(caja.last().estado==0){
						return false;
					}
				}

				if(gl.cajaid==2){
					if(caja.count==0) return false;

					if(caja.last().estado==0){
						return true;
					}
				}

			} else if(gl.cajaid==3 || gl.cajaid==5){

				if(caja.count==0) {
					if(gl.cajaid==3) gl.cajaid=0;
					return false;
				}

				if(caja.last().estado==1){
					return false;
				}else if(gl.cajaid==5) {
					if(caja.last().fecha!=du.getFechaActual()){
						gl.validDate=true;
						gl.lastDate=caja.last().fecha;
						gl.cajaid=6; return false;
					}
				}

			}else if(gl.cajaid==4) return false;

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			msgbox("Ocurrió error (valida) "+e);
			return false;
		}

		return true;
	}

	private void msgAskValid(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Registro");
		dialog.setMessage(msg);
		dialog.setCancelable(false);

		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});

		dialog.show();

	}

	private void msgAskIniciarCaja(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Registro");
		dialog.setMessage(msg);
		dialog.setCancelable(false);

		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				if(gl.cajaid==5){

					gl.cajaid=1;

					if (valida()){

						if (gl.cajaid!=2){
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Registro");
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Cierre Diario");
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
					msgAskIniciarCaja("La caja está cerrada. ¿Quiere realizar el inicio de caja?");
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


	//endregion

	//region Activity Events
	
	@Override
 	protected void onResume() {
		try{
			super.onResume();
			setPrintWidth();
		}catch (Exception e){
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
