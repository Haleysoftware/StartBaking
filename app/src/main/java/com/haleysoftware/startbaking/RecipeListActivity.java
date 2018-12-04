package com.haleysoftware.startbaking;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haleysoftware.startbaking.utils.NetworkHelper;
import com.haleysoftware.startbaking.utils.RecipeAdapter;
import com.haleysoftware.startbaking.utils.RecipeItem;
import com.haleysoftware.startbaking.utils.RecipeViewModel;
import com.haleysoftware.startbaking.widget.IngredWidgetProvider;

import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haleysoft on 11/13/18.
 */
public class RecipeListActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeOnClickHandler {

    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    @BindView(R.id.rv_recipe_list)
    RecyclerView cardList;
    @BindView(R.id.pb_loading)
    ProgressBar loadingView;
    @BindView(R.id.tv_error)
    TextView errorView;

    private RecipeAdapter recipeAdapter;

    /**
     * Calculates the number of columns of movie posters the phone can display
     *
     * @param context The context of the activity
     * @return The number of columns the RecycleView should display
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns < 2 ? 2 : noOfColumns;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);



        if (isTablet) {
            cardList.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns(this)));
        } else {
            cardList.setLayoutManager(new LinearLayoutManager(this));
        }
        recipeAdapter = new RecipeAdapter(this, this);
        cardList.setAdapter(recipeAdapter);

        if (NetworkHelper.hasInternet(this)) {
            loadingView.setVisibility(View.VISIBLE);
            getRecipeData();
        } else {
            errorView.setText(R.string.error_no_internet);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void getRecipeData() {
        loadingView.setVisibility(View.VISIBLE);
        RecipeViewModel viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.getRecipeList().observe(this, new Observer<List<RecipeItem>>() {
            @Override
            public void onChanged(@Nullable List<RecipeItem> recipeItems) {
                loadingView.setVisibility(View.GONE);
                if (recipeItems != null && recipeItems.size()>0) {
                    errorView.setVisibility(View.GONE);
                    cardList.setVisibility(View.VISIBLE);
                    recipeAdapter.setItemList(recipeItems);
                } else {
                    cardList.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText(R.string.error_no_data);
                }
            }
        });
    }

    @Override
    public void onClick(RecipeItem currentItem) {
        Intent widgetIntent = getIntent();
        if (widgetIntent != null && widgetIntent.hasExtra(IngredWidgetProvider.WIDGET_PICKER_ID)) {
            int widgetId = widgetIntent.getIntExtra(IngredWidgetProvider.WIDGET_PICKER_ID, 0);
            String prefKey = getString(R.string.pref_key);
            SharedPreferences preference = getSharedPreferences(prefKey, MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString(widgetId + IngredWidgetProvider.PREF_NAME_STRING, currentItem.getName());
            editor.putString(widgetId + IngredWidgetProvider.PREF_ING_JSON, currentItem.getIngredientJson());
            editor.putString(widgetId + IngredWidgetProvider.PREF_ING_STRING, currentItem.getIngredientString());
            editor.putString(widgetId + IngredWidgetProvider.PREF_STEP_JSON, currentItem.getStepJson());
            editor.apply();
            Intent intent = new Intent(this, IngredWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int ids[] = AppWidgetManager.getInstance(getApplication())
                    .getAppWidgetIds(new ComponentName(getApplication(), IngredWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intent);
            finish();
        } else {
            Intent intent = new Intent(this, StepListActivity.class);
            intent.putExtra(StepListActivity.STEP_RECIPE_NAME, currentItem.getName());
            intent.putExtra(StepListActivity.STEP_JSON_STEPS, currentItem.getStepJson());
            intent.putExtra(StepListActivity.STEP_INGREDIENTS_STRING, currentItem.getIngredientString());
            startActivity(intent);
        }
    }
}
