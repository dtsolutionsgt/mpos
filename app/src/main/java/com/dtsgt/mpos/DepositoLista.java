package com.dtsgt.mpos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsDocDeposito;
import com.dtsgt.classes.clsP_bancoObj;
import com.dtsgt.classes.clsP_depositoObj;
import com.dtsgt.ladapt.LA_P_deposito;

import java.util.Calendar;

public class DepositoLista extends PBase {

    private ListView listView;

    private clsP_depositoObj P_depositoObj;
    private clsP_bancoObj P_bancoObj;

    private LA_P_deposito adapter;

    private clsDocDeposito ddoc;


    //Fecha
    private boolean fechfinal;
    private TextView lblDateini,lblDatefin;
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
            setContentView(R.layout.activity_deposito_lista);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblDateini = findViewById(R.id.lblDateini2);
            lblDatefin = findViewById(R.id.lblDatefin2);

            P_depositoObj=new clsP_depositoObj(this,Con,db);
            P_bancoObj=new clsP_bancoObj(this,Con,db);P_bancoObj.fill();

            printer prn=new printer(this,null,gl.validimp);
            ddoc=new clsDocDeposito(this,prn.prw,gl.ruta,gl.vendnom,gl.peMon,gl.peDecImp, "");

            setHandlers();
            setFechaAct();

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doAdd(View v) {
        browse=1;
        startActivity(new Intent(this,Deposito.class)); ;
    }

    public void doExit(View v) {
        finish();
    }

    private void setHandlers(){
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsP_deposito item = (clsClasses.clsP_deposito)lvObj;

                        selid=item.codigo_deposito;
                        adapter.setSelectedIndex(position);
                        printDocument(selid);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+": "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void listItems() {
        try {

            datefin=du.ffecha24(datefin);
            P_depositoObj.fill("WHERE (FECHA>="+dateini+") AND (FECHA<="+datefin+") ORDER BY FECHA DESC");

            for (clsClasses.clsP_deposito itm : P_depositoObj.items) {
                itm.sfecha=du.sfecha(itm.fecha)+ " "+ du.shora(itm.fecha);
                itm.stotal=mu.frmcur(itm.monto_total);
                itm.cuenta=nombreBanco(itm.codigo_banco) + " - " +itm.cuenta;
            }

            adapter=new LA_P_deposito(this,this,P_depositoObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void printDocument(int depid) {
        try {
            ddoc.buildPrint(""+depid,0,gl.peModal);

            browse=0;
            startActivity(new Intent(this,PrintView.class));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Fecha

    public void showDateDialog1(View view) {
        try{

            fechfinal =false;
            obtenerFecha();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void showDateDialog2(View view) {
        try{

            fechfinal =true;
            obtenerFecha();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void obtenerFecha() {
        long dfecha;

        try {

            if (fechfinal) dfecha = datefin; else  dfecha  = dateini;

            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    final int mesActual = month + 1;

                    String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

                    if (fechfinal) lblDatefin.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    if (!fechfinal) lblDateini.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);

                    if (fechfinal) {
                        datefin = du.cfechaRep(cyear, cmonth, cday, false);
                    } else {
                        dateini  = du.cfechaRep(cyear, cmonth, cday, true);
                    }

                    listItems();
                }
            } , (int) du.getyear(dfecha), (int) du.getmonth(dfecha)-1,  (int) du.getday(dfecha));

            recogerFecha.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setFechaAct(){
        Long fecha,fechaini;

        try {
            fecha = du.getFechaActualReport();
            fechaini=du.addDays(fecha,-7);

            lblDateini.setText(du.univfechaReport(fechaini));
            lblDatefin.setText(du.univfechaReport(fecha));

            dateini = fechaini;
            datefin = fecha;

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Aux

    private String nombreBanco(int idbanco) {
         try {
            for (clsClasses.clsP_banco itm : P_bancoObj.items) {
                if (itm.codigo_banco==idbanco) {
                    return itm.nombre;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return "";
    }


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_depositoObj.reconnect(Con,db);
            P_bancoObj.reconnect(Con,db);

            if (browse==1) {
                browse=0;
                if (gl.gint>0) {
                    listItems();
                    printDocument(gl.gint);
                }
            }

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}