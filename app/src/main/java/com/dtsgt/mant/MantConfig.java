package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantConfig extends PBase {

    private CheckBox cb100,cb102,cb103,cb104,cb106;
    private Spinner spin16,spin105;
    private ImageView imgadd;

    private clsP_paramextObj holder;

    private ArrayList<String> items16= new ArrayList<String>();
    private ArrayList<String> items105= new ArrayList<String>();

    private boolean value100,value102,value103,value104,value106;
    private String  value16,value105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_config);

        super.InitBase();

        spin16 = (Spinner) findViewById(R.id.spinner16);
        cb100  = (CheckBox) findViewById(R.id.checkBox8);
        cb102  = (CheckBox) findViewById(R.id.checkBox10);
        cb103  = (CheckBox) findViewById(R.id.checkBox23);
        cb104  = (CheckBox) findViewById(R.id.checkBox22);
        spin105 = (Spinner) findViewById(R.id.spinner105);
        cb106  = (CheckBox) findViewById(R.id.checkBox24);

        imgadd = (ImageView) findViewById(R.id.imgImg2);

        holder =new clsP_paramextObj(this,Con,db);

        setHandlers();

        loadItem();

        if (gl.grantaccess) {
            if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
             }
        }

    }

    //region Events

    public void doSave(View view) {
        msgAskUpdate("Aplicar cambios de configuración");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    public void setHandlers() {
        spin16.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);
                    spinlabel.setPadding(5,0,0,0);spinlabel.setTextSize(18);
                    spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    value16=items16.get(position);
                } catch (Exception e) { }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        spin105.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);
                    spinlabel.setPadding(5,0,0,0);spinlabel.setTextSize(18);
                    spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    value105=items105.get(position);
                } catch (Exception e) { }
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
            holder.fill("WHERE ID="+16);
            value16=holder.first().valor;
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .16. "+e.getMessage());
            value16="GUA";
        }
        fillSpin16();

        try {
            holder.fill("WHERE ID="+100);
            value100=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .100. "+e.getMessage());
            value100=true;
        }
        cb100.setChecked(value100);

        try {
            holder.fill("WHERE ID="+102);
            value102=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .102. "+e.getMessage());
            value102=true;
        }
        cb102.setChecked(value102);

        try {
            holder.fill("WHERE ID="+103);
            value103=holder.first().valor.equalsIgnoreCase("1");
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .103. "+e.getMessage());
            value103=true;
        }
        cb103.setChecked(value103);

        try {
            holder.fill("WHERE ID="+104);
            value104=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .104. "+e.getMessage());
            value104=false;
        }
        cb104.setChecked(value104);

        try {
            holder.fill("WHERE ID="+105);
            value105=holder.first().valor;
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .105. "+e.getMessage());
            value105="";
        }
        fillSpin105();

        try {
            holder.fill("WHERE ID="+106);
            value106=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            //msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .105. "+e.getMessage());
            value106=true;
        }
        cb106.setChecked(value106);

    }

    private void updateItem() {
        String s100="N",s102="N",s103="N",s104="N",s105="",s106="S", s107="S", s108="3";

        try {

            if (cb100.isChecked())  s100="S";
            if (cb102.isChecked())  s102="S";
            if (cb103.isChecked())  s103="1";else s103="0";
            if (cb104.isChecked())  s104="S";
            if (!cb106.isChecked()) s106="N";
           // if (!cb107.isChecked()) s107="N";
           // if (!cb107.isChecked()) s107="N";

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=16");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=100");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=101");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=102");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=103");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=104");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=105");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=106");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=107");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=108");

            db.execSQL("INSERT INTO P_PARAMEXT VALUES ( 16,'Formato factura','"+value16+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (100,'Configuración centralizada','"+s100+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (101,'Imprimir orden para cosina','N')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (102,'Lista con imagenes','"+s102+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (103,'Pos modalidad','"+s103+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (104,'Imprimir factura','"+s104+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (105,'FEL','"+value105+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (106,'Mostrar foto de cliente para biometrico','"+s106+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (107,'Cierre diario','"+s107+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (108,'Días anulación permitida','"+s108+"')");

            db.setTransactionSuccessful();
            db.endTransaction();

            app.parametrosExtra();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void fillSpin16() {
        int selidx=0;

        try {
            items16.clear();
            items16.add("GUA");if (value16.equalsIgnoreCase("GUA")) selidx=0;
            items16.add("TICKET");if (value16.equalsIgnoreCase("TICKET")) selidx=1;

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items16);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin16.setAdapter(dataAdapter);

            try {
                spin16.setSelection(selidx);
            } catch (Exception e) {
                spin16.setSelection(0);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void fillSpin105() {
        int selidx=0;

        try {
            items105.clear();
            items105.add("SIN FEL"); if (value105.equalsIgnoreCase("SIN FEL")) selidx=0;
            items105.add("INFILE");  if (value105.equalsIgnoreCase("INFILE"))  selidx=1;

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, items105);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin105.setAdapter(dataAdapter);

            try {
                spin105.setSelection(selidx);
            } catch (Exception e) {
                spin105.setSelection(0);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Configuración");
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

    private void msgAskUpdate(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Configuración");
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

    //endregion

    //region Activity Events


    //endregion

}
