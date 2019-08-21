package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantProducto extends PBase {

    private ImageView imgstat;
    private EditText txt1,txt2,txt3,txt4;
    private Spinner spin;

    private clsP_productoObj holder;
    private clsClasses.clsP_producto item=clsCls.new clsP_producto();

    private ArrayList<String> spincode= new ArrayList<String>();
    private ArrayList<String> spinlist = new ArrayList<String>();

    private String id;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_producto);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.editText6);
        txt4 = (EditText) findViewById(R.id.editText13);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        spin = (Spinner) findViewById(R.id.spinner10);

        holder =new clsP_productoObj(this,Con,db);

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
        if (item.activo==1) {
            msgAskStatus("Deshabilitar registro");
        } else {
            msgAskStatus("Habilitar registro");
        }
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TextView spinlabel;
                String scod, idposition;

                try {
                    spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);
                    spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(18);
                    spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    scod = spincode.get(position);
                    item.linea = scod;

                 } catch (Exception e) {
                    addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    mu.msgbox(e.getMessage());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });
    }

    //endregion

    //region Main

    private void loadItem() {
        try {
            holder.fill("WHERE CODIGO='"+id+"'");
            item=holder.first();

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();
            imgstat.setVisibility(View.VISIBLE);
            if (item.activo==1) {
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

        item.codigo="";
        item.tipo="P";
        item.linea="";
        item.sublinea="1";
        item.empresa=gl.emp;
        item.marca="1";
        item.codbarra="";
        item.desccorta="";
        item.desclarga="";
        item.costo=0;
        item.factorconv=1;
        item.unidbas="UNI";
        item.unidmed="";
        item.unimedfact=0;
        item.unigra="";
        item.unigrafact=0;
        item.descuento="S";
        item.bonificacion="S";
        item.imp1=0;
        item.imp2=0;
        item.imp3=0;
        item.vencomp="";
        item.devol="N";
        item.ofrecer="N";
        item.rentab="N";
        item.descmax="N";
        item.peso_promedio=1;
        item.modif_precio=0;
        item.imagen="";
        item.video="";
        item.venta_por_peso=0;
        item.es_prod_barra=0;
        item.unid_inv="UNI";
        item.venta_por_paquete=0;
        item.venta_por_factor_conv=0;
        item.es_serializado=0;
        item.param_caducidad=0;
        item.producto_padre="";
        item.factor_padre=1;
        item.tiene_inv=0;
        item.tiene_vineta_o_tubo=0;
        item.precio_vineta_o_tubo=0;
        item.es_vendible=0;
        item.unigrasap=0;
        item.um_salida="UNI";
        item.activo=1;

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
        txt1.setText(item.codigo);
        txt2.setText(item.desclarga);
        txt3.setText(item.codbarra);
        txt4.setText(item.unidbas);

        fillSpinner(item.linea);
    }

    private boolean validaDatos() {
        String ss;

        try {

            if (newitem) {
                ss=txt1.getText().toString();
                if (ss.isEmpty()) {
                    msgbox("¡Falta definir código!");return false;
                }

                holder.fill("WHERE CODIGO='"+ss+"'");
                if (holder.count>0) {
                    //msgbox("¡Código ya existe!\n"+holder.first().nombre);return false;
                }

                item.codigo=ss;
            }

            ss=txt2.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Nombre incorrecto!");
                return false;
            } else {
                //item.nombre=ss;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private void fillSpinner(String selid){
        clsP_lineaObj lineas =new clsP_lineaObj(this,Con,db);
        int selidx=0;
        String scod;

        spincode.clear();spinlist.clear();

        try {
            lineas.fill(" WHERE (Activo=1) OR (Codigo='"+selid+"') ORDER BY Nombre");
            if (lineas.count==0) {
                msgAskReturn("Lista de familias está vacia, no se puede continuar");return;
            }

            for (int i = 0; i <lineas.count; i++) {
                scod=lineas.items.get(i).codigo;
                spincode.add(scod);
                spinlist.add(lineas.items.get(i).nombre);
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.linea=scod;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        try {
            spin.setSelection(selidx);
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
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
                if (item.activo==1) {
                    item.activo=0;
                } else {
                    item.activo=1;
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

    private void msgAskReturn(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Familias");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                  finish();
            }
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
