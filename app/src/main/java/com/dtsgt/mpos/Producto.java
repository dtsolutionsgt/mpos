package com.dtsgt.mpos;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses.clsCD;
import com.dtsgt.ladapt.ListAdaptProd;

import java.util.ArrayList;

public class Producto extends PBase {

	private ListView listView;
	private EditText txtFilter;
	private Spinner spinFam;
	
	private ArrayList<String> spincode= new ArrayList<String>();
	private ArrayList<String> spinlist = new ArrayList<String>();
	
	private ArrayList<clsCD> items;
	private ListAdaptProd adapter;
	private AppMethods app;
	
	private String famid="",itemid,pname,prname,um,ubas;
	private int act,prodtipo;
	private double disp_und;
	private double disp_peso;
	boolean ordPorNombre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_producto);
		
		super.InitBase();

		listView = (ListView) findViewById(R.id.listView1);
		txtFilter = (EditText) findViewById(R.id.editText1);
		spinFam = (Spinner) findViewById(R.id.spinner1);

		try {

			prodtipo=gl.prodtipo;
			gl.prodtipo=0;
			this.setTitle("Producto");
			if (prodtipo==1) {this.setTitle("Producto con existencia");}

			app = new AppMethods(this, gl, Con, db);

			items = new ArrayList<clsCD>();

			act=0;
			ordPorNombre=gl.peOrdPorNombre;
			if (!gl.linea_sel.isEmpty()) {
				famid=gl.linea_sel;
			}

			setHandlers();

			fillSpinner();// Toast index 0

			listItems();

		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    //region Events

	public void porCodigo(View view) {
		try{
			ordPorNombre=false;
			listItems();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	public void porNombre(View view) {
		try{
			ordPorNombre=true;
			listItems();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    private void setHandlers() {
		try{

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					try {

						Object lvObj = listView.getItemAtPosition(position);
						clsCD item = (clsCD) lvObj;

						adapter.setSelectedIndex(position);

						switch (prodtipo) {
							case  1:
								if (prodBarra(item.Cod)) {
									toastcent("Producto tipo barra, no se puede ingresar la cantidad");
									finish();return;
								}
						}

						itemid = item.Cod;
						prname = item.Desc;
						gl.um = item.um;
						gl.pprodname = prname;
						gl.prodcod = item.codInt;
						gl.costo = item.costo;

						appProd();
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox(e.getMessage());
					}
				}

				;
			});

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

					try {
						Object lvObj = listView.getItemAtPosition(position);
						clsCD item = (clsCD) lvObj;

						adapter.setSelectedIndex(position);

						if (prodBarra(item.Cod)) {
							toastcent("Producto tipo barra, no se puede ingresar la cantidad");
							finish();return true;
						}

						itemid = item.Cod;
						prname = item.Desc;
						gl.um = item.um;
						gl.pprodname = prname;
						gl.prodcod = item.codInt;
						gl.costo = item.costo;

						appProd();
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox(e.getMessage());
					}
					return true;
				}
			});

			txtFilter.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {
					int tl;

					tl = txtFilter.getText().toString().length();

					if (tl == 0 || tl > 1) {
						listItems();
					}
				}
			});

			spinFam.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					TextView spinlabel;
					String scod;

					try {
						spinlabel = (TextView) parentView.getChildAt(0);
						spinlabel.setTextColor(Color.BLACK);
						spinlabel.setPadding(5, 0, 0, 0);
						spinlabel.setTextSize(18);
						spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

						scod = spincode.get(position);
						famid = scod;gl.linea_sel=scod;
						listItems();

						act += 1;
					} catch (Exception e) {
						addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
						mu.msgbox(e.getMessage());
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					return;
				}

			});

		} catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

    }

    //endregion

    //region Main
	
	private void listItems() {

		Cursor DT;
		clsCD vItem;
		int cantidad;
		double disp;
		String vF,cod,name,exist,um,fam;

		ArrayList<clsCD> vitems = new ArrayList<clsCD>();;

		items.clear();itemid="*";//famid="0";

		vF=txtFilter.getText().toString().replace("'","");

		String sql = "";
		fam=famid;if (!vF.isEmpty()) fam="0";if (fam.isEmpty())fam="0";

        try {

            switch (prodtipo) {

                case 0: // Preventa
                    sql="SELECT CODIGO,DESCCORTA,UNIDBAS,ACTIVO, CODIGO_PRODUCTO, COSTO FROM P_PRODUCTO WHERE 1=1 ";
                    if (!fam.equalsIgnoreCase("0")) sql=sql+"AND (LINEA='"+fam+"') ";
                    if (vF.length()>0) sql=sql+"AND ((DESCCORTA LIKE '%"+vF+"%') OR (CODIGO LIKE '%"+vF+"%')) ";
                    if (ordPorNombre) sql+="ORDER BY DESCCORTA"; else sql+="ORDER BY CODIGO";
                    break;

                case 1:  // Venta

                    sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_PRODPRECIO.UNIDADMEDIDA, " +
                            "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO, COSTO  " +
                            "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO INNER JOIN " +
                            "P_PRODPRECIO ON P_STOCK.CODIGO=P_PRODPRECIO.CODIGO_PRODUCTO  " +
                            "WHERE (P_STOCK.CANT > 0) AND (P_PRODUCTO.ACTIVO=1) AND (P_PRODUCTO.CODIGO_TIPO ='P')";

                    if (!fam.equalsIgnoreCase("0"))  sql=sql+"AND (P_PRODUCTO.LINEA="+fam+") ";
                    if (vF.length()>0) sql=sql+"AND ((P_PRODUCTO.DESCCORTA LIKE '%"+vF+"%') OR (P_PRODUCTO.CODIGO LIKE '%"+vF+"%')) ";

                    sql += "UNION ";
                    sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA," +
                            "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO, COSTO FROM P_PRODUCTO  INNER JOIN " +
                            "P_PRODPRECIO ON P_PRODUCTO.CODIGO_PRODUCTO = P_PRODPRECIO.CODIGO_PRODUCTO  " +
                            "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M')) AND (P_PRODUCTO.ACTIVO=1)";
                        if (!fam.equalsIgnoreCase("0")) sql=sql+"AND (P_PRODUCTO.LINEA="+fam+") ";

                    if (vF.length()>0) sql=sql+"AND ((P_PRODUCTO.DESCCORTA LIKE '%"+vF+"%') OR (P_PRODUCTO.CODIGO LIKE '%"+vF+"%')) ";
                    if (ordPorNombre) sql += "ORDER BY P_PRODUCTO.DESCCORTA"; else sql+="ORDER BY P_PRODUCTO.CODIGO";
                    break;

                case 2: // Recarga
                    sql="SELECT CODIGO,DESCCORTA,UNIDBAS,CODIGO_TIPO, CODIGO_PRODUCTO, COSTO FROM P_PRODUCTO WHERE CODIGO_TIPO='P' ";
                    if (!fam.equalsIgnoreCase("0")) sql=sql+"AND (LINEA='"+fam+"') ";
                    if (vF.length()>0) sql=sql+"AND ((DESCCORTA LIKE '%"+vF+"%') OR (CODIGO LIKE '%"+vF+"%')) ";
                    if (ordPorNombre) sql+="ORDER BY DESCCORTA"; else sql+="ORDER BY CODIGO";
                    break;

                case 4: // Egreso , Traslado
                    if (gl.idalm!=gl.idalmpred) {
                        sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK_ALMACEN.UNIDADMEDIDA, " +
                                "P_PRODUCTO.ACTIVO, P_STOCK_ALMACEN.CODIGO_PRODUCTO, P_PRODUCTO.COSTO " +
                                "FROM P_STOCK_ALMACEN INNER JOIN " +
                                "P_PRODUCTO ON P_STOCK_ALMACEN.CODIGO_PRODUCTO=P_PRODUCTO.CODIGO_PRODUCTO " +
                                "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") ";

                        if (!fam.equalsIgnoreCase("0"))  sql+="AND (P_PRODUCTO.LINEA="+fam+") ";
                        if (vF.length()>0) sql+="AND ((P_PRODUCTO.DESCCORTA LIKE '%"+vF+"%') OR (P_PRODUCTO.CODIGO LIKE '%"+vF+"%')) ";
                        sql+="ORDER BY P_PRODUCTO.DESCCORTA";

                    } else {
                        sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK.UNIDADMEDIDA, " +
                                "P_PRODUCTO.ACTIVO, P_STOCK.CODIGO, P_PRODUCTO.COSTO " +
                                "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO " +
                                "WHERE (1=1) ";
                        if (!fam.equalsIgnoreCase("0"))  sql+="AND (P_PRODUCTO.LINEA="+fam+") ";
                        if (vF.length()>0) sql+="AND ((P_PRODUCTO.DESCCORTA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
                        sql+="ORDER BY P_PRODUCTO.DESCCORTA";
                    }
                    break;
            }

            DT=Con.OpenDT(sql);

			cantidad = DT.getCount();

			if (cantidad==0) {
                items.clear();vitems.clear();
            } else {
                DT.moveToFirst();
                while (!DT.isAfterLast()) {
                    cod = DT.getString(0);
                    name = DT.getString(1);
                    um = DT.getString(2);
                    disp=getDisp(cod);

                    vItem = clsCls.new clsCD();

                    vItem.Cod = cod;
                    vItem.prec="Precio : "+gl.peMon+prodPrecioBase(cod);
                    if (prodtipo!=1) vItem.prec="";
                    vItem.Desc = name;
                    vItem.codInt=DT.getInt(4);
                    vItem.costo = DT.getDouble(5);

                    if (disp>0) exist="Exist : "+mu.frmdecno(disp)+" "+um; else exist="";

                    vItem.Text=exist;if (prodtipo==2) vItem.Text="";

                    items.add(vItem);vitems.add(vItem);

                    DT.moveToNext();
                }
            }

			DT.close();

        } catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
	    }

	    items = (ArrayList<clsCD>) vitems.clone();

		adapter=new ListAdaptProd(this,vitems);
		listView.setAdapter(adapter);

	}
	
	private void appProd(){
		try{
			gl.gstr=itemid;
			super.finish();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	private double getDisp(String prodid) {
		Cursor dt;
		String umstock = "";
		double umf1,umf2,umfactor;

        disp_und =0;
		
		try {			
			//sql="SELECT UNIDADMEDIDA FROM P_PRODPRECIO WHERE (CODIGO='"+prodid+"') AND (NIVEL="+gl.nivel+")";
			sql=" SELECT UNIDADMEDIDA FROM P_STOCK WHERE (CODIGO='"+prodid+"') ";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0){
				dt.moveToFirst();
				um=dt.getString(0);
			}

			dt.close();
			
			sql="SELECT UNIDBAS	FROM P_PRODUCTO WHERE CODIGO='"+prodid+"'";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0){
				dt.moveToFirst();
				ubas=dt.getString(0);
			}

            if (dt!=null) dt.close();

		} catch (Exception e) {
			//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return 0;
		}
		
		try {
			sql=" SELECT  IFNULL(SUM(A.CANT),0) AS CANT, IFNULL(SUM(A.PESO),0) AS PESO " +
				" FROM(SELECT IFNULL(SUM(CANT),0) AS CANT, IFNULL(SUM(PESO),0) AS PESO " +
				" FROM P_STOCK WHERE (CODIGO='"+prodid+"') AND (UNIDADMEDIDA='"+um+"') ) AS A";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0) {

				dt.moveToFirst();
				disp_und =dt.getDouble(0);
				disp_peso =dt.getDouble(1);
			}

            if (dt!=null) dt.close();

			if (disp_und ==0) {

				sql=" SELECT  IFNULL(SUM(A.CANT),0) AS CANT, IFNULL(SUM(A.PESO),0) AS PESO " +
					" FROM(SELECT IFNULL(SUM(CANT),0) AS CANT, IFNULL(SUM(PESO),0) AS PESO " +
					" FROM P_STOCK WHERE (CODIGO='"+prodid+"')) AS A";
				dt=Con.OpenDT(sql);

				if (dt.getCount()>0) {
					dt.moveToFirst();
					disp_und =dt.getDouble(0);
					disp_peso =dt.getDouble(1);
				}
			}

            if (dt!=null) dt.close();

			if (disp_und >0) return disp_und;

		} catch (Exception e) {
			//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
	    }
		
		try {
			sql="SELECT UNIDADMEDIDA FROM P_STOCK WHERE (CODIGO='"+prodid+"')";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0){
				dt.moveToFirst();

				umstock=dt.getString(0);
			}

            if (dt!=null) dt.close();

			sql="SELECT FACTORCONVERSION FROM P_FACTORCONV " +
				"WHERE (PRODUCTO='"+prodid+"') AND (UNIDADSUPERIOR='"+um+"') AND (UNIDADMINIMA='"+ubas+"')";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0) {
				dt.moveToFirst();			
				umf1=dt.getDouble(0);
			} else {	
				return 0;
			}

            if (dt!=null) dt.close();

			sql="SELECT FACTORCONVERSION FROM P_FACTORCONV " +
				"WHERE (PRODUCTO='"+prodid+"') AND (UNIDADSUPERIOR='"+umstock+"') AND (UNIDADMINIMA='"+ubas+"')";
			dt=Con.OpenDT(sql);

			if (dt.getCount()>0) {
				dt.moveToFirst();			
				umf2=dt.getDouble(0);
			} else {
				if (dt!=null) dt.close();
				return 0;
			}

            if (dt!=null) dt.close();

			umfactor=umf1/umf2;

			sql=" SELECT  IFNULL(SUM(A.CANT),0) AS CANT, IFNULL(SUM(A.PESO),0) AS PESO " +
				" FROM(SELECT IFNULL(SUM(CANT),0) AS CANT, IFNULL(SUM(PESO),0) AS PESO " +
				" FROM P_STOCK WHERE (CODIGO='"+prodid+"') AND (UNIDADMEDIDA='"+umstock+"')) AS A";
			dt=Con.OpenDT(sql);
			if(dt.getCount()>0) {
				dt.moveToFirst();
				disp_und =dt.getDouble(0);
				disp_peso =dt.getDouble(1);
			}

            if (dt!=null) dt.close();

			return disp_und;
		} catch (Exception e) {
			//addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
			return 0;
	    }	
		
	}

    //endregion

	//region Aux
	
	private void fillSpinner(){
		Cursor DT;
		String icode,iname;
		int ssel=0;

        try {
            spincode.clear();spinlist.clear();
            spincode.add("0");spinlist.add("TODOS LOS PRODUCTOS");
        } catch (Exception e) {}

        try {

            switch (prodtipo) {
                case 0: // Preventa
                    sql="SELECT CODIGO_LINEA,NOMBRE FROM P_LINEA ORDER BY NOMBRE";
                    break;
                case 1:  // Venta
                    sql="SELECT DISTINCT P_PRODUCTO.LINEA,P_LINEA.NOMBRE "+
						"FROM P_STOCK INNER JOIN P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO " +
						"INNER JOIN P_LINEA ON P_PRODUCTO.LINEA=P_LINEA.CODIGO_LINEA " +
						"WHERE (P_STOCK.CANT > 0) AND (P_PRODUCTO.CODIGO_TIPO = 'P') AND (P_LINEA.ACTIVO=1) " +
						"UNION "+
						"SELECT DISTINCT P_PRODUCTO.LINEA,P_LINEA.NOMBRE "+
						"FROM P_PRODUCTO INNER JOIN P_LINEA ON P_PRODUCTO.LINEA=P_LINEA.CODIGO_LINEA " +
						"WHERE (P_PRODUCTO.CODIGO_TIPO = 'S' OR P_PRODUCTO.CODIGO_TIPO = 'M') AND (P_LINEA.ACTIVO=1) " +
						"ORDER BY P_LINEA.NOMBRE";
                    break;
                case 2: // Mercadeo propio
                    sql="SELECT CODIGO_LINEA,Nombre FROM P_LINEA ORDER BY Nombre";break;
                case 3:  // Mercadeo comp
                    break;
                case 4:  // Egreso , Traslado
                    if (gl.idalm==gl.idalmpred) {
                        sql = "SELECT DISTINCT P_LINEA.CODIGO_LINEA, P_LINEA.NOMBRE " +
                                "FROM P_STOCK INNER JOIN " +
                                "P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO  " +
                                "INNER JOIN P_LINEA ON P_PRODUCTO.LINEA= P_LINEA.CODIGO_LINEA " +
                                "ORDER BY P_LINEA.NOMBRE ";
                    } else {
                        sql = "SELECT DISTINCT P_LINEA.CODIGO_LINEA, P_LINEA.NOMBRE " +
                                "FROM P_STOCK_ALMACEN INNER JOIN " +
                                "P_PRODUCTO ON P_STOCK_ALMACEN.CODIGO_PRODUCTO = P_PRODUCTO.CODIGO_PRODUCTO  " +
                                "INNER JOIN P_LINEA ON P_PRODUCTO.LINEA = P_LINEA.CODIGO_LINEA " +
                                "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") ORDER BY P_LINEA.NOMBRE ";
                    }
                    break;
            }

            DT=Con.OpenDT(sql);

            int pos=1;
            if (DT.getCount()>0) {
                DT.moveToFirst();
                while (!DT.isAfterLast()) {

                    icode=DT.getString(0);
                    iname=DT.getString(1);

                    if (!famid.isEmpty()) {
                        if (!gl.linea_sel.isEmpty()) {
                            if (icode.equalsIgnoreCase(gl.linea_sel)) {
                                ssel=pos;famid=gl.linea_sel;
                            }
                        }
                    }

                    spincode.add(icode);
                    spinlist.add(iname);

                    DT.moveToNext();pos++;
                }
            }

            if (DT!=null) DT.close();
        } catch (Exception e) {
            mu.msgbox( e.getMessage());
        }

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
		spinFam.setAdapter(dataAdapter);

		try {
		    if (famid.isEmpty()) {
                spinFam.setSelection(0);
                String scod = spincode.get(0);
                famid = scod;
            } else {
                spinFam.setSelection(ssel);
            }
		} catch (Exception e) {
			spinFam.setSelection(0);
	    }

	}	

	public String ltrim(String ss,int sw) {
		try{
			int l=ss.length();
			if (l>sw) {
				ss=ss.substring(0,sw);
			} else {
				String frmstr="%-"+sw+"s";
				ss=String.format(frmstr,ss);
			}


		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
		return ss;
	}

	private boolean prodBarra(String prodid) {
		try {
			return app.prodBarra(prodid);
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			return false;
		}
	}

    public void limpiaFiltro(View view) {
		try{
			txtFilter.setText("");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

    }

    private String prodPrecioBase(String prid) {
        Cursor DT;
        double pr;
        String sprec="";

        try {
            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO="+app.codigoProducto(prid)+") AND (NIVEL="+gl.nivel+") ";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            pr=DT.getDouble(0);
            if (DT!=null) DT.close();
        } catch (Exception e) {
            pr=0;
        }

        sprec=mu.frmdec(pr);
        return sprec;
    }



    //endregion

    //region Activity Events
	
	protected void onResume() {
		try{
			super.onResume();
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

	//endregion

}
