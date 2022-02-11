package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.classes.clsKeybHandler;

public class DescMonto extends PBase {

    private TextView lbl1,lblKeyDP;

    private clsKeybHandler khand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_monto);

        super.InitBase();

        lbl1 = findViewById(R.id.lblPass);
        lblKeyDP = findViewById(R.id.textView110);

        khand = new clsKeybHandler(this, lbl1, lblKeyDP);
        khand.enable();
        khand.clear(true);

        gl.desc_monto=-1;
    }


    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Monto incorrecto");
            } else {
                if (khand.isValid) {
                    gl.desc_monto= khand.value;
                    finish();
                }
            }
        }
    }


}