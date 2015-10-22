package com.peirra.stacked;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kurt on 2015/10/20.
 */
public class StackedView extends View {

    private int frameColor = Color.RED;
    private float framePadding = 0;
    private int leftWidth;
    private int rightWidth;

    private final Rect bounds = new Rect();
    private final Rect imageBounds = new Rect();
    private final Paint borderPaint = new Paint();

    private boolean boundsSet;

    public StackedView(Context context) {
        super(context);
        init(null, 0);
    }

    public StackedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StackedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StackedLayout, defStyle, 0);
        frameColor = a.getColor(R.styleable.StackedLayout_frameColor,frameColor);
        framePadding = a.getDimension(R.styleable.StackedLayout_framePadding, framePadding);
        a.recycle();

        borderPaint.setAntiAlias(true);
        borderPaint.setColor(frameColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!boundsSet){
            bounds.set(0,0,getWidth(),getHeight());
        }

        canvas.drawRect(bounds,borderPaint);

    }
}
