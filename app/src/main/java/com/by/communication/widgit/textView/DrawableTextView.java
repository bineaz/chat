package com.by.communication.widgit.textView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.by.communication.R;
import com.by.communication.util.Util;

/**
 * Created by admin on 2016/10/11.
 */

public class DrawableTextView extends android.support.v7.widget.AppCompatTextView {

    private String tag = DrawableTextView.class.getSimpleName();

    private int solidColor;          //背景颜色
    private int stroke_Color;        //描边颜色
    private int gradientStartColor, gradientEndColor, gradientCenterColor;
    private int touchColor;          //触摸时背景颜色
    private int topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius;
    private int gradientLeft, gradientTop, gradientRight, gradientBottom;
    private int     cornerRadius;        //圆角大小
    private int     stroke_Width;        //描边宽度
    private int     strokeDashWidth;     //虚线宽度
    private int     gradientRadius;
    private int     strokeDashGap;       //虚线间隔
    private int     gradientAngle;
    private boolean gradientUseLevel;
    private int     gradientType;
    private int     gradientOrientation;
    private int     shapeType;

    private int shadowColor;
    private int shadowRadius;
    private int shadowLeft;
    private int shadowTop;
    private int shadowRight;
    private int shadowBottom;
    private int shadowOrientation;
    private int shadowStartColor;
    private int shadowCenterColor;
    private int shadowEndColor;


    private int drawableWidth;     //添加的小图标的size
    private int drawableHeight;     //添加的小图标的size

    private int drawableRightWidth;
    private int drawableRightHeight;

    private int drawableTopWidth;
    private int drawableTopHeight;

    private int drawableBottomHeight;
    private int drawableBottomColor;


    private OnDrawableClickListener     onDrawableClickListener;
    private OnLeftDrawableClickListener onLeftDrawableClickListener;

    private boolean isShadowDefined = false;

    private LayerDrawable    layerDrawable;
    private GradientDrawable gradientDrawable;

    public DrawableTextView(Context context)
    {
        this(context, null);
    }

    public DrawableTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initData(context, attrs);
    }


    private void initData(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        solidColor = a.getColor(R.styleable.DrawableTextView_dt_solidColor, Color.TRANSPARENT);
        stroke_Color = a.getColor(R.styleable.DrawableTextView_dt_stroke_Color, Color.TRANSPARENT);
        gradientStartColor = a.getColor(R.styleable.DrawableTextView_dt_gradientStartColor, Color.TRANSPARENT);
        gradientEndColor = a.getColor(R.styleable.DrawableTextView_dt_gradientEndColor, Color.TRANSPARENT);
        gradientCenterColor = a.getColor(R.styleable.DrawableTextView_dt_gradientCenterColor, Color.TRANSPARENT);
        touchColor = a.getColor(R.styleable.DrawableTextView_dt_touchSolidColor, Color.TRANSPARENT);
        cornerRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_cornerRadius, 0);
        topLeftRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_topLeftRadius, 0);
        topRightRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_topRightRadius, 0);
        bottomLeftRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_bottomLeftRadius, 0);
        bottomRightRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_bottomRightRadius, 0);
        stroke_Width = (int) a.getDimension(R.styleable.DrawableTextView_dt_stroke_Width, 0);
        strokeDashWidth = (int) a.getDimension(R.styleable.DrawableTextView_dt_strokeDashWidth, 0);
        strokeDashGap = (int) a.getDimension(R.styleable.DrawableTextView_dt_strokeDashGap, 0);
        gradientAngle = (int) a.getDimension(R.styleable.DrawableTextView_dt_gradientAngle, 0);
        gradientRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_gradient_radius, 0);
        gradientUseLevel = a.getBoolean(R.styleable.DrawableTextView_dt_gradientUseLevel, false);
        gradientType = a.getInt(R.styleable.DrawableTextView_dt_gradientType, -1);
        gradientOrientation = a.getInt(R.styleable.DrawableTextView_dt_gradientOrientation, -1);
        shapeType = a.getInt(R.styleable.DrawableTextView_dt_shapeType, -1);
        gradientLeft = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_gradientLeft, 0);
        gradientTop = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_gradientTop, 0);
        gradientRight = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_gradientRight, 0);
        gradientBottom = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_gradientBottom, 0);

        drawableWidth = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableWidth, Util.dip2px(getContext(), 15));
        drawableHeight = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableHeight, Util.dip2px(getContext(), 15));

        drawableRightWidth = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableRightWidth, Util.dip2px(getContext(), 25));
        drawableRightHeight = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableRightHeight, Util.dip2px(getContext(), 25));

        drawableTopWidth = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableTopWidth, Util.dip2px(getContext(), 25));
        drawableTopHeight = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableTopHeight, Util.dip2px(getContext(), 25));

        drawableBottomHeight = a.getDimensionPixelOffset(R.styleable.DrawableTextView_dt_drawableBottomHeight, 0);
        drawableBottomColor = a.getColor(R.styleable.DrawableTextView_dt_drawableBottomColor, Color.TRANSPARENT);

        if (a.hasValue(R.styleable.DrawableTextView_dt_shadowColor)) {
            isShadowDefined = true;
            shadowLeft = a.getDimensionPixelSize(R.styleable.DrawableTextView_dt_shadowLeft, 0);
            shadowTop = a.getDimensionPixelSize(R.styleable.DrawableTextView_dt_shadowTop, 0);
            shadowRight = a.getDimensionPixelSize(R.styleable.DrawableTextView_dt_shadowRight, 0);
            shadowBottom = a.getDimensionPixelSize(R.styleable.DrawableTextView_dt_shadowBottom, 0);
            shadowColor = a.getColor(R.styleable.DrawableTextView_dt_shadowColor, Color.TRANSPARENT);
            shadowRadius = (int) a.getDimension(R.styleable.DrawableTextView_dt_shadowRadius, 0);
            shadowOrientation = a.getInt(R.styleable.DrawableTextView_dt_shadowOrientation, -1);
            shadowStartColor = a.getColor(R.styleable.DrawableTextView_dt_shadowStartColor, Color.TRANSPARENT);
            shadowCenterColor = a.getColor(R.styleable.DrawableTextView_dt_shadowCenterColor, Color.TRANSPARENT);
            shadowEndColor = a.getColor(R.styleable.DrawableTextView_dt_shadowEndColor, Color.TRANSPARENT);
        }

        a.recycle();


        if (getBackground() == null) {
            setDrawables();
            update();
        }
    }

    public void setRightDrawable(Drawable drawable)
    {
        Drawable[] drawables = getCompoundDrawables();
        if (drawable != null)
            drawable.setBounds(0, 0, drawableRightWidth, drawableRightHeight);
        drawables[2] = drawable;

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setTopDrawable(Drawable drawable)
    {
        Drawable[] drawables = getCompoundDrawables();
        if (drawable != null)
            drawable.setBounds(0, 0, drawableTopWidth, drawableTopHeight);
        drawables[1] = drawable;

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setLeftDrawable(Drawable drawable)
    {
        Drawable[] drawables = getCompoundDrawables();
        if (drawable != null)
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        drawables[0] = drawable;

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setBottomDrawable(Drawable drawable, int width, int height)
    {
        Drawable[] drawables = getCompoundDrawables();
        drawables[3] = drawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, Util.dip2px(getContext(), width), Util.dip2px(getContext(), height));
        }

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setBottomDrawableColor(@ColorRes int color)
    {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawable = drawables[3];
        if (drawable != null) {
            drawable.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.ADD);
        }

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    private void setDrawables()
    {
        Drawable[] drawables = getCompoundDrawables();

        Drawable textDrawable;

        //设置宽高
        textDrawable = drawables[0];
        if (textDrawable != null && drawableWidth != -1 && drawableHeight != -1) {
            textDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
        }

        textDrawable = drawables[1];
        if (textDrawable != null && drawableWidth != -1 && drawableHeight != -1) {
            textDrawable.setBounds(0, 0, drawableTopWidth, drawableTopHeight);
        }

        textDrawable = drawables[2];
        if (textDrawable != null && drawableWidth != -1 && drawableHeight != -1) {
            textDrawable.setBounds(0, 0, drawableRightWidth, drawableRightHeight);
        }

        if (drawableBottomHeight > 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(drawableBottomColor);
            drawable.setBounds(0, 0, 3000, drawableBottomHeight);

            drawables[3] = drawable;
        }

        //update
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
//        final Drawable[] drawables = getCompoundDrawables();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (onDrawableClickListener != null) {
                // 0,1,2,3,    左，上，右，下

                Drawable drawable = getCompoundDrawables()[2];


                if (drawable != null && event.getX() >= (getWidth() - drawable.getBounds().width())) {
                    onDrawableClickListener.onRightDrawableClick();

                    return true;
                }


            } else if (onLeftDrawableClickListener != null) {
                Drawable drawable = getCompoundDrawables()[0];
                if (drawable != null && event.getX() <= drawable.getBounds().width()) {

                    onLeftDrawableClickListener.onLeftDrawableClick();
                    return true;
                }
            }


            if (touchColor != Color.TRANSPARENT) {
                gradientDrawable.setColor(touchColor);
                setGradientBacgGround();
                postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (touchColor != Color.TRANSPARENT) {
                gradientDrawable.setColor(solidColor);
                setGradientBacgGround();
            }
        }
        return super.onTouchEvent(event);
    }

    private void setGradientBacgGround()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(gradientDrawable);
        } else {
            setBackgroundDrawable(gradientDrawable);
        }
    }

    private GradientDrawable.Orientation getOrientation(int gradientOrientation)
    {
        GradientDrawable.Orientation orientation = null;
        switch (gradientOrientation) {
            case 0:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 1:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 4:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 5:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case 6:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 7:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
        }
        return orientation;
    }

    public DrawableTextView setStroke_Color(int color)
    {
        this.stroke_Color = color;
        return this;
    }

    public DrawableTextView setSolid_Color(int color)
    {
        this.solidColor = color;
        return this;
    }

    public DrawableTextView setTouchColor(int color)
    {
        this.touchColor = color;
        return this;
    }

    public DrawableTextView setCornerRadius(int cornerRadius)
    {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public DrawableTextView setTopLeftCornerRadius(int cornerRadius)
    {
        this.topLeftRadius = cornerRadius;
        return this;
    }

    public DrawableTextView setBottomLeftCornerRadius(int cornerRadius)
    {
        this.bottomLeftRadius = cornerRadius;
        return this;
    }

    public DrawableTextView setTopRightCornerRadius(int cornerRadius)
    {
        this.topRightRadius = cornerRadius;
        return this;
    }

    public DrawableTextView setBottomRightCornerRadius(int cornerRadius)
    {
        this.bottomRightRadius = cornerRadius;
        return this;
    }

    public OnLeftDrawableClickListener getOnLeftDrawableClickListener()
    {
        return onLeftDrawableClickListener;
    }

    public void setOnLeftDrawableClickListener(OnLeftDrawableClickListener onLeftDrawableClickListener)
    {
        this.onLeftDrawableClickListener = onLeftDrawableClickListener;
    }

    public OnDrawableClickListener getOnDrawableClickListener()
    {
        return onDrawableClickListener;
    }

    public void setOnDrawableClickListener(OnDrawableClickListener onDrawableClickListener)
    {
        this.onDrawableClickListener = onDrawableClickListener;
    }

    public DrawableTextView setStroke_Width(int width)
    {
        this.stroke_Width = width;
        return this;
    }

    public void drawBackground()
    {
        setGradientBacgGround();
    }

    public void drawBackgroundWithShadow()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(layerDrawable);
        } else {
            setBackgroundDrawable(layerDrawable);
        }
    }

    public void update()
    {

        createGradientDrawable();

        if (isShadowDefined) {
            createLayerDrawable();
            drawBackgroundWithShadow();
        } else {
            drawBackground();
        }


    }

    private void createGradientDrawable()
    {
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(stroke_Width, stroke_Color, strokeDashWidth, strokeDashGap);
        // 如果设定的有Orientation 就默认为是渐变色的Button，否则就是纯色的Button
        if (gradientOrientation != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                gradientDrawable.setOrientation(getOrientation(gradientOrientation));
                gradientDrawable.setColors(new int[]{gradientStartColor, gradientCenterColor, gradientEndColor});
            }
        } else {
            gradientDrawable.setColor(solidColor);
        }

        if (shapeType == 0) {
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        } else if (shapeType == 1) {
            gradientDrawable.setShape(GradientDrawable.OVAL);
        } else if (shapeType == 2) {
            gradientDrawable.setShape(GradientDrawable.LINE);
        } else if (shapeType == 3) {
            gradientDrawable.setShape(GradientDrawable.RING);
        }

        if (shapeType != GradientDrawable.OVAL) {
            if (cornerRadius != 0) {
                gradientDrawable.setCornerRadius(cornerRadius);
            } else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gradientDrawable.setCornerRadii(new float[]{topLeftRadius,
                        topLeftRadius, topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius, bottomLeftRadius,
                        bottomLeftRadius});
            }
        }

        if (gradientUseLevel) {
            gradientDrawable.setUseLevel(gradientUseLevel);
        }
        if (gradientType == 0) {
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        } else if (gradientType == 1) {
            gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        } else if (gradientType == 2) {
            gradientDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        }
        gradientDrawable.setGradientRadius(gradientRadius);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void createLayerDrawable()
    {
        Drawable drawable[] = new Drawable[2];

        GradientDrawable shadowDrawable = new GradientDrawable();

        if (shadowRadius == 0) {
            shadowRadius = cornerRadius;
        }
        shadowDrawable.setCornerRadius(shadowRadius);
        shadowDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);

        if (shadowOrientation != -1) {
            shadowDrawable.setOrientation(getOrientation(gradientOrientation));
            shadowDrawable.setColors(new int[]{shadowStartColor, shadowCenterColor, shadowEndColor});
        } else {
            shadowDrawable.setColor(shadowColor);
        }


        drawable[0] = shadowDrawable;
        drawable[1] = gradientDrawable;
        layerDrawable = new LayerDrawable(drawable);

        layerDrawable.setLayerInset(0, shadowLeft, shadowTop, shadowRight, shadowBottom);
        layerDrawable.setLayerInset(1, gradientLeft, gradientTop, gradientRight, gradientBottom);
    }

}
