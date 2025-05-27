package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class InstalaVersion extends PBase {

    private TextView lbl1,btnsend;
    private ProgressBar pbar;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_instala_version);

            super.InitBase();

            lbl1 = findViewById(R.id.textView322);lbl1.setText("");
            btnsend = findViewById(R.id.textView321);
            pbar = findViewById(R.id.progressBar9);pbar.setVisibility(View.INVISIBLE);

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
        lbl1.setText("Actualizando versión.\nEspere, por favor . . .");
        pbar.setVisibility(View.VISIBLE);

        downloadVersion();
    }

    public void doAccess(View view) {
        startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
    }


    public void doExit(View view) {
        if (idle) finish(); else toastcentlong("Espere, por favor . . .");
    }

    //endregion

    //region Main

    private void downloadVersion() {
        String fbname,fname;
        File file;

        try {

            String dir= Environment.getExternalStorageDirectory()+"";
            fname=dir+"/mpos.apk";
            fbname="mpos550.apk";

            StorageReference ref = storageReference.child(fbname);

            file=new File(fname);
            Uri localfile = Uri.fromFile(file);

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String ss=uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pbar.setVisibility(View.INVISIBLE);
                }
            });

            ref.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    if (file.exists()) {
                        updateFile();
                    } else {
                        msgbox("No se logro descargar archivo.");pbar.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    msgbox("Error de descarga : \n"+exception.getMessage());pbar.setVisibility(View.INVISIBLE);
                }
            });
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateFile() {
        try {
            String dir= Environment.getExternalStorageDirectory()+"";
            String ffname=dir+"/mpos.apk";
            File fapk = new File(ffname);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Uri uri = FileProvider.getUriForFile(this,getPackageName()+".provider",fapk);

                Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri, getMimeType(uri));
                startActivity(install);
            }

            finish();
        } catch (Exception e) {
            String ss=e.getMessage();
            toastlong("Mpos actualizador \n"+ss);finish();
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

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

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