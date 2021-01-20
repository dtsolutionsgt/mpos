package com.dtsgt.mant;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class MantConfigRes extends PBase {

    private CheckBox cb101,cb117;
    private CheckBox locCaja,locCajaRecep;
    private TextView txt120;
    private ImageView imgadd;

    private clsP_paramextObj holder;

    private boolean value101,value117;
    private double  value120;
    private boolean valCaja,valCajaRecep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_config_res);

        super.InitBase();

        cb101  = findViewById(R.id.chkEnvio8);
        cb117  = findViewById(R.id.chkEnvio14);
        txt120 = findViewById(R.id.editTextNumber3);

        locCaja      = findViewById(R.id.chkEnvio11);
        locCajaRecep = findViewById(R.id.chkEnvio12);

        imgadd = (ImageView) findViewById(R.id.imgImg2);

        holder =new clsP_paramextObj(this,Con,db);

        setHandlers();

        loadItem();

    }

    //region Events

    public void doSave(View view) {
        msgAskUpdate("Aplicar cambios de configuración");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    public void setHandlers() {

    }

    //endregion

    //region Main

    private void loadItem() {

        loadLocalItems();

        try {
            holder.fill("WHERE ID="+101);
            value101=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value101=true;
        }
        cb101.setChecked(value101);

        try {
            holder.fill("WHERE ID="+117);
            value117=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value117=true;
        }
        cb117.setChecked(value117);

        try {
            holder.fill("WHERE ID="+120);
            value120 =Double.parseDouble(holder.first().valor);
        } catch (Exception e) {
            value120=0;
        }
        txt120.setText(""+value120);

    }

    private void loadLocalItems() {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MPos", 0);
            SharedPreferences.Editor editor = pref.edit();

            try {
                valCaja=pref.getBoolean("pelCaja", false);
            } catch (Exception e) {
                valCaja=false;
            }
            locCaja.setChecked(valCaja);

            try {
                valCajaRecep=pref.getBoolean("pelCajaRecep", false);
            } catch (Exception e) {
                valCajaRecep=false;
            }
            locCajaRecep.setChecked(valCajaRecep);

        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }

    }

    private void updateItem() {
        String s101="N",s117="S",s120="0";
        double dval120;

        updateLocalItems();

        try {

            dval120=Double.parseDouble(txt120.getText().toString());if (dval120<0 | dval120>99) dval120=0;

            if (cb101.isChecked())  s101="S";
            if (!cb117.isChecked()) s117="N";
            s120=""+dval120;

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=101");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=117");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=120");

            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (101,'Imprimir orden para la cocina','"+s101+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (117,'Propina fija','"+s117+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (120,'Propina porcentaje','"+s120+"')");

            db.setTransactionSuccessful();
            db.endTransaction();

            app.parametrosExtra();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void updateLocalItems() {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MPos", 0);
            SharedPreferences.Editor editor = pref.edit();

            valCaja=locCaja.isChecked();
            editor.putBoolean("pelCaja", valCaja);

            valCajaRecep=locCajaRecep.isChecked();
            editor.putBoolean("pelCajaRecep", valCajaRecep);

            editor.commit();
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Aux

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
