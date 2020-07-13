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


public class MantRepCierre extends PBase {

    private CheckBox cb00,cb01,cb02,cb03,cb04,cb05,cb06,cb07,cb08,cb09,cb10;

    private clsP_paramextObj holder;

    private boolean value00,value01,value02,value03,value04,value05;
    private boolean value06,value07,value08,value09,value10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_rep_cierre);

        super.InitBase();

        cb00  = (CheckBox) findViewById(R.id.checkBox8);
        cb01  = (CheckBox) findViewById(R.id.checkBox10);
        cb02  = (CheckBox) findViewById(R.id.checkBox22);
        cb03  = (CheckBox) findViewById(R.id.checkBox23);
        cb04  = (CheckBox) findViewById(R.id.checkBox24);
        cb05  = (CheckBox) findViewById(R.id.chkCierreDiario);
        cb06  = (CheckBox) findViewById(R.id.chkEnvio);
        cb07  = (CheckBox) findViewById(R.id.chkEnvio2);
        cb08  = (CheckBox) findViewById(R.id.chkEnvio3);
        cb09  = (CheckBox) findViewById(R.id.chkEnvio4);
        cb10  = (CheckBox) findViewById(R.id.chkEnvio5);

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

        value00=app.paramCierre(500);cb00.setChecked(value00);
        value01=app.paramCierre(501);cb01.setChecked(value01);
        value02=app.paramCierre(502);cb02.setChecked(value02);
        value03=app.paramCierre(503);cb03.setChecked(value03);
        value04=app.paramCierre(504);cb04.setChecked(value04);
        value05=app.paramCierre(505);cb05.setChecked(value05);
        value06=app.paramCierre(506);cb06.setChecked(value06);
        value07=app.paramCierre(507);cb07.setChecked(value07);
        value08=app.paramCierre(508);cb08.setChecked(value08);
        value09=app.paramCierre(509);cb09.setChecked(value09);
        value10=app.paramCierre(510);cb10.setChecked(value10);
    }

    private void updateItem() {
        String s00="N",s01="N",s02="N",s03="N",s04="N",s05="N",s06="N",s07="N",s08="N",s09="N",s10="N";

        try {

            if (cb00.isChecked()) s00="S";
            if (cb01.isChecked()) s01="S";
            if (cb02.isChecked()) s02="S";
            if (cb03.isChecked()) s03="S";
            if (cb04.isChecked()) s04="S";
            if (cb05.isChecked()) s05="S";
            if (cb06.isChecked()) s06="S";
            if (cb07.isChecked()) s07="S";
            if (cb08.isChecked()) s08="S";
            if (cb09.isChecked()) s09="S";
            if (cb10.isChecked()) s10="S";

            db.beginTransaction();

            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=500");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=501");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=502");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=503");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=504");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=505");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=506");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=507");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=508");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=509");
            db.execSQL("DELETE FROM P_PARAMEXT WHERE ID=510");

            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (500,'Reporte de Facturas por Día','"+s00+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (501,'Reporte Venta por Día','"+s01+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (502,'Reporte Venta por Producto','"+s02+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (503,'Reporte por Forma de Pago','"+s03+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (504,'Reporte por Familia','"+s04+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (505,'Reporte Facturas Anuladas','"+s05+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (506,'Reporte Ventas por Vendedor','"+s06+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (507,'Reporte Ventas por Cliente Consolidado','"+s07+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (508,'Reporte Ventas por Cliente Detalle','"+s08+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (509,'Margen y Beneficio por Productos','"+s09+"')");
            db.execSQL("INSERT INTO P_PARAMEXT (ID, NOMBRE, VALOR) VALUES (510,'Margen y Beneficio por Familia','"+s10+"')");

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