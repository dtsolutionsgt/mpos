package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_mesaObj;
import com.dtsgt.classes.clsP_res_sesionObj;
import com.dtsgt.classes.clsP_res_turnoObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencuentaObj;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Res_mesa;

import java.util.ArrayList;

public class ResMesero extends PBase {

    private GridView gridView;
    private TextView lblcuenta, lblgrupo,lblmes;

    private clsP_res_grupoObj P_res_grupoObj;
    private clsP_res_turnoObj P_res_turnoObj;
    private clsP_res_mesaObj P_res_mesaObj;
    private clsP_res_sesionObj P_res_sesionObj;
    private clsT_ordenObj T_ordenObj;

    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();

    private LA_Res_mesa adapter;

    private ArrayList<clsClasses.clsRes_mesa> mesas= new ArrayList<clsClasses.clsRes_mesa>();
    private clsClasses.clsRes_mesa mesa= clsCls.new clsRes_mesa();

    private int idgrupo,cantpers;
    private String nommes,nmesa,idmesa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_mesero);

        super.InitBase();

        gridView = findViewById(R.id.gridView1);
        lblcuenta =findViewById(R.id.textView179a);
        lblgrupo =findViewById(R.id.textView179b);
        lblmes =findViewById(R.id.textView179b2);

        calibraPantalla();

        P_res_grupoObj=new clsP_res_grupoObj(this,Con,db);
        P_res_turnoObj=new clsP_res_turnoObj(this,Con,db);
        P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
        P_res_sesionObj=new clsP_res_sesionObj(this,Con,db);
        T_ordenObj=new clsT_ordenObj(this,Con,db);

        setHandlers();
        cargaConfig();

    }

    //region Events

    public void doGrupo(View view) {
        showGrupoMenu();
    }

    public void doComp(View view) {
        listMesa();
    }

    public void doRec(View view) {
        showQuickRecep();
    }

    public void doExit(View view) {
        if (gl.meserodir) msgAskExit("Salir"); else finish();
    }

    private void setHandlers() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = gridView.getItemAtPosition(position);
                mesa = (clsClasses.clsRes_mesa)lvObj;

                adapter.setSelectedIndex(position);
                abrirOrden();
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        int idmesa;

        try {
            mesas.clear();
            P_res_mesaObj.fill("WHERE CODIGO_GRUPO="+idgrupo);

            for (int i = 0; i <P_res_mesaObj.count; i++) {

                idmesa=P_res_mesaObj.items.get(i).codigo_mesa;

                mesa= clsCls.new clsRes_mesa();

                mesa.codigo_mesa=idmesa;
                mesa.nombre=P_res_mesaObj.items.get(i).nombre;

                P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+")");

                if (P_res_sesionObj.count>0) {
                    mesa.estado=P_res_sesionObj.first().estado;
                    mesa.pers=P_res_sesionObj.first().cantp;
                    mesa.cuentas=P_res_sesionObj.first().cantc;
                    mesa.fecha=P_res_sesionObj.first().fechault;

                    T_ordenObj.fill("WHERE (COREL='"+P_res_sesionObj.first().id+"') AND (ESTADO=1)");
                    mesa.pendiente=T_ordenObj.count;
                } else {
                    mesa.estado=0;
                    mesa.pers=0;
                    mesa.cuentas=0;
                    mesa.fecha=0;
                    mesa.pendiente=0;
                }

                mesas.add(mesa);
            }

            adapter=new LA_Res_mesa(this,this,mesas,true);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void abrirOrden() {
        try {
            gl.mesanom=mesa.nombre;
            P_res_sesionObj.fill("WHERE (Estado>0) AND (CODIGO_MESA="+mesa.codigo_mesa+")");
            if (P_res_sesionObj.count>0) {
                gl.idorden=P_res_sesionObj.first().id;
                startActivity(new Intent(this,Orden.class));
            } else {
                //inputPersonas();
                browse=1;
                startActivity(new Intent(this,Comensales.class));
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addOrden() {
        try {
            db.beginTransaction();

            clsClasses.clsP_res_sesion item = clsCls.new clsP_res_sesion();

            item.id=gl.codigo_ruta+"_"+mu.getCorelBase();
            item.codigo_mesa=mesa.codigo_mesa;
            item.vendedor=gl.idmesero;
            item.estado=1;
            item.cantp=cantpers;
            item.cantc=1;
            item.fechaini=du.getActDateTime();
            item.fechafin=0;
            item.fechault=du.getActDateTime();

            P_res_sesionObj.add(item);

            clsT_ordencuentaObj T_ordencuentaObj=new clsT_ordencuentaObj(this,Con,db);
            clsClasses.clsT_ordencuenta cuenta = clsCls.new clsT_ordencuenta();

            cuenta.corel=item.id;
            cuenta.id=1;
            cuenta.cf=1;
            cuenta.nombre="Consumidor final";
            cuenta.nit="C.F.";
            cuenta.direccion="Ciudad";
            cuenta.correo="";

            T_ordencuentaObj.add(cuenta);

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.idorden=item.id;
            startActivity(new Intent(this,Orden.class));
        } catch (Exception e) {
            db.endTransaction();
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listMesa() {

        try {
            lcode.clear();lname.clear();

            for (int i = 0; i <mesas.size(); i++) {
                if (mesas.get(i).estado==0) {
                    lcode.add(""+mesas.get(i).codigo_mesa);
                    lname.add(mesas.get(i).nombre);
                }
            }

            final String[] selitems = new String[lname.size()];

            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = "Mesa "+lname.get(i);
            }

            ExDialog mMenuDlg = new ExDialog(this);

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        idmesa=lcode.get(item);
                        nmesa=lname.get(item);
                        listComp();
                    } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

            AlertDialog Dialog = mMenuDlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listComp() {

        try {
            lcode.clear();lname.clear();

            P_res_sesionObj.fill("WHERE (CODIGO_MESA="+idmesa+") AND (Estado=-1) AND (FECHAULT>="+du.ffecha00(du.getActDate())+")");

            if (P_res_sesionObj.count==0) {
                msgbox("No existe ninguna cuenta completa para la mesa");return;
            }

            for (int i = 0; i <P_res_sesionObj.count; i++) {
                lcode.add(P_res_sesionObj.items.get(i).id);
                lname.add("Mesa : "+nmesa+"  "+du.shora(P_res_sesionObj.items.get(i).fechault));
            }

            final String[] selitems = new String[lname.size()];

            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            ExDialog mMenuDlg = new ExDialog(this);

            mMenuDlg.setTitle("Activar cuenta completa");

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        msgAskActivar("Activar la cuenta",lcode.get(item));
                     } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

            AlertDialog Dialog = mMenuDlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private void cargaConfig() {
        gl.salaid=0;

        try {
            clsVendedoresObj VendedoresObj=new clsVendedoresObj(this,Con,db);
            VendedoresObj.fill("WHERE codigo_vendedor="+gl.idmesero);
            nommes=VendedoresObj.first().nombre;
            lblmes.setText(nommes);

            P_res_turnoObj.fill("WHERE vendedor="+gl.idmesero);
            if (P_res_turnoObj.count>0) {
                idgrupo=P_res_turnoObj.items.get(P_res_turnoObj.count-1).codigo_grupo;
            } else {
                idgrupo=0;
                showGrupoMenu();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        setGrupo();
    }

    private void setGrupo() {
        clsClasses.clsP_res_turno item=clsCls.new clsP_res_turno();

        try {
            item.codigo_grupo=idgrupo;
            item.vendedor=gl.idmesero;
            item.fecha=du.getActDate();
            P_res_turnoObj.add(item);
        } catch (Exception e) {
            P_res_turnoObj.update(item);
        }

        if (idgrupo>0) {
            try {
                P_res_grupoObj.fill("WHERE CODIGO_GRUPO="+idgrupo);
                lblgrupo.setText(P_res_grupoObj.items.get(P_res_grupoObj.count-1).nombre);
            } catch (Exception e) {
            }
        } else lblgrupo.setText("Seleccione una area");

        listItems();
    }

    private void calibraPantalla() {
        /*
        if (gl.pelTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            lblmes.setTextSize(36);lblgrupo.setTextSize(36);
            gridView.setNumColumns(4);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            lblmes.setTextSize(20);lblgrupo.setTextSize(20);
            gridView.setNumColumns(2);
        }
        */
    }

    private void cantidadComensales() {
        try {
            cantpers=gl.comensales;
            if (cantpers<1 | cantpers>50) throw new Exception();
            addOrden();
        } catch (Exception e) {
            mu.msgbox("Cantidad incorrecta");return;
        }
    }

    //endregion

    //region Dialogs

    private void showGrupoMenu() {
        final AlertDialog Dialog;

        try {
            P_res_grupoObj.fill("ORDER BY Nombre");

            final String[] selitems = new String[P_res_grupoObj.count];
            for (int i = 0; i <P_res_grupoObj.count; i++) {
                selitems[i]=P_res_grupoObj.items.get(i).nombre;
            }

            AlertDialog.Builder menudlg = new AlertDialog.Builder(this);
            menudlg.setTitle("Area de mesas");

            menudlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    idgrupo=P_res_grupoObj.items.get(item).codigo_grupo;
                    setGrupo();
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

    public void showQuickRecep() {
        ExDialog dialog = new ExDialog(this);
        dialog.setMessage("¿Actualizar parametros de venta?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gl.findiaactivo=false;
                    gl.tipo = 0;
                    gl.autocom = 0;
                    gl.modoadmin = false;
                    gl.comquickrec = true;
                    startActivity(new Intent(ResMesero.this, WSRec.class));
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
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                   app.logoutUser(du.getActDateTime());
                   finish();
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskActivar(String msg,String id) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  + " ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        db.execSQL("UPDATE P_RES_SESION SET ESTADO=1 WHERE ID='"+id+"'");
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
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void inputPersonas() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Mesa "+mesa.nombre+" , cantidad de personas :");

        final EditText input = new EditText(this);
        alert.setView(input);

        input.setInputType(InputType.TYPE_CLASS_NUMBER );
        input.setText("2");
        input.requestFocus();

        alert.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    cantpers=Integer.parseInt(input.getText().toString());
                    if (cantpers<1 | cantpers>50) throw new Exception();
                    addOrden();
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
    protected void onResume() {
        super.onResume();
        try {
            P_res_grupoObj.reconnect(Con,db);
            P_res_turnoObj.reconnect(Con,db);
            P_res_mesaObj.reconnect(Con,db);
            P_res_sesionObj.reconnect(Con,db);
            T_ordenObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            cantidadComensales();
        }

        if (gl.cerrarmesero) finish();else listItems();
    }

    @Override
    public void onBackPressed() {
        try{
            msgAskExit("Salir");
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }
    //endregion

}