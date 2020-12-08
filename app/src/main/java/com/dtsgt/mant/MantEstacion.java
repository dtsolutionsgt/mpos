package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_archivoconfObj;
import com.dtsgt.classes.clsP_estacionObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantEstacion extends PBase {

    private ImageView imgadd;
    private EditText txt1,txt2;
    private TextView lblest;
    private RelativeLayout relest;

    private clsP_estacionObj holder;
    public  clsClasses.clsP_estacion item=clsCls.new clsP_estacion();
    private clsP_archivoconfObj P_impresoraObj;

    private String id;
    private boolean newitem=false;
    private int idimpresora =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_estacion);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        lblest =findViewById(R.id.textView219);

        imgadd = (ImageView) findViewById(R.id.imgImg2);
        relest=findViewById(R.id.relestacion);

        holder =new clsP_estacionObj(this,Con,db);
        P_impresoraObj=new clsP_archivoconfObj(this,Con,db);

        id=gl.gcods;
        if (id.isEmpty()) newItem(); else loadItem();
        if (!gl.peRest) relest.setVisibility(View.INVISIBLE);

        setHandlers();

        if (gl.peMCent) imgadd.setVisibility(View.INVISIBLE);

    }

    //region Events

    private void setHandlers() {

    }

    public void doSave(View view) {
        if (!validaDatos()) return;
        if (newitem) {
            msgAskAdd("Agregar nuevo registro");
        } else {
            msgAskUpdate("Actualizar registro");
        }
    }

    public void doImpresora(View view) {
        listaImpresoras();
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO_ESTACION="+id);
            item=holder.first();
            idimpresora=item.codigo_impresora;

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        item.codigo_estacion=holder.newID("SELECT MAX(codigo_estacion) FROM P_estacion");
        item.empresa=gl.emp;
        item.codigo_sucursal=gl.tienda;
        item.nombre="";
        item.codigo_impresora=0;

        showItem();
    }

    private void addItem() {
        try {
            item.codigo_impresora=idimpresora;
            holder.add(item);
            gl.gcods=""+item.codigo_estacion;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            item.codigo_impresora=idimpresora;
            holder.update(item);
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(""+item.codigo_estacion);
        txt2.setText(item.nombre);
        lblest.setText(nombreImpresora());
    }

    private boolean validaDatos() {
        String ss;

        try {

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                item.nombre=ss;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
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

    private void listaImpresoras() {
        final AlertDialog Dialog;

        try {
            P_impresoraObj.fill("WHERE NOTA_CREDITO=0 ORDER BY TIPO_HH");

            final String[] selitems = new String[P_impresoraObj.count];

            for (int i = 0; i <P_impresoraObj.count; i++) {
                selitems[i]=P_impresoraObj.items.get(i).tipo_hh;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Impresoras");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    idimpresora =P_impresoraObj.items.get(item).codigo_archivoconf;
                    lblest.setText(nombreImpresora());
                    dialog.cancel();
                }
            });

            menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            menudlg.setPositiveButton("Limpiar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    idimpresora=0;
                    lblest.setText(nombreImpresora());
                    dialog.cancel();
                }
            });

            Dialog = menudlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private String nombreImpresora() {
        try {
            P_impresoraObj.fill("WHERE CODIGO_ARCHIVOCONF="+idimpresora);
            return P_impresoraObj.first().tipo_hh;
        } catch (Exception e) {
            return "";
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            holder.reconnect(Con,db);
            P_impresoraObj.reconnect(Con,db);
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
