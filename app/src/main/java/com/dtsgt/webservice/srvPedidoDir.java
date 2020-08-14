package com.dtsgt.webservice;

import android.content.Intent;

public class srvPedidoDir extends srvBase {

    private wsPedidoDir wspd;

    private String dir;
    private int codigo;

    @Override
    public void execute() {
        wspd=new wsPedidoDir(URL,null);
        wspd.execute(codigo,dir);
    }

    public void loadParams(Intent intent) {
        codigo = intent.getIntExtra("codigo",0);
        dir = intent.getStringExtra("dir");
    }

}
