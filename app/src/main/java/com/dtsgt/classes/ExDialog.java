package com.dtsgt.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.dtsgt.mpos.R;

public class ExDialog extends  AlertDialog.Builder {

    public ExDialog(Context context) {
        super(context);

        Activity activity=(Activity) context;
        View titleView = activity.getLayoutInflater().inflate(R.layout.dialogstyle, null);
        setCustomTitle(titleView);

    }



}
