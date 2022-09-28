package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsP_proveedorObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.ladapt.ListAdaptMovInv;
import com.dtsgt.mant.Lista;
import java.util.ArrayList;
import java.util.Calendar;

public class ListaCompras extends PBase {

    private ListView listView;
    private TextView lblTipo, lblest;
    private ProgressBar pbar;
    private RelativeLayout relalm;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptMovInv adapter;
    private clsClasses.clsCFDV selitem;
    private clsClasses.clsCFDV sitem;

    private AppMethods app;
    private clsRepBuilder rep;

    private int tipo,idalmdpred,idalm,idest;
    private String itemid;
    private double htot;

    //Fecha
    private boolean dateTxt,report;
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
        setContentView(R.layout.activity_lista_compras);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        lblTipo = findViewById(R.id.lblDescrip2);
        lblest = findViewById(R.id.textView268);
        lblDateini = findViewById(R.id.lblDateini2);
        lblDatefin = findViewById(R.id.lblDatefin2);
        relalm= findViewById(R.id.relalm1);
        pbar=findViewById(R.id.progressBar6);pbar.setVisibility(View.INVISIBLE);

        app = new AppMethods(this, gl, Con, db);



        gl.corelmov="";

        setHandlers();
        setFechaAct();

        app.getURL();

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

    }


    //region Events

    public void nuevo(View view){
        Intent intent;

        gl.corelmov="";

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
                    startActivity(new Intent(this, InvAjuste.class));
                    finish();
                    break;
                case 2:
                    validaInicial();break;
                case 3:
                    startActivity( new Intent(this, InvAjuste.class));
                    finish();
                    break;
                case 4:
                    browse=1;
                    gl.listaedit=false;
                    gl.mantid = 9;
                    startActivity(new Intent(this,Lista.class));
                    break;
                case 5:
                    gl.closeDevBod=false;
                    startActivity(new Intent(this, InvAjuste.class));
                    finish();
                    break;
                case 6:
                    gl.tipo=6;
                    startActivity( new Intent(this, InvEgreso.class));
                    finish();
                    break;
                case 7:
                    gl.tipo=7;
                    startActivity( new Intent(this, InvEgreso.class));
                    finish();
                    break;
                case 8:
                    listaEstados();
                    break;
            }


        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void doAlmacenes(View view){
        listaEstados();
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
        rep.add("Fecha: "+du.sfecha(hfecha)+" "+du.shora(hfecha));
        rep.add("Operador: "+nombreOperador(huser));

        rep.add("Proveedor: "+nombreProveedor(hidprov));
        rep.add("Ref: "+href);

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

    private void msgAskResume(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Ingreso de mercancia");
        dialog.setMessage("¿" + msg + "?");

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

    public void listaEstados() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ListaCompras.this,"Seleccione un estado");

            listdlg.add("NUEVO");
            listdlg.add("EN PROCESO");
            listdlg.add("COMPLETO");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        try {
                            gl.nom_est=listdlg.getText(position);
                            lblest.setText(gl.nom_est);
                            listItems();
                        } catch (Exception e) {
                            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
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