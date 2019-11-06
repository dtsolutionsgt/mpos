package com.dtsgt.mant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Calendar;

public class MantCli extends PBase {

    private ImageView imgstat,imgfprint;
    private TextView lblDateCli,lblfprint;
    private EditText txt1,txt2,txt3,txt4,txt5,txt6;

    private clsP_clienteObj holder;
    private clsClasses.clsP_cliente item=clsCls.new clsP_cliente();

    public final Calendar c = Calendar.getInstance();
    private static final String BARRA = "/";
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    public int cyear, cmonth, cday;
    private long date=0;

    private String id, CERO="0";
    private boolean newitem=false;

    private ImageView img1,img2;
    private String idfoto,signfile;
    private int TAKE_PHOTO_CODE = 0;

    final int REQUEST_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_cli);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.txt3);
        txt4 = (EditText) findViewById(R.id.txt8);
        txt5 = (EditText) findViewById(R.id.txt9);
        txt6= (EditText) findViewById(R.id.txt11);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgfprint= (ImageView) findViewById(R.id.imageView51);
        lblDateCli = (TextView) findViewById(R.id.lblDateCli);
        lblfprint= (TextView) findViewById(R.id.textView147);
        img1 = (ImageView) findViewById(R.id.imageView43);
        img2 = (ImageView) findViewById(R.id.imageView50);

        holder =new clsP_clienteObj(this,Con,db);

        id=gl.gcods;

        idfoto=id;

        showImage();
        setHandlers();

        if (id.isEmpty()) newItem(); else loadItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == TAKE_PHOTO_CODE) {
                if (resultCode == RESULT_OK) {
                    toast("Foto OK.");
                    resizeFoto();
                    /*codCamera =  2;
                    showCamera();*/
                    showImage();
                } else {
                    Toast.makeText(this,"SIN FOTO.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        } catch (Exception e){
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
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
        if (item.bloqueado.equalsIgnoreCase("N")) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doEnroll(View view) {

        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/biomuu_erl.txt");
            if (file.exists()) file.delete();
        } catch (Exception e) {}

        try {
            String ss = txt2.getText().toString();if (ss.isEmpty()) ss="-";

            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.dts.uubio.uusample");
            intent.putExtra("method","1");
            intent.putExtra("param1",id);
            intent.putExtra("param2",ss);

            browse=1;
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    public void doCancelFPrint(View view) {
        msgAskDelPrint("Borrar la huella");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();

            showItem();

            txt1.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.bloqueado.equalsIgnoreCase("N")) {
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

        item.codigo=" ";
        item.nombre=" ";
        item.bloqueado ="N";
        item.tiponeg = "1";
        item.tipo = "1";
        item.subtipo = "1";
        item.canal = "1";
        item.subcanal = "1";
        item.nivelprecio = 1;
        item.mediapago = "1";
        item.limitecredito = 0;
        item.diacredito = 0;
        item.descuento = "S";
        item.bonificacion = "S";
        item.ultvisita = 0;
        item.impspec = 0;
        item.invtipo = "0";
        item.invequipo = "N";
        item.inv1 = "N";
        item.inv2 = "N";
        item.inv3 = "N";
        item.nit = " ";
        item.mensaje = "";
        item.email = " ";
        item.eservice =  " ";
        item.telefono =  " ";
        item.dirtipo = " ";
        item.direccion =" ";
        item.region =  " ";
        item.sucursal =  " ";
        item.municipio =  " ";
        item.ciudad =  " ";
        item.zona = 0;
        item.colonia =  " ";
        item.avenida =  " ";
        item.calle =  " ";
        item.numero =  " ";
        item.cartografico =  " ";
        item.coorx = 0;
        item.coory = 0;
        item.bodega =  " ";
        item.cod_pais =  " ";
        item.firmadig =  " ";
        item.codbarra =  " ";
        item.validacredito ="S";
        item.fact_vs_fact =  " ";
        item.chequepost =  " ";
        item.precio_estrategico =  "N";
        item.nombre_propietario =  " ";
        item.nombre_representante = " ";
        item.percepcion = 0;
        item.tipo_contribuyente = " ";
        item.id_despacho = 0;
        item.id_facturacion = 0;
        item.modif_precio = 0;

        showItem();
    }

    private void resizeFoto() {
        try {

            String fname = Environment.getExternalStorageDirectory() + "/RoadFotos/Cliente/" + idfoto + ".jpg";
            File file = new File(fname);

            Bitmap bitmap = BitmapFactory.decodeFile(fname);
            bitmap=mu.scaleBitmap(bitmap,640,360);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,out);

            out.flush();
            out.close();
        } catch (Exception e) {
            msgbox("No se logro procesar la foto. Por favor tome la de nuevo.");
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

            //=item.codigo;
            signfile= Environment.getExternalStorageDirectory()+"/RoadFotos/Cliente/"+idfoto+".jpg";
            //callback=1;

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File URLfoto = new File(Environment.getExternalStorageDirectory() + "/RoadFotos/Cliente/" + idfoto + ".jpg");
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
            prodimg = Environment.getExternalStorageDirectory() + "/RoadFotos/Cliente/" + idfoto + ".png";
            file = new File(prodimg);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                img1.setImageBitmap(bmImg);
            } else {
                prodimg = Environment.getExternalStorageDirectory() + "/RoadFotos/Cliente/" + idfoto + ".jpg";
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

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            holder.update(item);

            sql="UPDATE P_CLIENTE SET CODIGO='"+item.nit+"' WHERE CODIGO='"+item.codigo+"'";
            db.execSQL(sql);
            Toast.makeText(this, "Cliente Actualizado Correctamente", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    public void showDateDialog1(View view) {
        try{
            obtenerFecha();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void obtenerFecha(){
        try{

            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    final int mesActual = month + 1;
                    String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                    lblDateCli.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);
                    date = du.cfechaDesc(cyear, cmonth, cday);
                }
            },anio, mes, dia);

            recogerFecha.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void showItem() {
        String dateShow;

        txt1.setText(item.nit);
        txt2.setText(item.nombre);
        txt3.setText(item.direccion);
        txt4.setText(mu.frmint2((int) item.limitecredito));
        txt5.setText(item.email);
        txt6.setText(item.telefono);
        dateShow = du.univfechaReport(item.ultvisita);
        lblDateCli.setText(dateShow);
        date = item.ultvisita;

        muestraHuella();
    }

    private boolean validaDatos() {
        String ss;
        int ival;

        try {

            ss = txt1.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir NIT!");
                return false;
            }

            if(newitem){
                holder.fill("WHERE CODIGO='" + ss + "'");
                if (holder.count > 0) {
                    msgbox("¡NIT ya existe!\n" + holder.first().nombre);
                    return false;
                }
            }else {
                if(!item.codigo.equals(ss)){
                    holder.fill("WHERE NIT='" + ss + "'");
                    if(holder.count>0){
                        msgbox("¡NIT ya existe!\n" + holder.first().nombre);
                        return false;
                    }
                }

            }


            item.nit = txt1.getText().toString();
            if (newitem) item.codigo = item.nit;

            ss = txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");return false;
            } else {
                item.nombre=ss;
            }

            item.direccion=txt3.getText().toString();

            try {
                ss = txt4.getText().toString();if (ss.isEmpty()) ss="0";
                ival=Integer.parseInt(ss);
                if (ival<0) throw new Exception();
                item.limitecredito=ival;
            } catch (Exception e) {
                msgbox("¡Limite credito incorrecto!");return false;
            }

            try {
                ss = txt5.getText().toString();

                if (!ss.isEmpty()){
                    item.email = ss;
                }

            } catch (Exception e) {
                msgbox("Error al ingresar correo: "+e);return false;
            }

            try {
                ss = txt6.getText().toString();

                if (!ss.isEmpty()){
                    item.telefono = ss;
                }

            } catch (Exception e) {
                msgbox("Error al ingresar teléfono: "+e);return false;
            }

            if(date!=0){
                item.ultvisita= date;
                date=0;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void cargaHuella() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/biomuu_erl.txt");
            if (!file.exists()) return;

            file.delete();

            File huella = new File(Environment.getExternalStorageDirectory()+ "/fpuaudata/"+id+".uud");
            int size = (int) huella.length();
            byte[] fmtByte = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(huella));
                buf.read(fmtByte, 0, fmtByte.length);
                buf.close();
            } catch (Exception e) { }

            try {
                db.beginTransaction();

                db.execSQL("DELETE FROM FPrint WHERE CODIGO='"+id+"'");

                String sql = "INSERT INTO FPrint (EMPRESA,CODIGO,DEDO,IMAGE) VALUES(?,?,?,?)";
                SQLiteStatement insertStmt = db.compileStatement(sql);

                insertStmt.bindString(1,gl.emp);
                insertStmt.bindString(2,id);
                insertStmt.bindLong(3,1);
                insertStmt.bindBlob(4,fmtByte);

                insertStmt.executeInsert();

                db.setTransactionSuccessful();
                db.endTransaction();

                toast("Huella procesada");

            } catch (Exception e) {
                db.endTransaction();
                msgbox(e.getMessage());
            }

        } catch (Exception e) {
            //msgbox(e.getMessage());
        }

        muestraHuella();
    }

    private boolean tieneHuella() {
        Cursor dt;

        try {
            sql="SELECT CODIGO FROM FPrint WHERE CODIGO='"+id+"'";
            dt=Con.OpenDT(sql);
            return dt.getCount()>0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

    }

    private void muestraHuella() {
        if (tieneHuella()) {
            img2.setVisibility(View.VISIBLE);
            imgfprint.setVisibility(View.VISIBLE);
            lblfprint.setVisibility(View.VISIBLE);
        } else {
            img2.setVisibility(View.INVISIBLE);
            imgfprint.setVisibility(View.INVISIBLE);
            lblfprint.setVisibility(View.INVISIBLE);
        }
    }

    private void borraHuella() {
        try {
            db.execSQL("DELETE FROM FPrint WHERE CODIGO='"+id+"'");

            File huella = new File(Environment.getExternalStorageDirectory()+ "/fpuaudata/"+id+".uud");
            if (!huella.exists()) huella.delete();

            toast("Huella borrada");
            muestraHuella();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (item.bloqueado.equalsIgnoreCase("N")) {
                    item.bloqueado="S";
                } else {
                    item.bloqueado="N";
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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

    private void msgAskDelPrint(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Huella");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                borraHuella();
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
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        cargaHuella();
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    //endregion

}
