package com.by.communication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.net.PushSocketService;
import com.by.communication.re.ChatService;
import com.by.communication.util.ImageUtil;
import com.by.communication.util.Logger;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.util.Util;
import com.by.communication.widgit.adapter.BaseMultiItemQuickAdapter;
import com.by.communication.widgit.adapter.BaseViewHolder;
import com.by.communication.widgit.layout.MyLinearLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/3/30.
 */

public class ChatActivity extends IoActivity {

    @BindView(R.id.chatActivity_linearLayout)
    MyLinearLayout linearLayout;
    @BindView(R.id.chat_recyclerView)
    RecyclerView   recyclerView;

    @BindView(R.id.chatActivity_editText)
    EditText editText;
    @BindView(R.id.chatActivity_sendTextView)
    TextView sendTextView;

    private ChatAdapter            chatAdapter;
    private ArrayList<ChatMessage> chatMessageArrayList;

    private User friend;
    private User user;

    private Handler handler = new Handler();

    private ChatMessageDao chatMessageDao;

    @Override
    public int getLayoutResId()
    {
        return R.layout.chat_activity;
    }

    @Subscribe
    public void newMessage(Bundle bundle)
    {
        String action = bundle.getString("action");
        if (action != null && action.equals(PushSocketService.CHAT_MESSAGE)) {
            ChatMessage chatMessage = bundle.getParcelable("chatMessage");
            chatMessageArrayList.add(chatMessage);

            System.out.println(chatMessage.toString());

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    System.out.println("asdasdsadsadsa");
                    chatAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        user = App.getInstance().getUser();
        friend = getIntent().getBundleExtra("user").getParcelable("user");
        chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();

        chatMessageArrayList = new ArrayList<>();
        chatMessageArrayList.addAll(chatMessageDao.loadAll());

        Logger.list("chatMessage", chatMessageArrayList);

        chatAdapter = new ChatAdapter(chatMessageArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatAdapter);

        recyclerView.scrollToPosition(chatMessageArrayList.size() - 1);

        final int base = Util.getScreenHeight(getApplicationContext());
        linearLayout.setOnSoftKeyboardListener(new MyLinearLayout.OnSoftKeyboardListener() {
            @Override
            public void sizeChanged(int oldSize, int newSize)
            {
                if (newSize < base * 0.7f) {
                    recyclerView.scrollToPosition(chatMessageArrayList.size() - 1);
                }
            }
        });


        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Logger.e("why", "whywhywhy");
                String text = editText.getText().toString().trim();
                editText.setText("");

                //生成临时id，服务器返回后更新message_id
                final long temp_id = chatMessageDao.queryBuilder()
                        .orderAsc(ChatMessageDao.Properties.Id)
                        .limit(1)
                        .unique()
                        .getId() - 1;

                final ChatMessage temp_message = new ChatMessage(
                        temp_id,
                        user.getId(),
                        friend.getId(),
                        ChatMessage.TEXT,
                        text,
                        ChatMessage.SENDING
                );

                chatMessageDao.insert(temp_message);
                chatMessageArrayList.add(temp_message);
                chatAdapter.notifyDataSetChanged();

                RetrofitUtil.getInstance().service(ChatService.class)
                        .sendMessage(user.getId(), friend.getId(), ChatMessage.TEXT, text)
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
                                e.printStackTrace();
                                temp_message.setStatus(ChatMessage.SEND_FAILED);
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onNext(Response<ChatMessage> chatMessageResponse)
                            {
                                System.out.println(chatMessageResponse.toString());

                                ChatMessage chatMessage = chatMessageResponse.getData();

                                chatMessageDao.deleteByKey(temp_id);
                                chatMessageDao.insertOrReplaceInTx(chatMessage);

                                temp_message.setStatus(ChatMessage.SEND_SUCCESS);
                                chatAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }

    private class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

        public ChatAdapter(List<ChatMessage> data)
        {
            super(data);

            addItemType(ChatMessage.TEXT_SELF, R.layout.chat_text_self);
            addItemType(ChatMessage.TEXT_OTHER, R.layout.chat_text_other);
        }

        @Override
        protected void convert(BaseViewHolder holder, ChatMessage message)
        {
            switch (holder.getItemViewType()) {

                case ChatMessage.TEXT_SELF:
                case ChatMessage.TEXT_OTHER:
                    holder.setText(R.id.chatText_textView, message.getContent());
                    break;
            }

            ProgressBar progressBar = holder.getView(R.id.chat_progressBar);
            ImageView retryImageView = holder.getView(R.id.chat_resendImageView);
            ImageView avatar = holder.getView(R.id.chat_avatar);

            ImageUtil.displayAvatar(getApplicationContext(), avatar, message.getSender_id());

            switch (message.getStatus()) {

                case ChatMessage.SENDING:
                    progressBar.setVisibility(View.VISIBLE);
                    retryImageView.setVisibility(View.GONE);
                    break;

                case ChatMessage.SEND_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    retryImageView.setVisibility(View.GONE);
                    break;

                case ChatMessage.SEND_FAILED:
                    progressBar.setVisibility(View.GONE);
                    retryImageView.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }
}
