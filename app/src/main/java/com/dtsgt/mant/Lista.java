package com.dtsgt.mant;

import android.os.Bundle;
import android.view.View;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.ladapt.LA_Lista;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Lista extends PBase {

    private ListView listView;
    private LA_Lista adapter;
    private clsListaObj ViewObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);

        ViewObj=new clsListaObj(this,Con,db);

        setHandlers();

        listItems();

    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsLista item = (clsClasses.clsLista)lvObj;

                adapter.setSelectedIndex(position);

            };
        });
    }


    //endregion

    //region Main

    private void listItems() {

        try {
            ViewObj.fillSelect("SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_LINEA ORDER BY NOMBRE");

            adapter=new LA_Lista(this,this,ViewObj.items);
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
    protected void onResume() {
        super.onResume();
        try {
            ViewObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}
