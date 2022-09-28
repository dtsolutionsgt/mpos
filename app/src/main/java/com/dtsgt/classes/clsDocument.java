package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.DateUtils;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class clsDocument {

	public String nombre,numero,serie,ruta,rutanombre, nombre_cliente, nit_emisor, nit_cliente,tipo,ref,vendedor,codigo_ruta;
	public String resol,resfecha,resvence,resrango,fsfecha,modofact,fecharango,textofin;
	public String felcert,felnit,feluuid,feldcert,felIVA,felISR,felISR2,felcont,contacc,nitsuc,sfticket;
	public String tf1="",tf2="",tf3="",tf4="",tf5="",add1="",add2="",deviceid,mesa,cuenta,nommesero;
	public clsRepBuilder rep;
	public boolean docfactura,docrecibo,docanul,docpedido,docdevolucion,doccanastabod;
	public boolean docdesglose,pass,facturaflag,banderafel,propfija,impresionorden;
	public boolean parallevar,factsinpropina,modorest;
	public long ffecha;
    public int pendiente,diacred,pagoefectivo,empid;
	//#EJC20210705
	public String TipoCredito, NoAutorizacion;
	public double ptotal,pdesc,pprop,propvalor,propperc;

	//#CKFK 20210705
    public boolean es_pickup, es_delivery;

	protected android.database.sqlite.SQLiteDatabase db;
	protected BaseDatos Con;
	protected String sql;
	
	protected ArrayList<String> lines= new ArrayList<String>();
	
	protected Context cont;
	protected DateUtils DU;
	protected DecimalFormat decfrm;
	
	protected String clicod,clidir,pemodo;
	protected String vendcod,vendnom;

	protected int prw;
	
	public clsDocument(Context context,int printwidth,String cursym,int decimpres, String archivo) {
		cont=context;
		prw=printwidth;
		
		rep=new clsRepBuilder(cont,prw,true,cursym,decimpres, archivo);
		DU=new DateUtils();
		decfrm = new DecimalFormat("#,##0.00");

	}

	public boolean buildPrint(String corel,int reimpres) {
		setAddlog("Build print",""+DU.getActDateTime(),"");

		modofact="*";
		rep.clear();
				
		if (!buildHeader(corel,reimpres)) return false;
		if (!buildDetail()) return false;
		if (!buildFooter()) return false;
		
 		if (!rep.save()) return false;
		
		return true;
	}

	public boolean buildPrint(String corel,int reimpres,String modo) {
		int flag;

        modofact=modo;
		rep.clear();

		try{

			if (!buildHeader(corel,reimpres)) return false;
			if (!buildDetail()) return false;
			if (!buildFooter()) return false;

			flag=0;

			if (modofact.equalsIgnoreCase("TOL")) {
				if (docfactura && (reimpres==10)) flag=1;
				if (docfactura && (reimpres==4) || docdesglose) flag=0;
				if (doccanastabod){
					if (reimpres==1){
						flag=1;
					}else{
						flag=0;
					}
				}
				if (docrecibo && (reimpres==0)) flag=0;
				if (docdevolucion && (reimpres==1)) flag = 1;
				if (docpedido && (reimpres==1)) flag = 1;
			} else if(modofact.equalsIgnoreCase("*")) {
				if (doccanastabod) flag = 0;
				if (docdevolucion || docpedido) flag = 1;
			}

			if (flag==0) {
				if (!rep.save()) return false;
			} else if (flag==1){
				if (!rep.save(2)) return false;
			} else if (flag==2){
				if (!rep.save(3)) return false;
			} else if (flag==3){
				if (!rep.save(3)) return false;
			}

		}catch (Exception e){
			setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

		return true;
	}

    public boolean buildPrint(String corel,int reimpres,String modo,boolean esfactura) {

        int flag;

        modofact=modo;
        facturaflag=esfactura;
        rep.clear();

        try{

            if (!buildHeader(corel,reimpres)) return false;
            if (!buildDetail()) return false;
            if (!buildFooter()) return false;

            flag=0;

            if (modofact.equalsIgnoreCase("TOL")) {
                if (docfactura && (reimpres==10)) flag=1;
                if (docfactura && (reimpres==4) || docdesglose) flag=0;
                if (doccanastabod){
                    if (reimpres==1){
                        flag=1;
                    }else{
                        flag=0;
                    }
                }
                if (docrecibo && (reimpres==0)) flag=0;
                if (docdevolucion && (reimpres==1)) flag = 1;
                if (docpedido && (reimpres==1)) flag = 1;
            } else if(modofact.equalsIgnoreCase("*")) {
                if (doccanastabod) flag = 0;
                if (docdevolucion || docpedido) flag = 1;
            }

            if (flag==0) {
                if (!rep.save()) return false;
            } else if (flag==1){
                if (!rep.save(2)) return false;
            } else if (flag==2){
                if (!rep.save(3)) return false;
            } else if (flag==3){
                if (!rep.save(3)) return false;
            }

        }catch (Exception e){
            setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;
    }

    public boolean buildPrint(String pmesa,String pcuenta,double total,double desc,double prop,boolean ppropfija,double ppropvalor) {
	    rep.clear();

        try{
            mesa=pmesa;cuenta=pcuenta;
            ptotal=total;pdesc=desc;
            pprop=prop;propfija=ppropfija;propvalor=ppropvalor;propperc=prop;

            if (!buildHeader()) return false;
            if (!buildDetail()) return false;
            if (!buildFooter()) return false;

            if (!rep.save()) return false;

        } catch (Exception e){
            setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;
    }

    public boolean buildPrintAppend(String corel,int reimpres,String modo) {
		int flag;

		modofact=modo;
		rep.clear();

		if (!buildHeader(corel,reimpres)) return false;
		if (!buildDetail()) return false;
		if (!buildFooter()) return false;

		flag=0;

		if (modofact.equalsIgnoreCase("TOL")) {
			if (docfactura && (reimpres==10)) flag=1;
			if (docfactura && (reimpres==4) || docdesglose) flag=0;
			if (doccanastabod) flag=2;
			if (docrecibo && (reimpres==0)) flag=0;
			if (docdevolucion && (reimpres==1)) flag = 1;
		} else if(modofact.equalsIgnoreCase("*")) {
			if (doccanastabod) flag = 2;
			if (docdevolucion || docpedido) flag = 1;
		}

		if (flag==0) {
			if (!rep.saveappend()) return false;
		} else if (flag==1){
			if (!rep.saveappend(2)) return false;
		} else if (flag==2){
			if (!rep.saveappend(3)) return false;
		} else if (flag==3){
			if (!rep.saveappend(3)) return false;
		}

		return true;
	}
	
	public boolean buildPrint(String corel,int reimpres,BaseDatos pCon,android.database.sqlite.SQLiteDatabase pdb ) {
		
		rep.clear();
				
		if (!buildHeader(corel,reimpres,pCon,pdb)) return false;
		if (!buildDetail()) return false;
		if (!buildFooter()) return false;
		
		if (!rep.save()) return false;
		
		return true;
	}
	
	public boolean buildPrintExt(String corel,int reimpres,String modo) {
		
		pemodo=modo;
		
		rep.clear();
		
		if (!buildHeader(corel,0)) return false;
		if (!buildDetail()) return false;
		if (!buildFooter()) return false;
		
		if (!buildHeader(corel,reimpres)) return false;
		if (!buildDetail()) return false;
		if (!buildFooter()) return false;
		
		if (!rep.save()) return false;
		
		return true;
	}
	
	public boolean buildPrintSimple(String corel,int reimpres) {
		
		rep.clear();
		
		if (!buildFooter()) return false;
		
		if (!rep.save()) return false;
		
		return true;
	}


	// Methods Prototypes
	
	protected boolean buildDetail() {
		return true;
	}
	
	protected boolean buildFooter() {
		return true;
	}
	
	protected boolean loadDocData(String corel) {
		return true;
	}
	
	protected boolean loadHeadData(String corel) {
		nombre="";numero="";serie="";ruta="";vendedor="";
        nombre_cliente ="";
		return true;
	}

    protected void saveHeadLines(int reimpres) {
        String s,ss,ss2,su;
        String[] s2;
		int nidx;

        rep.empty();rep.empty();

        if (docfactura) {
            if (!facturaflag) {
                rep.addc("TICKET");
                rep.add("");
            }
        }

        for (int i = 0; i <lines.size(); i++) 		{

            s=lines.get(i);if (s.isEmpty()) s=" ";

            try {
                s=encabezado(s);
                ss=s.toUpperCase();
                nidx=ss.indexOf("NIT");
                //if (nidx>=0) s="NIT : "+nitsuc;
            } catch (Exception e) {
                s="##";
            }

            if (s.contains("%%")) {
                if (banderafel) rep.add("DOCUMENTO TRIBUTARIO ELECTRONICO");
                rep.add(nombre);
                s=s.replace("%%%","");
            }

            if (docpedido) {
                s=s.replace("Factura serie","Pedido");
                s=s.replace("numero : 0","");
            }

            if (docrecibo) {
				s=s.replace("Factura","Recibo");
            }

            if (docdevolucion) {
				s=s.replace("Factura","Recibo");
			}

			if (doccanastabod){
				s=s.replace("Factura","Recibo");
			}

			if (!s.equalsIgnoreCase("##") && !s.equalsIgnoreCase("@@")) {
			    su=s.toUpperCase();
			    if (su.contains("CLIENTE") ) {
                    if (su.contains("<<") ) {
                        s2=s.split("<<");
                        for (int j = 1; j <s2.length; j++) {
                            ss2=s2[j];
                            rep.add(ss2);
                        }
                    } else {
                        rep.add(s);
                    }
                } else {
			        s=rep.ctrim(s);
                    rep.add(s);
                }
            }

            if (docfactura) {

                //if (!modofact.equalsIgnoreCase("TICKET")) {
                if (facturaflag) {
                    if (i==7){
                        if (!banderafel) {
                            rep.add("");
                            if (docfactura) {
                                rep.add(resol);
                                rep.add(resfecha);
                                rep.add(resvence);
                                rep.add(resrango);
                            }
                        }
                    }
                }
            }
        }

        rep.add("Fecha : "+fsfecha);

        if (docfactura){

            if (!emptystr(nit_cliente)) rep.add("NIT : "+nit_cliente);

            if (!emptystr(clidir)) {

                clidir="Dir : "+clidir;

                if (clidir.length()>prw) {

                    String nuevaCadena = "", cadena = "";

                    cadena = clidir;
                    nuevaCadena = cadena.substring(0, prw);rep.add(nuevaCadena);
                    cadena = cadena.substring(prw);
                    if (cadena.length() > prw) {
                        nuevaCadena =cadena.substring(0, prw);rep.add(nuevaCadena);
                        cadena = cadena.substring(prw);
                        if (cadena.length() > prw) {
                            nuevaCadena = cadena.substring(0, prw);rep.add(nuevaCadena);
                        } if (cadena.length()>0) rep.add(cadena);
                    } else {
                        if (cadena.length()>0) rep.add(cadena);
                    }
                } else rep.add(clidir);


                /*
				String nuevaCadena="", cadena="";
				int vMod=0;
				double division =0.0;

				cadena = "Dir : "+clidir;
				vMod = (cadena.length() / 40)+1;

				if (cadena.length() > 39){

					for (int i = 0; i <vMod; i++) {
						if (cadena.length()>=40*(i+1)){
							nuevaCadena += cadena.substring((i*40),40) + "\n";
						}else{
							nuevaCadena += cadena.substring((i*40)-1,cadena.length());
						}
					}
				} else {
					nuevaCadena = cadena;
				}

            	rep.add(nuevaCadena);
            	*/

			}

            if (docfactura) {
                if (!facturaflag) {
                    rep.add("");
                    rep.add("Esto no es una factura fiscal");
                    rep.add("");
                }
            }

			if (pagoefectivo==1){
                rep.add("Condiciones de pago : Efectivo");
			} else {
			    if(!TipoCredito.isEmpty()){
                    rep.add("Condiciones de pago : ");
                    rep.add("Aut#: " + NoAutorizacion);
                } else {
                    rep.add("Condiciones de pago : Crédito");
                }
			}
		}

        rep.add("");
        if (es_pickup) rep.add("------- (RECOGER EN SITIO)  -------");
        if (es_delivery) rep.add("-------  (DELIVERY)  -------");

        if (docfactura && !(modofact.equalsIgnoreCase("TOL"))){

			rep.add("");
			if (docfactura && (reimpres==1)) rep.add("-------  R E I M P R E S I Ó N  -------");
			if (docfactura && (reimpres==10)) rep.add("-------  R E I M P R E S I Ó N  -------");
			if (docfactura && (reimpres==2)) rep.add("------  C O P I A  ------");
			if (docfactura && (reimpres==3)) rep.add("------       A N U L A D O      ------");
			//#HS_20181212 condición para factura pendiente de pago
			if(docfactura && (reimpres==4)) {
				rep.add("- P E N D I E N T E  D E  P A G O -");
				pendiente = reimpres;
			}
			if (docfactura && (reimpres==5)) rep.add("------  C O N T A B I L I D A D  ------");
			rep.add("");

		}else if ((docdevolucion || docpedido) && !(modofact.equalsIgnoreCase("TOL"))){

            //CKFK 2019-04-23 Consultar con Aaron
			rep.add("");
			if ((docdevolucion && (reimpres==1)) || (docpedido && (reimpres==1))) rep.add("-------  R E I M P R E S I O N  -------");
			if ((docdevolucion && (reimpres==2)) || (docpedido && (reimpres==2))) rep.add("------  C O P I A  ------");
			if ((docdevolucion && (reimpres==3)) || (docpedido && (reimpres==3))) rep.add("------       A N U L A D O      ------");
			rep.add("");

		}

		if (doccanastabod && !(modofact.equalsIgnoreCase("TOL"))){
			rep.add("");
			if (doccanastabod && (reimpres==1)) rep.add("-------  R E I M P R E S I O N  -------");
			rep.add("");
		}

    }

    protected String encabezado(String l) {

        String s,lu,a;
        int idx;

        if (l.isEmpty()) return " ";
        lu=l.trim();

        if (lu.length()==1 && lu.equalsIgnoreCase("N")) {
            //s=nombre;s=rep.ctrim(s);return s;
            return "##";
        }

        if (l.indexOf("dd-MM-yyyy")>=0) {
            //s=DU.sfecha(DU.getActDateTime());
            //l=l.replace("dd-MM-yyyy",s);return l;
            return DU.sfecha(DU.getActDateTime())+" "+DU.shora(DU.getActDateTime());
        }

        if (l.indexOf("HH:mm:ss")>=0) {
            //s=DU.shora(DU.getActDateTime());
            //l=l.replace("HH:mm:ss",s);return l;
            return "##";
        }

        idx=l.indexOf("@Serie");
        if (idx>=0) {
            if (docfactura) {
                /*
                if (l.length() > idx + serie.length()) {
                    l = l.substring(0, idx) + serie + l.substring(idx + 6, idx + l.length() - idx - 6);
                } else {
                    l = l.substring(0, idx) + serie;
                }
                */

                if (numero.length()<7) {
                    long nn=1000000+Long.parseLong(numero);
                    l=""+nn;l=l.substring(1,7);
                } else l=numero;

                if (facturaflag) {
                    l="%%%Serie : "+serie +" No. : "+l;
                } else {
                    sfticket=serie+l;l="";
                }

            } else {
                l="##";
            }
        }

        idx=lu.indexOf("@Vendedor");
        if (idx>=0) {
            rep.addc("");
            if (emptystr(vendnom)) return "@@";
            l=l.replace("@Vendedor", vendnom);return l;
        }

        idx=lu.indexOf("@Ruta");
        if (idx>=0) {
            if (emptystr(rutanombre)) return "@@";
            l=l.replace("@Ruta",rutanombre);return l;
        }

        idx=lu.indexOf("@Cliente");
        if (idx>=0) {
            if (emptystr(nombre_cliente)) return "@@";

            l=l.replace("@Cliente",nombre_cliente);l=l.trim();

            if (l.length()>prw) {
                //l=l.replace("@Cliente",clicod+" - "+cliente);

				String nuevaCadena="",cadena="";
				int vMod=0;

				cadena=l;
                nuevaCadena=cadena.substring(0,prw);cadena=cadena.substring(prw);
				if (cadena.length()>prw) {
                    nuevaCadena=nuevaCadena+"<<"+cadena.substring(0,prw);cadena=cadena.substring(prw);
                    if (cadena.length()>prw) {
                        nuevaCadena=nuevaCadena+"<<"+cadena.substring(0,prw);
                    } else nuevaCadena=nuevaCadena+"<<"+cadena;
                } else nuevaCadena=nuevaCadena+"<<"+cadena;

				/*
				vMod = (cadena.length()/prw)+1;

				for (int i = 0; i <vMod; i++) {
					if (cadena.length()>=prw*(i+1)){
						nuevaCadena += cadena.substring((i*prw),prw) + "\n";
					}else{
						nuevaCadena += cadena.substring((i*prw)-1,cadena.length());
					}
				}

				 */
				l="CLIENTE<<"+nuevaCadena;
                return l;
            }
            return l;
        }

        return l;
    }

    protected String encabezadoOrig(String l) {
        String s,lu,a;
        int idx;

        if (l.isEmpty()) return " ";
        lu=l.trim();

        if (lu.length()==1 && lu.equalsIgnoreCase("N")) {
            //s=nombre;s=rep.ctrim(s);return s;
            return "##";
        }

        if (l.indexOf("dd-MM-yyyy")>=0) {
            //s=DU.sfecha(DU.getActDateTime());
            //l=l.replace("dd-MM-yyyy",s);return l;
            return DU.sfecha(DU.getActDateTime())+" "+DU.shora(DU.getActDateTime());
        }

        if (l.indexOf("HH:mm:ss")>=0) {
            //s=DU.shora(DU.getActDateTime());
            //l=l.replace("HH:mm:ss",s);return l;
            return "##";
        }

        /*
		if (l.indexOf("@Numero") >=0) {
		    if (docfactura) {
                int index = l.indexOf("@Numero");

                String temp = l.substring(index + 7, index + 9);
                String temp1 = l.substring(index + 9, index + 10);

                int ctemp= Integer.parseInt(temp);

                String str=StringUtils.leftPad("", ctemp, temp1);

                if (!serie.isEmpty()) {
                    numero = StringUtils.right(str + numero, Integer.parseInt(temp));
                    if (!numero.isEmpty()){
                        int ctemp1= Integer.parseInt(numero);
                        if (ctemp1==0) numero = StringUtils.leftPad("", ctemp);
                    }
                }

                try {
                    if (l.length() > index + numero.length() ){
                        l = l.substring(0, index) + numero + l.substring(index + 10);
                    } else {
                        l = l.substring(0, index) + numero;
                    }
                } catch (Exception e) {
                    String ll=l;
                }

                if (l.indexOf("@Numero")>=0) {
                    l = StringUtils.replace(l,"@Numero","");
                    if (temp.length()>0 && temp1.length()>0){
                        l=StringUtils.replace(l,temp+temp1,"");
                    }
                }
            } else {
		        l="##";
            }
		}

		if (StringUtils.upperCase(l).indexOf("@SerNum") != -1) {
            if (docfactura) {
                int index = StringUtils.upperCase(l).indexOf("@SerNum");

                String temp = l.substring(index + 7, index + 9);
                String temp1 = l.substring(index + 10, index + 11);

                int ctemp= Integer.parseInt(temp);

                String str=StringUtils.leftPad("", ctemp, temp1);

                numero = StringUtils.right(str + numero, Integer.parseInt(temp));

                if (!numero.isEmpty()){
                    int ctemp1= Integer.parseInt(numero);
                    if (ctemp1==0) numero = StringUtils.leftPad("", ctemp);
                }

                try {
                    if ((l.length()) > index + serie.length() + numero.length()) {
                        l = l.substring(0, index) + serie + numero + l.substring(index + 1 + serie.length() + numero.length());
                    }else{
                        l = l.substring(0, index) + serie + numero;
                    }
                } catch (Exception e) {
                    String ll=l;
                    l="serie ::";
                }

                if (l.indexOf("@SerNum")>=0) {
                    l = StringUtils.replace(l,"@SerNum","");
                }

            } else {
                l="##";
            }
		}
        */

        idx=l.indexOf("@Serie");
        if (idx>=0) {
            if (docfactura) {
                if (l.length() > idx + serie.length()) {
                    l = l.substring(0, idx) + serie + l.substring(idx + 6, idx + l.length() - idx - 6);
                } else {
                    l = l.substring(0, idx) + serie;
                }
                if (l.indexOf("@Serie")>=0) {
                    l = StringUtils.replace(l,"@Serie","");
                }
                l="%%%Serie : "+serie +" No.: "+numero;
            } else {
                l="##";
            }
        }

        /*
		if ((l.indexOf("No.:")>=0) && (l.trim().length()==4)) {
            if (docfactura) {
                l = StringUtils.replace(l,"No.:","@@");
                l=l.trim();
            } else {
                l="##";
            }
		}
        */

        idx=lu.indexOf("@Vendedor");
        if (idx>=0) {
            rep.addc("");
            if (emptystr(vendnom)) return "@@";
            l=l.replace("@Vendedor", vendnom);return l;
        }

        idx=lu.indexOf("@Ruta");
        if (idx>=0) {
            if (emptystr(ruta)) return "@@";
            l=l.replace("@Ruta",ruta);return l;
        }

        idx=lu.indexOf("@Cliente");
        if (idx>=0) {
            if (emptystr(nombre_cliente)) return "@@";
            if(l.length()>20){
                l=l.replace("@Cliente",clicod+" - "+nombre_cliente);
                return l;
            }
            l=l.replace("@Cliente",clicod+" - "+rep.ltrim(nombre_cliente, 20));return l;
        }

        return l;
    }

    // Private

	private boolean buildHeader(String corel,int reimpres) {
		
		lines.clear();
		
		try {
			Con = new BaseDatos(cont);
			opendb();
			
			if (!corel.equalsIgnoreCase("0")) {
				loadDocData(corel);
				loadHeadData(corel);
			}

			loadHeadLines();

			try {
				Con.close();   
			} catch (Exception e1) {

			}
			
		} catch (Exception e) {
			setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			Toast.makeText(cont,"buildheader: "+e.getMessage(), Toast.LENGTH_SHORT).show();return false;
		}

		saveHeadLines(reimpres);
		
		return true;
	}

	private boolean buildHeader(String corel,int reimpres,BaseDatos pCon,android.database.sqlite.SQLiteDatabase pdb) {
		
		lines.clear();
		
		try {
				
			Con=pCon;db=pdb;
			
			if (!corel.equalsIgnoreCase("0")) {
				loadDocData(corel);
				loadHeadData(corel);
			}
			loadHeadLines();

			saveHeadLines(reimpres);
		} catch (Exception e) {
			setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			//Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();return false;
		}		

		return true;
	}

    private boolean buildHeader() {

        lines.clear();

        try {
            Con = new BaseDatos(cont);
            opendb();

            loadDocData("");

            try {
                Con.close();
            } catch (Exception e1) {}

        } catch (Exception e) {
            Toast.makeText(cont,"buildheader: "+e.getMessage(), Toast.LENGTH_SHORT).show();return false;
        }

        return true;
    }

    // Aux
	
	private boolean loadHeadLines() {
        clsP_fraseObj P_fraseObj=new clsP_fraseObj(cont,Con,db);
		Cursor DT;	
		String s,sucur;
		int frIVA,frISR;
		
		try {

			sql = "SELECT SUCURSAL FROM P_RUTA WHERE CODIGO_RUTA="+codigo_ruta;//+ruta;
			DT = Con.OpenDT(sql);
			DT.moveToFirst();
			sucur = DT.getString(0);

            sql="SELECT TEXTO,CODIGO_ESCENARIO_IVA, CODIGO_ESCENARIO_ISR,NIT FROM P_SUCURSAL WHERE CODIGO_SUCURSAL="+sucur;
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                textofin=DT.getString(0);
                frIVA=DT.getInt(1);
                frISR=DT.getInt(2);
                felISR2="";

                if (DT.getInt(1)>0) {
                    P_fraseObj.fill("WHERE Codigo_Frase="+frIVA);
                    if (P_fraseObj.count>0) {
                        felIVA=P_fraseObj.first().texto;
                    } else felIVA="";
                } else felIVA="";

                if (DT.getInt(2)>0) {
                    P_fraseObj.fill("WHERE Codigo_Frase="+frISR);
                    if (P_fraseObj.count>0) {
                        felISR=P_fraseObj.first().texto;
                        if (frISR==4) felISR2="Sujeto a pagos trimestrales ISR";
                    } else felISR="";
                } else felISR="";

                switch (empid) {
                    case 33:
                        felIVA="SUJETO A RETENCION DEFINITIVA";felISR="";break;
                    case 34:
                        felIVA="SUJETO A RETENCION DEFINITIVA";felISR="";break;
                }

            } else {
                textofin="";
            }

            nitsuc=DT.getString(3);

            banderafel=false;felcert="";felnit="";

            try {

                sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=105";
                DT=Con.OpenDT(sql);
                DT.moveToFirst();

                String val=DT.getString(0);

                if (val.equalsIgnoreCase("INFILE")) {
                    banderafel=true;
                    felcert="CERTIFICADOR : INFILE, S.A.";
                    felnit="NIT : 12521337";
                }

           } catch (Exception e) {
                banderafel=false;felcert="";felnit="";
            }

            sql="SELECT TEXTO FROM P_ENCABEZADO_REPORTESHH WHERE SUCURSAL='"+sucur+"' ORDER BY CODIGO";
			DT=Con.OpenDT(sql);
			if (DT.getCount()==0) return false;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {
				s=DT.getString(0);	
				lines.add(s);
				DT.moveToNext();
			}

			return true;
		} catch (Exception e) {
			setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			//Toast.makeText(cont,e.getMessage(), Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	public boolean emptystr(String s){
		if (s==null || s.isEmpty()) {
			return true;
		} else{
			return false;
		}
	}
	
	public String sfecha(long f) {
	    long vy,vm,vd;
		String s;
		
		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		vd=(long) f/10000;f=f % 10000;
		
		s="";
		if (vd>9) { s=s+String.valueOf(vd)+"-";} else {s=s+"0"+String.valueOf(vd)+"-";}  
		if (vm>9) { s=s+String.valueOf(vm)+"-20";} else {s=s+"0"+String.valueOf(vm)+"-20";}  
		if (vy>9) { s=s+String.valueOf(vy);} else {s=s+"0"+String.valueOf(vy);} 
		
		return s;
	}

	public String sfecha_dos(long f) {
		long vy,vm,vd;
		String s;

		vy=(long) f/100000000;f=f % 100000000;
		vm=(long) f/1000000;f=f % 1000000;
		vd=(long) f/10000;f=f % 10000;

		s="";
		if (vd>9) { s=s+String.valueOf(vd)+"-";} else {s=s+"0"+String.valueOf(vd)+"-";}
		if (vm>9) { s=s+String.valueOf(vm)+"-";} else {s=s+"0"+String.valueOf(vm)+"-";}
		if (vy>9) { s=s+String.valueOf(vy);} else {s=s+"0"+String.valueOf(vy);}

		return s;
	}

	public String shora(long vValue) {
		long h,m;
		String sh,sm;

		h=vValue % 10000;
		m=h % 100;if (m>9) {sm=String.valueOf(m);} else {sm="0"+String.valueOf(m);}
		h=(int) h/100;if (h>9) {sh=String.valueOf(h);} else {sh="0"+String.valueOf(h);}

		return sh+":"+sm;
	}
	
	public String frmdecimal(double val,int ndec) {
		String ss="",ff="#,##0.";
		DecimalFormat ffrmint = new DecimalFormat("#,##0"); 
		
		if (ndec<=0) {		
			ss=ffrmint.format((int) val);return ss;
		}
		
		for (int i = 1; i <ndec+1; i++) {
			ff=ff+"0";
		}
		
		DecimalFormat decim = new DecimalFormat(ff);
		ss=decim.format(val);
		
		return ss;
	}

	public void toast(String msg) {
		Toast.makeText(cont,msg, Toast.LENGTH_SHORT).show();	
	}

	public void setAddlog(String methodname,String msg,String info) {

		BufferedWriter writer = null;
		FileWriter wfile;

		try {

			String fname = Environment.getExternalStorageDirectory()+"/roadlog.txt";
			wfile=new FileWriter(fname,true);
			writer = new BufferedWriter(wfile);

			writer.write("Método: " + methodname + " Mensaje: " +msg + " Info: "+ info );
			writer.write("\r\n");

			writer.close();

		} catch (Exception e) {
			//msgbox("Error " + e.getMessage());
		}
	}

	private void opendb() {
		
		try {
			db = Con.getWritableDatabase();
		 	Con.vDatabase =db;
	    } catch (Exception e) {
	    	//Toast.makeText(cont,"Opendb "+e.getMessage(), Toast.LENGTH_LONG).show();
			setAddlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");

		}
	}	
		
}
