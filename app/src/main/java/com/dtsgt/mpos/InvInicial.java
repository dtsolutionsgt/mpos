package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;

public class InvInicial extends PBase {

    private ListView listView;
    private GridView grdbtn;
    private TextView lblCant,lblKeyDP,lblProd;
    private EditText txtCant,txtCosto;

    private clsKeybHandler khand;
    private LA_T_movd adapter;
    private ListAdaptMenuVenta adapterb;

    private clsT_movdObj T_movdObj;
    private clsP_productoObj P_productoObj;

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsT_movd selitem;

    private String barcode,prodname,um;
    private int prodid,selidx;
    private double cantt,costot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_inicial);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        grdbtn = (GridView) findViewById(R.id.grdbtn);
        lblCant = (TextView) findViewById(R.id.lblCant);lblCant.setText("");
        lblKeyDP =(TextView) findViewById(R.id.textView110);
        lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
        txtCant =(EditText) findViewById(R.id.editTextNumber);
        txtCosto =(EditText) findViewById(R.id.editTextNumberDecimal);

        prodid=0;

        khand=new clsKeybHandler(this,lblCant,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);

        setHandlers();

        menuItems();

        listItems();
    }

    //region Events

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            barcode=khand.getStringValue();
            if (!barcode.isEmpty()) addBarcode();
        }
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    selitem = (clsClasses.clsT_movd)lvObj;

                    adapter.setSelectedIndex(position);
                    selidx=position;

                };
            });

            grdbtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = grdbtn.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu)lvObj;

                    adapterb.setSelectedIndex(position);
                    processMenuBtn(item.ID);

                };
            });

            txtCant.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                    if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (arg1) {
                            case KeyEvent.KEYCODE_ENTER:
                                if (!txtCant.getText().toString().isEmpty())  txtCosto.requestFocus();
                                return true;
                        }
                    }
                    return false;
                }
            });

            txtCosto.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                    if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (arg1) {
                            case KeyEvent.KEYCODE_ENTER:
                                addItem();
                                return true;
                        }
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

    //region Main

    private void listItems() {
        double tc,can;

        selidx=-1;
        costot=0;cantt=0;

        try {
            T_movdObj.fill();

            for (int i = 0; i <T_movdObj.count; i++) {
                can=T_movdObj.items.get(i).cant;cantt+=can;
                tc=can*T_movdObj.items.get(i).precio; costot+=tc;
                T_movdObj.items.get(i).pesom=tc;
            }

            adapter=new LA_T_movd(this,this,T_movdObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void addItem() {
        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        int cant;
        double costo;

        if (prodid==0) {
            toast("Falta definir articulo");
            khand.clear(true);khand.enable();khand.focus();return;
        }

        try {
            cant=Integer.parseInt(txtCant.getText().toString());
            if (cant<=0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");txtCant.requestFocus();return;
        }

        try {
            costo=Integer.parseInt(txtCosto.getText().toString());
        } catch (Exception e) {
            costo=0;
        }

        if (costo<0) {
            toast("Costo incorrecto");txtCosto.requestFocus();return;
        }

        try {

            item.coreldet=T_movdObj.newID("SELECT MAX(coreldet) FROM T_MOVD");
            item.corel=" ";
            item.producto=prodid;
            item.cant=cant;
            item.cantm=0;
            item.peso=0;
            item.pesom=0;
            item.lote=prodname;
            item.codigoliquidacion=0;
            item.unidadmedida=um;
            item.precio=costo;

            T_movdObj.add(item);

            listItems();

            prodid=0;txtCant.setText("");txtCosto.setText("");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void addBarcode() {
        if (barraProducto()) {
            return;
        } else {
            toastlong("¡El producto "+barcode+" no existe!");
        }
    }

    private boolean barraProducto() {
          try {
            khand.clear(true);khand.enable();khand.focus();

            sql ="WHERE (CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"') " +
                 "OR (CODIGO_PRODUCTO="+barcode+")  COLLATE NOCASE";
            P_productoObj.fill(sql);
            if (P_productoObj.count==0) return false;

            selidx=-1;

            prodid=P_productoObj.first().codigo_producto;
            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);
            txtCant.requestFocus();

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    //endregion

    //region Menu

    private void menuItems() {
        clsClasses.clsMenu item;

        try {
            mmitems.clear();

            try {

                item = clsCls.new clsMenu();
                item.ID=50;item.Name="Buscar ";item.Icon=50;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=54;item.Name="Borrar linea ";item.Icon=54;
                mmitems.add(item);

                item = clsCls.new clsMenu();
                item.ID=55;item.Name="Borrar todo ";item.Icon=55;
                mmitems.add(item);

            } catch (Exception e) {
                addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
            }

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuBtn(int menuid) {
        try {

            switch (menuid) {
                case 50:
                    gl.gstr = "";browse = 1;gl.gstr="";
                    startActivity(new Intent(this, Producto.class));break;
                case 54:
                    borraLinea();break;
                case 55:
                    borraTodo();break;
             }
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    private void borraLinea() {

        if (selidx==-1) return;

        try {
            T_movdObj.delete(selitem);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar todo el inventario");
    }


    //endregion

    //region Aux


    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inventario inicial");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sql="DELETE FROM T_MOVD";
                    db.execSQL(sql);
                    listItems();
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
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
    protected void onResume() {
        super.onResume();

        try {
            T_movdObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (gl.gstr.isEmpty()) return;
            barcode=gl.gstr;
            addBarcode();
        }
    }

    //endregion

}