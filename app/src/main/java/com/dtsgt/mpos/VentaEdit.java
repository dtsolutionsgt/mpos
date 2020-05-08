package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class VentaEdit extends PBase {

    private TextView lbl1,lbl2,lbl3;

    private int cant,lcant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_edit);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView93);
        lbl2 = (TextView) findViewById(R.id.textView117);
        lbl3 = (TextView) findViewById(R.id.textView116);

        cant=gl.retcant;lcant=gl.limcant;

        lbl1.setText(gl.gstr);
        lbl2.setText(""+cant);
        if (lcant>=0) lbl3.setText("Disp : "+lcant);else lbl3.setText("");

        if (gl.tipoprodcod.equalsIgnoreCase("S")) lbl3.setVisibility(View.INVISIBLE);
    }


    //region Events

    public void doClose(View view) {
        gl.retcant=-1;
        finish();
    }

    public void doApply(View view) {
        gl.retcant=cant;
        finish();
    }

    public void doDec(View view) {
        if (cant>0) cant--;
        lbl2.setText(""+cant);
        if (cant==0) {
            gl.retcant=0;
            finish();
        }
     }

    public void doInc(View view) {
      if (gl.tipoprodcod.equalsIgnoreCase("P")) {
            if (cant<lcant || lcant<0) {
                cant++;
            } else {
                msgAskLimit("El producto no tiene suficiente existencia.\n¿Continuar?");
            }
        } else if (gl.tipoprodcod.equalsIgnoreCase("S")) {
            cant++;
        } else if (gl.tipoprodcod.equalsIgnoreCase("M")) {

        }

        lbl2.setText(""+cant);

     }

    public void doDelete(View view) {
        //msgAskDelete("Eliminar articulo de la venta");
        gl.retcant=0;
        finish();
    }

    //endregion

    //region Main


    //endregion

    //region Aux


    //endregion

    //region Dialogs

    private void msgAskDelete(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Borrar");
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setNegativeButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.retcant=0;
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
                    cant--;
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


    //endregion

}
