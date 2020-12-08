package com.dtsgt.mant;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_usgrupoObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MantImpresora extends PBase {

    private EditText txt1,txt2,txt3;
    private CheckBox cb1;
    private Spinner spin;

    private clsP_archivoconfObj holder;
    private clsClasses.clsP_archivoconf item=clsCls.new clsP_archivoconf();

    private ArrayList<String> spinlist=new ArrayList<String>();

    private String id;
    private boolean newitem=false;

    //"SIN IMPRESORA";
    //"EPSON TM BlueTooth";
    //"HP Engage USB"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_impresora);

        super.InitBase();

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.editText12);
        spin = findViewById(R.id.spinner18);
        cb1 = findViewById(R.id.checkBox21);

        id=gl.gcods;

        holder =new clsP_archivoconfObj(this,Con,db);

        if (id.isEmpty()) newItem(); else loadItem();

        setHandlers();

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

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);

                    if(spinlabel != null){
                        spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                        spinlabel.setTextSize(18);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);
                    }

                    item.tipo_impresora=spinlist.get(position);
                 } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO_ARCHIVOCONF="+id);
            item=holder.first();

            showItem();

            txt2.requestFocus();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        int newid;

        newitem=true;
        txt2.requestFocus();

        newid=holder.newID("SELECT MAX(codigo_archivoconf) FROM P_archivoconf");

        item.codigo_archivoconf=newid;
        item.ruta = ""+gl.codigo_ruta;
        item.tipo_hh = "";
        item.idioma = "";
        item.tipo_impresora = "SIN IMPRESORA";
        item.serial_hh = "";
        item.modif_peso = "";
        item.puerto_impresion = "";
        item.lbs_o_kgs = "";

        holder.fill("WHERE nota_credito=1");
        if (holder.count==0) item.nota_credito=1;else item.nota_credito=0;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo_archivoconf;
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
        txt1.setText(""+item.codigo_archivoconf);
        txt2.setText(item.tipo_hh);
        txt3.setText(item.puerto_impresion);
        cb1.setChecked(item.nota_credito==1);

        fillSpinner(item.tipo_impresora);
    }

    private boolean validaDatos() {
        String ss;

        try {

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                item.tipo_hh=ss;
            }

            ss=txt3.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡MAC/IP incorrecto!");
                return false;
            } else {
                item.puerto_impresion=ss;
            }

            if (cb1.isChecked()) item.nota_credito=1;else item.nota_credito=0;

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean fillSpinner(String selid){
        int selidx=0;
        int scod;

        spinlist.clear();

        try {
            spinlist.add("SIN IMPRESORA");
            spinlist.add("EPSON TM BlueTooth");
            spinlist.add("HP Engage USB");

            for (int i = 0; i <spinlist.size(); i++) {
                if (spinlist.get(i).equalsIgnoreCase(selid)) selidx=i;
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        try {
            spin.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }


    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        ExDialog dialog = new ExDialog(this);
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
        ExDialog dialog = new ExDialog(this);
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

    private void msgAskExit(String msg) {
        ExDialog dialog = new ExDialog(this);
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
