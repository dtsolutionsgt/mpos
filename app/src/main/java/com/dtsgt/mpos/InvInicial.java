package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;

public class InvInicial extends PBase {

    private ListView listView;
    private GridView grdbtn;
    private TextView lblBar,lblKeyDP,lblProd,lblCant,lblCosto,lblTCant,lblTCosto;

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
        lblBar = (TextView) findViewById(R.id.lblCant);lblBar.setText("");
        lblKeyDP = (TextView) findViewById(R.id.textView110);
        lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
        lblCant = (TextView) findViewById(R.id.textView158);
        lblCosto = (TextView) findViewById(R.id.textView160);
        lblTCant = (TextView) findViewById(R.id.textView155);
        lblTCosto = (TextView) findViewById(R.id.textView150);

        prodid=0;

        khand=new clsKeybHandler(this, lblBar,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);

        setHandlers();

        menuItems();

        listItems();
    }

    //region Events

    public void doSave(View view) {
        if (T_movdObj.count==0) {
            msgbox("No se puede guardar un inventario vacio");
        } else {
            msgAskSave("Completar y guardar inventario inicial");
        }
    }

    public void doFocusBar(View view) {
        khand.setLabel(lblBar,false);
    }

    public void doFocusCant(View view) {
        khand.setLabel(lblCant,false);
    }

    public void doFocusCosto(View view) {
        khand.setLabel(lblCosto,true);
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.label==lblBar) {
                barcode=khand.getStringValue();
                if (!barcode.isEmpty()) addBarcode();
            } else if (khand.label==lblCant) {
                khand.setLabel(lblCosto,true);
            } else if (khand.label==lblCosto) {
                addItem();
            }
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

                    setProduct();
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

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

    //region Main

    private void listItems() {
        double tc,can;

        selidx=-1;
        lblProd.setText("");lblBar.setText("");lblCant.setText("");lblCosto.setText("");
        khand.setLabel(lblBar,false);

        costot=0;cantt=0;

        try {
            T_movdObj.fill();

            for (int i = 0; i <T_movdObj.count; i++) {
                can=T_movdObj.items.get(i).cant;cantt+=can;
                tc=T_movdObj.items.get(i).precio; costot+=tc;
                T_movdObj.items.get(i).pesom=tc;
            }

            adapter=new LA_T_movd(this,this,T_movdObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblTCant.setText("Articulos : "+mu.frmint(cantt));
        lblTCosto.setText("Costo : "+mu.frmcur(costot));
    }

    private void addItem() {
        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        int cant;
        double costo,dd;
        String ss;

        if (prodid==0) {
            toast("Falta definir articulo");
            khand.setLabel(lblBar,false);return;
        }

        try {
            ss=lblCant.getText().toString();
            dd=Double.parseDouble(ss);
            cant=(int) dd;
            if (cant<=0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");khand.setLabel(lblCant,false);return;
        }

        try {
            costo=Double.parseDouble(lblCosto.getText().toString());
        } catch (Exception e) {
            costo=0;
        }

        if (costo<0) {
            toast("Costo incorrecto");khand.setLabel(lblCosto,true);return;
        }

        try {

            if (selidx==-1) {

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

            } else {
                selitem.cant=cant;
                selitem.precio=costo;

                T_movdObj.update(selitem);
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,false);
            lblProd.setText("");lblBar.setText("");lblCant.setText("");lblCosto.setText("");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void save() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        int provid;
        String corel=gl.ruta+"_"+mu.getCorelBase();

        try {

            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);
            clsD_MovObj mov=new clsD_MovObj(this,Con,db);

            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            provid=suc.first().codigo_proveedor;

            db.beginTransaction();

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA="Inventario inicial";
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR=provid;

            mov.add(header);

            for (int i = 0; i <T_movdObj.count; i++) {

                imov=T_movdObj.items.get(i);
                item =clsCls.new clsD_MovD();

                item.CORELDET=i+1;
                item.COREL=corel;
                item.PRODUCTO=imov.producto;
                item.CANT=imov.cant;
                item.CANTM=0;
                item.PESO=0;
                item.PESOM=0;
                item.LOTE="";
                item.CODIGOLIQUIDACION=0;
                item.UNIDADMEDIDA=imov.unidadmedida;
                item.PRECIO=imov.precio;

                movd.add(item);
            }

            db.execSQL("DELETE FROM T_MOVD");

            db.setTransactionSuccessful();
            db.endTransaction();


            toastlong("Inventario guardado");
            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addBarcode() {
        if (barraProducto()) {
            return;
        } else {
            lblProd.setText("");lblBar.setText("");
            lblCant.setText("");lblCosto.setText("");
            khand.setLabel(lblBar,false);

            toastlong("¡El producto "+barcode+" no existe!");
        }
    }

    private boolean barraProducto() {
          try {
            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            sql ="WHERE (CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"') " +
                 "OR (CODIGO_PRODUCTO="+barcode+")  COLLATE NOCASE";
            P_productoObj.fill(sql);
            if (P_productoObj.count==0) return false;


            prodid=P_productoObj.first().codigo_producto;
            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);
            khand.setLabel(lblCant,false);
            lblCant.setText("");lblCosto.setText("");

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void setProduct() {
        try {

            lblProd.setText("");lblBar.setText("");lblCant.setText("");lblCosto.setText("");

            prodid=selitem.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);

            lblCant.setText(mu.frmint(selitem.cant));
            khand.setLabel(lblCant,false);

            if (selitem.precio>0) lblCosto.setText(""+selitem.precio);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
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

    private void msgAskSave(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Inventario inicial");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    save();
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