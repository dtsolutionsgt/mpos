package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;

public class CierreInicio extends PBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cierre_inicio);

            super.InitBase();

            gl.cierre_ini_flag=-1;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    //region Events

    public void doCierre(View view) {
        gl.cierre_ini_flag=1;
        finish();
    }

    public void doReporte(View view) {
        gl.cierre_ini_flag=2;
        finish();
    }

    public void doExit(View view) {
        gl.cierre_ini_flag=-1;
        finish();
    }

    //endregion

    //region Main


    //endregion

    //region Dialogs


    //endregion

    //region Aux


    //endregion

    //region Activity Events


    //endregion

}