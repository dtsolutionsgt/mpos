package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialogT;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.ListAdaptMovInv;
import com.dtsgt.mant.Lista;
import com.dtsgt.webservice.srvInventConfirm;
import com.dtsgt.webservice.wsInventCompartido;

import java.util.ArrayList;
import java.util.Calendar;

public class ListaInventario extends PBase {

    private ListView listView;
    private TextView lblTipo,lblalm;
    private ProgressBar pbar;
    private RelativeLayout relalm;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptMovInv adapter;
    private clsClasses.clsCFDV selitem;
    private clsClasses.clsCFDV sitem;
    private clsP_almacenObj P_almacenObj;

    private AppMethods app;
    private wsInventCompartido wsi;
    private Runnable recibeInventario;
    private clsRepBuilder rep;

    private int tipo,idalmdpred,idalm;
    private String itemid;
    double htot;
    boolean almacenes;

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
        setContentView(R.layout.activity_lista_inventario);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        lblTipo = findViewById(R.id.lblDescrip2);
        lblalm = findViewById(R.id.textView268);
        lblDateini = findViewById(R.id.lblDateini2);
        lblDatefin = findViewById(R.id.lblDatefin2);
        relalm= findViewById(R.id.relalm1);
        pbar=findViewById(R.id.progressBar6);pbar.setVisibility(View.INVISIBLE);

        app = new AppMethods(this, gl, Con, db);

        tipo=gl.tipo;
        if (tipo==0) lblTipo.setText("Ingreso de mercancía");
        if (tipo==1) lblTipo.setText("Ajuste de inventario");
        if (tipo==2) lblTipo.setText("Inventario Inicial");
        if (tipo==3) lblTipo.setText("Orden de compra");
        if (tipo==4) lblTipo.setText("Ingreso de mercancía");
        if (tipo==5) lblTipo.setText("Ajuste de inventario");
        if (tipo==6) lblTipo.setText("Egreso de almacen");
        if (tipo==7) lblTipo.setText("Egreso de almacen");
        if (tipo==8) lblTipo.setText("Traslado entre almacenes");

        /*
        if (tipo==4) lblTipo.setText("Ingreso de mercancía - "+gl.nom_alm);
        if (tipo==5) lblTipo.setText("Ajuste de inventario - "+gl.nom_alm);
        if (tipo==6) lblTipo.setText("Egreso de almacen - "+gl.nom_alm);
        if (tipo==7) lblTipo.setText("Egreso de almacen - "+gl.nom_alm);
        if (tipo==8) lblTipo.setText("Traslado entre almacenes - "+gl.nom_alm);
        */

        gl.corelmov="";

        setHandlers();
        setFechaAct();

        app.getURL();
        wsi=new wsInventCompartido(this,gl.wsurl,gl.emp,gl.codigo_ruta,db,Con);

        P_almacenObj=new clsP_almacenObj(this,Con,db);
        almacenes=tieneAlmacenes();if (!almacenes) relalm.setVisibility(View.GONE);

        if (almacenes) {
            if (tipo==0) tipo=4;
            if (tipo==1) tipo=5;
            if (tipo==6) tipo=7;
        }

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

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
            Intent intent = new Intent(ListaInventario.this, srvInventConfirm.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("idstock",wsi.idstock);
            startService(intent);
        } catch (Exception e) {}
    }

    //region Events

    public void nuevo(View view){
        Intent intent;

        if (bloqueado) {
           //toast("Actualizando inventario . . .");return;
        }

        gl.corelmov="";

        if (almacenes) {

            if (gl.idalm==0) {
                toast("Falta seleccionar un almacen");
                listaAlmacenes();
                return;
            }

            if (tipo==7) tipo=6;

            switch (tipo) {
                case 4:
                    if (gl.idalm==idalmdpred) gl.tipo=0; else gl.tipo=4;
                    break;
                case 5:
                    if (gl.idalm==idalmdpred) gl.tipo=1;else gl.tipo=5;
                    break;
                case 6:
                    if (gl.idalm==idalmdpred) {
                        gl.tipo=6;
                    } else {
                        gl.tipo=7;
                    }
                    if (!almacenes) {
                        gl.tipo=6;
                    }
                    int itipo=gl.tipo;
                    break;
            }

        }

        tipo=gl.tipo;

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
                    intent = new Intent(this, InvAjuste.class);
                    startActivity(intent);
                    break;
                case 2:
                    validaInicial();break;
                case 3:
                    startActivity( new Intent(this, InvAjuste.class));
                    break;
                case 4:
                    browse=1;
                    gl.listaedit=false;
                    gl.mantid = 9;
                    startActivity(new Intent(this,Lista.class));
                    break;
                case 5:
                    gl.closeDevBod=false;
                    intent = new Intent(this, InvAjuste.class);
                    startActivity(intent);
                    break;
                case 6:
                    gl.tipo=6;
                    startActivity( new Intent(this, InvEgreso.class));
                    break;
                case 7:
                    gl.tipo=7;
                    startActivity( new Intent(this, InvEgreso.class));
                    break;
                case 8:
                    listaAlmacenes();
                    break;
            }

            finish();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void doAlmacenes(View view){
        listaAlmacenes();
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

                        if (tipo==0 | tipo==4) {
                            if (sitem.ival==-1) {
                                msgAskResume("Reanundar ingreso de mercancia");
                            } else {
                                generarImpresion();
                            }
                        } else {
                            generarImpresion();
                        }
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
                        /*
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsCFDV vItem = (clsClasses.clsCFDV)lvObj;

                        itemid=vItem.Cod;
                        adapter.setSelectedIndex(position);
                        sitem=vItem;

                        cargaDocumento(itemid);
                        */
                    } catch (Exception e) {
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
        int idprov,idalm,idalm2;

        items.clear();
        selidx=-1;vP=0;

        try {

            if (almacenes) {
                if (tipo==4 && gl.idalm==idalmdpred) tipo=0;
                if (tipo==0 && gl.idalm!=idalmdpred) tipo=4;
                if (tipo==5 && gl.idalm==idalmdpred) tipo=1;
                if (tipo==1 && gl.idalm!=idalmdpred) tipo=5;
                //if (tipo==7 && gl.idalm==idalmdpred) tipo=6;
                //if (tipo==6 && gl.idalm!=idalmdpred) tipo=7;
                if (tipo==6) tipo=7;
            } else {
                if (tipo==7) tipo=6;
            }

            if (tipo==8) P_almacenObj.fill();

            switch (tipo){

                case 0:
                    sql="SELECT COREL, REFERENCIA, FECHA,0,IMPRES, CODIGO_PROVEEDOR, ANULADO, 0 "+
                        "FROM D_MOV WHERE (TIPO='R') AND (ANULADO<1) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                        "ORDER BY FECHA DESC ";
                    break;
                case 1:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,IMPRES,0, 0, 0 "+
                            "FROM D_MOV WHERE (TIPO='D') AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 2:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,IMPRES,0, 0, 0 "+
                            "FROM D_MOV WHERE (TIPO='I') AND (ANULADO<>1) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 3:
                    sql="SELECT COREL,REFERENCIA,FECHA,0,0,CODIGO_PROVEEDOR, 0, 0 "+
                            "FROM D_COMPRA_LOCAL WHERE ((ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"')) " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 4:
                    sql="SELECT COREL, REFERENCIA, FECHA, 0, IMPRES, CODIGO_PROVEEDOR, ANULADO, 0 "+
                            "FROM D_MOV_ALMACEN WHERE (ALMACEN_DESTINO="+gl.idalm+") AND (TIPO='R') " +
                            "AND (ANULADO<1) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 5:
                    sql="SELECT COREL, REFERENCIA, FECHA, 0, IMPRES, CODIGO_PROVEEDOR, 0, 0 "+
                            "FROM D_MOV_ALMACEN WHERE (ALMACEN_DESTINO="+gl.idalm+") AND (TIPO='D') " +
                            "AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 6:
                    sql="SELECT COREL, REFERENCIA, FECHA, 0, IMPRES, CODIGO_PROVEEDOR, 0, 0 "+
                            "FROM D_MOV WHERE (TIPO='E') " +
                            "AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 7:
                    sql="SELECT COREL, REFERENCIA, FECHA, 0, IMPRES, CODIGO_PROVEEDOR, 0, 0 "+
                            "FROM D_MOV_ALMACEN WHERE (ALMACEN_DESTINO="+gl.idalm+") AND (TIPO='E') " +
                            "AND (ANULADO=0) AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
                            "ORDER BY FECHA DESC ";
                    break;
                case 8:
                    sql="SELECT COREL, REFERENCIA, FECHA, 0, IMPRES, CODIGO_PROVEEDOR, ALMACEN_ORIGEN, ALMACEN_DESTINO "+
                            "FROM D_MOV_ALMACEN WHERE  (TIPO='T') AND (ANULADO=0) " +
                            "AND ((ALMACEN_DESTINO="+gl.idalm+") OR (ALMACEN_ORIGEN="+gl.idalm+")) " +
                            "AND (FECHA BETWEEN '"+dateini+"' AND '"+datefin+"') " +
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
                    vItem.ival=DT.getInt(6);
                    vItem.Valor=DT.getString(0);

                    f=DT.getLong(2);
                    sf=du.sfecha(f)+" "+du.shora(f);
                    vItem.Fecha=sf;

                    try {
                        if (tipo==0 | tipo==4) {
                            vItem.UUID="Doc: "+vItem.UUID;
                            if (vItem.ival==-1) vItem.UUID+=" PENDIENTE";

                            idprov=DT.getInt(5);
                            P_proveedorObj.fill("WHERE (CODIGO_PROVEEDOR="+idprov+")");
                            if (P_proveedorObj.count>0) vItem.UUID+="- Proveedor: "+P_proveedorObj.first().nombre;
                        }
                    } catch (Exception e) {}

                    if (tipo==8) {
                        idalm=DT.getInt(6);
                        idalm2=DT.getInt(7);
                        vItem.UUID="De: "+nombreAlmacen(idalm)+" a: "+nombreAlmacen(idalm2);
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
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Impresion

    private void generarImpresion(){
        int aid=0;

        try {

            rep.clear();;

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
        rep.add("Fecha: "+du.sfecha(hfecha));
        rep.add("Operador: "+nombreOperador(huser));


        switch (tipo) {
            case 0: // Ingreso de mercancía
                rep.add("Proveedor: "+nombreProveedor(hidprov));
                rep.add("Ref: "+href);break;
            case 1: // Ajuste de inventario
                break;
            case 4: // Ingreso de mercancía
                rep.add("Almacen: "+nombreAlmacen(hidad));
                rep.add("Proveedor: "+nombreProveedor(hidprov));
                rep.add("Ref: "+href);break;
            case 5: // Ajuste de inventario
                rep.add("Almacen: "+nombreAlmacen(hidad));break;
            case 6: // Egreso de almacen
                break;
            case 7: // Egreso de almacen
                rep.add("Almacen: "+nombreAlmacen(hidad));break;
            case 8: // Traslado entre almacenes
                rep.add("Almacen origen: "+nombreAlmacen(hidao));
                rep.add("Almacen destino: "+nombreAlmacen(hidad));break;
        }

        rep.empty();
        rep.add3lrr("Cantidad","Costo","Valor");
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

    private void msgAskResume(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Ingreso de mercancia");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Reanundar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                browse=2;
                startActivity(new Intent(ListaInventario.this, InvRecep.class));
            }
        });

        dialog.setNeutralButton("Imprimir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                generarImpresion();
            }
        });

        dialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });


        dialog.show();

    }

    private void msgAskDelete(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inventario");
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

    private void msgAskImprimir(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inventario");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Ver", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                browse=2;
                startActivity(new Intent(ListaInventario.this, InvRecep.class));
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
            startActivity(new Intent(this, InvRecep.class));

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

    /*
    private void listaAlmacenes() {

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE (ACTIVO=1) AND (CODIGO_ALMACEN<>"+gl.idalm+") ORDER BY NOMBRE");

            int itemcnt=P_almacenObj.count;
            String[] selitems = new String[itemcnt];
            int[] selcod = new int[itemcnt];

            for (int i = 0; i <itemcnt; i++) {
                selitems[i] = P_almacenObj.items.get(i).nombre;
                selcod[i] = P_almacenObj.items.get(i).codigo_almacen;
            }

            ExDialogT menudlg = new ExDialogT(this);
            menudlg.setTitle("Almacen destino");

            menudlg.setItems(selitems ,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    gl.idalm2=selcod[item];gl.nom_alm2=selitems[item];

                    if (gl.idalm!=gl.idalm2) {
                        Intent intent = new Intent(ListaInventario.this, InvTrans.class);
                        startActivity(intent);
                    } else {
                        msgbox("Almacen destino incorrecto");
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

            final AlertDialog Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }
    */

    private String nombreAlmacen(int idalm) {
        try {
            for (int i = 0; i <P_almacenObj.count; i++) {
                if (P_almacenObj.items.get(i).codigo_almacen==idalm) return P_almacenObj.items.get(i).nombre;
            }
        } catch (Exception e) { }
        return " ";
    }

    private String nombreProveedor(int idprov) {
        try {
            clsP_proveedorObj P_proveedorObj=new clsP_proveedorObj(this,Con,db);
            P_proveedorObj.fill("WHERE (CODIGO_PROVEEDOR="+idprov+")");
            return P_proveedorObj.first().nombre;
        } catch (Exception e) {
            return " ";
        }
    }

    private String nombreOperador(int idoper) {
        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE (CODIGO_VENDEDOR="+idoper+")");
            return VendedoresObj.first().nombre;
        } catch (Exception e) {
            return " ";
        }
    }

    public boolean tieneAlmacenes() {

        gl.idalmpred =0;gl.idalm=0;
        idalmdpred=0;

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1");

            if (P_almacenObj.count<2) return false;

            idalmdpred=P_almacenObj.first().codigo_almacen;gl.idalmpred =idalmdpred;

            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) {
                idalmdpred=P_almacenObj.first().codigo_almacen;
                gl.idalmpred =idalmdpred;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    public void listaAlmacenes() {
        String titulo="Seleccione un almacen";

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");

            int itemcnt=P_almacenObj.count;
            String[] selitems = new String[itemcnt];
            int[] selcod = new int[itemcnt];

            for (int i = 0; i <itemcnt; i++) {
                selitems[i] = P_almacenObj.items.get(i).nombre;
                selcod[i] = P_almacenObj.items.get(i).codigo_almacen;
            }

            ExDialogT menudlg = new ExDialogT(this);
            menudlg.setTitle(titulo);

            menudlg.setItems(selitems ,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        idalm=selcod[item];gl.idalm=idalm;
                        gl.nom_alm=selitems[item];
                        lblalm.setText(gl.nom_alm);
                        listItems();
                    } catch (Exception e) {
                        msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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

            final AlertDialog Dialog = menudlg.create();
            Dialog.show();

            Button nbutton = Dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setBackgroundColor(Color.parseColor("#1A8AC6"));
            nbutton.setTextColor(Color.WHITE);

        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        try {
            P_almacenObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (!gl.pickcode.isEmpty()) {
                try {
                    gl.codigo_proveedor=Integer.parseInt(gl.pickcode);
                    gl.nombre_proveedor=gl.pickname;
                    startActivity(new Intent(this, InvRecep.class));
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