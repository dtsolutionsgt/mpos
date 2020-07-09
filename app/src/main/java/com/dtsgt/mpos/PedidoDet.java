package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidodObj;

public class PedidoDet extends PBase {

    private RelativeLayout rel1,rel2,rel3,rel4;

    private clsD_pedidoObj D_pedidoObj;
    private clsClasses.clsD_pedido item=clsCls.new clsD_pedido();

    private String pedid;
    private int est,modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_det);

        super.InitBase();

        rel1=findViewById(R.id.rel01);
        rel2=findViewById(R.id.rel02);
        rel3=findViewById(R.id.rel03);
        rel4=findViewById(R.id.rel04);

        pedid=gl.pedid;

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);

        loadItem();
    }

    //region Events

    public void doNuevo(View view) {
        modo=1;
        msgAsk("Marcar como Nuevo");
    }

    public void doPend(View view) {
        modo=2;
        msgAsk("Marcar como Pendiente");
    }

    public void doComp(View view) {
        modo=3;
        msgAsk("Marcar como Completo");
    }

    public void doEnt(View view) {
        modo=4;
        msgAsk("Marcar como Entregado");
    }

    //endregion

    //region Main

    private void loadItem() {
        D_pedidoObj.fill("WHERE Corel='"+pedid+"'");
        item=D_pedidoObj.first();

        est=1;
        if (item.codigo_usuario_creo>0) est=2;
        if (item.codigo_usuario_proceso>0) est=3;
        if (item.codigo_usuario_entrego>0) est=4;
        if (item.anulado==1) est=5;

        if (est==1) rel1.setBackgroundResource(R.drawable.frame_key);
        if (est==2) rel2.setBackgroundResource(R.drawable.frame_key);
        if (est==3) rel3.setBackgroundResource(R.drawable.frame_key);
        if (est==4) rel4.setBackgroundResource(R.drawable.frame_key);

    }

    private void estado() {
        if (modo==1) {
            item.codigo_usuario_creo=0;
            item.codigo_usuario_proceso=0;
            item.codigo_usuario_entrego=0;item.fecha_salida_suc=0;
        }
        if (modo==2) {
            item.codigo_usuario_creo=gl.codigo_vendedor;
            item.codigo_usuario_proceso=0;
            item.codigo_usuario_entrego=0;item.fecha_salida_suc=0;
        }
        if (modo==3) {
            item.codigo_usuario_proceso=gl.codigo_vendedor;
            item.codigo_usuario_entrego=0;item.fecha_salida_suc=0;
        }
        if (modo==4) {
            item.codigo_usuario_entrego=gl.codigo_vendedor;
            item.fecha_salida_suc=du.getActDateTime();
        }

        D_pedidoObj.update(item);

        finish();
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    private void msgAsk(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                estado();
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
            D_pedidoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}