package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_comboObj;

public class VentaEdit extends PBase {

    private TextView lbl1, lbl2, lbl3;

    private clsT_comboObj T_comboObj;
    private clsP_productoObj P_productoObj;

    private int cant, lcant, prodid, disp, dif;
    private String um;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_edit);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView93);
        lbl2 = (TextView) findViewById(R.id.textView117);
        lbl3 = (TextView) findViewById(R.id.textView116);

        cant = gl.retcant;
        lcant = gl.limcant;
        prodid = app.codigoProducto(gl.menuitemid);
        um = app.umVenta3(prodid);

        P_productoObj = new clsP_productoObj(this, Con, db);
        T_comboObj = new clsT_comboObj(this, Con, db);

        disp = dispProducto();

        lbl1.setText(gl.gstr);
        lbl2.setText("" + cant);
        lbl3.setText("Disponible : " + disp);

        if (!isProdStock(prodid)) lbl3.setVisibility(View.INVISIBLE);
    }


    //region Events

    public void doClose(View view) {
        gl.retcant = -1;
        finish();
    }

    public void doApply(View view) {
        dif = disp-cant;
        if (dif >= 0) {
            gl.retcant = cant;
            finish();
        } else {
            msgAskApply("El producto no tiene suficiente existencia.\nFalta : " + (-dif) + "\n¿Continuar?");
        }
    }

    public void doDec(View view) {
        if (cant > 0) cant--;
        lbl2.setText("" + cant);
        if (cant == 0) {
            gl.retcant = 0;
            finish();
        }
    }

    public void doInc(View view) {
        if (gl.tipoprodcod.equalsIgnoreCase("P")) {
            dif = disp - cant - 1;
            if (dif < 0) {
                msgAskLimit("El producto no tiene suficiente existencia.\nFalta : " + (-dif) + "\n¿Continuar?");
            } else {
                cant++;
            }
        } else {
            cant++;
        }
        lbl2.setText(""+cant);
    }

    public void doDelete(View view) {
        gl.retcant=0;
        finish();
    }

    //endregion

    //region Main


    //endregion

    //region Disponible



    private int dispProducto() {
        int cdisp, cstock, cbcombo;

        cstock=cantStock(prodid);
        cbcombo=cantProdCombo(prodid);
        cdisp=cstock-cbcombo;if (cdisp<0) cdisp=0;

        return cdisp;
    }

    private int cantStock(int prodid) {
        Cursor dt;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);

            sql="SELECT CANT FROM P_STOCK WHERE (CODIGO='"+P_productoObj.first().codigo+"') AND (UNIDADMEDIDA='"+P_productoObj.first().unidbas+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int cantProdCombo(int prodid) {
        Cursor dt;

        try {
            sql="SELECT SUM(CANT*UNID) FROM T_COMBO WHERE (IDSELECCION="+prodid+")";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isProdStock(int pid) {
        if (pid==0) return false;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+pid);
            return P_productoObj.first().codigo_tipo.equalsIgnoreCase("P");
        } catch (Exception e) {
            return false;
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs

    private void msgAskApply(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Aplicar");
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setNegativeButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.retcant=cant;
                     finish();
                }
            });

            dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void msgAskLimit(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle(R.string.app_name);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    cant++;
                    lbl2.setText(""+cant);
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
            P_productoObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}
