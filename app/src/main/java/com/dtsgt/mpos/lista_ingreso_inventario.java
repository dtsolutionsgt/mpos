package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.ListAdaptCFDV;
import com.dtsgt.ladapt.ListAdaptMovInv;
import com.dtsgt.mant.Lista;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInventCompartido;

import java.util.ArrayList;
import java.util.Calendar;

public class lista_ingreso_inventario extends PBase {

    private ListView listView;
    private TextView lblTipo;
    private ProgressBar pbar;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptMovInv adapter;
    private clsClasses.clsCFDV selitem;
    private clsClasses.clsCFDV sitem;

    private AppMethods app;
    private wsInventCompartido wsi;
    private Runnable recibeInventario;

    private int tipo;
    private String itemid;

    //Fecha
    private boolean dateTxt,report,bloqueado=false;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ingreso_inventario);

        super.InitBase();
        addlog("lista_ingreso_inventario",""+du.getActDateTime(),String.valueOf(gl.vend));

        listView = (ListView) findViewById(R.id.listView1);
        lblTipo= (TextView) findViewById(R.id.lblDescrip2);
        lblDateini = (TextView) findViewById(R.id.lblDateini2);
        lblDatefin = (TextView) findViewById(R.id.lblDatefin2);
        pbar=findViewById(R.id.progressBar6);pbar.setVisibility(View.INVISIBLE);

        app = new AppMethods(this, gl, Con, db);

        tipo=gl.tipo;
        if (tipo==0) lblTipo.setText("Ingreso de mercancía");
        if (tipo==1) lblTipo.setText("Ajuste de inventario");
        if (tipo==2) lblTipo.setText("Inventario Inicial");
        if (tipo==3) lblTipo.setText("Orden de compra");

        gl.corelmov="";

        setHandlers();

        setFechaAct();

        listItems();

        app.getURL();
        wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        recibeInventario = new Runnable() {
            public void run() {
                bloqueado=false;
                if (wsi.errflag) msgbox(wsi.error); else confirmaInventario();
                pbar.setVisibility(View.INVISIBLE);
            }
        };

        bloqueado=false;
        if (gl.peInvCompart) {
            pbar.setVisibility(View.VISIBLE);
            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    bloqueado=true;
                    wsi.execute(recibeInventario);
                }
            };
            mtimer.postDelayed(mrunner,200);
        }

    }

    private void confirmaInventario() {
        try {
            Intent intent = new Intent(lista_ingreso_inventario.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);
        } catch (Exception e) {}
    }

    //region Events

    public void nuevo(View view){
        Intent intent;

        if (bloqueado) {
            toast("Actualizando inventario . . .");return;
        }

        gl.corelmov="";

        try {

            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            gl.codigo_proveedor=suc.first().codigo_proveedor;
            gl.nombre_proveedor="Completo";

            switch (tipo){
                case 0:
                    browse=1;
                    gl.listaedit=false;
                    gl.mantid = 9;
                    startActivity(new Intent(this,Lista.class));
                    break;
                case 1:
                    gl.closeDevBod=false;
                    intent = new Intent(this,InvSalida.class);
                    startActivity(intent);
                    break;
                case 2:
                    validaInicial();break;
                case 3:
                    startActivity( new Intent(this,InvSalida.class));
                    break;
            }

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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
                        /*
                        if (vItem.val==0) {
                            gl.corelmov="";
                        } else {
                            if (tipo==0) msgAskResume("Continuar o borrar");
                        }

                         */
                        msgAskResume("Continuar o borrar");
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    if (bloqueado) {
                        toast("Actualizando inventario . . .");return true;
                    }

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

                        itemid=vItem.Cod;
                        adapter.setSelectedIndex(position);
                        sitem=vItem;

                        cargaDocumento(itemid);

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                    return true;
                }
            });

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Main

    public void listItems() {
        clsP_proveedorObj P_proveedorObj=new clsP_proveedorObj(this,Con,db);
        Cursor DT;
        clsClasses.clsCFDV vItem;
        long f,vP;
        double val;
        String id,sf,sval;

        items.clear();
        selidx=-1;vP=0;

        try {

            switch (tipo){

                case 0:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,IMPRES,CODIGO_PROVEEDOR "+
                        "FROM D_MOV WHERE (TIPO='R') AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                        "ORDER BY FECHA DESC ";
                    break;
                case 1:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,IMPRES,0 "+
                            "FROM D_MOV WHERE (TIPO='D') AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 2:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,IMPRES,0 "+
                            "FROM D_MOV WHERE (TIPO='I') AND (ANULADO<>1) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 3:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,0,0 "+
                            "FROM D_COMPRA_LOCAL WHERE ((ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"')) " +
                            "ORDER BY FECHA DESC ";
                    break;
            }

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    id=DT.getString(0);

                    vItem =clsCls.new clsCFDV();

                    vItem.Cod=DT.getString(0);
                    vItem.UUID=DT.getString(1);
                    vItem.val=DT.getDouble(4);

                    f=DT.getLong(2);
                    sf=du.sfecha(f)+" "+du.shora(f);
                    vItem.Fecha=sf;
                    if (vItem.val==0) vItem.Valor="";else  vItem.Valor="PENDIENTE";

                    if (tipo==0) {
                        P_proveedorObj.fill("WHERE (CODIGO_PROVEEDOR="+DT.getInt(5)+")");
                        if (P_proveedorObj.count>0)  vItem.UUID+=" - "+P_proveedorObj.first().nombre;
                    }

                    items.add(vItem);vP+=1;

                    DT.moveToNext();
                }
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox(e.getMessage());
        }

        adapter=new ListAdaptMovInv(this, items);
        listView.setAdapter(adapter);

        if (selidx>-1) {
            adapter.setSelectedIndex(selidx);
            listView.setSelection(selidx);
        }

        listView.setVisibility(View.VISIBLE);
    }

    //endregion

    //region Documents

    private void cargaDocumento(String itemid) {

        if (tipo==2 && sitem.Desc.equalsIgnoreCase("ACTIVO")) {
            abrirInicial();return;
        }

        try {
            gl.idmov=itemid;
            startActivity(new Intent(this,InvVista.class));
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
        }
    }

    //endregion

    //region Fecha

    public void showDateDialog1(View view) {
        try{
            obtenerFecha();
            dateTxt = false;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void showDateDialog2(View view) {
        try{
            obtenerFecha();
            dateTxt = true;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setFechaAct(){
        Long fecha;
        String date;

        try{
            fecha = du.getFechaActualReport();

            date = du.univfechaReport(fecha);

            lblDateini.setText(date);
            lblDatefin.setText(date);

            datefin = du.getFechaActualReport(false);
            dateini = du.getFechaActualReport(true);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Dialogs

    private void msgAskResume(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Ingreso de mercancia");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                browse=2;
                startActivity(new Intent(lista_ingreso_inventario.this,InvInicial.class));
            }
        });

        dialog.setNeutralButton("Borrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskDelete("Está seguro");
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskDelete(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Ingreso de mercanci");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                borrarIngreso();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Aux

    private void validaInicial() {
        try {
            clsT_movdObj T_movdObj=new clsT_movdObj(this,Con,db);
            T_movdObj.fill();
            if (T_movdObj.count>0) {
                msgbox("No se puede crear inventario inicial, ya existe uno activo");return;
            }


            clsP_stockObj P_stockObj=new clsP_stockObj(this,Con,db);
            P_stockObj.fill();

            if (P_stockObj.count>0) {
                for (int i = 0; i <P_stockObj.count; i++) {
                    if (P_stockObj.items.get(i).cant>0) {
                        msgbox("No se puede crear inventario inicial, hay productos con existencia");return;
                    }
                }
            }

            abrirInicial();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void abrirInicial() {
        try {
            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            gl.codigo_proveedor=suc.first().codigo_proveedor;
            gl.nombre_proveedor="Completo";

            gl.invregular=false;
            startActivity(new Intent(this,InvInicial.class));

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void borrarIngreso() {
        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM D_MOV WHERE COREL='"+gl.corelmov+"'");
            db.execSQL("DELETE FROM D_MOVD WHERE COREL='"+gl.corelmov+"'");

            db.setTransactionSuccessful();
            db.endTransaction();

            listItems();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
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
                    startActivity(new Intent(this,InvInicial.class));
                } catch (NumberFormatException e) {
                    msgbox("Codigo de proveedor incorrecto : "+gl.pickcode);
                }
            }
            return;
        }

        listItems();
    }

    @Override
    public void onBackPressed() {
        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}