package com.dtsgt.classes;

import android.view.View;
import android.widget.RelativeLayout;

import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.SalaDis;

import java.util.ArrayList;

public class clsDisSala {

    public double zoom;

    private SalaDis owner;
    private RelativeLayout board;

    private ArrayList<clsDisMesa> items=new ArrayList<clsDisMesa>();
    private ArrayList<clsClasses.clsP_res_mesa> mesas;

    public clsDisSala(SalaDis saladis,RelativeLayout pboard, ArrayList<clsClasses.clsP_res_mesa> pmesas) {
        owner=saladis;
        board=pboard;
        mesas=pmesas;
        zoom=1;
    }

    //region Public

    public void setZoom() {
        zoom=owner.zoom;
        owner.touch.setZoom(zoom);
        render();
    }

    public void save() {

    }

    //endregion

    //region Private

    public void render() {
        clsDisMesa item;

        try {
            board.removeAllViews();
            items.clear();

            for (int i = 0; i <mesas.size(); i++) {
                item=new clsDisMesa(owner,board,mesas.get(i));
                items.add(item);
            }
        } catch (Exception e) {
        }
    }

    public void update(View view, double posx, double posy) {
        String tag;
        int itemid;

        try {
            tag=view.getTag().toString();
            itemid=Integer.parseInt(tag.substring(1));
            if (tag.indexOf("T")==0) {
                posx=posx/(zoom*owner.unit);
                posy=posy/(zoom*owner.unit);
                actualizaMesa(itemid,posx,posy);
            }
            if (tag.indexOf("L")==0) {

            }
        } catch (Exception e) {
        }
    }

    private void actualizaMesa(int itemid,double posx, double posy) {
        for (int i = 0; i <mesas.size(); i++) {
            if (mesas.get(i).codigo_mesa==itemid) {
                mesas.get(i).posx=posx;
                mesas.get(i).posy=posy;
                return;
            }
        }
    }

    //endregion

}
