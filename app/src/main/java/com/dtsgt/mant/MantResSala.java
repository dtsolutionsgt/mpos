package com.dtsgt.mant;

import android.os.Bundle;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_salaObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantResSala extends PBase {

    private ImageView imgstat,imgadd;
    private EditText txt1;

    private clsP_res_salaObj holder;
    private clsClasses.clsP_res_sala item=clsCls.new clsP_res_sala();

    private String id;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_res_sala);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgadd = (ImageView) findViewById(R.id.imgImg2);

        holder =new clsP_res_salaObj(this,Con,db);

        id=gl.gcods;
        if (id.isEmpty()) newItem(); else loadItem();

        if (gl.peMCent) {
            imgadd.setVisibility(View.INVISIBLE);
            imgstat.setVisibility(View.INVISIBLE);
        }
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

    public void doStatus(View view) {
        msgAskDelete("Eliminar la sala y todas las mesas asociadas");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO_SALA="+id);
            item=holder.first();

            showItem();

            txt1.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            imgstat.setImageResource(R.drawable.delete_64);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.codigo_sala=holder.newID("SELECt MAX(codigo_sala) FROM P_RES_SALA");
        item.nombre="";
        item.empresa=gl.emp;
        item.codigo_sucursal=gl.tienda;
        item.activo=1;
        item.escala=1;
        item.tam_letra=24;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo_sala;
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

    private void deleteItem() {
        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_RES_MESA WHERE CODIGO_SALA="+item.codigo_sala);
            holder.delete(item);
            finish();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(item.nombre);
    }

    private boolean validaDatos() {
        String ss;

        try {
            ss=txt1.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir descripcion!");return false;
            }
            item.nombre=ss;

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
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskDelete(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskDelete2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskDelete2(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
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