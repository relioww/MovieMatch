package com.relioww.moviematch.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    private BackendAPI backendAPI;
    private SharedPreferences preferences;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backendAPI = new BackendAPI();
        preferences = getSharedPreferences("user", MODE_PRIVATE);

        binding.loginButton.setOnClickListener(onLoginClick);
        binding.registrationButton.setOnClickListener(onRegistrationClick);
    }

    private final View.OnClickListener onLoginClick = (view) -> {
        String username = binding.usernameField.getText().toString();
        String password = binding.passwordField.getText().toString();

        backendAPI.login(username, password, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    onLoginSuccess(result.getString("access_token"));
                } catch (JSONException e) {
                    showLoginError();
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(IOException e) {
                showLoginError();
                Log.e(TAG, e.toString());
            }
        });
    };

    private final View.OnClickListener onRegistrationClick = (view) -> {
        Intent intent = new Intent(LoginActivity.this,
                RegistrationActivity.class);
        finish();
        startActivity(intent);
    };

    private void onLoginSuccess(String accessToken) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("access_token", accessToken);
        prefEditor.apply();

        Intent intent = new Intent(this,
                AuthActivity.class);
        startActivity(intent);
        finish();

    }

    private void showLoginError() {
        this.runOnUiThread(() -> Toast.makeText(this,
                getString(R.string.incorrect_login_data),
                Toast.LENGTH_LONG).show());
    }
}