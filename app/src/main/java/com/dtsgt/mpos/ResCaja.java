package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.ladapt.LA_ResCaja;

public class ResCaja extends PBase {

    private GridView gridView;
    private TextView lblRec;
    private ImageView imgRec;

    private LA_ResCaja adapter;
    private clsViewObj ViewObj;

    private clsT_ordenObj T_ordenObj;
    private clsT_ordencomboObj T_ordencomboObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;
    private clsT_ventaObj T_ventaObj;
    private clsT_comboObj T_comboObj;

    private clsClasses.clsT_orden oitem;

    private Runnable rnCargaOrdenes;

    private Precio prc;

    private String corel,mesa;
    private int cuenta,counter;
    private boolean idle=true,exitflag=false;
    private boolean horiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_caja);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblRec = findViewById(R.id.textView212);
        imgRec = findViewById(R.id.imageView87);

        calibraPantalla();

        ViewObj=new clsViewObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);
        T_ordencomboObj=new clsT_ordencomboObj(this,Con,db);
        T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);
        T_ventaObj=new clsT_ventaObj(this,Con,db);
        T_comboObj = new clsT_comboObj(this, Con, db);

        prc=new Precio(this,mu,2);

        if (!gl.pelCajaRecep) {
            lblRec.setVisibility(View.INVISIBLE);imgRec.setVisibility(View.INVISIBLE);
        }

        setHandlers();
        listItems();
    }

    //region Events

    public void doRec(View view) {

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
                gl.ordcorel=corel;gl.primesa=mesa;gl.pricuenta=""+cuenta;

                showMenuMesa();
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        long ff;
        String fs,ssa;

        try {
            sql="SELECT T_ORDENCUENTA.ID AS Cuenta, P_RES_SESION.ID AS Corel, P_RES_MESA.NOMBRE, P_RES_SESION.ESTADO, P_RES_SESION.FECHAULT ,   '','','','' " +
                    "FROM P_RES_SESION INNER JOIN " +
                    "T_ORDENCUENTA ON P_RES_SESION.ID =T_ORDENCUENTA.COREL INNER JOIN " +
                    "P_RES_MESA ON P_RES_SESION.CODIGO_MESA = P_RES_MESA.CODIGO_MESA " +
                    "WHERE (P_RES_SESION.ESTADO IN (1, 2, 3)) " +
                    "ORDER BY P_RES_MESA.NOMBRE, CUENTA ";
            ViewObj.fillSelect(sql);

            for (int i = 0; i <ViewObj.count; i++) {
                fs=ViewObj.items.get(i).f4;
                try {
                    ff=Integer.parseInt(fs);
                    fs=du.shora(ff);
                } catch (Exception e) {
                    fs="";
                }
                ViewObj.items.get(i).f4=fs;

                if (cuentaPagada(ViewObj.items.get(i).f1,ViewObj.items.get(i).pk)) {
                    ViewObj.items.get(i).f3="4";
                }
            }

            for (int  i=ViewObj.count-1; i>=0; i--) {
                if (ViewObj.items.get(i).f3.equalsIgnoreCase("4")) ViewObj.items.remove(i);
            }

            adapter=new LA_ResCaja(this,this,ViewObj.items);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void completeItem() {
        try {
            db.execSQL("UPDATE P_RES_SESION SET ESTADO=-1,FECHAULT="+du.getActDateTime()+" WHERE ID='"+corel+"'");
            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void hideItem() {
        try {
            db.execSQL("UPDATE P_RES_SESION SET ESTADO=1,FECHAULT="+du.getActDateTime()+" WHERE ID='"+corel+"'");
            listItems();
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Venta

    private void crearVenta() {

        try {

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            clsP_res_sesionObj P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
            P_res_sesionObj.fill("WHERE ID='"+corel+"'");
            gl.mesero_venta=P_res_sesionObj.first().vendedor;

            gl.numero_orden=corel+"_"+cuenta;
            T_ordenObj.fill("WHERE COREL='"+corel+"'");
            counter=0;

            for (int i = 0; i <T_ordenObj.count; i++) {
                oitem=T_ordenObj.items.get(i);
                if (oitem.cuenta==cuenta) addItem();
            }

            cargaCliente();

            gl.ventalock=true;

            finish();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean addItem(){

        clsClasses.clsT_venta venta;
        clsClasses.clsT_combo combo;
        clsClasses.clsT_ordencombo citem;
        double tt;

        try {

            venta=clsCls.new clsT_venta();
            venta.producto=oitem.producto;
            venta.empresa=oitem.empresa;
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
                venta.total=oitem.total=tt;
            }

            T_ventaObj.add(venta);

            /*
            //#EJC20210708: Corrección en T_VENTA, consultar antes si el producto ya existe e incrementar la cantidad
            //para evitar que devuelva error de llave.
            Cursor dt;
            double prodtot=0;

            try {
                String vsql="SELECT CANT FROM T_venta WHERE (PRODUCTO='"+venta.producto+"') AND (UM='"+venta.um+"')";
                dt=Con.OpenDT(vsql);
                if (dt!=null){
                    //#ejc20210712: condición agregada con Jaros, para validar productos de tipo combo.
                    if (app.prodTipo(venta.producto).equalsIgnoreCase("M")){
                        T_ventaObj.add(venta);
                    }else{
                        if (dt.getCount()==0) {
                            T_ventaObj.add(venta);
                        }else{

                            venta.cant+= oitem.cant;
                            prodtot=mu.round(venta.precio*venta.cant,2);
                            venta.total = prodtot;
                            T_ventaObj.update(venta);
                        }
                    }
                };

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
            */

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
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    //endregion

    //region Recepcion



    //endregion

    //region Aux

    private boolean ventaVacia() {
        Cursor dt;

        try {
            sql="SELECT * FROM T_VENTA";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

            gl.codigo_cliente=gl.emp*10;

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            T_ordencuentaObj.fill("WHERE (COREL='"+corel+"') AND (ID="+cuenta+")");

            if (T_ordencuentaObj.count>0) {
                gl.gNombreCliente = T_ordencuentaObj.first().nombre;
                gl.gNITCliente = T_ordencuentaObj.first().nit;
                gl.gDirCliente = T_ordencuentaObj.first().direccion;
                gl.gCorreoCliente = T_ordencuentaObj.first().correo;
                gl.gNITcf=T_ordencuentaObj.first().cf==1;
            } else {
                gl.gNombreCliente = "Consumidor final";
                gl.gNITCliente ="C.F.";
                gl.gDirCliente = "Ciudad";
                gl.gCorreoCliente = "";
                gl.gNITcf=true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private Boolean cuentaPagada(String corr,int id) {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
            return D_facturaObj.count!=0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private Boolean cuentaVacia(String corr,int id) {
        try {
            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FACTLINK='"+corr+"_"+id+"') AND (ANULADO=0)");
            return D_facturaObj.count!=0;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
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

    //endregion

    //region Dialogs

    private void showMenuMesa() {
        clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);

        try {
            D_facturaObj.fill("WHERE (FACTLINK='"+corel+"_"+cuenta+"')");

            if (D_facturaObj.count==0) {
                showMenuMesaPendiente();
            } else {
                showMenuMesaCompleta();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showMenuMesaPendiente() {
        final AlertDialog Dialog;
        final String[] selitems = {"Preimpresion","Datos cliente","Pagar","Completar","Borrar"}; // cuenta

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Mesa "+mesa+" , Cuenta #"+cuenta);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        if (ventaVacia()) {
                            crearVenta();
                        } else msgbox("Antes de preimprimir la cuenta debe terminar la venta actual");
                        //msgAskPreimpresion("Imprimir la cuenta "+cuenta);
                        break;
                    case 1:
                        datosCuenta();break;
                    case 2:
                        if (ventaVacia()) {

                            if (gl.pePropinaFija) {
                                crearVenta();
                            } else {
                                inputPropina();
                            }

                        } else msgbox("Antes de pagar la cuenta debe terminar la venta actual");
                        //msgAskPago("Pagar la cuenta "+cuenta);
                        break;
                    case 3:
                        msgAskCompletar("Completar la mesa "+mesa);break;
                    case 4:
                        msgAskBorrar("Borrar la mesa "+mesa);break;
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

    private void showMenuMesaCompleta() {
        final AlertDialog Dialog;
        final String[] selitems = {"Completar"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Mesa "+mesa+" , Cuenta #"+cuenta);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        msgAskCompletar("Completar la mesa "+mesa);
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

    private void msgAskPreimpresion(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (ventaVacia()) {
                    crearVenta();
                } else msgbox("Antes de preimprimir la cuenta debe terminar la venta actual");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskPago(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (ventaVacia()) {
                    crearVenta();
                } else msgbox("Antes de pagar la cuenta debe terminar la venta actual");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskCompletar(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        if (!ventaVacia()) {
            if (!app.validaCompletarCuenta(corel)) {
                msgbox("No se puede completar la mesa,\nexisten cuentas pendientes de pago.");return;
            }
        }

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                completeItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskBorrar(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideItem();
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
            ViewObj.reconnect(Con,db);
            T_ordenObj.reconnect(Con,db);
            T_ordencomboObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (idle) {
            super.onBackPressed();
        } else {
            exitflag=true;
        }
    }

    //endregion

}