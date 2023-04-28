package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsBonFiltro;
import com.dtsgt.classes.clsBonif;
import com.dtsgt.classes.clsBonifGlob;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocomboObj;
import com.dtsgt.classes.clsD_pedidodObj;
import com.dtsgt.classes.clsDeGlob;
import com.dtsgt.classes.clsDescFiltro;
import com.dtsgt.classes.clsDescuento;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_cajahoraObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_orden_numeroObj;
import com.dtsgt.classes.clsP_prodclasifmodifObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsP_vendedor_rolObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.fel.FELVerificacion;
import com.dtsgt.ladapt.ListAdaptGridFam;
import com.dtsgt.ladapt.ListAdaptGridFamList;
import com.dtsgt.ladapt.ListAdaptGridProd;
import com.dtsgt.ladapt.ListAdaptGridProdList;
import com.dtsgt.ladapt.ListAdaptMenuVenta;
import com.dtsgt.ladapt.ListAdaptVenta;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Venta extends PBase {

    private ListView listView;
    private GridView gridViewOpciones,grdbtn,grdfam,grdprod;
    private TextView lblTot,lblTit,lblAlm,lblVend, lblCambiarNivelPrecio,lblCant,lblBarra;
    private TextView lblProd,lblDesc,lblStot,lblKeyDP,lblPokl,lblDir;
    private EditText txtBarra,txtFilter;
    private ImageView imgroad,imgscan,imgllevar;
    private RelativeLayout relScan;

    private ArrayList<clsVenta> items= new ArrayList<clsVenta>();
    private ListAdaptVenta adapter;
    private clsVenta selitem;
    private Precio prc;
    private clsKeybHandler khand;

    private ListAdaptMenuVenta adaptergrid;
    private ListAdaptMenuVenta adapterb;
    private ListAdaptGridFam adapterf;
    private ListAdaptGridFamList adapterfl;
    private ListAdaptGridProd adapterp;
    private ListAdaptGridProdList adapterpl;

    private ExDialog mMenuDlg;

    private ArrayList<clsClasses.clsMenu> mitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> fitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<clsClasses.clsMenu> pitems= new ArrayList<clsClasses.clsMenu>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();
    private ArrayList<String> peditems = new ArrayList<String>();

    private AppMethods app;

    private clsD_pedidoObj D_pedidoObj;
    private clsP_nivelprecioObj P_nivelprecioObj;
    private clsP_productoObj P_productoObj;
    private clsVendedoresObj MeserosObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;

    private clsRepBuilder rep;

    private clsVenta vitem;

    private int browse;
    private double cant,desc,mdesc,prec,precsin,imp,impval,pimp, descLinea;
    private double descmon,tot,totsin,percep,ttimp,ttperc,ttsin,prodtot,savecant;
    private double px,py,cpx,cpy,cdist,savetot,saveprec,prodtotlin;

    private String uid,seluid,prodid,uprodid,um,tiposcan,barcode,imgfold,tipo,pprodname,mesa,nivname;
    private int nivel,dweek,clidia,counter,menuitemid, lineaId;
    private boolean sinimp,softscanexist,porpeso,usarscan,handlecant=true,pedidos,descflag,meseros=false;
    private boolean decimal,menuitemadd,usarbio,imgflag,scanning=false,prodflag=true,listflag=true;
    private boolean horiz=true,porcentaje,domenvio,modoHN,modoSV;
    private int codigo_cliente, emp,pedidoscant,cod_prod;
    private String cliid,saveprodid,pedcorel;
    private int famid = -1;
    public static boolean DescPorProducto, DesPorLinea = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_venta);horiz=true;
        } else {
            setContentView(R.layout.activity_venta_ver);horiz=false;
        }

        super.InitBase();

        addlog("Venta",""+du.getActDateTime(),String.valueOf(gl.vend));

        setControls();

        P_nivelprecioObj=new clsP_nivelprecioObj(this,Con,db);
        P_nivelprecioObj.fill("ORDER BY Nombre");

        //gl.cliente="";
        //gl.iniciaVenta=false;
        gl.scancliente="";
        emp=gl.emp;
        gl.nivel=gl.nivel_sucursal;
        setNivel();

        modoHN=gl.codigo_pais.equalsIgnoreCase("HN");
        modoSV=gl.codigo_pais.equalsIgnoreCase("SV");

        cliid=gl.cliente;
        //cliid="0"; #CKFK 20200515 puse esto en comentario porque primero se le asigna el Id de cliente
        decimal=false;

        gl.atentini=du.getActDateTime();
        gl.ateninistr=du.geActTimeStr();
        gl.climode=true;
        mu.currsymb(gl.peMon);

        getURL();

        pedidos=gl.pePedidos;
        domenvio=gl.peDomEntEnvio;

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);P_productoObj.fill();
        MeserosObj =new clsVendedoresObj(this,Con,db);
        T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);

        app = new AppMethods(this, gl, Con, db);
        app.parametrosExtra();

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

        counter=1;

        prc=new Precio(this,mu,2);
        khand=new clsKeybHandler(this,lblCant,lblKeyDP);

        modoMeseros();

        menuItems();
        setHandlers();
        initValues();

        browse=0;
        txtBarra.requestFocus();txtBarra.setText("");
        clearItem();

        if (P_nivelprecioObj.count==0) {
            mu.msgbox("NO SE PUEDE VENDER, NO ESTÁ DEFINIDO NINGUNO NIVEL DE PRECIO");finish();return;
        }

        imgflag=gl.peMImg;
        gl.sin_propina=false;
        setVisual();

        checkLock();

        if(!gl.exitflag) {

            Handler mtimer = new Handler();

            Runnable mrunner= () -> {

                gl.scancliente="";
                browse=8;

                gl.iniciaVenta=false;

                if (usarbio) {
                    startActivity(new Intent(Venta.this,Clientes.class));
                } else {
                    if (!gl.cliposflag) {
                        gl.cliposflag=true;
                        if (!gl.exitflag) {
                            if (!gl.peRest) startActivity(new Intent(Venta.this,CliPos.class));
                        }
                    }
                }
            };
            mtimer.postDelayed(mrunner,100);
        }

        if (gl.ingreso_mesero) meseroAutoLogin();
    }

    //region Events

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            //if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                barcode=contents;
                toast(barcode);
            }
            //}
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void doFocus(View view) {
        try {
            txtBarra.requestFocus();
        } catch (Exception e) {}
    }

    public void showPromo(View view){
        try{
            gl.gstr="*";
            browse=3;

            Intent intent = new Intent(this,ListaPromo.class);
            startActivity(intent);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void doSoftScan(View view) {
		/*if (softscanexist) {
			try{
				browse=5;barcode="";

				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}catch (Exception e){
				addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
			}
		//} else {*/
        doFocus(view);
        //}
    }

    public void doNivel(View view) {
        showNivelMenu();
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            barcode=khand.getStringValue();
            if (!barcode.isEmpty()) {
                addBarcode();
            }
         }
    }

    public void subItemClick(int position,int handle) {
    }

    public void doFocusScan(View view) {
        txtBarra.requestFocus();
    }

    private void setHandlers(){

        try{

            listView.setOnTouchListener(new SwipeListener(this) {
                public void onSwipeRight() {
                    onBackPressed();
                }
                public void onSwipeLeft() {
                    //finishOrder(null);
                }
            });

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        vitem = (clsVenta)lvObj;

                        prodid=vitem.Cod;gl.prodid=prodid;
                        gl.prodmenu=app.codigoProducto(prodid);//gl.prodmenu=prodid;
                        uprodid=prodid;
                        prodtotlin=vitem.Total;
                        uid=vitem.emp;gl.menuitemid=uid;seluid=uid;// identificador unico de linea de T_VENTA ( Campo EMPRESA )
                        try {
                            gl.produid=Integer.parseInt(uid);
                        } catch (Exception e) {
                            gl.produid=0;
                        }
                        adapter.setSelectedIndex(position);

                        gl.gstr=vitem.Nombre;
                        gl.retcant=(int) vitem.Cant;
                        gl.limcant=getDisp(prodid);
                        menuitemadd=false;

                        if (!gl.ventalock) {
                            //tipo=prodTipo(gl.prodcod);
                            tipo=prodTipo(prodid);
                            gl.tipoprodcod=tipo;
                            gl.idmodgr=codigoModificador(app.codigoProducto(gl.prodid));

                            if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S") || tipo.equalsIgnoreCase("PB")) {
                                browse=6;
                                gl.menuitemid=prodid;
                                showVentaItemMenu(0);
                                //startActivity(new Intent(Venta.this,VentaEdit.class));
                            } else if (tipo.equalsIgnoreCase("M")) {
                                gl.newmenuitem=false;
                                gl.menuitemid=vitem.emp;
                                browse=7;
                                showVentaItemMenu(1);
                                //startActivity(new Intent(Venta.this,ProdMenu.class));
                            }
                        }
                    } catch (Exception e) {
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            txtBarra.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

                public void onTextChanged(CharSequence s, int start,int before, int count) {
                    //mu.msgbox("start "+start+" before "+before+" count "+count);

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

            grdfam.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = grdfam.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;
                        famid=item.icod;

                        if (imgflag) {
                            adapterf.setSelectedIndex(position);
                        } else {
                            adapterfl.setSelectedIndex(position);
                        }

                        listProduct();
                    } catch (Exception e) {
                        String ss=e.getMessage();
                    }
                };
            });

            grdprod.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    int kcant,ppos;

                    try {
                        Object lvObj = grdprod.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        if (imgflag) {
                            adapterp.setSelectedIndex(position);
                        } else {
                            adapterpl.setSelectedIndex(position);
                        }

                        prodid=item.Cod;
                        gl.prodid=prodid;
                        gl.prodcod=item.icod;
                        gl.gstr=prodid;
                        gl.prodmenu=gl.prodcod;
                        gl.pprodname=item.Name;
                        ppos=gl.pprodname.indexOf("[");
                        if (ppos<=1) pprodname=gl.pprodname;else pprodname=gl.pprodname.substring(0,ppos-1);

                        gl.um=app.umVenta(gl.prodid);
                        gl.menuitemid=prodid;
                        menuitemadd=true;

                        if (khand.val.isEmpty()) {
                            processItem(false);
                        } else {
                            try {
                                kcant=Integer.parseInt(khand.val);
                                if (kcant>0) processItem(kcant);
                            } catch (Exception e) { }
                            khand.clear();
                        }

                    } catch (Exception e) {
                        String ss=e.getMessage();
                    }
                };
            });

            grdprod.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Object lvObj = grdprod.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        adapterp.setSelectedIndex(position);

                        prodid=item.Cod;
                        gl.gstr=prodid;//gl.prodmenu=prodid;
                        gl.pprodname=item.Name;

                        msgAskAdd(item.Name);
                    } catch (Exception e) {}
                    return true;
                }
            });

            gridViewOpciones.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = gridViewOpciones.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        adaptergrid.setSelectedIndex(position);

                        //if (item.ID==1) {
                        //    if (validaFacturas()) processMenuTools(item.ID);
                        //} else {
                            processMenuTools(item.ID);
                        //}
                    } catch (Exception e) {
                        String ss=e.getMessage();
                    }
                };
            });

            grdbtn.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = grdbtn.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        adapterb.setSelectedIndex(position);
                        processMenuItems(item.ID);
                    } catch (Exception e) {
                        String ss=e.getMessage();
                    }
                };
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Main

    public void listItems() {
        Cursor DT;
        clsVenta item;
        double tt,stot,tdesc,desc;
        int ii;

        items.clear();tot=0;ttimp=0;ttperc=0;tdesc=0;selidx=-1;ii=0;seluid="";

        try {
            sql="SELECT T_VENTA.PRODUCTO, P_PRODUCTO.DESCCORTA, T_VENTA.TOTAL, T_VENTA.CANT, T_VENTA.PRECIODOC, " +
                    "T_VENTA.DES, T_VENTA.IMP, T_VENTA.PERCEP, T_VENTA.UM, T_VENTA.PESO, T_VENTA.UMSTOCK, " +
                    "T_VENTA.DESMON, T_VENTA.EMPRESA, T_VENTA.VAL2  " +
                    "FROM T_VENTA INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=T_VENTA.PRODUCTO "+
                    "ORDER BY P_PRODUCTO.DESCCORTA ";

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();
                while (!DT.isAfterLast()) {

                    tt=DT.getDouble(2);

                    item = clsCls.new clsVenta();
                    item.Cod=DT.getString(0);
                    item.Nombre=DT.getString(1);
                    item.Cant=DT.getDouble(3);
                    item.Prec=DT.getDouble(4);
                    item.Desc=DT.getDouble(5);
                    item.sdesc=mu.frmdec(item.Prec);
                    item.imp=DT.getDouble(6);
                    item.percep=DT.getDouble(7);
                    item.um=umVenta(item.Cod);
                    item.Peso=DT.getDouble(9);
                    item.emp=DT.getString(12);

                    if (item.emp.equalsIgnoreCase(uid)) {
                        selidx=ii;
                        seluid=uid;
                    }
                    desc=DT.getDouble(11);tdesc+=desc;
                    item.nota=DT.getString(13);

                    item.val=mu.frmdecimal(item.Cant,gl.peDecImp)+" "+ltrim(item.um,6);

                    if (desc>0) {
                        item.valp=mu.frmdecimal(desc,2);
                    } else {
                        item.valp=".";
                    }

                    if (sinimp) {
                        ttsin=tt-item.imp-item.percep;
                        item.Total=ttsin;
                        if (modoHN) item.Total=tt;
                        if (modoSV) item.Total=tt;
                    } else {
                        item.Total=tt;
                    }

                    T_ordencomboprecioObj.fill("WHERE (COREL='VENTA') AND (IDCOMBO="+item.emp+")");
                    if (T_ordencomboprecioObj.count>0) {
                        item.Prec=T_ordencomboprecioObj.first().prectotal;
                        item.sdesc=mu.frmdec(item.Prec);

                        tt=item.Cant*item.Prec;tt=mu.round2(tt);
                        item.Total=tt;
                    }

                    items.add(item);

                    tot+=tt;
                    ttimp+=item.imp;
                    ttperc+=item.percep;

                    DT.moveToNext();ii++;
                }
            }

            if (DT!=null) DT.close();

            adapter=new ListAdaptVenta(this,this, items);
            adapter.cursym=gl.peMon;
            listView.setAdapter(adapter);

            if (sinimp) {
                ttsin=tot-ttimp-ttperc;
                ttsin=mu.round(ttsin,2);
                lblTot.setText(mu.frmcur(ttsin));
                if (modoHN) {
                    lblTot.setText(mu.frmcur(tot));
                    lblStot.setText("Subt : "+mu.frmcur(ttsin));
                }
                if (modoSV) {
                    lblTot.setText(mu.frmcur(tot));
                    lblStot.setText("Subt : "+mu.frmcur(ttsin));
                }
            } else {
                tot=mu.round(tot,2);
                tdesc=mu.round(tdesc,2);
                stot=tot+tdesc;
                lblTot.setText(mu.frmcur(tot));
                lblDesc.setText("Desc : "+mu.frmcur(tdesc));
                lblStot.setText("Subt : "+mu.frmcur(stot));
            }

            if (selidx>-1) {
                adapter.setSelectedIndex(selidx);
                try {
                    Object lvObj = listView.getItemAtPosition(selidx);
                    vitem = (clsVenta)lvObj;
                    prodtotlin=vitem.Total;
                    //toast("tot "+prodtotlin);
                } catch (Exception e) {
                    prodtotlin=0;
                }

                listView.smoothScrollToPosition(selidx);
            } else seluid="";

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

    }

    private void processItem(boolean updateitem){
        boolean exists;

        if (gl.rol==4) {
            toast("El mesero no puede realizar venta en esta pantalla");return;
        }

        try{

            try {
                sql="SELECT Empresa,Cant FROM T_VENTA WHERE (PRODUCTO='"+prodid+"')";
                Cursor dt=Con.OpenDT(sql);
                exists=dt.getCount()>0;
            } catch (SQLException e) {
                exists=false;
            }
            descflag=true;

            String pid=gl.gstr;
            if (mu.emptystr(pid)) return;

            prodid=pid;

            gl.um=app.umVenta(prodid); um=gl.um;
            gl.bonprodid=prodid;

            khand.enable();khand.focus();

            gl.prodcod=app.codigoProducto(prodid);
            prodPrecio();
            saveprec=mu.round2(prc.preciobase);

            gl.dval=1;
            gl.limcant=getDisp(prodid);
            tipo=prodTipo(gl.prodcod);
            gl.tipoprodcod=tipo;

            if (!tipo.equalsIgnoreCase("M")) {
                if (tipo.equalsIgnoreCase("P")) {
                    if (gl.limcant>0) {
                        if (exists) descflag=false;
                        processCant(updateitem);
                    } else {
                        processCant(updateitem);
                        //msgAskLimit("El producto "+ pprodname+" no tiene existencia disponible.\n¿Continuar con la venta?",updateitem);
                    }
                } else if (tipo.equalsIgnoreCase("S")) {
                    if (exists) descflag=false;
                    processCant(updateitem);
                } else if (tipo.equalsIgnoreCase("PB")) {
                    if (!app.barrilAbierto(prodid)) {
                        msgbox("El producto no tiene abierto el barril");return;
                    }
                    if (exists) descflag=false;
                    processCant(updateitem);
                }
            } else if (tipo.equalsIgnoreCase("M")){
                processMenuItem();
            }
       } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
       }
    }

    private void processItem(int prcant){
        try {
            String pid=gl.gstr;
            if (mu.emptystr(pid)) return;

            prodid=pid;

            gl.um=app.umVenta(prodid); um=gl.um;
            gl.bonprodid=prodid;

            khand.enable();khand.focus();

            prodPrecio();

            gl.dval=prcant;
            gl.limcant=getDisp(prodid);
            tipo=prodTipo(gl.prodcod);
            gl.tipoprodcod=tipo;

            if (!tipo.equalsIgnoreCase("M")) {
                if (tipo.equalsIgnoreCase("P")) {
                    if (gl.limcant>0) {
                        processCant(false);
                    } else {
                        processCant(false);
                        //msgAskLimit("\"El producto \"+ pprodname+\" no tiene existencia disponible.\n¿Continuar con la venta?",false);
                    }
                } else if (tipo.equalsIgnoreCase("S")) {
                    processCant(false);
                }
            } else if (tipo.equalsIgnoreCase("M")){
                processMenuItem();
            }
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private boolean processProdBarra(String barra) {
        Cursor DT;

        txtBarra.setText("");
        txtBarra.requestFocus();

        try {

            sql="SELECT CODIGO,DESCLARGA FROM P_PRODUCTO WHERE (CODBARRA='"+barra+"') OR (CODIGO='"+barra+"')";
            DT=Con.OpenDT(sql);
            if (DT.getCount()==0) {
                toast("Barra no existe");return false;
            }

            DT.moveToFirst();
            gl.gstr=DT.getString(0);
            gl.pprodname=DT.getString(1);
            if (DT!=null) DT.close();

            processItem(false);

            txtBarra.requestFocus();

            return true;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());return false;
        }
    }

    private void setCant(){
        Cursor dt;
        double icant;

        try {
            sql="SELECT CANT FROM T_VENTA WHERE PRODUCTO='"+prodid+"'";
            dt=Con.OpenDT(sql);

            if(dt.getCount()>0) {
                dt.moveToFirst();
                icant = dt.getDouble(0);
            } else {
                icant =0;
            }

            if (dt!=null) dt.close();
        } catch (Exception e) {
            icant=0;
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        khand.setValue(icant);
        khand.enable();khand.focus();
    }

    private void processCant(boolean updateitem){
        clsDescuento clsDesc, clsDescLinea;
        clsBonif clsBonif;
        Cursor DT;
        double cnt,vv;
        String s;

        cnt = gl.dval;
        if (cnt < 0) return;

        cod_prod=app.codigoProducto(prodid);

        try {

            try {
                sql = "SELECT CODIGO,DESCCORTA FROM P_PRODUCTO WHERE CODIGO='" + prodid + "'";
                DT = Con.OpenDT(sql);
                DT.moveToFirst();
            } catch (Exception e) {
                addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
                mu.msgbox(e.getMessage());
            }

            cant = cnt;
            um = gl.um;

            // Bonificacion

            // Borra la anterior, si existe
            sql="DELETE FROM T_BONITEM WHERE Prodid='"+prodid+"'";
            db.execSQL(sql);

            desc = 0;
            prodPrecio();
            saveprec=mu.round2(prc.preciobase);
            savetot=saveprec*cant;

            gl.bonprodcant = cant;
            gl.bonus.clear();

            vv = cant * prec;vv = mu.round(vv, 2);

            //region Descuento , Bonif

             /*
            clsBonif = new clsBonif(this, prodid, cant, vv);
            if (clsBonif.tieneBonif()) {
                for (int i = 0; i < clsBonif.items.size(); i++) {
                    gl.bonus.add(clsBonif.items.get(i));
                }
            }
            */

            //Descuentos
            DescPorProducto = false;
            DesPorLinea = true;

            clsDesc = new clsDescuento(this, ""+cod_prod, cant);
            desc = clsDesc.getDesc();
            mdesc = clsDesc.monto;
            savecant=cant;
            descmon=0;

            //Se valida si existe descuento por producto
            if (DescPorProducto) {
                if (desc + mdesc > 0) {

                    browse = 3;
                    gl.promprod = "" + cod_prod;// prodid;
                    gl.promcant = cant;

                    if (desc > 0) {
                        gl.prommodo = 0;
                        gl.promdesc = desc;
                    } else {
                        gl.prommodo = 1;
                        gl.promdesc = mdesc;
                    }

                    saveprodid = prodid;
                    if (descflag) startActivity(new Intent(this, DescBon.class));
                    descflag = true;

                } else {
                    /*
                    if (gl.bonus.size() > 0) {
                        Intent intent = new Intent(this, BonList.class);
                        startActivity(intent);
                    }*/
                }
            } else if (DesPorLinea) {
                desc = 0;

                if (getLineaProducto()) {

                    Cursor dt;
                    double auxCant = 0;

                    //T_venta
                    int cprod = app.codigoProducto(prodid);

                    clsT_ventaObj T_Venta = new clsT_ventaObj(this, Con, db);
                    T_Venta.fill();

                    for (int i = 0; i < T_Venta.count; i++) {
                        sql="SELECT LINEA, CODIGO_PRODUCTO FROM P_PRODUCTO WHERE (CODIGO='"+T_Venta.items.get(i).producto+"')";
                        dt=Con.OpenDT(sql);
                        if (lineaId == dt.getInt(0)) {
                            if (dt.getInt(1) != cprod) {
                                auxCant += T_Venta.items.get(i).cant;
                            }
                        }
                        if (dt!=null) dt.close();
                    }

                    auxCant = auxCant + cant;
                    clsDescLinea = new clsDescuento(this, "" + cod_prod, auxCant);
                    descLinea = clsDescLinea.getDesc();

                    browse = 3;
                    gl.promprod =""+cod_prod;// prodid;
                    gl.promcant = cant;

                    if (descLinea > 0) {
                        gl.prommodo = 0;
                        gl.promdesc = descLinea;
                        saveprodid=prodid;
                        if (descflag) startActivity(new Intent(this, DescBon.class));
                        descflag=true;
                    }
                }
            } else {
                descLinea = 0;
                desc = 0;
            }

            prodPrecio();

            precsin = prc.precsin;
            imp = prc.imp;
            impval = prc.impval;
            totsin = prc.totsin;

            /*
            tot = prc.tot;
            descmon = savetot-tot;//prc.descmon;
            prodtot = tot;
            percep = 0;
            */

            tipo=prodTipo(gl.prodcod);

            if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S") || tipo.equalsIgnoreCase("PB")) {
                if (updateitem) {
                    if (updateItemUID()) clearItem();
                } else {
                    if (addItem()) clearItem();
                }
            } else if (tipo.equalsIgnoreCase("M")){
                if (updateitem) {
                    if (updateMenuItemUID()) clearItem();
                } else {
                    if (addItemMenu()) clearItem();
                 }
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }
    }

    public void actualizar_descuento_por_linea_otros_items(){

        clsDescuento clsDesc, clsDescLinea;
        double auxCant =0;
        Cursor dt;
        int cprod =0;

        try {

            clsT_ventaObj T_Venta = new clsT_ventaObj(this, Con, db);
            T_Venta.fill();

            for (int i = 0; i < T_Venta.count; i++) {
              actualizar_descuento_por_linea_by_tventa((T_Venta.items.get(i)));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private void processMenuItem() {
        counter++;
        gl.menuitemid=""+counter;
        gl.newmenuitem=true;
        gl.gstr=gl.pprodname;
        gl.retcant=1;

        browse=7;
        startActivity(new Intent(this,ProdMenu.class));
    }

    private void updateCant() {

        //if (gl.retcant<0 || gl.limcant==0) return;
        if (gl.retcant<0) return;

        prodid=uprodid;
        //#EJC20200710: Corrección al eliminar y adicionar cantidad en producto.
        gl.prodcod = app.codigoProducto(prodid);
        tipo=prodTipo(gl.prodcod);

        if (!tipo.isEmpty()){
            gl.dval=gl.retcant;
            gl.limcant=getDisp(prodid);
            processCant(true);
            updItemLineaProd();
            listItems();
        }

    }

    private void processCantMenu() {
        listItems();
    }

    private void updDesc(){
        try{
            //desc=gl.promdesc;
            cant=savecant;
            prodPrecio();

            if (desc > 0) {
                updItem();
            } else if (descLinea > 0) {
                updItemLineaProd();
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void updDescMonto(){
        try {

            if (gl.promdesc<0) return;

            desc=gl.promdesc;
            cant=savecant;
            prodPrecio();
            updItemMonto();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void prodPrecio() {
        double sdesc=desc;

        try {
            prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, 0,um,gl.prodcod);
            pimp=prc.imp;
            double impv=prc.impval;
            desc=sdesc;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }
    }

    private boolean addItem(){
        Cursor dt;
        double precdoc,fact,cantbas,peso,vtot;
        long prri;
        String umb;

        tipo=prodTipo(gl.prodcod);

        if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S") || tipo.equalsIgnoreCase("PB") ) {
            try {
                sql="SELECT Empresa,Cant FROM T_VENTA WHERE (PRODUCTO='"+prodid+"')";
                dt=Con.OpenDT(sql);
                if (dt.getCount()>0) {
                    if (dt!=null) dt.close();
                    openItem();return true;
                }
                if (dt!=null) dt.close();
            } catch (SQLException e) {
                mu.msgbox("Error : " + e.getMessage());return false;
            }
        }

        try {
            int icod=Integer.parseInt(prodid);

            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+icod+"') AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            umb=dt.getString(0);
            fact=dt.getDouble(1);
            if (dt!=null) dt.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            umb=um;fact=1;
        }

        if (gl.codigo_pais.equalsIgnoreCase("GT")) {
            prec = prodPrecioBaseImp(app.codigoProducto(prodid));
        } else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
            prec=prodPrecioBaseImp(app.codigoProducto(prodid));
        } else if (gl.codigo_pais.equalsIgnoreCase("SV")) {
            prec=prodPrecioBaseImp(app.codigoProducto(prodid));
        }
        prec=mu.round(prec,2);

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        vtot=prec*cant;
        prodtot=mu.round2dec(vtot);

        impval=mu.round2dec(impval);
        impval=impval*cant;


        /*
        vtot=vtot*100;
        prri=Math.round(vtot);
        vtot=(double) prri;
        prodtot=vtot*0.01;
        */

        //prodtot=mu.round(prec*cant,2);

        try {

            //if (sinimp) precdoc=precsin; else precdoc=prec;
            if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                precdoc = precsin;
            } else if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                precdoc = precsin;
            } else {
                precdoc=prec;
            }

            ins.init("T_VENTA");
            counter++;
            ins.add("PRODUCTO",prodid);
            ins.add("EMPRESA",""+counter);
            if (porpeso) ins.add("UM",gl.umpeso);else ins.add("UM",gl.um);
            ins.add("CANT",cant);
            ins.add("UMSTOCK",gl.um);
            if (gl.umfactor==0) gl.umfactor=1;

            ins.add("FACTOR",gl.umfactor);
            if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",prodtot);
            //if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
            ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);

            if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                ins.add("VAL1", pimp);
            } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                ins.add("VAL1", pimp);
            } else {
                ins.add("VAL1",0);
            }

            ins.add("VAL2","");
            ins.add("VAL3",0);
            ins.add("VAL4","0");
            ins.add("PERCEP",percep);

            db.execSQL(ins.sql());

            counter++;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();

        return true;
    }

    private boolean addItemMenu(){
        Cursor dt;
        double precdoc,fact,cantbas,peso,tt,cprec;
        int precid;
        String umb;


        try {
            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO='"+prodid+"') AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            umb=dt.getString(0);
            fact=dt.getDouble(1);
            if (dt!=null) dt.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            umb=um;fact=1;
        }

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

        impval=mu.round2dec(impval*cant);

        try {

            if (sinimp) precdoc=precsin; else precdoc=prec;

            ins.init("T_VENTA");
            ins.add("PRODUCTO",prodid);
            ins.add("EMPRESA",""+counter);
            if (porpeso) ins.add("UM",gl.umpeso);else ins.add("UM",gl.um);
            ins.add("CANT",cant);
            ins.add("UMSTOCK",gl.um);
            if (gl.umfactor==0) gl.umfactor=1;
            ins.add("FACTOR",gl.umfactor);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2","");
            ins.add("VAL3",0);
            ins.add("VAL4","");
            ins.add("PERCEP",percep);

            T_ordencomboprecioObj.fill("WHERE (COREL='VENTA') ORDER BY IDCOMBO DESC)");
            precid=T_ordencomboprecioObj.first().idcombo;

            T_ordencomboprecioObj.fill("WHERE (COREL='VENTA') AND (IDCOMBO="+precid+")");
            if (T_ordencomboprecioObj.count>0) {
                cprec=T_ordencomboprecioObj.first().prectotal;
                ins.add("PRECIO",cprec);
                ins.add("PRECIODOC",cprec);
                tt=cant*cprec;tt=mu.round2(tt);
                ins.add("TOTAL",tt);
            } else {
                if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
                if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
                ins.add("TOTAL",prodtot);
            }

            db.execSQL(ins.sql());

            counter++;
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_VENTA WHERE CANT=0";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();

        return true;
    }

    private void updItem(){
        double ptot=0;

        try {
            savetot=mu.round(saveprec*cant,2);
            //ptot=mu.round(prec*cant,2);
            //descmon = savetot-ptot;

            //#AT20230410 Calcular descuento
            if (gl.promdesc > 0) {
                descmon = savetot * gl.promdesc / 100;
                ptot = mu.round(savetot - descmon, 2);
            } else if (gl.prommdesc > 0) {
                descmon = savetot * gl.prommdesc / 100;
                ptot = mu.round(savetot - descmon, 2);
            } else {
                ptot=mu.round(prec*cant,2);
                descmon = savetot-ptot;
            }

            imp=mu.round2dec(imp*cant);

            upd.init("T_VENTA");

            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",ptot);
            upd.add("PRECIODOC",prec);

            upd.Where("PRODUCTO='"+prodid+"'");

            db.execSQL(upd.sql());

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();
    }

    //#AT20230424 UpdateItem Desucento por linea
    private void updItemLineaProd(){
        double ptot=0;
        Cursor dt;

        try {

            clsT_ventaObj T_Venta = new clsT_ventaObj(this, Con, db);
            T_Venta.fill();

            for (int i = 0; i < T_Venta.count; i++) {

                sql="SELECT LINEA, CODIGO_PRODUCTO FROM P_PRODUCTO WHERE (CODIGO='"+T_Venta.items.get(i).producto+"')";
                dt=Con.OpenDT(sql);

                if (lineaId == dt.getInt(0)) {

                    savetot=mu.round(T_Venta.items.get(i).precio * T_Venta.items.get(i).cant,2);

                    if (descLinea > 0) {
                        descmon = savetot * descLinea / 100;
                        ptot = mu.round(savetot - descmon, 2);
                        //actualizar_descuento_por_linea_otros_items();
                    } else {
                        ptot=mu.round(T_Venta.items.get(i).precio*T_Venta.items.get(i).cant,2);
                        descmon = savetot-ptot;
                    }

                    imp=mu.round2dec(imp*cant);

                    upd.init("T_VENTA");
                    upd.add("PRECIO",T_Venta.items.get(i).precio);
                    upd.add("IMP",imp);
                    upd.add("DES",descLinea);
                    upd.add("DESMON",descmon);
                    upd.add("TOTAL",ptot);
                    upd.add("PRECIODOC",T_Venta.items.get(i).precio);
                    upd.Where("PRODUCTO='"+T_Venta.items.get(i).producto+"'");
                    db.execSQL(upd.sql());
                }

                if (dt!=null) dt.close();
            }


        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();
    }

    private void actualizar_linea_on_delete(){
        double ptot=0;
        Cursor dt;

        try {

            clsT_ventaObj T_Venta = new clsT_ventaObj(this, Con, db);
            T_Venta.fill();

            for (int i = 0; i < T_Venta.count; i++) {

                sql="SELECT LINEA, CODIGO_PRODUCTO FROM P_PRODUCTO WHERE (CODIGO='"+T_Venta.items.get(i).producto+"')";
                dt=Con.OpenDT(sql);

                if (lineaId == dt.getInt(0)) {

                    savetot=mu.round(T_Venta.items.get(i).precio * T_Venta.items.get(i).cant,2);

                    if (descLinea > 0) {
                        descmon = savetot * descLinea / 100;
                        ptot = mu.round(savetot - descmon, 2);
                        actualizar_descuento_por_linea_otros_items();
                        return;
                    } else {
                        ptot=mu.round(T_Venta.items.get(i).precio*T_Venta.items.get(i).cant,2);
                        descmon = savetot-ptot;
                    }

                    imp=mu.round2dec(imp*cant);

                    upd.init("T_VENTA");
                    upd.add("PRECIO",T_Venta.items.get(i).precio);
                    upd.add("IMP",imp);
                    upd.add("DES",descLinea);
                    upd.add("DESMON",descmon);
                    upd.add("TOTAL",ptot);
                    upd.add("PRECIODOC",T_Venta.items.get(i).precio);
                    upd.Where("PRODUCTO='"+T_Venta.items.get(i).producto+"'");
                    db.execSQL(upd.sql());
                }

                if (dt!=null) dt.close();
            }


        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();
    }
    private void actualizar_descuento_por_linea_by_tventa(clsClasses.clsT_venta T_Venta){

        double ptot=0;
        Cursor dt;

        try {

            sql="SELECT LINEA, CODIGO_PRODUCTO FROM P_PRODUCTO WHERE (CODIGO='"+T_Venta.producto+"')";
            dt=Con.OpenDT(sql);

            if (lineaId == dt.getInt(0)) {

                savetot=mu.round(T_Venta.precio * T_Venta.cant,2);

                if (descLinea > 0) {
                    descmon = savetot * descLinea / 100;
                    ptot = mu.round(savetot - descmon, 2);
                } else {
                    ptot=mu.round(T_Venta.precio*T_Venta.cant,2);
                    descmon = savetot-ptot;
                }

                imp=mu.round2dec(imp*cant);

                upd.init("T_VENTA");
                upd.add("PRECIO",T_Venta.precio);
                upd.add("IMP",imp);
                upd.add("DES",descLinea);
                upd.add("DESMON",descmon);
                upd.add("TOTAL",ptot);
                upd.add("PRECIODOC",T_Venta.precio);
                upd.Where("PRODUCTO='"+T_Venta.producto+"'");
                db.execSQL(upd.sql());
            }

            if (dt!=null) dt.close();

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }
    }
    private void updItemMonto(){
        double ptot=0;

        try {

            savetot=mu.round(prec*cant,2);
            ptot=savetot-gl.promdesc;
            descmon = savetot-ptot;
            if (savetot>0) desc=100*descmon/savetot;else desc=0;

            imp=mu.round2dec(imp*cant);

            upd.init("T_VENTA");

            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",ptot);
            upd.add("PRECIODOC",prec);

            upd.Where("EMPRESA='"+uid+"'");

            db.execSQL(upd.sql());

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        listItems();
    }

    private boolean updateItemUID(){
        double precdoc,valimp;

        try {

            prodtot=prodtot*cant;
            prodtot= mu.round2dec(prodtot);

            if (sinimp) precdoc=precsin; else precdoc=prec;
            valimp=cant*prec*imp/100;valimp=mu.round2(valimp);

            impval=mu.round2dec(impval);
            impval=mu.round2dec(impval*cant);

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",impval);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);
            upd.Where("EMPRESA='"+uid+"'");
            db.execSQL(upd.sql());

            updDesc();
            listItems();

            return true;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

    }

    private boolean updateMenuItemUID(){

        double precdoc;

        try {

            prodtot=mu.round(prec*cant,2);
            if (sinimp) precdoc=precsin; else precdoc=prec;

            imp=mu.round2dec(imp*cant);

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);
            upd.Where("EMPRESA='"+uid+"'");
            db.execSQL(upd.sql());

            listItems();

            return true;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

    }

    private void delItem(){
        try {
            db.execSQL("DELETE FROM T_VENTA WHERE PRODUCTO='"+prodid+"'");
            listItems();
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }
    }

    public void finalizarOrden(){

        try{

            clsBonifGlob clsBonG;
            clsDeGlob clsDeG;
            String s,ss;

            if (!hasProducts())  {
                mu.msgbox("No puede continuar, no ha vendido ninguno producto !");return;
            }

            try{

                //#EJC20210705: Agregué validación de propina por media_pago.
                gl.delivery = hasProductsDelivery();
                gl.EsNivelPrecioDelivery = (gl.delivery || gl.pickup);
                if (gl.EsVentaDelivery) gl.EsNivelPrecioDelivery=true;

                //#EJC202211232059: Limpiar tablas antes de procesar pago.
                db.execSQL("DELETE FROM T_PAGO");
                db.execSQL("DELETE FROM T_BONITEM WHERE PRODID='*'");

            } catch (Exception e){
                gridViewOpciones.setEnabled(true);
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                mu.msgbox("finishOrder: "+e.getMessage());
            }

            if (gl.codigo_cliente==0) {
                toast("Falta definir cliente "+gl.codigo_cliente);
                browse=8;
                startActivity(new Intent(this,Clientes.class));
                return;
            }

            gl.gstr="";
            browse=1;

            gl.bonprodid="*";
            gl.bonus.clear();
            gl.descglob=0;
            gl.descgtotal=0;
            if (gl.gNITCliente.isEmpty()) {
                gl.gNITCliente="CF";
            } else {
               if (gl.gNITCliente.length()<6) gl.gNITCliente="CF";
            }

            clsDeG=new clsDeGlob(this,tot);ss="";

            if (!app.usaFEL()) {
                if (clsDeG.tieneDesc()) {
                    gl.descglob=clsDeG.valor;
                    gl.descgtotal=clsDeG.vmonto;
                }
            }

            // Bonificacion

            gl.bonprodid="*";
            gl.bonus.clear();

            if (gl.dvbrowse!=0){
                if (tot<gl.dvdispventa){
                    mu.msgbox("No puede totalizar la factura, es menor al monto permitido para la nota de crédito: " + gl.dvdispventa);return;
                }
            }
            gl.brw=0;

            browse=0;

            gridViewOpciones.setEnabled(false);

            gl.mesero_precuenta=false;
            gl.descadd=0;
            Intent intent = new Intent(this,FacturaRes.class);
            startActivity(intent);

            if (gl.bonus.size()>0) {
                //Intent intent = new Intent(this,BonList.class);
                //startActivity(intent);
            }
        } catch (Exception e){
            gridViewOpciones.setEnabled(true);
            mu.msgbox("finishOrder: "+e.getMessage());
        }
    }

    public void cambiaPrecio() {
        if (uid.equalsIgnoreCase("0")) return;

        browse=11;
        startActivity(new Intent(this,ValidaSuper.class));
    }

    //endregion

    //region Barras

    private void addBarcode() {
        gl.barra=barcode;

        if (gl.rol==4) {
            toast("El mesero no puede realizar venta en esta pantalla");return;
        }

        if (barraProducto()) {
            txtBarra.setText("");return;
        } else {
            toastlong("¡La barra "+barcode+" no existe!");
            txtBarra.setText("");
        }
    }

    private void actualizaTotalesBarra() {
        Cursor dt;
        int ccant;
        double ppeso,pprecio;

        try {

            //sql="SELECT COUNT(BARRA),SUM(PESO),SUM(PRECIO) FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            //#CKFK 20190410 se modificó esta consulta para sumar la cantidad y no contar las barras
            sql="SELECT SUM(CANTIDAD),SUM(PESO),SUM(PRECIO) FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            dt=Con.OpenDT(sql);

            ccant=0;ppeso=0;pprecio=0;

            if (dt.getCount()>0) {
                dt.moveToFirst();

                ccant=dt.getInt(0);
                ppeso=dt.getDouble(1);
                pprecio=dt.getDouble(2);
                if (dt!=null) dt.close();
            }

            sql="UPDATE T_VENTA SET Cant="+ccant+",Peso="+ppeso+",Total="+pprecio+" WHERE PRODUCTO='"+prodid+"'";
            db.execSQL(sql);

            sql="DELETE FROM T_VENTA WHERE Cant=0";
            db.execSQL(sql);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean barraProducto() {
        Cursor dt;

        try {
/*
            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO INNER JOIN " +
                    "P_PRODPRECIO ON (P_STOCK.CODIGO=P_PRODPRECIO.CODIGO_PRODUCTO)  " +
                    "WHERE (P_STOCK.CANT > 0) AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"')) ";
            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA FROM P_PRODUCTO " +
                    "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M')) " +
                    "AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"'))  COLLATE NOCASE";
 */
            sql="";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA FROM P_PRODUCTO " +
                   "WHERE (P_PRODUCTO.ACTIVO = 1) " +
                   "AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"'))  COLLATE NOCASE";

            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                khand.clear(true);

                gl.gstr=dt.getString(0);gl.um="UN";
                gl.pprodname=dt.getString(1);
                if (dt!=null) dt.close();

                processItem(false);
                return true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void borraBarra() {
        clsBonif clsBonif;
        int bcant,bontotal,boncant,bfaltcant,bon;
        String bprod="";

        try {
            db.execSQL("DELETE FROM T_BARRA WHERE BARRA='"+gl.barra+"' AND CODIGO='"+prodid+"'");
            actualizaTotalesBarra();

            gl.bonbarprod=prodid;

            bcant=cantBolsa();
            boncant=cantBonif();
            bfaltcant=cantFalt();

            clsBonif = new clsBonif(this, prodid, bcant, 0);
            if (clsBonif.tieneBonif()) {
                bon=(int) clsBonif.items.get(0).valor;
                bprod=clsBonif.items.get(0).lista;
                gl.bonbarid=clsBonif.items.get(0).lista;
            } else {
                bon=0;gl.bonbarid="";
            }

            bontotal=boncant+bfaltcant;

            //toast("Bolsas : "+bcant+" bon : "+bon+"  / "+bontotal);
            if (bon<bontotal) {
                removerBonif(bprod,(bontotal-bon));
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private int cantBolsa() {
        try {
            sql="SELECT BARRA FROM T_BARRA WHERE CODIGO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);

            int i=dt.getCount();
            if (dt!=null) dt.close();
            return i;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return 0;
        }
    }

    private int cantBonif() {
        try {
            sql="SELECT BARRA FROM T_BARRA_BONIF WHERE PRODUCTO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);
            int i=dt.getCount();
            if (dt!=null) dt.close();
            return i;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return 0;
        }
    }

    private int cantFalt() {
        try {
            sql="SELECT PRODID FROM T_BONIFFALT WHERE PRODUCTO='"+prodid+"'";
            Cursor dt=Con.OpenDT(sql);
            int i=dt.getCount();
            if (dt!=null) dt.close();
            return i;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return 0;
        }
    }

    private void removerBonif(String bprod,int bcant) {
        Cursor dt;
        String barra,sbarra="";
        int bc=0;

        try {
            for (int i = 1; i == bcant; i++) {

                sql = "SELECT CANT FROM T_BONIFFALT WHERE (PRODID='"+prodid+"') ";
                dt = Con.OpenDT(sql);

                if (dt.getCount() > 0) {
                    dt.moveToFirst();

                    sql="UPDATE T_BONIFFALT SET CANT=CANT-1 WHERE (PRODID='"+prodid+"') ";
                    db.execSQL(sql);

                    sql="DELETE FROM T_BONIFFALT WHERE CANT=0";
                    db.execSQL(sql);

                } else {

                    sql = "SELECT BARRA FROM T_BARRA_BONIF WHERE (PRODUCTO='"+prodid+"') ";
                    dt = Con.OpenDT(sql);

                    if (dt.getCount() > 0) {
                        dt.moveToLast();
                        barra=dt.getString(0);sbarra+=barra+"\n";bc++;

                        sql = "DELETE FROM T_BARRA_BONIF WHERE (PRODUCTO='"+prodid+"') AND (BARRA='"+barra+"') ";
                        db.execSQL(sql);
                    }
                }

                if (dt!=null) dt.close();
            }

            reportBonif();

            if (bc>0) msgbox("Las barra devueltas : \n"+sbarra);

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    private void reportBonif() {
        int bont,bon,bonf;

        bon=cantBonif();
        bonf=cantFalt();
        bont=bon+bonf;

        if (bonf==0) {
            toast("Bonificación actual : "+bon);
        } else {
            toast("Bonificación actual : "+bon+" / "+bont);
        }

    }

    //endregion

    //region No atencion

    private void setNoAtt(String scna){
        int cna;

        try {
            cna=Integer.parseInt(scna);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return;
        }

        //int cliid = gl.cliente; #CKFK 2020515

        saveAtten(""+cna);
    }

    private void saveAtten(String codnoate) {
        long ti,tf,td;

        ti=gl.atentini;tf=du.getActDateTime();
        td=du.timeDiff(tf,ti);if (td<1) td=1;

        try {
            ins.init("D_ATENCION");

            ins.add("RUTA",gl.ruta);
            ins.add("FECHA",ti);
            ins.add("HORALLEG",du.shora(ti)+":00");
            ins.add("HORASAL",du.shora(tf)+":00");
            ins.add("TIEMPO",td);

            ins.add("VENDEDOR",gl.vend);
            ins.add("CLIENTE",gl.cliente);
            ins.add("DIAACT",du.dayofweek(ti));
            ins.add("DIA",du.dayofweek(ti));
            ins.add("DIAFLAG","S");

            ins.add("SECUENCIA",1);
            ins.add("SECUENACT",1);
            ins.add("CODATEN",codnoate);
            ins.add("KILOMET",0);

            ins.add("VALORVENTA",0);
            ins.add("VALORNEXT",0);
            ins.add("CLIPORDIA",clidia);
            ins.add("CODOPER","X");
            ins.add("COREL","");

            if (gl.gpspass) ins.add("SCANNED","G");else ins.add("SCANNED",gl.escaneo);
            ins.add("STATCOM","N");
            ins.add("LLEGO_COMPETENCIA_ANTES",0);

            ins.add("CoorX",gl.gpspx);
            ins.add("CoorY",gl.gpspy);
            ins.add("CliCoorX",gl.gpscpx);
            ins.add("CliCoorY",gl.gpscpy);
            ins.add("Dist",gl.gpscdist);

            db.execSQL(ins.sql());
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            //mu.msgbox("Error : " + e.getMessage());
        }

    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.cliposflag=true;
                    if (gl.dvbrowse!=0) gl.dvbrowse =0;
                    browse=0;
                    gl.forcedclose=true;
                    doExit();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskDel(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    delItem();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void msgAskBarra(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    borraBarra();
                 }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }


    }

    private void msgAskAdd(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskLimit(String msg,boolean updateitem) {
        final boolean updatem=updateitem;
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    processCant(updatem);
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void valorDescuento() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Porcentaje descuento");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    double val=Double.parseDouble(s);
                    if (val<0) throw new Exception();

                    gl.promdesc=val;
                    updDesc();
                } catch (Exception e) {
                    mu.msgbox("Porcentaje incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void valorDescuentoMonto() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Monto descuento");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    double val=Double.parseDouble(s);
                    if (val<0) throw new Exception();

                    gl.promdesc=val;
                    updDescMonto();
                } catch (Exception e) {
                    mu.msgbox("Monto incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void ingresoNota() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nota");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setText(""+vitem.nota);
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    actualizaNota(s);
                } catch (Exception e) {
                    actualizaNota("");
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void showVentaItemMenu(int mmodo) {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Venta.this,"Venta");

            listdlg.add("Cambiar cantidad");
            listdlg.add("Nota");
            listdlg.add("Ingredientes adicionales");
            if (gl.idmodgr>0) listdlg.add("Modificadores");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                if (mmodo==0) {
                                    browse=6;
                                    startActivity(new Intent(Venta.this,VentaEdit.class));
                                } else {
                                    startActivity(new Intent(Venta.this,ProdMenu.class));
                                }
                                break;
                            case 1:
                                ingresoNota();break;
                            case 2:
                                //Ingredientes();
                                break;
                            case 3:
                                startActivity(new Intent(Venta.this,ModifVenta.class));break;
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

    //endregion

    //region Menu

    private void listFamily() {

        clsP_lineaObj P_lineaObj=new clsP_lineaObj(this,Con,db);
        clsClasses.clsMenu item;

        try {

            fitems.clear();
            P_lineaObj.fill("WHERE (Activo=1) ORDER BY NOMBRE ");

            for (int i = 0; i <P_lineaObj.count; i++) {
                item=clsCls.new clsMenu();
                item.Cod=P_lineaObj.items.get(i).codigo+"";
                item.Name=P_lineaObj.items.get(i).nombre;
                item.icod=P_lineaObj.items.get(i).codigo_linea;
                fitems.add(item);
            }

            if (imgflag) {
                adapterf=new ListAdaptGridFam(this,fitems,imgfold,horiz);
                grdfam.setAdapter(adapterf);
            } else {
                adapterfl=new ListAdaptGridFamList(this,fitems,imgfold,horiz);
                grdfam.setAdapter(adapterfl);
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

    }

    private void listProduct() {
        Cursor dt;
        clsClasses.clsMenu item;
        ArrayList<String> pcodes = new ArrayList<String>();
        String pcode;
        double pprec;
        int pact;

        try {
            pitems.clear();pcodes.clear();

            /*
            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_PRODPRECIO.UNIDADMEDIDA, " +
                    "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO  " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO INNER JOIN " +
                    "P_PRODPRECIO ON P_STOCK.CODIGO=P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE (P_PRODUCTO.ACTIVO=1) AND (P_PRODUCTO.CODIGO_TIPO ='P')";
            if (famid !=-1) {
                if (famid!=0) sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }

            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA, " +
                    "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO " +
                    "FROM P_PRODUCTO  INNER JOIN " +
                    "P_PRODPRECIO ON P_PRODUCTO.CODIGO_PRODUCTO = P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M') OR (P_PRODUCTO.CODIGO_TIPO ='PB')) " +
                    "AND (P_PRODUCTO.ACTIVO=1)";
            if (famid !=-1) {
                if (famid!=0)
                    sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }
            */

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA, " +
                    "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO " +
                    "FROM P_PRODUCTO  INNER JOIN " +
                    "P_PRODPRECIO ON P_PRODUCTO.CODIGO_PRODUCTO = P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE (P_PRODUCTO.ACTIVO=1)";
            if (famid !=-1) {
                if (famid!=0)
                    sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }
            sql += "ORDER BY P_PRODUCTO.DESCCORTA";

            dt=Con.OpenDT(sql);

            if (dt.getCount()==0){
                msgbox("¡No está definido el precio o ningúno artículo de la familia tiene existencia disponible!");return;
            }

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                pcode=dt.getString(0);
                if (!pcodes.contains(pcode)) {
                      if (dt.getInt(3)==1) {
                        item=clsCls.new clsMenu();
                        item.Cod=dt.getString(0);
                        item.icod=dt.getInt(4);
                          //pprec=prodPrecioBase(item.icod);

                        pprec=prodPrecioBaseImp(item.icod);
                        pprec=mu.round(pprec,2);

                        //item.Name=dt.getString(1)+" \n[ "+gl.peMon+pprec+" ]";
                          item.Name=dt.getString(1)+"  [ "+gl.peMon+mu.frmcur_sm(pprec)+" ]";
                        if (pprec>0) {
                            pitems.add(item);
                            pcodes.add(pcode);
                        }
                    }
                }

                dt.moveToNext();
            }
            if (dt!=null) dt.close();
        } catch (Exception e) 		{
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (imgflag) {
            adapterp=new ListAdaptGridProd(this,pitems,imgfold,horiz);
            grdprod.setAdapter(adapterp);
        } else {
            adapterpl=new ListAdaptGridProdList(this,pitems,imgfold,horiz);
            grdprod.setAdapter(adapterpl);
        }

    }

    private void menuItems() {

        clsClasses.clsMenu item;

        menuTools();

        try {
            mmitems.clear();

            try {

                if (meseros && gl.peRest) {
                    item = clsCls.new clsMenu();
                    item.ID=63;item.Name="Mesero";item.Icon=63;
                    //if (!gl.pelCajaRecep)
                    mmitems.add(item);
                }

                if (pedidos | gl.peDomEntEnvio) {
                    item = clsCls.new clsMenu();
                    item.ID=61;item.Name="Para llevar";item.Icon=61;
                    mmitems.add(item);
                }

                if (gl.peBotComanda) {
                    item = clsCls.new clsMenu();
                    item.ID=62;item.Name="Comanda";item.Icon=62;
                    mmitems.add(item);
                }

                if (gl.pelCaja && gl.peRest) {
                    item = clsCls.new clsMenu();
                    item.ID=65;item.Name="Caja";item.Icon=65;
                    if (gl.rol!=4) mmitems.add(item);
                }

                item = clsCls.new clsMenu();
                item.ID=52;item.Name="Cliente";item.Icon=52;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=71;item.Name="Descuento";item.Icon=71;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=50;item.Name="Buscar ";item.Icon=50;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=54;item.Name="Borrar linea ";item.Icon=54;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=55;item.Name="Borrar todo ";item.Icon=55;
                mmitems.add(item);

                if (gl.peRest | gl.pelOrdenComanda) {
                    item = clsCls.new clsMenu();
                    item.ID=70;item.Name="Mensaje";item.Icon=70;
                    mmitems.add(item);
                }

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuItems(int menuid) {
        try {

            switch (menuid) {
                case 50:
                    if (gl.rol==4) {
                        toast("El mesero no puede realizar venta en esta pantalla");return;
                    }
                    gl.gstr = "";
                    browse = 1;
                    gl.prodtipo = 1;
                    startActivity(new Intent(this, Producto.class));
                    break;
                case 51:
                    if (gl.rol==4) {
                        toast("El mesero no puede realizar venta en esta pantalla");return;
                    }
                    if (khand.isValid) {
                        barcode = khand.val;
                        addBarcode();
                    }
                    break;
                case 52:
                    if (!gl.exitflag) {
                        browse = 8;
                        gl.climode = false;

                        if (usarbio) {
                            startActivity(new Intent(Venta.this, Clientes.class));
                        } else {
                            if (!gl.forcedclose) {
                                gl.modo_domicilio=false;
                                startActivity(new Intent(Venta.this, CliPos.class));
                            }
                        }
                    }
                    break;
                case 53:
                    break;
                case 54:
                    if (!gl.ventalock) borraLinea();else toast("No se puede modificar el órden");
                    break;
                case 55:
                    if (!gl.ventalock) borraTodo();else toast("No se puede modificar el órden");
                    break;
                case 56:
                    showMenuSwitch();
                    break;
                case 61:
                    gl.modo_domicilio=true;
                    gl.dom_total=tot;
                    startActivity(new Intent(Venta.this, CliPos.class));
                    break;
                case 62:
                    if (hasProducts()) inputMesa(); else toastcent("La órden está vacia");
                    break;
                case 63:
                    if (gl.ingreso_mesero) {
                        gl.cerrarmesero=false;
                        meseroAutoLogin();
                    } else {
                        browse=12;
                        gl.cerrarmesero=false;gl.cierra_clave=false;gl.modoclave=0;
                        startActivity(new Intent(this,ValidaClave.class));
                    }

                    break;
                case 65:
                    if (gl.rol==4) {
                        msgbox("El mesero no puede entrar al menu caja");return;
                    }
                    browse=10;gl.modoclave=1;
                    startActivity(new Intent(this,ValidaClave.class));
                    break;
                case 70:
                    startActivity(new Intent(this,ImpresMsg.class));
                    break;
                case 71:
                    cambiaPrecio();break;
                case 72:
                    msgbox("Pendiente implementacion");break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void menuTools() {

        clsClasses.clsMenu item;

        mitems.clear();

        try {

            item = clsCls.new clsMenu();
            item.ID=1;item.Name="Pago";item.Icon=58;
            mitems.add(item);

            if (app.usaFEL()) {
                int pendfel=pendienteFEL();

                if (pendfel>0) {
                    item = clsCls.new clsMenu();
                    item.ID=15;item.Name="FEL";item.Icon=15;item.cant=pendfel;
                    mitems.add(item);
                }
            }

            if (pedidos | domenvio) {
                item = clsCls.new clsMenu();
                item.ID=16;item.Name="Para llevar";item.Icon=16;
                item.cant=pedidoscant;
                //if (!gl.peDomEntEnvio) mitems.add(item);
                mitems.add(item);
            }

            item = clsCls.new clsMenu();
            item.ID=3;item.Name="Reimpresión";item.Icon=3;
            mitems.add(item);

            if (gl.rol>1) {
                item = clsCls.new clsMenu();
                item.ID=4;item.Name="Anulación";item.Icon=4;
                mitems.add(item);
            }

            item = clsCls.new clsMenu();
            item.ID=14;item.Name="Actualizar";item.Icon=14;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=26;item.Name="Reportes";item.Icon=60;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=25;item.Name="Cierra Caja";item.Icon=59;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=24;item.Name="Salir";item.Icon=57;
            mitems.add(item);

            /*
            item = clsCls.new clsMenu();
            item.ID=7;item.Name="Existencias";item.Icon=7;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=101;item.Name="Baktún";item.Icon=101;
            mitems.add(item);
            */

            adaptergrid=new ListAdaptMenuVenta(this, mitems);
            gridViewOpciones.setAdapter(adaptergrid);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuTools(int menuid) {
        try {
            switch (menuid) {
                case 1:
                    if (!validaMinimoCF()) return;
                    if (!disponibleCorel()) return;
                    finalizarOrden();break;
                case 3:
                    menuImprDoc(3);break;
                case 4:
                    validaSupervisor();break;
                    //gl.tipo=3;menuAnulDoc();break;
                case 14:
                    showQuickRecep();break;
                case 15:
                    msgAskFEL("Certificar ("+pendienteFEL()+") factura(s) pendiente(s)");break;
                case 25:
                    msgAskCierreCaja("¿Realizar cierre de caja?");break;
                case 26:
                    showReportMenu();break;
                case 16:
                    menuPedidos();break;
                case 24:
                    exitBtn();
                    break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void validaSupervisor() {
        clsClasses.clsVendedores item;

        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            app.fillSuper(VendedoresObj);

            if (VendedoresObj.count==0) {
                msgbox("No está definido ningún supervisor");return;
            }

            extListPassDlg listdlg = new extListPassDlg();
            listdlg.buildDialog(Venta.this,"Autorización","Salir");

            for (int i = 0; i <VendedoresObj.count; i++) {
                item=VendedoresObj.items.get(i);
                listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
            }

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.onEnterClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listdlg.getInput().isEmpty()) return;

                    if (listdlg.validPassword()) {
                        gl.tipo=3;menuAnulDoc();
                        listdlg.dismiss();
                    } else {
                        toast("Contraseña incorrecta");
                    }
                }
            });

            listdlg.setWidth(350);
            listdlg.setLines(4);

            listdlg.show();

        } catch (Exception e) {
            String ss=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void cierreCaja(){
        try{
            if (ss.equalsIgnoreCase("Cierre de Caja")) gl.cajaid=3;

            gl.titReport = ss;

            if(valida()){

                if(gl.cajaid!=2){

                    gl.inicio_caja_correcto =false;

                    browse=1;

                    startActivity(new Intent(Venta.this,Caja.class));
                    finish();

                }

            } else {
                String txt="";

                if(gl.cajaid==3) txt = "La caja está cerrada, si desea iniciar operaciones o realizar pagos debe realizar el inicio de caja.";
                msgAskValid(txt);

            }

        }catch (Exception ex){

        }
    }

    public void showReportMenu() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Venta.this,"Reportes");

            listdlg.add("Reporte de Documentos por Día");
            listdlg.add("Reporte Venta por Día");
            listdlg.add("Reporte Venta por Producto");
            listdlg.add("Reporte por Forma de Pago");
            listdlg.add("Reporte por Familia");
            listdlg.add("Reporte Ventas por Vendedor");
            listdlg.add("Reporte de Ventas por Cliente");
            listdlg.add("Margen y Beneficio por Productos");
            listdlg.add("Margen y Beneficio por Familia");
            listdlg.add("Cierre X");
            listdlg.add("Cierre Z");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        ss=listdlg.getText(position);

                        if (ss.equalsIgnoreCase("Reporte de Documentos por Día")) gl.reportid=1;
                        if (ss.equalsIgnoreCase("Reporte Venta por Día")) gl.reportid=2;
                        if (ss.equalsIgnoreCase("Reporte Venta por Producto")) gl.reportid=3;
                        if (ss.equalsIgnoreCase("Reporte por Forma de Pago")) gl.reportid=4;
                        if (ss.equalsIgnoreCase("Reporte por Familia")) gl.reportid=5;
                        if (ss.equalsIgnoreCase("Reporte Ventas por Vendedor")) gl.reportid=6;
                        if (ss.equalsIgnoreCase("Margen y Beneficio por Productos")) gl.reportid=7;
                        if (ss.equalsIgnoreCase("Margen y Beneficio por Familia")) gl.reportid=8;
                        if (ss.equalsIgnoreCase("Cierre X")) gl.reportid=9;
                        if (ss.equalsIgnoreCase("Cierre Z")) gl.reportid=10;
                        if (ss.equalsIgnoreCase("Reporte de Ventas por Cliente")) gl.reportid=11;

                        gl.titReport = ss;

                        if (gl.reportid == 9 || gl.reportid == 10) {
                            startActivity(new Intent(Venta.this,CierreX.class));
                        }else{
                            startActivity(new Intent(Venta.this,Reportes.class));
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

    public boolean valida(){

        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);
            clsP_cajahoraObj caja_hora = new clsP_cajahoraObj(this,Con,db);

            caja.fill();

            //#EJC202211240844: Obtener la fecha con hora a partir de la que se empezó a facturar.
            caja_hora.fill("order by corel desc");

           if(gl.cajaid==3){

                if(caja.count==0) {
                    if(gl.cajaid==3) gl.cajaid=0;
                    return false;
                }

                if(caja.last().estado==1){
                    return false;
                }else if(gl.cajaid==5) {

                    if (gl.lastDate!=0){

                        if(caja.last().fecha!=gl.lastDate){

                            gl.validDate=true;

                            if (caja_hora!=null){
                                if (caja_hora.count>1){
                                    gl.lastDate=caja_hora.items.get(1).fechaini;
                                    gl.cajaid=6;
                                    return false;
                                }
                            }
                            gl.lastDate=caja.last().fecha;
                            gl.cajaid=6; return false;
                        }
                    }
                }

            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            msgbox("Ocurrió error (valida) "+e);
            return false;
        }

        return true;
    }

    private void msgAskValid(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskCierreCaja(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ss="Cierre de Caja";
                cierreCaja();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    public void menuImprDoc(int doctipo) {
        try{
            gl.tipo=doctipo;

            Intent intent = new Intent(this,Reimpresion.class);
            startActivity(intent);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void menuPedidos() {
        try{
            gl.closePedido=false;
            browse=9;
            if (pedidos) {
                startActivity(new Intent(this,Pedidos.class));
            } else {
                if (domenvio) {
                    startActivity(new Intent(this, PedidosEnv.class));
                }
            }
        } catch (Exception e){
            //addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void showVoidMenuTodo() {
        /*
        try{
            final AlertDialog Dialog;
            final String[] xselitems = {(gl.peMFact?"Factura":"Ticket"),"Deposito","Recarga","Devolución a bodega"};

            ExDialog menudlg = new ExDialog(this);

            menudlg.setItems(selitems ,	new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            gl.tipo=3;break;
                        case 1:
                            gl.tipo=2;break;
                        case 2:
                            gl.tipo=4;break;
                        case 3:
                            gl.tipo=5;break;
                    }

                    menuAnulDoc();
                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

         */
    }

    public void showQuickRecep() {
       ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Actualizar parametros de venta?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gl.findiaactivo=false;
                    gl.tipo = 0;
                    gl.autocom = 0;
                    gl.modoadmin = false;
                    gl.comquickrec = true;
                    startActivity(new Intent(Venta.this, WSRec.class));
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

    private void menuAnulDoc() {
        try{
            Intent intent = new Intent(this,Anulacion.class);
            startActivity(intent);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void borraLinea() {
        if (seluid.isEmpty()) return;

        try {
            sql="DELETE  FROM T_VENTA WHERE EMPRESA='"+seluid+"'";
            db.execSQL(sql);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar toda la venta");
    }

    private void showMenuSwitch() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Venta.this,"Ventas");

            listdlg.add("Iniciar nueva venta");
            listdlg.add("Cambiar venta");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                ;break;
                            case 1:
                                ;break;
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

    private void exitBtn() {
        Cursor dt;

        try {
            sql="SELECT * FROM T_VENTA";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                msgAskExit("Regresar al menú principal sin terminar la venta");
            } else {
                finish();
            }

        } catch (Exception e) {
        }
    }

    //endregion

    //region Comanda

    private void imprimeComanda() {
        clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
        clsT_comboObj T_comboObj=new clsT_comboObj(this,Con,db);
        clsClasses.clsT_venta venta;
        clsClasses.clsT_combo combo;

        int prid,idcombo,ln;
        String csi;

        try {
            rep.clear();
            rep.empty();
            rep.empty();
            //rep.empty();
            //rep.empty();
            //rep.empty();
            //rep.empty();
            rep.add("ORDEN MESA : "+mesa);
            //rep.empty();
            rep.add("Hora       : "+du.shora(du.getActDateTime()));
            rep.line();
            //rep.empty();

            T_ventaObj.fill();

            for (int i = 0; i <T_ventaObj.count; i++) {

                venta=T_ventaObj.items.get(i);
                prid=app.codigoProducto(venta.producto);
                s=mu.frmdecno(venta.cant)+"  "+getProd(prid);
                rep.add(s);

                if (app.prodTipo(prid).equalsIgnoreCase("M")) {
                    T_comboObj.fill("WHERE (IdCombo=" + venta.val4+") AND (IdSeleccion<>0)");

                    for (int j = 0; j <T_comboObj.count; j++) {
                        if (j==0) rep.line();
                        csi=getProd(T_comboObj.items.get(j).idseleccion);
                        if (!csi.equalsIgnoreCase("0")) s=" -  "+csi;
                        rep.add(s);
                    }
                    rep.line();
                }
            }

            rep.empty();
            /*
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();

             */

            /*
            ln=rep.items.size();
            if (ln<20) {
                for (int i = 0; i <20-ln; i++) {
                    rep.empty();
                }
            }
            */

            rep.save();

            if (gl.emp==47) {
                app.doPrint(1);
            } else {
                gl.QRCodeStr="";
                app.doPrint(2,1);
            }

            //} else {
            //    app.doPrint(1);
            //}
         } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private String getProd(int prodid) {
        try {
            P_productoObj.fill();
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) return P_productoObj.items.get(i).desclarga;
            }
        } catch (Exception e) {

        }
        return ""+prodid;
    }

    private void ejecutaImpresionComanda() {
        try {
            app.print3nstarw();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Pedidos

    private void crearPedido() {

        try {

            peditems.clear();
            if (crearPedidoEncabezado()) crearPedidoDetalle();

        } catch (Exception e) {
            msgbox(e.getMessage());return;
        }

        try {

            db.beginTransaction();

            for (int i = 0; i <peditems.size(); i++) {
                db.execSQL(peditems.get(i));
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            menuPedidos();

        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());
        }
    }
    
    private boolean crearPedidoEncabezado() {

        clsD_pedidoObj D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        clsClasses.clsD_pedido item = clsCls.new clsD_pedido();

        pedcorel=mu.getCorelBase();

        item.empresa=gl.emp;
        item.corel=pedcorel;
        item.fecha_sistema=du.getActDateTime();
        item.fecha_pedido=du.getActDateTime();
        item.fecha_recepcion_suc=du.getActDateTime();
        item.fecha_salida_suc=0;
        item.fecha_entrega=0;
        item.codigo_cliente=gl.codigo_cliente;
        item.codigo_direccion=0;
        item.codigo_sucursal=gl.tienda;
        item.total=tot;
        item.codigo_estado=0;
        item.codigo_usuario_creo=gl.codigo_vendedor;
        item.codigo_usuario_proceso=gl.codigo_vendedor;
        item.codigo_usuario_entrego=0;
        item.anulado=0;

        String ss=D_pedidoObj.addItemSql(item);
        peditems.add(ss);

        return true;
    }

    private void  crearPedidoDetalle() {
        clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
        clsD_pedidodObj D_pedidodObj=new clsD_pedidodObj(this,Con,db);
        clsD_pedidocomboObj D_pedidocomboObj=new clsD_pedidocomboObj(this,Con,db);
        clsT_comboObj T_comboObj=new clsT_comboObj(this,Con,db);
        clsClasses.clsT_venta venta;
        clsClasses.clsD_pedidod item;
        clsClasses.clsD_pedidocombo combo;
        int corid,comboid;
        String ss,pt,comid;

        T_ventaObj.fill();
        corid=D_pedidodObj.newID("SELECT MAX(COREL_DET) FROM D_pedidod");
        comboid=D_pedidocomboObj.newID("SELECT MAX(COREL_COMBO) FROM D_pedidocombo");

        for (int i = 0; i <T_ventaObj.count; i++) {
            venta=T_ventaObj.items.get(i);

            item = clsCls.new clsD_pedidod();

            item.corel=pedcorel;
            item.corel_det=corid;
            item.codigo_producto=app.codigoProducto(venta.producto);
            item.umventa=venta.um;
            item.cant=venta.cant;
            item.total=venta.total;
            item.nota="";
            item.codigo_tipo_producto=app.prodTipo(item.codigo_producto);pt=item.codigo_tipo_producto;

            ss=D_pedidodObj.addItemSql(item);
            peditems.add(ss);

            if (pt.equalsIgnoreCase("M")) {
                comid=venta.empresa;
                T_comboObj.fill("WHERE (IDCOMBO="+comid+")");

                for (int j = 0; j <T_comboObj.count; j++) {

                    combo = clsCls.new clsD_pedidocombo();

                    combo.corel_det=corid;
                    combo.corel_combo=comboid;comboid++;
                    combo.seleccion=corid;
                    combo.codigo_producto=T_comboObj.items.get(j).idseleccion;
                    combo.cant=T_comboObj.items.get(j).cant;
                    combo.nota="";

                    ss=D_pedidocomboObj.addItemSql(combo);
                    peditems.add(ss);

                }
            }

            corid++;
        }
    }

    private void estadoPedidos() {
        long tact,tlim,tbot;

        tact=du.getActDateTime();tlim=tact+100;tbot=du.getActDate();

        try {
            //D_pedidoObj.fill("WHERE (ANULADO=0) AND (CODIGO_USUARIO_CREO=0) ");
            String fsql="WHERE (ANULADO=0) AND (FECHA_ENTREGA=0) AND (FECHA_PEDIDO<="+tlim+") AND (FECHA_PEDIDO>="+tbot+")  AND (FECHA_SALIDA_SUC=0) ";
            D_pedidoObj.fill(fsql);
            int peds=D_pedidoObj.count;

            for (int i = 0; i <mitems.size(); i++) {
                if (mitems.get(i).ID==16) {
                    mitems.get(i).cant=peds;
                    adaptergrid.notifyDataSetChanged();
                    break;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion
    
    //region Aux

    private void setControls(){

        try{
            listView = (ListView) findViewById(R.id.listView1);
            gridViewOpciones = (GridView) findViewById(R.id.gridView2);
            gridViewOpciones.setEnabled(true);
            grdfam = (GridView) findViewById(R.id.grdFam);
            grdprod = (GridView) findViewById(R.id.grdProd);
            grdbtn = (GridView) findViewById(R.id.grdbtn);

            lblTot= (TextView) findViewById(R.id.lblTot);
            lblDesc= (TextView) findViewById(R.id.textView115);lblDesc.setText( "Desc : "+mu.frmcur(0));
            lblStot= (TextView) findViewById(R.id.textView103); lblStot.setText("Subt : "+mu.frmcur(0));
            lblTit= (TextView) findViewById(R.id.lblTit);
            lblAlm= (TextView) findViewById(R.id.lblTit2);
            lblVend= (TextView) findViewById(R.id.lblTit4);
            lblCambiarNivelPrecio = (TextView) findViewById(R.id.lblTit3);
            lblPokl= (TextView) findViewById(R.id.lblTit5);

            lblCant= (TextView) findViewById(R.id.lblCant);lblCant.setText("");
            lblBarra= (TextView) findViewById(R.id.textView122);lblBarra.setText("");
            lblKeyDP=(TextView) findViewById(R.id.textView110);
            lblDir=(TextView) findViewById(R.id.lblDir);

            imgroad= (ImageView) findViewById(R.id.imgRoadTit);
            imgscan= (ImageView) findViewById(R.id.imageView13);
            imgllevar= (ImageView) findViewById(R.id.imageView110);

            txtBarra=(EditText) findViewById(R.id.editText10);

            relScan= (RelativeLayout) findViewById(R.id.relScan);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setVisual() {
        if (imgflag) {
            if (horiz) {
                grdfam.setNumColumns(3);
                grdprod.setNumColumns(3);
            } else {
                grdfam.setNumColumns(1);
                grdprod.setNumColumns(3);
            }
        } else {
            grdfam.setNumColumns(2);
            grdprod.setNumColumns(1);
        }

        listFamily();
    }

    private void initValues(){
        Cursor DT;

        app.parametrosExtra();
        usarbio=gl.peMMod.equalsIgnoreCase("1");

        tiposcan="*";

        lblTit.setText(gl.cajanom);
        lblPokl.setText(gl.vendnom);

        try {
            sql="SELECT TIPO_HH FROM P_ARCHIVOCONF WHERE RUTA='"+gl.ruta+"'";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            tiposcan=DT.getString(0);
            if (DT!=null) DT.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            tiposcan="*";
        }

        usarscan=false;softscanexist=false;
        if (!mu.emptystr(tiposcan)) {
            if (tiposcan.equalsIgnoreCase("SOFTWARE")) {
                softscanexist=detectBarcodeScanner();
                usarscan=true;
            }
            if (!tiposcan.equalsIgnoreCase("SIN ESCANER")) usarscan=true;
        }

        if (usarscan) {
            imgscan.setVisibility(View.VISIBLE);
        } else {
            imgscan.setVisibility(View.INVISIBLE);
        }

        if (gl.codigo_pais.equalsIgnoreCase("HN")) {
            sinimp = true;
        } else if (gl.codigo_pais.equalsIgnoreCase("SV")) {
            sinimp = true;
        } else {
            sinimp=false;
        }

		/*
		contrib=gl.contrib;
		if (contrib.equalsIgnoreCase("C")) sinimp=true;
		if (contrib.equalsIgnoreCase("F")) sinimp=false;
		*/

        gl.sinimp=sinimp;

        try {
            sql="DELETE FROM T_VENTA";
            db.execSQL(sql);

            sql="DELETE FROM T_COMBO";
            db.execSQL(sql);

            sql="DELETE FROM T_ORDEN WHERE COREL='VENTA'";
            db.execSQL(sql);

            sql="DELETE FROM T_ORDENCOMBO WHERE COREL='VENTA'";
            db.execSQL(sql);

            sql="DELETE FROM T_ORDENCOMBOAD WHERE COREL='VENTA'";
            db.execSQL(sql);

            sql="DELETE FROM T_ORDENCOMBODET WHERE COREL='VENTA'";
            db.execSQL(sql);

            sql="DELETE FROM T_ORDENCOMBOPRECIO WHERE COREL='VENTA'";
            db.execSQL(sql);

            sql="DELETE FROM T_BARRA";
            db.execSQL(sql);

            //sql="DELETE FROM T_BARRA_BONIF";
            //db.execSQL(sql);

            sql="DELETE FROM T_BONIFFALT";
            db.execSQL(sql);

            sql="DELETE FROM T_PRODMENU";
            db.execSQL(sql);

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_PAGO";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_BONIFFALT";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        try {
            sql="DELETE FROM T_BONITEM";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        gl.ref1="";lblAlm.setText("");
        gl.ref2="";
        gl.ref3="";

        gl.nit_tipo="N";

        //#CKFK 20210706
        gl.pickup=false;
        gl.delivery=false;

        numeroOrden();

        clsDescFiltro clsDFilt=new clsDescFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        clsBonFiltro clsBFilt=new clsBonFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        imgfold= Environment.getExternalStorageDirectory()+ "/mPosFotos/";

        dweek=mu.dayofweek();

        lblTot.setText("Total : "+mu.frmcur(0));
        lblVend.setText("");

        khand.clear(true);khand.enable();

        uid="0";
    }

    private boolean hasProducts(){
        Cursor dt;

        try {

            sql="SELECT PRODUCTO FROM T_VENTA";
            dt=Con.OpenDT(sql);

            int i=dt.getCount();
            if (dt!=null) dt.close();

            return i>0;

         } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }

    private boolean hasProductsDelivery(){
        Cursor dt;

        try {

            sql="SELECT PRODUCTO FROM T_VENTA T INNER JOIN P_PRODUCTO P ON T.PRODUCTO = P.CODIGO WHERE P.CBCONV =1 ";
            dt=Con.OpenDT(sql);
            int CantRegistros=0;

            if(dt!=null){
                CantRegistros=dt.getCount();
                if (dt!=null) dt.close();
            }

            return CantRegistros>0;

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }

    private boolean getLineaProducto() {
        Cursor dt;
        try {
            sql="SELECT LINEA FROM P_PRODUCTO WHERE CODIGO_PRODUCTO = '"+cod_prod+"'";
            dt=Con.OpenDT(sql);

            if(dt!=null){
                lineaId = dt.getInt(0);

                if (dt!=null) dt.close();

                return  true;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        return  false;
    }

    private void showCredit(){
        try{
            if (hasCredits()){
                Intent intent = new Intent(this,Cobro.class);
                startActivity(intent);
            }
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private boolean hasCredits(){
         return false;
    }

    private void doExit(){
        try{
            gl.exitflag=true;
            gl.cliposflag=false;
            super.finish();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

    private String prodTipo(int prodid) {
        try {
            return app.prodTipo(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            throw e;
        }
    }

    private String prodTipo(String prodid) {
        try {
            return app.prodTipo(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            throw e;
        }
    }

    private boolean prodPorPeso(String prodid) {
        try {
            return app.ventaPeso(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean prodBarra(String prodid) {
        try {
            return app.prodBarra(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean prodRepesaje(String prodid) {
        try {
            return app.ventaRepesaje(prodid);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            return false;
        }
    }

    private boolean detectBarcodeScanner() {

        String packagename="com.google.zxing.client.android";
        PackageManager pm = this.getPackageManager();

        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            toast("Aplicacion ZXing Barcode Scanner no esta instalada");return false;
        }

    }

    private String prodPrecioBase(int prid) {
        Cursor DT;
        double pr,stot,pprec,tsimp;
        String sprec="";

        try {
            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO='"+prid+"') AND (NIVEL="+nivel+") ";
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

    private double prodPrecioBaseVal(int prid) {
        Cursor DT;
        double pr,stot,pprec,tsimp;
        String sprec="";

        try {
            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO='"+prid+"') AND (NIVEL="+nivel+") ";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            pr=DT.getDouble(0);

            if (DT!=null) DT.close();
        } catch (Exception e) {
            pr=0;
        }

        return pr;
    }

    private double prodPrecioBaseImp(int prid) {
        Cursor DT;
        double pr,prr,stot,pprec,tsimp;
        double imp1,imp2,imp3,vimp1,vimp2,vimp3,vimp;
        long prri;
        String sprec="";

        try {
            sql="SELECT PRECIO FROM P_PRODPRECIO WHERE (CODIGO_PRODUCTO="+prid+") AND (NIVEL="+nivel+") ";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            pr=DT.getDouble(0);

            sql="SELECT IMP1,IMP2,IMP3 FROM P_producto WHERE (CODIGO_PRODUCTO="+prid+")";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            imp1=DT.getDouble(0);imp2=DT.getDouble(1);imp3=DT.getDouble(2);

            if (imp1!=0) {
                try {
                    sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp1+")";
                    DT=Con.OpenDT(sql);
                    DT.moveToFirst();
                    vimp1=DT.getDouble(0);
                } catch (Exception e) {
                    vimp1=0;
                }
            } else vimp1=0;

            if (imp2!=0) {
                try {
                    sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp2+")";
                    DT=Con.OpenDT(sql);
                    DT.moveToFirst();
                    vimp2=DT.getDouble(0);
                } catch (Exception e) {
                    vimp2=0;
                }
           } else vimp2=0;

            if (imp3!=0) {
                try {
                    sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp3+")";
                    DT=Con.OpenDT(sql);
                    DT.moveToFirst();
                    vimp3=DT.getDouble(0);
                } catch (Exception e) {
                    vimp3=0;
                }
            } else vimp3=0;

            vimp=vimp1+vimp2+vimp3;
            vimp=vimp*0.01;
            pr=pr*(1+vimp);pr=pr+0.000001;
            //pr=mu.round2(pr);
            prr=pr*100;
            prri=Math.round(prr);
            prr=(double) prri;
            pr=prr*0.01;

            if (DT!=null) DT.close();
        } catch (Exception e) {
            pr=0;
        }

        return pr;
    }

    private int getDisp(String prid) {
        int cdisp, cstock, cbcombo;
        int vprodid=app.codigoProducto(prid);
        String vum = app.umVenta3(vprodid);

        cstock=cantStock(vprodid,vum);
        cbcombo=cantProdCombo(vprodid);
        cdisp=cstock-cbcombo;if (cdisp<0) cdisp=0;

        return cdisp;

    }

    private int cantStock(int prodid,String vum) {
        Cursor dt=null;

        try {

            sql="SELECT CANT FROM P_STOCK WHERE (CODIGO="+prodid+") AND (UNIDADMEDIDA='"+vum+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                int i=dt.getInt(0);
                if (dt!=null) dt.close();
                return i;
            }
            if (dt!=null) dt.close();
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (dt!=null) dt.close();
        }
    }

    private int cantProdCombo(int prodid) {
        Cursor dt=null;

        try {
            sql="SELECT SUM(CANT*UNID) FROM T_COMBO WHERE (IDSELECCION="+prodid+")";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                int i=dt.getInt(0);
                if (dt!=null) dt.close();
                return i;
            }
            if (dt!=null) dt.close();
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (dt!=null) dt.close();
        }
    }

    private void clearItem() {
        prodid="";gl.pprodname="";cant=0;prec=0;
    }

    private void openItem() {

        try {
            for (int i = 0; i <items.size(); i++) {

                if (items.get(i).Cod.equalsIgnoreCase(prodid)) {

                    clsVenta item = items.get(i);

                    prodid=item.Cod;//gl.prodmenu=prodid;
                    uprodid=prodid;
                    uid=item.emp;
                    gl.gstr=item.Nombre;
                    gl.retcant=(int) item.Cant;
                    gl.limcant=getDisp(prodid);
                    browse=6;

                    startActivity(new Intent(Venta.this,VentaEdit.class));
                 }
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }
    }

    private void cargaCliente() {

        Cursor DT;
        String ss;
        double lcred,cred,disp;

        //browse=0;

        gl.exitflag=false;
        if (!gl.scancliente.isEmpty())  gl.cliente=gl.scancliente;
        if (gl.cliente.isEmpty()) {
            toast("Cliente pendiente");return;
        }

        try {

            sql = "SELECT NOMBRE,LIMITECREDITO,NIT,DIRECCION,MEDIAPAGO, CODIGO_CLIENTE, EMAIL FROM P_CLIENTE " +
                      "WHERE CODIGO_CLIENTE="+gl.codigo_cliente;

            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            //gl.gNombreCliente =DT.getString(0);
            lcred=DT.getDouble(1);
            //gl.gNITCliente =DT.getString(2);
            //gl.gDirCliente =DT.getString(3);
            gl.media=DT.getInt(4);
            gl.gCorreoCliente =  DT.getString(6);

            if (lcred>0) {
                cred=totalCredito();
                if (cred>=0) {
                    disp=lcred-cred;if (disp<0) disp=0;
                } else disp=0;
             } else {
                disp=0;
            }

            gl.credito=disp;
            if (disp>0) ss=" [Cred: "+mu.frmcur(disp)+" ]";else ss="";

            lblVend.setText(DT.getString(0)+" "+gl.gNITCliente +" "+ss);

            if (DT!=null) DT.close();

        } catch (Exception e) {
            lblVend.setText("");
        }
    }

    private double totalCredito() {
        Cursor dt;
        double cred=0;
        long mm,yy;
        long ff;

        try {
            mm=du.getmonth(du.getActDate());
            yy=du.getyear(du.getActDate());
            ff=du.cfecha(yy,mm,1);

            sql="SELECT SUM(D_FACTURAP.VALOR) FROM D_FACTURAP  "+
               "INNER JOIN D_FACTURA ON D_FACTURAP.COREL=D_FACTURA.COREL "+
               "WHERE (D_FACTURA.FECHA>="+ff+") AND (D_FACTURA.ANULADO=0) " +
               "AND (D_FACTURA.CLIENTE='"+gl.codigo_cliente+"') AND (D_FACTURAP.TIPO='C')";
            dt = Con.OpenDT(sql);

            try {
                cred=dt.getDouble(0);
                if (dt!=null) dt.close();
            } catch (Exception e) {
                cred=0;
            }

        } catch (Exception e) {
            msgbox("No se puede determinar crédito dispónible, error : "+e.getMessage());return -1;
        }

        return cred;
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
            barcode=bc;
            addBarcode();
        }

        txtBarra.setText("");txtBarra.requestFocus();
        scanning=false;
    }

    private int pendienteFEL() {
        long flim,f1;

         try {
             flim=du.addDays(du.getActDate(),-5);
             //f1=du.getActDate();f1=du.cfecha(du.getyear(f1),du.getmonth(f1),1);
             //if (f1<flim) flim=f1;

             //sql="SELECT COREL FROM D_factura WHERE (FEELUUID=' ') AND (ANULADO=0) AND (FECHA>="+flim+")";
             //sql="SELECT COREL FROM D_factura WHERE (FEELUUID=' ') AND (ANULADO=0)";
             sql="select * from d_factura  where anulado=0 and " +
                 "(feelfechaprocesado=0 OR feeluuid=' ') and fecha>="+flim+";";

             Cursor DT=Con.OpenDT(sql);
             int i=DT.getCount();
             if (DT!=null) DT.close();
             return i;

        } catch (Exception e) {
            return 0;
        }
    }

    private void getURL() {

        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 6000;

        try {

            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {

                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 6000; else gl.timeout = Integer.valueOf(line);
                myReader.close();
            }

        } catch (Exception e) {}

    }

    private void setNivel() {

        nivel=gl.nivel;

        try {
            for (int i = 0; i <P_nivelprecioObj.count; i++) {
                if (P_nivelprecioObj.items.get(i).codigo==gl.nivel) {
                    lblCambiarNivelPrecio.setText(""+P_nivelprecioObj.items.get(i).nombre);
                    break;
                }
            }

            if (famid>0) listProduct();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
     }

    private void checkLock() {
        grdfam.setEnabled(!gl.ventalock);
        grdprod.setEnabled(!gl.ventalock);
    }

    private void modoMeseros() {
        Cursor dt;

        try {
            clsP_vendedor_rolObj P_vendedor_rolObj=new clsP_vendedor_rolObj(this,Con,db);
            P_vendedor_rolObj.fill("WHERE (codigo_sucursal="+gl.tienda+") AND (codigo_rol=4) ");

            if (P_vendedor_rolObj.count>0) {
                meseros=true;
            } else {
                sql="SELECT VENDEDORES.CODIGO_VENDEDOR, VENDEDORES.NOMBRE " +
                    "FROM VENDEDORES INNER JOIN P_RUTA ON VENDEDORES.RUTA=P_RUTA.CODIGO_RUTA " +
                    "WHERE (P_RUTA.SUCURSAL="+gl.tienda+") AND (VENDEDORES.NIVEL=4) ORDER BY VENDEDORES.NOMBRE";
                dt=Con.OpenDT(sql);
                meseros=dt.getCount()>0;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            meseros=false;
        }
    }

    private boolean validaFacturas() {
        long fi,ff;

        if (!app.usaFEL()) return true;

        try {
            /*
            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.ffecha00(fi);
            ff=du.addDays(ff,-4);ff=du.ffecha24(ff);
            if (fi>ff) {
                fi=du.addDays(ff,-1);fi=du.ffecha00(fi);
            }
            */

            ff=du.getActDate();fi=du.cfecha(du.getyear(ff),du.getmonth(ff),1);
            fi=du.addDays(du.getActDate(),-5);fi=du.ffecha00(fi);
            ff=du.addDays(ff,-4);ff=du.ffecha00(ff);

            sql="WHERE (ANULADO=0) AND (FECHA>="+fi+") AND (FECHA<="+ff+") AND (FEELUUID=' ')";

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill(sql);
            int fc=D_facturaObj.count;

            return true;
            /*
            if (fc==0) {
                return true;
            } else {
                msgAskSend("Existen facturas ("+fc+") pendientes de certificacion de mas que 4 días.\n La facturación queda bloqueada.");
                //msgAskSend("Existen facturas ("+fc+") pendientes de certificacion de mas que 4 días.\n Reporte el problema o la facturación quedará bloqueada.");
                return false;
            }

             */
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

    }

    private void enviaAvizo() {
        String subject,body;
        String dir=Environment.getExternalStorageDirectory()+"";

        try {
            subject="Bloqueo de venta : Ruta ID : "+gl.codigo_ruta;
            body="Estimado usuario,\n\nMPos reporta bloqueo de venta por razón de existencia de facturas" +
                    "pendientes de certificaciones de mas de 4 días.\n" +
                    "Por favor comuniquese con el soporte para solucionar el problema.\n" +
                    "Saludos\nDT Solutions S.A.\n";

            Uri uri=null;
            try {
                File f1 = new File(dir + "/posdts.db");
                File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
                File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
                FileUtils.copyFile(f1, f2);
                uri = Uri.fromFile(f3);

                app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            } catch (IOException e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            P_sucursalObj.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            String cor=P_sucursalObj.first().correo;if (cor.indexOf("@")<2) cor="";

            String[] TO = {"dtsolutionsgt@gmail.com"};if (!cor.isEmpty()) TO[0]=cor;
            String[] CC = {"dtsolutionsgt@gmail.com"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            if (!cor.isEmpty()) emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,body);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(emailIntent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void numeroOrden() {
        int ordennum;
        String ordencod;

        if (gl.pelOrdenComanda) {
            try {
                clsP_orden_numeroObj P_orden_numeroObj=new clsP_orden_numeroObj(this,Con,db);
                ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
                clsClasses.clsP_orden_numero orditem = clsCls.new clsP_orden_numero();
                orditem.id=ordennum;
                P_orden_numeroObj.add(orditem);


                ordennum=ordennum % 1000;ordennum=ordennum+1000;
                ordencod=""+ordennum;
                ordencod=gl.pelPrefijoOrden+ordencod.substring(1,4);
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                ordencod="---";
            }

            gl.ref1=ordencod.toUpperCase();
            lblAlm.setText("#"+gl.ref1);
        }
    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    private void meseroAutoLogin() {
        gl.idmesero=gl.codigo_vendedor;
        gl.meserodir=false;
        startActivity(new Intent(Venta.this,ResMesero.class));
    }

    private String umVenta(String cod) {
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

    private void actualizaNota(String nota) {
        try {
            sql="UPDATE T_VENTA SET VAL2='"+nota+"' WHERE (PRODUCTO='"+vitem.Cod+"') AND (EMPRESA='"+vitem.emp+"')";
            db.execSQL(sql);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private int codigoModificador(int idproducto) {
        int idclas,idmod;
        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+idproducto);
            if (P_productoObj.count==0) return 0;
            idclas=(int) P_productoObj.first().precio_vineta_o_tubo;

            clsP_prodclasifmodifObj P_prodclasifmodifObj=new clsP_prodclasifmodifObj(this,Con,db);
            P_prodclasifmodifObj.fill("WHERE (CODIGO_CLASIFICACION="+idclas+")");
            if (P_prodclasifmodifObj.count==0) return 0;
            idmod=P_prodclasifmodifObj.first().codigo_grupo;

            return idmod;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return 0;
    }

    private boolean disponibleCorel(){

        if (gl.codigo_pais.equalsIgnoreCase("GT")) {
            return disponibleCorelFactura();
        } else if (gl.codigo_pais.equalsIgnoreCase("HN")) {
            return disponibleCorelFactura();
        } else if (gl.codigo_pais.equalsIgnoreCase("SV")) {
            if (gl.sal_NIT) {
                return disponibleCorelFactura();
            } else if (gl.sal_NRC) {
                if (gl.sal_PER) {
                    return validaCorelCredito();
                } else {
                    return disponibleCorelFactura();
                }
            } else {
                return validaCorelTicket();
            }
        }

        return false;
    }

    private boolean disponibleCorelFactura(){
        Cursor DT;
        int ca,cf,ca1,ca2,fcorel;

        try {

            sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN FROM P_COREL WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=0) ";
            DT=Con.OpenDT(sql);

            try {
                if (DT.getCount()>0){
                    DT.moveToFirst();

                    ca1=DT.getInt(1);
                    cf=DT.getInt(3);
                } else {
                    mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n");
                    return false;
                }
            } catch (Exception e) {
                mu.msgbox("No esta definido correlativo de factura. No se puede continuar con la venta.\n");
                return false;
            }

            sql="SELECT MAX(COREL) FROM D_FACT_LOG ";
            DT=Con.OpenDT(sql);

            try {
                if (DT.getCount()>0){
                    DT.moveToFirst();
                    ca2=DT.getInt(0);
                } else {
                    ca2=0;
                }
            } catch (Exception e) {
                ca2=0;
            }

            ca=ca1;
            if (ca2>ca) ca=ca2;
            fcorel=ca+1;

            if (fcorel>cf) {
                mu.msgbox("Se ha acabado el talonario de facturas. No se puede continuar con la venta.");
                return false;
            }

            return true;

        } catch (Exception e) {
            mu.msgbox("disponibleCorel: " + e.getMessage());return false;
        }

    }

    private boolean validaCorelCredito() {
        Cursor DT;
        int ci,cf,ca1,ca2;
        double dd;
        long fv,fa;

        //if (gl.rol==4) return true;

        try {

            sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                    "WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=2)";
            DT=Con.OpenDT(sql);

            DT.moveToFirst();

            ca1=DT.getInt(1);
            ci=DT.getInt(2);
            cf=DT.getInt(3);

            if (ca1>=cf) {
                mu.msgbox("Se han terminado los correlativos de los creditos fiscales. No se puede continuar con la venta.");
                return false;
            }

            fa=du.getActDate();
            fv=DT.getLong(4);

            if (fa==fv) {
                msgbox("Último día de vigencia de autorización de los creditos fiscales.");
                toastlong("Último día de vigencia de autorización de los creditos fiscales.");
            }

            if (fa>fv) {
                mu.msgbox("Se ha acabado vigencia de autorización de los creditos fiscales. No se puede continuar con la venta.");
                return false;
            }

            dd=cf-ci;dd=0.90*dd;
            ca2=ci+((int) dd);
            if (ca1>ca2) porcentaje = true;

            if (DT!=null) DT.close();

        } catch (Exception e) {
            mu.msgbox("No esta definido correlativo de los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
            return false;
        }

        if (app.usaFEL()) {

            try {

                sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                        "WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=3) AND ACTIVA = 1 ";
                DT = Con.OpenDT(sql);

                DT.moveToFirst();

                ca1 = DT.getInt(1);
                ci = DT.getInt(2);
                cf = DT.getInt(3);

                if (ca1 >= cf) {
                    mu.msgbox("Se han terminado los correlativos de contingencias de los creditos fiscales. No se puede continuar con la venta.");
                    return false;
                }

                dd = cf - ci;
                dd = 0.75 * dd;
                ca2 = ci + ((int) dd);
                if (ca1 > ca2) porcentaje = true;

                if (DT != null) DT.close();

            } catch (Exception e) {
                mu.msgbox("No esta definido correlativo de contingencia para los creditos fiscales. No se puede continuar con la venta.\n"); //+e.getMessage());
                return false;
            }
        }

        return true;
    }

    private boolean validaCorelTicket() {
        Cursor DT;
        int ci,cf,ca1,ca2;
        double dd;
        long fv,fa;

        //if (gl.rol==4) return true;

        try {

            sql="SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                    "WHERE (RUTA="+gl.codigo_ruta+") AND (RESGUARDO=4)";
            DT=Con.OpenDT(sql);

            DT.moveToFirst();

            ca1=DT.getInt(1);
            ci=DT.getInt(2);
            cf=DT.getInt(3);

            if (ca1>=cf) {
                mu.msgbox("Se han terminado los correlativos de los tickets. No se puede continuar con la venta.");
                return false;
            }

            fa=du.getActDate();
            fv=DT.getLong(4);

            if (fa==fv) {
                msgbox("Último día de vigencia de autorización de los tickets.");
                toastlong("Último día de vigencia de autorización de los tickets.");
            }

            if (fa>fv) {
                mu.msgbox("Se ha acabado vigencia de autorización de los tickets. No se puede continuar con la venta.");
                return false;
            }

            dd=cf-ci;dd=0.90*dd;
            ca2=ci+((int) dd);
            if (ca1>ca2) porcentaje = true;

            if (DT!=null) DT.close();

        } catch (Exception e) {
            mu.msgbox("No esta definido correlativo de los tickets. No se puede continuar con la venta.\n"); //+e.getMessage());
            return false;
        }

        if (app.usaFEL()) {

            try {

                sql = "SELECT SERIE,CORELULT,CORELINI,CORELFIN,FECHAVIG,RESGUARDO FROM P_COREL " +
                        "WHERE (RUTA=" + gl.codigo_ruta + ") AND (RESGUARDO=5) AND ACTIVA = 1 ";
                DT = Con.OpenDT(sql);

                DT.moveToFirst();

                ca1 = DT.getInt(1);
                ci = DT.getInt(2);
                cf = DT.getInt(3);

                if (ca1 >= cf) {
                    mu.msgbox("Se han terminado los correlativos de contingencias de los tickets. No se puede continuar con la venta.");
                    return false;
                }

                dd = cf - ci;
                dd = 0.75 * dd;
                ca2 = ci + ((int) dd);
                if (ca1 > ca2) porcentaje = true;

                if (DT != null) DT.close();

            } catch (Exception e) {
                mu.msgbox("No esta definido correlativo de contingencia para los tickets. No se puede continuar con la venta.\n"); //+e.getMessage());
                return false;
            }
        }

        return true;
    }

    private boolean validaMinimoCF() {
        if (gl.codigo_pais.equalsIgnoreCase("GT")) {
            if (gl.codigo_cliente==gl.emp*10) {
                if (tot>2500) {
                    msgbox("El total de vanta máximo permitido para Consumidor final es Q2500.");
                    return false;
                }
            }
        }

        return true;
    }

    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog12, which) -> {
            try {
                sql="DELETE FROM T_VENTA";
                db.execSQL(sql);
                listItems();
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });
        dialog.setNegativeButton("No", (dialog1, which) -> {});
        dialog.show();

    }

    private void msgAskFEL(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", (dialog1, which) -> {
            try {
                gl.felcorel="";gl.feluuid="";
                if (gl.peFEL.equalsIgnoreCase(gl.felInfile)) {
                    startActivity(new Intent(Venta.this, FELVerificacion.class));
                } else if (gl.peFEL.equalsIgnoreCase(gl.felSal)) {
                    startActivity(new Intent(Venta.this, FELVerificacion.class));
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.setNegativeButton("No", (dialog12, which) -> {});

        dialog.show();

    }

    private void msgAskOrden(String msg) {

        if (!hasProducts()) {
            msgbox("La venta está vacía, no se puede convertir a orden!");return;
        }

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog1, which) -> crearPedido());
        dialog.setNegativeButton("No", (dialog12, which) -> {});

        dialog.show();

    }

    private void showNivelMenu() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Venta.this,"Nivel de precio");

            clsViewObj ViewObj=new clsViewObj(this,Con,db);

            sql="SELECT P_NIVELPRECIO_SUCURSAL.CODIGO_NIVEL_PRECIO AS NIVEL,P_NIVELPRECIO.NOMBRE AS NNOMBRE ,'','','','','','','' " +
                    "FROM P_NIVELPRECIO INNER JOIN P_NIVELPRECIO_SUCURSAL ON P_NIVELPRECIO.CODIGO = P_NIVELPRECIO_SUCURSAL.CODIGO_NIVEL_PRECIO " +
                    "WHERE (P_NIVELPRECIO_SUCURSAL.CODIGO_SUCURSAL="+gl.tienda+") " +
                    "UNION " +
                    "SELECT P_SUCURSAL.CODIGO_NIVEL_PRECIO AS NIVEL,P_NIVELPRECIO.NOMBRE AS NNOMBRE,'','','','','','','' " +
                    "FROM P_SUCURSAL INNER JOIN P_NIVELPRECIO ON P_SUCURSAL.CODIGO_NIVEL_PRECIO = P_NIVELPRECIO.CODIGO " +
                    "WHERE (P_SUCURSAL.CODIGO_SUCURSAL ="+gl.tienda+") " +
                    "ORDER BY NNOMBRE";

            ViewObj.fillSelect(sql);

            for (int i = 0; i <ViewObj.count; i++) {
                listdlg.add(ViewObj.items.get(i).f1);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    int niv=ViewObj.items.get(position).pk;
                    gl.nivel=niv;
                    setNivel();
                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.setOnLeftClick(v -> listdlg.dismiss());

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void inputMesa() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Impresión de comanda");
        alert.setMessage("MESA NUMERO: ");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER );
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Imprimir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    mesa=input.getText().toString();
                    imprimeComanda();
                } catch (Exception e) {
                    mu.msgbox("Valor incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void msgAskSend(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                enviaAvizo();
                processMenuTools(1);
            }
        });

        dialog.show();
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {

        try {

            super.onResume();

            gridViewOpciones.setEnabled(true);
            if (gl.parallevar) imgllevar.setVisibility(View.VISIBLE);else imgllevar.setVisibility(View.INVISIBLE);

            D_pedidoObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            MeserosObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);

            checkLock();

            listItems();

            try {
                P_nivelprecioObj.reconnect(Con,db);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gl.forcedclose) {
                super.finish();
                return;
            }

            gl.climode=true;
            menuTools();

            if (pedidos) estadoPedidos();

            try {
                txtBarra.requestFocus();
            } catch (Exception e) {}

            if (gl.iniciaVenta) {

                browse=0;
                lblVend.setText(" ");

                gl.nit_tipo="N";
                gl.numero_orden=" ";
                gl.nivel=gl.nivel_sucursal;
                setNivel();
                numeroOrden();

                gl.cliente_dom=0;gl.modo_domicilio=false;

                try  {
                    db.execSQL("DELETE FROM T_VENTA");
                    db.execSQL("DELETE FROM T_VENTA_MOD");
                    db.execSQL("DELETE FROM T_VENTA_ING");

                    listItems();
                } catch (SQLException e){
                    mu.msgbox("Error : " + e.getMessage());
                }

                Handler mtimer = new Handler();
                Runnable mrunner= () -> {
                    browse=8;
                    gl.iniciaVenta=false;

                    if (usarbio) {
                        startActivity(new Intent(Venta.this,Clientes.class));
                    } else {
                        if (!gl.cliposflag) {
                            gl.cliposflag=true;
                            if (!gl.forcedclose) {
                                if (!gl.peRest) startActivity(new Intent(Venta.this,CliPos.class));
                            }
                        }
                    }
                };
                mtimer.postDelayed(mrunner,100);

                if (gl.impresion_comanda) {
                    gl.impresion_comanda=false;
                    Handler mtimerc = new Handler();
                    Runnable mrunnerc= () -> ejecutaImpresionComanda();
                    mtimerc.postDelayed(mrunnerc,1000);
                }
            } else {}

            if (browse==7) {
                browse=0;processCantMenu();return;
            }

            if (!gl.scancliente.isEmpty()) {
                cargaCliente();
            }

            if (browse==-1)   {
                browse=0;finish();return;
            }

            if (browse==1)   {
                browse=0;processItem(false);return;
            }

            if (browse==2) {
                browse=0;processCant(false);return;
            }

            if (browse==3) {
                browse=0;
                prodid=saveprodid;
                if (gl.promapl) updDesc();return;
            }

            if (browse==4) {
                browse=0;listItems();return;
            }

            if (browse==5) {
                browse=0;addBarcode();return;
            }

            if (browse==6) {
                browse=0;updateCant();return;
            }

            if (browse==8) {
                browse=0;
                if (gl.forcedclose) {
                    super.finish();
                } else {
                    cargaCliente();
                }
            }

            if (browse==9) {
                browse=0;listItems();
                return;
            }


            if (browse==10) {
                browse=0;
                lblVend.setText(gl.gNombreCliente+" - "+gl.gNITCliente);
                listItems();
                return;
            }

            if (browse==11) {
                browse=0;
                if (gl.checksuper) {
                    browse=13;
                    gl.total_factura_previo_descuento=prodtotlin;
                    startActivity(new Intent(this,DescMonto.class));
                }
                return;
            }

            if (browse==12) {
                if (gl.cierra_clave) {
                    browse=0;gl.cierra_clave=false;
                } else {
                    if (gl.cerrarmesero) browse=12;else browse=0;
                    gl.cerrarmesero=false;gl.modoclave=0;
                    if (gl.mesero_lista) startActivity(new Intent(this,ValidaClave.class));
                }
                return;
            }

            if (browse==13) {
                browse=0;
                if (gl.desc_monto>=0) {
                    gl.promdesc=gl.desc_monto;
                    updDescMonto();
                }
                return;
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    @Override
    public void onBackPressed() {
        try{
            msgAskExit("Regresar al menu principal");
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

}
