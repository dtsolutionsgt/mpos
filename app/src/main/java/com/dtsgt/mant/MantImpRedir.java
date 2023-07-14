package com.dtsgt.mant;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_impresora_redireccionObj;
import com.dtsgt.ladapt.LA_P_impresora_redireccion;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class MantImpRedir extends PBase {

    private ListView listView;

    private clsP_impresoraObj P_impresoraObj;
    private LA_P_impresora_redireccion adapter;
    private clsP_impresora_redireccionObj P_impresora_redireccionObj;

    public ArrayList<clsClasses.clsP_impresora_redireccion> ritems= new ArrayList<clsClasses.clsP_impresora_redireccion>();
    public clsClasses.clsP_impresora_redireccion ritem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mant_imp_redir);

            super.InitBase();

            listView = (ListView) findViewById(R.id.listView1);

            P_impresoraObj=new clsP_impresoraObj(this,Con,db);
            P_impresora_redireccionObj=new clsP_impresora_redireccionObj(this,Con,db);

            setHandlers();

            P_impresoraObj.fill("WHERE (CODIGO_SUCURSAL="+gl.tienda+")");

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
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
                clsClasses.clsP_impresora_redireccion item = (clsClasses.clsP_impresora_redireccion)lvObj;

                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();
            };
        });
    }


    //endregion

    //region Main

    private void listItems() {
        try {
            ritems.clear();

            for (int i = 0; i <P_impresoraObj.count; i++) {

                ritem= clsCls.new clsP_impresora_redireccion();

                ritem.codigo_redir=0;
                ritem.empresa=gl.emp;
                ritem.codigo_ruta=gl.codigo_ruta;
                ritem.codigo_impresora=P_impresoraObj.items.get(i).codigo_impresora;
                ritem.codigo_impresora_final=codigoImprRedir(ritem.codigo_impresora);
                ritem.nombre1=P_impresoraObj.items.get(i).nombre;
                ritem.nombre2=nombreImpresora( ritem.codigo_impresora_final);

                ritems.add(ritem);
            }

            adapter=new LA_P_impresora_redireccion(this,this,ritems);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private int codigoImprRedir(int idimp) {
        try {
            P_impresora_redireccionObj.fill("WHERE (CODIGO_RUTA ="+gl.codigo_ruta+") AND (CODIGO_IMPRESORA="+idimp+")");
            if (P_impresora_redireccionObj.count>0) {
                return P_impresora_redireccionObj.first().codigo_impresora_final;
            } else {
                return 0;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
    }

    private String nombreImpresora(int idimp) {
        try {
            for (int j = 0; j <P_impresoraObj.count; j++) {
                if (P_impresoraObj.items.get(j).codigo_impresora==idimp) {
                    return P_impresoraObj.items.get(j).nombre;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_impresoraObj.reconnect(Con,db);
            P_impresora_redireccionObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}