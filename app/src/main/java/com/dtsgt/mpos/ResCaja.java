package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.dtsgt.classes.clsViewObj;
import com.dtsgt.ladapt.LA_ResCaja;

public class ResCaja extends PBase {

    private GridView gridView;

    private LA_ResCaja adapter;
    private clsViewObj ViewObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_caja);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);

        listItems();
    }

    //region Events


    //endregion

    //region Main

    private void listItems() {

        try {


            sql="SELECT P_RES_SESION.ESTADO, T_ORDENCUENTA.COREL, P_RES_MESA.NOMBRE,T_ORDENCUENTA.ID, '','','','','' " +
                    "FROM P_RES_MESA INNER JOIN " +
                    "P_RES_SESION ON P_RES_MESA.CODIGO_MESA = P_RES_SESION.CODIGO_MESA INNER JOIN " +
                    "T_ORDENCUENTA ON P_RES_SESION.ID = T_ORDENCUENTA.COREL " +
                    "WHERE        (P_RES_SESION.ESTADO = 2) OR (P_RES_SESION.ESTADO = 3) " +
                    "ORDER BY P_RES_MESA.NOMBRE, T_ORDENCUENTA.ID ";

            ViewObj.fillSelect(sql);

            adapter=new LA_ResCaja(this,this,ViewObj.items);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion


}