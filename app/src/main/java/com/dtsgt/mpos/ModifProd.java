package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_modificadorObj;
import com.dtsgt.ladapt.LA_P_modiflist;
import com.dtsgt.ladapt.LA_P_usopcion;

import java.util.ArrayList;

public class ModifProd extends PBase {

    private ListView listView;
    private TextView lblprod;

    private clsP_modificadorObj P_modificadorObj;

    private ArrayList<clsClasses.clsP_usopcion> items= new ArrayList<clsClasses.clsP_usopcion>();
    private LA_P_modiflist adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif_prod);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        lblprod   = findViewById(R.id.textView274);lblprod.setText(gl.gstr2);

        P_modificadorObj=new clsP_modificadorObj(this,Con,db);

        setHandlers();

        listItems();

        //gl.idmodgr  , gl.produid
    }


    //region Events

    public void doApply(View view) {


    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {



    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsP_usopcion item;

        try {
            P_modificadorObj.fill("WHERE (CODIGO_GRUPO="+gl.idmodgr+") ORDER BY NOMBRE");

            items.clear();
            for (int i = 0; i <P_modificadorObj.count; i++) {
                item = clsCls.new clsP_usopcion();

                item.codigo=P_modificadorObj.items.get(i).codigo_modif;
                item.menugroup=P_modificadorObj.items.get(i).nombre;
                item.nombre="";

                items.add(item);
            }

            adapter=new LA_P_modiflist(this,this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_modificadorObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}