package com.by.communication.entity;

/**
 * Produced a lot of bug on 2017/4/1.
 */

public class Response<T> {
    public static final int CODE_SUCCESS = 200;
    private int    code;
    private String info;
    T data;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "Response{" +
                "code=" + code +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isSuccess()
    {
        return code == CODE_SUCCESS;
    }
}
