package com.dtsgt.mant;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsListaObj;
import com.dtsgt.ladapt.LA_Lista;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Lista extends PBase {

    private ListView listView;
    private TextView lblTit,lblReg;
    private EditText txtFilter;

    private LA_Lista adapter;
    private clsListaObj ViewObj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        lblTit = (TextView) findViewById(R.id.lblTit);
        lblReg = (TextView) findViewById(R.id.textView85);
        txtFilter = (EditText) findViewById(R.id.txtFilter);

        ViewObj=new clsListaObj(this,Con,db);

        setMantID();

        setHandlers();

        listItems();

    }

    //region Events

    public void doClear(View view) {
        txtFilter.setText("");
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsLista item = (clsClasses.clsLista) lvObj;

                adapter.setSelectedIndex(position);
            }

            ;
        });

        txtFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tl = txtFilter.getText().toString().length();
                if (tl == 0 || tl > 1) listItems();
            }
        });
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            lblReg.setText("Registros : 0");
            setTableSQL();

            ViewObj.fillSelect(sql);
            adapter=new LA_Lista(this,this,ViewObj.items);
            listView.setAdapter(adapter);

            lblReg.setText("Registros : "+ViewObj.count);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void setTableSQL() {
        String ft=txtFilter.getText().toString();
        boolean flag=!ft.isEmpty();

        sql="";

        switch (gl.mantid) {
            case 0:
                lblTit.setText("Almacen");break;
            case 1:
                lblTit.setText("Bancos");break;
            case 2:
                lblTit.setText("Clientes");break;
            case 3://Familia
                lblTit.setText("Empresas");break;
            case 4:
                lblTit.setText("");
                sql="SELECT 0,CODIGO,NOMBRE,'','', '','','','' FROM P_LINEA ";
                if (flag)  sql+="WHERE (CODIGO='"+ft+"') OR (NOMBRE LIKE '%"+ft+"%')";
                sql+="ORDER BY NOMBRE";
                break;
            case 5:
                lblTit.setText("Forma pago");break;
            case 6:
                lblTit.setText("Impuestos");break;
            case 7:
                lblTit.setText("Moneda");break;
            case 8:
                lblTit.setText("Productos");break;
            case 9:
                lblTit.setText("Proveedores");break;
            case 10:
                lblTit.setText("Usuarios");break;
            case 11:
                lblTit.setText("Vendedores");break;
        }
    }


    //endregion

    //region Aux

    private void setMantID() {

        switch (gl.mantid) {
            case 0:
                lblTit.setText("Almacen");break;
            case 1:
                lblTit.setText("Bancos");break;
            case 2:
                lblTit.setText("Clientes");break;
            case 3:
                lblTit.setText("Empresas");break;
            case 4:
                lblTit.setText("Familia");break;
            case 5:
                lblTit.setText("Forma pago");break;
            case 6:
                lblTit.setText("Impuestos");break;
            case 7:
                lblTit.setText("Moneda");break;
            case 8:
                lblTit.setText("Productos");break;
            case 9:
                lblTit.setText("Proveedores");break;
            case 10:
                lblTit.setText("Usuarios");break;
            case 11:
                lblTit.setText("Vendedores");break;
        }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ViewObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    //endregion


}
