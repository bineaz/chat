package com.by.communication.widgit.listView.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by SX3 on 2016/12/29.
 */

public abstract class SingleListAdapter<T> extends BaseAdapter {
//    private Context context;
    private List<T> dataList;

    public SingleListAdapter( List<T> dataList) {
        this.dataList = dataList;
//        this.context = context;
    }

    public abstract int getLayoutId();

    public abstract void convert(ListHolder holder, T data, int position);

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        if (position < dataList.size())
            return dataList.get(position);

        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = getLayoutId();

        ListHolder holder;
        if (convertView == null) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

            holder = new ListHolder(parent.getContext(), itemView, parent, position);
            holder.mLayoutId = layoutId;
        } else {
            holder = (ListHolder) convertView.getTag();
            holder.mPosition = position;
        }


        convert(holder, getItem(position), position);
        return holder.getConvertView();
    }
}
