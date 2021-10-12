package com.dtsgt.mant;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_usgrupoObj;
import com.dtsgt.classes.clsP_usgrupoopcObj;
import com.dtsgt.classes.clsP_usopcionObj;
import com.dtsgt.ladapt.LA_P_usopcion;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import java.util.ArrayList;


public class MantRol extends PBase {

    private ListView listView;
    private Spinner spin;

    private ArrayList<Integer> spincode=new ArrayList<Integer>();
    private ArrayList<String> spinlist=new ArrayList<String>();

    private LA_P_usopcion adapter;
    private clsP_usopcionObj P_usopcionObj;
    private clsP_usgrupoopcObj P_usgrupoopcObj;

    private int idgroup=0;
    private int idopt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_rol);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        spin = (Spinner) findViewById(R.id.spinner17);

        P_usopcionObj=new clsP_usopcionObj(this,Con,db);
        P_usgrupoopcObj=new clsP_usgrupoopcObj(this,Con,db);

        setHandlers();

        fillSpinner();

        listItems();

        listView.setEnabled(true);
        //if (gl.peMCent) listView.setEnabled(false);

    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_usopcion item = (clsClasses.clsP_usopcion)lvObj;

                idopt=item.codigo;

                adapter.setSelectedIndex(position);

                if (item.nombre.isEmpty()) {
                    item.nombre="x";
                    addItem();
                } else {
                    item.nombre="";
                    delItem();
                }
                adapter.notifyDataSetChanged();

            };
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    TextView spinlabel = (TextView) parentView.getChildAt(0);
                    spinlabel.setTextColor(Color.BLACK);
                    spinlabel.setPadding(5, 0, 0, 0);
                    spinlabel.setTextSize(18);
                    spinlabel.setTypeface(spinlabel.getTypeface(), Typeface.BOLD);

                    idgroup = spincode.get(position);
                    listItems();
                } catch (Exception e) {
                    addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
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

    private void listItems() {
        ArrayList<Integer> opts=new ArrayList<Integer>();
        int opt;

        try {
            P_usopcionObj.fill();
            P_usgrupoopcObj.fill("WHERE GRUPO='"+idgroup+"'");

            for (int i = 0; i <P_usgrupoopcObj.count; i++) {
                opts.add(P_usgrupoopcObj.items.get(i).opcion);
            }

            for (int i = 0; i <P_usopcionObj.count; i++) {
                P_usopcionObj.items.get(i).menugroup=P_usopcionObj.items.get(i).menugroup+" "+P_usopcionObj.items.get(i).nombre;
                opt=P_usopcionObj.items.get(i).codigo;
                if (opts.contains(opt)) {
                    P_usopcionObj.items.get(i).nombre="X";
                } else {
                    P_usopcionObj.items.get(i).nombre="";
                }
            }

            adapter=new LA_P_usopcion(this,this,P_usopcionObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void addItem() {
        clsClasses.clsP_usgrupoopc opc=clsCls.new clsP_usgrupoopc();

        try {
            db.beginTransaction();

            opc.grupo=idgroup;
            opc.opcion=idopt;

            P_usgrupoopcObj.delete(opc);
            P_usgrupoopcObj.add(opc);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    private void delItem() {
        clsClasses.clsP_usgrupoopc opc=clsCls.new clsP_usgrupoopc();
        try {
            opc.grupo=idgroup;
            opc.opcion=idopt;

            P_usgrupoopcObj.delete(opc);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private boolean fillSpinner(){
        clsP_usgrupoObj grupo =new clsP_usgrupoObj(this,Con,db);

        spincode.clear();spinlist.clear();

        try {
            grupo.fill(" WHERE (CUENTA='S') ORDER BY Nombre");

            idgroup=grupo.first().codigo;

            for (int i = 0; i <grupo.count; i++) {
                spincode.add(grupo.items.get(i).codigo);
                spinlist.add(grupo.items.get(i).nombre);
            }
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            mu.msgbox( e.getMessage());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(dataAdapter);

        try {
            spin.setSelection(0);
        } catch (Exception e) {
        }

        return true;

    }


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_usopcionObj.reconnect(Con,db);
            P_usgrupoopcObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}
