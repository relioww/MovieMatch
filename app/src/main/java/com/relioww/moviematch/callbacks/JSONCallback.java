package com.relioww.moviematch.callbacks;

import org.json.JSONObject;

import java.io.IOException;

public interface JSONCallback {
    void onSuccess(JSONObject result);
    void onFailure(IOException e);
}
