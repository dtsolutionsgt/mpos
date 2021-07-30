package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class clsDocCuenta extends clsDocument {

	private ArrayList<itemData> items= new ArrayList<itemData>();
	private ArrayList<itemData> bons= new ArrayList<itemData>();

	private double tot,desc,imp,stot,percep,totNotaC;
	private boolean sinimp;
	private String 	contrib,corelNotaC,asignacion,ccorel,corelF;
	private int decimp,totitems;

	public clsDocCuenta(Context context, int printwidth, String cursymbol, int decimpres, String archivo) {
		super(context, printwidth,cursymbol,decimpres, archivo);

		docfactura=true;
		docdevolucion=false;
		docpedido=false;
		docrecibo=false;
		decimp=decimpres;
	}

	protected boolean loadHeadData(String corel) {

		Cursor DT;
		String cli="",vend="",val,empp="", anulado,s1,s2;
		long ff;
		int impres, cantimpres;
				
		super.loadHeadData(corel);

        if (banderafel) nombre = "FACTURA ELECTRONICA"; nombre = "FACTURA";
		
		try {

			sql=" SELECT SERIE,CORELATIVO,RUTA,VENDEDOR,CLIENTE,TOTAL,DESMONTO,IMPMONTO,EMPRESA,FECHAENTR,ADD1," +
				" ADD2,IMPRES, ANULADO, FEELUUID, FEELFECHAPROCESADO, FEELSERIE, FEELNUMERO, FEELCONTINGENCIA " +
				" FROM D_FACTURA WHERE COREL='"+corel+"'";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0) {

				DT.moveToFirst();

				serie=DT.getString(0);
				numero=""+DT.getInt(1);
				ruta=DT.getString(2);

				vend=DT.getString(3);
				cli=DT.getString(4);

				tot=DT.getDouble(5);
				desc=DT.getDouble(6);
				imp=DT.getDouble(7);
				stot=tot+desc;

				empp=DT.getString(8);
				ffecha=DT.getInt(9);fsfecha=sfecha(ffecha)+" "+shora(ffecha);

				add1=DT.getString(10);
				add2=DT.getString(11);

				vendcod=vend; //#CKFK 20200516 Validar para que se hace esto

				anulado=DT.getString(13);
				impres=DT.getInt(12);
				cantimpres=0;

                feluuid=DT.getString(14);
                feldcert=sfecha(DT.getLong(15));
                s1=DT.getString(16);if (!s1.isEmpty() && !s1.equalsIgnoreCase(" ")) serie=s1;
                s2=DT.getString(17);if (!s2.isEmpty() && !s2.equalsIgnoreCase(" ")) numero=s2;
                felcont=DT.getString(18);
                contacc=felcont;

                if (anulado.equals("S")?true:false){
					cantimpres = -1;
				} else if (cantimpres == 0 && impres > 0){
                    if (esPendientePago(corel)){
                        cantimpres = -2;
                    }else{
                        cantimpres = 1;
                    }
				} else if (esPendientePago(corel)){
					cantimpres = -2;
				}

				if (cantimpres>0){
					if (facturaflag) {
						nombre = "COPIA DE FACTURA";
					}else {
						nombre = "COPIA DE TICKET";
					}
				}else if (cantimpres==-1){
					if (facturaflag) {
						nombre = "FACTURA ANULADA";
					}else {
						nombre = "TICKET ANULADO";
					}
				}else if (cantimpres==-2){
					nombre = "FACTURA PENDIENTE DE PAGO";
				}else if (cantimpres==0){
					if (facturaflag) {
					    if (banderafel) nombre = "FACTURA ELECTRONICA"; nombre = "FACTURA";
                    }else {
					    nombre = "TICKET";
                    }
				}

			}

		} catch (Exception e) {
			//Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();return false;
	    }

        try {
            sql="SELECT NOMBRE FROM P_RUTA WHERE CODIGO_RUTA="+ruta;
            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            rutanombre=DT.getString(0);
        } catch (Exception e) {
            rutanombre=ruta;
        }

        try {
            sql="SELECT NOMBRE FROM VENDEDORES WHERE CODIGO_VENDEDOR="+vend;
            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            vendnom=DT.getString(0);
        } catch (Exception e) {
            vendnom=""+vend;
        }

        try {
			sql="SELECT RESOL,FECHARES,FECHAVIG,SERIE,CORELINI,CORELFIN FROM P_COREL WHERE (RUTA="+ruta+") AND (RESGUARDO=0)";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			resol="Resolucion No. : "+DT.getString(0);
			ff=DT.getLong(1);
			resfecha="De Fecha: "+sfecha_dos(ff);
			ff=DT.getLong(2);
			resvence="Vigente hasta: "+sfecha_dos(ff);
			//#EJC20181130: Se cambió el mensaje por revisión de auditor de SAT.
//			ff=DT.getInt(2);resvence="Resolucion vence : "+sfecha(ff);
			resrango="Serie : "+DT.getString(3)+" del "+DT.getInt(4)+" al "+DT.getInt(5);
			
		} catch (Exception e) {
			//Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();return false;
	    }	
		
		try {
            sinimp=false;
			
		} catch (Exception e) {
			sinimp=false;
	    }	
				
		val=vend;
		vendedor=val;
		
		try {
			sql="SELECT NOMBRE,PERCEPCION,TIPO_CONTRIBUYENTE,DIRECCION,NIT,DIACREDITO " +
				"FROM P_CLIENTE WHERE CODIGO_CLIENTE ='"+cli+"'";

			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			val=DT.getString(0);
            percep=DT.getDouble(1);
            
            contrib=""+DT.getString(2);
			if (contrib.equalsIgnoreCase("C")) sinimp=true;
			if (contrib.equalsIgnoreCase("F")) sinimp=false;
			
			clicod=cli;
			clidir=DT.getString(3);
			nit_cliente =DT.getString(4);
			diacred=DT.getInt(5);
			
		} catch (Exception e) {
			val=cli;
	    }

		try {
			sql="SELECT TIPO FROM D_FACTURAP WHERE (COREL='"+corel+"') AND (TIPO='E')";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0) {
			    pagoefectivo=1;
            } else {
			    pagoefectivo=0;
            }
		} catch (Exception e) {
            pagoefectivo=0;
		}

		try {

			sql="SELECT NOMBRE,NIT,DIRECCION FROM D_FACTURAF WHERE COREL='"+corel+"'";
			DT=Con.OpenDT(sql);	
			DT.moveToFirst();
			
			nombre_cliente =DT.getString(0);
		    nit_cliente =DT.getString(1);
          	clidir=DT.getString(2);
					
		} catch (Exception e) {
	    }


        add1=add1+" - ";
		add2=add2+" - ";
		
		return true;
		
	}

	protected boolean loadDocData(String corel) {

		Cursor DT;
		itemData item;

		items.clear();
		
		try {

    		sql="SELECT T_VENTA.PRODUCTO,P_PRODUCTO.DESCLARGA,T_VENTA.CANT,T_VENTA.PRECIODOC,T_VENTA.TOTAL " +
				"FROM T_VENTA INNER JOIN P_PRODUCTO ON T_VENTA.PRODUCTO = P_PRODUCTO.CODIGO";
			
			DT=Con.OpenDT(sql);
			DT.moveToFirst();
			totitems=DT.getCount();

            while (!DT.isAfterLast()) {

                item = new itemData();

                item.nombre = DT.getString(1);
                item.cant = DT.getDouble(2);
                item.prec = DT.getDouble(3);
                item.tot = DT.getDouble(4);

                items.add(item);

                DT.moveToNext();
            }

		} catch (Exception e) {
			Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
	    }		

		return true;
	}

	private void detalleCombo(String idcombo ) {

        clsD_facturasObj D_facturasObj=new clsD_facturasObj(cont,Con,db);
        clsP_productoObj P_productoObj=new clsP_productoObj(cont,Con,db);
        String prid,nombre;
        itemData item;

        D_facturasObj.fill("WHERE (COREL='"+ccorel+"') AND (ID="+idcombo+")");

        for (int i = 0; i <D_facturasObj.count; i++) {

            prid=D_facturasObj.items.get(i).producto;
            P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prid+")");
            nombre=P_productoObj.first().desclarga;

            item = new itemData();

            item.cod = prid;
            item.nombre = nombre;
            item.cant = 1;
            item.prec =0;
            item.imp = 0;
            item.descper = 0;
            item.desc = 0;
            item.tot = 0;
            item.um ="";
            item.ump ="";
            item.peso =0 ;
            item.flag=true;

            items.add(item);
        }

    }

	//region Detalle por empresa

	protected boolean buildDetail() {
        return detailBaseGUA();
	}

    protected boolean detailBaseGUA() {
        itemData item;
        String ss;

        rep.add("");rep.add("");rep.add("");
        rep.add(rep.ctrim(rutanombre));
        rep.add("");
        rep.line();
        rep.add(rep.ctrim("PREIMPRESION DE FACTURA"));
        rep.line();
        rep.add("");
        rep.add("Mesa : "+mesa);
        rep.add("");
        rep.add("Cuenta #"+cuenta);
        rep.add("");
        rep.add3sss("Cantidad ","Precio","Total");
        rep.line();

        for (int i = 0; i <items.size(); i++) {
            item=items.get(i);ss=""+((int) item.cant);
            rep.add(item.nombre);
            rep.add3lrr(rep.rtrim(ss,5),item.prec,item.tot);
        }

        rep.line();

        return true;
    }

  	//endregion

	//region Pie por empresa

	protected boolean buildFooter() {
	    return footerBaseGUA();
  	}

    private boolean footerBaseGUA() {

        if (pdesc!=0 | propfija) {
            if (pprop!=0 | propfija) {
                rep.addtotsp("Subtotal", (ptotal + pdesc-propvalor));
                if (pdesc != 0)  rep.addtotsp("Descuento", -pdesc);
                rep.addtotsp("Propina : ",propvalor);
            } else {
                if (pdesc != 0) {
                    rep.addtotsp("Subtotal", (ptotal + pdesc));
                    rep.addtotsp("Descuento", -pdesc);
                }
            }
        }

        rep.addtotsp("TOTAL A PAGAR ", ptotal);
        rep.add("");
        rep.add("Les atendio : ");
        rep.add(vendedor);
        rep.add("");

        if (pprop>0) {
            if (!propfija) {
                rep.add("Propina : ");
                rep.add("");
                rep.add("");
                rep.line();
            }
        }

        rep.add("");
        rep.add("Nombre cliente :");
        rep.add("");
        rep.line();
        rep.add("");
        rep.add("NIT : _________________");
        rep.add("");
        rep.add("Direccion :");
        rep.add("");
        rep.line();
        rep.add("Correo :");
        rep.add("");
        rep.line();
        rep.add(""); rep.add(""); rep.add(""); rep.add(""); rep.add("");

        return super.buildFooter();
    }

    //endregion

	//region Aux
	
	public double round2(double val){
		int ival;
		
		val=(double) (100*val);
		double rslt=Math.round(val);
		rslt=Math.floor(rslt);
		
		ival=(int) rslt;
		rslt=(double) ival;
		
		return (double) (rslt/100);
	}
	
	private class itemData {
		public String cod,nombre,um,ump;
		public double cant,peso,prec,imp,descper,desc,tot;
		public boolean flag;
	}

    private boolean esPendientePago(String corel){
        boolean vPendiente=false;
        return vPendiente;
    }

    //endregion

}
