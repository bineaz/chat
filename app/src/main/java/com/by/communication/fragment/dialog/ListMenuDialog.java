package com.by.communication.fragment.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.by.communication.R;
import com.by.communication.widgit.listView.InsetListView;
import com.by.communication.widgit.listView.adapter.ListHolder;
import com.by.communication.widgit.listView.adapter.SingleItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SX3 on 2017/1/4.
 */

public class ListMenuDialog extends BaseDialogFragment {
    private InsetListView     listView;
    private MenuAdapter       adapter;
    private ArrayList<String> menuArrayList;

    private AdapterView.OnItemClickListener onItemClickListener;

    @Override
    public int getResId()
    {
        return R.layout.dialog;
    }

    @Override
    public void init(View view)
    {

        listView = (InsetListView) view.findViewById(R.id.dialog_listView);
        adapter = new MenuAdapter(getActivity(), R.layout.chat_menu_item, menuArrayList);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setAdapter(adapter);
    }

    public void setData(ArrayList<String> data, AdapterView.OnItemClickListener onItemClickListener)
    {
        menuArrayList = data;
        this.onItemClickListener = onItemClickListener;
    }


    private class MenuAdapter extends SingleItemAdapter<String> {

        public MenuAdapter(Context context, int layoutId, List<String> dataList)
        {
            super(context, layoutId, dataList);
        }


        @Override
        protected void convert(ListHolder holder, String item, int position)
        {
            TextView textView = holder.getView(R.id.chatMenuItem_textView);
            textView.setText(item);
            if (position == 0) {
                textView.setTextSize(18);
                textView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                textView.setTextSize(15);
                textView.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
