/*Property of C U Soon*/


package com.by.communication.net;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.by.communication.App;
import com.by.communication.entity.ChatMessage;
import com.by.communication.gen.ChatMessageDao;
import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.callback.StringCallback;
import com.by.communication.util.ConstantUtil;
import com.by.communication.util.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import rx.Observable;
import rx.functions.Action1;

public class PushSocketService extends Service {
    public static final String CHAT_MESSAGE = "chat_message";
    private WebSocketFactory ws_factory;
    private WebSocket        ws;

    private String androidDeviceToken;
    private String webSocketToken;

    private ChatMessageDao chatMessageDao;

    @Override
    public void onCreate()
    {
        ns_connect();

        androidDeviceToken = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        chatMessageDao = App.getInstance().getDaoSession().getChatMessageDao();


        PingPong pp = new PingPong();
        Timer ppTimer = new Timer();
//
        ppTimer.schedule(pp, 10000, 120000);

        System.out.println("onCreate");
        super.onCreate();
    }

    private void ns_connect()
    {

        try {
            ws_factory = new WebSocketFactory();
            ws = ws_factory.createSocket(ConstantUtil.SOCKET_URI, 5000);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket WebSocket, String message) throws Exception
            {
                decodeMessage(message);
            }

            @Override
            public void onDisconnected(WebSocket WebSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            {

            }

            @Override
            public void onConnected(WebSocket WebSocket, Map<String, List<String>> headers)
            {

                System.out.println("connected");

                List<String> headerDetail;

                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {

                    headerDetail = entry.getValue();
                    for (int i = 0; i < headerDetail.size(); i++) {
                    }
                }
            }

            @Override
            public void onConnectError(WebSocket WebSocket, WebSocketException cause)
            {
                cause.printStackTrace();
            }

            @Override
            public void onError(WebSocket WebSocket, WebSocketException cause)
            {
                cause.printStackTrace();
            }

            @Override
            public void onSendError(WebSocket WebSocket, WebSocketException cause, WebSocketFrame frame) throws Exception
            {
            }
        });

        ws.connectAsynchronously();
    }

    private void decodeMessage(String message)
    {
        System.out.println(message);

        //连接socket成功消息
        if (message.startsWith("Hello")) {
            webSocketToken = message.substring(6, message.length());
            registerSocketToken();
        }

        //过滤心跳 (ping)
        else if (!message.endsWith("g")) {
            try {
                Gson gson = new Gson();
                ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);

                chatMessageDao.insertOrReplace(chatMessage);
                postMessage(chatMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void postMessage(ChatMessage chatMessage)
    {
        Bundle bundle = new Bundle();
        bundle.putString("action", CHAT_MESSAGE);
        bundle.putParcelable("chatMessage", chatMessage);
        EventBus.getDefault().post(bundle);
    }

    private void registerSocketToken()
    {
        HttpUtil.getInstance().post()
                .url("registerSocket")
                .addParams("user_id", String.valueOf(App.getInstance().getUser().getId()))
                .addParams("device_id", androidDeviceToken)
                .addParams("socket_id", webSocketToken)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        e.printStackTrace();
//                        registerSocketToken();
                    }

                    @Override
                    public void onResponse(String response, int id)
                    {
                        Logger.e("socket", response.toString());
                        loadHistory();
                    }
                });
    }

    private void loadHistory()
    {

    }

    @Override
    public void onDestroy()
    {
        ws.disconnect(); //disconnect WebSocket.
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        System.out.println("onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        flags = START_FLAG_RETRY;
        return super.onStartCommand(intent, flags, startId);
    }


    private class PingPong extends TimerTask {
        public void run()
        {
            ws.sendPing("ping");
        }
    }

}









