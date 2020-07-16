package com.dtsgt.webservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class srvInventConfirm extends srvBase {

    private wsInventConfirm wsic;

    private String idstock;

    @Override
    public void execute() {
        wsic=new wsInventConfirm(URL,idstock);
        wsic.execute();
        notification("Confirm service started");
    }

    @Override
    public void loadParams(Intent intent) {
        idstock = intent.getStringExtra("idstock");
    }


    @Override
    public void onDestroy() {
        notification("Confirm service complete");
    }

}
