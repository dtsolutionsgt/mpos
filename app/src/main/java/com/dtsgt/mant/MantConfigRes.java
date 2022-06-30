package com.dtsgt.mant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_paramextObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantConfigRes extends PBase {

    private CheckBox cb101,cb117,cb123,cb124,cb125;
    private CheckBox cb131, cb132, cb135, cb136, cb137, cb142;
    private EditText txt120,txt122,txt127;
    private ImageView imgadd;

    private clsP_paramextObj holder;

    private boolean value101,value117,value123,value124,value125,value131,value132,value135,value136;
    private boolean value137,value142;
    private double  value120,value127;
    private String  value122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_config_res);

        super.InitBase();

        cb101  = findViewById(R.id.chkEnvio8);
        cb117  = findViewById(R.id.chkEnvio14);
        cb123  = findViewById(R.id.chkEnvio15);
        cb124  = findViewById(R.id.chkEnvio16);
        cb125  = findViewById(R.id.chkEnvio17);

        txt120 = findViewById(R.id.editTextNumber3);
        txt122 = findViewById(R.id.editTextNumber6);
        txt127 = findViewById(R.id.editTextNumber7);

        cb131 = findViewById(R.id.chkEnvio131);
        cb132 = findViewById(R.id.chkEnvio12);
        cb136 = findViewById(R.id.chkEnvio20);
        cb135 = findViewById(R.id.chkEnvio21);
        cb137 = findViewById(R.id.chkEnvio25);
        cb142 = findViewById(R.id.chkEnvio29);

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

    public void setHandlers() {}

    //endregion

    //region Main

    private void loadItem() {

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

        try {
            holder.fill("WHERE ID="+122);
            value122 =holder.first().valor;
        } catch (Exception e) {
            value122="NO APLICA";
        }
        txt122.setText(value122);

        try {
            holder.fill("WHERE ID="+123);
            value123=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value123=true;
        }
        cb123.setChecked(value123);

        try {
            holder.fill("WHERE ID="+124);
            value124=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value124=false;
        }
        cb124.setChecked(value124);

        try {
            holder.fill("WHERE ID="+125);
            value125=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value125=false;
        }
        cb125.setChecked(value125);

        try {
            holder.fill("WHERE ID="+127);
            value127 =Double.parseDouble(holder.first().valor);
        } catch (Exception e) {
            value127=0;
        }
        txt127.setText(""+value127);

        try {
            holder.fill("WHERE ID="+131);
            value131=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value131=false;
        }
        cb131.setChecked(value131);

        try {
            holder.fill("WHERE ID="+132);
            value132=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value132=false;
        }
        cb132.setChecked(value132);

        try {
            holder.fill("WHERE ID="+135);
            value135 =holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value135 =false;
        }
        cb135.setChecked(value135);

        try {
            holder.fill("WHERE ID="+136);
            value136 =holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value136 =false;
        }
        cb136.setChecked(value136);

        try {
            holder.fill("WHERE ID="+137);
            value137 =holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value137 =false;
        }
        cb137.setChecked(value137);

        try {
            holder.fill("WHERE ID="+142);
            value142=holder.first().valor.equalsIgnoreCase("S");
        } catch (Exception e) {
            value142 =false;
        }
        cb142.setChecked(value142);

    }

    private void updateItem() {
        String s101="N",s117="S",s120="0",s122="NO APLICA",s123="N",s124="N",s125="N",s127="0",s131="0";
        String s132="N",s135="N",s136="N",s137="N",s142="N";
        double dval120,dval127;

        try {
            try {
                dval120=Double.parseDouble(txt120.getText().toString());
            } catch (NumberFormatException e) {
                dval120=0;
            }
            if (dval120<0 | dval120>99) dval120=0;

            try {
                dval127=Double.parseDouble(txt127.getText().toString());
            } catch (NumberFormatException e) {
                dval127=0;
            }
            if (dval127<0 | dval127>99) dval127=0;


            if (cb101.isChecked())  s101="S";
            if (!cb117.isChecked()) s117="N";
            s120=""+dval120;
            s122=""+txt122.getText().toString();
            if (cb123.isChecked())  s123="S";
            if (cb124.isChecked())  s124="S";
            if (cb125.isChecked())  s125="S";
            s127=""+dval127;
            if (cb131.isChecked()) s131 = "S";
            if (cb132.isChecked()) s132 = "S";
            if (cb135.isChecked()) s135 = "S";
            if (cb136.isChecked()) s136 = "S";
            if (cb137.isChecked()) s137 = "S";
            if (cb142.isChecked()) s142 = "S";

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=101");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=117");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=120");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=122");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=123");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=124");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=125");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=127");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=131");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=132");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=135");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=136");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=137");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=142");

            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (101,'Imprimir orden para la cocina','"+s101+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (117,'Propina fija','"+s117+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (120,'Propina porcentaje','"+s120+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (122,'Combo texto opcion no aplica','"+s122+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (123,'Editar total de combo','"+s123+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (124,'Agregar articulo a combo','"+s124+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (125,'Precio de combo sin limite','"+s125+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (127,'Propina pago con tarjeta','"+s127+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (131,'Modulo caja','"+s131+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (132,'Recepcion de ordenes para la caja','"+s132+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (135,'Pide contraseña para ingreso del mesero','"+s135+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (136,'Pide contraseña para ingreso a la caja','"+s136+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (137,'Envia ordenes a la caja','"+s137+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (142,'Actualizar ordenes de mesas','"+s142+"')");

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
