package com.github.geohunt.app.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the map fragment, where we will integrate an interactive map with the application.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}