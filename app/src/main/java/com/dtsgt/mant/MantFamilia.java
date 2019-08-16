package com.dtsgt.mant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class MantFamilia extends PBase {

    private ImageView imgstat;
    private TextView lbl1;
    private EditText txt1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mant_familia);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView1);
        txt1 = (EditText) findViewById(R.id.editText1);
        imgstat = (ImageView) findViewById(R.id.imageView31);
    }


}
