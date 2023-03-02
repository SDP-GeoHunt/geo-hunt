package com.github.geohunt.app.ui.mainmenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainMenuViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MainMenuViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is main menu fragment, where we will integrate the main menu of the application");
    }

    public LiveData<String> getText() {
        return mText;
    }
}