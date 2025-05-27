package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_stock_almacenObj;
import com.dtsgt.classes.clsP_unidadObj;
import com.dtsgt.classes.clsP_unidad_convObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_costoObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.classes.clsT_stockObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.LA_T_movr;
import com.dtsgt.ladapt.LA_T_venta_mod;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;
import java.util.Objects;

public class InvRecep extends PBase {

    private ListView listView,prodView;
    private GridView grdbtn;
    private EditText txtBarra,txtprod;
    private TextView lblBar,lblKeyDP,lblProd,lblCant,lblCosto,lblTCant;
    private TextView lblTCosto,lblTit,lblUni,lblDisp,lblDoc;
    private RelativeLayout relprod;

    private clsKeybHandler khand;
    private LA_T_movd adapter;
    private LA_T_movr adapterr;
    private LA_T_venta_mod adapterp;
    private ListAdaptMenuVenta adapterb;

    private clsRepBuilder rep;
    private fbStock fbs;
    private Runnable rnFbCallBack, rnFbListItems;

    private clsT_movdObj T_movdObj;
    private clsT_movrObj T_movrObj;
    private clsP_productoObj P_productoObj;
    private clsP_unidadObj P_unidadObj;
    private clsP_unidad_convObj P_unidad_convObj;
    private clsT_costoObj T_costoObj;
    private clsP_stockObj P_stockObj;
    private clsP_stock_almacenObj P_stock_almacenObj;
    private clsViewObj ViewObj;
    private clsT_stockObj T_stockObj;

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<>();
    private ArrayList<clsClasses.clsT_venta_mod> pitems= new ArrayList<clsClasses.clsT_venta_mod>();

    private clsClasses.clsT_movd selitem;
    private clsClasses.clsT_movr selitemr;

    private String barcode,prodname,um,invtext,ubas,convum,corel,docnum;
    private int prodid,fbprodid,selidx,cargalim,cargacnt;
    private double cantt,costot,convcant,htot;
    private boolean ingreso,almpr,almacen,readonly,scanning=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_inv_recep);
        } else {
            setContentView(R.layout.activity_inv_recep);
            //setContentView(R.layout.activity_inv_recep_ver);
        }

        super.InitBase();

        listView =  findViewById(R.id.listView1);
        prodView =  findViewById(R.id.listProd);
        grdbtn = findViewById(R.id.grdbtn);
        lblTit =  findViewById(R.id.lblTit3);
        txtBarra = findViewById(R.id.editText10);
        txtprod = findViewById(R.id.editTextText);
        lblBar =  findViewById(R.id.lblCant);lblBar.setText("");
        lblKeyDP =  findViewById(R.id.textView110);
        lblProd =  findViewById(R.id.textView156);lblProd.setText("");
        lblCant = findViewById(R.id.textView158);
        lblCosto =  findViewById(R.id.textView160);
        lblTCant =  findViewById(R.id.textView155);
        lblTCosto =  findViewById(R.id.textView150);
        lblUni =  findViewById(R.id.textView229);
        lblDisp =  findViewById(R.id.textView265);
        lblDoc =  findViewById(R.id.textView267);lblDoc.setText("");
        relprod = findViewById(R.id.relprod);relprod.setVisibility(View.INVISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        prodid=0;
        ingreso=gl.invregular;
        almacen=true;
        if (gl.tipo==0) almacen=false;
        almpr=gl.idalm==gl.idalmpred;
        if (almpr) almacen=false;

        almacen=true;

        khand=new clsKeybHandler(this, lblBar,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        T_movrObj=new clsT_movrObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);
        P_unidadObj=new clsP_unidadObj(this,Con,db);
        P_unidad_convObj=new clsP_unidad_convObj(this,Con,db);
        T_costoObj=new clsT_costoObj(this,Con,db);
        P_stockObj=new clsP_stockObj(this,Con,db);
        P_stock_almacenObj=new clsP_stock_almacenObj(this,Con,db);
        ViewObj=new clsViewObj(this,Con,db);
        T_stockObj=new clsT_stockObj(this,Con,db);

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

        rnFbCallBack = new Runnable() {
            public void run() {
                fbCallBack();
            }
        };
        fbs =new fbStock("Stock",gl.tienda);

        rnFbListItems = new Runnable() {
            public void run() {
                fbListItems();
            }
        };

        setHandlers();

        iniciaProceso();

        if (!gl.corelmov.isEmpty()) {
            corel=gl.corelmov;
            if (almacen) cargaDatosAlmacen();else cargaDatos();
        } else {
            readonly=false;
            docnum="";
            corel=gl.ruta+"_"+mu.getCorelBase();
            inputDoc();
        }
        if (readonly) txtBarra.setEnabled(false);

        String na=gl.nom_alm.toUpperCase();if (!na.isEmpty()) na="Almacén: "+na+ " -";
        invtext=na+" Ingreso de mercancía - "+ gl.nombre_proveedor.toUpperCase()+"  #"+corel;
        lblTit.setText(invtext);

        listItems();

        txtBarra.requestFocus();
    }

    //region Events

    public void doAdd(View view) {
        if (!readonly) addItem();
    }

    public void doSave(View view) {

        if (readonly) return;

        if (ingreso) {
            if (docnum.isEmpty()){
                toast("Ingrese el número de documento");
                inputDoc();
                return;
            }

            if (T_movrObj.count==0) {
                msgbox("No se ha agregado ningúno producto al inventario, no se puede completar");return;
            }
        } else {
            if (T_movdObj.count==0) {
                msgbox("No se puede guardar un inventario vacío");return;
            }
        }

        if (ingreso) {
            msgAskSave("Aplicar ingreso de mercancía");
        } else {
            msgAskSave("Borrar inventario actual y aplicar inventario inicial");
        }
    }

    public void doFocusBar(View view) {
        if (readonly) return;
        txtBarra.requestFocus();
        khand.setLabel(lblBar,true);
    }

    public void doFocusCant(View view) {
        if (readonly) return;

        khand.setLabel(lblCant,true);
    }

    public void doFocusCosto(View view) {
        if (readonly) return;

        if (gl.invregular) khand.setLabel(lblCosto,true);
    }

    public void doKey(View view) {

        if (readonly) return;

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
        if (readonly) return;
        listUnits();
    }

    public void doInterrupt(View view) {
        if (readonly) return;
        msgAskInterrupt("Interrupir el ingreso de mercancia");
    }

    public void doDoc(View view) {
        if (!readonly) inputDoc();
    }

    public void doHideList(View view) {
        relprod.setVisibility(View.INVISIBLE);
    }

    public void doProd(View view) {
        buscarProducto();
    }

    public void doClearFilter(View view) {
        txtprod.setText("");
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Object lvObj = listView.getItemAtPosition(position);

                if (readonly) return;

                selidx=position;

                if (ingreso) {
                    selitemr = (clsClasses.clsT_movr)lvObj;
                    adapterr.setSelectedIndex(position);
                    setProductr();
                } else {
                    selitem = (clsClasses.clsT_movd)lvObj;
                    adapter.setSelectedIndex(position);
                    setProduct();
                }
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

            grdbtn.setOnItemClickListener((parent, view, position, id) -> {
                Object lvObj = grdbtn.getItemAtPosition(position);
                clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                if (readonly) return;

                adapterb.setSelectedIndex(position);
                processMenuBtn(item.ID);

            });

            lblCosto.setOnFocusChangeListener((v, hasFocus) -> {
                if (readonly) return;

                if(hasFocus) {
                    if (lblCosto.getText().toString().equals("0")) lblCosto.setText("");
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
                        handlerTimer.postDelayed(() -> compareSC(ss), 500);
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

        try {

            selidx=-1;
            lblProd.setText("");
            lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");
            lblCant.setText("");
            lblCosto.setText("");
            khand.setLabel(lblBar,true);

            costot=0;
            cantt=0;

            if (ingreso) {

                try {

                    T_movrObj.fill();cl=T_movrObj.count;

                    for (int i = 0; i <T_movrObj.count; i++) {
                        can=T_movrObj.items.get(i).cant;cantt+=can;
                        tc=can*T_movrObj.items.get(i).precio; costot+=tc;
                        T_movrObj.items.get(i).pesom=tc;
                        T_movrObj.items.get(i).srazon=" ";

                        ex1=dispProd(T_movrObj.items.get(i).producto);ex2=ex1+can;
                        T_movrObj.items.get(i).val1=""+mu.frmdecno(ex1);
                        T_movrObj.items.get(i).val2=""+mu.frmdecno(ex2);
                    }

                    adapterr=new LA_T_movr(this,this,T_movrObj.items);
                    listView.setAdapter(adapterr);

                } catch (Exception e) {
                    msgbox(e.getMessage());
                }

            } else {

                try {

                    T_movdObj.fill();cl=T_movdObj.count;

                    for (int i = 0; i <T_movdObj.count; i++) {
                        can=T_movdObj.items.get(i).cant;cantt+=can;
                        tc=can*T_movdObj.items.get(i).precio; costot+=tc;
                        T_movdObj.items.get(i).pesom=tc;
                        T_movrObj.items.get(i).srazon=" ";
                    }

                    adapter=new LA_T_movd(this,this,T_movdObj.items);
                    listView.setAdapter(adapter);

                } catch (Exception e) {
                    msgbox(e.getMessage());
                }

                if (T_movdObj.count==0) creaEncabezadoInicial();
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

        lblTCant.setText("Cantidad : "+mu.frmdecno(cantt));
        lblTCosto.setText("Total : "+mu.frmcur(costot));
    }

    private void addItem() {

        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        double cant,costo,dd;
        String ss;

        if (prodid==0) {
            toast("Falta definir artículo");
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

        try {

            if (ingreso) {

                if (selidx==-1) {

                    itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                    itemr.corel=" ";
                    itemr.producto=prodid;
                    itemr.cant=cant;
                    itemr.cantm=0;
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
            } else {

                if (selidx==-1) {

                    item.coreldet=T_movdObj.newID("SELECT MAX(coreldet) FROM T_MOVD");
                    item.corel=" ";
                    item.producto=prodid;
                    item.cant=cant;
                    item.cantm=0;
                    item.peso=0;
                    item.pesom=0;
                    item.lote=prodname;
                    item.codigoliquidacion=0;
                    item.unidadmedida=lblUni.getText().toString();
                    item.precio=costo;

                    T_movdObj.add(item);

                } else {
                    selitem.cant=cant;
                    selitem.precio=costo;

                    T_movdObj.update(selitem);
                }
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,true);lblDisp.setText("Disponible:");
            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void save() {

        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;
        clsClasses.clsT_costo cost;

        try {

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            db.beginTransaction();

            //if (!ingreso) db.execSQL("DELETE FROM P_STOCK");
            if (!gl.corelmov.isEmpty()) {
                db.execSQL("DELETE FROM D_MOV WHERE COREL='" + gl.corelmov + "'");
                db.execSQL("DELETE FROM D_MOVD WHERE COREL='" + gl.corelmov + "'");
            }

            header =clsCls.new clsD_Mov();
            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            if (ingreso) header.TIPO="R";else header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA= docnum;
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");
            int corc=T_costoObj.newID("SELECT MAX(CODIGO_COSTO) FROM T_costo");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);
                    item =clsCls.new clsD_MovD();
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

                    updateStock(imovr.producto,convcant,convum);
                    //updateStock(imovr.producto,imovr.cant,imovr.unidadmedida);

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
                        msgbox(Objects.requireNonNull(new Object() {
                        }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
                    }

                    if (imovr.precio>0) {
                        sql = "UPDATE P_PRODUCTO SET COSTO=" + imovr.precio + " WHERE CODIGO_PRODUCTO=" + imovr.producto;
                        db.execSQL(sql);
                    }

                }

            } else {

                for (int i = 0; i <T_movdObj.count; i++) {

                    imov=T_movdObj.items.get(i);
                    converUMBas(imov.producto,imov.cant,imov.unidadmedida);

                    item =clsCls.new clsD_MovD();
                    item.coreldet=corm+i+1;
                    item.corel=corel;
                    item.producto=imov.producto;
                    item.cant=convcant;         // imov.cant;
                    item.cantm=0;
                    item.peso=0;
                    item.pesom=0;
                    item.lote="";
                    item.codigoliquidacion=0;
                    item.unidadmedida=convum.trim();   // imov.unidadmedida;
                    item.precio=imov.precio;
                    item.motivo_ajuste=0;
                    movd.add(item);

                    updateStock(imov.producto,convcant,convum);
                    //updateStock(imov.producto,imov.cant,imov.unidadmedida);
                }

                borraEncabezadoInicial();
                db.execSQL("DELETE FROM T_MOVD");
            }

            db.execSQL("DELETE FROM T_MOVR");
            db.setTransactionSuccessful();
            db.endTransaction();

            gl.autocom = 1;
            startActivity(new Intent(this,WSEnv.class));

            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void savealmacen() {

        clsClasses.clsD_mov_almacen header;
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr imovr;
        clsClasses.clsT_costo cost;

        String corel=gl.ruta+"_"+mu.getCorelBase();

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
            header.almacen_origen=0;
            header.almacen_destino=gl.idalm;
            header.anulado=0;
            header.fecha=du.getActDateTime();
            header.tipo="R";
            header.usuario=gl.codigo_vendedor;
            header.referencia= docnum;
            header.statcom="N";
            header.impres=0;
            header.codigoliquidacion=0;
            header.codigo_proveedor= gl.codigo_proveedor;
            header.total=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD_ALMACEN");
            int corc=T_costoObj.newID("SELECT MAX(CODIGO_COSTO) FROM T_costo");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);

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
                    item.unidadmedida=convum.trim();   // imovr.unidadmedida;
                    item.precio=imovr.precio;
                    item.motivo_ajuste=0;

                    movd.add(item);

                    updateStockAlmacen(imovr.producto,convcant,convum);

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
                        msgbox(Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
                    }

                    sql="UPDATE P_PRODUCTO SET COSTO="+imovr.precio+" WHERE CODIGO_PRODUCTO="+imovr.producto;
                    db.execSQL(sql);
                }

            }

            db.execSQL("DELETE FROM T_MOVR");
            db.setTransactionSuccessful();
            db.endTransaction();

            gl.autocom = 1;
            startActivity(new Intent(this,WSEnv.class));

            finish();

        } catch (Exception e) {
            db.endTransaction();
            String sp=e.getMessage();
            msgbox(Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
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

            sql ="WHERE (CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"') COLLATE NOCASE";
            P_productoObj.fill(sql);
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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void updateStock(int pcod,double pcant,String um) {
        try {
            clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

            ritem.idprod=pcod;
            ritem.idalm=0;
            ritem.cant=pcant;
            ritem.um=um.trim();
            ritem.bandera=0;

            fbs.addItem("/"+gl.tienda+"/",ritem);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateStockAlmacen(int pcod,double pcant,String um) {
        int idalmacen=gl.idalm;

        try {
            //if (gl.idalm==gl.idalmpred) idalmacen=0;

            clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

            ritem.idprod=pcod;
            ritem.idalm=idalmacen;
            ritem.cant=pcant;
            ritem.um=um.trim();
            ritem.bandera=0;

            fbs.addItem("/"+gl.tienda+"/",ritem);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
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

            docnum=D_movObj.first().REFERENCIA;
            lblDoc.setText(docnum);
            gl.codigo_proveedor=D_movObj.first().CODIGO_PROVEEDOR;
            gl.nombre_proveedor=nombreProveedor(gl.codigo_proveedor);
            readonly=D_movObj.first().ANULADO==0;

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

            db.execSQL("DELETE FROM T_stock");

            actualizaExistencias();

        } catch (Exception e) {
            msgbox(new Object() { }.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void actualizaExistencias() {
        fbExistItem fbei;

        try {
            db.execSQL("DELETE FROM T_stock");

            cargacnt=0;
            cargalim=adapterr.getCount();
            if (cargalim==0) return;

            for (int i = 0; i <adapterr.getCount(); i++) {
                fbei=new fbExistItem("Stock",adapterr,i,rnFbListItems);
            }
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

            docnum=D_movObj.first().referencia;
            lblDoc.setText(docnum);
            gl.codigo_proveedor=D_movObj.first().codigo_proveedor;
            gl.nombre_proveedor=nombreProveedor(gl.codigo_proveedor);
            readonly=D_movObj.first().anulado==0;

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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void hold() {

        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;
        String corel;

        try {

            if (gl.corelmov.isEmpty()) {
                corel=gl.ruta+"_"+mu.getCorelBase();
            } else {
                corel=gl.corelmov;
            }

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            db.beginTransaction();

            db.execSQL("DELETE FROM D_MOV WHERE COREL='"+gl.corelmov+"'");
            db.execSQL("DELETE FROM D_MOVD WHERE COREL='"+gl.corelmov+"'");

            header =clsCls.new clsD_Mov();
            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=-1;
            header.FECHA=du.getActDateTime();
            if (ingreso) header.TIPO="R";else header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA= docnum;//gl.nombre_proveedor;
            header.STATCOM="N";
            header.IMPRES=-1;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);

                    item =clsCls.new clsD_MovD();

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

                }

            }

            db.execSQL("DELETE FROM T_MOVR");
            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Ingreso de mercancía guardado");

            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void holdalmacen() {

        clsClasses.clsD_mov_almacen header;
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr imovr;
        String corel;

        try {

            if (gl.corelmov.isEmpty()) {
                corel=gl.ruta+"_"+mu.getCorelBase();
            } else {
                corel=gl.corelmov;
            }

            clsD_mov_almacenObj mov=new clsD_mov_almacenObj(this,Con,db);
            clsD_movd_almacenObj movd=new clsD_movd_almacenObj(this,Con,db);

            db.beginTransaction();
            db.execSQL("DELETE FROM D_MOV_ALMACEN WHERE COREL='" + gl.corelmov + "'");
            db.execSQL("DELETE FROM D_MOVD_ALMACEN WHERE COREL='" + gl.corelmov + "'");

            header =clsCls.new clsD_mov_almacen();
            header.corel=corel;
            header.codigo_sucursal=gl.tienda;
            header.almacen_origen=0;
            header.almacen_destino=gl.idalm;
            header.anulado=-1;
            header.fecha=du.getActDateTime();
            header.tipo="R";
            header.usuario=gl.codigo_vendedor;
            header.referencia= docnum;
            header.statcom="N";
            header.impres=0;
            header.codigoliquidacion=0;
            header.codigo_proveedor= gl.codigo_proveedor;
            header.total=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD_ALMACEN");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);

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

                }

            }

            db.execSQL("DELETE FROM T_MOVR");
            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Ingreso de mercancía guardado.");

            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Menu

    private void iniciaProceso() {

        clsClasses.clsMenu item;

        try {

            db.execSQL("DELETE FROM T_movr");
            db.execSQL("DELETE FROM T_stock");
            if (readonly) return;

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

            iniciaProductos();

        } catch (Exception e){
            addlog(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName(),e.getMessage(),"");
        }

    }

    private void processMenuBtn(int menuid) {

        if (readonly) return;

        try {
            switch (menuid) {
                case 50:
                    relprod.setVisibility(View.VISIBLE);
                    /*
                    gl.gstr = "";browse = 1;
                    gl.gstr="";
                    gl.prodtipo = 2;
                    startActivity(new Intent(this, Producto.class));
                    */
                    break;
                case 54:
                    borraLinea();break;
                case 55:
                    borraTodo();break;
                case 56:
                    imprimir();break;
            }
        } catch (Exception e) {
            addlog(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName(), e.getMessage(), "");
        }
    }

    private void borraLinea() {

        if (selidx==-1) return;

        try {
            if (ingreso) T_movrObj.delete(selitemr);else T_movdObj.delete(selitem);

            listItems();
        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar todos los articulos");
    }

    //endregion

    //region Impresion

    private void imprimir(){

        int aid=0;

        try {

            rep.clear();
            if (gl.tipo==4) aid=gl.idalm;

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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void impresionEncabezado(int aid) {

        rep.empty();
        rep.empty();
        rep.addc(gl.empnom);
        rep.addc(gl.tiendanom);
        rep.empty();
        rep.addc("PREIMPRESION");
        rep.addc("INGRESO DE MERCANCIA");
        rep.empty();
        rep.add("Numero: "+corel+" ");
        rep.add("Fecha: "+du.sfecha(du.getActDate()));
        rep.add("Operador: "+gl.vendnom);
        if (gl.tipo==4) {
            rep.add("Almacen: "+gl.nom_alm);
        }
        rep.add("Proveedor: "+gl.nombre_proveedor);
        rep.add("Ref: "+docnum);
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
            fbs.calculaTotal("/"+gl.tienda+"/",idalmacen,fbprodid,rnFbCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void fbCallBack() {
        try {
            lblDisp.setText("Disponible: "+mu.frmdecno(fbs.total)+" "+ fbs.unimed);

            db.execSQL("DELETE FROM T_stock WHERE IDPROD="+fbprodid);
            db.execSQL("INSERT INTO T_stock VALUES ("+fbprodid+","+fbprodid+","+ fbs.total+",'"+ fbs.unimed+"')");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    //region Firebase exist class

    private class fbExistItem extends fbStock {

        private LA_T_movr adapter;
        private Runnable rnFbExCallBack,rnListView;

        private int itempos,prid;
        private double newexist;

        public fbExistItem(String troot, LA_T_movr adapt,int ipos,Runnable rnCallback) {
            super(troot,gl.tienda);
            adapter=adapt;
            itempos=ipos;
            prid=adapter.items.get(itempos).producto;

            rnListView=rnCallback;

            rnFbExCallBack = new Runnable() {
                public void run() {
                    actualizaExistencia();
                }
            };

            runProcess();
        }

        public void runProcess() {
            calculaTotal("/"+gl.tienda+"/",0,prid,rnFbExCallBack);
        }

        private void actualizaExistencia() {
            String pum;
            double pcan,ex1,ex2;

            newexist=total;

            pum=adapter.items.get(itempos).unidadmedida;
            pcan=adapter.items.get(itempos).cant;

            ex1=newexist;ex2=ex1+pcan;
            adapter.items.get(itempos).val1=""+mu.frmdecno(ex1);
            adapter.items.get(itempos).val2=""+mu.frmdecno(ex2);

            db.execSQL("DELETE FROM T_stock WHERE IDPROD="+prid);
            db.execSQL("INSERT INTO T_stock VALUES ("+prid+","+prid+","+newexist+",'"+pum+"')");

            callBack=rnListView;
            runCallBack();

        }

    }

    private void fbListItems() {
        cargacnt++;
        if (cargacnt==cargalim) listItems();
    }

    //endregion

    //region Lista productos

    private void iniciaProductos() {
        clsClasses.clsT_venta_mod pitem;

        try {
            sql="SELECT CODIGO_PRODUCTO, DESCLARGA,'','','', '','','','' " +
                "FROM P_PRODUCTO WHERE  (CODIGO_TIPO='P') ORDER BY DESCLARGA";
            ViewObj.fillSelect(sql);

            pitems.clear();

            for (int i = 0; i <ViewObj.count; i++) {
                pitem = clsCls.new clsT_venta_mod();

                pitem.id=ViewObj.items.get(i).pk;
                pitem.idmod=0;
                pitem.nombre=ViewObj.items.get(i).f1;

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
            msgbox(Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void buscarProducto() {
        clsClasses.clsT_venta_mod pitem;
        String flt;

        try {

            flt=txtprod.getText().toString();

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

            adapterp=new LA_T_venta_mod(this,this,pitems);
            prodView.setAdapter(adapterp);

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
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void borraEncabezadoInicial() {
        try {
            db.execSQL("DELETE FROM D_MOV WHERE STATCOM='X'");
        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
        }
    }

    private void nombreUnidad() {
        /*
        try {
            P_unidadObj.fill("WHERE (CODIGO_UNIDAD='"+um+"')");
            ubas=P_unidadObj.first().nombre;
        } catch (Exception e) {
            ubas=um;
        }
        */

        ubas=um;
        lblUni.setText(um);
    }

    private String nombreProveedor(int idprov) {
        try {
            clsP_proveedorObj P_proveedorObj=new clsP_proveedorObj(this,Con,db);
            P_proveedorObj.fill("WHERE (CODIGO_PROVEEDOR="+idprov+")");
            return P_proveedorObj.first().nombre;
        } catch (Exception e) {
            return " ";
        }
    }

    private void listUnits() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvRecep.this,"Unidad de medida");

            P_unidad_convObj.fill("WHERE (CODIGO_UNIDAD1='"+um+"') AND (CODIGO_UNIDAD2<>'"+um+"')");
            listdlg.add(um);

            for (int i = 0; i <P_unidad_convObj.count; i++) {
                listdlg.add(P_unidad_convObj.items.get(i).codigo_unidad2);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    lblUni.setText(listdlg.getText(position));
                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.setOnLeftClick(v -> listdlg.dismiss());
            listdlg.show();

        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
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

    public double dispProd(int prodid) {
        double val=0;
        try {
            if (almacen) {
                P_stock_almacenObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+") AND (CODIGO_ALMACEN="+gl.idalm+")");
                if (P_stock_almacenObj.count>0) val=P_stock_almacenObj.first().cant;
            } else {
                T_stockObj.fill("WHERE IDPROD="+prodid);
                if (T_stockObj.count>0) val=T_stockObj.first().cant;
            }
            return val;
        } catch (Exception e) {
            msgbox(Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
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

    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> {
            try {
                sql="DELETE FROM T_MOVR";
                db.execSQL(sql);
                listItems();
            } catch (Exception e) {
                msgbox(Objects.requireNonNull(new Object() {
                }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
            }
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void msgAskSave(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> {
            try {
                if (ingreso) {
                    //if (almacen) {
                        savealmacen();
                    //}else {
                    //    save();
                    //}
                } else msgAskSave2("Continuar");
            } catch (Exception e) {
                msgbox(Objects.requireNonNull(new Object() {
                }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
            }
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void msgAskSave2(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> {
            try {
                if (almacen) savealmacen();else save();
            } catch (Exception e) {
                msgbox(Objects.requireNonNull(new Object() {
                }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
            }
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> finish());
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();
    }

    private void msgAskInterrupt(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> {
            try {
                if (almacen) holdalmacen();else hold();
            } catch (Exception e) {
                msgbox(Objects.requireNonNull(new Object() {
                }.getClass().getEnclosingMethod()).getName()+" . "+e.getMessage());
            }
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void inputDoc() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Número de documento");
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setText(docnum);
        input.requestFocus();
        alert.setPositiveButton("Aplicar", (dialog, whichButton) -> {
            try {
                docnum=input.getText().toString();
                if (docnum.isEmpty()) {
                    mu.msgbox("Valor incorrecto");
                } else {
                    lblDoc.setText(docnum);
                }
            } catch (Exception e) {
                mu.msgbox("Valor incorrecto");
            }
        });
        alert.setNegativeButton("Cancelar", (dialog, whichButton) -> {});
        alert.show();
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
            P_unidadObj.reconnect(Con,db);
            P_unidad_convObj.reconnect(Con,db);
            T_costoObj.reconnect(Con,db);
            P_stockObj.reconnect(Con,db);
            P_stock_almacenObj.reconnect(Con,db);
            ViewObj.reconnect(Con,db);
            T_stockObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        txtBarra.requestFocus();

        if (browse==1) {
            browse=0;
            if (gl.gstr.isEmpty()) return;
            barcode=gl.gstr;
            addBarcode();
        }
    }

    @Override
    public void onBackPressed()    {
        if (ingreso) {
            msgAskExit("Salir sin aplicar ingreso");
        } else {
            super.onBackPressed();
        }
    }

    //endregion

}