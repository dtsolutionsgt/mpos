package com.dtsgt.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.classes.clsP_usgrupoopcObj;
import com.dtsgt.mpos.CryptUtil;
import com.dtsgt.mpos.R;

import java.util.Currency;
import java.util.Locale;

public class AppMethods {

	private Context cont;
	private appGlobals gl;
	private SQLiteDatabase db;
	private BaseDatos.Insert ins;
	private BaseDatos.Update upd;
	private BaseDatos Con;
	private String sql;
	private String sp;

	// Location
	private LocationManager locationManager;
	private Location location;

	public AppMethods(Context context,appGlobals global,BaseDatos dbconnection, SQLiteDatabase database) {

		cont=context;
		gl=global;
		Con=dbconnection;
		db=database;
		
		ins=Con.Ins;
		upd=Con.Upd;
	}
	
	public void reconnect(BaseDatos dbconnection, SQLiteDatabase database) 	{
		Con=dbconnection;
		db=database;
		
		ins=Con.Ins;
		upd=Con.Upd;
	}

	//region Public
	
	public void parametrosExtra() {
		Cursor dt;
		String sql,val="";
		int ival;

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=1";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);
			if (ival<1)  ival=1;
			gl.peLimiteGPS =ival;
		} catch (Exception e) {
			gl.peLimiteGPS =0;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=2";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);

		} catch (Exception e) {
			val="N";
		}
		if (val.equalsIgnoreCase("S"))gl.peStockItf=true; else gl.peStockItf=false;

		// gl.peModal
		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=3";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			gl.peModal=dt.getString(0).toUpperCase();

		} catch (Exception e) {
			gl.peModal="-";
		}	

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=4";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
		} catch (Exception e) {
			val="N";
		}	

		if (val.equalsIgnoreCase("S"))gl.peSolicInv=true; else gl.peSolicInv=false;


		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=5";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			gl.peAceptarCarga=dt.getString(0).equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peAceptarCarga=false;
		}	

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=6";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			gl.peBotInv=dt.getString(0).equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peBotInv=false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=7";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			gl.peBotPrec=dt.getString(0).equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peBotPrec=false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=8";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			gl.peBotStock=dt.getString(0).equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peBotStock=false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=9";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);
			if (ival<2)  ival=2;
			if (ival>10) ival=-1;
			gl.peDec=ival;
		} catch (Exception e) {
			gl.peDec=-1;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=10";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);
			if (ival<0)  ival=0;
			if (ival>10) ival=10;
			gl.peDecImp=ival;
		} catch (Exception e) {
			gl.peDecImp=0;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=11";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);
			if (ival<1) ival=0;
			gl.peDecCant=ival;
		} catch (Exception e) {
			gl.peDecCant=0;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=12";	
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peMon=val;
		} catch (Exception e) {
			Locale defaultLocale = Locale.getDefault();
			Currency currency = Currency.getInstance(defaultLocale);
			gl.peMon=currency.getSymbol();		
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=13";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peVehAyud=val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peVehAyud=false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=14";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peEnvioParcial=val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peEnvioParcial=true;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=15";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peOrdPorNombre=val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peOrdPorNombre=true;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=16";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peFormatoFactura=val;
		} catch (Exception e) {
			gl.peFormatoFactura="";
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=17";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peImprFactCorrecta=val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peImprFactCorrecta=false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=18";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			if (val.equalsIgnoreCase("S")) {
				gl.peVentaGps = 1;
			} else if (val.equalsIgnoreCase("P")) {
				gl.peVentaGps = -1;
			} else {
				gl.peVentaGps = 0;
			}
		} catch (Exception e) {
			gl.peVentaGps=-1;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=19";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);
			if (ival<0)  ival=0;
			gl.peMargenGPS =ival;
		} catch (Exception e) {
			gl.peMargenGPS =0;
		}

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=100";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peMCent=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peMCent=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=101";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peMPrOrdCos=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peMPrOrdCos=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=102";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peMImg=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peMImg=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=103";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0).toUpperCase();
            if (emptystr(val)) throw new Exception();

            gl.peMMod=val;
        } catch (Exception e) {
            gl.peMMod="0";
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=104";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peMFact=val.equalsIgnoreCase("S");

        } catch (Exception e) {
            gl.peMFact=false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=105";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peFEL=val;

        } catch (Exception e) {
            gl.peFEL="";
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=106";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peFotoBio=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peFotoBio=true;
        }

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=107";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.cierreDiario=val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.cierreDiario=true;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=108";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.dias_anul=Integer.valueOf(val);

		} catch (Exception e) {
			gl.cierreDiario=true;
		}

	}

	public boolean grant(int menuopt,int rol) {

        try {
            clsP_usgrupoopcObj P_usgrupoopcObj=new clsP_usgrupoopcObj(cont,Con,db);
            P_usgrupoopcObj.fill("WHERE (GRUPO='"+rol+"') AND (OPCION="+menuopt+")");
            return P_usgrupoopcObj.count>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    //endregion

    //region Productos

    public boolean ventaPeso(String cod) {
        Cursor DT;
        String umm;

        try {
     		String sql = "SELECT UNIDADMEDIDA FROM P_PRODPRECIO WHERE CODIGO_PRODUCTO ='" + cod + "' AND NIVEL="+gl.nivel;
			DT = Con.OpenDT(sql);
			DT.moveToFirst();

			umm=DT.getString(0);

			return  umm.equalsIgnoreCase(gl.umpeso);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean prodBarra(String cod) {
        Cursor DT;

        try {
            String sql = "SELECT ES_PROD_BARRA FROM P_PRODUCTO WHERE CODIGO='" + cod + "'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            return  DT.getInt(0)==1;
        } catch (Exception e) {
            //toast(e.getMessage());
            return false;
        }
    }

    public String prodTipo(int cod) {
        Cursor DT;

        try {
            String sql = "SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            return  DT.getString(0);
        } catch (Exception e) {
            //toast(e.getMessage());
            return "P";
        }
    }

    public boolean ventaRepesaje(String cod) {
		Cursor DT;
		String umm;

		try {
			String sql = "SELECT VENTA_POR_PESO FROM P_PRODUCTO WHERE CODIGO='" + cod + "'";
			DT = Con.OpenDT(sql);
			DT.moveToFirst();
			return  DT.getInt(0)==1;
		} catch (Exception e) {
			//toast(e.getMessage());
			return false;
		}
	}

	public void estandartInventario()  {
		Cursor dt,df;
		String cod,ub,us,lote,doc,stat;
		double cant,cantm,fact,fact1,fact2;

		try {

			sql="SELECT P_STOCK.CODIGO,P_STOCK.UNIDADMEDIDA, P_PRODUCTO.UNIDBAS, P_STOCK.CANT, " +
					"P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.STATUS, P_STOCK.CANTM  " +
					"FROM  P_STOCK INNER JOIN P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO";
			dt=Con.OpenDT(sql);

			if (dt.getCount()==0) return;

			dt.moveToFirst();
			while (!dt.isAfterLast()) {

				cod=dt.getString(0);
				us=dt.getString(1);
				ub=dt.getString(2);
				cant=dt.getDouble(3);
				lote = dt.getString(4);
				doc = dt.getString(5);
				stat = dt.getString(6);
				cantm=dt.getDouble(7);

				if (!ub.equalsIgnoreCase(us)) {

					sql="SELECT FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+cod+"') AND (UNIDADSUPERIOR='"+us+"') ";
					df=Con.OpenDT(sql);
					if (df.getCount()==0) {
						msgbox("No existe factor conversion para el producto : " + cod);
						sql = "DELETE FROM P_STOCK WHERE CODIGO='" + cod + "'";
						db.execSQL(sql);
						fact1=1;
					} else {
						df.moveToFirst();
						fact1=df.getDouble(0);
					}

					sql="SELECT FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+cod+"') AND (UNIDADSUPERIOR='"+ub+"') ";
					df=Con.OpenDT(sql);
					if (df.getCount()==0) {
						msgbox("No existe factor conversion para el producto : "+cod);
						sql="DELETE FROM P_STOCK WHERE CODIGO='"+cod+"'";
						db.execSQL(sql);
						fact2=1;
					} else {
						df.moveToFirst();
						fact2=df.getDouble(0);
					}

					if (fact1>=fact2) {
						fact=fact1/fact2;
					} else {
						fact=fact2/fact1;
					}

					cant = cant * fact;
					cantm = cantm * fact;

					sql="UPDATE P_STOCK SET CANT=" + cant + ",CANTM=" + cantm + ",UNIDADMEDIDA='" + ub + "'  " +
						"WHERE (CODIGO='" + cod + "') AND (UNIDADMEDIDA='" + us + "') AND (LOTE='" + lote + "') AND (DOCUMENTO='" + doc + "') AND (STATUS='" + stat + "')";
					db.execSQL(sql);

				}

				dt.moveToNext();
			}

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

	}

	public double getPeso() {
		return 0;
	}

	public double getCantidad() {
		Cursor DT;
		double sumaCant=0;

		sql = " SELECT SUM(S.CANT) AS CANTUNI " +
				" FROM P_STOCK S, P_PRODUCTO P " +
				" WHERE P.ES_PROD_BARRA = 0 AND S.CODIGO= P.CODIGO AND S.CANT > 0";
		DT=Con.OpenDT(sql);

		if (DT.getCount()>0) {
			DT.moveToFirst();
			sumaCant=DT.getDouble(0);
		}

		DT.close();

		return sumaCant;
	}

	public String umVenta(String cod) {
		Cursor DT;
		String umm;

		try {
			String sql = "SELECT UNIDADMEDIDA FROM P_PRODPRECIO WHERE CODIGO_PRODUCTO ='" + cod + "' AND NIVEL="+gl.nivel;
			DT = Con.OpenDT(sql);
			DT.moveToFirst();

			umm=DT.getString(0);
			return  umm;
		} catch (Exception e) {
			//toast(e.getMessage());
			return "";
		}
	}

    public String umVenta2(String cod) {
        Cursor DT;
        String umm;

        try {
            String sql = "SELECT UNIDBAS FROM P_PRODUCTO WHERE CODIGO='" + cod+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);
            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return "";
        }
    }

    public String umVenta3(int cod) {
        Cursor DT;
        String umm;

        try {
            String sql = "SELECT UNIDBAS FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);
            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return "";
        }
    }

    public String umStock(String cod) {
		Cursor DT;
		String umm,sql;

		try {
			sql = "SELECT UNIDADMEDIDA FROM P_STOCK WHERE CODIGO='"+cod+ "'";
			DT = Con.OpenDT(sql);

			DT.moveToFirst();
			umm=DT.getString(0);

			return  umm;
		} catch (Exception e) {
			//toast(e.getMessage());
			return "";
		}
	}

    public int codigoProducto(String cod) {
        Cursor DT;
        int umm;

        try {
            String sql = "SELECT CODIGO_PRODUCTO FROM P_PRODUCTO WHERE CODIGO='" + cod+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getInt(0);
            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return 0;
        }
    }

    public String prodCod(int cod) {
        Cursor DT;
        String umm;

        try {
            String sql = "SELECT CODIGO FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);
            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return "";
        }
    }

    public double factorPeso(String cod) {
		Cursor DT;

		try {
			String sql = "SELECT PESO_PROMEDIO FROM P_PRODUCTO WHERE CODIGO='" + cod + "'";
			DT = Con.OpenDT(sql);
			DT.moveToFirst();

			return  DT.getDouble(0);
		} catch (Exception e) {
			//toast(e.getMessage());
			return 0;
		}
	}

	public double factorPres(String cod,String umventa,String umstock) {
		Cursor DT;
		String sql;

		try {
			sql="SELECT FACTORCONVERSION FROM P_FACTORCONV " +
				"WHERE (PRODUCTO='"+cod+"') AND (UNIDADSUPERIOR='"+umventa+"') AND (UNIDADMINIMA='"+umstock+"')";
			DT = Con.OpenDT(sql);

			if (DT.getCount()==0) {
				sql="SELECT FACTORCONVERSION FROM P_FACTORCONV " +
					"WHERE (PRODUCTO='"+cod+"') AND (UNIDADSUPERIOR='"+umstock+"') AND (UNIDADMINIMA='"+umventa+"')";
				DT = Con.OpenDT(sql);
			}

			DT.moveToFirst();

			return  DT.getDouble(0);
		} catch (Exception e) {
			return 1;
		}
	}

	public boolean validaImpresora() {
		return true;
	}

	public int nivelSucursal() {
        Cursor DT;

        try {
            String sql = "SELECT CODIGO_NIVEL_PRECIO FROM P_SUCURSAL WHERE CODIGO='" + gl.tienda + "'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            return  DT.getInt(0);
        } catch (Exception e) {
            return 1;
        }

    }

	//#CKFK20200524_FIX_BY_OPENDT Puse esta función en comentario porque la tabla P_IMPRESORA no existe en MPos
	/*public String impresTipo() {
		Cursor dt;
		String prnid;

		try {

			sql="SELECT prn FROM Params";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			prnid=dt.getString(0);

			sql="SELECT MARCA FROM P_IMPRESORA WHERE IDIMPRESORA='"+prnid+"'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()==0) return "SIN IMPRESORA";
			dt.moveToFirst();

			return dt.getString(0);

		} catch (Exception e) {
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return "SIN IMPRESORA";
		}
	}*/

	//#CKFK20200524_FIX_BY_OPENDT Puse esta función en comentario porque la tabla P_IMPRESORA no existe en MPos
	/*public String impresParam() {
		CryptUtil cu=new CryptUtil();
		Cursor dt;
		String prnid;

		try {

			sql="SELECT prn FROM Params";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			prnid=dt.getString(0);

			sql="SELECT MACADDRESS FROM P_IMPRESORA WHERE IDIMPRESORA='"+prnid+"'";
			dt=Con.OpenDT(sql);
			if (dt.getCount()==0) return " #### ";
			dt.moveToFirst();

			return cu.decrypt(dt.getString(0));

		} catch (Exception e) {
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			return " #### ";
		}
	}*/

    //endregion

    //region Impresion

    public void doPrint() {
        doPrint(0);
    }

    public void doPrint(int copies) {
	    if (copies<1) copies=1;

        loadPrintConfig();

        if (gl.prtipo.isEmpty() | gl.prtipo.equalsIgnoreCase("SIN IMPRESORA")) {
            toast("No se puede imprimir. No está definida impresora");return;
        }

        if (gl.prpar.isEmpty()) {
            toast("No se puede imprimir. No está definido el MAC.");return;
        }

        if (gl.prtipo.equalsIgnoreCase("EPSON TM BlueTooth")) {
            printEpsonTMBT(copies);
        }

        if (gl.prtipo.equalsIgnoreCase("HP Engage USB")) {
            HPEngageUSB(copies);
        }

    }

    private void printEpsonTMBT(int copies) {
        try {
            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.dts.epsonprint");

            intent.putExtra("mac","BT:"+gl.prpar);
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
            intent.putExtra("askprint",1);
            intent.putExtra("copies",copies);

            cont.startActivity(intent);
        } catch (Exception e) {
            msgbox("El controlador de impresión de Epson TM BT no está instalado");
            //msgbox("El controlador de Epson TM BT no está instalado\n"+e.getMessage());
        }
    }

    private void HPEngageUSB(int copies) {
        try {
            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.hp.retail.test");
            cont.startActivity(intent);
        } catch (Exception e) {
            msgbox("El controlador de impresión de HP Engage USB no está instalado");
            //msgbox("El controlador de HP Engage USB no está instalado\n"+e.getMessage());
        }
    }

    private void loadPrintConfig() {
        Cursor DT;

        //00:01:90:85:0D:8C

        try {
            gl.prtipo="";gl.prpar="";

            sql="SELECT TIPO_IMPRESORA,PUERTO_IMPRESION FROM P_ARCHIVOCONF";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                gl.prtipo=DT.getString(0);
                gl.prpar=DT.getString(1);
            }

        } catch (Exception e) {
            gl.prtipo="";gl.prpar="00:01:90:85:0D:8C";
        }

    }

    public boolean impresora() {
        loadPrintConfig();

        if (gl.prtipo.isEmpty() | gl.prtipo.equalsIgnoreCase("SIN IMPRESORA")) {
            return false;
        } else {
            return true;
        }
    }

    //endregion

	//region Aux

    //Función para saber la cantidad de registros en una tabla
    public int getDocCount(String ss,String pps) {

        Cursor DT;
        int cnt =0;
        String st;

        try {
            sql=ss;
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                cnt=DT.getCount();
                st=pps+" "+cnt;
                sp=sp+st+"\n";
            }

            return cnt;
        } catch (Exception e) {
            //mu.msgbox(sql+"\n"+e.getMessage());
            return 0;
        }
    }

    //Función para saber la cantidad de registros en una tabla específica
    public int getDocCountTipo(String tipo, boolean sinEnviar) {

        Cursor DT;
        int cnt = 0;
        String st, ss;
        String pps = "";

        try {

            switch(tipo) {
                case "Facturas":

                    sql="SELECT IFNULL(COUNT(COREL),0) AS CANT FROM D_FACTURA";
                    sql += (sinEnviar?" WHERE STATCOM = 'N'":"");
                    break;

                case "Pedidos":

                    sql="SELECT IFNULL(COUNT(COREL),0) AS CANT FROM D_PEDIDO";
                    sql += (sinEnviar?" WHERE STATCOM = 'N'":"");
                    break;

                case "Cobros":

                    sql="SELECT IFNULL(COUNT(COREL),0) AS CANT FROM D_COBRO";
                    sql += (sinEnviar?" WHERE STATCOM = 'N'":"");
                    break;

                case "Devolucion":

                    sql="SELECT IFNULL(COUNT(COREL),0) AS CANT FROM D_NOTACRED";
                    sql += (sinEnviar?" WHERE STATCOM = 'N'":"");
                    break;

                case "Inventario":

                    sql=" SELECT IFNULL(SUM(A.CANT),0) AS CANT " +
                            " FROM (SELECT IFNULL(COUNT(DOCUMENTO),0) AS CANT FROM P_STOCK ) A";
                    break;
            }

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0){
                DT.moveToFirst();
                cnt=DT.getInt(0);
            }

            st=pps+" "+cnt;
            sp=sp+st+"\n";

        } catch (Exception e) {

        }

        return cnt;

    }

    public long getDateRecep() {
        Cursor dt;
        long resultado = 0;

        try {
            sql="SELECT param1 FROM Params";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0){

				dt.moveToFirst();

				resultado = dt.getLong(0);
			}

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            resultado= 0;
        }

        return resultado;
    }

    public void setDateRecep(long ff) {
         try {
            sql = "UPDATE Params SET param1="+ff;
            db.execSQL(sql);
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Common
	
	protected void toast(String msg) {
		Toast toast= Toast.makeText(cont,msg, Toast.LENGTH_SHORT);  
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

	private boolean emptystr(String s){
		if (s==null || s.isEmpty()) {
			return true;
		} else{
			return false;
		}
	}

	public void msgbox(String msg) {

		try{

			if (!emptystr(msg)){

				AlertDialog.Builder dialog = new AlertDialog.Builder(cont);

				dialog.setTitle(R.string.app_name);
				dialog.setMessage(msg);

				dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(getApplicationContext(), "Yes button pressed",Toast.LENGTH_SHORT).show();
					}
				});
				dialog.show();

			}

		}catch (Exception ex)
			{toast(ex.getMessage());}
	}

	public int isOnWifi(){

		int activo=0;

		try{

			ConnectivityManager connectivityManager = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()){

				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					activo=1;
				}

				if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					activo = 2;
				}

			}

		} catch (Exception ex){

		}

		return activo;

	}

    //endregion

}
