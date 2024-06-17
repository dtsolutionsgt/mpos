package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.ladapt.LA_T_combo_cant;

import java.util.ArrayList;

public class ProdMenuCant extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lbl3;

    private clsT_comboObj T_comboObj;
    private clsP_productoObj P_productoObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;
    private Precio prc;

    private LA_T_combo_cant adapter;

    private ArrayList<clsClasses.clsT_combo_cant> citems= new ArrayList<clsClasses.clsT_combo_cant>();
    private clsClasses.clsT_combo_cant citem,selitem;

    private int idcombo,cantlim,cantact,uitemid,idmenuopc,cantfalt;
    private boolean newitem;
    private double prec,cant;

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
            T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);

            prc = new Precio(this, mu, 2,gl.peDescMax);

            idcombo=gl.idcombo;

            uitemid = Integer.parseInt(gl.menuitemid);
            newitem = gl.newmenuitem;
            prec=gl.preccombo;
            cant=1;

            listOptions();
            if (newitem) newItem(); else loadItems();
            setHandlers();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doApply(View view) {
        String ss;
        if (cantfalt!=0) {
            if (cantfalt==1) ss="1 articulo";else ss=cantfalt+" artículos.";
            msgbox("Falta elegir "+ss);return;
        } else {
            msgAskSave("Guardar");
        }
    }

    public void doDelete(View view) {
        //msgAskDelete("Eliminar articulo");
    }

    public void doClose(View view) {
        if (cantfalt==cantlim) finish();else msgAskExit("Salir sin aplicar");
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                selitem = (clsClasses.clsT_combo_cant)lvObj;

                adapter.setSelectedIndex(position);selidx=position;

                gl.set_cant_max=cantlim-cantact+selitem.cant;

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
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void newItem() {
        try {
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void loadItems() {
        int pid;

        try {

            for (int i = 0; i <citems.size(); i++) {
                pid=citems.get(i).codigo_producto;

                T_comboObj.fill("WHERE (IdCombo="+ uitemid+") AND (idseleccion="+pid+")");
                if (T_comboObj.count>0) citems.get(i).cant=(int) T_comboObj.first().cant;
            }

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listOptions() {
        try {

            lbl1.setText(""+gl.gstr);

            clsP_prodmenuopcObj P_prodmenuopcObj=new clsP_prodmenuopcObj(this,Con,db);
            P_prodmenuopcObj.fill("WHERE (codigo_menu="+idcombo+")");
            idmenuopc=P_prodmenuopcObj.first().codigo_menu_opcion;

            cantlim=(int) Math.abs(P_prodmenuopcObj.first().cant);
            lbl2.setText("Total opciones : "+cantlim);

            sql=" SELECT  P_PRODMENUOPC_DET.CODIGO_MENUOPC_DET, P_PRODMENUOPC_DET.CODIGO_PRODUCTO, P_PRODUCTO.DESCCORTA, P_PRODUCTO.UNIDBAS " +
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
                    citem.um=dt.getString(3);
                    citem.cant=0;

                    citems.add(citem);
                    dt.moveToNext();
                }
            }

            if (dt!=null) dt.close();

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
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

    private void saveItem() {
        clsClasses.clsT_combo item;

        try {

            String um=gl.um;

            double prec = prc.precio(gl.prodid, cant, gl.nivel, um, gl.umpeso, gl.dpeso,um,gl.prodmenu);

            double impval = prc.impval;
            double desc=prc.desc;
            double descmon = prc.descmon;
            double tot = prc.tot;

            tot=prec*cant;

            db.beginTransaction();

            if (!newitem){
                db.execSQL("DELETE FROM T_COMBO WHERE IdCombo="+uitemid);
                db.execSQL("DELETE FROM T_VENTA WHERE (PRODUCTO='"+gl.prodid+"') AND (EMPRESA='"+uitemid+"')");
            } else {
                guardaPrecios();
            }

            db.execSQL("UPDATE T_ordencomboprecio SET PRECTOTAL="+prec+" WHERE (COREL='VENTA') AND (IdCombo="+uitemid+")");

            for (int i = 0; i <citems.size(); i++) {

                if (citems.get(i).cant>0) {

                    item = clsCls.new clsT_combo();

                    item.codigo_menu = i;
                    item.idcombo = uitemid;
                    item.cant = citems.get(i).cant;
                    item.unid = 1;
                    item.idseleccion = citems.get(i).codigo_producto;
                    item.orden = i;

                    T_comboObj.add(item);
                }
            }

            tot=cant*prec;tot=mu.round2(tot);

            ins.init("T_VENTA");
            ins.add("PRODUCTO",gl.prodid);
            ins.add("EMPRESA",""+uitemid);
            ins.add("UM","UNI");
            ins.add("CANT",cant);
            ins.add("UMSTOCK","UNI");
            ins.add("FACTOR",1);
            ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",tot);
            ins.add("PRECIODOC",prec);
            ins.add("PESO",0);
            ins.add("VAL1",0);
            ins.add("VAL2",1);
            ins.add("VAL3",0);
            ins.add("VAL4",""+uitemid);
            ins.add("PERCEP",0);

            db.execSQL(ins.sql());

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.retcant=1;
            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox2(e.getMessage());
        }
    }

    private void guardaPrecios() {
        clsClasses.clsT_ordencomboprecio pitem = clsCls.new clsT_ordencomboprecio();

        pitem.corel="VENTA";
        pitem.idcombo=uitemid;
        pitem.precorig=prec;
        pitem.precitems=prec;
        pitem.precdif=0;
        pitem.prectotal=prec;

        T_ordencomboprecioObj.add(pitem);
    }

    //endregion

    //region Aux

    private void calcTotal() {
        cantact=0;
        for (int i = 0; i <citems.size(); i++) {
            cantact+=citems.get(i).cant;
        }
        cantfalt=cantlim-cantact;
        lbl3.setText("Falta elegir : "+cantfalt);
    }

    //endregion

    //region Dialogs

    private void msgAskSave(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("MPos");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();
    }

    private void msgAskExit(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("MPos");

            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.retcant=-1;
                    finish();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            T_comboObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);

            if (browse==1) {
                browse=0;
                updateCant();return;
            }

        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir sin aplicar");
    }


    //endregion

}