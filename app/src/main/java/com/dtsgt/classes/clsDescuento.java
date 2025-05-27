package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.appGlobals;

import java.util.ArrayList;

public class clsDescuento {
		
	public double monto,descper,descmonto;
	
	private int active;
	private android.database.sqlite.SQLiteDatabase db;
	private BaseDatos Con;
	private String vSQL;
	
	private MiscUtils MU;

	private ArrayList<Double> items = new ArrayList<Double>();
	private ArrayList<Double> montos = new ArrayList<Double>();
	
	private Context cont;
	
	private String prodid,lineaid,slineaid,marcaid,canttipo;
	private double cant,vmax,dmax;
	private boolean acum;
	
	public clsDescuento(Context context, String producto, double cantidad, double descmax) {
		
		cont=context;
		
		prodid=producto;cant=cantidad;
		
		try {
			active=0;
			Con = new BaseDatos(context);
			opendb();
		} catch (Exception e) {
		}
	    
	    MU=new MiscUtils(context);
		
		vmax=0;
		dmax=descmax;
		acum=false;
		lineaid="";slineaid="";marcaid="";
		
	}
	
	// Descuento local
	
	public double getDesc(){
		double dval=0;
		
		items.clear();
		
		//if (!validaPermisos()) return 0;
		
		listaDescRango();
		listaDescMult();
		
		dval=descFinal();
		monto=montoFinal();

        descper=dval;
        descmonto=monto;

		return dval;
	}

	public double getDescuento(){
		double dval=0;

		items.clear();

		listaDescRangoTipo();
		dval=descFinal();
		descper=dval;

		return dval;
	}
	
	private double descFinal() {
		double df=0,dm=0,sd=0;
		double vd;
		
		if (items.size()==0) return 0;
		
		for(int i = 0; i < items.size(); i++ ) {
			vd=items.get(i);
			sd+=vd;
			if (vd>dm) dm=vd;
		}	
		
		if (acum) {
			df=sd;			
		} else {	
			df=dm;
		}
		
		if (dmax>0) {
			if (df>dmax) df=dmax;
		}
		
		return df;
	}
	
	private double montoFinal() {
		double df=0,dm=0,sd=0;
		double vd;
		
		if (montos.size()==0) return 0;
		
		for(int i = 0; i < montos.size(); i++ ) {
			vd=montos.get(i);
			sd+=vd;
			if (vd>dm) dm=vd;
		}	
		
		if (acum) {
			df=sd;			
		} else {	
			df=dm;
		}
		
		if (dmax>0) {
			if (df>dmax) df=dmax;
		}
		
		return df;
	}
	
	public void listaDescRango() {

		Cursor DT;
		String iid;
		double val;

		if (cant<=0) return;

		try {
			vSQL="SELECT PRODUCTO,PTIPO,VALOR,PORCANT "+
				 "FROM T_DESC WHERE  ("+cant+">=RANGOINI) AND ("+cant+"<=RANGOFIN) "+
				 "AND (PTIPO<4) AND (DESCTIPO='R') AND (GLOBDESC='N') ";
			     //"AND (PTIPO<4) AND (DESCTIPO='R') AND (GLOBDESC='N') AND ((PORCANT='S') OR (PORCANT='1'))";
			DT=Con.OpenDT(vSQL);
			if (DT.getCount()==0) return;
			
			DT.moveToFirst();

			while (!DT.isAfterLast()) {
				  
				iid=DT.getString(0);

				canttipo=DT.getString(3);
				val=0;
				
				switch (DT.getInt(1)) {
					case 0: 
						if (iid.equalsIgnoreCase(prodid) || iid.equalsIgnoreCase("*")) {
							val=DT.getDouble(2);
						}break;
					case 1: 
						if (iid.equalsIgnoreCase(slineaid)) val=DT.getDouble(2);break;
					case 2:
							if (iid.equalsIgnoreCase(lineaid)) {
								val = DT.getDouble(2);
							}
							break;
					case 3:
						if (iid.equalsIgnoreCase(marcaid)) val = DT.getDouble(2);
						break;
				}		
				
				if (val>0) {
					if (canttipo.equalsIgnoreCase("S")) {
                        items.add(val);
                    } else {
                        montos.add(val);
                    }
				}
				
				DT.moveToNext();
			}

            if (DT!=null) DT.close();
		} catch (Exception e) {
		   	MU.msgbox(e.getMessage());
	    }
		
		
	}

	public void listaDescRangoTipo() {

		Cursor DT;
		String iid;
		double val;

		if (cant<=0) return;

		try {
			vSQL="SELECT PRODUCTO,PTIPO,VALOR,PORCANT "+
					"FROM T_DESC WHERE PRODUCTO ='"+prodid+"' AND ("+cant+">=RANGOINI) AND ("+cant+"<=RANGOFIN) "+
					"AND (PTIPO<4) AND (DESCTIPO='R') AND (GLOBDESC='N') ";
			//"AND (PTIPO<4) AND (DESCTIPO='R') AND (GLOBDESC='N') AND ((PORCANT='S') OR (PORCANT='1'))";
			DT=Con.OpenDT(vSQL);
			if (DT.getCount()==0) return;

			DT.moveToFirst();

			while (!DT.isAfterLast()) {

				iid=DT.getString(0);

				canttipo=DT.getString(3);
				val=0;

				switch (DT.getInt(1)) {
					case 0:
						if (iid.equalsIgnoreCase(prodid) || iid.equalsIgnoreCase("*")) {
							val=DT.getDouble(2);
						}break;
					case 1:
						if (iid.equalsIgnoreCase(prodid)) val=DT.getDouble(2);
						break;
					case 2:
						if (iid.equalsIgnoreCase(prodid)) val = DT.getDouble(2);
						break;
					case 3:
						if (iid.equalsIgnoreCase(prodid)) val = DT.getDouble(2);
						break;
				}

				if (val>0) {
					if (canttipo.equalsIgnoreCase("S")) {
						items.add(val);
					} else {
						montos.add(val);
					}
				}

				DT.moveToNext();
			}

			if (DT!=null) DT.close();
		} catch (Exception e) {
			MU.msgbox(e.getMessage());
		}


	}

	private void listaDescMult(){
		Cursor DT;
		String iid;
		double val,mcant,mul;
		
		try {
			vSQL="SELECT PRODUCTO,PTIPO,RANGOINI,RANGOFIN,VALOR,PORCANT "+
				 "FROM T_DESC WHERE ("+cant+">=RANGOINI) "+
				 "AND (PTIPO<4) AND (DESCTIPO='M') AND (GLOBDESC='N') ";
			   //"AND (PTIPO<4) AND (DESCTIPO='M') AND (GLOBDESC='N') AND ((PORCANT='S') OR (PORCANT='1'))";

			DT=Con.OpenDT(vSQL);
			
			if (DT.getCount()==0) return;
						
			DT.moveToFirst();
			while (!DT.isAfterLast()) {
					
				iid=DT.getString(0);

				canttipo=DT.getString(3);
				val=0;
				
				switch (DT.getInt(1)) {
					case 0: 
						if (iid.equalsIgnoreCase(prodid) || iid.equalsIgnoreCase("*")) val=DT.getDouble(4);break;
					case 1: 
						if (iid.equalsIgnoreCase(slineaid)) val=DT.getDouble(4);break;
					case 2:
						if (iid.equalsIgnoreCase(lineaid)) val=DT.getDouble(4);break;
					case 3: 
						if (iid.equalsIgnoreCase(marcaid)) val=DT.getDouble(4);break;
				}
				
				if (val>0) {				
					mcant=cant-DT.getDouble(2);
					mul=DT.getDouble(3);
					
					if (mul>0) {
						mcant=(int) (mcant/mul);mcant+=1;	
						val=val*mcant;
					} else {	
						val=0;
					}
					
					//if (val>0) items.add(val);
					if (val>0) {
						if (canttipo.equalsIgnoreCase("1")) montos.add(val); else items.add(val);
					}					
				}
				
				DT.moveToNext();
			}

            if (DT!=null) DT.close();
		} catch (Exception e) {
		   	MU.msgbox(e.getMessage());
	    }
		
		
	}
	
	
	// Aux
	
 	private boolean validaPermisos(){
		Cursor DT;
		
		try {
			vSQL="SELECT DESCUENTO,LINEA,'' AS SUBLINEA,MARCA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO="+prodid;
           	DT=Con.OpenDT(vSQL);
			DT.moveToFirst();
			
			if (DT.getInt(0)==0) return false;
			
			lineaid=DT.getString(1);
			slineaid=DT.getString(2);
			marcaid=DT.getString(3);
			
		} catch (Exception e) {
		   	return false;
	    }

		return true;
	}

	private boolean getLineaProducto(){
		Cursor DT;

		try {
			vSQL="SELECT DESCUENTO,LINEA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO="+prodid;
			DT=Con.OpenDT(vSQL);
			DT.moveToFirst();

			if (DT.getInt(0)==0) return false;

			lineaid=DT.getString(1);

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private boolean getMarcaProducto(){
		Cursor DT;

		try {
			vSQL="SELECT DESCUENTO,MARCA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO="+prodid;
			DT=Con.OpenDT(vSQL);
			DT.moveToFirst();

			if (DT.getInt(0)==0) return false;

			marcaid=DT.getString(1);

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
	
}
