package com.relioww.moviematch.main_menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.databinding.FragmentSearchBinding;
import com.relioww.moviematch.films.FilmItem;
import com.relioww.moviematch.films.FilmsListAdapter;
import com.relioww.moviematch.callbacks.OnFilmItemClickListener;
import com.relioww.moviematch.callbacks.OnFilmsListUpdated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private final String TAG = "SearchFragment";

    private BackendAPI backendAPI;
    private FragmentSearchBinding binding;

    private FilmsListAdapter adapter;
    private List<FilmItem> searchResultsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("access_token", "");
        backendAPI = new BackendAPI(accessToken);

        binding.searchButton.setOnClickListener(searchListener);
        setupRecyclerView();

        return binding.getRoot();
    }

    public void setupRecyclerView() {
        binding.searchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        OnFilmItemClickListener onItemClick = new OnFilmItemClickListener(
                getActivity(), backendAPI);
        OnFilmsListUpdated onListUpdated = (index) -> {
            getActivity().runOnUiThread(() -> adapter.notifyItemChanged(index));
        };
        searchResultsList = new ArrayList<>();
        adapter = new FilmsListAdapter(searchResultsList, onItemClick, onListUpdated);
        binding.searchResults.setAdapter(adapter);
    }

    private final View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchResultsList.clear();
            adapter.notifyDataSetChanged();
            String query = binding.searchQuery.getText().toString();

            backendAPI.search(query, new JSONCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    getActivity().runOnUiThread(() -> {
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
    };

}