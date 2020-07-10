package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.ladapt.LA_D_pedido;

public class Pedidos extends PBase {

    private GridView gridView;
    private TextView lblNue,lblPen,lblComp;

    private LA_D_pedido adapter;
    private clsD_pedidoObj D_pedidoObj;

    private int cnue,cpen,ccomp;
    private String sql;
    private boolean modo=false;

    private String sql1="WHERE (ANULADO=0) AND (CODIGO_USUARIO_ENTREGO=0) ORDER BY FECHA_RECEPCION_SUC,FECHA_SISTEMA ";
    private String sql2="WHERE (ANULADO=0) AND (FECHA_SALIDA_SUC>0) ORDER BY FECHA_SALIDA_SUC DESC ";
    private String sql3="WHERE (ANULADO=1) ORDER BY FECHA_RECEPCION_SUC DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        super.InitBase();

        gridView =(GridView) findViewById(R.id.gridView1);
        lblNue = (TextView) findViewById(R.id.textView179);lblNue.setText("");
        lblPen = (TextView) findViewById(R.id.textView181);lblPen.setText("");
        lblComp = (TextView) findViewById(R.id.textView182);lblComp.setText("");

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);

        setHandlers();

        sql=sql1;modo=false;
        listItems();
    }

    //region Events

    public void doMenu(View view) {
        showItemMenu();
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                clsClasses.clsD_pedido item = (clsClasses.clsD_pedido)lvObj;

                adapter.setSelectedIndex(position);
                gl.pedid=item.corel;

                browse=1;
                startActivity(new Intent(Pedidos.this,PedidoDet.class));
            };
        });

    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
        try {

            cnue=0;cpen=0;ccomp=0;

            D_pedidoObj.fill(sql);

            for (int i = 0; i <D_pedidoObj.count; i++) {
                item=D_pedidoObj.items.get(i);
                if (item.codigo_usuario_proceso>0) {
                    ccomp++;
                } else {
                    if (item.codigo_usuario_creo>0) cpen++;else cnue++;
                }
            }

            lblNue.setText(""+cnue);lblPen.setText(""+cpen);lblComp.setText(""+ccomp);
            adapter=new LA_D_pedido(this,this,D_pedidoObj.items,modo);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    private void showItemMenu() {
        final AlertDialog Dialog;
        final String[] selitems = {"Pedidos activos","Pedidos Entregados","Pedidos Anulados"};

        ExDialog menudlg = new ExDialog(this);

        menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        sql=sql1;modo=false;break;
                    case 1:
                        sql=sql2;modo=true;break;
                    case 2:
                        sql=sql3;modo=true;break;
                }

                listItems();
                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();
    }


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
          super.onResume();

          if (browse==1) {
              browse=0;
              listItems();return;
          }
    }

    //endregion

}