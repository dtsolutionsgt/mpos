package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsP_barril_tipoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.ladapt.LA_D_barril;
import com.dtsgt.webservice.srvOrdenEnvio;
import com.dtsgt.webservice.wsCommit;

import java.util.Collections;
import java.util.Comparator;


public class Barriles extends PBase {

    private ListView listView;
    private TextView lblreg;

    private LA_D_barril adapter;

    private clsD_barrilObj D_barrilObj;
    private clsP_productoObj P_productoObj;
    private clsP_barril_tipoObj P_barril_tipoObj;

    private clsClasses.clsD_barril barril;

    private wsCommit wscom;

    private Runnable rnConfirmCallback;

    private String selidbar;
    private int modo_cierre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barriles);

        try {
            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblreg = findViewById(R.id.textView293);

            D_barrilObj=new clsD_barrilObj(this,Con,db);
            P_productoObj=new clsP_productoObj(this,Con,db);
            P_barril_tipoObj=new clsP_barril_tipoObj(this,Con,db);

            P_productoObj.fill();
            P_barril_tipoObj.fill();

            setHandlers();

            rnConfirmCallback = new Runnable() {
                public void run() {
                    confirmCallback();
                }
            };
            wscom =new wsCommit(gl.wsurl);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doOpen(View view) {
        browse=1;
        gl.modo_apertura=true;
        listaDisponibles();
    }

    public void doReopen(View view) {
        browse=2;
        gl.modo_apertura=false;
        listaDisponibles();
    }

    public void doDel(View view) {
        if (selidbar.isEmpty()) {
            msgbox("Debe seleccionar un barril");return;
        }

        browse=3;
        cierreDlg();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsD_barril item = (clsClasses.clsD_barril)lvObj;

                adapter.setSelectedIndex(position);
                gl.gcods=item.codigo_barril;
                selidbar=item.codigo_barril;
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsD_barril item;

        selidbar="";

        try {

            D_barrilObj.fill("WHERE (ACTIVO=1) ");

            for (int i = 0; i <D_barrilObj.count; i++) {
                item=D_barrilObj.items.get(i);

                item.nprod=nombreProd(item.codigo_producto);
                item.ntipo=nombreTipo(item.codigo_tipo);
                item.nest=nombreEst(item.activo);
                item.nfecha=du.sfecha(item.fecha_vence);
                item.nfabrir=du.sfecha(item.fecha_inicio)+" "+du.shora(item.fecha_inicio);
            }

            Collections.sort(D_barrilObj.items, new ItemComparator());

            adapter=new LA_D_barril(this,this,D_barrilObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblreg.setText("Registros: "+D_barrilObj.count);
    }

    private void listaDisponibles() {
        Cursor dt;
        int idprod;

        try {
            clsP_productoObj P_productoObj=new clsP_productoObj(this,Con,db);
            P_productoObj.fill("WHERE (CODIGO_TIPO='B') AND (ACTIVO=1) ORDER BY CODBARRA ");
            if (P_productoObj.count==0) {
                msgbox("No está definido ninguno producto tipo barril");return;
            }

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Barriles.this,"Productos disponibles");

            for (int i = 0; i <P_productoObj.count; i++) {

                idprod=P_productoObj.items.get(i).codigo_producto;
                dt=Con.OpenDT("SELECT CODIGO_PRODUCTO FROM D_BARRIL WHERE (CODIGO_PRODUCTO="+idprod+")");

                if (dt.getCount()>0) {
                    dt=Con.OpenDT("SELECT CODIGO_PRODUCTO FROM D_BARRIL WHERE (CODIGO_PRODUCTO="+idprod+") AND (ACTIVO=1)");
                    if (dt.getCount()==0) {
                        dt=Con.OpenDT("SELECT CODIGO_PRODUCTO FROM D_BARRIL WHERE (CODIGO_PRODUCTO="+idprod+") AND (ACTIVO=-1)");
                        if (dt.getCount()>0) {
                            dt.moveToFirst();
                            listdlg.add(idprod, P_productoObj.items.get(i).desccorta);
                        }
                    }
                }
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        gl.gcods=""+listdlg.getCodigoInt(position);
                        startActivity(new Intent(Barriles.this,BarrilesDisponibles.class));
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

    private void abreBarril(String idbar) {
        try {
            D_barrilObj.fill("WHERE (CODIGO_BARRIL='"+idbar+"')");
            barril=D_barrilObj.first();

            barril.activo=1;
            barril.fecha_inicio=du.getActDateTime();
            barril.fecha_cierre=0;
            barril.fecha_salida=0;
            barril.usuario_inicio=gl.codigo_vendedor;
            barril.usuario_cierre=0;
            barril.statcom=0;

            D_barrilObj.update(barril);

            listItems();

            sendBarrilItem();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void reabreBarril(String idbar) {
        try {
            D_barrilObj.fill("WHERE (CODIGO_BARRIL='"+idbar+"')");
            barril=D_barrilObj.first();

            barril.activo=1;
            barril.fecha_cierre=0;
            barril.fecha_salida=0;
            barril.usuario_inicio=gl.codigo_vendedor;
            barril.usuario_cierre=0;
            barril.statcom=0;

            D_barrilObj.update(barril);

            listItems();

            sendBarrilItem();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void cierraBarril(String idbar) {
        try {
            D_barrilObj.fill("WHERE (CODIGO_BARRIL='"+idbar+"')");
            barril=D_barrilObj.first();

            barril.activo=modo_cierre;
            barril.fecha_cierre=du.getActDateTime();
            barril.usuario_cierre=gl.codigo_vendedor;
            barril.statcom=0;

            D_barrilObj.update(barril);

            listItems();

            sendBarrilItem();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Envio

    private void sendBarrilItem() {
        String cmd="";

        try {
            cmd = "DELETE FROM D_barril WHERE (CODIGO_BARRIL='" + barril.codigo_barril + "');";
            cmd += addItemSqlAndroid(barril) + ";";

            wscom.execute(cmd, rnConfirmCallback);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void confirmCallback() {
        try {
            if (wscom.errflag) {
                String ss=wscom.error;
                msgbox("Error de comunicacion :\n"+ss);
            } else {
                barril.statcom=1;
                D_barrilObj.update(barril);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public String addItemSqlAndroid(clsClasses.clsD_barril item) {

        ins.init("D_barril");

        ins.add("CODIGO_BARRIL",item.codigo_barril);
        ins.add("EMPRESA",item.empresa);
        ins.add("CODIGO_SUCURSAL",item.codigo_sucursal);
        ins.add("CODIGO_TIPO",item.codigo_tipo);
        ins.add("CODIGO_INTERNO",item.codigo_interno);
        ins.add("ACTIVO",item.activo);
        ins.add("CODIGO_PRODUCTO",item.codigo_producto);
        ins.add("LOTE",item.lote+"");
        if (item.fecha_inicio!=0) ins.add("FECHA_INICIO",du.univfechalong(item.fecha_inicio));
        if (item.fecha_cierre!=0) ins.add("FECHA_CIERRE",du.univfechalong(item.fecha_cierre));
        if (item.fecha_vence!=0) ins.add("FECHA_VENCE",du.univfechalong(item.fecha_vence));
        if (item.fecha_entrada!=0) ins.add("FECHA_ENTRADA",du.univfechalong(item.fecha_entrada));
        if (item.fecha_salida!=0) ins.add("FECHA_SALIDA",du.univfechalong(item.fecha_salida));
        ins.add("USUARIO_INICIO",item.usuario_inicio);
        ins.add("USUARIO_CIERRE",item.usuario_cierre);
        ins.add("STATCOM",item.statcom);

        return ins.sql();

    }

    //endregion

    //region Aux

    private String nombreProd(int id) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==id) {
                    return P_productoObj.items.get(i).desccorta;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    private String nombreTipo(int id) {
        try {
            for (int i = 0; i <P_barril_tipoObj.count; i++) {
                if (P_barril_tipoObj.items.get(i).codigo_tipo==id) {
                    return P_barril_tipoObj.items.get(i).descripcion;
                }
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        return "";
    }

    private String nombreEst(int id) {
        switch (id) {
            case -1:
                return "LLENO";
            case 0:
                return "VACIO";
            case 1:
                return "ABIERTO";
        }
        return "";
    }

    public class ItemComparator implements Comparator<clsClasses.clsD_barril> {
        public int compare(clsClasses.clsD_barril left, clsClasses.clsD_barril right) {
            //return left.nprod.compareTo(right.nprod);
            if (left.nprod.equalsIgnoreCase(right.nprod)) {
                return (int) Math.signum(left.fecha_vence-right.fecha_vence);
            } else {
                return left.nprod.compareTo(right.nprod);
            }
        }
    }

    //endregion

    //region Dialogs

    private void cierreDlg() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(Barriles.this,"Cierre de barril");

            listdlg.add( 0,"Cerrar como vacío");
            listdlg.add(-1,"Cerrar como lleno");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        modo_cierre=listdlg.getCodigoInt(position);
                        msgAskCierre("Cerrar el barril");
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

    private void msgAskCierre(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Cierre de barril");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cierraBarril(selidbar);
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
    public void onResume() {
        super.onResume();
        try {
            D_barrilObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            P_barril_tipoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (!gl.gstr.isEmpty()) abreBarril(gl.gstr);
            return;
        }

        if (browse==2) {
            browse=0;
            if (!gl.gstr.isEmpty()) reabreBarril(gl.gstr);
            return;
        }

        if (browse==3) {
            browse=0;
            return;
        }

        listItems();
    }

    //endregion

}