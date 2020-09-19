package com.dtsgt.classes;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.R;
import com.dtsgt.mpos.SalaDis;

public class clsDisMesa {

    private RelativeLayout board;
    private clsDisTouchHandler touch;

    private int wrap=RelativeLayout.LayoutParams.WRAP_CONTENT;

    public clsDisMesa(SalaDis owner, RelativeLayout pboard, clsClasses.clsP_res_mesa item) {
        board=pboard;
        touch=owner.touch;
        build(owner,item);
    }

    private void build(SalaDis owner,clsClasses.clsP_res_mesa item) {
        TextView tv;
        int posx,posy,sizex,sizeY;

        try {
            tv = new TextView(owner);

            tv.setId(item.codigo_mesa);
            tv.setText(item.nombre);tv.setTag("T"+item.codigo_mesa);
            tv.setTextColor(Color.BLACK);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(24);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.frame_dis);

            sizex=(int) (item.largo*touch.unit*touch.zoom);
            sizeY=(int) (item.ancho*touch.unit*touch.zoom);
            tv.setWidth(sizex);tv.setHeight(sizeY);

            RelativeLayout.LayoutParams rlParamsName = new RelativeLayout.LayoutParams(wrap,wrap);
            posx=(int) (item.posx*touch.unit*touch.zoom);
            posy=(int) (item.posy*touch.unit*touch.zoom);
            rlParamsName.setMargins(posx,posy,0,0);
            tv.setLayoutParams(rlParamsName);

            tv.setOnTouchListener(owner);
            tv.setOnLongClickListener(owner);
            tv.setLongClickable(true);

            board.addView(tv);

        } catch (Exception e) {
            //toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

}
