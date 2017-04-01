package com.by.communication.net.okhttp.builder;

import com.by.communication.net.okhttp.HttpUtil;
import com.by.communication.net.okhttp.request.OtherRequest;
import com.by.communication.net.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, HttpUtil.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
