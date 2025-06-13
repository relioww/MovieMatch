package com.relioww.moviematch.callbacks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.films.FilmItem;
import com.relioww.moviematch.films.FilmPageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OnFilmItemClickListener {
    private final String TAG = "FilmItemClickListener";

    Context context;
    BackendAPI backendAPI;

    public OnFilmItemClickListener(Context context, BackendAPI backendAPI) {
        this.context = context;
        this.backendAPI = backendAPI;
    }

    public void onClick(FilmItem item) {
        backendAPI.getFilm(item.getId(), new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Intent intent = new Intent(context, FilmPageActivity.class);
                try {
                    intent.putExtra("id", result.getInt("id"));
                    intent.putExtra("name", result.getString("name"));
                    intent.putExtra("year", result.getString("year"));
                    intent.putExtra("rating", result.getDouble("rating"));
                    intent.putExtra("description", result.getString("description"));
                    intent.putExtra("poster", result.getString("poster"));
                    intent.putExtra("web_url", result.getString("web_url"));
                    intent.putExtra("is_favorite", result.getBoolean("is_favorite"));
                    context.startActivity(intent);
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    }
}
