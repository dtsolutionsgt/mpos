package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.ListAdaptMovInv;
import com.dtsgt.mant.Lista;
import com.dtsgt.webservice.wsInventCompartido;

import java.util.ArrayList;
import java.util.Calendar;

public class CajaPagosLista extends PBase {

    private ListView listView;
    private TextView lblDateini,lblDatefin,lblCant,lblTot;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptMovInv adapter;
    private clsClasses.clsCFDV selitem;
    private clsClasses.clsCFDV sitem;

    private AppMethods app;
    private clsRepBuilder rep;

    private int tipo;
    private String itemid;
    private double htot;

    //Fecha
    private boolean dateTxt,report;
    public final Calendar c = Calendar.getInstance();
    private static final String BARRA = "/";
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    public int cyear, cmonth, cday, validCB=0;
    private long datefin,dateini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_caja_pagos_lista);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblDateini = findViewById(R.id.lblDateini2);
            lblDatefin = findViewById(R.id.lblDatefin2);
            lblCant = findViewById(R.id.lblPValor4);
            lblTot = findViewById(R.id.lblPValor3);

            app = new AppMethods(this, gl, Con, db);
            gl.corelmov="";

            setHandlers();
            setFechaAct();

            rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

            ajustaFormato();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void nuevo(View view){
        try {
            startActivity(new Intent(this, CajaPagos.class));
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setHandlers(){
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

                        itemid=vItem.Cod;gl.corelmov=itemid;
                        adapter.setSelectedIndex(position);

                        sitem=vItem;
                    } catch (Exception e) {
                        mu.msgbox( e.getMessage());
                    }
                };
            });

        } catch (Exception e){
        }
    }

    //endregion

    //region Main

    public void listItems() {
        clsP_proveedorObj P_proveedorObj=new clsP_proveedorObj(this,Con,db);
        Cursor DT;
        clsClasses.clsCFDV vItem;
        long f;
        double tot;

        items.clear();
        selidx=-1;tot=0;

        lblCant.setText("Pagos: 0");
        lblTot.setText("Total: "+mu.frmcur(0));

        try {

            sql="SELECT COREL, OBSERVACION, FECHA, MONTO, ITEM, TIPO, ANULADO, VENDEDOR, NODOCUMENTO "+
                "FROM P_cajapagos WHERE (FECHA >="+dateini+") AND (FECHA<="+datefin+") ORDER BY FECHA DESC";

            DT=Con.OpenDT(sql);
            if (DT.getCount()>0) {

                DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    vItem =clsCls.new clsCFDV();

                    vItem.Cod=DT.getString(0);
                    vItem.UUID=DT.getString(1);if (vItem.UUID.length()>1) vItem.UUID="- "+vItem.UUID;
                    vItem.val=DT.getDouble(4);
                    vItem.ival=DT.getInt(5);
                    vItem.Valor=mu.frmcur(DT.getDouble(3));tot+=DT.getDouble(3);

                    f=DT.getLong(2);
                    vItem.Fecha=du.sfecha(f)+"                     Documento: "+DT.getString(8);

                    vItem.UUID= nombreTipo(vItem.ival)+" "+vItem.UUID;

                    items.add(vItem);

                    DT.moveToNext();
                }
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        adapter=new ListAdaptMovInv(this, items);
        listView.setAdapter(adapter);

        lblCant.setText("Pagos: "+adapter.getCount());
        lblTot.setText("Total: "+mu.frmcur(tot));


        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.setSelection(selidx);
        }

        listView.setVisibility(View.VISIBLE);
    }

    //endregion

    //region Fecha

    public void showDateDialog1(View view) {
        try{
            obtenerFecha();
            dateTxt = false;
        }catch (Exception e){
        }

    }

    public void showDateDialog2(View view) {
        try{
            obtenerFecha();
            dateTxt = true;
        }catch (Exception e){
        }

    }

    private void obtenerFecha(){
        try{
            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    final int mesActual = month + 1;

                    String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

                    if(dateTxt) {
                        lblDatefin.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    }

                    if(!dateTxt) {
                        lblDateini.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    }

                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);

                    if(dateTxt) {
                        datefin = du.cfechaRep(cyear, cmonth, cday, false);
                    }

                    if(!dateTxt){
                        dateini  = du.cfechaRep(cyear, cmonth, cday, true);
                    }

                    long fechaSel=du.cfechaSinHora(cyear, cmonth, cday)*10000;

                    if (tipo==3){
                        long fecha_menor=du.addDays(du.getActDate(),-gl.dias_anul);

                        if (fechaSel<fecha_menor){
                            msgbox("La fecha de anulación debe ser mayor a la seleccionada");
                            return;
                        }
                    }

                    //listar nuevamente los documentos
                    listItems();
                }
            },anio, mes, dia);

            report=false;

            recogerFecha.show();

        }catch (Exception e){
        }

    }

    private void setFechaAct(){
        Long fecha,fi;
        String date,di;

        try{
            fecha = du.getFechaActualReport();fi=du.addDays(fecha,-7);
            date = du.univfechaReport(fecha);di=du.univfechaReport(fi);

            lblDateini.setText(di);
            lblDatefin.setText(date);

            //datefin = du.getFechaActualReport(false);
            //dateini = du.getFechaActualReport(true);
            dateini = du.cfechaRep((int) du.getyear(fi),(int) du.getmonth(fi),(int) du.getday(fi),true);
            datefin = du.cfechaRep((int) du.getyear(fecha),(int) du.getmonth(fecha),(int) du.getday(fecha),false);

        } catch (Exception e){
        }
    }

    //endregion

    //region Impresion

    private void generarImpresion(){
        int aid=0;

        try {

            rep.clear();

            switch (tipo) {
                case 0: // Ingreso de mercancía
                    aid=0;break;
                case 1: // Ajuste de inventario
                    aid=0;break;
                case 4: // Ingreso de mercancía
                    aid=gl.idalm;break;
                case 5: // Ajuste de inventario
                    aid=gl.idalm;break;
                case 6: // Egreso de almacen
                    aid=0;break;
                case 7: // Egreso de almacen
                    aid=gl.idalm;break;
                case 8: // Traslado entre almacenes
                    aid=gl.idalm;break;
            }

            impresionEncabezado(aid);
            impresionDetalle(aid);

            rep.line();
            rep.empty();
            rep.addtote("Valor total: ",mu.frmcur(htot));
            rep.empty();
            rep.empty();
            rep.empty();

            rep.save();

            app.printView();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void impresionEncabezado(int aid) {
        long hfecha;
        int huser,hidprov,hanul,hidao=0,hidad=0;
        String htipo,href,tn="";

        if (aid==0) {
            clsD_MovObj D_movObj=new clsD_MovObj(this,Con,db);
            D_movObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            hfecha=D_movObj.first().FECHA;
            htipo=D_movObj.first().TIPO;
            huser=D_movObj.first().USUARIO;
            href=D_movObj.first().REFERENCIA;
            hidprov=D_movObj.first().CODIGO_PROVEEDOR;
            htot=D_movObj.first().TOTAL;
            hanul=D_movObj.first().ANULADO;

        } else {
            clsD_mov_almacenObj D_movObj=new clsD_mov_almacenObj(this,Con,db);
            D_movObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            hfecha=D_movObj.first().fecha;
            htipo=D_movObj.first().tipo;
            huser=D_movObj.first().usuario;
            href=D_movObj.first().referencia;
            hidprov=D_movObj.first().codigo_proveedor;
            htot=D_movObj.first().total;
            hanul=D_movObj.first().anulado;
            hidao=D_movObj.first().almacen_origen;
            hidad=D_movObj.first().almacen_destino;

        }

        if (htipo.equalsIgnoreCase("R")) tn="INGRESO DE MERCANCIA";
        if (htipo.equalsIgnoreCase("D")) tn="AJUSTE DE INVENTARIO";
        if (htipo.equalsIgnoreCase("E")) tn="EGRESO DE MERCANCIA";
        if (htipo.equalsIgnoreCase("T")) tn="TRASLADO ENTRE ALMACENES";

        rep.empty();
        rep.empty();
        rep.addc(gl.empnom);
        rep.addc(gl.tiendanom);
        rep.empty();
        rep.addc(tn);
        rep.empty();
        rep.add("Numero: "+gl.corelmov+" "+((hanul==1)?"ANULADO":""));
        rep.add("Fecha: "+du.sfecha(hfecha)+" "+du.shora(hfecha));
        rep.add("Operador: "+nombreOperador(huser));


        switch (tipo) {
            case 0: // Ingreso de mercancía
                rep.add("Ref: "+href);break;
            case 1: // Ajuste de inventario
                break;

        }

        rep.empty();
        rep.add3lrr("Cantidad","Costo  ","Valor");
        rep.line();
    }

    private void impresionDetalle(int aid) {
        String dum;
        int dprid;
        double dcant,dprec,dtot;

        if (aid==0) {
            clsD_MovDObj D_movdObj=new clsD_MovDObj(this,Con,db);
            D_movdObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            for (int i = 0; i <D_movdObj.count; i++) {
                dum=D_movdObj.items.get(i).unidadmedida;
                dcant=D_movdObj.items.get(i).cant;
                dprec=D_movdObj.items.get(i).precio;
                dtot=Math.abs(dcant*dprec);

                rep.add(app.prodNombre(D_movdObj.items.get(i).producto));
                rep.add3lrre(mu.frmdecno(dcant)+" "+dum,dprec,dtot);
            }

        } else {

            clsD_movd_almacenObj D_movdObj=new clsD_movd_almacenObj(this,Con,db);
            D_movdObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            for (int i = 0; i <D_movdObj.count; i++) {
                dum=D_movdObj.items.get(i).unidadmedida;
                dcant=D_movdObj.items.get(i).cant;
                dprec=D_movdObj.items.get(i).precio;
                dtot=Math.abs(dcant*dprec);

                rep.add(app.prodNombre(D_movdObj.items.get(i).producto));
                rep.add3lrre(mu.frmdecno(dcant)+" "+dum,dprec,dtot);
            }

        }
    }

    //endregion

    //region Dialogs

    private void msgAskImprimir(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inventario");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Ver", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                browse=2;
                startActivity(new Intent(CajaPagosLista.this, InvRecep.class));
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.setNeutralButton("Imprimir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    //endregion

    //region Aux

    private String nombreOperador(int idoper) {
        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE (CODIGO_VENDEDOR="+idoper+")");
            return VendedoresObj.first().nombre;
        } catch (Exception e) {
            return " ";
        }
    }

    private String nombreTipo(int idtipo) {
        Cursor DT;
        String iname;

        try {
            sql="SELECT NOMBRE FROM P_CONCEPTOPAGO WHERE CODIGO="+idtipo;
            DT=Con.OpenDT(sql);
            DT.moveToFirst();
            iname=DT.getString(0);

            if (DT!=null) DT.close();

            return iname;
        } catch (Exception e) {
            return " ";
        }
    }

    private void ajustaFormato() {
        try {

            sql="UPDATE P_cajapagos SET referencia=proveedor  WHERE referencia is null";
            db.execSQL(sql);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        if (browse==1) {
            browse=0;
            if (!gl.pickcode.isEmpty()) {
                try {
                    gl.codigo_proveedor=Integer.parseInt(gl.pickcode);
                    gl.nombre_proveedor=gl.pickname;
                    startActivity(new Intent(this, InvRecep.class));
                    finish();
                } catch (NumberFormatException e) {
                    msgbox("Codigo de proveedor incorrecto : "+gl.pickcode);
                }
            }
            return;
        }

        listItems();

    }

    //endregion

}