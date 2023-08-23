package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
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
import com.dtsgt.classes.extListPassDlg;
import com.dtsgt.firebase.fbMesaAbierta;
import com.dtsgt.firebase.fbOrdenCuenta;
import com.dtsgt.firebase.fbResSesion;
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

public class ResMesero extends PBase {

    private GridView gridView;
    private TextView lblcuenta, lblgrupo,lblmes,lblbarril;
    private ImageView imgnowifi,imgbarril;

    private fbResSesion fbrs;
    private fbMesaAbierta fbma;
    private fbOrdenCuenta fboc;
    private Runnable rnFbResSesionList,rnFbMesaAbierta,rnFbMesaComensales,
            rnFbListenerResSesion, rnfbrsMesaActiva;

    private clsP_res_grupoObj P_res_grupoObj;
    private clsP_res_turnoObj P_res_turnoObj;
    private clsP_res_mesaObj P_res_mesaObj;
    private clsP_res_sesionObj P_res_sesionObj;
    private clsT_ordenObj T_ordenObj;
    private clsP_mesero_grupoObj P_mesero_grupoObj;
    private clsP_mesa_nombreObj P_mesa_nombreObj;
    private clsT_ordenpendObj T_ordenpendObj;
    private clsVendedoresObj VendedoresObj;

    private wsOpenDT wso;
    private wsCommit wscom;

    private Runnable rnCorelPutCallback,rnCorelGetCallback;

    private ArrayList<String> corels = new ArrayList<String>();

    private LA_Res_mesa adapter;

    private ArrayList<clsClasses.clsRes_mesa> mesas= new ArrayList<clsClasses.clsRes_mesa>();
    private clsClasses.clsRes_mesa mesa= clsCls.new clsRes_mesa();
    private clsClasses.clsfbResSesion rsesion = clsCls.new clsfbResSesion();

    private int idgrupo,cantpers,numorden,codigomesa;
    private String nommes,nmesa,idmesa,corcorel,dbg1,dbg2,idorden,nmesabrio;
    private boolean horiz,actorden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_res_mesero);

            super.InitBase();

            gridView = findViewById(R.id.gridView1);
            lblcuenta =findViewById(R.id.textView179a);
            lblgrupo =findViewById(R.id.textView179b);
            lblmes =findViewById(R.id.textView179b2);
            lblbarril =findViewById(R.id.textView221);
            imgbarril =findViewById(R.id.imageView106);
            imgnowifi=findViewById(R.id.imageView71a);

            calibraPantalla();

            P_res_grupoObj=new clsP_res_grupoObj(this,Con,db);
            P_res_turnoObj=new clsP_res_turnoObj(this,Con,db);
            P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
            P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            T_ordenObj=new clsT_ordenObj(this,Con,db);
            P_mesero_grupoObj=new clsP_mesero_grupoObj(this,Con,db);
            P_mesa_nombreObj=new clsP_mesa_nombreObj(this,Con,db);
            T_ordenpendObj=new clsT_ordenpendObj(this,Con,db);
            VendedoresObj=new clsVendedoresObj(this,Con,db);

            actorden=gl.peActOrdenMesas;

            //region Firebase

            fbrs=new fbResSesion("ResSesion",gl.tienda);
            fbma=new fbMesaAbierta("MesaAbierta",gl.tienda);
            fboc=new fbOrdenCuenta("OrdenCuenta",gl.tienda);

            rnFbResSesionList = () -> { FbResSesionList();};
            rnFbListenerResSesion = () -> { FbListenerResSesion();};
            rnFbMesaAbierta = () -> {FbMesaAbierta();};
            rnFbMesaComensales = () -> {FbMesaComensales();};
            rnfbrsMesaActiva = () -> {abrirOrdenMesa();};

            fbrs.setListener(rnFbListenerResSesion);

            //endregion

            setHandlers();
            cargaConfig();
            gl.ventalock=false;

            getURL();

            wscom =new wsCommit(gl.wsurl);
            wso=new wsOpenDT(gl.wsurl);

            rnCorelPutCallback = () -> PutCorelCallback();
            rnCorelGetCallback = () -> GetCorelCallback();

            if (!app.modoSinInternet()) imgnowifi.setVisibility(View.INVISIBLE);

            clsD_barrilObj D_barrilObj=new clsD_barrilObj(this,Con,db);
            D_barrilObj.fill();
            if (D_barrilObj.count==0) {
                lblbarril.setVisibility(View.INVISIBLE);
                imgbarril.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doGrupo(View view) {
        showGrupoMenu();
    }

    public void doComp(View view) {
        listMesa();
    }

    public void doRec(View view) {

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

                adapter.setSelectedIndex(position);
                gl.mesa_codigo=mesa.codigo_mesa;
                gl.mesa_vend=mesa.cod_vend;
                gl.mesero_venta=mesa.cod_vend;

                if (gl.peMesaAtenderTodos) {
                    abrirOrden();
                } else {
                    if (gl.idmesero==gl.mesa_vend) {
                        abrirOrden();
                    } else {
                        try {
                            VendedoresObj.fill("WHERE codigo_vendedor="+gl.mesa_vend);
                            nmesabrio=VendedoresObj.first().nombre;
                            msgmsg("La cuenta fue creada por "+nmesabrio+".\n No la puede ser atendida otro mesero.");
                        } catch (Exception e) {
                            abrirOrden();
                            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }
                    }
                }
            };
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    mesa = (clsClasses.clsRes_mesa)lvObj;

                    adapter.setSelectedIndex(position);
                    //opcionesMesa();
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

                mesa.estado=0;
                mesa.pers=0;
                mesa.cuentas=0;
                mesa.fecha=0;
                mesa.pendiente=0;
                mesa.cod_vend=gl.idmesero;

                mesas.add(mesa);
            }

            //adapter=new LA_Res_mesa(this,this,mesas,horiz);
            //gridView.setAdapter(adapter);

            fbrs.listItemsActivos(rnFbResSesionList);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void FbResSesionList() {
        try {
            //mesaActiva

            for (int i = 0; i <mesas.size(); i++) {
                mesa=mesas.get(i);

                if (mesaActiva(mesa.codigo_mesa)) {

                    mesa.estado=rsesion.estado;
                    mesa.pers=rsesion.cantp;
                    mesa.cuentas=rsesion.cantc;
                    mesa.fecha=rsesion.fechault;
                    mesa.idorden=rsesion.id;
                    mesa.cod_vend=rsesion.vendedor;

                    T_ordenObj.fill("WHERE (COREL='"+rsesion.id+"') AND (ESTADO=1)");
                    mesa.pendiente=T_ordenObj.count;

                    T_ordenpendObj.fill("WHERE GODIGO_ORDEN='"+rsesion.id+"'");
                    if (T_ordenpendObj.count>0) mesa.est_envio=0;

                }
            }

            adapter=new LA_Res_mesa(this,this,mesas,horiz);
            gridView.setAdapter(adapter);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void abrirOrden() {
        try {
            fbrs.listItemsActivos(rnfbrsMesaActiva);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void abrirOrdenMesa() {
        boolean activa=false;

        try {

            gl.mesa_grupo=idgrupo;
            gl.mesa_alias=aliasMesa(mesa.codigo_mesa);
            gl.mesanom=mesa.mesanum;
            codigomesa=mesa.codigo_mesa;
            gl.mesacodigo=codigomesa;
            gl.mesa_area=mesa.area;

            for (int i = 0; i <fbrs.items.size(); i++) {
                if (fbrs.items.get(i).codigo_mesa==mesa.codigo_mesa) {
                    gl.idorden=mesa.idorden;

                    activa=true;break;
                }
            }

            if (activa) {
                 try {
                    gl.idorden=mesa.idorden;
                    if (!gl.idorden.isEmpty()) {
                        validaMesaAbierta();
                    } else {
                        throw new Exception();
                    }
                } catch (Exception ed) {
                    try {
                        browse = 1;
                        gl.idorden = "";
                        validaMesaComensales();
                    } catch (Exception ee) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
                    }
                }
            } else {
                browse=1;
                gl.idorden="";
                validaMesaComensales();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addOrden() {

        try {

            clsClasses.clsfbResSesion item = clsCls.new clsfbResSesion();

            item.id=gl.codigo_ruta+"_"+mu.getCorelBase();
            item.codigo_mesa=codigomesa;
            item.vendedor=gl.idmesero;
            item.estado=1;
            item.cantp=cantpers;
            item.cantc=numorden;
            item.fechaini=du.getActDateTime();
            item.fechafin=0;
            item.fechault=du.getActDateTime();

            fbrs.setItem(item);

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            cuenta.corel=item.id;
            cuenta.id=1;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            fboc.setItem(cuenta);
            T_ordencuentaObj.add(cuenta);

            gl.idorden=item.id;idorden=item.id;
            //if (actorden) envioOrden(); else validaMesaAbierta();
            validaMesaAbierta();
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
                   listdlg.dismiss();
                };
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Bloqueo de mesa

    private void validaMesaAbierta() {
        try {
            if (wscom.errflag) toast("SIN CONEXIÓN A INTERNET");
            fbma.getItem(gl.mesacodigo,rnFbMesaAbierta);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void validaMesaComensales() {
        try {
            if (wscom.errflag) toast("SIN CONEXIÓN A INTERNET");
            fbma.getItem(gl.mesacodigo,rnFbMesaComensales);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void FbMesaAbierta() {

        try {
            if (fbma.errflag) {
                msgbox("FbMesaAbierta . "+fbma.error);return;
            }

            if (fbma.itemexists) {
                if (fbma.item.estado==1) {
                    String sm="La mesa está ocupada en\nel dispositivo: "+fbma.item.caja+"\n"+
                           "Inicio: "+du.shora(fbma.item.fecha)+"\nMesero: "+fbma.item.mesero;
                    msgAskMesa(sm);return;
                }
            }

            startActivity(new Intent(this,Orden.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void FbMesaComensales() {
        try {
            if (fbma.errflag) {
                msgbox("FbMesaComensales . "+fbma.error);return;
            }

            if (fbma.itemexists) {
                if (fbma.item.estado==1) {
                    String sm="La mesa está abierta :\n"+
                           fbma.item.caja+" a las "+du.shora(fbma.item.fecha)+" "+"\n"+fbma.item.mesero;
                    msgAskMesa(sm);return;
                }
            }

            startActivity(new Intent(this,Comensales.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void desbloquoMesa() {
        try {
            clsClasses.clsfbMesaAbierta item = clsCls.new clsfbMesaAbierta();

            item.codigo_mesa=gl.mesacodigo;
            item.estado=0;
            item.mesero=" ";
            item.caja=gl.rutanom;
            item.fecha=du.getActDateTime();

            fbma.setItem(item.codigo_mesa, item);

            msgdebloq("Mesa desbloqueada.\nAsegure se que ninguno otro\nusuario no la tiene abierta.");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Listener

    private void FbListenerResSesion() {
        try {
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void registerListener() {
        try {
            fbrs.refResSesion.addValueEventListener(fbrs.listResSesion);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void unregisterListener() {
        try {
            fbrs.refResSesion.removeEventListener(fbrs.listResSesion);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Corel global

    private void agregaOrden() {

        numorden=0;
        corcorel=gl.codigo_ruta+"_"+mu.getCorelBase();

        try {
            sql="INSERT INTO D_ORDEN_COREL (CODIGO_SUCURSAL,COREL,ID) " +
                    "SELECT "+gl.tienda+",ISNULL(MAX(COREL)+1,1),'"+corcorel+"' " +
                    "FROM D_ORDEN_COREL WHERE CODIGO_SUCURSAL="+gl.tienda;dbg1=sql;
            wscom.execute(sql,rnCorelPutCallback);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void PutCorelCallback() {
        try {
            if (wso.errflag) {
                toastlong("1. No se pudo asignar numero de orden\n"+wso.error);
                addOrden();return;
            }

            sql="SELECT MAX(COREL) FROM D_ORDEN_COREL WHERE ID='"+corcorel+"'";dbg2=sql;
            wso.execute(sql,rnCorelGetCallback);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
       }
    }

    private void GetCorelCallback() {
        String sse="/";

        try {
            if (wso.errflag) {
                toastlong("2. No se pudo asignar numero de orden\n"+wso.error);
                addOrden();return;
            }

            if (wso.openDTCursor.getCount() == 0) {
                toastlong("3. No se pudo asignar numero de orden");
                addOrden();return;
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
    }

    //endregion

    //region Estado

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

    //endregion

    //region Aux

    private boolean mesaActiva(int cmesa) {
        try {
            for (int ir = 0; ir <fbrs.items.size(); ir++) {
                rsesion=fbrs.items.get(ir);
                if (rsesion.codigo_mesa==cmesa) {
                    if (rsesion.estado>0) return true;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return false;
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

    private void cargaConfig() {

        gl.salaid=0;

        try {

            VendedoresObj.fill("WHERE codigo_vendedor="+gl.idmesero);

            nommes=VendedoresObj.first().nombre;
            lblmes.setText(nommes+" [ "+gl.rutanom+" ]");

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

            listItems();
        } catch (Exception e) {
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
                                //ingresaNombreMesa();break;
                            case 1:
                                //borraNombreMesa();break;
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

    private void msgAskMesa(String msg) {
        try {

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    validaSupervisor();
                }
            });

            dialog.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgdebloq(String msg) {
        try {

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void validaSupervisor() {

        clsClasses.clsVendedores item;

        try {
            app.fillSuper(VendedoresObj);

            if (VendedoresObj.count==0) {
                msgbox("No está definido ningún supervisor");return;
            }

            extListPassDlg listdlg = new extListPassDlg();
            listdlg.buildDialog(ResMesero.this,"Autorización","Salir");

            for (int i = 0; i <VendedoresObj.count; i++) {
                item=VendedoresObj.items.get(i);
                listdlg.addpassword(item.codigo_vendedor,item.nombre,item.clave);
            }

            listdlg.setOnLeftClick(v -> listdlg.dismiss());

            listdlg.onEnterClick(v -> {

                if (listdlg.getInput().isEmpty()) return;

                if (listdlg.validPassword()) {
                    desbloquoMesa();
                    listdlg.dismiss();
                } else {
                    toast("Contraseña incorrecta");
                }
            });

            listdlg.setWidth(350);
            listdlg.setLines(4);

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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
            VendedoresObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        registerListener();

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
            if (!gl.peNoCerrarMesas) {
                finish();
            }
        } else {
            listItems();
        }

    }

    @Override
    protected void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        try{
            app.logoutUser(du.getActDateTime());
            finish();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }
    //endregion

}