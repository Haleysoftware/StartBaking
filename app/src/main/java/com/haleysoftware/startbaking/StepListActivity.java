package com.haleysoftware.startbaking;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.haleysoftware.startbaking.Testing.CookingIdlingResource;
import com.haleysoftware.startbaking.utils.StepAdapter;
import com.haleysoftware.startbaking.utils.StepFactory;
import com.haleysoftware.startbaking.utils.StepItem;
import com.haleysoftware.startbaking.utils.StepViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindBool;
import butterknife.ButterKnife;

/**
 * Controls the activity and fragment that displays the list of recipe steps.
 * On a tablet, it also shows the selected recipe step.
 * <p>
 * Created by haleysoft on 11/16/18.
 */
public class StepListActivity extends AppCompatActivity implements StepAdapter.StepOnClickHandler,
        StepDetailFragment.OnButtonClickListener {

    public static final String STEP_RECIPE_NAME = "RecipeName";
    public static final String STEP_JSON_STEPS = "StepJson";
    public static final String STEP_INGREDIENTS_STRING = "IngredientString";

    @BindBool(R.bool.is_tablet)
    boolean isTablet;

    private String recipeName;
    private String ingredientString;

    // The Idling Resource which will be null in production.
    @Nullable
    private CookingIdlingResource idlingResource;

    /**
     * Unpacks the passed intent to extract the needed data.
     *
     * @param savedInstanceState Instance state from the system.
     */
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

    /**
     * The click handler for the step list fragment and adapter.
     * The ID and list size are used to control if the next and previous buttons should be active.
     * On a phone, it starts a new activity to display the step.
     * On a tablet, it changes out the step fragment with one for the selected step.
     *
     * @param id           The id or location in the list of the clicked step item.
     * @param stepItemList The list of steps.
     */
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

    /**
     * Takes the JSON string for the recipe steps and creates the list. If on a tablet, also
     * creates a fragment with the first step to display.
     *
     * @param stepJson The JSON string to use for the list.
     */
    private void getStepData(String stepJson) {

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

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
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
    }

    //************************* Test Code *************************

    /**
     * This is only used in tablet mode and these buttons are gone in that mode.
     *
     * @param buttonId The button that was pressed.
     */
    @Override
    public void onButtonPress(int buttonId) {
        //Do nothing. No buttons in Tablet mode
    }

    /**
     * Sends the idling resource to the test code
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new CookingIdlingResource();
        }
        return idlingResource;
    }
}
