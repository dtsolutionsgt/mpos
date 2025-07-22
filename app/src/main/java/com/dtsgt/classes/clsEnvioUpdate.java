package com.dtsgt.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dtsgt.base.BaseDatos;
import com.dtsgt.base.clsClasses;

import java.util.ArrayList;

public class clsEnvioUpdate {


    private Context cont;
    private BaseDatos Con;
    private SQLiteDatabase db;
    private BaseDatos.Insert ins;
    private BaseDatos.Update upd;
    private clsClasses clsCls = new clsClasses();

    private clsD_facturaObj D_facturaObj;
    private clsD_facturadObj D_facturadObj;
    private clsP_productoObj P_productoObj;

    private String sql;

    public clsEnvioUpdate(Context context, BaseDatos dbconnection, SQLiteDatabase dbase) {
        cont=context;
        Con=dbconnection;
        ins=Con.Ins;upd=Con.Upd;
        db = dbase;

        D_facturaObj=new clsD_facturaObj(cont,Con,db);
        D_facturadObj=new clsD_facturadObj(cont,Con,db);
        P_productoObj=new clsP_productoObj(cont,Con,db);
    }



    public String generaSQL(String corel,long corel_envio,int cod_sucursal,int cod_usuario) {
        double tot,prec,cant,descm,descpr;

        sql="";

        D_facturaObj.fill("WHERE (COREL='"+corel+"')");
        D_facturadObj.fill("WHERE (COREL='"+corel+"')");

        upd.init("D_NOTA_ENVIO_BOF_ENC");

        upd.add("EMPRESA",D_facturaObj.first().empresa );
        upd.add("CODIGO_NOTA_ENVIO_ESTATUS", 2);
        upd.add("CODIGO_SUCURSAL", cod_sucursal);
        upd.add("CODIGO_CLIENTE", D_facturaObj.first().cliente);
        upd.add("REFERENCIA", D_facturaObj.first().corel);
        upd.add("USER_AGR", cod_usuario);
        upd.add("USER_MOD", cod_usuario);

        upd.Where("CODIGO_NOTA_ENVIO_ENC="+corel_envio);

        sql=upd.sql()+";";

        for (clsClasses.clsD_facturad itm:D_facturadObj.items) {

            prec=itm.precio;cant=itm.cant;tot=prec*cant;
            descm=itm.desmon;
            if (tot>0) descpr=100*descm/tot; else descpr=0;

            ins.init("D_NOTA_ENVIO_BOF_DET");

            ins.add("CODIGO_NOTA_ENVIO_ENC",corel_envio);
            ins.add("CODIGO_PRODUCTO",itm.producto);
            ins.add("CANTIDAD",cant);
            ins.add("PRECIO_VENTA",prec);
            ins.add("TOTAL",itm.total);
            ins.add("DESCUENTO",itm.desmon);
            ins.add("DESCUENTO_PORCENTAJE",descpr);
            ins.add("NOMBRE_PRODUCTO",nombreProducto(itm.producto));
            ins.add("USER_AGR", cod_usuario);
            ins.add("USER_MOD", cod_usuario);

            sql+=ins.sql()+";";

        }

        return sql;
    }

    private String nombreProducto(int codp) {
        try {
            P_productoObj.fill("WHERE (codigo_producto="+codp+")");
            if (P_productoObj.count>0) return P_productoObj.first().desclarga; else return "";
        } catch (Exception e) {
            return "";
        }
    }


}
