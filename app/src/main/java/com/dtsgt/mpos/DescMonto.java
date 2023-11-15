package com.dtsgt.mpos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsKeybHandler;

public class DescMonto extends PBase {

    private TextView lbl1,lblKeyDP,lbltit,lblperc;

    private clsKeybHandler khand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_desc_monto);

            super.InitBase();

            lbl1 = findViewById(R.id.lblPass);
            lblKeyDP = findViewById(R.id.textView110);
            lbltit = findViewById(R.id.lblTit);
            lblperc = findViewById(R.id.textView328);

            if (gl.peDescPerc) {
                lbltit.setText("PORCENTAJE DESCUENTO ADICIONAL");lblperc.setVisibility(View.VISIBLE);
            } else {
                lbltit.setText("MONTO DESCUENTO ADICIONAL");lblperc.setVisibility(View.INVISIBLE);
            }

            khand = new clsKeybHandler(this, lbl1, lblKeyDP);
            khand.enable();
            khand.clear(true);

            gl.desc_monto=-1;

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Monto incorrecto");
            } else {
                if (khand.isValid) {
                    if (khand.value<0) {
                        msgbox("Descuento incorrecto.");return;
                    }

                    if (gl.peDescPerc) {
                        if (khand.value >= 100) {
                            msgbox("Porcentaje de descuento incorrecto.");
                        } else {
                            gl.desc_monto = khand.value;
                            finish();
                        }
                    } else {
                        if (khand.value >= gl.total_factura_previo_descuento) {
                            msgbox("Monto de descuento superior al total de la factura.");
                        } else {
                            gl.desc_monto = khand.value;
                            finish();
                        }
                    }


                }
            }
        }
    }

    private void msgMonto(String msg) {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage(msg  );

            dialog.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });

            dialog.show();
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }


}