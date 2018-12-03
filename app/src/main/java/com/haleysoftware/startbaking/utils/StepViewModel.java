package com.haleysoftware.startbaking.utils;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.util.List;

/**
 * Created by haleysoft on 11/15/18.
 */
public class StepViewModel extends ViewModel {

    private static MutableLiveData<List<StepItem>> stepLiveData = new MutableLiveData<>();

    /**
     *
     * @param jsonString
     */
    public StepViewModel(@NonNull String jsonString) {
        loadStepData(jsonString);
    }

    /**
     *
     * @param jsonString
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
     *
     * @return
     */
    public LiveData<List<StepItem>> getStepList() {
        return stepLiveData;
    }
}
