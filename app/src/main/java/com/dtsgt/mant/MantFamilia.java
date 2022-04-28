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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_estacionObj;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_linea_estacionObj;
import com.dtsgt.classes.clsP_linea_impresoraObj;
import com.dtsgt.ladapt.LA_P_usopcion;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MantFamilia extends PBase {

    private ImageView imgstat,img1,imgadd,imgfotoadd,imgfotodel;
    private EditText txt1,txt2;
    private ListView listView;

    private clsP_lineaObj holder;
    private clsP_linea_impresoraObj P_linea_impresoraObj;
    public clsClasses.clsP_linea item=clsCls.new clsP_linea();
    private ArrayList<clsClasses.clsP_usopcion> items= new ArrayList<clsClasses.clsP_usopcion>();

    private LA_P_usopcion adapter;

    private String id,idfoto,signfile;
    private boolean newitem=false;
    private int idlinea,TAKE_PHOTO_CODE = 0,idestacion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_familia);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);

        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgfotoadd  = (ImageView) findViewById(R.id.imageView40);
        imgfotodel = (ImageView) findViewById(R.id.imageView41);
        img1 = (ImageView) findViewById(R.id.imageView41);
        imgadd = (ImageView) findViewById(R.id.imgImg2);

        listView = (ListView) findViewById(R.id.listView1);

        holder =new clsP_lineaObj(this,Con,db);
        P_linea_impresoraObj=new clsP_linea_impresoraObj(this,Con,db);

        id=gl.gcods;
        idfoto=id;
        if (id.isEmpty()) newItem(); else loadItem();

        try {
            showImage();
            setHandlers();
            listItems();
        } catch (Exception e) {}

        if (gl.peMCent) {
            //if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.VISIBLE);
                imgstat.setVisibility(View.INVISIBLE);
                imgfotoadd.setVisibility(View.INVISIBLE);
                imgfotodel.setVisibility(View.INVISIBLE);
            //}
        }
    }

    //region Events

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

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

        txt1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.codigo = txt1.getText().toString().trim();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_usopcion item = (clsClasses.clsP_usopcion)lvObj;

                if (item.nombre.isEmpty()) item.nombre="x"; else item.nombre="";

                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();
            };
        });
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();
            idlinea=item.codigo_linea;

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

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            updatePrinters();

            gl.gcods=""+item.codigo;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            holder.update(item);
            updatePrinters();

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listItems() {
        ArrayList<Integer> opts=new ArrayList<Integer>();
        clsP_impresoraObj P_impresoraObj=new clsP_impresoraObj(this,Con,db);
        clsClasses.clsP_usopcion item;
        int opt;

        try {
            P_impresoraObj.fill("ORDER BY Nombre");

            for (int i = 0; i <P_impresoraObj.count; i++) {

                item=clsCls.new clsP_usopcion();

                item.codigo=P_impresoraObj.items.get(i).codigo_impresora;
                item.menugroup=P_impresoraObj.items.get(i).nombre;

                P_linea_impresoraObj.fill("WHERE (CODIGO_LINEA="+idlinea+") AND (CODIGO_IMPRESORA="+item.codigo+")");
                if (P_linea_impresoraObj.count>0) item.nombre="X";else item.nombre="";

                items.add(item);
            }

            adapter=new LA_P_usopcion(this,this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void updatePrinters() {
        clsClasses.clsP_linea_impresora item;

        try {
            db.beginTransaction();

            int newid=P_linea_impresoraObj.newID("SELECT MAX(codigo_linea_impresora) FROM P_linea_impresora");

            db.execSQL("DELETE FROM P_linea_impresora WHERE CODIGO_LINEA="+idlinea);

            for (int i = 0; i <items.size(); i++) {

                if (items.get(i).nombre.equalsIgnoreCase("X")) {

                    item = clsCls.new clsP_linea_impresora();

                    item.codigo_linea_impresora=newid;
                    item.codigo_linea=idlinea;
                    item.codigo_sucursal=gl.tienda;
                    item.empresa=gl.emp;
                    item.codigo_impresora=items.get(i).codigo;

                    P_linea_impresoraObj.add(item);newid++;
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(item.codigo);
        txt2.setText(item.nombre);
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

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            holder.reconnect(Con,db);
            P_linea_impresoraObj.reconnect(Con,db);
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
