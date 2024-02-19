package com.dtsgt.mpos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.ladapt.LA_P_producto;

public class ProductoLista extends PBase {

    private ListView listView;
    private EditText txtflt;
    private TextView lblreg;

    private LA_P_producto adapter;
    private clsP_productoObj P_productoObj;

    private boolean idle=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_producto_lista);

            super.InitBase();

            listView = findViewById(R.id.listView1);
            txtflt = findViewById(R.id.editTextText2);
            lblreg = findViewById(R.id.textView331);lblreg.setText("Encontrado: "+0);

            P_productoObj=new clsP_productoObj(this,Con,db);

            setHandlers();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }



    //region Events

    public void doExit(View view) {
        finish();
    }

    public void doClear(View view) {
        txtflt.setText("");
    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsP_producto item = (clsClasses.clsP_producto) lvObj;

                    adapter.setSelectedIndex(position);
                    gl.gstr = item.codigo;
                    finish();
                } catch (Exception e) {
                }
            });

            txtflt.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listItems();
                }
            });

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void listItems() {

        if (!idle) return;

        idle=false;
        lblreg.setText("Encontrado: "+0);


        try {
            String flt=txtflt.getText().toString().toUpperCase();

            if (flt.isEmpty()) {
                P_productoObj.fill();
            } else {
                sql="WHERE (UPPER(CODIGO) LIKE '%"+flt+"%' ) OR (UPPER(DESCCORTA) LIKE '%"+flt+"%') ORDER BY DESCCORTA LIMIT 10";
                P_productoObj.fill(sql);
            }

            adapter=new LA_P_producto(this,this,P_productoObj.items);
            listView.setAdapter(adapter);

            lblreg.setText("Encontrado: "+P_productoObj.items.size());
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
        idle=true;
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    public void onResume() {
        super.onResume();
        try {
            P_productoObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

}