package com.relioww.moviematch.films;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.BitmapCallback;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.core.ImageLoader;
import com.relioww.moviematch.databinding.ActivityFilmPageBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FilmPageActivity extends AppCompatActivity {
    private final String TAG = "FilmPageActivity";

    private BackendAPI backendAPI;
    private ImageLoader imageLoader;

    private ActivityFilmPageBinding binding;

    private int id;
    private boolean isFavorite;
    private String name, year, rating, description, poster, webUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilmPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences(
                "user", MODE_PRIVATE);
        String accessToken = preferences.getString(
                "access_token", "");

        backendAPI = new BackendAPI(accessToken);
        imageLoader = new ImageLoader();

        getFilmData();
        getFilmPoster();

        binding.filmName.setText(name);
        binding.filmYear.setText(year);
        binding.filmRating.setText(rating);
        binding.filmDescription.setText(description);

        binding.manageFavorite.setText(isFavorite
                ? R.string.remove_from_favorites
                : R.string.add_to_favorites);

        binding.manageFavorite.setOnClickListener(manageFavorite);
        binding.filmPageGoBack.setOnClickListener((view) -> finish());
        binding.kinopoiskLink.setOnClickListener(onKinopoiskLinkClick);
        binding.leaveReview.setOnClickListener(onLeaveReviewClick);
    }

    private final View.OnClickListener onKinopoiskLinkClick = (view) -> {
        Uri uri = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    };

    private final View.OnClickListener onLeaveReviewClick = (view) -> {
        Intent intent = new Intent(FilmPageActivity.this,
                FilmReviewActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    };

    private void getFilmData() {
        Intent intent = getIntent();

        id = intent.getIntExtra("id", 1);
        name = intent.getStringExtra("name");
        poster = intent.getStringExtra("poster");
        webUrl = intent.getStringExtra("web_url");

        year = getString(R.string.film_release_year,
                intent.getStringExtra("year"));

        isFavorite = intent.getBooleanExtra("is_favorite",
                false);

        rating = intent.getDoubleExtra(
                "rating", 0) == 0
                ? getString(R.string.kinopoisk_no_rating)
                : getString(R.string.kinopoisk_rating,
                intent.getDoubleExtra(
                        "rating", 0));

        description = intent.getStringExtra(
                "description").equals("null")
                ? getString(R.string.film_no_description)
                : intent.getStringExtra("description");
    }

    private void getFilmPoster() {
        imageLoader.getImage(poster, new BitmapCallback() {
            @Override
            public void onSuccess(Bitmap bitmap, int index) {
                FilmPageActivity.this.runOnUiThread(() -> {
                    binding.filmPoster.setImageBitmap(bitmap);
                });
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    }

    private final View.OnClickListener manageFavorite = (view) -> {
        int toastSuccessText = isFavorite
                ? R.string.removed_from_favorites
                : R.string.added_to_favorites;

        JSONCallback onResponse = new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    int toastText = R.string.favorites_error;

                    try {
                        String status = result.getString("status");
                        if (status.equals("ok")) {
                            isFavorite = !isFavorite;
                            binding.manageFavorite.setText(isFavorite
                                    ? R.string.remove_from_favorites
                                    : R.string.add_to_favorites);
                            toastText = toastSuccessText;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }

                    Toast.makeText(FilmPageActivity.this,
                            toastText, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(FilmPageActivity.this,
                            R.string.favorites_error,
                            Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, e.toString());
            }
        };

        if (isFavorite) {
            backendAPI.removeFromFavorites(id, onResponse);
        } else {
            backendAPI.addToFavorites(id, onResponse);
        }

    };

}