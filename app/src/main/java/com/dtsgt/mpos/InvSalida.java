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
import com.dtsgt.classes.clsP_motivoajusteObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.LA_T_movr;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;

public class InvSalida extends PBase {

    private ListView listView;
    private GridView grdbtn;
    private TextView lblBar,lblKeyDP,lblProd,lblCant, lblRazon,lblCosto,lblTCant,lblTCosto,lblTit;

    private clsKeybHandler khand;
    private LA_T_movr adapterr;
    private ListAdaptMenuVenta adapterb;

    private clsT_movdObj T_movdObj;
    private clsT_movrObj T_movrObj;
    private clsP_productoObj P_productoObj;
    private clsP_motivoajusteObj P_motivoajusteObj;
    private clsP_stockObj P_stockObj;

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsT_movr selitemr;

    private String barcode,prodname,um,invtext;
    private int prodid,selidx, motivo,exist,selcant;
    private double cantt,costot;
    private boolean ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_salida);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        grdbtn = (GridView) findViewById(R.id.grdbtn);
        lblTit = (TextView) findViewById(R.id.lblTit3);
        lblBar = (TextView) findViewById(R.id.lblCant);lblBar.setText("");
        lblKeyDP = (TextView) findViewById(R.id.textView110);
        lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
        lblCant = (TextView) findViewById(R.id.textView158);
        lblRazon = (TextView) findViewById(R.id.textView161);
        lblCosto = (TextView) findViewById(R.id.textView163);
        lblTCant = (TextView) findViewById(R.id.textView155);
        lblTCosto = (TextView) findViewById(R.id.textView150);

        prodid=0;
        ingreso=gl.invregular;
        if (ingreso) invtext="Ingreso de mercancía";else invtext="Inventario inicial";
        lblTit.setText(invtext);

        khand=new clsKeybHandler(this, lblBar,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        T_movrObj=new clsT_movrObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);
        P_motivoajusteObj=new clsP_motivoajusteObj(this,Con,db);
        P_stockObj=new clsP_stockObj(this,Con,db);

        setHandlers();

        iniciaProceso();

        listItems();
    }


    //region Events

    public void doSave(View view) {
        if (T_movrObj.count==0) {
            msgbox("No se puede guardar un inventario vacio");return;
        }
        msgAskSave("Aplicar ajuste de inventario");
    }

    public void doFocusBar(View view) {
        khand.setLabel(lblBar,false);
        lblProd.setText("");lblBar.setText("");lblCant.setText("");
        lblRazon.setText("");lblCosto.setText("");
        motivo=-1;
    }

    public void doFocusCant(View view) {
        khand.setLabel(lblCant,false);
    }

    public void doFocusCosto(View view) {
        if (gl.invregular) khand.setLabel(lblRazon,true);
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.label==lblBar) {
                barcode=khand.getStringValue();
                if (!barcode.isEmpty()) addBarcode();
            } else if (khand.label==lblCant) {
                khand.setLabel(lblRazon,true);
                if (validaDisp()) listaMotivos();
            } else if (khand.label== lblRazon) {
                if (validaDisp()) listaMotivos();
            }
        }
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    selitemr = (clsClasses.clsT_movr)lvObj;

                    adapterr.setSelectedIndex(position);
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
        lblProd.setText("");lblBar.setText("");lblCant.setText("");
        lblRazon.setText("");lblCosto.setText("");motivo=-1;
        khand.setLabel(lblBar,false);

        costot=0;cantt=0;

        try {
            T_movrObj.fill();

            for (int i = 0; i <T_movrObj.count; i++) {
                can=T_movrObj.items.get(i).cant;cantt+=can;
                tc=can*T_movrObj.items.get(i).precio; costot+=tc;
                T_movrObj.items.get(i).pesom=tc;
            }

            adapterr=new LA_T_movr(this,this,T_movrObj.items);
            listView.setAdapter(adapterr);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblTCant.setText("Articulos : "+mu.frmint(cantt));
        lblTCosto.setText("Costo : "+mu.frmcur(costot));
    }

    private void addItem() {
        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
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

        if (cant>exist) {
            toast("Insuficiente existencia ("+exist+")");khand.setLabel(lblCant,false);return;
        }

        try {
            costo=Double.parseDouble(lblCosto.getText().toString());
            if (costo<0) costo=0;
        } catch (Exception e) {
            costo=0;
        }

        if (motivo<0) {
            toast("Falta definir un motivo de ajuste");listaMotivos();return;
        }

        try {

            if (selidx==-1) {

                itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                itemr.corel=" ";
                itemr.producto=prodid;
                itemr.cant=cant;
                itemr.cantm=0;
                itemr.peso=0;
                itemr.pesom=0;
                itemr.lote=prodname;
                itemr.codigoliquidacion=0;
                itemr.unidadmedida=um;
                itemr.precio=costo;
                itemr.razon=motivo;

                T_movrObj.add(itemr);

            } else {
                selitemr.cant=cant;
                selitemr.razon=motivo;

                T_movrObj.update(selitemr);
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,false);
            lblProd.setText("");lblBar.setText("");lblCant.setText("");
            lblRazon.setText("");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void save() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;
        String corel=gl.ruta+"_"+mu.getCorelBase();

        try {

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            db.beginTransaction();

            if (!ingreso) db.execSQL("DELETE FROM P_STOCK");

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="D";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA=" ";
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            for (int i = 0; i <T_movrObj.count; i++) {

                imovr=T_movrObj.items.get(i);

                item =clsCls.new clsD_MovD();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=imovr.producto;
                item.cant=imovr.cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=imovr.unidadmedida;
                item.precio=imovr.precio;
                item.motivo_ajuste=imovr.razon;

                movd.add(item);

                adjustStock(imovr.producto,imovr.cant,imovr.unidadmedida);
            }

            db.execSQL("DELETE FROM T_MOVR");
            db.execSQL("DELETE FROM P_STOCK WHERE CANT=0");

            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Existencias actualizadas");
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
            lblCant.setText("");
            lblRazon.setText("");
            khand.setLabel(lblBar,false);
        }
    }

    private boolean barraProducto() {
        int tdisp=0;

        try {
            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            sql ="WHERE (CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"') " +
                    "OR (CODIGO_PRODUCTO="+barcode+")  COLLATE NOCASE";
            P_productoObj.fill(sql);
            if (P_productoObj.count==0) {
                toast("¡El producto "+barcode+" no existe!");return false;
            }

            prodid=P_productoObj.first().codigo_producto;
            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            P_stockObj.fill("WHERE codigo="+prodid);
            if (P_stockObj.count==0) {
                toast("¡El producto "+barcode+" no tiene existencia!");return false;
            }
            exist=(int) P_stockObj.first().cant;

            tdisp=0;
            T_movrObj.fill("WHERE PRODUCTO="+prodid);
            if (T_movrObj.count>0) {
                for (int i = 0; i <T_movrObj.count; i++) {
                    tdisp+=(int) T_movrObj.items.get(i).cant;
                }
            }

            if (selidx>=0) exist=exist+selcant;

            exist=exist-tdisp;
            if (exist<=0) {
                toast("¡El producto "+barcode+" no tiene existencia!");return false;
            }

            lblProd.setText(prodname);
            khand.setLabel(lblCant,false);khand.val="";
            lblCant.setText("");

            if (P_productoObj.first().costo>0) {
                lblCosto.setText(""+P_productoObj.first().costo);
            } else {
                lblCosto.setText("0");
            }

            motivo=-1;

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void setProduct() {
        try {

            lblProd.setText("");lblBar.setText("");lblCant.setText("");
            lblRazon.setText("");lblCosto.setText("");

            prodid=selitemr.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            selcant=(int) selitemr.cant;
            motivo=selitemr.razon;

            lblProd.setText(prodname);

            lblCant.setText(mu.frmint(selitemr.cant));
            khand.setLabel(lblCant,false);
            lblCosto.setText(""+selitemr.precio);
            lblRazon.setText(nombreMotivo(motivo));

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void adjustStock(int pcod,double pcant,String um) {
        sql="UPDATE P_STOCK SET CANT=CANT-"+pcant+" WHERE CODIGO="+pcod;
        db.execSQL(sql);
    }

    //endregion

    //region Menu

    private void iniciaProceso() {
        clsClasses.clsMenu item;

        try {

            db.execSQL("DELETE FROM T_movr");

            mmitems.clear();

            item = clsCls.new clsMenu();
            item.ID=50;item.Name="Buscar ";item.Icon=50;
            mmitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=54;item.Name="Borrar linea ";item.Icon=54;
            mmitems.add(item);

            item = clsCls.new clsMenu();
            item.ID=55;item.Name="Borrar todo ";item.Icon=55;
            mmitems.add(item);

            adapterb=new ListAdaptMenuVenta(this, mmitems);
            grdbtn.setAdapter(adapterb);

            motivo=-1;
            P_motivoajusteObj.fill("WHERE ACTIVO=1 ORDER BY Nombre");

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
            T_movrObj.delete(selitemr);
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraTodo() {
        msgAskTodo("Borrar todos los articulos");
    }

    //endregion

    //region Aux

    private String nombreMotivo(int mot) {
        for (int i = 0; i <P_motivoajusteObj.count; i++) {
            if (P_motivoajusteObj.items.get(i).codigo_motivo_ajuste==motivo) {
                return P_motivoajusteObj.items.get(i).nombre;
            }
        }
        return "";
    }

    private boolean validaDisp() {
        int cant;

        try {
            ss=lblCant.getText().toString();
            double dd=Double.parseDouble(ss);
            cant=(int) dd;
            if (cant<=0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");khand.setLabel(lblCant,false);
            return false;
        }

        if (cant>exist) {
            toast("Insuficiente existencia ("+exist+")");khand.setLabel(lblCant,false);
            return false;
        }


        return true;
    }

    //endregion

    //region Dialogs

    private void listaMotivos() {
        final AlertDialog Dialog;
        int sidx=-1;

        final String[] selitems = new String[P_motivoajusteObj.count];
        for (int i = 0; i <P_motivoajusteObj.count; i++) {
            selitems[i]= P_motivoajusteObj.items.get(i).nombre;
            if (P_motivoajusteObj.items.get(i).codigo_motivo_ajuste== motivo) sidx=i;
        }

        AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
        menudlg.setTitle("Razón de ajuste");

        menudlg.setSingleChoiceItems(selitems,sidx,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                int ii=item;

                motivo=P_motivoajusteObj.items.get(item).codigo_motivo_ajuste;
                lblRazon.setText(nombreMotivo(motivo));

                addItem();
                dialog.cancel();
            }
        });

        menudlg.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                motivo=-1;
                dialog.cancel();
            }
        });

        Dialog = menudlg.create();
        Dialog.show();
    }

    private void msgAskTodo(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(invtext);
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

        dialog.setTitle(invtext);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (ingreso) save();else msgAskSave2("Continuar");
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

    private void msgAskSave2(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(invtext);
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

    private void msgAskExit(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Title");
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
    protected void onResume() {
        super.onResume();

        try {
            T_movdObj.reconnect(Con,db);
            T_movrObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            P_motivoajusteObj.reconnect(Con,db);
            P_stockObj.reconnect(Con,db);
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

    @Override
    public void onBackPressed()    {
        msgAskExit("Salir sin aplicar ajuste");
    }

    //endregion

}