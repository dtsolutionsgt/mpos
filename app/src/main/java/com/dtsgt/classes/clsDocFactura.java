package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class clsDocFactura extends clsDocument {

	private ArrayList<itemData> items= new ArrayList<itemData>();
	private ArrayList<itemData> bons= new ArrayList<itemData>();
    private ArrayList<itemData> pagos= new ArrayList<itemData>();

    //#EJC20210804: Variable global para parámetro de QR string.
    public String QRCodeStr = "";

    private double tot,desc,imp,stot,percep,propina,totNotaC;
	private boolean sinimp,detcombo;
	private String 	contrib,corelNotaC,asignacion,ccorel,corelF;
	private int decimp,totitems;

    private ArrayList<String> plines = new ArrayList<String>();

    private DecimalFormat ffrmnodec = new DecimalFormat("#,##0.##");

    Bitmap bitmap;  //QRGEncoder qrgEncoder;
    ImageView qrImage;
    String TAG = "GenerateQRCode";
    //String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    String qrpath = Environment.getExternalStorageDirectory().getPath() + "/";



    public clsDocFactura(Context context,int printwidth,String cursymbol,int decimpres, String archivo,boolean detallecombo) {
		super(context, printwidth,cursymbol,decimpres, archivo);

		docfactura=true;
		docdevolucion=false;
		docpedido=false;
		docrecibo=false;
		decimp=decimpres;
        detcombo=detallecombo;
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
				" ADD2,IMPRES, ANULADO, FEELUUID, FEELFECHAPROCESADO, FEELSERIE, FEELNUMERO, FEELCONTINGENCIA, EMPRESA " +
				" FROM D_FACTURA WHERE COREL='"+corel+"'";
			DT=Con.OpenDT(sql);

			if (DT.getCount()>0) {

				DT.moveToFirst();

				serie=DT.getString(0);
				numero=""+DT.getInt(1);
				ruta=DT.getString(2);codigo_ruta=ruta;

				vend=DT.getString(3);
				cli=DT.getString(4);

				tot=DT.getDouble(5);
				desc=DT.getDouble(6);
				imp=DT.getDouble(7);
				stot=tot+desc;

				empp=DT.getString(8);
				ffecha=DT.getLong(9);fsfecha=sfecha(ffecha)+" "+shora(ffecha);

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
                empid=DT.getInt(19);

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
			
			resol="Resolucion No.: "+DT.getString(0);
			ff=DT.getLong(1);
			resfecha="De Fecha: "+sfecha_dos(ff);
			ff=DT.getLong(2);
			resvence="Vigente hasta: "+sfecha_dos(ff);
			//#EJC20181130: Se cambió el mensaje por revisión de auditor de SAT.
//			ff=DT.getInt(2);resvence="Resolucion vence : "+sfecha(ff);
			resrango="Serie: "+DT.getString(3)+" del "+DT.getInt(4)+" al "+DT.getInt(5);
			
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

        //#EJC20210705: TipoCredito, NoAutorizacion;
        try {

            /*
            sql="SELECT A.DESC2 AS Tipo, A.DESC1 AS Autorizacion FROM T_PAGO A" +
                    "INNER JOIN P_MEDIAPAGO B" +
                    "ON A.CODPAGO = B.CODIGO\n" +
                    "INNER JOIN D_FACTURAP P\n" +
                    "ON A.ITEM = P.ITEM\n" +
                    "AND B.NIVEL = 4 AND (P.COREL='" +corel+ "')";
             */

            sql="SELECT DESC2,DESC1 FROM D_FACTURAP WHERE (TIPO='K') AND (COREL='" +corel+ "')";
            DT=Con.OpenDT(sql);
            if (DT.getCount()>0) {
                DT.moveToFirst();
                TipoCredito=DT.getString(0);
                NoAutorizacion=DT.getString(1);
            } else {
                TipoCredito="";
                NoAutorizacion="";
            }
        } catch (Exception e) {
            TipoCredito="";
            NoAutorizacion="";
        }

        plines.clear();

        sql="SELECT P.DESC2, SUM(P.VALOR) " +
                "FROM P_MEDIAPAGO M INNER JOIN D_FACTURAP P ON P.CODPAGO = M.CODIGO " +
                "WHERE (COREL='" +corel+ "') GROUP BY P.DESC2";

        try {
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                while (!DT.isAfterLast()) {
                    s1=DT.getString(0);if (s1.isEmpty()) s1="Contado";
                    plines.add(addtotsp(s1,DT.getDouble(1)));
                    DT.moveToNext();
                }
            }

        } catch (Exception e) {
        }


        propina=0;
        try {
            sql="SELECT PROPINA FROM D_FACTURAPR WHERE (COREL='"+corel+"')";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                propina=DT.getDouble(0);
             }
        } catch (Exception e) {
            propina=0;
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

        add1=add1+"";
		add2=add2+" - ";

		//#EJC20210729: GET NIT EMISOR.
        try {
            sql="SELECT NIT FROM P_SUCURSAL ";
            DT=Con.OpenDT(sql);
            if(DT!=null){
                DT.moveToFirst();
                if (DT.getCount()>0) {
                    nit_emisor=DT.getString(0);

                } else {
                    nit_emisor="";
                }
            }
        } catch (Exception e) {

        }

        //region #EJC20210729: QR CODE
        String Numero_Factura=feluuid;
        if (Numero_Factura.equalsIgnoreCase(" ")) {
            Numero_Factura=contacc;
        }

        nit_emisor=nit_emisor.trim();
        nit_emisor=nit_emisor.replace("-","");
        nit_emisor=nit_emisor.replace(".","");
        nit_emisor=nit_emisor.replace(" ","");
        nit_emisor=nit_emisor.toUpperCase();

        nit_cliente=nit_cliente.trim();
        nit_cliente=nit_cliente.replace("-","");
        nit_cliente=nit_cliente.replace(".","");
        nit_cliente=nit_cliente.replace(" ","");
        nit_cliente=nit_cliente.toUpperCase();

        QRCodeStr= "https://felpub.c.sat.gob.gt/verificador-web/publico/vistas/verificacionDte.jsf?tipo=autorizacion&" +
                "numero="+ Numero_Factura + "&emisor="+ nit_emisor +"&receptor="+ nit_cliente +"&monto=" + stot;

//        if (!QRCodeStr.isEmpty()) {
//            try {
//                qrgEncoder = new QRGEncoder(QRCodeStr, null, QRGContents.Type.TEXT, 350);
//                bitmap = qrgEncoder.encodeAsBitmap();
//                if (!QRGSaver.save(qrpath, "qr", bitmap, QRGContents.ImageType.IMAGE_JPEG)) {
//                    throw new Exception("Error al guardar la barra");
//                }
//            } catch (Exception e) {
//                Toast.makeText(cont, "Error QR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }

		return true;
	}

	protected boolean loadDocData(String corel) {

		Cursor DT;
		itemData item,bon,pag;
		String corNota,idcombo;
		int corrl;

		ccorel=corel;

		loadHeadData(corel);
		
		items.clear();bons.clear();pagos.clear();
		
		try {

           //#CKFK 20200520 quité la consulta que buscaba en las notas de crédito porque aquí no existe esa tabla

			sql="SELECT D_FACTURAD.PRODUCTO,P_PRODUCTO.DESCLARGA,D_FACTURAD.CANT,D_FACTURAD.PRECIODOC,D_FACTURAD.IMP, " +
				"D_FACTURAD.DES,D_FACTURAD.DESMON, D_FACTURAD.TOTAL, D_FACTURAD.UMVENTA, D_FACTURAD.UMPESO, D_FACTURAD.PESO, D_FACTURAD.VAL1, D_FACTURAD.VAL2 " +
				"FROM D_FACTURAD INNER JOIN P_PRODUCTO ON D_FACTURAD.PRODUCTO = P_PRODUCTO.CODIGO_PRODUCTO " +
				"WHERE (D_FACTURAD.COREL='"+corel+"')";	
			
			DT=Con.OpenDT(sql);
			DT.moveToFirst();
			totitems=DT.getCount();

			while (!DT.isAfterLast()) {

                item = new itemData();
                item.cod = DT.getString(0);
                item.nombre = DT.getString(1);
                item.cant = DT.getDouble(2);
                item.prec = DT.getDouble(3);
                item.imp = DT.getDouble(4);
                item.descper = DT.getDouble(5);
                item.desc = DT.getDouble(6);
                item.tot = DT.getDouble(7);
                item.um = DT.getString(8);
                item.ump = DT.getString(9);
                item.peso = DT.getDouble(10);
                if (sinimp) item.tot = item.tot - item.imp;
                item.flag=false;
                items.add(item);

                if (DT.getDouble(11)==1) {
                    if (detcombo) detalleCombo(DT.getString(12));
                }

                DT.moveToNext();
            }

			try {
				//#CKFK 20200520 Quité el union con D_BONIFBARRA porque esa tabla no existe en el MPOS
				sql = "SELECT D_BONIF.PRODUCTO,P_PRODUCTO.DESCLARGA AS NOMBRE,D_BONIF.CANT, D_BONIF.UMVENTA, " +
					  "D_BONIF.CANT*D_BONIF.FACTOR AS TPESO " +
					  "FROM D_BONIF INNER JOIN P_PRODUCTO ON D_BONIF.PRODUCTO = P_PRODUCTO.CODIGO_PRODUCTO " +
					  "WHERE (D_BONIF.COREL='" + ccorel + "')";

				DT=Con.OpenDT(sql);
				if (DT.getCount()>0) DT.moveToFirst();

				while (!DT.isAfterLast()) {

					bon = new itemData();

					bon.cod = DT.getString(0);
					bon.nombre = DT.getString(1);
					bon.cant = DT.getDouble(2);
					bon.um = DT.getString(3);
					bon.peso = DT.getDouble(4);

					bons.add(bon);
					DT.moveToNext();
				}

			} catch (Exception e) {
				Toast.makeText(cont,"Impresion bonif : "+e.getMessage(), Toast.LENGTH_LONG).show();
			}

			// Medias de pago
            try {

                sql="SELECT P_MEDIAPAGO.NOMBRE, SUM(D_FACTURAP.VALOR) " +
                        "FROM   D_FACTURAP INNER JOIN P_MEDIAPAGO ON D_FACTURAP.CODPAGO=P_MEDIAPAGO.CODIGO " +
                        "WHERE  (D_FACTURAP.COREL='" + ccorel + "') GROUP BY P_MEDIAPAGO.NOMBRE";

                DT=Con.OpenDT(sql);
                if (DT.getCount()>0) DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    pag = new itemData();

                    pag.nombre = DT.getString(0);
                    pag.cant = DT.getDouble(1);

                    pagos.add(pag);
                    DT.moveToNext();
                }

            } catch (Exception e) {
                Toast.makeText(cont,"Impresion pagos : "+e.getMessage(), Toast.LENGTH_LONG).show();
            }

		} catch (Exception e) {
			Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
	    }		
		
		return true;
	}

	private void detalleCombo(String idcombo ) {

        clsD_facturacObj D_facturacObj=new clsD_facturacObj(cont,Con,db);
        String nombre;
        itemData item;

        D_facturacObj.fill("WHERE (COREL='"+ccorel+"') AND (IDCombo="+idcombo+")");

        for (int i = 0; i <D_facturacObj.count; i++) {

            nombre=D_facturacObj.items.get(i).nombre;

            item = new itemData();

            item.cod = "";
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

    private void detalleComboOrig(String idcombo ) {

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
	    /*
	    if (modofact.equalsIgnoreCase("GUA")) {
            return detailBaseGUA();
        } else {
            return detailBase();
        }
        */
        if (pais.equalsIgnoreCase("GUA")) {
            return detailBaseGUA();
        } else if (pais.equalsIgnoreCase("HON")) {
            return detailBaseHON();
        } else {
            return detailBaseGUA();
        }
	}

    protected boolean detailBaseHON() {

        itemData item;
        String cu,cp;

        rep.add("HONDURAS");
        rep.add3sss("Cantidad ","Precio","Total");
        rep.line();

        for (int i = 0; i <items.size(); i++) {
            item=items.get(i);
            if (!item.flag) {
                rep.add(item.nombre);
                rep.add3lrr(rep.rtrim(""+item.cant,5),item.prec,item.tot);
            } else {
                rep.add("   - "+item.nombre);
            }
        }

        rep.line();

        return true;
    }

    protected boolean detailBaseGUA() {

        itemData item;
        String cu,cp;

        rep.add3sss("Cantidad ","Precio","Total");
        rep.line();

        for (int i = 0; i <items.size(); i++) {
            item=items.get(i);
            if (!item.flag) {
                rep.add(item.nombre);
                rep.add3lrr(rep.rtrim(""+item.cant,5),item.prec,item.tot);
            } else {
                rep.add("   - "+item.nombre);
            }
        }

        rep.line();

        return true;
    }

    protected boolean detailToledano() {

		itemData item;
		String ss;


		rep.add("CODIGO   DESCRIPCION        UM  CANT");
		rep.add("       KGS    PRECIO           VALOR");
		rep.line();

		for (int i = 0; i <items.size(); i++) {
			item=items.get(i);

			ss=rep.ltrim(item.cod+" "+item.nombre,prw-10);
			ss=ss+rep.rtrim(item.um,4)+" "+rep.rtrim(frmdecimal(item.cant,2),5);
			rep.add(ss);
			ss=rep.rtrim(frmdecimal(item.peso,decimp),10)+" "+rep.rtrim(frmdecimal(item.prec,2),8);
			ss=rep.ltrim(ss,prw-10);
			ss=ss+" "+rep.rtrim(frmdecimal(item.tot,2),9);
			rep.add(ss);

		}

		rep.line();


		return true;
	}

	protected boolean detailBase() {

		itemData item;
		String cu,cp;


		rep.add3fact("Cantidad      Peso","  Precio","Total");
		rep.line();

		for (int i = 0; i <items.size(); i++) {
			item=items.get(i);
			rep.add(item.nombre);
			//rep.add3lrr(rep.rtrim(""+item.cant,5),item.prec,item.tot);

			cu=frmdecimal(item.cant,decimp)+" "+rep.ltrim(item.um,6);
			cp=frmdecimal(0,decimp)+" "+rep.ltrim(item.ump,3);

			rep.add3fact(cu+" "+cp,item.prec,item.tot);
		}

		rep.line();

		return true;
	}

	//endregion

	//region Bonificaciones

	private void bonificaciones() {

		itemData item;
		String ss;

		if (bons.size()==0) return;

		rep.line();
		rep.add("----   B O N I F I C A C I O N  ----");
		rep.line();
		rep.add("CODIGO   DESCRIPCION        UM  CANT");
		rep.add("       KGS    ");
		rep.line();

		for (int i = 0; i <bons.size(); i++) {

			item=bons.get(i);

			ss=rep.ltrim(item.cod+" "+item.nombre,prw-10);
			ss=ss+rep.rtrim(item.um,4)+" "+rep.rtrim(frmdecimal(item.cant,2),5);
			rep.add(ss);
			ss=rep.rtrim(frmdecimal(item.peso,2),10);
			ss=rep.ltrim(ss,prw-10);
			rep.add(ss);

		}

		rep.line();

		rep.add("");
		rep.add("");
		rep.add("");
		rep.add("");
	}

    //endregion

	//region Pie por empresa

	protected boolean buildFooter() {
        /*if (modofact.isEmpty()) {
        //if (modofact.equalsIgnoreCase("GUA")) {
            if (facturaflag) { // Factura
                return footerBaseGUA();
            } else { // Ticket
                return footerBaseGUATicket();
            }
        } else {
            return footerBaseGUA();
        }*/

        if (pais.equalsIgnoreCase("GUA")) {
            if (facturaflag) {
                return footerBaseGUA();
            } else {
                return footerBaseGUATicket();
            }
        } else if (pais.equalsIgnoreCase("HON")) {
            return footerBaseHON();
        } else {
            return footerBaseGUA();
        }
	}

    private boolean footerBaseGUATicket() {

        double totimp,totperc;

        if (factsinpropina) {
            stot = stot - propina;
            tot = tot - propina;
            if (desc != 0) {
                rep.addtotsp("Subtotal: ", stot);
                rep.addtotsp("Descuento: ", -desc);
            }
        } else {
            if (desc != 0 | propina != 0) {
                stot = stot - propina;
                rep.addtotsp("Subtotal: ", stot);
            }
            if (desc != 0) rep.addtotsp("Descuento: ", -desc);
            if (propina != 0) rep.addtotsp("Propina: ", propina);
        }

        rep.addtotsp("TOTAL A PAGAR: ", tot);
        rep.add("");

        rep.add("Detalle pago : ");
        for (int i = 0; i <pagos.size(); i++) {
            rep.addtotDS(pagos.get(i).nombre,pagos.get(i).cant);
        }

        //rep.addc("----------------------");
        //rep.addc("Firma cliente  ");
        rep.add("");
        //rep.addc("ESTE DOCUMENTO ");
        //rep.addc("NO ES UNA FACTURA COMERCIAL");
        //rep.add("");
        //rep.addc(sfticket);
        rep.add("");
        rep.add("");
        rep.add("");
        rep.add("");

        return super.buildFooter();
    }

    private boolean footerBaseGUA() {

        double totimp,totperc;

        if (sinimp) {

            stot=stot-imp;
            totperc=stot*(percep/100);totperc=round2(totperc);
            totimp=imp-totperc;

            rep.addtotsp("Subtotal: ", stot);
            rep.addtotsp("Impuesto : ", totimp);
            if (contrib.equalsIgnoreCase("C")) rep.addtotsp("Percepcion : ", totperc);
            rep.addtotsp("Descuento: ", -desc);
            rep.addtotsp("TOTAL : ", tot);

            rep.add("Honduras");

        } else {

            if (factsinpropina) {
                stot = stot - propina;
                tot = tot - propina;
                if (desc != 0) {
                    rep.addtotsp("Subtotal: ", stot);
                    rep.addtotsp("Descuento: ", -desc);
                }
            } else {
                if (desc != 0 | propina != 0) {
                    stot = stot - propina;
                    rep.addtotsp("Subtotal: ", stot);
                }
                if (desc != 0) rep.addtotsp("Descuento: ", -desc);
                if (propina != 0) rep.addtotsp("Propina: "+ffrmnodec.format(propperc) + "% ", propina);
            }

            rep.addtotsp("TOTAL A PAGAR: ", tot);
        }

        if (plines.size()>0) {
            rep.add("");
            rep.add("Desglose de pago:");
            for (int ii= 0; ii <plines.size(); ii++) {
                rep.add(plines.get(ii));
            }
        }

        //#AT20230102 Se muestra abajo del desglose de pago
        if (parallevar){
            rep.add("");
            rep.addc("PARA LLEVAR");
            rep.add("");
        }

        if (modorest) {
            rep.add("");
            rep.add("Le atendió: "+nommesero);
        }

        rep.add("");

        try {
            if (!textopie.isEmpty()) {
                rep.addc(textopie);
            }
        } catch (Exception e) {}

        //banderafel=true;
        if (banderafel) {

            if (feluuid.equalsIgnoreCase(" ")) {
                rep.add("");
                rep.add("Factura generada en modo de contingencia");
                rep.add("Numero de Acceso: "+contacc);
                rep.add("Su factura pueden encontrar en el portal");
                rep.add("SAT bajo identificacion: "+serie+numero);
            }

            if (!feluuid.equalsIgnoreCase(" ")) {
                rep.add("");
                rep.add("Número de autorización: ");
                rep.add(feluuid);
                rep.add("Fecha de certificación: "+feldcert);
            }

            if (!felIVA.isEmpty()) {
                rep.add(felIVA);
            }
            if (!felISR.isEmpty()) {
                rep.add(felISR);
                if (!felISR2.isEmpty()) {
                    rep.add(felISR2);
                }
            }

            rep.add("");
            rep.add(felcert);
            rep.add(felnit);
            rep.add("");
            rep.addc("Powered by DTSolutions, S.A.");
            rep.addc("dts.com.gt");

        }

        //#HS_20181212 Validación para factura pendiente de pago
        if(pendiente == 4){
            rep.add("");
            rep.add("ESTE NO ES UN DOCUMENTO LEGAL");
            rep.add("EXIJA SU FACTURA ORIGINAL");
            rep.add("");
        }

        if (impresionorden) {
            String sod=add1;
            if (!sod.isEmpty()) {
                rep.add("");
                rep.addc("************************");
                rep.add("");
                rep.addc("ORDEN # "+sod.toUpperCase());
                rep.add("");
                rep.addc("************************");
                rep.add("");
            }

        } else {
            rep.add("");
        }

        rep.add("");
        //rep.addc("Powered by: dts.com.gt");

        return super.buildFooter();
    }

    private boolean footerBaseHON() {

        double totimp,totperc;

        if (sinimp) {
            stot=stot-imp;
            totperc=stot*(percep/100);totperc=round2(totperc);
            totimp=imp-totperc;

            rep.addtotsp("Subtotal: ", stot);
            rep.addtotsp("Impuesto : ", totimp);
            if (contrib.equalsIgnoreCase("C")) rep.addtotsp("Percepcion : ", totperc);
            rep.addtotsp("Descuento: ", -desc);
            rep.addtotsp("TOTAL : ", tot);

        } else {

            if (factsinpropina) {
                stot = stot - propina;
                tot = tot - propina;
                if (desc != 0) {
                    rep.addtotsp("Subtotal: ", stot);
                    rep.addtotsp("Descuento: ", -desc);
                }
            } else {
                if (desc != 0 | propina != 0) {
                    stot = stot - propina;
                    rep.addtotsp("Subtotal: ", stot);
                }
                if (desc != 0) rep.addtotsp("Descuento: ", -desc);
                if (propina != 0) rep.addtotsp("Propina: "+ffrmnodec.format(propperc)+" % ", propina);
            }

            rep.addtotsp("TOTAL A PAGAR: ", tot);
        }

        rep.add("HONDURAS");

        if (plines.size()>0) {
            rep.add("");
            rep.add("Desglose de pago:");
            for (int ii= 0; ii <plines.size(); ii++) {
                rep.add(plines.get(ii));
            }
        }

        if (modorest) {
            rep.add("");
            rep.add("Le atendio: "+nommesero);
        }

        rep.add("");

        try {
            if (!textopie.isEmpty()) {
                rep.addc(textopie);
            }
        } catch (Exception e) {}

        //banderafel=true;
        if (banderafel) {

            if (feluuid.equalsIgnoreCase(" ")) {
                rep.add("");
                rep.add("Factura generada en modo de contingencia");
                rep.add("Numero de Acceso: "+contacc);
                rep.add("Su factura pueden encontrar en el portal");
                rep.add("SAT bajo identificacion: "+serie+numero);
            }

            if (!feluuid.equalsIgnoreCase(" ")) {
                rep.add("");
                rep.add("Número de autorización: ");
                rep.add(feluuid);
                rep.add("Fecha de certificación: "+feldcert);
            }

            if (!felIVA.isEmpty()) {
                rep.add(felIVA);
            }
            if (!felISR.isEmpty()) {
                rep.add(felISR);
                if (!felISR2.isEmpty()) {
                    rep.add(felISR2);
                }
            }

            rep.add("");
            rep.add(felcert);
            rep.add(felnit);
            rep.add("");
            rep.add("Powered by DTSolutions, S.A.");
            rep.addc("dts.com.gt");
        }

        //#HS_20181212 Validación para factura pendiente de pago
        if(pendiente == 4){
            rep.add("");
            rep.add("ESTE NO ES UN DOCUMENTO LEGAL");
            rep.add("EXIJA SU FACTURA ORIGINAL");
            rep.add("");
        }

        if (parallevar){
            rep.add("");
            rep.addc("PARA LLEVAR");
            rep.add("");
        }


        if (impresionorden) {
            String sod=add1;
            if (!sod.isEmpty()) {
                rep.add("");
                rep.addc("************************");
                rep.add("");
                rep.addc("ORDEN: # "+sod.toUpperCase());
                rep.add("");
                rep.addc("************************");
                rep.add("");
            }

        } else {
            rep.add("");
        }

        rep.add("");

        return super.buildFooter();
    }

	private boolean footerToledano() {
		double totimp, totperc,totalNotaC;

		stot = stot - imp;
		totperc = stot * (percep / 100);
		totperc = round2(totperc);
		totimp = imp - totperc;
		totalNotaC =   tot - totNotaC;

		rep.addtotsp("Subtotal", stot);

		if (corelF.equals(asignacion)) {

			rep.addtotsp("Nota de Credito", totNotaC);
			rep.addtotsp("ITBM:", totimp);
			rep.addtotsp("Total:", totalNotaC);
			rep.add("");
			rep.add("");
			rep.add("Total de items: "+totitems);
			rep.add("");
			bonificaciones();
			rep.add("");
			rep.line();
			rep.addc("Firma Cliente");
			rep.add("");
			rep.addc("Se aplico nota de crédito: "+corelNotaC);
			rep.add("");
			rep.addc("DE SER UNA VENTA AL CREDITO, SOLAMEN");
			rep.addc("TE NUESTRO CORRESPONDIENTE RECIBO SE");
			rep.addc("CONSIDERARA COMO EVIDENCIA  DE  PAGO");
			rep.add("");

			rep.add("Serial : "+deviceid);
			rep.add(resol);
			rep.add(resfecha);
			rep.add("");

		} else {

			rep.addtotsp("ITBM", totimp);
			rep.addtotsp("Total", tot);
			rep.add("");
			rep.add("");
			rep.add("Total de items: "+totitems);
			rep.add("");
			bonificaciones();
			rep.add("");
			rep.line();
			rep.addc("Firma Cliente");
			rep.add("");

			if (pendiente!=4){
				rep.addc("DE SER UNA VENTA AL CREDITO, SOLAMEN");
				rep.addc("TE NUESTRO CORRESPONDIENTE RECIBO SE");
				rep.addc("CONSIDERARA COMO EVIDENCIA  DE  PAGO");
				rep.add("");
			}

			rep.add("Serial : "+deviceid);
			rep.add(resol);
			rep.add(resfecha);
			rep.add("");

		}

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

    private String addtotsp(String s1,double val) {
        String sval;
        int size = 12;

        //#AT20221214 Se suma 1 al valor 12 para que se utilicen 39 espacios.
        if (prw == 40) {
            size += 1;
        }

        sval=cursymbol+decfrm.format(val);
        return ltrim(s1,prw-14)+""+rtrim(sval,size)+"  ";
    }

    private String ltrim(String ss,int sw) {
        int l=ss.length();
        if (l>sw) {
            ss=ss.substring(0,sw);
        } else {
            String frmstr="%-"+sw+"s";
            ss=String.format(frmstr,ss);
        }

        return ss;
    }

    private String rtrim(String ss,int sw) {
        int sl,l;
        String sp="";

        ss=ss.trim();
        l=ss.length();

        if (l>=sw) {
            ss=ss.substring(0,sw);
        } else {
            String frmstr="%"+sw+"s";
            ss=String.format(frmstr,ss);
        }

        return ss;
    }

    //endregion

}
