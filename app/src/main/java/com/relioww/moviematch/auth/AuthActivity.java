package com.relioww.moviematch.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.relioww.moviematch.MainActivity;
import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AuthActivity extends AppCompatActivity {
    private final String TAG = "AuthActivity";

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;
    private BackendAPI backendAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backendAPI = new BackendAPI();
        preferences = getSharedPreferences("user", MODE_PRIVATE);

        String access_token =  preferences.getString("access_token", "");
        if (access_token.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        backendAPI.setAccessToken(access_token);
        checkAccessToken();
    }

    public void checkAccessToken() {
        backendAPI.getMe(new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Intent intent = new Intent(AuthActivity.this,
                        MainActivity.class);
                prefEditor = preferences.edit();
                try {
                    int id = result.getInt("id");
                    String username = result.getString("username");
                    prefEditor.putInt("id", id);
                    prefEditor.putString("username", username);
                    prefEditor.apply();
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                    intent = new Intent(AuthActivity.this,
                            LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(IOException e) {
                Intent intent = new Intent(AuthActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}