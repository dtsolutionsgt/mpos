package com.dtsgt.mpos;

import android.content.Intent;
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
import com.dtsgt.mant.MantBarril;

import java.util.Collections;
import java.util.Comparator;

public class ListaBarril extends PBase {

    private ListView listView;
    private TextView lblprod,lblest,lblreg;

    private LA_D_barril adapter;

    private clsD_barrilObj D_barrilObj;
    private clsP_productoObj P_productoObj;
    private clsP_barril_tipoObj P_barril_tipoObj;

    private int idprod,idest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_barril);

        try {
            super.InitBase();

            listView = findViewById(R.id.listView1);
            lblprod = findViewById(R.id.textView291);
            lblest = findViewById(R.id.textView292);
            lblreg = findViewById(R.id.textView293);

            D_barrilObj=new clsD_barrilObj(this,Con,db);
            P_productoObj=new clsP_productoObj(this,Con,db);
            P_barril_tipoObj=new clsP_barril_tipoObj(this,Con,db);

            setHandlers();

            idprod=0;lblprod.setText("Todos los productos");
            idest=0;lblest.setText("Todos los estados");

            P_productoObj.fill();
            P_barril_tipoObj.fill();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        try {
            long ff=du.getActDate();ff=du.addDays(ff,30);
            db.execSQL("DELETE FROM D_barril WHERE (ACTIVO=-99) AND (FECHA_CIERRE<"+ff+")");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doAdd(View view) {
        try {
            gl.gcods="";
            startActivity(new Intent(this, MantBarril.class));
        } catch (Exception e) {
            String ee=e.getMessage();
        }
    }

    public void doProd(View view) {
        listProd();
    }

    public void doEst(View view) {
        listEstado();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsD_barril item = (clsClasses.clsD_barril)lvObj;

                adapter.setSelectedIndex(position);
                gl.gcods=item.codigo_barril;
                startActivity(new Intent(ListaBarril.this, MantBarril.class));
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        clsClasses.clsD_barril item;

        try {
            if (idprod==0) {
                sql="WHERE (ACTIVO<>-99) ";
            } else {
                sql="WHERE (ACTIVO<>-99) AND (CODIGO_PRODUCTO="+idprod+") ";
            }

            switch (idest) {
                case 1:
                    sql+=" AND (ACTIVO=1)  ";break;
                case 2:
                    sql+=" AND (ACTIVO=-1) ";break;
                case 3:
                    sql+=" AND (ACTIVO=0)  ";break;
            }

            D_barrilObj.fill(sql);

            for (int i = 0; i <D_barrilObj.count; i++) {
                item=D_barrilObj.items.get(i);

                item.nprod=nombreProd(item.codigo_producto);
                item.ntipo=nombreTipo(item.codigo_tipo);
                item.nest=nombreEst(item.activo);
                item.nfecha=du.sfecha(item.fecha_vence);
            }

            Collections.sort(D_barrilObj.items, new ItemComparator());

            adapter=new LA_D_barril(this,this,D_barrilObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblreg.setText("Registros: "+D_barrilObj.count);
    }

    //endregion

    //region Aux

    private void listProd() {
        try {
            P_productoObj.fill("WHERE (CODIGO_TIPO='B') AND (ACTIVO=1) ORDER BY DESCCORTA");

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ListaBarril.this,"Producto");

            listdlg.add(0,"Todos los productos");
            for (int i = 0; i <P_productoObj.count; i++) {
                listdlg.add(P_productoObj.items.get(i).codigo_producto,P_productoObj.items.get(i).desccorta);
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idprod=listdlg.getCodigoInt(position);
                        listItems();
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

    private void listEstado() {
        try {

            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(ListaBarril.this,"Estado");

            listdlg.add(0,"Todos los estados");
            listdlg.add(1,"Abierto");
            listdlg.add(2,"Lleno");
            listdlg.add(3,"Vacio");

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        idest=listdlg.getCodigoInt(position);
                        listItems();
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