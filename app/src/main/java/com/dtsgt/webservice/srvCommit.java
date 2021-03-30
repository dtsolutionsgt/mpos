package com.dtsgt.webservice;

import android.content.Intent;

public class srvCommit extends srvBase {

    private wsCommit wscom;

    private String command;

    @Override
    public void execute() {
        wscom =new wsCommit(URL);
        wscom.execute(command,null);
    }

    @Override
    public void loadParams(Intent intent) {
        command = intent.getStringExtra("command");
    }

}
