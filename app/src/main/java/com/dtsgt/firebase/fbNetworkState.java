package com.dtsgt.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.dtsgt.mpos.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class fbNetworkState extends BroadcastReceiver {

    private Context cont;

    private NotificationManager notificationManager;

    private int iconresource=R.drawable.logo;

    @Override
    public void onReceive(Context context, Intent intent) {
        cont=context;
        boolean mConnected;

        try {
            ConnectivityManager mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
            mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            notificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);

            if (mConnected) {
                //notify("MPos está conectado al internet");

                Handler mtimer = new Handler();
                Runnable mrunner= () -> {
                    notify("MPos está conectado a internet");
                };
                mtimer.postDelayed(mrunner,40000);

            } else {
                notify("Se perdió la conexión a internet");
            }

        } catch (Exception e) {
            toast("Network status error : "+e.getMessage());
        }

        refreshFirebase();
    }

    private void refreshFirebase() {
        try {
            fbStock fbb=new fbStock("Stock");
            fbb.fdb.goOnline();
            fbb.fdt.push();
        } catch (Exception e) {
            toast("Network status error : "+e.getMessage());
        }
    }

    public void notify(String msg) {
        int notificationId = createID();
        String channelId = "channel-id",channelName = "Channel Name";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent fullScreenIntent = new Intent(cont, fbNetworkState.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(cont, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notificationLayout = new RemoteViews(cont.getPackageName(), R.layout.notification);

        notificationLayout.setTextViewText(R.id.title, msg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(cont, channelId)
                .setSmallIcon(iconresource)
                //.setLargeIcon(bm)
                .setContentTitle("MPos")
                .setContentText(msg)
                .setVibrate(new long[]{100, 250})
                .setLights(Color.YELLOW, 500, 5000)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContent(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setSound(Uri.parse("android.resource://" + cont.getPackageName() + "/" + R.raw.sonarsub))
                .setColor(Color.parseColor("#6200EE"));

        notificationManager.notify(notificationId, mBuilder.build());

    }

    private int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.ENGLISH).format(now));
        return id;
    }

    private void toast(String msg) {
        Toast toast= Toast.makeText(cont,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
