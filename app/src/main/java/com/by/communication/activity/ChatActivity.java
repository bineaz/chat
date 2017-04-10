package com.by.communication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatFile;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.fragment.image.ImageItem;
import com.by.communication.fragment.image.PickImageFragment;
import com.by.communication.gen.ChatFileDao;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.net.PushSocketService;
import com.by.communication.re.ChatService;
import com.by.communication.util.FragmentUtil;
import com.by.communication.util.ImageUtil;
import com.by.communication.util.Logger;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.util.TimeUtil;
import com.by.communication.util.Util;
import com.by.communication.util.an.compress.Luban;
import com.by.communication.util.an.compress.OnCompressListener;
import com.by.communication.widgit.adapter.BaseMultiItemQuickAdapter;
import com.by.communication.widgit.adapter.BaseViewHolder;
import com.by.communication.widgit.layout.MyLinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

    @BindView(R.id.chatActivity_funcImageView)
    ImageView funcImageView;

    private ChatAdapter            chatAdapter;
    private ArrayList<ChatMessage> chatMessageArrayList;

    private User friend;
    private User user;

    private Handler handler = new Handler();

    private ChatMessageDao      chatMessageDao;
    private LinearLayoutManager manager;

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

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    boolean scroll = false;
                    System.out.println(manager.findLastCompletelyVisibleItemPosition() + " " + chatMessageArrayList.size());
                    if (manager.findLastCompletelyVisibleItemPosition() == chatMessageArrayList.size() - 2) {
                        scroll = true;
                    }
                    chatAdapter.notifyDataSetChanged();
                    if (scroll) {
                        recyclerView.scrollToPosition(chatMessageArrayList.size() - 1);
                    } else {
                        Util.toast(getApplicationContext(), "new message");
                    }
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
        QueryBuilder<ChatMessage> qb = chatMessageDao.queryBuilder();

        QueryBuilder.LOG_SQL = true;

        qb.where(new WhereCondition.StringCondition(
                "T.'SENDER_ID' ='" + friend.getId() + "' AND T.'RECEIVER_ID' = '" + user.getId() + "'" +
                        " OR T.'SENDER_ID' ='" + user.getId() + "' AND T.'RECEIVER_ID' = '" + friend.getId() + "'"
        ));

        chatMessageArrayList.addAll(qb.orderAsc(ChatMessageDao.Properties.Timestamp).build().list());

        Logger.list("chatMessage", chatMessageArrayList);

        chatAdapter = new ChatAdapter(chatMessageArrayList);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
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

        setTitle(friend.getUsername());


        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                sendMessage(ChatMessage.TEXT, null);
            }
        });

        funcImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PickImageFragment pickImageFragment = new PickImageFragment();
                pickImageFragment.setOnImagePickListener(new PickImageFragment.OnImagePickListener() {
                    @Override
                    public void onPick(ImageItem item)
                    {
                        Logger.e(item);
                        File file = new File(item.path);

                        Luban.get(getApplicationContext())
                                .load(file)
                                .putGear(Luban.THIRD_GEAR)
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart()
                                    {

                                    }

                                    @Override
                                    public void onSuccess(File file)
                                    {
                                        Logger.e("image", file.length() + " " + file.getName());
                                        sendMessage(ChatMessage.IMAGE, file);

                                    }

                                    @Override
                                    public void onError(Throwable e)
                                    {

                                    }
                                }).launch();

                    }
                });
                FragmentUtil.addFragment(getSupportFragmentManager(), R.id.chatActivity_MainFrameLayout, pickImageFragment, true);
            }
        });
    }

    private void sendMessage(final int type, final File file)
    {
        Logger.d("why", "whywhywhy");
        String text = "";
        String file_name = null;

        final ChatFileDao chatFileDao = getApp().getDaoSession().getChatFileDao();

        MultipartBody.Part part = null;

        switch (type) {
            case ChatMessage.TEXT:
                text = editText.getText().toString().trim();
                editText.setText("");
                break;

            case ChatMessage.IMAGE:
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                part = MultipartBody.Part.createFormData("file", "file", requestBody);

                file_name = file.getName();

                chatFileDao.insert(new ChatFile(
                        ChatMessage.IMAGE,
                        file_name,
                        Util.convertFileToByte(file)
                ));
                break;

            case ChatMessage.VOICE:
                break;
        }


        //生成临时id，服务器返回后更新message_id
        final long temp_id;
        ChatMessage c = chatMessageDao.queryBuilder()
                .orderAsc(ChatMessageDao.Properties.Id)
                .limit(1)
                .unique();

        if (c != null) {
            temp_id = c.getId() - 1;
        } else {
            temp_id = -1;
        }

        final ChatMessage temp_message = new ChatMessage(
                temp_id,
                user.getId(),
                friend.getId(),
                type,
                text,
                file_name,
                ChatMessage.SENDING
        );

        chatMessageDao.insert(temp_message);
        chatMessageArrayList.add(temp_message);


        chatAdapter.notifyDataSetChanged();

        RetrofitUtil.getInstance().service(ChatService.class)
                .sendMessage(user.getId(), friend.getId(), type,
                        type == ChatMessage.TEXT ? RequestBody.create(null, text) : null,
                        type == ChatMessage.TEXT ? null : part)
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
                        ChatMessage chatMessage = chatMessageResponse.getData();

                        chatMessage.setTimestamp(TimeUtil.covertToLocalTime(chatMessage.getTimestamp()));

                        chatMessageDao.deleteByKey(temp_id);
                        chatMessageDao.insertOrReplaceInTx(chatMessage);

                        if (type != ChatMessage.TEXT) {
                            ChatFile chatFile = chatFileDao.queryBuilder().where(ChatFileDao.Properties.File_name.eq(file.getName())).unique();
                            chatFile.setFile_name(chatMessage.getPath());
                            chatFileDao.update(chatFile);

                            temp_message.setPath(chatMessage.getPath());
                        }

                        temp_message.setStatus(ChatMessage.SEND_SUCCESS);
                        chatAdapter.notifyDataSetChanged();
                    }
                });
    }


    private class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

        public ChatAdapter(List<ChatMessage> data)
        {
            super(data);

            addItemType(ChatMessage.TEXT_SELF, R.layout.chat_text_self);
            addItemType(ChatMessage.TEXT_OTHER, R.layout.chat_text_other);
            addItemType(ChatMessage.IMAGE_SELF, R.layout.chat_image_self);
            addItemType(ChatMessage.IMAGE_OTHER, R.layout.chat_image_other);
            addItemType(ChatMessage.VOICE_SELF, R.layout.chat_text_other);
            addItemType(ChatMessage.VOICE_OTHER, R.layout.chat_text_other);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ChatMessage message)
        {
            switch (holder.getItemViewType()) {

                case ChatMessage.TEXT_SELF:
                case ChatMessage.TEXT_OTHER:
                    holder.setText(R.id.chatText_textView, message.getContent());
                    break;

                case ChatMessage.IMAGE_SELF:
                case ChatMessage.IMAGE_OTHER:
                    ImageView imageView = holder.getView(R.id.chat_imageView);
                    ImageUtil.displayImage(imageView, message.getPath());
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

            retryImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (message.getContent_type() == ChatMessage.TEXT) {

                    } else {
                        Util.toast(getApplicationContext(), getString(R.string.not_supported));
                    }
                }
            });

        }

        public void resend(final ChatMessage chatMessage)
        {

            int type = chatMessage.getContent_type();

            RetrofitUtil.getInstance().service(ChatService.class)
                    .sendMessage(user.getId(), friend.getId(), type,
                            RequestBody.create(null, chatMessage.getContent()), null)
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
                            chatMessage.setStatus(ChatMessage.SEND_FAILED);
                            chatAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNext(Response<ChatMessage> chatMessageResponse)
                        {
                            ChatMessage chatMessage = chatMessageResponse.getData();

                            chatMessage.setTimestamp(TimeUtil.covertToLocalTime(chatMessage.getTimestamp()));

                            chatMessageDao.deleteByKey(chatMessage.getId());
                            chatMessageDao.insertOrReplaceInTx(chatMessage);

                            chatMessage.setStatus(ChatMessage.SEND_SUCCESS);
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
}
