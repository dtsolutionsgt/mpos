package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.ladapt.LA_ResCaja;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.wsOpenDT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TimerTask;

public class ResCaja extends PBase {

    private GridView gridView;
    private TextView lblRec;
    private ImageView imgRec,imgnowifi;
    private RelativeLayout relmain;

    private LA_ResCaja adapter;
    private clsViewObj ViewObj;

    private clsT_ordenObj T_ordenObj;
    private clsT_ordencomboObj T_ordencomboObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;
    private clsT_ventaObj T_ventaObj;
    private clsT_comboObj T_comboObj;

    private wsOpenDT wso;

    //private wsOrdenRecall wsor;

    private ArrayList<String> brtitems = new ArrayList<String>();

    private clsClasses.clsT_orden oitem;

    //private Runnable rnCargaOrdenes;
    //private Runnable rnOrdenesNuevos;

    private Runnable rnBroadcastCallback;
    private Runnable rnDetailCallback;

    private Precio prc;

    private String corel,mesa,numpedido,brtcorel;
    private int cuenta,counter,idmesero;
    private boolean idle=true,exitflag=false,loading=false;

    private TimerTask ptask;
    private int period=10000,delay=50;
    private boolean horiz,espedido,actorden,wsidle=true;

    private String orddir= Environment.getExternalStorageDirectory().getPath() + "/mposordcaja";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_caja);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblRec = findViewById(R.id.textView212);
        imgRec = findViewById(R.id.imageView87);
        imgnowifi=findViewById(R.id.imageView71a);
        relmain=findViewById(R.id.relcmain);

        calibraPantalla();

        ViewObj=new clsViewObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);
        T_ordencomboObj=new clsT_ordencomboObj(this,Con,db);
        T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);
        T_ventaObj=new clsT_ventaObj(this,Con,db);
        T_comboObj = new clsT_comboObj(this, Con, db);

        actorden=gl.peActOrdenMesas;

        prc=new Precio(this,mu,2);

        setHandlers();
        listItems();
        gl.ventalock=false;

        getURL();
        wso=new wsOpenDT(gl.wsurl);wsidle=true;
        rnBroadcastCallback = () -> broadcastCallback();

        rnDetailCallback = new Runnable() {
            public void run() {
                detailCallback();
            }
        };

        /*
        rnOrdenesNuevos = new Runnable() {
            public void run() { procesaOrdenes(); }
        };*/

        //if (!app.modoSinInternet())
        imgnowifi.setVisibility(View.INVISIBLE);

    }

    //region Events

    public void doRec(View view) {
        recibeOrdenes();
    }

    public void doOrden(View view) { }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                clsClasses.clsView item = (clsClasses.clsView)lvObj;

                adapter.setSelectedIndex(position);

                corel=item.f1;mesa=item.f2;cuenta=item.pk;
                gl.ordcorel=corel;gl.primesa=mesa;
                gl.pricuenta=""+cuenta;gl.nocuenta_precuenta= gl.pricuenta;
                espedido=app.esmesapedido(gl.emp,item.f5);
                gl.EsVentaDelivery=espedido;
                numpedido=item.f6;

                showMenuMesa();
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        long ff,flim=du.addHours(-12);
        String fs,ssa;

        try {
            sql="SELECT T_ORDENCUENTA.ID AS Cuenta, P_RES_SESION.ID AS Corel, P_RES_MESA.NOMBRE, P_RES_SESION.ESTADO, P_RES_SESION.FECHAULT , " +
                    "P_RES_MESA.CODIGO_GRUPO,P_RES_SESION.CANTC,'','' " +
                    "FROM P_RES_SESION INNER JOIN " +
                    "T_ORDENCUENTA ON P_RES_SESION.ID =T_ORDENCUENTA.COREL INNER JOIN " +
                    "P_RES_MESA ON P_RES_SESION.CODIGO_MESA = P_RES_MESA.CODIGO_MESA " +
                    "WHERE (P_RES_SESION.ESTADO IN (1, 2, 3)) AND (P_RES_SESION.FECHAINI>="+flim+") " +
                    "ORDER BY P_RES_MESA.NOMBRE, CUENTA ";
            ViewObj.fillSelect(sql);

            for (int i = 0; i <ViewObj.count; i++) {
                fs=ViewObj.items.get(i).f4;
                try {
                    ff=Long.parseLong(fs);
                    fs=du.shora(ff);
                } catch (Exception e) {
                    fs="";
                }
                ViewObj.items.get(i).f4=fs;

                if (cuentaPagada(ViewObj.items.get(i).f1,ViewObj.items.get(i).pk)) {
                    ViewObj.items.get(i).f3="4";
                }

                if (app.esmesapedido(gl.emp,ViewObj.items.get(i).f5)) {
                    ViewObj.items.get(i).f7="#"+ViewObj.items.get(i).f6;
                } else ViewObj.items.get(i).f7="";
                //ViewObj.items.get(i).f7="#"+ViewObj.items.get(i).f6;
            }

            for (int  i=ViewObj.count-1; i>=0; i--) {
                if (ViewObj.items.get(i).f3.equalsIgnoreCase("4")) ViewObj.items.remove(i);
            }

            adapter=new LA_ResCaja(this,this,ViewObj.items);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void completeItem() {
        try {
            //osql+=sql+";";
            sql="UPDATE P_RES_SESION SET ESTADO=-1,FECHAFIN="+du.getActDateTime()+",FECHAULT="+du.getActDateTime()+" WHERE ID='"+corel+"'";
            db.execSQL(sql);
            if (gl.pelCajaRecep) enviaCompleto(sql);
            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void anulaItem() {

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_RES_SESION WHERE (ID='"+corel+"')");
            db.execSQL("DELETE FROM T_orden WHERE (COREL='" + corel + "')");
            db.execSQL("DELETE FROM T_ordencuenta WHERE (COREL='" + corel + "')");

            db.setTransactionSuccessful();
            db.endTransaction();

            anulaOrden();
            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    private void anulaOrden() {
        String cmd="";

        try {

            cmd += "DELETE FROM P_res_sesion WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + corel + "')" + ";";
            cmd += "DELETE FROM T_orden WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + corel + "')" + ";";
            cmd += "DELETE FROM T_ordencuenta WHERE (EMPRESA=" + gl.emp + ") AND (COREL='" + corel + "')" + ";";

            cmd+=buildAnulJournal();

            try {
                Intent intent = new Intent(ResCaja.this, srvOrdenEnvio.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                startService(intent);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String buildAnulJournal() {
        clsClasses.clsT_ordencom pitem;
        clsClasses.clsT_ordencuenta citem;
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
                    pitem.corel_orden=corel;
                    pitem.corel_linea=1;

                    pitem.comanda="DELETE FROM P_RES_SESION WHERE (ID=<>"+corel+"<>)";
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                    pitem.comanda="DELETE FROM T_orden WHERE (COREL=<>" + corel + "<>)";
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                    pitem.comanda="DELETE FROM T_ordencuenta WHERE (COREL=<>" + corel + "<>)" ;
                    ss+=addItemSqlOrdenCom(pitem) + ";";

                }
            }

            return ss;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return "";
        }
    }

    public String addItemSqlOrdenCom(clsClasses.clsT_ordencom item) {

        ins.init("T_ordencom");

        ins.add("CODIGO_RUTA",item.codigo_ruta);
        ins.add("COREL_ORDEN",item.corel_orden);
        ins.add("COREL_LINEA",item.corel_linea);
        ins.add("COMANDA",item.comanda);

        return ins.sql();

    }

    //endregion

    //region Venta

    private void crearVenta() {
        if (actorden) {
            ordenDetail();
        } else {
            buildVenta();
        }
    }

    private void buildVenta() {

        try {

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+corel+"'");
            gl.mesero_venta=P_res_sesionObj.first().vendedor;


            gl.numero_orden=corel+"_"+cuenta;
            gl.nummesapedido=numpedido;

            T_ordenObj.fill("WHERE COREL='"+corel+"'");
            counter=0;

            for (int i = 0; i <T_ordenObj.count; i++) {
                oitem=T_ordenObj.items.get(i);
                if (oitem.cuenta==cuenta) addItem();
            }

            gl.cuenta_borrar=cuenta;
            gl.caja_est_pago    ="UPDATE T_ORDEN SET ESTADO=2 WHERE ((COREL='"+corel+"') AND (CUENTA="+cuenta+"))";
            gl.caja_est_pago_cmd="UPDATE T_ORDEN SET ESTADO=2 WHERE ((COREL=<>"+corel+"<>) AND (CUENTA="+cuenta+"))";
            gl.caja_est_pago_cue="DELETE FROM T_ORDENCUENTA WHERE ((COREL=<>"+corel+"<>) AND (ID="+cuenta+"))";

            cargaCliente();

            gl.ventalock=true;

            finish();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean addItem(){
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
            //venta.empresa=oitem.empresa;
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

            T_ordencomboprecioObj.fill("WHERE (COREL='"+corel+"') AND (IDCOMBO="+oitem.empresa+")");

            if (T_ordencomboprecioObj.count>0) {
                venta.precio=T_ordencomboprecioObj.first().prectotal;
                venta.preciodoc=venta.precio;
                tt=venta.cant*venta.precio;tt=mu.round2(tt);
                venta.total=tt;
                oitem.total=tt;
            }

            //T_ventaObj.add(venta);

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
                }else{
                    if (T_ventaObj.count==0) {
                        T_ventaObj.add(venta);
                    } else {
                        ventau.cant+= oitem.cant;
                        ventau.total = ventau.total+mu.round(oitem.total,2);
                        T_ventaObj.update(ventau);
                    }
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            if (app.prodTipo(venta.producto).equalsIgnoreCase("M")) {

                T_ordencomboObj.fill("WHERE (COREL='"+corel+"') AND (IDCOMBO="+oitem.empresa+")");
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
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    //endregion

    //region Detalle


    private void ordenDetail() {

        try {
            brtcorel=corel;

            sql="SELECT ID, COREL, PRODUCTO, EMPRESA, UM, CANT, UMSTOCK, FACTOR, PRECIO, IMP, " +
                    "DES, DESMON, TOTAL, PRECIODOC, PESO, VAL1, VAL2, VAL3, VAL4, PERCEP, CUENTA, ESTADO " +
                    "FROM T_ORDEN WHERE (COREL='"+ brtcorel +"') AND (CUENTA="+cuenta+")";
            wso.execute(sql,rnDetailCallback);
        } catch (Exception e) {
             toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void detailCallback() {

        clsClasses.clsT_orden btitem;

        try {

            brtitems.clear();

            if (wso.errflag) {
                //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+wso.error);
                //msgAskPagar("No se logró conectar al servidor.\nEs posible que la cuenta NO ESTÁ actualizada.");
                msgAskPagar("Por favor revise la cuenta.");

                return;
            }

            if (wso.openDTCursor.getCount()==0) return;

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

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return;
        }

        try {
            db.beginTransaction();

            for (int i = 0; i <brtitems.size(); i++) {
                sql=brtitems.get(i);
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            buildVenta();
        } catch (Exception e) {
            db.endTransaction();
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

    //endregion

    //region Recepcion

    private void broadcastCallback() {
        wsidle=true;
        if (wso.errflag) {
            //toastlong("wsCallBack "+wso.error);
            showButton();
            msgBoxWifi("No hay conexíon al internet");
            relmain.setBackgroundColor(Color.parseColor("#F4C6D0"));
        } else {
            procesaOrdenes();
        }
        cierraPantalla();
    }

    private void recibeOrdenes() {

        if (!wsidle) return;

        try {
            wsidle=false;
            lblRec.setVisibility(View.INVISIBLE);imgRec.setVisibility(View.INVISIBLE);

            sql="SELECT  CODIGO, COREL_ORDEN, COMANDA, COREL_LINEA FROM T_ORDENCOM " +
                "WHERE (CODIGO_RUTA="+gl.codigo_ruta+") " +
                "AND (COREL_LINEA IN (1,3,99,100)) " +
                "ORDER BY COREL_ORDEN,CODIGO";
            wso.execute(sql,rnBroadcastCallback);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            showButton();
            wsidle=true;
        }
    }

    private void procesaOrdenes() {
        int iid,trtipo;
        String cor,cmd,del="",ins="";

        try {
            if (wso.openDTCursor.getCount()==0) {
                showButton();
                return;
            }

            wso.openDTCursor.moveToFirst();
            cmd = "";

            while (!wso.openDTCursor.isAfterLast()) {

                iid = wso.openDTCursor.getInt(0);
                cor = wso.openDTCursor.getString(1);
                sql = wso.openDTCursor.getString(2);
                trtipo = wso.openDTCursor.getInt(3);

                del = "DELETE FROM P_res_sesion WHERE ID='" + cor + "'";
                ins = sql.replaceAll("<>", "'");

                try {
                    db.beginTransaction();

                    if (trtipo==1) {
                        db.execSQL(del);
                    }

                    switch (trtipo) {
                        case 3:
                            try {
                                db.execSQL(ins);
                            } catch (SQLException e) {
                                String ee=e.getMessage();
                            }
                            break;
                        case 100:
                            //aplicaNombreMesa(cor,sql)
                            break;
                        default:
                            db.execSQL(ins);break;
                    }

                    db.setTransactionSuccessful();
                    db.endTransaction();

                    cmd += "DELETE FROM T_ORDENCOM WHERE CODIGO=" + iid + ";";
                } catch (Exception e) {
                    String se=e.getMessage();
                    db.endTransaction();
                    //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage() + "\n" + del + "\n" + ins);
                    return;
                }

                wso.openDTCursor.moveToNext();
            }

            if (!cmd.isEmpty()) confirmaOrdenes(cmd);
        } catch (Exception e) {
            //msgbox(new Object() { }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

        showButton();

        listItems();
    }

    private void showButton() {
        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                lblRec.setVisibility(View.VISIBLE);imgRec.setVisibility(View.VISIBLE);
            }
        };
        mtimer.postDelayed(mrunner,2000);
    }

    private void confirmaOrdenes(String cmd) {
        try {
            Intent intent = new Intent(ResCaja.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",cmd);
            startService(intent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    /*
    //region Recepcion Vieja

    //region Recepcion orig

    private void recibeOrdenes() {
        int pp;
        String fname;

        if (loading) return;

        loading=true;

        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/mposordcaja";
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                fname=files[i].getName();
                pp=fname.indexOf(".txt");
                if (pp>0){
                    if (!agregaOrden(path+"/"+fname)) {
                        msgbox2("Ocurrio error en la recepción de requerimiento :\n"+app.errstr);
                    }
                }
            }
        } catch (Exception e) {
            msgbox2("recibeOrdenes : "+e.getMessage());
        }

        loading=false;
        listItems();

    }

    public boolean agregaOrden(String fname) {
        File file=null;
        BufferedReader br=null;
        ArrayList<String> items=new ArrayList<String>();
        String sql;int lim;
        boolean flag=true;

        app.errstr="";

        try {
            file=new File(fname);
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            app.errstr=e.getMessage();return false;
        }

        if (flag) {
            try {
                db.beginTransaction();

                while ((sql=br.readLine())!= null) {
                    items.add(sql);
                    db.execSQL(sql);
                }

                db.setTransactionSuccessful();
                db.endTransaction();

            } catch (Exception e) {
                db.endTransaction();app.errstr=e.getMessage();
                return false;
            }
        }

        try {
            br.close();
        } catch (Exception e) {
            app.errstr=e.getMessage();
        }

        file.delete();

        return true;
    }

    private void iniciaOrdenes() {
        if (!gl.pelCajaRecep) return;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(ptask=new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        recibeOrdenes();
                    }
                });
            }
        }, delay, period);
    }

    private void cancelaOrdenes() {
        if (!gl.pelCajaRecep) return;

        try {
            ptask.cancel();
        } catch (Exception e) {}
    }

    //endregion

    //region Recepcion de ordenes - recall

    private void listaOrdenes() {
        try {
            //wsor =new wsOrdenRecall(gl.wsurl,gl.emp,idmesero);
            //wsor.execute(rnOrdenesNuevos);
            //("Actualizando, espere por favor . . .");
        } catch (Exception e) {
            app.addToOrdenLog(du.getActDateTime(),
                    "ResCaja."+new Object(){}.getClass().getEnclosingMethod().getName(),
                    e.getMessage(),gl.wsurl+"::"+gl.emp+"::"+idmesero);
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void procesaOrdenes() {
        FileWriter wfile=null;
        BufferedWriter writer=null;
        File file;
        String s="",corel="",fname;
        int pp,ppe;
        boolean flag=false;

        if (wsor.errflag) {
            toastlong("wsCallBack " + wsor.error);
            app.addToOrdenLog(du.getActDateTime(),
                    "ResCaja." + new Object() {}.getClass().getEnclosingMethod().getName(),
                    wsor.error, gl.wsurl + "::" + gl.emp + "::" + idmesero);
        }

        if (wsor.items.size()==0) {
            return;
        }

        try {

            for (int i = 0; i < wsor.items.size(); i++) {

                s= wsor.items.get(i);
                pp=s.indexOf("FILE");ppe=s.indexOf("ENDFILE");

                if (pp == 0) {
                    corel = s.substring(4);fname = orddir + "/" + corel + ".txt";
                    file = new File(fname);flag = !file.exists();

                    if (flag) {

                        try {
                            wfile = new FileWriter(fname, false);
                            writer = new BufferedWriter(wfile);

                            s="DELETE FROM P_RES_SESION WHERE ID='"+corel+"'";
                            writer.write(s);writer.write("\r\n");
                            s="DELETE FROM T_ORDEN WHERE COREL='"+corel+"'";
                            writer.write(s);writer.write("\r\n");
                            s="DELETE FROM T_ORDENCUENTA WHERE COREL='"+corel+"'";
                            writer.write(s);writer.write("\r\n");

                        } catch (IOException e) {
                            msgbox("MPos recall error : "+e.getMessage());flag=false;
                        }
                    } else {
                        //items.add(corel);
                    }
                } else if (ppe == 0) {
                    if (flag) {
                        try {
                            writer.close();writer = null;wfile = null;
                            //items.add(corel);
                        } catch (IOException e) {
                             msgbox("MPos recall error : "+e.getMessage());
                        }
                    }
                } else {
                    if (flag) {
                        try {
                            writer.write(s);writer.write("\r\n");
                        } catch (IOException e) {
                            msgbox("MPos recall error : "+e.getMessage());flag=false;
                        }
                    }
                }

            }

            msgbox("Lista de ordenes actualizada");
        } catch (Exception e) {
            msgbox("MPos recall error : "+e.getMessage());
        }


    }

    public void listaMeseros() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResCaja.this,"Mesero");

            clsVendedoresObj VendedoresObj = new clsVendedoresObj(this, Con, db);
            sql = "WHERE (RUTA IN (SELECT CODIGO_RUTA FROM P_RUTA WHERE SUCURSAL="+gl.tienda+")) AND " +
                    "(NIVEL=4) AND (ACTIVO=1) ORDER BY NOMBRE";
            VendedoresObj.fill(sql);

            for (int i = 0; i <VendedoresObj.count; i++) {
                listdlg.add(VendedoresObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idmesero=VendedoresObj.items.get(position).codigo_vendedor;
                        listaOrdenes();
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

    //endregion
    */

    //region Aux

    private boolean ventaVacia() {
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

    private void datosCuenta() {
        try {
            startActivity(new Intent(this,ResCliente.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaCliente() {
        try {

            gl.codigo_cliente=gl.emp*10;

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+corel+"') AND (ID="+cuenta+")");

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

    private Boolean cuentaPagada(String corr,int id) {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
            return D_facturaObj.count!=0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private Boolean cuentaVacia(String corr,int id) {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
            return D_facturaObj.count!=0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private void calibraPantalla() {
        if (pantallaHorizontal()) horiz=true; else horiz=false;

        if (horiz) {
            //lblmes.setTextSize(36);lblgrupo.setTextSize(36);
            gridView.setNumColumns(4);
        } else {
            //lblmes.setTextSize(20);lblgrupo.setTextSize(20);
            gridView.setNumColumns(2);
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

    private void enviaCompleto(String csql) {
        try {
            Intent intent = new Intent(ResCaja.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",csql);
            startService(intent);
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    private void cierraPantalla() {
        try {
            Handler ctimer = new Handler();
            Runnable crunner=new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            };
            ctimer.postDelayed(crunner,20000);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    private void showMenuMesa() {
        clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);

        try {
            D_facturaObj.fill("WHERE (FACTLINK='"+corel+"_"+cuenta+"')");

            if (D_facturaObj.count==0) {
                showMenuMesaPendiente();
            } else {
                showMenuMesaCompleta();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showMenuMesaPendiente() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResCaja.this,"Mesa "+mesa+" , Cuenta #"+cuenta);

            listdlg.add("Precuenta");
            listdlg.add("Datos cliente");
            listdlg.add("Pagar");
            listdlg.add("Borrar");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                if (ventaVacia()) {
                                    crearVenta();
                                } else msgbox("Antes de imprimir precuenta debe terminar la venta actual");
                                break;
                            case 1:
                                datosCuenta();break;
                            case 2:
                                if (ventaVacia()) {

                                    if (gl.pePropinaFija) {
                                        crearVenta();
                                    } else {
                                        inputPropina();
                                    }

                                } else msgbox("Antes de pagar la cuenta debe terminar la venta actual");
                                //msgAskPago("Pagar la cuenta "+cuenta);
                                break;
                            case 3:
                                validaSupervisor();
                                //browse=1;startActivity(new Intent(ResCaja.this,ValidaSuper.class));
                                break;
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

    private void msgAskPagar(String msg) {
        try {

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    buildVenta();
                }
            });

            dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });


            dialog.show();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void msgAskPagarOrig(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage(msg);

        dialog.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                buildVenta();
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void showMenuMesaCompleta() {

        //msgAskCompletar("Completar mesa "+mesa);

    }

    private void inputPropina() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Monto propina");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    crearVenta();
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

    private void msgAskPreimpresion(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (ventaVacia()) {
                    crearVenta();
                } else msgbox("Antes de preimprimir la cuenta debe terminar la venta actual");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskPago(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (ventaVacia()) {
                    crearVenta();
                } else msgbox("Antes de pagar la cuenta debe terminar la venta actual");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskCompletar(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        if (!ventaVacia()) {
            if (!app.validaCompletarCuenta(corel)) {
                msgbox("No se puede completar la mesa,\nexisten cuentas pendientes de pago.");return;
            }
        }

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                completeItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskBorrar(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                anulaItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void validaSupervisor() {
        clsClasses.clsVendedores item;

        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE (RUTA=" + gl.codigo_ruta+") AND ((NIVEL=2) OR (NIVEL=3)) ORDER BY NOMBRE");

            if (VendedoresObj.count==0) {
                msgbox("No está definido ningún supervisor");return;
            }

            extListPassDlg listdlg = new extListPassDlg();
            listdlg.buildDialog(ResCaja.this,"Autorización","Salir");

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
                        msgAskBorrar("Borrar todas las cuentas de la mesa "+mesa);
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
                    if (gl.peCajaPricipal!=gl.codigo_ruta) finish();
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
        super.onResume();

        //iniciaOrdenes();

        try {
            ViewObj.reconnect(Con,db);
            T_ordenObj.reconnect(Con,db);
            T_ordencomboObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (actorden) {
            recibeOrdenes();
        }

        if (browse==1) {
            browse=0;
            if (gl.checksuper) msgAskBorrar("Borrar todas las cuentas de la mesa "+mesa);
            return;
        }
    }

    @Override
    protected void onPause() {
        //cancelaOrdenes();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (idle) {
            super.onBackPressed();
        } else {
            exitflag=true;
        }
    }

    //endregion

}