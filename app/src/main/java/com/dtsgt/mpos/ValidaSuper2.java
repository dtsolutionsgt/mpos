package com.dtsgt.mpos;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Login;

import java.util.ArrayList;

public class ValidaSuper2 extends PBase {

    private ListView listView;
    private EditText txt1;

    private LA_Login adapter;
    private ArrayList<clsClasses.clsMenu> mitems = new ArrayList<clsClasses.clsMenu>();

    private clsVendedoresObj VendedoresObj;

    private String usr="",pwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valida_super2);

        super.InitBase();

    }

}