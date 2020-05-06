/*
 *  Copyright (C) 2011-2019 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 *  All right reserved.
 *
 *  This software is the confidential and proprietary
 *  information of iBOXCHAIN Company of China.
 *  ("Confidential Information"). You shall not disclose
 *  such Confidential Information and shall use it only
 *  in accordance with the terms of the contract agreement
 *  you entered into with iBOXCHAIN inc.
 */

package com.example.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


/**
 * Create by tangliangliang
 * on 2019/11/5
 */
public class ScrollBlockView extends View {
    // 背景颜色
    private int mBgColor;
    private Paint mBgPaint;
    // 滑块画笔
    private Paint mBlockPaint;
    // 滑块颜色
    private int mBlockColor;
    // 滑块的宽度
    private int mBlockWidth;
    // 滑块区域
    private RectF rectF;
    private Region mRegion;
    // View的宽度
    private int mWidth;
    // 当前滑动的位置[0, 1]
    private float scrollRate = 1f;
    // 缩放比例
    private float scale = 6f;

    // 滑动监听
    private OnScrollListener listener;

    public ScrollBlockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
        initData();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollBlockView);
        mBgColor = typedArray.getColor(R.styleable.ScrollBlockView_bgColor, Color.parseColor("#ECFBFF"));
        mBlockColor = typedArray.getColor(R.styleable.ScrollBlockView_blockColor, Color.parseColor("#D6F6FF"));
        typedArray.recycle();
    }

    private void initPaint() {
        mBlockPaint = new Paint();
        mBlockPaint.setColor(mBlockColor);
        mBlockPaint.setStyle(Paint.Style.FILL);

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    private void initData() {
        rectF = new RectF();
        mRegion = new Region();
        setBackgroundColor(mBgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getMeasuredWidth();
        mBlockWidth = (int) (mWidth / scale);
        rectF.left = (mWidth - mBlockWidth) * scrollRate;
        rectF.top = 0;
        rectF.right = (mWidth - mBlockWidth) * scrollRate + mBlockWidth;
        rectF.bottom = getHeight();
        mRegion.set((int)rectF.left, (int) rectF.top, (int)rectF.right, (int)rectF.bottom);
        canvas.drawRect(rectF, mBlockPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return mRegion.contains((int) x, (int) y);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (x < mBlockWidth / 2f || x > getWidth() - mBlockWidth / 2f) {
                return false;
            }

            // scrollRate是以滑块的left位置为基准计算出来的，范围[0, 1]
            float scrollRate = (x - mBlockWidth / 2f) / (mWidth - mBlockWidth);
            // 滑块位置已经改变，不需要重新绘制了
            setScrollRate(scrollRate, scale);

            if (listener != null) {
                listener.onScroll(scrollRate);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置滑块移动的比例
     */
    public void setScrollRate(float scrollRate, float scale) {
        // 当值重复时，不再重绘
        if (this.scrollRate == scrollRate && this.scale == scale) {
            return;
        }
        if (scale < 1) {
            this.scale = 1;
        } else {
            this.scale = scale;
        }

        if (this.scrollRate > 1) {
            this.scrollRate = 1;
        } else if (this.scrollRate < 0) {
            this.scrollRate = 0;
        } else {
            this.scrollRate = scrollRate;
        }
        invalidate();
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public interface OnScrollListener {
        void onScroll(float scrollRate);
    }
}
