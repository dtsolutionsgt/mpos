package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsT_stockalmObj;
import com.dtsgt.firebase.fbStock;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InicioInventario extends PBase {

    private ProgressBar pBar,pBarw;

    private fbStock fbs;
    private DatabaseReference fbconnRef;

    private Runnable rnFbPullCallBack,rnFbFinishItem;
    private ValueEventListener fbconnListener;

    private ArrayList<clsClasses.clsT_stockalm> stockitems= new ArrayList<clsClasses.clsT_stockalm>();

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

            fbs =new fbStock("Stock",gl.tienda);
            fbconnRef = fbs.fdb.getReference(".info/connected");

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
            fbs.listStock(fbsucursal,rnFbPullCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildItemList() {
        clsClasses.clsT_stockalm fbitem;

        try {
            stockitems.clear();

            if (fbs.saitems.size() == 0) {
                processItems();return;
            }

            clsT_stockalmObj T_stockalmObj=new clsT_stockalmObj(this,Con,db);

            db.execSQL("DELETE FROM T_stockalm");

            for (int i = 0; i < fbs.saitems.size(); i++) {
                T_stockalmObj.add(fbs.saitems.get(i));
            }

            try {
                sql="select IDPROD,IDALM,SUM(CANT),UM FROM T_STOCKALM GROUP BY IDPROD,IDALM";
                Cursor dt=Con.OpenDT(sql);

                dt.moveToFirst();int ii=dt.getCount();
                while (!dt.isAfterLast()) {

                    fbitem=clsCls.new clsT_stockalm();

                    fbitem.id=0;
                    fbitem.idprod=dt.getInt(0);
                    fbitem.idalm=dt.getInt(1);
                    fbitem.cant=dt.getDouble(2);
                    fbitem.um=dt.getString(3);

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
            //fbs.updateValue("/config"+fbsucursal,"fecha",""+du.getActDate());
            //fbs.updateValue("/config"+fbsucursal,"ruta",""+gl.codigo_ruta);

            fbs.updateValue("/config/",""+gl.tienda,""+du.getActDate());

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
        private int idprod,idalm;
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
                idalm=stockitems.get(ppos).idalm;
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

                            stitem.idprod=idprod;
                            stitem.idalm=idalm;
                            stitem.cant=totcant;
                            stitem.um=um;
                            stitem.bandera=1;

                            addItem(fbsucursal,stitem);

                            for (int i = 0; i < fbs.saitems.size(); i++) {
                                if (fbs.saitems.get(i).idprod==idprod && fbs.saitems.get(i).idalm==idalm) {
                                    removeValue(rnode+ fbs.saitems.get(i).key);
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