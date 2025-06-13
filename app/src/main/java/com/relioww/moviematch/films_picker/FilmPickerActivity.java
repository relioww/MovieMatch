package com.relioww.moviematch.films_picker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FilmPickerActivity extends AppCompatActivity {
    private final String TAG = "FilmPickerActivity";

    private BackendAPI backendAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_picker);

        SharedPreferences preferences = getSharedPreferences(
                "user", MODE_PRIVATE);
        String accessToken = preferences.getString(
                "access_token", "");
        backendAPI = new BackendAPI(accessToken);

        PickerLoadingFragment loadingFragment = new PickerLoadingFragment();
        PickerMainFragment mainFragment = new PickerMainFragment((selectedFriends, prefs) -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.pickerFragmentView, loadingFragment)
                    .commit();
            pickFilms(selectedFriends, prefs);
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pickerFragmentView, mainFragment)
                .commit();
    }

    public void pickFilms(ArrayList<Integer> selectedFriends, String prefs) {
        backendAPI.pickFilms(selectedFriends, prefs, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Intent intent = new Intent(FilmPickerActivity.this,
                        PickerResultActivity.class);
                intent.putExtra("films", result.toString());
                finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    }
}