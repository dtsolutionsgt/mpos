package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsP_motivoajusteObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_stockObj;
import com.dtsgt.classes.clsP_stock_almacenObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_movdObj;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.ladapt.LA_T_movr;
import com.dtsgt.ladapt.ListAdaptMenuVenta;

import java.util.ArrayList;

public class InvEgreso extends PBase {

    private ListView listView;
    private GridView grdbtn;
    private EditText txtBarra;
    private TextView lblBar,lblKeyDP,lblProd,lblCant, lblRazon,lblCosto;
    private TextView lblTCant,lblTCosto,lblTit,lblDisp;

    private clsKeybHandler khand;
    private LA_T_movr adapterr;
    private ListAdaptMenuVenta adapterb;
    private clsRepBuilder rep;

    private clsT_movdObj T_movdObj;
    private clsT_movrObj T_movrObj;
    private clsP_productoObj P_productoObj;
    private clsP_motivoajusteObj P_motivoajusteObj;
    private clsP_stockObj P_stockObj;
     private clsP_stock_almacenObj P_stock_almacenObj;

    private ArrayList<clsClasses.clsMenu> mmitems= new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsT_movr selitemr;

    private String barcode,prodname,um,invtext,corel;
    private int prodid,selidx, motivo,selcant;
    private double exist,cantt,costot,disp,costo,htot;
    private boolean almpr,almacen,valcant,scanning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_inv_egreso);
        } else {
            setContentView(R.layout.activity_inv_egreso_ver);
        }

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        grdbtn = (GridView) findViewById(R.id.grdbtn);
        txtBarra = findViewById(R.id.editText10);
        lblTit = (TextView) findViewById(R.id.lblTit3);
        lblBar = (TextView) findViewById(R.id.lblCant);lblBar.setText("");
        lblKeyDP = (TextView) findViewById(R.id.textView110);
        lblProd = (TextView) findViewById(R.id.textView156);lblProd.setText("");
        lblCant = (TextView) findViewById(R.id.textView158);
        lblRazon = (TextView) findViewById(R.id.textView161);
        lblCosto = (TextView) findViewById(R.id.textView163);
        lblTCant = (TextView) findViewById(R.id.textView155);
        lblTCosto = (TextView) findViewById(R.id.textView150);
        lblDisp = (TextView) findViewById(R.id.textView265);

        corel=gl.ruta+"_"+mu.getCorelBase();
        String na=gl.nom_alm.toUpperCase();if (!na.isEmpty()) na="Almacén: "+na+ " -";
        invtext=na+" Egreso de almacen - #"+corel;
        lblTit.setText(invtext);

        prodid=0;
        almpr=gl.idalm==gl.idalmpred;
        almacen=gl.tipo==7;
        if (almpr) {
            almacen=false;
        }

        khand=new clsKeybHandler(this, lblBar,lblKeyDP);
        khand.clear(true);khand.enable();

        T_movdObj=new clsT_movdObj(this,Con,db);
        T_movrObj=new clsT_movrObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);
        P_motivoajusteObj=new clsP_motivoajusteObj(this,Con,db);
        P_stockObj=new clsP_stockObj(this,Con,db);
        P_stock_almacenObj=new clsP_stock_almacenObj(this,Con,db);

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp, "");

        setHandlers();

        iniciaProceso();

        listItems();
        txtBarra.requestFocus();
    }

    //region Events

    public void doSave(View view) {
        if (T_movrObj.count==0) {
            msgbox("No se puede guardar un inventario vacio");return;
        }
        msgAskSave("Aplicar egreso de mercancía");
    }

    public void doFocusBar(View view) {
        txtBarra.requestFocus();
        khand.setLabel(lblBar,true);
        lblProd.setText("");lblBar.setText("");lblCant.setText("");
        lblRazon.setText("");lblCosto.setText("");
        motivo=-1;
    }

    public void doFocusCant(View view) {
        khand.setLabel(lblCant,true);
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
                khand.setLabel(lblCosto,true);
            } else if (khand.label==lblCosto) {
                khand.setLabel(lblRazon,true);
                listaMotivos();
            } else if (khand.label== lblRazon) {
                listaMotivos();
            }
        }

    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

            txtBarra.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

                public void onTextChanged(CharSequence s, int start,int before, int count) {
                    final CharSequence ss=s;

                    if (!scanning) {
                        scanning=true;
                        Handler handlerTimer = new Handler();
                        handlerTimer.postDelayed(new Runnable(){
                            public void run() {
                                compareSC(ss);
                            }}, 500);
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
        double tc,can,ex1,ex2;
        String ss;

        selidx=-1;
        lblProd.setText("");lblBar.setText("");lblCant.setText("");
        lblRazon.setText("");lblCosto.setText("");motivo=-1;
        khand.setLabel(lblBar,true);

        costot=0;cantt=0;

        try {
            T_movrObj.fill();

            for (int i = 0; i <T_movrObj.count; i++) {
                can=T_movrObj.items.get(i).cant;cantt+=can;
                tc=can*T_movrObj.items.get(i).precio; costot+=tc;
                T_movrObj.items.get(i).pesom=tc;

                ex1=dispProd(T_movrObj.items.get(i).producto);ex2=ex1-can;
                T_movrObj.items.get(i).val1=""+mu.frmdecno(ex1);
                T_movrObj.items.get(i).val2=""+mu.frmdecno(ex2);

                P_motivoajusteObj.fill("WHERE (CODIGO_MOTIVO_AJUSTE="+T_movrObj.items.get(i).razon+")");
                if (P_motivoajusteObj.count>0) {
                    T_movrObj.items.get(i).srazon=P_motivoajusteObj.first().nombre;
                } else {
                    T_movrObj.items.get(i).srazon="";
                }
            }

            adapterr=new LA_T_movr(this,this,T_movrObj.items);
            listView.setAdapter(adapterr);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblTCant.setText("Cantidad : "+mu.frmdecno(cantt));
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
            if (cant==0) throw new Exception();
        } catch (Exception e) {
            toast("Cantidad incorrecta");khand.setLabel(lblCant,true);return;
        }

        //if (cant>exist) toast("Insuficiente existencia ("+exist+")");

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
            if (almacen) {
                clsP_stock_almacenObj P_stock_almacenObj=new clsP_stock_almacenObj(this,Con,db);
                P_stock_almacenObj.fill("WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") AND " +
                        "(CODIGO_PRODUCTO="+prodid+") AND (UNIDADMEDIDA='"+um+"') ");
                if (P_stock_almacenObj.count>0) disp=P_stock_almacenObj.first().cant;else disp=0;
            } else {
                clsP_stockObj P_stockObj=new clsP_stockObj(this,Con,db);
                P_stockObj.fill(" WHERE CODIGO="+prodid+" ");
                if (P_stockObj.count>0) disp=P_stockObj.first().cant;else disp=0;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return;
        }

        if (!valcant) {
            if (disp<cant) {
                msgAskCant("Cantidad "+mu.frmdecno(cant)+" es mayor que " +
                           "disponible "+mu.frmdecno(disp)+".\nContinuar");return;
            }
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
                selitemr.precio=costo;
                selitemr.razon=motivo;

                T_movrObj.update(selitemr);
            }

            listItems();

            prodid=0;khand.setLabel(lblBar,true);
            lblProd.setText("");lblBar.setText("");lblCant.setText("");lblDisp.setText("Disponible:");
            lblRazon.setText("");

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        valcant=false;
    }

    private void save() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;

        try {

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            db.beginTransaction();

            header =clsCls.new clsD_Mov();

            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="E";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA=" ";
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= gl.codigo_proveedor;
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            for (int i = 0; i <T_movrObj.count; i++) {

                imovr=T_movrObj.items.get(i);

                item =clsCls.new clsD_MovD();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=imovr.producto;
                item.cant=-imovr.cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=imovr.unidadmedida.trim();
                item.precio=imovr.precio;
                item.motivo_ajuste=imovr.razon;

                movd.add(item);

                adjustStock(imovr.producto,-imovr.cant,imovr.unidadmedida);
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

    private void savealmacen() {
        clsClasses.clsD_mov_almacen header;
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr imovr;

        try {

            clsD_mov_almacenObj mov=new clsD_mov_almacenObj(this,Con,db);
            clsD_movd_almacenObj movd=new clsD_movd_almacenObj(this,Con,db);

            db.beginTransaction();

            header =clsCls.new clsD_mov_almacen();

            header.corel=corel;
            header.codigo_sucursal=gl.tienda;
            header.almacen_origen=0;
            header.almacen_destino=gl.idalm;
            header.anulado=0;
            header.fecha=du.getActDateTime();
            header.tipo="E";
            header.usuario=gl.codigo_vendedor;
            header.referencia=" ";
            header.statcom="N";
            header.impres=0;
            header.codigoliquidacion=0;
            header.codigo_proveedor= gl.codigo_proveedor;
            header.total=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD_ALMACEN");

            for (int i = 0; i <T_movrObj.count; i++) {

                imovr=T_movrObj.items.get(i);

                item =clsCls.new clsD_movd_almacen();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=imovr.producto;
                item.cant=-imovr.cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=imovr.unidadmedida.trim();
                item.precio=imovr.precio;
                item.motivo_ajuste=imovr.razon;

                movd.add(item);

                adjustStockAlmacen(imovr.producto,-imovr.cant,imovr.unidadmedida);
            }

            db.execSQL("DELETE FROM P_STOCK WHERE CANT=0");
            db.execSQL("DELETE FROM P_STOCK_ALMACEN WHERE CANT=0");

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
            lblProd.setText("");lblBar.setText("");
            lblCant.setText("");
            lblRazon.setText("");
            khand.setLabel(lblBar,true);
        }
    }

    private boolean barraProducto() {
        double tdisp=0;

        try {
            khand.clear(true);khand.enable();khand.focus();
            selidx=-1;

            if (!almacen) {
                gl.idalm=0;gl.idalmpred=0;
            }

            if (gl.idalm!=gl.idalmpred) {
                sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK_ALMACEN.UNIDADMEDIDA, " +
                        "P_PRODUCTO.CODIGO_PRODUCTO, P_PRODUCTO.COSTO " +
                        "FROM P_STOCK_ALMACEN INNER JOIN " +
                        "P_PRODUCTO ON P_STOCK_ALMACEN.CODIGO_PRODUCTO=P_PRODUCTO.CODIGO_PRODUCTO " +
                        "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") ";
            } else {
                sql="SELECT P_PRODUCTO.CODIGO, P_PRODUCTO.DESCCORTA, P_STOCK.UNIDADMEDIDA, " +
                        "P_PRODUCTO.CODIGO_PRODUCTO, P_PRODUCTO.COSTO " +
                        "FROM P_STOCK INNER JOIN P_PRODUCTO ON P_STOCK.CODIGO=P_PRODUCTO.CODIGO_PRODUCTO " +
                        "WHERE (1=1) ";
            }

            sql+="AND (P_PRODUCTO.CODBARRA='"+barcode+"') OR (P_PRODUCTO.CODIGO='"+barcode+"') COLLATE NOCASE";
            //msgbox(sql);

            Cursor dt=Con.OpenDT(sql);
            if (dt.getCount()==0) {
                toast("¡El producto "+barcode+" no existe!");return false;
            }


            dt.moveToFirst();
            prodid=dt.getInt(3);
            prodname=dt.getString(0)+" - "+dt.getString(1);
            um=dt.getString(2);
            costo=dt.getDouble(4);

            lblProd.setText(prodname);
            khand.setLabel(lblCant,true);khand.val="";
            lblCant.setText("");txtBarra.setText("");

            if (costo>0) {
                lblCosto.setText(""+costo);
            } else {
                lblCosto.setText("");
            }

            lblDisp.setText("Disponible: "+dispProdUni(prodid));

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

            lblCant.setText(mu.frmdecno(selitemr.cant));
            khand.setLabel(lblCant,true);
            lblCosto.setText(""+selitemr.precio);
            lblRazon.setText(nombreMotivo(motivo));

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void adjustStock(int pcod,double pcant,String um) {
        um=um.trim();
        sql="UPDATE P_STOCK SET CANT=CANT-"+Math.abs(pcant)+" WHERE CODIGO="+pcod+" ";
        db.execSQL(sql);
    }

    private void adjustStockAlmacen(int pcod,double pcant,String um) {
        um=um.trim();
        sql="UPDATE P_stock_almacen SET CANT=CANT-"+Math.abs(pcant)+" " +
                "WHERE (P_STOCK_ALMACEN.CODIGO_ALMACEN="+gl.idalm+") AND (CODIGO_PRODUCTO="+pcod+") AND (UNIDADMEDIDA='"+um+"') ";
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
            item.ID=56;item.Name="Imprimir ";item.Icon=62;
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

                    gl.gstr = "";
                    browse = 1;
                    gl.gstr="";

                    gl.prodtipo = 4;
                    startActivity(new Intent(this, Producto.class));break;

                case 54:
                    borraLinea();break;
                case 55:
                    borraTodo();break;
                case 56:
                    imprimir();break;
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

    //region Impresion

    private void imprimir(){
        int aid=0;

        try {

            rep.clear();
            if (gl.tipo==7) aid=gl.idalm;

            impresionEncabezado(aid);
            impresionDetalle(aid);

            rep.line();
            rep.empty();
            rep.addtote("Valor total: ",mu.frmcur(costot));
            rep.empty();
            rep.empty();
            rep.empty();

            rep.save();

            app.printView();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void impresionEncabezado(int aid) {

        rep.empty();
        rep.empty();
        rep.addc(gl.empnom);
        rep.addc(gl.tiendanom);
        rep.empty();
        rep.addc("PREIMPRESION");
        rep.addc("EGRESO DE MERCANCIA");
        rep.empty();
        rep.add("Numero: "+corel+" ");
        rep.add("Fecha: "+du.sfecha(du.getActDate()));
        rep.add("Operador: "+gl.vendnom);
        if (gl.tipo==7) rep.add("Almacen: "+gl.nom_alm);
        rep.empty();
        rep.add3lrr("Cantidad","Costo","Valor");
        rep.line();
    }

    private void impresionDetalle(int aid) {
        String dum;
        double dcant,dprec,dtot;

        T_movrObj.fill();

        for (int i = 0; i <T_movrObj.count; i++) {

            dum=T_movrObj.items.get(i).unidadmedida;
            dcant=T_movrObj.items.get(i).cant;
            dprec=T_movrObj.items.get(i).precio;
            dtot=Math.abs(dcant*dprec);

            rep.add(app.prodNombre(T_movrObj.items.get(i).producto));
            rep.add3lrre(mu.frmdecno(dcant)+" "+dum,dprec,dtot);
        }

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

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            if (app.horizscr()) return true; else return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    public String dispProdUni(int prodid) {
        double val=0;
        String uum="";

        try {
            if (almacen) {
                P_stock_almacenObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+") AND (CODIGO_ALMACEN="+gl.idalm+")");
                if (P_stock_almacenObj.count>0) {
                    val=P_stock_almacenObj.first().cant;
                    uum=P_stock_almacenObj.first().unidadmedida;
                }
            } else {
                P_stockObj.fill("WHERE CODIGO="+prodid);
                if (P_stockObj.count>0) {
                    val=P_stockObj.first().cant;
                    uum=P_stockObj.first().unidadmedida;
                }
            }
            return mu.frmdecno(val)+" "+uum;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    public double dispProd(int prodid) {
        double val=0;
        try {
            if (almacen) {
                P_stock_almacenObj.fill("WHERE (CODIGO_PRODUCTO="+prodid+") AND (CODIGO_ALMACEN="+gl.idalm+")");
                if (P_stock_almacenObj.count>0) val=P_stock_almacenObj.first().cant;
            } else {
                P_stockObj.fill("WHERE CODIGO="+prodid);
                if (P_stockObj.count>0) val=P_stockObj.first().cant;
            }
            return val;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return 0;
    }

    private void compareSC(CharSequence s) {
        String os,bc;

        bc=txtBarra.getText().toString();
        if (bc.isEmpty() || bc.length()<2) {
            txtBarra.setText("");
            scanning=false;
            return;
        }
        os=s.toString();

        if (bc.equalsIgnoreCase(os)) {
            barcode=bc;gl.barra=barcode;
            if (!barraProducto()) toastlong("¡La barra >> "+barcode+" << no existe!");
        }

        txtBarra.setText("");txtBarra.requestFocus();
        scanning=false;
    }

    //endregion

    //region Dialogs

    private void listaMotivos() {
        int sidx=-1;

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvEgreso.this,"Motivo de ajuste");

            P_motivoajusteObj.fill("WHERE ACTIVO=1 ORDER BY Nombre");

            for (int i = 0; i <P_motivoajusteObj.count; i++) {
                listdlg.add(P_motivoajusteObj.items.get(i).nombre);
                if (P_motivoajusteObj.items.get(i).codigo_motivo_ajuste== motivo) sidx=i;
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        motivo=P_motivoajusteObj.items.get(position).codigo_motivo_ajuste;
                        lblRazon.setText(nombreMotivo(motivo));

                        valcant=false;
                        addItem();

                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    motivo=-1;
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void msgAskTodo(String msg) {
        ExDialog dialog = new ExDialog(this);

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
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    msgAskSave2("Está seguro de continuar");
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
                    if (almacen) {
                        savealmacen();
                    } else {
                        save();
                    }
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

    private void msgAskCant(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                valcant=true;
                addItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { }
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
            P_stock_almacenObj.reconnect(Con,db);
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
        msgAskExit("Salir sin aplicar egreso");
    }

    //endregion

}