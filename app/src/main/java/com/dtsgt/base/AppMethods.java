package com.dtsgt.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_usuario_asistenciaObj;
import com.dtsgt.classes.clsP_usgrupoopcObj;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppMethods {

    public String errstr;

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
		double dval;

        parametrosExtraLocal();

		//region Parametros Road

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

		//endregion

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

            gl.peImpOrdCos =val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peImpOrdCos =false;
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
            gl.dias_anul=5;
		}

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=109";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peEnvio=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peEnvio=true;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=110";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peAvizoFEL=Integer.valueOf(val);
        } catch (Exception e) {
            gl.peAvizoFEL=3;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=111";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peCajaRec=val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peCajaRec=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=112";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peInvCompart =val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peInvCompart =false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=113";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pePedidos =val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.pePedidos =false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=114";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            ival=Integer.parseInt(val);
            if (ival<1 | ival>5) ival=1;

            gl.peNumImp =ival;
        } catch (Exception e) {
            gl.peNumImp=1;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=115";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peRepVenCod = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peRepVenCod =false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=116";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peAnulSuper = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peAnulSuper=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=117";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pePropinaFija = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.pePropinaFija =true;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=118";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peRest = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peRest =false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=119";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peModifPed = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peModifPed=false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=120";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            dval=Double.parseDouble(val);
            if (dval<0 | dval>99) dval=0;

            gl.pePropinaPerc =dval;
        } catch (Exception e) {
            gl.pePropinaPerc=0;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=121";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peBotComanda = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peBotComanda = false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=122";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            gl.peComNoAplic = dt.getString(0);
        } catch (Exception e) {
            gl.peComNoAplic = "NO APLICA";
        }


        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=123";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peEditTotCombo = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peEditTotCombo = false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=124";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peAgregarCombo = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peAgregarCombo = false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=125";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peComboLimite = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peComboLimite = false;
        }


    }

    public void parametrosExtraLocal() {
        try {
            SharedPreferences pref = cont.getApplicationContext().getSharedPreferences("MPos", 0);
            SharedPreferences.Editor editor = pref.edit();

            try {
                gl.pelCaja=pref.getBoolean("pelCaja", false);
            } catch (Exception e) {
                gl.pelCaja=false;
            }

            try {
                gl.pelCajaRecep=pref.getBoolean("pelCajaRecep", false);
            } catch (Exception e) {
                gl.pelCajaRecep=false;
            }

            try {
                gl.pelPrefijoOrden=pref.getString("pelPrefCaja", "");
            } catch (Exception e) {
                gl.pelPrefijoOrden="";
            }

            try {
                gl.pelDespacho=pref.getBoolean("pelDespacho",false);
            } catch (Exception e) {
                gl.pelDespacho=false;
            }

            try {
                gl.pelOrdenComanda=pref.getBoolean("pelOrdenComanda",false);
            } catch (Exception e) {
                gl.pelOrdenComanda=false;
            }


        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public boolean paramCierre(int pid) {
        Cursor dt;
        String sql,val="";

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID="+pid;
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            return val.equalsIgnoreCase("S");
        } catch (Exception e) {
            return false;
        }

    }

    public void paramCierreAplica(int pid,boolean valor) {
        String sql,val="";

        try {
            if (valor) val="S";else val="N";
            sql="UPDATE P_PARAMEXT SET VALOR='"+val+"' WHERE ID="+pid;
            db.execSQL(sql);
        } catch (Exception e) {
            toast("paramCierreAplica : "+e.getMessage());
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

    public boolean usaFEL() {
        if (gl.peFEL.isEmpty()| gl.peFEL.equalsIgnoreCase(" ") | gl.peFEL.equalsIgnoreCase("SIN FEL")) {
            return false;
        } else {
            return true;
        }
	}

    public void getURL() {
        gl.wsurl = "http://52.41.114.122/MPosWS_QA/Mposws.asmx";
        gl.timeout = 20000;

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 20000;else gl.timeout = Integer.valueOf(line);
                myReader.close();
            } else {
                BufferedWriter writer = null;
                FileWriter wfile;

                wfile=new FileWriter(file1,false);
                writer = new BufferedWriter(wfile);
                writer.write(gl.wsurl);writer.write("\r\n");
                writer.write("20000");writer.write("\r\n");
                writer.close();

                toast("Creado archivo de conexion");
            }
        } catch (Exception e) {}
    }

    public void logoutUser(long ff) {
        try {


            long f0=ff / 10000;
            f0=f0*10000;

            clsD_usuario_asistenciaObj D_usuario_asistenciaObj=new clsD_usuario_asistenciaObj(cont,Con,db);
            D_usuario_asistenciaObj.fill("WHERE (CODIGO_VENDEDOR="+gl.codigo_vendedor+") AND (FECHA="+f0+")");
            if (D_usuario_asistenciaObj.count==0) return;

            clsClasses.clsD_usuario_asistencia item=D_usuario_asistenciaObj.first();
            item.fin=ff;
            D_usuario_asistenciaObj.update(item);

        } catch (Exception e) {

        }
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
        String result="";

        try {

            String sql = "SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            result = DT.getString(0);

        } catch (Exception e) {
            throw e;
        }

		return result;

    }

    public String prodTipo(String cod) {

        Cursor DT;
        String result="";

        try {

            String sql = "SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO='" + cod+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            result = DT.getString(0);

        } catch (Exception e) {
            throw e;
        }

        return result;

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
		String umm="";

		try {
			String sql = "SELECT P_PRODPRECIO.UNIDADMEDIDA " +
					     "FROM P_PRODPRECIO INNER JOIN P_PRODUCTO ON P_PRODPRECIO.CODIGO_PRODUCTO = P_PRODUCTO.CODIGO_PRODUCTO " +
					     "WHERE P_PRODUCTO.CODIGO ='" + cod + "' AND P_PRODPRECIO.NIVEL="+gl.nivel;
			DT = Con.OpenDT(sql);

			if (DT != null){

				if(DT.getCount()>0){

					DT.moveToFirst();

					umm=DT.getString(0);
				}

				DT.close();
			}

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

    public String prodNombre(int cod) {
        Cursor DT;
        String umm;

        try {
            String sql = "SELECT DESCCORTA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);
            return  umm;
        } catch (Exception e) {
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
        int niv;

        try {
            String sql = "SELECT CODIGO_NIVEL_PRECIO FROM P_SUCURSAL WHERE CODIGO_SUCURSAL=" + gl.tienda ;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            niv=DT.getInt(0);
        } catch (Exception e) {
            niv=1;
        }

        return niv;
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
        doPrint(0,0);
    }

    public void doPrint(int prtipo) {
        doPrint(0,prtipo);
    }

    public void doPrint(int copies,int tipoimpr) {

	    if (copies<1) copies=1;

        loadPrintConfig(tipoimpr);

        if (tipoimpr==0) {
            if (gl.prtipo.isEmpty() | gl.prtipo.equalsIgnoreCase("SIN IMPRESORA")) {
                toast("No se puede imprimir. No está definida impresora");return;
            }
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

	private boolean rename(File from, File to) {
		return from.getParentFile().exists() && from.exists() && from.renameTo(to);
	}

    private void printEpsonTMBT(int copies) {

        try {
            //toast(gl.prpar);

            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.dts.epsonprint");
            intent.putExtra("mac","BT:"+gl.prpar);
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
            intent.putExtra("askprint",1);
            intent.putExtra("copies",copies);
            cont.startActivity(intent);

        } catch (Exception e) {
            toastlong("El controlador de Epson TM BT no está instalado");

            String fname = Environment.getExternalStorageDirectory()+"/print.txt";
            String fnamenew = Environment.getExternalStorageDirectory()+"/not_printed.txt";

            File currentFile = new File(fname);
            File newFile = new File(fnamenew);

            if (rename(currentFile, newFile)) {
                //Success
                Log.i("TAG", "Success");
            } else {
                //Fail
                Log.i("TAG", "Fail");
            }

            //msgbox("El controlador de impresión está instalado (Ref -> Could be: EpsonTMBT)");
            //msgbox("El controlador de Epson TM BT no está instalado\n"+e.getMessage());
        }
    }

    public void print3nstarw() {
        try {
            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.dts.prn3nsw");
            cont.startActivity(intent);
        } catch (Exception e) {
            toastlong("El controlador de 3nStar LAN  no está instalado");
        }
    }

    private void HPEngageUSB(int copies) {
        try {
            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.hp.retail.test");
            cont.startActivity(intent);
        } catch (Exception e) {
			//#EJC20200627: Modifique mensaje, menos especifico.
            msgbox("El controlador de impresiónUSB no está instalado (Ref -> HPEngage?) ");
            //msgbox("El controlador de HP Engage USB no está instalado\n"+e.getMessage());
        }
    }

    public void loadPrintConfig() {
        Cursor DT;

        //00:01:90:85:0D:8C

        try {
            gl.prtipo="";gl.prpar="";

            sql="SELECT TIPO_IMPRESORA,PUERTO_IMPRESION FROM P_ARCHIVOCONF WHERE (RUTA="+gl.codigo_ruta+") ";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                gl.prtipo=DT.getString(0);
                gl.prpar=DT.getString(1);
            }

        } catch (Exception e) {
            gl.prtipo="SIN IMPRESORA";gl.prpar="00:00:00:00:00:00";
        }

    }

    public void loadPrintConfig(int tipoimpr) {
        Cursor DT;

        try {
            gl.prtipo="";gl.prpar="";

            //sql="SELECT TIPO_IMPRESORA,PUERTO_IMPRESION FROM P_ARCHIVOCONF WHERE (RUTA="+gl.codigo_ruta+") AND (NOTA_CREDITO=1)";
            sql="SELECT TIPO_IMPRESORA,PUERTO_IMPRESION FROM P_ARCHIVOCONF WHERE (RUTA="+gl.codigo_ruta+") ";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                gl.prtipo=DT.getString(0);
                gl.prpar=DT.getString(1);
            }
        } catch (Exception e) {
            gl.prtipo="SIN IMPRESORA";gl.prpar="00:00:00:00:00:00";
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

    //region Pedidos

    public boolean agregaPedido(String fname,String ename,long fa,String cor) {
        File file=null;
        BufferedReader br=null;
        ArrayList<String> items=new ArrayList<String>();
        String sql;
        int lim;
        boolean flag=true;

        cor=cor.replace(".txt","");

        try {
            sql="SELECT Corel FROM D_PEDIDO WHERE Corel='"+cor+"'";
            Cursor dt=Con.OpenDT(sql);
            if (dt.getCount()>0) flag=false;
        } catch (Exception e) {
            String ss=e.getMessage();
        }

        try {
            file=new File(fname);
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            moveFile(fname,ename);errstr=e.getMessage();return false;
        }

        if (flag) {
            try {
                db.beginTransaction();

                while ((sql=br.readLine())!= null) {
                    items.add(sql);
                    db.execSQL(sql);
                }

                lim=limitePedido(cor);

                //sql="UPDATE D_PEDIDO SET FECHA_RECEPCION_SUC="+fa+",EMPRESA=0,FIRMA_CLIENTE="+lim+",CODIGO_USUARIO_CREO="+gl.codigo_vendedor+" WHERE FECHA_RECEPCION_SUC=0";
                sql="UPDATE D_PEDIDO SET FECHA_RECEPCION_SUC="+fa+",EMPRESA=0,FIRMA_CLIENTE="+lim+",CODIGO_USUARIO_CREO=0 WHERE FECHA_RECEPCION_SUC=0";
                db.execSQL(sql);

                db.setTransactionSuccessful();
                db.endTransaction();

            } catch (Exception e) {
                db.endTransaction();errstr=e.getMessage();moveFile(fname,ename);
                return false;
            }
        }

        try {
            br.close();
        } catch (Exception e) {
            errstr=e.getMessage();
        }

        file.delete();

        return true;
    }

    private void moveFile(String fname,String ename) {
        try {
            FileUtils.forceDelete(new File(fname));
            FileUtils.moveFile(new File(fname),new File(ename));
        } catch (Exception e) {}
    }

    private int limitePedido(String corel) {
        Cursor dt;

        try {
            String sqls="SELECT SUM(P_PRODUCTO.TIEMPO_PREPARACION) FROM  D_PEDIDOD " +
                    "INNER JOIN P_PRODUCTO ON D_PEDIDOD.CODIGO_PRODUCTO=P_PRODUCTO.CODIGO_PRODUCTO " +
                    "WHERE (D_PEDIDOD.COREL='"+corel+"')";
            dt=Con.OpenDT(sqls);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
    }

    public int pendientesPago(String pcor) {
        try {
            sql="SELECT P.COREL FROM D_FACTURAP P " +
                "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                "WHERE (P.TIPO='E') AND (F.FECHA>2009230000) AND (F.PEDCOREL<>'') AND (F.ANULADO=0) AND (P.VALOR=0) ";
            if (!pcor.isEmpty()) sql+=" AND (F.PEDCOREL='"+pcor+"')";
            Cursor dt=Con.OpenDT(sql);

            return dt.getCount();
        } catch (Exception e) {
            return -1;
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

    protected void toastlong(String msg) {
        Toast toast= Toast.makeText(cont,msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
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

		try {
			if (!emptystr(msg)){

                ExDialog dialog = new ExDialog(cont);

                dialog.setMessage(msg);

				dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(getApplicationContext(), "Yes button pressed",Toast.LENGTH_SHORT).show();
					}
				});
				dialog.show();

			}

		} catch (Exception ex) {
		    toast(ex.getMessage());
		}
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

    public void zip(String file, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        int BUFFER_SIZE = 6 * 1024;

        try {
            String[] files = new String[1];files[0]=file;

            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    //endregion

}
