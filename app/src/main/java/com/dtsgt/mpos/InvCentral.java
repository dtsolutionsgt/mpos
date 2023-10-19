package com.dtsgt.mpos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_stock_inv_detObj;
import com.dtsgt.classes.clsP_stock_inv_errObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.classes.extWaitDlg;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.LA_imp_inv;
import com.dtsgt.webservice.srvCommit;
import com.dtsgt.webservice.wsOpenDT;

import java.util.ArrayList;

public class InvCentral extends PBase {

    private ListView listView;
    private TextView lblTCant,lblTCosto,lblTit;

    private wsOpenDT wsic;
    private Runnable rnInvCent;
    private clsRepBuilder rep;
    private extWaitDlg waitdlg;

    private clsP_stock_inv_detObj P_stock_inv_detObj;
    private clsP_stock_inv_errObj P_stock_inv_errObj;

    private ArrayList<clsClasses.clsT_movr> items= new ArrayList<clsClasses.clsT_movr>();
    private clsClasses.clsT_movr item;

    private ArrayList<Integer> pcod= new ArrayList<Integer>();
    private ArrayList<String>  pum= new ArrayList<String>();

    private LA_imp_inv adapter;

    private fbStock fbs;

    private String inserr,alm_nom;
    private int idinv,codigo_proveedor,idalm,idalmdpred;
    private boolean almacen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_central);

            super.InitBase();

            listView =  findViewById(R.id.listView1);
            lblTit = findViewById(R.id.lblTit3);
            lblTCant = findViewById(R.id.textView155);
            lblTCosto = findViewById(R.id.textView150);

            idinv=gl.invcent_cod;idalm=gl.invcen_alm;

            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) idalmdpred=P_almacenObj.first().codigo_almacen;else idalmdpred=0;

            P_almacenObj.fill("WHERE CODIGO_ALMACEN="+idalm);
            if (P_almacenObj.count>0) {
                alm_nom=" - Almacen: "+P_almacenObj.first().nombre;
            } else alm_nom="";
            almacen=false;
            if (idalm>0) {
                if (idalm!=idalmdpred) almacen=true;
            }

            lblTit.setText("INVENTARIO INICIAL #"+idinv+alm_nom);

            P_stock_inv_detObj=new clsP_stock_inv_detObj(this,Con,db);
            P_stock_inv_errObj=new clsP_stock_inv_errObj(this,Con,db);

            app.getURL();

            fbs =new fbStock("Stock",gl.tienda);

            wsic=new wsOpenDT(gl.wsurl);
            rnInvCent= () -> {callbackInvCent();};

            setHandlers();

            waitdlg= new extWaitDlg();

            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            codigo_proveedor=suc.first().codigo_proveedor;

            printer prn=new printer(this,null,gl.validimp);
            rep=new clsRepBuilder(this,prn.prw,true,gl.peMon,gl.peDecImp,"");

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    requestInventory();
                }
            };
            mtimer.postDelayed(mrunner,200);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doApply(View view) {
        msgAskAplicar();
    }

    public void doPrint(View view) {
        msgAskImprimir();
    }

    public void doCancel(View view) {
        msgAskCancelar();
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    adapter.setSelectedIndex(position);
                };
            });

        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), "");
        }
    }

    //endregion

    //region Main

    private void listItems() {

        double tot;

        try {

            sql="SELECT P_STOCK_INV_DET.CODIGO_PRODUCTO, P_STOCK_INV_DET.UNIDADMEDIDA,'', " +
                    "P_STOCK_INV_DET.CANT, P_STOCK_INV_DET.COSTO, " +
                    "P_PRODUCTO.DESCLARGA ,'', '','' " +
                    "FROM P_STOCK_INV_DET INNER JOIN " +
                    "P_PRODUCTO ON P_PRODUCTO.CODIGO_PRODUCTO= P_STOCK_INV_DET.CODIGO_PRODUCTO " +
                    "WHERE  (P_STOCK_INV_DET.CODIGO_INVENTARIO_ENC="+idinv+") " +
                    "ORDER BY P_PRODUCTO.DESCLARGA";

            clsViewObj ViewObj=new clsViewObj(this,Con,db);
            ViewObj.fillSelect(sql);
            int cl=ViewObj.items.size();

            db.execSQL("DELETE FROM P_STOCK_INV_ERR WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            tot=0;
            for (int i = 0; i <cl; i++) {

                item = clsCls.new clsT_movr();

                item.coreldet=i;
                item.corel=" ";
                item.producto=ViewObj.items.get(i).pk;
                item.cant=Double.parseDouble(ViewObj.items.get(i).f3);
                item.cantm=0;
                item.peso=0;
                item.pesom=item.cant*Double.parseDouble(ViewObj.items.get(i).f4);tot+=item.pesom;
                item.lote=ViewObj.items.get(i).f5;
                item.codigoliquidacion=0;
                item.unidadmedida=ViewObj.items.get(i).f1;
                item.precio=0;
                item.razon=0;

                item.srazon="";
                item.val1="";
                item.val2="";

                items.add(item);

            }

            adapter=new LA_imp_inv(this,this,items);
            listView.setAdapter(adapter);

            lblTCant.setText("Registros: "+cl);
            lblTCosto.setText("Costo inventario: "+mu.frmcur(tot));

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void saveInventory() {
        clsClasses.clsP_stock_inv_det item;
        Cursor dt=wsic.openDTCursor;

        try {
            db.beginTransaction();

            db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                item = clsCls.new clsP_stock_inv_det();

                item.codigo_inventario_enc=idinv;
                item.codigo_producto=dt.getInt(0);
                item.unidadmedida=dt.getString(1);
                item.cant=dt.getDouble(2);
                item.costo=dt.getDouble(3);

                P_stock_inv_detObj.add(item);

                dt.moveToNext();
            }

            dt.close();

            listItems();

            db.setTransactionSuccessful();
            db.endTransaction();

            enviaEstado(1);
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void callbackInvCent() {
        try {

            try {
                waitdlg.wdhandle.dismiss();
            } catch (Exception e) {}

            if (wsic.errflag) {
                msgbox(wsic.error);return;
            }

            if (wsic.openDTCursor.getCount()==0) {
                msgbox("El inventario está vecio");return;
            }

            saveInventory();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void requestInventory() {
        try {
            waitdlg.buildDialog(this,"Recibiendo productos . . .","Ocultar");
            waitdlg.show();

            sql="SELECT CODIGO_PRODUCTO, UNIDADMEDIDA, CANT, COSTO " +
                    "FROM P_STOCK_INVENTARIO_DET  WHERE (CODIGO_INVENTARIO_ENC="+idinv+") ";
            wsic.execute(sql,rnInvCent);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean applyInventory() {
        if (almacen) {
            return aplicarInventario();
        } else {
            return aplicarInventario();
        }
    }

    private boolean aplicarInventario() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_costo cost;

        int pc,pi,errs=0;
        String um,corel;
        double cant,cc,costo=0;

        try {
            db.execSQL("DELETE FROM P_STOCK_INV_ERR WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            P_productoObj.fill();

            pcod.clear();pum.clear();

            for (int i = 0; i <P_productoObj.count; i++) {
                pcod.add(P_productoObj.items.get(i).codigo_producto);
                pum.add(P_productoObj.items.get(i).unidbas);
            }

            P_productoObj.items.clear();

            P_stock_inv_detObj.fill("WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");
            for (int i = 0; i <P_stock_inv_detObj.count; i++) {
                pc=P_stock_inv_detObj.items.get(i).codigo_producto;
                um=P_stock_inv_detObj.items.get(i).unidadmedida;
                cc=P_stock_inv_detObj.items.get(i).cant*P_stock_inv_detObj.items.get(i).costo;
                costo+=cc;

                if (!pcod.contains(pc)) {
                    logError(pc,"Producto no existe o deshabilitado");errs++;
                } else {
                    pi=pcod.indexOf(pc);
                    if (!pum.get(pi).equalsIgnoreCase(um)) {
                        logError(pc,"Incorrecta UM: "+um);errs++;
                    }
                }
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

        if (errs>0) return false;

        try {
            db.beginTransaction();

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);
            corel=gl.ruta+"_"+mu.getCorelBase();

            header =clsCls.new clsD_Mov();
            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="R";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA= du.sfecha(du.getActDateTime());
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= codigo_proveedor;
            header.TOTAL=costo;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            db.execSQL("DELETE FROM P_STOCK");

            for (int i = 0; i <P_stock_inv_detObj.count; i++) {
                pc=P_stock_inv_detObj.items.get(i).codigo_producto;
                um=P_stock_inv_detObj.items.get(i).unidadmedida;
                cant=P_stock_inv_detObj.items.get(i).cant;

                if (!addtoStock(pc,cant,um)) {
                   logError(pc,inserr);errs++;
                }

                item =clsCls.new clsD_MovD();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=P_stock_inv_detObj.items.get(i).codigo_producto;
                item.cant=P_stock_inv_detObj.items.get(i).cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=P_stock_inv_detObj.items.get(i).unidadmedida;
                item.precio=P_stock_inv_detObj.items.get(i).costo;
                item.motivo_ajuste=0;

                movd.add(item);

            }

            if (errs==0) db.setTransactionSuccessful();

            db.endTransaction();

            return errs==0;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private boolean aplicarInventarioAlmacen() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_costo cost;

        int pc,pi,errs=0;
        String um,corel;
        double cant,cc,costo=0;

        try {
            db.execSQL("DELETE FROM P_STOCK_INV_ERR WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            P_productoObj.fill();

            pcod.clear();pum.clear();

            for (int i = 0; i <P_productoObj.count; i++) {
                pcod.add(P_productoObj.items.get(i).codigo_producto);
                pum.add(P_productoObj.items.get(i).unidbas);
            }

            P_productoObj.items.clear();

            P_stock_inv_detObj.fill("WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");
            for (int i = 0; i <P_stock_inv_detObj.count; i++) {
                pc=P_stock_inv_detObj.items.get(i).codigo_producto;
                um=P_stock_inv_detObj.items.get(i).unidadmedida;
                cc=P_stock_inv_detObj.items.get(i).cant*P_stock_inv_detObj.items.get(i).costo;
                costo+=cc;

                if (!pcod.contains(pc)) {
                    logError(pc,"Producto no existe o deshabilitado");errs++;
                } else {
                    pi=pcod.indexOf(pc);
                    if (!pum.get(pi).equalsIgnoreCase(um)) {
                        logError(pc,"Incorrecta UM: "+um);errs++;
                    }
                }
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }

        if (errs>0) return false;

        try {
            db.beginTransaction();

            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);
            corel=gl.ruta+"_"+mu.getCorelBase();

            header =clsCls.new clsD_Mov();
            header.COREL=corel;
            header.RUTA=gl.codigo_ruta;
            header.ANULADO=0;
            header.FECHA=du.getActDateTime();
            header.TIPO="R";
            header.USUARIO=gl.codigo_vendedor;
            header.REFERENCIA= du.sfecha(du.getActDateTime());
            header.STATCOM="N";
            header.IMPRES=0;
            header.CODIGOLIQUIDACION=0;
            header.CODIGO_PROVEEDOR= codigo_proveedor;
            header.TOTAL=costo;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            db.execSQL("DELETE FROM P_STOCK");

            for (int i = 0; i <P_stock_inv_detObj.count; i++) {
                pc=P_stock_inv_detObj.items.get(i).codigo_producto;
                um=P_stock_inv_detObj.items.get(i).unidadmedida;
                cant=P_stock_inv_detObj.items.get(i).cant;

                if (!addtoStock(pc,cant,um)) {
                    logError(pc,inserr);errs++;
                }

                item =clsCls.new clsD_MovD();

                item.coreldet=corm+i+1;
                item.corel=corel;
                item.producto=P_stock_inv_detObj.items.get(i).codigo_producto;
                item.cant=P_stock_inv_detObj.items.get(i).cant;
                item.cantm=0;
                item.peso=0;
                item.pesom=0;
                item.lote="";
                item.codigoliquidacion=0;
                item.unidadmedida=P_stock_inv_detObj.items.get(i).unidadmedida;
                item.precio=P_stock_inv_detObj.items.get(i).costo;
                item.motivo_ajuste=0;

                movd.add(item);

            }

            if (errs==0) db.setTransactionSuccessful();

            db.endTransaction();

            return errs==0;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private boolean addtoStock(int pcod,double pcant,String um) {
        int idalmacen=idalm;

        try {
            if (!almacen) idalmacen=0;

            clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

            ritem.idprod=pcod;
            ritem.idalm=idalmacen;
            ritem.cant=pcant;
            ritem.um=um.trim();
            ritem.bandera=0;

            fbs.addItem("/"+gl.tienda+"/",ritem);

            return true;
        } catch (Exception e) {
            inserr=e.getMessage();return false;
        }
    }

    private boolean addtoStockOld(int pcod,double pcant,String um) {
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

            db.execSQL(ins.sql());

            return true;
        } catch (Exception e) {
            inserr=e.getMessage();
            return false;
        }

    }

    private void logError(int prid,String err) {
        try {
            clsClasses.clsP_stock_inv_err item = clsCls.new clsP_stock_inv_err();

            item.codigo_inventario_enc=idinv;
            item.codigo_producto=prid;
            item.nota=err;

            P_stock_inv_errObj.add(item);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void showInvErrors() {
        String ss="";

        try {
            P_stock_inv_errObj.fill("WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");

            for (int i = 0; i <P_stock_inv_errObj.count; i++) {
                ss+="Producto: "+P_stock_inv_errObj.items.get(i).codigo_producto+"\n";
                ss+="- "+P_stock_inv_errObj.items.get(i).nota+"\n";
                if (i==2 && P_stock_inv_errObj.count>3) {
                    ss+="Total errores: "+P_stock_inv_errObj.count+"\n";break;
                }
            }
            msgbox(ss);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void enviaEstado(int estado) {
        String cmd;
        String fs = "" + du.univfechahora(du.getActDateTime());

        try {
            cmd="UPDATE P_STOCK_INVENTARIO_ENC SET ESTADO="+estado+",";
            if (estado==2) {
                cmd+="FECHA_APLICACION='"+fs+"',USUARIO_APLICO="+gl.codigo_vendedor+" ";
            } else {
                cmd+="fec_mod='"+fs+"',user_mod="+gl.codigo_vendedor+" ";
            }
            cmd+="WHERE (CODIGO_INVENTARIO_ENC="+idinv+")";

            Intent intent = new Intent(InvCentral.this, srvCommit.class);
            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("command",cmd);
            startService(intent);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void imprimirInventario() {
        String s1,s2;

        try {
            rep.clear();

            rep.empty();
            rep.addc("INVENTARIO INICIAL");
            setDatosVersion();

            for (int i = 0; i <items.size(); i++) {
                item=items.get(i);

                s1=item.lote;
                s2=item.cant+" "+item.unidadmedida;

                rep.addtotrs(s1,s2);
            }

            rep.line();
            rep.addc("FIN DE REPORTE");
            rep.empty();
            rep.empty();
            rep.empty();

            rep.save();

            app.doPrint(1,0);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setDatosVersion() {
        rep.empty();
        rep.line();
        rep.add("Empresa: " + gl.empnom);
        rep.add("Sucursal: " + gl.tiendanom);
        rep.add("Caja: " + gl.rutanom);
        rep.add("Impresión: "+du.sfecha(du.getActDateTime())+" "+du.shora(du.getActDateTime()));
        rep.add("Vesión MPos: "+gl.parVer);
        rep.add("Generó: "+gl.vendnom);
        rep.line();
    }

    //endregion

    //region Dialogs

    public void msgAskAplicar() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Este proceso creara las nuevas existencias.\n¿Continuar?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Continuar", (dialog12, which) -> {
            try {
                msgAskAplicar2();
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    public void msgAskAplicar2() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Está seguro?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Continuar", (dialog12, which) -> {
            try {
                if (applyInventory()) {
                    enviaEstado(2);
                    msgAskOK();
                } else showInvErrors();
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    public void msgAskCancelar() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Cancelar carga de inventario?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Cancelar", (dialog12, which) -> {
             try {
                 db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");
                 enviaEstado(0);
                 toastcentlong("INVENTARIO CANCELADO");
                 finish();
             } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
             }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    public void msgAskExit(String msg) {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿"+msg+"?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("OK", (dialog12, which) -> {
            try {
                db.execSQL("DELETE FROM P_STOCK_INV_DET WHERE (CODIGO_INVENTARIO_ENC="+idinv+")");
                enviaEstado(2);
                finish();
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }

    public void msgAskOK() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("Inventario inicializado.");
        dialog.setCancelable(false);

        dialog.setPositiveButton("OK", (dialog12, which) -> {
            try {
               finish();
            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        });

        dialog.show();
    }

    public void msgAskImprimir() {

        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Imprimir carga de inventario?");
        dialog.setCancelable(false);

        dialog.setPositiveButton("Imprimir", (dialog12, which) -> {
            imprimirInventario();
        });

        dialog.setNegativeButton("Salir", (dialog1, which) -> {});

        dialog.show();
    }


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_stock_inv_detObj.reconnect(Con,db);
            P_stock_inv_errObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        msgAskExit("Salir sin aplicar inventario");
    }


    //endregion

}