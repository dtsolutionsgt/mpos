package com.dtsgt.classes;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.dtsgt.mpos.R;
import com.dtsgt.mpos.SalaDis;

public class clsDisTouchHandler {

    public int unit,grid;
    public double zoom;

    private SalaDis owner;

    private int posx,posy,sizex,sizey;
    private int dimX,dimY,dispY,relx,rely,oldx,oldy;

    private boolean longclick=false;

    private final int longcbound=50;

    public clsDisTouchHandler(SalaDis owner) {
        this.owner=owner;
        dimX=owner.dimX;
        dimY=owner.dimY;
        dispY=owner.dispY;
        unit=(int) owner.unit;
        grid=(int) (owner.unit/4);
        zoom=1;
    }

    //region Main

    public void onTouch(View view, MotionEvent motionEvent) {
        final int X = (int) motionEvent.getRawX();
        final int Y = (int) motionEvent.getRawY();

        sizex=view.getWidth();sizey=view.getHeight();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                longclick=false;
                handler.postDelayed(mLongPressed, 500);

                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                relx=X-lParams.leftMargin;oldx=X;
                rely=Y-lParams.topMargin;oldy=Y;
                view.setBackgroundResource(R.drawable.frame_dis_sel);
                break;

            case MotionEvent.ACTION_UP:

                handler.removeCallbacks(mLongPressed);

                RelativeLayout.LayoutParams layoutParamssn = (RelativeLayout.LayoutParams) view.getLayoutParams();
                snap(X-relx,Y-rely);
                layoutParamssn.leftMargin = posx;
                layoutParamssn.topMargin = posy;
                view.setLayoutParams(layoutParamssn);

                if (longclick) {
                    view.performLongClick();
                } else {
                    owner.sala.update(view,posx,posy);
                }

                view.setBackgroundResource(R.drawable.frame_dis);
                longclick=false;

                break;

            case MotionEvent.ACTION_MOVE:

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin=X-relx;
                layoutParams.topMargin=Y-rely;
                view.setLayoutParams(layoutParams);

                if (Math.abs(oldx-X)> longcbound | Math.abs(oldx-X)> longcbound) handler.removeCallbacks(mLongPressed);
                break;
        }

    }

    public void setZoom(double vzoom) {
        zoom=vzoom;
    }

    //endregion

    //region Aux

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            longclick = true;
        }
    };

    private void snap(int cx,int cy) {
        int gz;
        double sp;

        if (cx>dimX-sizex-2*grid) cx=dimX-sizex-2*grid;
        if (cx<grid) cx=grid;
        if (cy>dimY+dispY-sizey-2*grid) cy=dimY+dispY-sizey-2*grid;
        if (cy<dispY+grid) cy=dispY+grid;

        gz=(int) (grid*zoom);if (gz<2) gz=2;
        sp=cx/gz;sp=gz*Math.round(sp);
        posx=(int) sp;
        sp=cy/gz;sp=gz*Math.round(sp);
        posy=(int) sp;

    }

    //endregion

}
