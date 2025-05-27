package com.dtsgt.mpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.ladapt.ListAdaptProd;

import java.util.ArrayList;

public class ProdExist extends PBase {

    private ListView listView;
    private EditText txtFilter;

    private ArrayList<clsClasses.clsCD> items;
    private ListAdaptProd adapter;

    private String itemid,prname;
    boolean ordPorNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prod_exist);

            super.InitBase();

            listView = (ListView) findViewById(R.id.listView1);
            txtFilter = (EditText) findViewById(R.id.editText1);

            ordPorNombre=true;

            app = new AppMethods(this, gl, Con, db);
            items = new ArrayList<clsClasses.clsCD>();

            setHandlers();

            listItems();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void porCodigo(View view) {
        try{
            ordPorNombre=false;
            listItems();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    public void porNombre(View view) {
        try{
            ordPorNombre=true;
            listItems();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    private void setHandlers() {
        try {

            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    Object lvObj = listView.getItemAtPosition(position);
                    clsClasses.clsCD item = (clsClasses.clsCD) lvObj;

                    adapter.setSelectedIndex(position);

                    gl.pprodname = item.Desc;
                    gl.prodcod = item.codInt;

                    msgAskExist();
                } catch (Exception e) {
                    mu.msgbox(e.getMessage());
                }
            });

            txtFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int tl;

                    tl = txtFilter.getText().toString().length();

                    if (tl == 0 || tl > 1) {
                        listItems();
                    }
                }
            });

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Main

    private void listItems() {
        Cursor DT;
        clsClasses.clsCD vItem;
        String vF,cod,name,sql="";

        items.clear();

        try {
            vF=txtFilter.getText().toString().replace("'","");

            sql="SELECT  CODIGO, DESCCORTA, ' ', ACTIVO, CODIGO_PRODUCTO, 0  " +
                "FROM P_PRODUCTO WHERE (CODIGO_TIPO ='P') AND (ACTIVO=1) ";
            if (vF.length()>0) sql=sql+"AND ((DESCCORTA LIKE '%"+vF+"%') OR (CODIGO LIKE '%"+vF+"%')) ";
            if (ordPorNombre) sql += "ORDER BY DESCCORTA"; else sql+="ORDER BY CODIGO";
            DT=Con.OpenDT(sql);

            if (DT.getCount()>0) {

                DT.moveToFirst();
                while (!DT.isAfterLast()) {
                    cod = DT.getString(0);
                    name = DT.getString(1);

                    vItem = clsCls.new clsCD();

                    vItem.Cod = cod;
                    vItem.prec=" ";
                    vItem.Desc = name;
                    vItem.codInt=DT.getInt(4);
                    vItem.costo = DT.getDouble(5);
                    vItem.Text="";

                    items.add(vItem);

                    DT.moveToNext();
                }
            }

            if (DT!=null) DT.close();

            adapter=new ListAdaptProd(this,items);
            listView.setAdapter(adapter);

        } catch (Exception e){
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Dialogs

    private void msgAskExist() {
        try{

            ExDialog dialog = new ExDialog(this);
            dialog.setMessage("Buscar existencias ?");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ProdExist.this,ProdExistList.class));
                }
            });

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    //endregion

    //region Aux

    public String ltrim(String ss,int sw) {

        try{

            int l=ss.length();
            if (l>sw) {
                ss=ss.substring(0,sw);
            } else {
                String frmstr="%-"+sw+"s";
                ss=String.format(frmstr,ss);
            }


        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
        return ss;
    }

    public void limpiaFiltro(View view) {
        try{
            txtFilter.setText("");
        } catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }

    }

    //endregion

    //region Activity Events

    //endregion

}