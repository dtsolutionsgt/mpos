package com.dtsgt.classes;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class clsDocCajaPagos  extends clsDocument {

    private ArrayList<itemData> items= new ArrayList<itemData>();

    private int modo,totitem;
    private double totb,totm;

    public clsDocCajaPagos(Context context, int printwidth,String pnombre,String pruta,String pvend,String cursymbol,int decimpres, String archivo) {
        super(context, printwidth,cursymbol,decimpres, archivo);
        docfactura=false;

        nombre="PAGOS DE CAJA";
        numero="";
        serie="";
        ruta=pruta;
        vendedor=pvend;
        nombre_cliente ="";
        fsfecha=DU.getActDateStr();
    }

    protected boolean loadHeadData(String corel) {
        Cursor DT;
        itemData item;

        items.clear();

        try {
            sql="SELECT C.COREL,C.OBSERVACION,C.MONTO,P.NOMBRE,T.NOMBRE,C.NODOCUMENTO,C.FECHA " +
                    "FROM P_PROVEEDOR P INNER JOIN P_CAJAPAGOS C ON C.PROVEEDOR = P.CODIGO " +
                    "INNER JOIN P_CONCEPTOPAGO T ON C.TIPO = T.CODIGO " +
                    "WHERE (C.COREL='"+corel+"')";
            DT=Con.OpenDT(sql);
            DT.moveToFirst();

            while (!DT.isAfterLast()) {

                item =new itemData();

                item.corel=DT.getString(0);
                item.desc=DT.getString(1);

                item.monto=DT.getDouble(2);
                item.prov=DT.getString(3);
                item.cpago=DT.getString(4);
                item.noDoc=DT.getString(5);
                item.fecha=DU.univfechaReport(DT.getInt(6));

                items.add(item);
                DT.moveToNext();
            }
            return true;
        } catch (Exception e) {
            Toast.makeText(cont,e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    protected boolean buildDetail() {
        itemData item;

        rep.addc("REIMPRESION");
        rep.addc("PAGOS DE CAJA");

        rep.line();

        totitem=items.size();
        totb=0;totm=0;

        for (int i = 0; i <items.size(); i++) {

            item=items.get(i);

            rep.add("Fecha: "+DU.getActDateStr());
            rep.add("Proveedor: "+item.prov);
            rep.add("Concepto Pago: "+item.cpago);
            rep.add("Fecha Inicio Caja: "+item.fecha);
            rep.empty();

            rep.add("DESCRIPCION");
            rep.add("DOC ASOCIADO                   TOTAL");
            rep.line();
            rep.add(item.desc);
            rep.addtot(item.noDoc,item.monto);
            rep.line();
            rep.addtot("Total: ",item.monto);
        }

        rep.line();

        return true;
    }

    protected boolean buildFooter() {
        return super.buildFooter();
    }

    private class itemData {
        public String corel,desc,prov,cpago,noDoc,fecha;
        public double monto;
    }

}
