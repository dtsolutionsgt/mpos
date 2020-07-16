package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsExist;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.ladapt.ListAdaptExist;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInvActual;
import com.dtsgt.webservice.wsInventCompartido;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class Exist extends PBase {

	private ListView listView;
	private EditText txtFilter;
	private TextView lblReg;
	private ProgressBar pbar;

    private AppMethods app;

	private ArrayList<clsClasses.clsExist> items= new ArrayList<clsClasses.clsExist>();
	private ListAdaptExist adapter;
	private clsClasses.clsExist selitem;

    private wsInventCompartido wsi;
    private clsDocExist doc;
    private printer prn;
    private clsRepBuilder rep;

    private Runnable printclose;
    private Runnable recibeInventario;

	private int tipo,lns, cantExistencia;
	private String itemid,prodid,savecant;
	private boolean bloqueado=false;
    private double cantT,disp,dispm,dispT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exist);
		
		super.InitBase();
		addlog("Exist",""+du.getActDateTime(),String.valueOf(gl.vend));
		
		tipo=((appGlobals) vApp).tipo;

		app = new AppMethods(this, gl, Con, db);
		gl.validimp=app.validaImpresora();
		if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

		listView = (ListView) findViewById(R.id.listView1);
		txtFilter = (EditText) findViewById(R.id.txtFilter);
		lblReg = (TextView) findViewById(R.id.textView1);lblReg.setText("");
		pbar=findViewById(R.id.progressBar5);pbar.setVisibility(View.INVISIBLE);

		setHandlers();

			try {
				sql="DELETE FROM P_STOCK WHERE CANT+CANTM=0";
				db.execSQL(sql);
			} catch (SQLException e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			}
		
		rep=new clsRepBuilder(this,gl.prw,false,gl.peMon,gl.peDecImp, "");
		
		listItems();	
		
		printclose= new Runnable() {
		    public void run() {
		    	Exist.super.finish();
		    }
		};
		
		prn=new printer(this,printclose,gl.validimp);
		doc=new clsDocExist(this,prn.prw,"");

		/*int cant = CantExistencias();
		Toast.makeText(this, "Cantidad." + cant, Toast.LENGTH_SHORT).show();*/

        app.getURL();
        wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,3,db,Con);

        recibeInventario = new Runnable() {
            public void run() {
                bloqueado=false;
                if (wsi.errflag) {
                    msgbox(wsi.error);
                } else {
                    confirmaInventario();
                    listItems();
                    pbar.setVisibility(View.INVISIBLE);
                }
            }
        };

        bloqueado=false;
        if (gl.peInvCompart) {
            pbar.setVisibility(View.VISIBLE);
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    bloqueado=true;
                    wsi.execute(recibeInventario);
                }
            };
            mtimer.postDelayed(mrunner,200);
        }
	}

    private void confirmaInventario() {
        try {
            Intent intent = new Intent(Exist.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);
        } catch (Exception e) {}
    }

	//region Events
	
	public void printDoc(View view) {
		try{
			if(items.size()==0){
				msgbox("No hay inventario disponible");
				return;
			}
			if (doc.buildPrint("0",0)) {
				//prn.printask();
                app.doPrint();
			}
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	private void printEpson() {
		try {
			Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.dts.epsonprint");
			intent.putExtra("mac","BT:00:01:90:85:0D:8C");
			intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/print.txt");
			intent.putExtra("askprint",1);
			this.startActivity(intent);
		} catch (Exception e) {
			msgbox(e.getMessage());
		}
	}
		
	public void limpiaFiltro(View view) {
		try{
			txtFilter.setText("");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}
	
	//endregion

	//region Main
	
 	private void setHandlers(){

		try{
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
					try {
						if(!gl.dev){
							Object lvObj = listView.getItemAtPosition(position);
							clsClasses.clsExist item = (clsClasses.clsExist)lvObj;

							itemid=item.Cod;

							adapter.setSelectedIndex(position);

							//appProd();

						}else {

							Object lvObj = listView.getItemAtPosition(position);
							clsClasses.clsExist vItem = (clsClasses.clsExist)lvObj;

							prodid=vItem.Cod;

							adapter.setSelectedIndex(position);

							savecant="";
							setCant();
						}
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox( e.getMessage());
					}
				};
			});


			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsClasses.clsExist item = (clsClasses.clsExist) lvObj;

						adapter.setSelectedIndex(position);
						if (item.flag==1 | item.flag==2) itemDetail(item);
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
					}
					return true;
				}
			});

			txtFilter.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {}

				public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

				public void onTextChanged(CharSequence s, int start,int before, int count) {
					int tl;

					tl=txtFilter.getText().toString().length();

					if (tl==0 || tl>1) listItems();
				}
			});
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	   
	}

	private void setCant(){

		try{

            ExDialog alert = new ExDialog(this);

			getDisp();
			alert.setTitle("Ingrese la cantidad a devolver");
			alert.setMessage("Existencias del producto "+prodid+" :  "+dispT);

			final EditText input = new EditText(this);
			input.setText(savecant);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);

			alert.setView(input);

			alert.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					closekeyb();
				}
			});

			alert.setNeutralButton("Devolver en estado Bueno",  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					closekeyb();
					setCant("B",input.getText().toString());
				}
			});

			alert.setPositiveButton("Devolver en estado Malo", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					closekeyb();
					setCant("M",input.getText().toString());
				}
			});

			final AlertDialog dialog = alert.create();
			dialog.show();

			showkeyb();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void getDisp(){
		Cursor DT;

		try {
			sql="SELECT CANT,CANTM FROM P_STOCK WHERE CODIGO='"+prodid+"'";
			DT=Con.OpenDT(sql);

			if (DT.getCount()==0) {
				disp=0;dispm=0;	return;
			}

			DT.moveToFirst();

			disp=DT.getDouble(0);
			dispm=DT.getDouble(1);
			dispT = disp + dispm;

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox( e.getMessage());
		}
	}

	private void setCant(String est,String s){
		double val;

		try{
			try {
				val=Double.parseDouble(s);
				if (val<0) throw new Exception();
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
				mu.msgbox("Cantidad incorrecta");return;
			}

			if (est.equalsIgnoreCase("B")) {
				if (val>dispT) {
					savecant=s;
					setCant();
					mu.msgbox("Cantidad mayor que existencia : "+dispT);
					return;
				}
			} else {
				if (val>dispT) {
					savecant=s;
					setCant();
					mu.msgbox("Cantidad mayor que existencia : "+dispT);
					return;
				}
			}

			cantT=val;

			addItem(est);

			Toast.makeText(this, "Agregado correctamente", Toast.LENGTH_SHORT).show();
			super.finish();

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}


	private void addItem(String est){
		Cursor DT;
		String SQL;

		try {
			SQL="SELECT * FROM T_DEVOL WHERE CODIGO= '"+prodid+"'";
			DT=Con.OpenDT(SQL);

			if (DT.getCount()==0) {
				sql = "INSERT INTO T_DEVOL VALUES('" + prodid + "',0,0)";
				db.execSQL(sql);
			}

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
		}

		try {
			if (est.equalsIgnoreCase("B")) {
				sql="UPDATE T_DEVOL SET CANT=CANT+"+cantT+" WHERE CODIGO='"+prodid+"'";
			} else {
				sql="UPDATE T_DEVOL SET CANTM=CANTM+"+cantT+" WHERE CODIGO='"+prodid+"'";
			}
			db.execSQL(sql);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return;
		}

		try {
			sql="DELETE FROM T_DEVOL WHERE CANT=0 AND CANTM=0";
			db.execSQL(sql);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox("Error : " + e.getMessage());
		}

		//updData(est);
		listItems();

	}


	public float CantExistencias() {
		Cursor DT;
		float cantidad=0,cantb=0;

		try {

			sql = "SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,P_STOCK.CANT,P_STOCK.CANTM,P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS " +
					"FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=P_STOCK.CODIGO  WHERE 1=1 ";
			if (Con != null){
				DT = Con.OpenDT(sql);
				cantidad = DT.getCount();
			}else {
				cantidad = 0;
			}

			cantb = 0;

			cantidad=cantidad+cantb;
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return 0;
		}

		return cantidad;
	}

	private void listItems() {
		Cursor dt, dp;
		clsClasses.clsExist item,itemm,itemt;
		String vF,pcod, cod, name, um, ump, sc, scm, sct="", sp, spm, spt="";
		double val, valm, valt, peso, pesot;
		int icnt;

		items.clear();lblReg.setText(" ( 0 ) ");

		vF = txtFilter.getText().toString().replace("'", "");

		try {

			sql = "SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA " +
					"FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK.CODIGO  WHERE 1=1 ";
			if (vF.length() > 0) sql = sql + "AND ((P_PRODUCTO.DESCLARGA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
			sql += "GROUP BY P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA ";
			sql += "ORDER BY P_PRODUCTO.DESCLARGA";

			dp = Con.OpenDT(sql);

			if (dp.getCount() == 0) {
				adapter = new ListAdaptExist(this, items,gl.usarpeso);
				listView.setAdapter(adapter);
				return;
			}

			lblReg.setText(" ( " + dp.getCount() + " ) ");
			dp.moveToFirst();


			while (!dp.isAfterLast()) {

				pcod=dp.getString(0);
                valt=0;pesot=0;

				sql =  "SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,SUM(P_STOCK.CANT) AS TOTAL,SUM(P_STOCK.CANTM)," +
						"P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS,SUM(P_STOCK.PESO)  " +
					   "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK.CODIGO  " +
					   "WHERE (P_PRODUCTO.CODIGO_PRODUCTO='"+pcod+"') " +
                       "GROUP BY P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS ";
           		sql+=  "ORDER BY TOTAL ";

				dt = Con.OpenDT(sql);
				icnt=dt.getCount();
                if (icnt==1) {
                    dt.moveToFirst();
                    if (dt.getDouble(2)>0 && dt.getDouble(3)>0) icnt=2;
                }

				item = clsCls.new clsExist();
				item.Cod = pcod;
				item.Desc = dp.getString(1);
				item.flag = 0;
				item.items=icnt;
				items.add(item);

				//if (dt.getCount() == 0) return;

				if (dt.getCount()>0) dt.moveToFirst();
				while (!dt.isAfterLast()) {

					cod = dt.getString(0);
					name = dt.getString(1);
					val = dt.getDouble(2);
					valm = dt.getDouble(3);
					um = dt.getString(4);
					peso =  dt.getDouble(9);

					valt += val + valm;
					pesot += peso ;

					ump = gl.umpeso;
					sp = mu.frmdecimal(peso, gl.peDecImp) + " " + rep.ltrim(ump, 3);
					if (!gl.usarpeso) sp = "";
					spm = mu.frmdecimal(peso, gl.peDecImp) + " " + rep.ltrim(ump, 3);
					if (!gl.usarpeso) spm = "";
					spt = mu.frmdecimal(pesot, gl.peDecImp) + " " + rep.ltrim(ump, 3);
					if (!gl.usarpeso) spt = "";

					sc = mu.frmdecimal(val, gl.peDecImp) + " " + rep.ltrim(um, 2);
					scm = mu.frmdecimal(valm, gl.peDecImp) + " " + rep.ltrim(um, 2);
					sct = mu.frmdecimal(valt, gl.peDecImp) + " " + rep.ltrim(um, 2);

					item = clsCls.new clsExist();
					itemm = clsCls.new clsExist();

					item.Cod = cod;itemm.Cod = cod;
					item.Fecha = cod;itemm.Fecha = cod;
					item.Desc = name;itemm.Desc = name;
					item.cant = val;itemm.cant = val;
					item.cantm = valm;itemm.cantm = valm;

					item.Valor = sc;itemm.Valor = sc;
					item.ValorM = scm;itemm.ValorM = scm;
					item.ValorT = sct;itemm.ValorT = sct;

					item.Peso = sp;itemm.Peso = sp;
					item.PesoM = spm;itemm.PesoM = spm;
					item.PesoT = spt;item.PesoT = spt;

					item.Lote = dt.getString(5);//if (mu.emptystr(item.Lote)) item.Lote =cod;
                    itemm.Lote = item.Lote;
					item.Doc = dt.getString(6);itemm.Doc = dt.getString(6);
					item.Centro = dt.getString(7);itemm.Centro = dt.getString(7);
					item.Stat = dt.getString(8);itemm.Stat = dt.getString(8);

					if (val>0) {
						item.flag = 1;
						items.add(item);

						if (valm > 0) {
						    icnt++;
							itemm.flag = 2;
							items.add(itemm);
  						}
					} else {
  						item.flag = 2;
						items.add(item);
					}
					dt.moveToNext();
				}

				if (icnt>1) {
                    itemt = clsCls.new clsExist();
                    itemt.ValorT = sct;
                    itemt.PesoT = spt;
                    itemt.flag = 3;
                    items.add(itemt);
                }

				dp.moveToNext();
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox(e.getMessage());
		}

		adapter = new ListAdaptExist(this, items, gl.usarpeso);
		listView.setAdapter(adapter);

	}

	private void listItemsOld() {
		Cursor DT;
		clsClasses.clsExist item;
		String vF, cod, name, um, ump, sc, scm, sct, sp, spm, spt;
		double val, valm, valt,peso,pesom,pesot;

		items.clear();

		vF = txtFilter.getText().toString().replace("'", "");

		try {

			//vSQL="SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,SUM(P_STOCK.CANT),SUM(P_STOCK.CANTM) "+
			//     "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=P_STOCK.CODIGO  WHERE 1=1 ";
			//if (vF.length()>0) vSQL=vSQL+"AND ((P_PRODUCTO.DESCLARGA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
			//vSQL+="GROUP BY P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA ORDER BY P_PRODUCTO.DESCLARGA";

			sql = "SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,P_STOCK.CANT,P_STOCK.CANTM,P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS " +
					"FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=P_STOCK.CODIGO  WHERE 1=1 ";
			if (vF.length() > 0)
				sql = sql + "AND ((P_PRODUCTO.DESCLARGA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
			sql += "ORDER BY P_PRODUCTO.DESCLARGA,P_STOCK.UNIDADMEDIDA";

			DT = Con.OpenDT(sql);

			lblReg.setText(" ( " + DT.getCount() + " ) ");

			if (DT.getCount() == 0) return;

			DT.moveToFirst();
			while (!DT.isAfterLast()) {

				cod = DT.getString(0);
				name = DT.getString(1);
				val = DT.getDouble(2);
				valm = DT.getDouble(3);
				um = DT.getString(4);
				peso=0;
				pesom=0;

				valt=val+valm;
				pesot=peso+pesom;

				ump = "";
				sp = mu.frmdecimal(peso, gl.peDecImp) + " " + rep.ltrim(ump, 3);
				if (!gl.usarpeso) sp = "";
				spm = mu.frmdecimal(pesom, gl.peDecImp) + " " + rep.ltrim(ump, 3);
				if (!gl.usarpeso) spm = "";
				spt = mu.frmdecimal(pesot, gl.peDecImp) + " " + rep.ltrim(ump, 3);
				if (!gl.usarpeso) spt = "";

				sc = mu.frmdecimal(val, gl.peDecImp) + " " + rep.ltrim(um, 6);
				scm = mu.frmdecimal(valm, gl.peDecImp) + " " + rep.ltrim(um, 6);
				sct = mu.frmdecimal(valt, gl.peDecImp) + " " + rep.ltrim(um, 6);

				item = clsCls.new clsExist();

				item.Cod = cod;
				item.Fecha = cod;
				item.Desc = name;
				item.cant = val;
				item.cantm = valm;

				item.Valor = sc;
				item.ValorM = scm;
				item.ValorT = sct;

				item.Peso = sp;
				item.PesoM = spm;
				item.PesoT = spt;

				item.Lote = DT.getString(5);
				item.Doc = DT.getString(6);
				item.Centro = DT.getString(7);
				item.Stat = DT.getString(8);

				if (valm == 0) item.flag = 0;
				else item.flag = 1;

				items.add(item);

				DT.moveToNext();
			}
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			mu.msgbox(e.getMessage());
		}

		adapter = new ListAdaptExist(this, items, gl.usarpeso);
		listView.setAdapter(adapter);
	}

	private void appProd(){
		try{
			if (tipo==0) return;

			((appGlobals) vApp).gstr=itemid;
			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private void itemDetail(clsClasses.clsExist item) {
		String ss;

		try{
			ss="Lote : "+item.Lote+"\n";
			ss+="Documento : "+item.Doc+"\n";
			ss+="Centro : "+item.Centro+"\n";
			ss+="Estado : "+item.Stat+"\n";


            ExDialog dialog = new ExDialog(this);
			dialog.setMessage(ss);

			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialog.show();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}


	//endregion

	private class clsDocExist extends clsDocument {

		public clsDocExist(Context context, int printwidth, String archivo) {
			super(context, printwidth,gl.peMon,gl.peDecImp, archivo);

			nombre="REPORTE DE EXISTENCIAS";
			numero="";
			serie="";
			ruta=gl.ruta;
			vendedor=gl.vendnom;
			cliente="";
			vendcod=gl.vend;
			fsfecha=du.getActDateStr();

		}

		protected boolean buildDetail() {
			clsExist item;
			String s1,s2,lote;
            int ic,tot=0;

			try {
				String vf=txtFilter.getText().toString();
				if (!mu.emptystr(vf)) rep.add("Filtro : "+vf);

				rep.empty();
				rep.line();lns=items.size();

				rep.add("CODIGO   DESCRIPCION");
				rep.add("UM       CANTIDAD          ESTADO");
				rep.line();

				for (int i = 0; i <items.size(); i++) {

					item=items.get(i);
                    ic=item.items;
                    lote=item.Lote;

                    switch (item.flag) {
                        case 0:
                            rep.add(item.Cod + "   " + item.Desc);
                            /*if (ic<2) {
                                if (!(lote==null || lote.isEmpty())) rep.add(item.Cod);
                            } else {
                                rep.add(item.Cod);
                            }*/
                            break;
                        case 1:
							ss=rep.rtrim(item.Peso ,2)+"       "+rep.rtrim(""+item.Valor,4);
							ss=ss+" "+rep.rtrim("BUENO",18);
							rep.add(ss);
							tot+=item.cant;break;
                        case 2:
                            rep.add("Estado malo");
                            rep.add3lrr(item.Lote,item.ValorM,item.PesoM);
							tot+=item.cant;break;
                        case 3:
							ss=rep.rtrim("", 6)+" "+rep.rtrim(frmdecimal(tot,2),8);
							rep.add(ss);
                    }

					//rep.add3lrr(item.Cod,item.Peso,item.Valor);
					//if (item.flag==1) rep.add3lrr("Est.malo" ,item.PesoM,item.ValorM);
				}
  				rep.line();
				return true;
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			    msgbox(e.getMessage());
				return false;
			}

		}

		protected boolean buildFooter() {

			double SumaPeso = 0;
			double SumaCant = 0;

			try {

				SumaPeso = app.getPeso();
				SumaCant = app.getCantidad();

				rep.empty();
				rep.add("Total unidades:  " + StringUtils.leftPad(mu.frmdecimal(SumaCant,gl.peDecImp), 10));
				//rep.add("Total peso:      " + StringUtils.leftPad(mu.frmdecimal(SumaPeso,gl.peDecImp), 10));
				rep.add("Total registros: " + StringUtils.leftPad(String.valueOf(lns), 7));
				rep.empty();
				rep.empty();
				rep.empty();
				rep.add("Firma Vendedor" + StringUtils.leftPad( "____________________",5));
				rep.empty();
				rep.empty();
				rep.add("Firma Auditor" + StringUtils.leftPad("____________________",6));
				rep.empty();
				rep.empty();
				rep.empty();

				return true;
			} catch (Exception e) {
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
				return false;
			}

		}

	}


	//region Activity Events

	@Override
	protected  void onResume(){
		try{
			super.onResume();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}


	}

	@Override
	protected void onPause() {
		try{
			super.onPause();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion
}
