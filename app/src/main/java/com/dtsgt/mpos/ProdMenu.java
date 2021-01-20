package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.ExDialog;
import com.dtsgt.classes.clsP_prodmenuObj;
import com.dtsgt.classes.clsP_prodmenuopcdetObj;
import com.dtsgt.classes.clsP_productoObj;
import com.dtsgt.classes.clsT_comboObj;
import com.dtsgt.ladapt.ListAdaptOpcion;

import java.util.ArrayList;

public class ProdMenu extends PBase {

    private ListView listView;
    private TextView lbl1,lbl2,lbl3;
    private ImageView img1;

    private ListAdaptOpcion adapter;
    private clsT_comboObj T_comboObj;
    private clsP_productoObj P_productoObj;

    private ArrayList<clsClasses.clsOpcion> items= new ArrayList<clsClasses.clsOpcion>();
    private ArrayList<String> lcode = new ArrayList<String>();
    private ArrayList<String> lname = new ArrayList<String>();

    private Precio prc;

    private int cant,lcant, uitemid;
    private boolean newitem;
    private String ststr,prodname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_menu);

        super.InitBase();

        listView = (ListView) findViewById(R.id.listView);
        lbl1 = (TextView) findViewById(R.id.textView93);
        lbl2 = (TextView) findViewById(R.id.textView117);
        lbl3 = (TextView) findViewById(R.id.textView116);
        img1 = (ImageView) findViewById(R.id.imageView27);

        P_productoObj = new clsP_productoObj(this, Con, db);
        T_comboObj = new clsT_comboObj(this, Con, db);
        prc = new Precio(this, mu, 2);

        setHandlers();

        cant=1;
        //lcant = gl.limcant;

        uitemid = Integer.parseInt(gl.menuitemid);
        newitem = gl.newmenuitem;

        lbl1.setText(gl.gstr);
        lbl2.setText(""+cant);

        if (newitem) {
            newItem();img1.setVisibility(View.INVISIBLE);
        } else {
            listItems();
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
                        listOptions(item.Name,item.codigo_menu_opcion);

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

        int menuid,selid,idcombo;

        listMenuItems();

        try {

            clsT_comboObj combo=new clsT_comboObj(this,Con,db);
            combo.fill("WHERE IdCombo="+ uitemid);
            cant=combo.first().cant; lbl2.setText(""+cant);

            for (int i = 0; i <items.size(); i++) {

                menuid=items.get(i).codigo_menu_opcion;
                combo.fill("WHERE (IdCombo="+ uitemid+") AND (CODIGO_MENU="+menuid+")");

                try {
                    selid=combo.first().idseleccion;

                    if (selid>0) {
                        items.get(i).cod=selid;
                        items.get(i).Name=getProdName(selid);
                        items.get(i).bandera=1;
                    } else {
                        items.get(i).cod=0;
                        //items.get(i).Name=combo.first().;
                        items.get(i).bandera=0;
                    }

                    items.get(i).cant=combo.first().cant;
                } catch (Exception e) {
                }
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            mu.msgbox2(e.getMessage());
        }
    }

    private void newItem() {
        uitemid=T_comboObj.newID("SELECT MAX(IdCombo) FROM T_COMBO");gl.menuitemid=""+uitemid;
        listMenuItems();
    }

    private void listMenuItems() {

        clsP_prodmenuObj P_menuObj=new clsP_prodmenuObj(this,Con,db);
        clsClasses.clsOpcion item;

        try {

            items.clear();
            //P_menuObj.fill("WHERE CODIGO_PRODUCTO='"+gl.prodmenu+"' ORDER BY ORDEN,NOMBRE");
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

                items.add(item);
            }

            adapter=new ListAdaptOpcion(this,items);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            mu.msgbox2(e.getMessage());
        }
    }

    private boolean saveItem() {
        clsClasses.clsT_combo item;

        try {

            String um=getProdUM(gl.prodmenu);

            double prec = prc.precio(gl.prodid, cant, gl.nivel, um, gl.umpeso, gl.dpeso,um,gl.prodmenu);

            double impval = prc.impval;
            double desc=prc.desc;
            double descmon = prc.descmon;
            double tot = prc.tot;

            db.beginTransaction();

            if (!newitem){
                db.execSQL("DELETE FROM T_COMBO WHERE IdCombo="+uitemid);
                db.execSQL("DELETE FROM T_VENTA WHERE (PRODUCTO='"+gl.prodid+"') AND (EMPRESA='"+uitemid+"')");
            }

            for (int i = 0; i <items.size(); i++) {

                item=clsCls.new clsT_combo();

                //#EJC20200524: Revisar
                item.codigo_menu=items.get(i).codigo_menu_opcion;
                item.idcombo=uitemid;
                item.cant=cant;
                item.unid=items.get(i).unid;
                item.idseleccion=items.get(i).cod;
                item.orden=items.get(i).orden;

                T_comboObj.add(item);
            }

            ins.init("T_VENTA");
            ins.add("PRODUCTO",gl.prodid);
            ins.add("EMPRESA",""+uitemid);
            ins.add("UM","UNI");
            ins.add("CANT",cant);
            ins.add("UMSTOCK","UNI");
            ins.add("FACTOR",1);
            ins.add("PRECIO",prec);
            ins.add("IMP",impval);
            ins.add("DES",desc);
            ins.add("DESMON",descmon);
            ins.add("TOTAL",tot);
            ins.add("PRECIODOC",prec);
            ins.add("PESO",0);
            ins.add("VAL1",0);
            ins.add("VAL2",1);
            ins.add("VAL3",0);
            ins.add("VAL4",""+uitemid);
            ins.add("PERCEP",0);

            db.execSQL(ins.sql());

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

    private void listOptions(String title,int idoption) {

        clsP_productoObj prod=new clsP_productoObj(this,Con,db);
        clsP_prodmenuopcdetObj opc=new clsP_prodmenuopcdetObj(this,Con,db);
        clsClasses.clsOpcion item;

        final AlertDialog Dialog;
        int cod;

        try {

            lcode.clear();lname.clear();

            opc.fill("WHERE CODIGO_MENU_OPCION="+idoption);

            for (int i = 0; i <opc.count; i++) {
                //#EJC20200524: Buscar aquí los productos de cada menu_opcion.
                cod=opc.items.get(i).codigo_producto;
                lcode.add(""+cod);
                lname.add(getProdName(cod));
            }

            final String[] selitems = new String[lname.size()];

            for (int i = 0; i < lname.size(); i++) {
                selitems[i] = lname.get(i);
            }

            ExDialog mMenuDlg = new ExDialog(this);

            mMenuDlg.setItems(selitems , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try {
                        items.get(selidx).cod=Integer.parseInt(lcode.get(item));
                        items.get(selidx).Name=lname.get(item);
                        items.get(selidx).bandera=1;
                        adapter.notifyDataSetChanged();
                        validaStock();
                    } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            });

            mMenuDlg.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

            Dialog = mMenuDlg.create();
            Dialog.show();

        } catch (Exception e) {
            msgbox2(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void deleteItem() {
        try {
            db.beginTransaction();

            sql="DELETE FROM T_VENTA WHERE VAL4='"+uitemid+"'";
            db.execSQL(sql);
            db.execSQL("DELETE FROM T_COMBO WHERE (IdCombo="+uitemid+")");
            db.execSQL(sql);

            db.setTransactionSuccessful();
            db.endTransaction();

            gl.retcant=0;
            finish();

        } catch (Exception e) {
            db.endTransaction();
            msgbox(e.getMessage());
        }
    }


    //endregion

    //region Disponible

    private boolean validaStock() {
        boolean flag=true;
        ststr="";

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
        Cursor dt;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);

            sql="SELECT CANT FROM P_STOCK WHERE (CODIGO='"+P_productoObj.first().codigo+"') AND (UNIDADMEDIDA='"+P_productoObj.first().unidbas+"')";
            dt=Con.OpenDT(sql);

            if (dt.getCount()>0) {
                dt.moveToFirst();
                return dt.getInt(0);
            } return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int cantProdVenta(int prodid) {
        Cursor dt;

        try {
            P_productoObj.fill("WHERE CODIGO_PRODUCTO="+prodid);
            prodname=P_productoObj.first().desclarga;

            sql="SELECT SUM(CANT) FROM T_VENTA WHERE PRODUCTO='"+P_productoObj.first().codigo+"'";
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

    //endregion

    //region Dialogs

    private void msgAskDelete(String msg) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("MPos");
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("MPos");

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("MPos");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("MPos");
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

    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_productoObj.reconnect(Con,db);
            T_comboObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox2(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    //endregion

}
