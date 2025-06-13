package com.relioww.moviematch.films;

import android.graphics.Bitmap;
import android.util.Log;

import com.relioww.moviematch.callbacks.BitmapCallback;
import com.relioww.moviematch.callbacks.OnFilmsListUpdated;
import com.relioww.moviematch.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FilmsListUpdater {
    private final String TAG = "FilmsListUpdater";

    private final OnFilmsListUpdated callback;
    private final ArrayList<FilmItem> filmsList;
    private final ImageLoader imageLoader;

    public FilmsListUpdater(ArrayList<FilmItem> filmsList,
                            OnFilmsListUpdated callback) {
        this.filmsList = filmsList;
        this.callback = callback;
        imageLoader = new ImageLoader();
    }

    public void updateList(JSONArray newData) throws JSONException {
        for (int i = 0; i < newData.length(); i++) {
            JSONObject item = (JSONObject) newData.get(i);
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
                    callback.onItemChanged(index);
                }

                @Override
                public void onFailure(IOException e) {
                    Log.e(TAG, e.toString());
                }
            });
        }
    }
}
