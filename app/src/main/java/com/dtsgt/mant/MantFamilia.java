package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_estacionObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_linea_estacionObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.io.File;
import java.io.FileOutputStream;

public class MantFamilia extends PBase {

    private ImageView imgstat,img1,imgadd,imgfotoadd,imgfotodel;
    private EditText txt1,txt2;
    private TextView lblfoto,lblest;
    private RelativeLayout relest;

    private clsP_lineaObj holder;
    public clsClasses.clsP_linea item=clsCls.new clsP_linea();
    private clsP_estacionObj P_estacionObj;
    private clsP_linea_estacionObj P_linea_estacionObj;
    public clsClasses.clsP_linea_estacion estacion=clsCls.new clsP_linea_estacion();

    private String id,idfoto,signfile;
    private boolean newitem=false;
    private int TAKE_PHOTO_CODE = 0,idestacion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_familia);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        lblfoto = (TextView) findViewById(R.id.lblBU);
        lblest =findViewById(R.id.textView219);

        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgfotoadd  = (ImageView) findViewById(R.id.imageView40);
        imgfotodel = (ImageView) findViewById(R.id.imageView41);
        img1 = (ImageView) findViewById(R.id.imageView41);
        imgadd = (ImageView) findViewById(R.id.imgImg2);
        relest=findViewById(R.id.relestacion);

        holder =new clsP_lineaObj(this,Con,db);
        P_estacionObj=new clsP_estacionObj(this,Con,db);
        P_linea_estacionObj=new clsP_linea_estacionObj(this,Con,db);

        id=gl.gcods;
        idfoto=id;
        if (id.isEmpty()) newItem(); else loadItem();
        if (!gl.peRest) relest.setVisibility(View.INVISIBLE);

        showImage();
        setHandlers();

        if (gl.peMCent) {
            //if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
                imgstat.setVisibility(View.INVISIBLE);
                lblfoto.setVisibility(View.INVISIBLE);
                imgfotoadd.setVisibility(View.INVISIBLE);
                imgfotodel.setVisibility(View.INVISIBLE);
            //}
        }
    }

    //region Events

    private void setHandlers() {

        txt1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.codigo = txt1.getText().toString().trim();
            }
        });
    }

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    public void doStatus(View view) {
        if (item.activo==1) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doEstacion(View view) {
        listaEstaciones();
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void resizeFoto() {
        try {

            String fname = Environment.getExternalStorageDirectory() + "/mPosFotos/Familia/" + idfoto + ".jpg";
            File file = new File(fname);

            Bitmap bitmap = BitmapFactory.decodeFile(fname);
            bitmap=mu.scaleBitmap(bitmap,640,360);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,out);

            out.flush();
            out.close();
        } catch (Exception e) {
            msgbox("No se logró procesar la foto. Por favor tómela de nuevo.");
        }
    }

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();

            P_linea_estacionObj.fill("WHERE codigo_linea="+item.codigo_linea);
            if (P_linea_estacionObj.count>0) {
                estacion=P_linea_estacionObj.first();
                idestacion=estacion.codigo_estacion;
            } else {
                idestacion=0;
                estacion.codigo_linea_estacion=P_linea_estacionObj.newID("SELECT MAX(codigo_linea_estacion) FROM P_linea_estacion");
                estacion.codigo_linea=item.codigo_linea;
                estacion.codigo_sucursal=gl.tienda;
                estacion.empresa=gl.emp;
                estacion.codigo_estacion=idestacion;
            }

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.activo==1) {
                imgstat.setImageResource(R.drawable.delete_64);
            } else {
                imgstat.setImageResource(R.drawable.mas);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.codigo="";
        item.marca="1";
        item.nombre="";
        item.activo=1;
        item.imagen="";

        idestacion=0;

        estacion.codigo_linea_estacion=P_linea_estacionObj.newID("SELECT MAX(codigo_linea_estacion) FROM P_linea_estacion");
        estacion.codigo_linea=item.codigo_linea;
        estacion.codigo_sucursal=gl.tienda;
        estacion.empresa=gl.emp;
        estacion.codigo_estacion=idestacion;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);

            estacion.codigo_estacion=idestacion;
            P_linea_estacionObj.add(estacion);

            gl.gcods=""+item.codigo;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            holder.update(item);

            estacion.codigo_estacion=idestacion;
            try {
                P_linea_estacionObj.add(estacion);
            } catch (Exception e) {
                P_linea_estacionObj.update(estacion);
            }

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void camera(View view){
        try{
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                msgbox("El dispositivo no soporta toma de foto");return;
            }

            if(item.codigo.isEmpty()){
                msgbox("Debe agregar un codigo de producto para tomar la foto");return;
            }

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            //idfoto=item.codigo;
            signfile= Environment.getExternalStorageDirectory()+"/mPosFotos/Familia/"+idfoto+".jpg";
            //callback=1;

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File URLfoto = new File(Environment.getExternalStorageDirectory() + "/mPosFotos/Familia/" + idfoto + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(URLfoto));
            startActivityForResult(cameraIntent,TAKE_PHOTO_CODE);


        }catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox("Error en camera: "+e.getMessage());
        }
    }

    public void showImage(){
        String prodimg;
        File file;

        try {
            prodimg = Environment.getExternalStorageDirectory() + "/mPosFotos/Familia/" + idfoto + ".png";
            file = new File(prodimg);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                img1.setImageBitmap(bmImg);
            } else {
                prodimg = Environment.getExternalStorageDirectory() + "/mPosFotos/Familia/" + idfoto + ".jpg";
                file = new File(prodimg);
                if (file.exists()) {
                    Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                    img1.setImageBitmap(bmImg);
                }
            }
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(item.codigo);
        txt2.setText(item.nombre);
        lblest.setText(nombreEstacion());
    }

    private boolean validaDatos() {
        String ss;

        try {

            if (newitem) {
                ss=txt1.getText().toString();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill("WHERE CODIGO='"+ss+"'");
                if (holder.count>0) {
                    msgbox("¡Código ya existe!\n"+holder.first().nombre);return false;
                }

                item.codigo=ss;
            }

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                item.nombre=ss;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private String nombreEstacion() {
        try {
            P_estacionObj.fill("WHERE CODIGO_ESTACION="+idestacion);
            return P_estacionObj.first().nombre;
        } catch (Exception e) {
            return "";
        }
    }

    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskUpdate(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskStatus(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (item.activo==1) {
                   item.activo=0;
                } else {
                   item.activo=1;
                };
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void listaEstaciones() {
        final AlertDialog Dialog;

        try {
            P_estacionObj.fill("ORDER BY NOMBRE");

            final String[] selitems = new String[P_estacionObj.count];

            for (int i = 0; i <P_estacionObj.count; i++) {
                selitems[i]=P_estacionObj.items.get(i).nombre;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Estacion de cocina");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    idestacion=P_estacionObj.items.get(item).codigo_estacion;
                    lblest.setText(nombreEstacion());
                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            menudlg.setPositiveButton("Limpiar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    idestacion=0;
                    lblest.setText(nombreEstacion());
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            holder.reconnect(Con,db);
            P_estacionObj.reconnect(Con,db);
            P_linea_estacionObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == TAKE_PHOTO_CODE) {
                if (resultCode == RESULT_OK) {
                    toast("Foto OK.");
                    resizeFoto();
                    showImage();
                } else {
                    Toast.makeText(this,"SIN FOTO.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }
    }

    //endregion

}
