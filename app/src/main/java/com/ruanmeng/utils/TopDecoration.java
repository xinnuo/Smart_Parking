package com.ruanmeng.utils;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TopDecoration extends RecyclerView.ItemDecoration {

    private int gapSize;

    public TopDecoration() {
        gapSize = DensityHelperKt.dp2px(10);
    }

    public TopDecoration(int gapSize) {
        this.gapSize = DensityHelperKt.dp2px(gapSize);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        int adapterPostion = parent.getChildAdapterPosition(view);

        int totalChildCount = parent.getAdapter().getItemCount();
        int spanCount = 0; // grid的列数
        int spanSize = 1;

        GridLayoutManager.SpanSizeLookup sizeLookup = null;

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            spanCount = manager.getSpanCount();
            sizeLookup = manager.getSpanSizeLookup();
            spanSize = sizeLookup.getSpanSize(adapterPostion);

            if (spanCount == 0) return;

            //数据视图
            if (spanSize == spanCount) {
                //视图占满一行，不做偏移处理
                outRect.set(0, 0, 0, 0);
            } else {
                int lastFullSpanCountPos = getLastFullSpanCountPostion(sizeLookup, spanCount, adapterPostion);
                //检查是否允许网格中的第一行元素的marginTop是否允许设置值 -true标识允许
                int top = isFristGridRow(spanCount, position, lastFullSpanCountPos) ? gapSize : 0;

                outRect.set(0, top, 0, 0);
            }

        }

    }

    //寻找最近一个占据spanCount列的位置
    private int getLastFullSpanCountPostion(GridLayoutManager.SpanSizeLookup sizeLookup, int spanCount, int adapterPostion) {

        for (int index = adapterPostion; index >= 0; index--) {
            if (sizeLookup.getSpanSize(index) == spanCount)
                return index;
        }

        return -1;
    }

    //是否为第一行数据
    private boolean isFristGridRow(int spanCount, int position, int lastFullSpanCountPos) {
        return (position - lastFullSpanCountPos) <= spanCount;
    }
}
