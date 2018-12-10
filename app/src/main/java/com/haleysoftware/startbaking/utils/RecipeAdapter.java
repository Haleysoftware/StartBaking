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
 * The Adapter class for the recipe RecyclerView.
 * <p>
 * Created by haleysoft on 11/13/18.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeOnClickHandler clickHandler;
    private List<RecipeItem> recipeItems;
    private Context context;

    /**
     * Creates the Adapter.
     *
     * @param context      The context of the attached activity.
     * @param clickHandler The class that will handle the item clicks.
     */
    public RecipeAdapter(Context context, RecipeOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    /**
     * Sets the new RecipeItem list to the activity.
     *
     * @param recipeItems The new list of RecipeItems to use.
     */
    public void setRecipeItems(List<RecipeItem> recipeItems) {
        this.recipeItems = recipeItems;
        notifyDataSetChanged();
    }

    /**
     * Creates the view holder and the view.
     *
     * @param parent   The parent view group.
     * @param viewType The type of view to inflate. Not used.
     * @return The new RecipeViewHolder.
     */
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutItemId = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutItemId, parent, false);
        return new RecipeViewHolder(view);
    }

    /**
     * Attaches the data from the RecipeItem to the views.
     *
     * @param recipeViewHolder The view holder to attach the data to.
     * @param position         The position of the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        RecipeItem recipeItem = recipeItems.get(position);

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
     * Returns the number of items in the list.
     *
     * @return The number of items or 0 if list is null.
     */
    @Override
    public int getItemCount() {
        if (recipeItems == null) return 0;
        return recipeItems.size();
    }

    /**
     * The click handler interface. This needs to be implemented by the activity.
     */
    public interface RecipeOnClickHandler {
        void onClick(RecipeItem currentItem);
    }

    //************************* ViewHolder Class *************************

    /**
     * This class holds the links the list item views with the details.
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
         * Creates the holder and binds the data long with sets the on click listener.
         *
         * @param itemView The view at this list position.
         */
        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            card.setOnClickListener(this);
        }

        /**
         * Sets up what to do when an item is clicked and passes the item to the click handler.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (clickHandler != null) {
                int adapterPosition = getAdapterPosition();
                clickHandler.onClick(recipeItems.get(adapterPosition));
            }
        }
    }
}