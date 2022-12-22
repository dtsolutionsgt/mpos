package com.dtsgt.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class extMontoDlg {

    private TextView mTitleLabel,mBtnLeft,mBtnMid,mBtnRight,mLblPass,lblKeyDP,lblTitle;
    private TextView k0,k1,k2,k3,k4,k5,k6,k7,k8,k9,kpd,kc;
    private ImageView kb,ke;
    private RelativeLayout mRel,mRelTop,mRelBot,mRelPass;
    private LinearLayout mbuttons;

    private Dialog dialog;
    private Context cont;

    private clsKeybHandler khand;

    private int buttonCount,selidx;
    private int bwidth=420,bheight=850,mwidth=0,mheight=0,mlines=6,mminlines=1;
    private String pass;

    //region Public methods

    private void buildDialogbase(Activity activity,String titletext,String butleft,String butmid,String butright) {

        dialog = new Dialog(activity);
        cont=dialog.getContext();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.extmontodlg);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        mRel = dialog.findViewById(R.id.extlistdialogrel);
        mRelTop = dialog.findViewById(R.id.xdlgreltop);
        mRelBot = dialog.findViewById(R.id.xdlgrelbut);
        mRelPass = dialog.findViewById(R.id.relpass);
        mLblPass = dialog.findViewById(R.id.textView295);
        lblKeyDP = dialog.findViewById(R.id.textView110);
        lblTitle = dialog.findViewById(R.id.textView294);
        mbuttons = dialog.findViewById(R.id.linbuttons);

        initKeys();

        khand = new clsKeybHandler(cont, mLblPass, lblKeyDP,true);
        khand.clear();
        khand.enable();
        lblKeyDP.setVisibility(View.VISIBLE);

        mTitleLabel = dialog.findViewById(R.id.lbltitulo);
        mTitleLabel.setText(titletext);

        mBtnLeft = dialog.findViewById(R.id.btnexit);
        mBtnLeft.setText(butleft);
        mBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mBtnMid = dialog.findViewById(R.id.btndel);
        mBtnMid.setText(butmid);
        mBtnMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mBtnRight = dialog.findViewById(R.id.btnadd);
        mBtnRight.setText(butright);
        mBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        switch (buttonCount) {
            case 1:
                mBtnMid.setVisibility(View.GONE);mBtnRight.setVisibility(View.GONE);
                mbuttons.setWeightSum(1);break;
            case 2:
                mBtnRight.setVisibility(View.GONE);
                mbuttons.setWeightSum(2);break;
            case 3:
                mbuttons.setWeightSum(3);break;
        }

        mwidth=0;mheight=0;
        mlines =0;
        selidx=-1;

    }

    public void buildDialog(Activity activity,String titletext) {
        buttonCount=1;
        buildDialogbase(activity,titletext,"Salir","","");
    }

    public void buildDialog(Activity activity,String titletext,String butleft) {
        buttonCount=1;
        buildDialogbase(activity,titletext,butleft,"","");
    }

    public void buildDialog(Activity activity,String titletext,String butleft,String butmid) {
        buttonCount=2;
        buildDialogbase(activity,titletext,butleft,butmid,"");
    }

    public void buildDialog(Activity activity,String titletext,String butleft,String butmid,String butright) {
        buttonCount=3;
        buildDialogbase(activity,titletext,butleft,butmid,butright);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void onEnterClick(@Nullable View.OnClickListener l) {
        ke.setOnClickListener(l);
    }

    public void setOnLeftClick(@Nullable View.OnClickListener l) {
        mBtnLeft.setOnClickListener(l);
    }

    public void setOnMiddleClick(@Nullable View.OnClickListener l) {
        mBtnMid.setOnClickListener(l);
    }

    public void setOnRightClick(@Nullable View.OnClickListener l) {
        mBtnRight.setOnClickListener(l);
    }

    public void setDPVisible(boolean visible) {
        if (visible)  lblKeyDP.setVisibility(View.VISIBLE);else  lblKeyDP.setVisibility(View.INVISIBLE);
    }

    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    public String getInput() {
        return pass+"";
    }

    public void show() {
        dialog.show();
    }

    //endregion

    //region Private

    public class clsListDialogItem {
        public int  idresource;
        public String codigo;
        public String text;
        public String text2;
        public String password;
    }


    private void initKeys() {
        View.OnClickListener cl;

        cl=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doKey(v);
            }
        };

        k0 = dialog.findViewById(R.id.textView114);k0.setOnClickListener(cl);
        k1 = dialog.findViewById(R.id.textView112);k1.setOnClickListener(cl);
        k2 = dialog.findViewById(R.id.textView113);k2.setOnClickListener(cl);
        k3 = dialog.findViewById(R.id.textView109);k3.setOnClickListener(cl);
        k4 = dialog.findViewById(R.id.textView100);k4.setOnClickListener(cl);
        k5 = dialog.findViewById(R.id.textView101);k5.setOnClickListener(cl);
        k6 = dialog.findViewById(R.id.textView105);k6.setOnClickListener(cl);
        k7 = dialog.findViewById(R.id.textView98);k7.setOnClickListener(cl);
        k8 = dialog.findViewById(R.id.textView99);k8.setOnClickListener(cl);
        k9 = dialog.findViewById(R.id.textView102);k9.setOnClickListener(cl);

        kpd = dialog.findViewById(R.id.textView110);kpd.setOnClickListener(cl);
        kc = dialog.findViewById(R.id.textView106);kc.setOnClickListener(cl);
        kb = dialog.findViewById(R.id.imageView24);kb.setOnClickListener(cl);
        ke = dialog.findViewById(R.id.imageView25);ke.setOnClickListener(cl);

    }

    private void doKey(View view) {
        String ss,pp;

        try {
            ss = view.getTag().toString()+"";
            khand.handleKey(ss);
            pass = khand.val+"";
        } catch (Exception e) {
            pass="";
        }
    }

    //endregion

}
