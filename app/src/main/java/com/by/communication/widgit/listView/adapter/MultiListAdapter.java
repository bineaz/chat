package com.by.communication.widgit.listView.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by SX3 on 2016/12/29.
 */

public abstract class MultiListAdapter<T> extends BaseAdapter {
    private Context context;
    private List<T> dataList;

    public MultiListAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        this.context = context;
    }


    public abstract int getViewType(T data, int position);

    public abstract int getLayoutId(int viewType);

    public abstract int getItemViewTypeCount();

    public abstract void convert(ListHolder holder, T data, int position);


    @Override
    public int getViewTypeCount() {
        return getItemViewTypeCount();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = getLayoutId(getViewType(dataList.size() > position ? dataList.get(position) : null, position));

        ListHolder holder;
        if (convertView == null) {
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);

            holder = new ListHolder(context, itemView, parent, position);
            holder.mLayoutId = layoutId;
        } else {
            holder = (ListHolder) convertView.getTag();
            holder.mPosition = position;
        }


        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(dataList.size() > position ? dataList.get(position) : null, position);
    }
}
