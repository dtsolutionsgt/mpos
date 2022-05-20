package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_mesa_nombreObj;
import com.dtsgt.classes.clsP_mesero_grupoObj;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_res_turnoObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Res_mesa;
import com.dtsgt.webservice.srvOrdenEnvio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResMesero extends PBase {

    private GridView gridView;
    private TextView lblcuenta, lblgrupo,lblmes;
    private ImageView imgwsref,imgnowifi;

    private clsP_res_grupoObj P_res_grupoObj;
    private clsP_res_turnoObj P_res_turnoObj;
    private clsP_res_mesaObj P_res_mesaObj;
    private clsP_res_sesionObj P_res_sesionObj;
    private clsT_ordenObj T_ordenObj;
    private clsP_mesero_grupoObj P_mesero_grupoObj;
    private clsP_mesa_nombreObj P_mesa_nombreObj;

    private WebService ws;

    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();
    private ArrayList<String> corels = new ArrayList<String>();

    private LA_Res_mesa adapter;

    private ArrayList<clsClasses.clsRes_mesa> mesas= new ArrayList<clsClasses.clsRes_mesa>();
    private clsClasses.clsRes_mesa mesa= clsCls.new clsRes_mesa();

    private int idgrupo,cantpers;
    private String nommes,nmesa,idmesa;
    private boolean horiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_mesero);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblcuenta =findViewById(R.id.textView179a);
        lblgrupo =findViewById(R.id.textView179b);
        lblmes =findViewById(R.id.textView179b2);
        imgwsref=findViewById(R.id.imageView120);imgwsref.setVisibility(View.INVISIBLE);
        imgnowifi=findViewById(R.id.imageView71a);

        calibraPantalla();

        P_res_grupoObj=new clsP_res_grupoObj(this,Con,db);
        P_res_turnoObj=new clsP_res_turnoObj(this,Con,db);
        P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
        P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);
        P_mesero_grupoObj=new clsP_mesero_grupoObj(this,Con,db);
        P_mesa_nombreObj=new clsP_mesa_nombreObj(this,Con,db);

        setHandlers();
        cargaConfig();
        gl.ventalock=false;

        getURL();
        ws=new WebService(ResMesero.this,gl.wsurl);

        if (!app.modoSinInternet()) imgnowifi.setVisibility(View.INVISIBLE);

    }

    //region Events

    public void doGrupo(View view) {
        showGrupoMenu();
    }

    public void doComp(View view) {
        listMesa();
    }

    public void doRec(View view) {
        procesaEstadoMesas();
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
                abrirOrden();
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
        int idmesa;
        String amesa;

        corels.clear();

        try {
            mesas.clear();
            P_res_mesaObj.fill("WHERE CODIGO_GRUPO="+idgrupo);

            for (int i = 0; i <P_res_mesaObj.count; i++) {

                idmesa=P_res_mesaObj.items.get(i).codigo_mesa;

                mesa= clsCls.new clsRes_mesa();

                mesa.codigo_mesa=idmesa;
                mesa.nombre=P_res_mesaObj.items.get(i).nombre;mesa.mesanum=mesa.nombre;
                mesa.alias=" ";

                amesa=aliasMesa(idmesa);
                if (!amesa.isEmpty()) {
                    mesa.alias=mesa.nombre+" - "+amesa;mesa.nombre=" ";
                }

                P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+")");

                if (P_res_sesionObj.count>0) {
                    corels.add(P_res_sesionObj.first().id);

                    mesa.estado=P_res_sesionObj.first().estado;
                    mesa.pers=P_res_sesionObj.first().cantp;
                    mesa.cuentas=P_res_sesionObj.first().cantc;
                    mesa.fecha=P_res_sesionObj.first().fechault;

                    T_ordenObj.fill("WHERE (COREL='"+P_res_sesionObj.first().id+"') AND (ESTADO=1)");
                    mesa.pendiente=T_ordenObj.count;
                } else {
                    mesa.estado=0;
                    mesa.pers=0;
                    mesa.cuentas=0;
                    mesa.fecha=0;
                    mesa.pendiente=0;
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

            P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+")");
            if (P_res_sesionObj.count>0) {
                gl.idorden=P_res_sesionObj.first().id;
                startActivity(new Intent(this,Orden.class));
            } else {
                //inputPersonas();
                browse=1;
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
            item.codigo_mesa=mesa.codigo_mesa;
            item.vendedor=gl.idmesero;
            item.estado=1;
            item.cantp=cantpers;
            item.cantc=1;
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

            gl.idorden=item.id;
            startActivity(new Intent(this,Orden.class));
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listMesa() {

        try {
            lcode.clear();lname.clear();

            for (int i = 0; i <mesas.size(); i++) {
                if (mesas.get(i).estado==0) {
                    lcode.add(""+mesas.get(i).codigo_mesa);
                    lname.add(mesas.get(i).nombre);
                }
            }

            final String[] selitems = new String[lname.size()];

            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = "Mesa "+lname.get(i);
            }

            ExDialog mMenuDlg = new ExDialog(this);

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        idmesa=lcode.get(item);
                        nmesa=lname.get(item);
                        listComp();
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
    }

    private void listComp() {

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

            final String[] selitems = new String[lname.size()];

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
    }

    //endregion

    //region Estado

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

        if (ws.openDTCursor.getCount()==0) return;

        try {
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

    private void envioMesa(String idorden) {

        estadoMesa(idorden,-1);
        if (!gl.pelMeseroCaja) return;

        String cmd = "UPDATE P_res_sesion SET Estado=-1 WHERE (EMPRESA=" + gl.emp + ") AND (ID='" + idorden + "')" + ";";

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
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        setGrupo();
    }

    private void setGrupo() {
        clsClasses.clsP_res_turno item=clsCls.new clsP_res_turno();

        try {
            item.codigo_grupo=idgrupo;
            item.vendedor=gl.idmesero;
            item.fecha=du.getActDate();
            P_res_turnoObj.add(item);
        } catch (Exception e) {
            P_res_turnoObj.update(item);
        }

        if (idgrupo>0) {
            try {
                P_res_grupoObj.fill("WHERE CODIGO_GRUPO="+idgrupo);
                lblgrupo.setText(P_res_grupoObj.items.get(P_res_grupoObj.count-1).nombre);
            } catch (Exception e) {
            }
        } else lblgrupo.setText("Seleccione una area");

        listItems();
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
            addOrden();
        } catch (Exception e) {
            mu.msgbox("Cantidad incorrecta");return;
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
        final AlertDialog Dialog;

        try {
            sql="WHERE (CODIGO_GRUPO IN (SELECT CODIGO_GRUPO FROM P_mesero_grupo " +
                    "WHERE (CODIGO_MESERO="+gl.idmesero+")) ) ORDER BY Nombre";
            P_res_grupoObj.fill(sql);

            final String[] selitems = new String[P_res_grupoObj.count];
            for (int i = 0; i <P_res_grupoObj.count; i++) {
                selitems[i]=P_res_grupoObj.items.get(i).nombre;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Grupos de mesas");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    idgrupo=P_res_grupoObj.items.get(item).codigo_grupo;
                    setGrupo();
                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            menudlg.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    browse=2;
                    startActivity(new Intent(ResMesero.this,ValidaSuper.class));
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showGrupoChecks() {
        final AlertDialog Dialog;

        try {
            P_res_grupoObj.fill("ORDER BY Nombre");

            final String[] selitems = new String[P_res_grupoObj.count];
            final boolean[] checked = new boolean[P_res_grupoObj.count];

            for (int i = 0; i <P_res_grupoObj.count; i++) {
                selitems[i]=P_res_grupoObj.items.get(i).nombre;
                P_mesero_grupoObj.fill("WHERE (CODIGO_MESERO="+gl.idmesero+") AND (CODIGO_GRUPO="+P_res_grupoObj.items.get(i).codigo_grupo+")");
                checked[i]=P_mesero_grupoObj.count>0;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);

            menudlg.setTitle("Grupos asignados");

            menudlg.setMultiChoiceItems(selitems, checked ,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                            if (isChecked) {
                                checked[whichButton]=true;
                            } else {
                                checked[whichButton]=false;
                            }
                        }
                    });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            menudlg.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clsClasses.clsP_mesero_grupo item;

                    try {
                        db.beginTransaction();

                        db.execSQL("DELETE FROM P_mesero_grupo WHERE (CODIGO_MESERO="+gl.idmesero+")");

                        for (int i = 0; i <P_res_grupoObj.count; i++) {
                            if (checked[i]) {
                                item = clsCls.new clsP_mesero_grupo();

                                item.codigo_mesero=gl.idmesero;
                                item.codigo_grupo=P_res_grupoObj.items.get(i).codigo_grupo;

                                P_mesero_grupoObj.add(item);
                            }
                        }

                        db.setTransactionSuccessful();
                        db.endTransaction();

                        dialog.cancel();

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

            Dialog = menudlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

    private void inputPersonas() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Mesa "+mesa.nombre+" , cantidad de personas :");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER );
        input.setText("2");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    cantpers=Integer.parseInt(input.getText().toString());
                    if (cantpers<1 | cantpers>50) throw new Exception();
                    addOrden();
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

    private void opcionesMesa() {
        final AlertDialog Dialog;
        //final String[] selitems = {"Cambiar nombre","Borrar nombre","Trasladar cuentas"};
        final String[] selitems = {"Cambiar nombre","Borrar nombre"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Mesa");

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        ingresaNombreMesa();break;
                    case 1:
                        borraNombreMesa();break;
                    case 2:
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
    }

    private void ingresaNombreMesa() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nombre mesa");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                clsClasses.clsP_mesa_nombre nitem;
                String s=input.getText().toString();

                if (s.isEmpty()) {
                    mu.msgbox("Nombre incorrecto");return;
                }

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
        } catch (Exception e) {
            msgbox(e.getMessage());
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
                procesaEstadoMesas();
            } else {
                actualizaEstadosOrdenes();
            }
        }

    }

    @Override
    public void onBackPressed() {
        try{
            //msgAskExit("Salir");
            app.logoutUser(du.getActDateTime());
            finish();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }
    //endregion

}