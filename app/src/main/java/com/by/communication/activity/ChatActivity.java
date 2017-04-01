package com.by.communication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.by.communication.R;
import com.by.communication.entity.ChatMessage;
import com.by.communication.widgit.adapter.BaseMultiItemQuickAdapter;
import com.by.communication.widgit.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public class ChatActivity extends BaseActivity {

    @BindView(R.id.chat_recyclerView)
    RecyclerView recyclerView;

    private ChatAdapter            chatAdapter;
    private ArrayList<ChatMessage> chatMessageArrayList;

    @Override
    public int getLayoutResId()
    {
        return R.layout.chat_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        chatMessageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(chatAdapter);
    }

    private class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

        public ChatAdapter(List<ChatMessage> data)
        {
            super(data);

            addItemType(0, R.layout.chat_text_self);
        }

        @Override
        protected void convert(BaseViewHolder holder, ChatMessage item)
        {

        }
    }
}
