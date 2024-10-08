package com.dtsgt.mpos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extTextDlg;
import com.dtsgt.ladapt.LA_D_domicilio_enc;
import com.dtsgt.mant.MantBarril;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PedidosDom extends PBase {

    private GridView gridView;
    private TextView lblNue,lblPen,lblComp,lblHora,lblStat,lblPend,lblnped;
    private ImageView img1;

    private LA_D_domicilio_enc adapter;

    private clsD_domicilio_encObj D_domicilio_encObj;

    public recPedidoRecibido rcPedido = new recPedidoRecibido();

    private ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();

    private clsClasses.clsD_domicilio_enc selitem;

    private long fechahoy;
    private int totppend,totnue,totpend,totcomp,selest,nuevoest;

    private TimerTask ptask;
    private int period=10000,delay=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pedidos_dom);

            super.InitBase();

            gridView = findViewById(R.id.gridView1);
            lblNue =  findViewById(R.id.textView179);lblNue.setText("");
            lblPen =  findViewById(R.id.textView181);lblPen.setText("");
            lblComp =  findViewById(R.id.textView182);lblComp.setText("");
            lblHora =  findViewById(R.id.textView194);lblHora.setText(du.shora(du.getActDateTime()));
            lblPend =  findViewById(R.id.textView179a);lblPend.setText("");
            lblnped = findViewById(R.id.textView354);lblnped.setVisibility(View.INVISIBLE);

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);

            fechahoy =du.getActDate();

            setHandlers();

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doAdd(View view) {
        startActivity(new Intent(this,DomImport.class));
    }

    public void doMenu(View view) {

    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    selitem = (clsClasses.clsD_domicilio_enc)lvObj;
                    selidx = position;

                    adapter.setSelectedIndex(position);
                    procesaEstado();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            };
        });

    }

    //endregion

    //region Main

    private void listItems() {
        Long fa=du.getActDateTime();

        try {
            items.clear();

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado<=2)  ORDER BY idorden DESC");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                if (itm.estado<2) itm.estado=2;
                items.add(itm);
            }

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado in (3,5,6)) ORDER BY idorden DESC");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                items.add(itm);
            }

            for (clsClasses.clsD_domicilio_enc itm : items) {
                itm.sorden="#"+itm.idorden % 1000;
                itm.sestado=app.estadoNombre(itm.estado);
                itm.shora=du.shora(itm.fecha_hora);
                itm.smin=du.sTimeDiff(fa,itm.fecha_hora);
            }

            adapter=new LA_D_domicilio_enc(this,this,items);
            gridView.setAdapter(adapter);

            calculaTotales();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void calculaTotales() {
        try {
            totppend=0;totnue=0;totpend=0;totcomp=0;

            for (clsClasses.clsD_domicilio_enc itm : items) {

                int est=itm.estado;

                switch (est) {
                    case 2:
                        totnue++;break;
                    case 3:
                        totpend++;break;
                    case 5:
                        totcomp++;break;
                    case 6:
                        totppend++;break;
                }

            }

            lblNue.setText(""+totnue);lblPen.setText(""+totpend);
            lblComp.setText(""+totcomp);lblPend.setText(""+totppend);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void procesaEstado() {
        String msg="";

        try {
            selest=selitem.estado;

            switch (selest) {
                case 2:
                    msg="EN PROCESO";nuevoest=3;break;
                case 3:
                    msg="COMPLETO";nuevoest=5;break;
                case 5:
                    msg="EN TRANSITO";nuevoest=6;break;
                case 6:
                    msg="ENTREGADO";nuevoest=7;break;
            }

            extTextDlg txtdlg = new extTextDlg();
            txtdlg.buildDialog(PedidosDom.this,"Orden #"+selitem.sorden,"Salir","Opciones","Aplicar");

            txtdlg.setText("Marcar orden como\n\n"+msg+"\n");

            txtdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) { txtdlg.dismiss(); }
            });

            txtdlg.setOnMiddleClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuOrden();txtdlg.dismiss();
                }
            });

            txtdlg.setOnRightClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aplicarEstado(nuevoest);txtdlg.dismiss();
                }
            });

            txtdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicarEstado(int nestado) {
        try {
            selitem.estado=nestado;
            D_domicilio_encObj.update(selitem);

            items.get(selidx).estado=nestado;
            items.get(selidx).sestado=app.estadoNombre(nestado);

            adapter.notifyDataSetChanged();

            calculaTotales();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Timer

    private void initTimer() {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(ptask=new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        processTimer();
                    }
                });
            }
        }, delay, period);
    }

    private void cancelTimer() {
        try {
            ptask.cancel();
        } catch (Exception e) {}
    }

    private void processTimer() {
        Long fa=du.getActDateTime();
        lblHora.setText(du.shora(fa));

        try {
            for (clsClasses.clsD_domicilio_enc itm : items) {
                itm.smin=du.sTimeDiff(fa,itm.fecha_hora);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux

    public class recPedidoRecibido extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null && action.equals("com.dtsgt.PEDIDO_RECIBIDO")) {
                //toast("Custom broadcast received!");
            }
        }
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    ;break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void menuOrden() {
        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(PedidosDom.this,"Orden");

            listdlg.add(1,"Previo estado");
            listdlg.add(2,"Anular orden");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        processMainMenu(position);
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
            listdlg.setSelectedIndex(1);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void processMainMenu(int iidx) {
        toast("menu "+iidx);
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};


            D_domicilio_encObj.reconnect(Con,db);

            listItems();

            initTimer();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        cancelTimer();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.dtsgt.PEDIDO_RECIBIDO");
        registerReceiver(rcPedido, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(rcPedido);
    }

    //endregion

}