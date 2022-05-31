package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_orden_bitacoraObj;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocObj;
import com.dtsgt.classes.clsD_pedidoordenObj;
import com.dtsgt.ladapt.LA_D_pedido;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.srvPedidosBitacora;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class PedidosEnv extends PBase {

    private GridView gridView;
    private TextView lblNue,lblPen,lblComp,lblHora,lblStat,lblPend;
    private ImageView img1;

    private LA_D_pedido adapter;
    private clsD_pedidoObj D_pedidoObj;
    private clsD_pedidocObj D_pedidocObj;
    private clsD_pedidoordenObj D_pedidoordenObj;
    private clsD_orden_bitacoraObj D_orden_bitacoraObj;

    private WebService ws;

    private int cnue,cpen,ccomp;
    private String sql,sql1,sql2,sql3;
    private boolean modo=false,horiz,wsidle=true;
    private long tbot;

    private TimerTask ptask;
    private int period=10000,delay=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_envv);

        super.InitBase();

        horiz=pantallaHorizontal();

        gridView =(GridView) findViewById(R.id.gridView1);
        lblNue = (TextView) findViewById(R.id.textView179);lblNue.setText("");
        lblPen = (TextView) findViewById(R.id.textView181);lblPen.setText("");
        lblComp = (TextView) findViewById(R.id.textView182);lblComp.setText("");
        lblHora = (TextView) findViewById(R.id.textView194);lblHora.setText("Ordenes "+app.prefijoCaja().toUpperCase());
        lblStat = (TextView) findViewById(R.id.textView199);lblStat.setText("");
        lblPend = (TextView) findViewById(R.id.textView179a);lblPend.setText("");
        img1 = (ImageView) findViewById(R.id.imageView86);img1.setImageResource(R.drawable.blank32);

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        D_pedidocObj=new clsD_pedidocObj(this,Con,db);
        D_pedidoordenObj=new clsD_pedidoordenObj(this,Con,db);
        D_orden_bitacoraObj=new clsD_orden_bitacoraObj(this,Con,db);

        if (horiz) {
            gridView.setNumColumns(4);
        } else {
            gridView.setNumColumns(2);
        }

        tbot=du.getActDate();
        sql1="WHERE (ANULADO=0) AND (FECHA_ENTREGA=0) ORDER BY FECHA_SALIDA_SUC,EMPRESA ";
        sql2="WHERE (ANULADO=0) AND (FECHA_PEDIDO>="+tbot+") AND (FECHA_ENTREGA>0) ORDER BY FECHA_ENTREGA DESC ";
        sql3="WHERE (ANULADO=1) AND (FECHA_PEDIDO>="+tbot+") ORDER BY EMPRESA DESC";

        setHandlers();

        app.getURL();
        ws=new WebService(PedidosEnv.this,gl.wsurl);

        sql=sql1;modo=false;
        listItems();
    }

    //region Events

    public void doMenu(View view) {
        showItemMenu();
    }

    public void doExit(View view) {
        finish();
    }

    public void doRefresh(View view) {
        toast("Actualizando . . .");
        recibePedidos();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                clsClasses.clsD_pedido item = (clsClasses.clsD_pedido)lvObj;

                adapter.setSelectedIndex(position);
                gl.pedid=item.corel;

                browse=1;
                startActivity(new Intent(PedidosEnv.this,PedidoEnv.class));
            };
        });

    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
        String cli,stat;
        long tact,tlim;
        double tate,efi;
        int retr,mdif,mlim,sate,slim,ppend;

        statistics();

        try {
            tact=du.getActDateTime();tlim=tact+100;
            cnue=0;cpen=0;ccomp=0;stat="";
            retr=0;sate=0;slim=0;
            ppend=app.pendientesPago("");

            if (!modo){
                sql="WHERE (ANULADO=0) AND (FECHA_ENTREGA=0) AND (FECHA_PEDIDO<="+tlim+") AND (FECHA_PEDIDO>="+tbot+") " +
                     "ORDER BY FECHA_SALIDA_SUC,EMPRESA,FECHA_PEDIDO ";
            }

            D_pedidoObj.fill(sql);
            //sql=sql1;modo=false;

            for (int i = 0; i <D_pedidoObj.count; i++) {
                item=D_pedidoObj.items.get(i);

                D_pedidocObj.fill("WHERE COREL='"+item.corel+"'");
                if (D_pedidocObj.count>0) cli=D_pedidocObj.first().nombre;else cli="";
                cli="";
                item.nombre=cli;

                //item.tdif=du.timeDiff(tact,item.fecha_recepcion_suc);
                item.tdif=-1;
                if (item.fecha_salida_suc!=0) item.tdif=du.timeDiff(item.fecha_salida_suc,item.fecha_recepcion_suc);
                if (item.anulado==1) item.tdif=-1;

                //item.lim=item.firma_cliente;
                item.lim= limitePedido(item.corel);
                mdif=(int) item.tdif;mlim=(int) item.lim;
                sate+=mdif;slim+=mlim;
                if (mdif>mlim) retr++;

                if (item.codigo_usuario_proceso>0) {
                    ccomp++;
                } else {
                    if (item.codigo_usuario_creo>0) cpen++;else cnue++;
                }

                item.internet=true;item.domicilio=true;item.idorden="";item.nombre="DOMICILIO";

                D_pedidoordenObj.fill("WHERE COREL='"+item.corel+"'");
                if (D_pedidoordenObj.count>0) {
                    item.internet=false;
                    if (D_pedidoordenObj.first().tipo==0) item.domicilio=false;
                    item.idorden=D_pedidoordenObj.first().orden;
                    if (item.domicilio) {
                        item.nombre="DOMICILIO\n"+cli.toUpperCase();
                    } else {
                        item.nombre="ENTREGA\n"+cli.toUpperCase();
                    }
                }

            }

            lblNue.setText(""+cnue);lblPen.setText(""+cpen);lblComp.setText(""+ccomp);
            if (ppend>-1) lblPend.setText(""+ppend);else lblPend.setText("");

            adapter=new LA_D_pedido(this,this,D_pedidoObj.items,modo);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }


    }

    private void iniciaPedidos() {

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(ptask=new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public synchronized void run() {
                             recibePedidos();
                        }
                    });
                }
            }, delay, period);
    }

    private void cancelaPedidos() {
        try {
            ptask.cancel();
        } catch (Exception e) {}
    }

    private void recibePedidos() {
        int pp;
        String fname;

        //lblHora.setText(du.shora(du.getActDateTime()));

        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/mposordser";
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                fname=files[i].getName();
                pp=fname.indexOf(".txt");
                if (pp>0){
                    if (!app.agregaPedido(path+"/"+fname,path+"/error/"+fname,du.getActDateTime(),fname)) {
                       //msgbox2("Ocurrio error en recepción de orden :\n"+app.errstr);
                    }
                }
            }
        } catch (Exception e) {
            //msgbox2("recibePedidos : "+e.getMessage());
        }

        listItems();
        procesaEstados();
    }

    //endregion

    //region Pedidos locales

    @Override
    protected void wsCallBack(Boolean throwing,String errmsg) {
        try {
            super.wsCallBack(throwing, errmsg);
            aplicaEstados();
        } catch (Exception e) {
            toastlong("wsCallBack "+e.getMessage());toastlong("wsCallBack "+e.getMessage());
            //msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
        wsidle=true;
    }

    private void procesaEstados() {
        if (!wsidle) return;

        try {
            wsidle=false;
            sql="SELECT CODIGO,COREL_PEDIDO,COMANDA,COREL_LINEA " +
                "FROM D_PEDIDOCOM WHERE (CODIGO_RUTA="+gl.codigo_ruta+") ORDER BY CODIGO";
            ws.openDT(sql);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            wsidle=true;
        }
    }

    private void aplicaEstados() {
        int iid,lin;
        String cor,cmd,del="",ins="",ord="";

        if (ws.openDTCursor.getCount()==0) return;

        try {
            ws.openDTCursor.moveToFirst();cmd="";

            while (!ws.openDTCursor.isAfterLast()) {

                iid=ws.openDTCursor.getInt(0);cmd+="DELETE FROM D_PEDIDOCOM WHERE CODIGO="+iid+";";
                cor=ws.openDTCursor.getString(1);
                sql=ws.openDTCursor.getString(2);
                lin=ws.openDTCursor.getInt(3);

                if (lin==1) {
                    del="DELETE FROM D_PEDIDO WHERE COREL='"+cor+"'";
                    ins=sql.replaceAll("<>", "'");
                } else if (lin==2) {
                    ord = sql.replaceAll("<>", "'");
                } else if (lin==0) {
                    del="DELETE FROM D_PEDIDO WHERE COREL='"+cor+"'";
                    ins=sql.replaceAll("<>", "'");
                    ord="";
                }

                ws.openDTCursor.moveToNext();
            }

            try {
                db.beginTransaction();

                db.execSQL(del);
                db.execSQL(ins);
                if (!ord.isEmpty()) {
                    db.execSQL(ord);
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                String ss=e.getMessage();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            enviaConfirmacion(cmd);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void enviaConfirmacion(String cmd) {
        try {
            Intent intent = new Intent(PedidosEnv.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",cmd);
            startService(intent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Stats

    private void statistics() {
        clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
        long tact,tlim;
        String stat,sqls;
        double tate,efi,ltate;
        int retr,mdif,mlim,sate,slim,cord,rid;

        try {

            tact=du.getActDateTime();tlim=tact+100;
            retr=0;sate=0;slim=0;cord=0;

            sqls="WHERE (ANULADO=0) AND (FECHA_PEDIDO<="+tlim+") AND (FECHA_PEDIDO>="+tbot+") AND (CODIGO_USUARIO_CREO="+gl.codigo_vendedor+") " +
                    "ORDER BY FECHA_SALIDA_SUC,EMPRESA,FECHA_PEDIDO ";
            D_pedidoObj.fill(sqls);

            for (int i = 0; i <D_pedidoObj.count; i++) {
                item=D_pedidoObj.items.get(i);cord++;

                item.tdif=du.timeDiff(tact,item.fecha_recepcion_suc);
                if (item.fecha_salida_suc!=0) item.tdif=du.timeDiff(item.fecha_salida_suc,item.fecha_recepcion_suc);

                item.lim=item.firma_cliente;
                mdif=(int) item.tdif;mlim=(int) item.lim;
                sate+=mdif;slim+=mlim;
                if (mdif>mlim && mlim>0) retr++;
            }

            if (D_pedidoObj.count>0) {
                ltate = statTPPO();
                tate = sate / D_pedidoObj.count;

                rid=R.drawable.blank32;
                if (ltate>0) {
                    if (tate<=ltate) rid=R.drawable.stat_arr_up; else rid=R.drawable.stat_arr_dn;
                }
                img1.setImageResource(rid);

                if (sate>0) efi=100*slim/sate;else efi=0;if (efi>100) efi=100;
                stat="Tiempo : "+mu.frmdecimal(tate,1)+" min , Ordenes : "+cord+" , Eficiencia : "+ mu.frmint(efi)+" %  , Retrasados : "+retr;

                updateStats(tate,cord,efi,retr);
            } else {
                stat="";
            }
        } catch (Exception e) {
            mu.msgbox(e.getMessage());stat="";
        }

        String ss=stat;
        lblStat.setText(stat);
    }

    private void updateStats(double tate,int cord,double efi,int retr) {
        clsClasses.clsD_orden_bitacora stat=null;
        long ff;

        ff=du.getActDateTime();ff=ff/100;ff=ff*100;

        try {
            stat = clsCls.new clsD_orden_bitacora();

            stat.fecha=ff;
            stat.codigo_sucursal=gl.tienda;
            stat.codigo_vendedor=gl.codigo_vendedor;
            stat.cant_ordenes=cord;
            stat.cant_retrasados=retr;
            stat.tppo=tate;
            stat.eficiencia=efi;
            stat.statcom=0;

            D_orden_bitacoraObj.add(stat);

        } catch (Exception e) {
            try {
                D_orden_bitacoraObj.update(stat);
            } catch (Exception ee) {
                //toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
            }
        }

        syncStat(stat);

    }

    private void syncStat(clsClasses.clsD_orden_bitacora stat) {
        String s,ss;

       // toast("sync stat");

        /*
        try {
            D_orden_bitacoraObj.fill("WHERE CODIGO_VENDEDOR="+gl.codigo_vendedor+" ORDER BY FECHA DESC");
            stat=D_orden_bitacoraObj.items.get(1);
        } catch (Exception ee) {
            return;
        }
        */

        try {
            s=gl.emp+";";
            ss=du.univfecha_vb_net(stat.fecha);s+=ss+";";
            ss=""+gl.tienda;s+=ss+";";
            ss=""+gl.codigo_vendedor;s+=ss+";";
            ss=""+stat.cant_ordenes;s+=ss+";";
            ss=""+stat.cant_retrasados;s+=ss+";";
            ss=""+stat.tppo;s+=ss+";";
            ss=""+stat.eficiencia;s+=ss+";";

            Intent intent = new Intent(PedidosEnv.this, srvPedidosBitacora.class);

            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("params",s);

            startService(intent);
        } catch (Exception ee) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
        }
    }

    private double statTPPO() {
        try {
            D_orden_bitacoraObj.fill("WHERE CODIGO_VENDEDOR="+gl.codigo_vendedor+" ORDER BY FECHA DESC");
            return D_orden_bitacoraObj.first().tppo;
        } catch (Exception ee) {
            return -1;
        }
    }

    //endregion

    //region Aux

    private int limitePedido(String corel) {
        Cursor dt;

        try {
            String sqls="SELECT SUM(P_PRODUCTO.TIEMPO_PREPARACION) FROM  D_PEDIDOD " +
                "INNER JOIN P_PRODUCTO ON D_PEDIDOD.CODIGO_PRODUCTO=P_PRODUCTO.CODIGO_PRODUCTO " +
                "WHERE (D_PEDIDOD.COREL='"+corel+"')";
            dt=Con.OpenDT(sqls);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
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

    //endregion

    //region Dialogs

    private void showItemMenu() {
        final AlertDialog Dialog;
        final String[] selitems = {"Borrar Ordenes"};

        ExDialog menudlg = new ExDialog(this);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        browse=2;
                        startActivity(new Intent(PedidosEnv.this,ValidaSuper.class));
                }

                listItems();
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

    private void msgAskDelete(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskDelete2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskDelete2(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    db.beginTransaction();
                    db.execSQL("DELETE FROM D_PEDIDO");
                    db.execSQL("DELETE FROM D_PEDIDOC");
                    db.execSQL("DELETE FROM D_PEDIDOCOMBO");
                    db.execSQL("DELETE FROM D_PEDIDOD");

                    db.setTransactionSuccessful();
                    db.endTransaction();

                    toast("Órdenes borradas");
                    finish();

                } catch (Exception e) {
                    db.endTransaction();
                    msgbox(e.getMessage());
                }

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
            D_pedidocObj.reconnect(Con,db);
            D_pedidoordenObj.reconnect(Con,db);
            D_orden_bitacoraObj.reconnect(Con,db);
        } catch (Exception e) { }

        if (gl.closePedido) {
            gl.closePedido=false;
            finish();return;
        }

        iniciaPedidos();

        if (browse==1) {
            browse=0;
            listItems();return;
        }

        if (browse==2) {
            browse=0;
            if (gl.checksuper) msgAskDelete("Borrar todos las órdenes");
            gl.checksuper=false;
            return;
        }
    }

    @Override
    protected void onPause() {
        cancelaPedidos();
        super.onPause();
    }

    //endregion

}