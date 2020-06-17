package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_motivoajusteObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantMotivoAjuste extends PBase {

    private ImageView imgstat,imgadd;
    private EditText txt1,txt2;

    private clsP_motivoajusteObj holder;
    private clsClasses.clsP_motivoajuste item=clsCls.new clsP_motivoajuste();

    private String id;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_motivo_ajuste);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        imgstat = (ImageView) findViewById(R.id.imageView61);
        imgadd = (ImageView) findViewById(R.id.imgImg7);

        holder =new clsP_motivoajusteObj(this,Con,db);

        id=gl.gcods;
        if (id.isEmpty()) newItem(); else loadItem();

        if (gl.peMCent) {
            //if (!app.grant(13,gl.rol)) {
                imgadd.setVisibility(View.INVISIBLE);
                imgstat.setVisibility(View.INVISIBLE);
            //}
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
        if (item.activo) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO_MOTIVO_AJUSTE='"+id+"'");
            item=holder.first();

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.activo) {
                imgstat.setImageResource(R.drawable.delete_64);
            } else {
                imgstat.setImageResource(R.drawable.mas);
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.codigo_motivo_ajuste=0;
        item.empresa=gl.emp;
        item.nombre="";
        item.activo=true;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo_motivo_ajuste;
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

    //endregion

    //region Aux

    private void showItem() {
        if(newitem) txt1.setText(""); else txt1.setText(""+item.codigo_motivo_ajuste);
        txt2.setText(item.nombre);
    }

    private boolean validaDatos() {
        String ss;

        try {

            if (newitem) {
                ss=txt1.getText().toString();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill("WHERE CODIGO_MOTIVO_AJUSTE='"+ss+"'");
                if (holder.count>0) {
                    msgbox("¡Código ya existe!\n"+holder.first().nombre);return false;
                }

                item.codigo_motivo_ajuste=Integer.parseInt(ss);
            }

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
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

    private void msgAskStatus(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Registro");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (item.activo) {
                    item.activo=false;
                } else {
                    item.activo=true;
                };
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