package com.by.communication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.re.ChatService;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.widgit.adapter.BaseMultiItemQuickAdapter;
import com.by.communication.widgit.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public class ChatActivity extends BaseActivity {

    @BindView(R.id.chat_recyclerView)
    RecyclerView recyclerView;

    private ChatAdapter            chatAdapter;
    private ArrayList<ChatMessage> chatMessageArrayList;

    private User friend;
    private User user;

    @BindView(R.id.chatActivity_editText)
    EditText editText;
    @BindView(R.id.chatActivity_sendTextView)
    TextView sendTextView;

    @Override
    public int getLayoutResId()
    {
        return R.layout.chat_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        user = App.getInstance().getUser();
        friend = getIntent().getBundleExtra("user").getParcelable("user");

        chatMessageArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(chatAdapter);


        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                RetrofitUtil.getInstance().service(ChatService.class)
                        .sendMessage(user.getId(), 0, ChatMessage.TEXT, editText.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Response<ChatMessage>>() {
                            @Override
                            public void onCompleted()
                            {

                            }

                            @Override
                            public void onError(Throwable e)
                            {

                            }

                            @Override
                            public void onNext(Response<ChatMessage> chatMessageResponse)
                            {

                            }
                        });
            }
        });
    }

    private class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

        public ChatAdapter(List<ChatMessage> data)
        {
            super(data);

            addItemType(0, R.layout.chat_text_self);
            addItemType(1, R.layout.chat_text_other);
        }

        @Override
        protected void convert(BaseViewHolder holder, ChatMessage item)
        {

        }
    }
}
