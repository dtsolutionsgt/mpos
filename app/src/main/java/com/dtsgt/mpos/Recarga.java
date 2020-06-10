package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsDocMov;
import com.dtsgt.ladapt.ListAdaptDevCli;

import java.util.ArrayList;

public class Recarga extends PBase {

	private ListView listView;
	private TextView lblTotal;
	
	private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
	private ListAdaptDevCli adapter;
	private clsClasses.clsCFDV selitem;

	private printer prn;
	private Runnable printclose;
	private clsDocMov mdoc;
	
	private String itemid,prodid;
	private double cant, precio, totCant, total;
	private String estado;
	private int itempos,emp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recarga);

		super.InitBase();
		addlog("Recarga",""+du.getActDateTime(),gl.vend);

		listView = (ListView) findViewById(R.id.listView1);
		lblTotal = (TextView) findViewById(R.id.lblTotal);

		emp=((appGlobals) vApp).emp;
		estado="R";
		
		setHandlers();
		
		browse=0;
		fecha=du.getActDateTime();
		((appGlobals) vApp).devrazon="0";

		gl.cod_prov_recarga = 0;
		
		clearData();
		
		printclose= new Runnable() {
		    public void run() {
		    	Recarga.super.finish();
		    }
		};
		
		prn=new printer(this,printclose,gl.validimp);
		
		mdoc=new clsDocMov(this,prn.prw,"Recarga",gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");
	}


	// Events
	
	public void showProd(View view) {
		try{

			((appGlobals) vApp).gstr="";

			browse=1;
			itempos=-1;
			gl.prodtipo=2;

			if (gl.cod_prov_recarga ==0){
				Intent intent = new Intent(this,ListaProveedores.class);
				startActivity(intent);
			}else{
				Intent intent = new Intent(this,Producto.class);
				startActivity(intent);
			}

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}	
	
	public void finishDevol(View view){
		try{
			if (!hasProducts()) {
				mu.msgbox("No puede continuar, no ha agregado ninguno producto !");return;
			}

			msgAskComplete("Aplicar ingreso");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	
	// Main
	
	private void setHandlers(){

		try{
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

						adapter.setSelectedIndex(position);

						updCantCod(vItem.Cod);

					} catch (Exception e) {
						mu.msgbox( e.getMessage());
					}
				};
			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	    
	}
	
	private void listItems(){
		Cursor DT;
		clsClasses.clsCFDV vItem;	
		String s;
				
		items.clear();
		
		try {
			
			sql="SELECT P_PRODUCTO.CODIGO, T_CxCD.CANT, (T_CxCD.CANT * T_CxCD.PRECIO) AS TOTAL, " +
				"P_PRODUCTO.DESCCORTA, T_CxCD.ITEM, T_CxCD.PRECIO "+
			    "FROM T_CxCD INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=T_CxCD.CODIGO "+
				"ORDER BY P_PRODUCTO.DESCCORTA";
			
			DT=Con.OpenDT(sql);
			if (DT.getCount()==0) {return;}
			
			DT.moveToFirst();
			while (!DT.isAfterLast()) {
				  
			  vItem = clsCls.new clsCFDV();
			  
			  vItem.Cod=DT.getString(0);
			  vItem.Desc=DT.getString(3);
			  vItem.Valor=DT.getString(2);
			  s=mu.frmdec(DT.getDouble(1));
			  vItem.Fecha=s;
			  vItem.id=DT.getInt(4);
			  vItem.precio=DT.getDouble(5);
			  
			  items.add(vItem);	
			 
			  DT.moveToNext();
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		   	mu.msgbox( e.getMessage());
	    }
			 
		adapter=new ListAdaptDevCli(this,items);
		listView.setAdapter(adapter);
	}
	
	private void processItem(){
		String pid;

		try{
			pid=((appGlobals) vApp).gstr;
			if (mu.emptystr(pid)) return;

			prodid=pid;

			setCant();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void setCant(){
		try{
			browse=2;

			itempos=-1;

			((appGlobals) vApp).prod=prodid;
			((appGlobals) vApp).gstr="";
			((appGlobals) vApp).gstr2="";

			Intent intent = new Intent(this,RecargCant.class);
			startActivity(intent);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void updCant(int item){
		Cursor DT;
		String prid,rz;
		double pcant=0;

		try{
			try {
				sql="SELECT CODIGO,CODDEV,CANT FROM T_CxCD WHERE Item="+item;
				DT=Con.OpenDT(sql);
				DT.moveToFirst();

				prid=DT.getString(0);
				rz=DT.getString(1);

			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
				return;
			}

			browse=2;

			itempos=item;

		/*
		((appGlobals) vApp).prod=prid;
		((appGlobals) vApp).gstr=rz;
		//((appGlobals) vApp).dval=pcant;

		Intent intent = new Intent(this,DevCliCant.class);
		*/


			((appGlobals) vApp).prod=prid;
			((appGlobals) vApp).gstr="";
			((appGlobals) vApp).gstr2=""+pcant;
			Intent intent = new Intent(this,RecargCant.class);

			startActivity(intent);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void updCantCod(String item){
		Cursor DT;
		String prid,rz;
		double pcant=0;

		try{
			browse=2;
			prodid=item;

			((appGlobals) vApp).prod=item;
			((appGlobals) vApp).gstr="0";
			((appGlobals) vApp).gstr2=""+pcant;

			Intent intent = new Intent(this,RecargCant.class);
			startActivity(intent);

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void processCant(){
		double cnt, vPrecio;
		String raz;

		try{
			cnt=((appGlobals) vApp).dval;
			vPrecio=((appGlobals) vApp).precio_recarga;

			if (cnt<0) return;
			if (vPrecio<=0) return;

			raz=((appGlobals) vApp).devrazon;
			cant=cnt;
			precio=vPrecio;

			addItem(raz);
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	private void addItem(String raz){
		Cursor DT;
		int id;

		try {

			sql="DELETE FROM T_CxCD WHERE CODIGO='"+gl.prodcod+"'";
			db.execSQL(sql);
		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}
		
		try {
			sql="SELECT MAX(Item) FROM T_CxCD";	
			DT=Con.OpenDT(sql);
			DT.moveToFirst();	
			id=DT.getInt(0);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			id=0;
		}	
		
		id+=1;

		try {
			
			ins.init("T_CxCD");
			
			ins.add("Item",id);
			ins.add("CODIGO",gl.prodcod);
			ins.add("CANT",cant);
			ins.add("CODDEV",raz);
			ins.add("TOTAL",0);
			ins.add("PRECIO",precio);
			ins.add("PRECLISTA",0);
			ins.add("REF","");
			ins.add("PESO",0);
			ins.add("FECHA_CAD",0);
			ins.add("LOTE","00");
			ins.add("UMVENTA","UNI");
			ins.add("UMSTOCK","UNI");
			ins.add("UMPESO","UNI");
			ins.add("FACTOR",1);
			ins.add("POR_PESO","N");
			ins.add("TIENE_LOTE",0);

	    	db.execSQL(ins.sql());
	    	
		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("Error : " + e.getMessage());
		}	
		
		try {
			sql="DELETE FROM T_CxCD WHERE CANT=0";
			db.execSQL(sql);
		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("Error : " + e.getMessage());
		}

		listItems();
		String totales= getTotals();
		lblTotal.setText(totales);

	}
	
	private void saveDevol(){
		Cursor DT;
		String corel,pCod;
		Double pCant, pPrecio;
		clsD_MovDObj movd = new clsD_MovDObj(Recarga.this, Con, db);
		
		corel=((appGlobals) vApp).ruta+"_"+mu.getCorelBase();
		
		try {

			if (!validaDatos()){
				return;
			}

			db.beginTransaction();
			
			ins.init("D_MOV");
			
	 		ins.add("COREL",corel);
			ins.add("RUTA",((appGlobals) vApp).ruta);
			ins.add("ANULADO",0);
			ins.add("FECHA",fecha);
			ins.add("TIPO","R");
			ins.add("USUARIO",gl.codigo_vendedor);
			ins.add("REFERENCIA","");
			ins.add("STATCOM","N");
			ins.add("IMPRES",0);
			ins.add("CODIGOLIQUIDACION",0);
			ins.add("CODIGO_PROVEEDOR",gl.cod_prov_recarga);
			ins.add("TOTAL",total);
		
			db.execSQL(ins.sql());
			
			sql="SELECT Item,CODIGO,CANT,CODDEV, PRECIO FROM T_CxCD WHERE CANT>0";
			DT=Con.OpenDT(sql);
	
			DT.moveToFirst();
			while (!DT.isAfterLast()) {
			
				pCod=DT.getString(1);
				pCant=DT.getDouble(2);
				pPrecio=DT.getDouble(4);
				
			  	ins.init("D_MOVD");

				ins.add("CORELDET",movd.maxId());
				ins.add("COREL",corel);
				ins.add("PRODUCTO",pCod);
				ins.add("CANT",pCant);
				ins.add("CANTM",0);
				ins.add("PESO",0);
				ins.add("PESOM",0);
				ins.add("LOTE",pCod);
				ins.add("CODIGOLIQUIDACION",0);
				ins.add("PRECIO",pPrecio);
			
			    db.execSQL(ins.sql());
			    
			    updateStock(corel,pCod,pCant);
			    	
			    DT.moveToNext();
			}

			db.setTransactionSuccessful();				
			db.endTransaction();
			
			toast("Recarga guardada");
			
			super.finish();
			
			try {
				if (mdoc.buildPrint(corel,1)) prn.printask(printclose);
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
				mu.msgbox(e.getMessage());
			}
			
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			db.endTransaction();
		   	mu.msgbox( e.getMessage());
		}	
		
	}

	private boolean validaDatos(){

		boolean resultado=true;

		try{

			if (gl.cod_prov_recarga ==0){
				msgbox("Debe ingresar el proveedor de ingreso de mercancía");
				resultado=false;
			}

			if (!hasProducts()){
				msgbox("Debe ingresar productos al ingreso de mercancía");
				resultado=false;
			}

		}catch (Exception ex){
			msgbox("Ocurrió un error validando datos " + ex.getMessage());
		}

		return resultado;
	}

	private void updateStock(String corel,String pcod,double pcant) {
		Cursor DT;
		int icod=Integer.parseInt(pcod);
		String scod=app.prodCod(icod);
		
	    try {
	      	
	     	ins.init("P_STOCK");
		  	
			ins.add("CODIGO",icod);
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
			ins.add("COREL_D_MOV","");
			ins.add("UNIDADMEDIDA",app.umVenta3(icod));

			String sp=ins.sql();
	    	
	    	db.execSQL(ins.sql());
	    } catch (Exception e) {
	    }

	    sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE CODIGO='"+icod+"' ";
	    db.execSQL(sql);
	}
	
	
	// Aux 
	
	private void clearData(){
		try {
			sql="DELETE FROM T_CxCD";
			db.execSQL(sql);
		} catch (SQLException e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}	
	}
	
	private void msgAskExit(String msg) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage(msg  + " ?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doExit();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					;
				}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

			
	}	
	
	private void msgAskComplete(String msg) {
		try{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(R.string.app_name);
			dialog.setMessage(msg  + " ?");

			dialog.setIcon(R.drawable.ic_quest);

			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					saveDevol();
				}
			});

			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					;
				}
			});

			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

			
	}	
	
	private boolean hasProducts(){
		Cursor DT;
		
		try {
			sql="SELECT CODIGO FROM T_CxCD";	
			DT=Con.OpenDT(sql);
				
			return DT.getCount()>0;
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return false;
		}	
	}

	private String getTotals(){
		Cursor DT;

		String resultado = "";

		try {

			sql="SELECT SUM(CANT), SUM(PRECIO*CANT) AS TOTAL FROM T_CxCD";
			DT=Con.OpenDT(sql);

			if(DT != null){
				if (DT.getCount()>0){

					DT.moveToFirst();

					total=DT.getDouble(1);
					totCant=DT.getDouble(0);

					resultado = String.format("Cantidad: %s                  Total: %s", mu.frmdec(totCant),  mu.frmdec(total));

				}
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

		return resultado;
	}

	private void doExit(){
		try{
			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	
	// Activity Events
	
	@Override
	protected void onResume() {
		try{
			super.onResume();

			//if (((appGlobals) vApp).closeVenta) super.finish();

			if (browse==1) {
				browse=0;
				processItem();return;
			}

			if (browse==2) {
				browse=0;
				processCant();return;
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	
	}

	@Override
	public void onBackPressed() {
		try{
			msgAskExit("Salir sin terminar recarga");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}	


}
