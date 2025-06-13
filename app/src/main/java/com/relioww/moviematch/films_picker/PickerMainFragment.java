package com.relioww.moviematch.films_picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.relioww.moviematch.databinding.FragmentPickerMainBinding;
import com.relioww.moviematch.friends.ChooseFriendsActivity;
import com.relioww.moviematch.callbacks.OnPickFilmsClick;

import java.util.ArrayList;

public class PickerMainFragment extends Fragment {
    private ArrayList<Integer> selectedFriends;
    private FragmentPickerMainBinding binding;
    private OnPickFilmsClick listener;

    public PickerMainFragment(OnPickFilmsClick listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPickerMainBinding.inflate(inflater);

        selectedFriends = new ArrayList<>();

        binding.chooseFriends.setOnClickListener(onChooseFriendsClick);
        binding.pickFilms.setOnClickListener(onPickFilmsClick);
        binding.pickerMainGoBack.setOnClickListener((v) -> getActivity().finish());

        return binding.getRoot();
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    selectedFriends = intent.getIntegerArrayListExtra(
                            "selected_friends");
                }
            });

    private final View.OnClickListener onChooseFriendsClick = (view) -> {
        Intent intent = new Intent(getActivity(),
                ChooseFriendsActivity.class);
        startForResult.launch(intent);
    };

    private final View.OnClickListener onPickFilmsClick = (view) -> {
        String filmPreferences = binding.filmPreferences.getText().toString();
        listener.onPickFilmsClick(selectedFriends, filmPreferences);
    };
}