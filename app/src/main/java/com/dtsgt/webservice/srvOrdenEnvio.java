package com.dtsgt.webservice;

import android.content.Intent;

public class srvOrdenEnvio extends srvBase {

    private wsOrdenEnvio wsoe;

    private String command;

    @Override
    public void execute() {
        wsoe =new wsOrdenEnvio(URL);
        wsoe.execute(command,null);
    }

    @Override
    public void loadParams(Intent intent) {
        command = intent.getStringExtra("command");
    }

}
