package com.haleysoftware.startbaking.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.util.List;

/**
 * The ViewModel for the step list to create live data from the JSON string.
 * <p>
 * Created by haleysoft on 11/15/18.
 */
public class StepViewModel extends ViewModel {

    private static MutableLiveData<List<StepItem>> stepLiveData = new MutableLiveData<>();

    /**
     * Creates the view model and starts the data load.
     *
     * @param jsonString The JSON string used to create the list of steps.
     */
    StepViewModel(@NonNull String jsonString) {
        loadStepData(jsonString);
    }

    /**
     * Loads data into a list of step items for the step list activity.
     *
     * @param jsonString The JSON string used to create the list of steps.
     */
    private static void loadStepData(String jsonString) {
        new AsyncTask<String, Void, List<StepItem>>() {

            @Override
            protected List<StepItem> doInBackground(String... strings) {
                try {
                    return ReadJson.getStepsFromJson(strings[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<StepItem> stepItems) {
                super.onPostExecute(stepItems);
                stepLiveData.setValue(stepItems);
            }
        }.execute(jsonString);
    }

    /**
     * Returns the live data of step items.
     *
     * @return The live data in form of a list of step items.
     */
    public LiveData<List<StepItem>> getStepList() {
        return stepLiveData;
    }
}
