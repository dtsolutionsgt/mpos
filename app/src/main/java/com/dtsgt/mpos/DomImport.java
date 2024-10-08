package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.clsP_orden_numeroObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extTextDlg;
import com.dtsgt.firebase.fbPedidoEnc;
import com.dtsgt.mant.MantBarril;

import java.util.ArrayList;
import java.util.Stack;

public class DomImport extends PBase {

    private TextView lbl1;
    private ProgressBar pbar;

    private fbPedidoEnc fbpe;

    private Stack<String> pedidos = new Stack<>();

    private clsD_domicilio_encObj D_domicilio_encObj;
    private clsP_orden_numeroObj P_orden_numeroObj;

    private clsClasses.clsD_domicilio_enc eitem;
    private ArrayList<clsClasses.clsD_domicilio_det> ditems= new ArrayList<clsClasses.clsD_domicilio_det>();

    private int pcant,pedimp,pedpos;
    private boolean idle = false;
    private String corel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_import);

            super.InitBase();

            lbl1 = findViewById(R.id.textView352);
            lbl1.setText("");
            pbar = findViewById(R.id.progressBar10);

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);
            P_orden_numeroObj=new clsP_orden_numeroObj(this,Con,db);

            fbpe = new fbPedidoEnc("Domicilio/" + gl.emp + "/" + gl.tienda + "/" + du.actDate() + "/");

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> { listItems(); }, 200);

        } catch (Exception e) {
            msgbox(new Object() {
            }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }


    //region Events


    //endregion

    //region Main

    private void listItems() {
        try {
            fbpe.listPending(() -> { cantPendientes(); });
        } catch (Exception e) {
            finerr(e.getMessage());
        }
    }

    private void cantPendientes() {
        try {
            if (fbpe.errflag) throw new Exception(fbpe.error);

            pedidos.clear(); pcant=0;pedimp=0;pedpos=0;

            for (String itm : fbpe.doms) {
                pedidos.push(itm);
            }

            pcant = pedidos.size();
            if (pcant == 0) {
                msgexit("No hay ningúno pedido nuevo");return;
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> { procesaPedido(); }, 200);
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void procesaPedido() {
        try {
            if (pedidos.isEmpty()) {
                finpedidos();
            } else {
                corel=pedidos.pop();pedpos++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {lbl1.setText("Cargando pedido "+pedpos+" / "+pcant);}
                });

                cargaEncabezado();
            }
        } catch (Exception e) {
            finerr(e.getMessage());
        }
    }

    private void cargaEncabezado() {
        try {
            fbpe.getItem(corel,() -> { llenaEncabezado(); });
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            proximoPedido();
        }
    }

    private void llenaEncabezado() {
        try {
            if (fbpe.errflag) throw new Exception(fbpe.error);

            eitem= fbpe.item;
            eitem.idorden=numeroOrdenLocal();

            cargaDetalle();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            proximoPedido();
        }
    }

    private void cargaDetalle() {
        try {
            //fbpe.getItem(corel,() -> { guardaPedido(); });

            guardaPedido();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            proximoPedido();
        }
    }

    private void guardaPedido() {
        try {
            if (fbpe.errflag) throw new Exception(fbpe.error);

            try {
                db.beginTransaction();

                db.execSQL("DELETE FROM D_domicilio_enc WHERE (COREL='"+corel+"')");
                db.execSQL("DELETE FROM D_domicilio_det WHERE (COREL='"+corel+"')");

                D_domicilio_encObj.add(eitem);


                db.setTransactionSuccessful();
                db.endTransaction();

                pedimp++;

                fbpe.updateItem(eitem.corel);
            } catch (Exception e) {
                db.endTransaction();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            proximoPedido();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
            proximoPedido();
        }
    }

    private void proximoPedido() {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> { procesaPedido(); }, 200);
        } catch (Exception e) {
            finerr(e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    private void msgexit(String msg) {
          try {
            extTextDlg txtdlg = new extTextDlg();
            txtdlg.buildDialog(DomImport.this,"Pedidos","OK");

            txtdlg.setText(msg);

            txtdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            txtdlg.show();

        } catch (Exception e){ }
    }

    //endregion

    //region Aux

    private void finpedidos() {
        String ss;

        switch (pedimp) {
            case 0:
                ss="No se ha creado ningúno nuevo pedido .";break;
            case 1:
                ss="Creado 1 nuevo pedido .";break;
            default :
                ss="Creado "+pedimp+" pedidos nuevos.";break;
        }
        finok(ss);
    }

    private void finok(String msg) {
        idle=false;pbar.setVisibility(View.INVISIBLE);lbl1.setText("");
        msgexit(msg);
    }

    private void finerr(String msg) {
        idle=false;pbar.setVisibility(View.INVISIBLE);lbl1.setText("");
        msgexit("Ocurrió error : \n"+msg);
    }

    private int numeroOrdenLocal() {
        int ordennum;

        try {

            ordennum=P_orden_numeroObj.newID("SELECT MAX(ID) FROM P_orden_numero");
            if (ordennum % gl.peMaxOrden==0) ordennum++;

            clsClasses.clsP_orden_numero orditem = clsCls.new clsP_orden_numero();
            orditem.id=ordennum;
            P_orden_numeroObj.add(orditem);

            try {
                int ilimit=ordennum-10;
                db.execSQL("DELETE FROM P_orden_numero WHERE ID<"+ilimit);
            } catch (Exception e) { }

            if (ordennum==0) ordennum=1;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            ordennum=0;
        }

        return ordennum;
    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            D_domicilio_encObj.reconnect(Con,db);
            P_orden_numeroObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (idle) {
            super.onBackPressed();
        } else {
            toast("Espere por favor . . . ");
        }
    }

    //endregion

}