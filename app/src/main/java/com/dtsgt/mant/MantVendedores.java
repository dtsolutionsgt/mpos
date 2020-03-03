package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsP_usgrupoObj;
import com.dtsgt.classes.clsVendedoresBL;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_P_sucursal;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MantVendedores extends PBase {

    private ImageView imgstat;
    private EditText txt1,txt2,txt3,txt4;
    private ListView listView;
    private Spinner spin;

    private clsVendedoresObj holder;
    private clsClasses.clsVendedores item=clsCls.new clsVendedores();
    private LA_P_sucursal adapter;
    private clsP_sucursalObj P_sucursalObj;

    private ArrayList<String> spincode=new ArrayList<String>();
    private ArrayList<String> spinlist=new ArrayList<String>();

    private String id;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_vendedores);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt2);
        txt3 = (EditText) findViewById(R.id.editText12);
        txt4 = (EditText) findViewById(R.id.editText14);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        listView = (ListView) findViewById(R.id.listView1);
        spin = (Spinner) findViewById(R.id.spinner18);

        id=gl.gcods;

        holder =new clsVendedoresObj(this,Con,db);
        P_sucursalObj=new clsP_sucursalObj(this,Con,db);

        if (id.isEmpty()) newItem(); else loadItem();

        setHandlers();
        listItems();
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

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_sucursal item = (clsClasses.clsP_sucursal)lvObj;

                adapter.setSelectedIndex(position);

                if (item.activo==1) item.activo=0;else item.activo=1;
                adapter.notifyDataSetChanged();

            };
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(18);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = spincode.get(position);
                    item.nivel =Integer.parseInt(scod);
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
        item.nombre="";
        item.clave="";
        item.ruta="";
        item.nivel=1;
        item.nivelprecio=0;
        item.bodega="";
        item.subbodega="";
        item.activo=1;

        showItem();
    }

    private void addItem() {
        try {
            //holder.add(item);
            saveItems();
            gl.gcods=""+item.codigo;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            //holder.update(item);
            saveItems();
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void saveItems(){
        clsVendedoresBL vends=new clsVendedoresBL(holder);

        try {
            db.beginTransaction();

            vends.delete(item.codigo);

            for (int i = 0; i <P_sucursalObj.count; i++) {
                if (P_sucursalObj.items.get(i).activo==1) {
                    item.ruta=P_sucursalObj.items.get(i).codigo;
                    holder.add(item);
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    private void listItems() {
        int act;

        try {
            P_sucursalObj.fill();

            for (int i = 0; i <P_sucursalObj.count; i++) {
                holder.fill("WHERE (CODIGO='"+item.codigo+"') AND (RUTA='"+P_sucursalObj.items.get(i).codigo+"')");
                if (holder.count>0) act=1;else act=0;
                P_sucursalObj.items.get(i).activo=act;
            }

            adapter=new LA_P_sucursal(this,this,P_sucursalObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(item.codigo);
        txt2.setText(item.nombre);
        txt3.setText(item.clave);
        txt4.setText(""+item.nivelprecio);

        fillSpinner(""+item.nivel);
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

            ss=txt3.getText().toString();
            if (ss.isEmpty()) {
                msgbox("¡Clave incorrecta!");
                return false;
            } else {
                item.clave=ss;
            }

            ss=txt4.getText().toString();if (ss.isEmpty()) ss="0";
            if (ss.isEmpty()) {
                msgbox("¡Comisión incorrecta!");return false;
            } else {
                try {
                    item.nivelprecio=Double.parseDouble(ss);
                    if (item.nivelprecio<0 || item.nivelprecio>=100) throw new Exception();
                } catch (Exception e) {
                    msgbox("¡Comisión incorrecta!");return false;
                }
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean fillSpinner(String selid){
        clsP_usgrupoObj grupos =new clsP_usgrupoObj(this,Con,db);
        int selidx=0;
        String scod;

        spincode.clear();spinlist.clear();

        try {
            grupos.fill(" WHERE (Cuenta='S') ORDER BY Nombre");

            for (int i = 0; i <grupos.count; i++) {
                scod=grupos.items.get(i).codigo;
                spincode.add(scod);
                spinlist.add(grupos.items.get(i).nombre);
                if (scod.equalsIgnoreCase(selid)) selidx=i;
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
            P_sucursalObj.reconnect(Con,db);
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
