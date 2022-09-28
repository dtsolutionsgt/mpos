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

public class extListPassDlg {

    /*

        public void buildDialog(Activity activity,String titletext,String buttontext)
        public void buildDialog(Activity activity,String titletext)  buttontext="Salir"
        public void dismiss()
        public void add(int idresource,String text)  idresource=0  sin imagen
        public void clear()
        public void setWidth(int pWidth)   min 100
        public void setHeight(int pHeight) min 100
        public void setLines(int pLines)   min 1
        public void show()

        public void setOnExitListener(@Nullable View.OnClickListener l)
        public void setOnItemClickListener(@Nullable OnItemClickListener l)

     */

    private ListView mList;
    private TextView mTitleLabel,mBtnLeft,mBtnMid,mBtnRight,mLblPass,lblKeyDP;
    private TextView k0,k1,k2,k3,k4,k5,k6,k7,k8,k9,kpd,kc;
    private ImageView kb,ke;
    private RelativeLayout mRel,mRelTop,mRelBot,mRelPass;
    private LinearLayout mbuttons;

    private Dialog dialog;
    private Context cont;
    private Adapter adapter;

    private clsKeybHandler khand;

    public ArrayList<clsListDialogItem> items=new ArrayList<clsListDialogItem>();

    private int buttonCount,selidx,validUser;
    private int bwidth=420,bheight=850,mwidth=0,mheight=0,mlines=6,mminlines=1;
    private String pass,validname;
    private boolean validpass;

    //region Public methods

    private void buildDialogbase(Activity activity,String titletext,String butleft,String butmid,String butright) {

        dialog = new Dialog(activity);
        cont=dialog.getContext();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.extlistpassdlg);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        mRel = dialog.findViewById(R.id.extlistdialogrel);
        mRelTop = dialog.findViewById(R.id.xdlgreltop);
        mRelBot = dialog.findViewById(R.id.xdlgrelbut);
        mRelPass = dialog.findViewById(R.id.relpass);
        mLblPass = dialog.findViewById(R.id.textView295);
        lblKeyDP = dialog.findViewById(R.id.textView110);
        mbuttons = dialog.findViewById(R.id.linbuttons);

        mLblPass.setTransformationMethod(new PasswordTransformationMethod());

        initKeys();

        khand = new clsKeybHandler(cont, mLblPass, lblKeyDP,true);
        khand.clear();
        khand.enable();
        lblKeyDP.setVisibility(View.INVISIBLE);

        mList = dialog.findViewById(R.id.listview1);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                focusItem(position);
                    //dialog.dismiss();
            };
        });

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
        items.clear();
        selidx=-1;
        validpass=false;
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

    public void setOnItemClickListener(@Nullable OnItemClickListener l) {
        mList.setOnItemClickListener(l);
    }

    public void addpassword(int codigo,String text,String pass) {
        clsListDialogItem item = new clsListDialogItem();

        item.idresource=0;
        item.codigo=codigo+"";
        item.text=text;
        item.text2="";
        item.password=pass;

        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public void setWidth(int pWidth) {
        mwidth=pWidth;
        if (mwidth<100) mwidth=0;
    }

    public void setHeight(int pHeight) {
        mheight=pHeight;
        if (mheight<100) mheight=0;
    }

    public void setLines(int pLines) {
        mlines=pLines;
        if (mlines<1) mlines=0;
        if (mlines>0) mheight=0;
    }

    public void setMinLines(int pLines) {
        mminlines=pLines;
        if (mminlines<1) mminlines=1;
    }

    public String getText(int index) {
        try {
            return items.get(index).text;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCodigo(int index) {
        try {
            return items.get(index).codigo;
        } catch (Exception e) {
            return "";
        }
    }

    public int getCodigoInt(int index) {
        try {
            return Integer.parseInt(items.get(index).codigo);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getPassword(int index) {
        try {
            return items.get(index).password;
        } catch (Exception e) {
            return "";
        }
    }

    public void focusItem(int position) {
        selidx=position;
        adapter.setSelectedIndex(position);
    }

    public String getInput() {
        return pass+"";
    }

    public boolean validPassword() {return validpass;}

    public int validUserId() {
        return validUser;
    }

    public String validUserName() {
        return validname;
    }

    public void show() {
        int fwidth,fheight,icount,rlcount;
        int itemHeight,headerHeight,footerHeight;

        fwidth=bwidth;fheight=bheight;

        adapter=new Adapter(cont);
        mList.setAdapter(adapter);
        icount=adapter.getCount();rlcount=icount;
        if (icount<1) return;


        if (mlines>0) {
            icount=mlines;mheight=0;
        }
        if (icount>rlcount) icount=rlcount;
        if (icount<mminlines) icount=mminlines;

        if (fwidth==0) fwidth=bwidth;

        if (mwidth*mheight>0) {
            fwidth=mwidth;
            fheight=mheight;

        } else {

            try {
                ListAdapter adap = mList.getAdapter();
                View listItem = adap.getView(0,null,mList);
                listItem.measure(0,0);
                itemHeight=listItem.getMeasuredHeight()+1;

                headerHeight=mRelTop.getLayoutParams().height+7;
                footerHeight=mRelBot.getLayoutParams().height+mRelPass.getLayoutParams().height+7;

                fheight=icount*itemHeight+headerHeight+footerHeight;
            } catch (Exception e) {
                fwidth=bwidth;fheight=bheight;
            }
        }

        DisplayMetrics displayMetrics = cont.getResources().getDisplayMetrics();
        int dispw = displayMetrics.widthPixels;dispw=(int) (0.9*dispw);
        int disph = displayMetrics.heightPixels;disph=(int) (0.9*disph);

        if (fwidth>dispw) fwidth=dispw;
        if (fheight>disph) fheight=disph;

        mRel.getLayoutParams().width = fwidth;
        mRel.getLayoutParams().height =fheight;

        dialog.getWindow().setLayout(fwidth, fheight);

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

    private class Adapter extends BaseAdapter {

        private LayoutInflater l_Inflater;
        private int selectedIndex;

        public Adapter(Context context) {
            l_Inflater = LayoutInflater.from(context);
            selectedIndex = -1;
        }

        public void setSelectedIndex(int ind) {
            selectedIndex = ind;
            notifyDataSetChanged();
        }

        public void refreshItems() {
            notifyDataSetChanged();
        }

        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = l_Inflater.inflate(R.layout.extlistdlgitem, null);
                holder = new ViewHolder();

                holder.text  = convertView.findViewById(R.id.lbltext);
                holder.text2 = convertView.findViewById(R.id.textView284);
                holder.icon  = convertView.findViewById(R.id.imgicon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(items.get(position).text+" ");
            holder.text2.setText(items.get(position).text2+" ");
            if (items.get(position).text2.isEmpty()) {
                holder.text2.setVisibility(View.GONE);
            } else {
                holder.text2.setVisibility(View.VISIBLE);
            }

            try {
                if (items.get(position).idresource != 0) {
                    holder.icon.setImageResource(items.get(position).idresource);
                    holder.icon.setVisibility(View.VISIBLE);
                } else {
                    holder.icon.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.icon.setVisibility(View.GONE);
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                //convertView.setBackgroundColor(Color.parseColor("#CCE6F3"));
                convertView.setBackgroundColor(Color.parseColor("#A0A0A0"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        private class ViewHolder {
            TextView text,text2;
            ImageView icon;
        }
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

        validpass=false;
        validUser=-1;
        validname="";
        try {
            ss = view.getTag().toString()+"";
            khand.handleKey(ss);
            pass = khand.val+"";
            if (selidx>-1) {
                pp=getPassword(selidx)+"";
                validpass=pass.equalsIgnoreCase(pp);
                if (validpass) {
                    validUser=getCodigoInt(selidx);
                    validname=getText(selidx);
                }
            }
        } catch (Exception e) {
            validpass=false;pass="";
        }
    }

    private class PasswordTransformationMethod extends android.text.method.PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '*'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };

    //endregion

}
