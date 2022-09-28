package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_modificadorObj;
import com.dtsgt.classes.clsT_orden_modObj;
import com.dtsgt.ladapt.LA_P_modiflist;
import java.util.ArrayList;

public class ModifProd extends PBase {

    private ListView listView;
    private TextView lblprod;

    private clsP_modificadorObj P_modificadorObj;

    private ArrayList<clsClasses.clsP_usopcion> items= new ArrayList<clsClasses.clsP_usopcion>();
    private LA_P_modiflist adapter;

    private clsT_orden_modObj T_orden_modObj;

    private boolean ischanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_modif_prod);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblprod   = findViewById(R.id.textView274);lblprod.setText(gl.gstr2);

            P_modificadorObj=new clsP_modificadorObj(this,Con,db);
            T_orden_modObj=new clsT_orden_modObj(this,Con,db);

            setHandlers();

            listItems();
            //gl.idmodgr  , gl.produid  ,  gl.ordcorel
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doApply(View view) {
        save();
    }

    public void doExit(View view) {
        if (ischanged)  msgAskExit("Salír sin guardar cambios"); else finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsP_usopcion item = (clsClasses.clsP_usopcion)lvObj;

                if (item.nombre.isEmpty()) item.nombre="x"; else item.nombre="";

                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();

                ischanged=true;
            };
        });

    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsP_usopcion item;

        try {
            T_orden_modObj.fill("WHERE (COREL='"+gl.ordcorel+"')");

            P_modificadorObj.fill("WHERE (CODIGO_GRUPO="+gl.idmodgr+") ORDER BY NOMBRE");

            items.clear();
            for (int i = 0; i <P_modificadorObj.count; i++) {
                item = clsCls.new clsP_usopcion();

                item.codigo=P_modificadorObj.items.get(i).codigo_modif;
                item.menugroup=P_modificadorObj.items.get(i).nombre;

                if (isChecked(item.codigo)) item.nombre="x";else item.nombre="";

                items.add(item);
            }

            adapter=new LA_P_modiflist(this,this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void save() {
        clsClasses.clsT_orden_mod item;

        try {
            db.beginTransaction();

            sql="DELETE FROM T_ORDEN_MOD WHERE (COREL='"+gl.ordcorel+"') AND (ID="+gl.produid+")";
            db.execSQL(sql);

            T_orden_modObj.fill();

            for (int i = 0; i <items.size(); i++) {
                if (items.get(i).nombre.equalsIgnoreCase("x")) {
                    item = clsCls.new clsT_orden_mod();

                    item.corel=gl.ordcorel;
                    item.id=gl.produid ;
                    item.idmod=items.get(i).codigo;
                    item.nombre=items.get(i).menugroup;

                    T_orden_modObj.add(item);
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            finish();
        } catch (Exception e) {
            db.endTransaction();msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux

    private boolean isChecked(int idmod) {
        for (int i = 0; i <T_orden_modObj.count; i++) {
            if (T_orden_modObj.items.get(i).idmod==idmod && T_orden_modObj.items.get(i).id==gl.produid) return true;
        }
        return false;
    }


    //endregion

    //region Dialogs

    private void msgAskExit(String msg) {

        //ExDialog dialog = new ExDialog(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Modificadores");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_modificadorObj.reconnect(Con,db);
            T_orden_modObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }


    //endregion

}