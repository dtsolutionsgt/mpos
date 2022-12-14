package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.SwipeListener;
import com.dtsgt.classes.clsD_facturaObj;
import com.dtsgt.classes.clsDocFactura;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.ladapt.ListAdaptCFDV;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class MarcarFacturas extends PBase {

    private ListView listView;
    private TextView lblDateini,lblDatefin,lblcant;
    private EditText txt1;

    private ArrayList<clsClasses.clsCFDV> items= new ArrayList<clsClasses.clsCFDV>();
    private ListAdaptCFDV adapter;
    private clsClasses.clsCFDV selitem;

    private clsRepBuilder rep;
    private clsDocFactura fdoc;

    private clsClasses.clsCFDV sitem;
    private AppMethods app;

    private String CSQL;
    private boolean factsend,bloqueado=false;

    private int tipo,depparc,fcorel;
    private String selid,itemid,pass,fserie,fres,felcorel,uuid;
    private boolean demomode;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_facturas);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        lblDateini = findViewById(R.id.lblDateini2);
        lblDatefin = findViewById(R.id.lblDatefin2);
        lblcant = findViewById(R.id.textView208);

        txt1 = findViewById(R.id.editTextNumberPassword2);

        app = new AppMethods(this, gl, Con, db);

        itemid="*";
        setHandlers();
        setFechaAct();
        listItems();

        fdoc=new clsDocFactura(this,38,gl.peMon,gl.peDecImp,"",gl.peComboDet);

        pass=strfechasinhora(du.getActDate())+gl.codigo_ruta;
     }

    //region Events

    public void anulDoc(View view) {
        try {
            String s=txt1.getText().toString();
            if (!s.equalsIgnoreCase(pass)) throw new Exception();
            txt1.setText("");
            msgAsk1("Marcar las facturas en rango de fechas definidas como certificadas");
        } catch (Exception e) {
            mu.msgbox("Contraseña incorrecta");return;
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
                    "WHERE (D_FACTURA.ANULADO=0) AND ((D_FACTURA.FECHA>="+dateini+") AND (D_FACTURA.FECHA<"+datefin+")) " +
                    "AND (D_FACTURA.FEELUUID=' ') AND (D_FACTURA.FEELCONTINGENCIA<>' ') "+
                    "ORDER BY D_FACTURA.COREL DESC ";

            DT=Con.OpenDT(sql);

            lblcant.setText("Facturas : "+DT.getCount());

            if (DT.getCount()>0) {
                DT.moveToFirst();

                while (!DT.isAfterLast()) {

                    id=DT.getString(0);

                    vItem =clsCls.new clsCFDV();

                    vItem.Cod=DT.getString(0);
                    vItem.Desc=DT.getString(1);

                    sf=DT.getString(2)+ StringUtils.right("000000" + Integer.toString(DT.getInt(4)), 6);;

                    vItem.Fecha=sf;
                    val=DT.getDouble(3);
                    try {
                        sval=mu.frmcur(val);
                    } catch (Exception e) {
                        sval=""+val;
                    }

                    vItem.Valor=sval;

                    items.add(vItem);

                    if (id.equalsIgnoreCase(selid)) selidx=vP;
                    vP+=1;

                    vItem.UUID=DT.getString(5);
                    vItem.FechaFactura=du.sfecha(DT.getLong(6));

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

    public void aplicaBandera() {
        try {
            db.beginTransaction();

            //toast("Espere por favor . . . ");

            sql="UPDATE D_FACTURA SET FEELUUID='"+pass+"' WHERE (D_FACTURA.ANULADO=0)  " +
                    "AND ((D_FACTURA.FECHA>="+dateini+") AND (D_FACTURA.FECHA<="+datefin+")) " +
                    "AND (FEELUUID=' ') AND (FEELCONTINGENCIA<>' ')";
            db.execSQL(sql);

            db.setTransactionSuccessful();
            db.endTransaction();

            msgExit("Transaccion completa");
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Fecha

    public void showDateDialog1(View view) {
        try{
            obtenerFecha();
            dateTxt=false;
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void showDateDialog2(View view) {
        try{
            obtenerFecha();
            dateTxt=true;
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
        try {
            datefin=du.addDays(du.getActDate(),-4);

            clsD_facturaObj D_facturaObj=new clsD_facturaObj(this,Con,db);
            D_facturaObj.fill("WHERE (FEELUUID=' ') AND (FEELCONTINGENCIA<>' ') ORDER BY FECHA");
            if (D_facturaObj.count>0) dateini=D_facturaObj.first().fecha; else dateini=datefin;

            dateini=du.ffecha00(dateini);datefin=du.ffecha24(datefin);

            lblDateini.setText(du.sfecha(dateini));
            lblDatefin.setText(du.sfecha(datefin));
        } catch (Exception e){
        }
    }

    //endregion

    //region Dialogs

    private void msgAsk1(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("MPos");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAsk2("Esta transaccion no se puede revertir.\nEstá seguro de proceder");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAsk2(String msg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("MPos");
        dialog.setMessage("¿" + msg + "?");
        dialog.setPositiveButton("Si", (dialog1, which) -> aplicaBandera());
        dialog.setNegativeButton("No", (dialog12, which) -> {});
        dialog.show();

    }

    private void msgExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Mpos");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Si", (dialog1, which) -> finish());
        dialog.show();
    }

    //endregion

    //region Aux

    public String strfechasinhora(long f) {
        int vy,vm,vd;
        String s;

        f=f/10000;
        vy=(int) f/10000;f=f % 10000;
        vm=(int) f/100;f=f % 100;
        vd=(int) f;

        s="20"+vy;
        if (vm>9) s=s+vm; else s=s+"0"+vm;
        if (vd>9) s=s+vd; else s=s+"0"+vd;

        return s;
    }

    //endregion

    //region Activity Events

    //endregion

}