package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsP_mesa_nombreObj;
import com.dtsgt.classes.clsP_mesero_grupoObj;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_salaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_res_turnoObj;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsT_ordenpendObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.extListChkDlg;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.ladapt.LA_Res_mesa;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.wsCommit;
import com.dtsgt.webservice.wsOpenDT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ResMesero extends PBase {

    private GridView gridView;
    private TextView lblcuenta, lblgrupo,lblmes,lblbarril;
    private ImageView imgwsref,imgnowifi,imgbarril;
    private RelativeLayout relmain;

    private clsP_res_grupoObj P_res_grupoObj;
    private clsP_res_turnoObj P_res_turnoObj;
    private clsP_res_mesaObj P_res_mesaObj;
    private clsP_res_sesionObj P_res_sesionObj;
    private clsT_ordenObj T_ordenObj;
    private clsP_mesero_grupoObj P_mesero_grupoObj;
    private clsP_mesa_nombreObj P_mesa_nombreObj;
    private clsT_ordenpendObj T_ordenpendObj;

    private WebService ws;
    private wsOpenDT wso;
    private wsCommit wscom;

    private Runnable rnBroadcastCallback,rnCorelPutCallback,rnCorelGetCallback,rnOrden,rnLock;

    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();
    private ArrayList<String> corels = new ArrayList<String>();

    private LA_Res_mesa adapter;

    private ArrayList<clsClasses.clsRes_mesa> mesas= new ArrayList<clsClasses.clsRes_mesa>();
    private clsClasses.clsRes_mesa mesa= clsCls.new clsRes_mesa();

    private int idgrupo,cantpers,numorden,codigomesa;
    private String nommes,nmesa,idmesa,corcorel,dbg1,dbg2,idorden;
    private boolean horiz,actorden,wsidle=false,wcoridle=true;

    private TimerTask ptask,etask;
    private int period=15000,delay=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_mesero);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblcuenta =findViewById(R.id.textView179a);
        lblgrupo =findViewById(R.id.textView179b);
        lblmes =findViewById(R.id.textView179b2);
        lblbarril =findViewById(R.id.textView221);
        imgbarril =findViewById(R.id.imageView106);
        imgwsref=findViewById(R.id.imageView120);imgwsref.setVisibility(View.INVISIBLE);
        imgnowifi=findViewById(R.id.imageView71a);
        relmain=findViewById(R.id.relmmain);

        calibraPantalla();

        P_res_grupoObj=new clsP_res_grupoObj(this,Con,db);
        P_res_turnoObj=new clsP_res_turnoObj(this,Con,db);
        P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
        P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);
        P_mesero_grupoObj=new clsP_mesero_grupoObj(this,Con,db);
        P_mesa_nombreObj=new clsP_mesa_nombreObj(this,Con,db);
        T_ordenpendObj=new clsT_ordenpendObj(this,Con,db);

        actorden=gl.peActOrdenMesas;

        setHandlers();
        cargaConfig();
        gl.ventalock=false;

        getURL();

        ws=new WebService(ResMesero.this,gl.wsurl);
        wscom =new wsCommit(gl.wsurl);

        rnCorelPutCallback = new Runnable() {
            public void run() {
                PutCorelCallback();
            }
        };

        wso=new wsOpenDT(gl.wsurl);wsidle=true;

        rnBroadcastCallback = () -> broadcastCallback();

        rnCorelGetCallback = () -> GetCorelCallback();

        rnOrden = new Runnable() {
            public void run() {
                ordenAction();
            }
        };

        rnLock = new Runnable() {
            public void run() {
                ordenAction();
            }
        };


        if (!app.modoSinInternet()) imgnowifi.setVisibility(View.INVISIBLE);

        clsD_barrilObj D_barrilObj=new clsD_barrilObj(this,Con,db);
        D_barrilObj.fill();
        if (D_barrilObj.count==0) {
            lblbarril.setVisibility(View.INVISIBLE);
            imgbarril.setVisibility(View.INVISIBLE);
        }

        imgwsref.setVisibility(View.INVISIBLE);
    }

    //region Events

    public void doGrupo(View view) {
        showGrupoMenu();
    }

    public void doComp(View view) {
        listMesa();
    }

    public void doRec(View view) {
        recibeOrdenes();
    }

    public void doBarril(View view) {
        try {
            startActivity(new Intent(this,Barriles.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doExit(View view) {
        if (gl.meserodir) {
            msgAskExit("Salir");
        } else {
            gl.cerrarmesero=true;finish();
        }
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                mesa = (clsClasses.clsRes_mesa)lvObj;

                if (wsidle) {
                    adapter.setSelectedIndex(position);
                    gl.mesa_codigo=mesa.codigo_mesa;
                    gl.mesa_vend=mesa.cod_vend;
                    abrirOrden();
                } else toast("Actualizando, espere . . .");
            };
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    mesa = (clsClasses.clsRes_mesa)lvObj;

                    adapter.setSelectedIndex(position);
                    opcionesMesa();
                } catch (Exception e) {
                }
                return true;
            }
        });


    }

    //endregion

    //region Main

    private void listItems() {
        showItems();
    }

    private void showItems() {
        clsClasses.clsP_res_sesion last;
        long flim=du.addHours(-12);
        int idmesa;
        String amesa;
        boolean espedido;

        try {
            corels.clear();
            mesas.clear();

            clsP_res_salaObj P_res_salaObj=new clsP_res_salaObj(this,Con,db);

            P_res_mesaObj.fill("WHERE CODIGO_GRUPO="+idgrupo);

            for (int i = 0; i <P_res_mesaObj.count; i++) {

                idmesa=P_res_mesaObj.items.get(i).codigo_mesa;
                espedido=app.esmesapedido(gl.emp,""+P_res_mesaObj.items.get(i).codigo_grupo);

                mesa= clsCls.new clsRes_mesa();

                mesa.codigo_mesa=idmesa;
                mesa.nombre=P_res_mesaObj.items.get(i).nombre;mesa.mesanum=mesa.nombre;
                mesa.alias=" ";mesa.alias2=" ";
                mesa.est_envio=1;
                mesa.numorden="";
                mesa.idgrupo=P_res_mesaObj.items.get(i).codigo_grupo;

                P_res_salaObj.fill("WHERE (CODIGO_SALA="+P_res_mesaObj.items.get(i).codigo_sala+")");
                if (P_res_salaObj.count>0) {
                    mesa.area=P_res_salaObj.first().nombre;
                } else mesa.area="";

                amesa=aliasMesa(idmesa);
                if (!amesa.isEmpty()) {
                    mesa.alias=mesa.nombre+" - "+amesa;mesa.nombre=" ";
                    mesa.alias2=amesa;
                }

                P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+") AND (FECHAINI>="+flim+")");

                if (P_res_sesionObj.count>0) {

                    last=P_res_sesionObj.items.get(P_res_sesionObj.count-1);

                    corels.add(last.id);

                    mesa.estado=last.estado;
                    mesa.pers=last.cantp;
                    mesa.cuentas=last.cantc;
                    mesa.fecha=last.fechault;
                    mesa.idorden=last.id;
                    mesa.cod_vend=last.vendedor;

                    if (espedido) mesa.numorden="#"+last.cantc;

                    T_ordenObj.fill("WHERE (COREL='"+last.id+"') AND (ESTADO=1)");
                    mesa.pendiente=T_ordenObj.count;

                    T_ordenpendObj.fill("WHERE GODIGO_ORDEN='"+last.id+"'");
                    if (T_ordenpendObj.count>0) mesa.est_envio=0;

                } else {
                    mesa.estado=0;
                    mesa.pers=0;
                    mesa.cuentas=0;
                    mesa.fecha=0;
                    mesa.pendiente=0;
                    mesa.cod_vend=gl.idmesero;
                }

                mesas.add(mesa);
            }

            adapter=new LA_Res_mesa(this,this,mesas,horiz);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void abrirOrden() {
        try {

            gl.mesa_grupo=idgrupo;
            gl.mesa_alias=aliasMesa(mesa.codigo_mesa);
            gl.mesanom=mesa.mesanum;
            codigomesa=mesa.codigo_mesa;
            gl.mesacodigo=codigomesa;
            gl.mesa_area=mesa.area;

            P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+")");
            if (P_res_sesionObj.count>0) {
                //gl.idorden=P_res_sesionObj.first().id;
                try {
                    gl.idorden=mesa.idorden;
                    if (!gl.idorden.isEmpty()) {
                        startActivity(new Intent(this,Orden.class));
                    } else {
                        throw new Exception();
                    }
                } catch (Exception ed) {
                    try {
                        P_res_sesionObj.first().estado=-1;
                        P_res_sesionObj.update(P_res_sesionObj.first());
                        browse = 1;
                        gl.idorden = "";
                        startActivity(new Intent(this, Comensales.class));
                    } catch (Exception ee) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
                    }
                }
            } else {
                //inputPersonas();
                browse=1;
                gl.idorden="";
                startActivity(new Intent(this,Comensales.class));
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addOrden() {

        try {

            db.beginTransaction();

            clsClasses.clsP_res_sesion item = clsCls.new clsP_res_sesion();

            item.id=gl.codigo_ruta+"_"+mu.getCorelBase();
            item.codigo_mesa=codigomesa;
            item.vendedor=gl.idmesero;
            item.estado=1;
            item.cantp=cantpers;
            item.cantc=numorden;
            item.fechaini=du.getActDateTime();
            item.fechafin=0;
            item.fechault=du.getActDateTime();

            P_res_sesionObj.add(item);

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            cuenta.corel=item.id;
            cuenta.id=1;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            T_ordencuentaObj.add(cuenta);

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.idorden=item.id;idorden=item.id;

            if (actorden) {
                envioOrden();
            } else {
                startActivity(new Intent(this,Orden.class));
            }
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void listMesa() {
        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResMesero.this,"Mesas");

            for (int i = 0; i <mesas.size(); i++) {
                if (mesas.get(i).estado==0) {
                    listdlg.add(""+mesas.get(i).codigo_mesa,mesas.get(i).nombre);
                }
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    idmesa=listdlg.items.get(position).codigo;
                    nmesa=listdlg.items.get(position).text;
                    listComp();
                    listdlg.dismiss();
                };
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listComp() {

        /*
        try {
            lcode.clear();lname.clear();

            P_res_sesionObj.fill("WHERE (CODIGO_MESA="+idmesa+") AND (Estado=-1) AND (FECHAULT>="+du.ffecha00(du.getActDate())+")");

            if (P_res_sesionObj.count==0) {
                msgbox("No existe ninguna cuenta completa para la mesa");return;
            }

            for (int i = 0; i <P_res_sesionObj.count; i++) {
                lcode.add(P_res_sesionObj.items.get(i).id);
                lname.add("Mesa : "+nmesa+"  "+du.shora(P_res_sesionObj.items.get(i).fechault));
            }

            final String[] xselitems = new String[lname.size()];

            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            ExDialog mMenuDlg = new ExDialog(this);

            mMenuDlg.setTitle("Activar cuenta completa");

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        msgAskActivar("Activar la cuenta",lcode.get(item));
                     } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

            AlertDialog Dialog = mMenuDlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

         */
    }

    //endregion

    //region Envio

    private void envioOrden() {
        String cmd="";

        try {
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

            cmd+=buildDetailJournal();

            try {
                enviaCommit(cmd);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void enviaCommit(String cmd) {
        if (!actorden) return;
        wscom.execute(cmd,rnOrden);
    }

    private void ordenAction() {
        try {
            if (wscom.errflag) {
                toastlong("SIN CONEXIÓN A INTERNET");
            }

            startActivity(new Intent(this,Orden.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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
            clsClasses.clsP_res_sesion rsitem=P_res_sesionObj.first();

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

    //endregion

    //region Broadcast

    private void broadcastCallback() {
        wsidle=true;
        if (wso.errflag) {
            msgBoxWifi("No hay conexíon al internet");
            relmain.setBackgroundColor(Color.parseColor("#F4C6D0"));
            //toastlong("wsCallBack "+wso.error);
        } else {
            procesaOrdenes();
        }

        cierraPantalla();
    }

    private void iniciaOrdenes() {
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(ptask=new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public synchronized void run() {
                            //recibeOrdenes();
                        }
                    });
                }
            }, delay, period);
        } catch (Exception e) { }
    }

    private void cancelaOrdenes() {
        try {
            ptask.cancel();
        } catch (Exception e) {}
    }

    private void recibeOrdenes() {
        if (!wsidle) return;
        if (!wcoridle) return;

        try {
            wsidle=false;
            imgwsref.setVisibility(View.VISIBLE);

            sql="SELECT  CODIGO, COREL_ORDEN, COMANDA, COREL_LINEA " +
                "FROM T_ORDENCOM WHERE (CODIGO_RUTA="+gl.codigo_ruta+") AND " +
                "(COREL_LINEA IN (1,3,99,100)) ORDER BY COREL_ORDEN,CODIGO";
            wso.execute(sql,rnBroadcastCallback);

        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wsidle=true;
            imgwsref.setVisibility(View.INVISIBLE);
        }
    }

    private void procesaOrdenes() {
        int iid,trtipo;
        String cor,cmd,del="",ins="";

        try {
            if (wso.openDTCursor.getCount()==0) {
                imgwsref.setVisibility(View.INVISIBLE);wsidle=true;
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
                            } catch (SQLException e) { }
                            break;
                        case 100:
                            aplicaNombreMesa(cor,sql);break;
                        default:
                            db.execSQL(ins);break;
                    }

                    db.setTransactionSuccessful();
                    db.endTransaction();

                    cmd += "DELETE FROM T_ORDENCOM WHERE CODIGO=" + iid + ";";
                } catch (Exception e) {
                    db.endTransaction();
                    imgwsref.setVisibility(View.INVISIBLE);wsidle=true;
                    //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage() + "\n" + del + "\n" + ins);
                    return;
                }

                wso.openDTCursor.moveToNext();
            }

            if (!cmd.isEmpty()) confirmaOrdenes(cmd);
        } catch (Exception e) {
            //msgbox(new Object() { }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

        imgwsref.setVisibility(View.INVISIBLE);wsidle=true;
        listItems();
    }

    private void confirmaOrdenes(String cmd) {
        try {
            Intent intent = new Intent(ResMesero.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",cmd);
            startService(intent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicaNombreMesa(String idmesa,String nmesa) {
        try {

            clsClasses.clsP_mesa_nombre nitem = clsCls.new clsP_mesa_nombre();
            nitem.codigo_mesa=Integer.parseInt(idmesa);
            nitem.nombre=nmesa;

            if (nmesa.isEmpty()) {
                P_mesa_nombreObj.delete(nitem);
            } else {
                try {
                    P_mesa_nombreObj.add(nitem);
                } catch (Exception e) {
                    P_mesa_nombreObj.update(nitem);
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }


    }

    //endregion

    //region Corel global

    private void agregaOrden() {

        numorden=0;
        boolean espedido=app.esmesapedido(gl.emp,""+mesa.idgrupo);

        if (!espedido) {
            addOrden();return;
        }

        if(!wsidle) {
            msgbox("Agregar orden: No se puede crear orden, por favor intente de nuevo ");return;
        }

        corcorel=gl.codigo_ruta+"_"+mu.getCorelBase();

        try{

            wcoridle=false;

            sql="INSERT INTO D_ORDEN_COREL (CODIGO_SUCURSAL,COREL,ID) " +
                "SELECT "+gl.tienda+",ISNULL(MAX(COREL)+1,1),'"+corcorel+"' " +
                "FROM D_ORDEN_COREL WHERE CODIGO_SUCURSAL="+gl.tienda;dbg1=sql;
            wscom.execute(sql,rnCorelPutCallback);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wcoridle=true;
        }
    }

    private void PutCorelCallback() {
        try {
            if (wso.errflag) {
                toastlong("1. No se pudo asignar numero de orden\n"+wso.error);
                addOrden();wcoridle=true;return;
            }

            sql="SELECT MAX(COREL) FROM D_ORDEN_COREL WHERE ID='"+corcorel+"'";dbg2=sql;
            wso.execute(sql,rnCorelGetCallback);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wcoridle=true;
        }
    }

    private void GetCorelCallback() {
        String sse="/";

        try {
            if (wso.errflag) {
                toastlong("2. No se pudo asignar numero de orden\n"+wso.error);
                addOrden();wcoridle=true;return;
            }

            if (wso.openDTCursor.getCount() == 0) {
                toastlong("3. No se pudo asignar numero de orden");
                addOrden();wcoridle=true;return;
            }

            try {
                wso.openDTCursor.moveToFirst();
                sse=">"+wso.openDTCursor.getString(0)+"<";
                numorden = wso.openDTCursor.getInt(0);
            } catch (Exception e) {
                msgbox("No se pudo asignar número de orden \n"+e.getMessage()+" - "+sse);
                String d1=dbg1;
                String d2=dbg2;
                numorden=0;
            }

            numorden=numorden % 1000;

            addOrden();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

        wcoridle=true;
    }

    //endregion

    //region Estado

    private void iniciaEstados() {
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(etask=new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public synchronized void run() {
                            procesaEstadoMesas();
                        }
                    });
                }
            }, delay, period);
        } catch (Exception e) { }
    }

    private void cancelaEstados() {
        try {
            etask.cancel();
        } catch (Exception e) {}
    }

    @Override
    protected void wsCallBack(Boolean throwing,String errmsg) {
        imgwsref.setVisibility(View.INVISIBLE);
        try {
            super.wsCallBack(throwing, errmsg);
            aplicaEstados();
        } catch (Exception e) {
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void procesaEstadoMesas() {

        String ss=" IN (";

        imgwsref.setVisibility(View.INVISIBLE);

        try {
            if (corels.size()==0) return;

            imgwsref.setVisibility(View.VISIBLE);

            for (int i = 0; i <corels.size(); i++) {
                ss+="'"+corels.get(i)+"'";
                if (i<corels.size()-1) ss+=",";else ss+=")";
            }

            sql="SELECT ID,COREL,ESTADO FROM T_ORDEN  WHERE COREL "+ss;
            ws.openDT(sql);

        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicaEstados() {

        int iid,est;
        String cor;

        try {

            if (ws.openDTCursor.getCount()==0) return;

            db.beginTransaction();

            ws.openDTCursor.moveToFirst();

            while (!ws.openDTCursor.isAfterLast()) {

                iid=ws.openDTCursor.getInt(0);
                cor=ws.openDTCursor.getString(1);
                est=ws.openDTCursor.getInt(2);

                if (est<0 | est>1) {
                    sql = "UPDATE T_ORDEN SET ESTADO=" + est + " WHERE (COREL='" + cor + "') AND (EMPRESA=" + iid + ")";
                    db.execSQL(sql);
                }

                validaCompleto(cor);

                ws.openDTCursor.moveToNext();
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void validaCompleto(String idorden) {

        try {

            clsT_ordenObj T_ordenObj=new clsT_ordenObj(this,Con,db);

            T_ordenObj.fill("WHERE (COREL='"+idorden+"')");
            int ti=T_ordenObj.count;

            T_ordenObj.fill("WHERE (COREL='"+idorden+"') AND (ESTADO=2)");
            int tic=T_ordenObj.count;

            if (ti>0) {
                if (ti==tic) {
                    cerrarCuentas(idorden);
                }
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cerrarCuentas(String idorden) {

        try {

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            clsClasses.clsP_res_sesion sess=P_res_sesionObj.first();

            sess.estado=-1;
            sess.fechault=du.getActDateTime();
            P_res_sesionObj.update(sess);

            envioMesa(idorden);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void envioMesa(String orden) {

        estadoMesa(orden,-1);
        if (!gl.pelMeseroCaja) return;

        if (orden.isEmpty()) {
            msgbox("envioMesa : idorden vacio");
        }

        String cmd = "UPDATE P_res_sesion SET Estado=-1 WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + orden + "')" + ";";

        Intent intent = new Intent(ResMesero.this, srvOrdenEnvio.class);
        intent.putExtra("URL",gl.wsurl);
        intent.putExtra("command",cmd);
        startService(intent);
    }

    private void estadoMesa(String idorden,int est) {

        try {

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+idorden+"'");
            P_res_sesionObj.first().estado=est;
            P_res_sesionObj.update(P_res_sesionObj.first());

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void actualizaEstadosOrdenes() {

        try {

            if (corels.size()==0) return;

            for (int i = 0; i <corels.size(); i++) {
                validaCompleto(corels.get(i));
            }

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void cargaConfig() {

        gl.salaid=0;

        try {

            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE codigo_vendedor="+gl.idmesero);

            nommes=VendedoresObj.first().nombre;
            lblmes.setText(nommes);

            P_res_turnoObj.fill("WHERE vendedor="+gl.idmesero);

            if (P_res_turnoObj.count>0) {
                idgrupo=P_res_turnoObj.items.get(P_res_turnoObj.count-1).codigo_grupo;
            } else {
                idgrupo=0;
                showGrupoMenu();
            }

            setGrupo();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void setGrupo() {

        clsClasses.clsP_res_turno item=clsCls.new clsP_res_turno();

        try {
            try {
                item.codigo_grupo=idgrupo;
                item.vendedor=gl.idmesero;
                item.fecha=du.getActDate();
                P_res_turnoObj.add(item);
            } catch (Exception e) {
                P_res_turnoObj.update(item);
            }
        } catch (Exception e) {}


        try {
            if (idgrupo>0) {
                P_res_grupoObj.fill("WHERE CODIGO_GRUPO="+idgrupo);
                lblgrupo.setText(P_res_grupoObj.items.get(P_res_grupoObj.count-1).nombre);
            } else lblgrupo.setText("Seleccione una area");

            listItems();
        } catch (Exception e) {}

    }

    private void calibraPantalla() {

        if (pantallaHorizontal()) horiz=true; else horiz=false;

        if (horiz) {
            lblmes.setTextSize(36);lblgrupo.setTextSize(36);
            gridView.setNumColumns(4);
        } else {
            lblmes.setTextSize(20);lblgrupo.setTextSize(20);
            gridView.setNumColumns(2);
        }
    }

    private void cantidadComensales() {
        try {
            cantpers=gl.comensales;
            if (cantpers<1 | cantpers>50) throw new Exception();
            agregaOrden();
        } catch (Exception e) {
            //mu.msgbox("Cantidad incorrecta");
            return;
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

        } catch (Exception e) {
            Log.d("Error: ", e.getMessage());
        }

    }

    private String aliasMesa(int idm) {


        try {
            P_mesa_nombreObj.fill("WHERE CODIGO_MESA="+idm);
            if (P_mesa_nombreObj.count>0) {
                return P_mesa_nombreObj.first().nombre;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    private void borraNombreMesa() {
        clsClasses.clsP_mesa_nombre nitem;

        try {
            P_mesa_nombreObj.fill("WHERE CODIGO_MESA="+mesa.codigo_mesa);
            nitem=P_mesa_nombreObj.first();
            P_mesa_nombreObj.delete(nitem);

            broadcastJournalNameFlag(mesa.codigo_mesa,"");
            listItems();
        } catch (Exception e) {
        }
    }

    private void broadcastJournalNameFlag(int idmesa,String nmesa) {
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
                    pitem.corel_orden=""+idmesa;
                    pitem.corel_linea=100;
                    pitem.comanda=nmesa;

                    cmd+=addItemSqlOrdenCom(pitem) + ";";
                }

            }

            try {
                Intent intent = new Intent(ResMesero.this, srvCommit.class);
                intent.putExtra("URL",gl.wsurl);
                intent.putExtra("command",cmd);
                startService(intent);
            } catch (Exception e) {
                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                app.addToOrdenLog(du.getActDateTime(),"Orden."+new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),cmd);
            }

        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    private void showGrupoMenu() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResMesero.this,"Grupos de mesas","Salir","Configurar");

            sql="WHERE (CODIGO_GRUPO IN (SELECT CODIGO_GRUPO FROM P_mesero_grupo " +
                    "WHERE (CODIGO_MESERO="+gl.idmesero+")) ) ORDER BY Nombre";
            P_res_grupoObj.fill(sql);

            if (P_res_grupoObj.count==0) {
                showGrupoChecks();
                /*
                browse=2;
                startActivity(new Intent(ResMesero.this,ValidaSuper.class));
                return;
                */
            }

            for (int i = 0; i <P_res_grupoObj.count; i++) {
                listdlg.add(P_res_grupoObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idgrupo=P_res_grupoObj.items.get(position).codigo_grupo;
                        setGrupo();
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(v -> listdlg.dismiss());

            listdlg.setOnMiddleClick(v -> {
                showGrupoChecks();
                /*
                browse=2;
                startActivity(new Intent(ResMesero.this,ValidaSuper.class));
                                 */
                listdlg.dismiss();
            });

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void showGrupoChecks() {

        try {

            extListChkDlg listdlg = new extListChkDlg();
            listdlg.buildDialog(ResMesero.this,"Grupos asignados","Salir","","Aplicar");
            listdlg.setWidth(350);listdlg.setLines(5);

            P_res_grupoObj.fill("ORDER BY Nombre");
            for (int i = 0; i <P_res_grupoObj.count; i++) {
                P_mesero_grupoObj.fill("WHERE (CODIGO_MESERO="+gl.idmesero+") AND (CODIGO_GRUPO="+P_res_grupoObj.items.get(i).codigo_grupo+")");
                listdlg.add(P_res_grupoObj.items.get(i).nombre,P_mesero_grupoObj.count>0);
            }

            listdlg.setOnExitListener(v -> listdlg.dismiss());

            listdlg.setOnAddListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clsClasses.clsP_mesero_grupo item;

                    try {

                        db.beginTransaction();
                        db.execSQL("DELETE FROM P_mesero_grupo WHERE (CODIGO_MESERO="+gl.idmesero+")");

                        for (int i = 0; i <P_res_grupoObj.count; i++) {

                            if (listdlg.items.get(i).checked) {

                                item = clsCls.new clsP_mesero_grupo();
                                item.codigo_mesero=gl.idmesero;
                                item.codigo_grupo=P_res_grupoObj.items.get(i).codigo_grupo;
                                P_mesero_grupoObj.add(item);
                            }
                        }

                        db.setTransactionSuccessful();
                        db.endTransaction();

                        listdlg.dismiss();

                        db.execSQL("DELETE FROM P_res_turno WHERE vendedor="+gl.idmesero);

                        gl.exitflag=true;
                        gl.cliposflag=false;

                        finish();

                    } catch (Exception e) {
                        db.endTransaction();
                        msgbox(e.getMessage());
                    }
                }
            });

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void showQuickRecep() {
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
                    startActivity(new Intent(ResMesero.this, WSRec.class));
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
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                   app.logoutUser(du.getActDateTime());
                   finish();
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

    private void msgAskActivar(String msg,String id) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        db.execSQL("UPDATE P_RES_SESION SET ESTADO=1 WHERE ID='"+id+"'");
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
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void opcionesMesa() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResMesero.this,"Nombre de mesa");

            listdlg.add(R.drawable.btn_complete,"Cambiar nombre");
            listdlg.add(R.drawable.btn_del_line,"Borrar nombre");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                ingresaNombreMesa();break;
                            case 1:
                                borraNombreMesa();break;
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

    private void ingresaNombreMesa() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nombre mesa");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setText(mesa.alias2);
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                clsClasses.clsP_mesa_nombre nitem;
                String s=input.getText().toString();

                if (s.isEmpty()) {
                    mu.msgbox("Nombre incorrecto");return;
                }

                s=mu.Capitalize(s);
                nitem = clsCls.new clsP_mesa_nombre();
                try {
                    nitem.codigo_mesa=mesa.codigo_mesa;
                    nitem.nombre=s;

                    P_mesa_nombreObj.add(nitem);
                } catch (Exception e) {
                    P_mesa_nombreObj.update(nitem);
                }

                broadcastJournalNameFlag(mesa.codigo_mesa,s);
                listItems();
                return;
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void msgBoxWifi(String msg) {
        try{
            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
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

        try {
            P_res_grupoObj.reconnect(Con,db);
            P_res_turnoObj.reconnect(Con,db);
            P_res_mesaObj.reconnect(Con,db);
            P_res_sesionObj.reconnect(Con,db);
            T_ordenObj.reconnect(Con,db);
            P_mesero_grupoObj.reconnect(Con,db);
            P_mesa_nombreObj.reconnect(Con,db);
            T_ordenpendObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (actorden) {
            recibeOrdenes();
            //iniciaOrdenes();
        }

        if (browse==1) {
            browse=0;
            cantidadComensales();
        }

        if (browse==2) {
            browse=0;
            if (gl.checksuper) showGrupoChecks();
            return;
        }

        if (gl.cerrarmesero) {
            if (!gl.peNoCerrarMesas) finish();
        } else {
            listItems();

            if (gl.pelMeseroCaja) {
                //procesaEstadoMesas();
                iniciaEstados();
            } else {
                //actualizaEstadosOrdenes();
            }
            actualizaEstadosOrdenes();
        }

    }

    @Override
    protected void onPause() {
        if (actorden) cancelaOrdenes();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try{
            //msgAskExit("Salir");
            if (actorden) cancelaOrdenes();
            cancelaEstados();
            app.logoutUser(du.getActDateTime());
            finish();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }
    //endregion

}