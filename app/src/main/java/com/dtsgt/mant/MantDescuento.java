package com.dtsgt.mant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_descuentoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MantDescuento extends PBase {

    //date variables
    public final Calendar c = Calendar.getInstance();
    private static final String CERO = "0";
    private static final String BARRA = "/";
    private TextView lblDate,lblDate1;
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);
    public int cyear, cmonth, cday;
    private long date,date1;

    private ImageView imgstat;
    private EditText txt,txt2;
    private Spinner spinner;

    private clsP_descuentoObj holder;
    private clsP_productoObj prod;
    private clsClasses.clsP_descuento item=clsCls.new clsP_descuento();

    private ArrayList<String> spincode= new ArrayList<String>();
    private ArrayList<String> spinlist = new ArrayList<String>();
    private String id_item,nameProd;
    private boolean newitem=false,dateTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_descuento);

        super.InitBase();

        imgstat = (ImageView) findViewById(R.id.imageView31);
        spinner = (Spinner) findViewById(R.id.spinner12);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt = (EditText) findViewById(R.id.txt);
        lblDate = (TextView) findViewById(R.id.textView126);
        lblDate1 = (TextView) findViewById(R.id.textView125);

        holder =new clsP_descuentoObj(this,Con,db);
        prod =new clsP_productoObj(this,Con,db);

        id_item=gl.gcods;
        setHandlers();

        if (id_item.isEmpty()) newItem(); else loadItem();
    }

    //region Events

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    public void doStatus(View view) {
        if (item.activo==1) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }


    private void setHandlers(){

        try{

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    TextView spinlabel;

                    try {
                        spinlabel=(TextView)parentView.getChildAt(0);
                        spinlabel.setTextColor(Color.BLACK);
                        spinlabel.setPadding(5, 0, 0, 0);
                        spinlabel.setTextSize(18);

                        id_item = spinner.getSelectedItem().toString();

                        prod.fill("WHERE CODIGO = '"+id_item+"'");

                        if(prod.count==0) return;

                        nameProd = prod.first().desccorta.trim();

                        txt2.setText(nameProd);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    return;
                }

            });

        }catch (Exception ex){
            msgbox(ex.getMessage());
        }

    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill(" WHERE PRODUCTO='"+id_item+"'");
            item=holder.first();

            FillSpinner();
            showItem();

            txt.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.activo==1) {
                imgstat.setImageResource(R.drawable.delete_64);
            } else {
                imgstat.setImageResource(R.drawable.mas);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.cliente="*";
        item.ctipo=0;
        item.producto="";
        item.ptipo=0;
        item.tiporuta=0;
        item.rangoini=1;
        item.rangofin=999;
        item.desctipo="R";
        item.valor=0;
        item.globdesc="N";
        item.porcant="S";
        item.fechaini=0;
        item.fechafin=0;
        item.coddesc=1;
        item.nombre="1";
        item.activo=1;
        FillSpinner();

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.producto;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            holder.update(item);
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void showItem() {
        try{
            if(newitem){
                spinner.setEnabled(true);
                id_item = spinner.getSelectedItem().toString();
            }else {
                spinner.setEnabled(false);
                id_item = spinner.getSelectedItem().toString();

                prod.fill("WHERE CODIGO = '"+id_item+"'");
                nameProd = prod.first().desccorta.trim();

                txt2.setText(nameProd);
                txt.setText(""+item.valor);
                lblDate.setText(du.sfecha(item.fechaini));
                lblDate1.setText(du.sfecha(item.fechafin));
            }

        }catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void FillSpinner(){
        Cursor DT;
        String icode;

        try{

            if(newitem){

                sql="SELECT CODIGO FROM P_PRODUCTO";

                DT=Con.OpenDT(sql);

                if(DT.getCount()<=0){
                    spinlist.add("No hay productos existentes");
                }else{
                    spinlist.add("Seleccione un producto ...");

                    DT.moveToFirst();
                    while (!DT.isAfterLast()) {

                        icode=DT.getString(0);

                        spinlist.add(icode);

                        DT.moveToNext();
                    }
                }

            }else {
                spinlist.add(id_item);
            }

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            spinner.setSelection(0);
        }

        try{
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(dataAdapter);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            spinner.setSelection(0);
        }

    }

    private boolean validaDatos() {
        String ss;
        int s;
        double s2;

        try {

            if (newitem) {
                ss=spinner.getSelectedItem().toString().trim();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill(" WHERE PRODUCTO='"+ss+"'");
                if (holder.count>0) {
                    msgbox("¡Un descuento con este producto ya existe!\n"+holder.first().nombre);return false;
                }

                item.producto=ss;
            }

            ss=txt2.getText().toString().trim();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto, ingrese de nuevo el código!");
                return false;
            } else {
                item.nombre=ss;
            }

            ss=txt.getText().toString().trim();
            s2=Double.parseDouble(ss);
            if(s2<=0 || s2>100){
                msgbox("¡Valor de descuento incorrecto!");
                return false;
            } else {
                item.valor=s2;
            }

            ss=lblDate.getText().toString().trim();
            if(ss.isEmpty() || ss.equals("00/00/0000")){
                msgbox("¡Fecha inicial incorrecta!");
                return false;
            }else {
                item.fechaini = (int) date;
            }

            ss=lblDate1.getText().toString().trim();
            if(ss.isEmpty() || ss.equals("00/00/0000")){
                msgbox("¡Fecha final incorrecta!");
                return false;

            }else if(date1<date) {
                msgbox("¡Fecha final incorrecta!, La fecha de vencimiento es menor que la inicial");
                return false;

            }else {
                item.fechafin = (int) date1;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    //endregion


    //region Date

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
                    String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                    if(dateTxt) lblDate1.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    if(!dateTxt) lblDate.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);
                    if(dateTxt) date1 = du.cfechaDesc(cyear, cmonth, cday);
                    if(!dateTxt) date = du.cfechaDesc(cyear, cmonth, cday);
                }
            },anio, mes, dia);

            recogerFecha.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion Date

    //region Dialogs

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }


    private void msgAskUpdate(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskStatus(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (item.activo==1) {
                    item.activo=0;
                } else {
                    item.activo=1;
                };
                updateItem();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            holder.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    //endregion

}
