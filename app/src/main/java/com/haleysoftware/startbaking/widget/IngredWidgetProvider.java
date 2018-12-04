package com.haleysoftware.startbaking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.haleysoftware.startbaking.R;
import com.haleysoftware.startbaking.RecipeListActivity;
import com.haleysoftware.startbaking.StepListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class IngredWidgetProvider extends AppWidgetProvider {


    /**
     * Need to save the following into pref
     *
     * bool for if new widget
     * recipe name String
     * ingredient Json String
     * ingredient String
     * step Json String
     */

    public static final String WIDGET_PICKER_BOOL = "widget_pick";
    public static final String WIDGET_PICKER_ID = "widget_id";

    public static final String PREF_NAME_STRING = "_recipe_name";
    public static final String PREF_ING_JSON = "_ingredient_json";
    public static final String PREF_ING_STRING = "_ingredient_string";
    public static final String PREF_STEP_JSON = "_step_json";



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view); //Edit
        String prefKey = context.getString(R.string.pref_key);
        SharedPreferences preferences = context.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        String jsonString = preferences.getString(appWidgetId + PREF_ING_JSON, "");

        if (jsonString.isEmpty()) {
            views.setViewVisibility(R.id.tv_widget_empty, View.VISIBLE);
            views.setViewVisibility(R.id.ll_widget_keeper, View.GONE);
            setupEmptyClick(context, views, appWidgetId);
        } else {
            views.setViewVisibility(R.id.tv_widget_empty, View.GONE);
            views.setViewVisibility(R.id.ll_widget_keeper, View.VISIBLE);
            setupListClick(context, preferences, views, appWidgetId);
        }

        //Creates the adapter for displaying the data
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(ListWidgetService.ING_JSON_STRING, jsonString);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.sv_widget_holder, intent);
        views.setEmptyView(R.id.ll_widget_keeper, R.id.tv_widget_empty);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.sv_widget_holder);
    }

    private static void setupEmptyClick(Context context, RemoteViews views, int appWidgetId) {
        Intent intent = new Intent(context, RecipeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(WIDGET_PICKER_BOOL, true);
        intent.putExtra(WIDGET_PICKER_ID, appWidgetId);
        PendingIntent pendingIntentFirst = PendingIntent.getActivity(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_empty, pendingIntentFirst);
    }

    private static void setupListClick(Context context, SharedPreferences preferences,
                                       RemoteViews views, int appWidgetId) {
        String recipeName = preferences.getString(appWidgetId + PREF_NAME_STRING, "");
        String stepJson = preferences.getString(appWidgetId + PREF_STEP_JSON, "");
        String ingredientString = preferences.getString(appWidgetId + PREF_ING_STRING, "");

        views.setTextViewText(R.id.tv_widget_name, recipeName);
        Intent intent = new Intent(context, StepListActivity.class);
        intent.putExtra(StepListActivity.STEP_RECIPE_NAME, recipeName);
        intent.putExtra(StepListActivity.STEP_JSON_STEPS, stepJson);
        intent.putExtra(StepListActivity.STEP_INGREDIENTS_STRING, ingredientString);
        PendingIntent pendingIntentStep = PendingIntent.getActivity(context, appWidgetId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_widget_name, pendingIntentStep);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        String prefKey = context.getString(R.string.pref_key);
        SharedPreferences preferences = context.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int id : appWidgetIds) {
            editor.remove(id + PREF_NAME_STRING);
            editor.remove(id + PREF_ING_JSON);
            editor.remove(id + PREF_ING_STRING);
            editor.remove(id + PREF_STEP_JSON);
        }
        editor.apply();
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String prefKey = context.getString(R.string.pref_key);
            context.deleteSharedPreferences(prefKey);
        }
    }
}

