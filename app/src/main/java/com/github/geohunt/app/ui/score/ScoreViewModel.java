package com.github.geohunt.app.ui.score;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScoreViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ScoreViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is score display fragment, where we will display the score and other relevant data of the user.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}