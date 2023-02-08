package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_stock_inv_detObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extWaitDlg;
import com.dtsgt.ladapt.LA_imp_inv;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.wsOpenDT;

import java.util.ArrayList;

public class InvCentral extends PBase {

    private ListView listView;
    private TextView lblTCant,lblTCosto,lblTit;

    private wsOpenDT wsic;

    private Runnable rnInvCent;

    private extWaitDlg waitdlg;

    private clsP_stock_inv_detObj P_stock_inv_detObj;

    private LA_imp_inv adapter;

    private int idinv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_central);

            super.InitBase();

            listView =  findViewById(R.id.listView1);
            lblTit = findViewById(R.id.lblTit3);
            lblTCant = findViewById(R.id.textView155);
            lblTCosto = findViewById(R.id.textView150);

            idinv=gl.invcent_cod;

            lblTit.setText("INVENTARIO INICIAL #"+idinv);

            P_stock_inv_detObj=new clsP_stock_inv_detObj(this,Con,db);

            app.getURL();

            wsic=new wsOpenDT(gl.wsurl);
            rnInvCent = new Runnable() {
                public void run() {
                    callbackInvCent();
                }
            };

            setHandlers();

            waitdlg= new extWaitDlg();

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    requestInventory();
                }
            };
            mtimer.postDelayed(mrunner,200);


        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doApply(View view) {
        msgAskAplicar();
    }

    public void doPrint(View view) {

    }

    public void doCancel(View view) {
        msgAskCancelar();
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    adapter.setSelectedIndex(position);
                };
            });

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

    //region Main

    private void listItems() {
        ArrayList<clsClasses.clsT_movr> items= new ArrayList<clsClasses.clsT_movr>();
        clsClasses.clsT_movr item;
        double tot;

        try {

            sql="SELECT P_STOCK_INV_DET.CODIGO_PRODUCTO, P_STOCK_INV_DET.UNIDADMEDIDA,'', " +
                    "P_STOCK_INV_DET.CANT, P_STOCK_INV_DET.COSTO, " +
                    "P_PRODUCTO.DESCLARGA ,'', '','' " +
                    "FROM P_STOCK_INV_DET INNER JOIN " +
                    "P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO= P_STOCK_INV_DET.CODIGO_PRODUCTO " +
                    "WHERE  (P_STOCK_INV_DET.CODIGO_INVENTARIO_ENC="+idinv+") " +
                    "ORDER BY P_PRODUCTO.DESCLARGA";

            clsViewObj ViewObj=new clsViewObj(this,Con,db);
            ViewObj.fillSelect(sql);
            int cl=ViewObj.items.size();

            db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            tot=0;
            for (int i = 0; i <cl; i++) {

                item = clsCls.new clsT_movr();

                item.coreldet=i;
                item.corel=" ";
                item.producto=ViewObj.items.get(i).pk;
                item.cant=Double.parseDouble(ViewObj.items.get(i).f3);
                item.cantm=0;
                item.peso=0;
                item.pesom=item.cant*Double.parseDouble(ViewObj.items.get(i).f4);tot+=item.pesom;
                item.lote=ViewObj.items.get(i).f5;
                item.codigoliquidacion=0;
                item.unidadmedida=ViewObj.items.get(i).f1;
                item.precio=0;
                item.razon=0;

                item.srazon="";
                item.val1="";
                item.val2="";

                items.add(item);

            }

            adapter=new LA_imp_inv(this,this,items);
            listView.setAdapter(adapter);

            lblTCant.setText("Registros: "+cl);
            lblTCosto.setText("Costo inventario: "+mu.frmcur(tot));

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void saveInventory() {
        clsClasses.clsP_stock_inv_det item;
        Cursor dt=wsic.openDTCursor;

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                item = clsCls.new clsP_stock_inv_det();

                item.codigo_inventario_enc=idinv;
                item.codigo_producto=dt.getInt(0);
                item.unidadmedida=dt.getString(1);
                item.cant=dt.getDouble(2);
                item.costo=dt.getDouble(3);

                P_stock_inv_detObj.add(item);

                dt.moveToNext();
            }

            dt.close();

            listItems();

            db.setTransactionSuccessful();
            db.endTransaction();

            enviaEstado(1);
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void callbackInvCent() {
        try {

            try {
                waitdlg.wdhandle.dismiss();
            } catch (Exception e) {}

            if (wsic.errflag) {
                msgbox(wsic.error);return;
            }

            if (wsic.openDTCursor.getCount()==0) {
                msgbox("El inventario está vecio");return;
            }

            saveInventory();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void requestInventory() {
        try {
            waitdlg.buildDialog(this,"Recibiendo productos . . .","Ocultar");
            waitdlg.show();

            sql="SELECT CODIGO_PRODUCTO, UNIDADMEDIDA, CANT, COSTO " +
                    "FROM P_STOCK_INVENTARIO_DET  WHERE (CODIGO_INVENTARIO_ENC="+idinv+") ";
            wsic.execute(sql,rnInvCent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean applyInventory() {
        try {
            db.beginTransaction();

            db.setTransactionSuccessful();
            db.endTransaction();

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }


    //endregion

    //region Aux

    private void enviaEstado(int estado) {
        try {
            String cmd="UPDATE P_STOCK_INVENTARIO_ENC SET ESTADO="+estado+" WHERE (CODIGO_INVENTARIO_ENC="+idinv+")";

            Intent intent = new Intent(InvCentral.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",cmd);
            startService(intent);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    public void msgAskAplicar() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Aplicar carga de inventario?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Aplicar", (dialog12, which) -> {
            try {
                if (applyInventory()) {
                    enviaEstado(2);
                    finish();
                }
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    public void msgAskCancelar() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Cancelar carga de inventario?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Cancelar", (dialog12, which) -> {
             try {
                 db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");
                 enviaEstado(0);
                 finish();
             } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
             }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_stock_inv_detObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}