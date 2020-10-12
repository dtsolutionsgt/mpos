package com.dtsgt.mant;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_impuestoObj;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_salaObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantResMesa extends PBase {

    private ImageView imgstat,imgadd;
    private EditText txt1,txt2,txt3;
    private Spinner spin1,spin2;

    private clsP_res_mesaObj holder;
    private clsClasses.clsP_res_mesa item=clsCls.new clsP_res_mesa();
    private ArrayList<String> code1,code2,list1,list2;


    private String id;
    private int idsala,idgrupo;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_res_mesa);

        super.InitBase();

        txt1 = (EditText) findViewById(R.id.txt1);
        txt2 = (EditText) findViewById(R.id.txt16);
        txt3 = (EditText) findViewById(R.id.txt17);
        imgstat = (ImageView) findViewById(R.id.imageView31);
        imgadd = (ImageView) findViewById(R.id.imgImg2);
        spin1 = (Spinner) findViewById(R.id.spinner20);
        spin2 = (Spinner) findViewById(R.id.spinner21);

        holder =new clsP_res_mesaObj(this,Con,db);

        code1=new ArrayList<String>();list1=new ArrayList<String>();
        code2=new ArrayList<String>();list2=new ArrayList<String>();

        setHandlers();

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
        msgAskDelete("Eliminar la mesa");
    }

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    private void setHandlers() {

      spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = code1.get(position);
                    item.codigo_sala = Integer.parseInt(scod);
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

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(21);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    String scod = code2.get(position);
                    item.codigo_grupo = Integer.parseInt(scod);
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
            holder.fill("WHERE CODIGO_MESA="+id);
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

        item.codigo_mesa=holder.newID("SELECT MAX(codigo_mesa) FROM P_RES_MESA");
        item.empresa=gl.emp;
        item.codigo_sucursal=gl.tienda;
        item.nombre="";
        item.codigo_sala=0;
        item.codigo_grupo=0;
        item.largo=1;
        item.ancho=1;
        item.posx=0;
        item.posy=0;
        item.codigo_qr="";

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
            holder.delete(item);
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void showItem() {
        txt1.setText(item.nombre);
        txt2.setText(mu.frmdecno(item.largo));
        txt3.setText(mu.frmdecno(item.ancho));

        idsala=item.codigo_sala;
        idgrupo=item.codigo_grupo;
        fillSpin1(idsala);
        fillSpin2(idgrupo);

    }

    private boolean validaDatos() {
        String ss;
        double val;

        ss=txt1.getText().toString();
        if (ss.isEmpty()) {
            msgbox("¡Falta definir descripcion!");return false;
        }
        item.nombre=ss;

        try {
            val=Double.parseDouble(txt2.getText().toString());
            if (val<=0) throw new Exception();
            item.largo=val;
        } catch (Exception e) {
            msgbox("Largo incorrecto");txt2.requestFocus();return false;
        }

        try {
            val=Double.parseDouble(txt3.getText().toString());
            if (val<=0) throw new Exception();
            item.ancho=val;
        } catch (Exception e) {
            msgbox("Ancho incorrecto");txt3.requestFocus();return false;
        }

        return true;
    }


    private void fillSpin1(int selid) {
        clsP_res_salaObj sala = new clsP_res_salaObj(this, Con, db);
        int selidx = 0;
        String scod;

        code1.clear();list1.clear();

        try {
            sala.fill("ORDER BY Nombre");
            if (sala.count==0) {
                msgAskReturn("Lista de salas está vacia, no se puede continuar");return;
            }

            for (int i = 0; i <sala.count; i++) {
                scod=""+sala.items.get(i).codigo_sala;
                code1.add(scod);
                list1.add(sala.items.get(i).nombre);
                if (sala.items.get(i).codigo_sala==selid) selidx=i;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin1.setAdapter(dataAdapter);

        try {
            spin1.setSelection(selidx);
        } catch (Exception e) {
        }
    }

    private void fillSpin2(int selid){
        clsP_res_grupoObj grupo =new clsP_res_grupoObj(this,Con,db);
        int selidx=-1;
        String scod;

        code2.clear();list2.clear();

        try {
            grupo.fill("ORDER BY Nombre");
            if (grupo.count==0) {
                msgAskReturn("Lista de grupos está vacia, no se puede continuar");return;
            }

            for (int i = 0; i <grupo.count; i++) {
                scod=""+grupo.items.get(i).codigo_grupo;
                code2.add(scod);
                list2.add(grupo.items.get(i).nombre);
                if (grupo.items.get(i).codigo_grupo==selid) selidx=i;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin2.setAdapter(dataAdapter);

        try {
            spin2.setSelection(selidx);
        } catch (Exception e) {
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

    private void msgAskReturn(String msg) {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage( msg );

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
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