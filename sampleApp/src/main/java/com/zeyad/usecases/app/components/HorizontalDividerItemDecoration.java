package com.zeyad.usecases.app.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HorizontalDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    /**
     * Custom divider will be used
     */
    public HorizontalDividerItemDecoration(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
            mDivider.draw(c);
        }
    }
}
