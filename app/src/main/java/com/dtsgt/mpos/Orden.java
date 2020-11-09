package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsOrden;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsBonFiltro;
import com.dtsgt.classes.clsBonif;
import com.dtsgt.classes.clsDescFiltro;
import com.dtsgt.classes.clsDescuento;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.ladapt.ListAdaptGridFam;
import com.dtsgt.ladapt.ListAdaptGridFamList;
import com.dtsgt.ladapt.ListAdaptGridProd;
import com.dtsgt.ladapt.ListAdaptGridProdList;
import com.dtsgt.ladapt.ListAdaptMenuVenta;
import com.dtsgt.ladapt.ListAdaptOrden;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.srvPedidoEstado;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Orden extends PBase {

    private ListView listView;
    private GridView gridView,grdbtn,grdfam,grdprod;
    private TextView lblTot,lblTit,lblAlm,lblVend;
    private TextView lblProd,lblDesc,lblStot,lblKeyDP,lblPokl,lblDir;
    private ImageView imgroad;

    private ArrayList<clsClasses.clsOrden> items= new ArrayList<clsOrden>();
    private ListAdaptOrden adapter;
    private clsOrden selitem;
    private Precio prc;

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

    private clsP_nivelprecioObj P_nivelprecioObj;
    private clsP_productoObj P_productoObj;

    private clsRepBuilder rep;

    private int browse;
    private double cant,desc,mdesc,prec,precsin,imp,impval;
    private double descmon,tot,totsin,percep,ttimp,ttperc,ttsin,prodtot,savecant;
    private double px,py,cpx,cpy,cdist,savetot,saveprec;

    private String uid,seluid,prodid,uprodid,um,tiposcan,barcode,imgfold,tipo,pprodname,mesa,nivname;
    private int nivel,dweek,clidia,counter;
    private boolean sinimp,softscanexist,porpeso,usarscan,handlecant=true,descflag;
    private boolean decimal,menuitemadd,usarbio,imgflag,scanning=false,prodflag=true,listflag=true;
    private int codigo_cliente, emp,cod_prod,cantcuentas;
    private String idorden,cliid,saveprodid;
    private int famid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden);

        super.InitBase();

        setControls();

        P_nivelprecioObj=new clsP_nivelprecioObj(this,Con,db);
        P_nivelprecioObj.fill("ORDER BY Nombre");

        gl.scancliente="";
        emp=gl.emp;
        gl.nivel_sucursal=nivelSucursal();
        gl.nivel=gl.nivel_sucursal;nivel=gl.nivel;
        idorden=gl.idorden;

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

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

        counter=1;

        prc=new Precio(this,mu,2);

        setHandlers();
        initValues();

        browse=0;
        clearItem();

        if (P_nivelprecioObj.count==0) {
            toastlong("NO SE PUEDE VENDER, NO ESTÁ DEFINIDO NINGUNO NIVEL DE PRECIO");finish();return;
        }

        imgflag=false;//imgflag=gl.peMImg;

        setVisual();

        listItems();

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
        exitBtn();
    }

    public void subItemClick(int position,int handle) {
    }

    private void setHandlers(){

        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    int flag;
                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsOrden item = (clsOrden)lvObj;selitem=item;

                        prodid=item.Cod;gl.produid=item.id;

                        flag=items.get(position).estado;
                        if (flag==0) flag=1;else flag=0;
                        items.get(position).estado=flag;

                        try {
                            sql="UPDATE T_orden SET ESTADO="+flag+" WHERE (ID="+item.id+") AND (COREL='"+idorden+"')";
                            db.execSQL(sql);

                            sql="UPDATE P_res_sesion SET FECHAULT="+du.getActDateTime()+" WHERE (ID='"+idorden+"')";
                            db.execSQL(sql);

                        } catch (SQLException e) {
                            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }

                        adapter.setSelectedIndex(position);
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsOrden item = (clsOrden)lvObj;selitem=item;

                        prodid=item.Cod;gl.prodid=prodid;
                        gl.produid=item.id;
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
                          } else if (tipo.equalsIgnoreCase("M")) {
                            gl.newmenuitem=false;
                            gl.menuitemid=item.emp;
                            browse=7;
                        }
                        showItemPopMenu();
                   } catch (Exception e) {
                        mu.msgbox( e.getMessage());
                    }

                    return true;
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

                        processItem(false);

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
                        processMenuTools(item.ID);
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
        clsOrden item;
        double tt,stot,tdesc,desc;
        int ii;

        items.clear();tot=0;ttimp=0;ttperc=0;tdesc=0;selidx=-1;ii=0;seluid="";

        try {
            sql="SELECT T_ORDEN.PRODUCTO, P_PRODUCTO.DESCCORTA, T_ORDEN.TOTAL, T_ORDEN.CANT, T_ORDEN.PRECIODOC, " +
                "T_ORDEN.DES, T_ORDEN.IMP, T_ORDEN.PERCEP, T_ORDEN.UM, T_ORDEN.PESO, T_ORDEN.UMSTOCK, " +
                "T_ORDEN.DESMON, T_ORDEN.EMPRESA, T_ORDEN.CUENTA, T_ORDEN.ESTADO, T_ORDEN.ID " +
                "FROM T_ORDEN INNER JOIN P_PRODUCTO ON P_PRODUCTO.CODIGO=T_ORDEN.PRODUCTO "+
                "WHERE (COREL='"+idorden+"') ORDER BY T_ORDEN.ID ";

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();
                while (!DT.isAfterLast()) {

                    tt=DT.getDouble(2);

                    item = clsCls.new clsOrden();

                    item.Cod=DT.getString(0);
                    item.Nombre=DT.getString(1);
                    item.Cant=DT.getDouble(3);
                    item.icant=(int) item.Cant;
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

        adapter=new ListAdaptOrden(this,this, items);
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
                }
            } else if (tipo.equalsIgnoreCase("M")){
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

            clsDesc = new clsDescuento(this, ""+cod_prod, cant);
            desc = clsDesc.getDesc();
            mdesc = clsDesc.monto;
            */

            desc =0;mdesc=0;
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
        int newid;

        tipo=prodTipo(gl.prodcod);

        /*
         if (tipo.equalsIgnoreCase("P") || tipo.equalsIgnoreCase("S")) {
            try {
                sql="SELECT Empresa,Cant FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (PRODUCTO='"+prodid+"')";
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
        */

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
            sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            newid=dt.getInt(0)+1;
        } catch (Exception e) {
            newid=1;
        }

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
            ins.add("VAL2","0");
            ins.add("VAL3",0);
            ins.add("VAL4","0");
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

        listItems();

        return true;
    }

    private boolean addItemMenu(){
        Cursor dt;
        double precdoc,fact,cantbas,peso;
        String umb;
        int newid;

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
            sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            newid=dt.getInt(0)+1;
        } catch (Exception e) {
            newid=1;
        }

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

        listItems();

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

            upd.Where("(COREL='"+idorden+"') AND (EMPRESA='"+uid+"')");

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
            db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            listItems();
        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());
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

            envioMesa(2);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicarPago() {
        try {
            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            envioMesa(3);

            sess.estado=3;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cerrarCuentas() {
        try {
            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            sess.estado=-1;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            envioMesa(-1);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void splitItem() {
        clsClasses.clsT_orden item,fitem;
        int cnt,newid;

        try {
            db.beginTransaction();

            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);
            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ID="+selitem.id+")");
            fitem=T_ordenObj.first();
            cnt=(int) fitem.cant;if (cnt<=1) return;

            newid=T_ordenObj.newID("SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')");

            fitem.cant=1;
            T_ordenObj.update(fitem);

            for (int i = 1; i <cnt; i++) {
                //item = clsCls.new clsT_orden();
                item = fitem;
                item.id=newid;newid++;
                item.cant=1;

                T_ordenObj.add(item);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Menus

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

    private void menuTools() {

        clsClasses.clsMenu item;

        mitems.clear();

        try {

            item = clsCls.new clsMenu();
            item.ID=52;item.Name="Cuentas";item.Icon=12;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=3;item.Name="Preimpresión";item.Icon=3;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=1;item.Name="Pago";item.Icon=58;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=2;item.Name="Completar";item.Icon=55;
            mitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=50;item.Name="Buscar ";item.Icon=64;
            mitems.add(item);

            /*
            item = clsCls.new clsMenu();
            item.ID=24;item.Name="Salir";item.Icon=57;
            mitems.add(item);
            */

            adaptergrid=new ListAdaptMenuVenta(this, mitems);
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
                    if (est==2) {
                        s="Procesar pago";
                    } else {
                        if (est==1) {
                            s="La mesa esta pendiente de preimpresion.\nProcesar pago";
                        } else {
                            s="La mesa esta pendiente de completar.\nProcesar pago";
                        }
                    }
                    msgAskOrden(s);break;
                case 2:
                    if (est==3) {
                        s="Completar la mesa";
                    } else {
                        if (est==1) {
                            s="La mesa esta pendiente de preimpresion.\nCompletar orden";
                        } else {
                            s="La mesa esta pendiente de pago.\nCompletar orden";
                        }
                    }
                    msgAskCuentas(s);break;
                case 3:
                    if (est==1) {
                        s="Procesar preimpresion";
                    } else {
                        if (est==2) {
                            s="La mesa esta pendiente de pago.\nProcesar preimpresion";
                        } else {
                            s="La mesa esta pendiente de completar.\nProcesar preimpresion";
                        }
                    }
                    msgAskPrint(s);break;
                case 24:
                    exitBtn();break;
                case 50:
                    gl.gstr = "";browse=1;gl.prodtipo=1;
                    startActivity(new Intent(this, Producto.class));
                    break;
                case 52:
                    break;
            }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void exitBtn() {
        finish();
    }

    //endregion

    //region Comanda

    private void imprimeComanda() {
        clsT_ordenObj T_ventaObj=new clsT_ordenObj(this,Con,db);
        clsT_ordencomboObj T_comboObj=new clsT_ordencomboObj(this,Con,db);
        clsClasses.clsT_orden venta;
        clsClasses.clsT_ordencombo combo;

        int prid,idcombo;

        try {
            rep.clear();
            rep.empty();
            rep.add("ORDEN MESA : "+mesa);
            rep.empty();
            rep.add("Hora : "+du.shora(du.getActDateTime()));
            rep.line();
            rep.empty();

            T_ventaObj.fill();

            for (int i = 0; i <T_ventaObj.count; i++) {
                venta=T_ventaObj.items.get(i);

                prid=app.codigoProducto(venta.producto);
                s=mu.frmdecno(venta.cant)+" x "+getProd(prid);
                rep.add(s);

                if (app.prodTipo(prid).equalsIgnoreCase("M")) {
                    T_comboObj.fill("WHERE IdCombo="+venta.val4);

                    for (int j = 0; j <T_comboObj.count; j++) {
                        if (j==0) rep.line();
                        s=" -  "+getProd(T_comboObj.items.get(j).idseleccion);
                        rep.add(s);
                    }
                    rep.line();
                }
            }

            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.save();

            //app.doPrint(1);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private String getProd(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) return P_productoObj.items.get(i).desclarga;
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

    //endregion

    //region Envio

    private void envioMesa(int estado) {
       String cmd="";

       estadoMesa(estado);

       try {

           if (estado>1) {
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

           Intent intent = new Intent(Orden.this, srvOrdenEnvio.class);
           intent.putExtra("URL",gl.wsurl);
           intent.putExtra("command",cmd);
           startService(intent);

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
                            startActivity(new Intent(Orden.this,RepesajeLista.class));break;
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
            gridView = (GridView) findViewById(R.id.gridView2);gridView.setEnabled(true);
            grdfam = (GridView) findViewById(R.id.grdFam);
            grdprod = (GridView) findViewById(R.id.grdProd);
            grdbtn = (GridView) findViewById(R.id.grdbtn);

            lblTot= (TextView) findViewById(R.id.lblTot);
            lblDesc= (TextView) findViewById(R.id.textView115);lblDesc.setText( "Desc : "+mu.frmcur(0));
            lblStot= (TextView) findViewById(R.id.textView103); lblStot.setText("Subt : "+mu.frmcur(0));
            lblTit= (TextView) findViewById(R.id.lblTit);
            lblAlm= (TextView) findViewById(R.id.lblTit2);
            lblVend= (TextView) findViewById(R.id.lblTit4);
            lblPokl= (TextView) findViewById(R.id.lblTit5);

            lblKeyDP=(TextView) findViewById(R.id.textView110);
            lblDir=(TextView) findViewById(R.id.lblDir);

            imgroad= (ImageView) findViewById(R.id.imgRoadTit);

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
        lblVend.setText(gl.mesanom);
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

            if (DT!=null) DT.close();

        } catch (Exception e) {

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

    private void getURL() {
        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 20000;

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 20000; else gl.timeout = Integer.valueOf(line);
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

            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public void agregarCuenta() {
        try {
            cantcuentas++;

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            cuenta.corel=idorden;
            cuenta.id=cantcuentas;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            T_ordencuentaObj.add(cuenta);

            asignaCuenta(cantcuentas);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    private void showItemPopMenu() {
        final AlertDialog Dialog;
        final String[] selitems = {"Modificar","Cambiar cuenta","Borrar","Dividir"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Articulo del orden");

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        if (browse==6) {
                            startActivity(new Intent(Orden.this,VentaEdit.class));
                        } else if (browse==7) {
                            startActivity(new Intent(Orden.this,OrdenMenu.class));
                        }
                        break;
                    case 1:
                        showMenuCuenta();break;
                    case 2:
                        msgAskDel("Está seguro de borrar");break;
                    case 3:
                        if (selitem.Cant>1) {
                            msgAskDividir("Dividir articulo");
                        } else {
                            toastcent("Articulo con cantidad 1 no se puede dividir");
                        }
                        break;
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

    private void msgAskOrden(String msg) {

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
                cerrarCuentas();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

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
        final AlertDialog Dialog;

        try {

            T_ordencuentaObj.fill("WHERE (COREL='"+idorden+"') ORDER BY ID");

            final String[] selitems = new String[T_ordencuentaObj.count];

            cantcuentas=T_ordencuentaObj.count;

            for (int i = 0; i <T_ordencuentaObj.count; i++) {
                selitems[i]=""+(i+1);
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Seleccione una cuenta");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    asignaCuenta(item+1);

                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            menudlg.setPositiveButton("Nueva cuenta", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    agregarCuenta();
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

            gridView.setEnabled(true);

            P_productoObj.reconnect(Con,db);

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
        }
    }

    //endregion

}