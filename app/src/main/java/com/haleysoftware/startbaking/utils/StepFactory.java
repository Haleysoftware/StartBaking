package com.haleysoftware.startbaking.utils;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by haleysoft on 11/18/18.
 */
public class StepFactory extends ViewModelProvider.NewInstanceFactory {

    private String stepJson;

    public StepFactory(String stepJson) {
        this.stepJson = stepJson;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StepViewModel(stepJson);
    }
}
