package com.relioww.moviematch.callbacks;

import android.graphics.Bitmap;

import java.io.IOException;

public interface BitmapCallback {
    void onSuccess(Bitmap bitmap, int index);
    void onFailure(IOException e);
}