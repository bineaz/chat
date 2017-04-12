package com.by.communication.re;

import com.by.communication.entity.Friend;
import com.by.communication.entity.Response;
import com.by.communication.entity.User;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("login")
    Observable<Response<User>> login(@Field("phone") String phone, @Field("password") String password);

    @GET("getFriend/{user_id}")
    Observable<Response<List<Friend>>> getFriend(@Path("user_id") long user_id);
}
