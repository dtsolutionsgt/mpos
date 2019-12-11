package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantConfig extends PBase {

    private CheckBox cb100,cb101,cb102,cb103,cb104;

    private clsP_paramextObj holder;

    private boolean value100,value101,value102,value103,value104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_config);

        super.InitBase();

        cb100 = (CheckBox) findViewById(R.id.checkBox8);
        cb101 = (CheckBox) findViewById(R.id.checkBox10);
        cb102 = (CheckBox) findViewById(R.id.checkBox21);
        cb103 = (CheckBox) findViewById(R.id.checkBox23);
        cb104 = (CheckBox) findViewById(R.id.checkBox22);

        holder =new clsP_paramextObj(this,Con,db);
        loadItem();

    }

    //region Events

    public void doSave(View view) {
        msgAskUpdate("Aplicar cambios de configuración");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE ID="+100);
            value100=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .100. "+e.getMessage());
            value100=true;
        }
        cb100.setChecked(value100);

        try {
            holder.fill("WHERE ID="+101);
            value101=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .101. "+e.getMessage());
            value101=false;
        }
        cb101.setChecked(value101);

        try {
            holder.fill("WHERE ID="+102);
            value102=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .102. "+e.getMessage());
            value102=true;
        }
        cb102.setChecked(value102);

        try {
            holder.fill("WHERE ID="+104);
            value104=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" .104. "+e.getMessage());
            value104=false;
        }
        cb104.setChecked(value104);


    }

    private void updateItem() {
        String s100="N",s101="N",s102="N",s104="N";

        try {

            if (cb100.isChecked()) s100="S";
            if (cb101.isChecked()) s101="S";
            if (cb102.isChecked()) s102="S";
            if (cb104.isChecked()) s104="S";

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=100");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=101");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=102");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=104");

            db.execSQL("INSERT INTO P_PARAMEXT VALUES (100,'Configuración centralizada','"+s100+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (101,'Imprimir orden para cosina','"+s101+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (102,'Lista con imagenes','"+s102+"')");
            db.execSQL("INSERT INTO P_PARAMEXT VALUES (104,'Imprimir factura','"+s104+"')");

            db.setTransactionSuccessful();
            db.endTransaction();

            finish();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Aux


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
