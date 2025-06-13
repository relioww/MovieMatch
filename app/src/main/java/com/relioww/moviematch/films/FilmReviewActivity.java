package com.relioww.moviematch.films;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.databinding.ActivityFilmReviewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FilmReviewActivity extends AppCompatActivity {
    private final String TAG = "FilmReviewActivity";

    private ActivityFilmReviewBinding binding;
    private BackendAPI backendAPI;
    private int filmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilmReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sendReview.setOnClickListener(onReviewSend);
        binding.reviewCancel.setOnClickListener((view) -> finish());

        SharedPreferences preferences = getSharedPreferences(
                "user", MODE_PRIVATE);
        String accessToken = preferences.getString("access_token", "");
        backendAPI = new BackendAPI(accessToken);

        filmId = getIntent().getIntExtra("id", 1);
    }

    private void showToast(int text) {
        this.runOnUiThread(() -> {
            Toast.makeText(FilmReviewActivity.this,
                    text, Toast.LENGTH_SHORT).show();
        });
    }

    private final View.OnClickListener onReviewSend = (view) -> {
        float rating = binding.ratingBar.getRating();
        String commentary = binding.filmCommentary.getText().toString();

        backendAPI.leaveReview(filmId, rating, commentary, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if (status.equals("ok")) {
                        showToast(R.string.review_left);
                    } else if (status.equals("error")) {
                        Log.e(TAG, result.getString("description"));
                        showToast(R.string.review_error);
                    }
                    finish();
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                    showToast(R.string.review_error);
                    finish();
                }
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
                showToast(R.string.review_error);
                finish();
            }
        });
    };
}