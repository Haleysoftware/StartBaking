package com.haleysoftware.startbaking.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haleysoftware.startbaking.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The Adapter for the step list RecyclerView.
 * It takes a list of StepItems and displays it in the RecyclerView.
 * <p>
 * Created by haleysoft on 11/13/18.
 */
public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private final StepOnClickHandler clickHandler;
    private List<StepItem> stepItems;

    /**
     * Creates the Adapter and passes the class to send the clicks to.
     *
     * @param clickHandler The class that will handle the clicks.
     */
    public StepAdapter(StepOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    /**
     * Passes the list of items to the adapter and refreshes the data.
     *
     * @param stepItems The new list of StepItems to display.
     */
    public void setStepItems(List<StepItem> stepItems) {
        this.stepItems = stepItems;
        notifyDataSetChanged();
    }

    /**
     * Creates the view holder for the step list.
     *
     * @param parent   The group of views for the list adapter.
     * @param viewType The type of view to create. Not used.
     * @return The new view holder for the view.
     */
    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutItemId = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutItemId, parent, false);
        return new StepViewHolder(view);
    }

    /**
     * Attaches the data from the list of step items to the views in the RecyclerView.
     *
     * @param stepViewHolder The holder of the views.
     * @param position       The list position of the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int position) {
        StepItem stepItem = stepItems.get(position);

        String title = stepItem.getTitle();

        stepViewHolder.title.setText(title);
    }

    /**
     * The number of items in the list. So the RecyclerView knows when to stop.
     *
     * @return Returns 0 if null. Returns the number of items in the list.
     */
    @Override
    public int getItemCount() {
        if (stepItems == null) return 0;
        return stepItems.size();
    }

    /**
     * Interface of the click handler that needs to be implemented for item clicks to be done.
     */
    public interface StepOnClickHandler {
        void onClick(int id, List<StepItem> stepItemList);
    }


    //************************* ViewHolder Class *************************

    /**
     * The view holder class the Adapter class uses to hold the item data.
     */
    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_step_title)
        TextView title;

        /**
         * Creates the holder, binds the view, and attaches the click listener.
         *
         * @param itemView The current item in the adapter.
         */
        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            title.setOnClickListener(this);
        }

        /**
         * Creates how item clicks should be handled.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int adapterPosition = getAdapterPosition();
                clickHandler.onClick(adapterPosition, stepItems);
            }
        }
    }
}
