package com.relioww.moviematch.favorites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.databinding.ActivityFavoritesBinding;
import com.relioww.moviematch.films.FilmItem;
import com.relioww.moviematch.films.FilmsListAdapter;
import com.relioww.moviematch.callbacks.OnFilmItemClickListener;
import com.relioww.moviematch.callbacks.OnFilmsListUpdated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {
    private final String TAG = "FavoritesActivity";

    private BackendAPI backendAPI;
    private ActivityFavoritesBinding binding;
    private FilmsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences(
                "user", MODE_PRIVATE);
        String accessToken = preferences.getString(
                "access_token", "");

        backendAPI = new BackendAPI(accessToken);

        binding.favoritesGoBack.setOnClickListener((view) -> finish());

        setupRecyclerView();
        updateFavorites();
    }

    private void setupRecyclerView() {
        binding.favoritesView.setLayoutManager(new LinearLayoutManager(this));
        OnFilmItemClickListener onItemClick = new OnFilmItemClickListener(
                this, backendAPI);
        OnFilmsListUpdated onListUpdated = (index) -> {
            runOnUiThread(() -> adapter.notifyItemChanged(index));
        };
        ArrayList<FilmItem> favoritesList = new ArrayList<>();
        adapter = new FilmsListAdapter(favoritesList, onItemClick, onListUpdated);
        binding.favoritesView.setAdapter(adapter);
    }

    private void updateFavorites() {
        backendAPI.getFavorites(new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    try {
                        JSONArray films = result.getJSONArray("results");
                        adapter.insertData(films);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                });
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    }

}