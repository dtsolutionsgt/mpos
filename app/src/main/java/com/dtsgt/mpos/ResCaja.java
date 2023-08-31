package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbOrden;
import com.dtsgt.firebase.fbOrdenCuenta;
import com.dtsgt.firebase.fbOrdenEstado;
import com.dtsgt.ladapt.LA_ResCaja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResCaja extends PBase {

    private GridView gridView;
    private ImageView imgnowifi;

    private LA_ResCaja adapter;
    private clsViewObj ViewObj;

    private clsT_ordencomboObj T_ordencomboObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;
    private clsT_ventaObj T_ventaObj;
    private clsT_comboObj T_comboObj;

    private clsClasses.clsT_orden oitem;

    private fbOrdenEstado fboe;
    private fbOrdenCuenta fboc;
    private fbOrden fbo;

    private Runnable rnfboeLista,rnFbListenerrefOrdenEstado,rnfbocListaCliente,rnfboItems;

    private String corel,mesa,numpedido;
    private int cuenta,idmesero;
    private boolean idle=true;

    private boolean horiz,espedido,actorden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_res_caja);

            super.InitBase();

            gridView = findViewById(R.id.gridView1);
            imgnowifi=findViewById(R.id.imageView71a);

            calibraPantalla();

            ViewObj=new clsViewObj(this,Con,db);
            T_ordencomboObj=new clsT_ordencomboObj(this,Con,db);
            T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);
            T_ventaObj=new clsT_ventaObj(this,Con,db);
            T_comboObj = new clsT_comboObj(this, Con, db);

            actorden=gl.peActOrdenMesas;

            fboe=new fbOrdenEstado("OrdenEstado",gl.tienda);
            fboc=new fbOrdenCuenta("OrdenCuenta",gl.tienda);

            rnfboeLista = () -> showItems();
            rnFbListenerrefOrdenEstado = () -> FbListenerOrdenEstado();
            rnfbocListaCliente = () -> cargaDatosCliente();
            rnfboItems = () -> cargaOrden();

            fboe.setListener(rnFbListenerrefOrdenEstado);

            setHandlers();
            listItems();
            gl.ventalock=false;

            getURL();

            imgnowifi.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doSincon(View view) {
        startActivity(new Intent(this,Nowifi.class));
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                clsClasses.clsView item = (clsClasses.clsView)lvObj;

                adapter.setSelectedIndex(position);

                corel=item.f1;mesa=item.f2;cuenta=item.pk;
                gl.ordcorel=corel;gl.primesa=mesa;
                gl.pricuenta=""+cuenta;gl.nocuenta_precuenta= gl.pricuenta;
                espedido=app.esmesapedido(gl.emp,item.f5);
                gl.EsVentaDelivery=espedido;
                numpedido=item.f6;
                idmesero=Integer.parseInt(item.f8);

                showMenuMesa();
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            fboe.listItems(rnfboeLista);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showItems() {
        ArrayList<clsClasses.clsView> witems= new ArrayList<clsClasses.clsView>();
        clsClasses.clsView witem;
        clsClasses.clsFbOrdenEstado fbitem;

        try {

            witems.clear();

            for (int i = 0; i <fboe.items.size(); i++) {

                fbitem=fboe.items.get(i);

                if (fbitem.estado==1) {
                    witem=clsCls.new clsView();

                    witem.pk=fbitem.id;
                    witem.f1=fbitem.corel;
                    witem.f2=fbitem.nombre;
                    witem.f3="3";
                    witem.f4=du.shora(fbitem.fecha);
                    witem.f7="";
                    witem.f8=""+fbitem.mesero;

                    witems.add(witem);
                }

            }

            adapter=new LA_ResCaja(this,this,witems);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Listener

    private void FbListenerOrdenEstado() {
        try {
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void registerListener() {
        try {
            fboe.refOrdenEstado.addValueEventListener(fboe.listOrdenEstado);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void unregisterListener() {
        try {
            fboe.refOrdenEstado.removeEventListener(fboe.listOrdenEstado);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Venta

    private void crearVenta() {
        buildVenta();
    }

    private void buildVenta() {

        try {

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            gl.mesero_venta=idmesero;
            gl.numero_orden=corel+"_"+cuenta;
            gl.nummesapedido=numpedido;
            gl.cuenta_pagar=cuenta;
            gl.cuenta_borrar=cuenta;

            fbo=new fbOrden("Orden",gl.tienda,corel);
            fbo.listItems(rnfboItems);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaOrden() {
        try {
            if (fbo.errflag) throw new Exception(fbo.error);
            if (!fbo.listresult) {
                msgSync();return;
            }

            for (int i = 0; i <fbo.items.size(); i++) {
                oitem=fbo.items.get(i);
                if (oitem.cuenta==cuenta) {
                    addItem();
                }
            }

            cargaCliente();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean existeCliente(String NIT) {

        Cursor DT;
        boolean resultado=false;

        try{

            if (mu.emptystr(NIT)) {
                resultado=false;
            } else {

                sql="SELECT CODIGO, NOMBRE,DIRECCION,NIVELPRECIO,DIRECCION, MEDIAPAGO,TIPO_CONTRIBUYENTE,CODIGO_CLIENTE, EMAIL FROM P_CLIENTE " +
                        "WHERE NIT = '" + NIT + "'";
                DT=Con.OpenDT(sql);

                if (DT != null){

                    if (DT.getCount()>0){

                        DT.moveToFirst();

                        gl.rutatipo="V";
                        gl.cliente=DT.getString(0);
                        gl.nivel=gl.nivel_sucursal;
                        gl.percepcion=0;
                        gl.contrib=DT.getString(6);;
                        gl.scancliente = gl.cliente;
                        gl.gNombreCliente =DT.getString(1);
                        gl.gNITCliente =NIT;
                        gl.gDirCliente =DT.getString(4);
                        gl.media=DT.getInt(5);
                        gl.codigo_cliente=DT.getInt(7);

                        resultado=true;

                    }
                }
                if (DT!=null) DT.close();
            }

        } catch (Exception e){
            mu.toast("Ocurrió un error buscando al cliente");
            resultado=false;
        }
        return resultado;
    }

    private boolean addItem(){

        clsClasses.clsT_venta venta,ventau=null;
        clsClasses.clsT_combo combo;
        clsClasses.clsT_ordencombo citem;

        double tt;
        int itemid;

        try {

            itemid=T_ventaObj.newID("SELECT MAX(EMPRESA) FROM T_VENTA");

            venta=clsCls.new clsT_venta();
            venta.producto=oitem.producto;
            venta.empresa=""+itemid;
            //venta.empresa=oitem.empresa;
            venta.um=oitem.um;
            venta.cant=oitem.cant;
            venta.umstock=oitem.umstock;
            venta.factor=oitem.factor;
            venta.precio=oitem.precio;
            venta.imp=oitem.imp;
            venta.des=oitem.des;
            venta.desmon=oitem.desmon;
            venta.total=oitem.total;
            venta.preciodoc=oitem.preciodoc;
            venta.peso=oitem.peso;
            venta.val1=oitem.val1;
            venta.val2=oitem.val2;
            venta.val3=oitem.val3;
            venta.val4=oitem.val4;
            venta.percep=oitem.percep;

            T_ordencomboprecioObj.fill("WHERE (COREL='"+corel+"') AND (IDCOMBO="+oitem.empresa+")");

            if (T_ordencomboprecioObj.count>0) {
                venta.precio=T_ordencomboprecioObj.first().prectotal;
                venta.preciodoc=venta.precio;
                tt=venta.cant*venta.precio;tt=mu.round2(tt);
                venta.total=tt;
                oitem.total=tt;
            }

            //T_ventaObj.add(venta);

            //#EJC20210708: Corrección en T_VENTA, consultar antes si el producto ya existe e incrementar la cantidad
            //para evitar que devuelva error de llave.
            Cursor dt;
            double prodtot=0;

            try {
                //#jp20220209
                T_ventaObj.fill("WHERE (PRODUCTO='"+venta.producto+"') AND (UM='"+venta.um+"')");
                if (T_ventaObj.count>0) ventau=T_ventaObj.first();

                if (app.prodTipo(venta.producto).equalsIgnoreCase("M")){
                    T_ventaObj.add(venta);
                } else {
                    if (T_ventaObj.count==0) {
                        T_ventaObj.add(venta);
                    } else {
                        ventau.cant+= oitem.cant;
                        ventau.total = ventau.total+mu.round(oitem.total,2);
                        ventau.imp = ventau.imp + mu.round(oitem.imp,2);
                        T_ventaObj.update(ventau);
                    }
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            if (app.prodTipo(venta.producto).equalsIgnoreCase("M")) {

                T_ordencomboObj.fill("WHERE (COREL='"+corel+"') AND (IDCOMBO="+oitem.empresa+")");
                for (int j = 0; j <T_ordencomboObj.count; j++) {

                    citem=T_ordencomboObj.items.get(j);
                    combo=clsCls.new clsT_combo();
                    combo.codigo_menu=citem.codigo_menu;
                    combo.idcombo=citem.idcombo;
                    combo.cant=citem.cant;
                    combo.unid=citem.unid;
                    combo.idseleccion=citem.idseleccion;
                    combo.orden=citem.orden;

                    T_comboObj.add(combo);
                }
            }

        } catch (SQLException e) {
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    //endregion

    //region Aux

    private boolean ventaVacia() {
        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM T_VENTA");
            db.execSQL("DELETE FROM T_COMBO");

            db.setTransactionSuccessful();
            db.endTransaction();

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

        return false;
    }

    private void datosCuenta() {
        try {
            startActivity(new Intent(this,ResCliente.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaCliente() {
        try {
            fboc.getItem(gl.ordcorel,""+cuenta,rnfbocListaCliente);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cargaDatosCliente() {

        try {

            if (fboc.errflag) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+fboc.error);return;
            }

            gl.codigo_cliente=gl.emp*10;

            if (fboc.itemexists) {

                gl.gNombreCliente = fboc.item.nombre;
                gl.gNITCliente = fboc.item.nit;
                gl.gDirCliente = fboc.item.direccion;
                gl.gCorreoCliente = fboc.item.correo;
                gl.gNITcf=fboc.item.nit.equalsIgnoreCase("C.F.");

            } else {
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="C.F.";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;

                toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . Cuenta no existe.");return;
            }

            gl.ventalock=true;

            finish();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void calibraPantalla() {
        if (pantallaHorizontal()) horiz=true; else horiz=false;

        if (horiz) {
            //lblmes.setTextSize(36);lblgrupo.setTextSize(36);
            gridView.setNumColumns(4);
        } else {
            //lblmes.setTextSize(20);lblgrupo.setTextSize(20);
            gridView.setNumColumns(2);
        }
    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    private void getURL() {
        gl.wsurl = "http://192.168.0.12/mposws/mposws.asmx";
        gl.timeout = 6000;

        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "/mposws.txt");

            if (file1.exists()) {
                FileInputStream fIn = new FileInputStream(file1);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

                gl.wsurl = myReader.readLine();
                String line = myReader.readLine();
                if(line.isEmpty()) gl.timeout = 6000; else gl.timeout = Integer.valueOf(line);
                myReader.close();
            }
        } catch (Exception e) {}

    }

    //endregion

    //region Dialogs

    private void showMenuMesa() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ResCaja.this,"Mesa "+mesa+" , Cuenta #"+cuenta);

            listdlg.add("Datos cliente");
            listdlg.add("Pagar");
            listdlg.add("Borrar de la lista");
            //listdlg.add("Precuenta");

            listdlg.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        switch (position) {
                            case 0:
                                datosCuenta();break;
                            case 1:
                                if (ventaVacia()) {

                                    if (gl.pePropinaFija) {
                                        crearVenta();
                                    } else {
                                        inputPropina();
                                    }

                                } else msgbox("Antes de pagar la cuenta debe terminar la venta actual");
                                //msgAskPago("Pagar la cuenta "+cuenta);
                                break;
                            case 2:
                                msgAskReset("Esta opción no borra la cuenta y los articulos,\n" +
                                             "solo la elimina de la lista. Despues de imprimir\n" +
                                             "la precuenta va a aparecer de nuevo.\n" +
                                             "¿Borrar la cuenta de la lista?");
                                break;
                            case 3:
                                if (ventaVacia()) {
                                    crearVenta();
                                } else msgbox("Antes de imprimir precuenta debe terminar la venta actual");
                                break;
                        }

                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void msgAskReset(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        fboe.removeKey(gl.ordcorel+"_"+gl.pricuenta);
                        listItems();
                    } catch (Exception e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                    }
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void inputPropina() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Monto propina");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String s=input.getText().toString();
                    crearVenta();
                } catch (Exception e) {
                    mu.msgbox("Monto incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    private void msgSync() {
        try{
            ExDialog dialog = new ExDialog(this);
            dialog.setMessage("La cuenta no está actualizada.\nIntente de nuevo.");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { }
            });

            dialog.show();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {
            ViewObj.reconnect(Con,db);
            T_ordencomboObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        registerListener();
    }

    @Override
    protected void onPause() {
        //cancelaOrdenes();
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (idle) {
            super.onBackPressed();
        }
    }

    //endregion

}