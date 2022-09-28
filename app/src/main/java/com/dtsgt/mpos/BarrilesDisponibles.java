package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsD_barrilObj;
import com.dtsgt.classes.clsP_barril_tipoObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.ladapt.LA_D_barril;
import java.util.Comparator;

public class BarrilesDisponibles extends PBase {

    private ListView listView;
    private TextView lblreg;

    private LA_D_barril adapter;

    private clsD_barrilObj D_barrilObj;
    private clsP_productoObj P_productoObj;
    private clsP_barril_tipoObj P_barril_tipoObj;

    private String cod_barril;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barriles_disponibles);

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

            gl.gstr="";
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsD_barril item = (clsClasses.clsD_barril)lvObj;

                adapter.setSelectedIndex(position);
                cod_barril=item.codigo_barril;

                if (gl.modo_apertura) {
                    msgAskOpen("Está seguro de abrir barril");
                } else {
                    msgAskReOpen("Está seguro de reabrir barril");
                }
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsD_barril item;

        try {
            if (gl.modo_apertura) {
                sql="WHERE ((ACTIVO=-1) OR (ACTIVO=0) )AND (CODIGO_PRODUCTO="+gl.gcods+") ";
            } else {
                sql="WHERE (ACTIVO=0) AND (CODIGO_PRODUCTO="+gl.gcods+") ";
            }

            D_barrilObj.fill(sql);

            for (int i = 0; i <D_barrilObj.count; i++) {
                item=D_barrilObj.items.get(i);

                item.nprod=nombreProd(item.codigo_producto);
                item.ntipo=nombreTipo(item.codigo_tipo);
                item.nest=nombreEst(item.activo);
                item.nfecha=du.sfecha(item.fecha_vence);
            }

            //Collections.sort(D_barrilObj.items, new ItemComparator());

            adapter=new LA_D_barril(this,this,D_barrilObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblreg.setText("Registros: "+D_barrilObj.count);
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

    private void msgAskOpen(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Control de barriles");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gl.gstr=cod_barril;
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskReOpen(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Control de barriles");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gl.gstr=cod_barril;
                finish();
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

        listItems();
    }

    //endregion


}