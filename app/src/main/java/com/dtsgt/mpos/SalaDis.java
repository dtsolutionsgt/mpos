package com.dtsgt.mpos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.classes.clsDisSala;
import com.dtsgt.classes.clsDisTouchHandler;
import com.dtsgt.classes.clsP_res_mesaObj;

public class SalaDis extends PBase implements View.OnTouchListener,View.OnLongClickListener,View.OnClickListener {

    private RelativeLayout board;
    private TextView lblzoom,lblchar;


    public int dimX,dimY,dispY,unit;
    public clsDisTouchHandler touch;
    public clsDisSala sala;
    public double zoom=1;

    private clsP_res_mesaObj P_res_mesaObj;
    private clsClasses.clsP_res_mesa item=clsCls.new clsP_res_mesa();


    //private int wrap=RelativeLayout.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_dis);

        super.InitBase();

        board=findViewById(R.id.relsala);
        lblzoom=findViewById(R.id.textView201);
        lblchar=findViewById(R.id.textView202);

        P_res_mesaObj=new clsP_res_mesaObj(this,Con,db);
        sala=new clsDisSala(this,board,P_res_mesaObj.items);

        setHandlers();
    }

    //region Events

    public void doZoomOut(View view) {
        if (zoom<4) zoom+=0.1;setZoom();
    }

    public void doZoomIn(View view) {
        if (zoom>0.1) zoom-=0.1;setZoom();
    }


    public void doReset(View view) {
        msgAskReset("Reiniciar el diseño de la sala");
    }

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

            touch=new clsDisTouchHandler(this);

            loadItems();

        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }

    private void loadItems() {
        try {
            buildItems();//P_res_mesaObj.fill();
            sala.setZoom();
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void setZoom() {
        int zv=(int) (zoom*100);

        lblzoom.setText(zv+" %");
        sala.setZoom();
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
        toast("long click "+view.getTag().toString());
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    //endregion

    //region Aux

    private void buildItems() {
        try {
            P_res_mesaObj.items.clear();

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=1;item.nombre="1";item.sucursal=gl.tienda;
            item.posx=1;item.posy=1;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=2;item.nombre="2";item.sucursal=gl.tienda;
            item.posx=0;item.posy=2;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

            item=clsCls.new clsP_res_mesa();
            item.codigo_mesa=3;item.nombre="3";item.sucursal=gl.tienda;
            item.posx=2;item.posy=2;
            item.largo=1;item.ancho=1;
            P_res_mesaObj.items.add(item);

        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Dialogs


    private void msgAskReset(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Title");
        dialog.setMessage("¿" + msg + "?");

        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ;
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
            P_res_mesaObj.reconnect(Con,db);
        } catch (Exception e) {
            msgbox(e.getMessage());
        }
    }




    //endregion

}