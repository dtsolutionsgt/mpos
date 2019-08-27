package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_clienteObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantCli extends PBase {
    private ImageView imgstat;
    private EditText txt1,txt2,txt3;

    private clsP_clienteObj holder;
    private clsClasses.clsP_cliente item=clsCls.new clsP_cliente();

    private String id;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_cli);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.txt3);
        imgstat = (ImageView) findViewById(R.id.imageView31);

        holder =new clsP_clienteObj(this,Con,db);

        id=gl.gcods;
        if (id.isEmpty()) newItem(); else loadItem();
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
        if (item.bloqueado.equalsIgnoreCase("N")) {
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
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();

            showItem();

            txt1.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.bloqueado.equalsIgnoreCase("N")) {
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

        item.codigo=" ";
        item.nombre=" ";
        item.bloqueado ="N";
        item.tiponeg = "1";
        item.tipo = "1";
        item.subtipo = "1";
        item.canal = "1";
        item.subcanal = "1";
        item.nivelprecio = 1;
        item.mediapago = "1";
        item.limitecredito = 0;
        item.diacredito = 0;
        item.descuento = "S";
        item.bonificacion = "S";
        item.ultvisita = 0;
        item.impspec = 0;
        item.invtipo = "0";
        item.invequipo = "N";
        item.inv1 = "N";
        item.inv2 = "N";
        item.inv3 = "N";
        item.nit = " ";
        item.mensaje = "";
        item.email = " ";
        item.eservice =  " ";
        item.telefono =  " ";
        item.dirtipo = " ";
        item.direccion =" ";
        item.region =  " ";
        item.sucursal =  " ";
        item.municipio =  " ";
        item.ciudad =  " ";
        item.zona = 0;
        item.colonia =  " ";
        item.avenida =  " ";
        item.calle =  " ";
        item.numero =  " ";
        item.cartografico =  " ";
        item.coorx = 0;
        item.coory = 0;
        item.bodega =  " ";
        item.cod_pais =  " ";
        item.firmadig =  " ";
        item.codbarra =  " ";
        item.validacredito ="S";
        item.fact_vs_fact =  " ";
        item.chequepost =  " ";
        item.precio_estrategico =  "N";
        item.nombre_propietario =  " ";
        item.nombre_representante = " ";
        item.percepcion = 0;
        item.tipo_contribuyente = " ";
        item.id_despacho = 0;
        item.id_facturacion = 0;
        item.modif_precio = 0;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo;
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
        txt1.setText(item.nit);
        txt2.setText(item.nombre);
        txt3.setText(item.direccion);
    }

    private boolean validaDatos() {
        String ss;

        try {

            ss = txt1.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Falta definir NIT!");
                return false;
            }

            holder.fill("WHERE CODIGO='" + ss + "'");
            if (holder.count > 0) {
                msgbox("¡NIT ya existe!\n" + holder.first().nombre);
                return false;
            }

            item.nit = txt1.getText().toString();
            if (newitem) item.codigo = item.nit;

            ss = txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                item.nombre=ss;
            }

            item.direccion=txt3.getText().toString();

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
                if (item.bloqueado.equalsIgnoreCase("N")) {
                    item.bloqueado="S";
                } else {
                    item.bloqueado="N";
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
