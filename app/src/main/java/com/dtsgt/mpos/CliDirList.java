package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_cliente_dirObj;
import com.dtsgt.ladapt.LA_P_cliente_dir;
import com.dtsgt.webservice.wsOpenDT;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CliDirList extends PBase {

    private ListView listView;
    private TextView lblNom;
    private ProgressBar pbar;

    private LA_P_cliente_dir adapter;
    private clsP_cliente_dirObj P_cliente_dirObj;

    private wsOpenDT ws;

    private Runnable rnAfterOpen;

    private int cliid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cli_dir_list);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        lblNom=findViewById(R.id.lblDescrip2);
        pbar=findViewById(R.id.progressBar6);pbar.setVisibility(View.VISIBLE);

        P_cliente_dirObj=new clsP_cliente_dirObj(this,Con,db);

        ws=new wsOpenDT(gl.wsurl);

        rnAfterOpen = new Runnable() {
            public void run() {
                procesaDirecciones();
            }
        };

        cliid=gl.codigo_cliente;
        lblNom.setText(gl.gstr);
        selidx=-1;selid=-1;

        setHandlers();

        listItems();
        loadDirs();

    }

    //region Events

    public void doAdd(View view) {
        browse=1;
        gl.idclidir=0;
        startActivity(new Intent(this,CliDirDom.class));
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_cliente_dir item = (clsClasses.clsP_cliente_dir)lvObj;

                adapter.setSelectedIndex(position);

                gl.dom_ddir= item.direccion+" , "+item.referencia;
                finish();
            };
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsP_cliente_dir item = (clsClasses.clsP_cliente_dir) lvObj;

                    adapter.setSelectedIndex(position);

                    browse=1;
                    gl.idclidir=item.codigo_direccion;selid=item.codigo_direccion;
                    startActivity(new Intent(CliDirList.this,CliDirDom.class));

                } catch (Exception e) {
                }
                return true;
            }
        });

    }

    //endregion

    //region Main

    private void listItems() {

        try {
            P_cliente_dirObj.fill("WHERE (CODIGO_CLIENTE="+cliid+") ORDER BY DIRECCION");

            adapter = new LA_P_cliente_dir(this, this, P_cliente_dirObj.items);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void loadDirs() {
        try {
            sql="SELECT CODIGO_DIRECCION, DIRECCION, REFERENCIA, TELEFONO " +
                "FROM P_CLIENTE_DIR WHERE (CODIGO_CLIENTE="+cliid+")";
            ws.execute(sql,rnAfterOpen);
        } catch (Exception e) {
            toast("No se pudo obtener lista de direcciones");
        }
    }

    private void procesaDirecciones() {
        Cursor dt=ws.openDTCursor;
        clsClasses.clsP_cliente_dir item;
        int rc,did;

        try {
            pbar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {}


        rc=dt.getCount();if (rc==0) return;
        //sql="SELECT CODIGO_DIRECCION, DIRECCION, REFERENCIA, TELEFONO " +
        try {
            dt.moveToFirst();
            while (!dt.isAfterLast()) {

                did=dt.getInt(0);
                P_cliente_dirObj.fill("WHERE (CODIGO_DIRECCION="+did+")");

                if (P_cliente_dirObj.count==0) {

                    item = clsCls.new clsP_cliente_dir();

                    //item.codigo_direccion=P_cliente_dirObj.newID("SELECT MAX(CODIGO_DIRECCION) FROM P_cliente_dir");

                    item.codigo_direccion=did;
                    item.codigo_cliente=cliid;
                    item.referencia=dt.getString(2);
                    item.codigo_departamento="2";
                    item.codigo_municipio="10";
                    item.direccion=dt.getString(1);
                    item.zona_entrega=0;
                    item.telefono=dt.getString(3);

                    P_cliente_dirObj.add(item);
                } else {
                    item=P_cliente_dirObj.first();

                    item.direccion=dt.getString(1);
                    item.referencia=dt.getString(2);
                    item.telefono=dt.getString(3);

                    P_cliente_dirObj.update(item);
                }
                dt.moveToNext();
            }

            listItems();
        } catch (Exception e) {
            toast("No se pudo procesar lista de direcciones");
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
            P_cliente_dirObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            listItems();return;
        }
    }

    //endregion

}