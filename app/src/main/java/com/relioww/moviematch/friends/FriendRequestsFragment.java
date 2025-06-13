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
import android.widget.Toast;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.JSONCallback;
import com.relioww.moviematch.core.BackendAPI;
import com.relioww.moviematch.databinding.FragmentFriendRequestsBinding;
import com.relioww.moviematch.callbacks.OnFriendRequestAccepted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment {
    private final String TAG = "FriendRequestsFragment";

    private BackendAPI backendAPI;
    private ArrayList<FriendRequestItem> friendRequestsList;
    private FriendRequestsAdapter adapter;
    private FragmentFriendRequestsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendRequestsBinding.inflate(inflater);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        String accessToken = preferences.getString("access_token", "");
        backendAPI = new BackendAPI(accessToken);

        binding.sendFriendRequest.setOnClickListener(onSendFriendRequestClick);

        setupRecyclerView();
        getFriendRequests();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.friendRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendRequestsList = new ArrayList<>();
        adapter = new FriendRequestsAdapter(friendRequestsList, onRequestAccept);
        binding.friendRequests.setAdapter(adapter);
    }

    private void getFriendRequests() {
        backendAPI.getFriendRequests(new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                getActivity().runOnUiThread(() -> {
                    try {
                        JSONArray friendRequests = result.getJSONArray(
                                "friend_requests");
                        updateFriendRequestsList(friendRequests);
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

    private void updateFriendRequestsList(JSONArray friendRequests) throws  JSONException {
        for (int i = 0; i < friendRequests.length(); i++) {
            JSONObject friend = friendRequests.getJSONObject(i);
            friendRequestsList.add(new FriendRequestItem(
                    friend.getInt("friend_request_id"),
                    friend.getInt("user_id"),
                    friend.getString("username")));
            adapter.notifyItemInserted(i);
        }
    }

    private final View.OnClickListener onSendFriendRequestClick = (view) -> {
        String username = binding.friendRequestUsername.getText().toString();

        backendAPI.sendFriendRequest(username, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    int toastText = status.equals("ok")
                            ? R.string.friend_request_sent
                            : R.string.friend_request_error;

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), toastText,
                                Toast.LENGTH_LONG).show();
                    });
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(IOException e) {
                Log.e(TAG, e.toString());
            }
        });
    };

    private final OnFriendRequestAccepted onRequestAccept = (position, friendRequestID) -> {
        backendAPI.acceptFriendRequest(friendRequestID, new JSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                friendRequestsList.remove(position);
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