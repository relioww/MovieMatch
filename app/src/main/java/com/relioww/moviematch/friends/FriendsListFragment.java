package com.relioww.moviematch.friends;

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
import com.relioww.moviematch.databinding.FragmentFriendsListBinding;
import com.relioww.moviematch.callbacks.OnFriendRemoved;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FriendsListFragment extends Fragment {
    private final String TAG = "FriendsList";

    private BackendAPI backendAPI;
    private FragmentFriendsListBinding binding;
    private ArrayList<FriendItem> friendsList;
    private FriendsListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsListBinding.inflate(inflater);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("access_token", "");
        backendAPI = new BackendAPI(accessToken);

        setupRecyclerView();
        getFriends();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.friendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsList = new ArrayList<>();
        adapter = new FriendsListAdapter(friendsList, false,
                onFriendRemoved);
        binding.friendsList.setAdapter(adapter);
    }

    private void getFriends() {
        backendAPI.getFriends(new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                getActivity().runOnUiThread(() -> {
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

    private final OnFriendRemoved onFriendRemoved = (position, friendRequestID) -> {
        friendsList.remove(position);

        backendAPI.deleteFriend(friendRequestID, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                getActivity().runOnUiThread(() -> {
                    adapter.notifyItemRemoved(position);
                });
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    };

}