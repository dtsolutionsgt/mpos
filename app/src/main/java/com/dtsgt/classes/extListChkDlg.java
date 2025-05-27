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

public class extListChkDlg {

    private ListView mList;
    private TextView mTitleLabel,mExitButton,mDelButton,mAddButton;
    private RelativeLayout mRel,mRelTop,mRelBot;
    private LinearLayout mbuttons;

    private Dialog dialog;
    private Context cont;
    private Adapter adapter;

    public ArrayList<clsListDialogItem> items=new ArrayList<clsListDialogItem>();

    private int buttonCount;
    private int bwidth=480,bheight=550,mwidth=0,mheight=0,mlines=6,mminlines=1;

    //region Public methods

    public void buildDialog(Activity activity,String titletext,String butleft,String butmid,String butright) {

        dialog = new Dialog(activity);
        cont=dialog.getContext();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.extlistchkdlg);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        mRel = dialog.findViewById(R.id.extlistdialogrel);
        mRelTop = dialog.findViewById(R.id.xdlgreltop);
        mRelBot = dialog.findViewById(R.id.xdlgrelbut);
        mbuttons = dialog.findViewById(R.id.linbuttons);

        mList = dialog.findViewById(R.id.listview1);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                if (items.get(position).checked) {
                    items.get(position).checked=false;
                } else {
                    items.get(position).checked=true;
                }
                try {
                    adapter.refreshItems();
                } catch (Exception e) {};
             };
        });

        mTitleLabel = dialog.findViewById(R.id.lbltitulo);
        mTitleLabel.setText(titletext);

        mExitButton = dialog.findViewById(R.id.btnexit);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mDelButton = dialog.findViewById(R.id.btndel);
        mDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        mAddButton = dialog.findViewById(R.id.btnadd);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        buttonCount=3;

        if (butleft.isEmpty()) {
            mExitButton.setVisibility(View.GONE);buttonCount--;
        }
        if (butmid.isEmpty()) {
            mDelButton.setVisibility(View.GONE);buttonCount--;
        }
        if (butright.isEmpty()) {
            mAddButton.setVisibility(View.GONE);buttonCount--;
        }

        if (buttonCount<0) buttonCount=1;
        mbuttons.setWeightSum(buttonCount);

        mwidth=0;mheight=0;
        mlines =0;
        items.clear();

    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setOnExitListener(@Nullable View.OnClickListener l) {
        mExitButton.setOnClickListener(l);
    }

    public void setOnDelListener(@Nullable View.OnClickListener l) {
        mDelButton.setOnClickListener(l);
    }

    public void setOnAddListener(@Nullable View.OnClickListener l) {
        mAddButton.setOnClickListener(l);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener l) {
        mList.setOnItemClickListener(l);
    }

    public void add(String text) {
        clsListDialogItem item = new clsListDialogItem();

        item.idresource=0;
        item.codigo=0;
        item.text=text;
        item.text2="";
        item.checked=false;

        items.add(item);
    }

    public void add(String text,boolean checked) {
        clsListDialogItem item = new clsListDialogItem();

        item.idresource=0;
        item.codigo=0;
        item.text=text;
        item.text2="";
        item.checked=checked;

        items.add(item);
    }

    public void add(int codigo,String text) {
        clsListDialogItem item = new clsListDialogItem();

        item.idresource=0;
        item.codigo=codigo;
        item.text=text;
        item.text2="";
        item.checked=false;

        items.add(item);
    }

    public void add(String text,String text2) {
        clsListDialogItem item = new clsListDialogItem();

        item.idresource=0;
        item.codigo=0;
        item.text=text;
        item.text2=text2;
        item.checked=false;

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
                footerHeight=mRelBot.getLayoutParams().height+7;

                fheight=icount*itemHeight+headerHeight+footerHeight;
            } catch (Exception e) {
                fwidth=bwidth;fheight=bheight;
            }
        }

        DisplayMetrics displayMetrics = cont.getResources().getDisplayMetrics();
        int dispw = displayMetrics.widthPixels;dispw=(int) (0.8*dispw);
        int disph = displayMetrics.heightPixels;disph=(int) (0.8*disph);

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
        public int codigo;
        public String text;
        public String text2;
        public boolean checked=false;
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

            if (items.get(position).checked) {
                holder.icon.setImageResource(R.drawable.check_on_gr);
            } else {
                holder.icon.setImageResource(R.drawable.check_off_gr);
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                convertView.setBackgroundColor(Color.parseColor("#CCE6F3"));
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

    //endregion

}
