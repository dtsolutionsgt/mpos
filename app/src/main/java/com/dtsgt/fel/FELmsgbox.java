package com.dtsgt.fel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class FELmsgbox extends PBase {

    private TextView lbl1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_e_lmsgbox);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView169);
        lbl1.setText(gl.FELmsg);

    }

    public void doExit(View view) {
        finish();
    }

}