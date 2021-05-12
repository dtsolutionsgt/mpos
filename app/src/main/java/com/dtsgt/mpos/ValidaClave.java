package com.dtsgt.mpos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsKeybHandler;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Login;

import java.util.ArrayList;

public class ValidaClave extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lblKeyDP;

    private LA_Login adapter;
    private ArrayList<clsClasses.clsMenu> mitems = new ArrayList<clsClasses.clsMenu>();
    private clsClasses.clsMenu selitem;
    private clsKeybHandler khand;

    private String usr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valida_clave);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        lbl1 = findViewById(R.id.lblPass);
        lbl2 = findViewById(R.id.lblTit);
        lblKeyDP = findViewById(R.id.textView110);

        khand = new clsKeybHandler(this, lbl1, lblKeyDP);
        khand.enable();
        khand.clear(false);

        setHandlers();

        if (gl.modoclave == 0) lbl2.setText("Ingreso mesero"); else lbl2.setText("Ingreso caja");

        listItems();

    }


    //region Events

    public void doKey(View view) {
        khand.handleKey(view.getTag().toString());
        if (khand.isEnter) {
            if (khand.val.isEmpty()) {
                toast("Clave incorrecta");
            } else {
                if (khand.isValid) validaClave(khand.val);
            }
        }
    }

    private void setHandlers() {

        try {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        Object lvObj = listView.getItemAtPosition(position);
                        selitem = (clsClasses.clsMenu) lvObj;

                        adapter.setSelectedIndex(position);
                        usr = selitem.Cod;
                    } catch (Exception e) {
                       mu.msgbox(e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

    }

    //endregion

    //region Main

    private void listItems() {
        clsVendedoresObj VendedoresObj = new clsVendedoresObj(this, Con, db);
        clsClasses.clsMenu item;
        int nivel;

        try {
            mitems.clear();

            if (gl.modoclave == 0) nivel=4; else nivel=5;

            sql = "WHERE CODIGO_VENDEDOR IN (SELECT VENDEDORES.CODIGO_VENDEDOR " +
                    "FROM VENDEDORES INNER JOIN P_RUTA ON VENDEDORES.RUTA=P_RUTA.CODIGO_RUTA " +
                    "WHERE (P_RUTA.SUCURSAL=" + gl.tienda + ") AND (VENDEDORES.NIVEL="+nivel+")) ORDER BY VENDEDORES.NOMBRE";
            VendedoresObj.fill(sql);

            for (int i = 0; i < VendedoresObj.count; i++) {
                item = clsCls.new clsMenu();
                item.Cod = VendedoresObj.items.get(i).codigo;
                item.Name = item.Cod+" - "+VendedoresObj.items.get(i).nombre;
                item.Pass = VendedoresObj.items.get(i).clave;
                mitems.add(item);
            }

            adapter = new LA_Login(this, mitems);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            msgbox(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    private void validaClave(String clave) {
        if (usr.isEmpty()) {
            toast("Debe seleccionar un usuario");return;
        }



    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion

}