package com.relioww.moviematch.films_picker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.relioww.moviematch.databinding.ActivityPickerResultBinding;
import com.relioww.moviematch.films.FilmItem;
import com.relioww.moviematch.films.FilmsListAdapter;
import com.relioww.moviematch.callbacks.BitmapCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.core.ImageLoader;
import com.relioww.moviematch.callbacks.OnFilmItemClickListener;
import com.relioww.moviematch.callbacks.OnFilmsListUpdated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PickerResultActivity extends AppCompatActivity {
    private final String TAG = "PickerResult";

    private ActivityPickerResultBinding binding;
    private ArrayList<FilmItem> filmsList;
    private FilmsListAdapter adapter;
    private BackendAPI backendAPI;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickerResultBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent intent = getIntent();

        SharedPreferences preferences = getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("access_token", "");
        backendAPI = new BackendAPI(accessToken);
        imageLoader = new ImageLoader();

        setupRecyclerView();

        try {
            JSONObject jsonObject = new JSONObject(
                    intent.getStringExtra("films"));
            JSONArray films = jsonObject.getJSONArray("results");
            updateFilmsList(films);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        binding.pickerResultsGoBack.setOnClickListener((v) -> {
            finish();
        });

    }

    private void setupRecyclerView() {
        binding.pickerResultsView.setLayoutManager(new LinearLayoutManager(this));
        filmsList = new ArrayList<>();
        OnFilmItemClickListener onItemClick = new OnFilmItemClickListener(
                this, backendAPI);
        OnFilmsListUpdated onListUpdated = (index) -> {
            runOnUiThread(() -> adapter.notifyItemChanged(index));
        };
        adapter = new FilmsListAdapter(filmsList, onItemClick, onListUpdated);
        binding.pickerResultsView.setAdapter(adapter);
    }

    private void updateFilmsList(JSONArray films) throws JSONException {
        for (int i = 0; i < films.length(); i++) {
            JSONObject item = (JSONObject) films.get(i);
            int id = item.getInt("id");
            String name = item.getString("name");
            int year = item.getInt("year");
            String poster = item.getString("poster");
            filmsList.add(new FilmItem(id, name,
                    null, year));

            imageLoader.getImage(poster, i, new BitmapCallback() {
                @Override
                public void onSuccess(Bitmap bitmap, int index) {
                    filmsList.get(index).setImage(bitmap);
                    runOnUiThread(() -> {
                        adapter.notifyItemChanged(index);
                    });
                }

                @Override
                public void onFailure(IOException e) {
                    Log.e(TAG, e.toString());
                }
            });

        }
    }
}