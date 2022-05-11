package com.dtsgt.mpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsD_pedidoObj;
import com.dtsgt.classes.clsD_pedidocObj;
import com.dtsgt.classes.clsD_pedidocomboObj;
import com.dtsgt.classes.clsD_pedidodObj;
import com.dtsgt.classes.clsP_impresoraObj;
import com.dtsgt.classes.clsP_linea_impresoraObj;
import com.dtsgt.classes.clsP_orden_numeroObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsRepBuilder;
import com.dtsgt.classes.clsT_comandaObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordenObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ventaObj;
import com.dtsgt.classes.clsViewObj;
import com.dtsgt.ladapt.LA_D_pedidod;
import com.dtsgt.webservice.srvPedidoEstado;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PedidoDet extends PBase {

    private ListView listView;
    private TextView lblID,lblAnul,lblFecha,lblTiempo,lblPend,lblTot;
    private RelativeLayout rel1,rel2,rel3,rel4,rel5;

    private LA_D_pedidod adapter;

    private clsD_pedidoObj D_pedidoObj;
    private clsD_pedidodObj D_pedidodObj;
    private clsD_pedidocomboObj D_pedidocomboObj;
    private clsP_productoObj P_productoObj;
    private clsT_ventaObj T_ventaObj;
    private clsT_comboObj T_comboObj;
    private clsT_comandaObj T_comandaObj;
    private clsP_linea_impresoraObj P_linea_impresoraObj;
    private clsP_impresoraObj P_impresoraObj;

    private clsClasses.clsD_pedido item=clsCls.new clsD_pedido();
    private clsClasses.clsD_pedidod pitem=clsCls.new clsD_pedidod();
    private clsClasses.clsT_venta venta=clsCls.new clsT_venta();

    private clsRepBuilder rep;

    private ArrayList<String> tl=new ArrayList<String>();

    private TimerTask ptask;
    private int period=10000,delay=50;

    private String pedid,corelfact;
    private int est,modo,counter,ordennum,prodlinea;
    private double monto=0;
    private boolean horiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pantallaHorizontal()) {
            setContentView(R.layout.activity_pedido_det);horiz=true;
        } else {
            setContentView(R.layout.activity_pedido_det_ver);horiz=false;
        }

        super.InitBase();

        listView = findViewById(R.id.listView1);
        rel1=findViewById(R.id.rel01);
        rel2=findViewById(R.id.rel02);
        rel3=findViewById(R.id.rel05);
        rel4=findViewById(R.id.rel04);
        rel5=findViewById(R.id.rel03);
        lblID=findViewById(R.id.textView190);
        lblAnul=findViewById(R.id.textView191);
        lblFecha=findViewById(R.id.textView193);
        lblTiempo=findViewById(R.id.textView195);
        lblPend=findViewById(R.id.textView198);lblPend.setVisibility(View.INVISIBLE);
        lblTot=findViewById(R.id.textView200);

        pedid=gl.pedid;
        gl.closePedido=false;

        D_pedidoObj=new clsD_pedidoObj(this,Con,db);
        D_pedidodObj=new clsD_pedidodObj(this,Con,db);
        D_pedidocomboObj=new clsD_pedidocomboObj(this,Con,db);
        P_productoObj=new clsP_productoObj(this,Con,db);P_productoObj.fill();
        T_ventaObj=new clsT_ventaObj(this,Con,db);
        T_comboObj = new clsT_comboObj(this, Con, db);
        T_comandaObj=new clsT_comandaObj(this,Con,db);
        P_linea_impresoraObj=new clsP_linea_impresoraObj(this,Con,db);
        P_impresoraObj=new clsP_impresoraObj(this,Con,db);

        rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");

        setHandlers();

        loadItem();
        listItems();
    }

    //region Events

    public void doNuevo(View view) {
        modo=1;
        msgAsk("Marcar como Nuevo");
    }

    public void doPend(View view) {
        modo=2;
        msgAsk("Marcar como Preparando");
    }

    public void doComp(View view) {
        modo=3;
        msgAsk("Marcar como Completo");
    }

    public void doEnt(View view) {
        modo=4;
        msgAsk("Marcar como Entregando");
    }

    public void doPago(View view) {
        msgAskPago("Aplicar pago de monto "+mu.frmcur(monto)+" ");
    }

    public void doCliente(View view) {
        startActivity(new Intent(this,PedidoCli.class));
    }

    public void doAnul(View view) {
        boolean flag=gl.peAnulSuper;

        if (gl.rol==2 | gl.rol==3) flag=false;

        if (flag) {
            browse=1;
            startActivity(new Intent(this,ValidaSuper.class));
        } else {
            msgAskAnul("Anular orden");
        }
    }

    public void doExit(View view) {
        finish();
    }

    private void setHandlers() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                Object lvObj = listView.getItemAtPosition(position);
                clsClasses.clsD_pedidod item = (clsClasses.clsD_pedidod)lvObj;

                adapter.setSelectedIndex(position);
                /*
                String sn=item.nota;String s=item.umventa;
                if (!sn.isEmpty() && sn.length()>3) s=s+"\n"+sn;
                bigtoast(s);
                */
            };
        });
    }

    //endregion

    //region Main

    private void listItems() {
        ArrayList<clsClasses.clsD_pedidod> lines= new ArrayList<clsClasses.clsD_pedidod>();
        clsClasses.clsD_pedidod item,line;
        String s,cp,cn;

        try {

            rep.clear();
            rep.empty();
            rep.line();
            rep.empty();
            rep.add("ORDEN "+lblID.getText().toString());
            rep.empty();
            rep.line();
            rep.empty();

            D_pedidodObj.fill("WHERE Corel='"+pedid+"'");

            for (int i = 0; i <D_pedidodObj.count; i++) {

                item=D_pedidodObj.items.get(i);
                s=mu.frmdecno(item.cant)+" x "+getProd(item.codigo_producto);
                item.umventa=s;
                rep.add(s);

                line=clsCls.new clsD_pedidod();
                line.umventa=s;line.nota="";
                cn=item.nota;
                if (!cn.isEmpty()) {
                    line.umventa+=" / "+cn;rep.add("N : "+s);
                }
                lines.add(line);


                if (item.codigo_tipo_producto.equalsIgnoreCase("M")) {
                    //lines.add(line);
                    D_pedidocomboObj.fill("WHERE COREL_DET="+item.corel_det);
                    for (int j = 0; j <D_pedidocomboObj.count; j++) {
                        if (j==0) rep.line();

                        if (D_pedidocomboObj.items.get(j).codigo_producto!=0) {
                            cp = getProd(D_pedidocomboObj.items.get(j).codigo_producto);
                            cn = D_pedidocomboObj.items.get(j).nota;

                            line = clsCls.new clsD_pedidod();
                            line.umventa = "";
                            line.nota = " - " + cp;
                            rep.add(line.nota);

                            if (!cn.isEmpty()) {
                                line.nota += " / " + cn;
                                rep.add(" - N: " + cn);
                            }
                            lines.add(line);
                        }
                        if (j<D_pedidocomboObj.count-1) rep.empty();
                     }
                }

                rep.line();
            }


            adapter=new LA_D_pedidod(this,this,lines,horiz);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            mu.msgbox(e.getMessage());
        }
    }

    private void loadItem() {

        D_pedidoObj.fill("WHERE Corel='"+pedid+"'");
        item=D_pedidoObj.first();

        lblFecha.setText(du.shora(item.fecha_pedido)+"   -   Fecha : "+du.sfecha(item.fecha_pedido));

        est=1;
        if (item.codigo_usuario_creo>0) est=2;
        if (item.codigo_usuario_proceso>0) est=3;
        if (item.fecha_salida_suc>0) est=4;
        if (item.fecha_entrega>0) est=5;
        if (item.anulado==1) est=0;

        if (est==1) rel1.setBackgroundResource(R.drawable.frame_stat);
        if (est==2) rel2.setBackgroundResource(R.drawable.frame_stat);
        if (est==3) rel3.setBackgroundResource(R.drawable.frame_stat);
        if (est==4) rel4.setBackgroundResource(R.drawable.frame_stat);
        if (est==5) rel5.setBackgroundResource(R.drawable.frame_stat);

        rel1.setVisibility(View.VISIBLE);
        rel2.setVisibility(View.VISIBLE);
        rel3.setVisibility(View.INVISIBLE);
        rel4.setVisibility(View.INVISIBLE);
        rel5.setVisibility(View.INVISIBLE);
        if (est>=2) rel3.setVisibility(View.VISIBLE);
        if (est>=3) rel4.setVisibility(View.VISIBLE);
        if ((est>=4) && (app.pendientesPago(pedid)==0)) rel5.setVisibility(View.VISIBLE);

        if (item.empresa>0) lblID.setText("#"+item.empresa % 1000);else lblID.setText("");
        ordennum=item.empresa % 1000;

        if (item.anulado==1) lblAnul.setVisibility(View.INVISIBLE);

        lblTot.setText("Total : "+mu.frmcur(item.total));
        if (app.pendientesPago(pedid)>0) {
            monto=montoPago(pedid);
            lblTot.setText("Total : "+mu.frmcur(monto));
            if (monto>0) lblPend.setVisibility(View.VISIBLE);
        }
    }

    private void estado() {
        int ordid=1;
        gl.pedcorel="";

        D_pedidoObj.fill("ORDER BY Empresa DESC");
        if (D_pedidoObj.count>0) ordid=D_pedidoObj.first().empresa+1;

        if (modo==1) {
            item.codigo_estado=0;
            item.codigo_usuario_creo=0;
            item.codigo_usuario_proceso=0;
            item.fecha_salida_suc=0;
            item.fecha_entrega=0;
            item.anulado=0;
        }
        if (modo==2) {
            item.codigo_estado=1;
            item.empresa=ordid;
            item.codigo_usuario_creo=gl.codigo_vendedor;
            item.codigo_usuario_proceso=0;
            item.fecha_salida_suc=0;
            item.fecha_entrega=0;
            item.anulado=0;

            lblID.setText("#"+item.empresa % 1000);
        }
        if (modo==3) {
            item.codigo_estado = 2;
            item.codigo_usuario_proceso = gl.codigo_vendedor;
            item.fecha_salida_suc = 0;
            item.fecha_entrega = 0;
            item.anulado = 0;
            gl.pedcorel=pedid;
        }
        if (modo==4) {
            item.codigo_estado=3;
            item.fecha_salida_suc=du.getActDateTime();
            item.fecha_entrega=0;
            item.anulado=0;
        }
        if (modo==5) {
            item.codigo_estado=4;
            item.fecha_entrega=du.getActDateTime();
            item.anulado=0;
        }

        try {
            D_pedidoObj.update(item);

            Intent intent = new Intent(PedidoDet.this, srvPedidoEstado.class);

            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("correlativo",item.corel);
            intent.putExtra("estado_pedido",modo);
            intent.putExtra("valor_estado",gl.codigo_vendedor);

            startService(intent);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        if (modo==2) {
            imprimirOrden();return;
        } else  if (modo==3) {
            if (ventaVacia()) {
                crearVenta();
            } else {
                msgAskVenta("Borrar la venta actual y convertir orden a una venta nueva");
            }
            return;
        } else {
            finish();
        }
    }

    private void anular() {
       try {
            item.anulado=1;
            D_pedidoObj.update(item);

            Intent intent = new Intent(PedidoDet.this, srvPedidoEstado.class);

            intent.putExtra("URL",gl.wsurl);
            intent.putExtra("correlativo",item.corel);
            intent.putExtra("estado_pedido",0);
            intent.putExtra("valor_estado",1);

            startService(intent);

            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void aplicarPago() {
        try {
            sql="UPDATE D_FACTURAP SET VALOR="+monto+" WHERE (COREL='"+corelfact+"') AND (TIPO='E') AND (VALOR=0) ";
            db.execSQL(sql);
            modo=5;
            estado();
        } catch (Exception e) {
             msgbox(e.getMessage());
        }
    }

    //endregion

    //region Venta

    private void crearVenta() {
        String sql;

        counter=0;
        try {

            gl.codigo_cliente=gl.emp*10;

            db.execSQL("DELETE FROM T_COMBO");
            db.execSQL("DELETE FROM T_VENTA");

            sql="SELECT COREL, 0 AS COREL_DET, CODIGO_PRODUCTO, UMVENTA, SUM(CANT) AS Expr1, SUM(TOTAL) AS Expr2,'' AS NOTA,'' AS CODIGO_TIPO_PRODUCTO " +
                    "FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO<>'M') " +
                    "GROUP BY COREL, CODIGO_PRODUCTO, UMVENTA";
            D_pedidodObj.fillSelect(sql);

            for (int i = 0; i <D_pedidodObj.count; i++) {
                pitem=D_pedidodObj.items.get(i);
                addItem();
            }

            //sql="SELECT COREL, 0 AS COREL_DET, CODIGO_PRODUCTO, UMVENTA, CANT, TOTAL,'' AS NOTA,'' AS CODIGO_TIPO_PRODUCTO " +
            //        "FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO='M') ";
            sql="SELECT * FROM D_PEDIDOD WHERE (COREL='"+pedid+"') AND (CODIGO_TIPO_PRODUCTO='M') ";
            D_pedidodObj.fillSelect(sql);

            for (int i = 0; i <D_pedidodObj.count; i++) {
                pitem=D_pedidodObj.items.get(i);
                addItem();
            }

            gl.closePedido=true;

            crearTicketEntrega();

            gl.ventalock=true;
            finish();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void crearTicketEntrega() {
        clsClasses.clsD_pedidoc item;
        String s;
        String[] sp;
        int sps=gl.prw-2;

        try {

            clsD_pedidocObj D_pedidocObj=new clsD_pedidocObj(this,Con,db);
            D_pedidocObj.fill("WHERE (corel='"+pedid+"')");

            try {
                item=D_pedidocObj.first();
            } catch (Exception e) {
                item = clsCls.new clsD_pedidoc();

                item.corel=pedid;
                item.nombre="C.F.";
                item.telefono="";
                item.direccion="Ciudad";
                item.referencia="";
                item.nit="C.F.";
            }

            gl.gNombreCliente=item.nombre;
            gl.gNITCliente=item.nit;
            gl.gDirCliente=item.direccion;
            gl.gCorreoCliente="";

            rep.clear();
            rep.empty();
            rep.line();
            rep.empty();
            rep.add("ENTREGA ORDEN "+lblID.getText().toString());
            rep.empty();
            rep.line();
            rep.empty();

            rep.add("Nombre : ");
            s=item.nombre+" ";
            sp=splitByLen(s,sps);for (int i = 0; i <sp.length; i++) rep.add(sp[i]);
            rep.add("Direccion : ");
            s=item.direccion+" ";
            sp=splitByLen(s,sps);for (int i = 0; i <sp.length; i++) rep.add(sp[i]);
            s=item.referencia;
            if (!s.isEmpty()) {
                s=" - "+s;
                sp=splitByLen(s,sps);for (int i = 0; i <sp.length; i++) rep.add(sp[i]);
            }
            rep.add("Telefono : "+item.telefono);

            rep.empty();
            rep.line();
            rep.add("Hora : "+du.shora(du.getActDateTime()));
            rep.line();
            rep.empty();
            rep.empty();
            rep.empty();
            rep.save();

            app.doPrint();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private boolean addItem(){
        clsClasses.clsT_combo combo;
        clsClasses.clsD_pedidocombo citem;
        double prodtot,precdoc,fact,cantbas,peso;
        String umb;

        counter++;

        try {
            umb=pitem.umventa;
            fact=1;
            cantbas=pitem.cant*fact;
            peso=0;
            prodtot=pitem.total;
            precdoc=prodtot/cantbas;precdoc=mu.round2(precdoc);

            ins.init("T_VENTA");

            ins.add("PRODUCTO",app.prodCod(pitem.codigo_producto));
            ins.add("EMPRESA",""+counter);
            ins.add("UM",umb);
            ins.add("CANT",pitem.cant);
            ins.add("UMSTOCK",umb);
            ins.add("FACTOR",fact);
            ins.add("PRECIO",precdoc);
            ins.add("IMP",0);
            ins.add("DES",0);
            ins.add("DESMON",0);
            ins.add("TOTAL",prodtot);
            ins.add("PRECIODOC",precdoc);
            ins.add("PESO",peso);
            ins.add("VAL1",0);
            ins.add("VAL2",""+counter);
            ins.add("VAL3",0);
            ins.add("VAL4","0");
            ins.add("PERCEP",0);

            db.execSQL(ins.sql());

            if (pitem.codigo_tipo_producto.equalsIgnoreCase("M")) {

                D_pedidocomboObj.fill("WHERE COREL_DET="+pitem.corel_det);
                for (int j = 0; j <D_pedidocomboObj.count; j++) {

                    citem=D_pedidocomboObj.items.get(j);

                    combo=clsCls.new clsT_combo();

                    combo.codigo_menu=citem.seleccion;
                    combo.idcombo=counter;
                    combo.cant=citem.cant;
                    combo.unid=citem.cant;
                    combo.idseleccion=citem.codigo_producto;
                    combo.orden=0;

                    T_comboObj.add(combo);

                }
            }

        } catch (SQLException e) {
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),sql);
            mu.msgbox("Error : " + e.getMessage());return false;
        }

        return true;
    }

    //endregion

    //region Comandas

    private void imprimirOrden() {

        if (!gl.peImpOrdCos) return;

        if (gl.pelComandaBT) {

            try {
                rep.empty();
                rep.line();
                rep.add("Hora : "+du.shora(du.getActDateTime()));
                rep.line();
                rep.empty();
                rep.empty();
                rep.empty();
                rep.save();

                app.doPrint();

            } catch (Exception e) {
                msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            }
        } else {
            imprimeComanda();
        }

        finish();
    }

    private void imprimeComanda() {
        if (!divideComanda()) return;
        if (!generaArchivos()) return;
        app.print3nstarw();
    }

    private boolean divideComanda() {
        clsClasses.clsD_pedidod  pedido;
        String prname,cname,nn;
        int prodid,prid,iddet,linea=1;

        try {

            db.execSQL("DELETE FROM T_comanda");

            D_pedidodObj.fill("WHERE (COREL='"+pedid+"')");

            for (int i = 0; i <D_pedidodObj.count; i++) {
                pedido=D_pedidodObj.items.get(i);

                prodid = pedido.codigo_producto;
                iddet = pedido.corel_det;
                prname=getProd(prodid);
                s = mu.frmdecno(pedido.cant) + " x " + prname;
                nn="";

                if (!app.prodTipo(prodid).equalsIgnoreCase("M")) {

                    P_linea_impresoraObj.fill("WHERE CODIGO_LINEA="+prodlinea);
                    for (int k = 0; k <P_linea_impresoraObj.count; k++) {
                        prid=P_linea_impresoraObj.items.get(k).codigo_impresora;
                        agregaComanda(linea,prid,s);linea++;
                        if (!nn.isEmpty()) {
                            agregaComanda(linea,prid,nn);linea++;
                        }
                    }

                } else {

                    D_pedidocomboObj.fill("WHERE (COREL_DET=" + iddet+") ");
                    cname=s;//+" [#"+idcomb+"]";

                    for (int j = 0; j < D_pedidocomboObj.count; j++) {
                        prodid=D_pedidocomboObj.items.get(j).codigo_producto;
                        s = " -  " + getProd(prodid);
                        P_linea_impresoraObj.fill("WHERE CODIGO_LINEA="+prodlinea);

                        for (int k = 0; k <P_linea_impresoraObj.count; k++) {
                            prid=P_linea_impresoraObj.items.get(k).codigo_impresora;
                            agregaComanda(linea,prid,cname);linea++;
                            agregaComanda(linea,prid,s);linea++;
                            if (!nn.isEmpty()) {
                                agregaComanda(linea,prid,nn);linea++;
                            }
                        }
                    }
                }

            }
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return false;
        }
    }

    private boolean agregaComanda(int linea,int prid,String texto) {
        try {
            clsClasses.clsT_comanda item = clsCls.new clsT_comanda();

            item.linea=linea;
            item.id=prid;
            item.texto=texto;

            T_comandaObj.add(item);
            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    private boolean generaArchivos() {
        clsRepBuilder rep;
        int printid;
        String fname,ss;
        File file;

        try {

            P_impresoraObj.fill();

            for (int i = 0; i <P_impresoraObj.count; i++) {
                fname = Environment.getExternalStorageDirectory()+"/comanda_"+P_impresoraObj.items.get(i).codigo_impresora+".txt";
                file=new File(fname);
                try {
                    file.delete();
                } catch (Exception e) { }
            }
        } catch (Exception e) {
        }

        try {
            clsViewObj ViewObj=new clsViewObj(this,Con,db);
            ViewObj.fillSelect("SELECT DISTINCT ID, '','','','', '','','','' FROM T_comanda ORDER BY ID");

            for (int i = 0; i <ViewObj.count; i++) {
                printid=ViewObj.items.get(i).pk;
                P_impresoraObj.fill("WHERE (CODIGO_IMPRESORA="+printid+")");

                rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"comanda_"+printid+".txt");

                rep.add(P_impresoraObj.first().tipo_impresora);
                rep.add(P_impresoraObj.first().nombre);
                rep.add(P_impresoraObj.first().ip);

                rep.add("");rep.add("");rep.add("");
                rep.add("ORDEN : "+ordennum);
                //rep.add("MESA : "+mesa);
                rep.add("Hora : "+du.shora(du.getActDateTime()));
                rep.add("Mesero : "+gl.nombre_mesero_sel);

                rep.line16();

                T_comandaObj.fill("WHERE ID="+printid+" ORDER BY LINEA");
                //T_comandaObj.fillSelect("SELECT COUNT(ID),ID,TEXTO WHERE ID="+printid+" GROUP BY ID,TEXTO");

                tl.clear();
                for (int j = 0; j <T_comandaObj.count; j++) {
                    ss=T_comandaObj.items.get(j).texto;
                    if (ss.indexOf(" - ")==0) {
                        tl.add(ss.toUpperCase());
                    } else {
                        if (gl.emp==14) {
                            //if (!itemexists(ss)) tl.add(ss.toUpperCase());
                        } else {
                            tl.add(ss.toUpperCase());
                        }
                    }
                };

                for (int j = 0; j <tl.size(); j++) {
                    rep.add(tl.get(j));
                }

                //for (int j = 0; j <T_comandaObj.count; j++) {
                //    rep.add(T_comandaObj.items.get(j).texto);
                //}

                rep.line16();
                rep.add("");rep.add("");rep.add("");

                rep.save();rep.clear();
            }

            //mesa
            //rep=new clsRepBuilder(this,gl.prw,true,gl.peMon,gl.peDecImp,"");


            return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return false;
        }
    }

    //endregion


    //region Aux

    private String getProd(int prodid) {
        try {
            for (int i = 0; i <P_productoObj.count; i++) {
                if (P_productoObj.items.get(i).codigo_producto==prodid) {
                    prodlinea=P_productoObj.items.get(i).linea;
                    return P_productoObj.items.get(i).desclarga;
                }
            }
        } catch (Exception e) {}
        return ""+prodid;
    }

    private void bigtoast(String msg) {
        try {
            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            RelativeLayout toastLayout = (RelativeLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(30);
            toastTV.setTypeface(null, Typeface.BOLD);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private boolean ventaVacia() {
        Cursor dt;

        try {
            sql="SELECT * FROM T_VENTA";
            dt=Con.OpenDT(sql);
            if (dt.getCount()==0) return true;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

        return false;
    }

    private void borrarVenta() {
        try {
            sql="DELETE FROM T_VENTA";
            db.execSQL(sql);

            sql="DELETE FROM T_COMBO";
            db.execSQL(sql);

            sql="DELETE FROM T_PRODMENU";
            db.execSQL(sql);

            sql="DELETE FROM T_PAGO";
            db.execSQL(sql);

            gl.iniciaVenta=false;

            crearVenta();

        } catch (SQLException e) {
            mu.msgbox("Error : " + e.getMessage());
        }
      }

    private void iniciaTiempo() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(ptask=new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        actualizaTiempo();
                    }
                });
            }
        }, delay, period);
    }

    private void cancelaTiempo() {
        try {
            ptask.cancel();
        } catch (Exception e) {}
    }

    private void actualizaTiempo() {
        item.tdif=-1;
        if (item.fecha_salida_suc==0) {
            item.tdif=du.timeDiff(du.getActDateTime(),item.fecha_recepcion_suc);
        } else {
            item.tdif=du.timeDiff(item.fecha_salida_suc,item.fecha_recepcion_suc);
        }
        if (item.anulado==1) item.tdif=-1;

        if (item.tdif>-1) {
            if (horiz) lblTiempo.setText(item.tdif+" min");else  lblTiempo.setText(item.tdif+" m");
        }else{
            lblTiempo.setText("");
        }

    }

    public double montoPago(String pcor) {
        try {
            sql="SELECT Total,Corel FROM D_FACTURA WHERE PEDCOREL='"+pcor+"'";
            Cursor dt=Con.OpenDT(sql);

            dt.moveToFirst();
            corelfact=dt.getString(1);
            return dt.getDouble(0);
        } catch (Exception e) {
            return 0;
        }
    }

    private String[] splitByLen(String s, int size) {
        if(s == null || size <= 0)
            return null;
        int chunks = s.length() / size + ((s.length() % size > 0) ? 1 : 0);
        String[] arr = new String[chunks];
        for(int i = 0, j = 0, l = s.length(); i < l; i += size, j++)
            arr[j] = s.substring(i, Math.min(l, i + size));
        return arr;
    }

    public boolean pantallaHorizontal() {
        try {
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            return point.x>point.y;
        } catch (Exception e) {
            return true;
        }
    }

    //endregion

    //region Dialogs

    private void msgAsk(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                estado();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskAnul(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                msgAskAnul2("Está seguro");
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskAnul2(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                anular();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskVenta(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                borrarVenta();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskPago(String msg) {
        ExDialog dialog = new ExDialog(this);

        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                aplicarPago();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();

        iniciaTiempo();

        try {
            D_pedidoObj.reconnect(Con,db);
            D_pedidodObj.reconnect(Con,db);
            D_pedidocomboObj.reconnect(Con,db);
            P_productoObj.reconnect(Con,db);
            T_ventaObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_comandaObj.reconnect(Con,db);
            P_linea_impresoraObj.reconnect(Con,db);
            P_impresoraObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (gl.checksuper) msgAskAnul("Anular orden");
            gl.checksuper=false;return;
        }
    }

    @Override
    protected void onPause() {
        cancelaTiempo();
        super.onPause();
    }

    //endregion

}