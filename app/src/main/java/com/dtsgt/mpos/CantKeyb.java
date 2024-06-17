package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.classes.clsKeybHandler;

public class CantKeyb extends PBase {

    private TextView lbl1,lblKeyDP,lbltit;

    private clsKeybHandler khand;

    private int cant,cantmax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cant_keyb);

            super.InitBase();

            lbl1 = findViewById(R.id.lblPass);
            lblKeyDP = findViewById(R.id.textView110);
            lbltit = findViewById(R.id.lblTit);

            khand = new clsKeybHandler(this, lbl1, lblKeyDP);
            khand.enable();
            khand.clear(false);

            cant=gl.set_cant;cantmax=gl.set_cant_max;gl.set_cant=-1;
            lbltit.setText("Cantidad máxima: "+cantmax);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Cantidad incorrecta");
            } else {
                if (khand.isValid) {
                    if (khand.value>cantmax) {
                        msgbox("Cantidad superior al maxima.");
                    } else {
                        gl.set_cant =(int) khand.value;
                        finish();
                    }
                }
            }
        }
    }

}