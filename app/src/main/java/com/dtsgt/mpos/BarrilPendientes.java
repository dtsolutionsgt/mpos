package com.dtsgt.mpos;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsD_barril_transObj;
import com.dtsgt.webservice.wsCommit;

public class BarrilPendientes extends PBase {

    private TextView lbl;

    private wsCommit wsbtr;

    private Runnable rnBarTrans;
    private Runnable rnBarInv;

    private clsD_barril_transObj D_barril_transObj;
    private clsD_barrilObj D_barrilObj;

    private clsClasses.clsD_barril_trans btrans;
    private clsClasses.clsD_barril binv;

    private int btrpos,binvpos,btrtot,btrtr,btrinv, bpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barril_pendientes);

        super.InitBase();

        lbl=findViewById(R.id.textView301);

        D_barril_transObj=new clsD_barril_transObj(this,Con,db);
        D_barrilObj=new clsD_barrilObj(this,Con,db);

        rnBarTrans = new Runnable() {
            public void run() {
                envioBarrilTrans();
            }
        };

        rnBarInv = new Runnable() {
            public void run() {
                envioBarrilInv();
            }
        };

        wsbtr =new wsCommit(gl.wsurl);

        Handler mtimer = new Handler();
        Runnable mrunner=new Runnable() {
            @Override
            public void run() {
                listBarrilTrans();
            }
        };
        mtimer.postDelayed(mrunner,200);

    }

    //region Events


    //endregion

    //region Main

    private void listBarrilTrans() {
        try {
            D_barril_transObj.fill("WHERE STATCOM=0");
            D_barrilObj.fill("WHERE STATCOM<>1");

            btrtr=D_barril_transObj.count;
            btrinv=D_barrilObj.count;
            btrtot=btrtr+btrinv;

            btrpos=0;binvpos=0;
            bpos =1;

            if (btrtot==0) finish();

            lbl.setText("Actualizando . . . .   ("+btrtot+")");

            if (btrtr>0) {
                ejecutaEnvioTrans();
            } else {
                if (btrinv>0) {
                    ejecutaEnvioInv();
                    //finish();
                } else {
                    finish();
                }
            }
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            finish();
        }
    }

    //endregion

    //region Barril Trans

    private void ejecutaEnvioTrans() {
        String cmd;

        try {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                        lbl.setText("Actualizando . . . .   "+ bpos +" / "+btrtot);
                    } catch (Exception e) {}
                }
            });
        } catch (Exception e) {}

        try {
            btrans=D_barril_transObj.items.get(btrpos);
            cmd= addItemBarrilTrans(btrans);
            wsbtr.execute(cmd,rnBarTrans);
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            finish();
        }
    }

    private void envioBarrilTrans() {
        try {
            if (!wsbtr.errflag) {
                btrans.statcom=1;
                D_barril_transObj.update(btrans);
            } else {
                String ee=wsbtr.error;
                toastlong("envioBarrilTrans : "+ee);
            }
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            finish();
        }

        btrpos++;
        bpos++;

        if (btrpos<D_barril_transObj.count) {
            ejecutaEnvioTrans();
        } else {
            ejecutaEnvioInv();
            //finish();
        }
    }

    public String addItemBarrilTrans(clsClasses.clsD_barril_trans item) {

        ins.init("D_barril_trans");

        //ins.add("CODIGO_TRANS",item.codigo_trans);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("FECHAHORA",du.univfechahora(item.fechahora));
        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("CANTIDAD",item.cantidad);
        ins.add("UM",item.um);
        ins.add("MESERO",item.mesero);
        ins.add("TIPO_MOV",item.tipo_mov);
        ins.add("IDTRANS",item.idtrans);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }


    //endregion

    //region Barril

    public void ejecutaEnvioInv() {
        String cmd;

        try {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                        lbl.setText("Actualizando . . . .   "+ bpos +" / "+btrtot);
                    } catch (Exception e) {}
                }
            });
        } catch (Exception e) {}

        try {
            binv=D_barrilObj.items.get(binvpos);
            if (binv.statcom==0) {
                cmd= addItemBarrilInv(binv);
            } else {
                cmd= updateItemBarrilInv(binv);
            }
            wsbtr.execute(cmd,rnBarInv);
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            finish();
        }
    }

    private void envioBarrilInv() {
        try {
            if (!wsbtr.errflag) {
                binv.statcom=1;
                D_barrilObj.update(binv);
            } else {
                String ee=wsbtr.error;
                toastlong("envioBarrilTrans : "+ee);
            }
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            finish();
        }

        binvpos++;
        bpos++;

        if (binvpos<D_barrilObj.count) {
            ejecutaEnvioInv();
        } else {
            finish();
        }
    }

    public String addItemBarrilInv(clsClasses.clsD_barril item) {
        String fv=""+du.univfechahora(item.fecha_vence);
        String fe=""+du.univfechahora(item.fecha_entrada);

        ins.init("D_barril");

        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("LOTE",item.lote);
        //ins.add("FECHA_INICIO",item.fecha_inicio);
        //ins.add("FECHA_CIERRE",item.fecha_cierre);
        ins.add("FECHA_VENCE",fv);
        ins.add("FECHA_ENTRADA",fe);
        //ins.add("FECHA_SALIDA",item.fecha_salida);
        ins.add("USUARIO_INICIO",item.usuario_inicio);
        ins.add("USUARIO_CIERRE",item.usuario_cierre);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    public String updateItemBarrilInv(clsClasses.clsD_barril item) {

        upd.init("D_barril");

        upd.add("EMPRESA",item.empresa);
        upd.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        upd.add("CODIGO_TIPO",item.codigo_tipo);
        upd.add("CODIGO_INTERNO",item.codigo_interno);
        upd.add("ACTIVO",item.activo);
        upd.add("CODIGO_PRODUCTO",item.codigo_producto);
        upd.add("LOTE",item.lote+"");
        if (item.fecha_inicio!=0) upd.add("FECHA_INICIO",du.univfechalong(item.fecha_inicio));
        if (item.fecha_cierre!=0) upd.add("FECHA_CIERRE",du.univfechalong(item.fecha_cierre));
        if (item.fecha_vence!=0) upd.add("FECHA_VENCE",du.univfechalong(item.fecha_vence));
        if (item.fecha_entrada!=0) upd.add("FECHA_ENTRADA",du.univfechalong(item.fecha_entrada));
        if (item.fecha_salida!=0) upd.add("FECHA_SALIDA",du.univfechalong(item.fecha_salida));
        upd.add("USUARIO_INICIO",item.usuario_inicio);
        upd.add("USUARIO_CIERRE",item.usuario_cierre);
        upd.add("STATCOM",item.statcom);

        upd.Where("(CODIGO_BARRIL='"+item.codigo_barril+"')");

        return upd.sql();

    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion

}