package com.dtsgt.webservice;

import android.content.Intent;

public class srvPedidosBitacora extends srvBase {

    private wsPedidosBitacora wspb;

    private String params;

    @Override
    public void execute() {
        wspb=new wsPedidosBitacora(URL,null);
        wspb.execute(params);
    }

    public void loadParams(Intent intent) {
        params = intent.getStringExtra("params");
    }

}
