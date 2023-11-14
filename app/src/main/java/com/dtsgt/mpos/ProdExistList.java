package com.dtsgt.mpos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsP_almacenObj;
import com.dtsgt.classes.clsP_sucursalObj;
import com.dtsgt.firebase.fbStock;
import com.dtsgt.ladapt.LA_exist_list;
import com.dtsgt.webservice.wsOpenDT;

import java.util.ArrayList;

public class ProdExistList extends PBase {

    private ListView listView;
    private TextView lbl1;

    private wsOpenDT wso;
    private fbStock fbs;
    private Runnable rnFbalmacenExistencia,rnbuildList;

    private LA_exist_list adapter;

    private ArrayList<clsClasses.clsT_exist_list> items= new ArrayList<clsClasses.clsT_exist_list>();
    private clsClasses.clsT_exist_list item;

    private int prodid,sucid,almid,tasks,taskpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prod_exist_list);

            super.InitBase();

            listView = (ListView) findViewById(R.id.listview1);
            lbl1 = (TextView) findViewById(R.id.textView325);
            lbl1.setText(gl.pprodname);

            prodid=gl.prodcod;

            app.getURL();

            fbs =new fbStock("Stock",gl.tienda);

            wso=new wsOpenDT(gl.wsurl);

            rnFbalmacenExistencia= () -> {FbalmacenExistencia();};
            rnbuildList= () -> {buildList();};

            listItems();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //region Events

    public void doExit(View view) {
        finish();
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            sql="SELECT P_SUCURSAL.CODIGO_SUCURSAL, P_SUCURSAL.DESCRIPCION, P_ALMACEN.CODIGO_ALMACEN, P_ALMACEN.NOMBRE " +
                "FROM  P_SUCURSAL INNER JOIN P_ALMACEN ON P_SUCURSAL.CODIGO_SUCURSAL=P_ALMACEN.CODIGO_SUCURSAL " +
                "AND   P_SUCURSAL.EMPRESA = P_ALMACEN.EMPRESA " +
                "WHERE (P_SUCURSAL.EMPRESA="+gl.emp+") AND (P_SUCURSAL.ACTIVO=1) AND (P_ALMACEN.ACTIVO=1) " +
                "ORDER BY P_SUCURSAL.DESCRIPCION,P_ALMACEN.NOMBRE";
            wso.execute(sql,rnbuildList);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildList() {
        Cursor dt;
        int succod=0;

        try {
            if (wso.errflag) throw new Exception(wso.error);

            items.clear();tasks=0;
            dt=wso.openDTCursor;

            if (dt.getCount()>0) {
                dt.moveToFirst();
                while (!dt.isAfterLast()) {

                    sucid=dt.getInt(0);
                    if (sucid!=succod) {
                        item=clsCls.new clsT_exist_list();

                        item.idsucursal=sucid;
                        item.idalmacen=0;
                        item.flag=0;
                        item.nombre=dt.getString(1);

                        items.add(item);succod=sucid;
                    }

                    item=clsCls.new clsT_exist_list();

                    item.idsucursal=sucid;
                    item.idalmacen=dt.getInt(2);
                    item.flag=1;
                    item.nombre=dt.getString(3);

                    items.add(item);


                    dt.moveToNext();
                }
            }

            adapter=new LA_exist_list(this,this,items);
            listView.setAdapter(adapter);

            tasks=items.size();taskpos=0;
            buscaExistencia();
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void buildListOld() {
       try {
            if (wso.errflag) throw new Exception(wso.error);

            items.clear();tasks=0;

            clsP_sucursalObj P_sucursalObj=new clsP_sucursalObj(this,Con,db);
            clsP_almacenObj P_almacenObj=new clsP_almacenObj(this,Con,db);

            P_sucursalObj.fill("WHERE (ACTIVO=1) ORDER BY DESCRIPCION");

            for (int s = 0; s <P_sucursalObj.count; s++) {

                sucid=P_sucursalObj.items.get(s).codigo_sucursal;

                item=clsCls.new clsT_exist_list();

                item.idsucursal=sucid;
                item.idalmacen=0;
                item.flag=0;
                item.nombre=P_sucursalObj.items.get(s).descripcion;

                items.add(item);

                P_almacenObj.fill("WHERE (CODIGO_SUCURSAL="+sucid+") AND (ACTIVO=1) ORDER BY NOMBRE");

                for (int a = 0; a <P_almacenObj.count; a++) {

                    item=clsCls.new clsT_exist_list();

                    item.idsucursal=sucid;
                    item.idalmacen=P_almacenObj.items.get(a).codigo_almacen;
                    item.flag=1;
                    item.nombre=P_almacenObj.items.get(a).nombre;

                    items.add(item);
                }
            }

            adapter=new LA_exist_list(this,this,items);
            listView.setAdapter(adapter);

            tasks=items.size();taskpos=0;
            buscaExistencia();
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void buscaExistencia() {
        try {
            if (taskpos>=tasks) {
                adapter.notifyDataSetChanged();
                return;
            }

            sucid=items.get(taskpos).idsucursal;
            almid=items.get(taskpos).idalmacen;

            if (items.get(taskpos).flag>0) {
                fbs.calculaTotal("/"+sucid+"/",almid,prodid,rnFbalmacenExistencia);
            } else {
                fbs.total=0;
                fbs.errflag=false;
                FbalmacenExistencia();
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void FbalmacenExistencia() {
        try {
            if (fbs.errflag) throw new Exception(fbs.error);

            if (fbs.total>0) {
                items.get(taskpos).flag=2;
                items.get(taskpos).nombre+=": "+mu.frmdecno(fbs.total);
            }

            taskpos++;
            buscaExistencia();

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
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