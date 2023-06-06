package com.dtsgt.mpos;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsExist;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_stockObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbPStock;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.ListAdaptExist;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Exist extends PBase {

	private ListView listView;
	private EditText txtFilter;
	private TextView lblReg,lblTit,lblTotal,lblalm;
	private ProgressBar pbar;
    private RelativeLayout relalm;

    private AppMethods app;

	private ArrayList<clsClasses.clsExist> items= new ArrayList<clsClasses.clsExist>();
	private ListAdaptExist adapter;
	private clsClasses.clsExist selitem;
    private clsP_almacenObj P_almacenObj;

    private clsDocExist doc;
    private printer prn;
    private clsRepBuilder rep;

    private fbStock fbb;

    private Runnable rnFbCallBack;
    private Runnable printclose;

	private int tipo,lns, cantExistencia, idalmdpred,idalm;
	private String itemid,prodid,savecant, fbsucursal;
	private boolean bloqueado=false,almacenes;
    private double cantT,disp,dispm,dispT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_exist);

            super.InitBase();

            tipo=((appGlobals) vApp).tipo;
            app = new AppMethods(this, gl, Con, db);
            gl.validimp=app.validaImpresora();
            if (!gl.validimp) msgbox("¡La impresora no está autorizada!");

            listView = findViewById(R.id.listView1);
            txtFilter = findViewById(R.id.txtFilter);
            lblReg = findViewById(R.id.textView1);lblReg.setText("");
            lblTit= findViewById(R.id.lblTit);lblTit.setText("Existencias bodega: "+gl.nom_alm.toUpperCase());
            lblTotal = findViewById(R.id.lblTit7);
            lblalm = findViewById(R.id.textView268);
            relalm= findViewById(R.id.relalm1);
            pbar=findViewById(R.id.progressBar5);pbar.setVisibility(View.VISIBLE);

            rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

            fbb=new fbStock("Stock",gl.tienda);
            rnFbCallBack = new Runnable() {
                public void run() {
                    buildItemList();
                }
            };
            fbsucursal ="/"+gl.tienda+"/";

            P_almacenObj=new clsP_almacenObj(this,Con,db);
            almacenes=tieneAlmacenes();
            if (!almacenes) {
                gl.idalm=0;gl.idalmpred=0;relalm.setVisibility(View.GONE);
                listItems();
            }

            printclose= new Runnable() {
                public void run() {
                    Exist.super.finish();
                }
            };

            prn=new printer(this,printclose,gl.validimp);
            doc=new clsDocExist(this,prn.prw,"");

            app.getURL();

            bloqueado=false;

            setHandlers();

        } catch (Exception e) {
            String ss=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doAlmacenes(View view){
        listaAlmacenes();
    }

	public void printDoc(View view) {
		try{
			if (items.size()==0){
				msgbox("No hay inventario disponible");
				return;
			}
			//if (doc.buildPrint("0",0)) app.doPrint();

            msgAskImprimir("Imprimir inventario");

		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	public void limpiaFiltro(View view) {
		try{
			txtFilter.setText("");
		}catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

    private void setHandlers(){

        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        if(!gl.dev){
                            Object lvObj = listView.getItemAtPosition(position);
                            clsClasses.clsExist item = (clsClasses.clsExist)lvObj;

                            itemid=item.Cod;

                            adapter.setSelectedIndex(position);
                        } else {

                            Object lvObj = listView.getItemAtPosition(position);
                            clsClasses.clsExist vItem = (clsClasses.clsExist)lvObj;

                            prodid=vItem.Cod;savecant="";

                            adapter.setSelectedIndex(position);
                        }
                    } catch (Exception e) {
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
                        //if (item.flag==1 | item.flag==2) itemDetail(item);
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

                    if (tl==0 || tl>1) buildItemList();
                }
            });
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

	//region Main

	private void listItems() {
        try {
            if (gl.idalm==gl.idalmpred) {
                fbb.listExist(fbsucursal,0,rnFbCallBack);
            } else {

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
 	}

    private void buildItemList() {
        clsClasses.clsExist item,itemt;
        clsClasses.clsT_stock fbitem;
        ArrayList<clsClasses.clsT_stock> sitems= new ArrayList<clsClasses.clsT_stock>();

        String vF,cod, name;
        double costo, total, gtotal=0;
        boolean flag;

        //fbb.orderByNombre();

        items.clear();gtotal=0;
        lblReg.setText("Registros : 0 ");lblTotal.setText("Valor total: "+mu.frmcur(gtotal));

        vF = txtFilter.getText().toString().replace("'","").toUpperCase();

        try {

            if (fbb.sitems.size() == 0) {
                adapter = new ListAdaptExist(this, items,gl.usarpeso);
                listView.setAdapter(adapter);
                pbar.setVisibility(View.INVISIBLE);
                return;
            }

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            clsT_stockObj T_stockObj=new clsT_stockObj(this,Con,db);
            db.execSQL("DELETE FROM T_stock");

            for (int i = 0; i <fbb.sitems.size(); i++) {
                T_stockObj.add(fbb.sitems.get(i));
            }

            try {
                sql="select IDPROD,SUM(CANT),UM FROM T_STOCK GROUP BY IDPROD";
                Cursor dt=Con.OpenDT(sql);

                sitems.clear();
                if (dt.getCount()>0) {
                    dt.moveToFirst();
                    while (!dt.isAfterLast()) {

                        fbitem=clsCls.new clsT_stock();

                        fbitem.id=0;
                        fbitem.idprod=dt.getInt(0);
                        fbitem.cant=dt.getDouble(1);
                        fbitem.um=dt.getString(2);

                        P_productoObj.fill("WHERE CODIGO_PRODUCTO="+fbitem.idprod+"");
                        fbitem.nombre=P_productoObj.first().desclarga;
                        fbitem.costo=P_productoObj.first().costo;

                        sitems.add(fbitem);
                        dt.moveToNext();
                    }
                }

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            Collections.sort(sitems, new ItemComparatorNombre());

            try {
                db.beginTransaction();

                for (int i = 0; i <sitems.size(); i++) {

                    fbitem=sitems.get(i);
                    flag=false;

                    cod=""+fbitem.id;
                    name=fbitem.nombre.toUpperCase();
                    costo=fbitem.costo;
                    total=fbitem.cant*costo;gtotal+=total;


                    if (!vF.isEmpty()) {
                        if (cod.indexOf(vF)>-1) {
                            flag=true;
                        } else {
                            if (name.indexOf(vF)>-1) flag=true;
                        }
                    } else flag=true;

                    if (fbitem.cant==0) flag=false;

                    if (flag) {

                        item = clsCls.new clsExist();
                        item.Cod = ""+fbitem.id;
                        item.Desc = fbitem.nombre;
                        item.flag = 0;
                        item.items=2;
                        items.add(item);

                        itemt = clsCls.new clsExist();
                        itemt.totaluni=mu.frmdecimal(fbitem.cant,0)+" "+fbitem.um;
                        itemt.ValorT = mu.frmdecimal(costo, gl.peDecImp);
                        itemt.PesoT = mu.frmdecimal(total, gl.peDecImp);
                        itemt.flag = 3;
                        items.add(itemt);
                    }

                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(e.getMessage());
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        adapter = new ListAdaptExist(this, items, gl.usarpeso);
        listView.setAdapter(adapter);

        lblTotal.setText("Valor total: "+mu.frmcur(gtotal));
        lblReg.setText("Registros : "+ ((int) items.size()/2));
        pbar.setVisibility(View.INVISIBLE);

    }

    private void buildItemListOld() {
        clsClasses.clsExist item,itemt;
        clsClasses.clsFbPStock fbitem;
        String vF,cod, name;
        double costo, total, gtotal=0;
        boolean flag;

        //fbb.orderByNombre();

        items.clear();gtotal=0;
        lblReg.setText("Registros : 0 ");lblTotal.setText("Valor total: "+mu.frmcur(gtotal));

        vF = txtFilter.getText().toString().replace("'","").toUpperCase();

        /*
        try {

            if (fbb.items.size() == 0) {
                adapter = new ListAdaptExist(this, items,gl.usarpeso);
                listView.setAdapter(adapter);
                return;
            }

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);

            for (int i = 0; i <fbb.items.size(); i++) {

                fbitem=fbb.items.get(i);
                flag=false;

                P_productoObj.fill("WHERE CODIGO_PRODUCTO="+fbitem.id+"");
                cod=P_productoObj.first().codigo.toUpperCase();name=fbitem.nombre.toUpperCase();
                costo=P_productoObj.first().costo;
                total=fbitem.cant*costo;gtotal+=total;


                if (!vF.isEmpty()) {
                    if (cod.indexOf(vF)>-1) {
                        flag=true;
                    } else {
                        if (name.indexOf(vF)>-1) flag=true;
                    }
                } else flag=true;

                if (fbitem.cant==0) flag=false;

                if (flag) {

                    item = clsCls.new clsExist();
                    item.Cod = ""+fbitem.id;
                    item.Desc = fbitem.nombre;
                    item.flag = 0;
                    item.items=2;
                    items.add(item);

                    itemt = clsCls.new clsExist();
                    itemt.totaluni=mu.frmdecimal(fbitem.cant,0)+" "+fbitem.um;
                    itemt.ValorT = mu.frmdecimal(costo, gl.peDecImp);
                    itemt.PesoT = mu.frmdecimal(total, gl.peDecImp);
                    itemt.flag = 3;
                    items.add(itemt);
                }

            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        }

        adapter = new ListAdaptExist(this, items, gl.usarpeso);
        listView.setAdapter(adapter);

        lblTotal.setText("Valor total: "+mu.frmcur(gtotal));
        lblReg.setText("Registros : "+ ((int) items.size()/2));
        pbar.setVisibility(View.INVISIBLE);


         */
    }

    private void listItemsOld() {
        Cursor dt, dp;
        clsClasses.clsExist item,itemm,itemt;
        String vF,pcod, cod, name, um, ump, sc, scm, sct="", sp, spm, spt="",rcant;
        double val, valm, valt, peso, pesot, costo, total,gtotal=0,ccant;
        int icnt;

        items.clear();lblReg.setText(" ( 0 ) ");

        vF = txtFilter.getText().toString().replace("'", "");

        try {
            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);

            if (gl.idalm==gl.idalmpred) {
                sql = "SELECT P_STOCK.CODIGO, P_PRODUCTO.DESCLARGA, P_PRODUCTO.CODIGO " +
                        "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK.CODIGO  WHERE 1=1 ";
                if (vF.length() > 0) sql = sql + "AND ((P_PRODUCTO.DESCLARGA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
                sql += "GROUP BY P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA ";
                sql += "ORDER BY P_PRODUCTO.DESCLARGA";

            } else {
                sql = "SELECT P_STOCK_ALMACEN.CODIGO_PRODUCTO, P_PRODUCTO.DESCLARGA, P_PRODUCTO.CODIGO " +
                        "FROM P_STOCK_ALMACEN INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK_ALMACEN.CODIGO_PRODUCTO  " +
                        "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") ";
                if (vF.length() > 0) sql = sql + "AND ((P_PRODUCTO.DESCLARGA LIKE '%" + vF + "%') OR (P_PRODUCTO.CODIGO LIKE '%" + vF + "%')) ";
                sql += "GROUP BY P_STOCK_ALMACEN.CODIGO_PRODUCTO,P_PRODUCTO.DESCLARGA ";
                sql += "ORDER BY P_PRODUCTO.DESCLARGA";
            }

            dp = Con.OpenDT(sql);
            if (dp.getCount() == 0) {
                adapter = new ListAdaptExist(this, items,gl.usarpeso);
                listView.setAdapter(adapter);
                return;
            }

            lblReg.setText(" ( " + dp.getCount() + " ) ");
            dp.moveToFirst();gtotal=0;

            while (!dp.isAfterLast()) {

                pcod=dp.getString(0);
                valt=0;pesot=0;

                P_productoObj.fill("WHERE CODIGO_PRODUCTO='"+pcod+"'");
                costo=P_productoObj.first().costo;
                if (costo==0) {

                }

                if (gl.idalm==gl.idalmpred) {
                    sql =  "SELECT P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,SUM(P_STOCK.CANT) AS TOTAL,SUM(P_STOCK.CANTM)," +
                            "P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS,SUM(P_STOCK.PESO),P_PRODUCTO.CODIGO   " +
                            "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK.CODIGO  " +
                            "WHERE (P_PRODUCTO.CODIGO_PRODUCTO='"+pcod+"') " +
                            "GROUP BY P_STOCK.CODIGO,P_PRODUCTO.DESCLARGA,P_STOCK.UNIDADMEDIDA,P_STOCK.LOTE,P_STOCK.DOCUMENTO,P_STOCK.CENTRO,P_STOCK.STATUS ";
                    sql+=  "ORDER BY TOTAL ";
                } else {
                    sql =  "SELECT P_STOCK_ALMACEN.CODIGO_PRODUCTO, P_PRODUCTO.DESCLARGA, SUM(P_STOCK_ALMACEN.CANT) AS TOTAL, " +
                            "SUM(P_STOCK_ALMACEN.CANTM), P_STOCK_ALMACEN.UNIDADMEDIDA, P_STOCK_ALMACEN.LOTE, " +
                            "P_STOCK_ALMACEN.LOTE, 0, 0, SUM(P_STOCK_ALMACEN.PESO),P_PRODUCTO.CODIGO   " +
                            "FROM P_STOCK_ALMACEN INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO=P_STOCK_ALMACEN.CODIGO_PRODUCTO  " +
                            "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") AND (P_PRODUCTO.CODIGO_PRODUCTO='"+pcod+"') " +
                            "GROUP BY P_STOCK_ALMACEN.CODIGO_PRODUCTO, P_PRODUCTO.DESCLARGA, " +
                            "P_STOCK_ALMACEN.UNIDADMEDIDA, P_STOCK_ALMACEN.LOTE ";
                    sql+=  "ORDER BY TOTAL ";
                }

                dt = Con.OpenDT(sql);
                icnt=dt.getCount();
                if (icnt==1) {
                    dt.moveToFirst();
                    if (dt.getDouble(2)>0 && dt.getDouble(3)>0) icnt=2;
                }

                item = clsCls.new clsExist();
                item.Cod = pcod;
                item.Desc = dp.getString(1)+"  [ "+dp.getString(2)+" ]";
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

                    rcant = mu.frmint2((int) val) + " " + rep.ltrim(um, 2);

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

                    item.rcant=rcant;

                    item.Lote = dt.getString(5);//if (mu.emptystr(item.Lote)) item.Lote =cod;
                    itemm.Lote = item.Lote;
                    item.Doc = dt.getString(6);itemm.Doc = dt.getString(6);
                    item.Centro = dt.getString(7);itemm.Centro = dt.getString(7);
                    item.Stat = dt.getString(8);itemm.Stat = dt.getString(8);

                    total=item.cant*costo;gtotal+=total;
                    item.precio=mu.frmdecimal(costo, gl.peDecImp);
                    item.total=mu.frmdecimal(total, gl.peDecImp);

                    if (val>0) {
                        item.flag = 1;
                        items.add(item);

                        if (valm > 0) {
                            icnt++;
                            itemm.flag = 2;
                            items.add(itemm);
                        }
                    } else {
                        item.flag = 1;//item.flag = 2;
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
        lblTotal.setText("Valor total: "+mu.frmcur(gtotal));
    }

    //endregion

    //region clsDocExist

    private class clsDocExist extends clsDocument {

        public clsDocExist(Context context, int printwidth, String archivo) {
            super(context, printwidth,gl.peMon,gl.peDecImp, archivo);

            nombre="REPORTE DE EXISTENCIAS";
            numero="";
            serie="";
            ruta=gl.ruta;
            vendedor=gl.vendnom;
            nombre_cliente ="";
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

    //endregion

    //region Aux

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

    public boolean tieneAlmacenes() {

        gl.idalmpred =0;gl.idalm=0;
        idalmdpred=0;

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1");

            if (P_almacenObj.count<2) return false;

            idalmdpred=P_almacenObj.first().codigo_almacen;gl.idalmpred =idalmdpred;

            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) {
                idalmdpred=P_almacenObj.first().codigo_almacen;
                gl.idalmpred =idalmdpred;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    public void listaAlmacenes() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Exist.this,"Seleccione un almacen");

            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");

            for (int i = 0; i <P_almacenObj.count; i++) {
                listdlg.add(P_almacenObj.items.get(i).codigo_almacen+"",P_almacenObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        try {
                            idalm=Integer.parseInt(listdlg.items.get(position).codigo);
                            gl.idalm=idalm;
                            gl.nom_alm=listdlg.items.get(position).text;
                            lblalm.setText(gl.nom_alm);
                            listItems();
                        } catch (Exception e) {
                            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void imprimirInventario() {
        clsClasses.clsExist item;
        String s1,s2;

        try {
            rep.clear();

            rep.empty();
            rep.addc("REPORTE DE EXISTENCIAS");
            setDatosVersion();

            for (int i = 0; i <items.size(); i++) {
                item=items.get(i);

                if (item.flag==1) {
                    s1 = item.Desc;
                    s2 = item.rcant;
                    rep.addtotrs(s1, s2);
                }
            }

            rep.line();
            rep.addc("FIN DE REPORTE");
            rep.empty();
            rep.empty();
            rep.empty();

            rep.save();

            app.doPrint(1,0);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setDatosVersion() {
        rep.empty();
        rep.line();
        rep.add("Empresa: " + gl.empnom);
        rep.add("Sucursal: " + gl.tiendanom);
        rep.add("Caja: " + gl.rutanom);
        rep.add("Impresión: "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
        rep.add("Vesión MPos: "+gl.parVer);
        rep.add("Generó: "+gl.vendnom);
        rep.line();
    }

    private  class ItemComparatorNombre implements Comparator<clsClasses.clsT_stock> {
        public int compare(clsClasses.clsT_stock left, clsClasses.clsT_stock right) {
            return left.nombre.compareTo(right.nombre);
        }
    }


    //endregion

    //region Dialogs

    private void msgAskImprimir(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imprimirInventario();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

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

    @Override
    public void onBackPressed() {
        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}
