package com.dtsgt.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ExDialogT extends  AlertDialog.Builder {

    public ExDialogT(Context context) {
        super(context);

        Activity activity=(Activity) context;
        //View titleView = activity.getLayoutInflater().inflate(R.layout.dialogstyle, null);
        //setCustomTitle(titleView);
    }

    @Override
    public AlertDialog show() {
        Button btnPos,btnNeg,btnNeut;

        AlertDialog adg=super.show();

        TextView textView = (TextView) adg.getWindow().findViewById(android.R.id.message);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(24);
        textView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);

        int btntextsize=24;
        int btnbackcolor=Color.parseColor("#63CE60");

        try {
            btnPos=adg.getButton(DialogInterface.BUTTON_POSITIVE);
            btnPos.setTextSize(btntextsize);
            btnPos.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            btnPos.setBackgroundColor(btnbackcolor);
        } catch (Exception e) {}

        try {
            btnNeg=adg.getButton(DialogInterface.BUTTON_NEGATIVE);
            btnNeg.setTextSize(btntextsize);
            btnNeg.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            btnNeg.setBackgroundColor(btnbackcolor);
        } catch (Exception e) {}

        try {
            btnNeut=adg.getButton(DialogInterface.BUTTON_NEUTRAL);
            btnNeut.setTextSize(btntextsize);
            btnNeut.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            btnNeut.setBackgroundColor(btnbackcolor);
        } catch (Exception e) {}

        return adg;
    }

    public void setItems(ArrayList<String> citems, DialogInterface.OnClickListener onClickListener) {}
}