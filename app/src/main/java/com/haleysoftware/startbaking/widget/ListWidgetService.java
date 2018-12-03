package com.haleysoftware.startbaking.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.haleysoftware.startbaking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haleysoft on 11/27/18.
 */
public class ListWidgetService extends RemoteViewsService {

    public static final String ING_JSON_STRING = "IngredientJson";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (intent != null && intent.hasExtra(ING_JSON_STRING)) {
            String jsonString = intent.getStringExtra(ING_JSON_STRING);
            return new ListRemoteViewsFactory(getApplicationContext(), jsonString);
        }
        return null;
    }

    class  ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private String ingredientJson;
        private List<IngredientItem> ingredientItems;

        ListRemoteViewsFactory(Context context, String jsonString) {
            this.context = context;
            this.ingredientJson = jsonString;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            try {
                ingredientItems = getIngredientsFromJson(ingredientJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientItems == null) return 0;
            return ingredientItems.size();
        }

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

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

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
