package com.relioww.moviematch;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.relioww.moviematch.main_menu.AboutFragment;
import com.relioww.moviematch.main_menu.ProfileFragment;
import com.relioww.moviematch.main_menu.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SearchFragment searchFragment;
    ProfileFragment profileFragment;
    AboutFragment aboutFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView =
                findViewById(R.id.bottomNavigationView);

        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();
        aboutFragment = new AboutFragment();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment newFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.search) {
                newFragment = searchFragment;
            } else if (itemId == R.id.profile) {
                newFragment = profileFragment;
            } else if (itemId == R.id.about) {
                newFragment = aboutFragment;
            }

            if (newFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, newFragment)
                        .commit();
            }
            return true;
        });
    }
}