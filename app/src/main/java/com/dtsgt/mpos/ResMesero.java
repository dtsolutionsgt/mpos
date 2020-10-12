package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_res_grupoObj;
import com.dtsgt.classes.clsP_res_turnoObj;

public class ResMesero extends PBase {

    private TextView lblcuenta, lblgrupo;

    private clsP_res_grupoObj P_res_grupoObj;
    private clsP_res_turnoObj P_res_turnoObj;

    private int idgrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_mesero);

        super.InitBase();

        lblcuenta =findViewById(R.id.textView179a);
        lblgrupo =findViewById(R.id.textView179b);

        P_res_grupoObj=new clsP_res_grupoObj(this,Con,db);
        P_res_turnoObj=new clsP_res_turnoObj(this,Con,db);

        cargaConfig();

    }

    //region Events

    public void doGrupo(View view) {
        showNivelMenu();
    }

    public void doOrder(View view) {
        startActivity(new Intent(this,Orden.class));
    }

    public void doRec(View view) {
        showQuickRecep();
    }

    public void doExit(View view) {
        msgAskExit("Regresar al menu principal");
    }

    //endregion

    //region Main


    //endregion

    //region Aux

    private void cargaConfig() {
        gl.salaid=0;

        try {
            P_res_turnoObj.fill();
            if (P_res_turnoObj.count>0) {
                idgrupo=P_res_turnoObj.items.get(P_res_turnoObj.count-1).codigo_grupo;
            } else {
                idgrupo=0;
                showNivelMenu();
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
            item.vendedor=gl.codigo_vendedor;
            item.fecha=du.getActDate();
            P_res_turnoObj.add(item);
        } catch (Exception e) {
            P_res_turnoObj.update(item);
        }

        if (idgrupo>0) {
            try {
                P_res_grupoObj.fill("WHERE CODIGO_GRUPO="+idgrupo);
                lblgrupo.setText(P_res_grupoObj.first().nombre);


                return;
            } catch (Exception e) {
            }
        }
        lblgrupo.setText("Seleccione una area");
    }

    //endregion

    //region Dialogs

    private void showNivelMenu() {
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

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_res_grupoObj.reconnect(Con,db);
            P_res_turnoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        try{
            msgAskExit("Regresar al menu principal");
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }
    //endregion


}