package com.dtsgt.mpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidodObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.ladapt.LA_D_pedidod;
import com.dtsgt.webservice.srvPedidoEstado;

public class PedidoDet extends PBase {

    private ListView listView;
    private TextView lblID,lblAnul;
    private RelativeLayout rel1,rel2,rel3,rel4;

    private LA_D_pedidod adapter;
    private clsD_pedidodObj D_pedidodObj;
    private clsD_pedidoObj D_pedidoObj;
    private clsP_productoObj P_productoObj;
    private clsT_ventaObj T_ventaObj;
    private clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
    private clsClasses.clsT_venta venta=clsCls.new clsT_venta();

    private String pedid;
    private int est,modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_det);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        rel1=findViewById(R.id.rel01);
        rel2=findViewById(R.id.rel02);
        rel3=findViewById(R.id.rel03);
        rel4=findViewById(R.id.rel04);
        lblID=findViewById(R.id.textView190);
        lblAnul=findViewById(R.id.textView191);

        pedid=gl.pedid;

        D_pedidodObj=new clsD_pedidodObj(this,Con,db);
        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);P_productoObj.fill();
        T_ventaObj=new clsT_ventaObj(this,Con,db);

        setHandlers();

        loadItem();
        listItems();
    }

    //region Events

    public void doNuevo(View view) {
        modo=1;
        msgAsk("Marcar como Nuevo");
    }

    public void doPend(View view) {
        modo=2;
        msgAsk("Marcar como Pendiente");
    }

    public void doComp(View view) {
        modo=3;
        msgAsk("Marcar como Completo");
    }

    public void doEnt(View view) {
        modo=4;
        msgAsk("Marcar como Entregado");
    }

    public void doCliente(View view) {
        startActivity(new Intent(this,PedidoCli.class));
    }

    public void doAnul(View view) {
        msgAskAnul("Anular orden");
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsD_pedidod item = (clsClasses.clsD_pedidod)lvObj;

                adapter.setSelectedIndex(position);
                /*
                String sn=item.nota;String s=item.umventa;
                if (!sn.isEmpty() && sn.length()>3) s=s+"\n"+sn;
                bigtoast(s);
                */
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        String s;
        clsClasses.clsD_pedidod item;

        try {
            D_pedidodObj.fill("WHERE Corel='"+pedid+"'");

            for (int i = 0; i <D_pedidodObj.count; i++) {
                item=D_pedidodObj.items.get(i);
                s=mu.frmdecno(item.cant)+" x "+getProd(item.codigo_producto);
                item.umventa=s;
            }

            adapter=new LA_D_pedidod(this,this,D_pedidodObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void loadItem() {

        D_pedidoObj.fill("WHERE Corel='"+pedid+"'");
        item=D_pedidoObj.first();

        est=1;
        if (item.codigo_usuario_creo>0) est=2;
        if (item.codigo_usuario_proceso>0) est=3;
        if (item.fecha_salida_suc>0) est=4;
        if (item.anulado==1) est=5;

        if (est==1) rel1.setBackgroundResource(R.drawable.frame_key);
        if (est==2) rel2.setBackgroundResource(R.drawable.frame_key);
        if (est==3) rel3.setBackgroundResource(R.drawable.frame_key);
        if (est==4) rel4.setBackgroundResource(R.drawable.frame_key);

        if (item.empresa>0) lblID.setText("#"+item.empresa % 1000);else lblID.setText("");

        if (item.anulado==1) lblAnul.setVisibility(View.INVISIBLE);
    }

    private void estado() {
        int ordid=1;

        D_pedidoObj.fill("ORDER BY Empresa DESC");
        if (D_pedidoObj.count>0) ordid=D_pedidoObj.first().empresa+1;

        if (modo==1) {
            item.codigo_usuario_creo=0;
            item.codigo_usuario_proceso=0;
            item.fecha_salida_suc=0;
            item.anulado=0;
        }
        if (modo==2) {
            item.empresa=ordid;
            item.codigo_usuario_creo=gl.codigo_vendedor;
            item.codigo_usuario_proceso=0;
            item.fecha_salida_suc=0;
            item.anulado=0;
        }
        if (modo==3) {
            item.codigo_usuario_proceso=gl.codigo_vendedor;
            item.fecha_salida_suc=0;
            item.anulado=0;
        }
        if (modo==4) {
            item.fecha_salida_suc=du.getActDateTime();
            item.anulado=0;
        }

        try {
            D_pedidoObj.update(item);

            Intent intent = new Intent(PedidoDet.this, srvPedidoEstado.class);

            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("correlativo",item.corel);
            intent.putExtra("estado_pedido",modo);
            intent.putExtra("valor_estado",gl.codigo_vendedor);

            startService(intent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        if (modo==2) imprimirOrden();

        if (modo==3) {
            if (ventaVacia()) {
                crearVenta();
            } else {
                msgAskVenta("Borra la venta actual y convertir orden a una venta nueva");
            }
            return;
        }

        finish();

    }

    private void anular() {
       try {
            item.anulado=1;
            D_pedidoObj.update(item);

            Intent intent = new Intent(PedidoDet.this, srvPedidoEstado.class);

            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("correlativo",item.corel);
            intent.putExtra("estado_pedido",5);
            intent.putExtra("valor_estado",1);

            startService(intent);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void imprimirOrden() {
        gl.closePedido=true;
        finish();
    }

    //endregion

    //region Venta

    private void crearVenta() {
        String sql;

        try {

            clsClasses.clsD_pedidod item;

            try {
                sql="SELECT COREL, 0 AS COREL_DET, CODIGO_PRODUCTO, UMVENTA, SUM(CANT) AS Expr1, SUM(TOTAL) AS Expr2,'' AS NOTA,'' AS CODIGO_TIPO_PRODUCTO " +
                    "FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO='P') " +
                    "GROUP BY COREL, CODIGO_PRODUCTO, UMVENTA";
                D_pedidodObj.fillSelect(sql);

                for (int i = 0; i <D_pedidodObj.count; i++) {
                    item=D_pedidodObj.items.get(i);
                }

             } catch (Exception e) {
                mu.msgbox(e.getMessage());
            }


            //gl.closePedido=true;
            //finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    private String getProd(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) return P_productoObj.items.get(i).desclarga;
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

    private void bigtoast(String msg) {
        try {
            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            RelativeLayout toastLayout = (RelativeLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toastTV.setTypeface(null, Typeface.BOLD);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean ventaVacia() {
        Cursor dt;

        try {
            sql="SELECT * FROM T_VENTA";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void borrarVenta() {
        try {
            sql="DELETE FROM T_VENTA";
            db.execSQL(sql);

            sql="DELETE FROM T_COMBO";
            db.execSQL(sql);

            sql="DELETE FROM T_PRODMENU";
            db.execSQL(sql);

            sql="DELETE FROM T_PAGO";
            db.execSQL(sql);

            gl.iniciaVenta=true;

            crearVenta();

        } catch (SQLException e) {
            mu.msgbox("Error : " + e.getMessage());
        }
      }

    //endregion

    //region Dialogs

    private void msgAsk(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                estado();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

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
                anular();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskVenta(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                borrarVenta();
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
    protected void onResume() {
        super.onResume();
        try {
            D_pedidoObj.reconnect(Con,db);
            D_pedidodObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}