package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_unidadObj;
import com.dtsgt.classes.clsP_unidad_convObj;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.ladapt.LA_T_movd;
import com.dtsgt.ladapt.LA_T_movr;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;

public class InvInicial extends PBase {

    private ListView listView;
    private GridView grdbtn;
    private TextView lblBar,lblKeyDP,lblProd,lblCant,lblCosto,lblTCant;
    private TextView lblTCosto,lblTit,lblDocumento,lblUni;

    private clsKeybHandler khand;
    private LA_T_movd adapter;
    private LA_T_movr adapterr;
    private ListAdaptMenuVenta adapterb;

    private clsT_movdObj T_movdObj;
    private clsT_movrObj T_movrObj;
    private clsP_productoObj P_productoObj;
    private clsP_unidadObj P_unidadObj;
    private clsP_unidad_convObj P_unidad_convObj;

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsT_movd selitem;
    private clsClasses.clsT_movr selitemr;

    private String barcode,prodname,um,invtext,ubas,convum;
    private int prodid,selidx;
    private double cantt,costot,convcant;
    private boolean ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_inv_inicial);
        } else {
            setContentView(R.layout.activity_inv_inicial_ver);
        }

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        grdbtn = (GridView) findViewById(R.id.grdbtn);
        lblTit = (TextView) findViewById(R.id.lblTit3);
        lblDocumento = (TextView) findViewById(R.id.lblDocumento);
        lblBar = (TextView) findViewById(R.id.lblCant);lblBar.setText("");
        lblKeyDP = (TextView) findViewById(R.id.textView110);
        lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
        lblCant = (TextView) findViewById(R.id.textView158);
        lblCosto = (TextView) findViewById(R.id.textView160);
        lblTCant = (TextView) findViewById(R.id.textView155);
        lblTCosto = (TextView) findViewById(R.id.textView150);
        lblUni = (TextView) findViewById(R.id.textView229);

        prodid=0;
        ingreso=gl.invregular;
        if (ingreso) {
            invtext="Ingreso de mercancía "+ gl.codigo_proveedor + "-" + gl.nombre_proveedor;
        } else {
            invtext="Inventario inicial";
        }

        lblTit.setText(invtext);

        khand=new clsKeybHandler(this, lblBar,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        T_movrObj=new clsT_movrObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);
        P_unidadObj=new clsP_unidadObj(this,Con,db);
        P_unidad_convObj=new clsP_unidad_convObj(this,Con,db);

        setHandlers();

        iniciaProceso();

        if (!gl.corelmov.isEmpty()) cargaDatos();

        listItems();

        lblDocumento.requestFocus();
    }

    //region Events

    public void doAdd(View view) {
        addItem();
    }

    public void doSave(View view) {

        if (ingreso) {
            if (lblDocumento.getText().toString().isEmpty()){
                msgbox("Ingrese el número de documento");
                lblDocumento.requestFocus();
                return;
            }

            if (T_movrObj.count==0) {
                msgbox("No se ha agregado ningúno producto al inventario, no se puede completar");return;
            }
        } else {
            if (T_movdObj.count==0) {
                msgbox("No se puede guardar un inventario vacío");return;
            }
        }

        if (ingreso) {
            msgAskSave("Aplicar ingreso de mercancía");
        } else {
            msgAskSave("Borrar inventario actual y aplicar inventario inicial");
        }
    }

    public void doFocusBar(View view) {
        khand.setLabel(lblBar,true);
    }

    public void doFocusCant(View view) {
        khand.setLabel(lblCant,true);
    }

    public void doFocusCosto(View view) {
        if (gl.invregular) khand.setLabel(lblCosto,true);
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.label==lblBar) {
                barcode=khand.getStringValue();
                if (!barcode.isEmpty()) addBarcode();
            } else if (khand.label==lblCant) {
                if (gl.invregular) {
                    if (lblCosto.getText().toString().equals("0")){
                        khand.val="0";
                        lblCosto.setText("");
                    }
                    khand.setLabel(lblCosto,true);
                } else {
                    addItem();
                }
            } else if (khand.label==lblCosto) {
                if (gl.invregular) addItem();
            }
        }
    }

    public void doListUnits(View view) {
       listUnits();
    }

    public void doInterrupt(View view) {
        msgAskInterrupt("Interrupir el ingreso de mercancia");
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    Object lvObj = listView.getItemAtPosition(position);

                    selidx=position;

                    if (ingreso) {
                        selitemr = (clsClasses.clsT_movr)lvObj;
                        adapterr.setSelectedIndex(position);
                        setProductr();
                    } else {
                        selitem = (clsClasses.clsT_movd)lvObj;
                        adapter.setSelectedIndex(position);
                        setProduct();
                    }
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

            lblCosto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {

                        if (lblCosto.getText().toString().equals("0")){
                            lblCosto.setText("");
                        }

                    }
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
        int cl=0;

        selidx=-1;
        lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
        lblBar.setText("");lblCant.setText("");lblCosto.setText("");
        khand.setLabel(lblBar,true);

        costot=0;cantt=0;

        if (ingreso) {

            try {
                T_movrObj.fill();cl=T_movrObj.count;

                for (int i = 0; i <T_movrObj.count; i++) {
                    can=T_movrObj.items.get(i).cant;cantt+=can;
                    tc=can*T_movrObj.items.get(i).precio; costot+=tc;
                    T_movrObj.items.get(i).pesom=tc;
                    T_movrObj.items.get(i).srazon=" ";
                }

                adapterr=new LA_T_movr(this,this,T_movrObj.items);
                listView.setAdapter(adapterr);
            } catch (Exception e) {
                msgbox(e.getMessage());
            }

        } else {

            try {
                T_movdObj.fill();cl=T_movdObj.count;

                for (int i = 0; i <T_movdObj.count; i++) {
                    can=T_movdObj.items.get(i).cant;cantt+=can;
                    tc=can*T_movdObj.items.get(i).precio; costot+=tc;
                    T_movdObj.items.get(i).pesom=tc;
                    T_movrObj.items.get(i).srazon=" ";
                }

                adapter=new LA_T_movd(this,this,T_movdObj.items);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                msgbox(e.getMessage());
            }

            if (T_movdObj.count==0) creaEncabezadoInicial();

        }

        lblTCant.setText("Cantidad : "+mu.frmdecno(cl));;
        lblTCosto.setText("Total : "+mu.frmcur(costot));
    }

    private void addItem() {
        clsClasses.clsT_movd item=clsCls.new clsT_movd();
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        double cant,costo,dd;
        String ss;

        if (prodid==0) {
            toast("Falta definir articulo");
            khand.setLabel(lblBar,true);return;
        }

        try {
            ss=lblCant.getText().toString();
            dd=Double.parseDouble(ss);
            cant=dd;
            if (cant<=0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");khand.setLabel(lblCant,true);return;
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

            if (ingreso) {

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
                    itemr.unidadmedida=lblUni.getText().toString();
                    itemr.precio=costo;

                    T_movrObj.add(itemr);

                } else {
                    selitemr.cant=cant;
                    selitemr.precio=costo;

                    T_movrObj.update(selitemr);
                }
            } else {

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
                    item.unidadmedida=lblUni.getText().toString();
                    item.precio=costo;

                    T_movdObj.add(item);

                } else {
                    selitem.cant=cant;
                    selitem.precio=costo;

                    T_movdObj.update(selitem);
                }
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,true);
            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

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
            if (!gl.corelmov.isEmpty()) {
                db.execSQL("DELETE FROM D_MOV WHERE COREL='" + gl.corelmov + "'");
                db.execSQL("DELETE FROM D_MOVD WHERE COREL='" + gl.corelmov + "'");
            }

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            if (ingreso) header.TIPO="R";else header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA=lblDocumento.getText().toString();//gl.nombre_proveedor;
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);

                    item =clsCls.new clsD_MovD();

                    item.coreldet=corm+i+1;
                    item.corel=corel;
                    item.producto=imovr.producto;
                    item.cant=convcant;         // imovr.cant;
                    item.cantm=0;
                    item.peso=0;
                    item.pesom=0;
                    item.lote="";
                    item.codigoliquidacion=0;
                    item.unidadmedida=convum;   // imovr.unidadmedida;
                    item.precio=imovr.precio;
                    item.motivo_ajuste=0;

                    movd.add(item);

                    updateStock(imovr.producto,convcant,convum);
                    //updateStock(imovr.producto,imovr.cant,imovr.unidadmedida);
                }

            } else {

                for (int i = 0; i <T_movdObj.count; i++) {

                    imov=T_movdObj.items.get(i);
                    converUMBas(imov.producto,imov.cant,imov.unidadmedida);

                    item =clsCls.new clsD_MovD();

                    item.coreldet=corm+i+1;
                    item.corel=corel;
                    item.producto=imov.producto;
                    item.cant=convcant;         // imov.cant;
                    item.cantm=0;
                    item.peso=0;
                    item.pesom=0;
                    item.lote="";
                    item.codigoliquidacion=0;
                    item.unidadmedida=convum;   // imov.unidadmedida;
                    item.precio=imov.precio;
                    item.motivo_ajuste=0;

                    movd.add(item);

                    updateStock(imov.producto,convcant,convum);
                    //updateStock(imov.producto,imov.cant,imov.unidadmedida);
                }

                borraEncabezadoInicial();
                db.execSQL("DELETE FROM T_MOVD");
            }

            db.execSQL("DELETE FROM T_MOVR");

            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Existencias actualizadas");

            if (gl.peInvCompart) {
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            }

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
            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");
            khand.setLabel(lblBar,true);

            toastlong("¡El producto "+barcode+" no existe!");
        }
    }

    private boolean barraProducto() {
          try {
            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            sql =" WHERE (CODBARRA='"+barcode+"') OR (CODIGO='"+barcode+"') " +
                 " COLLATE NOCASE  ";
            P_productoObj.fill(sql);
            if (P_productoObj.count==0) {
                return false;
            }

            prodid=P_productoObj.first().codigo_producto;
            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            khand.setLabel(lblCant,true);khand.val="";
            lblCant.setText("");

            if (P_productoObj.first().costo>0) {
                lblCosto.setText(""+P_productoObj.first().costo);
            } else {
                lblCosto.setText("0");
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        prodid=0;
        return false;
    }

    private void setProduct() {
        try {

            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

            prodid=selitem.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            lblCant.setText(mu.frmdecno(selitem.cant));
            khand.setLabel(lblCant,true);
            lblCosto.setText(""+selitem.precio);

            lblUni.setText(selitemr.unidadmedida);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setProductr() {
        try {

            lblProd.setText("");lblUni.setVisibility(View.INVISIBLE);
            lblBar.setText("");lblCant.setText("");lblCosto.setText("");

            prodid=selitemr.producto;

            sql ="WHERE (CODIGO_PRODUCTO="+prodid+")";
            P_productoObj.fill(sql);

            prodname=P_productoObj.first().codigo+" - "+P_productoObj.first().desclarga;
            um=P_productoObj.first().unidbas;

            lblProd.setText(prodname);nombreUnidad();
            lblUni.setVisibility(View.VISIBLE);

            lblCant.setText(mu.frmdecno(selitemr.cant));
            khand.setLabel(lblCant,true);
            lblCosto.setText(""+selitemr.precio);

            lblUni.setText(selitemr.unidadmedida);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateStock(int pcod,double pcant,String um) {
        try {

            ins.init("P_STOCK");

            ins.add("CODIGO",pcod);
            ins.add("CANT",pcant);
            ins.add("CANTM",0);
            ins.add("PESO",0);
            ins.add("plibra",0);
            ins.add("LOTE","");
            ins.add("DOCUMENTO","");

            ins.add("FECHA",0);
            ins.add("ANULADO",0);
            ins.add("CENTRO","");
            ins.add("STATUS","");
            ins.add("ENVIADO",1);
            ins.add("CODIGOLIQUIDACION",0);
            ins.add("COREL_D_MOV","");
            ins.add("UNIDADMEDIDA",um);

            String sp=ins.sql();

            db.execSQL(ins.sql());
        } catch (Exception e) {
            sql="UPDATE P_STOCK SET CANT=CANT+"+pcant+" WHERE (CODIGO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
            db.execSQL(sql);
        }

    }

    private void converUMBas(int prodid,Double ccant,String umed) throws Exception {
        String prumb;
        double factor;

        P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);
        prumb=P_productoObj.first().unidbas;
        factor=1;

        if (!umed.equalsIgnoreCase(prumb)) {
            P_unidad_convObj.fill("WHERE (CODIGO_UNIDAD1='"+prumb+"') AND (CODIGO_UNIDAD2='"+umed+"')");
            factor=P_unidad_convObj.first().factor;
        }

        if (factor<=0) throw new Exception("Factor conversion no existe "+prumb+" / "+umed);

        convum=prumb;
        convcant=ccant/factor;
    }

    private void cargaDatos() {
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movr itemr=clsCls.new clsT_movr();
        int prodid;
        String pname;

        try {

            clsD_MovObj D_movObj=new clsD_MovObj(this,Con,db);
            D_movObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            lblDocumento.setText(D_movObj.first().REFERENCIA);
            gl.codigo_proveedor=D_movObj.first().CODIGO_PROVEEDOR;

            db.execSQL("DELETE FROM T_MOVR");

            clsD_MovDObj D_movdObj=new clsD_MovDObj(this,Con,db);
            D_movdObj.fill("WHERE (COREL='"+gl.corelmov+"')");

            clsT_movrObj T_movrObj=new clsT_movrObj(this,Con,db);

            for (int i = 0; i <D_movdObj.count; i++) {
                item=D_movdObj.items.get(i);
                prodid=item.producto;

                P_productoObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+")");
                if (P_productoObj.count>0) pname=P_productoObj.first().desclarga;else pname="";

                itemr.coreldet=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                itemr.corel=" ";
                itemr.producto=prodid;
                itemr.cant=item.cant;
                itemr.cantm=0;
                itemr.peso=0;
                itemr.pesom=0;
                itemr.lote=pname;
                itemr.codigoliquidacion=0;
                itemr.unidadmedida=item.unidadmedida;
                itemr.precio=item.precio;

                T_movrObj.add(itemr);
            }

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void hold() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;
        String corel;

        try {
            if (gl.corelmov.isEmpty()) {
                corel=gl.ruta+"_"+mu.getCorelBase();
            } else {
                corel=gl.corelmov;
            }

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            db.beginTransaction();

            db.execSQL("DELETE FROM D_MOV WHERE COREL='"+gl.corelmov+"'");
            db.execSQL("DELETE FROM D_MOVD WHERE COREL='"+gl.corelmov+"'");

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            if (ingreso) header.TIPO="R";else header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA=lblDocumento.getText().toString();//gl.nombre_proveedor;
            header.STATCOM="N";
            header.IMPRES=-1;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            if (ingreso) {

                for (int i = 0; i <T_movrObj.count; i++) {

                    imovr=T_movrObj.items.get(i);
                    converUMBas(imovr.producto,imovr.cant,imovr.unidadmedida);

                    item =clsCls.new clsD_MovD();

                    item.coreldet=corm+i+1;
                    item.corel=corel;
                    item.producto=imovr.producto;
                    item.cant=convcant;         // imovr.cant;
                    item.cantm=0;
                    item.peso=0;
                    item.pesom=0;
                    item.lote="";
                    item.codigoliquidacion=0;
                    item.unidadmedida=convum;   // imovr.unidadmedida;
                    item.precio=imovr.precio;
                    item.motivo_ajuste=0;

                    movd.add(item);


                }

            }

            db.execSQL("DELETE FROM T_MOVR");

            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Ingreso de mercancia guardado");

            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //endregion

    //region Menu

    private void iniciaProceso() {
        clsClasses.clsMenu item;

        try {

            if (ingreso) db.execSQL("DELETE FROM T_movr");

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
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void processMenuBtn(int menuid) {
        try {
            switch (menuid) {
                case 50:
                    gl.gstr = "";        browse = 1;
                    gl.gstr="";
                    gl.prodtipo = 2;
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
            if (ingreso) T_movrObj.delete(selitemr);else T_movdObj.delete(selitem);

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

    private void creaEncabezadoInicial() {
        clsClasses.clsD_Mov header;
        String corel=gl.ruta+"_"+mu.getCorelBase();

        try {
            clsD_MovObj mov=new clsD_MovObj(this,Con,db);

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="I";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA="ACTIVO";
            header.STATCOM="X";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;

            mov.add(header);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void borraEncabezadoInicial() {
        try {
            db.execSQL("DELETE FROM D_MOV WHERE STATCOM='X'");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void nombreUnidad() {
        try {
            P_unidadObj.fill("WHERE (CODIGO_UNIDAD='"+um+"')");
            ubas=P_unidadObj.first().nombre;
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            ubas=um;
        }

        lblUni.setText(um);
    }

    private void listUnits() {
        final AlertDialog Dialog;

        try {
            P_unidad_convObj.fill("WHERE (CODIGO_UNIDAD1='"+um+"') AND (CODIGO_UNIDAD2<>'"+um+"')");

            final String[] selitems = new String[P_unidad_convObj.count+1];
            selitems[0]=um;
            for (int i = 0; i <P_unidad_convObj.count; i++) {
                selitems[i+1]=P_unidad_convObj.items.get(i).codigo_unidad2;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Unidad de medida");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    lblUni.setText(selitems[item]);
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

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskTodo(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sql="DELETE FROM T_MOVR";
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
        ExDialog dialog = new ExDialog(this);

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
        ExDialog dialog = new ExDialog(this);

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

        ExDialog dialog = new ExDialog(this);

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

    private void msgAskInterrupt(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    hold();
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
            T_movrObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            P_unidadObj.reconnect(Con,db);
            P_unidad_convObj.reconnect(Con,db);
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
        if (ingreso) {
            msgAskExit("Salir sin aplicar ingreso");
        } else {
            super.onBackPressed();
        }
    }

    //endregion

}