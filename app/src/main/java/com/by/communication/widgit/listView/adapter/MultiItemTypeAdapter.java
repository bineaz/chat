package com.by.communication.widgit.listView.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.List;

public class MultiItemTypeAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> dataList;

    private ItemViewDelegateManager mItemViewDelegateManager;


    public MultiItemTypeAdapter(Context context, List<T> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    @Override
    public int getViewTypeCount() {
        if (useItemViewDelegateManager())
            return mItemViewDelegateManager.getItemViewDelegateCount();
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (useItemViewDelegateManager()) {
            return mItemViewDelegateManager.getItemViewType(dataList.get(position), position);
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(dataList.get(position), position);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ListHolder listHolder;
        if (convertView == null) {
            View itemView = LayoutInflater.from(mContext).inflate(layoutId, parent,
                    false);
            listHolder = new ListHolder(mContext, itemView, parent, position);
            listHolder.mLayoutId = layoutId;
            onViewHolderCreated(listHolder, listHolder.getConvertView());
        } else {
            listHolder = (ListHolder) convertView.getTag();
            listHolder.mPosition = position;
        }


        convert(listHolder, getItem(position), position);
        return listHolder.getConvertView();
    }

    protected void convert(ListHolder listHolder, T item, int position) {
        mItemViewDelegateManager.convert(listHolder, item, position);
    }

    public void onViewHolderCreated(ListHolder holder, View itemView) {
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
