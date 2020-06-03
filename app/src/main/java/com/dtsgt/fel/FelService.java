package com.dtsgt.fel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.mpos.R;

public class FelService extends Service {


    private SQLiteDatabase db;
    private BaseDatos Con;
    private BaseDatos.Insert ins;
    private BaseDatos.Update upd;
    private boolean dbopen=false;

    public MiscUtils mu;
    public DateUtils du;

    private String sql,result;

    public FelService() {
        mu=new MiscUtils(this);
        du=new DateUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String urlPath = intent.getStringExtra("URL");
        //notify("Started : "+urlPath);

        Session();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //region Main

    private void Session() {
        result="";
        dbopen=openDB();
        if (!dbopen) {
            notify(result);return;
        }

        processData();
        closeDatabase();
    }

    private void processData() {
        String s1;

        try {
            s1=du.getCorelBaseLong();

            //sql="INSERT INTO T_LOTES VALUES('"+s1+"','',0,0)";
            //db.execSQL(sql);

            result=s1;

            notify(result);
        } catch (Exception e) {
            notify(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion


    //region Database

    private boolean openDB() {
        try {
            Con = new BaseDatos(this);
            db = Con.getWritableDatabase();

            if (db!= null) {
                Con.vDatabase=db;
                ins=Con.Ins;
                upd=Con.Upd;
                return true;
            } else {
                result="No se puede conectar a la base de datos";return false;
            }
        } catch (Exception e) {
            result=e.getMessage();return false;
        }
    }

    private void closeDatabase() {
        try {
            if (dbopen) Con.close();
        } catch (Exception e) { }
        dbopen=false;
    }


    //endregion

    //region Aux

    private void notify(String msg) {
        Notification n  = new Notification.Builder(this)
                .setContentTitle("Fel service")
                .setContentText(msg)
                .setSmallIcon(R.drawable.fel)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    //endregion

}
