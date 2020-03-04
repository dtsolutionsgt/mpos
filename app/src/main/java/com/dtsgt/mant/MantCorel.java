package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_corelObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantCorel extends PBase {

    private EditText txt1,txt2,txt3,txt4,txt5;
    private ImageView imgadd;

    private clsP_corelObj holder;
    private String id;

    private clsClasses.clsP_corel item=clsCls.new clsP_corel();

    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_corel);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.txt10);
        txt4 = (EditText) findViewById(R.id.txt12);txt4.setEnabled(false);
        txt5 = (EditText) findViewById(R.id.txt14);
        imgadd = (ImageView) findViewById(R.id.imgImg2);

        holder =new clsP_corelObj(this,Con,db);

        loadItem();

        if (gl.grantaccess) {
            if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
            }
        }
    }

    //region Main

    private void loadItem() {
        try {
            holder.fill();
            if (holder.count==0) {
                newItem();
            } else {
                item=holder.first();
                showItem();
            }

            txt5.requestFocus();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    private void newItem() {
        newitem=true;
        txt5.requestFocus();

        item.resol="";
        item.serie="";
        item.corelini=0;
        item.corelfin=0;
        item.corelult=0;
        item.fechares=0;
        item.ruta="";
        item.fechavig=0;
        item.resguardo=0;
        item.valor1=0;

        showItem();
    }


    private void showItem() {
        if(newitem){
            txt1.setText("");
            txt2.setText("");
            txt3.setText("");
            txt4.setText("");
            txt5.setText("");
        } else {
            txt1.setText(item.serie);
            txt2.setText(""+item.corelini);
            txt3.setText(""+item.corelfin);
            txt4.setText(""+item.corelult);
            txt5.setText(item.resol);
        }
    }

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    private boolean validaDatos() {
        String ss;

        try {

            ss=txt5.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir la resolución!");return false;
            }
            item.resol=ss;

            ss=txt1.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir la serie!");return false;
            }
            item.serie=ss;

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir inicial!");
                return false;
            } else {
                item.corelini=Integer.parseInt(ss);
            }

            ss=txt3.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir final!");
                return false;
            } else {
                item.corelfin=Integer.parseInt(ss);
            }

            //item.corelult=item.corelini;

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
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

    private void addItem() {
        try {
            holder.add(item);
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region msg

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

    private void msgAskAdd(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addItem();
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
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    //endregion
}

