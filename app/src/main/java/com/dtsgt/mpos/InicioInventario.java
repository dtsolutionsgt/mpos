package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsT_stockObj;
import com.dtsgt.firebase.fbStock;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InicioInventario extends PBase {

    private ProgressBar pBar,pBarw;

    private fbStock fbb;
    private DatabaseReference fbconnRef;

    private Runnable rnFbPullCallBack,rnFbFinishItem;
    private ValueEventListener fbconnListener;

    private ArrayList<clsClasses.clsT_stock> stockitems= new ArrayList<clsClasses.clsT_stock>();

    private String fbsucursal;
    private int pcount,ppos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inicio_inventario);

            super.InitBase();

            pBar=findViewById(R.id.progressBar9);pBar.setVisibility(View.INVISIBLE);
            pBarw=findViewById(R.id.progressBar12);

            fbb=new fbStock("Stock",gl.tienda);
            fbconnRef = fbb.fdb.getReference(".info/connected");

            rnFbPullCallBack = new Runnable() {
                public void run() {
                    buildItemList();
                }
            };

            fbsucursal ="/"+gl.tienda+"/";

            fbconnListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        msgAskStart("Antes de iniciar inventario asegure se que ninguno otro dispositivo está activo");
                    } else {
                        msgboxexit("Proceso cancelado.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    toastlong("Proceso cancelado.");finish();
                }
            };

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Main

    private void compactarInventario() {
        try {
            fbb.listExist(fbsucursal,0,rnFbPullCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildItemList() {
        clsClasses.clsT_stock fbitem;

        try {
            stockitems.clear();

            if (fbb.sitems.size() == 0) {
                processItems();return;
            }

            clsT_stockObj T_stockObj=new clsT_stockObj(this,Con,db);

            db.execSQL("DELETE FROM T_stock");

            for (int i = 0; i <fbb.sitems.size(); i++) {
                T_stockObj.add(fbb.sitems.get(i));
            }

            try {
                sql="select IDPROD,SUM(CANT),UM FROM T_STOCK GROUP BY IDPROD";
                Cursor dt=Con.OpenDT(sql);

                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    fbitem=clsCls.new clsT_stock();

                    fbitem.id=0;
                    fbitem.idprod=dt.getInt(0);
                    fbitem.cant=dt.getDouble(1);
                    fbitem.um=dt.getString(2);

                    stockitems.add(fbitem);

                    dt.moveToNext();
                }

                processItems();
            } catch (Exception ee) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+ee.getMessage());
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void processItems() {

        pBarw.setVisibility(View.INVISIBLE);pBar.setVisibility(View.VISIBLE);

        if (stockitems.size()==0) {
            finishProcess();
        } else {
            pcount=stockitems.size();
            ppos=0;

            pBar.setMax(pcount);pBar.setProgress(0);

            fbStockItem sitem=new fbStockItem("Stock");
            sitem.processItem();
        }
    }

    private void finishProcess() {
        try {
            //fbb.updateValue("/config"+fbsucursal,"fecha",""+du.getActDate());
            //fbb.updateValue("/config"+fbsucursal,"ruta",""+gl.codigo_ruta);

            fbb.updateValue("/config/",""+gl.tienda,""+du.getActDate());

            toast("Inventario inicializado.");
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region StockItem

    private class fbStockItem extends fbStock {

        public clsClasses.clsFbStock stitem;

        private String um;
        private int idprod;
        private double totcant;

        public fbStockItem(String troot) {
            super(troot, gl.tienda);

            rnFbFinishItem = new Runnable() {
                public void run() {
                    finishItem();
                }
            };
        }

        private void processItem() {
            try {
                idprod=stockitems.get(ppos).idprod;
                um=stockitems.get(ppos).um;
                totcant=stockitems.get(ppos).cant;

                compactItem();

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        }

        private void compactItem() {
            try {
                transerr="";transresult=false;transstatus=-1;
                String rnode=fbsucursal;

                fdt.runTransaction(new Transaction.Handler() {
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        try {

                            transstatus=0;

                            stitem=clsCls.new clsFbStock();

                            stitem.idalm=0;
                            stitem.idprod=idprod;
                            stitem.cant=totcant;
                            stitem.um=um;
                            stitem.bandera=1;

                            addItem(fbsucursal,stitem);

                            for (int i = 0; i <fbb.sitems.size(); i++) {
                                if (fbb.sitems.get(i).idprod==idprod) {
                                    removeValue(rnode+fbb.sitems.get(i).key);
                                }
                            }

                            transstatus=1;
                            transresult=true;
                        } catch (Exception ee) {
                            transstatus=0;transresult=false;transerr=ee.getMessage();
                        }

                        return Transaction.success(mutableData);
                    }


                    public void onComplete(DatabaseError databaseError, boolean complete, DataSnapshot dataSnapshot) {
                        if (complete) {
                            transresult=true;
                        } else {
                            transresult=false;transerr=databaseError.getMessage();
                        }
                        callBack=rnFbFinishItem;
                        runCallBack();
                    }

                });
            } catch (Exception ee) {
                transresult=false;transerr=ee.getMessage();
            }
        }

        private void finishItem() {
            try {
                ppos++;

                runOnUiThread(new Runnable() {
                    public void run() {
                        pBar.setProgress(ppos);
                    }
                });

                if (ppos>=pcount) finishProcess(); else processItem();

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        }

    }

    //endregion

    //region Dialogs

    private void msgAskStart(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inicio de inventario");
        dialog.setMessage(msg);

        dialog.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                compactarInventario();
            }
        });

        dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }

    public void msgboxexit(String msg) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setCancelable(false);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        } catch (Exception e){ }
    }

    //endregion

    //region Activity Events

    @Override
    protected  void onResume(){
        try{
            super.onResume();
            fbconnRef.addValueEventListener(fbconnListener);
        } catch (Exception e) {}
    }

    @Override
    protected void onPause() {
        try {
            try {
                fbconnRef.removeEventListener(fbconnListener);
            } catch (Exception e) { }
            super.onPause();
        } catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
        try {
            fbconnRef.removeEventListener(fbconnListener);
        } catch (Exception e) {}
    }

    @Override
    public void onDestroy() {
        try {
            fbconnRef.removeEventListener(fbconnListener);
        } catch (Exception e) {}
        super.onDestroy();

    }


    //endregion

}