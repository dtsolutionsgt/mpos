package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DomEntrega extends PBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dom_entrega);

            super.InitBase();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


}