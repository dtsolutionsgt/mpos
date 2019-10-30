package com.dtsgt.mpos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.BaseDatosVersion;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Login;
import com.dtsgt.ladapt.ListAdaptMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends PBase {

    private TextView lblRuta, lblRTit, lblVer, lblEmp, lblPass,lblKeyDP;
    private ImageView imgLogo;

    private BaseDatosVersion dbVers;

    private ListView listView;
    private LA_Login adapter;
    private ArrayList<clsClasses.clsMenu> mitems= new ArrayList<clsClasses.clsMenu>();

    private clsKeybHandler khand;

    private boolean rutapos, scanning = false;
    private String cs1, cs2, cs3, barcode, epresult,usr, pwd;

    private String parVer = "2.1.1 / 28-Ago-2019";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            grantPermissions();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    // Grant permissions

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

            lblRuta = (TextView) findViewById(R.id.lblCDisp);
            lblRTit = (TextView) findViewById(R.id.lblCUsed);
            lblVer = (TextView) findViewById(R.id.textView10);
            lblEmp = (TextView) findViewById(R.id.textView82);
            lblPass = (TextView) findViewById(R.id.lblPass);
            lblKeyDP = (TextView) findViewById(R.id.textView110);
            imgLogo = (ImageView) findViewById(R.id.imgNext);

            listView = (ListView) findViewById(R.id.listView1);

            lblVer.setText("Version " + gl.parVer);

            // DB VERSION
            dbVers = new BaseDatosVersion(this, db, Con);
            dbVers.checkVersion(1);

            setHandlers();

            khand=new clsKeybHandler(this,lblPass,lblKeyDP);
            khand.enable();

            initSession();

            try {
                File file1 = new File(Environment.getExternalStorageDirectory(), "/debug.txt");
                if (file1.exists()) gl.debug=true;else gl.debug=false;
            } catch (Exception e) {
                gl.debug=false;
            }

            /*
            if (!validaLicencia()) {
                startActivity(new Intent(this, comWSLic.class));
                return;
            } else {
                supervisorRuta();
            } */

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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
        acesoAdmin();
    }

    public void gotoMenu() {
        try {
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    public void doLogin(View view) {

        /*
        if (!validaLicencia()) {
            startActivity(new Intent(this, comWSLic.class));
            return;
        }
        */

        try {
            processLogIn();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    public void doLoginScreen(View view) {
        usr="1";pwd="1";
        if (checkUser()) gotoMenu();
    }

    public void doRegister(View view) {
        try {
            startActivity(new Intent(this, comWSLic.class));
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    public void doKey(View view) {
        String ss;
        ss=view.getTag().toString();

        khand.handleKey(ss);
        if (khand.isEnter) {
            if (khand.isValid) {
                pwd=khand.val;
                processLogIn();
            }
        }
    }

    private void setHandlers() {

        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                        adapter.setSelectedIndex(position);
                        usr=item.Cod;

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
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

        /*
        if (dbVacia()) {
            gl.modoadmin = true;
            gl.autocom = 0;
            toastcent("¡La base de datos está vacia!");
            startActivity(new Intent(MainActivity.this, ComWS.class));
        }
        */

        configBase();

        try {
            //#HS_20181122_1505 Se agrego el campo Impresion.
            sql = "SELECT CODIGO,NOMBRE,VENDEDOR,VENTA,WLFOLD,IMPRESION,SUCURSAL,CELULAR FROM P_RUTA";
            DT = Con.OpenDT(sql);

            if (DT.getCount() > 0) {

                DT.moveToFirst();

                gl.ruta = DT.getString(0);
                gl.rutanom = DT.getString(1);
                gl.vend = DT.getString(2);
                gl.rutatipog = "V";
                s = DT.getString(3);
                gl.wsURL = DT.getString(4);
                gl.impresora = DT.getString(5);
                gl.sucur = DT.getString(6);

                if (!mu.emptystr(DT.getString(7))) {
                    vCellCom = DT.getString(7);
                }
                gl.CellCom = (vCellCom.equalsIgnoreCase("S"));

                rutapos = s.equalsIgnoreCase("R");

            } else {

                gl.ruta = "";
                gl.rutanom = "";
                gl.vend = "0";
                gl.rutatipog = "V";
                gl.wsURL = "http://192.168.1.1/wsAndr/wsAndr.asmx";

            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        try {
            //#HS_20181120_1616 Se agrego el campo UNIDAD_MEDIDA_PESO.//campo INCIDENCIA_NO_LECTURA
            sql = " SELECT EMPRESA,NOMBRE,DEVOLUCION_MERCANCIA,USARPESO,FIN_DIA,DEPOSITO_PARCIAL,UNIDAD_MEDIDA_PESO," +
                    " INCIDENCIA_NO_LECTURA, LOTE_POR_DEFECTO FROM P_EMPRESA";
            DT = Con.OpenDT(sql);

            if (DT.getCount() > 0) {
                DT.moveToFirst();

                gl.emp = DT.getString(0);
                gl.empnom = DT.getString(1);
                gl.devol = DT.getInt(2) == 1;
                s = DT.getString(3);
                gl.usarpeso = s.equalsIgnoreCase("S");
                gl.banderafindia = DT.getInt(4) == 1;
                gl.umpeso = DT.getString(6);
                gl.incNoLectura = DT.getInt(7) == 1; //#HS_20181211 Agregue campo incNoLectura para validacion en cliente.
                gl.depparc = DT.getInt(5) == 1;
                gl.lotedf = DT.getString(8);
            } else {
                gl.emp = "";
                gl.devol = false;
                msgbox("¡No se pudo cargar configuración de la empresa!");
            }

        } catch (Exception e) {
            msgbox("¡No se pudo cargar configuración de la empresa!");
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
        }

        gl.vendnom = "Vendedor 1";

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/SyncFold");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/RoadFotos");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/RoadFotos/Familia");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/RoadFotos/Producto");
            directory.mkdirs();
        } catch (Exception e) {
        }

        try {
            File directory = new File(Environment.getExternalStorageDirectory() + "/RoadFotos/Cliente");
            directory.mkdirs();
        } catch (Exception e) {
        }

        //Id de Dispositivo
        gl.deviceId = androidid();
        gl.devicename = getLocalBluetoothName();

        try {
            AppMethods app = new AppMethods(this, gl, Con, db);
            app.parametrosExtra();
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            msgbox(e.getMessage());
        }

        configBase();

        llenaUsuarios();
    }

    private void processLogIn() {

        /*/
        if (!validaLicencia()) {
             mu.msgbox("¡Licencia invalida!");
            startActivity(new Intent(this,comWSLic.class));
            return;
        }
        */

        if (checkUser()) gotoMenu();

    }

    private boolean checkUser() {
        Cursor DT;
        String dpwd;

        configBase();

        try {

            if (usr.isEmpty()) {
                mu.msgbox("Usuario incorrecto.");return false;
            }
            if (pwd.isEmpty()) {
                mu.msgbox("Contraseña incorrecta.");return false;
            }


            sql = "SELECT NOMBRE,CLAVE,NIVEL,NIVELPRECIO FROM VENDEDORES WHERE CODIGO='" + usr + "'  COLLATE NOCASE";
            DT = Con.OpenDT(sql);

            if (DT.getCount() == 0) {
                mu.msgbox("Usuario incorrecto !");
                return false;
            }

            DT.moveToFirst();
            dpwd = DT.getString(1);
            if (!pwd.equalsIgnoreCase(dpwd)) {
                mu.msgbox("Contraseña incorrecta !");
                return false;
            }

            gl.nivel = DT.getInt(2);gl.rol=gl.nivel;

            if (gl.caja.isEmpty() || gl.tienda.isEmpty()) {
                if (gl.nivel==3) {
                    gl.modoinicial=true;return true;
                } else {
                    toastlong("No está configurada la caja. Informe al gerente.");
                    acesoAdmin();return false;
                }
            } else {
               gl.modoinicial=false;
            }

            if (gl.nivel!=3) {

                sql = "SELECT NOMBRE,CLAVE,NIVEL,NIVELPRECIO FROM VENDEDORES " +
                      "WHERE (CODIGO='" + usr + "') AND (RUTA='"+gl.tienda+"') COLLATE NOCASE";
                DT = Con.OpenDT(sql);

                if (DT.getCount() == 0) {
                    mu.msgbox("¡El usuario no tiene permiso de ingreso para "+gl.tiendanom+"!");return false;
                }
            }

            gl.vendnom = DT.getString(0);
            gl.vend = usr;
            gl.vnivprec = DT.getInt(3);

            khand.clear();khand.enable();

            return true;

        } catch (Exception e) {
            addlog(new Object() { }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            return false;
        }

    }

    public void supervisorRuta() {
        Cursor DT;

        try {

            sql = "SELECT CODIGO FROM P_VENDEDOR WHERE RUTA = '" + gl.ruta + "' AND NIVEL = 2";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            if (DT.getCount() > 0) {
                gl.codSupervisor = DT.getString(0);
            }else{
                gl.codSupervisor = "";
            }

        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            Log.d("supervisorRuta error: ", e.getMessage());
        }

    }

    //endregion

    //region Ventas Demo

    public void showDemoMenu() {

        try {
            final AlertDialog Dialog;
            final String[] selitems = {"Datos de cliente", "Base de datos original", "Borrar datos de venta"};

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Datos de demo");

            menudlg.setItems(selitems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    switch (item) {
                        case 0:
                            Intent intent = new Intent(MainActivity.this, DemoData.class);
                            startActivity(intent);
                            break;
                        case 1:
                            copyRawFile();
                            break;
                        case 2:
                            borrarDatos(1);
                            break;
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
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    private void copyRawFile() {
        String fn;
        int rid, rslt;
        try {
            Field[] fields = R.raw.class.getFields();
            for (Field f : fields)
                try {
                    fn = f.getName();
                    if (fn.equalsIgnoreCase("rd_param")) {
                        rid = f.getInt(null);
                        rslt = copyRawFile(rid);
                        if (rslt == 1) {
                            Intent intent = new Intent(this, ComDrop.class);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    addlog(new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
                }
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }

    }

    private int copyRawFile(int rawid) {

        try {
            InputStream in = getResources().openRawResource(rawid);
            File file = new File(Environment.getExternalStorageDirectory(), "/SyncFold/rd_param.txt");
            FileOutputStream out = new FileOutputStream(file);

            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
            return 1;
        } catch (Exception e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
            mu.msgbox("Error : " + e.getMessage());
            return 0;
        }
    }

    private void borrarDatos(int showmsg) {

        try {

            db.beginTransaction();

            sql = "DELETE FROM D_FACTURA";
            db.execSQL(sql);
            sql = "DELETE FROM D_FACTURAD";
            db.execSQL(sql);
            sql = "DELETE FROM D_FACTURAP";
            db.execSQL(sql);
            sql = "DELETE FROM D_FACTURAD_LOTES";
            db.execSQL(sql);

            sql = "DELETE FROM D_PEDIDO";
            db.execSQL(sql);
            sql = "DELETE FROM D_PEDIDOD";
            db.execSQL(sql);

            sql = "DELETE FROM D_BONIF";
            db.execSQL(sql);
            sql = "DELETE FROM D_BONIF_LOTES";
            db.execSQL(sql);
            sql = "DELETE FROM D_REL_PROD_BON";
            db.execSQL(sql);
            sql = "DELETE FROM D_BONIFFALT";
            db.execSQL(sql);

            sql = "DELETE FROM D_DEPOS";
            db.execSQL(sql);
            sql = "DELETE FROM D_DEPOSD";
            db.execSQL(sql);

            sql = "DELETE FROM D_MOV";
            db.execSQL(sql);
            sql = "DELETE FROM D_MOVD";
            db.execSQL(sql);

            sql = "DELETE FROM D_ATENCION";
            db.execSQL(sql);


            db.setTransactionSuccessful();
            db.endTransaction();

            if (showmsg == 1)
                Toast.makeText(this, "Datos de venta borrados", Toast.LENGTH_SHORT).show();

        } catch (SQLException e) {
            addlog(new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            db.endTransaction();
            mu.msgbox("Error : " + e.getMessage());
        }

    }

    //endregion

    //region Aux

    private void acesoAdmin() {

        if (!tieneTiendaCaja()) return;

        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Contraseña de administrador");//	alert.setMessage("Serial");

            final EditText input = new EditText(this);
            alert.setView(input);

            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText("");
            input.requestFocus();

            alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                     try {
                        String s = input.getText().toString();
                        if (s.equalsIgnoreCase("1965")) {
                            startActivity(new Intent(MainActivity.this, ConfigCaja.class));
                        } else {
                            mu.msgbox("Contraseña incorrecta");return;
                        }

                    } catch (Exception e) {
                        addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
                    }
                }
            });

            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();
        } catch (Exception e) {
            addlog(new Object() { }.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
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

    @SuppressLint("MissingPermission")
    private String androidid() {
        String uniqueID = "";
        try {

            TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            uniqueID = tm.getDeviceId();

            if (uniqueID==null){
                uniqueID = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
            }

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
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

        gl.tienda="";gl.caja="";
        gl.tiendanom="";gl.cajanom="";

        try {
            sql = "SELECT url,sucursal,ruta FROM Params";
            DT = Con.OpenDT(sql);
            DT.moveToFirst();

            gl.urlglob=DT.getString(0);
            gl.tienda=DT.getString(1);
            gl.caja=DT.getString(2);

            if (!gl.tienda.isEmpty()) {
                try {
                    sql = "SELECT DESCRIPCION FROM P_SUCURSAL WHERE CODIGO='"+gl.tienda+"'";
                    DT = Con.OpenDT(sql);
                    DT.moveToFirst();
                    gl.tiendanom=DT.getString(0);
                } catch (Exception e) {
                    gl.tiendanom="";
                }
            }

            if (!gl.caja.isEmpty()) {
                try {
                    sql = "SELECT NOMBRE FROM P_RUTA WHERE CODIGO='"+gl.caja+"'";
                    DT = Con.OpenDT(sql);
                    DT.moveToFirst();
                    gl.cajanom=DT.getString(0);
                } catch (Exception e) {
                    gl.cajanom="";
                }
            }

        } catch (Exception e) {
             gl.tiendanom="";gl.cajanom="";
        }

        lblRTit.setText(gl.tiendanom);
        lblRuta.setText(gl.cajanom);
        lblEmp.setText(gl.empnom);

        try {
            String emplogo = Environment.getExternalStorageDirectory() + "/mposlogo.png";
            File file = new File(emplogo);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(emplogo);
                imgLogo.setImageBitmap(bmImg);
            }
        } catch (Exception e) {
        }

    }

    public boolean tieneTiendaCaja() {
        Cursor DT;

        try {

            sql = "SELECT DESCRIPCION FROM P_SUCURSAL";
            DT = Con.OpenDT(sql);
            if (DT.getCount()==0) {
                msgbox("¡No se puede continuar, no está definida ninguna tienda!");return false;
            }

            sql = "SELECT NOMBRE FROM P_RUTA";
            DT = Con.OpenDT(sql);
            if (DT.getCount()==0) {
                msgbox("¡No se puede continuar, no está definida ninguna caja!");return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
   }

    public void llenaUsuarios() {
        clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
        clsClasses.clsMenu item;

        usr="";pwd="";

        try {
            mitems.clear();
            VendedoresObj.fill();

            for (int i = 0; i <VendedoresObj.count; i++) {
                item=clsCls.new clsMenu();
                item.Cod=VendedoresObj.items.get(i).codigo;
                item.Name=VendedoresObj.items.get(i).nombre;
                mitems.add(item);
            }

            adapter=new LA_Login(this,mitems);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Activity Events

    protected void onResume() {
        try {
            super.onResume();
            initSession();

            if (browse==1) {
                browse=0;return;
            }

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

}



