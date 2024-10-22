package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_domicilio_encObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbPedidoEnc;
import com.dtsgt.ladapt.LA_D_domicilio_enc;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DomPedComp extends PBase {

    private GridView gridView;
    private TextView lblcant;

    private LA_D_domicilio_enc adapter;

    private clsD_domicilio_encObj D_domicilio_encObj;

    private ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();

    private clsClasses.clsD_domicilio_enc selitem;

    private long fechahoy;
    private int limtiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_ped_comp);

            super.InitBase();

            gridView = findViewById(R.id.gridView1);
            lblcant =  findViewById(R.id.textView196);lblcant.setText("");

            limtiempo=gl.peDomTiempo;

            D_domicilio_encObj=new clsD_domicilio_encObj(this,Con,db);

            fechahoy =du.getActDate();

            setHandlers();

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doAdd(View view) {
        startActivity(new Intent(this,DomImport.class));
    }

    public void doMenu(View view) {
        startActivity(new Intent(this, DomPedComp.class));
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                try {
                    Object lvObj = gridView.getItemAtPosition(position);
                    selitem = (clsClasses.clsD_domicilio_enc)lvObj;
                    selidx = position;

                    adapter.setSelectedIndex(position);

                    browse=1;
                    gl.dom_det_cod=selitem.corel;
                    startActivity(new Intent(DomPedComp.this,DomDetComp.class));
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
            };
        });

    }

    //endregion

    //region Main

    private void listItems() {
        Long fa=du.getActDateTime();
        int tent=0,tanul=0;

        try {
            items.clear();

            D_domicilio_encObj.fill("WHERE (fecha_hora>="+ fechahoy +") AND (estado in (4,7)) ORDER BY idorden");
            for (clsClasses.clsD_domicilio_enc itm : D_domicilio_encObj.items) {
                items.add(itm);
            }

            for (clsClasses.clsD_domicilio_enc itm : items) {

                itm.sorden="#"+itm.idorden % 1000;
                itm.sestado=app.estadoNombre(itm.estado);
                itm.shora=du.shora(itm.fecha_hora);
                itm.smin="";
                itm.timeflag=false;

                if (itm.estado==7) tent++;
                if (itm.estado==4) tanul++;

            }

            adapter=new LA_D_domicilio_enc(this,this,items);
            gridView.setAdapter(adapter);

            lblcant.setText("ENTREGADOS: "+tent+"  - ANULADOS: "+tanul);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    ;break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            D_domicilio_encObj.reconnect(Con,db);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

}