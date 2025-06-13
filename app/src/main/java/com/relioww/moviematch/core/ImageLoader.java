package com.relioww.moviematch.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.relioww.moviematch.callbacks.BitmapCallback;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ImageLoader {
    private final String TAG = "ImageLoader";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public void getImage(String url, BitmapCallback callback) {
        getImage(url, -1, callback);
    }

    public void getImage(String url, int index, BitmapCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(
                                new IOException("Ошибка при выполнении http запроса"));
                    }

                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    callback.onSuccess(bitmap, index);
                }
            }
        });
    }
}
