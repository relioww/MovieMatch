package com.relioww.moviematch.friends;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.relioww.moviematch.R;
import com.relioww.moviematch.databinding.ActivityFriendsBinding;

public class FriendsActivity extends AppCompatActivity {
    private ActivityFriendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FriendsListFragment friendsList= new FriendsListFragment();
        FriendRequestsFragment friendRequests = new FriendRequestsFragment();

        binding.friendsMenu.setOnItemSelectedListener(item -> {
            Fragment newFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.friendsList) {
                newFragment = friendsList;
            } else if (itemId == R.id.friendRequests) {
                newFragment = friendRequests;
            } else if (itemId == R.id.returnHome) {
                finish();
            }

            if (newFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.friendsFragmentContainer, newFragment)
                        .commit();
            }

            return true;
        });

    }
}