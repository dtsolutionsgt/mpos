package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.DateUtils;

public class clsDescFiltro {
	
	public String estr;
	public int ival;
	
	private int active;
	private SQLiteDatabase db;
	private BaseDatos Con;
	private BaseDatos.Insert ins;
	private BaseDatos.Update upd;
	private String vSQL;
	
	private DateUtils DU;
	
	private int cliid,rutaid;
	private long fecha;
	
	public clsDescFiltro(Context context, int ruta, int cliente) {
		
		cliid=cliente;rutaid=ruta;
		
		active=0;
		Con = new BaseDatos(context);
	    opendb();
	    ins=Con.Ins;upd=Con.Upd;
	    
	    DU=new DateUtils();fecha=DU.getActDate();
	    
	    processFilter();
	    
	    closedb();
	}	
	
	private void processFilter(){
		
		try {
			vSQL="DELETE FROM T_DESC";
			db.execSQL(vSQL);
		} catch (SQLException e) {
			return;
		}
		
		//if (!validaPermisos()) return;
		
		filtrarDescuentos();
		
	}
	
	private void filtrarDescuentos() {
		Cursor DT;
		int i;

		i=0;
		
		try {
			
			vSQL="SELECT CLIENTE,CTIPO,PRODUCTO,PTIPO,TIPORUTA,RANGOINI,RANGOFIN,DESCTIPO,VALOR,GLOBDESC,PORCANT,FECHAINI,FECHAFIN,CODDESC,NOMBRE "+
				 "FROM P_DESCUENTO WHERE ((FECHAINI<="+fecha+") AND (FECHAFIN>="+fecha+")) ";
			DT=Con.OpenDT(vSQL);
				
			if (DT.getCount()>0) {
			
				DT.moveToFirst();
				while (!DT.isAfterLast()) {
				  
					try {
						
						ins.init("T_DESC");
						
						ins.add("ID",i);
						ins.add("PRODUCTO",DT.getString(2));
						ins.add("PTIPO",DT.getInt(3));
						ins.add("RANGOINI",DT.getDouble(5));
						ins.add("RANGOFIN",DT.getDouble(6));
						ins.add("DESCTIPO",DT.getString(7));
						ins.add("VALOR",DT.getDouble(8));
						ins.add("GLOBDESC",DT.getString(9));
						ins.add("PORCANT",DT.getString(10));
						ins.add("NOMBRE",DT.getString(14));
						
				    	db.execSQL(ins.sql());
				    	
					} catch (SQLException e) {
					}	
			  
					DT.moveToNext();i+=1;
				}	
			}
			
			ival=i;
			
		} catch (Exception e) {
			estr=e.getMessage();
	    }
		

	}
	
	
	// Aux
	
	private boolean validaPermisos(){
		Cursor DT;
	
		//try {
		//	vSQL="SELECT DESCUENTO FROM P_RUTA WHERE CODIGO='"+rutaid+"'";
        //   	DT=Con.OpenDT(vSQL);
		//	DT.moveToFirst();
		//	if (DT.getString(0).equalsIgnoreCase("N")) return false;
		//} catch (Exception e) {
		//   	return false;
	    //}
		
		try {
			vSQL="SELECT DESCUENTO FROM P_CLIENTE WHERE CODIGO_CODIGO="+cliid;
           	DT=Con.OpenDT(vSQL);
			DT.moveToFirst();
			if (DT.getInt(0)==0) return false;
		} catch (Exception e) {
		   	return false;
	    }
		
		return true;
	}
	
	private void opendb() {
		try {
			db = Con.getWritableDatabase();
		 	Con.vDatabase =db;
			active=1;	
	    } catch (Exception e) {
	    	active= 0;
	    }
	}		

	private void closedb(){
		try {
			Con.close();  
		} catch (Exception e) { }
	}
	
}
