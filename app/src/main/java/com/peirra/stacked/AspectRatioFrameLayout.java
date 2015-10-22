package com.peirra.stacked;

/**
 * Created by kurt on 2015/10/20.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * A {@link FrameLayout} that resizes itself to match a specified aspect ratio.
 */
public final class AspectRatioFrameLayout extends FrameLayout {

    /**
     * The {@link FrameLayout} will not resize itself if the fractional difference between its natural
     * aspect ratio and the requested aspect ratio falls below this threshold.
     */
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;

    private float aspectRatio = 1.3333f;
    //This tolerance allows the view to occupy the whole of the screen when the requested aspect ratio is very close, but not exactly equal to, the aspect ratio of the screen
    private float maxRatioDeformation = 0.01f;

    public AspectRatioFrameLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StackedLayout, defStyle, 0);
        aspectRatio = a.getFloat(R.styleable.AspectRatioFrameLayout_aspectRatio, aspectRatio);
        maxRatioDeformation = a.getFloat(R.styleable.AspectRatioFrameLayout_aspectThreshold, maxRatioDeformation);
        a.recycle();
        setAspectRatio(aspectRatio);
    }


    /**
     * Set the aspect ratio that this view should satisfy.
     *
     * @param widthHeightRatio The width to height ratio.
     */
    public void setAspectRatio(float widthHeightRatio) {
        if (this.aspectRatio != widthHeightRatio) {
            this.aspectRatio = widthHeightRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (aspectRatio == 0) {
            // Aspect ratio not set.
            return;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float viewAspectRatio = (float) width / height;
        float aspectDeformation = aspectRatio / viewAspectRatio - 1;
        if (Math.abs(aspectDeformation) <= MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
            // We're within the allowed tolerance.
            return;
        }

        if (aspectDeformation > 0) {
            height = (int) (width / aspectRatio);
        } else {
            width = (int) (height * aspectRatio);
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

}
