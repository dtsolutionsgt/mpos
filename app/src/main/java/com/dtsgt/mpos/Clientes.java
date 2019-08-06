package com.dtsgt.mpos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsCDB;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.ladapt.ListAdaptCliList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Clientes extends PBase {

	private ListView listView;
	private Spinner spinList, spinFilt;
	private EditText txtFiltro;
	private TextView lblCant;

	private ArrayList<clsCDB> items = new ArrayList<clsCDB>();
	private ArrayList<String> cobros = new ArrayList<String>();
	private ArrayList<String> ppago = new ArrayList<String>();

	private AlertDialog.Builder mMenuDlg;

	private ListAdaptCliList adapter;
	private clsCDB selitem;
	private AppMethods app;

	private int selidx, fecha, dweek, browse;
	private String selid, bbstr, bcode;
	private boolean scanning = false,porcentaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clientes);

		super.InitBase();
		addlog("Clientes", "" + du.getActDateTime(), gl.vend);

		listView = (ListView) findViewById(R.id.listView1);
		spinList = (Spinner) findViewById(R.id.spinner1);
		spinFilt = (Spinner) findViewById(R.id.spinner8);
		txtFiltro = (EditText) findViewById(R.id.txtFilter);
		lblCant = (TextView) findViewById(R.id.lblCant);

		app = new AppMethods(this, gl, Con, db);
		gl.validimp = app.validaImpresora();
		if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

		setHandlers();

		selid = "";
		selidx = -1;

		dweek = mu.dayofweek();

		fillSpinners();

		listItems();

		closekeyb();

		gl.escaneo = "N";

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			bcode = txtFiltro.getText().toString().trim();
			barcodeClient();
		}
		return super.dispatchKeyEvent(e);
	}


	// Events

	public void applyFilter(View view) {
		try {
			//listItems();
			//hidekeyb();
			txtFiltro.setText("");
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	public void showVenta(View view) {

		try {
			gl.tcorel = mu.getCorelBase();//gl.ruta/

			//Intent intent = new Intent(this,CliNuevoApr.class);
			Intent intent = new Intent(this, CliNuevo.class);
			startActivity(intent);
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void setHandlers() {

		try {

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					Object lvObj = listView.getItemAtPosition(position);
					clsCDB sitem = (clsCDB) lvObj;
					selitem = sitem;

					selid = sitem.Cod;
					selidx = position;
					adapter.setSelectedIndex(position);

					showCliente();

				};
			});

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					boolean pedit, pbor;
					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsClasses.clsCDB item = (clsClasses.clsCDB) lvObj;

						selid = item.Cod;
						selidx = position;
						adapter.setSelectedIndex(position);

						pedit = puedeeditarse();
						pbor = puedeborrarse();

						if (pbor && pedit) {
							showItemMenu();
						} else {
							if (pbor) msgAskBor("Eliminar cliente nuevo");
							if (pedit) msgAskEdit("Cambiar datos de cliente nuevo");
						}
					} catch (Exception e) {
						addlog(new Object() {
						}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
					}
					return true;
				}
			});

			listView.setOnTouchListener(new SwipeListener(this) {
				public void onSwipeRight() {
					finish();
				}

				public void onSwipeLeft() {
				}
			});

			spinList.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					TextView spinlabel;
					String scod;

					//	try {
					spinlabel = (TextView) parentView.getChildAt(0);
					spinlabel.setTextColor(Color.BLACK);
					spinlabel.setPadding(5, 0, 0, 0);
					spinlabel.setTextSize(18);

					dweek = position;

					listItems();

				/*	} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox( e.getMessage());
					}*/

				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					return;
				}

			});

			spinFilt.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					listItems();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					return;
				}

			});

			txtFiltro.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				public void onTextChanged(CharSequence s, int start, int before, int count) {
					int tl = txtFiltro.getText().toString().length();
					if (tl > 1) gl.escaneo = "S";
					if (tl == 0 || tl > 1) listItems();
				}
			});

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
			mu.msgbox(e.getMessage());
		}

	}


	// Main

	public void listItems() {
		Cursor DT;
		clsCDB vItem;
		int vP;
		String id, filt, ss;

		items.clear();

		selidx = -1;
		vP = 0;
		filt = txtFiltro.getText().toString().replace("'", "");

		try {

			cobros.clear();
			sql = "SELECT DISTINCT CLIENTE FROM P_COBRO ";
			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				for (int i = 0; i < DT.getCount(); i++) {
					//	try {
					cobros.add(DT.getString(0));
					DT.moveToNext();
				/*	} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
					}*/
				}
			}

			ppago.clear();
			sql = "SELECT D_FACTURA.CLIENTE " +
					"FROM D_FACTURA INNER JOIN  D_FACTURAP ON  D_FACTURA.COREL = D_FACTURAP.COREL " +
					"GROUP BY  D_FACTURA.CLIENTE, D_FACTURA.COREL, D_FACTURA.ANULADO " +
					"HAVING  (D_FACTURA.ANULADO='N') AND (SUM(D_FACTURAP.VALOR=0))";

			sql = "SELECT DISTINCT CLIENTE FROM D_FACTURA WHERE (ANULADO='N') AND (COREL NOT IN " +
					"  (SELECT DISTINCT D_FACTURA_1.COREL " +
					"   FROM D_FACTURA AS D_FACTURA_1 INNER JOIN " +
					"   D_FACTURAP ON D_FACTURA_1.COREL=D_FACTURAP.COREL))";


			DT = Con.OpenDT(sql);

			if (DT.getCount() > 0) {
				DT.moveToFirst();
				for (int i = 0; i < DT.getCount(); i++) {
					//	try {
					ss = DT.getString(0);
					if (!ppago.contains(ss)) ppago.add(ss);
					DT.moveToNext();
				/*	} catch (Exception e) {
					}*/
				}
			}

			sql = "SELECT DISTINCT P_CLIRUTA.CLIENTE,P_CLIENTE.NOMBRE,P_CLIRUTA.BANDERA,P_CLIENTE.COORX,P_CLIENTE.COORY " +
					"FROM P_CLIRUTA INNER JOIN P_CLIENTE ON P_CLIRUTA.CLIENTE=P_CLIENTE.CODIGO " +
					"WHERE (1=1) ";

			if (mu.emptystr(filt)) {
				if (dweek != 0) sql += "AND (P_CLIRUTA.DIA =" + dweek + ") ";
			}

			if (!mu.emptystr(filt)) {
				sql += "AND ((P_CLIRUTA.CLIENTE LIKE '%" + filt + "%') OR (P_CLIENTE.NOMBRE LIKE '%" + filt + "%')) ";
			}
			sql += "ORDER BY P_CLIRUTA.SECUENCIA,P_CLIENTE.NOMBRE";

			DT = Con.OpenDT(sql);

			lblCant.setText("" + DT.getCount() + "");


			if (DT.getCount() > 0) {

				DT.moveToFirst();
				while (!DT.isAfterLast()) {

					id = DT.getString(0);

					vItem = clsCls.new clsCDB();

					vItem.Cod = DT.getString(0);
					ss = DT.getString(0);
					vItem.Desc = DT.getString(1);
					vItem.Bandera = DT.getInt(2);
					vItem.Adds = "";
					vItem.coorx = DT.getDouble(3);
					vItem.coory = DT.getDouble(4);

					if (cobros.contains(ss)) vItem.Cobro = 1;
					else vItem.Cobro = 0;
					if (ppago.contains(ss)) vItem.ppend = 1;
					else vItem.ppend = 0;

					switch (spinFilt.getSelectedItemPosition()) {
						case 0:
							items.add(vItem);
							break;
						case 1:
							if (vItem.Cobro == 1) items.add(vItem);
							break;
						case 2:
							if (vItem.ppend == 1) items.add(vItem);
							break;
					}

					if (id.equalsIgnoreCase(selid)) selidx = vP;
					vP += 1;

					DT.moveToNext();
				}
			}

		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
			mu.msgbox(e.getMessage());
		}

		adapter = new ListAdaptCliList(this, items);
		listView.setAdapter(adapter);

		if (selidx > -1) {
			adapter.setSelectedIndex(selidx);
			listView.setSelection(selidx);
		}

	}

	public void showCliente() {

		try {
			//gl.cliente = selid;
			gl.closeCliDet = false;
			gl.closeVenta = false;
			if (!validaVenta()) return;//Se valida si hay correlativos de factura para la venta

			startActivity(new Intent(this, Venta.class));
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void editCliente() {
		try {
			gl.tcorel = selid;
			startActivity(new Intent(this, CliNuevoAprEdit.class));
		} catch (Exception e) {
			addlog(new Object() {
			}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
		}

	}

	private void barcodeClient() {
		Cursor dt;

		try {
			sql = "SELECT Codigo FROM P_CLIENTE WHERE CODBARRA='" + bcode + "'";
			dt = Con.OpenDT(sql);

			if (dt.getCount() == 0) {
				msgbox("Cliente no existe " + bcode + " ");
				txtFiltro.setText("");
				txtFiltro.requestFocus();
				return;
			}

			dt.moveToFirst();
			selid = dt.getString(0);
			showCliente();

			txtFiltro.setText("");
			txtFiltro.requestFocus();
		} catch (Exception e) {
			msgbox(new Object() {
			}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
		}
	}


	// Aux

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

	private void showItemMenu() {
		try{
			final AlertDialog Dialog;
			final String[] selitems = {"Eliminar cliente","Cambiar datos"};

			AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
			menudlg.setTitle("Cliente nuevo");

			menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
						case 0:
							msgAskBor("Eliminar cliente");break;
						case 1:
							editCliente();break;
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
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

    }

	private void fillSpinners(){
		try{
			List<String> dlist = new ArrayList<String>();

			dlist.add("Todos");
			dlist.add("Lunes");
			dlist.add("Martes");
			dlist.add("Miercoles");
			dlist.add("Jueves");
			dlist.add("Viernes");
			dlist.add("Sabado");
			dlist.add("Domingo");

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, dlist);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinList.setAdapter(dataAdapter);
			spinList.setSelection(dweek);


			List<String> flist = new ArrayList<String>();
			flist.add("Todos");
			flist.add("Con cobros");
			flist.add("Pago pendiente");

			ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, flist);
			dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinFilt.setAdapter(dataAdapter2);

			if (gl.filtrocli==-1) {
				spinFilt.setSelection(0);
			} else {
				spinFilt.setSelection(gl.filtrocli);
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

	private boolean puedeborrarse() {
		Cursor dt;
		String sql = "";

		try {
			sql="SELECT * FROM D_CLINUEVO WHERE CODIGO='"+selid+"'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()==0) return false;

			sql="SELECT * FROM D_FACTURA WHERE CLIENTE='"+selid+"'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()>0) return false;
		
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		return true;
	}
	
	private boolean puedeeditarse() {
		Cursor dt;
		String sql = "";

		try {
			sql="SELECT * FROM D_CLINUEVO WHERE CODIGO='"+selid+"' AND STATCOM='N'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()==0) return false;
		
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		return true;
	}
	
	private void msgAskBor(String msg) {

		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setMessage("¿" + msg + "?");

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					borraCliNuevo();
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

	private void msgAskEdit(String msg) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setMessage("¿" + msg + "?");

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					editCliente();
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

	private void borraCliNuevo() {
		try {
			db.beginTransaction();

			db.execSQL("DELETE FROM D_CLINUEVO WHERE CODIGO='"+selid+"'");
			db.execSQL("DELETE FROM P_CLIRUTA WHERE CLIENTE='"+selid+"'");
					
			db.setTransactionSuccessful();
			db.endTransaction();
			
			listItems();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			db.endTransaction();
			mu.msgbox(e.getMessage());
		}
	}

	// Activity Events
	
	protected void onResume() {
		try{
			super.onResume();
			listItems();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

}
