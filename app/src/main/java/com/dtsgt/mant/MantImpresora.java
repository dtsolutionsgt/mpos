package com.dtsgt.mant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_impresora_marcaObj;
import com.dtsgt.classes.clsP_impresora_modeloObj;
import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.mpos.Orden;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MantImpresora extends PBase {

    private EditText txt1,txt2,txt3,txt4,txt5;
    private TextView lbl1,lbl2;
    private Spinner spin;

    private clsP_impresoraObj holder;
    private clsClasses.clsP_impresora item=clsCls.new clsP_impresora();

    private ArrayList<String> spinlist=new ArrayList<String>();

    private String id,nmarca;
    private int idmarca;
    private boolean newitem=false;

    //"SIN IMPRESORA";
    //"EPSON TM BlueTooth";
    //"HP Engage USB"
    //"3nStar LAN"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_impresora);

        super.InitBase();

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.editText12);
        txt4 = findViewById(R.id.editText8);
        txt5 = findViewById(R.id.editText21);
        lbl1 = findViewById(R.id.textView222);
        lbl2 = findViewById(R.id.textView224);
        spin = findViewById(R.id.spinner18);

        id=gl.gcods;

        holder =new clsP_impresoraObj(this,Con,db);

        if (id.isEmpty()) newItem(); else loadItem();

        setHandlers();

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

    public void doExit(View view) {
        msgAskExit("Salir");
    }

    public void doPrinter(View view) {
        showMarcaList();
    }

    private void setHandlers() {

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);

                    if (spinlabel != null){
                        spinlabel.setTextColor(Color.BLACK);spinlabel.setPadding(5, 0, 0, 0);
                        spinlabel.setTextSize(18);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);
                    }

                    item.tipo_impresora=spinlist.get(position);
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
            holder.fill("WHERE codigo_impresora="+id);
            item=holder.first();

            showItem();

            txt1.setEnabled(false);
            txt2.requestFocus();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        int newid;

        newitem=true;
        txt1.setEnabled(true);
        txt2.requestFocus();

        newid=holder.newID("SELECT MAX(codigo_archivoconf) FROM P_archivoconf");

        item.codigo_impresora=newid;
        item.empresa=gl.emp;
        item.codigo_sucursal=gl.tienda;
        item.nombre="";
        item.numero_serie="";
        item.codigo_marca=0;
        item.codigo_modelo=0;
        item.tipo_impresora="SIN IMPRESORA";
        item.mac="";
        item.ip="";
        item.fecha_agr=0;
        item.impresiones=1;
        item.activo=1;

        showItem();
    }

    private void addItem() {
        try {
            holder.add(item);
            gl.gcods=""+item.codigo_impresora;
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
        txt1.setText(""+item.codigo_impresora);
        txt2.setText(item.nombre);
        txt3.setText(item.mac);
        txt4.setText(item.ip);
        txt5.setText(item.numero_serie);

        fillSpinnerTipo(item.tipo_impresora);
        setMarca(item.codigo_modelo);
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

            item.mac=""+txt3.getText().toString();
            item.ip=""+txt4.getText().toString();
            item.numero_serie=""+txt5.getText().toString();

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean fillSpinnerTipo(String selid){
        int selidx=0;
        int scod;

        spinlist.clear();

        try {
            spinlist.add("SIN IMPRESORA");
            spinlist.add("EPSON TM BlueTooth");
            spinlist.add("HP Engage USB");
            spinlist.add("3nStar LAN");

            for (int i = 0; i <spinlist.size(); i++) {
                if (spinlist.get(i).equalsIgnoreCase(selid)) selidx=i;
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

    /*
    private boolean fillSpinnerModelo(int selid){
        clsP_impresora_modeloObj modelos =new clsP_impresora_modeloObj(this,Con,db);
        int selidx=0;
        int scod;

        spincode.clear();spinimp.clear();

        try {
            modelos.fill(" WHERE (Activo=1) OR (codigo_impresora_modelo='"+selid+"') ORDER BY Nombre");
            if (modelos.count==0) {
                msgAskReturn("Lista de modelos está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <modelos.count; i++) {
                scod=modelos.items.get(i).codigo_impresora_modelo;
                spincode.add(""+scod);
                spinimp.add(modelos.items.get(i).nombre);
                if (scod==selid) selidx=i;

            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinimp);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinm.setAdapter(dataAdapter);

        try {
            spinm.setSelection(selidx);
            setMarca(Integer.parseInt(spincode.get(selidx)));
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;
    }
    */


    private void showMarcaList() {

         try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(MantImpresora.this,"Marca de impresora");
            listdlg.setLines(6);

            clsP_impresora_marcaObj marcas =new clsP_impresora_marcaObj(this,Con,db);
            marcas.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");
            for (int i = 0; i <marcas.count; i++) {
                listdlg.add(marcas.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {

                try {
                    idmarca=marcas.items.get(position).codigo_impresora_marca;
                    nmarca=marcas.items.get(position).nombre;
                    showModeloList();

                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void showModeloList() {

        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(MantImpresora.this,nmarca);

            clsP_impresora_modeloObj modelos =new clsP_impresora_modeloObj(this,Con,db);
            modelos.fill("WHERE CODIGO_IMPRESORA_MARCA="+idmarca+" ORDER BY NOMBRE");

            for (int i = 0; i <modelos.count; i++) {
                listdlg.add(modelos.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        item.codigo_modelo=modelos.items.get(position).codigo_impresora_modelo;
                        setMarca(item.codigo_modelo);
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }


    }

    private void setMarca(int idmodelo) {

        try {

            clsP_impresora_modeloObj modelos =new clsP_impresora_modeloObj(this,Con,db);
            clsP_impresora_marcaObj marcas =new clsP_impresora_marcaObj(this,Con,db);

            modelos.fill("WHERE CODIGO_IMPRESORA_MODELO="+idmodelo);
            lbl2.setText(modelos.first().nombre);

            marcas.fill("WHERE CODIGO_IMPRESORA_MARCA="+modelos.first().codigo_impresora_marca);
            lbl1.setText(marcas.first().nombre);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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
