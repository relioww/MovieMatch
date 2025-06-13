package com.relioww.moviematch.friends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.databinding.ActivityChooseFriendsBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ChooseFriendsActivity extends AppCompatActivity {
    private final String TAG = "ChooseFriendsActivity";

    private ArrayList<FriendItem> friendsList;
    private BackendAPI backendAPI;
    private ActivityChooseFriendsBinding binding;
    private FriendsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backendAPI = new BackendAPI();
        SharedPreferences preferences = getSharedPreferences(
                "user", MODE_PRIVATE);
        backendAPI.setAccessToken(preferences.getString(
                "access_token", ""));

        binding.chooseOk.setOnClickListener(onChooseOkClick);

        setupRecyclerView();
        getFriends();
    }

    private void getFriends() {
        backendAPI.getFriends(new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                runOnUiThread(() -> {
                    try {
                        JSONArray friends = result.getJSONArray("friends");
                        updateFriendsList(friends);
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

    private void updateFriendsList(JSONArray friends) throws JSONException {
        for (int i = 0; i < friends.length(); i++) {
            JSONObject friend = friends.getJSONObject(i);
            friendsList.add(new FriendItem(
                    friend.getInt("friend_request_id"),
                    friend.getInt("user_id"),
                    friend.getString("username")
            ));
            adapter.notifyItemInserted(i);
        }
    }

    private void setupRecyclerView() {
        binding.friendsChooseView.setLayoutManager(new LinearLayoutManager(this));
        friendsList = new ArrayList<>();
        adapter = new FriendsListAdapter(
                friendsList, true, null);
        binding.friendsChooseView.setAdapter(adapter);
    }

    private final View.OnClickListener onChooseOkClick = (view) -> {
        ArrayList<Integer> selectedFriends = adapter.getSelectedFriendsIds();
        Intent data = new Intent();
        data.putIntegerArrayListExtra("selected_friends", selectedFriends);
        setResult(RESULT_OK, data);
        finish();
    };
}