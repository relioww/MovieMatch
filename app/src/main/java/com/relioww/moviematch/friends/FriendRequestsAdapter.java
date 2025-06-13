package com.relioww.moviematch.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.OnFriendRequestAccepted;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.MyViewHolder> {
    private final String TAG = "FriendRequestsAdapter";

    private final ArrayList<FriendRequestItem> friendRequests;

    OnFriendRequestAccepted listener;

    public FriendRequestsAdapter(ArrayList<FriendRequestItem> friendRequests, OnFriendRequestAccepted listener) {
        this.friendRequests = friendRequests;
        this.listener = listener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friendRequestUsername;
        Button acceptFriendRequest;

        public MyViewHolder(View view) {
            super(view);
            friendRequestUsername = view.findViewById(R.id.friendRequestUsername);
            acceptFriendRequest = view.findViewById(R.id.acceptFriendRequest);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.friend_request_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FriendRequestItem item = friendRequests.get(position);
        holder.friendRequestUsername.setText(item.getUsername());
        holder.acceptFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                listener.onFriendRequestAccepted(position, item.getRequestId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }
}