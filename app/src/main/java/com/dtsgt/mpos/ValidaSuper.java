package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsVendedoresObj;
import com.dtsgt.ladapt.LA_Login;

import java.util.ArrayList;

public class ValidaSuper extends PBase {

    private ListView listView;
    private EditText txt1;

    private LA_Login adapter;
    private ArrayList<clsClasses.clsMenu> mitems = new ArrayList<clsClasses.clsMenu>();

    private clsVendedoresObj VendedoresObj;

    private String usr="",pwd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valida_super);

        super.InitBase();

        listView = findViewById(R.id.listView1);
        txt1= findViewById(R.id.txt1);

        gl.checksuper=false;

        VendedoresObj=new clsVendedoresObj(this,Con,db);

        llenaUsuarios();

        setHandlers();
        txt1.requestFocus();
    }

    //region Events

    public void doCheck(View view) {
        if (usr.isEmpty()) {
            msgbox("Falta seleccionar un usuario");return;
        }
        pwd=txt1.getText().toString();
        if (pwd.isEmpty()) msgbox("Debe ingresar una contraseña"); else checkUser();
    }

    private void setHandlers() {
        try {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsMenu item = (clsClasses.clsMenu) lvObj;

                    adapter.setSelectedIndex(position);
                    usr = item.Cod;txt1.requestFocus();
                }
            });
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }


    //endregion

    //region Main

    public void llenaUsuarios() {
        clsClasses.clsMenu item;

        try {

            mitems.clear();
            VendedoresObj.fill("WHERE (RUTA=" + gl.codigo_ruta+") AND ((NIVEL=2) OR (NIVEL=3)) ORDER BY NOMBRE");

            for (int i = 0; i < VendedoresObj.count; i++) {
                item = clsCls.new clsMenu();
                item.Cod =""+VendedoresObj.items.get(i).codigo_vendedor;
                item.Name = item.Cod+" - "+VendedoresObj.items.get(i).nombre;// estaba .ruta #CKFK 20200517
                mitems.add(item);
            }

            adapter = new LA_Login(this, mitems);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void checkUser() {
         try {

            VendedoresObj.fill("WHERE CODIGO_VENDEDOR=" + usr);
            if (VendedoresObj.count>0) {
                if (pwd.equalsIgnoreCase(VendedoresObj.first().clave)) {
                    gl.checksuper=true;finish();
                }
            }
            msgbox("Contraseña incorrecta");
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            VendedoresObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }
    //endregion

}