package com.by.communication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.entity.ChatFile;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.fragment.PhotoFragment;
import com.by.communication.fragment.image.ImageItem;
import com.by.communication.fragment.image.PickImageFragment;
import com.by.communication.gen.ChatFileDao;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.media.RecordVoiceButton;
import com.by.communication.media.VoiceManager;
import com.by.communication.net.PushSocketService;
import com.by.communication.net.okhttp.FileRequestBody;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.RetrofitCallback;
import com.by.communication.net.okhttp.callback.FileCallBack;
import com.by.communication.permission.AndPermission;
import com.by.communication.permission.PermissionListener;
import com.by.communication.re.ChatService;
import com.by.communication.util.ConstantUtil;
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
import butterknife.OnClick;
import okhttp3.Call;
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
    ImageView         funcImageView;
    @BindView(R.id.chatActivity_inputTypeImageButton)
    ImageButton       inputTypeImageButton;
    @BindView(R.id.chatActivity_recordVoiceButton)
    RecordVoiceButton recordVoiceButton;

    private ChatAdapter            chatAdapter;
    private ArrayList<ChatMessage> chatMessageArrayList;

    private User friend;
    private User user;

    private Handler handler = new Handler();

    private ChatMessageDao      chatMessageDao;
    private LinearLayoutManager manager;

    private VoiceManager voiceManager;

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
        voiceManager = VoiceManager.getInstance(getApplicationContext());

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

                sendMessage(ChatMessage.TEXT, null, 0);
            }
        });

        funcImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Util.hideKeyBoard(editText);

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
                                        sendMessage(ChatMessage.IMAGE, file, 0);

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

        recordVoiceButton.setOnRecordVoiceListener(new RecordVoiceButton.OnRecordVoiceListener() {
            @Override
            public void onFinishRecord(long length, String strLength, String filePath)
            {
                File file = new File(filePath);
                sendMessage(ChatMessage.AUDIO, file, (int) length);
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        recordVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (AndPermission.hasPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                    recordVoiceButton.startRecord();
                } else {
                    if (AndPermission.hasAlwaysDeniedPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                        AndPermission.defaultSettingDialog(ChatActivity.this, 100).show();
                    } else {
                        AndPermission.with(ChatActivity.this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO).requestCode(100).send();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();

                File file = new File(uri.getPath());
                sendMessage(ChatMessage.FILE, file, (int) file.length());

//                System.out.println(uri.getPath().toString() + "  " + file.length());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions)
        {
            // 权限申请成功回调。
            if (requestCode == 100) {
                recordVoiceButton.startRecord();
            } else if (requestCode == 101) {
                Util.toast(getApplicationContext(), "手机坏了");
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions)
        {
            // 权限申请失败回调。
            String[] strings = new String[deniedPermissions.size()];
            for (int i = 0; i < deniedPermissions.size(); i++) {
                strings[i] = deniedPermissions.get(i);
            }

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(ChatActivity.this, strings)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(ChatActivity.this, 100).show();
            }
        }
    };

    @OnClick({R.id.chatActivity_inputTypeImageButton})
    public void click(View v)
    {
        switch (v.getId()) {
            case R.id.chatActivity_inputTypeImageButton:

                if (editText.isShown()) {
                    editText.setVisibility(View.GONE);
                    recordVoiceButton.setVisibility(View.VISIBLE);
                } else {
                    editText.setVisibility(View.VISIBLE);
                    recordVoiceButton.setVisibility(View.GONE);
                }

                break;
        }
    }

    private void sendMessage(final int type, final File file, int length)
    {
        String text = "";
        String file_name = file == null ? null : file.getName();

        final ChatFileDao chatFileDao = getApp().getDaoSession().getChatFileDao();

        MultipartBody.Part part = null;
        RequestBody requestBody = null;

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
                length,
                ChatMessage.SENDING
        );


        switch (type) {
            case ChatMessage.TEXT:
                text = editText.getText().toString().trim();
                editText.setText("");
                break;

            case ChatMessage.IMAGE:
                requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                part = MultipartBody.Part.createFormData("file", "file", requestBody);

                chatFileDao.insert(new ChatFile(
                        ChatMessage.IMAGE,
                        file_name,
                        Util.convertFileToByte(file)
                ));
                break;

            case ChatMessage.AUDIO:

                requestBody = RequestBody.create(MediaType.parse("audio/x-aiff"), file);
                part = MultipartBody.Part.createFormData("file", file_name, requestBody);
                break;

            case ChatMessage.FILE:

                requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                FileRequestBody body = new FileRequestBody(requestBody, new RetrofitCallback() {

                    @Override
                    public void onProgress(long total, float progress)
                    {
                        temp_message.setProgress(progress);
                        System.out.println(total + "  " + progress);
                    }
                });
                part = MultipartBody.Part.createFormData("file", file_name, body);
                break;
        }


        chatMessageDao.insert(temp_message);
        chatMessageArrayList.add(temp_message);


        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatMessageArrayList.size() - 1);

        RetrofitUtil.getInstance().service(ChatService.class)
                .sendMessage(user.getId(), friend.getId(), type,
                        type == ChatMessage.TEXT ? RequestBody.create(null, text) : null,
                        length,
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

                        if (type == ChatMessage.IMAGE) {
                            ChatFile chatFile = chatFileDao.queryBuilder().where(ChatFileDao.Properties.File_name.eq(file.getName())).unique();
                            chatFile.setFile_name(chatMessage.getPath());
                            chatFileDao.update(chatFile);

                            temp_message.setPath(chatMessage.getPath());
                        } else if (type == chatMessage.AUDIO) {

                        }

                        temp_message.setStatus(ChatMessage.SEND_SUCCESS);
                        chatAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (voiceManager.isPlaying()) {
            voiceManager.stopPlay();
        }

        voiceManager = null;
    }

    private class ChatAdapter extends BaseMultiItemQuickAdapter<ChatMessage, BaseViewHolder> {

        public ChatAdapter(List<ChatMessage> data)
        {
            super(data);

            addItemType(ChatMessage.TEXT_SELF, R.layout.chat_text_self);
            addItemType(ChatMessage.TEXT_OTHER, R.layout.chat_text_other);
            addItemType(ChatMessage.IMAGE_SELF, R.layout.chat_image_self);
            addItemType(ChatMessage.IMAGE_OTHER, R.layout.chat_image_other);
            addItemType(ChatMessage.AUDIO_SELF, R.layout.chat_audio_self);
            addItemType(ChatMessage.AUDIO_OTHER, R.layout.chat_audio_other);
            addItemType(ChatMessage.FILE_SELF, R.layout.chat_file_self);
            addItemType(ChatMessage.FILE_OTHER, R.layout.chat_file_other);
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

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            PhotoFragment photoFragment = new PhotoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("path", message.getPath());
                            photoFragment.setArguments(bundle);
                            FragmentUtil.addFragment(getSupportFragmentManager(), R.id.chatActivity_MainFrameLayout, photoFragment, FragmentUtil.SPREAD);
                        }
                    });
                    break;

                case ChatMessage.AUDIO_SELF:
                case ChatMessage.AUDIO_OTHER:
                    TextView audioView = holder.getView(R.id.chat_audioView);

                    int l = message.getLength();
                    int dpw;
                    audioView.setText(l + "'");

                    ViewGroup.LayoutParams params = audioView.getLayoutParams();
                    if (l > 20) {
                        dpw = 220;
                    } else {
                        dpw = 50 + 170 * l / 20;
                    }

                    params.width = Util.dip2px(getApplicationContext(), dpw);
                    audioView.setLayoutParams(params);

                    audioView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if (voiceManager.isPlaying()) {
                                voiceManager.continueOrPausePlay();
                            } else {
                                final String path = ConstantUtil.AUDIO_BASE_PATH + message.getPath();

                                if (!new File(path).exists()) {
                                    HttpUtil.get()
                                            .rawUrl(ConstantUtil.AUDIO_BASE_URL + message.getPath())
                                            .build()
                                            .execute(new FileCallBack(ConstantUtil.AUDIO_BASE_PATH, message.getPath()) {
                                                @Override
                                                public void onError(Call call, Exception e, int id)
                                                {
                                                    Logger.d(TAG, e.getMessage());
                                                }

                                                @Override
                                                public void onResponse(File response, int id)
                                                {
                                                    Logger.d(TAG, message.getPath() + "downloaded");
                                                    playAudio(path);
                                                }
                                            });
                                } else {
                                    playAudio(path);
                                }
                            }
                        }
                    });

                case ChatMessage.FILE_SELF:
                case ChatMessage.FILE_OTHER:

                    if (message.isSending()) {
                        final ProgressBar p = holder.getView(R.id.chat_loadProgressBar);
                        p.setVisibility(View.VISIBLE);
                        message.setOnProgressListener(new ChatMessage.OnProgressListener() {
                            @Override
                            public void onProgress(float progress)
                            {
                                int pro = (int) (progress * 100);
                                p.setProgress(pro);

                                if (pro==100){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            p.setVisibility(View.GONE);
                                        }
                                    });

                                }
                            }
                        });
                    }

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

        private void playAudio(String path)
        {
            voiceManager.startPlay(path);
            voiceManager.setVoicePlayListener(new VoiceManager.VoicePlayCallBack() {
                @Override
                public void voiceTotalLength(long time, String strTime)
                {
                    Logger.d("voice", time + "  " + strTime);
                }

                @Override
                public void playDoing(long time, String strTime)
                {
                    Logger.d("voice", time + "  " + strTime);
                }

                @Override
                public void playPause()
                {

                }

                @Override
                public void playStart()
                {
                    Logger.d("voice", "start");
                }

                @Override
                public void playFinish()
                {
                    Logger.d("voice", "finish");
                }
            });
        }

        public void resend(final ChatMessage chatMessage)
        {

            int type = chatMessage.getContent_type();

            RetrofitUtil.getInstance().service(ChatService.class)
                    .sendMessage(user.getId(), friend.getId(), type,
                            RequestBody.create(null, chatMessage.getContent()), 0, null)
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
