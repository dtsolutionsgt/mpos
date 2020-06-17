package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantCaja extends PBase {

    private ImageView imgstat,imgadd;
    private EditText txt1,txt2;
    private Spinner spin;

    private clsP_rutaObj holder;
    private clsClasses.clsP_ruta item=clsCls.new clsP_ruta();

    private ArrayList<String> spincode,spinlist;

    private String id;
    private int precpos=0;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_caja);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgadd = (ImageView) findViewById(R.id.imgImg2);
        spin = (Spinner) findViewById(R.id.spinner10);

        holder =new clsP_rutaObj(this,Con,db);

        spincode=new ArrayList<String>();spinlist=new ArrayList<String>();

        id=gl.gcods;

        setHandlers();

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

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = spincode.get(position);
                    item.sucursal = scod;
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

            /*
            if (item.activo.equalsIgnoreCase("S")) {
                imgstat.setImageResource(R.drawable.delete_64);
            } else {
                imgstat.setImageResource(R.drawable.mas);
            }

             */
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        newitem=true;
        txt1.requestFocus();

        imgstat.setVisibility(View.INVISIBLE);

        item.codigo="";
        item.nombre="";
        item.sucursal="1";
        item.activo=false;

        showItem();
    }

    private void addItem() {

        try {
            holder.add(item);

            gl.gcods = "" + item.codigo;
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
        txt2.setText(item.nombre);

        if (!fillSpinner(item.sucursal)) return;
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
                    msgbox("¡Código ya existe!\n"+holder.first().nombre);return false;
                }
                item.codigo=ss;
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

    private boolean fillSpinner(String selid){
        clsP_sucursalObj sucur =new clsP_sucursalObj(this,Con,db);
        int selidx=0;
        String scod;

        spincode.clear();spinlist.clear();

        try {
            sucur.fill(" WHERE (Activo=1) OR (Codigo='"+selid+"') ORDER BY Nombre");
            if (sucur.count==0) {
                msgAskReturn("Lista de sucursales está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <sucur.count; i++) {
                scod=sucur.items.get(i).codigo;
                spincode.add(scod);
                spinlist.add(sucur.items.get(i).descripcion);
                if (scod.equalsIgnoreCase(selid)) selidx=i;
                if (i==0 &&  newitem) item.sucursal=scod;
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

        return true;

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
