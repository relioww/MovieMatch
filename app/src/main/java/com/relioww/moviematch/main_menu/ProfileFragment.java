package com.relioww.moviematch.main_menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relioww.moviematch.databinding.FragmentProfileBinding;
import com.relioww.moviematch.favorites.FavoritesActivity;
import com.relioww.moviematch.R;
import com.relioww.moviematch.auth.LoginActivity;
import com.relioww.moviematch.films_picker.FilmPickerActivity;
import com.relioww.moviematch.friends.FriendsActivity;

public class ProfileFragment extends Fragment {
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater);

        preferences = getActivity().getSharedPreferences(
                "user", Context.MODE_PRIVATE);
        binding.userId.setText(getString(R.string.user_id,
                preferences.getInt("id", 1)));
        binding.username.setText(preferences.getString(
                "username", ""));

        binding.logoutButton.setOnClickListener(onLogoutClick);
        binding.friendsMenu.setOnClickListener(onFriendsClick);
        binding.favoritesPage.setOnClickListener(onFavoritesClick);
        binding.filmsPicker.setOnClickListener(onFilmsPickerClick);

        return binding.getRoot();
    }

    private final DialogInterface.OnClickListener onLogoutConfirm = (dialog, id) -> {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString("access_token", "");
        prefEditor.apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().finish();
        startActivity(intent);
    };

    private final View.OnClickListener onLogoutClick = (v) -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setPositiveButton(R.string.logout_confirm, onLogoutConfirm)
                .setNegativeButton(R.string.logout_cancel, (dialog, id) -> {
                    dialog.cancel();
                })
                .show();
    };

    private final View.OnClickListener onFriendsClick = (view) -> {
        Intent intent = new Intent(getActivity(), FriendsActivity.class);
        startActivity(intent);
    };

    private final View.OnClickListener onFavoritesClick = (view) -> {
        Intent intent = new Intent(getActivity(), FavoritesActivity.class);
        startActivity(intent);
    };

    private final View.OnClickListener onFilmsPickerClick = (view) -> {
        Intent intent = new Intent(getActivity(), FilmPickerActivity.class);
        startActivity(intent);
    };
}