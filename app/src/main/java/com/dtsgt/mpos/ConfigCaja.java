package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_rutaObj;
import com.dtsgt.classes.clsP_sucursalObj;

import java.util.ArrayList;

public class ConfigCaja extends PBase {

    private Spinner spin,spin2;

    private ArrayList<String> spincode,spinlist,cajalist,cajacode;

    private int idsuc,idcaja;
    private int precpos=0;
    private boolean newitem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_caja);

        super.InitBase();

        spin = (Spinner) findViewById(R.id.spinner10);
        spin2 = (Spinner) findViewById(R.id.spinner12);

        spincode=new ArrayList<String>();spinlist=new ArrayList<String>();
        cajacode=new ArrayList<String>();cajalist=new ArrayList<String>();

        gl.configCajaSuc = false;

        setHandlers();

        loadItem();
    }

    //region Events

    public void doSave(View view) {
        if (validaDatos()) msgAskAdd("Aplicar asignación");
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
                    spinlabel.setTextSize(18);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    idsuc = Integer.valueOf(spincode.get(position));

                    fillSpinner2("");

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
                    spinlabel.setTextSize(18);spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    idcaja = Integer.valueOf(cajacode.get(position));
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
        Cursor DT;

        try {
            sql = "SELECT SUCURSAL,RUTA FROM Params";
            DT = Con.OpenDT(sql);

            idsuc=DT.getInt(0);
            idcaja=DT.getInt(1);

            fillSpinner(String.valueOf(idsuc));
            fillSpinner2(String.valueOf(idcaja));

            if (spinlist.size()==2 && cajalist.size()==2){
                if (validaDatos()) msgAskAdd("Aplicar asignación");
            }

            if (DT!=null) DT.close();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateItem() {
        try {
            sql = "UPDATE Params SET SUCURSAL='"+idsuc+"',RUTA='"+idcaja+"'";
            db.execSQL(sql);

            gl.configCajaSuc = true;

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private boolean validaDatos() {

        int ii=0;
        try {
            if (idsuc==0) {
                msgbox("Falta definir tienda");return false;
            }

            if (idcaja==0) {
                msgbox("Falta definir caja");return false;
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
        spincode.add("0");spinlist.add("");

        try {
            sucur.fill(" WHERE (Activo=1) OR (Codigo='"+selid+"') ORDER BY Descripcion");
            if (sucur.count==0) {
                msgAskReturn("Lista de sucursales está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <sucur.count; i++) {
                scod=String.valueOf(sucur.items.get(i).codigo_sucursal);
                spincode.add(scod);
                spinlist.add(sucur.items.get(i).descripcion);
                if (scod.equalsIgnoreCase(selid)) selidx=i+1;
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        if (spinlist.size()==2){
            selidx=1;
        }

        try {
            spin.setSelection(selidx);

            if(selidx==1){
                idsuc =Integer.valueOf(spincode.get(1));
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }

    private boolean fillSpinner2(String selid){
        clsP_rutaObj ruta =new clsP_rutaObj(this,Con,db);

        int selidx=0;
        int scod;

        cajacode.clear();cajalist.clear();
        cajacode.add("0");cajalist.add("");

        try {
            //ruta.fill(" WHERE (Activo='S') OR (Codigo='"+selid+"') ORDER BY Nombre");

            if (idsuc==0) {
                ruta.fill("  ORDER BY Nombre");
            }else{

                ruta.fill(" WHERE SUCURSAL = " + idsuc + " ORDER BY Nombre");
            }

            if (ruta.count==0) {
                msgAskReturn("Lista de cajas está vacia, no se puede continuar");return false;
            }

            for (int i = 0; i <ruta.count; i++) {
                scod=ruta.items.get(i).codigo_ruta;
                cajacode.add(String.valueOf(scod));
                cajalist.add(ruta.items.get(i).nombre);
                if (String.valueOf(scod).equalsIgnoreCase(selid)) selidx=i+1;
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cajalist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin2.setAdapter(dataAdapter);

        if (cajalist.size()==2){
            selidx=1;
        }

        try {
            spin2.setSelection(selidx);

            if (selidx==1){
                idcaja =Integer.valueOf(cajacode.get(1));
            }

        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

        return true;

    }

    //endregion

    //region Dialogs

    private void msgAskAdd(String msg) {
        ExDialog dialog = new ExDialog(this);

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
    public void onBackPressed() {
        msgAskExit("Salir");
    }

    //endregion
}
