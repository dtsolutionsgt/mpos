package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_stock_almacenObj;
import com.dtsgt.classes.clsP_unidadObj;
import com.dtsgt.classes.clsP_unidad_convObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_costoObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.LA_T_movr;
import com.dtsgt.ladapt.LA_T_venta_mod;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;
import java.util.Objects;

public class InvTrans extends PBase {

    private ListView listView,prodView;
    private GridView grdbtn;
    private EditText txtBarra;
    private TextView lblBar,lblKeyDP,lblProd,lblCant,lblCosto,lblTCant;
    private TextView lblTCosto,lblTit,lblDocumento,lblUni,lblDisp,txtprod;
    private RelativeLayout relprod;

    private clsKeybHandler khand;
    private LA_T_movd adapter;
    private LA_T_movr adapterr;
    private LA_T_venta_mod adapterp;
    private ListAdaptMenuVenta adapterb;
    private clsRepBuilder rep;

    private clsT_movdObj T_movdObj;
    private clsT_movrObj T_movrObj;
    private clsP_productoObj P_productoObj;
    private clsP_productoObj P_prodObj;

    private clsP_unidadObj P_unidadObj;
    private clsP_unidad_convObj P_unidad_convObj;
    private clsT_costoObj T_costoObj;
    private clsP_stockObj P_stockObj;
    private clsP_stock_almacenObj P_stock_almacenObj;

    private fbStock fbb;
    private Runnable rnFbCallBack,rnFbListItems;

    public ArrayList<clsClasses.clsFbStock> fbbitems= new ArrayList<clsClasses.clsFbStock>();
    public ArrayList<Integer> fbbcodes= new ArrayList<Integer>();

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsT_venta_mod> pitems= new ArrayList<clsClasses.clsT_venta_mod>();

    private clsClasses.clsT_movd selitem;
    private clsClasses.clsT_movr selitemr;

    private String barcode,prodname,um,invtext,ubas,convum,corel;
    private int prodid,selidx,fbprodid;
    private double cantt,costot,convcant,disp,dispact,costo,htot;
    private boolean almpr,almacen,valcant,scanning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);

            if (pantallaHorizontal()) {
                setContentView(R.layout.activity_inv_trans);
            } else {
                setContentView(R.layout.activity_inv_trans_ver);
            }

            super.InitBase();

            listView = (ListView) findViewById(R.id.listView1);
            prodView =  findViewById(R.id.listProd);
            grdbtn = (GridView) findViewById(R.id.grdbtn);
            txtBarra = findViewById(R.id.editText10);
            txtprod = findViewById(R.id.editTextText);

            lblTit = (TextView) findViewById(R.id.lblTit3);
            lblDocumento = (TextView) findViewById(R.id.lblDocumento);
            lblBar = (TextView) findViewById(R.id.lblCant);lblBar.setText("");
            lblKeyDP = (TextView) findViewById(R.id.textView110);
            lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
            lblCant = (TextView) findViewById(R.id.textView158);
            lblCosto = (TextView) findViewById(R.id.textView160);
            lblTCant = (TextView) findViewById(R.id.textView155);
            lblTCosto = (TextView) findViewById(R.id.textView150);
            lblUni = (TextView) findViewById(R.id.textView229);
            lblDisp = (TextView) findViewById(R.id.textView265);
            relprod = findViewById(R.id.relprod);relprod.setVisibility(View.INVISIBLE);

            prodid=0;
            almpr=gl.idalm==gl.idalmpred;
            if (almpr) almacen=false;else almacen=true;

            corel=gl.ruta+"_"+mu.getCorelBase();
            String na=gl.nom_alm.toUpperCase();if (!na.isEmpty()) na="almacén: "+na+ " -";
            invtext=na+" Traslado de: "+ gl.nom_alm + " a: " + gl.nom_alm2+"  - #"+corel;
            lblTit.setText(invtext);

            khand=new clsKeybHandler(this, lblBar,lblKeyDP);
            khand.clear(true);khand.enable();

            T_movdObj=new clsT_movdObj(this,Con,db);
            T_movrObj=new clsT_movrObj(this,Con,db);
            P_productoObj=new clsP_productoObj(this,Con,db);
            P_prodObj=new clsP_productoObj(this,Con,db);
            P_unidadObj=new clsP_unidadObj(this,Con,db);
            P_unidad_convObj=new clsP_unidad_convObj(this,Con,db);
            T_costoObj=new clsT_costoObj(this,Con,db);
            P_stockObj=new clsP_stockObj(this,Con,db);
            P_stock_almacenObj=new clsP_stock_almacenObj(this,Con,db);

            rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

            fbb=new fbStock("Stock",gl.tienda);
            rnFbListItems= () -> {fbListItems();};
            rnFbCallBack= () -> { fbCallBack();};

            setHandlers();

            iniciaProceso();

            if (!gl.corelmov.isEmpty()) {
                if (almacen) cargaDatosAlmacen();else cargaDatos();
            }

            listItems();

            lblDocumento.requestFocus();
            txtBarra.requestFocus();

            int idapr=gl.idalm;if (almpr) idapr=0;
            fbb.listItems("/"+gl.tienda+"/",idapr,rnFbListItems);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doAdd(View view) {
        valcant=false;
        addItem();
    }

    public void doSave(View view) {

        /*
        if (lblDocumento.getText().toString().isEmpty()){
            msgbox("Ingrese el número de documento");
            lblDocumento.requestFocus();
            return;
        }
        */

        if (T_movrObj.count==0) {
            msgbox("No se ha agregado ningúno producto al inventario, no se puede completar");return;
        }

        msgAskSave("Aplicar traslado entre bodegas");

    }

    public void doFocusBar(View view) {
        txtBarra.requestFocus();
        khand.setLabel(lblBar,true);
    }

    public void doFocusCant(View view) {
        khand.setLabel(lblCant,true);
    }

    public void doFocusCosto(View view) {
        if (gl.invregular) khand.setLabel(lblCosto,true);
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.label==lblBar) {
                barcode=khand.getStringValue();
                if (!barcode.isEmpty()) addBarcode();
            } else if (khand.label==lblCant) {
                if (gl.invregular) {
                    if (lblCosto.getText().toString().equals("0")){
                        khand.val="0";
                        lblCosto.setText("");
                    }
                    khand.setLabel(lblCosto,true);
                } else {
                    addItem();
                }
            } else if (khand.label==lblCosto) {
                if (gl.invregular) addItem();
            }
        }
    }

    public void doListUnits(View view) {
        listUnits();
    }

    public void doHideList(View view) {
        relprod.setVisibility(View.INVISIBLE);
    }

    public void doProd(View view) {
        buscarProducto();
    }

    public void doClearFilter(View view) {
        txtprod.setText("");
        iniciaProductos();
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = listView.getItemAtPosition(position);

                    selidx=position;

                    selitemr = (clsClasses.clsT_movr)lvObj;
                    adapterr.setSelectedIndex(position);
                    setProductr();

                };
            });

            prodView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = prodView.getItemAtPosition(position);
                    clsClasses.clsT_venta_mod item = (clsClasses.clsT_venta_mod)lvObj;

                    adapterp.setSelectedIndex(position);

                    codigoProducto(item.id);
                };
            });

            grdbtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = grdbtn.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adapterb.setSelectedIndex(position);
                    processMenuBtn(item.ID);

                };
            });

            lblCosto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {

                        if (lblCosto.getText().toString().equals("0")){
                            lblCosto.setText("");
                        }

                    }
                }
            });

            txtBarra.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

                public void onTextChanged(CharSequence s, int start,int before, int count) {
                    final CharSequence ss=s;

                    if (!scanning) {
                        scanning=true;
                        Handler handlerTimer = new Handler();
                        handlerTimer.postDelayed(new Runnable(){
                            public void run() {
                                compareSC(ss);
                            }}, 500);
                    }
                }

            });

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

    //region Main

    private void listItems() {
        double tc,can,ex1,ex2;
        int cl=0;

        selidx=-1;
        lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
        lblBar.setText("");lblCant.setText("");lblCosto.setText("");
        khand.setLabel(lblBar,true);

        costot=0;cantt=0;

        try {
            T_movrObj.fill();cl=T_movrObj.count;

            for (int i = 0; i <T_movrObj.count; i++) {
                can=T_movrObj.items.get(i).cant;cantt+=can;
                tc=can*T_movrObj.items.get(i).precio; costot+=tc;
                T_movrObj.items.get(i).pesom=tc;
                T_movrObj.items.get(i).srazon=" ";

                //ex1=dispProd(T_movrObj.items.get(i).producto);
                ex1=T_movrObj.items.get(i).cantm;
                ex2=ex1-can;
                T_movrObj.items.get(i).val1=""+mu.frmdecno(ex1);
                T_movrObj.items.get(i).val2=""+mu.frmdecno(ex2);
            }

            adapterr=new LA_T_movr(this,this,T_movrObj.items);
            listView.setAdapter(adapterr);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        lblTCant.setText("Cantidad : "+mu.frmdecno(cl));
        lblTCosto.setText("Total : "+mu.frmcur(costot));
    }

    private void addItem() {
        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        double cant,costo,dd;
        String ss;

        if (prodid==0) {
            toast("Falta definir articulo");
            khand.setLabel(lblBar,true);return;
        }

        try {
            ss=lblCant.getText().toString();
            dd=Double.parseDouble(ss);
            cant=dd;
            if (cant<=0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");khand.setLabel(lblCant,true);return;
        }

        try {
            costo=Double.parseDouble(lblCosto.getText().toString());
        } catch (Exception e) {
            costo=0;
        }

        if (costo<0) {
            toast("Costo incorrecto");khand.setLabel(lblCosto,true);return;
        }

        /*
        try {
            if (almacen) {
                clsP_stock_almacenObj P_stock_almacenObj=new clsP_stock_almacenObj(this,Con,db);
                P_stock_almacenObj.fill("WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") AND " +
                        "(CODIGO_PRODUCTO="+prodid+") AND (UNIDADMEDIDA='"+um+"') ");
                if (P_stock_almacenObj.count>0) disp=P_stock_almacenObj.first().cant;else disp=0;
            } else {
                clsP_stockObj P_stockObj=new clsP_stockObj(this,Con,db);
                P_stockObj.fill(" WHERE CODIGO="+prodid+" ");
                if (P_stockObj.count>0) disp=P_stockObj.first().cant;else disp=0;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return;
        }

        if (!valcant) {
            if (dispact<cant) {
                msgAskCant("Cantidad "+mu.frmdecno(cant)+" es mayor que " +
                           "disponible "+mu.frmdecno(disp)+".\nContinuar");return;
            }
        }
        */

        if (dispact<cant) {
            toast("Cantidad "+mu.frmdecno(cant)+" es mayor que disponible "+mu.frmdecno(disp));
        }

        try {

            if (selidx==-1) {

                itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                itemr.corel=" ";
                itemr.producto=prodid;
                itemr.cant=cant;
                itemr.cantm=dispact;
                itemr.peso=0;
                itemr.pesom=0;
                itemr.lote=prodname;
                itemr.codigoliquidacion=0;
                itemr.unidadmedida=lblUni.getText().toString();
                itemr.precio=costo;

                T_movrObj.add(itemr);

            } else {
                selitemr.cant=cant;
                selitemr.precio=costo;

                T_movrObj.update(selitemr);
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,true);
            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");lblDisp.setText("Disponible:");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        valcant=false;
    }

    private void savealmacen() {
        clsClasses.clsD_mov_almacen header;
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr imovr;
        clsClasses.clsT_costo cost;

        try {

            clsD_mov_almacenObj mov=new clsD_mov_almacenObj(this,Con,db);
            clsD_movd_almacenObj movd=new clsD_movd_almacenObj(this,Con,db);

            db.beginTransaction();

            if (!gl.corelmov.isEmpty()) {
                db.execSQL("DELETE FROM D_MOV_ALMACEN WHERE COREL='" + gl.corelmov + "'");
                db.execSQL("DELETE FROM D_MOVD_ALMACEN WHERE COREL='" + gl.corelmov + "'");
            }

            header =clsCls.new clsD_mov_almacen();

            header.corel=corel;
            header.codigo_sucursal=gl.tienda;
            header.almacen_origen=gl.idalm;
            header.almacen_destino=gl.idalm2;
            header.anulado=0;
            header.fecha=du.getActDateTime();
            header.tipo="T";
            header.usuario=gl.codigo_vendedor;
            header.referencia="";
            header.statcom="N";
            header.impres=0;
            header.codigoliquidacion=0;
            header.codigo_proveedor= gl.codigo_proveedor;
            header.total=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD_ALMACEN");
            int corc=T_costoObj.newID("SELECT MAX(CODIGO_COSTO) FROM T_costo");

            for (int i = 0; i <T_movrObj.count; i++) {

                imovr=T_movrObj.items.get(i);
                converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);
                convum=convum.trim();

                item =clsCls.new clsD_movd_almacen();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=imovr.producto;
                item.cant=convcant;         // imovr.cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=convum;   // imovr.unidadmedida;
                item.precio=imovr.precio;
                item.motivo_ajuste=0;

                movd.add(item);

                if (gl.idalm==gl.idalmpred) {
                    egresoStock(imovr.producto,convcant,convum);
                } else {
                    egresoStockAlmacen(imovr.producto,convcant,convum);
                }

                if (gl.idalm2==gl.idalmpred) {
                    ingresoStock(imovr.producto,convcant,convum);
                } else {
                    ingresoStockAlmacen(imovr.producto,convcant,convum);
                }

                corc++;

                try {
                    cost = clsCls.new clsT_costo();

                    cost.corel=corel;
                    cost.codigo_costo=corc;
                    cost.codigo_producto=imovr.producto;
                    cost.fecha=du.getActDateTime();
                    cost.costo=imovr.precio;
                    cost.codigo_proveedor=gl.codigo_proveedor;
                    cost.statcom=0;

                    T_costoObj.add(cost);

                } catch (Exception e) {
                    String sp=e.getMessage();
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }

                sql="UPDATE P_PRODUCTO SET COSTO="+imovr.precio+" WHERE CODIGO_PRODUCTO="+imovr.producto;
                db.execSQL(sql);

            }

            db.execSQL("DELETE FROM P_STOCK WHERE CANT=0");
            db.execSQL("DELETE FROM P_STOCK_ALMACEN WHERE CANT=0");

            db.execSQL("DELETE FROM T_MOVR");

            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Existencias actualizadas");

            /*
            if (gl.peInvCompart) {
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            }
            */

            finish();

        } catch (Exception e) {
            db.endTransaction();
            String sp=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addBarcode() {
        if (barraProducto()) {
            return;
        } else {
            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");
            khand.setLabel(lblBar,true);

            toastlong("¡El producto "+barcode+" no existe!");
        }
    }

    private boolean barraProducto() {
        try {
            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            if (gl.idalm!=gl.idalmpred) {
                sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK_ALMACEN.UNIDADMEDIDA, " +
                        "P_PRODUCTO.CODIGO_PRODUCTO, P_PRODUCTO.COSTO " +
                        "FROM P_STOCK_ALMACEN INNER JOIN " +
                        "P_PRODUCTO ON P_STOCK_ALMACEN.CODIGO_PRODUCTO=P_PRODUCTO.CODIGO_PRODUCTO " +
                        "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") ";
            } else {
                sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK.UNIDADMEDIDA, " +
                        "P_PRODUCTO.CODIGO_PRODUCTO, P_PRODUCTO.COSTO " +
                        "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO " +
                        "WHERE (1=1) ";
            }

            sql+="AND (P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"') COLLATE NOCASE";

            Cursor dt=Con.OpenDT(sql);
            if (dt.getCount()==0) {

                toast("¡El producto "+barcode+" no existe!");return false;
            }

            dt.moveToFirst();
            prodid=dt.getInt(3);
            prodname=dt.getString(0)+" - "+dt.getString(1);
            um=dt.getString(2);
            costo=dt.getDouble(4);

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            khand.setLabel(lblCant,true);khand.val="";
            lblCant.setText("");txtBarra.setText("");

            if (costo>0) {
                lblCosto.setText(""+costo);
            } else {
                lblCosto.setText("0");
            }

            try {
                T_costoObj.fill("WHERE CODIGO_PRODUCTO="+P_productoObj.first().codigo_producto+" ORDER BY FECHA DESC LIMIT 1");
                if (T_costoObj.count>0) lblCosto.setText(""+T_costoObj.first().costo);
            } catch (Exception e) {
            }

            lblDisp.setText("Disponible: "+dispProdUni(prodid));

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void setProduct() {
        try {

            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

            prodid=selitem.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            lblCant.setText(mu.frmdecno(selitem.cant));
            khand.setLabel(lblCant,true);
            lblCosto.setText(""+selitem.precio);

            lblUni.setText(selitemr.unidadmedida);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setProductr() {
        try {

            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

            prodid=selitemr.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            lblCant.setText(mu.frmdecno(selitemr.cant));
            khand.setLabel(lblCant,true);
            lblCosto.setText(""+selitemr.precio);

            lblUni.setText(selitemr.unidadmedida);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void ingresoStock(int pcod, double pcant, String um) {
        try {
            updateStock(0,pcod,pcant,um);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void ingresoStockAlmacen(int pcod, double pcant, String um) {
        try {
            updateStock(gl.idalm2,pcod,pcant,um);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void egresoStock(int pcod, double pcant, String um) {
        try {
            pcant=Math.abs(pcant);
            updateStock(0,pcod,-pcant,um);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void egresoStockAlmacen(int pcod, double pcant, String um) {
        try {
            pcant=Math.abs(pcant);
            updateStock(gl.idalm2,pcod,-pcant,um);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void ingresoStockOld(int pcod, double pcant, String um) {
        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",pcod);
            ins.add("CANT",pcant);
            ins.add("CANTM",0);
            ins.add("PESO",0);
            ins.add("plibra",0);
            ins.add("LOTE","");
            ins.add("DOCUMENTO","");

            ins.add("FECHA",0);
            ins.add("ANULADO",0);
            ins.add("CENTRO","");
            ins.add("STATUS","");
            ins.add("ENVIADO",1);
            ins.add("CODIGOLIQUIDACION",0);
            ins.add("COREL_D_MOV","");
            ins.add("UNIDADMEDIDA",um);

            String sp=ins.sql();

            db.execSQL(ins.sql());
        } catch (Exception e) {
            sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE (CODIGO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
            db.execSQL(sql);
        }
    }

    private void ingresoStockAlmacenOld(int pcod, double pcant, String um) {
        Cursor dt;
        int newid;

        try {
            sql="SELECT CODIGO_STOCK FROM P_stock_almacen  " +
                    "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm2+") AND (CODIGO_PRODUCTO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
            dt=Con.OpenDT(sql);

            if (dt.getCount()==0) {

                sql="SELECT MAX(CODIGO_STOCK) FROM P_stock_almacen";
                dt=Con.OpenDT(sql);
                if (dt.getCount()>0) {
                    dt.moveToFirst();
                    newid=dt.getInt(0)+1;
                } else newid=1;

                ins.init("P_stock_almacen");

                ins.add("CODIGO_STOCK",newid);
                ins.add("EMPRESA",gl.emp);
                ins.add("CODIGO_SUCURSAL",gl.tienda);
                ins.add("CODIGO_ALMACEN",gl.idalm2);
                ins.add("CODIGO_PRODUCTO",pcod);
                ins.add("UNIDADMEDIDA",um);
                ins.add("LOTE","");
                ins.add("CANT",pcant);
                ins.add("CANTM",0);
                ins.add("PESO",0);
                ins.add("PESOM",0);
                ins.add("ANULADO",0);

                String sp=ins.sql();
                db.execSQL(ins.sql());

            } else {
                sql="UPDATE P_stock_almacen SET CANT=CANT+"+pcant+" " +
                        "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm2+") AND (CODIGO_PRODUCTO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
                db.execSQL(sql);
            }

            if (dt!=null) dt.close();
        } catch (Exception e) {
            String sp=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void egresoStockOld(int pcod,double pcant,String um) {
        sql="UPDATE P_STOCK SET CANT=CANT-"+Math.abs(pcant)+" WHERE CODIGO="+pcod+" ";
        db.execSQL(sql);
    }

    private void egresoStockAlmacenOld(int pcod,double pcant,String um) {
        sql="UPDATE P_stock_almacen SET CANT=CANT-"+Math.abs(pcant)+" " +
                "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") AND (CODIGO_PRODUCTO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
        db.execSQL(sql);
    }

    private void updateStock(int idalmacen,int pcod,double pcant,String um) {
        clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

        ritem.idprod=pcod;
        ritem.idalm=idalmacen;
        ritem.cant=pcant;
        ritem.um=um.trim();
        ritem.bandera=0;

        fbb.addItem("/"+gl.tienda+"/",ritem);
    }

    private void converUMBas(int prodid,Double ccant,String umed) throws Exception {
        String prumb;
        double factor;

        P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);
        prumb=P_productoObj.first().unidbas;
        factor=1;

        if (!umed.equalsIgnoreCase(prumb)) {
            P_unidad_convObj.fill("WHERE (CODIGO_UNIDAD1='"+prumb+"') AND (CODIGO_UNIDAD2='"+umed+"')");
            factor=P_unidad_convObj.first().factor;
        }

        if (factor<=0) throw new Exception("Factor conversion no existe "+prumb+" / "+umed);

        convum=prumb;
        convcant=ccant/factor;
    }

    private void cargaDatos() {
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        int prodid;
        String pname;

        try {

            clsD_MovObj D_movObj=new clsD_MovObj(this,Con,db);
            D_movObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            lblDocumento.setText(D_movObj.first().REFERENCIA);
            gl.codigo_proveedor=D_movObj.first().CODIGO_PROVEEDOR;

            db.execSQL("DELETE FROM T_MOVR");

            clsD_MovDObj D_movdObj=new clsD_MovDObj(this,Con,db);
            D_movdObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            clsT_movrObj T_movrObj=new clsT_movrObj(this,Con,db);

            for (int i = 0; i <D_movdObj.count; i++) {
                item=D_movdObj.items.get(i);
                prodid=item.producto;

                P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
                if (P_productoObj.count>0) pname=P_productoObj.first().desclarga;else pname="";

                itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                itemr.corel=" ";
                itemr.producto=prodid;
                itemr.cant=item.cant;
                itemr.cantm=0;
                itemr.peso=0;
                itemr.pesom=0;
                itemr.lote=pname;
                itemr.codigoliquidacion=0;
                itemr.unidadmedida=item.unidadmedida;
                itemr.precio=item.precio;

                T_movrObj.add(itemr);
            }

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaDatosAlmacen() {
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        int prodid;
        String pname;

        try {

            clsD_mov_almacenObj D_movObj=new clsD_mov_almacenObj(this,Con,db);
            D_movObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            lblDocumento.setText(D_movObj.first().referencia);
            gl.codigo_proveedor=D_movObj.first().codigo_proveedor;

            db.execSQL("DELETE FROM T_MOVR");

            clsD_movd_almacenObj D_movdObj=new clsD_movd_almacenObj(this,Con,db);
            D_movdObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            clsT_movrObj T_movrObj=new clsT_movrObj(this,Con,db);

            for (int i = 0; i <D_movdObj.count; i++) {
                item=D_movdObj.items.get(i);
                prodid=item.producto;

                P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
                if (P_productoObj.count>0) pname=P_productoObj.first().desclarga;else pname="";

                itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                itemr.corel=" ";
                itemr.producto=prodid;
                itemr.cant=item.cant;
                itemr.cantm=0;
                itemr.peso=0;
                itemr.pesom=0;
                itemr.lote=pname;
                itemr.codigoliquidacion=0;
                itemr.unidadmedida=item.unidadmedida;
                itemr.precio=item.precio;

                T_movrObj.add(itemr);
            }

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Menu

    private void iniciaProceso() {
        clsClasses.clsMenu item;

        try {

            db.execSQL("DELETE FROM T_movr");

            mmitems.clear();

            item = clsCls.new clsMenu();
            item.ID=50;item.Name="Buscar ";item.Icon=50;
            mmitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=56;item.Name="Imprimir ";item.Icon=62;
            mmitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=54;item.Name="Borrar linea ";item.Icon=54;
            mmitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=55;item.Name="Borrar todo ";item.Icon=55;
            mmitems.add(item);

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuBtn(int menuid) {
        try {
            switch (menuid) {
                case 50:
                    relprod.setVisibility(View.VISIBLE);
                    break;
                    /*
                    gl.gstr = "";browse = 1;
                    gl.prodtipo=4;
                    startActivity(new Intent(this, Producto.class));break;
                    */
                case 54:
                    borraLinea();break;
                case 55:
                    borraTodo();break;
                case 56:
                    imprimir();break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void borraLinea() {

        if (selidx==-1) return;

        try {
            T_movrObj.delete(selitemr);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar todos los articulos");
    }

    //endregion

    //region Lista productos

    private void fbListItems() {
        int pcod;

        try {
            if (!db.isOpen()) onResume();

            if (fbb.errflag) throw new Exception(fbb.error);

            P_prodObj.fill();
            fbbitems.clear();fbbcodes.clear();

            for (int i = 0; i <fbb.items.size(); i++) {
                pcod=fbb.items.get(i).idprod;
                if (!fbbcodes.contains(pcod)) {
                    fbb.items.get(i).nombre=prodnom(fbb.items.get(i).idprod);
                    fbbitems.add(fbb.items.get(i));
                    fbbcodes.add(pcod);
                }
            }

            iniciaProductos();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String prodnom(int idpr) {
        try {
            for (int ii = 0; ii <P_prodObj.items.size(); ii++) {
                if (P_prodObj.items.get(ii).codigo_producto==idpr) return P_prodObj.items.get(ii).desccorta;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    private void iniciaProductos() {
        clsClasses.clsT_venta_mod pitem;

        try {

            pitems.clear();

            for (int i = 0; i <fbbitems.size(); i++) {
                pitem = clsCls.new clsT_venta_mod();

                pitem.id=fbbitems.get(i).idprod;
                pitem.idmod=0;
                pitem.nombre=fbbitems.get(i).nombre;

                pitems.add(pitem);
            }

            adapterp=new LA_T_venta_mod(this,this,pitems);
            prodView.setAdapter(adapterp);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean codigoProducto(int prodcod) {

        try {

            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodcod+")");
            if (P_productoObj.count==0) return false;

            prodid=P_productoObj.first().codigo_producto;
            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            khand.setLabel(lblCant,true);khand.val="";
            lblCant.setText("");txtBarra.setText("");

            if (P_productoObj.first().costo>0) {
                lblCosto.setText(""+P_productoObj.first().costo);
            } else {
                lblCosto.setText("0");
            }

            try {
                T_costoObj.fill("WHERE CODIGO_PRODUCTO="+P_productoObj.first().codigo_producto+" ORDER BY FECHA DESC LIMIT 1");
                if (T_costoObj.count>0) lblCosto.setText(""+T_costoObj.first().costo);
            } catch (Exception e) { }

            lblDisp.setText("Disponible: ");
            dispProdUni(P_productoObj.first().codigo_producto);

            return true;

        } catch (Exception e) {
            String ss=e.getMessage();
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void buscarProducto() {
        clsClasses.clsT_venta_mod pitem;
        String flt,ss;
        int i1;

        try {

            flt=txtprod.getText().toString().toUpperCase();
            if (flt.isEmpty()) {
                iniciaProductos();return;
            }

            pitems.clear();

            for (int i = 0; i <fbbitems.size(); i++) {
                ss=fbbitems.get(i).nombre.toUpperCase();
                i1=ss.indexOf(flt);

                if (i1>=0) {
                    pitem = clsCls.new clsT_venta_mod();

                    pitem.id = fbbitems.get(i).idprod;
                    pitem.idmod = 0;
                    pitem.nombre = fbbitems.get(i).nombre;

                    pitems.add(pitem);
                }
            }

            /*
            sql="SELECT CODIGO_PRODUCTO,   DESCLARGA,'','','',   '','','','' " +
                    "FROM P_PRODUCTO WHERE (CODIGO_TIPO='P') " ;
            if (!flt.isEmpty()) sql+=" AND (DESCLARGA LIKE '%"+flt+"%') ";
            sql+="ORDER BY DESCLARGA";
            ViewObj.fillSelect(sql);

            pitems.clear();

            for (int i = 0; i <ViewObj.count; i++) {
                pitem = clsCls.new clsT_venta_mod();

                pitem.id=ViewObj.items.get(i).pk;
                pitem.idmod=0;
                pitem.nombre=ViewObj.items.get(i).f1;

                pitems.add(pitem);
            }
            */

            adapterp=new LA_T_venta_mod(this,this,pitems);
            prodView.setAdapter(adapterp);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Impresion

    private void imprimir(){
        int aid=0;

        try {

            rep.clear();

            impresionEncabezado(aid);
            impresionDetalle(aid);

            rep.line();
            rep.empty();
            rep.addtote("Valor total: ",mu.frmcur(costot));
            rep.empty();
            rep.empty();
            rep.empty();

            rep.save();

            app.printView();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void impresionEncabezado(int aid) {

        rep.empty();
        rep.empty();
        rep.addc(gl.empnom);
        rep.addc(gl.tiendanom);
        rep.empty();
        rep.addc("PREIMPRESION");
        rep.addc("TRASLADO ENTRE ALMACENES");
        rep.empty();
        rep.add("Numero: "+corel+" ");
        rep.add("Fecha: "+du.sfecha(du.getActDate()));
        rep.add("Operador: "+gl.vendnom);
        rep.add("Almacen origen: "+gl.nom_alm);
        rep.add("Almacen destino: "+gl.nom_alm2);
        rep.empty();
        rep.add3lrr("Cantidad","Costo","Valor");
        rep.line();
    }

    private void impresionDetalle(int aid) {
        String dum;
        double dcant,dprec,dtot;

        T_movrObj.fill();

        for (int i = 0; i <T_movrObj.count; i++) {

            dum=T_movrObj.items.get(i).unidadmedida;
            dcant=T_movrObj.items.get(i).cant;
            dprec=T_movrObj.items.get(i).precio;
            dtot=Math.abs(dcant*dprec);

            rep.add(app.prodNombre(T_movrObj.items.get(i).producto));
            rep.add3lrre(mu.frmdecno(dcant)+" "+dum,dprec,dtot);
        }

    }

    //endregion

    //region Firebase

    private void getFbProdStock(int prodid) {
        try {
            int idalmacen=gl.idalm;if (almpr) idalmacen=0;

            fbprodid=prodid;
            fbb.calculaTotal("/"+gl.tienda+"/",idalmacen,fbprodid,rnFbCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void fbCallBack() {
        try {
            dispact=fbb.total;
            lblDisp.setText("Disponible: "+mu.frmdecno(fbb.total)+" "+fbb.unimed);

            db.execSQL("DELETE FROM T_stock WHERE IDPROD="+fbprodid);
            db.execSQL("INSERT INTO T_stock VALUES ("+fbprodid+","+fbprodid+","+fbb.total+",'"+fbb.unimed+"')");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    //region Aux

    private void creaEncabezadoInicial() {
        clsClasses.clsD_Mov header;
        String corel=gl.ruta+"_"+mu.getCorelBase();

        try {
            clsD_MovObj mov=new clsD_MovObj(this,Con,db);

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA="ACTIVO";
            header.STATCOM="X";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;

            mov.add(header);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraEncabezadoInicial() {
        try {
            db.execSQL("DELETE FROM D_MOV WHERE STATCOM='X'");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void nombreUnidad() {
        ubas=um;
        /*
        try {
            P_unidadObj.fill("WHERE (CODIGO_UNIDAD='"+um+"')");
            ubas=P_unidadObj.first().nombre;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            ubas=um;
        }
        */
        lblUni.setText(um);
    }

    private void listUnits() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvTrans.this,"Unidad de medida");

            P_unidad_convObj.fill("WHERE (CODIGO_UNIDAD1='"+um+"') AND (CODIGO_UNIDAD2<>'"+um+"')");
            listdlg.add(um);
            for (int i = 0; i <P_unidad_convObj.count; i++) {
                listdlg.add(P_unidad_convObj.items.get(i).codigo_unidad2);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        lblUni.setText(listdlg.getText(position));
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

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            if (app.horizscr()) return true; else return point.x>point.y;
       } catch (Exception e) {
            return true;
        }
    }

    public String dispProdUni(int prodid) {
        try {
            getFbProdStock(prodid);
        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() { }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
        return "";
    }

    public String dispProdUniOld(int prodid) {
        double val=0;
        String uum="";

        try {
            if (almacen) {
                P_stock_almacenObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+") AND (CODIGO_ALMACEN="+gl.idalm+")");
                if (P_stock_almacenObj.count>0) {
                    val=P_stock_almacenObj.first().cant;
                    uum=P_stock_almacenObj.first().unidadmedida;
                }
            } else {
                P_stockObj.fill("WHERE CODIGO="+prodid);
                if (P_stockObj.count>0) {
                    val=P_stockObj.first().cant;
                    uum=P_stockObj.first().unidadmedida;
                }
            }
            return mu.frmdecno(val)+" "+uum;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    public double dispProd(int prodid) {
        double val=0;
        try {
            if (almacen) {
                P_stock_almacenObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+") AND (CODIGO_ALMACEN="+gl.idalm+")");
                if (P_stock_almacenObj.count>0) val=P_stock_almacenObj.first().cant;
            } else {
                P_stockObj.fill("WHERE CODIGO="+prodid);
                if (P_stockObj.count>0) val=P_stockObj.first().cant;
            }
            return val;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
    }

    private void compareSC(CharSequence s) {
        String os,bc;

        bc=txtBarra.getText().toString();
        if (bc.isEmpty() || bc.length()<2) {
            txtBarra.setText("");
            scanning=false;
            return;
        }
        os=s.toString();

        if (bc.equalsIgnoreCase(os)) {
            barcode=bc;gl.barra=barcode;
            if (!barraProducto()) toastlong("¡La barra "+barcode+" no existe!");
        }

        txtBarra.setText("");txtBarra.requestFocus();
        scanning=false;
    }

    private String nombreAlmacen(int idalm) {
        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);

            for (int i = 0; i <P_almacenObj.count; i++) {
                if (P_almacenObj.items.get(i).codigo_almacen==idalm) return P_almacenObj.items.get(i).nombre;
            }
        } catch (Exception e) { }
        return " ";
    }

    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sql="DELETE FROM T_MOVR";
                    db.execSQL(sql);
                    listItems();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskSave(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    savealmacen();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskExit(String msg) {

        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskCant(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                valcant=true;
                addItem();
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
    protected void onResume() {
        super.onResume();

        try {
            T_movdObj.reconnect(Con,db);
            T_movrObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            P_prodObj.reconnect(Con,db);
            P_unidadObj.reconnect(Con,db);
            P_unidad_convObj.reconnect(Con,db);
            T_costoObj.reconnect(Con,db);
            P_stockObj.reconnect(Con,db);
            P_stock_almacenObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (gl.gstr.isEmpty()) return;
            barcode=gl.gstr;
            addBarcode();
        }
    }

    @Override
    public void onBackPressed()    {
        msgAskExit("Salir sin aplicar traslado");
    }

    //endregion

}