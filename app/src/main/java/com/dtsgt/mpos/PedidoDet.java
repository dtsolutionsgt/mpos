package com.dtsgt.mpos;

import android.os.Bundle;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidodObj;

public class PedidoDet extends PBase {

    private clsD_pedidoObj D_pedidoObj;
    private clsClasses.clsD_pedido item=clsCls.new clsD_pedido();

    private String pedid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_det);

        super.InitBase();

        pedid=gl.pedid;

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        D_pedidoObj.fill("WHERE Corel='"+pedid+"'");
        item=D_pedidoObj.first();

    }

    //region Events


    //endregion

    //region Main



    //endregion

    //region Aux


    //endregion

    //region Dialogs


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