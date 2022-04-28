package com.dtsgt.mpos;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PrintView extends PBase {

    private TextView lbl1;

    private String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_view);

        super.InitBase();

        lbl1 = (TextView) findViewById(R.id.textView2);

        listItems();
    }

    //region Events

    public void doPrint(View view) {
        app.doPrint();
        finish();
    }

    //endregion

    //region Main

    private void listItems() {
        try {
            fname = Environment.getExternalStorageDirectory()+"/print.txt";
            File file = new File(fname);
            lbl1.setText(getFileContent(file));
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
         }
    }

    //endregion

    //region Aux

    public String getFileContent(File file) throws IOException {
        String str = "",ss;
        BufferedReader bf = null;

        try {
            bf = new BufferedReader(new FileReader(file));
            while(bf.ready())

                try {
                    ss= bf.readLine();ss=ss.replace("null","");
                    str +=ss+"\n";
                } catch (Exception e){
                }

        } catch (Exception e){
        }  finally {
            bf.close();
        }
        return str;
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events


    //endregion


}