package com.haleysoftware.startbaking;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haleysoftware.startbaking.utils.StepAdapter;
import com.haleysoftware.startbaking.utils.StepItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The fragment that displays the list of steps for the selected recipe.
 * <p>
 * Created by haleysoft on 11/16/18.
 */
public class StepListFragment extends Fragment {

    @BindView(R.id.rv_step_list)
    RecyclerView stepList;

    private StepAdapter stepAdapter;
    StepAdapter.StepOnClickHandler clickListener;

    // Mandatory empty constructor
    public StepListFragment() {
    }

    /**
     * Checks if the activity implements the needed onclick and throws an error if not.
     * If it is implemented, assigns it to the local variable.
     *
     * @param context The attached activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            clickListener = (StepAdapter.StepOnClickHandler) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ClassCastException(context.toString()
                    + " must implement StepAdapter.StepOnClickHandler");
        }
    }

    /**
     * Binds the views and sets up the list adapter.
     *
     * @param inflater           The layout inflater.
     * @param container          The layout view group.
     * @param savedInstanceState the instance state from the system.
     * @return the inflated view to display.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step_list, container, false);
        ButterKnife.bind(this, rootView);
        stepList.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(stepList.getContext(),
                DividerItemDecoration.VERTICAL);
        stepList.addItemDecoration(divider);
        stepAdapter = new StepAdapter(clickListener);
        stepList.setAdapter(stepAdapter);
        return rootView;
    }

    /**
     * Passes the new list of step items to the adapter to diaplay.
     *
     * @param stepItems The new list of steps.
     */
    public void setStepData(List<StepItem> stepItems) {
        if (stepItems != null && stepItems.size() > 0) {
            stepAdapter.setStepItems(stepItems);
        }
    }
}
