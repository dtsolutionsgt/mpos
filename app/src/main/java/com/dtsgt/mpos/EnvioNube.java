package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class EnvioNube extends PBase {

    private TextView lbl1,btnsend;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_envio_nube);

            super.InitBase();

            lbl1 = findViewById(R.id.textView322);lbl1.setText("");
            btnsend = findViewById(R.id.textView321);

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doSend(View view) {
        if (app.isOnWifi()==0) {
            lbl1.setText("¡No hay conexión a internet!");return;
        }

        idle=false;
        btnsend.setVisibility(View.INVISIBLE);
        lbl1.setText("Enviando base de datos.\n\nEspere, por favor . . .");

        sendDB();
    }

    public void doExit(View view) {
        if (idle) finish(); else toastcentlong("Espere, por favor . . .");
    }

    //endregion

    //region Main

    private void sendDB() {
        String dir= Environment.getExternalStorageDirectory()+"";

        try {
            File f1 = new File(dir + "/posdts.db");
            File f2 = new File(dir + "/posdts_"+gl.codigo_ruta+".db");
            File f3 = new File(dir + "/posdts_"+gl.codigo_ruta+".zip");
            FileUtils.copyFile(f1, f2);
            Uri uri = Uri.fromFile(f3);

            app.zip(dir+"/posdts_"+gl.codigo_ruta+".db",dir + "/posdts_"+gl.codigo_ruta+".zip");

            StorageReference ref = storageReference.child("dbmpos/posdts_"+gl.codigo_ruta+".zip");

            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public synchronized void run() {
                                    try {
                                        idle=true;
                                        msgExit();
                                    } catch (Exception e) {
                                        lbl1.setText("Error: "+e.getMessage());
                                    }

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)  {
                            String errmsg=e.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public synchronized void run() {
                                    idle=true;
                                    lbl1.setText("Error: "+errmsg);
                                }
                            });
                        }
                    });

        } catch (Exception e) {
            lbl1.setText("Error: "+e.getMessage()); idle=true;
        }

    }

    //endregion

    //region Dialogs

    private void msgExit() {
        try {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage("Envío completo");
            dialog.setCancelable(false);

            dialog.setNeutralButton("OK", (dialog1, which) -> {
                finish();
            });
            dialog.show();
        } catch (Exception ex) {
            toast(ex.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Activity Events

    @Override
    public void onBackPressed() {
        if (idle) {
            super.onBackPressed();
        } else {
            toastcentlong("Espere, por favor . . .");
        }
    }

    //endregion

}