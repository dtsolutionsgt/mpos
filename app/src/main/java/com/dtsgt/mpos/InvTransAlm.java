package com.dtsgt.mpos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsT_almacenObj;
import com.dtsgt.classes.clsT_mov_almacenObj;
import com.dtsgt.classes.clsT_movd_almacenObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.classes.extWaitDlg;
import com.dtsgt.ladapt.LA_T_mov_almacen;
import com.dtsgt.webservice.wsCommit;
import com.dtsgt.webservice.wsOpenDT;

import java.util.ArrayList;

public class InvTransAlm extends PBase {

    private ListView listView;
    private TextView lblalmorig,lblalmdest,lblfechaini,lblfechafin;

    private clsT_mov_almacenObj T_mov_almacenObj;
    private clsT_almacenObj T_almacenObj;

    public ArrayList<clsClasses.clsD_traslado_almacen> tras= new ArrayList<clsClasses.clsD_traslado_almacen>();
    public ArrayList<clsClasses.clsT_movd_almacen> tdets= new ArrayList<clsClasses.clsT_movd_almacen>();

    private LA_T_mov_almacen adapter;

    private wsOpenDT wst;
    private wsCommit wscom;
    private Runnable rnTrasLista,rnTrasEnc,rnTrasDet,rnAlmTrans,rnCamEstado,rnAnulacion;

    private extWaitDlg waitdlg;

    private boolean idle=true, es_fechafin;
    private String idmov,nomalmorig,nomalmdest,corel;
    private int idtrasalmacen,idalmtransito,idalmorig=0,idalmdest=0,modo_alm;
    private int cyear,cmonth,cday;
    private long fechaini, fechafin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inv_trans_alm);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblalmorig = findViewById(R.id.textView268);
            lblalmdest = findViewById(R.id.textView338);
            lblfechaini = findViewById(R.id.lblDateini2);
            lblfechafin = findViewById(R.id.lblDatefin2);

            T_mov_almacenObj=new clsT_mov_almacenObj(this,Con,db);
            T_almacenObj=new clsT_almacenObj(this,Con,db);

            waitdlg= new extWaitDlg();

            app.getURL();
            wst=new wsOpenDT(gl.wsurl);
            wscom =new wsCommit(gl.wsurl);

            rnAlmTrans  = () -> {cbAlmTrans();};
            rnTrasLista = () -> {cbTrasLista();};
            rnTrasEnc   = () -> {cbTrasEnc();};
            rnTrasDet   = () -> {cbTrasDet();};
            rnCamEstado = () -> {cbCamEstado();};
            rnAnulacion = () -> {cbAnulacion();};

            T_almacenObj.fill("WHERE ACTIVO=1 ORDER BY NOMBRE");

            setHandlers();
            setFechaAct();
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

    public void doAlmOrig(View view){
        modo_alm=0;
        listaAlmacenes();
    }

    public void doAlmDest(View view){
        modo_alm=1;
        listaAlmacenes();
    }

    public void showDateDialog1(View view) {
        try {
            es_fechafin = false;
            obtenerFecha();
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void showDateDialog2(View view) {
        try {
            es_fechafin = true;
            obtenerFecha();
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsT_mov_almacen item = (clsClasses.clsT_mov_almacen)lvObj;

                adapter.setSelectedIndex(position);

                idtrasalmacen=item.idtrasalmacen;
                corel=item.corel;

                if (item.estado==1) menuItem();else msgask(1,"¿Imprimir documento?");

            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsT_mov_almacen item;
        long fi,ff;

        try {
            fi=du.ffecha00(fechaini);ff=du.ffecha24(fechafin);

            //sql="WHERE (estado>0) AND (fechaini>="+fi+") AND (fechaini<="+ff+") ";
            sql="WHERE (1=1) ";
            if (idalmorig!=0) sql+=" AND (ALMACEN_ORIGEN="+idalmorig+") ";
            if (idalmdest!=0) sql+=" AND (ALMACEN_DESTINO="+idalmdest+") ";

            T_mov_almacenObj.fill(sql);

            for (int i = 0; i <T_mov_almacenObj.count; i++) {
                item=T_mov_almacenObj.items.get(i);

                item.sestado=nombreEstado(item.estado);
                item.salmacen=nombreAlmacen(item.almacen_origen)+"  --->   "+nombreAlmacen(item.almacen_destino);
                item.sfechaini=du.sfecha(item.fechaini);
                item.sfechafin=du.sfecha(item.fechafin);
                if (item.completo==0) item.scompleto=" ";else item.scompleto="Modificado";
                if (item.estado<2) item.scompleto=" ";
            }

            adapter=new LA_T_mov_almacen(this,this,T_mov_almacenObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void almTrans() {
        try {
            waitdlg.buildDialog(this,"Recibiendo traslados . . .","Ocultar");
            waitdlg.show();

            sql="SELECT CODIGO_ALMACEN,CODIGO_SUCURSAL,CAST(ACTIVO AS INT)," +
                "NOMBRE,CAST(ES_PRINCIPAL AS INT),CAST(ES_DE_TRANSITO AS INT) " +
                "FROM P_ALMACEN WHERE (EMPRESA="+gl.emp+")";
            wst.execute(sql, rnAlmTrans);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            cancel();
        }
    }

    private void cbAlmTrans() {
        clsClasses.clsT_almacen item;

        try {
            if (wst.errflag) throw new Exception(wst.error);

            Cursor dt=wst.openDTCursor;

            try {
                db.beginTransaction();

                db.execSQL("DELETE FROM T_almacen");

                if (dt.getCount()>0) {
                    dt.moveToFirst();
                    while (!dt.isAfterLast()) {

                        item = clsCls.new clsT_almacen();

                        item.codigo_almacen=dt.getInt(0);
                        item.codigo_sucursal=dt.getInt(1);
                        item.activo=dt.getInt(2);
                        item.nombre=dt.getString(3);
                        item.es_principal=dt.getInt(4);
                        item.es_de_transito=dt.getInt(5);

                        T_almacenObj.add(item);
                        dt.moveToNext();
                    }
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }

            T_almacenObj.fill("WHERE (ES_DE_TRANSITO=1)");
            if (T_almacenObj.count==0) {
                msgbox("No está definido almacen de transito.");cancel();return;
            }
            idalmtransito=T_almacenObj.first().codigo_almacen;


            T_almacenObj.fill("WHERE (ACTIVO=1) ORDER BY NOMBRE");

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
                    idtrasalmacen =tras.get(position).codigo_traslado_almacen;
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
            item.idtrasalmacen= idtrasalmacen;
            item.estado=0;
            item.fechaini=du.getActDateTime();
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

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                db.endTransaction();
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                idle=true;
            }

            cambiaEstadoRemoto();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        cancel();
    }

    private void cambiaEstadoRemoto() {
        String sff= du.univfechahora(du.getActDateTime());

        try {
            sql="UPDATE D_TRASLADO_ALMACEN SET " +
                "CODIGO_ESTADO_TRANSACCION=2," +
                "FECHA_RECIBIDO='"+sff+"'," +
                "USR_RECIBIDO=" +gl.codigo_vendedor+" " +
                "WHERE (CODIGO_TRASLADO_ALMACEN="+ idtrasalmacen +")";
            wscom.execute(sql,rnCamEstado);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbCamEstado() {
        try {
            if (wscom.errflag) throw new Exception(wscom.error);

            db.execSQL("UPDATE T_mov_almacen SET estado=1 WHERE (COREL='"+idmov+"')");

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void anularTraslado() {
        String sff= du.univfechahora(du.getActDateTime());


        try {
            sql="UPDATE D_TRASLADO_ALMACEN SET " +
                "CODIGO_ESTADO_TRANSACCION=4," +
                "FECHA_RECIBIDO='"+sff+"'," +
                "USR_RECIBIDO=" +gl.codigo_vendedor+" " +
                "WHERE (CODIGO_TRASLADO_ALMACEN="+ idtrasalmacen +")";
            wscom.execute(sql,rnAnulacion);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cbAnulacion() {
        try {
            if (wscom.errflag) throw new Exception(wscom.error);

            db.execSQL("UPDATE T_mov_almacen SET estado=-1 WHERE (COREL='"+corel+"')");

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void impresion() {
        try {

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    public void listaAlmacenes() {

        try {

            T_almacenObj.fill("WHERE (ACTIVO=1) AND (ES_DE_TRANSITO=0) ORDER BY NOMBRE");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvTransAlm.this,"Seleccione un almacen");

            listdlg.add("0","Todos los almacenes");

            for (int i = 0; i <T_almacenObj.count; i++) {
                listdlg.add(T_almacenObj.items.get(i).codigo_almacen+"",T_almacenObj.items.get(i).nombre);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        try {
                            if (modo_alm==0) {
                                idalmorig=Integer.parseInt(listdlg.items.get(position).codigo);
                                nomalmorig=listdlg.items.get(position).text;
                                lblalmorig.setText(nomalmorig);
                            } else {
                                idalmdest=Integer.parseInt(listdlg.items.get(position).codigo);
                                nomalmdest=listdlg.items.get(position).text;
                                lblalmdest.setText(nomalmdest);
                            }

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

    private String nombreAlmacen(int idalmac) {
        for (int i = 0; i <T_almacenObj.count; i++) {
            if (idalmac==T_almacenObj.items.get(i).codigo_almacen) {
                return T_almacenObj.items.get(i).nombre;
            }
        }
        return " ";
    }

    private String nombreEstado(int idest) {
        String sr=" ";

        switch (idest) {
            case 0:
                sr="Nuevo";break;
            case 1:
                sr="Pendiente";break;
            case 2:
                sr="En proceso";break;
            case 3:
                sr="Completo";break;
        }

        return sr;
    }

    private void obtenerFecha(){
        long ff;

        try {
            if (es_fechafin) {
                ff= fechafin;
            } else {
                ff= fechaini;
            }

            cyear = (int) du.getyear(ff);
            cmonth = (int) du.getmonth(ff);
            cday = (int) du.getday(ff);

            DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    int mesActual = month + 1;
                    String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                    String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

                    if (es_fechafin) {
                        lblfechafin.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                    } else {
                        lblfechaini.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                    }

                    cyear = year;
                    cmonth = Integer.parseInt(mesFormateado);
                    cday = Integer.parseInt(diaFormateado);

                    if (es_fechafin) {
                        fechafin = du.cfechaRep(cyear, cmonth, cday, false);
                    } else {
                        fechaini = du.cfechaRep(cyear, cmonth, cday, true);
                    }

                    listItems();
                }
            },cyear, cmonth, cday);

            recogerFecha.show();
        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setFechaAct() {
        Long fecha, fi;

        try {
            fecha = du.getFechaActualReport();
            fi = du.addDays(fecha, -7);

            lblfechaini.setText(du.sfecha(fi));
            lblfechafin.setText(du.sfecha(fecha));

            fechaini = du.cfechaRep((int) du.getyear(fi), (int) du.getmonth(fi), (int) du.getday(fi), true);
            fechafin = du.cfechaRep((int) du.getyear(fecha), (int) du.getmonth(fecha), (int) du.getday(fecha), false);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

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
                case 1:
                    impresion();break;
                case 2:
                    anularTraslado();break;
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void menuItem() {
        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(InvTransAlm.this,"Opciones ");

            listdlg.add("Procesar");
            listdlg.add("Anular");

            listdlg.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    if (position==0) {
                        gl.idtrasalmacen=corel;
                        startActivity(new Intent(this,InvTransAlmDet.class));
                    } else {
                        msgask(2,"¿Anular traslado?");
                    }

                    listdlg.dismiss();
                } catch (Exception e) {}
            });

            listdlg.setOnLeftClick(v -> listdlg.dismiss());

            listdlg.show();
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
            T_almacenObj.reconnect(Con,db);

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