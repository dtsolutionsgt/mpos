package com.dtsgt.classes;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class clsDocCanastaBod extends clsDocument {

    private ArrayList<itemData> items= new ArrayList<itemData>();

    private double tot,desc,imp,stot,percep;
    private boolean sinimp;
    private String 	contrib,recfact,estadoDev,corelNC,corelF,asignacion,s1;
    private int totitems, totcant;

    public String vTipo;
    public clsDocCanastaBod(Context context, int printwidth, String cursym, int decimpres, String archivo) {
        super(context, printwidth, cursym, decimpres, archivo);
        docpedido=false;
        docfactura=false;
        docrecibo=false;
        docdevolucion=false;
        doccanastabod=true;
    }

    protected boolean loadHeadData(String corel) {
        Cursor DT;
        int cntimpres;
        String val, vend;

        super.loadHeadData(corel);

        try {
            sql="SELECT COREL, RUTA, TIPO, REFERENCIA, USUARIO, FECHA, IMPRES "+
                "FROM D_MOV "+
                "WHERE ANULADO =  'N' AND TIPO='D' " ;

            DT=Con.OpenDT(sql);

            if (DT.getCount()>0)DT.moveToFirst();

            cntimpres=DT.getInt(6);

            if (cntimpres>0){
                if(vTipo.equals("CANASTA"))  nombre="COPIA DE DEVOLUCION DE CANASTA";
                if(vTipo.equals("PASEANTE"))  nombre="COPIA DE DEVOLUCION DE BODEGA";
            }else{
                if(vTipo.equals("CANASTA"))  nombre="DEVOLUCION DE CANASTA";
                if(vTipo.equals("PASEANTE"))  nombre="DEVOLUCION DE BODEGA";
            }

            serie=DT.getString(0);
            ruta=DT.getString(1);

            tipo=DT.getString(2);
            ref=DT.getString(3);
            vend=DT.getString(4);//#CKFK 20200516 Validar si se guarda el codigo o el codigo_vendedor

            ffecha=DT.getInt(5);
            fsfecha=sfecha(ffecha);

            corel=serie;
        } catch (Exception e) {
            Toast.makeText(cont,"loadHeadData"+e.getMessage(), Toast.LENGTH_SHORT).show();return false;
        }


        vendcod=vend;
        val=vend;
        vendedor=val; //#CKFK 20200516 Validar para que se hace esto

        return true;

    }

    protected boolean loadDocData(String corel) {
        Cursor DTs;
        itemData item;
        String ss;
        String cod="";
        loadHeadData(corel);

        items.clear();

        try {

            if(vTipo.equals("CANASTA")) {
                sql="SELECT M.PRODUCTO,P.DESCLARGA,M.CANT,M.PESO,M.LOTE,M.UNIDADMEDIDA "+
                    "FROM D_MOVDCAN M INNER JOIN P_PRODUCTO P ON M.PRODUCTO=P.CODIGO "+
                    "WHERE M.COREL='"+corel+"'";
            }else if(vTipo.equals("PASEANTE")) {
                sql="SELECT M.PRODUCTO,P.DESCCORTA,M.CANT,M.CANTM,M.UNIDADMEDIDA " +
                        "FROM D_MOVD M " +
                        "INNER JOIN P_PRODUCTO P ON M.PRODUCTO=P.CODIGO " +
                        "WHERE M.COREL='"+corel+"'";
            }

            DTs=Con.OpenDT(sql);

            if (DTs.getCount()>0)DTs.moveToFirst();

            totitems = DTs.getCount();

            while (!DTs.isAfterLast()) {

                ss=DTs.getString(0);

                if (!emptystr(ss)){

                    item =new itemData();

                    item.cod= ss;
                    item.nombre=DTs.getString(1);
                    item.cant=DTs.getDouble(2);
                    item.peso=DTs.getDouble(3);
                    item.um=DTs.getString(4);

                    items.add(item);

                }

                DTs.moveToNext();
            }

        } catch (Exception e) {

        }

        return true;
    }

    protected boolean buildDetail() {
        rep.add("");

        if(modofact.equalsIgnoreCase("TOL")) detailToledano();
        if(modofact.equalsIgnoreCase("*")) detailToledano();

        return true;
    }

    protected boolean detailToledano() {
        itemData item;
        String ss;
        tot=0;totcant=0;stot=0;

        rep.add("");
        rep.add("CODIGO   DESCRIPCION");
        rep.add("UM       CANTIDAD          ESTADO");
        rep.line();

        for (int i = 0; i <items.size(); i++) {
            item=items.get(i);

            if(item.cant>0){
                ss=rep.ltrim(item.cod+"      "+item.nombre,prw);
                rep.add(ss);
                ss=rep.rtrim(item.um,2)+"       "+rep.rtrim(""+item.cant,4);
                ss=ss+" "+rep.rtrim("BUENO",18);
                rep.add(ss);
                tot+=item.cant;
            }

            if(item.peso>0){
                ss=rep.ltrim(item.cod+"      "+item.nombre,prw-10);
                rep.add(ss);
                ss=rep.rtrim(item.um,2)+"       "+rep.rtrim(""+item.peso,4);
                ss=ss+" "+rep.rtrim("MALO",18);
                rep.add(ss);
                stot+=item.peso;
            }

            totcant += item.cant+item.peso;
        }

        rep.line();
        ss=rep.rtrim("", 6)+" "+rep.rtrim(frmdecimal(totcant,2),8);
        /*ss=rep.ltrim(ss,prw-10);
        ss=ss+" "+rep.rtrim(frmdecimal(tot,3),9);*/
        rep.add(ss);

        return true;
    }

    protected boolean buildFooter() {

        rep.add("");
        rep.add("Total items: " + totitems);
        rep.add("Total estado bueno: " + tot);
        rep.add("Total estado malo: " + stot);
        rep.add("");
        rep.add("Serial : "+deviceid);
        rep.add("");

        return super.buildFooter();
    }


    // Aux

    public double round2(double val){
        int ival;

        val=(double) (100*val);
        double rslt=Math.round(val);
        rslt=Math.floor(rslt);

        ival=(int) rslt;
        rslt=(double) ival;

        return (double) (rslt/100);
    }

    private class itemData {
        public String cod,nombre,estado,um,lote;
        public double cant,peso;
    }
}
