package com.haleysoftware.startbaking.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.net.URL;
import java.util.List;

/**
 * Created by haleysoft on 11/13/18.
 */
public class RecipeViewModel extends AndroidViewModel {

    private static MutableLiveData<List<RecipeItem>> listLiveData = new MutableLiveData<>();

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        loadRecipeData();
    }

    private static void loadRecipeData() {
        new AsyncTask<Void, Void, List<RecipeItem>>() {

            @Override
            protected List<RecipeItem> doInBackground(Void... voids) {
                URL url = NetworkHelper.urlBuilder();
                try {
                    String networkResponse = NetworkHelper.internetResponse(url);
                    return ReadJson.getRecipesFromJson(networkResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<RecipeItem> recipeItems) {
                super.onPostExecute(recipeItems);
                listLiveData.setValue(recipeItems);
            }
        }.execute();
    }

    public LiveData<List<RecipeItem>> getRecipeList() {
        return listLiveData;
    }
}
