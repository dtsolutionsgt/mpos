package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_mov_almacenObj;
import com.dtsgt.classes.clsD_movd_almacenObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_almacenObj;
import com.dtsgt.classes.clsT_mov_almacenObj;
import com.dtsgt.classes.clsT_movd_almacenObj;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.LA_T_movd_almacen;
import com.dtsgt.webservice.wsCommit;

public class InvTransAlmDet extends PBase {

    private ListView listView;
    private TextView lblalmorig,lblalmdest,lblref,lblstat,lbldif;

    private clsT_mov_almacenObj T_mov_almacenObj;
    private clsT_movd_almacenObj T_movd_almacenObj;
    private clsT_almacenObj T_almacenObj;
    private clsP_productoObj P_productoObj;

    private clsClasses.clsT_mov_almacen mitem;

    private LA_T_movd_almacen adapter;

    private fbStock fbs;
    private wsCommit wscom;

    private String corel;
    private int idtrasalm,iddestalm,savepos=-1,idtrasalmacen;
    private boolean completo;
    private double cant1,cant2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_trans_alm_det);

            super.InitBase();

            listView   = findViewById(R.id.listView1);
            lblalmorig = findViewById(R.id.textView268);
            lblalmdest = findViewById(R.id.textView338);
            lblref     = findViewById(R.id.lblDateini2);
            lblstat    = findViewById(R.id.textView339);
            lbldif     = findViewById(R.id.textView347);

            T_movd_almacenObj=new clsT_movd_almacenObj(this,Con,db);
            T_mov_almacenObj=new clsT_mov_almacenObj(this,Con,db);
            T_almacenObj=new clsT_almacenObj(this,Con,db);
            P_productoObj=new clsP_productoObj(this,Con,db);

            corel=gl.idtrasalmacen;

            fbs =new fbStock("Stock",gl.tienda);
            wscom =new wsCommit(gl.wsurl);

            loadItem();
            listItems();

            setHandlers();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doPause(View view) {
        msgask(2,"¿Interrupir y continuar más tarde?");
    }

    public void doSave(View view) {

        if (idtrasalm==0) {
            msgbox("No está definido almacen de transito, no se puede procedeer.");return;
        }

        if (completo) {
            msgask(1,"¿Completar traslado?");
        } else {
            msgbox("No se puede completar, falta verificar articulos");
        }
    }

    public void doVerify(View view) {
        msgask(3,"¿Verificados todos los articulos no verificados?");
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsT_movd_almacen item = (clsClasses.clsT_movd_almacen)lvObj;

                adapter.setSelectedIndex(position);
                savepos=position;
                cant1=item.cant;
                msgask();
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsT_movd_almacen item;
        int vercnt,cnt,difcnt;
        double cant,cantv;

        try {
            T_movd_almacenObj.fill("WHERE (COREL='"+corel+"')");

            completo=false;
            cnt=T_movd_almacenObj.count;
            vercnt=0;difcnt=0;

            for (int i = 0; i <T_movd_almacenObj.count; i++) {
                item=T_movd_almacenObj.items.get(i);
                cant=item.cant;cantv=item.cantact;
                item.flag=0;
                if (item.estado==1) {
                    vercnt++;
                    if (cant==cantv) {
                        item.flag=1;
                    } else {
                        difcnt++;item.flag=-1;
                    }
                }

                item.sprod=nombreProducto(item.producto);
                item.scant=mu.frmdecno(item.cant);
                item.scantv=mu.frmdecno(item.cantact);
            }

            completo=vercnt==cnt;
            lblstat.setText("Verificado: "+vercnt+" / "+cnt);
            lbldif.setText("Con diferencia: "+difcnt+" / "+cnt);

            adapter=new LA_T_movd_almacen(this,this,T_movd_almacenObj.items);
            listView.setAdapter(adapter);

            try {
                adapter.setSelectedIndex(savepos);
            } catch (Exception e) { }


        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void loadItem() {
        try {
            T_mov_almacenObj.fill("WHERE (COREL='"+corel+"')");
            mitem=T_mov_almacenObj.first();

            T_almacenObj.fill("WHERE ES_DE_TRANSITO=1");
            if (T_almacenObj.count>0) {
                idtrasalm = T_almacenObj.first().codigo_almacen;
            } else {
                msgbox("No está definido almacen de transito, no se puede procedeer.");
                idtrasalm = 0;return;
            }

            T_almacenObj.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");
            lblalmorig.setText(nombreAlmacen(mitem.almacen_origen));
            lblalmdest.setText(nombreAlmacen(mitem.almacen_destino));
            lblref.setText(""+mitem.referencia);

            iddestalm = mitem.almacen_destino;
            idtrasalmacen=mitem.idtrasalmacen;

            T_movd_almacenObj.fill("WHERE (COREL='"+corel+"')");
            sql="WHERE (CODIGO_PRODUCTO IN (";
            for (int i = 0; i <T_movd_almacenObj.count; i++) {
                sql+=""+T_movd_almacenObj.items.get(i).producto;
                if (i<T_movd_almacenObj.count-1) sql+=",";
            }
            sql+="))";
            P_productoObj.fill(sql);


        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void aceptaCantidad() {
        clsClasses.clsT_movd_almacen item;
        try {
            item=T_movd_almacenObj.items.get(savepos);
            item.cantact=item.cant;
            item.estado=1;

            T_movd_almacenObj.update(item);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void nuevaCantidad(double ncant) {
        clsClasses.clsT_movd_almacen item;
        try {
            item=T_movd_almacenObj.items.get(savepos);
            item.cantact=ncant;
            item.estado=1;

            T_movd_almacenObj.update(item);

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void verificarTodo() {
        clsClasses.clsT_movd_almacen item;

        try {
            for (int i = 0; i <T_movd_almacenObj.count; i++) {

                item=T_movd_almacenObj.items.get(i);

                if (item.estado==0) {
                    item.cantact=item.cant;
                    item.estado=1;

                    T_movd_almacenObj.update(item);
                }
            }

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void save() {
        try {
            if (!saveDocTrans()) {
                return;
            }
            if (!saveStock()) {
                try {
                    db.execSQL("DELETE FROM T_mov_almacen WHERE (COREL='"+corel+"')");
                    db.execSQL("DELETE FROM T_movd_almacen WHERE (COREL='"+corel+"')");
                } catch (Exception e) {
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }
                msgbox("Ocurrio error.");
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean saveDocTrans() {
        clsClasses.clsD_mov_almacen item;
        clsClasses.clsD_movd_almacen ditem;
        clsClasses.clsT_movd_almacen mditem;
        double tot,total=0;

        try {

            String mcorel=gl.ruta+"_"+mu.getCorelBase();

            db.beginTransaction();

            clsD_mov_almacenObj D_mov_almacenObj=new clsD_mov_almacenObj(this,Con,db);
            clsD_movd_almacenObj D_movd_almacenObj=new clsD_movd_almacenObj(this,Con,db);

            db.execSQL("UPDATE T_mov_almacen SET estado=2,completo=1 WHERE (COREL='"+corel+"')");

            T_movd_almacenObj.fill("WHERE (COREL='"+corel+"')");

            for (int i = 0; i <T_movd_almacenObj.count; i++) {

                mditem=T_movd_almacenObj.items.get(i);

                ditem = clsCls.new clsD_movd_almacen();

                ditem.corel=mcorel;
                ditem.producto=mditem.producto;
                ditem.cant=mditem.cant;
                ditem.cantm=0;
                ditem.peso=0;
                ditem.pesom=0;
                ditem.lote=" ";
                ditem.codigoliquidacion=0;
                ditem.unidadmedida=mditem.um;
                ditem.coreldet=i;
                ditem.precio=mditem.precio;
                ditem.motivo_ajuste=0;

                D_movd_almacenObj.add(ditem);

                tot=ditem.cant*ditem.precio;
                total+=tot;
            }


            item = clsCls.new clsD_mov_almacen();

            item.corel=mcorel;
            item.codigo_sucursal=gl.tienda;
            item.almacen_origen=mitem.almacen_origen;
            item.almacen_destino=mitem.almacen_destino;
            item.anulado=0;
            item.fecha=du.getActDateTime();
            item.tipo="T";
            item.usuario=gl.codigo_vendedor;
            item.referencia=" ";
            item.statcom="N";
            item.impres=0;
            item.codigoliquidacion=0;
            item.codigo_proveedor=gl.codigo_proveedor;
            item.total=total;

            D_mov_almacenObj.add(item);

            db.setTransactionSuccessful();
            db.endTransaction();

            enviaEstado();

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean saveStock() {
        clsClasses.clsFbStock bitem;
        clsClasses.clsT_movd_almacen item;

        try {

            T_movd_almacenObj.fill("WHERE (COREL='"+corel+"')");

            fbs.bitems.clear();
            for (int i = 0; i <T_movd_almacenObj.count; i++) {
                item=T_movd_almacenObj.items.get(i);

                bitem=clsCls.new clsFbStock();

                bitem.idprod=item.producto;
                bitem.idalm=iddestalm;
                bitem.cant=item.cant;
                bitem.um=item.um;
                bitem.bandera=0;

                fbs.bitems.add(bitem);

                bitem=clsCls.new clsFbStock();

                bitem.idprod=item.producto;
                bitem.idalm=idtrasalm;
                bitem.cant=-item.cant;
                bitem.um=item.um;
                bitem.bandera=0;

                fbs.bitems.add(bitem);

            }

            fbs.transStockUpdate(null);

            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private void enviaEstado() {
        String sff= du.univfechahora(du.getActDateTime());

        try {
            sql="UPDATE D_TRASLADO_ALMACEN SET " +
                    "CODIGO_ESTADO_TRANSACCION=3," +
                    "FECHA_PROCESADO='"+sff+"'," +
                    "USR_PROCESADO=" +gl.codigo_vendedor+" " +
                    "WHERE (CODIGO_TRASLADO_ALMACEN="+ idtrasalmacen +")";
            wscom.execute(sql,null);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private String nombreAlmacen(int idalmac) {
        for (int i = 0; i <T_almacenObj.count; i++) {
            if (idalmac==T_almacenObj.items.get(i).codigo_almacen) {
                return T_almacenObj.items.get(i).nombre;
            }
        }
        return " ";
    }

    private String nombreProducto(int idprod) {
        for (int i = 0; i <P_productoObj.count; i++) {
            if (idprod==P_productoObj.items.get(i).codigo_producto) {
                return P_productoObj.items.get(i).desclarga;
            }
        }
        return " ";
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    finish();break;
                case 1:
                    save();break;
                case 2:
                    finish();break;
                case 3:
                    msgask(4,"¿Está seguro?");break;
                case 4:
                    verificarTodo();break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void msgask(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage("¿Cantidad correcta?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aceptaCantidad();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ingresaCantidad();
            }
        });

        dialog.setNeutralButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        //dialog.show();

        AlertDialog dlg=dialog.create();
        dlg.show();

        dlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }

    private void ingresaCantidad() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nueva cantidad");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(""+mu.frmdecno(cant1));input.selectAll();
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    double dval=Double.parseDouble(input.getText().toString());
                    if (dval<0) throw new Exception();
                    nuevaCantidad(dval);
                } catch (Exception e) {
                    mu.msgbox("Valor incorrecto");return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });

        alert.show();
    }

    //endregion

    //region Activity Events

    @Override
    public void onResume() {

        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            T_movd_almacenObj.reconnect(Con,db);
            T_mov_almacenObj.reconnect(Con,db);
            T_almacenObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        msgask(0,"¿Salir sin completar?");
    }

    //endregion

}