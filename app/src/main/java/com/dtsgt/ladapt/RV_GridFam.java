package com.dtsgt.ladapt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;


import java.io.File;
import java.util.List;

public class RV_GridFam extends RecyclerView.Adapter<RV_GridFam.MyViewHolder> {

    public List<clsMenu> items;

    public static int selectedItemPosition=-1;
    private String imgpath;
    private File file;

    public RV_GridFam(List<clsMenu> itemList,String imgfold) {
        this.items = itemList;
        imgpath=imgfold;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lbl1;
        public ImageView img1;
        public View rootLayout;

        public MyViewHolder(View view) {
            super(view);
            rootLayout = itemView;
            lbl1 = view.findViewById(R.id.lbl1);
            img1 = view.findViewById(R.id.img1);
        }

        public void bind(clsMenu item, int position) {
            lbl1.setText(""+item.Name);

            try {
                String prodimg = imgpath+"familia/"+item.Cod+".png";
                file = new File(prodimg);
                if (file.exists()) {
                    Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                    img1.setImageBitmap(bmImg);
                } else {
                    prodimg = imgpath+"familia/"+item.Cod+".jpg";
                    file = new File(prodimg);
                    if (file.exists()) {
                        Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                        img1.setImageBitmap(bmImg);
                    } else img1.setImageResource(R.drawable.blank_prod);
                }
            } catch (Exception e) {
                img1.setImageResource(R.drawable.blank_prod);
            }

            if (selectedItemPosition == position) {
                rootLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                rootLayout.setBackgroundColor(Color.WHITE);
            }

            rootLayout.setOnClickListener(v -> {
                int previousPosition = selectedItemPosition;
                selectedItemPosition =  getAbsoluteAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedItemPosition);
            });

            rootLayout.setOnLongClickListener(v -> {
                int previousPosition = selectedItemPosition;
                selectedItemPosition =  getAbsoluteAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedItemPosition);

                return false;
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_gridventa, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_GridFam.MyViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedIndex(int selpos) {
        int previousSelectedPosition = selectedItemPosition;
        selectedItemPosition = selpos;

        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selpos);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
