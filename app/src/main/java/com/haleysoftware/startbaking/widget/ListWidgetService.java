package com.haleysoftware.startbaking.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.haleysoftware.startbaking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the list view for the widget.
 *
 * Created by haleysoft on 11/27/18.
 */
public class ListWidgetService extends RemoteViewsService {

    public static final String ING_JSON_STRING = "IngredientJson";

    /**
     * Returns the view factory for the list view.
     *
     * @param intent the passed intent with data to display.
     * @return the new view factory to handle the list view.
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (intent != null && intent.hasExtra(ING_JSON_STRING)) {
            String jsonString = intent.getStringExtra(ING_JSON_STRING);
            return new ListRemoteViewsFactory(getApplicationContext(), jsonString);
        }
        return null;
    }

    /**
     * The remote view factory that handles creating the views for the list.
     */
    class  ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private String ingredientJson;
        private List<IngredientItem> ingredientItems;

        /**
         * Creates the view factory and passes the jsonString to load.
         *
         * @param context    The widget activity.
         * @param jsonString The JSON string to use to display data.
         */
        ListRemoteViewsFactory(Context context, String jsonString) {
            this.context = context;
            this.ingredientJson = jsonString;
        }

        // Not used
        @Override
        public void onCreate() {
        }

        /**
         * When data has been changed, it tries to extract the data from the JSON string.
         */
        @Override
        public void onDataSetChanged() {
            try {
                ingredientItems = getIngredientsFromJson(ingredientJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Not used
        @Override
        public void onDestroy() {
        }

        /**
         * Tells the system the number of items currently in the list.
         *
         * @return The size of the list.
         */
        @Override
        public int getCount() {
            if (ingredientItems == null) return 0;
            return ingredientItems.size();
        }

        /**
         * Loads data from the list into the current view.
         *
         * @param position The location of the list to display.
         * @return the view to load into the list.
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (ingredientItems == null || ingredientItems.size() == 0) return null;

            int amount = ingredientItems.get(position).getAmount();
            String measure = ingredientItems.get(position).getMeasure();
            String item = ingredientItems.get(position).getItem();

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

            views.setTextViewText(R.id.tv_widget_amount, String.valueOf(amount));
            views.setTextViewText(R.id.tv_widget_measure, measure);
            views.setTextViewText(R.id.tv_widget_item, item);

            return views;
        }

        // Not using
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * Tells the widget how many types of views there are.
         *
         * @return 1 view type.
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * Tells the widget what view to give the current row.
         * This is not used and just returns 0 as there is only one row.
         *
         * @param position The location of the item in the list.
         * @return the item type to use. Always returns 0.
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        // Not used
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Takes a JSON string of ingredients and returns a list of items.
         *
         * @param jsonString The JSON string to use to create the list.
         * @return A list of ingredients.
         * @throws JSONException The error if there was one with the JSON file.
         */
        private List<IngredientItem> getIngredientsFromJson(String jsonString) throws JSONException {
            final String MAKES_QUANTITY = "quantity"; //int
            final String MAKES_MEASURE = "measure"; //String
            final String MAKES_INGREDIENT = "ingredient"; //String

            JSONArray ingredientArray = new JSONArray(jsonString);
            int ingLength = ingredientArray.length();
            ArrayList<IngredientItem> parsedIng = new ArrayList<>(ingLength);

            for (int i = 0; i < ingLength; i++) {
                JSONObject ingredient = ingredientArray.getJSONObject(i);

                int makeQuantity = ingredient.getInt(MAKES_QUANTITY);
                String makeMeasure = ingredient.getString(MAKES_MEASURE);
                String makeIngredient = ingredient.getString(MAKES_INGREDIENT);

                parsedIng.add(new IngredientItem(makeQuantity, makeMeasure, makeIngredient));
            }
            return parsedIng;
        }

    }
}
