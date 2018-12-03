package com.haleysoftware.startbaking.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haleysoftware.startbaking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haleysoft on 11/13/18.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeOnClickHandler clickHandler;
    private List<RecipeItem> itemList;
    private Context context;

    public interface RecipeOnClickHandler {
        void onClick(RecipeItem currentItem);
    }

    /**
     *
     * @param context
     * @param clickHandler
     */
    public RecipeAdapter(Context context, RecipeOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    /**
     *
     * @param itemList
     */
    public void setItemList(List<RecipeItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutItemId = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutItemId, viewGroup, false);
        return new RecipeViewHolder(view);
    }

    /**
     *
     * @param recipeViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        RecipeItem recipeItem = itemList.get(position);

        String title = recipeItem.getName();
        int serve = recipeItem.getServe();
        String image = recipeItem.getImage();

        recipeViewHolder.title.setText(title);
        String serveText = context.getResources().getString(R.string.serving, serve);
        recipeViewHolder.serve.setText(serveText);
        if (image.isEmpty()) {
            recipeViewHolder.image.setVisibility(View.GONE);
        } else {
            Picasso.get().load(image).into(recipeViewHolder.image);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (itemList == null) return 0;
        return itemList.size();
    }

    //************************* ViewHolder Class *************************

    /**
     *
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        MaterialCardView card;
        @BindView(R.id.tv_title_recipe)
        TextView title;
        @BindView(R.id.tv_serve_recipe)
        TextView serve;
        @BindView(R.id.iv_image_recipe)
        ImageView image;

        /**
         *
         * @param itemView
         */
        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            card.setOnClickListener(this);
        }

        /**
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int adapterPosition = getAdapterPosition();
                clickHandler.onClick(itemList.get(adapterPosition));
            }
        }
    }
}
