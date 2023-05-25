package com.dtsgt.mpos;

import android.os.Bundle;
import android.view.View;

import com.dtsgt.base.clsClasses;
import com.dtsgt.firebase.fbPStock;
import com.dtsgt.firebase.fbPStockTrans;
import com.dtsgt.firebase.fbPStockTransExec;

public class FBTest extends PBase {

    private fbPStock fbb;
    private fbPStockTrans fbt;

    private Runnable rnCallBack,rnTransBack;

    private int idsuc,idalm,rnmode;
    private String uid,node,pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbtest);

        super.InitBase();

        idsuc=102;idalm=0;
        node="/"+idsuc+"/";
        pref=idsuc+"_"+idalm+"_";

        rnCallBack = new Runnable() {
            public void run() {
                runCallBack();
            }
        };

        rnTransBack = new Runnable() {
            public void run() {
                runTransBack();
            }
        };

        fbb=new fbPStock("PStock",gl.tienda);
        fbt=new fbPStockTrans("PStockTrans",gl.tienda);

    }

    //region Events

    public void doBtn1(View view) {
        execTrans();
        //setValue2();
    }

    public void doBtn2(View view) {
        String rnode;
        rnmode = 1;
        rnode="/"+idsuc+"/"+idsuc+"_"+idalm+"_20353x";
        getTransVal(rnode);
        //getValue(rnode);
    }


    //endregion

    //region Main

    private void execTrans() {
        try { //-NWEDJiWWuDAFUWEV0To
            fbPStockTransExec fbte=new fbPStockTransExec("PStockTrans",gl.tienda);
            fbte.processTrans("-NWEDJiWWuDAFUWEV0To",rnTransBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void runTransBack() {

    }

    private void setValue() {
        clsClasses.clsFbPStock item;

        try {

            item=newitem2(20353,23,0,"Botella IPA","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(20354,15,0,"Botella Lager","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(20355,25,0,"Botella Stout","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(21160,12,0,"Pechuga","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(21161, 5,0,"Ensalada","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(21223,10,0,"Cuadril","UN");fbb.setItem(node+pref+item.id,item);
            item=newitem2(21224,19,0,"Pierna","UN");fbb.setItem(node+pref+item.id,item);

            msgbox("OK");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setValue2() {
        clsClasses.clsFbPStockTrans item;

        try {
            item=newitemtr(20353,23,0,"Botella IPA","UN");fbt.addItem(node,item);
            item=newitemtr(20354,15,0,"Botella Lager","UN");fbt.addItem(node,item);
            item=newitemtr(20355,25,0,"Botella Stout","UN");fbt.addItem(node,item);

            msgbox("OK");
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }


    private clsClasses.clsFbPStock newitem2(int id,double cant,int idalm,String nombre,String um) {
        clsClasses.clsFbPStock ritem=clsCls.new clsFbPStock();

        ritem.id=id;
        ritem.idalm=idalm;
        ritem.cant=cant;
        ritem.nombre=nombre;
        ritem.um=um;

        return ritem;
    };

    private clsClasses.clsFbPStockTrans newitemtr(int id,double cant,int idalm,String nombre,String um) {
        clsClasses.clsFbPStockTrans ritem=clsCls.new clsFbPStockTrans();

        ritem.idruta=id;
        ritem.id=id;
        ritem.idalm=idalm;
        ritem.cant=cant;
        ritem.nombre=nombre;
        ritem.um=um;
        ritem.bandera=0;

        return ritem;
    };


    private void getValue(String rnode) {
        try {
            fbb.getItem(rnode,rnCallBack);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void getTransVal(String rnode) {
        clsClasses.clsFbPStock item;
        try {
            item=newitem2(20353,2,0,"Botella IPA","UN");

            fbb.transUpdateCant(item,null);
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void runCallBack() {
        try {
            switch (rnmode) {
                case 1:
                    msgbox(fbb.item.nombre+" : "+fbb.item.cant);
                    break;
                case 2:
                    msgbox(fbb.item.nombre+" : "+fbb.item.cant);
                    break;
            }

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