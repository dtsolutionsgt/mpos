package com.dtsgt.classes;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.dtsgt.mpos.R;

public class clsDisTouchHandler {

    public int grid;
    public double zoom;

    private Context cont;

    private int posx,posy;
    private int unit,dimX,dimY,dispY,relx,rely,oldx,oldy;

    private boolean longclick=false;

    private final int longcbound=50;

    public clsDisTouchHandler(Context context, int width, int height, int top, int unitsize) {
        cont=context;
        dimX=width;
        dimY=height;
        dispY=top;
        unit=unitsize;
        grid=(int) unit/4;
        zoom=1;
    }

    //region Main

    public void onTouch(View view, MotionEvent motionEvent) {
        final int X = (int) motionEvent.getRawX();
        final int Y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                longclick=false;
                handler.postDelayed(mLongPressed, 1000);

                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                relx = X - lParams.leftMargin;oldx=X;
                rely = Y - lParams.topMargin;oldy=Y;
                view.setBackgroundResource(R.drawable.frame_dis_sel);
                break;

            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(mLongPressed);

                if (longclick) {
                    view.performLongClick();
                } else {
                    RelativeLayout.LayoutParams layoutParamssn = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    snap(X-relx,Y-rely);
                    layoutParamssn.leftMargin = posx;
                    layoutParamssn.topMargin = posy;
                    view.setLayoutParams(layoutParamssn);
                }

                view.setBackgroundResource(R.drawable.frame_dis);
                longclick=false;

                break;

            case MotionEvent.ACTION_MOVE:

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X-relx;
                layoutParams.topMargin = Y-rely;
                view.setLayoutParams(layoutParams);

                if (Math.abs(oldx-X)> longcbound | Math.abs(oldx-X)> longcbound) handler.removeCallbacks(mLongPressed);
                break;
        }

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

        gz=(int) (grid*zoom);if (gz<2) gz=2;
        sp=cx/gz;sp=gz*Math.round(sp);
        posx=(int) sp;
        sp=cy/gz;sp=gz*Math.round(sp);
        posy=(int) sp;

    }

    //endregion

}
