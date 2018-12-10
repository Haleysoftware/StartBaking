package com.haleysoftware.startbaking.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * View Model Factory used to create the step view model.
 * <p>
 * Created by haleysoft on 11/18/18.
 */
public class StepFactory extends ViewModelProvider.NewInstanceFactory {

    private String stepJson;

    /**
     * Creates the step factory.
     *
     * @param stepJson The JSON string to use to create the list of steps.
     */
    public StepFactory(String stepJson) {
        this.stepJson = stepJson;
    }

    /**
     * Creates and returns the Step View Model and passes the JSON string.
     *
     * @param modelClass The class used for the view model.
     * @param <T>        Generic used for StepViewModel
     * @return The view model for the list of steps.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StepViewModel(stepJson);
    }
}
