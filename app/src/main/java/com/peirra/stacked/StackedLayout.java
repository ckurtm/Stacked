package com.peirra.stacked;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class StackedLayout extends ViewGroup {

    String TAG = StackedLayout.class.getSimpleName();

    private int frameColor = Color.RED;
    private float framePadding = 0;

    private  Rect viewBounds = new Rect();
    private final Rect frameBounds = new Rect();
    private final Rect imageBounds = new Rect();
    private final Paint borderPaint = new Paint();

    private int MAX_FRAME_COUNT = 10;
    private int MAX_STROKE_WIDTH = 2;
    private int frameCount = MAX_FRAME_COUNT;
    private int frameDelta = 1;
    private int strokeWith = MAX_STROKE_WIDTH;
    private final float[] verticalLong = new float[4];
    private final float[] verticalShort = new float[4];
    private final float[] horizontalLong = new float[4];
    private final float[] horizontalShort = new float[4];

    public StackedLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public StackedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StackedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StackedLayout, defStyle, 0);
        frameColor = a.getColor(R.styleable.StackedLayout_frameColor, frameColor);
        framePadding = a.getDimension(R.styleable.StackedLayout_framePadding, framePadding);
        frameCount = a.getInt(R.styleable.StackedLayout_frameCount, MAX_FRAME_COUNT);
        int frameMaxCount = a.getInt(R.styleable.StackedLayout_frameMaxCount, MAX_FRAME_COUNT);
        if(frameCount > frameMaxCount){
            frameCount = frameMaxCount;
        }
        a.recycle();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(frameColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(strokeWith);
        borderPaint.setStrokeCap(Paint.Cap.SQUARE);
        setFrameCount(frameCount);

    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int height = 0;
        int width = 0;
        int childState = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Measure the child.
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                width = Math.max(width,child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                height = Math.max(height,child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }
        // Check against our minimum height and width
        height = (int) (Math.max(height, getSuggestedMinimumHeight()) - (2 * framePadding));
        width  = (int) (Math.max(width, getSuggestedMinimumWidth()) - (2 * framePadding));
        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec,childState << MEASURED_HEIGHT_STATE_SHIFT));
        viewBounds.set(strokeWith, strokeWith, getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingTop());

    }


    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        strokeWith = Math.min(MAX_STROKE_WIDTH,(int) Math.floor(framePadding / frameCount) / 2);
        borderPaint.setStrokeWidth(strokeWith);
        frameDelta = (int) Math.floor(framePadding / (float) frameCount);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawFrames(canvas); // we need to draw the frames before we draw the child views
        super.onDraw(canvas);
    }


    private void drawFrames(Canvas canvas){
        canvas.drawRect(imageBounds, borderPaint);
        frameBounds.set(imageBounds); //initial frame is around the iamge
        for(int i=1;i<frameCount;i++){
            frameBounds.offset(frameDelta, - frameDelta); //offset the frame by 1
            verticalLong[0] = frameBounds.right;
            verticalLong[1] = frameBounds.top;
            verticalLong[2] = frameBounds.right;
            verticalLong[3] = frameBounds.bottom;

            verticalShort[0] = frameBounds.left;
            verticalShort[1] = frameBounds.top;
            verticalShort[2] = frameBounds.left;
            verticalShort[3] = frameBounds.top+frameDelta;

            horizontalLong[0] = frameBounds.left;
            horizontalLong[1] = frameBounds.top;
            horizontalLong[2] = frameBounds.right;
            horizontalLong[3] = frameBounds.top;

            horizontalShort[0] = frameBounds.right-frameDelta;
            horizontalShort[1] = frameBounds.bottom;
            horizontalShort[2] = frameBounds.right;
            horizontalShort[3] = frameBounds.bottom;

            canvas.drawLines(verticalLong,borderPaint);
            canvas.drawLines(verticalShort,borderPaint);
            canvas.drawLines(horizontalLong,borderPaint);
            canvas.drawLines(horizontalShort,borderPaint);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

    }

    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        // These are the far left and right edges in which we are performing layout.
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();

        // These are the top and bottom edges in which we are performing layout.
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int width = (int) (child.getMeasuredWidth() - (2*framePadding));
                final int height = (int) (child.getMeasuredHeight() - (2* framePadding));

                // Compute the frame in which we are placing this child.


                imageBounds.left = (int) (leftPos + lp.leftMargin + framePadding);
                imageBounds.right = (int) (rightPos - lp.rightMargin - framePadding);
                imageBounds.top = (int) (parentTop + lp.topMargin + framePadding);
                imageBounds.bottom = (int) (parentBottom - lp.bottomMargin - framePadding);

                // Use the child's gravity and size to determine its final
                // frame within its container.
                Gravity.apply(Gravity.CENTER, width, height, imageBounds, imageBounds);
                // Place the child.
                child.layout(imageBounds.left, imageBounds.top,
                        imageBounds.right, imageBounds.bottom);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new StackedLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Custom per-child layout information.
     */
    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
