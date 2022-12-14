package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.ladapt.ListAdaptCFDV;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class CajaPagosPend extends PBase {

    private ListView listView;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptCFDV adapter;
    private clsClasses.clsCFDV selitem;

    private clsClasses.clsCFDV sitem;

    private int tipo;
    private String selid,itemid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja_pagos_pend);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);

        gl.tipo=3;tipo=gl.tipo;

        itemid="*";

        setHandlers();

        listItems();

        //doc=new clsDocAnul(this,34,"");

        //fdoc=new clsDocFactura(this,34,gl.peMon,gl.peDecImp,"");

    }

    //region Events

    public void anulDoc(View view) {

        try {
            if (itemid.equalsIgnoreCase("*")) {
                mu.msgbox("Debe seleccionar un documento.");return;
            }

            boolean flag=gl.peAnulSuper;
            if (gl.rol==2 | gl.rol==3) flag=false;

            if (flag) {
                browse=1;
                startActivity(new Intent(this,ValidaSuper.class));
            } else {
                msgAsk("Aplicar pago");
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void setHandlers(){
        try{

            listView.setOnTouchListener(new SwipeListener(this) {
                public void onSwipeRight() {
                    onBackPressed();
                }
                public void onSwipeLeft() {}
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

                        itemid=vItem.Cod;
                        adapter.setSelectedIndex(position);

                        sitem=vItem;
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

                        itemid=vItem.Cod;
                        adapter.setSelectedIndex(position);
                        sitem=vItem;

                        anulDoc(view);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                    return true;
                }
            });
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Main

    public void listItems() {

        Cursor DT;
        clsClasses.clsCFDV vItem;
        int vP,f;
        double val;
        String id,sf,sval,ccor;

        items.clear();
        selidx=-1;vP=0;

        try {

            sql="SELECT P.COREL FROM D_FACTURAP P " +
                    "INNER JOIN D_FACTURA F ON P.COREL=F.COREL " +
                    "WHERE (P.TIPO='E') AND (F.FECHA>2009230000) AND (F.PEDCOREL<>'') AND (F.ANULADO=0) AND (P.VALOR=0) ";
            Cursor dt=Con.OpenDT(sql);
            if (dt.getCount()>0) {

                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    ccor=dt.getString(0);

                    sql="SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO, "+
                            "D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR "+
                            "FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE "+
                            "WHERE (D_FACTURA.COREL='"+ccor+"')  ";

                    DT=Con.OpenDT(sql);

                    if (DT.getCount()>0) {
                        DT.moveToFirst();

                        while (!DT.isAfterLast()) {

                            id=DT.getString(0);
                            vItem =clsCls.new clsCFDV();

                            vItem.Cod=DT.getString(0);
                            vItem.Desc=DT.getString(1);
                            if (tipo==2) vItem.Desc+=" - "+DT.getString(4);

                            if (tipo==3) {
                                 sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;
                            }else if(tipo==1||tipo==6){
                                sf=DT.getString(0);
                            }else{
                                f=DT.getInt(2);sf=du.sfecha(f)+" "+du.shora(f);
                            }

                            vItem.Fecha=sf;
                            val=DT.getDouble(3);vItem.val=val;
                            try {
                                sval=mu.frmcur(val);
                            } catch (Exception e) {
                                sval=""+val;
                            }

                            vItem.Valor=sval;
                            if (tipo==4 || tipo==5) vItem.Valor="";

                            items.add(vItem);

                            if (id.equalsIgnoreCase(selid)) selidx=vP;vP+=1;

                            if (tipo==3) {
                                vItem.UUID=DT.getString(5);
                                vItem.FechaFactura=du.univfechalong(DT.getLong(6));
                            }else{
                                vItem.UUID="";
                                vItem.FechaFactura="";
                            }

                            DT.moveToNext();
                        }
                    }

                    if (DT!=null) DT.close();

                    dt.moveToNext();
                }
            }

            if (dt!=null) dt.close();

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        }

        adapter=new ListAdaptCFDV(this, items);
        listView.setAdapter(adapter);

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.setSelection(selidx);
        }

        listView.setVisibility(View.VISIBLE);
    }

    private void pagarDocumento() {
        try {
            aplicarPago();
            listItems();
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Documents

    private void aplicarPago() {
        try {
            sql="UPDATE D_FACTURAP SET Valor="+sitem.val+" WHERE COREL='"+sitem.Cod+"'";
            db.execSQL(sql);

            mu.msgbox("Pago aplicado.");
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" : "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void msgAsk(String msg) {

        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("mPos");
            dialog.setMessage("¿" + msg  + "?");
            dialog.setIcon(R.drawable.ic_quest);
            dialog.setPositiveButton("Si", (dialog1, which) -> pagarDocumento());
            dialog.setNegativeButton("No", null);
            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

}
