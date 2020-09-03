package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocObj;
import com.dtsgt.ladapt.LA_D_pedido;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Pedidos extends PBase {

    private GridView gridView;
    private TextView lblNue,lblPen,lblComp,lblHora,lblStat;

    private LA_D_pedido adapter;
    private clsD_pedidoObj D_pedidoObj;
    private clsD_pedidocObj D_pedidocObj;

    private int cnue,cpen,ccomp;
    private String sql,sql1,sql2,sql3;
    private boolean modo=false;
    private long tbot;

    private TimerTask ptask;
    private int period=10000,delay=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        super.InitBase();

        gridView =(GridView) findViewById(R.id.gridView1);
        lblNue = (TextView) findViewById(R.id.textView179);lblNue.setText("");
        lblPen = (TextView) findViewById(R.id.textView181);lblPen.setText("");
        lblComp = (TextView) findViewById(R.id.textView182);lblComp.setText("");
        lblHora = (TextView) findViewById(R.id.textView194);lblHora.setText(du.shora(du.getActDateTime()));
        lblStat = (TextView) findViewById(R.id.textView199);lblStat.setText("");

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        D_pedidocObj=new clsD_pedidocObj(this,Con,db);

        tbot=du.getActDate();
        sql1="WHERE (ANULADO=0) AND (FECHA_ENTREGA=0) ORDER BY FECHA_SALIDA_SUC,EMPRESA ";
        sql2="WHERE (ANULADO=0) AND (FECHA_PEDIDO>="+tbot+") AND (FECHA_ENTREGA>0) ORDER BY FECHA_ENTREGA DESC ";
        sql3="WHERE (ANULADO=1) AND (FECHA_PEDIDO>="+tbot+") ORDER BY EMPRESA DESC";

        setHandlers();

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

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                clsClasses.clsD_pedido item = (clsClasses.clsD_pedido)lvObj;

                adapter.setSelectedIndex(position);
                gl.pedid=item.corel;

                browse=1;
                startActivity(new Intent(Pedidos.this,PedidoDet.class));
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
        int retr,mdif,mlim,sate,slim;

        statistics();

        try {
            tact=du.getActDateTime();tlim=tact+100;
            cnue=0;cpen=0;ccomp=0;stat="";
            retr=0;sate=0;slim=0;

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
                item.nombre=cli;

                item.tdif=du.timeDiff(tact,item.fecha_recepcion_suc);
                if (item.fecha_salida_suc!=0) item.tdif=du.timeDiff(item.fecha_salida_suc,item.fecha_recepcion_suc);
                if (item.anulado==1) item.tdif=-1;

                item.lim=item.firma_cliente;//item.lim= limitePedido(item.corel);
                mdif=(int) item.tdif;mlim=(int) item.lim;
                sate+=mdif;slim+=mlim;
                if (mdif>mlim) retr++;

                if (item.codigo_usuario_proceso>0) {
                    ccomp++;
                } else {
                    if (item.codigo_usuario_creo>0) cpen++;else cnue++;
                }
            }

            lblNue.setText(""+cnue);lblPen.setText(""+cpen);lblComp.setText(""+ccomp);
            adapter=new LA_D_pedido(this,this,D_pedidoObj.items,modo);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }


    }

    private void statistics() {
        clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
        long tact,tlim;
        String stat,sqls;
        double tate,efi;
        int retr,mdif,mlim,sate,slim;

        try {

            tact=du.getActDateTime();tlim=tact+100;
            retr=0;sate=0;slim=0;

            sqls="WHERE (ANULADO=0) AND (FECHA_PEDIDO<="+tlim+") AND (FECHA_PEDIDO>="+tbot+") " +
                        "ORDER BY FECHA_SALIDA_SUC,EMPRESA,FECHA_PEDIDO ";
            D_pedidoObj.fill(sqls);

            for (int i = 0; i <D_pedidoObj.count; i++) {
                item=D_pedidoObj.items.get(i);

                item.tdif=du.timeDiff(tact,item.fecha_recepcion_suc);
                if (item.fecha_salida_suc!=0) item.tdif=du.timeDiff(item.fecha_salida_suc,item.fecha_recepcion_suc);

                item.lim=item.firma_cliente;
                mdif=(int) item.tdif;mlim=(int) item.lim;
                sate+=mdif;slim+=mlim;
                if (mdif>mlim && mlim>0) retr++;
            }

            if (D_pedidoObj.count>0) {
                tate=sate/D_pedidoObj.count;
                if (sate>0) efi=100*slim/sate;else efi=0;
                stat="Tiempo promedio : "+mu.frmdecimal(tate,1)+" min   ,   Eficiencia : "+ mu.frmint(efi)+" %   ,   Retrasados : "+retr;
            } else stat="";
        } catch (Exception e) {
            mu.msgbox(e.getMessage());stat="";
        }

        String ss=stat;
        lblStat.setText(stat);

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

        lblHora.setText(du.shora(du.getActDateTime()));

        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/mposordser";
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                fname=files[i].getName();
                pp=fname.indexOf(".txt");
                if (pp>1){
                    if (!app.agregaPedido(path+"/"+fname,path+"/error/"+fname,du.getActDateTime(),fname)) {
                        msgbox2("Ocurrio error en recepción de orden :\n"+app.errstr);
                    }
                }
            }
        } catch (Exception e) {
            msgbox2("recibePedidos : "+e.getMessage());
        }

        listItems();

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

    //endregion

    //region Dialogs


    private void showItemMenu() {
        final AlertDialog Dialog;
        final String[] selitems = {"Pedidos Activos","Pedidos Pagados","Pedidos Anulados"};

        ExDialog menudlg = new ExDialog(this);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        sql=sql1;modo=false;break;
                    case 1:
                        sql=sql2;modo=true;break;
                    case 2:
                        sql=sql3;modo=true;break;
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


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            D_pedidocObj.reconnect(Con,db);
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
    }

    @Override
    protected void onPause() {
        cancelaPedidos();
        super.onPause();
    }

    //endregion

}