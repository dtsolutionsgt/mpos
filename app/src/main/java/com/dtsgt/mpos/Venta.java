package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsBonFiltro;
import com.dtsgt.classes.clsBonif;
import com.dtsgt.classes.clsBonifGlob;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsDeGlob;
import com.dtsgt.classes.clsDescFiltro;
import com.dtsgt.classes.clsDescuento;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_cajacierreObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.fel.FelFactura;
import com.dtsgt.ladapt.ListAdaptGridFam;
import com.dtsgt.ladapt.ListAdaptGridFamList;
import com.dtsgt.ladapt.ListAdaptGridProd;
import com.dtsgt.ladapt.ListAdaptGridProdList;
import com.dtsgt.ladapt.ListAdaptMenuVenta;
import com.dtsgt.ladapt.ListAdaptVenta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Venta extends PBase {

    private ListView listView;
    private GridView gridView,grdbtn,grdfam,grdprod;
    private TextView lblTot,lblTit,lblAlm,lblVend,lblNivel,lblCant,lblBarra;
    private TextView lblProd,lblDesc,lblStot,lblKeyDP,lblPokl,lblDir;
    private EditText txtBarra,txtFilter;
    private ImageView imgroad,imgscan;
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

    private AppMethods app;

    private clsD_pedidoObj D_pedidoObj;
    private clsP_nivelprecioObj P_nivelprecioObj;

    private int browse;
    private double cant,desc,mdesc,prec,precsin,imp,impval;
    private double descmon,tot,totsin,percep,ttimp,ttperc,ttsin,prodtot,savecant;
    private double px,py,cpx,cpy,cdist,savetot,saveprec;

    private String uid,seluid,prodid,uprodid,um,tiposcan,barcode,imgfold,tipo,pprodname,nivname;
    private int nivel,dweek,clidia,counter;
    private boolean sinimp,softscanexist,porpeso,usarscan,handlecant=true,pedidos,descflag;
    private boolean decimal,menuitemadd,usarbio,imgflag,scanning=false,prodflag=true,listflag=true;
    private int codigo_cliente, emp,pedidoscant,cod_prod;
    private String cliid,saveprodid;
    private int famid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);

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

        cliid=gl.cliente;
        //cliid="0"; #CKFK 20200515 puse esto en comentario porque primero se le asigna el Id de cliente
        decimal=false;

        gl.atentini=du.getActDateTime();
        gl.ateninistr=du.geActTimeStr();
        gl.climode=true;
        mu.currsymb(gl.peMon);

        getURL();

        pedidos=gl.pePedidos;
        D_pedidoObj=new clsD_pedidoObj(this,Con,db);

        app = new AppMethods(this, gl, Con, db);
        app.parametrosExtra();

        counter=1;

        prc=new Precio(this,mu,2);
        khand=new clsKeybHandler(this,lblCant,lblKeyDP);

        menuItems();
        setHandlers();
        initValues();

        browse=0;
        txtBarra.requestFocus();txtBarra.setText("");
        clearItem();

        if (P_nivelprecioObj.count==0) {
            toastlong("NO SE PUEDE VENDER, NO ESTÁ DEFINIDO NINGUNO NIVEL DE PRECIO");finish();return;
        }

        imgflag=gl.peMImg;
        setVisual();

        checkLock();

        if(!gl.exitflag) {

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    gl.scancliente="";
                    browse=8;

                    gl.iniciaVenta=false;

                    if (usarbio) {
                        startActivity(new Intent(Venta.this,Clientes.class));
                    } else {
                        if (!gl.cliposflag) {
                            gl.cliposflag=true;
                            if (!gl.exitflag) {
                                startActivity(new Intent(Venta.this,CliPos.class));
                            }
                        }
                    }
                }
            };
            mtimer.postDelayed(mrunner,100);
        }

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

    public void finishOrder(View view) {
        finalizarOrden();
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
            if (!barcode.isEmpty()) addBarcode();
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
                    finishOrder(null);
                }
            });

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    /*
                    if (listflag) listflag=false;else return;

                    Handler mtimer = new Handler();
                    Runnable mrunner=new Runnable() {
                        @Override
                        public void run() {
                            listflag=true;
                        }
                    };
                    mtimer.postDelayed(mrunner,1000);
                    */


                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsVenta item = (clsVenta)lvObj;

                        prodid=item.Cod;gl.prodid=prodid;
                        gl.prodmenu=app.codigoProducto(prodid);//gl.prodmenu=prodid;
                        uprodid=prodid;
                        uid=item.emp;gl.menuitemid=uid;seluid=uid;
                        adapter.setSelectedIndex(position);

                        gl.gstr=item.Nombre;
                        gl.retcant=(int) item.Cant;
                        gl.limcant=getDisp(prodid);
                        menuitemadd=false;

                        //tipo=prodTipo(gl.prodcod);
                        tipo=prodTipo(prodid);
                        gl.tipoprodcod=tipo;
                        if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S")) {
                            browse=6;
                            gl.menuitemid=prodid;
                            startActivity(new Intent(Venta.this,VentaEdit.class));
                        } else if (tipo.equalsIgnoreCase("M")) {
                            gl.newmenuitem=false;
                            gl.menuitemid=item.emp;
                            browse=7;
                            startActivity(new Intent(Venta.this,ProdMenu.class));
                        }

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsVenta vItem = (clsVenta)lvObj;

                        prodid=vItem.Cod;
                        adapter.setSelectedIndex(position);

                        if (prodRepesaje(prodid) && gl.rutatipo.equalsIgnoreCase("V")) {
                            gl.gstr=prodid;
                            gl.gstr2=vItem.Nombre;
                            showItemMenu();
                        } else {
                            msgAskDel("Borrar producto");
                        }
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                    return true;
                }
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

            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        Object lvObj = gridView.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        adaptergrid.setSelectedIndex(position);
                        processMenuMenu(item.ID);
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
                        processMenuBtn(item.ID);
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
                    "T_VENTA.DES, T_VENTA.IMP, T_VENTA.PERCEP, T_VENTA.UM, T_VENTA.PESO, T_VENTA.UMSTOCK, T_VENTA.DESMON, T_VENTA.EMPRESA  " +
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
                    if (prodPorPeso(item.Cod)) 	{
                        item.um=DT.getString(10);
                    } else {
                        //item.um=DT.getString(8);
                        item.um=app.umVenta(item.Cod);
                    }
                    item.Peso=DT.getDouble(9);
                    item.emp=DT.getString(12);
                    if (item.emp.equalsIgnoreCase(uid)) {
                        selidx=ii;
                        seluid=uid;
                    }
                    desc=DT.getDouble(11);tdesc+=desc;

                    item.val=mu.frmdecimal(item.Cant,gl.peDecImp)+" "+ltrim(item.um,6);
                    if (gl.usarpeso) {
                        item.valp=mu.frmdecimal(item.Peso,gl.peDecImp)+" "+ltrim(gl.umpeso,6);
                    } else {
                        item.valp=".";
                    }

                    if (sinimp) {
                        ttsin=tt-item.imp-item.percep;
                        item.Total=ttsin;
                    } else {
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
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

        adapter=new ListAdaptVenta(this,this, items);
        adapter.cursym=gl.peMon;
        listView.setAdapter(adapter);

        if (sinimp) {
            ttsin=tot-ttimp-ttperc;
            ttsin=mu.round(ttsin,2);
            lblTot.setText(mu.frmcur(ttsin));
        } else {
            tot=mu.round(tot,2);
            tdesc=mu.round(tdesc,2);
            stot=tot-tdesc;
            lblTot.setText(mu.frmcur(tot));
            lblDesc.setText("Desc : "+mu.frmcur(tdesc));
            lblStot.setText("Subt : "+mu.frmcur(stot));
        }

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.smoothScrollToPosition(selidx);
        } else seluid="";

    }

    private void processItem(boolean updateitem){
        boolean exists;

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
                        msgAskLimit("El producto "+ pprodname+" no tiene existencia disponible.\n¿Continuar con la venta?",updateitem);
                    }
                } else if (tipo.equalsIgnoreCase("S")) {
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

        try{

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
                        msgAskLimit("\"El producto \"+ pprodname+\" no tiene existencia disponible.\n¿Continuar con la venta?",false);
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
        clsDescuento clsDesc;
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

            clsDesc = new clsDescuento(this, ""+cod_prod, cant);
            desc = clsDesc.getDesc();
            mdesc = clsDesc.monto;
            savecant=cant;
            descmon=0;

            if (desc + mdesc > 0) {

                browse = 3;
                gl.promprod =""+cod_prod;// prodid;
                gl.promcant = cant;

                if (desc > 0) {
                    gl.prommodo = 0;
                    gl.promdesc = desc;
                } else {
                    gl.prommodo = 1;
                    gl.promdesc = mdesc;
                }

                saveprodid=prodid;
                if (descflag) startActivity(new Intent(this, DescBon.class));
                descflag=true;

            } else {
                /*
                if (gl.bonus.size() > 0) {
                    Intent intent = new Intent(this, BonList.class);
                    startActivity(intent);
                }
                */
            }

            prodPrecio();

            /*
            precsin = prc.precsin;
            imp = prc.imp;
            impval = prc.impval;
            tot = prc.tot;
            descmon = savetot-tot;//prc.descmon;
            prodtot = tot;
            totsin = prc.totsin;
            percep = 0;
             */

            tipo=prodTipo(gl.prodcod);

            if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S")) {
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

    private void processMenuItem() {

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
            listItems();
        }

    }

    private void processCantMenu() {
        listItems();
    }

    private void updDesc(){
        try{
            desc=gl.promdesc;
            cant=savecant;
            prodPrecio();
            updItem();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void prodPrecio() {
        double sdesc=desc;

        try {
            if (prodPorPeso(prodid)) {
                prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, gl.dpeso,um,gl.prodcod);
                if (prc.existePrecioEspecial(prodid,cant,gl.codigo_cliente,gl.clitipo,um,gl.umpeso,gl.dpeso)) {
                    if (prc.precioespecial>0) prec=prc.precioespecial;
                }
            } else {
                prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, 0,um,gl.prodcod);
                if (prc.existePrecioEspecial(prodid,cant,gl.codigo_cliente,gl.clitipo,um,gl.umpeso,0)) {
                    if (prc.precioespecial>0) prec=prc.precioespecial;
                }
            }

            prec=mu.round(prec,2);
            desc=sdesc;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }
    }

    private boolean addItem(){
        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;

        tipo=prodTipo(gl.prodcod);

        if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S")) {
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

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

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
            if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",prodtot);
            if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2","0");
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
        double precdoc,fact,cantbas,peso;
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
            if (porpeso) ins.add("PRECIO",gl.prectemp); else ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",prodtot);
            if (porpeso) ins.add("PRECIODOC",gl.prectemp); else ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2","");
            ins.add("VAL3",0);
            ins.add("VAL4","");
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

    private void updItem(){
        double ptot=0;

        try {
            savetot=mu.round(saveprec*cant,2);
            ptot=mu.round(prec*cant,2);
            descmon = savetot-ptot;

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

    private boolean updateItemUID(){
        double precdoc;

        try {

            prodtot=mu.round(prec*cant,2);
            if (sinimp) precdoc=precsin; else precdoc=prec;

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
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

    private boolean updateMenuItemUID(){
        double precdoc;

        try {

            prodtot=mu.round(prec*cant,2);
            if (sinimp) precdoc=precsin; else precdoc=prec;

            upd.init("T_VENTA");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
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

            if (gl.cliente.isEmpty()) {
                toast("Cliente pendiente");
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

            /*
            clsBonG=new clsBonifGlob(this,tot);
            if (clsBonG.tieneBonif()) {
                for (int i = 0; i <clsBonG.items.size(); i++) {
                    //s=clsBonG.items.get(i).valor+"   "+clsBonG.items.get(i).tipolista+"  "+clsBonG.items.get(i).lista;
                    //Toast.makeText(this,s, Toast.LENGTH_SHORT).show();
                    gl.bonus.add(clsBonG.items.get(i));
                }
            } else {

            }
            */

            if (gl.dvbrowse!=0){
                if (tot<gl.dvdispventa){
                    mu.msgbox("No puede totalizar la factura, es menor al monto permitido para la nota de crédito: " + gl.dvdispventa);return;
                }
            }
            gl.brw=0;

            browse=0;

            Intent intent = new Intent(this,FacturaRes.class);
            startActivity(intent);

            if (gl.bonus.size()>0) {
                //Intent intent = new Intent(this,BonList.class);
                //startActivity(intent);
            }
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox("finishOrder: "+e.getMessage());
        }
    }

    //endregion

    //region Barras

    private void addBarcode() {
        gl.barra=barcode;

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

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO INNER JOIN " +
                    "P_PRODPRECIO ON (P_STOCK.CODIGO=P_PRODPRECIO.CODIGO_PRODUCTO)  " +
                    "WHERE (P_STOCK.CANT > 0) AND ((P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"')) ";
            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA FROM P_PRODUCTO " +
                    "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M')) " +
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

    public void showAtenDialog() {
        try{
            final AlertDialog Dialog;

            final String[] selitems = new String[lname.size()];
            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            mMenuDlg = new ExDialog(this);

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        String s=lcode.get(item);
                        setNoAtt(s);
                        doExit();
                    } catch (Exception e) {
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            Dialog = mMenuDlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

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


    //endregion

    //region Menu

    private void listFamily() {

        clsP_lineaObj P_lineaObj=new clsP_lineaObj(this,Con,db);
        clsClasses.clsMenu item;

        try {

            fitems.clear();
            P_lineaObj.fill("WHERE Activo=1");

            for (int i = 0; i <P_lineaObj.count; i++) {
                item=clsCls.new clsMenu();
                item.Cod=P_lineaObj.items.get(i).codigo+"";
                item.Name=P_lineaObj.items.get(i).nombre;
                item.icod=P_lineaObj.items.get(i).codigo_linea;
                fitems.add(item);
            }

            if (imgflag) {
                adapterf=new ListAdaptGridFam(this,fitems,imgfold);
                grdfam.setAdapter(adapterf);
            } else {
                adapterfl=new ListAdaptGridFamList(this,fitems,imgfold);
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
        int pact;

        try {
            pitems.clear();pcodes.clear();

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_PRODPRECIO.UNIDADMEDIDA, " +
                    "P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO  " +
                    "FROM P_PRODUCTO INNER JOIN	P_STOCK ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO INNER JOIN " +
                    "P_PRODPRECIO ON P_STOCK.CODIGO=P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE (P_PRODUCTO.ACTIVO=1) AND (P_PRODUCTO.CODIGO_TIPO ='P')";
            if (famid !=-1) {
                if (famid!=0) sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }

            sql += "UNION ";
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA,P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO " +
                    "FROM P_PRODUCTO  INNER JOIN " +
                    "P_PRODPRECIO ON P_PRODUCTO.CODIGO_PRODUCTO = P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M')) AND (P_PRODUCTO.ACTIVO=1)";
            if (famid !=-1) {
                if (famid!=0)
                    sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }

            sql += "ORDER BY P_PRODUCTO.DESCCORTA";
            dt=Con.OpenDT(sql);

            if (dt.getCount()==0){
                msgbox("¡Ningún artículo de la familia tiene existencia disponible!");return;
            }

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                pcode=dt.getString(0);
                if (!pcodes.contains(pcode)) {
                      if (dt.getInt(3)==1) {
                        item=clsCls.new clsMenu();
                        item.Cod=dt.getString(0);
                        item.icod=dt.getInt(4);
                        item.Name=dt.getString(1)+" \n[ "+gl.peMon+prodPrecioBase(item.icod)+" ]";

                        pitems.add(item);pcodes.add(pcode);
                    }
                }

                dt.moveToNext();
            }
            if (dt!=null) dt.close();
        } catch (Exception e) 		{
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (imgflag) {
            adapterp=new ListAdaptGridProd(this,pitems,imgfold);
            grdprod.setAdapter(adapterp);
        } else {
            adapterpl=new ListAdaptGridProdList(this,pitems,imgfold);
            grdprod.setAdapter(adapterpl);
        }

    }

    private void menuItems() {

        clsClasses.clsMenu item;

        menuTools();

        try {
            mmitems.clear();

            try {

                if (pedidos) {
                    item = clsCls.new clsMenu();
                    item.ID=61;item.Name="Orden ";item.Icon=61;
                    mmitems.add(item);
                }

                item = clsCls.new clsMenu();
                item.ID=50;item.Name="Buscar ";item.Icon=50;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=52;item.Name="Cliente";item.Icon=52;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=54;item.Name="Borrar linea ";item.Icon=54;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=55;item.Name="Borrar todo ";item.Icon=55;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=56;item.Name="Ventas";item.Icon=56;
                //mmitems.add(item);

                //item = clsCls.new clsMenu();
                //item.ID=51;item.Name="Barra";item.Icon=51;
                //mmitems.add(item);

                //item = clsCls.new clsMenu();
                //item.ID=53;item.Name="Bloquear";item.Icon=53;
                //mmitems.add(item);

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

            if (pedidos) {
                item = clsCls.new clsMenu();
                item.ID=16;item.Name="Pedidos";item.Icon=16;item.cant=pedidoscant+1;
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
            gridView.setAdapter(adaptergrid);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuMenu(int menuid) {
        try {
            switch (menuid) {
                case 1:
                    finalizarOrden();break;
                case 3:
                    menuImprDoc(3);break;
                case 4:
                    gl.tipo=3;menuAnulDoc();break;
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

    private void processMenuBtn(int menuid) {
        try {

            switch (menuid) {
                case 50:
                    gl.gstr = "";
                    browse = 1;
                    gl.prodtipo = 1;
                    startActivity(new Intent(this, Producto.class));
                    break;
                case 51:
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
                                startActivity(new Intent(Venta.this, CliPos.class));
                            }
                        }
                    }
                    break;
                case 53:
                    break;
                case 54:
                    if (!gl.ventalock) borraLinea();else toast("No se puede modificar el orden");
                    break;
                case 55:
                    if (!gl.ventalock) borraTodo();else toast("No se puede modificar el orden");
                    break;
                case 56:
                    showMenuSwitch();
                    break;
                case 61:
                    msgAskOrden("Convertir al orden");
                    break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
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

        try{

            final AlertDialog Dialog;

            final String[] selitems = {"Reporte de Documentos por Día", "Reporte Venta por Día", "Reporte Venta por Producto", "Reporte por Forma de Pago", "Reporte por Familia", "Reporte Ventas por Vendedor", "Reporte de Ventas por Cliente", "Margen y Beneficio por Productos", "Margen y Beneficio por Familia", "Cierre X", "Cierre Z"};

            ExDialog  menudlg = new ExDialog(this);
            menudlg.setTitle("Reportes");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    ss=selitems[item];

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

            Button nbuttonp = Dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbuttonp.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbuttonp.setTextColor(Color.WHITE);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public boolean valida(){
        try{

            clsP_cajacierreObj caja = new clsP_cajacierreObj(this,Con,db);

            caja.fill();

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

    public void showPrintMenuTodo() {

        try {
            final AlertDialog Dialog;
            //final String[] selitems = {"Factura","Pedido","Recibo","Deposito","Recarga","Devolución a bodega","Cierre de dia", "Nota crédito"};
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Deposito","Recarga","Devolución a bodega"};


            ExDialog menudlg = new ExDialog(this);

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            menuImprDoc(3);break;
                        case 1:
                            menuImprDoc(2);break;
                        case 2:
                            menuImprDoc(4);break;
                        case 3:
                            menuImprDoc(5);break;
                    }

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
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

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
            Intent intent = new Intent(this,Pedidos.class);
            startActivity(intent);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    public void showVoidMenuTodo() {
        try{
            final AlertDialog Dialog;
            final String[] selitems = {(gl.peMFact?"Factura":"Ticket"),"Deposito","Recarga","Devolución a bodega"};

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
            final AlertDialog Dialog;
            final String[] selitems = {"Iniciar nueva venta","Cambiar venta"};

            ExDialog menudlg = new ExDialog(this);

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            ;break;
                        case 1:
                            ;break;
                    }

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
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

    //region Pedidos

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

    private void showItemMenu() {
        try{
            final AlertDialog Dialog;
            final String[] selitems = {"Repesaje","Borrar"};

            ExDialog menudlg = new ExDialog(this);

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            browse=4;
                            startActivity(new Intent(Venta.this,RepesajeLista.class));break;
                        case 1:
                            msgAskDel("Borrar producto");break;
                    }

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
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void setControls(){

        try{
            listView = (ListView) findViewById(R.id.listView1);
            gridView = (GridView) findViewById(R.id.gridView2);
            grdfam = (GridView) findViewById(R.id.grdFam);
            grdprod = (GridView) findViewById(R.id.grdProd);
            grdbtn = (GridView) findViewById(R.id.grdbtn);

            lblTot= (TextView) findViewById(R.id.lblTot);
            lblDesc= (TextView) findViewById(R.id.textView115);lblDesc.setText( "Desc : "+mu.frmcur(0));
            lblStot= (TextView) findViewById(R.id.textView103); lblStot.setText("Subt : "+mu.frmcur(0));
            lblTit= (TextView) findViewById(R.id.lblTit);
            lblAlm= (TextView) findViewById(R.id.lblTit2);
            lblVend= (TextView) findViewById(R.id.lblTit4);
            lblNivel= (TextView) findViewById(R.id.lblTit3);
            lblPokl= (TextView) findViewById(R.id.lblTit5);

            lblCant= (TextView) findViewById(R.id.lblCant);lblCant.setText("");
            lblBarra= (TextView) findViewById(R.id.textView122);lblBarra.setText("");
            lblKeyDP=(TextView) findViewById(R.id.textView110);
            lblDir=(TextView) findViewById(R.id.lblDir);

            imgroad= (ImageView) findViewById(R.id.imgRoadTit);
            imgscan= (ImageView) findViewById(R.id.imageView13);

            txtBarra=(EditText) findViewById(R.id.editText10);

            relScan= (RelativeLayout) findViewById(R.id.relScan);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setVisual() {
        if (imgflag) {
            grdfam.setNumColumns(3);
            grdprod.setNumColumns(3);
        } else {
            grdfam.setNumColumns(2);
            grdprod.setNumColumns(1);
        }

        listFamily();
    }

    private void initValues(){
        Cursor DT;
        String contrib;

        app.parametrosExtra();
        usarbio=gl.peMMod.equalsIgnoreCase("1");

        tiposcan="*";

        lblTit.setText(gl.tiendanom+" - "+gl.cajanom);
        lblAlm.setText(gl.tiendanom);
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

        try {
            sinimp=false;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            sinimp=false;
        }

		/*
		contrib=gl.contrib;
		if (contrib.equalsIgnoreCase("C")) sinimp=true;
		if (contrib.equalsIgnoreCase("F")) sinimp=false;
		*/

        sinimp=false;
        gl.sinimp=sinimp;

        try {
            sql="DELETE FROM T_VENTA";
            db.execSQL(sql);

            sql="DELETE FROM T_COMBO";
            db.execSQL(sql);

            sql="DELETE FROM T_BARRA";
            db.execSQL(sql);

            sql="DELETE FROM T_BARRA_BONIF";
            db.execSQL(sql);

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

        gl.ref1="";
        gl.ref2="";
        gl.ref3="";

        clsDescFiltro clsDFilt=new clsDescFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        clsBonFiltro clsBFilt=new clsBonFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        imgfold= Environment.getExternalStorageDirectory()+ "/mPosFotos/";

        dweek=mu.dayofweek();

        lblTot.setText("Total : "+mu.frmcur(0));
        lblVend.setText("");

        khand.clear(true);khand.enable();
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
        Cursor dt;

        try {

            sql="SELECT CANT FROM P_STOCK WHERE (CODIGO='"+prodid+"') AND (UNIDADMEDIDA='"+vum+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                int i=dt.getInt(0);
                if (dt!=null) dt.close();
                return i;
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int cantProdCombo(int prodid) {
        Cursor dt;

        try {
            sql="SELECT SUM(CANT*UNID) FROM T_COMBO WHERE (IDSELECCION="+prodid+")";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                int i=dt.getInt(0);
                if (dt!=null) dt.close();
                return i;
            } return 0;
        } catch (Exception e) {
            return 0;
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

            gl.gNombreCliente =DT.getString(0);
            lcred=DT.getDouble(1);
            gl.gNITCliente =DT.getString(2);
            gl.gDirCliente =DT.getString(3);
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
        int mm,yy;
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
         try {
             sql="SELECT COREL FROM D_factura WHERE (FEELUUID=' ') AND (ANULADO=0)";
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
                    lblNivel.setText(""+P_nivelprecioObj.items.get(i).nombre);break;
                }
            }

            if (famid>0) listProduct();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
     }

    private void checkLock() {

        if (gl.ventalock) toast("El orden está protegido, no se puede modificar");

        listView.setEnabled(!gl.ventalock);
        grdfam.setEnabled(!gl.ventalock);
        grdprod.setEnabled(!gl.ventalock);

    }

    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sql="DELETE FROM T_VENTA";
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

    private void msgAskFEL(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gl.felcorel="";gl.feluuid="";
                    startActivity(new Intent(Venta.this, FelFactura.class));
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

    private void msgAskOrden(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toast("Pendiente implementacion");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void showNivelMenu() {
        final AlertDialog Dialog;
        int nivuser=0;

        try {

            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE CODIGO_VENDEDOR="+gl.codigo_vendedor);
            if (VendedoresObj.count>0) nivuser=(int) VendedoresObj.first().nivelprecio;

            P_nivelprecioObj.fill("WHERE (CODIGO="+gl.nivel_sucursal+") OR (CODIGO="+nivuser+") ORDER BY Nombre");

            final String[] selitems = new String[P_nivelprecioObj.count];
            for (int i = 0; i <P_nivelprecioObj.count; i++) {
                selitems[i]=P_nivelprecioObj.items.get(i).nombre;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Nivel de precio");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    int niv=P_nivelprecioObj.items.get(item).codigo;
                    gl.nivel=niv;
                    setNivel();

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

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

   //endregion

    //region Activity Events

    @Override
    protected void onResume() {

        try {
            super.onResume();
            D_pedidoObj.reconnect(Con,db);

            checkLock();

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

                gl.nivel=gl.nivel_sucursal;
                setNivel();

                try  {
                    db.execSQL("DELETE FROM T_VENTA");
                    listItems();
                } catch (SQLException e){
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
                    mu.msgbox("Error : " + e.getMessage());
                }

                Handler mtimer = new Handler();
                Runnable mrunner=new Runnable() {
                    @Override
                    public void run() {
                        browse=8;
                        gl.iniciaVenta=false;

                        if (usarbio) {
                            startActivity(new Intent(Venta.this,Clientes.class));
                        } else {
                            if (!gl.cliposflag) {
                                gl.cliposflag=true;
                                if (!gl.forcedclose) {
                                    startActivity(new Intent(Venta.this,CliPos.class));
                                }
                            }
                        }
                    }
                };
                mtimer.postDelayed(mrunner,100);
            } else {
            }

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
