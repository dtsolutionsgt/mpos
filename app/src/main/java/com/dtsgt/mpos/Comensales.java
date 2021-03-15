package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.classes.clsKeybHandler;

public class Comensales extends PBase {

    private TextView lbl1,lblKeyDP;

    private clsKeybHandler khand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comensales);

        super.InitBase();

        lbl1 = findViewById(R.id.lblPass);
        lblKeyDP = findViewById(R.id.textView110);

        khand = new clsKeybHandler(this, lbl1, lblKeyDP);
        khand.enable();
        khand.clear(false);

        gl.comensales=0;
    }

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Cantidad incorrecta");
            } else {
                if (khand.isValid) {
                    gl.comensales=(int) khand.value;finish();
                }
            }
        }
    }


}