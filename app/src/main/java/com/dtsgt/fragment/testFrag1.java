package com.dtsgt.fragment;

import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsgt.classes.clsP_lineaObj;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class testFrag1 extends fragBase implements View.OnClickListener {

    private TextView lbl1;
    private Button btn1;
    private View fragmentView;

    private clsP_lineaObj P_lineaObj;

    private String mParam1,mParam2;

    //region Fragment Handlers

    public testFrag1(PBase owner) {
        super(owner);

        try {
            P_lineaObj=new clsP_lineaObj(owner,owner.Con,owner.db);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    public static testFrag1 newInstance(PBase owner,String param1,String param2) {
        testFrag1 fragment = new testFrag1(owner);

        Bundle args = new Bundle();
        args.putString("param1",param1);
        args.putString("param2",param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_test, container, false);

        lbl1 =  fragmentView.findViewById(R.id.lbltest);
        btn1 =  fragmentView.findViewById(R.id.button5);btn1.setOnClickListener(this);

        lbl1.setText(mParam1+" "+gl.codigo_ruta);

        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        int vid=v.getId();

        try {
            if (vid==R.id.button5) {
                button1click();
            }
        } catch (Exception e) {
            toast(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            P_lineaObj.reconnect(owner.Con, owner.db);
        } catch (Exception e) {
            toast(new Object() {}.getClass().getEnclosingMethod().getName() + " . " + e.getMessage());
        }
    }

    //endregion

    //region Main

    private void button1click() {
        try {
            P_lineaObj.fill(mParam2);
            String ss=P_lineaObj.first().nombre;
            lbl1.setText(ss);
        } catch (Exception e) {
            toast(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion

    //region Aux

    protected void toast(String msg) {
        Toast toast= Toast.makeText(this.getActivity().getApplicationContext(),msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    //endregion

}