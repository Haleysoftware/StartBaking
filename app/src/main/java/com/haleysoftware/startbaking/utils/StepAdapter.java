package com.haleysoftware.startbaking.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haleysoftware.startbaking.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haleysoft on 11/13/18.
 */
public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private final StepOnClickHandler clickHandler;
    private List<StepItem> itemList;

    public interface StepOnClickHandler {
        void onClick(int id, List<StepItem> stepItemList);
    }

    public StepAdapter(StepOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setItemList(List<StepItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        int layoutItemId = R.layout.step_list_item;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(layoutItemId, viewGroup, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int position) {
        StepItem stepItem = itemList.get(position);

        String title = stepItem.getTitle();

        stepViewHolder.title.setText(title);
    }

    @Override
    public int getItemCount() {
        if (itemList == null) return 0;
        return itemList.size();
    }


    //************************* ViewHolder Class *************************

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_step_title)
        TextView title;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickHandler != null) {
                int adapterPosition = getAdapterPosition();
                clickHandler.onClick(adapterPosition, itemList);
            }
        }
    }
}
