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
import com.relioww.moviematch.databinding.ActivityRegistrationBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {
    private final String TAG = "Registration";

    private BackendAPI backendAPI;
    private SharedPreferences preferences;

    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backendAPI = new BackendAPI();
        preferences = getSharedPreferences("user", MODE_PRIVATE);

        binding.registerButton.setOnClickListener(onRegisterClick);
        binding.goBackButton.setOnClickListener(onGoBackClick);
    }

    private final View.OnClickListener onRegisterClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = binding.usernameField.getText().toString();
            String password = binding.passwordField.getText().toString();
            String passwordConfirmation = binding.confirmPasswordField
                    .getText()
                    .toString();

            int error = checkDataForErrors(username, password,
                    passwordConfirmation);
            if (error != -1) {
                showToast(error);
                return;
            }

            registerUser(username, password);
        }
    };

    private int checkDataForErrors(String username, String password,
                                   String passwordConfirmation) {
        int error = -1;
        if (username.isBlank() || username.length() < 5) {
            error = R.string.username_too_short;
        } else if (password.isBlank() ||password.length() < 8) {
            error = R.string.password_too_short;
        } else if (!password.equals(passwordConfirmation)) {
            error = R.string.check_password_error;
        }
        return error;
    }

    private void registerUser(String username, String password) {
        backendAPI.register(username, password, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                String access_token;
                try {
                    access_token = result.getString("access_token");
                } catch (JSONException e) {
                    showToast(R.string.registration_error);
                    Log.e(TAG, e.toString());
                    return;
                }

                onRegisterSuccess(access_token);
            }

            @Override
            public void onFailure(IOException e) {
                showToast(R.string.registration_error);
                Log.e(TAG, e.toString());
            }
        });
    }

    private final View.OnClickListener onGoBackClick = (view) -> {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    };

    private void onRegisterSuccess(String accessToken) {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("access_token", accessToken);
        prefEditor.apply();

        Intent intent = new Intent(
                RegistrationActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(int string) {
        this.runOnUiThread(() -> Toast.makeText(this, string, Toast.LENGTH_SHORT).show());
    }
}