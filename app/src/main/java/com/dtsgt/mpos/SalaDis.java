package com.dtsgt.mpos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsDisTouchHandler;
import com.dtsgt.classes.clsP_res_mesaObj;

public class SalaDis extends PBase implements View.OnTouchListener,View.OnLongClickListener {

    private RelativeLayout board;

    private clsDisTouchHandler touch;
    private clsP_res_mesaObj P_res_mesaObj;
    private clsClasses.clsP_res_mesa item=clsCls.new clsP_res_mesa();

    private int dimX,dimY,dispY,unit,posx,posy;
    private double zoom=1;

    private int wrap=RelativeLayout.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_dis);

        super.InitBase();

        board=(RelativeLayout)findViewById(R.id.relsala);

        P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);

        setHandlers();
    }

    //region Events


    private void setHandlers() {
        try {
            board.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            board.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            initBoard();
                        }
                    }
            );
        } catch (Exception e){
            msgbox(e.getMessage());
        }
    }

    //endregion

    //region Main

    private void initBoard() {
        try {
            int[] location = new int[2];
            board.getLocationInWindow(location);
            dispY=location[1];

            dimX=board.getWidth();dimY=board.getHeight();
            unit=(int)(0.8*Math.floor(dimY/5));unit=(int) unit/2;unit=2*unit;

            touch=new clsDisTouchHandler(this,dimX,dimY,dispY,unit);

            toast(unit+ " / "+touch.grid);

            loadItems();

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void loadItems() {
        try {

            buildItems();//P_res_mesaObj.fill();

            board.removeAllViews();

            addItem(1);
            addItem(2);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void buildItems() {
        try {
            P_res_mesaObj.items.clear();

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=1;item.nombre="1";item.sucursal=gl.tienda;
            item.posx=0;item.posx=0;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=2;item.nombre="2";item.sucursal=gl.tienda;
            item.posx=0;item.posx=0;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=3;item.nombre="3";item.sucursal=gl.tienda;
            item.posx=0;item.posx=0;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void addItem(int id) {
        TextView tv;
        RelativeLayout.LayoutParams rlParamsName = new RelativeLayout.LayoutParams(wrap,wrap);
        int posx,posy;

        try {
            tv = new TextView(this);

            tv.setId(id);
            tv.setText("T"+id);tv.setTag("T"+id);
            tv.setTextColor(Color.BLACK);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(24);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.frame_dis);
            tv.setWidth(100);tv.setHeight(80);

            posx=50*id;posy=35*id;
            rlParamsName.setMargins(posx, posy, 0, 0);
            tv.setLayoutParams(rlParamsName);

            tv.setOnTouchListener(this);
            tv.setOnLongClickListener(this);
            tv.setLongClickable(true);

            board.addView(tv);

        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Handlers

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        touch.onTouch(view,motionEvent);
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        toast("long click ");
        return true;
    }
    //endregion

    //region Aux


    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    @Override
    protected void onResume() {
        super.onResume();
        try {
            P_res_mesaObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }



    //endregion

}