package com.by.communication.widgit.listView.adapter;

import android.content.Context;


import java.util.List;

public abstract class SingleItemAdapter<T> extends MultiItemTypeAdapter<T>
{

    public SingleItemAdapter(Context context, final int layoutId, List<T> dataList)
    {
        super(context, dataList);

        addItemViewDelegate(new ItemViewDelegate<T>()
        {
            @Override
            public int getItemViewLayoutId()
            {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position)
            {
                return true;
            }

            @Override
            public void convert(ListHolder holder, T t, int position)
            {
                SingleItemAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ListHolder holder, T item, int position);

}
