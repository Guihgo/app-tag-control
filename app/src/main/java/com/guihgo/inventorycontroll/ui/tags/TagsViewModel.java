package com.guihgo.inventorycontroll.ui.tags;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TagsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TagsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tags fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}