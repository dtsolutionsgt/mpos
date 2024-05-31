package com.dtsgt.mpos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsT_mov_almacenObj;
import com.dtsgt.classes.clsT_movd_almacenObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extWaitDlg;
import com.dtsgt.ladapt.LA_T_mov_almacen;
import com.dtsgt.webservice.wsOpenDT;

import java.util.ArrayList;

public class InvTransAlm extends PBase {

    private ListView listView;

    private LA_T_mov_almacen adapter;
    private clsT_mov_almacenObj T_mov_almacenObj;

    public ArrayList<clsClasses.clsD_traslado_almacen> tras= new ArrayList<clsClasses.clsD_traslado_almacen>();
    public ArrayList<clsClasses.clsT_movd_almacen> tdets= new ArrayList<clsClasses.clsT_movd_almacen>();

    private wsOpenDT wst;
    private Runnable rnTrasLista,rnTrasEnc,rnTrasDet,rnAlmTrans;

    private extWaitDlg waitdlg;

    private boolean idle=true;
    private String idmov;
    private int idtrasalm,idalmtransito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_trans_alm);

            super.InitBase();

            listView = findViewById(R.id.listView1);

            T_mov_almacenObj=new clsT_mov_almacenObj(this,Con,db);

            waitdlg= new extWaitDlg();

            app.getURL();
            wst=new wsOpenDT(gl.wsurl);

            rnAlmTrans  = () -> {cbAlmTrans();};
            rnTrasLista = () -> {cbTrasLista();};
            rnTrasEnc   = () -> {cbTrasEnc();};
            rnTrasDet   = () -> {cbTrasDet();};

            setHandlers();
            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void onAdd(View view) {
        if (idle) {
            idle=false;
            almTrans();
        } else {
            toast("Espere, por favor . . .");
        }
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {


            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            T_mov_almacenObj.fill();

            adapter=new LA_T_mov_almacen(this,this,T_mov_almacenObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }
    //endregion

    //region Lista traslados

    private void almTrans() {
        try {
            waitdlg.buildDialog(this,"Recibiendo traslados . . .","Ocultar");
            waitdlg.show();

            sql="SELECT CODIGO_ALMACEN FROM P_ALMACEN WHERE (ES_DE_TRANSITO=1) ";
            wst.execute(sql, rnAlmTrans);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void cbAlmTrans() {
        try {
            if (wst.errflag) throw new Exception(wst.error);

            Cursor dt=wst.openDTCursor;
            if (dt.getCount()==0) {
                msgbox("No está definido almacen de transito.");cancel();return;
            }

            dt.moveToFirst();
            idalmtransito=dt.getInt(0);

            listaTras();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void listaTras() {
        try {
            sql="SELECT D_TRASLADO_ALMACEN.CODIGO_TRASLADO_ALMACEN, D_TRASLADO_ALMACEN.COREL_D_MOV_ALMACEN, D_MOV_ALMACEN.REFERENCIA " +
                "FROM  D_TRASLADO_ALMACEN INNER JOIN D_MOV_ALMACEN ON D_TRASLADO_ALMACEN.COREL_D_MOV_ALMACEN = D_MOV_ALMACEN.COREL " +
                "WHERE (D_TRASLADO_ALMACEN.CODIGO_SUCURSAL="+gl.tienda+") AND (D_TRASLADO_ALMACEN.CODIGO_ESTADO_TRANSACCION=1) " +
                "AND (D_MOV_ALMACEN.TIPO='T') AND (D_MOV_ALMACEN.ANULADO=0) ";

            wst.execute(sql, rnTrasLista);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void cbTrasLista() {
        clsClasses.clsD_traslado_almacen item;

        cancel();

        try {
            tras.clear();
            if (wst.errflag) throw new Exception(wst.error);

            Cursor dt=wst.openDTCursor;
            if (dt.getCount()==0) {
                msgbox("No existe ninguno traslado pendiente.");return;
            }

            dt.moveToFirst();
            while (!dt.isAfterLast()) {
                item = clsCls.new clsD_traslado_almacen();

                item.codigo_traslado_almacen=dt.getInt(0);
                item.corel_d_mov_almacen=dt.getString(1);
                item.referencia=dt.getString(2);

                tras.add(item);
                dt.moveToNext();
            }

            mostrarListaTras();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void mostrarListaTras() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvTransAlm.this,"Traslados pendientes");

            for (int i = 0; i <tras.size(); i++) {
                listdlg.add(tras.get(i).referencia);
            }

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    idmov=tras.get(position).corel_d_mov_almacen;
                    idtrasalm=tras.get(position).codigo_traslado_almacen;
                    msgask(0,"¿Recibir traslado "+tras.get(position).referencia+" ?");

                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.setOnLeftClick(v -> listdlg.dismiss());

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void recibirEncTras() {

        try {
            idle=false;
            waitdlg.buildDialog(this,"Recibiendo traslado . . .","Ocultar");
            waitdlg.show();

            sql="SELECT ALMACEN_ORIGEN, ALMACEN_DESTINO, REFERENCIA,TOTAL " +
                "FROM  D_MOV_ALMACEN WHERE (COREL='"+idmov+"')";

            wst.execute(sql, rnTrasEnc);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void cbTrasEnc() {

        try {

            if (wst.errflag) throw new Exception(wst.error);

            if (wst.openDTCursor.getCount()==0) {
                msgbox("El traslado no existe .");return;
            }

            Cursor dt=wst.openDTCursor;
            dt.moveToFirst();

            clsClasses.clsT_mov_almacen item = clsCls.new clsT_mov_almacen();

            item.corel=idmov;
            item.almacen_origen=dt.getInt(0);
            item.almacen_destino=dt.getInt(1);
            item.referencia=dt.getString(2);
            item.total=dt.getDouble(3);
            item.idtrasalmacen=idtrasalm;
            item.estado=0;
            item.fechaini=0;
            item.fechafin=0;
            item.usrini=0;
            item.usrfin=0;
            item.idalmtrans=idalmtransito;
            item.completo=0;


            try {
                db.beginTransaction();

                db.execSQL("DELETE FROM T_mov_almacen WHERE (COREL='"+idmov+"')");
                db.execSQL("DELETE FROM T_movd_almacen WHERE (COREL='"+idmov+"')");

                T_mov_almacenObj.add(item);

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                cancel();
                return;
            }

            recibirDetTras();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void recibirDetTras() {
        try {
            sql="SELECT COREL,PRODUCTO,CANT,UNIDADMEDIDA,PRECIO " +
                "FROM D_MOVD_ALMACEN WHERE (COREL='"+idmov+"')";
            wst.execute(sql, rnTrasDet);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void cbTrasDet() {
        clsClasses.clsT_movd_almacen item;

        try {
            tdets.clear();
            if (wst.errflag) throw new Exception(wst.error);

            if (wst.openDTCursor.getCount()==0) {
                msgbox("El traslado no tiene ninguno producto .");
                cancel();return;
            }

            clsT_movd_almacenObj T_movd_almacenObj=new clsT_movd_almacenObj(this,Con,db);

            Cursor dt=wst.openDTCursor;
            dt.moveToFirst();

            while (!dt.isAfterLast()) {

                item = clsCls.new clsT_movd_almacen();

                item.corel=idmov;

                item.corel=dt.getString(0);
                item.producto=dt.getInt(1);
                item.cant=dt.getDouble(2);
                item.um=dt.getString(3);
                item.cantact=0;
                item.estado=0;
                item.precio=dt.getDouble(4);

                tdets.add(item);
                dt.moveToNext();
            }

            try {
                db.beginTransaction();

                for (int i = 0; i <tdets.size(); i++) {
                    T_movd_almacenObj.add(tdets.get(i));
                }

                db.execSQL("UPDATE T_mov_almacen SET estado=1 WHERE (COREL='"+idmov+"')");

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                idle=true;
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        cancel();
    }


    //endregion

    //region Aux

    private void cancel() {
        idle=true;
        try {
            waitdlg.wdhandle.dismiss();
        } catch (Exception e) {}
    }

    //endregion

    //region Dialogs

    public void dialogswitch() {
        try {
            switch (gl.dialogid) {
                case 0:
                    recibirEncTras();break;

            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        try {
            super.onResume();
            gl.dialogr = () -> {dialogswitch();};

            T_mov_almacenObj.reconnect(Con,db);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (idle) super.onBackPressed(); else toast("Espere, por favor . . .");
    }

    //endregion

}