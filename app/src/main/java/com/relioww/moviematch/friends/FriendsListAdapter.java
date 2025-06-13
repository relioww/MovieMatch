package com.relioww.moviematch.friends;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relioww.moviematch.R;
import com.relioww.moviematch.callbacks.OnFriendRemoved;

import java.util.ArrayList;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder> {
    private final String TAG = "FriendsListAdapter";

    ArrayList<FriendItem> friends;
    boolean isSelectionMode;
    OnFriendRemoved onFriendRemoved;

    public FriendsListAdapter(ArrayList<FriendItem> friends, boolean isSelectionMode,
                              OnFriendRemoved onFriendRemoved) {
        this.friends = friends;
        this.isSelectionMode = isSelectionMode;
        this.onFriendRemoved = onFriendRemoved;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friendUsername;
        Button deleteFriend;

        public MyViewHolder(View view) {
            super(view);
            friendUsername = view.findViewById(R.id.friendUsername);
            deleteFriend = view.findViewById(R.id.deleteFriend);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.friend_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FriendItem item = friends.get(position);
        holder.friendUsername.setText(item.getUsername());

        if (isSelectionMode) {
            holder.deleteFriend.setVisibility(View.GONE);
            holder.itemView.setOnClickListener((view) -> {
                item.setSelected(!item.isSelected());
                notifyItemChanged(position);
            });

            holder.itemView.setBackgroundColor(
                    item.isSelected() ?
                            Color.parseColor("#e0e0e0") :
                            Color.TRANSPARENT
            );

        }

        holder.deleteFriend.setOnClickListener((view) -> {
            onFriendRemoved.onFriendRemoved(position, item.getFriendshipId());
        });
    }

    public ArrayList<Integer> getSelectedFriendsIds() {
        ArrayList<Integer> selected = new ArrayList<>();
        for (FriendItem item : friends) {
            if (item.isSelected()) {
                selected.add(item.getUserId());
            }
        }
        return selected;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
