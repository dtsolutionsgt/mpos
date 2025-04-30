package com.dtsgt.mpos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsDocDepos;
import com.dtsgt.classes.clsDocDeposito;
import com.dtsgt.classes.clsFinDia;
import com.dtsgt.classes.clsP_depositoObj;
import com.dtsgt.ladapt.ListAdaptDepos;

import java.util.ArrayList;


public class Deposito extends PBase {
	
	private Spinner  spinBanco;
	private EditText txtBol;
	private TextView lblEf,lblCheq,lblTot,lblBol;
	
	private ListView listView;
	private ImageView btnSave,btnCancel,btnSelAll,btnSelNone;

    private ArrayList<clsClasses.clsDepos> items = new ArrayList<clsClasses.clsDepos>();
	private ListAdaptDepos adapter;
	private clsClasses.clsDepos selitem;
	private clsFinDia claseFinDia;
	private DateUtils claseDateUtils;
	
	private ArrayList<String> spincode= new ArrayList<String>();
	private ArrayList<String> spinname = new ArrayList<String>();
	private ArrayList<String> spincuenta = new ArrayList<String>();
	private ArrayList<String> depcorel = new ArrayList<String>();

	private String bancoid="",cuenta="",bol, corel,nombre;
	private int boldep,codigo_banco;
	private double tcheq,ttot;
	private boolean depparc; //#HS_20181120_1625 Se cambio el tipo a la variable de entero a boolean.

	private AppMethods app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_deposito);

			super.InitBase();

			spinBanco = (Spinner) findViewById(R.id.spinner1);
			txtBol = (EditText) findViewById(R.id.txt1);
			lblEf = (TextView) findViewById(R.id.lblpSaldo);lblEf.setText(mu.frmcur(0));
			lblCheq = (TextView) findViewById(R.id.lblCheq);lblCheq.setText(mu.frmcur(0));
			lblTot = (TextView) findViewById(R.id.lblTot);lblTot.setText(mu.frmcur(0));
			lblBol = (TextView) findViewById(R.id.textView2);

			setHandlers();

			fillSpinner();
			fillDocList();

			ttot=0;gl.gint=0;

			app = new AppMethods(this, gl, Con, db);
			gl.validimp=app.validaImpresora();
			if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

			boldep=gl.boldep;
			depparc=gl.depparc;

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}
	
	
	//region Events
	
	public void doSave(View view){
		try {
			if (checkValues()) msgAskSave("Guardar depósito");
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	public void doDesglose(View view){
		try {
			startActivity(new Intent(this, DesgloseMon.class));
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	public void doExit(View view){
		try {
			finish();
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	private void setHandlers(){

		spinBanco.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				TextView spinlabel;

				try {
					spinlabel=(TextView)parentView.getChildAt(0);
					spinlabel.setTextColor(Color.BLACK);
					spinlabel.setPadding(5, 0, 0, 0);
					spinlabel.setTextSize(18);

					bancoid=spincode.get(position);
					codigo_banco=Integer.parseInt(bancoid);
					cuenta=spincuenta.get(position);
					nombre=spinlabel.getText().toString();

				} catch (Exception e) {
					msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				return;
			}

		});

	}

	//endregion

	//region Main

	private boolean guardaDeposito() {
		try {
			db.beginTransaction();

			clsP_depositoObj P_depositoObj = new clsP_depositoObj(this, Con, db);

			int newid = P_depositoObj.newID("SELECT MAX(codigo_deposito) FROM P_deposito");

			clsClasses.clsP_deposito item = clsCls.new clsP_deposito();

			item.codigo_deposito = newid;
			item.empresa = gl.emp;
			item.codigo_sucursal = gl.tienda;
			item.codigo_ruta = gl.codigo_ruta;
			item.fecha = du.getActDateTime();
			item.codigo_banco = codigo_banco;
			item.cuenta = cuenta;
			item.boleta = bol;
			item.monto_efectivo = gl.totDep;
			item.monto_cheques = tcheq;
			item.monto_total = tcheq + gl.totDep;
			item.codigo_vendedor = gl.codigo_vendedor;
			item.statcom = "N";
			item.referencia = 0;

			P_depositoObj.add(item);

			for (String itm : depcorel) {
				sql = "UPDATE D_FACTURA SET DEPOS=1 WHERE COREL='"+itm+"'";
				db.execSQL(sql);
			}

			db.setTransactionSuccessful();
			db.endTransaction();

			gl.gint=newid;

			return true;
		} catch (Exception e) {
			db.endTransaction();
			msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
			return false;
		}

	}

	@SuppressLint("SuspiciousIndentation")
	private void finishDoc(){

		try{
			if (!guardaDeposito()) return;
	    	db.execSQL("Delete from T_DEPOSB");
			finish();
		} catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	//endregion

	//region Dialogs

	private void msgAskSave(String msg) {
		try {
			ExDialog dialog = new ExDialog(this);
			dialog.setMessage("¿" + msg + "?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finishDoc();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			dialog.show();
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}


	}

	private void msgAskExit(String msg) {

		try{

			ExDialog dialog = new ExDialog(this);
			dialog.setMessage("¿" + msg + "?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					;
				}
			});

			dialog.show();
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}


	}

	//endregion

	//region Aux

	private void desglose() {
		Cursor DT;
		int c100 = 0, c50 = 0, c20 = 0, c10 = 0, c5 = 0, c2 = 0, c1 = 0, c050 = 0, c025 = 0, c010 = 0, c005 = 0, c001 = 0;
		double val1 = 0.0, val2 = 0.0, val5 = 0.0, val10 = 0.0, val20 = 0.0, val50 = 0.0, val100 = 0.0, val005 = 0.0, val050 = 0.0, val025 = 0.0, val010 = 0.0, val001 = 0.0, valtotc1 = 0.0, valtotc2 = 0.0, valtot = 0.0;
		double flt = 0;
		String tipo;

		try {
			val100 = 0;val50 = 0;val20 = 0;	val10 = 0;val5 = 0;	val2 = 0;val1 = 0;
			val050 = 0;val025 = 0;val010 = 0;val005 = 0;val001 = 0;
			lblEf.setText(String.valueOf(mu.frmcur(0)));

			sql = "SELECT * FROM T_DEPOSB";
			DT = Con.OpenDT(sql);

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				tipo = DT.getString(0);

				if (tipo.equals("100")) {
					c100 += DT.getInt(1);val100 += (Double.parseDouble(tipo) * c100);
				}

				if (tipo.equals("50")) {
					c50 += DT.getInt(1);val50 += (Double.parseDouble(tipo) * c50);
				}

				if (tipo.equals("20")) {
					c20 += DT.getInt(1);val20 += (Double.parseDouble(tipo) * c20);
				}

				if (tipo.equals("10")) {
					c10 += DT.getInt(1);val10 += (Double.parseDouble(tipo) * c10);
				}

				if (tipo.equals("5")) {
					c5 += DT.getInt(1);val5 += (Double.parseDouble(tipo) * c5);
				}

				if (tipo.equals("2")) {
					c2 += DT.getInt(1);val2 += (Double.parseDouble(tipo) * c2);
				}

				if (tipo.equals("1")) {
					c1 += DT.getInt(1);val1 += (Double.parseDouble(tipo) * c1);
				}

				if (tipo.equals("0.5")) {
					c050 += DT.getInt(1);val050 += (Double.parseDouble(tipo) * c050);
				}

				if (tipo.equals("0.25")) {
					c025 += DT.getInt(1);val025 += (Double.parseDouble(tipo) * c025);
				}

				if (tipo.equals("0.1")) {
					c010 += DT.getInt(1);val010 += (Double.parseDouble(tipo) * c010);
				}

				if (tipo.equals("0.05")) {
					c005 += DT.getInt(1);val005 += (Double.parseDouble(tipo) * c005);
				}

				if (tipo.equals("0.01")) {
					c001 += DT.getInt(1);val001 += (Double.parseDouble(tipo) * c001);
				}

				DT.moveToNext();
			}

			valtotc1 = val100 + val50 + val20 + val10 + val5 + val2 + val1;
			valtotc2 = val050 + val025 + val010 + val005 + val001;
			valtot = valtotc1 + valtotc2;

			gl.totDep=valtot;

			lblEf.setText(String.valueOf(mu.frmcur(mu.round(gl.totDep, gl.peDec))));
			ttot=gl.totDep+tcheq;
			lblEf.setText(mu.frmcur(gl.totDep));
			lblTot.setText(mu.frmcur(ttot));
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	private void fillDocList(){
		Cursor DT,DTD;
		double chec;

		items.clear();

		try {

			db.execSQL("DELETE FROM T_DEPOSB");

			depcorel.clear();tcheq=0;

			sql="SELECT COREL,TOTAL FROM D_FACTURA  WHERE ANULADO=0 AND DEPOS=0 ";
			DT=Con.OpenDT(sql);

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				sql="SELECT SUM(Valor),Count(Valor) FROM D_FACTURAP WHERE (COREL='"+DT.getString(0)+"') AND  (TIPO='C')";
				DTD=Con.OpenDT(sql);
				try {
					DTD.moveToFirst();
					chec=DTD.getDouble(0);tcheq+=chec;
					if (chec>0) depcorel.add(DT.getString(0));
				} catch (Exception ee) {
					msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
					chec=0;
				}

				DT.moveToNext();
			}

			lblCheq.setText(mu.frmcur(tcheq));

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			lblCheq.setText(mu.frmcur(0));
		}

	}

	private void fillSpinner(){
		Cursor DT;
		  
		try {
			sql="SELECT CODIGO_BANCO,NOMBRE,CUENTA FROM P_BANCO WHERE (ACTIVO=1) ORDER BY Nombre,Cuenta";
			DT=Con.OpenDT(sql);
					
			DT.moveToFirst();
			while (!DT.isAfterLast()) {
				  
			  spincode.add(DT.getString(0));
			  spinname.add(DT.getString(1)+" - "+DT.getString(2));
			  spincuenta.add(DT.getString(2));
			  
			  DT.moveToNext();
			}
					
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
	    }
					
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinname);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
		spinBanco.setAdapter(dataAdapter);
			
		bancoid="";
		
	}	
	
	private boolean checkValues(){
		try {

			if (tcheq+gl.totDep==0) {
				msgbox("El deposito está vacío. No se puede realizar.");return false;
			}

			if (mu.emptystr(bancoid)) {
				mu.msgbox("Falta definir banco");return false;
			}

			bol=txtBol.getText().toString();

			if (mu.emptystr(bol)) {
				mu.msgbox("Falta definir boleto");txtBol.requestFocus();return false;
			}

			return true;
		}catch (Exception e){
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return false;
		}

	}

	//endregion

	//region Activity Events

	@Override
	protected void onResume() {
		try {
			super.onResume();
			desglose();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

	@Override
	public void onBackPressed() {
		msgAskExit("Salir sin guardar depósito");
	}

	//endregion
}
