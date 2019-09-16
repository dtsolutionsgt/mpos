package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodopclistObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.ladapt.ListAdaptOpcion;

import java.util.ArrayList;

public class ProdMenu extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lbl3;

    private ListAdaptOpcion adapter;

    private ArrayList<clsClasses.clsOpcion> items= new ArrayList<clsClasses.clsOpcion>();
    private ArrayList<clsClasses.clsOpcion> ops= new ArrayList<clsClasses.clsOpcion>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();

    private int cant,lcant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_menu);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView);
        lbl1 = (TextView) findViewById(R.id.textView93);
        lbl2 = (TextView) findViewById(R.id.textView117);
        lbl3 = (TextView) findViewById(R.id.textView116);

        setHandlers();

        cant=gl.retcant;lcant=gl.limcant;

        lbl1.setText(gl.gstr);
        lbl2.setText(""+cant);

        listItems();
    }

    //region Events

    public void doClose(View view) {
        gl.retcant=-1;
        finish();
    }

    public void doApply(View view) {
        gl.retcant=cant;
        finish();
    }

    public void doDec(View view) {
        if (cant>0) cant--;
        lbl2.setText(""+cant);
    }

    public void doInc(View view) {
        cant++;
        lbl2.setText(""+cant);
     }

    public void doDelete(View view) {
        msgAskDelete("Eliminar registro de la venta");
    }

    private void setHandlers(){
        try{

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsOpcion item = (clsClasses.clsOpcion)lvObj;

                        adapter.setSelectedIndex(position);
                        selidx=position;

                        listOptions(item.Descrip,item.listid);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox( e.getMessage());
                    }
                };
            });
        } catch (Exception e) {
        }
    }

    //endregion

    //region Main

    private void listItems() {
        clsP_prodmenuObj P_menuObj=new clsP_prodmenuObj(this,Con,db);
        clsClasses.clsOpcion item;

        try {
            items.clear();
            P_menuObj.fill("WHERE Codigo='"+gl.prodmenu+"'");

            for (int i = 0; i <P_menuObj.count; i++) {
                item=clsCls.new clsOpcion();

                item.ID=P_menuObj.items.get(i).item;
                item.Cod=P_menuObj.items.get(i).codigo;
                item.Name=P_menuObj.items.get(i).nombre;
                item.Descrip=item.Name;
                item.listid=P_menuObj.items.get(i).idopcion;
                item.bandera=0;

                items.add(item);
            }

            adapter=new ListAdaptOpcion(this,items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void listOptions(String title,int idoption) {
        clsP_productoObj prod=new clsP_productoObj(this,Con,db);
        clsP_prodopclistObj P_listObj=new clsP_prodopclistObj(this,Con,db);
        clsClasses.clsOpcion item;
        final AlertDialog Dialog;
        String cod,name;

        try {
            ops.clear();lcode.clear();lname.clear();
            P_listObj.fill("WHERE ID="+idoption);

            for (int i = 0; i <P_listObj.count; i++) {
                cod=P_listObj.items.get(i).producto;
                prod.fill("WHERE CODIGO='"+cod+"'");
                name=prod.first().desclarga;

                lcode.add(cod);
                lname.add(name);
            }


            final String[] selitems = new String[lname.size()];
            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            AlertDialog.Builder mMenuDlg = new AlertDialog.Builder(this);
            mMenuDlg.setTitle(title);

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        items.get(selidx).Cod=lcode.get(item);
                        items.get(selidx).Name=lname.get(item);
                        items.get(selidx).bandera=1;
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

            Dialog = mMenuDlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs

    private void msgAskDelete(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Borrar");
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.retcant=0;
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


    //endregion


}
