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
import com.dtsgt.classes.clsD_domicilio_detObj;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extTextDlg;
import com.dtsgt.firebase.fbPedidoEnc;
import com.dtsgt.ladapt.LA_D_domicilio_enc;
import com.dtsgt.webservice.srvCommit;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DomPedidos extends PBase {

    private GridView gridView;
    private TextView lblNue,lblPen,lblComp,lblHora,lblStat,lblPend,lblnped;
    private ImageView img1;

    private fbPedidoEnc fbpe;

    private LA_D_domicilio_enc adapter;

    private clsD_domicilio_encObj D_domicilio_encObj;
    private clsD_domicilio_detObj D_domicilio_detObj;

    public recPedidoRecibido rcPedido = new recPedidoRecibido();

    private ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();

    private clsClasses.clsD_domicilio_enc selitem;

    private long fechahoy;
    private int totppend,totnue,totpend,totcomp,selest,nuevoest,limtiempo,focusidx=-1;
    private String focuscorel="";

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

            limtiempo=gl.peDomTiempo;

            fbpe = new fbPedidoEnc("Domicilio/"+gl.emp+"/"+gl.tienda+"/"+du.actDate()+"/");

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);
            D_domicilio_detObj=new clsD_domicilio_detObj(this,Con,db);

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
        startActivity(new Intent(this,DomImport.class));
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

                    browse=1;
                    gl.dom_est_val=selitem.estado;
                    gl.dom_det_cod=selitem.corel;
                    startActivity(new Intent(DomPedidos.this,DomDetalle.class));
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

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado<=2)  ORDER BY idorden");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                if (itm.estado<2) itm.estado=2;
                items.add(itm);
            }

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado in (3,5,6)) ORDER BY estado,idorden");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                items.add(itm);
            }

            itmpos=0;
            for (clsClasses.clsD_domicilio_enc itm : items) {

                itm.sorden="#"+itm.idorden % 1000;
                itm.sestado=app.estadoNombre(itm.estado);
                itm.shora=du.shora(itm.fecha_hora);
                itm.smin=du.sTimeDiff(fa,itm.fecha_hora);
                itm.timeflag=du.timeDiff(fa,itm.fecha_hora)>limtiempo;

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

            if (nestado==3) crearVenta();

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

            Intent intent = new Intent(DomPedidos.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",ss);
            startService(intent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }



    //endregion

    //region Venta

    private void crearVenta() {
        clsClasses.clsT_venta venta;
        clsClasses.clsT_combo combo;

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            clsT_comboObj T_comboObj = new clsT_comboObj(this, Con, db);
            clsT_ventaObj T_ventaObj=new clsT_ventaObj(this,Con,db);
            int itemid=T_ventaObj.newID("SELECT MAX(EMPRESA) FROM T_VENTA");

            D_domicilio_detObj.fill("WHERE (corel='"+selitem.corel+"')");
            for (clsClasses.clsD_domicilio_det ditem:D_domicilio_detObj.items) {

                venta=clsCls.new clsT_venta();

                venta.producto=ditem.codigo_producto;
                venta.empresa=""+itemid;
                venta.um=ditem.um;
                venta.cant=ditem.cant;
                venta.umstock=ditem.um;
                venta.factor=1;
                venta.precio=ditem.precio;
                if (ditem.imp<0.005) venta.imp=0; else venta.imp=ditem.imp;
                venta.des=ditem.des;
                venta.desmon=ditem.desmon;
                venta.total=ditem.total;
                venta.preciodoc=ditem.precio;
                venta.peso=0;
                venta.val1=0;
                venta.val2="";
                venta.val3=0;
                venta.val4="0";
                venta.percep=0;

                T_ventaObj.add(venta);

            }

            db.setTransactionSuccessful();
            db.endTransaction();

            cargaCliente();

            gl.ped_dom_cliente=selitem.cliente_nombre;
            gl.ped_dom_dir=selitem.direccion_text;
            gl.ped_dom_texto=selitem.texto;
            gl.ped_dom_tel=selitem.telefono;
            gl.ped_dom_cambio=mu.frmcur(selitem.cambio);
            gl.ped_dom_orden="#"+selitem.idorden % 1000;

            gl.pedido_dom_import=true;

            gl.ventalock=false;

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaCliente() {

        try {
            if (selitem.codigo_cliente==gl.emp*10) {
                gl.codigo_cliente=selitem.codigo_cliente;
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="C.F.";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;
            } else {
                gl.codigo_cliente=selitem.codigo_cliente;
                gl.gNombreCliente = selitem.cliente_nombre;
                gl.gNITCliente = selitem.nit;
                gl.gNITCliente=gl.gNITCliente.replace("-","");
                gl.gDirCliente = selitem.direccion_text;
                gl.gCorreoCliente = "";
                gl.gNITcf=false;
            }

            gl.cli_muni=gl.cli_muni_suc;
            gl.cli_depto=gl.cli_depto_suc;

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
                itm.timeflag=du.timeDiff(fa,itm.fecha_hora)>limtiempo;
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

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            D_domicilio_encObj.reconnect(Con,db);
            D_domicilio_detObj.reconnect(Con,db);

            initTimer();

            if (browse==1) {
                browse=0;
                aplicarEstado(gl.dom_est_val);return;
            }

            if (browse==2) {
                browse=0;
                listItems();return;
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