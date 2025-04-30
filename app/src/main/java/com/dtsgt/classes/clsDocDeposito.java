package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.appGlobals;

import java.util.ArrayList;

public class clsDocDeposito extends clsDocument {

	private ArrayList<itemData> items= new ArrayList<itemData>();

	private double tot,tote,totc,desgloseTotal=0;
	private int numc;
	private String banco,cuenta,ref,ss,st="";
	protected appGlobals gl;
	protected MiscUtils mu;
	protected DateUtils du;

	public clsDocDeposito(Context context, int printwidth, String pruta, String pvend, String cursymbol, int decimpres, String archivo) {
		super(context, printwidth,cursymbol,decimpres, "");
		docfactura=false;

		gl=((appGlobals) context.getApplicationContext());
		du=new DateUtils();

		nombre="DEPOSITO";
		numero="";
		serie="";
		nombre_cliente ="";
		ruta=pruta;
		vendedor=pvend;
		vendcod=gl.vend;
	}

	protected boolean loadHeadData(String corel) {
		Cursor DT;
		String val;
				
		//super.loadHeadData(corel);
		
		//nombre="DEPOSITO";
			
		try {

			sql="SELECT CODIGO_BANCO,CUENTA,BOLETA,MONTO_TOTAL,MONTO_EFECTIVO," +
					"MONTO_CHEQUES,0,FECHA,CODIGO_VENDEDOR FROM P_deposito WHERE CODIGO_DEPOSITO="+corel;
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			val=DT.getString(0);
			cuenta=DT.getString(1);	
			ref=DT.getString(2);
			
			tot=DT.getDouble(3);
			tote=DT.getDouble(4);
			totc=DT.getDouble(5);
			numc=DT.getInt(6);
			serie=corel;

			fsfecha=du.sfecha(DT.getInt(7));

		} catch (Exception e) {
			Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();return false;
	    }	
		
		try {
			sql="SELECT NOMBRE FROM P_BANCO WHERE CODIGO_BANCO='"+val+"'";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			banco=DT.getString(0);
		} catch (Exception e) {
			banco=val;
	    }	
			
		
		return true;
		
	}	
	
	protected boolean loadDocData(String corel) {
		return true;
	}	
		
	protected boolean buildDetail() {

		rep.empty();
		rep.addc("DEPOSITO BANCARIO");
		rep.empty();
		rep.empty();
		rep.line();
		rep.add("Empresa: " + gl.empnom);
		rep.add("Sucursal: " + gl.tiendanom);
		rep.add("Caja: " + gl.rutanom);
		rep.add("Impresion: "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
		rep.add("Vesion: "+gl.parVer);
		rep.add("Genero: "+gl.vendnom);
		rep.line();
		rep.add("Banco: "+banco);
		rep.add("Cuenta: "+cuenta);
		rep.add("Boleto: "+ref);
		rep.line();
		rep.addtotrs("Total efectivo :", rep.frmdec(tote));
		rep.addtotrs("Total cheques :", rep.frmdec(totc));
		rep.line();
		rep.addtotrs("Total  :", rep.frmdec(tot));
		rep.line();
		rep.add("");
		rep.add("");
		rep.add("");
		rep.add("");
		rep.line();
		rep.add("Firma Vendedor");
		rep.empty();
		rep.add("");
		rep.add("");

		return true;
	}



	protected boolean detail(){

		itemData item;

		rep.line();

		/*
		for (int i = 0; i <items.size(); i++) {
			item=items.get(i);

			rep.add(item.nombre);
			rep.add3lrr(item.tipo,item.num,item.monto);
		}

		 */
		rep.line();
		rep.add("");

		return true;
	}


	// Aux
	
	private class itemData {
		public String cod,nombre,tipo,num,serie,banco,cuenta;
		public double monto,totc;


		public Double denom,total;
		public String corr,type,moneda,bancoCorr;
		public int cant,numc;
	}
		
	
	
}
