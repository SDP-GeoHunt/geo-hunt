package com.github.geohunt.app.ui.photosubmission;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PhotoSubmissionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PhotoSubmissionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the photo submission fragment, where we will integrate the photo submission for the application.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}