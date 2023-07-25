package com.dtsgt.mpos;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.dtsgt.fragment.testFrag2;
import com.dtsgt.fragment.testFrag1;

public class FragmentTest extends PBase {

    private int fri=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);

        super.InitBase();
    }

    public void doBtn1(View view) {
        doFragment();
    }

    private void doFragment() {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            testFrag1 fr= testFrag1.newInstance(this,"P_LINEA","WHERE ACTIVO=1");
            testFrag2 fr2= new testFrag2();

            fri++;
            if (fri % 2 ==0) {
                ft.replace(R.id.fragmentContainerView, (Fragment) fr2);
            } else {
                ft.replace(R.id.fragmentContainerView, (Fragment) fr);
            }

            ft.commit();
            fr=null;fr2=null;
        } catch (Exception e) {
            msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    //endregion
}