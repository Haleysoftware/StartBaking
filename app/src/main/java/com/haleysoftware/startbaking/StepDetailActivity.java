package com.haleysoftware.startbaking;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.haleysoftware.startbaking.utils.StepItem;

import java.util.List;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnButtonClickListener {

    private static ConstraintLayout.LayoutParams paramsNotFullscreen;

    private int id;
    private List<StepItem> stepItems;
    private String ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Intent intent = getIntent();
        boolean hasIdExtra = intent.hasExtra(StepItem.STEP_ID_EXTRA);
        boolean hasListExtra = intent.hasExtra(StepItem.STEP_ITEM_EXTRA);
        boolean hasRecipeName = intent.hasExtra(StepItem.RECIPE_NAME_EXTRA);
        boolean hasIngredients = intent.hasExtra(StepItem.RECIPE_ING_EXTRA);
        if (hasIdExtra && hasListExtra && hasRecipeName && hasIngredients) {
            this.id = intent.getIntExtra(StepItem.STEP_ID_EXTRA, 0);
            this.stepItems = intent.getParcelableArrayListExtra(StepItem.STEP_ITEM_EXTRA);
            this.ingredients = intent.getStringExtra(StepItem.RECIPE_ING_EXTRA);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(intent.getStringExtra(StepItem.RECIPE_NAME_EXTRA));
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setButtonState(id, stepItems.size());
            fragment.setIngredients(ingredients);
            fragment.setStepItem(stepItems.get(id));
            fragmentManager.beginTransaction().add(R.id.step_detail_fragment, fragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBar toolbar = getSupportActionBar();

        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailFragment fragment = (StepDetailFragment) fragmentManager
                .findFragmentById(R.id.step_detail_fragment);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                && fragment != null && toolbar != null) {
            fragment.fullscreenMode(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            toolbar.hide();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                && fragment != null && toolbar != null) {
            fragment.fullscreenMode(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            toolbar.show();
        }
    }

    @Override
    public void onButtonPress(int buttonId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailFragment fragment = new StepDetailFragment();
        switch (buttonId) {
            case R.id.b_next:
                if (id < stepItems.size()-1) {
                    id++;
                }
                break;
            case R.id.b_previous:
                if (id > 0) {
                    id--;
                }
                break;
        }
        fragment.setButtonState(id, stepItems.size());
        fragment.setIngredients(ingredients);
        fragment.setStepItem(stepItems.get(id));
        fragmentManager.beginTransaction().replace(R.id.step_detail_fragment, fragment).commit();
    }
}
