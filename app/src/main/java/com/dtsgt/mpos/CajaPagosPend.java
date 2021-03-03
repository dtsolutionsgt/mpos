package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
                msgAsk("Anular documento");
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
        String id,sf,sval;

        items.clear();
        selidx=-1;vP=0;

        try {

                sql="SELECT D_FACTURA.COREL,P_CLIENTE.NOMBRE,D_FACTURA.SERIE,D_FACTURA.TOTAL,D_FACTURA.CORELATIVO, "+
                        "D_FACTURA.FEELUUID, D_FACTURA.FECHAENTR "+
                        "FROM D_FACTURA INNER JOIN P_CLIENTE ON D_FACTURA.CLIENTE=P_CLIENTE.CODIGO_CLIENTE "+
                        "WHERE (D_FACTURA.ANULADO=0) AND (D_FACTURA.KILOMETRAJE=0)  " +
                        "ORDER BY D_FACTURA.COREL DESC ";


            //#CKFK 20200520 Quité la anulación de NC porque aquí no existe la tabla

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
                        //sf=DT.getString(2) + " - " + Integer.toString(DT.getInt(4));
                        //#CKFK 20200617 Modifique el formato en el que muestra el documento
                        sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;
                    }else if(tipo==1||tipo==6){
                        sf=DT.getString(0);
                    }else{
                        f=DT.getInt(2);sf=du.sfecha(f)+" "+du.shora(f);
                    }

                    vItem.Fecha=sf;
                    val=DT.getDouble(3);
                    try {
                        sval=mu.frmcur(val);
                    } catch (Exception e) {
                        sval=""+val;
                    }

                    vItem.Valor=sval;

                    if (tipo==4 || tipo==5) vItem.Valor="";

                    items.add(vItem);

                    if (id.equalsIgnoreCase(selid)) selidx=vP;
                    vP+=1;

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
            aplicarPago(itemid);
            listItems();
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Documents

    private void aplicarPago(String itemid) {
        Cursor DT;
        String um;
        int prcant,prod;

        try{
            sql="SELECT PRODUCTO,UMSTOCK,CANT FROM D_FACTURAD WHERE Corel='"+itemid+"'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {
                DT.moveToFirst();
                while (!DT.isAfterLast()) {
                    prod=DT.getInt(0);
                    um=DT.getString(1);
                    prcant=DT.getInt(2);

                    DT.moveToNext();
                }
            }

            sql="SELECT PRODUCTO,UMSTOCK,CANT FROM D_FACTURAS WHERE Corel='"+itemid+"'";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    prod=DT.getInt(0);
                    um=DT.getString(1);
                    prcant=DT.getInt(2);

                    if (app.prodTipo(prod).equalsIgnoreCase("P")) {
                        revertProd(prod,um,prcant);
                    }

                    DT.moveToNext();
                }
            }

            mu.msgbox("Pago aplicado.");

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" : "+e.getMessage());
        }
    }

    private void revertProd(int pcod,String um,int pcant) {

        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",""+pcod);
            ins.add("CANT",0);
            ins.add("CANTM",0);
            ins.add("PESO",0);
            ins.add("plibra",0);
            ins.add("LOTE","");
            ins.add("DOCUMENTO","");

            ins.add("FECHA",0);
            ins.add("ANULADO",0);
            ins.add("CENTRO","");
            ins.add("STATUS","");
            ins.add("ENVIADO",1);
            ins.add("CODIGOLIQUIDACION",0);
            ins.add("COREL_D_MOV", "");
            ins.add("UNIDADMEDIDA", um);

            db.execSQL(ins.sql());

        } catch (Exception e){
            try {
                sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE (CODIGO='"+pcod+"') AND (UNIDADMEDIDA='"+um+"')";
                db.execSQL(sql);
            } catch (Exception ee){
                msgbox(ee.getMessage());
            }
        }

    }

    //endregion

    //region Aux

    private void msgAsk(String msg) {

        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("ROAD");
            dialog.setMessage("¿" + msg  + "?");

            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    pagarDocumento();
                }
            });
            dialog.setNegativeButton("No", null);
            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion


}
