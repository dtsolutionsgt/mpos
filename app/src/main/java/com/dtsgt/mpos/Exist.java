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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.ListAdaptExist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    private ImageView imgref;

    private AppMethods app;

	private ArrayList<clsClasses.clsExist> items= new ArrayList<clsClasses.clsExist>();
	private ListAdaptExist adapter;
	private clsClasses.clsExist selitem;
    private clsP_almacenObj P_almacenObj;

    private clsDocExist doc;
    private printer prn;
    private clsRepBuilder rep;

    private fbStock fbs;
    private DatabaseReference fbconnRef;
    private ValueEventListener fbconnListener,fbrefListener;

    private Runnable rnFbCallBack;
    private Runnable printclose;

	private int tipo,lns, cantExistencia, idalmdpred,idalm;
	private String itemid,prodid,savecant, fbsucursal,pdef_nom;
	private boolean bloqueado=false,almacenes,idle=false,disconnected=false;
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
            pbar=findViewById(R.id.progressBar5);pbar.setVisibility(View.INVISIBLE);
            imgref= findViewById(R.id.imageView146);imgref.setVisibility(View.INVISIBLE);

            rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

            try {
                fbs.fdb.goOnline();
                fbs.fdt.push();
            } catch (Exception e) {}

            fbs =new fbStock("Stock",gl.tienda);
            fbconnRef = fbs.fdb.getReference(".info/connected");
            CreateFbCheckStatus();

            rnFbCallBack = new Runnable() {
                public void run() {
                    buildItemList();
                }
            };
            fbsucursal ="/"+gl.tienda+"/";

            P_almacenObj=new clsP_almacenObj(this,Con,db);
            almacenes=tieneAlmacenes();
            almacenes=true;

            if (almacenes) {
                if (idalmdpred>0) {
                    try {
                        idalm=idalmdpred;gl.idalm=idalmdpred;
                        gl.nom_alm = pdef_nom;lblalm.setText(gl.nom_alm);
                        listItems();
                    } catch (Exception e) {
                        msgbox(new Object() { }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
                    }
                }
            } else {
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

    public void doRefresh(View view) {
        if (idle) listItems();
    }

    public void doClean(View view) {
        txtFilter.setText("");
        listItems();
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
            pbar.setVisibility(View.VISIBLE);
            imgref.setVisibility(View.INVISIBLE);
            idle=false;

            if (gl.idalm==gl.idalmpred) {
                fbs.listExist(fbsucursal,gl.idalm,rnFbCallBack);
            } else {
                fbs.listExist(fbsucursal,gl.idalm,rnFbCallBack);
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

        //fbs.orderByNombre();

        items.clear();gtotal=0;
        lblReg.setText("Registros : 0 ");lblTotal.setText("Valor total: "+mu.frmcur(gtotal));

        vF = txtFilter.getText().toString().replace("'","").toUpperCase();

        try {

            if (fbs.sitems.size() == 0) {
                adapter = new ListAdaptExist(this, items,gl.usarpeso);
                listView.setAdapter(adapter);
                pbar.setVisibility(View.INVISIBLE);
                return;
            }

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            clsT_stockObj T_stockObj=new clsT_stockObj(this,Con,db);
            db.execSQL("DELETE FROM T_stock");

            for (int i = 0; i < fbs.sitems.size(); i++) {
                T_stockObj.add(fbs.sitems.get(i));
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

        //imgref.setVisibility(View.VISIBLE);
        idle=true;

        try {
            //if (disconnected) fbconnRef.addValueEventListener(fbrefListener);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

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

        gl.idalmpred =0;gl.idalm=0;idalmdpred=0;pdef_nom="";

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1");

            if (P_almacenObj.count<2) return false;

            idalmdpred=P_almacenObj.first().codigo_almacen;gl.idalmpred =idalmdpred;

            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) {
                idalmdpred=P_almacenObj.first().codigo_almacen;
                gl.idalmpred =idalmdpred;
                pdef_nom=P_almacenObj.first().nombre;
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

    private class ItemComparatorNombre implements Comparator<clsClasses.clsT_stock> {
        public int compare(clsClasses.clsT_stock left, clsClasses.clsT_stock right) {
            return left.nombre.compareTo(right.nombre);
        }
    }

    private void CreateFbCheckStatus() {
        try {
            fbconnListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    disconnected=!connected;
                    if (!connected) msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                 }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    disconnected=true;
                    msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                }

            };

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        try {
            fbrefListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    disconnected=!connected;

                    if (!connected) msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                    try {
                        fbconnRef.removeEventListener(this);
                    } catch (Exception e) {}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    disconnected=true;
                    msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");

                    try {
                        fbconnRef.removeEventListener(this);
                    } catch (Exception e) {}
                }

            };

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    public void msgboxexit(String msg) {
        try {
            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        } catch (Exception e){ }
    }


    //endregion

	//region Activity Events

	@Override
	protected  void onResume(){
		try{
			super.onResume();
            fbconnRef.addValueEventListener(fbconnListener);

        }catch (Exception e){
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}
	}

	@Override
	protected void onPause() {
		try {
            try {
                fbconnRef.removeEventListener(fbconnListener);
            } catch (Exception e) { }
            super.onPause();
		} catch (Exception e) {
			addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
		}

	}

    @Override
    public void onBackPressed() {
        try {
            fbconnRef.removeEventListener(fbconnListener);
        } catch (Exception e) { }

        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}
