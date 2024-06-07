package com.dtsgt.mpos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_prodmenuopcObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.ladapt.LA_T_combo_cant;

import java.util.ArrayList;

public class ProdMenuCant extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lbl3;

    private clsT_comboObj T_comboObj;
    private clsP_productoObj P_productoObj;

    private LA_T_combo_cant adapter;

    private ArrayList<clsClasses.clsT_combo_cant> citems= new ArrayList<clsClasses.clsT_combo_cant>();
    private clsClasses.clsT_combo_cant citem,selitem;

    private int idcombo,cantlim,cantact,uitemid,idmenuopc;
    private boolean newitem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prod_menu_cant);

            super.InitBase();

            listView = (ListView) findViewById(R.id.listView);
            lbl1 = (TextView) findViewById(R.id.textView93);
            lbl2 = (TextView) findViewById(R.id.textView316);
            lbl3 = (TextView) findViewById(R.id.textView315);

            T_comboObj = new clsT_comboObj(this, Con, db);
            P_productoObj = new clsP_productoObj(this, Con, db);

            idcombo=gl.idcombo;

            uitemid = Integer.parseInt(gl.menuitemid);
            newitem = gl.newmenuitem;

            loadItem();
            if (newitem) newItem(); else listItems();
            setHandlers();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doApply(View view) {

    }

    public void doDelete(View view) {
        //msgAskDelete("Eliminar articulo");
    }

    public void doClose(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                selitem = (clsClasses.clsT_combo_cant)lvObj;

                adapter.setSelectedIndex(position);selidx=position;

                gl.set_cant_max=cantlim-cantact-selitem.cant;

                browse=1;
                startActivity(new Intent(ProdMenuCant.this,CantKeyb.class));
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            calcTotal();

            adapter=new LA_T_combo_cant(this,this,citems);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void newItem() {
        try {
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void loadItem() {

        try {
            lbl1.setText(""+gl.gstr);

            clsP_prodmenuopcObj P_prodmenuopcObj=new clsP_prodmenuopcObj(this,Con,db);
            P_prodmenuopcObj.fill("WHERE (codigo_menu="+idcombo+")");
            idmenuopc=P_prodmenuopcObj.first().codigo_menu_opcion;

            cantlim=(int) Math.abs(P_prodmenuopcObj.first().cant);
            lbl2.setText("Total opciones : "+cantlim);

            sql=" SELECT  P_PRODMENUOPC_DET.CODIGO_MENUOPC_DET, P_PRODMENUOPC_DET.CODIGO_PRODUCTO, P_PRODUCTO.DESCCORTA " +
                " FROM P_PRODMENUOPC_DET INNER JOIN P_PRODUCTO ON P_PRODMENUOPC_DET.CODIGO_PRODUCTO = P_PRODUCTO.CODIGO_PRODUCTO " +
                " WHERE (P_PRODMENUOPC_DET.CODIGO_MENU_OPCION = "+idmenuopc+") ORDER BY P_PRODUCTO.DESCCORTA";
            Cursor dt=Con.OpenDT(sql);

            citems.clear();
            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {
                    citem = clsCls.new clsT_combo_cant();

                    citem.codigo_menuopc_det=dt.getInt(0);
                    citem.codigo_producto=dt.getInt(1);
                    citem.nombre=dt.getString(2);
                    citem.cant=0;

                    citems.add(citem);
                    dt.moveToNext();
                }
            }

            if (dt!=null) dt.close();


        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateCant() {
        try {
            if (gl.set_cant==-1) return;

            citems.get(selidx).cant=gl.set_cant;
            adapter.notifyDataSetChanged();
            calcTotal();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void calcTotal() {
        cantact=0;
        for (int i = 0; i <citems.size(); i++) {
            cantact+=citems.get(i).cant;
        }
        lbl3.setText("Falta elegir : "+(cantlim-cantact));
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            T_comboObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);

            if (browse==1) {
                browse=0;
                updateCant();return;
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    //endregion

}