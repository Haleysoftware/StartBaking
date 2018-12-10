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
 * The live data view model for the recipe item data.
 * <p>
 * Created by haleysoft on 11/13/18.
 */
public class RecipeViewModel extends AndroidViewModel {

    private static MutableLiveData<List<RecipeItem>> listLiveData = new MutableLiveData<>();

    /**
     * Creates the view model and starts loading the data.
     *
     * @param application The calling activity.
     */
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        loadRecipeData();
    }

    /**
     * Loads the recipe data from the stored URL and passes it to the class for JSON strings.
     */
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

    /**
     * Returns the list of RecipeItems from the Live Data.
     *
     * @return The list of RecipeItems.
     */
    public LiveData<List<RecipeItem>> getRecipeList() {
        return listLiveData;
    }
}
