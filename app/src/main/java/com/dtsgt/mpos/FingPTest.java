package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class FingPTest extends PBase {

    private EditText txt1;

    private String pass;
    private long datelim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fing_ptest);

        super.InitBase();

        txt1 = findViewById(R.id.editTextNumberPassword);

        pass=strfechasinhora(du.getActDate())+gl.codigo_ruta;

        datelim=du.addDays(du.getActDate(),-4);

        int i=0;

    }

    //region Events

    public void doBandera(View view) {
        try {
            String s=txt1.getText().toString();
            if (!s.equalsIgnoreCase(pass)) throw new Exception();
            txt1.setText("");
            msgAsk1("Marcar las facturas previas de fecha "+du.sfecha(datelim)+" como certificadas");
        } catch (Exception e) {
            mu.msgbox("Contraseña incorrecta");return;
        }
    }

    //endregion

    //region Main

    public void aplicaBandera() {
        try {
            db.beginTransaction();

            toast("Espere por favor . . . ");

            sql="UPDATE D_FACTURA SET FEELUUID='"+pass+"' WHERE (FECHA<"+datelim+") AND (FEELUUID=' ') AND (FEELCONTINGENCIA<>' ')";
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

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aplicaBandera();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Mpos");
        dialog.setMessage(msg);

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();

    }

    //endregion

    //region Activity Events


    //endregion



}
