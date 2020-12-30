package com.dtsgt.mpos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.BaseDatosVersion;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_usuario_asistenciaObj;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Login;
import com.dtsgt.webservice.startPedidosImport;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends PBase {

    private ListView listView;
    private TextView lblRuta, lblRTit, lblVer, lblEmp, lblPass, lblKeyDP;
    private ImageView imgLogo, imgFel;
    private Spinner spin;

    private BaseDatosVersion dbVers;

    private LA_Login adapter;
    private ArrayList<clsClasses.clsMenu> mitems = new ArrayList<clsClasses.clsMenu>();
    private ArrayList<String> spincode=new ArrayList<String>();
    private ArrayList<String> spinlist=new ArrayList<String>();


    private clsKeybHandler khand;

    private boolean rutapos, scanning = false;
    private String cs1, cs2, cs3, barcode, epresult, usr, pwd;

    private String parVer = "3.2.48";

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            grantPermissions();

            //typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.inconsolata);

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    private void grantPermissions() {

        try {

            if (Build.VERSION.SDK_INT >= 20) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && checkCallingOrSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    startApplication();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WAKE_LOCK,
                                    Manifest.permission.READ_PHONE_STATE
                            }, 1);
                }
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void startApplication() {

        try {
            super.InitBase();

            this.setTitle("MPos");
            gl.parVer = parVer;

            lblRuta = (TextView) findViewById(R.id.lblCDisp);lblRuta.setText("");
            lblRTit = (TextView) findViewById(R.id.lblCUsed);lblRTit.setText("");
            lblVer = (TextView) findViewById(R.id.textView10);
            lblEmp = (TextView) findViewById(R.id.textView82);lblEmp.setText("");
            lblPass = (TextView) findViewById(R.id.lblPass);
            lblKeyDP = (TextView) findViewById(R.id.textView110);
            imgLogo = (ImageView) findViewById(R.id.imgNext);
            imgFel = (ImageView) findViewById(R.id.imgFel);

            listView = (ListView) findViewById(R.id.listView1);
            spin = (Spinner) findViewById(R.id.spinner22);

            lblVer.setText("Version " + gl.parVer);

            // DB VERSION
            dbVers = new BaseDatosVersion(this, db, Con);
            dbVers.update();

            setHandlers();

            khand = new clsKeybHandler(this, lblPass, lblKeyDP);
            khand.enable();

            gl.peMCent = false;

            iniciaPantalla();
            initSession();

            try {
                File file1 = new File(Environment.getExternalStorageDirectory(), "/debug.txt");
                if (file1.exists()) gl.debug = true;
                else gl.debug = false;
            } catch (Exception e) {
                gl.debug = false;
            }

            /*
            if (!validaLicencia()) {
                startActivity(new Intent(this, comWSLic.class));
                return;
            } else
            {
                supervisorRuta();
            } */

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                startApplication();
            } else {
                super.finish();
            }
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    //region Events

    public void comMan(View view) {
        accesoAdmin();
    }

    public void gotoMenu() {
        try {
            if (gl.rol != 4) {
                startActivity(new Intent(this,Menu.class));
            } else {
                if (gl.peRest) {
                    gl.idmesero=gl.codigo_vendedor;
                    gl.meserodir=true;
                    startActivity(new Intent(this,ResMesero.class));
                } else {
                    msgbox("No está activado modulo restaurante");
                }
            }
        } catch (Exception e) {
        }
    }

    public void doOrientation(View view) {
        showOrientMenu();
    }

    public void doLoginScreen(View view) {
        //usr = "1";
        //pwd = "1";
        //if (checkUser()) gotoMenu();
    }

    public void doRegister(View view) {
        /*
        try {
            startActivity(new Intent(this, comWSLic.class));
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
        */
    }

    public void doKey(View view) {
        String ss;
        ss = view.getTag().toString();

        khand.handleKey(ss);
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Falta contraseña");
            } else {
                if (khand.isValid) {
                    pwd = khand.val;
                    processLogIn();
                }
            }
        }
    }

    public void doFPTest(View view) {
        //startActivity(new Intent(this, FingPTest.class));
    }

    private void setHandlers() {

        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu) lvObj;

                        adapter.setSelectedIndex(position);
                        usr = item.Cod;

                    } catch (Exception e) {
                        addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
                        mu.msgbox(e.getMessage());
                    }
                }

                ;
            });

            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    try {
                        TextView spinlabel = (TextView) parentView.getChildAt(0);
                        spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                        spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                        usr = spincode.get(position);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    return;
                }

            });

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    //endregion

    //region Main

    private void initSession() {
        Cursor DT;
        String s, rn = "";
        String vCellCom = "";

        if (dbVacia()) {
            gl.emp = 3;
            gl.modoadmin = true;
            gl.autocom = 0;
            toastcent("¡La base de datos está vacia!");
            browse = 1;
            Intent intent = new Intent(MainActivity.this, WSRec.class);
            intent.putExtra("bd_vacia", true);
            startActivity(intent);
        }

        configBase();

        try {
            //#HS_20181122_1505 Se agrego el campo Impresion.
            sql = "SELECT CODIGO,NOMBRE,SUCURSAL, CODIGO_RUTA FROM P_RUTA";
            DT = Con.OpenDT(sql);

            if (DT.getCount() > 0) {
                DT.moveToFirst();
                gl.ruta = DT.getString(0);
                gl.rutanom = DT.getString(1);
                gl.vend = "1";
                gl.rutatipog = "V";
                gl.wsurl = "";
                gl.impresora = "";
                gl.sucur = DT.getString(2);
                gl.codigo_ruta = DT.getInt(3);

                vCellCom = "";
                gl.CellCom = false;

                rutapos = true;

            } else {
                gl.ruta = "";
                gl.rutanom = "";
                gl.vend = "";
                gl.rutatipog = "V";
                gl.wsurl = "http://192.168.1.1/wsAndr/wsAndr.asmx";
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        try {
            //#HS_20181120_1616 Se agrego el campo UNIDAD_MEDIDA_PESO.//campo INCIDENCIA_NO_LECTURA
            sql = " SELECT EMPRESA,NOMBRE,CLAVE,COD_PAIS FROM P_EMPRESA";
            DT = Con.OpenDT(sql);

            if (DT.getCount() > 0) {
                DT.moveToFirst();
                gl.emp = DT.getInt(0);
                gl.empnom = DT.getString(1);
                gl.devol = false;
                gl.usarpeso = false;
                gl.banderafindia = false;
                gl.umpeso = "";
                gl.incNoLectura = false; //#HS_20181211 Agregue campo incNoLectura para validacion en cliente.
                gl.depparc = false;
                gl.lotedf = "";
                gl.clave=DT.getString(2);
                gl.codigo_pais=DT.getString(3);

                if (DT!=null) DT.close();
            } else {
                gl.emp = 3;
                gl.devol = false;
                toast("¡No se pudo cargar configuración de la empresa!");
            }

        } catch (Exception e) {
            toast("¡No se pudo cargar configuración de la empresa!");
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        gl.vendnom = "Vendedor 1";

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/SyncFold");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/mPosFotos");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/mPosFotos/Familia");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/mPosFotos/Familia");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/mPosFotos/Cliente");
            directory.mkdirs();
        } catch (Exception e) {
        }

        //Id de Dispositivo
        gl.deviceId = androidid();
        gl.devicename = getLocalBluetoothName();

        try {
            AppMethods app = new AppMethods(this, gl, Con, db);
            app.parametrosExtra();

            mu.curr=gl.peMon;
        } catch (Exception e) {
            addlog(new Object() { }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(e.getMessage());
        }

        if (!gl.peFEL.equals("SIN FEL") && !gl.peFEL.isEmpty()) {
            imgFel.setVisibility(View.VISIBLE);
        }else{
            imgFel.setVisibility(View.GONE);
        }

        configBase();

        llenaUsuarios();

        if (gl.pePedidos) {
            String params=gl.wsurl+"#"+gl.emp+"#"+gl.tienda;
            startPedidosImport.startService(this,params);
            //toasttop("Captura de ordenes activada");
        }

    }

    private void processLogIn() {

        /*/
        if (!validaLicencia()) {
             mu.msgbox("¡Licencia invalida!");
            startActivity(new Intent(this,comWSLic.class));
            return;
        }
        */

        if (checkUser()) {
            logUser();
            gotoMenu();
        }

    }

    private boolean checkUser() {
        Cursor DT;
        String dpwd;

        configBase();

        try {

            gl.idmesero=0;

            if (usr.isEmpty()) {

                mu.msgbox("Usuario incorrecto.");
                return false;
            }
            if (pwd.isEmpty()) {
                mu.msgbox("Contraseña incorrecta.");
                return false;
            }

            sql = "SELECT NOMBRE,CLAVE,NIVEL,NIVELPRECIO,CODIGO_VENDEDOR FROM VENDEDORES WHERE CODIGO='" + usr + "'  COLLATE NOCASE";
            DT = Con.OpenDT(sql);

            if (DT.getCount() == 0) {
                mu.msgbox("Usuario incorrecto !");
                return false;
            }

            DT.moveToFirst();
            dpwd = DT.getString(1);
            if (!pwd.equalsIgnoreCase(dpwd)) {
                mu.msgbox("Contraseña incorrecta!");
                return false;
            }

            gl.nivel = gl.nivel_sucursal;
            gl.rol = DT.getInt(2);

            //#CKFK 20200517 if (gl.caja.isEmpty() || gl.tienda==0) {
            if (gl.codigo_ruta == 0 || gl.tienda==0) {
                if (gl.rol == 3) {
                    browse=2;
                    startActivity(new Intent(MainActivity.this, ConfigCaja.class));
                    return false;
                } else {
                    toastlong("No está configurada la caja. Informe al gerente.");
                    browse=2;
                    accesoAdmin();
                    return false;
                }
            } else {
                gl.modoinicial = false;
            }

            sql = "SELECT NOMBRE,CLAVE,NIVEL,NIVELPRECIO, CODIGO_VENDEDOR FROM VENDEDORES " +
                    "WHERE (CODIGO='" + usr + "') AND (RUTA='" + gl.codigo_ruta + "') COLLATE NOCASE";
            DT = Con.OpenDT(sql);

            if (gl.rol != 3) {
                if (DT.getCount() == 0) {
                    mu.msgbox("¡El usuario no tiene permiso de ingreso para " + gl.tiendanom + "!");
                    return false;
                }
            }

            gl.vendnom=DT.getString(0);
            gl.vend=usr;
            gl.codigo_vendedor=DT.getInt(4);
            gl.vnivprec=DT.getInt(3);

            khand.clear();
            khand.enable();

            if (DT!=null) DT.close();

            return true;

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            return false;
        }

    }

    private void logUser() {
        long ff=du.getActDate();

        try {

            clsD_usuario_asistenciaObj D_usuario_asistenciaObj=new clsD_usuario_asistenciaObj(this,Con,db);
            D_usuario_asistenciaObj.fill("WHERE (CODIGO_VENDEDOR="+gl.codigo_vendedor+") AND (FECHA="+ff+")");
            if (D_usuario_asistenciaObj.count>0) return;

            clsClasses.clsD_usuario_asistencia item = clsCls.new clsD_usuario_asistencia();

            item.codigo_asistencia=D_usuario_asistenciaObj.newID("SELECT MAX(CODIGO_ASISTENCIA) FROM D_usuario_asistencia");
            item.empresa=gl.emp;
            item.codigo_sucursal=gl.tienda;
            item.codigo_vendedor=gl.codigo_vendedor;
            item.fecha=ff;
            item.inicio=du.getActDateTime();
            item.fin=0;
            item.bandera=0;

            D_usuario_asistenciaObj.add(item);

        } catch (Exception e) {

        }
    }

    //endregion

    //region Orientacion

    private void iniciaPantalla() {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MPos", 0);
            SharedPreferences.Editor editor = pref.edit();

            try {
                gl.pelTablet=pref.getBoolean("pelTablet", true);
            } catch (Exception e) {
                gl.pelTablet=true;
            }

            if (gl.pelTablet) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                listView.setVisibility(View.VISIBLE);
                spin.setVisibility(View.INVISIBLE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                listView.setVisibility(View.INVISIBLE);
                spin.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void guardaPantalla(boolean istablet) {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MPos", 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.putBoolean("pelTablet", istablet);

            editor.commit();
            iniciaPantalla();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void accesoAdmin() {

        if (!tieneTiendaCaja()) return;

        try {
            //AlertDialog.Builder alert = new AlertDialog.Builder(this);

            ExDialog alert = new ExDialog(this);

            alert.setTitle("Contraseña de administrador");//	alert.setMessage("Serial");

            final EditText input = new EditText(this);
            alert.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText("");
            input.requestFocus();
            showkeyb();

            alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        String s = input.getText().toString();
                        if (s.equalsIgnoreCase("1965")) {
                            startActivity(new Intent(MainActivity.this, ConfigCaja.class));
                        } else {
                            mu.msgbox("Contraseña incorrecta");
                            return;
                        }

                    } catch (Exception e) {
                        addlog(new Object() {
                        }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
                    }
                }
            });

            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.show();

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private boolean dbVacia() {
        Cursor dt;

        try {
            sql = "SELECT CODIGO FROM P_RUTA";
            dt = Con.OpenDT(sql);

            return dt.getCount() == 0;

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            return true;
        }
    }

    private boolean validaLicencia() {
        CryptUtil cu = new CryptUtil();
        Cursor dt;
        String lic, lickey, licruta, rutaencrypt;
        Integer msgLic = 0;

        try {
            lickey = cu.encrypt(gl.deviceId);
            rutaencrypt = cu.encrypt(gl.ruta);

            sql = "SELECT lic, licparam FROM Params";
            dt = Con.OpenDT(sql);
            dt.moveToFirst();
            lic = dt.getString(0);
            licruta = dt.getString(1);


            if (dt!=null) dt.close();

            if (mu.emptystr(lic)) {
                toastlong("El dispositivo no tiene licencia válida de handheld");
                return false;
            }

            if (mu.emptystr(licruta)) {
                toastlong("El dispositivo no tiene licencia válida de ruta");
                return false;
            }


            if (lic.equalsIgnoreCase(lickey) && licruta.equalsIgnoreCase(rutaencrypt)) {
                return true;
            }

            if (!lic.equalsIgnoreCase(lickey) && !licruta.equalsIgnoreCase(rutaencrypt)) {
                toastlong("El dispositivo no tiene licencia válida de handheld, ni de ruta");
                return false;
            }

            if (!lic.equalsIgnoreCase(lickey)) {
                toastlong("El dispositivo no tiene licencia válida de handheld");
                return false;
            }

            if (!licruta.equalsIgnoreCase(rutaencrypt)) {
                toastlong("El dispositivo no tiene licencia válida de ruta");
                return false;
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            mu.msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " : " + e.getMessage());
        }

        return false;

    }

    @SuppressLint("MissingPermission")
    private String androidid() {
        String uniqueID = "";
        try {

            TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            uniqueID = tm.getDeviceId();

            if (uniqueID == null) {
                uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            }

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            uniqueID = "0000000000";
        }

        return uniqueID;
    }

    public String getLocalBluetoothName() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            return "";
        } else {
            return mBluetoothAdapter.getName();
        }
    }

    public void configBase() {
        Cursor DT;

        gl.tienda = 0;
        gl.caja = "";
        gl.tiendanom = "";
        gl.cajanom = "";

        try {
            sql = "SELECT url,sucursal,ruta FROM Params";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            gl.urlglob = DT.getString(0);
            gl.tienda = DT.getInt(1);
            gl.codigo_ruta = DT.getInt(2);//gl.caja #CKFK 20200516 lo cambié porque debemos trabajar con el codigo_ruta4

            if (gl.tienda!=0) {
                try {
                    sql = "SELECT DESCRIPCION FROM P_SUCURSAL WHERE CODIGO_SUCURSAL='" + gl.tienda + "'";
                    DT = Con.OpenDT(sql);
                    DT.moveToFirst();
                    gl.tiendanom = DT.getString(0);
                } catch (Exception e) {
                    gl.tiendanom = "";
                }
            }

            //#CKFK 20200516 if (!gl.caja.isEmpty()) {
            if (gl.codigo_ruta !=0 ) {
                try {
                    sql = "SELECT NOMBRE FROM P_RUTA WHERE CODIGO_RUTA='" + gl.codigo_ruta + "'";
                    DT = Con.OpenDT(sql);
                    DT.moveToFirst();
                    gl.cajanom = DT.getString(0);

                } catch (Exception e) {
                    gl.cajanom = "";
                }
            }

        } catch (Exception e) {
            gl.tiendanom = "";
            gl.cajanom = "";
        }

        lblRTit.setText(gl.tiendanom);
        lblRuta.setText(gl.cajanom);
        lblEmp.setText(gl.empnom);

        app.getURL();



        try {
            String emplogo = Environment.getExternalStorageDirectory() + "/mPosFotos/" + "/mposlogo.png";
            File file = new File(emplogo);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(emplogo);
                imgLogo.setImageBitmap(bmImg);
            }
        } catch (Exception e) {}

        try {
            String orddir=Environment.getExternalStorageDirectory().getPath() + "/mposordser";
            File directory = new File(orddir);
            directory.mkdirs();
        } catch (Exception e) {}

        try {
            String errdir=Environment.getExternalStorageDirectory().getPath() + "/mposordser/error";
            File directory = new File(errdir);
            directory.mkdirs();
        } catch (Exception e) {}

    }

    public boolean tieneTiendaCaja() {

        Cursor DT;

        try {

            sql = "SELECT DESCRIPCION FROM P_SUCURSAL";
            DT = Con.OpenDT(sql);

            if (DT.getCount() == 0) {
                msgbox("¡No se puede continuar, no está definida ninguna tienda!");
                return false;
            }

            sql = "SELECT NOMBRE FROM P_RUTA";
            DT = Con.OpenDT(sql);

            if (DT.getCount() == 0) {
                msgbox("¡No se puede continuar, no está definida ninguna caja!");
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void llenaUsuarios() {
        clsVendedoresObj VendedoresObj = new clsVendedoresObj(this, Con, db);
        clsClasses.clsMenu item;

        usr = "";pwd = "";
        spincode.clear();spinlist.clear();

        try {

            mitems.clear();

            if (gl.codigo_ruta ==0){
                VendedoresObj.fill("ORDER BY CODIGO_VENDEDOR");
            } else {
                VendedoresObj.fill("WHERE RUTA = " + gl.codigo_ruta+" ORDER BY Nombre");
            }

            for (int i = 0; i < VendedoresObj.count; i++) {
                item = clsCls.new clsMenu();
                item.Cod = VendedoresObj.items.get(i).codigo;
                item.Name = item.Cod+" - "+VendedoresObj.items.get(i).nombre;// estaba .ruta #CKFK 20200517
                mitems.add(item);

                spincode.add(item.Cod);
                spinlist.add(item.Name);

            }

            adapter = new LA_Login(this, mitems);
            listView.setAdapter(adapter);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(dataAdapter);

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    private void msgAskLic(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(R.string.app_name);
        dialog.setMessage(msg);
        dialog.setIcon(R.drawable.ic_quest);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    private void showOrientMenu() {
        final AlertDialog Dialog;
        final String[] selitems = {"Tableta","Telefono"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Calibracion de pantalla");

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        guardaPantalla(true);break;
                    case 1:
                        guardaPantalla(false);break;
                }

                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();
    }

    //endregion

    //region Activity Events

    protected void onResume() {
        try {
            super.onResume();

            if (browse ==2 && gl.configCajaSuc){
                browse=0;
                processLogIn();
                return;
            }

            if (browse !=1){
                initSession();
            }

            if (browse == 1) {
                browse = 0;
                return;
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

}