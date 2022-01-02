package com.dtsgt.mpos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.dtsgt.classes.clsD_MovDObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.ladapt.LA_D_movd;

public class InvVista extends PBase {

    private ListView listView;
    private TextView lblTCant,lblTCosto,lblTit;

    private LA_D_movd adapter;

    private clsD_MovDObj D_movdObj;
    private clsP_productoObj P_productoObj;

    private double cantt,costot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_vista);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView1);
        lblTit = (TextView) findViewById(R.id.lblTit3);
        lblTCant = (TextView) findViewById(R.id.textView155);
        lblTCosto = (TextView) findViewById(R.id.textView150);

        if (gl.tipo==0) lblTit.setText("Ingresos");
        if (gl.tipo==1) lblTit.setText("Salidas");
        if (gl.tipo==2) lblTit.setText("Inventario Inicial");

        D_movdObj=new clsD_MovDObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);

        listItems();
    }

    //region Events


    //endregion

    //region Main

    private void listItems() {
        double tc,can;
        int prid;

        selidx=-1;

        costot=0;cantt=0;

        try {
            D_movdObj.fill("WHERE Corel='"+gl.idmov+"'");

            for (int i = 0; i <D_movdObj.count; i++) {
                can=D_movdObj.items.get(i).cant;cantt+=can;
                tc=can*D_movdObj.items.get(i).precio; costot+=tc;
                D_movdObj.items.get(i).pesom=tc;

                prid=D_movdObj.items.get(i).producto;
                P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prid);
                try {
                    D_movdObj.items.get(i).lote=P_productoObj.first().desclarga;
                } catch (Exception e) {
                    D_movdObj.items.get(i).lote="";
                    msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
                }

            }

            adapter=new LA_D_movd(this,this,D_movdObj.items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }

        lblTCant.setText("Articulos : "+mu.frmint(cantt));
        lblTCosto.setText("Costo : "+mu.frmcur(costot));
    }

    //endregion


    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    //endregion

}