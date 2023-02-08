package com.dtsgt.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_usuario_asistenciaObj;
import com.dtsgt.classes.clsP_prodmenuopcObj;
import com.dtsgt.classes.clsP_prodmenuopcdetObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_usgrupoopcObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.mpos.PrintView;
import com.dtsgt.mpos.R;

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
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppMethods {

    public String errstr;

    public ArrayList<String> citems= new ArrayList<String>();

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
			gl.peLimiteGPS =6000;
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
            gl.peInvCompart =false;
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

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=126";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peComboDet = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peComboDet = false;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=127";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            dval=Double.parseDouble(val);
            if (dval<0 | dval>99) dval=0;

            gl.pePropinaCarta =dval;
        } catch (Exception e) {
            gl.pePropinaCarta=0;
        }

        try {
            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=128";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.peFactSinPropina = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.peFactSinPropina = false;
        }

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=129";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.pelComandaBT = val.equalsIgnoreCase("S");

		} catch (Exception e) {
			gl.pelComandaBT = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=130";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.pelPrefijoOrden = val;
		} catch (Exception e) {
			gl.pelPrefijoOrden = "";
		}

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=131";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.pelCaja = val.equalsIgnoreCase("S");

		} catch (Exception e) {
			gl.pelCaja = false;
		}

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=132";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelCajaRecep = val.equalsIgnoreCase("S");

        } catch (Exception e) {
            gl.pelCajaRecep = false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=133";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelDespacho = val.equalsIgnoreCase("S");

        } catch (Exception e) {
            gl.pelDespacho = false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=134";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelOrdenComanda = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.pelOrdenComanda = false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=135";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelClaveMes = val.equalsIgnoreCase("S");
        } catch (Exception e) {
            gl.pelClaveMes = false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=136";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelClaveCaja = val.equalsIgnoreCase("S");

        } catch (Exception e) {
            gl.pelClaveCaja = false;
        }

        try {

            sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=137";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            val=dt.getString(0);
            if (emptystr(val)) throw new Exception();

            gl.pelMeseroCaja = val.equalsIgnoreCase("S");

        } catch (Exception e) {
            gl.pelMeseroCaja = false;
        }

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=138";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peVentaDomicilio = val.equalsIgnoreCase("S");

		} catch (Exception e) {
			gl.peVentaDomicilio = false;
		}

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=139";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peVentaEntrega = val.equalsIgnoreCase("S");

		} catch (Exception e) {
			gl.peVentaEntrega = false;
		}

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=140";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peDomEntEnvio = val.equalsIgnoreCase("S");

		} catch (Exception e) {
			gl.peDomEntEnvio = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=141";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peNoCerrarMesas = val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peNoCerrarMesas = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=142";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peActOrdenMesas = val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peActOrdenMesas = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=143";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peLineaIngred = Integer.parseInt(val);
		} catch (Exception e) {
			gl.peLineaIngred = 0;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=147";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			gl.peTextoPie =val;
		} catch (Exception e) {
			gl.peTextoPie = "";
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=148";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peRedondPropina = val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peRedondPropina = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=149";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);

			gl.peCajaPricipal = ival;
		} catch (Exception e) {
			gl.peCajaPricipal = 34;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=150";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peFraseIVA = val;
		} catch (Exception e) {
			gl.peFraseIVA = "";
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=151";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peFraseISR = val;
		} catch (Exception e) {
			gl.peFraseISR = "";
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=152";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peCafeTicket = val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peCafeTicket = false;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=153";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			ival=Integer.parseInt(val);

			gl.pePorConsumo = ival;
		} catch (Exception e) {
			gl.peCajaPricipal = 0;
		}

		try {
			sql="SELECT VALOR FROM P_PARAMEXT WHERE ID=154";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();

			val=dt.getString(0);
			if (emptystr(val)) throw new Exception();

			gl.peNoEnviar = val.equalsIgnoreCase("S");
		} catch (Exception e) {
			gl.peNoEnviar = false;
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
        if (gl.peFEL.isEmpty() | gl.peFEL.equalsIgnoreCase(" ") |
                gl.peFEL.equalsIgnoreCase("N") | gl.peFEL.equalsIgnoreCase("SIN FEL")) {
            return false;
        } else {
            return true;
        }
	}

    public void getURL() {

        gl.wsurl = "http://52.41.114.122/MPosWS_QA/Mposws.asmx";
        gl.timeout = 6000;

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {

                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 6000;else gl.timeout = Integer.valueOf(line);
                myReader.close();

            } else {
                BufferedWriter writer = null;
                FileWriter wfile;

                wfile=new FileWriter(file1,false);
                writer = new BufferedWriter(wfile);
                writer.write(gl.wsurl);writer.write("\r\n");
                writer.write(String.valueOf(gl.timeout));writer.write("\r\n");
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

	         if (DT!=null) DT.close();

			return  umm.equalsIgnoreCase(gl.umpeso);

        } catch (Exception e) {
            return false;
        }
    }

	public double prodPrecio(int cod) {

		double prec;

		try {

			String sql = "SELECT PRECIO FROM P_PRODPRECIO WHERE CODIGO_PRODUCTO =" + cod + " AND NIVEL="+gl.nivel;
			Cursor DT = Con.OpenDT(sql);
			DT.moveToFirst();
			prec=DT.getDouble(0);

			if (DT!=null) DT.close();

			return  prec;

		} catch (Exception e) {
			return 0;
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
			if (DT!=null) DT.close();

        } catch (Exception e) {
            throw e;
        }

		return result;
    }

    public String prodTipo(String cod) {
        Cursor DT;
        String result="S";

        try {

            String sql = "SELECT CODIGO_TIPO FROM P_PRODUCTO WHERE CODIGO='" + cod+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            result = DT.getString(0);
			if (DT!=null) DT.close();

        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    public int codigoCombo(int cod) {

        Cursor DT;

        try {

            String sql = "SELECT CODIGO_MENU FROM P_PRODMENU WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

			int val=DT.getInt(0);

			if (DT!=null){
				DT.close();
			}

            return val;

        } catch (Exception e) {
            return 0;
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

		if (DT!=null) DT.close();
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
				if (DT.getCount()>0){
					DT.moveToFirst();
					umm=DT.getString(0);
				}
				DT.close();
			}

            if (DT!=null) DT.close();

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

            if (DT!=null) DT.close();

            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return "";
        }
    }

    public String umVenta2(int cod) {
        Cursor DT;
        String umm;

        try {
            String sql = "SELECT UNIDBAS FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);

            if (DT!=null) DT.close();

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

            if (DT!=null) DT.close();

            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return "";
        }
    }

    public String umStock(String cod) {
		Cursor DT=null;
		String umm,sql;

		try {
			sql = "SELECT UNIDADMEDIDA FROM P_STOCK WHERE CODIGO='"+cod+ "'";
			DT = Con.OpenDT(sql);

			DT.moveToFirst();
			umm=DT.getString(0);
			if (DT!=null) DT.close();

			return  umm;
		} catch (Exception e) {
			//toast(e.getMessage());
			return "";
		}
	}

    public int codigoProducto(String cod) {
        Cursor DT=null;
        int umm;

        try {
            String sql = "SELECT CODIGO_PRODUCTO FROM P_PRODUCTO WHERE CODIGO='" + cod+"'";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getInt(0);

            if (DT!=null) DT.close();
            return  umm;
        } catch (Exception e) {
            //toast(e.getMessage());
            return 0;
        } finally {
            if (DT!=null) DT.close();
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
			if (DT!=null) DT.close();
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
			if (DT!=null) DT.close();
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
			if (DT!=null) DT.close();
        } catch (Exception e) {
            niv=1;
        }

        return niv;
    }

    //endregion

    //region Impresion

    public void doPrint() {
        doPrint(0,0);
    }

    public void doPrint(int prtipo) {
        doPrint(0,prtipo);
    }

    @SuppressLint("SuspiciousIndentation")
	public void doPrint(int copies, int tipoimpr) {

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
            if (estadoBluTooth()) {
                printEpsonTMBT(copies);
            } else return;
        }

        if (gl.prtipo.equalsIgnoreCase("HP Engage USB")) {
            HPEngageUSB(copies);
        }

        if (gl.prtipo.equalsIgnoreCase("Aclas")) {
            printAclas(copies);
        }

    }

    @SuppressLint("MissingPermission")
	private boolean estadoBluTooth() {
        final int REQUEST_ENABLE_BT = 1;
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                toastlong("El dispositivo no soporta Bluetooth");return false;
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    return true;
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    Activity activity = (Activity) cont;
                    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return false;
                }
            }
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

	private boolean rename(File from, File to) {
		return from.getParentFile().exists() && from.exists() && from.renameTo(to);
	}

    private void printEpsonTMBT(int copies) {

        try {

            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("com.dts.epsonprint");
            intent.putExtra("mac","BT:"+gl.prpar);
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
            intent.putExtra("askprint",1);
            intent.putExtra("copies",copies);
			intent.putExtra("QRCodeStr",""+gl.QRCodeStr);
            cont.startActivity(intent);


		} catch (Exception e) {
            toastlong("El controlador de Epson TM BT no está instalado");

            String fname = Environment.getExternalStorageDirectory()+"/print.txt";
            String fnamenew = Environment.getExternalStorageDirectory()+"/not_printed.txt";

            File currentFile = new File(fname);
            File newFile = new File(fnamenew);

            if (rename(currentFile, newFile)) Log.i("TAG", "Success");else Log.i("TAG", "Fail");

            //msgbox("El controlador de impresión está instalado (Ref -> Could be: EpsonTMBT)");
            //msgbox("El controlador de Epson TM BT no está instalado\n"+e.getMessage());
        }
    }

    private void printAclas(int copies) {
        try {
            Intent intent = cont.getPackageManager().getLaunchIntentForPackage("aclasprn.dts");
            intent.putExtra("mac","BT:"+gl.prpar);
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
            intent.putExtra("askprint",1);
            intent.putExtra("copies",copies);
            intent.putExtra("QRCodeStr",""+gl.QRCodeStr);
            cont.startActivity(intent);
        } catch (Exception e) {
            toastlong("El controlador de Aclas no está instalado");
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

			if (DT!=null) DT.close();
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
			if (DT!=null) DT.close();
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

	public void printView() {
		try {
			cont.startActivity(new Intent(cont, PrintView.class));
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}

    //endregion

    //region Pedidos / Mesas

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

			dt.close();

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

			dt.close();

            return dt.getCount();

        } catch (Exception e) {
            return -1;
        }
    }

	public String prefijoCaja() {

		Cursor dt;
		String val;

		try {

			sql="SELECT VALOR FROM P_PARAMEXT WHERE (ID=130) ";
			dt=Con.OpenDT(sql);
			dt.moveToFirst();
			val=dt.getString(0);

			val = val+"-";

			dt.close();

		} catch (Exception e) {
			val = gl.codigo_ruta+"-";
		}

		return val;
	}

	public void addToOrdenLog(long fecha,String metodo,String error,String nota) {
		/*
		try {
			clsClasses clsCls = new clsClasses();
			clsClasses.clsD_orden_log item = clsCls.new clsD_orden_log();
			clsD_orden_logObj D_orden_logObj=new clsD_orden_logObj(cont,Con,db);

			int newid=D_orden_logObj.newID("SELECT MAX(COREL) FROM D_orden_log");

			item.corel=newid;
			item.fecha=fecha;
			item.metodo=metodo;
			item.error=error;
			item.nota=nota;

			D_orden_logObj.add(item);
		} catch (Exception e) {
			toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
		*/
	}

    //endregion

    //region Caja

    public boolean validaCompletarCuenta(String corel) {

        int ccant,compl,cuenta;

        try {

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(cont,Con,db);
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(cont,Con,db);

            T_ordencuentaObj.fill("WHERE COREL='"+corel+"'");
            ccant=T_ordencuentaObj.count;compl=0;

            for (int i = 0; i <ccant; i++) {
                cuenta=T_ordencuentaObj.items.get(i).id;
                D_facturaObj.fill("WHERE (FACTLINK='"+corel+"_"+cuenta+"') AND (ANULADO=0)");
                if (D_facturaObj.count!=0) compl++;
            }

            return compl==ccant;

        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
			}.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());return false;
        }
    }

	public boolean esmesapedido(int idemp,String idgrupomesa) {

		boolean rslt=false;
		int idgrupo;

		try {
			idgrupo=Integer.parseInt(idgrupomesa);
		} catch (NumberFormatException e) {
			return false;
		}

		if (idemp==31) {
			if (idgrupo==27) rslt=true;
		}

		return rslt;
	}


    //endregion

    //region Cuentas

    public int cuentaActiva(String corel) {

        try {

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(cont,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+corel+"') ORDER BY ID");

            for (int i =0; i< T_ordencuentaObj.count; i++) {
                if (!cuentaPagada(corel, T_ordencuentaObj.items.get(i).id)) {
                    return T_ordencuentaObj.items.get(i).id;
                }
            }

            agregarCuenta(corel);

            int newcid=T_ordencuentaObj.newID("SELECT MAX(ID) FROM T_ordencuenta WHERE (corel='"+corel+"')");

            return newcid;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return 1;
        }
    }

	public void primeraCuenta(String corel) {

		try {

			clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(cont,Con,db);
			T_ordencuentaObj.fill("WHERE (COREL='"+corel+"') ");

			if (T_ordencuentaObj.count==0) agregarCuenta(corel);

		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}
	}


	private void agregarCuenta(String corel) {

        try {

            clsClasses clsCls = new clsClasses();
            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(cont,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            int newcid=T_ordencuentaObj.newID("SELECT MAX(ID) FROM T_ordencuenta WHERE (corel='"+corel+"')");

            cuenta.corel=corel;
            cuenta.id=newcid;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            T_ordencuentaObj.add(cuenta);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private Boolean cuentaPagada(String corr,int id) {

        try {

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(cont,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");

			return D_facturaObj.count!=0;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    public int cuentaActivaPostpago(String corel) {

        try {

            clsT_ordenObj T_ordenObj=new clsT_ordenObj(cont,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+corel+"') AND (PERCEP=0) ORDER BY CUENTA DESC");

            if (T_ordenObj.count>0) {
                return T_ordenObj.first().cuenta;
            }

            agregarCuenta(corel);

            int newcid=T_ordenObj.newID("SELECT MAX(cuenta) FROM T_orden WHERE (corel='"+corel+"')");

            return newcid;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return 1;
        }
    }

    //endregion

	//region Barriles

	public boolean barrilAbierto(String codprod) {

		Cursor dt=null;
		boolean rslt=false;

		try {

			dt=Con.OpenDT("SELECT PRODUCTO_PADRE FROM P_PRODUCTO WHERE (CODIGO='"+codprod+"') ");

			if (dt.getCount()>0) {
				dt.moveToFirst();
				int idprod=dt.getInt(0);

				dt=Con.OpenDT("SELECT CODIGO_PRODUCTO FROM D_BARRIL WHERE (CODIGO_PRODUCTO="+idprod+") AND (ACTIVO=1)");
				rslt=dt.getCount()>0;
			}

			dt.close();

		} catch (Exception e){
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		if (dt!=null) dt.close();
		return rslt;
	}

	public boolean esProductoBarril(int idprod) {

		Cursor dt=null;
		boolean rslt=false;
		gl.bar_prod=0;gl.bar_cant=0;

		try {

			dt=Con.OpenDT("SELECT CODIGO_TIPO,PRODUCTO_PADRE,FACTOR_PADRE FROM P_PRODUCTO WHERE (CODIGO_PRODUCTO="+idprod+")");

			if (dt.getCount()>0) {

				dt.moveToFirst();

				rslt=dt.getString(0).equalsIgnoreCase("PB");
				gl.bar_prod=dt.getInt(1);
				gl.bar_cant=dt.getInt(2);
			}

			dt.close();

		} catch (Exception e){
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		if (dt!=null) dt.close();
		return rslt;
	}

	public String barrilProd(int idprod) {

		Cursor dt=null;

		gl.bar_um="";gl.bar_idbarril="";

		try {

			dt=Con.OpenDT("SELECT UNIDBAS FROM P_PRODUCTO WHERE (CODIGO_PRODUCTO="+idprod+") ");
			dt.moveToFirst();
			gl.bar_um=dt.getString(0);
			dt.close();

			dt=Con.OpenDT("SELECT CODIGO_BARRIL FROM D_BARRIL WHERE (CODIGO_PRODUCTO="+idprod+") AND (ACTIVO=1)");
			dt.moveToFirst();
			gl.bar_idbarril=dt.getString(0);
			dt.close();

		} catch (Exception e){
			//msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
			String ss=e.getMessage();
			gl.bar_um="";gl.bar_idbarril="";
		}

		if (dt!=null) dt.close();

		return gl.bar_idbarril;
	}

	public boolean pendienteBarrilEnvio() {

		Cursor dt;
		int pb=0,pt=0;

		try {
			dt=Con.OpenDT("SELECT STATCOM FROM D_BARRIL WHERE (STATCOM=0)");
			pb=dt.getCount();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		try {
			dt=Con.OpenDT("SELECT STATCOM FROM D_BARRIL_TRANS WHERE (STATCOM=0)");
			pt=dt.getCount();
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
		}

		return (pb+pt)>0;
	}

	//endregion

	//region Aux

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

			if (DT!=null) DT.close();
            return cnt;
        } catch (Exception e) {
            //mu.msgbox(sql+"\n"+e.getMessage());
            return 0;
        }
    }

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

			DT.close();

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

			dt.close();

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

    public String cliFromNIT(String cod) {

        Cursor DT;
        String umm;

        try {

            String sql = "SELECT DESCCORTA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO=" + cod;
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            umm=DT.getString(0);

			DT.close();

            return  umm;

        } catch (Exception e) {
            return "";
        }
    }

    public boolean setScreenDim(Activity owner) {

        try {

            Point point = new Point();
            owner.getWindowManager().getDefaultDisplay().getRealSize(point);

            DisplayMetrics dm = cont.getResources().getDisplayMetrics();
            int width=dm.widthPixels;
            int height=dm.heightPixels;
            double x = Math.pow(width,2);
            double y = Math.pow(height,2);
            double diagonal = Math.sqrt(x+y);

            int dens=dm.densityDpi;
            double screenInches = diagonal/(double)dens;

            gl.scrx=point.x;gl.scry=point.y;
            gl.scrdim=(int) screenInches;
            gl.scrhoriz=gl.scrx>gl.scry;

            return gl.scrhoriz;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return true;
        }

    }

    public boolean validaCombo(int idcombo) {

	    int idopc,idprod;
	    String prname="",opname;
	    boolean flag;

        try {

            clsP_prodmenuopcObj P_prodmenuopcObj=new clsP_prodmenuopcObj(cont,Con,db);
            clsP_prodmenuopcdetObj P_prodmenuopc_detObj=new clsP_prodmenuopcdetObj(cont,Con,db);
            clsP_productoObj P_productoObj=new clsP_productoObj(cont,Con,db);

            citems.clear();
            P_productoObj.fill();
            if (P_productoObj.count==0) return false;

            P_prodmenuopcObj.fill("WHERE (CODIGO_MENU="+idcombo+")");

            for (int i = 0; i <P_prodmenuopcObj.count; i++) {

                idopc=P_prodmenuopcObj.items.get(i).codigo_menu_opcion;
                opname=P_prodmenuopcObj.items.get(i).nombre;
                P_prodmenuopc_detObj.fill("WHERE (CODIGO_MENU_OPCION="+idopc+")");

                for (int j = 0; j <P_prodmenuopc_detObj.count; j++) {
                    idprod=P_prodmenuopc_detObj.items.get(j).codigo_producto;
                    flag=false;

                    for (int p = 0; p <P_productoObj.count; p++) {
                        if (P_productoObj.items.get(p).codigo_producto==idprod) {
                            //if (P_productoObj.items.get(p).activo==0) {
                                flag=true;
                                prname=P_productoObj.items.get(p).desccorta;
                            //}
                            break;
                        }
                    }

                    if (!flag) {
                        prname=""+idprod+" , opc. "+opname;
                        if (!citems.contains(prname)) citems.add(prname);
                    }
                }
            }

            return citems.size()==0;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return true;
        }
    }

    public boolean modoSinInternet() {

        try {

            Cursor DT=Con.OpenDT("SELECT * FROM P_modo_emergencia");
            return DT.getCount()>0;

        } catch (Exception e) {
            return false;
        }

    }

	public void setGradResource(int iditem) {

		int pos, rr=R.drawable.frame_btn2,ss=R.drawable.frame_btn4;

		try {

			pos=iditem % 6;

			switch (pos) {
				case 0:
					rr=R.drawable.br0;ss=R.drawable.br0s;break;
				case 1:
					rr=R.drawable.br1;ss=R.drawable.br1s;break;
				case 2:
					rr=R.drawable.br2;ss=R.drawable.br2s;break;
				case 3:
					rr=R.drawable.br3;ss=R.drawable.br3s;break;
				case 4:
					rr=R.drawable.br4;ss=R.drawable.br4s;break;
				case 5:
					rr=R.drawable.br5;ss=R.drawable.br5s;break;
			}

		} catch (Exception e) {
			rr=R.drawable.frame_btn2;ss=R.drawable.frame_btn4;
		}

		gl.idgrres=rr;gl.idgrsel=ss;
	}

	public void fillSuper(clsVendedoresObj VendedoresObj) {
		try {
			sql="WHERE (ACTIVO=1) AND (CODIGO_VENDEDOR IN " +
					"(SELECT CODIGO_VENDEDOR FROM P_VENDEDOR_ROL WHERE (CODIGO_SUCURSAL="+gl.tienda+") " +
					"AND (CODIGO_ROL IN (2,3) ) ) ) ORDER BY NOMBRE ";
			VendedoresObj.fill(sql);

			if (VendedoresObj.count==0) {
				VendedoresObj.fill("WHERE (RUTA=" + gl.codigo_ruta+") AND ((NIVEL=2) OR (NIVEL=3)) ORDER BY NOMBRE");
			}
		} catch (Exception e) {
			msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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