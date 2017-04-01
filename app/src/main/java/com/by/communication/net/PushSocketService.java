/*Property of C U Soon*/


package com.by.communication.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.by.communication.util.ConstantUtil;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

public class PushSocketService extends Service {
    private WebSocketFactory ws_factory;
    private WebSocket        ws;

    @Override
    public void onCreate()
    {
        ns_connect();

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
                System.out.println(message);
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
//                try {
//                    Observable.timer(3, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
//                        @Override
//                        public void call(Long aLong)
//                        {
//                            ns_connect();
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

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
            ws.sendPing("Are you there?");
        }
    }

}









