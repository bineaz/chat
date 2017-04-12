package com.by.communication.re;

/**
 * Produced a lot of bug on 2017/4/10.
 */

import com.by.communication.util.Logger;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson           gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter)
    {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException
    {
        String response = value.string();
        Logger.d("response string", response);

        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);


        try {
            T object = adapter.read(jsonReader);
            Logger.d("response object", object.toString());

            return object;
        } finally {
            value.close();
        }
    }
}