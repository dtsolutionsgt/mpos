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
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_f_e_lmsgbox);

            super.InitBase();

            lbl1 = (TextView) findViewById(R.id.textView169);

            String fm=gl.FELmsg;
            if (fm.length()>150) fm=fm.substring(0,149)+"...";

            lbl1.setText(fm);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public void doExit(View view) {
        finish();
    }

}