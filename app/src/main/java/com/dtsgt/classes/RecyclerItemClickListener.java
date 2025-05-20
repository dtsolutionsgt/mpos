package com.dtsgt.classes;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsgt.mpos.Venta;

public class RecyclerItemClickListener  implements RecyclerView.OnItemTouchListener {

    private RecyclerItemClickListener.OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public interface OnItemClickListener  {
        public void onItemClick(View view, int position);
        public void onLongItemClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, RecyclerItemClickListener.OnItemClickListener listener) {

        mListener=listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });

    }

    public RecyclerItemClickListener(Venta context, RecyclerView recfam, AdapterView.OnItemClickListener onItemClickListener) {
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }


}
