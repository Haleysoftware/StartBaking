package com.haleysoftware.startbaking;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.haleysoftware.startbaking.utils.RecipeItem;
import com.haleysoftware.startbaking.utils.StepAdapter;
import com.haleysoftware.startbaking.utils.StepFactory;
import com.haleysoftware.startbaking.utils.StepItem;
import com.haleysoftware.startbaking.utils.StepViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class StepListActivity extends AppCompatActivity implements StepAdapter.StepOnClickHandler,
        StepDetailFragment.OnButtonClickListener {

    public static final String STEP_RECIPE_NAME = "RecipeName";
    public static final String STEP_JSON_STEPS = "StepJson";
    public static final String STEP_INGREDIENTS_STRING = "IngredientString";

    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    private String recipeName;
    private String ingredientString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        boolean hasRecipeName = intent.hasExtra(STEP_RECIPE_NAME);
        boolean hasStepJson = intent.hasExtra(STEP_JSON_STEPS);
        boolean hasIngredientString = intent.hasExtra(STEP_INGREDIENTS_STRING);
        if (hasRecipeName && hasStepJson && hasIngredientString) {
            recipeName = intent.getStringExtra(STEP_RECIPE_NAME);
            String stepJson = intent.getStringExtra(STEP_JSON_STEPS);
            ingredientString = intent.getStringExtra(STEP_INGREDIENTS_STRING);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }

            getStepData(stepJson);
        }
    }

    @Override
    public void onClick(int id, List<StepItem> stepItemList) {
        if (isTablet) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setButtonState(id, stepItemList.size());
            stepDetailFragment.setIngredients(ingredientString);
            stepDetailFragment.setStepItem(stepItemList.get(id));
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_fragment, stepDetailFragment).commit();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra(StepItem.RECIPE_NAME_EXTRA, recipeName);
            intent.putExtra(StepItem.STEP_ID_EXTRA, id);
            intent.putExtra(StepItem.RECIPE_ING_EXTRA, ingredientString);
            intent.putParcelableArrayListExtra(StepItem.STEP_ITEM_EXTRA,
                    (ArrayList<? extends Parcelable>) stepItemList);
            startActivity(intent);
        }
    }

    private void getStepData(String stepJson) {
        StepViewModel viewModel = ViewModelProviders.of(this, new StepFactory(stepJson))
                .get(StepViewModel.class);
        viewModel.getStepList().observe(this, new Observer<List<StepItem>>() {
            @Override
            public void onChanged(@Nullable List<StepItem> stepItems) {
                if (stepItems != null && stepItems.size() != 0) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    StepListFragment stepListFragment = (StepListFragment) fragmentManager
                            .findFragmentById(R.id.step_list_fragment);
                    if (stepListFragment != null) {
                        stepListFragment.setStepData(stepItems);
                    }

                    if (isTablet) {
                        StepDetailFragment stepDetailFragment = new StepDetailFragment();
                        stepDetailFragment.setButtonState(0, stepItems.size());
                        stepDetailFragment.setIngredients(ingredientString);
                        stepDetailFragment.setStepItem(stepItems.get(0));
                        fragmentManager.beginTransaction().add(R.id.step_detail_fragment, stepDetailFragment)
                                .commit();
                    }
                }
            }
        });
    }

    @Override
    public void onButtonPress(int buttonId) {
        //Do nothing. No buttons in Tablet mode
    }
}
