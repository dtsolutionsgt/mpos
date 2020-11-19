package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencomboObj;
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
    private clsT_ventaObj T_ventaObj;
    private clsT_comboObj T_comboObj;

    private clsClasses.clsT_orden oitem;

    private Runnable rnCargaOrdenes;

    private Precio prc;

    private String corel,mesa;
    private int cuenta,counter;
    private boolean idle=true,exitflag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_caja);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblRec = findViewById(R.id.textView212);
        imgRec = findViewById(R.id.imageView87);

        ViewObj=new clsViewObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);
        T_ordencomboObj=new clsT_ordencomboObj(this,Con,db);
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
                gl.primesa=mesa;gl.pricuenta=""+cuenta;

                if (gl.pelCajaRecep) {
                    if (item.f3.equalsIgnoreCase("2")) showMenuPreimpresion();
                    if (item.f3.equalsIgnoreCase("3")) showMenuPago();
                } else {
                    if (item.f3.equalsIgnoreCase("2")) msgAskPreimpresion("Imprimir cuenta "+cuenta);
                    if (item.f3.equalsIgnoreCase("3")) msgAskPago("Pagar cuenta "+cuenta);
                }
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        long ff;
        String fs;

        try {

            sql="SELECT T_ORDENCUENTA.ID AS Cuenta, P_RES_SESION.ID AS Corel, P_RES_MESA.NOMBRE, P_RES_SESION.ESTADO, P_RES_SESION.FECHAULT ,   '','','','' " +
                    "FROM P_RES_SESION INNER JOIN " +
                    "T_ORDENCUENTA ON P_RES_SESION.ID =T_ORDENCUENTA.COREL INNER JOIN " +
                    "P_RES_MESA ON P_RES_SESION.CODIGO_MESA = P_RES_MESA.CODIGO_MESA " +
                    "WHERE (P_RES_SESION.ESTADO IN (2, 3)) ORDER BY P_RES_MESA.NOMBRE, CUENTA ";
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
            }

            adapter=new LA_ResCaja(this,this,ViewObj.items);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void deleteItem() {

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_RES_SESION WHERE ID='"+corel+"'");
            db.execSQL("DELETE FROM T_ORDEN WHERE COREL='"+corel+"'");
            db.execSQL("DELETE FROM T_ORDENCUENTA WHERE COREL='"+corel+"'");

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

    }

    //endregion

    //region Venta

    private void crearVenta() {

        try {
            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            T_ordenObj.fill("WHERE COREL='"+corel+"'");
            counter=0;

            for (int i = 0; i <T_ordenObj.count; i++) {
                oitem=T_ordenObj.items.get(i);
                if (oitem.cuenta==cuenta) addItem();
            }

            /*

            //sql="SELECT COREL, 0 AS COREL_DET, CODIGO_PRODUCTO, UMVENTA, CANT, TOTAL,'' AS NOTA,'' AS CODIGO_TIPO_PRODUCTO " +
            //        "FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO='M') ";
            sql="SELECT * FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO='M') ";
            D_pedidodObj.fillSelect(sql);

            for (int i = 0; i <D_pedidodObj.count; i++) {
                pitem=D_pedidodObj.items.get(i);
                addItem();
            }

             */

            gl.ventalock=false;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean addItem(){
        clsClasses.clsT_venta venta;
        clsClasses.clsT_combo combo;
        clsClasses.clsT_ordencombo citem;

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

            T_ventaObj.add(venta);

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

    //endregion

    //region Dialogs

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
                } else msgbox("Antes de pagar al cuenta debe terminar la venta actual");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void showMenuPreimpresion() {
        final AlertDialog Dialog;
        final String[] selitems = {"Imprimir","Borrar"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Mesa "+mesa);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        ;break;
                    case 1:
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

    private void showMenuPago() {
        final AlertDialog Dialog;
        final String[] selitems = {"Pagar","Completar","Borrar"};

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Mesa "+mesa);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        ;break;
                    case 1:
                        ;break;
                    case 2:
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

    private void msgAskBorrar(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mesa "+mesa);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
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