package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsOrden;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsBonFiltro;
import com.dtsgt.classes.clsBonif;
import com.dtsgt.classes.clsBonifGlob;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsD_barril_transObj;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsDeGlob;
import com.dtsgt.classes.clsDescFiltro;
import com.dtsgt.classes.clsDescuento;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_linea_impresoraObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_orden_numeroObj;
import com.dtsgt.classes.clsP_prodclasifmodifObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_comandaObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_orden_ingObj;
import com.dtsgt.classes.clsT_orden_modObj;
import com.dtsgt.classes.clsT_orden_notaObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsT_ordenerrorObj;
import com.dtsgt.classes.clsT_ordenpendObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListChkDlg;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.ladapt.ListAdaptGridFam;
import com.dtsgt.ladapt.ListAdaptGridFamList;
import com.dtsgt.ladapt.ListAdaptGridProd;
import com.dtsgt.ladapt.ListAdaptGridProdList;
import com.dtsgt.ladapt.ListAdaptMenuOrden;
import com.dtsgt.ladapt.ListAdaptMenuVenta;
import com.dtsgt.ladapt.ListAdaptOrden;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.wsCommit;
import com.dtsgt.webservice.wsOpenDT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Orden extends PBase {

    private ListView listView;
    private GridView gridView,grdbtn,grdfam,grdprod;
    private TextView lblTot,lblCant,lblTit,lblAlm,lblVend,lblCent,lblCom;
    private TextView lblProd,lblDesc,lblStot,lblKeyDP,lblPokl,lblDir;
    private EditText txtbarra;
    private ImageView imgroad,imgrefr,imgnowifi;
    private RelativeLayout relprod,relsep,relsep2,relback;

    private ArrayList<clsClasses.clsOrden> items= new ArrayList<clsOrden>();
    private ArrayList<String> tl=new ArrayList<String>();

    private ListAdaptOrden adapter;
    private clsOrden selitem;
    private clsClasses.clsT_orden oitem;
    private clsClasses.clsP_res_sesion rsitem;
    private clsClasses.clsD_barril_trans btrans;

    private Precio prc;
    private clsListaObj ViewObj;

    private ListAdaptMenuOrden adaptergrid;
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
    private ArrayList<clsClasses.clsLista> ilist= new ArrayList<clsClasses.clsLista>();
    private ArrayList<clsClasses.clsLista> plist= new ArrayList<clsClasses.clsLista>();
    private ArrayList<String> brtitems = new ArrayList<String>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();

    private WebService ws;

    private wsCommit wscom;
    private wsOpenDT wso;
    private wsCommit wsbtr;
    private wsCommit wslock;

    private Runnable rnBroadcastCallback;
    private Runnable rnDetailCallback;
    private Runnable rnBarTrans;
    private Runnable rnClose;
    private Runnable rnOrdenInsert,rnOrdenQuery;

    //private Handler ctimer;
    //private Runnable crunner;

    private AppMethods app;

    private clsP_nivelprecioObj P_nivelprecioObj;
    private clsP_productoObj P_productoObj;
    private clsP_linea_impresoraObj P_linea_impresoraObj;
    private clsT_comandaObj T_comandaObj;
    private clsT_orden_notaObj T_orden_notaObj;
    private clsP_impresoraObj P_impresoraObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;
    private clsT_ventaObj T_ventaObj;
    private clsT_ordencomboObj T_ordencomboObj;
    private clsT_comboObj T_comboObj;
    private clsT_orden_modObj T_orden_modObj;
    private clsT_orden_ingObj T_orden_ingObj;
    private clsD_barril_transObj D_barril_transObj;
    private clsP_orden_numeroObj P_orden_numeroObj;

    private clsRepBuilder rep;

    private int browse;
    private double cant,desc,mdesc,prec,precsin,imp,impval;
    private double descmon,tot,totsin,percep,ttimp,ttperc,ttsin,prodtot,savecant,pimp;
    private double px,py,cpx,cpy,cdist,savetot,saveprec;

    private String uid,seluid,prodid,uprodid,um,tiposcan,barcode,imgfold,tipo,pprodname,mesa,nivname,cbui;
    private int nivel,dweek,clidia,counter,prodlinea,cuenta, IdcuentamovDestino,lineaingred;
    private boolean sinimp,softscanexist,porpeso,usarscan,handlecant=true,descflag;
    private boolean enviarorden,actorden,modo_emerg,exit_mode,close_flag;
    private boolean decimal,menuitemadd,usarbio,imgflag,scanning=false,ordencentral;
    private boolean prodflag=true,listflag=true,horiz,wsoidle=true,ordenpedido,barril;
    private int codigo_cliente, emp,cod_prod,cantcuentas,ordennum,idimp1,idimp2,idtransbar;
    private String idorden,cliid,saveprodid, brtcorel, idresorig, idresdest;
    private int famid = -1,statenv,estado_modo,brtid,numpedido,btrpos,valsupermodo;
    private int IdCuentaAMover =0;

    private int maxitems=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_orden);horiz=true;
        } else {
            setContentView(R.layout.activity_orden_ver);horiz=false;
        }

        super.InitBase();

        setControls();
        calibraPantalla();

        P_linea_impresoraObj=new clsP_linea_impresoraObj(this,Con,db);
        T_comandaObj=new clsT_comandaObj(this,Con,db);
        T_orden_notaObj=new clsT_orden_notaObj(this,Con,db);
        T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);
        P_impresoraObj=new clsP_impresoraObj(this,Con,db);
        T_ventaObj=new clsT_ventaObj(this,Con,db);
        T_ordencomboObj=new clsT_ordencomboObj(this,Con,db);
        T_comboObj = new clsT_comboObj(this, Con, db);
        P_nivelprecioObj=new clsP_nivelprecioObj(this,Con,db);
        P_nivelprecioObj.fill("ORDER BY Nombre");
        ViewObj=new clsListaObj(this,Con,db);
        T_orden_modObj=new clsT_orden_modObj(this,Con,db);
        T_orden_ingObj=new clsT_orden_ingObj(this,Con,db);
        D_barril_transObj=new clsD_barril_transObj(this,Con,db);
        P_orden_numeroObj=new clsP_orden_numeroObj(this,Con,db);

        gl.scancliente="";
        emp=gl.emp;
        gl.nivel_sucursal=nivelSucursal();
        gl.nivel=gl.nivel_sucursal;nivel=gl.nivel;

        //msgbox("id orden "+gl.idorden);

        idorden=gl.idorden;
        gl.ordcorel=gl.idorden;

        enviarorden= gl.pelMeseroCaja;
        actorden=gl.peActOrdenMesas;
        gl.mesero_lista=true;
        lineaingred=gl.peLineaIngred;

        cliid=gl.cliente;
        decimal=false;

        gl.atentini=du.getActDateTime();
        gl.ateninistr=du.geActTimeStr();
        gl.climode=true;
        mu.currsymb(gl.peMon);

        getURL();

        P_productoObj=new clsP_productoObj(this,Con,db);P_productoObj.fill();

        app = new AppMethods(this, gl, Con, db);
        app.parametrosExtra();
        modo_emerg=app.modoSinInternet();

        setPrintWidth();
        rep=new clsRepBuilder(this,24,true,gl.peMon,gl.peDecImp,"");
        //rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

        counter=1;

        prc=new Precio(this,mu,2,gl.peDescMax);

        setHandlers();
        initValues();

        browse=0;
        clearItem();

        if (P_nivelprecioObj.count==0) {
            toastlong("NO SE PUEDE VENDER, NO ESTÁ DEFINIDO NINGUNO NIVEL DE PRECIO");
            cerrarOrden();return;
        }

        imgflag=false;//imgflag=gl.peMImg;

        setVisual();

        listItems();

        if (gl.nombre_mesero_sel.isEmpty()) gl.nombre_mesero_sel=gl.vendnom;

        ws=new WebService(Orden.this,gl.wsurl);
        actualizaEstadoOrden(0);

        rnBroadcastCallback = () -> broadcastCallback();

        wscom =new wsCommit(gl.wsurl);
        wslock =new wsCommit(gl.wsurl);

        //agregaBloqueo();

        rnClose = () -> closeAction();

        /*
        rnDetailCallback = () -> {
            switch (wso.level) {
                case 1:
                    broadcastDetailCallback();;break;
                case 2:
                    broadcastAccountCallback();;break;
                case 3:
                    broadcastCompleteCallback();break;
            }
        };
        */

        wso=new wsOpenDT(gl.wsurl);

        rnBarTrans = new Runnable() {
            public void run() {
                envioBarrilTrans();
            }
        };
        wsbtr =new wsCommit(gl.wsurl);

        rnOrdenInsert= () -> {
            ordenInsert();
        };

        rnOrdenQuery= () -> {
            ordenQuery();
        };

        /*
        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                if (actorden) {
                    broadcastDetail();
                } else {
                    cierraPantalla();
                }
            }
        };
        mtimer.postDelayed(mrunner,500);

         */

        //fbs=new fbP_res_sesion("P_RES_SESION/"+gl.emp+"/"+gl.tienda+"/");

        /*
        ctimer = new Handler();
        crunner=new Runnable() {
            @Override
            public void run() {
                //gl.cerrarmesero=true;
                //gl.mesero_lista=true;
                //cerrarOrden();
            }
        };
        */
    }

    //region Events

    /*
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //super.onActivityResult(requestCode, resultCode, intent);
        try {
            //if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                barcode = contents;
                toast(barcode);
            }
            //}
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }
    */

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

    public void doExit(View view){
        exitBtn(false);
    }

    public void doAdd(View view) {
        gl.gstr = "";browse=1;gl.prodtipo=1;
        startActivity(new Intent(this, Producto.class));
    }

    public void doBarcode(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ingrese codigo");
        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER );
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", (dialog, whichButton) -> {
            try {
                int bci=Integer.parseInt(input.getText().toString());
                barcode=""+bci;
                addBarcode();
            } catch (Exception e) {
                mu.msgbox("Codigo incorrecto");return;
            }
        });
        alert.setNegativeButton("Cancelar", (dialog, whichButton) -> {});
        alert.show();
    }

    public void subItemClick(int position,int handle) {
    }

    private void setHandlers(){

        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {

                try {

                    Object lvObj = listView.getItemAtPosition(position);
                    clsOrden item = (clsOrden)lvObj;selitem=item;

                    prodid=item.Cod;gl.prodid=prodid;
                    gl.produid=item.id;
                    cbui=item.emp;
                    gl.prodmenu=app.codigoProducto(prodid);//gl.prodmenu=prodid;
                    uprodid=prodid;
                    uid=""+item.id;gl.menuitemid=uid;seluid=uid;
                    adapter.setSelectedIndex(position);
                    //#EJC202210221616:Set the splited account to move.
                    IdCuentaAMover = item.cuenta;
                    gl.gstr2=item.Nombre;
                    //gl.gstr=item.Nombre+" \n[ "+gl.peMon+prodPrecioBase(app.codigoProducto(gl.prodid))+" ]";;
                    gl.gstr=item.Nombre+" \n[ "+gl.peMon+app.prodPrecio(app.codigoProducto(gl.prodid))+" ]";;

                    gl.retcant=(int) item.Cant;
                    gl.limcant=getDisp(prodid);
                    menuitemadd=false;

                    //tipo=prodTipo(gl.prodcod);
                    tipo=prodTipo(prodid);
                    gl.tipoprodcod=tipo;

                    if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S") || tipo.equalsIgnoreCase("PB")) {
                        browse=6;
                        gl.menuitemid=prodid;
                    } else if (tipo.equalsIgnoreCase("M")) {
                        gl.newmenuitem=false;
                        gl.menuitemid=item.emp;
                        browse=7;
                    }

                    statenv=(int) item.percep;
                    gl.idmodgr=codigoModificador(app.codigoProducto(gl.prodid));

                    if (item.estado==1) {
                        showItemPopMenu();
                    } else {
                        showItemPopMenuLock();
                    }

                } catch (Exception e) {
                    mu.msgbox( e.getMessage());
                }

                cierraPantalla();
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsOrden item = (clsOrden)lvObj;selitem=item;
                    prodid=item.Cod;gl.produid=item.id;

                    if (items.get(position).estado==0) {
                        msgAskState("Agregar a la comanda",1,position);
                    } else {
                        msgAskState("Marcar como preparado",0,position);
                    }
                    cierraPantalla();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
                return true;
            });

            txtbarra.setOnKeyListener((v, keyCode, event) -> {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                    barcode=txtbarra.getText().toString();
                    if (!barcode.isEmpty()) addBarcode();
                    return true;
                } else {
                    return false;
                }
            });

            grdfam.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = grdfam.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    famid=item.icod;
                    gl.idgrres=item.idres;
                    gl.idgrsel=item.idressel;

                    if (imgflag) {
                        adapterf.setSelectedIndex(position);
                    } else {
                        adapterfl.setSelectedIndex(position);
                    }

                    listProduct();
                    cierraPantalla();
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            });

            grdprod.setOnItemClickListener((parent, view, position, id) -> {
                int kcant,ppos;

                try {
                    Object lvObj = grdprod.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    if (imgflag) {
                        adapterp.setSelectedIndex(position);
                    } else {
                        adapterpl.setSelectedIndex(position);
                    }

                    if(item.valor<=0) {
                        msgbox("No se puede vender producto sin precio");return;
                    }

                    prodid=item.Cod;
                    gl.prodid=prodid;
                    gl.prodcod=item.icod;
                    gl.gstr=prodid; gl.gstr2=item.Name;
                    gl.prodmenu=gl.prodcod;
                    gl.pprodname=item.Name;
                    ppos=gl.pprodname.indexOf("[");
                    if (ppos<=1) pprodname=gl.pprodname;else pprodname=gl.pprodname.substring(0,ppos-1);

                    gl.um=app.umVenta(gl.prodid);
                    gl.menuitemid=prodid;
                    menuitemadd=true;

                    processItem(false);

                } catch (Exception e) {
                    String ss=e.getMessage();
                }

                cierraPantalla();
            });

            grdprod.setOnItemLongClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = grdprod.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adapterp.setSelectedIndex(position);

                    prodid=item.Cod;
                    gl.gstr=prodid;//gl.prodmenu=prodid;
                    gl.pprodname=item.Name;

                    msgAskAdd(item.Name);
                } catch (Exception e) {}
                cierraPantalla();
                return true;
            });

            gridView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adaptergrid.setSelectedIndex(position);
                    processMenuTools(item.ID);
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
                cierraPantalla();
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Main

    public void listItems() {
        Cursor DT;
        clsOrden item;
        double tt,stot,tdesc,desc;
        int ii,idpr;
        boolean esingr;
        String snota;

        items.clear();tot=0;ttimp=0;ttperc=0;tdesc=0;selidx=-1;ii=0;seluid="";

        try {
            sql="SELECT T_ORDEN.PRODUCTO, P_PRODUCTO.DESCCORTA, T_ORDEN.TOTAL, T_ORDEN.CANT, T_ORDEN.PRECIODOC, " +
                "T_ORDEN.DES, T_ORDEN.IMP, T_ORDEN.PERCEP, T_ORDEN.UM, T_ORDEN.PESO, T_ORDEN.UMSTOCK, " +
                "T_ORDEN.DESMON, T_ORDEN.EMPRESA, T_ORDEN.CUENTA, T_ORDEN.ESTADO, T_ORDEN.ID " +
                "FROM T_ORDEN INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=T_ORDEN.PRODUCTO "+
                "WHERE (COREL='"+idorden+"') AND (T_ORDEN.ESTADO<2) ORDER BY T_ORDEN.ID ";

            DT=Con.OpenDT(sql);

            try {
                lblCant.setText("Articulos : "+DT.getCount());
            } catch (Exception e) {
                lblCant.setText("Articulos : -");
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }


            if (DT.getCount()>0) {

                DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    tt=DT.getDouble(2);

                    item = clsCls.new clsOrden();

                    item.Cod=DT.getString(0);idpr=app.codigoProducto(item.Cod);
                    item.id=DT.getInt(15);
                    item.Cant=DT.getDouble(3);
                    item.icant=(int) item.Cant;
                    esingr=esIngrediente(idpr);
                    item.Nombre=item.icant+" x "+DT.getString(1);
                    item.modif=esModificado(item.id);
                    if (esingr) item.modif=false;
                    item.indent=esingr;
                    item.Prec=DT.getDouble(4);
                    item.Desc=DT.getDouble(5);
                    item.sdesc=mu.frmdec(item.Prec);
                    item.imp=DT.getDouble(6);
                    item.percep=DT.getDouble(7);
                    item.um=DT.getString(8);
                    item.Peso=DT.getDouble(9);
                    item.emp=DT.getString(12);

                    if (item.emp.equalsIgnoreCase(uid)) {
                        selidx=ii;
                        seluid=uid;
                    }

                    desc=DT.getDouble(11);tdesc+=desc;

                    item.cuenta=DT.getInt(13);
                    item.estado=DT.getInt(14);
                    item.id=DT.getInt(15);

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

                    T_ordencomboprecioObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+item.emp+")");
                    if (T_ordencomboprecioObj.count>0) {
                        item.Prec=T_ordencomboprecioObj.first().prectotal;
                        item.sdesc=mu.frmdec(item.Prec);

                        tt=item.icant*item.Prec;tt=mu.round2(tt);
                        item.Total=tt;
                    }
                    T_ordencomboprecioObj.items.clear();

                    snota="";
                    T_orden_notaObj.fill("WHERE (COREL='"+idorden+"') AND (ID="+item.id+")");
                    if (T_orden_notaObj.count>0) snota=T_orden_notaObj.first().nota;

                    item.nota=snota;

                    //if (!cuentaPagada(idorden,item.cuenta)) {
                        items.add(item);

                        tot+=tt;
                        ttimp+=item.imp;
                        ttperc+=item.percep;
                    //}

                    DT.moveToNext();ii++;
                }
            }

            if (DT!=null) DT.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox( e.getMessage());
        }

        adapter=new ListAdaptOrden(this,this, items, horiz);
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
            //adapter.setSelectedIndex(selidx);
            //listView.smoothScrollToPosition(selidx);
        } else seluid="";


        try {
            if (adapter.getCount()>0) {
                adapter.setSelectedIndex(adapter.getCount()-1);
                listView.smoothScrollToPosition(adapter.getCount()-1);
            }
        } catch (Exception e) { }

    }

    private void processItem(boolean updateitem){
        boolean exists;

        try{

            try {
                sql="SELECT Empresa,Cant FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (PRODUCTO='"+prodid+"')";
                Cursor dt=Con.OpenDT(sql);
                exists=dt.getCount()>0;
            } catch (SQLException e) {
                exists=false;
            }
            descflag=true;

            String pid=gl.gstr;
            if (mu.emptystr(pid)) return;

            prodid=pid;
            gl.prodcod=app.codigoProducto(prodid);

            gl.um=app.umVenta(prodid); um=gl.um;
            gl.bonprodid=prodid;

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
                } else if (tipo.equalsIgnoreCase("PB")) {
                    if (!app.barrilAbierto(prodid)) {
                        msgbox("El producto no tiene abierto el barril");return;
                    }

                    if (exists) descflag=false;
                    processCant(updateitem);
                }
            } else if (tipo.equalsIgnoreCase("M")){
                gl.prodmenu=gl.prodcod;
                gl.prodid=prodid;
                prodPrecio();
                gl.menuprecio=prec;
                processMenuItem();
            }
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
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

            clsDesc = new clsDescuento(this, ""+cod_prod, cant,gl.peDescMax);
            desc = clsDesc.getDesc();
            mdesc = saveprec*cant*desc/100;mdesc=mu.round2(mdesc);

            savecant=cant;

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

            double dsc=desc;

            precsin = prc.precsin;
            imp = prc.imp;
            impval = prc.impval;
            impval=mu.round2dec(impval);

            tot = prc.tot;
            descmon = mdesc;
            prodtot = tot;
            totsin = prc.totsin;
            percep = 0;

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
            sql="DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (CANT=0)";
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
        startActivity(new Intent(this,OrdenMenu.class));

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

            envioCuentas();
            listItems();
        }

    }

    private void processCantMenu() {
        envioCuentas();
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
            prec = prc.precio(prodid, cant, nivel, um, gl.umpeso, 0,um,gl.prodcod);
            prec=mu.round(prec,2);

            pimp=prc.imp;
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }
    }

    private boolean addItem(){

        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;
        int newid,cui;

        tipo=prodTipo(gl.prodcod);
        gl.uidingrediente=0;

        if (items.size()>=maxitems) {
            msgbox("Se alcanzó la cantidad máxima de artículos\nNo se puede continuar.");
            return false;
        }

        try {

            int icod=app.codigoProducto(prodid);

            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO="+icod+") AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);

            dt.moveToFirst();
            umb=dt.getString(0);
            fact=dt.getDouble(1);

            if (dt!=null) dt.close();

        } catch (Exception e) {
            umb=um;fact=1;
        }

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

        try {
            sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            newid=dt.getInt(0)+1;
        } catch (Exception e) {
            newid=1;
        }

        try {

            if (sinimp) precdoc=precsin; else precdoc=prec;

            cui=app.cuentaActiva(idorden);
            if (existenCuentasPagadas()) {
                cui=app.cuentaActivaPostpago(idorden);
            }

            ins.init("T_ORDEN");

            ins.add("ID",newid);
            ins.add("COREL",idorden);
            ins.add("PRODUCTO",prodid);
            //ins.add("EMPRESA",""+counter);
            ins.add("EMPRESA",""+newid);
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

            //ins.add("VAL1",0);
            if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                ins.add("VAL1", pimp);
            } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                ins.add("VAL1", pimp);
            } else {
                ins.add("VAL1",0);
            }

            ins.add("VAL2","0");
            ins.add("VAL3",0);
            ins.add("VAL4","0");
            ins.add("PERCEP",percep);
            ins.add("CUENTA",cui);
            ins.add("ESTADO",1);

            db.execSQL(ins.sql());

            counter++;

            gl.uidingrediente=newid;

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (CANT=0)";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        if (actorden) envioOrden();
        listItems();

        return true;
    }

    private boolean addItemMenu(){
        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;
        int nid1,nid2,newid;

        if (items.size()>=maxitems) {
            msgbox("Se alcanzó la cantidad máxima de artículos\nNo se puede continuar.");
            return false;
        }

        try {
            int icod=app.codigoProducto(prodid);

            sql="SELECT UNIDADMINIMA,FACTORCONVERSION FROM P_FACTORCONV WHERE (PRODUCTO="+icod+") AND (UNIDADSUPERIOR='"+gl.um+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();

            umb=dt.getString(0);
            fact=dt.getDouble(1);
            if (dt!=null) dt.close();
        } catch (Exception e) {
            umb = um;fact = 1;
        }

        cantbas=cant*fact;
        peso=mu.round(gl.dpeso*gl.umfactor,gl.peDec);
        prodtot=mu.round(prec*cant,2);

        try {
            sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            newid=dt.getInt(0)+1;
        } catch (Exception e) {
            newid=1;
        }

        /*
        try {
            sql="SELECT MAX(ID) FROM T_ORDEN";
            //sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            nid1=dt.getInt(0)+1;
        } catch (Exception e) {
            nid1=1;
        }

        try {
            sql="SELECT MAX(ID) FROM T_orden_cor";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            nid2=dt.getInt(0)+1;
        } catch (Exception e) {
            nid2=1;
        }

        newid=nid2;if (nid1>newid) newid=nid1;

        try {
            db.execSQL("UPDATE T_orden_cor SET ID="+newid);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

         */

        try {

            if (sinimp) precdoc=precsin; else precdoc=prec;

            ins.init("T_ORDEN");

            ins.add("ID",newid);
            ins.add("COREL",idorden);
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
            ins.add("CUENTA",1);
            ins.add("ESTADO",1);

            db.execSQL(ins.sql());

            counter++;
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        try {
            sql="DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (CANT=0)";
            db.execSQL(sql);
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
        }

        if (actorden) envioOrden();
        listItems();

        return true;
    }

    private boolean addItemVenta(){
        clsClasses.clsT_venta venta,ventau=null;
        clsClasses.clsT_combo combo;
        clsClasses.clsT_ordencombo citem;

        double tt;
        String sv;
        int itemid,vv,mv;

        try {
            T_ventaObj.fill();
            if (T_ventaObj.count>0) {
                mv=0;
                for (int i = 0; i <T_ventaObj.count; i++) {
                    sv=T_ventaObj.items.get(i).empresa;
                    try {
                        vv=Integer.parseInt(sv);
                        if (vv>mv) mv=vv;
                    } catch (Exception e) { }
                }
                if (mv>0) itemid=mv+1;else itemid=100;
            } else {
                itemid=100;
            }

            int itemmax=T_ventaObj.newID("SELECT MAX(EMPRESA) FROM T_VENTA");
            if (itemid<itemmax) itemid=itemmax;

            venta=clsCls.new clsT_venta();

            venta.producto=oitem.producto;
            venta.empresa=""+itemid;
            venta.um=oitem.um;
            venta.cant=oitem.cant;
            venta.umstock=oitem.umstock;
            venta.factor=oitem.factor;
            venta.precio=oitem.precio;
            venta.imp=oitem.imp;
            venta.des=oitem.des;
            venta.desmon=oitem.desmon;
            venta.total=oitem.total;
            venta.preciodoc=oitem.preciodoc;
            venta.peso=oitem.peso;
            venta.val1=oitem.val1;
            venta.val2=oitem.val2;
            venta.val3=oitem.val3;
            venta.val4=oitem.val4;
            venta.percep=oitem.percep;

            T_ordencomboprecioObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+oitem.empresa+")");
            if (T_ordencomboprecioObj.count>0) {
                venta.precio=T_ordencomboprecioObj.first().prectotal;
                venta.preciodoc=venta.precio;
                tt=venta.cant*venta.precio;tt=mu.round2(tt);
                venta.total=tt;
                oitem.total=tt;
            }

            //#EJC20210708: Corrección en T_VENTA, consultar antes si el producto ya existe e incrementar la cantidad
            //para evitar que devuelva error de llave.
            Cursor dt;
            double prodtot=0;

            try {

                //#jp20220209
                T_ventaObj.fill("WHERE (PRODUCTO='"+venta.producto+"') AND (UM='"+venta.um+"')");
                if (T_ventaObj.count>0) ventau=T_ventaObj.first();

                if (app.prodTipo(venta.producto).equalsIgnoreCase("M")){
                    T_ventaObj.add(venta);
                } else {
                    if (T_ventaObj.count==0) {
                        T_ventaObj.add(venta);
                    } else {
                        ventau.cant+= oitem.cant;
                        ventau.total = ventau.total+mu.round(oitem.total,2);
                        ventau.imp = ventau.imp + mu.round(oitem.imp,2);
                        T_ventaObj.update(ventau);
                    }
                }

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }


            if (app.prodTipo(venta.producto).equalsIgnoreCase("M")) {

                T_ordencomboObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+oitem.empresa+")");
                for (int j = 0; j <T_ordencomboObj.count; j++) {

                    citem=T_ordencomboObj.items.get(j);
                    combo=clsCls.new clsT_combo();
                    combo.codigo_menu=citem.codigo_menu;
                    combo.idcombo=citem.idcombo;
                    combo.cant=citem.cant;
                    combo.unid=citem.unid;
                    combo.idseleccion=citem.idseleccion;
                    combo.orden=citem.orden;

                    T_comboObj.add(combo);
                }
            }

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    private boolean addItemVentaOrig(){
        clsClasses.clsT_venta venta,ventau=null;
        clsClasses.clsT_combo combo;
        clsClasses.clsT_ordencombo citem;

        double tt;
        int itemid;

        try {

            itemid=T_ventaObj.newID("SELECT MAX(EMPRESA) FROM T_VENTA");

            venta=clsCls.new clsT_venta();
            venta.producto=oitem.producto;
            venta.empresa=""+itemid;
            venta.um=oitem.um;
            venta.cant=oitem.cant;
            venta.umstock=oitem.umstock;
            venta.factor=oitem.factor;
            venta.precio=oitem.precio;
            venta.imp=oitem.imp;
            venta.des=oitem.des;
            venta.desmon=oitem.desmon;
            venta.total=oitem.total;
            venta.preciodoc=oitem.preciodoc;
            venta.peso=oitem.peso;
            venta.val1=oitem.val1;
            venta.val2=oitem.val2;
            venta.val3=oitem.val3;
            venta.val4=oitem.val4;
            venta.percep=oitem.percep;

            T_ordencomboprecioObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+oitem.empresa+")");

            if (T_ordencomboprecioObj.count>0) {
                venta.precio=T_ordencomboprecioObj.first().prectotal;
                venta.preciodoc=venta.precio;
                tt=venta.cant*venta.precio;tt=mu.round2(tt);
                venta.total=tt;
                oitem.total=tt;
            }

            //#EJC20210708: Corrección en T_VENTA, consultar antes si el producto ya existe e incrementar la cantidad
            //para evitar que devuelva error de llave.
            Cursor dt;
            double prodtot=0;

            try {

                //#jp20220209
                T_ventaObj.fill("WHERE (PRODUCTO='"+venta.producto+"') AND (UM='"+venta.um+"')");
                if (T_ventaObj.count>0) ventau=T_ventaObj.first();

                if (app.prodTipo(venta.producto).equalsIgnoreCase("M")){
                    T_ventaObj.add(venta);
                } else {
                    if (T_ventaObj.count==0) {
                        T_ventaObj.add(venta);
                    } else {
                        ventau.cant+= oitem.cant;
                        ventau.total = ventau.total+mu.round(oitem.total,2);
                        T_ventaObj.update(ventau);
                    }
                }


                /*
                String vsql="SELECT CANT FROM T_venta WHERE (PRODUCTO='"+venta.producto+"') AND (UM='"+venta.um+"')";
                dt=Con.OpenDT(vsql);

                if (dt!=null){
                    //#ejc20210712: condición agregada con Jaros, para validar productos de tipo combo.
                    if (app.prodTipo(venta.producto).equalsIgnoreCase("M")){
                        T_ventaObj.add(venta);
                    }else{
                        if (dt.getCount()==0) {
                            T_ventaObj.add(venta);
                        } else {
                            venta.cant+= oitem.cant;
                            //prodtot=mu.round(venta.precio*venta.cant,2);
                            prodtot=venta.total+mu.round(oitem.total,2);
                            venta.total = prodtot;
                            T_ventaObj.update(venta);
                        }
                    }
                };
                */

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }


            if (app.prodTipo(venta.producto).equalsIgnoreCase("M")) {

                T_ordencomboObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+oitem.empresa+")");
                for (int j = 0; j <T_ordencomboObj.count; j++) {

                    citem=T_ordencomboObj.items.get(j);
                    combo=clsCls.new clsT_combo();
                    combo.codigo_menu=citem.codigo_menu;
                    combo.idcombo=citem.idcombo;
                    combo.cant=citem.cant;
                    combo.unid=citem.unid;
                    combo.idseleccion=citem.idseleccion;
                    combo.orden=citem.orden;

                    T_comboObj.add(combo);
                }
            }

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    private void updItem(){
        double ptot=0;

        try {
            savetot=mu.round(saveprec*cant,2);
            ptot=mu.round(prec*cant,2);
            descmon = savetot-ptot;

            upd.init("T_ORDEN");

            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",ptot);
            upd.add("PRECIODOC",prec);

            upd.Where("(COREL='"+idorden+"') AND (PRODUCTO='"+prodid+"')");

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

            upd.init("T_ORDEN");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);

            upd.Where("(COREL='"+idorden+"') AND (ID='"+uid+"')");

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

            upd.init("T_ORDEN");
            upd.add("CANT",cant);
            upd.add("PRECIO",prec);
            upd.add("IMP",imp);
            upd.add("IMP",imp);
            upd.add("DES",desc);
            upd.add("DESMON",descmon);
            upd.add("TOTAL",prodtot);
            upd.add("PRECIODOC",precdoc);

            upd.Where("(COREL='"+idorden+"') AND (EMPRESA='"+uid+"')");

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
            db.beginTransaction();

            db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDENCOMBO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOAD WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBODET WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOPRECIO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");

            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (PUID="+gl.produid+")");

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    private void borrarItemComanda() {
        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDENCOMBO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOAD WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBODET WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOPRECIO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");

            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (PUID="+gl.produid+")");

            db.setTransactionSuccessful();
            db.endTransaction();

            try {
                String cmd = "DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")" + ";";
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent)
                 */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }


    }

    private void delItemIng(){
        int puid;

        try {
            db.beginTransaction();

            T_orden_ingObj.fill("WHERE (Corel='"+gl.ordcorel+"') AND (Id="+gl.produid+")");
            for (int i = 0;i <T_orden_ingObj.count; i++) {
                puid=T_orden_ingObj.items.get(i).puid;
                db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+puid+")");
            }

            db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDENCOMBO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOAD WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBODET WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOPRECIO WHERE (COREL='"+idorden+"') AND (IdCombo="+cbui+")");

            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (PUID="+gl.produid+")");

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    private void aplicarPreimpresion() {
        try {
            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            sess.estado=2;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            envioMesa(2,true);
            gl.cerrarmesero=true;gl.mesero_lista=true;
            cerrarOrden();
            //gl.cerrarmesero=true;
            //startActivity(new Intent(this,ResCaja.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicarPago() {
        if (!pendientesPago()) {
            msgbox("No existe cuenta pendiente de pago.");return;
        }

        try {

            sql= "UPDATE T_orden SET PERCEP=1 WHERE (COREL='"+idorden+"') AND (PERCEP=0)" + ";";
            db.execSQL(sql);

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            sess.estado=3;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            envioMesa(3,true);

            gl.cerrarmesero=true;gl.mesero_lista=true;
            cerrarOrden();
            //gl.cerrarmesero=true;
            //startActivity(new Intent(this,ResCaja.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cerrarCuentas(int modo) {

        try {

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            sess.estado=-1;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            if (modo>0) {
                envioMesa(-1,false);
                P_res_sesionObj.update(sess);
                if (actorden)  broadcastJournalFlagAll(99,false);
                toast("La mesa fue pagada");
            } else {
                envioMesa(-1,false);
                P_res_sesionObj.update(sess);
                if (actorden)  broadcastJournalFlagAll(99,true);
                gl.cerrarmesero=true;gl.mesero_lista=true;
                if (modo==-9) toastcentlong("Las cuentas de la órden han sido cerradas");
                cerrarOrden();
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void splitItem() {
        clsClasses.clsT_orden item,fitem;
        int cnt,newid;
        double ttot;

        try {
            db.beginTransaction();

            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ID="+selitem.id+")");
            fitem=T_ordenObj.first();
            cnt=(int) fitem.cant;if (cnt<=1) return;

            newid=T_ordenObj.newID("SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')");

            fitem.cant=1;
            ttot=fitem.cant*fitem.precio;ttot=mu.round2(ttot);
            fitem.total=ttot;
            T_ordenObj.update(fitem);

            for (int i = 1; i <cnt; i++) {
                //item = clsCls.new clsT_orden();
                item = fitem;
                item.empresa=""+newid;
                item.id=newid;newid++;
                item.cant=1;
                ttot=item.cant*item.precio;ttot=mu.round2(ttot);
                item.total=ttot;

                T_ordenObj.add(item);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            envioCuentas();
            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Precuenta

    private void crearVenta() {

        int prdcnt=0;

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());return;
        }

        try {

            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            if (P_res_sesionObj.count==0) {
                msgbox("No se puede crear factura. Por favor anule la e ingrese la de nuevo.");return;
            }

            gl.mesero_venta=P_res_sesionObj.first().vendedor;

            gl.numero_orden=idorden+"_"+cuenta;
            T_ordenObj.fill("WHERE COREL='"+idorden+"'");
            counter=0;

            for (int i = 0; i <T_ordenObj.count; i++) {
                oitem=T_ordenObj.items.get(i);
                if (oitem.cuenta==cuenta) {
                    addItemVenta();prdcnt++;
                }
            }

            if (prdcnt==0) {
                msgbox("No se puede continuar, la cuenta no contiene ninun articulo");return;
            }

            gl.caja_est_pago="UPDATE T_ORDEN SET ESTADO=2 WHERE ((COREL='"+idorden+"') AND (CUENTA="+cuenta+"))";

            cargaCliente();

            gl.ventalock=true;
            gl.cerrarmesero=true;
            gl.mesero_lista=false;
            gl.mesero_precuenta=true;

            finalizarOrden();
            exitBtn(false);

            //finish();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void finalizarOrden(){

        try{
            clsBonifGlob clsBonG;
            clsDeGlob clsDeG;
            String s,ss;

            try{
                gl.delivery = false;
                gl.EsNivelPrecioDelivery = (gl.delivery || gl.pickup);
            } catch (Exception e){
                mu.msgbox("finishOrder: "+e.getMessage());
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
            gl.brw=0;

            Intent intent = new Intent(this,FacturaRes.class);
            startActivity(intent);

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox("finishOrder: "+e.getMessage());
        }
    }

    //endregion

    //region Menu

    private void listFamily() {
        int ii,pos;

        clsP_lineaObj P_lineaObj=new clsP_lineaObj(this,Con,db);
        clsClasses.clsMenu item;

        try {

            fitems.clear();
            P_lineaObj.fill("WHERE (Activo=1) ORDER BY NOMBRE ");

            ii=0;pos=0;

            for (int i = 0; i <P_lineaObj.count; i++) {

                item=clsCls.new clsMenu();

                item.Cod=P_lineaObj.items.get(i).codigo+"";
                item.Name=P_lineaObj.items.get(i).nombre;
                item.icod=P_lineaObj.items.get(i).codigo_linea;
                item.val=P_lineaObj.items.get(i).marca;

                app.setGradResource(pos);
                item.idres=gl.idgrres;
                item.idressel=gl.idgrsel;

                fitems.add(item);ii++;

                if (ii==2) {
                    ii=0;pos++;if (pos==6) pos=0;
                }
            }

            if (!horiz) imgflag=false;
            if (imgflag) {
                adapterf=new ListAdaptGridFam(this,fitems,imgfold,true);
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
            sql += "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA,P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO " +
                    "FROM P_PRODUCTO  INNER JOIN " +
                    "P_PRODPRECIO ON P_PRODUCTO.CODIGO_PRODUCTO = P_PRODPRECIO.CODIGO_PRODUCTO  " +
                    "WHERE ((P_PRODUCTO.CODIGO_TIPO ='S') OR (P_PRODUCTO.CODIGO_TIPO ='M') OR (P_PRODUCTO.CODIGO_TIPO ='PB')) " +
                    "AND (P_PRODUCTO.ACTIVO=1)";
            if (famid !=-1) {
                if (famid!=0)
                    sql = sql + "AND (P_PRODUCTO.LINEA=" + famid + ") ";
            }
             */

            sql = "SELECT DISTINCT P_PRODUCTO.CODIGO,P_PRODUCTO.DESCCORTA,P_PRODPRECIO.UNIDADMEDIDA,P_PRODUCTO.ACTIVO, P_PRODUCTO.CODIGO_PRODUCTO " +
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
                        item.Name=dt.getString(1)+" \n[ "+gl.peMon+prodPrecioBaseImp(item.icod)+" ]";
                        item.valor=gl.dval;
                        item.idres=gl.idgrres;
                        item.idressel=gl.idgrsel;

                        pitems.add(item);pcodes.add(pcode);
                    }
                }

                dt.moveToNext();
            }
            if (dt!=null) dt.close();
        } catch (Exception e) 		{
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }

        if (!horiz) imgflag=false;
        if (imgflag) {
            adapterp=new ListAdaptGridProd(this,pitems,imgfold,true);
            grdprod.setAdapter(adapterp);
        } else {
            adapterpl=new ListAdaptGridProdList(this,pitems,imgfold,horiz);
            grdprod.setAdapter(adapterpl);
        }

    }

    private void menuTools() {

        clsClasses.clsMenu item;

        mitems.clear();

        try {

            if (gl.peImpOrdCos) {
                item = clsCls.new clsMenu();
                item.ID=66;
                item.Name="Comandas";
                item.Icon=66;
                mitems.add(item);
            }

            item = clsCls.new clsMenu();
            item.ID = 73;
            item.Name = "Precuenta";
            item.Icon = 73;
            mitems.add(item);

            /*
            if (gl.pelMeseroCaja) {
                if (!app.modoSinInternet()) {
                    item = clsCls.new clsMenu();
                    item.ID = 3;
                    item.Name = "Precuenta caja";
                    item.Icon = 68;
                    mitems.add(item);
               }
            }
            */

            /*
            item = clsCls.new clsMenu();
            item.ID = 1;
            item.Name = "Pago";
            item.Icon = 67;
            mitems.add(item);
             */

            item = clsCls.new clsMenu();
            item.ID = 0;
            item.Name = "---------";
            item.Icon = 0;
            mitems.add(item);

            if (barril) {
                item = clsCls.new clsMenu();
                item.ID = 78;
                item.Name = "Barriles";
                item.Icon = 78;
                mitems.add(item);
            }

            /*
            item = clsCls.new clsMenu();
            item.ID = 1;
            item.Name = "Buscar";
            item.Icon = 5;
            mitems.add(item);

             */

            /*
            if (actorden) {
                item = clsCls.new clsMenu();
                item.ID = 77;
                item.Name = "Actualizar cuenta";
                item.Icon = 10;
                mitems.add(item);
            }

             */

            if (gl.peImpOrdCos) {
               if (!gl.pelComandaBT) {
                    item = clsCls.new clsMenu();
                    item.ID = 75;
                    item.Name = "Cambiar impresora";
                    item.Icon = 75;
                    mitems.add(item);
                }
            }

            item = clsCls.new clsMenu();
            item.ID = 74;
            item.Name = "Borrar cuentas";
            item.Icon = 74;
            mitems.add(item);

            /*
            if (gl.pelMeseroCaja) {
                if (!app.modoSinInternet()) {
                    item = clsCls.new clsMenu();
                    item.ID = 76;
                    item.Name = "Enviar cuentas a caja";
                    item.Icon = 10;
                    mitems.add(item);
                }
            }


            */

            adaptergrid=new ListAdaptMenuOrden(this, mitems,horiz);
            gridView.setAdapter(adaptergrid);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuTools(int menuid) {
        String s="";

        try {

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            int est=P_res_sesionObj.first().estado;

            switch (menuid) {

                case 1:

                    if (!hasProducts()) {
                        msgbox("La venta está vacia.");return;
                    }
                    if (!pendientesPago()) {
                        msgbox("No existe cuenta pendiente de pago.");return;
                    }

                    //if (pendienteImpresion()) {
                    //    msgbox("Existen articulos pendientes de impresion, no se puede proceder con pago.");return;
                    //}

                    if (modo_emerg) {
                        msgAskPagoCaja("Enviar solicitud de pago a la caja");
                    } else {
                        if (enviarorden) {
                            actualizaEstadoOrden(1);
                        } else {
                            msgAskPagoCaja("Enviar solicitud de pago a la caja");
                        }
                    }

                    break;

                case 2:

                    if (!hasProducts()) {
                        msgbox("La venta está vacia.");return;
                    }
                    if (pendientesPago()) {
                        //if (!app.validaCompletarCuenta(idorden)) {
                        msgbox("No se puede completar la mesa,\nexiste cuenta pendiente de pago.");return;
                    }
                    msgAskCuentas("Completar la mesa");
                    break;

                case 3:
                    if (!hasProducts()) {
                        msgbox("La venta está vacia.");return;
                    }
                    //if (pendienteImpresion()) {
                    //    msgbox("Existen articulos pendientes de impresion, no se puede proceder con la precuenta.");
                    //} else {
                        if (enviarorden) {
                            actualizaEstadoOrden(2);
                        } else {
                            msgAskPrint("Enviar impresión de precuenta a la caja");
                        }
                    //}
                    break;

                case 5:
                    doAdd(null);break;

                case 50:
                    gl.gstr = "";browse=1;gl.prodtipo=1;
                    startActivity(new Intent(this, Producto.class));
                    break;

                case 66:
                    if (!hasProducts()) {
                        msgbox("La venta está vacia.");return;
                    }
                    //actualizaEstadoOrden(3);
                    msgAskComanda();
                    break;

                case 73:
                    if (!hasProducts()) {
                        msgbox("La venta está vacia.");return;
                    }
                    if (pendienteImpresion()) {
                        msgbox("Existen articulos pendientes de impresion, no se puede proceder con la precuenta.");
                    } else {
                        if (limpiaVenta()) {
                            showListaCuentas();
                        }
                    }

                    break;

                case 74:
                    //browse=11;
                    //startActivity(new Intent(Orden.this,ValidaSuper2.class));
                    //msgBorrarOrden("Cerrar todas las cuentas del orden");
                    valsupermodo=0;
                    validaSupervisor();
                    break;

                case 75:
                    msgAskImpresora("Este proceso redirecciona impresión de una impresora que dejo funcionar a otra.\n¿Continuar?");
                    break;

                case 76:
                    msgAskEnvioCaja("Enviar cuentas a la caja");
                    break;

                case 77:
                    msgAskReenvio("Actualizar cuentas");
                    break;

                case 78:
                    startActivity(new Intent(this,Barriles.class));
                    break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void exitBtn(boolean enviar) {
        try {
            //if (cantRegBarril()==0) {

            cerrarOrden();

            gl.cerrarmesero=true;
            gl.mesero_lista=true;

            try {
                //ctimer.removeCallbacks(crunner);
            } catch (Exception e) {}


            //} else {
            //  evnioBarril(true);
            //}
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Numero orden

    private void numeroOrden() {
        ordencentral=true;
        if (gl.pelOrdenComanda | gl.peNumOrdCommandaVenta) {
            if (gl.peNumOrdCentral) {
                numeroOrdenCentral();
            } else {
                numeroOrdenLocal();
            }
        } else {
            numeroOrdenLocal();
        }
    }

    private void numeroOrdenLocal() {

        if (gl.pelOrdenComanda | gl.peNumOrdCommandaVenta) {
            try {
                ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
                if (ordennum % gl.peMaxOrden==0) ordennum++;

                clsClasses.clsP_orden_numero orditem = clsCls.new clsP_orden_numero();
                orditem.id=ordennum;
                P_orden_numeroObj.add(orditem);

                try {
                    int ilimit=ordennum-10;
                    db.execSQL("DELETE FROM P_orden_numero WHERE ID<"+ilimit);
                } catch (Exception e) { }

                ordennum=ordennum % gl.peMaxOrden;if (ordennum==0) ordennum=1;

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            lblAlm.setText("#"+ordennum);toast("#"+ordennum);
            procesaComanda();
        } else {
            procesaComanda();
        }
    }

    private void numeroOrdenContingencia() {
        ordencentral=false;

        if (!db.isOpen()) onResume();

        if (gl.pelOrdenComanda | gl.peNumOrdCommandaVenta) {
            try {
                ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
                if (ordennum % gl.peMaxOrden==0) ordennum++;

                clsClasses.clsP_orden_numero orditem = clsCls.new clsP_orden_numero();
                orditem.id=ordennum;
                P_orden_numeroObj.add(orditem);

                try {
                    int ilimit=ordennum-10;
                    db.execSQL("DELETE FROM P_orden_numero WHERE ID<"+ilimit);
                } catch (Exception e) { }

                ordennum=ordennum % gl.peMaxOrden;if (ordennum==0) ordennum=1;
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            lblAlm.setText("#"+ordennum);toastlong("#"+ordennum);
            procesaComanda();
        }
    }

    private void numeroOrdenCentral() {
        gl.ref1="";lblAlm.setText("# ...");

        try {
            sql="INSERT INTO T_ORDEN_CODIGO (ID_ORDEN,CODIGO_SUCURSAL,CODIGO_RUTA) " +
                    "SELECT ISNULL(MAX(ID_ORDEN)+1,1),"+gl.tienda+","+gl.codigo_ruta+" " +
                    "FROM T_ORDEN_CODIGO WHERE CODIGO_SUCURSAL="+gl.tienda;
            wscom.execute(sql,rnOrdenInsert);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            numeroOrdenContingencia();
        }
    }

    private void ordenInsert() {
        if (!wscom.errflag) {
            try {
                sql="SELECT MAX(ID_ORDEN) FROM T_ORDEN_CODIGO WHERE CODIGO_SUCURSAL="+gl.tienda;
                wso.execute(sql,rnOrdenQuery);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                numeroOrdenContingencia();
            }
        } else {
            toast(wscom.error);
            numeroOrdenContingencia();
        }
    }

    private void ordenQuery() {
        if (!wso.errflag) {
            try {
                wso.openDTCursor.moveToFirst();
                ordennum=wso.openDTCursor.getInt(0);
                ordennum=ordennum % gl.peMaxOrden;if (ordennum==0) ordennum=1;

                lblAlm.setText("#"+ordennum);toast("#"+ordennum);
                procesaComanda();
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                numeroOrdenContingencia();
            }
        } else {
            toast(wso.error);
            numeroOrdenContingencia();
        }
    }

    //endregion

    //region Comanda

    private void imprimeComanda() {
        numeroOrden();
        //procesaComanda();
    }

    private void procesaComanda() {
        if (!db.isOpen()) onResume();

        if (gl.pelComandaBT) {
            imprimeComandaBT();
            evnioBarril(false);
        } else {
            if (!divideComanda()) return;
            if (!generaArchivos()) return;
            //generaRegistrosBarril();
            ejecutaImpresion();

            aplicaImpresion();
            if (actorden) envioOrden();else cerrarOrden();

            exitBtn(false);
        }

    }

    private boolean divideComanda() {
        clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
        clsT_ordencomboObj T_comboObj=new clsT_ordencomboObj(this,Con,db);
        clsClasses.clsT_orden venta;
        clsClasses.clsT_ordencombo combo;

        String prname,cname,nn;
        int prodid,prid=0,idcomb,pruid,linea=1,paso=0;

        try {

            //clsP_orden_numeroObj P_orden_numeroObj=new clsP_orden_numeroObj(this,Con,db);
            //ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");

            //clsClasses.clsP_orden_numero ord = clsCls.new clsP_orden_numero();
            //ord.id=ordennum;
            //P_orden_numeroObj.add(ord);

            db.execSQL("DELETE FROM T_comanda");

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");
            if (T_ordenObj.count>0) {
                for (int i = 0; i < T_ordenObj.count; i++) {
                    paso=1;
                    venta = T_ordenObj.items.get(i);

                    pruid = venta.id;
                    prodid = app.codigoProducto(venta.producto);
                    prname = getProd(prodid);
                    s = mu.frmdecno(venta.cant) + "  " + prname;

                    nn = "";
                    T_orden_notaObj.fill("WHERE (id=" + venta.id + ") AND (corel='" + idorden + "')");
                    if (T_orden_notaObj.count > 0) nn = T_orden_notaObj.first().nota + "";

                    paso=2;
                    if (!app.prodTipo(prodid).equalsIgnoreCase("M")) {

                        P_linea_impresoraObj.fill("WHERE CODIGO_LINEA=" + prodlinea);

                        if (P_linea_impresoraObj.count > 0) {
                            paso=3;
                            for (int k = 0; k < P_linea_impresoraObj.count; k++) {

                                if (!esIngrediente(prodid)) {

                                    prid = P_linea_impresoraObj.items.get(k).codigo_impresora;
                                    agregaComanda(linea, prid, s);
                                    linea++;
                                    if (!nn.isEmpty()) {
                                        agregaComanda(linea, prid, nn);
                                        linea++;
                                    }

                                    T_orden_modObj.fill("WHERE (COREL='" + idorden + "') AND (ID=" + pruid + ")");
                                    paso=4;
                                    if (T_orden_modObj.count > 0) {
                                        for (int ii = 0; ii < T_orden_modObj.count; ii++) {
                                            nn = "   " + T_orden_modObj.items.get(ii).nombre;
                                            agregaComanda(linea, prid, nn);
                                            linea++;
                                        }
                                    }

                                    T_orden_ingObj.fill("WHERE (Corel='" + gl.ordcorel + "') AND (Id=" + pruid + ") ORDER BY Nombre");
                                    paso=5;
                                    if (T_orden_ingObj.count > 0) {
                                        for (int ii = 0; ii < T_orden_ingObj.count; ii++) {
                                            nn = "   " + T_orden_ingObj.items.get(ii).nombre;
                                            agregaComanda(linea, prid, nn);
                                            linea++;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        paso=10;
                        T_comboObj.fill("WHERE (IdCombo=" + venta.val4 + ") AND (IdSeleccion<>0)");
                        idcomb = mu.CInt(venta.val4);
                        idcomb = idcomb % 100;
                        //cname=s+" [#"+idcomb+"]";
                        cname = s;

                        for (int j = 0; j < T_comboObj.count; j++) {
                            prodid = T_comboObj.items.get(j).idseleccion;
                            //s = " - " + getProd(prodid);
                            s = " " + getProd(prodid);
                            P_linea_impresoraObj.fill("WHERE CODIGO_LINEA=" + prodlinea);

                            for (int k = 0; k < P_linea_impresoraObj.count; k++) {
                                prid = P_linea_impresoraObj.items.get(k).codigo_impresora;
                                if (j == 0) {
                                    agregaComanda(linea, prid, cname);
                                    linea++;
                                }
                                agregaComanda(linea, prid, s);
                                linea++;
                                if (!nn.isEmpty()) {
                                    agregaComanda(linea, prid, nn);
                                    linea++;
                                }

                                if (k == P_linea_impresoraObj.count - 1) {
                                    T_orden_modObj.fill("WHERE (COREL='" + idorden + "') AND (ID=" + pruid + ")");
                                    for (int ii = 0; ii < T_orden_modObj.count; ii++) {
                                        nn = "  " + T_orden_modObj.items.get(ii).nombre;
                                        agregaComanda(linea, prid, nn);
                                        linea++;
                                    }
                                }
                            }
                        }
                    }

                }
            }
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage()+"\nPaso: "+paso);return false;
        }
    }

    private boolean agregaComanda(int linea,int prid,String texto) {
        try {
            clsClasses.clsT_comanda item = clsCls.new clsT_comanda();

            item.linea=linea;
            item.id=prid;
            item.texto=texto;

            T_comandaObj.add(item);
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean generaArchivos() {

        clsRepBuilder rep;
        int printid,ln;
        String fname,ss,narea,prip;
        File file;

        try {

            P_impresoraObj.fill();
            for (int i = 0; i <P_impresoraObj.count; i++) {
                fname = Environment.getExternalStorageDirectory()+"/comanda_"+P_impresoraObj.items.get(i).codigo_impresora+".txt";
                file=new File(fname);
                try {
                    file.delete();
                } catch (Exception e) { }
            }
        } catch (Exception e) { }

        try {

            clsViewObj ViewObj=new clsViewObj(this,Con,db);
            ViewObj.fillSelect("SELECT DISTINCT ID, '','','','', '','','','' FROM T_comanda ORDER BY ID");

            for (int i = 0; i <ViewObj.count; i++) {
                printid=ViewObj.items.get(i).pk;

                if (printid>0) {

                    P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA=" + printid + ")");

                    if (P_impresoraObj.count>0) {
                        rep = new clsRepBuilder(this, gl.prw, true, gl.peMon, gl.peDecImp, "comanda_" + printid + ".txt");

                        rep.add(P_impresoraObj.first().tipo_impresora);
                        rep.add(P_impresoraObj.first().nombre);
                        prip=app.ipBypass(P_impresoraObj.first().ip);
                        rep.add(prip);

                        rep.empty();
                        //rep.empty();
                        //rep.empty();
                        //rep.empty();
                        //rep.empty();
                        //rep.empty();

                        //ordenpedido=numpedido>0;

                        narea="";
                        if (gl.emp==30) {
                            narea = " " + gl.mesa_area;
                        }

                        if (ordenpedido) {
                            rep.add("ORDEN : #" + numpedido);
                            rep.empty();
                            if (numpedido == 0) rep.add("MESA : " + mesa+narea);
                            if (!gl.mesa_alias.isEmpty()) rep.add(gl.mesa_alias);
                            rep.add("Hora : " + du.shora(du.getActDateTime())+ "   "+du.sfecha(du.getActDateTime()));
                            rep.add("Mesero : " + gl.nombre_mesero_sel);
                        } else {
                            rep.add("ORDEN : " + ordennum);
                            rep.empty();
                            rep.add("MESA : " + mesa+narea);
                            if (!gl.mesa_alias.isEmpty()) rep.add(gl.mesa_alias);
                            rep.add("Hora : " + du.shora(du.getActDateTime())+ "   "+du.sfecha(du.getActDateTime()));
                            rep.add("Mesero : " + gl.nombre_mesero_sel);
                        }

                        rep.line24();

                        T_comandaObj.fill("WHERE ID=" + printid + " ORDER BY LINEA");
                        //T_comandaObj.fillSelect("SELECT COUNT(ID),ID,TEXTO WHERE ID="+printid+" GROUP BY ID,TEXTO");

                        tl.clear();
                        for (int j = 0; j < T_comandaObj.count; j++) {
                            ss = T_comandaObj.items.get(j).texto;
                            if (ss.indexOf(" - ") == 0) {
                                tl.add(ss.toUpperCase());
                            } else {
                                if (gl.emp == 14) {
                                    if (!itemexists(ss)) tl.add(ss.toUpperCase());
                                } else {
                                    tl.add(ss.toUpperCase());
                                }
                            }
                        }

                        for (int j = 0; j < tl.size(); j++) {
                            rep.add(tl.get(j));
                        }

                        //for (int j = 0; j <T_comandaObj.count; j++) {
                        //    rep.add(T_comandaObj.items.get(j).texto);
                        //}

                        rep.line24();
                        rep.add("");
                        if (gl.mesa_grupo == 19) rep.add("PARA LLEVAR");
                        if (ordenpedido) rep.add("PARA LLEVAR");
                        rep.add("");
                        rep.add("");

                        ln = rep.items.size();
                        if (ln < 20) {
                            for (int ii = 0; ii < 20 - ln; ii++) {
                                rep.empty();
                            }
                        }

                        rep.save();
                        rep.clear();
                    }
                }
            }

            //mesa
            //rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

            return true;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean itemexists(String tli) {
        String ts;
        if (tl.size()==0) return false;

        for (int i = 0; i <tl.size(); i++) {
            ts=tl.get(i);
            if (ts.equalsIgnoreCase(tli)) return true;
        }

        return false;
    }

    private void ejecutaImpresion() {
        try {
            app.print3nstarw();
            actualizaEstado();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void actualizaEstado() {
        try {
            db.execSQL("UPDATE T_ORDEN SET ESTADO=0 WHERE (COREL='"+idorden+"') AND (ESTADO=1)");
            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void imprimeComandaBT() {
        clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
        clsT_ordencomboObj T_comboObj = new clsT_ordencomboObj(this, Con, db);
        clsClasses.clsT_orden venta;
        clsClasses.clsT_ordencombo combo;
        String prname, csi;
        int prid;

        try {
            T_ordenObj.fill("WHERE (COREL='" + idorden + "') AND (ESTADO=1)");

            if (T_ordenObj.count == 0) {
                msgInfo("Ninguno artículo está marcado para la impresión");
                return;
            }

            //clsP_orden_numeroObj P_orden_numeroObj = new clsP_orden_numeroObj(this, Con, db);
            ordennum = P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
            clsClasses.clsP_orden_numero ord = clsCls.new clsP_orden_numero();
            ord.id = ordennum;
            P_orden_numeroObj.add(ord);

            ordencentral=false;

            rep.clear();
            rep.empty();
            rep.add("ORDEN : "+ordennum);
            rep.empty();
            rep.add("MESA : "+mesa);
            rep.add("Hora : "+du.shora(du.getActDateTime()));
            rep.empty();
            rep.line();
            rep.empty();

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");

            for (int i = 0; i <T_ordenObj.count; i++) {
                venta=T_ordenObj.items.get(i);

                prid = app.codigoProducto(venta.producto);
                prname=getProd(prid);
                s = mu.frmdecno(venta.cant) + " " + prname;
                rep.add(s);

                if (app.prodTipo(prid).equalsIgnoreCase("M")) {
                    T_comboObj.fill("WHERE IdCombo=" + venta.val4);

                    for (int j = 0; j < T_comboObj.count; j++) {
                        if (j == 0) rep.line();
                        csi=getProd(T_comboObj.items.get(j).idseleccion);
                        if (!csi.equalsIgnoreCase("0")) s =" -  "+csi;
                        rep.add(s);
                    }
                    rep.line();
                }
            }

            rep.line();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.save();

            app.doPrint(1);

            sql="UPDATE T_orden SET ESTADO=0 WHERE (COREL='"+idorden+"')";
            db.execSQL(sql);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String getProd(int prodid) {
        try {
            P_productoObj.fill();
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) {
                    prodlinea=P_productoObj.items.get(i).linea;
                    return P_productoObj.items.get(i).desclarga;
                }
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

    /*
    private String getProdCorto(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) {
                    prodlinea=P_productoObj.items.get(i).linea;
                    return P_productoObj.items.get(i).desccorta;
                }
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

     */

    //endregion

    //region Barril

    private void evnioBarril(boolean exit) {
        exit_mode=exit;
        btrpos=0;
        if (D_barril_transObj.count>0) {
            ejecutaEnvioTrans();
        } else {
            exitBarril();
        }
    }

    private void ejecutaEnvioTrans() {
        String cmd;

        try {
            btrans=D_barril_transObj.items.get(btrpos);
            cmd=addItemBarril(btrans);
            wsbtr.execute(cmd,rnBarTrans);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioBarrilTrans() {
        try {
            if (!wsbtr.errflag) {
                btrans.statcom=1;
                D_barril_transObj.update(btrans);
            } else {
                String ee=wsbtr.error;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        btrpos++;
        if (btrpos<D_barril_transObj.count) {
            ejecutaEnvioTrans();
        } else {
            exitBarril();
        }
    }

    private void generaRegistrosBarril() {
        clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
        clsT_ordencomboObj T_comboObj=new clsT_ordencomboObj(this,Con,db);
        clsClasses.clsT_orden venta;

        int prodid;

        try {
            Cursor dt=Con.OpenDT("SELECT MAX(CODIGO_TRANS) FROM D_barril_trans");
            dt.moveToFirst();
            idtransbar=dt.getInt(0)+1;
        } catch (Exception e) {
            idtransbar=1;
        }


        try {

            //clsP_orden_numeroObj P_orden_numeroObj=new clsP_orden_numeroObj(this,Con,db);
            ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
            clsClasses.clsP_orden_numero ord = clsCls.new clsP_orden_numero();
            ord.id=ordennum;
            P_orden_numeroObj.add(ord);

            db.execSQL("DELETE FROM T_comanda");

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");

            for (int i = 0; i <T_ordenObj.count; i++) {
                venta=T_ordenObj.items.get(i);
                prodid = app.codigoProducto(venta.producto);

                if (!app.prodTipo(prodid).equalsIgnoreCase("M")) {
                   if (app.esProductoBarril(prodid)) {
                       agregaRegistroBarril(gl.bar_prod,gl.bar_cant, venta.cant);
                   }
                } else {
                    T_comboObj.fill("WHERE (IdCombo=" + venta.val4+") AND (IdSeleccion<>0)");
                    for (int j = 0; j < T_comboObj.count; j++) {
                        prodid=T_comboObj.items.get(j).idseleccion;
                        if (app.esProductoBarril(prodid)) {
                           agregaRegistroBarril(gl.bar_prod,gl.bar_cant, T_comboObj.items.get(j).cant);
                        }
                    }
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void agregaRegistroBarril(int idprod,double factor,double cant) {

        if (app.barrilProd(idprod).isEmpty()) return;

        try {
            ins.init("D_barril_trans");

            ins.add("CODIGO_TRANS",idtransbar);
            ins.add("EMPRESA",gl.emp);
            ins.add("CODIGO_SUCURSAL",gl.tienda);
            ins.add("FECHAHORA",du.getActDateTime());
            ins.add("CODIGO_BARRIL",gl.bar_idbarril);
            ins.add("CODIGO_PRODUCTO",idprod);
            ins.add("CANTIDAD",factor*cant);
            ins.add("UM",gl.bar_um);
            ins.add("MESERO",gl.idmesero);
            ins.add("TIPO_MOV",1);
            ins.add("IDTRANS",idorden);
            ins.add("STATCOM",0);

            db.execSQL(ins.sql());

        } catch (Exception e) {
            String ee=e.getMessage()+"\n"+ins.sql();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        idtransbar++;
    }

    private int cantRegBarril() {
        try {
            D_barril_transObj.fill("WHERE STATCOM=0");
            return D_barril_transObj.count;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
    }

    public String addItemBarril(clsClasses.clsD_barril_trans item) {

        ins.init("D_barril_trans");

        //ins.add("CODIGO_TRANS",item.codigo_trans);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHAHORA",du.univfechahora(item.fechahora));
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UM",item.um);
        ins.add("MESERO",item.mesero);
        ins.add("TIPO_MOV",item.tipo_mov);
        ins.add("IDTRANS",item.idtrans);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    private void exitBarril() {
        if (exit_mode) {
            gl.cerrarmesero=true;gl.mesero_lista=true;
            cerrarOrden();
        }
    }

    //endregion

    //region Envio

    private void envioMesa(int estado,boolean cerrar) {

       String cmd="";

       if (estado!=1) estadoMesa(estado);

        //#EJC202210222023: Intentar enviar a cocina aunque esté en modo emergencia....
       //if (modo_emerg) return;
       //if (!gl.pelMeseroCaja) return;

       try {

           if (idorden.isEmpty()) {
                msgbox("envioMesa : idorden vacio");
           }

           if (estado>=1) {

               cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
               cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
               cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

               clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
               P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
               cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

               clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
               T_ordenObj.fill("WHERE (COREL='" + idorden + "')");
               for (int i = 0; i < T_ordenObj.count; i++) {
                   cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
               }

               clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
               T_ordencuentaObj.fill("WHERE (COREL='" + idorden + "')");
               for (int i = 0; i < T_ordencuentaObj.count; i++) {
                   cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
               }
           } else {
               cmd += "UPDATE P_res_sesion SET Estado=-1 WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
           }

           try {
               /*
               Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
               intent.putExtra("URL",gl.wsurl);
               intent.putExtra("command",cmd);
               if (actorden) startService(intent);
                */
               enviaCommit(cerrar,cmd);
           } catch (Exception e) {
               toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
           }

       } catch (Exception e) {
           msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
       }
    }

    private void estadoMesa(int est) {
        try {
            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            P_res_sesionObj.first().estado=est;
            P_res_sesionObj.update(P_res_sesionObj.first());
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioOrden() {
        String cmd="";

        //if (modo_emerg) return;
        /*  if (!actorden) {
            envioOrdenOrig();return;
        }    */

        try {

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
            cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";
            //cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournal();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioCuentas() {
        String cmd="";

        if (!actorden) return;

        try {
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournal();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioArticulos() {
        String cmd="";

        if (!actorden) return;

        try {


            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
            cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";
            //cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournal();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioOrdenDestino(String idordendest) {

        String cmd="";

        //#EJC202210222023: Intentar enviar a cocina aunque esté en modo emergencia....
        //if (modo_emerg) return;

        try {

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idordendest + "'");
            cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournalTodasCajas();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(true,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioOrdenDestinoBorrar(String idordendest) {
        String cmd="";

        try {
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idordendest + "'");
            cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournalTodasCajas();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(true,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioOrdenDestinoBorrar(String idordendest,int idcuentaorig) {
        String cmd="";

        try {
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") " +
                   "AND (COREL='" + idorden + "')  AND (CUENTA="+idcuentaorig+")" + ";";

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idordendest + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idordendest + "'");
            cmd += P_res_sesionObj.addItemSql(P_res_sesionObj.first(), gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idordendest + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            cmd+=buildDetailJournalTodasCajas();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void enviaCommit(boolean cerrar,String cmd) {
        if (!actorden) return;

        close_flag=cerrar;
        wscom.execute(cmd,rnClose);
    }

    private void closeAction() {
        try {
            if (wscom.errflag) {
                toastlong("SIN CONEXIÓN A INTERNET");
            }
            if (close_flag) {
                cerrarOrden();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Broadcast

    private void broadcastOrder() {
        String cmd="";

        try {

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
            rsitem=P_res_sesionObj.first();
            cmd += P_res_sesionObj.addItemSql(rsitem,gl.emp) + ";";

            clsT_ordenObj T_ordenObj = new clsT_ordenObj(this, Con, db);
            T_ordenObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordenObj.count; i++) {
                cmd += T_ordenObj.addItemSql(T_ordenObj.items.get(i), gl.emp) + ";";
            }

            clsT_ordencuentaObj T_ordencuentaObj = new clsT_ordencuentaObj(this, Con, db);
            T_ordencuentaObj.fill("WHERE (COREL='" + idorden + "')");
            for (int i = 0; i < T_ordencuentaObj.count; i++) {
                cmd += T_ordencuentaObj.addItemSql(T_ordencuentaObj.items.get(i), gl.emp) + ";";
            }

            wscom.execute(cmd,rnBroadcastCallback);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void broadcastCallback() {
        try {
            if (wscom.errflag) {
                String ss=wscom.error;
                addtoPending(ss);
                msgbox("Error de comunicacion :\n"+ss);
            } else {
                removeFromPending();
                broadcastJournal();
            }
            nombreMesa();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void broadcastJournalFlag(int flag) {
        clsClasses.clsT_ordencom pitem;
        int idruta;

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            String cmd="";

            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                if (idruta!=gl.codigo_ruta) {

                    pitem= clsCls.new clsT_ordencom();

                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=flag;
                    pitem.comanda="";

                    if (flag==99) pitem.comanda=updItemSqlAndroid();

                    cmd+=addItemSqlOrdenCom(pitem) + ";";
                }

            }

            try {
                /*
                Intent intent = new Intent(Orden.this, srvCommit.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                app.addToOrdenLog(du.getActDateTime(),
                        "Orden."+new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),cmd);
            }

        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void broadcastJournalFlagAll(int flag,boolean cerrar) {
        clsClasses.clsT_ordencom pitem;
        int idruta;

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            String cmd="";

            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                pitem= clsCls.new clsT_ordencom();

                pitem.codigo_ruta=idruta;
                pitem.corel_orden=idorden;
                pitem.corel_linea=flag;
                pitem.comanda="";

                if (flag==99) pitem.comanda=updItemSqlAndroid();

                cmd+=addItemSqlOrdenCom(pitem) + ";";

            }

            try {
                /*
                Intent intent = new Intent(Orden.this, srvCommit.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(cerrar,cmd);

            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
             }

        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void broadcastJournal() {
        clsClasses.clsT_ordencom pitem;
        clsClasses.clsT_ordencuenta citem;
        int idruta;

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE COREL='" + idorden + "'");

            String cmd="";

            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                if (idruta!=gl.codigo_ruta) {

                    pitem= clsCls.new clsT_ordencom();

                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=1;
                    pitem.comanda= addP_res_sesionSqlAndroid(rsitem,gl.emp);

                    cmd+=addItemSqlOrdenCom(pitem) + ";";

                    /*
                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=2;
                    pitem.comanda="";

                    cmd+=addItemSqlOrdenCom(pitem) + ";";
                    */

                    for (int c = 0; c <T_ordencuentaObj.count; c++) {
                        citem=T_ordencuentaObj.items.get(c);

                        pitem.codigo_ruta=idruta;
                        pitem.corel_orden=idorden;
                        pitem.corel_linea=3;
                        pitem.comanda= addsT_ordencuentaSqlAndroid(citem);

                        ss+=addItemSqlOrdenCom(pitem) + ";";
                    }

                }

            }

            try {
                /*
                Intent intent = new Intent(Orden.this, srvCommit.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                */
                enviaCommit(false,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                app.addToOrdenLog(du.getActDateTime(),
                        "Orden."+new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),cmd);
            }

            cerrarOrden();
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private String buildDetailJournal() {
        clsClasses.clsT_ordencom pitem;
        clsClasses.clsT_ordencuenta citem;
        int idruta;
        String ss="";

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
            rsitem=P_res_sesionObj.first();

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE COREL='" + idorden + "'");

            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                if (idruta!=gl.codigo_ruta) {

                    pitem= clsCls.new clsT_ordencom();

                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=1;
                    pitem.comanda= addP_res_sesionSqlAndroid(rsitem,gl.emp);

                    ss+=addItemSqlOrdenCom(pitem) + ";";

                    /*
                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=2;
                    pitem.comanda="";
                    ss+=addItemSqlOrdenCom(pitem) + ";";
                    */

                    for (int c = 0; c <T_ordencuentaObj.count; c++) {
                        citem=T_ordencuentaObj.items.get(c);

                        pitem.codigo_ruta=idruta;
                        pitem.corel_orden=idorden;
                        pitem.corel_linea=3;
                        pitem.comanda= addsT_ordencuentaSqlAndroid(citem);

                        ss+=addItemSqlOrdenCom(pitem) + ";";
                    }

                }
            }

            return ss;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return "";
        }
    }

    private String buildDetailJournalTodasCajas() {
        clsClasses.clsT_ordencom pitem;
        clsClasses.clsT_ordencuenta citem;
        int idruta;
        String ss="";

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            clsP_res_sesionObj P_res_sesionObj = new clsP_res_sesionObj(this, Con, db);
            P_res_sesionObj.fill("WHERE ID='" + idorden + "'");
            rsitem=P_res_sesionObj.first();

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE COREL='" + idorden + "'");


            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                pitem= clsCls.new clsT_ordencom();

                pitem.codigo_ruta=idruta;
                pitem.corel_orden=idorden;
                pitem.corel_linea=1;
                pitem.comanda= addP_res_sesionSqlAndroid(rsitem,gl.emp);

                ss+=addItemSqlOrdenCom(pitem) + ";";

                pitem.codigo_ruta=idruta;
                pitem.corel_orden=idorden;
                pitem.corel_linea=2;
                pitem.comanda="";

                ss+=addItemSqlOrdenCom(pitem) + ";";

                for (int c = 0; c <T_ordencuentaObj.count; c++) {
                    citem=T_ordencuentaObj.items.get(c);

                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=3;
                    pitem.comanda= addsT_ordencuentaSqlAndroid(citem);

                    ss+=addItemSqlOrdenCom(pitem) + ";";
                }

            }

            return ss;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return "";
        }
    }

    public String addP_res_sesionSqlAndroid(clsClasses.clsP_res_sesion item, int idemp) {
        String corr="<>"+item.id+"<>";

        ins.init("P_res_sesion");

        //ins.add("EMPRESA",idemp);
        ins.add("ID",corr);
        ins.add("CODIGO_MESA",item.codigo_mesa);
        ins.add("VENDEDOR",item.vendedor);
        ins.add("ESTADO",item.estado);
        ins.add("CANTP",item.cantp);
        ins.add("CANTC",item.cantc);
        ins.add("FECHAINI",item.fechaini);
        ins.add("FECHAFIN",item.fechafin);
        ins.add("FECHAULT",item.fechault);

        return ins.sql();

    }

    public String addsT_ordencuentaSqlAndroid(clsClasses.clsT_ordencuenta item) {
        String corr="<>"+idorden+"<>";

        ins.init("T_ordencuenta");

        ins.add("COREL",corr);
        ins.add("ID",item.id);
        ins.add("CF",item.cf);
        ins.add("NOMBRE","<>"+item.nombre+"<>");
        ins.add("NIT","<>"+item.nit+"<>");
        ins.add("DIRECCION","<>"+item.direccion+"<>");
        ins.add("CORREO","<>"+item.correo+"<>");

        return ins.sql();

    }

    public String updItemSqlAndroid() {
        String corr="<>"+idorden+"<>";
        return "UPDATE P_res_sesion SET ESTADO=-1 WHERE ID='"+corr+"'";
    }

    public String addItemSqlOrdenCom(clsClasses.clsT_ordencom item) {

        ins.init("T_ordencom");

        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        return ins.sql();

    }

    public void addtoPending(String errs) {
        long fa,fl;

        fa=du.getActDateTime();fl=du.ffecha00(fa);

        try {
            db.beginTransaction();

            clsT_ordenpendObj T_ordenpendObj=new clsT_ordenpendObj(this,Con,db);
            clsClasses.clsT_ordenpend pitem = clsCls.new clsT_ordenpend();

            pitem.godigo_orden=idorden;
            pitem.fecha=du.getActDateTime();
            pitem.tipo=1;

            T_ordenpendObj.delete(pitem);
            T_ordenpendObj.add(pitem);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());return;
        }

        try {
            clsT_ordenerrorObj T_ordenerrorObj=new clsT_ordenerrorObj(this,Con,db);

            clsClasses.clsT_ordenerror eitem = clsCls.new clsT_ordenerror();

            eitem.godigo_orden=idorden;
            eitem.fecha=du.getActDateTime();
            eitem.tipo=1;
            eitem.error=errs;
            eitem.estado=0;

            T_ordenerrorObj.add(eitem);

        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    public void removeFromPending() {
        long fa,fl;

        fa=du.getActDateTime();fl=du.ffecha00(fa);

        try {
            clsT_ordenpendObj T_ordenpendObj=new clsT_ordenpendObj(this,Con,db);
            clsClasses.clsT_ordenpend pitem = clsCls.new clsT_ordenpend();

            pitem.godigo_orden=idorden;
            pitem.fecha=du.getActDateTime();
            pitem.tipo=1;

            T_ordenpendObj.delete(pitem);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        try {
            sql="DELETE FROM T_ordenpend WHERE fecha<" + fl;
            db.execSQL(sql);
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Broadcast Detalle

    /*

    private void broadcastDetail() {
        wsoidle=false;

        if (actorden) broadcastDetailCallback();
    }

    private void broadcastDetailCallback() {

        wso.level=2;

        try {
            if (wso.errflag) {
                msgBoxWifi("No hay conexíon al internet");
                relback.setBackgroundColor(Color.parseColor("#F4C6D0"));
                cierraPantalla();
                wsoidle=true;return;
            }

            brtcorel=idorden;

            sql="SELECT ID, COREL, PRODUCTO, EMPRESA, UM, CANT, UMSTOCK, FACTOR, PRECIO, IMP, " +
                "DES, DESMON, TOTAL, PRECIODOC, PESO, VAL1, VAL2, VAL3, VAL4, PERCEP, CUENTA, ESTADO " +
                "FROM T_ORDEN WHERE COREL='"+ brtcorel +"'";

            wso.execute(sql,rnDetailCallback);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wsoidle=true;
        }
    }

    private void broadcastAccountCallback() {

        clsClasses.clsT_orden btitem;
        
        wso.level=3;

        try {

            if (wso.errflag) {
                cierraPantalla();
                wsoidle=true;return;
            }

            if (wso.openDTCursor.getCount()==0) {
                cierraPantalla();
                wsoidle=true;return;
            }

            brtitems.add("DELETE FROM T_orden WHERE COREL='"+brtcorel+"'");

            wso.openDTCursor.moveToFirst();
            while (!wso.openDTCursor.isAfterLast()) {

                btitem = clsCls.new clsT_orden();

                btitem.id=wso.openDTCursor.getInt(0);
                btitem.corel=wso.openDTCursor.getString(1);
                btitem.producto=wso.openDTCursor.getString(2);
                btitem.empresa=wso.openDTCursor.getString(3);
                btitem.um=wso.openDTCursor.getString(4);
                btitem.cant=wso.openDTCursor.getDouble(5);
                btitem.umstock=wso.openDTCursor.getString(6);
                btitem.factor=wso.openDTCursor.getDouble(7);
                btitem.precio=wso.openDTCursor.getDouble(8);
                btitem.imp=wso.openDTCursor.getDouble(9);

                btitem.des=wso.openDTCursor.getDouble(10);
                btitem.desmon=wso.openDTCursor.getDouble(11);
                btitem.total=wso.openDTCursor.getDouble(12);
                btitem.preciodoc=wso.openDTCursor.getDouble(13);
                btitem.peso=wso.openDTCursor.getDouble(14);
                btitem.val1=wso.openDTCursor.getDouble(15);
                btitem.val2=wso.openDTCursor.getString(16);
                btitem.val3=wso.openDTCursor.getDouble(17);
                btitem.val4=wso.openDTCursor.getString(18);
                btitem.percep=wso.openDTCursor.getDouble(19);
                btitem.cuenta=wso.openDTCursor.getInt(20);
                btitem.estado=wso.openDTCursor.getInt(21);

                brtitems.add(addTordenItemSql(btitem));

                wso.openDTCursor.moveToNext();
            }

            sql="SELECT COREL, ID, EMPRESA, CF, NOMBRE, NIT, DIRECCION, CORREO " +
                "FROM T_ORDENCUENTA WHERE (COREL='"+brtcorel+"')";
            wso.execute(sql,rnDetailCallback);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wsoidle=true;
        }
    }

    private void broadcastCompleteCallback() {
        clsClasses.clsT_ordencuenta btitem;

        try {
            if (wso.errflag) {
                cierraPantalla();
                wsoidle=true;return;
            }

            if (wso.openDTCursor.getCount()>0) {

                brtitems.add("DELETE FROM T_ORDENCUENTA WHERE COREL='"+brtcorel+"'");

                wso.openDTCursor.moveToFirst();
                while (!wso.openDTCursor.isAfterLast()) {

                    btitem = clsCls.new clsT_ordencuenta();

                    btitem.corel=wso.openDTCursor.getString(0);
                    btitem.id=wso.openDTCursor.getInt(1);
                    btitem.cf=wso.openDTCursor.getInt(2);
                    btitem.nombre=wso.openDTCursor.getString(3);
                    btitem.nit=wso.openDTCursor.getString(4);
                    btitem.direccion=wso.openDTCursor.getString(5);
                    btitem.correo=wso.openDTCursor.getString(6);

                    brtitems.add(addTordencuentaItemSql(btitem));

                    wso.openDTCursor.moveToNext();
                }
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wsoidle=true; return;
        }

        try {
            db.beginTransaction();

            for (int i = 0; i <brtitems.size(); i++) {
                sql=brtitems.get(i);
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            broadcastConfirmDetail();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

        cierraPantalla();
        wsoidle=true;
    }

    private void broadcastConfirmDetail() {
        String cmd="DELETE FROM T_ordencom WHERE CODIGO="+brtid;

        try {
            enviaCommit(false,cmd);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
         }
    }

    public String addTordenItemSql(clsClasses.clsT_orden item) {

        ins.init("T_orden");

        ins.add("ID",item.id);
        ins.add("COREL",item.corel);
        ins.add("PRODUCTO",item.producto);
        ins.add("EMPRESA",item.empresa);
        ins.add("UM",item.um);
        ins.add("CANT",item.cant);
        ins.add("UMSTOCK",item.umstock);
        ins.add("FACTOR",item.factor);
        ins.add("PRECIO",item.precio);
        ins.add("IMP",item.imp);
        ins.add("DES",item.des);
        ins.add("DESMON",item.desmon);
        ins.add("TOTAL",item.total);
        ins.add("PRECIODOC",item.preciodoc);
        ins.add("PESO",item.peso);
        ins.add("VAL1",item.val1);
        ins.add("VAL2",item.val2);
        ins.add("VAL3",item.val3);
        ins.add("VAL4",item.val4);
        ins.add("PERCEP",item.percep);
        ins.add("CUENTA",item.cuenta);
        ins.add("ESTADO",item.estado);

        return ins.sql();

    }

    public String addTordencuentaItemSql(clsClasses.clsT_ordencuenta item) {

        ins.init("T_ordencuenta");

        ins.add("COREL",item.corel);
        ins.add("ID",item.id);
        ins.add("CF",item.cf);
        ins.add("NOMBRE",item.nombre);
        ins.add("NIT",item.nit);
        ins.add("DIRECCION",item.direccion);
        ins.add("CORREO",item.correo);

        return ins.sql();

    }

    */

    //endregion

    //region Estado

    private void actualizaEstadoOrden(int modo) {
        estado_modo=modo;
        if (items.size()>0) {

            if (actorden) {
                try {
                    sql="SELECT ID,ESTADO FROM T_ORDEN WHERE (COREL='"+idorden+"')";
                    ws.openDT(sql);
                } catch (Exception e) {
                    //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            } else {
                try {
                    sql="SELECT ID,ESTADO FROM T_ORDEN WHERE (COREL='"+idorden+"')";
                    ws.openDT(sql);
                } catch (Exception e) {
                    //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            }


        } else imgrefr.setVisibility(View.INVISIBLE);

    }

    private void aplicaImpresion() {
        clsClasses.clsT_orden oitem;

        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");

            for (int i = 0; i <T_ordenObj.count; i++) {
                oitem=T_ordenObj.items.get(i);
                oitem.estado=0;
                T_ordenObj.update(oitem);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    @Override
    protected void wsCallBack(Boolean throwing,String errmsg) {
        imgrefr.setVisibility(View.INVISIBLE);

        try {

            super.wsCallBack(throwing, errmsg);

            aplicaEstadoCaja();

            switch (estado_modo) {
                case 1:
                    if (pendientesCuentas()) {
                        msgbox("Existe cuenta pendiente de completar pago, por favor espere . . .");return;
                    }
                    msgAskPagoCaja("Enviar solicitud de pago a la caja");break;
                case 2:
                    msgAskPrint("Enviar impresión de precuenta a la caja");break;
            }

        } catch (Exception e) {
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void aplicaEstadoCaja() {
        int iid,est;

        if (ws.openDTCursor.getCount()==0) return;

        try {
            db.beginTransaction();

            ws.openDTCursor.moveToFirst();
            while (!ws.openDTCursor.isAfterLast()) {
                iid=ws.openDTCursor.getInt(0);
                est=ws.openDTCursor.getInt(1);

                if (est<0 | est>1) {
                    sql="UPDATE T_ORDEN SET ESTADO="+est+" WHERE (COREL='"+idorden+"') AND (EMPRESA="+iid+")";
                    db.execSQL(sql);
                }

                ws.openDTCursor.moveToNext();
            }

            listItems();

            db.setTransactionSuccessful();
            db.endTransaction();

            validaCompleto();
        } catch (Exception e) {
            db.endTransaction();
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void validaCompleto() {
        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);

            T_ordenObj.fill("WHERE (COREL='"+idorden+"')");
            int ti=T_ordenObj.count;

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=2)");
            int tic=T_ordenObj.count;

            if (ti==tic) cerrarCuentas(ti);

        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean pendientesPago() {
        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO<2)");
            return T_ordenObj.count>0;
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    //endregion

    //region Mover cuentas

    private void listaMesasParaMover() {

        long ff=du.ffecha00(du.getActDate());

        try {

            sql="SELECT P_RES_MESA.CODIGO_MESA,  P_RES_MESA.NOMBRE, P_RES_SESION.ID,'','',  '','','','' " +
                    "FROM P_RES_SESION INNER JOIN P_RES_MESA ON P_RES_SESION.CODIGO_MESA=P_RES_MESA.CODIGO_MESA " +
                    "WHERE (P_RES_SESION.ESTADO>0)  AND " +
                    "(P_RES_SESION.FECHAINI>"+ff+") AND (P_RES_MESA.CODIGO_MESA<>"+gl.mesacodigo+") " +
                    "ORDER BY P_RES_MESA.NOMBRE";
            ViewObj.fillSelect(sql,0);

            if (ViewObj.count==0) {
                msgbox("No hay ninguna mesa disponible");return;
            }

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"Seleccione una mesa");

            for (int i = 0; i <ViewObj.count; i++) {
                listdlg.add(ViewObj.items.get(i).f1);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    validaCuenta(ViewObj.items.get(position).pk,ViewObj.items.get(position).f2);
                    listdlg.dismiss();
                } catch (Exception e) {}
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

    private void validaCuenta(int idmesa,String idsession) {

        IdcuentamovDestino =0;

        try {

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+idsession+"') ORDER BY ID");
            if (T_ordencuentaObj.count==0) {
                msgbox("La mesa no tiene ninguna cuenta disponible");return;
            }

            IdcuentamovDestino =T_ordencuentaObj.first().id;

            /*
            for (int i = T_ordencuentaObj.count-1; i>=0; i--) {
                if (!cuentaPendientePago(idsession, T_ordencuentaObj.items.get(i).id)) {
                    idcuentamov=T_ordencuentaObj.items.get(i).id;break;
                }
            }
            if (idcuentamov==0) {
                msgbox("La mesa no tiene ninguna cuenta disponible");return;
            }
            */

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage("¿Mover la cuenta?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    idresdest=idsession;
                    aplicaCuentas(idsession, IdcuentamovDestino,IdCuentaAMover);
                }
            });

            dialog.setNegativeButton("No", (dialog1, which) -> { });

            dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicaCuentas(String idsession,int IdCuentaDestino,int IdCuentaOrigen) {
        Cursor dt;
        int id,mid=1;

        try {

            sql="SELECT MAX(ID) FROM T_orden WHERE COREL='"+idsession+"'";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                mid=dt.getInt(0)+1;
            } else {
                msgbox("No se puede mover la cuenta");return;
            }

            sql="SELECT ID FROM T_orden WHERE COREL='" + idorden + "' AND CUENTA =" + IdCuentaOrigen;
            dt=Con.OpenDT(sql);

            if (dt.getCount()==0) {
                msgbox("La cuenta está vacía");return;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return;
        }

        try {

            db.beginTransaction();
            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                id=dt.getInt(0);

                sql="UPDATE T_ORDEN SET ID="+mid+",COREL='"+idsession+"',CUENTA="+IdCuentaDestino+" " +
                     "WHERE (COREL='"+idorden+"') AND (ID="+id+")";
                db.execSQL(sql);

                dt.moveToNext();mid++;

            }

            //#EJC202210221704:Eliminar pre-cuenta asociada si existe al registro.
            sql="DELETE FROM T_ORDENcuenta WHERE COREL='" + idorden + "' AND ID =" + IdCuentaOrigen;
            db.execSQL(sql);

            /*
            if (actorden) {
                envioOrdenDestino(idsession);
            }
            */

            db.setTransactionSuccessful();
            db.endTransaction();

            sql="SELECT ID FROM T_orden WHERE COREL='" + idorden + "'";
            dt=Con.OpenDT(sql);

            //#EJC202210221638: Si la mesa ya no tiene items, cerrar la mesa y la cuenta.
            if (dt.getCount()==0) {
                cerrarCuentas(-1);
                envioOrdenDestinoBorrar(idsession);
            }else{
                envioOrdenDestinoBorrar(idorden,IdCuentaOrigen);
                envioOrdenDestino(idsession);
                //finish();
            }

        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Ingredientes

    private void Ingredientes() {

        try {
            listaAdicionales();

            extListChkDlg listdlg = new extListChkDlg();
            listdlg.buildDialog(Orden.this,gl.gstr2,"Salir","Borrar","Agregar");

            listdlg.setOnExitListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.setOnDelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i <ilist.size(); i++) {
                        //if (checked[i]) {
                        // borrarIngrediente(ilist.get(i).pk);
                        //}
                    }

                    listItems();
                    listdlg.dismiss();
                }
            });

            listdlg.setOnAddListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listaIngredientes();
                    listdlg.dismiss();
                }
            });

            //llitem.pk=T_orden_ingObj.items.get(i).iding;
            //llitem.f1=T_orden_ingObj.items.get(i).nombre+sprodpr;
            //llitem.f2=T_orden_ingObj.items.get(i).codigo_ing+"";

            for (int i = 0; i <ilist.size(); i++) {
                listdlg.add(ilist.get(i).pk,ilist.get(i).f1);
            }

            listdlg.setLines(5);

            listdlg.show();


        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void listaAdicionales() {
        clsClasses.clsLista llitem;
        double prodpr;
        String sprodpr;

        try {
            ilist.clear();

            T_orden_ingObj.fill("WHERE (Corel='"+gl.ordcorel+"') AND (Id="+gl.produid+") ORDER BY Nombre");

            for (int i = 0;i <T_orden_ingObj.count; i++) {
                llitem = clsCls.new clsLista();

                llitem.pk=T_orden_ingObj.items.get(i).iding;
                prodpr=app.prodPrecio(llitem.pk);
                if (prodpr>0) sprodpr=" [ "+mu.frmcur(prodpr)+" ]";else sprodpr=" ";
                llitem.f1=T_orden_ingObj.items.get(i).nombre+sprodpr;
                llitem.f2=T_orden_ingObj.items.get(i).codigo_ing+"";

                ilist.add(llitem);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listaIngredientes() {
        clsClasses.clsP_producto prod;
        double prodpr;
        String sprodpr;

        try {
            P_productoObj.fill("WHERE (LINEA="+lineaingred+") ORDER BY DESCLARGA");
            if (P_productoObj.count==0) {
                msgbox("No está definido ningúno ingrediente");return;
            }

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"Ingredientes");
            listdlg.setLines(6);

            for (int i = 0; i <P_productoObj.count; i++) {
                prod=P_productoObj.items.get(i);
                prodpr=app.prodPrecio(prod.codigo_producto);
                if (prodpr>0) sprodpr=" [ "+mu.frmcur(prodpr)+" ]";else sprodpr="";

                listdlg.add(prod.codigo_producto+"",prod.desccorta,sprodpr);
           }

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        agregaIngrediente(listdlg.items.get(position).codigo,
                                          listdlg.items.get(position).text);
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listaIngredientesOld() {
        final AlertDialog Dialog;
        double prodpr;
        String sprodpr;

        clsClasses.clsLista llitem;

        try {
            P_productoObj.fill("WHERE (LINEA="+lineaingred+") ORDER BY DESCLARGA");
            if (P_productoObj.count==0) {
                msgbox("No está definido ningúno ingrediente");return;
            }

            final String[] iselitems = new String[P_productoObj.count];

            plist.clear();
            for (int i = 0; i <P_productoObj.count; i++) {

                llitem = clsCls.new clsLista();

                llitem.pk=P_productoObj.items.get(i).codigo_producto;
                llitem.f1=P_productoObj.items.get(i).desccorta;

                prodpr=app.prodPrecio(llitem.pk);
                if (prodpr>0) sprodpr=" [ "+mu.frmcur(prodpr)+" ]";else sprodpr=" ";
                llitem.f1+=sprodpr;

                iselitems[i]=llitem.f1;

                plist.add(llitem);
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Ingredientes");

            menudlg.setItems(iselitems,  new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    agregaIngrediente(plist.get(item).pk,plist.get(item).f1);
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    listItems();
                    Ingredientes();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void agregaIngrediente(int iding,String ning) {
        try {
            int puid=agregaProductoIngrediente(iding);
            if (puid==0) {
                msgbox("No se puede agregar ingrediente");return;
            }

            int newid=T_orden_ingObj.newID("SELECT MAX(CODIGO_ING) FROM T_orden_ing");

            clsClasses.clsT_orden_ing item= clsCls.new clsT_orden_ing();

            item.codigo_ing=newid;
            item.corel=gl.ordcorel;
            item.id=gl.produid;
            item.iding=iding;
            item.nombre=ning;
            item.puid=puid;

            T_orden_ingObj.add(item);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        Ingredientes();
        listItems();
    }

    private void agregaIngrediente(String iding,String ning) {
        try {
            agregaIngrediente(Integer.parseInt(iding),ning);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean esIngrediente() {
         try {
            T_orden_ingObj.fill("WHERE (Corel='"+gl.ordcorel+"') AND (Puid="+gl.produid+") ");
            if (T_orden_ingObj.count>0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return false;
    }

    private boolean esIngrediente(int pruid) {
        try {
            T_orden_ingObj.fill("WHERE (Corel='"+gl.ordcorel+"') AND (IdIng="+pruid+") ");
            if (T_orden_ingObj.count>0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return false;
    }

    private boolean esModificado(int pruid) {
        if (tieneIngrediente(pruid)) {
            return true;
        }

        try {
            T_orden_modObj.fill("WHERE (COREL='"+gl.ordcorel+"' AND ID="+pruid+")");
            if (T_orden_modObj.count>0) return true;
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private boolean tieneIngrediente(int pruid) {
        try {
            T_orden_ingObj.fill("WHERE (Corel='"+gl.ordcorel+"') AND (ID="+pruid+") ");
            if (T_orden_ingObj.count>0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return false;
    }

    private void borrarIngrediente(int puid,String codigo_ing) {

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_ORDEN_ING WHERE (COREL='"+idorden+"') AND (CODIGO_ING="+codigo_ing+")");
            //db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+puid+")");

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    //endregion

    //region Anular

    private void anulaItem() {

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_RES_SESION WHERE (ID='"+idorden+"')");
            db.execSQL("DELETE FROM T_orden WHERE (COREL='" + idorden + "')");
            db.execSQL("DELETE FROM T_ordencuenta WHERE (COREL='" + idorden + "')");

            db.setTransactionSuccessful();
            db.endTransaction();

            anulaOrden();
            //listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    private void anulaOrden() {
        String cmd="";

        try {

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + idorden + "')" + ";";

            cmd+=buildAnulJournal();

            try {
                /*
                Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                if (actorden) startService(intent);
                 */
                enviaCommit(true,cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            gl.cerrarmesero=true;gl.mesero_lista=true;
            cerrarOrden();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String buildAnulJournal() {
        clsClasses.clsT_ordencom pitem;
        int idruta;
        String ss="";

        try {

            clsP_rutaObj P_rutaObj=new clsP_rutaObj(this,Con,db);
            P_rutaObj.fill();

            for (int i = 0; i <P_rutaObj.count; i++) {

                idruta=P_rutaObj.items.get(i).codigo_ruta;

                if (idruta!=gl.codigo_ruta) {

                    pitem= clsCls.new clsT_ordencom();

                    pitem.codigo_ruta=idruta;
                    pitem.corel_orden=idorden;
                    pitem.corel_linea=1;

                    pitem.comanda="DELETE FROM P_RES_SESION WHERE (ID=<>"+idorden+"<>)";
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                    pitem.comanda="DELETE FROM T_orden WHERE (COREL=<>" + idorden + "<>)";
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                    pitem.comanda="DELETE FROM T_ordencuenta WHERE (COREL=<>" + idorden + "<>)" ;
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                }
            }

            return ss;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return "";
        }
    }

    //endregion

    //region Aux

    private void setControls(){

        try {
            listView = findViewById(R.id.listView1);
            gridView =  findViewById(R.id.gridView2);gridView.setEnabled(true);
            grdbtn =  findViewById(R.id.grdbtn);
            grdprod =  findViewById(R.id.grdProd);
            grdprod.setBackgroundColor(Color.TRANSPARENT);grdprod.setVerticalSpacing(5);grdprod.setHorizontalSpacing(5);
            grdfam = findViewById(R.id.grdFam);
            grdfam.setBackgroundColor(Color.WHITE);grdfam.setVerticalSpacing(5);grdfam.setHorizontalSpacing(5);

            lblTot=  findViewById(R.id.lblTot);
            lblCant= findViewById(R.id.textView252);
            lblDesc= findViewById(R.id.textView115);lblDesc.setText( "Desc : "+mu.frmcur(0));
            lblStot= findViewById(R.id.textView103); lblStot.setText("Subt : "+mu.frmcur(0));
            lblTit=  findViewById(R.id.lblTit);
            lblAlm=  findViewById(R.id.lblTit2);
            lblVend= findViewById(R.id.lblTit4);
            lblPokl= findViewById(R.id.lblTit5);
            lblCent= findViewById(R.id.textView72);
            lblCom= findViewById(R.id.textView275);

            txtbarra= findViewById(R.id.txtbarra);

            lblKeyDP= findViewById(R.id.textView110);
            lblDir= findViewById(R.id.lblDir);

            imgnowifi=findViewById(R.id.imageView121);
            imgroad=  findViewById(R.id.imgRoadTit);
            imgrefr = findViewById(R.id.imageView119);
            if (!gl.pelMeseroCaja) imgrefr.setVisibility(View.INVISIBLE);

            relprod=findViewById(R.id.relProd);
            relsep=findViewById(R.id.relSepar);
            relsep2=findViewById(R.id.relSep2);
            relback=findViewById(R.id.relomain);

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setVisual() {
        if (imgflag) {
            grdfam.setNumColumns(3);
            grdprod.setNumColumns(3);
        } else {
            grdfam.setNumColumns(2);
            grdprod.setNumColumns(2);
        }

        if (!horiz) {
            grdfam.setNumColumns(1);
            grdprod.setNumColumns(2);
        }

        listFamily();
    }

    private void initValues(){
        Cursor DT;
        String contrib;

        app.parametrosExtra();
        if (!app.modoSinInternet()) imgnowifi.setVisibility(View.INVISIBLE);

        usarbio=gl.peMMod.equalsIgnoreCase("1");

        tiposcan="*";

        lblTit.setText("");lblAlm.setText("");lblPokl.setText("");

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

        try {
            sinimp=false;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            sinimp=false;
        }

        sinimp=false;
        gl.sinimp=sinimp;

        gl.ref1="";
        gl.ref2="";
        gl.ref3="";

        clsDescFiltro clsDFilt=new clsDescFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        clsBonFiltro clsBFilt=new clsBonFiltro(this,gl.codigo_ruta,gl.codigo_cliente);

        imgfold= Environment.getExternalStorageDirectory()+ "/mPosFotos/";

        dweek=mu.dayofweek();

        lblTot.setText("Total : "+mu.frmcur(0));

        mesa=gl.mesanom;
        nombreMesa();

        app.primeraCuenta(idorden);

        try {
            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            numpedido=P_res_sesionObj.first().cantc;
            lblCom.setText(""+P_res_sesionObj.first().cantp);
        } catch (Exception e) {
            numpedido=0;
        }
        ordenpedido=numpedido>0;

        clsD_barrilObj D_barrilObj=new clsD_barrilObj(this,Con,db);
        D_barrilObj.fill();
        barril=D_barrilObj.count>0;

    }

    private void nombreMesa() {
        String spend="";

        try {
            clsT_ordenpendObj T_ordenpendObj=new clsT_ordenpendObj(this,Con,db);
            T_ordenpendObj.fill("WHERE GODIGO_ORDEN='"+idorden+"'");
            if (T_ordenpendObj.count>0) spend="- SIN ENVIO";
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        lblVend.setText("Mesa : "+mesa+" "+spend);
    }

    private boolean hasProducts(){
        Cursor dt;

        try {
            sql="SELECT PRODUCTO FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);

            int i=dt.getCount();
            if (dt!=null) dt.close();
            return i>0;
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            return false;
        }
    }

    private void doExit(){
        try{
            gl.exitflag=true;
            gl.cliposflag=false;
            cerrarOrden();
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
        gl.dval=pr;

        return sprec;
    }

    private double prodPrecioBaseImp(int prid) {
        Cursor DT;
        double pr,stot,pprec,tsimp;
        double imp1,imp2,imp3,vimp1,vimp2,vimp3,vimp;
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
                sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp1+")";
                DT=Con.OpenDT(sql);
                DT.moveToFirst();
                vimp1=DT.getDouble(0);
            } else vimp1=0;

            if (imp2!=0) {
                sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp2+")";
                DT=Con.OpenDT(sql);
                DT.moveToFirst();
                vimp2=DT.getDouble(0);
            } else vimp2=0;

            if (imp3!=0) {
                sql="SELECT VALOR FROM P_IMPUESTO  WHERE (CODIGO_IMPUESTO="+imp3+")";
                DT=Con.OpenDT(sql);
                DT.moveToFirst();
                vimp3=DT.getDouble(0);
            } else vimp3=0;

            vimp=vimp1+vimp2+vimp3;
            pr=pr*(1+vimp/100);
            pr=mu.round2(pr);

            if (DT!=null) DT.close();
        } catch (Exception e) {
            pr=0;
        }

        gl.dval=pr;
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

                    clsOrden item = items.get(i);

                    prodid=item.Cod;//gl.prodmenu=prodid;
                    uprodid=prodid;
                    uid=item.emp;
                    gl.gstr=item.Nombre;
                    gl.retcant=(int) item.Cant;
                    gl.limcant=getDisp(prodid);
                    browse=6;

                    startActivity(new Intent(Orden.this,VentaEdit.class));
                }
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }
    }

    private void cargaCliente() {

        try {

            gl.codigo_cliente=gl.emp*10;

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+idorden+"') AND (ID="+cuenta+")");

            if (T_ordencuentaObj.count>0) {
                gl.gNombreCliente = T_ordencuentaObj.first().nombre;
                gl.gNITCliente = T_ordencuentaObj.first().nit;
                gl.gDirCliente = T_ordencuentaObj.first().direccion;
                gl.gCorreoCliente = T_ordencuentaObj.first().correo;
                gl.gNITcf=T_ordencuentaObj.first().cf==1;
            } else {
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="C.F.";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    public void asignaCuenta(int idcuenta) {
        clsClasses.clsT_orden item;

        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ID="+selitem.id+")");
            item=T_ordenObj.first();
            item.cuenta=idcuenta;
            T_ordenObj.update(item);

            envioCuentas();

            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public void agregarCuenta() {
        int newcid;

        try {

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            sql="SELECT MAX(ID) FROM T_ordencuenta WHERE (corel='"+idorden+"')";
            Cursor dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                newcid=dt.getInt(0)+1;
            } else {
                newcid=1;
            }

            cantcuentas=newcid;

            cuenta.corel=idorden;
            cuenta.id=newcid;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            T_ordencuentaObj.add(cuenta);

            asignaCuenta(newcid);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setPrintWidth() {
        Cursor DT;
        int prwd=32;

        try {
            sql="SELECT COL_IMP FROM P_EMPRESA";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            prwd=DT.getInt(0);

            if (DT!=null) DT.close();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            prwd=32;
        }

        gl.prw=prwd;

    }

    private void calibraPantalla() {
        if (horiz) {
            relprod.setVisibility(View.VISIBLE);
            relsep.setVisibility(View.VISIBLE);
            lblCent.setVisibility(View.VISIBLE);
            //relsep2.setBackgroundResource(R.drawable.blue_strip);
        } else {
            relsep.setVisibility(View.VISIBLE);
            lblCent.setVisibility(View.INVISIBLE);
            relsep2.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean cuentaPagada(String corr,int id) {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
            return D_facturaObj.count!=0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private Boolean cuentaPendientePago(String corr,int cue) {
        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+corr+"') AND (CUENTA="+cue+") AND (PERCEP=1) ORDER BY CUENTA DESC");
            return T_ordenObj.count>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
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

    private boolean pendienteImpresion() {

        if (!gl.peImpOrdCos) return false;

        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");
            return T_ordenObj.count>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private boolean pendientesCuentas() {
        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO<2) AND (PERCEP=1)");
            return T_ordenObj.count>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean existenCuentasPagadas() {
        try {
            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (PERCEP=1)");
            return T_ordenObj.count>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean limpiaVenta() {
        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_VENTA");
            db.execSQL("DELETE FROM T_COMBO");

            db.setTransactionSuccessful();
            db.endTransaction();

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

        return false;
    }

    private void redireccionarImpresora() {
        try {
            P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA="+idimp2+")");
            String ip=P_impresoraObj.first().ip;
            P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA="+idimp1+")");
            P_impresoraObj.first().ip=ip;
            P_impresoraObj.update(P_impresoraObj.first());

            msgbox("La impresora redirecionada.");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addBarcode() {
        gl.barra=barcode;

        if (barraProducto()) {
            txtbarra.setText("");return;
        } else {
            toastlong("¡El producto "+barcode+" no existe!");
            txtbarra.setText("");
        }
    }

    private boolean barraProducto() {
        Cursor dt;

        try {

            sql="SELECT DISTINCT CODIGO,DESCCORTA,CODIGO_PRODUCTO FROM P_PRODUCTO " +
                "WHERE (ACTIVO=1) AND ((CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"')) COLLATE NOCASE";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                gl.gstr=dt.getString(0);gl.um="UN";
                gl.pprodname=dt.getString(1);
                gl.prodcod=dt.getInt(2);
                if (dt!=null) dt.close();

                processItem(false);
                return true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private int agregaProductoIngrediente(int idpord) {
        Cursor dt;

        try {

            sql="SELECT DISTINCT CODIGO,DESCCORTA,CODIGO_PRODUCTO FROM P_PRODUCTO " +
                    "WHERE (CODIGO_PRODUCTO="+idpord+")";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();

                gl.gstr=dt.getString(0);gl.um="UN";
                gl.pprodname=dt.getString(1);
                gl.prodcod=dt.getInt(2);
                if (dt!=null) dt.close();

                gl.uidingrediente=0;

                processItem(false);

                return gl.uidingrediente;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return 0;
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

    private void cierraPantalla() {
        try {
            //ctimer.removeCallbacks(crunner);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        try {
            //ctimer.postDelayed(crunner,20000);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    /*
    private void agregaBloqueo() {
        try {
            sql="INSERT INTO P_RES_MESA_BLOQ (CODIGO_MESA,CODIGO_VENDEDOR) " +
                    "         VALUES ("+gl.mesa_codigo+","+gl.mesa_vend+")";
            wscom.execute(sql,null);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borrarBloqueo() {
        try {
            sql="DELETE FROM P_RES_MESA_BLOQ WHERE (CODIGO_MESA="+gl.mesa_codigo+")";

            Intent intent = new Intent(Orden.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",sql);
            startService(intent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }
    */

    private void cerrarOrden(){
        //borrarBloqueo();
        finish();
    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  );

            dialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    /*
                    gl.cliposflag=true;
                    if (gl.dvbrowse!=0) gl.dvbrowse =0;
                    browse=0;
                    gl.forcedclose=true;
                    doExit();
                    */

                    gl.cerrarmesero=true;gl.mesero_lista=true;
                    cerrarOrden();
                }
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
                    if (esIngrediente()) {
                        delItem();
                    } else {
                        delItemIng();
                    }
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

    private void msgAskDelCom(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (esIngrediente()) {
                        msgbox("Artículo es un ingrediente, no se puede borrar");
                    } else {
                        borrarItemComanda();
                    }
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

    private void msgAskComanda() {

        clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
        int art;
        String msg;

        try {

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=1)");
            art=T_ordenObj.count;

            if (art==0) {
                msgInfo("Ninguno artículo está marcado para la impresión");return;
            }

            if (art==1) {
                msg="¿Enviar 1 artículo a la cocina?";
            } else {
                msg="¿Enviar "+art+" artículos a la cocina?";
            }

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", (dialog12, which) -> {
                actualizaEstadoOrden(3);
                imprimeComanda();
                //aplicaImpresion();
                //if (actorden) envioOrden();else cerrarOrden();
            });

            dialog.setNegativeButton("No", (dialog1, which) -> {});

            dialog.show();

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" : "+e.getMessage());
        }
    }

    private void msgInfo(String msg) {

        try {

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskLimit(String msg,boolean updateitem) {
        final boolean updatem=updateitem;
        try {

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

    private void msgAskState(String msg,int flag,int position) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    items.get(position).estado=flag;

                    try {
                        sql="UPDATE T_orden SET ESTADO="+flag+" WHERE (ID="+selitem.id+") AND (COREL='"+idorden+"')";
                        db.execSQL(sql);

                        sql="UPDATE P_res_sesion SET FECHAULT="+du.getActDateTime()+" WHERE (ID='"+idorden+"')";
                        db.execSQL(sql);

                    } catch (SQLException e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }

                    adapter.setSelectedIndex(position);
                    adapter.notifyDataSetChanged();
                    cierraPantalla();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    cierraPantalla();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void showItemPopMenu() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,gl.gstr2);

            if (gl.idmodgr>0) listdlg.setLines(7);else listdlg.setLines(6);

            listdlg.add(R.drawable.agregar,"Cantidad"); //imagen , texto - si imagen=0 no se despliega
            listdlg.add(R.drawable.reportes,"Nota");
            listdlg.add(R.drawable.cambio_usuario,"Cambiar cuenta");
            listdlg.add(R.drawable.anulacion,"Borrar");
            listdlg.add(R.drawable.recibir_archivos,"Dividir");
            listdlg.add(R.drawable.venta_add,"Ingredientes adicionales");
            if (gl.idmodgr>0) listdlg.add(R.drawable.btn_detail,"Modificadores");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                if (browse==6) {
                                    startActivity(new Intent(Orden.this,VentaEdit.class));
                                } else if (browse==7) {
                                    startActivity(new Intent(Orden.this,OrdenMenu.class));
                                }
                                break;
                            case 1:
                                inputNota();break;
                            case 2:
                                showMenuCuenta();break;
                            case 3:
                                if (statenv==1) {
                                    msgbox("El artículo es parte de una cuenta enviada a pagar, no se puede borrar");
                                } else {
                                    msgAskDel("Está seguro de borrar");
                                }
                                break;
                            case 4:
                                if (selitem.Cant>1) {
                                    msgAskDividir("Dividir articulo");
                                } else {
                                    toastcent("No se puede dividir articulo con cantidad 1 ");
                                }
                                break;
                            case 5:
                                if (esIngrediente()) {
                                    msgbox("El articúlo es un ingrediente.");return;
                                }
                                if (lineaingred<1) {
                                    msgbox("No existe lista de ingredientes.");return;
                                }
                                Ingredientes();
                                break;
                            case 6:
                                startActivity(new Intent(Orden.this,ModifProd.class));
                                break;
                        }

                        cierraPantalla();
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cierraPantalla();
                    listdlg.dismiss();
                }
            });

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showItemPopMenuLock() {

        try {
            extListDlg listdlg = new extListDlg();

            listdlg.buildDialog(Orden.this,gl.gstr2);

            listdlg.add(R.drawable.cambio_usuario,"Cambiar cuenta");//imagen , texto - si imagen=0 no se despliega
            listdlg.add(R.drawable.recibir_archivos,"Dividir");
            listdlg.add(R.drawable.avanzar,"Mover cuenta a otra mesa");
            listdlg.add(R.drawable.avanzar,"Borrar");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                showMenuCuenta();break;
                            case 1:
                                if (selitem.Cant>1) {
                                    msgAskDividir("Dividir artículo");
                                } else {
                                    toastcent("Artículo con cantidad 1 no se puede dividir");
                                }
                                break;
                            case 2:
                                listaMesasParaMover();
                                break;
                            case 3:
                                valsupermodo=1;
                                validaSupervisor();
                                break;
                        }

                        cierraPantalla();
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

            listdlg.show(); //Alto de dialog

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void msgAskDividir(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    splitItem();
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

    private void msgAskPagoCaja(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aplicarPago();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskEnvioCaja(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                envioMesa(1,true);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskReenvio(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                broadcastOrder();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskPrecuentaImpresion(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                crearVenta();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskPrint(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aplicarPreimpresion();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskCuentas(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskCuentas2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskCuentas2(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cerrarCuentas(-1);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgBorrarOrden(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgBorrarOrden2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgBorrarOrden2(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //cerrarCuentas(-9);
                anulaItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void inputNota() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String nn="";

        try {
            T_orden_notaObj.fill("WHERE (id="+gl.produid+") AND (corel='"+idorden+"')");
            if (T_orden_notaObj.count>0) nn=T_orden_notaObj.first().nota+"";
        } catch (Exception e) {
            nn="";
        }

        alert.setTitle("Nota");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setText(""+nn);
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    aplicaNota(s);
                } catch (Exception e) {
                    mu.msgbox("Error : "+e.getMessage());return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void aplicaNota(String nn) {
        clsClasses.clsT_orden_nota nota = clsCls.new clsT_orden_nota();

        try {
            nota.id=gl.produid;
            nota.corel=idorden;
            nota.nota=nn+"";
            T_orden_notaObj.add(nota);
        } catch (Exception e) {
            T_orden_notaObj.update(nota);
        }

        listItems();
    }

    private void inputMesa() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Impresion de comanda");
        alert.setMessage("MESA NUMERO : ");

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

    private void showMenuCuenta() {
        clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
        int maxcuenta=1;

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"Seleccione una cuenta","Salir","Nueva cuenta");

            T_ordencuentaObj.fill("WHERE (COREL='"+idorden+"') ORDER BY ID");
            for (int i = T_ordencuentaObj.count-1; i>=0; i--) {
                if (cuentaPendientePago(idorden, T_ordencuentaObj.items.get(i).id)) {
                    //if (cuentaPagada(idorden, T_ordencuentaObj.items.get(i).id)) {
                    T_ordencuentaObj.items.remove(i);
                } else {
                    if (T_ordencuentaObj.items.get(i).id>maxcuenta) maxcuenta=T_ordencuentaObj.items.get(i).id;
                }
            }

            cantcuentas=T_ordencuentaObj.items.size();
            for (int i = 0; i <cantcuentas; i++) {
                listdlg.add(""+T_ordencuentaObj.items.get(i).id);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    asignaCuenta(Integer.parseInt(listdlg.items.get(position).text));
                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.setOnMiddleClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregarCuenta();
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void showListaCuentas() {
        clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"Imprimir precuenta");

            T_ordencuentaObj.fill("WHERE (COREL='"+idorden+"') ORDER BY ID");
            if (T_ordencuentaObj.count>0) {
                for (int i = T_ordencuentaObj.count - 1; i >= 0; i--) {
                    if (cuentaPendientePago(idorden, T_ordencuentaObj.items.get(i).id)) {
                        T_ordencuentaObj.items.remove(i);
                    }
                }
            }

            if (T_ordencuentaObj.items.size()==0) {
                toast("No existe ninguna cuenta activa");return;
            }

            if (T_ordencuentaObj.items.size()==1) {
                cuenta=T_ordencuentaObj.items.get(0).id;
                gl.nocuenta_precuenta=""+cuenta;
                crearVenta();
                return;
            }

            cantcuentas=T_ordencuentaObj.items.size();
            for (int i = 0; i <cantcuentas; i++) {
                listdlg.add(""+T_ordencuentaObj.items.get(i).id);
            }

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        cuenta=Integer.parseInt(listdlg.getText(position));
                        gl.nocuenta_precuenta=""+cuenta;
                        crearVenta();
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

        /*
             menudlg.setPositiveButton("Nueva cuenta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    agregarCuenta();
                }
            });
        */

    }

    private void msgAskImpresora(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage(msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showListaImpresoras();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void showListaImpresoras() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"¿Cual impresora no funciona?");

            P_impresoraObj.fill(" ORDER BY NOMBRE");
            if (P_impresoraObj.count<2) {
                msgbox("Está definida solo una impresora, no se puede redireccionar");return;
            }

            int cimp=P_impresoraObj.items.size();
            for (int i = 0; i <cimp; i++) {
                listdlg.add(P_impresoraObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idimp1=P_impresoraObj.items.get(position).codigo_impresora;
                        showListaImpresoras2();
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

    private void showListaImpresoras2() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Orden.this,"¿A cual impresora redireccionar?");

            P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA<>"+idimp1+") ORDER BY NOMBRE");
            int cimp=P_impresoraObj.items.size();
            for (int i = 0; i <cimp; i++) {
                listdlg.add(P_impresoraObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idimp2=P_impresoraObj.items.get(position).codigo_impresora;
                        redireccionarImpresora();
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

    private void validaSupervisor() {
        clsClasses.clsVendedores item;

        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            app.fillSuper(VendedoresObj);

            if (VendedoresObj.count==0) {
                msgbox("No está definido ningún supervisor");return;
            }

            extListPassDlg listdlg = new extListPassDlg();
            listdlg.buildDialog(Orden.this,"Autorización","Salir");

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
                        if (valsupermodo==0) {
                            msgBorrarOrden("Cerrar todas las cuentas de la órden");
                        } else if (valsupermodo==1) {
                            msgAskDelCom("Borrar artículo");
                        }

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
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void msgBoxWifi(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  );
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (gl.peCajaPricipal!=gl.codigo_ruta) cerrarOrden();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {

        try {
            super.onResume();

            gridView.setEnabled(true);

            P_productoObj.reconnect(Con,db);
            P_linea_impresoraObj.reconnect(Con,db);
            T_comandaObj.reconnect(Con,db);
            T_orden_notaObj.reconnect(Con,db);
            P_impresoraObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);
            T_ordencomboObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_orden_modObj.reconnect(Con,db);
            T_orden_ingObj.reconnect(Con,db);
            D_barril_transObj.reconnect(Con,db);
            P_orden_numeroObj.reconnect(Con,db);

            try {
                P_nivelprecioObj.reconnect(Con,db);
            } catch (Exception e) { }

            if (gl.forcedclose) {
                cerrarOrden();
                return;
            }

            gl.climode=true;
            menuTools();

            if (gl.iniciaVenta) {

                browse=0;

                gl.nivel=gl.nivel_sucursal;

                try  {
                    db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"')");
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
                            startActivity(new Intent(Orden.this,Clientes.class));
                        } else {
                            if (!gl.cliposflag) {
                                gl.cliposflag=true;
                                if (!gl.forcedclose) {
                                    startActivity(new Intent(Orden.this,CliPos.class));
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
                browse=0;cerrarOrden();return;
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

            if (browse==6) {
                browse=0;updateCant();return;
            }

            if (browse==8) {
                browse=0;
                if (gl.forcedclose) {
                    cerrarOrden();
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
                if (gl.checksuper) {
                    delItem();
                }
                return;
            }

            if (browse==11) {
                browse=0;if (gl.checksuper) msgBorrarOrden("Cerrar todas las cuentas del orden");
                return;
            }

        } catch (Exception e){ }
    }

    @Override
    protected void onPause() {
        try {
            //ctimer.removeCallbacks(crunner);
        } catch (Exception e) {}
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //if (actorden) envioOrden();
        //gl.cerrarmesero=true;
        //super.onBackPressed();
    }

    //endregion

}