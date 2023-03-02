package com.github.geohunt.app.ui.score;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.geohunt.app.R;
import com.github.geohunt.app.databinding.FragmentSlideshowBinding;
import com.github.geohunt.app.ui.photosubmission.PhotoSubmissionViewModel;

public class ScoreFragment extends Fragment {

    private ScoreViewModel mViewModel;
    private FragmentSlideshowBinding binding;

    public static ScoreFragment newInstance() {
        return new ScoreFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScoreViewModel slideshowViewModel =
                new ViewModelProvider(this).get(ScoreViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}