package com.dtsgt.mpos;

import android.content.Context;
import android.database.Cursor;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.classes.clsDescuento;

import java.text.DecimalFormat;

public class Precio {

	public double costo,descmon,imp,impval,tot,precsin,totsin,precdoc,precioespecial,desc,preciobase;
	
	private int active;
	private android.database.sqlite.SQLiteDatabase db;
	private BaseDatos Con;
	private String sql;
	
	private Context cont;
	
	private DecimalFormat ffrmprec;
	private MiscUtils mu;
	
	private String prodid,um,umpeso,umventa;
	private double cant,prec;
	private int nivel,ndec,codprod;
	private boolean porpeso;
	
	public Precio(Context context,MiscUtils mutil,int numdec) {
		
		cont=context;
		mu=mutil;
		ndec=numdec;
		
		try {
			active=0;
			Con = new BaseDatos(context);
			opendb();
		} catch (Exception e) {
		}
		
		costo=0;descmon=0;imp=0;tot=0;
		
		ffrmprec = new DecimalFormat("#0.00");
		
	}

	public double precio(String prod,double pcant,int nivelprec,String unimedida,String unimedidapeso,double ppeso,String umven,int codigoprod) {

		prodid=prod;codprod=codigoprod;
		cant=pcant;nivel=nivelprec;
		um=unimedida;umpeso=unimedidapeso;umventa=umven;
		prec=0;costo=0;descmon=0;imp=0;tot=0;precioespecial=0;
        if (pcant==0) pcant=1;

		clsDescuento clsDesc=new clsDescuento(cont,""+codigoprod,cant);
		desc=clsDesc.getDesc();

		if (cant>0) {
		    prodPrecio(ppeso);
        } else {
		    prodPrecioBase();
        }

		return prec;
	}

	public boolean existePrecioEspecial(String prod,
										double pcant,
										int cliente,
										String clitipo,
										String unimedida,
										String unimedidapeso,
										double ppeso) {
		prodid=prod;cant=pcant;um=unimedida;
		umpeso=unimedidapeso;
		prec=0;costo=0;descmon=0;imp=0;tot=0;precioespecial=0;

		if (cant==0) return false;

		return prec>0;
	}

	
	// Private
	
	private void prodPrecio(double ppeso) {
		Cursor DT;
		double pr,stot,pprec,tsimp;
		String sprec="";

		try {

			if (ppeso>0) {
				sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO='"+codprod+"') AND (NIVEL="+nivel+") ";
			} else {
				sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO='"+codprod+"') AND (NIVEL="+nivel+")  ";
			}

			DT=Con.OpenDT(sql);
			DT.moveToFirst();
			pr=DT.getDouble(0);
			
		} catch (Exception e) {
			pr=0;

			try {
				sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO="+codprod+") AND (NIVEL="+nivel+")  ";
				DT=Con.OpenDT(sql);
				DT.moveToFirst();
				pr=DT.getDouble(0);
			} catch (Exception ee) {
				pr=0;
			}
	    }

        preciobase=pr;
		
		totsin=pr*cant;tsimp=mu.round(totsin,ndec);
		
		//percep=0;
		
		imp=getImp();
		pr=pr*(1+imp/100);

		// total
		stot=pr*cant;stot=mu.round(stot,ndec);
		if (imp>0) impval=stot-tsimp; else impval=0;
		descmon=(double) (stot*desc/100);descmon=mu.round(descmon,ndec);
		tot=stot-descmon;

		if (cant>0) prec=(double) (tot/cant); else prec=pr;

		try {
			sprec=ffrmprec.format(prec);sprec=sprec.replace(",",".");
			pprec=Double.parseDouble(sprec);
			precdoc=mu.round(pprec,ndec);
		} catch (Exception e) {
			precdoc=prec;
		}

		if (ppeso>0) prec=prec*ppeso/cant;
			
		try {
			sprec=ffrmprec.format(prec);sprec=sprec.replace(",",".");
			pprec=Double.parseDouble(sprec);
			pprec=mu.round(pprec,ndec);
		} catch (Exception e) {
			pprec=prec;
		}
		prec=pprec;

		// total
		stot=prec*cant;stot=mu.round(stot,ndec);
		if (imp>0) impval=stot-tsimp; else impval=0;
		descmon=(double) (stot*desc/100);descmon=mu.round(descmon,ndec);
		tot=stot-descmon;

		if (imp==0) precsin=prec; else precsin=prec/(1+imp/100);
		//Toast.makeText(cont,sprec+" - "+pprec+" / "+prec+" prec sin : "+precsin, Toast.LENGTH_SHORT).show();
		
		totsin=mu.round(precsin*cant,ndec);
		if (cant>0) precsin=(double) (totsin/cant);	
		
		try {
			sprec=ffrmprec.format(precsin);sprec=sprec.replace(",",".");
			pprec=Double.parseDouble(sprec);
			pprec=mu.round(pprec,ndec);
		} catch (Exception e) {
			pprec=precsin;
		}
		precsin=pprec;

	}
	
	private void prodPrecioBase() {
		Cursor DT;
		double pr,stot,pprec,tsimp;
		String sprec="";

		try {

			sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO="+codprod+") AND (NIVEL="+nivel+") ";
           	DT=Con.OpenDT(sql);
			DT.moveToFirst();
							  
			pr=DT.getDouble(0);
			
		} catch (Exception e) {
			pr=0;
	    }

        preciobase=pr;
		totsin=pr;tsimp=mu.round(totsin,ndec);
		
		imp=getImp();
		pr=pr*(1+imp/100);
		
		stot=pr;stot=mu.round(stot,ndec);
		
		if (imp>0) impval=stot-tsimp; else impval=0;
		
		descmon=0;
		
		tot=stot-descmon;
		prec=tot;
			
		try {
			sprec=ffrmprec.format(prec);sprec=sprec.replace(",",".");
			pprec=Double.parseDouble(sprec);
			pprec=mu.round(pprec,ndec);
		} catch (Exception e) {
			pprec=prec;
		}
		prec=pprec;
		
		if (imp==0) precsin=prec; else precsin=prec/(1+imp/100);
		
		totsin=mu.round(precsin,ndec);
		precsin=totsin;	
		
		try {
			sprec=ffrmprec.format(precsin);sprec=sprec.replace(",",".");
			pprec=Double.parseDouble(sprec);
			pprec=mu.round(pprec,ndec);
		} catch (Exception e) {
			pprec=precsin;
		}
		precsin=pprec;

	}

    public double prodPrecioBase(int cprod,int nnivel) {
        Cursor DT;
        double pr,stot,pprec,tsimp;
        String sprec="";

        try {

            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO="+cprod+") AND (NIVEL="+nnivel+") ";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            pr=DT.getDouble(0);

        } catch (Exception e) {
            pr=0;
        }

        preciobase=pr;
        totsin=pr;tsimp=mu.round(totsin,ndec);

        imp=getImp();
        pr=pr*(1+imp/100);

        stot=pr;stot=mu.round(stot,ndec);

        if (imp>0) impval=stot-tsimp; else impval=0;

        descmon=0;

        tot=stot-descmon;
        prec=tot;

        try {
            sprec=ffrmprec.format(prec);sprec=sprec.replace(",",".");
            pprec=Double.parseDouble(sprec);
            pprec=mu.round(pprec,ndec);
        } catch (Exception e) {
            pprec=prec;
        }
        prec=pprec;

        if (imp==0) precsin=prec; else precsin=prec/(1+imp/100);

        totsin=mu.round(precsin,ndec);
        precsin=totsin;

        try {
            sprec=ffrmprec.format(precsin);sprec=sprec.replace(",",".");
            pprec=Double.parseDouble(sprec);
            pprec=mu.round(pprec,ndec);
        } catch (Exception e) {
            pprec=precsin;
        }
        precsin=pprec;

        return prec;
    }

	private double getImp() {
		Cursor DT;
		double imv=0,im1=0,im2=0,im3=0;
		int ic1,ic2,ic3;
		
		try {
			sql="SELECT IMP1,IMP2,IMP3 FROM P_PRODUCTO WHERE (CODIGO_PRODUCTO="+codprod+") ";
           	DT=Con.OpenDT(sql);
			DT.moveToFirst();
							  
			ic1=DT.getInt(0);
			ic2=DT.getInt(1);
			ic3=DT.getInt(2);
			
			if (ic1>0) {
				sql="SELECT VALOR FROM P_IMPUESTO WHERE CODIGO_IMPUESTO="+ic1;
	           	DT=Con.OpenDT(sql);
	           	
				try {
					DT.moveToFirst();
					im1=DT.getDouble(0);
				} catch (Exception e) {
					im1=0;
				}	
				
			}
			
			if (ic2>0) {
				sql="SELECT VALOR FROM P_IMPUESTO WHERE CODIGO_IMPUESTO="+ic2;
	           	DT=Con.OpenDT(sql);
	           	
				try {
					DT.moveToFirst();
					im2=DT.getDouble(0);
				} catch (Exception e) {
					im2=0;
				}	
				
			}
			
			if (ic3>0) {
				sql="SELECT VALOR FROM P_IMPUESTO WHERE CODIGO_IMPUESTO="+ic3;
	           	DT=Con.OpenDT(sql);
	           	
				try {
					DT.moveToFirst();
					im3=DT.getDouble(0);
				} catch (Exception e) {
					im3=0;
				}	
				
			}

            if (DT!=null) DT.close();

			imv=im1+im2+im3;
			
			return imv;
		} catch (Exception e) {
			return 0;
	    }		
		
	}




	// Aux
	
	public double round2(double val){
		int ival;
		
		val=(double) (100*val);
		double rslt=Math.round(val);
		rslt=Math.floor(rslt);
		
		ival=(int) rslt;
		rslt=(double) ival;
		
		return (double) (rslt/100);
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
