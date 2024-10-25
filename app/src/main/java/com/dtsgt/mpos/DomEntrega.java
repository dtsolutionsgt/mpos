package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

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
import com.dtsgt.classes.clsD_domicilio_detObj;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.clsD_domicilio_entregaObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbPedidoEnc;
import com.dtsgt.ladapt.LA_D_domicilio_enc;
import com.dtsgt.webservice.srvCommit;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DomEntrega extends PBase {

    private GridView gridView;
    private TextView lblPen,lblHora,lblPend;
    private ImageView img1;

    private fbPedidoEnc fbpe;

    private LA_D_domicilio_enc adapter;

    private clsD_domicilio_encObj D_domicilio_encObj;
    private clsD_domicilio_detObj D_domicilio_detObj;
    private clsD_domicilio_entregaObj D_domicilio_entregaObj;

    private ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();

    clsClasses.clsD_domicilio_enc selitem;
    clsClasses.clsD_domicilio_entrega eitem;

    private long fechahoy;
    private int totppend,totnue,totpend,totcomp,selest,nuevoest,focusidx=-1;
    private String focuscorel="";

    private TimerTask ptask;
    private int period=10000,delay=50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_entrega);

            super.InitBase();

            gridView = findViewById(R.id.gridView1);
            lblPen =  findViewById(R.id.textView181);lblPen.setText("");
            lblHora =  findViewById(R.id.textView194);lblHora.setText(du.shora(du.getActDateTime()));
            lblPend =  findViewById(R.id.textView179a);lblPend.setText("");

            fbpe = new fbPedidoEnc("Domicilio/"+gl.emp+"/"+gl.tienda+"/"+du.actDate()+"/");

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);
            D_domicilio_detObj=new clsD_domicilio_detObj(this,Con,db);
            D_domicilio_entregaObj=new clsD_domicilio_entregaObj(this,Con,db);

            app.getURL();
            fechahoy =du.getActDate();

            listItems();

            setHandlers();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doAdd(View view) {
        gl.repartidor_select=false;
        startActivity(new Intent(this,DomRepartidor.class));
    }

    public void doMenu(View view) {
        browse=2;
        startActivity(new Intent(this, DomPedComp.class));
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

                    gl.dom_est_val=selitem.estado;
                    gl.dom_det_cod=selitem.corel;
                    gl.ped_dom_orden="#"+selitem.idorden % 1000;

                    showItemMenu();
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
        int itmpos;

        try {
            items.clear();focusidx=-1;

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado=6)  ORDER BY idorden");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                if (itm.estado<2) itm.estado=2;
                items.add(itm);
            }

            itmpos=0;
            for (clsClasses.clsD_domicilio_enc itm : items) {

                itm.sorden="#"+itm.idorden % 1000;
                itm.sestado="EN ESPERA";
                itm.shora=du.shora(itm.fecha_hora);
                itm.fecha_entrega=0;
                itm.smin="";
                itm.timeflag=false;

                try {
                    D_domicilio_entregaObj.fill("WHERE (COREL_ORDEN='"+itm.corel+"') ORDER BY COREL DESC");
                    eitem=D_domicilio_entregaObj.first();

                    itm.cliente_nombre=eitem.nombre;
                    if (eitem.idrepar==0) itm.timeflag=true;
                    if (eitem.estado==1) {
                        itm.shora=du.shora(eitem.fechaini);
                        itm.smin=du.sTimeDiff(fa,eitem.fechaini);
                        itm.fecha_entrega=eitem.fechaini;
                        itm.sestado="EN TRANSITO";
                    }

                    if (eitem.total==eitem.pago) itm.estadopago=2; else itm.estadopago=1;

                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .ent. "+e.getMessage());
                }

                if (itm.corel.equalsIgnoreCase(focuscorel)) focusidx=itmpos;
                itmpos++;
            }

            adapter=new LA_D_domicilio_enc(this,this,items);
            gridView.setAdapter(adapter);

            if (focusidx>-1) {
                adapter.setSelectedIndex(focusidx);
                gridView.smoothScrollToPosition(focusidx);
            }

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
                    case 6:
                        totpend++;break;
                }

            }

            lblPen.setText(""+totpend);
            lblPend.setText(""+totppend);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void asignaRepartidor() {
        try {
            D_domicilio_entregaObj.fill("WHERE (COREL_ORDEN='"+selitem.corel+"') ORDER BY COREL DESC");
            eitem=D_domicilio_entregaObj.first();

            eitem.idrepar=gl.repartidor_codigo;
            eitem.nombre=gl.gstr;
            eitem.placa=gl.gstr2;
            eitem.idempresa=gl.ped_dom_empresa;

            eitem.estado=1;
            eitem.fechaini=du.getActDateTime();

            D_domicilio_entregaObj.update(eitem);

            listItems();
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

            fbpe.updateState(selitem.corel,selitem.estado);
            enviaEstado(nestado);

            focuscorel=selitem.corel;
            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void enviaEstado(int estado) {
        String ss="";
        String sf=du.univfechahora(du.getActDateTime());

        try {
            switch (estado) {
                case 3: // PROCESANDO
                    ss="UPDATE D_DOMICILIO_ENC SET estado="+estado+",fecha_inicio='"+sf+"' WHERE (corel='"+selitem.corel+"')";
                    break;
                case 4: // ANULADO
                    ss="UPDATE D_DOMICILIO_ENC SET estado="+estado+" WHERE (corel='"+selitem.corel+"')";
                    break;
                case 5: // COMPLETO
                    ss="UPDATE D_DOMICILIO_ENC SET estado="+estado+",fecha_completo='"+sf+"' WHERE (corel='"+selitem.corel+"')";
                    break;
                case 6: // EN TRANSITO
                    ss="UPDATE D_DOMICILIO_ENC SET estado="+estado+" WHERE (corel='"+selitem.corel+"')";
                    break;
                case 7: // ENTREGADO
                    ss="UPDATE D_DOMICILIO_ENC SET estado="+estado+",fecha_entrega='"+sf+"' WHERE (corel='"+selitem.corel+"')";
                    break;
            }


        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void completarOrden() {
        try {
            aplicarEstado(7);
            listItems();
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
                if (itm.fecha_entrega==0) {
                    itm.smin=du.sTimeDiff(fa,itm.fecha_hora);
                } else {
                    itm.smin=du.sTimeDiff(fa,itm.fecha_entrega);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    completarOrden();break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void showItemMenu() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(DomEntrega.this,"Opciones");

            listdlg.add("Repartidor");

            if (selitem.estadopago==1) listdlg.add("Pagar");
            if (selitem.estadopago==2) listdlg.add("Completar");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                browse=2;
                                gl.repartidor_codigo=0;
                                gl.repartidor_select=true;
                                startActivity(new Intent(DomEntrega.this,DomRepartidor.class));
                                break;
                            case 1:
                                browse=1;
                                if (selitem.estadopago==1) {
                                    startActivity(new Intent(DomEntrega.this,DomPago.class));
                                }
                                if (selitem.estadopago==2) msgask(0,"¿Completar orden?");
                                break;
                            case 2:
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

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            D_domicilio_encObj.reconnect(Con,db);
            D_domicilio_detObj.reconnect(Con,db);
            D_domicilio_entregaObj.reconnect(Con,db);

            initTimer();

            if (browse==1) {
                browse=0;
                aplicarEstado(gl.dom_est_val);return;
            }

            if (browse==2) {
                browse=0;
                asignaRepartidor();return;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        cancelTimer();
        super.onPause();
    }


    //endregion

}