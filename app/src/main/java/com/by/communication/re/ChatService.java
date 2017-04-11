package com.by.communication.re;

import com.by.communication.entity.ChatMessage;
import com.by.communication.entity.Response;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public interface ChatService {
    @GET("getChatHistory/{user_id}/{last_message_id}")
    Observable<Response<List<ChatMessage>>> getChatMessageHistory(@Path("user_id") long user_id, @Path("last_message_id") long last_message_id);

    /**
     * @param sender_id    发送者id
     * @param receiver_id  接收者id
     * @param content_type 消息类型
     * @param content      文字消息
     * @param file         文件消息  图片 语音 文档
     * @return
     */
    @POST("sendMessage")
    @Multipart
    Observable<Response<ChatMessage>> sendMessage(
            @Part("sender_id") long sender_id, @Part("receiver_id") long receiver_id,
            @Part("content_type") int content_type, @Part("content") RequestBody content,
            @Part("length") int length, @Part MultipartBody.Part file);
}
