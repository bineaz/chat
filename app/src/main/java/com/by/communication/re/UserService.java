package com.by.communication.re;

import com.by.communication.entity.Response;
import com.by.communication.entity.User;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("login")
    Observable<Response<User>> login(@Field("username") String username, @Field("phone") String phone, @Field("password") String password);
}
