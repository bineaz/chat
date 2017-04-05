package com.by.communication.widgit.listView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ListView;

import com.by.communication.R;


/**
 * Created by admin on 2016/10/13.
 */

public class InsetListView extends ListView {
    private String TAG = InsetListView.class.getSimpleName();

    private int left;
    private int right;
    private int top;
    private int bottom;

    private int color;
    private int divider_height;


    public InsetListView(Context context) {
        this(context, null);
    }

    public InsetListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InsetListView);

        left = ta.getDimensionPixelSize(R.styleable.InsetListView_ilv_insetLeft, 0);
        right = ta.getDimensionPixelSize(R.styleable.InsetListView_ilv_insetRight, 0);
        top = ta.getDimensionPixelSize(R.styleable.InsetListView_ilv_insetTop, 0);
        bottom = ta.getDimensionPixelSize(R.styleable.InsetListView_ilv_insetBottom, 0);

        color = ta.getColor(R.styleable.InsetListView_ilv_solidColor,  ContextCompat.getColor(getContext(),R.color.list_divider_color));
        divider_height = ta.getDimensionPixelSize(R.styleable.InsetListView_ilv_dividerHeight, 0);


        ta.recycle();

//        setOverScrollMode(OVER_SCROLL_NEVER);
//        setSelector(R.drawable.list_selector);

        update();

    }

    public void setInsetLeft(int left) {
        this.left = left;
        update();
    }

    public void setDivider_height(int divider_height) {
        this.divider_height = divider_height;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public void update() {
        ColorDrawable colorDrawable = new ColorDrawable(color);
        InsetDrawable drawable = new InsetDrawable(colorDrawable, left, top, right, bottom);

        setDivider(drawable);
        setDividerHeight(divider_height);
    }
}
