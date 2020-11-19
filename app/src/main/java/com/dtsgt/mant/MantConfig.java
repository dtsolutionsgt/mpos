package com.dtsgt.mant;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantConfig extends PBase {

    private CheckBox cb100,cb102,cb103,cb104,cb106,cb107,cb109,cb111,cb112,cb113,cb115;
    private CheckBox cb116,cb118,cb119;
    private Spinner spin16,spin105;
    private TextView txt108,txt110,txt114;
    private ImageView imgadd;

    private clsP_paramextObj holder;

    private ArrayList<String> items16= new ArrayList<String>();
    private ArrayList<String> items105= new ArrayList<String>();

    private boolean value100,value102,value103,value104,value106, value107,value109;
    private boolean value111,value112,value113,value115,value116,value118,value119;
    private String  value16,value105;
    private int value108,value110,value114;


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
        cb107  = (CheckBox) findViewById(R.id.chkCierreDiario);
        txt108 = (EditText) findViewById(R.id.txtDiasAnul);
        cb109  = (CheckBox) findViewById(R.id.chkEnvio);
        txt110 = (EditText) findViewById(R.id.txtDiasAnul2);
        cb111  = (CheckBox) findViewById(R.id.chkEnvio2);
        cb112  = (CheckBox) findViewById(R.id.chkEnvio3);
        cb113  = (CheckBox) findViewById(R.id.chkEnvio4);
        txt114 = (EditText) findViewById(R.id.editTextNumber2);
        cb115  = (CheckBox) findViewById(R.id.chkEnvio6);
        cb116  = (CheckBox) findViewById(R.id.chkEnvio7);
        cb118  = (CheckBox) findViewById(R.id.chkEnvio9);
        cb119  = (CheckBox) findViewById(R.id.chkEnvio10);

        imgadd = (ImageView) findViewById(R.id.imgImg2);

        holder =new clsP_paramextObj(this,Con,db);

        setHandlers();

        loadItem();

        /*
        if (gl.peMCent) {
            //if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
            //}
        }
        */

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
            value16="GUA";
        }
        fillSpin16();

        try {
            holder.fill("WHERE ID="+100);
            value100=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value100=true;
        }
        cb100.setChecked(value100);

        try {
            holder.fill("WHERE ID="+102);
            value102=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value102=true;
        }
        cb102.setChecked(value102);

        try {
            holder.fill("WHERE ID="+103);
            value103=holder.first().valor.equalsIgnoreCase("1");
        } catch (Exception e) {
            value103=true;
        }
        cb103.setChecked(value103);

        try {
            holder.fill("WHERE ID="+104);
            value104=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
             value104=false;
        }
        cb104.setChecked(value104);

        try {
            holder.fill("WHERE ID="+105);
            value105=holder.first().valor;
        } catch (Exception e) {
            value105="";
        }
        fillSpin105();

        try {
            holder.fill("WHERE ID="+106);
            value106=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
             value106=true;
        }
        cb106.setChecked(value106);

        try {
            holder.fill("WHERE ID="+107);
            value107=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value107=true;
        }
        cb107.setChecked(value107);

        try {
            holder.fill("WHERE ID="+108);
            value108=Integer.parseInt(holder.first().valor);
        } catch (Exception e) {
            value108=5;
        }
        txt108.setText(""+value108);

        try {
            holder.fill("WHERE ID="+109);
            value109=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value109=true;
        }
        cb109.setChecked(value109);

        try {
            holder.fill("WHERE ID="+110);
            value110 =Integer.parseInt(holder.first().valor);
        } catch (Exception e) {
            value110=3;
        }
        txt110.setText(""+value110);

        try {
            holder.fill("WHERE ID="+111);
            value111=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value111=false;
        }
        cb111.setChecked(value111);

        try {
            holder.fill("WHERE ID="+112);
            value112=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value112=false;
        }
        cb112.setChecked(value112);

        try {
            holder.fill("WHERE ID="+113);
            value113=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value113=false;
        }
        cb113.setChecked(value113);

        try {
            holder.fill("WHERE ID="+114);
            value114 =Integer.parseInt(holder.first().valor);
        } catch (Exception e) {
            value114=1;
        }
        txt114.setText(""+value114);

        try {
            holder.fill("WHERE ID="+115);
            value115=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value115=false;
        }
        cb115.setChecked(value115);

        try {
            holder.fill("WHERE ID="+116);
            value116=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value116=false;
        }
        cb116.setChecked(value116);

        try {
            holder.fill("WHERE ID="+118);
            value118=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value118=false;
        }
        cb118.setChecked(value118);

        try {
            holder.fill("WHERE ID="+119);
            value119=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value119=false;
        }
        cb119.setChecked(value119);
    }

    private void updateItem() {
        String s100="N",s102="N",s103="N",s104="N",s105="",s106="S", s107="S",
               s108="5", s109="S", s110="3", s111="N", s112="N", s113="N", s114="1",
               s115="N", s116="N", s118="N", s119="N";

        try {

            if (cb100.isChecked())  s100="S";
            if (cb102.isChecked())  s102="S";
            if (cb103.isChecked())  s103="1";else s103="0";
            if (cb104.isChecked())  s104="S";
            if (!cb106.isChecked()) s106="N";
            if (!cb107.isChecked()) s107="N";
            s108 = txt108.getText().toString();
            if (!cb109.isChecked()) s109="N";
            s110 = txt110.getText().toString();
            if (cb111.isChecked())  s111="S";
            if (cb112.isChecked())  s112="S";
            if (cb113.isChecked())  s113="S";
            s114 = txt114.getText().toString();
            if (cb115.isChecked())  s115="S";
            if (cb116.isChecked())  s116="S";
            if (cb118.isChecked())  s118="S";
            if (cb119.isChecked())  s119="S";

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=16");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=100");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=102");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=103");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=104");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=105");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=106");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=107");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=108");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=109");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=110");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=111");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=112");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=113");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=114");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=115");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=116");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=118");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=119");

            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES ( 16,'Formato factura','"+value16+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (100,'Configuración centralizada','"+s100+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (102,'Lista con imagenes','"+s102+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (103,'Pos modalidad','"+s103+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (104,'Imprimir factura','"+s104+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (105,'FEL','"+value105+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (106,'Mostrar foto de cliente para biometrico','"+s106+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (107,'Cierre diario','"+s107+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (108,'Días anulación permitida','"+s108+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (109,'Envío de facturas automatico','"+s109+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (110,'Días avizo FEL','"+s110+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (111,'Actualizacion antes de inico de caja','"+s111+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (112,'Inventario compartido','"+s112+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (113,'Recepcion de pedidos a domicilio','"+s113+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (114,'Numero de impresiones de factura','"+s114+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (115,'Reporte ventas por producto con codigo','"+s115+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (116,'Anulacion autoriza supervisor','"+s116+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (118,'Modulo restaurante','"+s118+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (119,'Modificar orden a domicilio','"+s119+"')");

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

    //endregion

    //region Activity Events


    //endregion

}
