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

    public void setStepData(List<StepItem> stepItems) {
        if (stepItems != null && stepItems.size()>0) {
            stepAdapter.setItemList(stepItems);
        }
    }
}
