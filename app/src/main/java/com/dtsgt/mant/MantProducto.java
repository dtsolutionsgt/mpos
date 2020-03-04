package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_factorconvObj;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_nivelprecioObj;
import com.dtsgt.classes.clsP_prodprecioBL;
import com.dtsgt.classes.clsP_prodprecioObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import java.util.ArrayList;
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
import java.io.File;
import java.io.FileOutputStream;

public class MantProducto extends PBase {

    private ImageView imgstat,imgadd,imgfotoadd,imgfotodel;
    private EditText txt1,txt2,txt3,txt4,txt5,txt6;
    private TextView lblfotoadd,btnprec;
    private Spinner spin,spin1,spin2,spin3,spinp;
    private CheckBox cbDec;
    private RadioButton rb1,rb2,rb3;

    private clsP_productoObj holder;
    private clsClasses.clsP_producto item=clsCls.new clsP_producto();
    public  ArrayList<clsClasses.clsP_nivelpreciolist> precios = new ArrayList<clsClasses.clsP_nivelpreciolist>();

    private ArrayList<String> spincode,code1,code2,code3,spinlist,list1,list2,list3,listp;

    private String id;
    private int precpos=0;
    private boolean newitem=false;

    private ImageView img1;
    private String idfoto,signfile;
    private int TAKE_PHOTO_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_producto);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.editText6);
        txt4 = (EditText) findViewById(R.id.editText13);
        txt5 = (EditText) findViewById(R.id.editText11);
        txt6 = (EditText) findViewById(R.id.editText15);
        lblfotoadd = (TextView) findViewById(R.id.lblBU3);
        btnprec = (TextView) findViewById(R.id.textView123);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgadd = (ImageView) findViewById(R.id.imgImg2);
        imgfotoadd = (ImageView) findViewById(R.id.imageView44);
        imgfotodel = (ImageView) findViewById(R.id.imageView45);
        spin = (Spinner) findViewById(R.id.spinner10);
        spin1 = (Spinner) findViewById(R.id.spinner14);
        spin2 = (Spinner) findViewById(R.id.spinner13);
        spin3 = (Spinner) findViewById(R.id.spinner11);
        spinp = (Spinner) findViewById(R.id.spinner15);
        cbDec = (CheckBox) findViewById(R.id.checkBox9);
        rb1 = (RadioButton) findViewById(R.id.radioButton);
        rb2 = (RadioButton) findViewById(R.id.radioButton3);
        rb3 = (RadioButton) findViewById(R.id.radioButton4);
        img1 = (ImageView) findViewById(R.id.imageView45);

        holder =new clsP_productoObj(this,Con,db);

        spincode=new ArrayList<String>();spinlist=new ArrayList<String>();
        code1=new ArrayList<String>();list1=new ArrayList<String>();
        code2=new ArrayList<String>();list2=new ArrayList<String>();
        code3=new ArrayList<String>();list3=new ArrayList<String>();
        listp=new ArrayList<String>();

        id=gl.gcods;
        idfoto=id;

        showImage();
        setHandlers();

        buildPrices();
        if (id.isEmpty()) newItem(); else loadItem();
        showPrices();

        if (gl.grantaccess) {
            if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
                imgstat.setVisibility(View.INVISIBLE);
                imgfotoadd.setVisibility(View.INVISIBLE);
                imgfotodel.setVisibility(View.INVISIBLE);
                lblfotoadd.setVisibility(View.INVISIBLE);
                btnprec.setVisibility(View.INVISIBLE);
            }
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

    public void doPrice(View view) {
        updatePrice();
    }

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = spincode.get(position);
                    item.linea = scod;
                 } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                 try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = code1.get(position);
                    item.imp1 = Integer.parseInt(scod);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = code2.get(position);
                    item.imp2 = Integer.parseInt(scod);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = code3.get(position);
                    item.imp3 = Integer.parseInt(scod);
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spinp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    precpos = position;
                    selectPrice();
                } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        txt1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.codigo = txt1.getText().toString().trim();
            }
        });

    }

    //endregion

    //region Main


    private void resizeFoto() {
        try {

            String fname = Environment.getExternalStorageDirectory() + "/RoadFotos/" + idfoto + ".jpg";
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

            //idfoto=item.codigo;
            signfile= Environment.getExternalStorageDirectory()+"/RoadFotos/"+idfoto+".jpg";
            //callback=1;

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File URLfoto = new File(Environment.getExternalStorageDirectory() + "/RoadFotos/" + idfoto + ".jpg");
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
            prodimg = Environment.getExternalStorageDirectory() + "/RoadFotos/" + idfoto + ".png";
            file = new File(prodimg);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                img1.setImageBitmap(bmImg);
            } else {
                prodimg = Environment.getExternalStorageDirectory() + "/RoadFotos/" + idfoto + ".jpg";
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

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();

            loadPrices();
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
        item.tipo="P";
        item.linea="";
        item.sublinea="1";
        item.empresa=gl.emp;
        item.marca="1";
        item.codbarra="";
        item.desccorta="";
        item.desclarga="";
        item.costo=0;
        item.factorconv=1;
        item.unidbas="UNI";
        item.unidmed="";
        item.unimedfact=0;// cantidad minima
        item.unigra="";
        item.unigrafact=0;
        item.descuento="S";
        item.bonificacion="S";
        item.imp1=0;
        item.imp2=0;
        item.imp3=0;
        item.vencomp="";
        item.devol="N";
        item.ofrecer="N";
        item.rentab="N";
        item.descmax="N";
        item.peso_promedio=1;
        item.modif_precio=0;
        item.imagen="";
        item.video="";
        item.venta_por_peso=0;
        item.es_prod_barra=0;
        item.unid_inv="UNI";
        item.venta_por_paquete=0;
        item.venta_por_factor_conv=0;
        item.es_serializado=0;
        item.param_caducidad=0;
        item.producto_padre="";
        item.factor_padre=1;
        item.tiene_inv=0;
        item.tiene_vineta_o_tubo=0;
        item.precio_vineta_o_tubo=0;
        item.es_vendible=0;
        item.unigrasap=0; // 1 -permite decimales en cantidad
        item.um_salida="UNI";
        item.activo=1;

        showItem();

    }

    private void addItem() {
        clsP_factorconvObj fact  =new clsP_factorconvObj(this,Con,db);
        clsClasses.clsP_factorconv fitem= clsCls.new clsP_factorconv();

        try {
            db.beginTransaction();

            holder.add(item);

            fact.delete(item.codigo);

            fitem.producto=item.codigo;
            fitem.unidadsuperior = item.unidbas;
            fitem.factorconversion = 1;
            fitem.unidadminima =item.unidbas;

            fact.add(fitem);

            savePrices();

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.gcods = "" + item.codigo;
            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {

            db.beginTransaction();

            holder.update(item);
            savePrices();

            db.setTransactionSuccessful();
            db.endTransaction();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Precio

    private void buildPrices() {
        clsP_nivelprecioObj niv  =new clsP_nivelprecioObj(this,Con,db);
        clsClasses.clsP_nivelpreciolist pitem;

        precios.clear();

        try {

            niv.fill("WHERE Activo=1 ORDER BY Codigo");

            for (int i = 0; i <niv.count; i++) {

                pitem= clsCls.new clsP_nivelpreciolist();

                pitem.codigo="";
                pitem.nivel=niv.items.get(i).codigo;
                pitem.nombre=niv.items.get(i).nombre;
                pitem.precio=0;
                pitem.unidadmedida="";

                precios.add(pitem);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void loadPrices() {
        try {
            clsP_prodprecioObj prec  = new clsP_prodprecioObj(this,Con,db);
            clsClasses.clsP_prodprecio pitem;

            for (int i = 0; i <precios.size(); i++) {
                prec.fill("WHERE CODIGO='"+item.codigo+"' AND NIVEL="+precios.get(i).nivel);
                if (prec.count>0) precios.get(i).precio=prec.first().precio;
             }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean showPrices() {
        clsP_impuestoObj imp =new clsP_impuestoObj(this,Con,db);
        int selidx=0;
        String scod;

        listp.clear();

        try {
            if (precios.size()==0) {
                msgAskReturn("Lista de precios está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <precios.size(); i++) {
                 listp.add(precios.get(i).nombre);
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listp);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinp.setAdapter(dataAdapter);

        try {
            spinp.setSelection(0);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        precpos=0;
        selectPrice();

        return true;
    }

    private void selectPrice() {
        try {
            txt5.setText(mu.frmdecno(precios.get(precpos).precio));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void savePrices() {
        clsP_prodprecioObj prec  = new clsP_prodprecioObj(this,Con,db);
        clsP_prodprecioBL prodprec = new clsP_prodprecioBL(prec);
        clsClasses.clsP_prodprecio pitem;

        prodprec.delete(item.codigo);

        for (int i = 0; i <precios.size(); i++) {

            pitem= clsCls.new clsP_prodprecio();

            pitem.codigo=item.codigo;
            pitem.nivel=precios.get(i).nivel;
            pitem.unidadmedida=item.unidbas;
            pitem.precio=precios.get(i).precio;

            prec.add(pitem);
        }
    }

    private void updatePrice() {

        try {
            if (txt5.getText().toString().isEmpty()) txt5.setText("0");
            double pr=Double.parseDouble(txt5.getText().toString());
            if (pr<0) throw new Exception();

            precios.get(precpos).precio=pr;
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        } catch (Exception e) {
            msgbox("Precio incorrecto");
        }
    }

    //endregion

    //region Aux

    private void showItem() {

        txt1.setText(item.codigo);
        txt2.setText(item.desclarga);
        txt3.setText(item.codbarra);
        txt4.setText(item.unidbas);
        txt6.setText(mu.frmdecno(item.costo));

        cbDec.setChecked(item.unigrasap==1);

        if (!fillSpinner(item.linea)) return;
        if (!showPrices()) return;
        if (!fillSpin1(""+(int) item.imp1)) return;
        fillSpin2(""+(int) item.imp2);
        fillSpin3(""+(int) item.imp3);

        rb1.setChecked(true);
        if (item.tipo.equalsIgnoreCase("S")) rb2.setChecked(true);
        if (item.tipo.equalsIgnoreCase("M")) rb3.setChecked(true);
    }

    private boolean validaDatos() {
        String ss;
        double dval;

        try {

            if (newitem) {
                ss=txt1.getText().toString();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill("WHERE CODIGO='"+ss+"'");
                if (holder.count>0) {
                    msgbox("¡Código ya existe!\n"+holder.first().desclarga);return false;
                }
                item.codigo=ss;
            }

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                if (ss.length()>50) ss=ss.substring(0,50);
                item.desclarga=ss;
                if (ss.length()>25) ss=ss.substring(0,25);
                item.desccorta=ss;
            }

            ss=txt6.getText().toString();
            if (ss.isEmpty()) ss="0";

            try {
               dval=Double.parseDouble(ss);
               if (dval<0) throw new Exception();
               item.costo=dval;
            } catch (Exception ex) {
                msgbox("¡Costo incorrecto!");return false;
            }

            item.tipo="P";
            if (rb2.isChecked()) item.tipo="S";
            if (rb3.isChecked()) item.tipo="M";

            if (cbDec.isChecked()) item.unigrasap=1;else item.unigrasap=0;

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean fillSpinner(String selid){
        clsP_lineaObj lineas =new clsP_lineaObj(this,Con,db);
        int selidx=0;
        String scod;

        spincode.clear();spinlist.clear();

        try {
            lineas.fill(" WHERE (Activo=1) OR (Codigo='"+selid+"') ORDER BY Nombre");
            if (lineas.count==0) {
                msgAskReturn("Lista de familias está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <lineas.count; i++) {
                scod=lineas.items.get(i).codigo;
                spincode.add(scod);
                spinlist.add(lineas.items.get(i).nombre);
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.linea=scod;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        try {
            spin.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }

    private boolean fillSpin1(String selid){
        clsP_impuestoObj imp =new clsP_impuestoObj(this,Con,db);
        int selidx=0;
        String scod;

        code1.clear();list1.clear();

        try {
            imp.fill(" WHERE (Activo=1) OR (Codigo="+selid+") ORDER BY Valor");
            if (imp.count==0) {
                msgAskReturn("Lista de impuestos está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <imp.count; i++) {
                scod=""+imp.items.get(i).codigo;
                code1.add(scod);
                list1.add(""+imp.items.get(i).valor+" %");
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.imp1=Integer.parseInt(scod);
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin1.setAdapter(dataAdapter);

        try {
            spin1.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;
    }

    private void fillSpin2(String selid){
        clsP_impuestoObj imp =new clsP_impuestoObj(this,Con,db);
        int selidx=0;
        String scod;

        code2.clear();list2.clear();

        try {
            imp.fill(" WHERE (Activo=1) OR (Codigo="+selid+") ORDER BY Valor");
            if (imp.count==0) {
                msgAskReturn("Lista de impuestos está vacia, no se puede continuar");return;
            }

            for (int i = 0; i <imp.count; i++) {
                scod=""+imp.items.get(i).codigo;
                code2.add(scod);
                list2.add(""+imp.items.get(i).valor+" %");
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.imp2=Integer.parseInt(scod);
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin2.setAdapter(dataAdapter);

        try {
            spin2.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void fillSpin3(String selid){
        clsP_impuestoObj imp =new clsP_impuestoObj(this,Con,db);
        int selidx=0;
        String scod;

        code3.clear();list3.clear();

        try {
            imp.fill(" WHERE (Activo=1) OR (Codigo="+selid+") ORDER BY Valor");
            if (imp.count==0) {
                msgAskReturn("Lista de impuestos está vacia, no se puede continuar");return;
            }

            for (int i = 0; i <imp.count; i++) {
                scod=""+imp.items.get(i).codigo;
                code3.add(scod);
                list3.add(""+imp.items.get(i).valor+" %");
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.imp3=Integer.parseInt(scod);
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list3);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin3.setAdapter(dataAdapter);

        try {
            spin3.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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

    private void msgAskReturn(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Familias");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                  finish();
            }
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
                    /*codCamera =  2;
                    showCamera();*/
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
