package com.dtsgt.mpos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsD_MovObj;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsDocument;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_movrObj;
import com.dtsgt.classes.clsT_stockObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.ListAdaptExist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InvAnular extends PBase {

    private ListView listView;
    private EditText txtFilter;
    private TextView lblReg,lblTit,lblTotal,lblalm;
    private ProgressBar pbar;
    private RelativeLayout relalm;
    private ImageView imganul;

    private AppMethods app;

    private ArrayList<clsClasses.clsExist> items= new ArrayList<clsClasses.clsExist>();
    private ListAdaptExist adapter;
    private clsClasses.clsExist selitem;
    private clsP_almacenObj P_almacenObj;
    private clsT_movrObj T_movrObj;

    private fbStock fbs;
    private DatabaseReference fbconnRef;
    private ValueEventListener fbconnListener,fbrefListener;

    private Runnable rnFbCallBack;

    private int lns, cantExistencia, idalmdpred,idalm;
    private String itemid,prodid,savecant, fbsucursal,pdef_nom,corel;
    private boolean bloqueado=false,almacenes,idle=false,disconnected=false;
    private double cantT,disp,dispm,dispT,costot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_anular);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtFilter = findViewById(R.id.txtFilter);
            lblReg = findViewById(R.id.textView1);lblReg.setText("");
            lblTit= findViewById(R.id.lblTit);
            lblTotal = findViewById(R.id.lblTit7);
            lblalm = findViewById(R.id.textView268);
            relalm= findViewById(R.id.relalm1);
            pbar=findViewById(R.id.progressBar5);pbar.setVisibility(View.INVISIBLE);
            imganul= findViewById(R.id.imageView78);

            app = new AppMethods(this, gl, Con, db);
            app.getURL();

            T_movrObj=new clsT_movrObj(this,Con,db);

            clsP_sucursalObj suc=new clsP_sucursalObj(this,Con,db);
            suc.fill("WHERE CODIGO_SUCURSAL="+gl.tienda);
            gl.codigo_proveedor=suc.first().codigo_proveedor;

            try {
                fbs.fdb.goOnline();
                fbs.fdt.push();
            } catch (Exception e) {}

            fbs =new fbStock("Stock",gl.tienda);
            fbconnRef = fbs.fdb.getReference(".info/connected");
            CreateFbCheckStatus();

            rnFbCallBack = new Runnable() {
                public void run() {
                    buildItemList();
                }
            };
            fbsucursal ="/"+gl.tienda+"/";

            P_almacenObj=new clsP_almacenObj(this,Con,db);
            almacenes=tieneAlmacenes();
            almacenes=true;

            if (idalmdpred>0) {
                try {
                    idalm=idalmdpred;gl.idalm=idalmdpred;
                    gl.nom_alm=pdef_nom;lblalm.setText(gl.nom_alm);
                    listItems();
                } catch (Exception e) {
                    msgbox(new Object() { }.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
                }
            }

            bloqueado=false;

            setHandlers();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doAlmacenes(View view){
        listaAlmacenes();
    }

    public void doAnul(View view) {
        String ss=" ";

        try{
            if (items.size()==0){
                msgbox("No hay inventario disponible");return;
            }

            if (almacenes) ss=" del almacen "+lblalm.getText().toString();
            msgAskAnul("Anular inventario"+ss);
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setHandlers(){

        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        if(!gl.dev){
                            Object lvObj = listView.getItemAtPosition(position);
                            clsClasses.clsExist item = (clsClasses.clsExist)lvObj;

                            itemid=item.Cod;

                            adapter.setSelectedIndex(position);
                        } else {

                            Object lvObj = listView.getItemAtPosition(position);
                            clsClasses.clsExist vItem = (clsClasses.clsExist)lvObj;

                            prodid=vItem.Cod;savecant="";

                            adapter.setSelectedIndex(position);
                        }
                    } catch (Exception e) {
                        mu.msgbox( e.getMessage());
                    }
                };
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsExist item = (clsClasses.clsExist) lvObj;

                        adapter.setSelectedIndex(position);
                        //if (item.flag==1 | item.flag==2) itemDetail(item);
                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                    }
                    return true;
                }
            });

            txtFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start,int count, int after) { }

                public void onTextChanged(CharSequence s, int start,int before, int count) {
                    int tl;

                    tl=txtFilter.getText().toString().length();

                    if (tl==0 || tl>1) buildItemList();
                }
            });
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Main

    private void anulaInventario() {
        try {
            if (almacenes) {

                //if (idalmdpred!=idalm) {
                    savealmacen();
                //} else {
                //    save();
                //}
            } else {
                save();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void save() {
        clsClasses.clsD_Mov header;
        clsClasses.clsD_MovD item;
        clsClasses.clsT_movd imov;
        clsClasses.clsT_movr imovr;

        boolean errflag=false;

        try {
            clsD_MovObj mov=new clsD_MovObj(this,Con,db);
            clsD_MovDObj movd=new clsD_MovDObj(this,Con,db);

            corel=gl.ruta+"_"+mu.getCorelBase();

            db.beginTransaction();

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
            header.TOTAL=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD");

            for (int i = 0; i <T_movrObj.items.size(); i++) {

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
            db.execSQL("DELETE FROM P_STOCK WHERE CANT=0");

            db.setTransactionSuccessful();
            db.endTransaction();

            if (errflag) {
                toastlong("El ajuste no actualizó todos los productos, por favor revise las existencias.");
            } else {
                toastlong("Existencias actualizadas");
            }

            startActivity(new Intent(this,InicioInventario.class));

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            };
            mtimer.postDelayed(mrunner,200);


            //gl.imp_inventario=true;
            //gl.corelmov=corel;

            finish();

        } catch (Exception e) {
            db.endTransaction();
            String se=e.getMessage();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void savealmacen() {
        clsClasses.clsD_mov_almacen header;
        clsClasses.clsD_movd_almacen item;
        clsClasses.clsT_movr imovr;

        try {
            corel=gl.ruta+"_"+mu.getCorelBase();

            clsD_mov_almacenObj mov=new clsD_mov_almacenObj(this,Con,db);
            clsD_movd_almacenObj movd=new clsD_movd_almacenObj(this,Con,db);

            db.beginTransaction();

            header =clsCls.new clsD_mov_almacen();

            header.corel=corel;
            header.codigo_sucursal=gl.tienda;
            header.almacen_origen=gl.idalm;
            header.almacen_destino=0;
            header.anulado=0;
            header.fecha=du.getActDateTime();
            header.tipo="D";
            header.usuario=gl.codigo_vendedor;
            header.referencia=" ";
            header.statcom="N";
            header.impres=0;
            header.codigoliquidacion=0;
            header.codigo_proveedor= gl.codigo_proveedor;
            header.total=costot;

            mov.add(header);

            int corm=movd.newID("SELECT MAX(coreldet) FROM D_MOVD_ALMACEN");

            for (int i = 0; i <T_movrObj.items.size(); i++) {

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

            db.execSQL("DELETE FROM T_MOVR");

            db.setTransactionSuccessful();
            db.endTransaction();

            toastlong("Existencias actualizadas");

            startActivity(new Intent(this,InicioInventario.class));

            Handler mtimer = new Handler();
            Runnable mrunner= () -> {
                gl.autocom = 1;
                startActivity(new Intent(this,WSEnv.class));
            };
            mtimer.postDelayed(mrunner,200);

            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void adjustStock(int pcod,double pcant,String um) {
        try {
            clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

            ritem.idprod=pcod;
            ritem.idalm=0;
            ritem.cant=pcant;
            ritem.um=um.trim();
            ritem.bandera=0;

            fbs.addItem("/"+gl.tienda+"/",ritem);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void adjustStockAlmacen(int pcod,double pcant,String um) {
        int idalmacen=gl.idalm;

        try {
            //if (gl.idalm==gl.idalmpred) idalmacen=0;

            clsClasses.clsFbStock ritem=clsCls.new clsFbStock();

            ritem.idprod=pcod;
            ritem.idalm=idalmacen;
            ritem.cant=pcant;
            ritem.um=um.trim();
            ritem.bandera=0;

            fbs.addItem("/"+gl.tienda+"/",ritem);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listItems() {
        try {
            pbar.setVisibility(View.VISIBLE);
            imganul.setVisibility(View.INVISIBLE);
            idle=false;

            fbs.listExist(fbsucursal,gl.idalm,rnFbCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildItemList() {
        clsClasses.clsExist item,itemt;
        clsClasses.clsT_stock fbitem;
        clsClasses.clsT_movr itemr;
        ArrayList<clsClasses.clsT_stock> sitems= new ArrayList<clsClasses.clsT_stock>();

        String vF,cod, name;
        double costo, total, gtotal=0;
        boolean flag;
        int corr;

        //fbs.orderByNombre();

        items.clear();gtotal=0;
        lblReg.setText("Registros : 0 ");lblTotal.setText("Valor total: "+mu.frmcur(gtotal));

        vF = txtFilter.getText().toString().replace("'","").toUpperCase();

        try {

            if (fbs.sitems.size() == 0) {
                adapter = new ListAdaptExist(this, items,gl.usarpeso);
                listView.setAdapter(adapter);
                pbar.setVisibility(View.INVISIBLE);
                return;
            }

            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            clsT_stockObj T_stockObj=new clsT_stockObj(this,Con,db);
            db.execSQL("DELETE FROM T_stock");

            for (int i = 0; i < fbs.sitems.size(); i++) {
                T_stockObj.add(fbs.sitems.get(i));
            }

            try {

                sql="select IDPROD,SUM(CANT),UM FROM T_STOCK GROUP BY IDPROD";
                Cursor dt=Con.OpenDT(sql);

                sitems.clear();
                if (dt.getCount()>0) {
                    dt.moveToFirst();
                    while (!dt.isAfterLast()) {

                        fbitem=clsCls.new clsT_stock();

                        fbitem.id=0;
                        fbitem.idprod=dt.getInt(0);
                        fbitem.cant=dt.getDouble(1);
                        fbitem.um=dt.getString(2);

                        P_productoObj.fill("WHERE CODIGO_PRODUCTO="+fbitem.idprod+"");
                        fbitem.nombre=P_productoObj.first().desclarga;
                        fbitem.costo=P_productoObj.first().costo;

                        sitems.add(fbitem);

                        dt.moveToNext();
                    }
                }

                corr=T_movrObj.newID("SELECT MAX(coreldet) FROM T_MOVR");
                T_movrObj.items.clear();

                for (int i = 0; i <sitems.size(); i++) {
                    itemr=clsCls.new clsT_movr();

                    itemr.coreldet=corr;
                    itemr.corel=" ";
                    itemr.producto=sitems.get(i).idprod;
                    itemr.cant=sitems.get(i).cant;
                    itemr.cantm=0;
                    itemr.peso=0;
                    itemr.pesom=0;
                    itemr.lote=sitems.get(i).nombre;
                    itemr.codigoliquidacion=0;
                    itemr.unidadmedida=sitems.get(i).um;
                    itemr.precio=0;
                    itemr.razon=0;

                    T_movrObj.items.add(itemr);

                    corr++;

                }

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            Collections.sort(sitems, new InvAnular.ItemComparatorNombre());

            try {
                db.beginTransaction();

                for (int i = 0; i <sitems.size(); i++) {

                    fbitem=sitems.get(i);
                    flag=false;

                    cod=""+fbitem.id;
                    name=fbitem.nombre.toUpperCase();
                    costo=fbitem.costo;
                    total=fbitem.cant*costo;gtotal+=total;


                    if (!vF.isEmpty()) {
                        if (cod.indexOf(vF)>-1) {
                            flag=true;
                        } else {
                            if (name.indexOf(vF)>-1) flag=true;
                        }
                    } else flag=true;

                    if (fbitem.cant==0) flag=false;

                    if (flag) {

                        item = clsCls.new clsExist();
                        item.Cod = ""+fbitem.id;
                        item.Desc = fbitem.nombre;
                        item.flag = 0;
                        item.items=2;
                        items.add(item);

                        itemt = clsCls.new clsExist();
                        itemt.totaluni=mu.frmdecimal(fbitem.cant,0)+" "+fbitem.um;
                        itemt.ValorT = mu.frmdecimal(costo, gl.peDecImp);
                        itemt.PesoT = mu.frmdecimal(total, gl.peDecImp);
                        itemt.flag = 3;
                        items.add(itemt);
                    }

                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(e.getMessage());
            }

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        adapter = new ListAdaptExist(this, items, gl.usarpeso);
        listView.setAdapter(adapter);

        lblTotal.setText("Valor total: "+mu.frmcur(gtotal));costot=gtotal;
        lblReg.setText("Registros : "+ ((int) items.size()/2));
        pbar.setVisibility(View.INVISIBLE);

        imganul.setVisibility(View.VISIBLE);

        idle=true;

    }

    //endregion

    //region Aux

    private boolean tieneAlmacenes() {

        gl.idalmpred =0;gl.idalm=0;idalmdpred=0;pdef_nom="";

        try {
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1");

            if (P_almacenObj.count<2) return false;

            idalmdpred=P_almacenObj.first().codigo_almacen;gl.idalmpred =idalmdpred;

            P_almacenObj.fill("WHERE ACTIVO=1 AND ES_PRINCIPAL=1");
            if (P_almacenObj.count>0) {
                idalmdpred=P_almacenObj.first().codigo_almacen;
                gl.idalmpred =idalmdpred;
                pdef_nom=P_almacenObj.first().nombre;
            }

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private void listaAlmacenes() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvAnular.this,"Seleccione un almacen");

            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);
            P_almacenObj.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");

            for (int i = 0; i <P_almacenObj.count; i++) {
                listdlg.add(P_almacenObj.items.get(i).codigo_almacen+"",P_almacenObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        try {
                            idalm=Integer.parseInt(listdlg.items.get(position).codigo);
                            gl.idalm=idalm;
                            gl.nom_alm=listdlg.items.get(position).text;
                            lblalm.setText(gl.nom_alm);
                            listItems();
                        } catch (Exception e) {
                            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                        }
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private class ItemComparatorNombre implements Comparator<clsClasses.clsT_stock> {
        public int compare(clsClasses.clsT_stock left, clsClasses.clsT_stock right) {
            return left.nombre.compareTo(right.nombre);
        }
    }

    private void CreateFbCheckStatus() {
        try {
            fbconnListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    disconnected=!connected;
                    if (!connected) msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    disconnected=true;
                    msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                }

            };

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        try {
            fbrefListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    disconnected=!connected;

                    if (!connected) msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");
                    try {
                        fbconnRef.removeEventListener(this);
                    } catch (Exception e) {}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    disconnected=true;
                    msgboxexit("Las existencias no están actualizadas.\nPor favor intente mas tarde.");

                    try {
                        fbconnRef.removeEventListener(this);
                    } catch (Exception e) {}
                }

            };

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }


    }

    //endregion

    //region Dialogs

    private void msgAskAnul(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskAnul2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskAnul2(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                anulaInventario();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    public void msgboxexit(String msg) {
        try {
            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            dialog.show();
        } catch (Exception e){ }
    }

    //endregion

    //region Activity Events

    @Override
    protected  void onResume(){
        try{
            super.onResume();

            T_movrObj.reconnect(Con,db);
            fbconnRef.addValueEventListener(fbconnListener);

        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    @Override
    protected void onPause() {
        try {
            try {
                fbconnRef.removeEventListener(fbconnListener);
            } catch (Exception e) { }
            super.onPause();
        } catch (Exception e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    @Override
    public void onBackPressed() {
        try {
            fbconnRef.removeEventListener(fbconnListener);
        } catch (Exception e) { }

        if (bloqueado) toast("Actualizando inventario . . .");else super.onBackPressed();
    }

    //endregion

}