package com.by.communication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.by.communication.App;
import com.by.communication.R;
import com.by.communication.activity.ChatActivity;
import com.by.communication.entity.Friend;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;
import com.by.communication.gen.FriendDao;
import com.by.communication.gen.UserDao;
import com.by.communication.re.UserService;
import com.by.communication.util.ImageUtil;
import com.by.communication.util.RetrofitUtil;
import com.by.communication.widgit.listView.InsetListView;
import com.by.communication.widgit.listView.adapter.ListHolder;
import com.by.communication.widgit.listView.adapter.SingleListAdapter;

import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class FriendFragment extends BaseFragment {
    @BindView(R.id.friendFragment_listView)
    InsetListView listView;

    private List<User>    userArrayList;
    private FriendAdapter adapter;

    @Override
    public int getResId()
    {
        return R.layout.friend_fragment;
    }

    @Override
    public void init(View view)
    {
        final UserDao userDao = App.getInstance().getDaoSession().getUserDao();

        userArrayList = userDao.loadAll();
        adapter = new FriendAdapter(userArrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("user_id", userArrayList.get(i).getId());
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", userArrayList.get(i));
                intent.putExtra("user", bundle);
                startActivity(intent);
            }
        });

        if (userArrayList.size() < 20) {
//            final Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("http://chat.tangcheng.me/index.php/Chat/")
////                .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(HttpUtil.getInstance().getOkHttpClient())
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .build();

            RetrofitUtil.getInstance()
                    .service(UserService.class)
                    .getFriend(1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<List<Friend>>>() {
                        @Override
                        public void onCompleted()
                        {
//                        Logger.e("", "complete");
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Response<List<Friend>> response)
                        {
                            System.out.println(response.toString());

                            if (response.getCode() == response.CODE_SUCCESS) {
                                List<Friend> friendList = response.getData();
                                userArrayList.clear();
                                for (int i = 0; i < friendList.size(); i++) {
                                    userArrayList.add(friendList.get(i).convertToUser());
                                }

                                userDao.insertOrReplaceInTx(userArrayList);
                                FriendDao friendDao = App.getInstance().getDaoSession().getFriendDao();
                                friendDao.insertOrReplaceInTx(friendList);

                                adapter.notifyDataSetChanged();
                            }


                        }
                    });

//            UserService userService = retrofit.create(UserService.class);
//
//            Observable<Response<List<Friend>>> observable = userService.getFriend(1);
//            observable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<Response<List<Friend>>>() {
//                        @Override
//                        public void onCompleted()
//                        {
////                        Logger.e("", "complete");
//                        }
//
//                        @Override
//                        public void onError(Throwable e)
//                        {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onNext(Response<List<Friend>> response)
//                        {
//                            System.out.println(response.toString());
//
//                            if (response.getCode() == response.CODE_SUCCESS) {
//                                List<Friend> friendList = response.getData();
//                                userArrayList.clear();
//                                for (int i = 0; i < friendList.size(); i++) {
//                                    userArrayList.add(friendList.get(i).convertToUser());
//                                }
//
//                                userDao.insertOrReplaceInTx(userArrayList);
//                                FriendDao friendDao = App.getInstance().getDaoSession().getFriendDao();
//                                friendDao.insertOrReplaceInTx(friendList);
//
//                                adapter.notifyDataSetChanged();
//                            }
//
//
//                        }
//                    });
        }

    }

    private class FriendAdapter extends SingleListAdapter<User> {

        public FriendAdapter(List<User> dataList)
        {
            super(dataList);
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.friend_fragment_item;
        }

        @Override
        public void convert(ListHolder holder, User data, int position)
        {
            TextView nameTextView = holder.getView(R.id.friendFragmentItem_nameTextView);
            ImageView avatar = holder.getView(R.id.friendFragmentItem_avatar);

            nameTextView.setText(data.getUsername());
            ImageUtil.displayAvatar(getActivity(), avatar, 1);
        }
    }
}
