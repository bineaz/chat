package com.by.communication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.activity.ChatActivity;
import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.ChatRecord;
import com.by.communication.entity.Response;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.re.ChatService;
import com.by.communication.re.SubscriberAdapter;
import com.by.communication.util.ImageUtil;
import com.by.communication.util.Logger;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.util.Usp;
import com.by.communication.widgit.listView.InsetListView;
import com.by.communication.widgit.listView.adapter.ListHolder;
import com.by.communication.widgit.listView.adapter.SingleListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class ChatRecordFragment extends BaseFragment {
    @BindView(R.id.chatRecordFragment_listView)
    InsetListView listView;
    private ChatAdapter           adapter;
    private ArrayList<ChatRecord> chatRecordArrayList;

    @Override
    public int getResId()
    {
        return R.layout.chat_record_fragment;
    }

    @Override
    public void init(View view)
    {
        chatRecordArrayList = new ArrayList<>();
        adapter = new ChatAdapter(chatRecordArrayList);
        listView.setAdapter(adapter);

//        final long max_message_id;
//        ChatMessageDao chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();
//        ChatMessage c = chatMessageDao.queryBuilder()
//                .orderDesc(ChatMessageDao.Properties.Id)
//                .limit(1)
//                .unique();
//
//        if (c != null) {
//            max_message_id = c.getId();
//        } else {
//            max_message_id = 0;
//        }
//
//        Logger.d("max_id", "" + max_message_id);
//
//        RetrofitUtil.getInstance()
//                .service(ChatService.class)
//                .getChatMessageHistory(Usp.getInstance().getUserId(), max_message_id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SubscriberAdapter<Response<List<ChatMessage>>>() {
//                    @Override
//                    public void onCompleted()
//                    {
//
//                    }
//
//                    @Override
//                    public void onNext(Response<List<ChatMessage>> response)
//                    {
//                        if (response.getCode() == response.CODE_SUCCESS) {
//                            ChatMessageDao chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();
//
//                            chatMessageDao.insertOrReplaceInTx(response.getData());
//
//
//                        }
//                    }
//                });

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://chat.tangcheng.me/index.php/Chat/")
////                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(HttpUtil.getInstance().getOkHttpClient())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        ChatService chatService = retrofit.create(ChatService.class);
//
//        Observable<Response<List<ChatMessage>>> observable = chatService.getChatMessageHistory(1, 1);
//        observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Response<List<ChatMessage>>>() {
//                    @Override
//                    public void onCompleted()
//                    {
////                        Logger.e("", "complete");
//                    }
//
//                    @Override
//                    public void onError(Throwable e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Response<List<ChatMessage>> response)
//                    {
//                        System.out.println(response.toString());
//
//                        if (response.getCode() == response.CODE_SUCCESS) {
//                            ChatMessageDao chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();
//
//                            chatMessageDao.insertOrReplaceInTx(response.getData());
//
//
//                        }
//
//
//                    }
//                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user_id", chatRecordArrayList.get(i).getChat_id());
                startActivity(intent);
            }
        });
    }


    private class ChatAdapter extends SingleListAdapter<ChatRecord> {

        public ChatAdapter(List<ChatRecord> dataList)
        {
            super(dataList);
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.chat_record_item;
        }

        @Override
        public void convert(ListHolder holder, ChatRecord data, int position)
        {
            ImageView avatar = holder.getView(R.id.chatRecordItem_avatar);
            TextView timeTextView = holder.getView(R.id.chatRecordItem_timeTextView);
            TextView nameTextView = holder.getView(R.id.chatRecordItem_nameTextView);
            TextView messageTextView = holder.getView(R.id.chatRecordItem_messageTextView);

            timeTextView.setText(data.getLast_chat_message().getTimestamp());
            nameTextView.setText(data.getChat_id() + "");
            messageTextView.setText(data.getLast_chat_message().getContent());
            ImageUtil.displayAvatar(getActivity(), avatar, 1);

        }
    }

}
