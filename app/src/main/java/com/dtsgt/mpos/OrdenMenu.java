package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodmenuopcObj;
import com.dtsgt.classes.clsP_prodmenuopcdetObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.classes.clsT_ordencomboObj;
import com.dtsgt.classes.clsT_ordencomboadObj;
import com.dtsgt.classes.clsT_ordencombodetObj;
import com.dtsgt.classes.clsT_ordencomboprecioObj;
import com.dtsgt.classes.extListDlg;
import com.dtsgt.firebase.fbOrden;
import com.dtsgt.firebase.fbOrdenCombo;
import com.dtsgt.firebase.fbOrdenComboPrecio;
import com.dtsgt.ladapt.ListAdaptOpcion;

import java.util.ArrayList;

public class OrdenMenu extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lbl3;
    private ImageView img1,img2,imgSave;

    private ListAdaptOpcion adapter;
    private clsT_ordencomboObj T_comboObj;
    private clsP_productoObj P_productoObj;
    private clsT_ordencomboprecioObj T_ordencomboprecioObj;

    private fbOrden fbo;
    private fbOrdenCombo fbocb;
    private fbOrdenComboPrecio fbop;

    private Runnable rnfbocbList;

    private ArrayList<clsClasses.clsOpcion> items= new ArrayList<clsClasses.clsOpcion>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();
    private ArrayList<String> mname = new ArrayList<String>();

    private Precio prc;

    private int cant,lcant, uitemid,nivel,idcomboval;
    private double precorig,precitems,precdif, precio, precnuevo;
    private boolean newitem,valido;
    private String ststr,prodname,idorden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_orden_menu);

            super.InitBase();

            listView = findViewById(R.id.listView);
            lbl1 = findViewById(R.id.textView93);
            lbl2 = findViewById(R.id.textView117);
            lbl3 = findViewById(R.id.textView225);
            img1 = findViewById(R.id.imageView27);
            img2 = findViewById(R.id.imageView108);img2.setVisibility(View.INVISIBLE);
            imgSave = findViewById(R.id.imgImg3);

            //peEditTotCombo;

            P_productoObj = new clsP_productoObj(this, Con, db);
            T_comboObj = new clsT_ordencomboObj(this, Con, db);
            T_ordencomboprecioObj=new clsT_ordencomboprecioObj(this,Con,db);

            prc = new Precio(this, mu, 2,gl.peDescMax);

            setHandlers();

            cant=1;nivel=gl.nivel;

            uitemid = Integer.parseInt(gl.menuitemid);
            newitem = gl.newmenuitem;
            idorden=gl.idorden;

            precorig=gl.menuprecio;
            int prodcode=app.codigoProducto(gl.prodid);
            precorig=prodPrecioItem(prodcode);
            idcomboval=app.codigoCombo(prodcode);

            lbl1.setText(gl.gstr2);
            lbl2.setText(""+cant);
            lbl3.setText(mu.frmcur(gl.menuprecio));

            app.parametrosExtra();
            if (gl.peAgregarCombo) img2.setVisibility(View.VISIBLE);

            fbo=new fbOrden("Orden",gl.tienda,idorden);
            fbocb=new fbOrdenCombo("OrdenCombo",gl.tienda);
            fbop=new fbOrdenComboPrecio("OrdenComboPrecio",gl.tienda);

            rnfbocbList= () -> {fbocbList();};

            if (newitem) {
                newItem();
            } else {
                listItems();
            }

            if (idcomboval==0) {
                valido=false;
            } else {
                valido=app.validaCombo(idcomboval);
            }
            if (!valido) {
                imgSave.setVisibility(View.INVISIBLE);
                listaInvalidos();
            }

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Events

    public void doApply(View view) {
        if (!validaData()) {
            //msgAskApply("Aplicar sin definir todas las opciónes");
            msgbox("No están definidas todas las opciónes");return;
        } else {
            if (validaStock()) {
                saveItem();
            } else {
                msgAskSave("Agregar a venta sin existencias");
            }
        }
    }

    public void doDec(View view) {
        if (cant>0) cant--;
        lbl2.setText(""+cant);
        validaStock();
    }

    public void doInc(View view) {
        cant++;
        lbl2.setText(""+cant);
        validaStock();
    }

    public void doDelete(View view) {
        msgAskDelete("Eliminar articulo");
    }

    public void doAdd(View view) {
        browse=1;
        gl.gstr="";gl.prodtipo=1;
        startActivity(new Intent(this, Producto.class));
    }

    public void doClose(View view) {
        exit();
    }

    private void setHandlers(){

        try{

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    try {

                        Object lvObj = listView.getItemAtPosition(position);
                        clsClasses.clsOpcion item = (clsClasses.clsOpcion)lvObj;

                        adapter.setSelectedIndex(position);
                        selidx=position;

                        //#EJC20200524: listar items de la opcion de menu
                        listOptions(item.Name,item.codigo_menu_opcion,item.cant,item.opcional);

                    } catch (Exception e) {
                        addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
                        mu.msgbox2(e.getMessage());
                    }
                };
            });

        } catch (Exception e) {
            mu.msgbox2( e.getMessage());
        }
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            listMenuItems();
            fbocb.listItems(idorden,uitemid,rnfbocbList);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void fbocbList() {
        int menuid,selid,ccant;

        try {

            /*
            clsT_ordencomboObj combo=new clsT_ordencomboObj(this,Con,db);
            combo.fill("WHERE (COREL='"+idorden+"') AND (IdCombo="+ uitemid+")");
            try {
                cant=combo.first().cant;
            } catch (Exception e) {}
            */


            lbl2.setText(""+fbocb.items.size());

            for (int i = 0; i <items.size(); i++) {

                menuid=items.get(i).codigo_menu_opcion;
                selid=0;ccant=0;
                //combo.fill("WHERE (COREL='"+idorden+"') AND (IdCombo="+ uitemid+") AND (CODIGO_MENU="+menuid+")");

                try {
                    //selid=combo.first().idseleccion;

                    for (int ii = 0; ii <fbocb.items.size(); ii++) {
                        if (fbocb.items.get(ii).codigo_menu==menuid) {
                            selid=fbocb.items.get(ii).idseleccion;
                            ccant=fbocb.items.get(ii).cant;
                            break;
                        }
                    }

                    if (selid!=0) {
                        if (selid>0) {
                            items.get(i).cod=selid;
                            items.get(i).Name=getProdName(selid);
                            items.get(i).bandera=1;
                        } else {
                            items.get(i).cod=0;
                            items.get(i).bandera=0;
                        }

                        //items.get(i).cant=combo.first().cant;
                        items.get(i).cant=ccant;
                        items.get(i).precio=prodPrecioItem(selid);
                        items.get(i).sprec=mu.frmdec(items.get(i).precio);
                        if (items.get(selidx).precio==0) items.get(selidx).sprec="";
                    } else {
                        items.get(i).Name=gl.peComNoAplic;
                    }

                    if (items.get(i).cod==0) items.get(i).modo=0; else items.get(i).modo=1;

                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            }

            adapter.notifyDataSetChanged();
            precioFinal();
        } catch (Exception e) {
            mu.msgbox2(e.getMessage());
        }
    }

    private void updateItems() {
        int selid;

        try {
            for (int i = 0; i <items.size(); i++) {

                try {
                    selid=items.get(i).cod;

                    if (selid!=0) {
                        if (selid>0) {
                            items.get(i).Name=getProdName(selid);
                            items.get(i).bandera=1;
                        } else {
                            items.get(i).cod=0;
                            items.get(i).bandera=0;
                        }

                        items.get(i).precio=prodPrecioItem(selid);
                        items.get(i).sprec=mu.frmdec(items.get(i).precio);
                        if (items.get(selidx).precio==0) items.get(selidx).sprec="";
                   } else {
                        items.get(i).Name=gl.peComNoAplic;
                    }
                } catch (Exception e) {
                    String ss=e.getMessage();
                }
            }

            adapter.notifyDataSetChanged();
            precioFinal();
        } catch (Exception e) {
            mu.msgbox2(e.getMessage());
        }
    }

    private void newItem() {
        try {
            uitemid=du.getOrdenCorel(gl.codigo_ruta);
            gl.menuitemid=""+uitemid;

            listMenuItems();
            precioInicial();

            img1.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void listMenuItems() {
        clsP_prodmenuObj P_menuObj=new clsP_prodmenuObj(this,Con,db);
        clsClasses.clsOpcion item;
        clsT_ordencomboadObj T_ordencomboadObj=new clsT_ordencomboadObj(this,Con,db);
        clsClasses.clsT_ordencomboad aitem;

        try {

            items.clear();

            P_menuObj.fill_by_idproducto(gl.prodmenu);

            for (int i = 0; i <P_menuObj.count; i++) {

                item=clsCls.new clsOpcion();

                item.codigo_menu_opcion =P_menuObj.items.get(i).codigo_menu;
                item.Name=P_menuObj.items.get(i).nombre;
                item.bandera=0;
                item.orden=P_menuObj.items.get(i).orden;
                item.cod=0;
                item.unid=P_menuObj.items.get(i).unid;
                if (item.unid>0) item.Name+="*";
                item.cant=P_menuObj.items.get(i).cant;if (item.cant==0) item.cant=1;

                item.opcional=P_menuObj.items.get(i).cant==0;
                item.adicional=false;
                item.modo=1;

                item.precio=0;
                item.sprec=mu.frmdec(item.precio);
                if (item.precio==0) item.sprec="";

                items.add(item);
            }

            T_ordencomboadObj.fill("WHERE (COREL='"+idorden+"') AND (IDCOMBO="+uitemid+")");
            for (int i = 0; i <T_ordencomboadObj.count; i++) {

                item=clsCls.new clsOpcion();
                item.codigo_menu_opcion =T_ordencomboadObj.items.get(i).id;
                item.Name=T_ordencomboadObj.items.get(i).nombre;
                item.bandera=0;
                item.orden=0;
                item.cod=0;
                item.unid=T_ordencomboadObj.items.get(i).cant;
                if (item.unid>0) item.Name+="*";
                item.cant=T_ordencomboadObj.items.get(i).cant;
                item.opcional=T_ordencomboadObj.items.get(i).cant==0;
                item.adicional=true;
                item.modo=-1;

                item.precio=0;
                item.sprec=mu.frmdec(item.precio);
                if (item.precio==0) item.sprec="";

                items.add(item);
            }

            adapter=new ListAdaptOpcion(this,items);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            mu.msgbox2(e.getMessage());
        }
    }

    private boolean saveItem() {
        Cursor dt;
        clsClasses.clsT_ordencombo ocitem;
        int newid,cui;
        double prec,impval,desc,descmon,tot,pimp;

        try {
            sql="SELECT MAX(ID) FROM T_ORDEN WHERE (COREL='"+idorden+"')";
            dt=Con.OpenDT(sql);
            dt.moveToFirst();
            newid=dt.getInt(0)+1;
        } catch (Exception e) {
            newid=1;
        }

        try {

            String um=getProdUM(gl.prodmenu);

            prec = prc.precio(gl.prodid, cant, gl.nivel, um, gl.umpeso, gl.dpeso,um,gl.prodmenu);
            impval = prc.impval;
            desc=prc.desc;
            descmon = prc.descmon;
            tot = prc.tot;
            pimp = prc.imp;

            if (precnuevo>0) {
                prec=precnuevo;
                tot=prec*cant;
            }

            db.beginTransaction();

            /*
            if (!newitem){
                db.execSQL("DELETE FROM T_ORDENCOMBO WHERE (COREL='"+idorden+"') AND (IdCombo="+uitemid+")");
                db.execSQL("DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (ID="+gl.produid+")");
            } else {
                guardaPrecios();
            }
            db.execSQL("UPDATE T_ordencomboprecio SET PRECTOTAL="+precnuevo+" WHERE (COREL='"+idorden+"') AND (IdCombo="+uitemid+")");
            */

            newid=du.getOrdenCorel(gl.codigo_ruta);


            guardaPrecios();

            for (int i = 0; i <items.size(); i++) {

                ocitem=clsCls.new clsT_ordencombo();

                ocitem.corel=idorden;
                ocitem.codigo_menu=items.get(i).codigo_menu_opcion;
                ocitem.idcombo=uitemid; //
                ocitem.cant=cant;
                ocitem.unid=items.get(i).unid;
                ocitem.idseleccion=items.get(i).cod;
                ocitem.orden=items.get(i).orden;

                fbocb.setItem(ocitem);

                //T_comboObj.add(item);
            }

            //cui=app.cuentaActiva(idorden);


            cui=gl.combo_cuenta;

            clsClasses.clsT_orden fbitem=clsCls.new clsT_orden();

            //fbitem.id=newid;
            fbitem.id=uitemid;
            fbitem.corel=idorden;
            fbitem.producto=gl.prodid;
            //fbitem.empresa=""+newid;
            fbitem.empresa=""+uitemid;
            fbitem.um=gl.um;
            fbitem.cant=cant;
            fbitem.umstock="UNI";
            fbitem.factor=1;
            fbitem.precio=prec;
            fbitem.imp=impval;
            fbitem.des=desc;
            fbitem.desmon=descmon;
            fbitem.total=tot;
            fbitem.preciodoc=prec;
            fbitem.peso=0;

            if (gl.codigo_pais.equalsIgnoreCase("HN")) {
                fbitem.val1=pimp;
            } else  if (gl.codigo_pais.equalsIgnoreCase("SV")) {
                fbitem.val1=pimp;
            } else {
                fbitem.val1=0;
            }

            fbitem.val2="1";
            fbitem.val3=0;
            fbitem.val4="0"+uitemid;
            fbitem.percep=0;
            fbitem.cuenta=cui;
            fbitem.estado=1;
            fbitem.idmesero=gl.idmesero;

            fbo.setItem(fbitem.id,fbitem);

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.retcant=cant;
            finish();

            return true;
        } catch (Exception e) {
            db.endTransaction();
            msgbox2(e.getMessage());
            return false;
        }
    }

    private void listOptions(String title,int idoption,int cant,boolean opcional) {
        final AlertDialog Dialog;
        clsP_prodmenuopcdetObj opc=new clsP_prodmenuopcdetObj(this,Con,db);
        clsT_ordencombodetObj T_ordencombodetObj=new clsT_ordencombodetObj(this,Con,db);
        int cod;


        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(OrdenMenu.this,"Opciones");

            lcode.clear();lname.clear();mname.clear();

            opc.fill("WHERE CODIGO_MENU_OPCION="+idoption);
            T_ordencombodetObj.fill("WHERE CODIGO_MENU_OPCION="+idoption);

            if (opcional) {
                lcode.add("0");lname.add(gl.peComNoAplic);mname.add(gl.peComNoAplic);
            }

            for (int i = 0; i <opc.count; i++) {
                cod=opc.items.get(i).codigo_producto;
                lcode.add(""+cod);
                mname.add(getProdName(cod));
                lname.add(getProdName(cod)+" ["+mu.frmdec(prodPrecioItem(cod))+"]");
            }

            for (int i = 0; i <T_ordencombodetObj.count; i++) {
                cod=T_ordencombodetObj.items.get(i).codigo_producto;
                lcode.add(""+cod);
                mname.add(getProdName(cod));
                lname.add(getProdName(cod)+" ["+mu.frmdec(prodPrecioItem(cod))+"]");
            }

            for (int i = 0; i < lname.size(); i++) {
                listdlg.add(lname.get(i));
            }


            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        try {

                            clsClasses.clsOpcion item=items.get(selidx);

                            item.cod=Integer.parseInt(lcode.get(position));
                            item.Name=mname.get(position);
                            item.bandera=1;
                            item.precio=prodPrecioItem(item.cod);
                            item.sprec=mu.frmdec(item.precio);
                            if (item.precio==0) item.sprec="";

                            if (item.cod==0) {
                                item.modo=0;
                                doAdd(null);
                            } else item.modo=1;

                            adapter.notifyDataSetChanged();
                            precioFinal();
                            validaStock();
                        } catch (Exception e) {
                            toast(e.getMessage());
                        }

                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    private void deleteItem() {
        try {
            db.beginTransaction();

            sql="DELETE FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (VAL4='"+uitemid+"')";
            db.execSQL(sql);
            db.execSQL("DELETE FROM T_ORDENCOMBO WHERE (COREL='"+idorden+"') AND (IdCombo="+uitemid+")");
            db.execSQL("DELETE FROM T_ORDENCOMBOAD WHERE (COREL='"+idorden+"') AND (IdCombo="+uitemid+")");

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.retcant=0;
            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }

    private void agregaArticulo(int aid) {

        clsP_prodmenuopcObj P_prodmenuopcObj=new clsP_prodmenuopcObj(this,Con,db);
        clsT_ordencomboObj combo=new clsT_ordencomboObj(this,Con,db);
        clsT_ordencomboadObj T_ordencomboadObj=new clsT_ordencomboadObj(this,Con,db);
        clsClasses.clsT_ordencomboad item;
        clsT_ordencombodetObj T_ordencombodetObj=new clsT_ordencombodetObj(this,Con,db);
        clsClasses.clsT_ordencombodet ditem;
        clsClasses.clsOpcion oitem;

        String prodname=app.prodNombre(aid);
        int id=0,id1,id2;

        try {
            db.beginTransaction();

            // P_PRODMENUOPC

            id1=T_comboObj.newID("SELECT MAX(CODIGO_MENU) FROM T_ordencombo");
            id2=T_ordencomboadObj.newID("SELECT MAX(ID) FROM T_ordencomboad");
            id=id1+1000000;if (id2>id) id=id2;

            item = clsCls.new clsT_ordencomboad();

            item.id=id;
            item.corel=idorden;
            item.idcombo=uitemid;
            item.nombre=prodname;
            //item.nombre="Adicional";
            item.cant=1;

            T_ordencomboadObj.add(item);

            // P_PRODMENUOPCDET

            id1=T_ordencombodetObj.newID("SELECT MAX(CODIGO_MENUOPC_DET) FROM T_ordencombodet");
            id2=T_comboObj.newID("SELECT MAX(CODIGO_MENU) FROM T_ordencombo");
            if (id2>id1) id1=id2;

            ditem = clsCls.new clsT_ordencombodet();

            ditem.codigo_menuopc_det=id1;
            ditem.idcombo=uitemid;
            ditem.codigo_menu_opcion=id;
            ditem.codigo_producto=aid;
            ditem.corel=idorden;

            T_ordencombodetObj.add(ditem);

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }

        try {
            oitem=clsCls.new clsOpcion();

            oitem.codigo_menu_opcion =id;
            oitem.cod=aid;
            oitem.bandera=1;
            oitem.Name=prodname;
            oitem.bandera=0;
            oitem.orden=0;
            oitem.unid=0;
            oitem.cant=0;
            oitem.opcional=true;
            oitem.adicional=true;
            oitem.precio=prodPrecioItem(aid);
            oitem.sprec=mu.frmdec(oitem.precio);
            if (oitem.precio==0) oitem.sprec="";

            items.add(oitem);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }

        adapter.notifyDataSetChanged();

        adapter=new ListAdaptOpcion(this,items);
        listView.setAdapter(adapter);

        updateItems();

    }

    //endregion

    //region Precios

    private void precioFinal() {
        precio=precorig-precdif;

        try {

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).cod==0 && items.get(i).modo==0) {
                    if (!items.get(i).adicional) precio -= precioItem(items.get(i).codigo_menu_opcion);
                }
            }

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).cod!=0 && items.get(i).adicional ) {
                    precio += prodPrecioItem(items.get(i).cod);
                }
            }

            precio+=precdif;
            if (!gl.peComboLimite) {
                if (precio<precorig) precio=precorig;
            }

            precnuevo=precio;
            lbl3.setText(mu.frmcur(precnuevo));

            if (precorig==precnuevo) {
                lbl3.setTextColor(Color.parseColor("#1B76B9"));
            } else {
                lbl3.setTextColor(Color.RED);
            }
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void precioInicial() {
        try {
            double iprec=0;

            for (int i = 0; i < items.size(); i++) {
                iprec+=precioItem(items.get(i).codigo_menu_opcion);
            }

            precitems=iprec;
            precdif=precitems-precorig;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private double precioItem(int itemid) {
        clsP_prodmenuopcdetObj opc = new clsP_prodmenuopcdetObj(this, Con, db);
        int cod;
        double pp,pmin=10000000;

        try {
            opc.fill("WHERE CODIGO_MENU_OPCION="+itemid);
            for (int ii = 0; ii <opc.count; ii++) {
                cod=opc.items.get(ii).codigo_producto;
                pp=prodPrecioItem(cod);
                if (ii==0) {
                    pmin=pp;
                } else {
                    if (pp<pmin) pmin=pp;
                }
            }
            return pmin;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());return 0;
        }
    }

    private void guardaPrecios() {
        clsClasses.clsT_ordencomboprecio pitem = clsCls.new clsT_ordencomboprecio();

        pitem.corel=idorden;
        pitem.idcombo=uitemid;
        pitem.precorig=precorig;
        pitem.precitems=precitems;
        pitem.precdif=precdif;
        pitem.prectotal=precnuevo;

        fbop.setItem(pitem);
        //T_ordencomboprecioObj.add(pitem);
    }

    //endregion

    //region Disponible

    private boolean validaStock() {
        boolean flag=true;
        ststr="";

        /*
        for (int i = 0; i <items.size(); i++) {
            boolean ss=isProdStock(items.get(i).cod);
            if (isProdStock(items.get(i).cod)) {
                if (!stockProducto(items.get(i).cod,cant,items.get(i).unid)) {
                    flag=false;
                }
            }
        }

        if (!flag) msgbox2(ststr);


        return flag;
        */

        return true;
    }

    private boolean stockProducto(int prodid,int prcant,int unid){
        int ctot, cstock, cvent, cbcombo, cavent=0;

        prodname=""+prcant;

        cstock=cantStock(prodid);
        cvent=cantProdVenta(prodid);
        cbcombo=cantProdCombo(prodid);
        cavent=cantProdItems(prodid);

        ctot=cvent+unid*(cbcombo+cavent);

        if (cstock<ctot) {
            ststr+=" Falta - "+prodname+" : "+(ctot-cstock)+"\n";
        }

        return cstock>=ctot;
    }

    private int cantStock(int prodid) {
        Cursor dt=null;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);

            //sql="SELECT CANT FROM P_STOCK WHERE (CODIGO='"+P_productoObj.first().codigo+"') AND (UNIDADMEDIDA='"+P_productoObj.first().unidbas+"')";
            sql="SELECT CANT FROM P_STOCK WHERE (CODIGO='"+prodid+"') AND (UNIDADMEDIDA='"+P_productoObj.first().unidbas+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                int val=dt.getInt(0);
                if (dt!=null) dt.close();
                return val;
            }
            if (dt!=null) dt.close();
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (dt!=null) dt.close();
        }
    }

    private int cantProdVenta(int prodid) {
        Cursor dt;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);
            prodname=P_productoObj.first().desclarga;

            sql="SELECT SUM(CANT) FROM T_ORDEN WHERE (COREL='"+idorden+"') AND (PRODUCTO='"+P_productoObj.first().codigo+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int cantProdCombo(int prodid) {
        Cursor dt;

        try {
            sql="SELECT SUM(CANT) FROM T_COMBO WHERE (IdCombo="+ uitemid+") AND (IDSELECCION="+prodid+")";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int cantProdItems(int prodid) {
        int prcant=0;

        try {
            for (int i = 0; i <items.size(); i++) {
                if (items.get(i).cod==prodid) {
                    prcant+=cant;
                }
            }
            return prcant;
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isProdStock(int pid) {
        if (pid==0) return false;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+pid);
            return P_productoObj.first().codigo_tipo.equalsIgnoreCase("P");
        } catch (Exception e) {
            return false;
        }
    }

    //endregion

    //region Aux

    private boolean validaData() {
        for (int i = 0; i <items.size(); i++) {
            if (items.get(i).unid>0) {
                if (items.get(i).bandera==0) return false;
            }
        }
        return true;
    }

    private void exit() {
        if (newitem) {
            for (int i = 0; i <items.size(); i++) {
                if (items.get(i).bandera==1) {
                    msgAskExit("Salir sin aplicar");return;
                }
            }
        }

        gl.retcant=-1;
        finish();
    }

    private String getProdName(int pid) {
        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+pid);
            return P_productoObj.first().desclarga;
        } catch (Exception e) {
            return "";
        }
    }

    private String getProdUM(int pid) {
        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+pid);
            return P_productoObj.first().unidbas;
        } catch (Exception e) {
            return "";
        }
    }

    private double prodPrecioItem(int cprod) {
        if (cprod==0) return 0;

        try {
            double pr=prc.prodPrecioBase(cprod, nivel);
            return mu.round(pr,2);
        } catch (Exception e) {
            addlog(new Object() {}.getClass().getEnclosingMethod().getName(), e.getMessage(), sql);
            return 0;
        }
    }

    //endregion

    //region Dialogs

    private void msgAskDelete(String msg) {
        try{
            AlertDialog.Builder dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem();
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

    private void msgAskExit(String msg) {

        try{
            AlertDialog.Builder dialog = new ExDialog(this);
            dialog.setMessage(msg);
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    gl.retcant=-1;
                    finish();
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

    private void msgAskApply(String msg) {
        AlertDialog.Builder dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void msgAskSave(String msg) {
        AlertDialog.Builder dialog = new ExDialog(this);
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveItem();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialog.show();

    }

    private void listaInvalidos() {

        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("MPos combo");

            dialog.setMessage("Uno o mas productos definidos como opcion de combo " +
                    "no está activa y por esta razón no se puede vender. " +
                    "Por favor informe a su superior");
            dialog.setIcon(R.drawable.ic_quest);

            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mostrarLista();
                }
            });

            dialog.show();
        }catch (Exception e){
            addlog(new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage(),"");
        }
    }

    private void mostrarLista() {

        try {
            extListDlg listdlg = new extListDlg();
            listdlg.buildDialog(OrdenMenu.this,"Producto de combo inactivo");

            for (int i = 0; i <app.citems.size(); i++) {
                listdlg.add(app.citems.get(i));
            }

            listdlg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                    try {
                        listdlg.dismiss();
                    } catch (Exception e) {}
                };
            });

            listdlg.setOnLeftClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listdlg.dismiss();
                }
            });

            listdlg.show();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_productoObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
            T_ordencomboprecioObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox2(e.getMessage());
        }

        if (browse==1) {
            browse=0;
            if (!gl.gstr.isEmpty()) agregaArticulo(app.codigoProducto(gl.gstr));
            return;
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    //endregion

}